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
import java.util.*;
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

    // TODO: Restrict parsing to a single project?

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
            InspectCodePredicates.isValidLineNumber()));

    try (FileInputStream reportFileInputStream = new FileInputStream(inspectCodeReportFile)) {
      try {
        final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
        saxParser.parse(reportFileInputStream, xmlFileParser);
      } catch (ParserConfigurationException | SAXException | IOException exception) {
        LOGGER.error(
            "An exception occurred while trying to parse the data stream of the report XML file " + inspectCodeReportFile + ".",
            exception);
      }
    } catch (IOException ioe) {
      LOGGER.error("Could not open file " + inspectCodeReportFile + " for parsing.", ioe);
      return;
    }

    // Retrieve the issue type definition identifiers from the InspectCode xml file, which are used as rule keys
    final Set<String> occurredIssueDefinitions = xmlFileParser.getRuleDefinitions()
        .parallelStream()
        .map(SonarQubeRuleDefinitionModel::getRuleDefinitionKey)
        .collect(Collectors.toSet());

    // Create a map consisting of the InspectCode issue type definition identifier as key and the corresponding SonarQube RuleKey,
    // but only for those issue definitions, that are contained within the InspectCode report XML file
    final Map<String, RuleKey> ruleKeyMap =
        activeRuleCollection.parallelStream()
            .filter(activeRule -> occurredIssueDefinitions.contains(activeRule.ruleKey().rule()))
            .collect(Collectors.toMap(activeRule -> activeRule.ruleKey().rule(), ActiveRule::ruleKey));

    // TODO: Use configuration property ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE to retrieve the solution directory

    // Retrieve the path to the XML output file of the InspectCode command line tool from the configuration and try to access the file
    // Retrieve the base directory where the C# solution file is located
    final String baseDirectoryPath = this.getBaseDirectory(context);

    // Iterate all issues found within the InspectCode report XML file
    for (SonarQubeIssueModel sonarQubeIssueModel : xmlFileParser.getIssues()) {
      if (!ruleKeyMap.containsKey(sonarQubeIssueModel.getRuleKey())) {
        LOGGER.warn(
            "Could not find rule definition identifier {} within the set of active SonarQube rules.",
            sonarQubeIssueModel.getRuleKey());
        this.logSkippedIssue(sonarQubeIssueModel);
        continue;
      }

      InputFile sourceCodeFile = fileSystem.inputFile(
          fileSystem.predicates().hasPath(baseDirectoryPath + sonarQubeIssueModel.getFilePath()));
      if (sourceCodeFile == null || !sourceCodeFile.isFile()) {
        LOGGER.warn("Could not find source code file {} using the SonarQube FileSystem API.", sonarQubeIssueModel.getFilePath());
        this.logSkippedIssue(sonarQubeIssueModel);
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
   * Retrieves the path to the solution file via configuration property {@value ReSharperCltConfiguration#PROPERTY_KEY_SOLUTION_FILE} and
   * tries to verify that the solution file referenced by the property does exist. Otherwise a warning message is logged and the path of the
   * base directory is returned.
   *
   * @param context
   *     An implementation of the {@link SensorContext} used to access the plugin configuration in order to retrieve the value of property
   *     {@value ReSharperCltConfiguration#PROPERTY_KEY_SOLUTION_FILE}.
   *
   * @return The absolute path to the base directory where the solution file is located.
   */
  @NotNull
  private String getBaseDirectory(@NotNull final SensorContext context) {
    // Helper variables
    final FileSystem fileSystem = context.fileSystem();
    final String baseDirectoryPath;

    // Check if the configuration property value has a non-default value
    if (!context.config().hasKey(ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE)) {
      LOGGER.info(
          "Property {} is not defined, using default value to locate the solution file.",
          ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE);
      // Set the helper variable to null
      baseDirectoryPath = null;
    } else {
      // Retrieve the configuration property value that refers to the solution file
      final Optional<String> configPath = context.config().get(ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE);

      // Check if the configuration property value is set
      if (configPath.isPresent()) {
        // Try to find a corresponding file using the FileSystem API
        final File file = fileSystem.resolvePath(configPath.get());
        if (file == null || !file.exists()) {
          LOGGER.warn(
              "Could not locate solution file at {}. Using default value to locate the solution file.",
              configPath.get());
          // Set the helper variable to null
          baseDirectoryPath = null;
        } else {
          // Retrieve the base directory where the solution is located
          baseDirectoryPath = (file.isFile() ? file.getParent() : file.getAbsolutePath() + File.separator);
        }
      } else {
        LOGGER.warn(
            "Property {} is defined, but its value is null. Using default value to locate the solution file.",
            ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE);
        // Set the helper variable to null
        baseDirectoryPath = null;
      }
    }

    // Check if the configuration property did not provide a valid path to the solution file and return the default value
    return baseDirectoryPath != null ? baseDirectoryPath : fileSystem.baseDir().getParentFile() + File.separator;
  }

  /**
   * Logs a message that the supplied {@code sonarQubeIssueModel} has been skipped using the class-private {@link #LOGGER}.
   *
   * @param sonarQubeIssueModel
   *     The issue that has been skipped.
   */
  private void logSkippedIssue(@NotNull final SonarQubeIssueModel sonarQubeIssueModel) {
    LOGGER.info(
        "Skipping issue for rule {}, which occurred at line {} in range {}-{} of file {}.",
        sonarQubeIssueModel.getRuleKey(),
        sonarQubeIssueModel.getTextRange().start().line(),
        sonarQubeIssueModel.getTextRange().start().lineOffset(),
        sonarQubeIssueModel.getTextRange().end().lineOffset(),
        sonarQubeIssueModel.getFilePath());
  }
}
