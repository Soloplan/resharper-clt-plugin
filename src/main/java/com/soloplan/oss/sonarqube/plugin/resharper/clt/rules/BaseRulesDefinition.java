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
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.XmlDataValidator;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeCategoryOverrideModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionOverrideModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.InspectCodeXmlFileParser;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.InspectCodeXmlFileValidator;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.RuleOverrideXmlFileParser;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.xml.SonarQubeRuleDefinitionOverrideXmlFileValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.config.Configuration;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class BaseRulesDefinition
        implements RulesDefinition {

  /**
   * Gets an implementation of the {@link Logger} interface for this class.
   * <p/>
   * Please note, that message arguments are defined with {@code {}}, but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  protected final Logger logger;

  /**
   * Stores a reference to an instance of the {@link RulesRepositoryConfiguration} class supplied to the constructor.
   */
  protected final RulesRepositoryConfiguration rulesRepositoryConfiguration;

  /**
   * Stores a reference to an instance of the {@link Configuration} class provided to the constructor by the SonarQube instance.
   */
  protected final Configuration configuration;

  private final RuleOverrideXmlFileParser xmlFileParser = new RuleOverrideXmlFileParser();

  /**
   * Creates a new instance of the {@link VBNetRulesDefinition} class storing a reference to the supplied {@link
   * RulesRepositoryConfiguration} instance internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param rulesRepositoryConfiguration An instance of class {@link RulesRepositoryConfiguration} containing the required configuration of this class.
   * @param config                       An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  BaseRulesDefinition(@NotNull final RulesRepositoryConfiguration rulesRepositoryConfiguration, @NotNull final Configuration config) {
    this.logger = Loggers.get(this.getClass());
    this.rulesRepositoryConfiguration = rulesRepositoryConfiguration;
    this.configuration = config;
  }

  @Override
  public void define(Context context) {
    // Create a new repository which will get all rules parsed from the InspectCode XML file
    final NewRepository rulesRepository =
            context.createRepository(this.rulesRepositoryConfiguration.repositoryKey, this.rulesRepositoryConfiguration.language)
                    .setName(this.rulesRepositoryConfiguration.repositoryName);

    // Read SonarQube rule definitions from issue definitions contained in InspectCode XML file
    final Collection<SonarQubeRuleDefinitionModel> sonarQubeRuleDefinitions =
            this.getSonarQubeRuleDefinitionsFromInspectCodeFile();

    // Check if at least a single rule definition has been found
    if (!sonarQubeRuleDefinitions.isEmpty()) {

      // Parse SonarOverride file and apply its values to the SonarQubeRuleDefinitionModels before using them
      this.applySonarQubeRuleDefinitionOverrides(sonarQubeRuleDefinitions);

      // Create a new SonarQube rule for each defined issue type
      for (SonarQubeRuleDefinitionModel sonarQubeRuleDefinitionModel : sonarQubeRuleDefinitions) {
        NewRule newRule = rulesRepository
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

    // Finish working with the newly created repository and publish the rules to SonarQube
    rulesRepository.done();
  }

  /**
   * Provides a {@link Collection} of {@link Predicate}s that will be applied to all {@link InspectCodeIssueDefinitionModel} instances
   * parsed from the {@code InspectCode} issue definition file in order to filter out any irrelevant issue definitions and keep only those
   * that are meaningful for the language for which this repository provides rules.
   *
   * @return A {@link Collection} of {@link Predicate}s that can be applied to all {@link InspectCodeIssueDefinitionModel} during parsing of
   * the {@code InspectCode} issue definition file in {@link #parseInspectCodeIssueDefinitions(InputStream)}.
   */
  @Nullable
  protected abstract Collection<Predicate<InspectCodeIssueDefinitionModel>> getIssueDefinitionFilterPredicates();

  /**
   * Retrieves a collection of {@link SonarQubeRuleDefinitionModel} instances from the {@code InspectCode} issue definition file which is
   * contained within the resources of the plugin.
   *
   * @return A collection of {@link SonarQubeRuleDefinitionModel} instances parsed from the {@code InspectCode} issue definition file
   * located in the plugin's resources.
   */
  @NotNull
  private Collection<SonarQubeRuleDefinitionModel> getSonarQubeRuleDefinitionsFromInspectCodeFile() {
    // TODO: Replace this with something more configurable
    final String resourceName = "/com/jetbrains/resharper/inspectcode/inspectcode_issue_definitions.xml";

    // Initialize the resulting variable so it won't be null
    Collection<SonarQubeRuleDefinitionModel> parsedRuleDefinitions = Collections.emptyList();

    // Declare the input stream upfront instead of using try-with-resource, because we might need to wrap it for schema validation
    InputStream inputStream = null;
    //noinspection TryFinallyCanBeTryWithResources (See comment line above)
    try {
      // Retrieve the XML file resource to parse
      inputStream = this.getClass().getResourceAsStream(resourceName);

      if (inputStream == null) {
        this.logger.error("Could not find resource '%s'.", resourceName);
      } else {
        // Check if XML file validation is enabled
        final boolean doValidateFile = this.configuration != null
                ? this.configuration.getBoolean(ReSharperCltConfiguration.PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION).orElse(false)
                : false;

        // Start XML schema validation only if enabled, otherwise assume the file is valid
        if (doValidateFile && !this.validateXmlData(inputStream, new InspectCodeXmlFileValidator())) {
          this.logger.error("Verification of XML file using the internal XML Schema Definition has failed.");
        } else {
          // Parse XML file containing all declared inspect code issues
          parsedRuleDefinitions = this.parseInspectCodeIssueDefinitions(inputStream);
        }
      }
    } catch (Exception e) {
      this.logger.error("An exception occurred while trying to parse the data stream of the XML file.", e);
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
   * @param xmlFileInputStream An {@link InputStream} of an XML file which contains {@code InspectCode} issue definitions to be parsed and converted to SonarQube
   *                           rule definitions.
   * @return A {@link Collection} of {@link SonarQubeRuleDefinitionModel} instances that seem to be valid for the C# language.
   */
  private Collection<SonarQubeRuleDefinitionModel> parseInspectCodeIssueDefinitions(@NotNull InputStream xmlFileInputStream) {
    // Create a new SAX parser implementation that will parse and convert the XML file of the InspectCode command line tool
    final InspectCodeXmlFileParser xmlFileParser = new InspectCodeXmlFileParser(
            new InspectCodeIssueDefinitionToSonarQubeRuleDefinitionConverter(),
            new InspectCodeIssueToSonarQubeIssueConverter(),
            this.getIssueDefinitionFilterPredicates(),
            Collections.singletonList(x -> false),  // Rule definitions should not parse any actual issues
            Collections.singletonList(x -> false)); // Rule definitions should not parse any actual issues
    try {
      final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
      saxParser.parse(xmlFileInputStream, xmlFileParser);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      this.logger.error("An exception occurred while trying to parse the data stream of the XML file.", e);
    }
    return xmlFileParser.getRuleDefinitions();
  }

  /**
   * Parses all {@code SonarQube} compatible rule definition overrides from the resources file {@code sonarqube_rule_overrides.xml} and
   * converts them to valid {@link SonarQubeRuleDefinitionOverrideModel} instances.
   *
   * @return A {@link Collection} of {@link SonarQubeRuleDefinitionOverrideModel} instances parsed from the XML resource file.
   */
  private void parseOverridesXml() {
    final String overrideFileName = "sonarqube_rule_overrides.xml";

    // the override file is either located directly in the application folder or at a path that is specified as environment variable
    // SONAR_PLUGIN_INSPECTCODE_OVERRIDEFILE
    String envPath = System.getenv("SONAR_PLUGIN_INSPECTCODE_OVERRIDEFILE");
    final String localOverrideFile = envPath != null ? envPath : overrideFileName;
    final String resourceName = "/com/jetbrains/resharper/inspectcode/" + overrideFileName;

    // Declare the input stream upfront instead of using try-with-resource, because we might need to wrap it for schema validation
    InputStream inputStream = null;
    //noinspection TryFinallyCanBeTryWithResources (See comment line above)
    try {
      // if a local override file exists: use it, otherwise use the default one from the plugin
      final File overrideFile = new File(localOverrideFile);
      if (overrideFile.exists()) {
        inputStream = new FileInputStream(overrideFile.getAbsolutePath());
      } else {
        // Retrieve the XML file resource to parse
        inputStream = this.getClass().getResourceAsStream(resourceName);
      }
      if (inputStream == null) {
        this.logger.error("Could not find resource '%s'.", resourceName);
      } else {
        // Check if XML file validation is enabled
        final boolean doValidateFile = this.configuration != null
                ? this.configuration.getBoolean(ReSharperCltConfiguration.PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION).orElse(false)
                : false;

        // Start XML schema validation only if enabled, otherwise assume the file is valid
        if (doValidateFile && !this.validateXmlData(inputStream, new SonarQubeRuleDefinitionOverrideXmlFileValidator())) {
          this.logger.error("Verification of overrides XML file using the internal XML Schema Definition has failed.");
        } else {
          // Parse XML file containing all rule definition overrides
          this.parseRuleOverrideXmlFile(inputStream);
        }
      }
    } catch (Exception e) {
      this.logger.error("An exception occurred while trying to parse and verify the data stream of the XML file.", e);
    } finally {
      // Close the input stream after verification and parsing
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException ignored) { /* ignored */ }
      }
    }
  }

  private void parseRuleOverrideXmlFile(@NotNull InputStream xmlFileInputStream) {
    // Create a new SAX parser implementation that will parse and convert the XML file containing the rule definition overrides

    try {
      final SAXParser saxParser = SAXParserFactory.newInstance().newSAXParser();
      saxParser.parse(xmlFileInputStream, xmlFileParser);
    } catch (ParserConfigurationException | SAXException | IOException e) {
      this.logger.error("An exception occurred while trying to parse the data stream of the XML file.", e);
    }
  }

  /**
   * Parses the SonarQube overrides XML file from the plugins resources and applies its values to the supplied collection of {@link
   * SonarQubeRuleDefinitionModel}s.
   *
   * @param sonarQubeRuleDefinitionModels The collection of {@link SonarQubeRuleDefinitionModel} instances that will be updated using {@link
   *                                      SonarQubeRuleDefinitionOverrideModel} instances parsed from the XML file.
   */
  private void applySonarQubeRuleDefinitionOverrides(@NotNull final Collection<SonarQubeRuleDefinitionModel> sonarQubeRuleDefinitionModels) {
    try {
      this.parseOverridesXml();

      Collection<SonarQubeRuleDefinitionOverrideModel> sonarQubeRuleDefinitionOverrides = this.xmlFileParser.getRuleDefinitionOverrides();
      Collection<InspectCodeCategoryOverrideModel> inspectCodeCategoryOverrideModels = this.xmlFileParser.getCategoryOverrides();

      // Parse all SonarQube rule definition overrides from the XML file and create a map using the unique rule identifier as key
      final Map<String, SonarQubeRuleDefinitionOverrideModel> ruleDefinitionOverrideMap =
              sonarQubeRuleDefinitionOverrides.parallelStream()
                      .collect(Collectors.toMap(SonarQubeRuleDefinitionOverrideModel::getRuleDefinitionKey, item -> item));
      this.logger.debug("Found {} applicable rule overrides.", ruleDefinitionOverrideMap.size());

      final Map<String, InspectCodeCategoryOverrideModel> categoryOverrideModelMap =
              inspectCodeCategoryOverrideModels.parallelStream()
                      .collect(Collectors.toMap(InspectCodeCategoryOverrideModel::getCategoryId, item -> item));
      this.logger.debug("Found {} applicable category overrides.", categoryOverrideModelMap.size());

      // apply the category overrides first
      for (SonarQubeRuleDefinitionModel sonarQubeRuleDefinitionModel : sonarQubeRuleDefinitionModels) {
        if (sonarQubeRuleDefinitionModel.getInspectcodeModel() == null || sonarQubeRuleDefinitionModel.getInspectcodeModel().getCategoryId() == null) {
          continue;
        }

        if (categoryOverrideModelMap.containsKey(sonarQubeRuleDefinitionModel.getInspectcodeModel().getCategoryId())) {
          this.logger.debug("Applying category override for rule '{}'.", sonarQubeRuleDefinitionModel.getRuleDefinitionKey());
          InspectCodeCategoryOverrideModel model = categoryOverrideModelMap.get(sonarQubeRuleDefinitionModel.getInspectcodeModel().getCategoryId());
          sonarQubeRuleDefinitionModel.setSonarQubeRuleType(model.getSonarQubeRuleType());
          sonarQubeRuleDefinitionModel.setSonarQubeSeverity(model.getSonarQubeSeverity());
        }
      }

      SonarQubeRuleDefinitionOverrideModel ruleDefinitionOverrideModel;

      // Iterate all SonarQube rule definitions and apply the override if present
      for (SonarQubeRuleDefinitionModel sonarQubeRuleDefinitionModel : sonarQubeRuleDefinitionModels) {
        // Stop iterating over the rule definitions if no more overrides are available
        if (ruleDefinitionOverrideMap.isEmpty()) {
          this.logger.debug("There are no more rule definition overrides left.");
          break;
        }

        // Remove the override from the map as it is no longer used in order to improve the speed of future look ups
        ruleDefinitionOverrideModel = ruleDefinitionOverrideMap.remove(sonarQubeRuleDefinitionModel.getRuleDefinitionKey());

        // If a matching override could be retrieved from the map, apply its values
        if (ruleDefinitionOverrideModel != null) {
          this.logger.debug("Applying rule definition override for rule '{}'.", sonarQubeRuleDefinitionModel.getRuleDefinitionKey());
          sonarQubeRuleDefinitionModel.setSonarQubeRuleType(ruleDefinitionOverrideModel.getSonarQubeRuleType());
          sonarQubeRuleDefinitionModel.setSonarQubeSeverity(ruleDefinitionOverrideModel.getSonarQubeSeverity());
        }
      }

      this.logger.debug("Application of rule definition overrides has finished.");

    } catch (Exception exception) {
      this.logger.error("An unhandled exception occurred during application of the rule definition overrides.", exception);
    }
  }

  /**
   * Validates the content of the supplied XML data using an XML schema definition validator.
   *
   * @param xmlDataInputStream The {@link InputStream} containing the XML data to validate.
   * @param xmlDataValidator   An implementation of the {@link XmlDataValidator} interface that is used to validate the supplied xml data.
   * @return {@code True} if the validation of the {@code InspectCode} issue definition file using an XML schema succeeded; otherwise {@code
   * false}.
   */
  private boolean validateXmlData(@NotNull final InputStream xmlDataInputStream, @NotNull XmlDataValidator xmlDataValidator) {
    // Result variable
    boolean success;

    // Wrap the resource into a BufferedInputStream because we will reset it later on
    BufferedInputStream bufferedInputStream = new BufferedInputStream(xmlDataInputStream);
    if (xmlDataInputStream.markSupported()) {
      xmlDataInputStream.mark(Integer.MAX_VALUE);
    }

    // Use the supplied XmlDataValidator implementation and store its result
    success = xmlDataValidator.validateXmlData(xmlDataInputStream);

    // Reset the input stream to its original position if the XML file has been validated
    if (bufferedInputStream.markSupported()) {
      try {
        bufferedInputStream.reset();
      } catch (IOException exception) {
        this.logger.error("An exception occurred while trying to reset the stream after XML schema validation.", exception);
        success = false;
      }
    } else {
      this.logger.error("Could not reset stream after XML schema validation.");
      success = false;
    }

    return success;
  }

  /**
   * A package private class containing required information for setting up an instance of the {@link BaseRulesDefinition} class. Using this
   * class helps keeping the parameter list of the constructor concise and readable.
   */
  static final class RulesRepositoryConfiguration {

    /**
     * The key used to uniquely identify the rule repository within SonarQube.
     */
    final String repositoryKey;

    /**
     * The name of the rule repository displayed within the web interface of SonarQube.
     */
    final String repositoryName;

    /**
     * The language identifier for which the rules will be defined.
     */
    final String language;

    /**
     * Creates a new instance of the {@link RulesRepositoryConfiguration} class with the supplied arguments. All supplied {@link String}
     * arguments will be trimmed before stored internally.
     *
     * @param repositoryKey  The key used to uniquely identify the rule repository within SonarQube.
     * @param repositoryName The name of the rule repository displayed within the web interface of SonarQube.
     * @param language       The human-readable name of the language for which the rules will be defined.
     */
    RulesRepositoryConfiguration(@NotNull final String repositoryKey, @NotNull final String repositoryName, @NotNull final String language) {
      this.repositoryKey = repositoryKey.trim();
      this.repositoryName = repositoryName.trim();
      this.language = language.trim();
    }
  }
}
