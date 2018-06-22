/*
 *    Copyright 2018 Soloplan GmbH
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.soloplan.oss.sonarqube.plugin.resharper.clt.sensors;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.converters.InspectCodeIssueDefinitionToSonarQubeRuleDefinitionConverter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.converters.InspectCodeIssueToSonarQubeIssueConverter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.CSharpLanguage;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeIssueModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates.InspectCodePredicates;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates.ObjectPredicates;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.InspectCodeXmlFileParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.fs.InputFile;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.batch.sensor.issue.NewIssueLocation;
import org.sonar.api.batch.sensor.issue.internal.DefaultIssueLocation;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CSharpSensor
    implements Sensor {

  /**
   * Gets an implementation of the {@link Logger} interface for this class.
   * <p/>
   * Please note, that message arguments are defined with {@code {}}, but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  private static final Logger LOGGER = Loggers.get(CSharpSensor.class);

  @Override
  public void describe(SensorDescriptor descriptor) {
    // Describe the purpose of the Sensor and restrict it to files of the correct language
    // and rules contained within the correct issue repository
    descriptor.name("Add issues detected by the InspectCode command line tool to C# files")
        .onlyOnLanguage(CSharpLanguage.LANGUAGE_NAME)
        .createIssuesForRuleRepository(ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY);
  }

  @Override
  public void execute(SensorContext context) {
    // Retrieve the path to the XML output file of the InspectCode command line tool from the configuration and try to access the file
    final File inspectCodeReportFile = this.getInspectCodeReportFile(
        context.config().get(ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH).orElse(""));
    // Stop if the report file could not be retrieved
    if (inspectCodeReportFile == null) {
      // Error logging is done within method 'getInspectCodeReportFile()'
      return;
    }

    // Retrieve all active SonarQube rules for the InspectCode C# repository from the context
    final Collection<ActiveRule> activeRuleCollection =
        context.activeRules().findByRepository(ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY);

    // Stop if no rules are activated for the InspectCode C# repository
    if (activeRuleCollection.isEmpty()) {
      LOGGER.info("There are no active rules for repository '{}'.", ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY);
      return;
    }

    // Stop if no C# source files are present
    final FileSystem fileSystem = context.fileSystem();
    if (!fileSystem.hasFiles(fileSystem.predicates().hasLanguage(CSharpLanguage.LANGUAGE_NAME))) {
      LOGGER.info("There are no source files present for language C#.");
      return;
    }

    // Retrieve and validate all mandatory properties from the SonarQube configuration
    final SonarQubeSensorProperties sonarQubeProperties = new SonarQubeSensorProperties(context.config());
    if (!sonarQubeProperties.validatePropertyValues(LOGGER)) {
      LOGGER.warn("Not all mandatory properties are set, skipping issue parsing for the current project.");
      return;
    }

    // Create a predicate to match the project name for the issues
    final Predicate<String> projectNamePredicate =
        element -> element != null && element.trim().equalsIgnoreCase(sonarQubeProperties.projectName);

    // Parse and convert the XML file of the InspectCode command line tool
    final SonarQubeSensorXmlParserResults sonarQubeSensorXmlParserResults =
        this.parseInspectCodeXmlReportFile(inspectCodeReportFile, projectNamePredicate);

    // If null is returned, an error has occurred during parsing, hence abort the creation of issues for this project
    if (sonarQubeSensorXmlParserResults == null) {
      LOGGER.info("An error occurred during parsing of InspectCode XML file '{}'. Aborting scan for project '{}'.",
          inspectCodeReportFile.getAbsolutePath(),
          sonarQubeProperties.projectName);
      return;
    }

    // Retrieve a collection of all issued found by InspectCode for the current project
    if (sonarQubeSensorXmlParserResults.parsedIssues.isEmpty()) {
      LOGGER.debug("No issues have been parsed for project {}. Skipping project...", sonarQubeProperties.projectName);
      return;
    }

    // Retrieve the issue type definition identifiers from the parsed InspectCode XML file, which are used as rule keys
    final Set<String> occurredIssueDefinitions = sonarQubeSensorXmlParserResults.ruleDefinitions
        .parallelStream()
        .map(SonarQubeRuleDefinitionModel::getRuleDefinitionKey)
        .collect(Collectors.toSet());

    // Create a map consisting of the InspectCode issue type definition identifier as key and the corresponding SonarQube RuleKey,
    // but only for those issue definitions, that are contained within the InspectCode report XML file
    final Map<String, RuleKey> ruleKeyMap =
        activeRuleCollection.parallelStream()
            .filter(activeRule -> occurredIssueDefinitions.contains(activeRule.ruleKey().rule()))
            .collect(Collectors.toMap(activeRule -> activeRule.ruleKey().rule(), ActiveRule::ruleKey));

    // TODO: Create method to get the relative base directory of the InspectCode executable
    //       It might be necessary to check whether the solution file name stored within the SonarQube properties is relative or absolute.
    //       Additionally, the XML file generated by InspectCode contains a path to the solution file, too. But the latter is always
    //       stored relative to the directory where the InspectCode executable has been run.

    // Build the absolute path to the solution file
    final File solutionFile = new File(sonarQubeProperties.userDir + File.separator + sonarQubeProperties.solutionFileName);
    final String inspectCodeRelativeBaseDir = solutionFile.getParent() + File.separator;

    // Iterate all issues found within the InspectCode report XML file matching the project name predicate
    for (SonarQubeIssueModel sonarQubeIssueModel : sonarQubeSensorXmlParserResults.parsedIssues) {
      // Skip this issue if its rule definition is not activated in the quality profile of this project
      if (!ruleKeyMap.containsKey(sonarQubeIssueModel.getRuleKey())) {
        final String reason = String.format(
            "Could not find rule definition identifier %s within the set of active SonarQube rules.",
            sonarQubeIssueModel.getRuleKey());
        this.logSkippedIssue(sonarQubeIssueModel, reason);
        continue;
      }

      // Construct an absolute path from the filesystem root to the source file where the issue occurred,
      // relative to the path where the solution file is located.
      final String absoluteFilePath = inspectCodeRelativeBaseDir + sonarQubeIssueModel.getFilePath();

      final InputFile sourceCodeFile = fileSystem.inputFile(fileSystem.predicates().hasPath(absoluteFilePath));
      if (sourceCodeFile == null || !sourceCodeFile.isFile()) {
        final String reason = String.format(
            "Could not find source code file %s using the SonarQube FileSystem API.",
            absoluteFilePath);
        this.logSkippedIssue(sonarQubeIssueModel, reason);
        continue;
      }

      // TODO: Create a better TextRange by calculating the start and end character
      // Note: The InspectCode XML file contains the amount of characters since the start of the file as offset instead of the index within
      //       the line, hence using sonarQubeIssueModel.getTextRange().start().lineOffset() leads to a runtime exception because there are
      //       not enough characters within the supplied line
      TextRange textRange = sourceCodeFile.selectLine(sonarQubeIssueModel.getTextRange().start().line());

      NewIssueLocation issueLocation = new DefaultIssueLocation()
          .on(sourceCodeFile)
          .at(textRange)
          .message(sonarQubeIssueModel.getMessage());

      // Create a new issue within SonarQube
      context.newIssue().at(issueLocation).forRule(ruleKeyMap.get(sonarQubeIssueModel.getRuleKey())).save();
    }
  }

  /**
   * Tries to access the {@link File} referenced by the supplied {@code inspectCodeReportFilePath}, while verifying that the path actually
   * refers to an existing file. If the supplied {@code inspectCodeReportFilePath} is either {@code null}, an empty string, refers to a
   * non-existing file, or a directory, {@code null} is returned. In addition, error messages are logged using the class-private {@link
   * #LOGGER}.
   *
   * @param inspectCodeReportFilePath
   *     The path referencing the output file of the {@code InspectCode} command line tool.
   *
   * @return The {@link File} referenced by the supplied {@code inspectCodeReportFilePath}. Might return {@code null}, if the supplied
   *     {@code inspectCodeReportFilePath} is either {@code null}, an empty string, refers to a non-existing file, or a directory.
   */
  private File getInspectCodeReportFile(@NotNull String inspectCodeReportFilePath) {
    // Sanitize the supplied argument
    inspectCodeReportFilePath = inspectCodeReportFilePath.trim();

    // Log an error message if the value of the configuration is not set
    if (inspectCodeReportFilePath.isEmpty()) {
      LOGGER.error(
          "Can't analyze InspectCode report because the XML file to analyze is not defined. " +
              "Please set property {} to refer to the XML output file of the InspectCode command line tool.",
          ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH);
      return null;
    }

    // Access the output file of the InspectCode tool and validate it
    final File inspectCodeReportFile = new File(inspectCodeReportFilePath);
    if (!inspectCodeReportFile.exists()) {
      LOGGER.error(
          "Can't analyze InspectCode report because the XML file to analyze defined by property {} does not exist: '{}'.",
          ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH,
          inspectCodeReportFilePath);
      return null;
    } else if (!inspectCodeReportFile.isFile()) {
      LOGGER.error(
          "Can't analyze InspectCode report because the XML file defined by property {} is not a file: '{}'.",
          ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH,
          inspectCodeReportFilePath);
      return null;
    } else {
      // TODO: Implement XML Schema Definition validation?

      // Return the file, since it seems valid
      return inspectCodeReportFile;
    }
  }

  /**
   * Logs a message that the supplied {@code sonarQubeIssueModel} has been skipped using the class-private {@link #LOGGER}.
   *
   * @param sonarQubeIssueModel
   *     The issue that has been skipped.
   * @param reason
   *     The reason why this issue has been skipped. Might be {@code null} if no reason should be logged.
   */
  private void logSkippedIssue(@NotNull final SonarQubeIssueModel sonarQubeIssueModel, @Nullable String reason) {
    // Sanitize the supplied reason
    reason = reason != null ? reason.trim() : "";

    // Helper variable
    final TextRange textRange = sonarQubeIssueModel.getTextRange();

    // Build the log message from the supplied arguments
    final StringBuilder sb = new StringBuilder(128)
        .append("Skipping issue for rule ")
        .append(sonarQubeIssueModel.getRuleKey())
        .append(", which occurred at line ")
        .append(textRange.start().line())
        .append(" in range ")
        .append(textRange.start().lineOffset())
        .append("-")
        .append(textRange.end().lineOffset())
        .append("of file ")
        .append(sonarQubeIssueModel.getFilePath())
        .append(".");
    if (!reason.isEmpty()) {
      sb.append("Reason: ").append(reason).append(".");
    }

    LOGGER.info(sb.toString());
  }

  /**
   * Creates a new instance of the {@link InspectCodeXmlFileParser} class and parses the XML report file generated by the InspectCode
   * command line tool referenced by the supplied {@code inspectCodeXmlReportFile}. If {@code projectNamePredicate} is not {@code null},
   * only issues found for projects matching the {@link Predicate} are parsed otherwise all issues defined within the XML report file are
   * parsed and returned. If the result of this method is {@code null}, an error occurred during parsing (which has been logged using the
   * {@link #LOGGER}).
   *
   * @param inspectCodeXmlReportFile
   *     A reference to the XML report file generated by the InspectCode command line tool to be parsed.
   * @param projectNamePredicate
   *     If set to {@code null}, all issues for all projects contained within the supplied InspectCode XML file are parsed. Otherwise, only
   *     issues for projects where the name matches the supplied {@link Predicate} will be parsed and returned by this method.
   *
   * @return An instance of the {@link SonarQubeSensorXmlParserResults} class, containing a {@link Collection} of all issues parsed from the
   *     supplied {@code inspectCodeXmlReportFile} where the project name matches the supplied {@code projectNamePredicate} and another
   *     Collection containing all SonarQube rule definitions of the issues found in the XML report file. If an error occurred during
   *     parsing of the XML report file, {@code null} is returned.
   */
  @Nullable
  private SonarQubeSensorXmlParserResults parseInspectCodeXmlReportFile(
      @NotNull final File inspectCodeXmlReportFile,
      @Nullable Predicate<String> projectNamePredicate) {

    // Sanitize the supplied predicate, so it is never null
    projectNamePredicate = projectNamePredicate != null ? projectNamePredicate : x -> !x.isEmpty();

    // Create a new SAX parser implementation that will parse and convert the XML file of the InspectCode command line tool
    final InspectCodeXmlFileParser xmlFileParser = new InspectCodeXmlFileParser(
        new InspectCodeIssueDefinitionToSonarQubeRuleDefinitionConverter(),
        new InspectCodeIssueToSonarQubeIssueConverter(),
        Arrays.asList(
            ObjectPredicates.isNotNullPredicate(),
            InspectCodePredicates.hasNonEmptyIssueDescription()),
        Arrays.asList(
            ObjectPredicates.isNotNullPredicate(),
            InspectCodePredicates.hasValidIssueOffset(),
            InspectCodePredicates.isValidLineNumber()),
        Arrays.asList(
            ObjectPredicates.isNotNullPredicate(),
            projectNamePredicate));

    // Use 'try-with-resource' to automatically close the input stream on error or finish
    try (FileInputStream reportFileInputStream = new FileInputStream(inspectCodeXmlReportFile)) {
      try {
        // Parse the input stream using the xmlFileParser created above which will store the results
        final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.parse(reportFileInputStream, xmlFileParser);
      } catch (ParserConfigurationException | SAXException | IOException exception) {
        LOGGER.error(
            "An exception occurred while trying to parse the data stream of the report XML file " + inspectCodeXmlReportFile + ".",
            exception);
      }
    } catch (IOException ioe) {
      LOGGER.error("Could not open file " + inspectCodeXmlReportFile + " for parsing.", ioe);
      return null;
    }

    // Return a new instance of the resulting class which contains the items parsed from the InspectCode XML report file
    return new SonarQubeSensorXmlParserResults(
        xmlFileParser.getIssues(),
        xmlFileParser.getRuleDefinitions()
    );
  }
}
