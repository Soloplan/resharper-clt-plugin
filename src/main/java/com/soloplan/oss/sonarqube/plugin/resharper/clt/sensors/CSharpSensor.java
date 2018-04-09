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
import com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.CSharpLanguage;
import org.sonar.api.batch.fs.FileSystem;
import org.sonar.api.batch.rule.ActiveRule;
import org.sonar.api.batch.rule.ActiveRules;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.batch.sensor.SensorContext;
import org.sonar.api.batch.sensor.SensorDescriptor;
import org.sonar.api.rule.RuleKey;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import java.io.File;
import java.util.Collection;
import java.util.Set;
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
    // Retrieve the path to the XML output file of the InspectCode command line tool
    final String inspectCodeReportFilePath =
        context.config().get(ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH).orElse("");

    // Log an error message if the value of the configuration is not set
    if (inspectCodeReportFilePath.trim().isEmpty()) {
      LOGGER.error(
          "Can't analyze InspectCode report because the XML file to analyze is not defined. " +
              "Please set property {} to refer to the XML output file of the InspectCode command line tool.",
          ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH);
      return;
    }

    // Access the output file of the InspectCoe tool defined via configuration property and validate it
    final File inspectCodeReportFile = new File(inspectCodeReportFilePath);
    if (!inspectCodeReportFile.exists()) {
      LOGGER.error(
          "Can't analyze InspectCode report because the XML file to analyze defined by property {} does not exist: '{}'.",
          ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH,
          inspectCodeReportFilePath);
    } else if (!inspectCodeReportFile.isFile()) {
      LOGGER.error(
          "Can't analyze InspectCode report because the XML file defined by property {} is not a file: '{}'.",
          ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH,
          inspectCodeReportFilePath);
    }

    // NOTE: Bunch of debug code lines
    final ActiveRules activeRules = context.activeRules();
    final Collection<ActiveRule> allRules = activeRules.findAll();
    final Collection<ActiveRule> repoRules =
        activeRules.findByRepository(ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY);
    final Collection<ActiveRule> languageRules =
        activeRules.findByLanguage(CSharpLanguage.LANGUAGE_NAME);

    LOGGER.info("Active rules: total={}, in repo={}, language={}", allRules.size(), repoRules.size(), languageRules.size());
    System.out.println("Active rules: total=" + allRules.size() + ", in repo=" + repoRules.size()
        + ", language=" + languageRules.size() + ".");
    // End of bunch of debug code lines

    // Stop if no rules are activated for the repository
    if (repoRules.isEmpty()) {
      LOGGER.info("There are no active rules for repository '" + ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY + "'.");
      return;
    }

    // Stop if no C# source files are present
    final FileSystem fileSystem = context.fileSystem();
    if (!fileSystem.hasFiles(fileSystem.predicates().hasLanguage(CSharpLanguage.LANGUAGE_NAME))) {
      LOGGER.info("There are no source files present for language C#.");
      return;
    }

    final Set<RuleKey> activeRuleKeySet = repoRules.parallelStream().map(ActiveRule::ruleKey).collect(Collectors.toSet());

    // Note: Found issue types are contained within the XMl file, as well.
    //       Maybe we could parse them and further restrict the collection of active rules, in order to restrict the iterations


    // TODO implement this
  }
}
