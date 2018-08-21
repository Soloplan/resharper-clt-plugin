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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.rules;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.converters.InspectCodeIssueDefinitionToSonarQubeRuleDefinitionConverter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.converters.InspectCodeIssueToSonarQubeIssueConverter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.CSharpLanguage;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionOverrideModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates.InspectCodePredicates;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates.ObjectPredicates;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.InspectCodeXmlFileParser;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.InspectCodeXmlFileValidator;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.SonarQubeRuleDefinitionOverrideXmlFileParser;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.config.Configuration;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link RulesDefinition} interface which will create a new implementation of the {@link
 * org.sonar.api.server.rule.RulesDefinition.Repository} in order to provide additional rules for the C# language to SonarQube.
 */
public class CSharpRulesDefinition
    implements RulesDefinition {

  /**
   * Gets an implementation of the {@link Logger} interface for this class. Please note, that message arguments are defined with {@code {}},
   * but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  private static final Logger LOGGER = Loggers.get(CSharpRulesDefinition.class);

  /** Stores a reference to an instance of the {@link Configuration} class provided to the constructor by the SonarQube instance. */
  private Configuration configuration;

  /**
   * Creates a new instance of the {@link CSharpRulesDefinition} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param config
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public CSharpRulesDefinition(Configuration config) {
    this.configuration = config;
  }

  @Override
  public void define(Context context) {
    // Create a new repository which will get all rules parsed from the InspectCode XML file
    final NewRepository csharpRulesRepository =
        context.createRepository(ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY, CSharpLanguage.LANGUAGE_NAME)
            .setName(ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_NAME);

    // Read SonarQube rule definitions from issue definitions contained in InspectCode XML file
    final Collection<SonarQubeRuleDefinitionModel> sonarQubeRuleDefinitions =
        this.getSonarQubeRuleDefinitionsFromInspectCodeFile();

    // Check if at least a single rule definition has been found
    if (!sonarQubeRuleDefinitions.isEmpty()) {

      // Parse SonarOverride file and apply its values to the SonarQubeRuleDefinitionModels before using them
      this.applySonarQubeRuleDefinitionOverrides(sonarQubeRuleDefinitions);

      // Create a new SonarQube rule for each defined issue type
      for (SonarQubeRuleDefinitionModel sonarQubeRuleDefinitionModel : sonarQubeRuleDefinitions) {
        NewRule newRule = csharpRulesRepository
            .createRule(sonarQubeRuleDefinitionModel.getRuleDefinitionKey())
            .setName(sonarQubeRuleDefinitionModel.getRuleName())
            .setSeverity(sonarQubeRuleDefinitionModel.getSonarQubeSeverity().getSonarQubeSeverityValue())
            .setType(sonarQubeRuleDefinitionModel.getSonarQubeRuleType().getRuleType())
            .setStatus(sonarQubeRuleDefinitionModel.getRuleStatus())
            .setActivatedByDefault(sonarQubeRuleDefinitionModel.isActivatedByDefault());

        // Set description using corresponding method
        switch (sonarQubeRuleDefinitionModel.getRuleDescriptionSyntax()) {
          case MARKDOWN:
            newRule.setMarkdownDescription(sonarQubeRuleDefinitionModel.getRuleDescription());
            break;
          case HTML:
          default:
            newRule.setHtmlDescription(sonarQubeRuleDefinitionModel.getRuleDescription());
            break;
        }

        // TODO Create actual debt remediation functions
        newRule.setDebtRemediationFunction(newRule.debtRemediationFunctions().constantPerIssue("15 min"));
      }
    }

    // Finish working with the newly created repository
    csharpRulesRepository.done();
  }

  /**
   * Retrieves a collection of {@link SonarQubeRuleDefinitionModel} instances from the {@code InspectCode} issue definition file which is
   * contained within the resources of the plugin.
   *
   * @return A collection of {@link SonarQubeRuleDefinitionModel} instances parsed from the {@code InspectCode} issue definition file
   *     located in the plugin's resources.
   */
  @NotNull
  private Collection<SonarQubeRuleDefinitionModel> getSonarQubeRuleDefinitionsFromInspectCodeFile() {
    // NOTE: Should be replaced by something more configurable
    final String resourceName = "/com/jetbrains/resharper/inspectcode/inspectcode_2017-3-3.xml";

    // Initialize the resulting variable so it won't be null
    Collection<SonarQubeRuleDefinitionModel> parsedRuleDefinitions = Collections.emptyList();

    // Declare the input stream upfront instead of using try-with-resource, because we might need to wrap it for schema validation
    InputStream inputStream = null;
    //noinspection TryFinallyCanBeTryWithResources (See comment line above)
    try {
      // Retrieve the XML file resource to parse
      inputStream = this.getClass().getResourceAsStream(resourceName);

      if (inputStream == null) {
        LOGGER.error("Could not find resource '%s'.", resourceName);
      } else {
        // Check if XML file validation is enabled
        final boolean doValidateFile = this.configuration != null
            ? this.configuration.getBoolean(ReSharperCltConfiguration.PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION).orElse(false)
            : false;

        // Start XML schema validation only if enabled, otherwise assume the file is valid
        if (doValidateFile && !this.validateInspectCodeIssueDefinitionFile(inputStream)) {
          LOGGER.error("Verification of XML file using the internal XML Schema Definition has failed.");
        } else {
          // Parse XML file containing all declared inspect code issues
          parsedRuleDefinitions = this.parseInspectCodeIssueDefinitions(inputStream);
        }
      }
    } catch (Exception e) {
      LOGGER.error("An exception occurred while trying to parse the data stream of the XML file.", e);
    } finally {
      // Close the input stream after verification and parsing
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ignored) { /* ignored */ }
      }
    }

    return parsedRuleDefinitions;
  }

  /**
   * Parses all {@code InspectCode} issue definitions that seem to have a meaningful usage for the C# language from the supplied {@code
   * xmlFileInputStream} and converts them to valid {@link SonarQubeRuleDefinitionModel} instances.
   *
   * @param xmlFileInputStream
   *     An {@link InputStream} of an XML file which contains {@code InspectCode} issue definitions to be parsed and converted to SonarQube
   *     rule definitions.
   *
   * @return A {@link Collection} of {@link SonarQubeRuleDefinitionModel} instances that seem to be valid for the C# language.
   */
  private Collection<SonarQubeRuleDefinitionModel> parseInspectCodeIssueDefinitions(@NotNull InputStream xmlFileInputStream) {
    // Create a new SAX parser implementation that will parse and convert the XML file of the InspectCode command line tool
    final InspectCodeXmlFileParser xmlFileParser = new InspectCodeXmlFileParser(
        new InspectCodeIssueDefinitionToSonarQubeRuleDefinitionConverter(),
        new InspectCodeIssueToSonarQubeIssueConverter(),
        Arrays.asList(
            ObjectPredicates.isNotNullPredicate(),
            InspectCodePredicates.hasValidIssueSeverity(),
            InspectCodePredicates.hasNonEmptyIssueDescription(),
            InspectCodePredicates.isCSharpIssueDefinition(),
            InspectCodePredicates.isVisualBasicIssueDefinition().negate(),
            InspectCodePredicates.isWebRelatedCategory().negate()),
        Collections.singletonList(x -> false),  // Rule definitions should not parse any actual issues
        Collections.singletonList(x -> false)); // Rule definitions should not parse any actual issues
    try {
      final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
      saxParser.parse(xmlFileInputStream, xmlFileParser);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      LOGGER.error("An exception occurred while trying to parse the data stream of the XML file.", e);
    }
    return xmlFileParser.getRuleDefinitions();
  }

  /**
   * Validates the content of the {@code InspectCode} issue definition file using an XML schema definition.
   *
   * @param xmlFileInputStream
   *     The {@link InputStream} of the {@code InspectCode} issue definition file.
   *
   * @return {@code True} if the validation of the {@code InspectCode} issue definition file using an XML schema succeeded; otherwise {@code
   *     false}.
   */
  private boolean validateInspectCodeIssueDefinitionFile(@NotNull final InputStream xmlFileInputStream) {
    // Result variable
    boolean success;

    // Wrap the resource into a BufferedInputStream because we will reset it later on
    BufferedInputStream bufferedInputStream = new BufferedInputStream(xmlFileInputStream);
    if (xmlFileInputStream.markSupported()) {
      xmlFileInputStream.mark(Integer.MAX_VALUE);
    }
    // TODO Validate XML file without updating it: https://stackoverflow.com/questions/2991091/java-xsd-validation-of-xml-without-namespace
    success = InspectCodeXmlFileValidator.validateXmlFile(xmlFileInputStream);

    // Reset the input stream to its original position if the XML file has been validated
    if (bufferedInputStream.markSupported()) {
      try {
        bufferedInputStream.reset();
      } catch (IOException exception) {
        LOGGER.error("An exception occurred while trying to reset the stream after XML schema validation.", exception);
        success = false;
      }
    } else {
      LOGGER.error("Could not reset stream after XML schema validation.");
      success = false;
    }

    return success;
  }

  /**
   * Parses the SonarQube overrides XML file from the plugins resources and applies its values to the supplied collection of {@link
   * SonarQubeRuleDefinitionModel}s.
   *
   * @param sonarQubeRuleDefinitionModels
   *     The collection of {@link SonarQubeRuleDefinitionModel} instances that will be updated using {@link
   *     SonarQubeRuleDefinitionOverrideModel} instances parsed from the XML file.
   */
  private void applySonarQubeRuleDefinitionOverrides(@NotNull final Collection<SonarQubeRuleDefinitionModel> sonarQubeRuleDefinitionModels) {
    try {
      // Parse all SonarQube rule definition overrides from the XML file and create a map using the unique rule identifier as key
      final Map<String, SonarQubeRuleDefinitionOverrideModel> ruleDefinitionOverrideMap =
          this.parseSonarQubeRuleDefinitionOverrides().parallelStream()
              .collect(Collectors.toMap(SonarQubeRuleDefinitionOverrideModel::getRuleDefinitionKey, item -> item));

      LOGGER.debug("Found %d applicable rule overrides.", ruleDefinitionOverrideMap.size());

      // Helper variable
      SonarQubeRuleDefinitionOverrideModel ruleDefinitionOverrideModel;

      // Iterate all SonarQube rule definitions and apply the override if present
      for (SonarQubeRuleDefinitionModel sonarQubeRuleDefinitionModel : sonarQubeRuleDefinitionModels) {
        // Stop iterating over the rule definitions if no more overrides are available
        if (ruleDefinitionOverrideMap.isEmpty()) {
          LOGGER.debug("There are no more rule definition overrides left.");
          break;
        }

        // Remove the override from the map as it is no longer used in order to improve the speed of future look ups
        ruleDefinitionOverrideModel = ruleDefinitionOverrideMap.remove(sonarQubeRuleDefinitionModel.getRuleDefinitionKey());

        // If a matching override could be retrieved from the map, apply its values
        if (ruleDefinitionOverrideModel != null) {
          LOGGER.debug("Applying rule definition override for rule '%s'.", sonarQubeRuleDefinitionModel.getRuleDefinitionKey());
          sonarQubeRuleDefinitionModel.setSonarQubeRuleType(ruleDefinitionOverrideModel.getSonarQubeRuleType());
          sonarQubeRuleDefinitionModel.setSonarQubeSeverity(ruleDefinitionOverrideModel.getSonarQubeSeverity());
        }
      }

      LOGGER.debug("Application of rule definition overrides has finished.");

    } catch (Exception exception) {
      LOGGER.error("An unhandled exception occurred during application of the rule definition overrides.", exception);
    }
  }

  /**
   * Parses all {@code SonarQube} compatible rule definition overrides from the resources file {@code inspectcode-overrides.xml} and
   * converts them to valid {@link SonarQubeRuleDefinitionOverrideModel} instances.
   *
   * @return A {@link Collection} of {@link SonarQubeRuleDefinitionOverrideModel} instances parsed from the XML resource file.
   */
  private Collection<SonarQubeRuleDefinitionOverrideModel> parseSonarQubeRuleDefinitionOverrides() {
    // NOTE: Should be replaced by something more configurable
    final String resourceName = "/com/jetbrains/resharper/inspectcode/inspectcode-overrides.xml";

    // Initialize the resulting variable so it won't be null
    Collection<SonarQubeRuleDefinitionOverrideModel> sonarQubeRuleDefinitionOverrides = Collections.emptyList();

    // Declare the input stream upfront instead of using try-with-resource, because we might need to wrap it for schema validation
    InputStream inputStream = null;
    //noinspection TryFinallyCanBeTryWithResources (See comment line above)
    try {
      // Retrieve the XML file resource to parse
      inputStream = this.getClass().getResourceAsStream(resourceName);

      if (inputStream == null) {
        LOGGER.error("Could not find resource '%s'.", resourceName);
      } else {
        // Check if XML file validation is enabled
        final boolean doValidateFile = this.configuration != null
            ? this.configuration.getBoolean(ReSharperCltConfiguration.PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION).orElse(false)
            : false;

        // Start XML schema validation only if enabled, otherwise assume the file is valid
        if (doValidateFile && !this.validateSonarQubeRuleDefinitionOverridesFile(inputStream)) {
          LOGGER.error("Verification of XML file using the internal XML Schema Definition has failed.");
        } else {
          // Parse XML file containing all rule definition overrides
          sonarQubeRuleDefinitionOverrides = this.parseSonarQubeRuleDefinitionOverrides(inputStream);
        }
      }
    } catch (Exception e) {
      LOGGER.error("An exception occurred while trying to parse the data stream of the XML file.", e);
    } finally {
      // Close the input stream after verification and parsing
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ignored) { /* ignored */ }
      }
    }

    return sonarQubeRuleDefinitionOverrides;
  }

  /**
   * Validates the content of the {@code SonarQube} compatible rule definition overrides file using an XML schema definition.
   *
   * @param xmlFileInputStream
   *     The {@link InputStream} of the {@code SonarQube} compatible rule definition overrides file.
   *
   * @return {@code True} if the validation of the {@code SonarQube} compatible rule definition overrides file using an XML schema
   *     succeeded; otherwise {@code false}.
   */
  private boolean validateSonarQubeRuleDefinitionOverridesFile(@NotNull final InputStream xmlFileInputStream) {
    // Result variable
    boolean success;

    // Wrap the resource into a BufferedInputStream because we will reset it later on
    BufferedInputStream bufferedInputStream = new BufferedInputStream(xmlFileInputStream);
    if (xmlFileInputStream.markSupported()) {
      xmlFileInputStream.mark(Integer.MAX_VALUE);
    }
    // TODO Validate XML file without updating it: https://stackoverflow.com/questions/2991091/java-xsd-validation-of-xml-without-namespace
    success = true;

    // Reset the input stream to its original position if the XML file has been validated
    if (bufferedInputStream.markSupported()) {
      try {
        bufferedInputStream.reset();
      } catch (IOException exception) {
        LOGGER.error("An exception occurred while trying to reset the stream after XML schema validation.", exception);
        success = false;
      }
    } else {
      LOGGER.error("Could not reset stream after XML schema validation.");
      success = false;
    }

    return success;
  }

  /**
   * Parses all SonarQube compatible rule definition overrides from the supplied {@code xmlFileInputStream} and converts them to valid
   * {@link SonarQubeRuleDefinitionOverrideModel} instances.
   *
   * @param xmlFileInputStream
   *     An {@link InputStream} of an XML file which contains SonarQube compatible rule definition override to be parsed and converted to
   *     instances of the corresponding model class.
   *
   * @return A {@link Collection} of {@link SonarQubeRuleDefinitionOverrideModel} instances parsed from the input stream.
   */
  private Collection<SonarQubeRuleDefinitionOverrideModel> parseSonarQubeRuleDefinitionOverrides(@NotNull InputStream xmlFileInputStream) {
    // Create a new SAX parser implementation that will parse and convert the XML file containing the rule definition overrides
    final SonarQubeRuleDefinitionOverrideXmlFileParser xmlFileParser = new SonarQubeRuleDefinitionOverrideXmlFileParser();
    try {
      final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
      saxParser.parse(xmlFileInputStream, xmlFileParser);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      LOGGER.error("An exception occurred while trying to parse the data stream of the XML file.", e);
    }
    return xmlFileParser.getRuleDefinitionOverrides();
  }
}
