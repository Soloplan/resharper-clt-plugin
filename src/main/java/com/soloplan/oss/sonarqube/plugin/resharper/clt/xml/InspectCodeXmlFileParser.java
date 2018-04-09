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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.xml;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.XmlParserErrorSeverity;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.Converter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.SonarQubeRuleDefinitionProvider;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.function.Predicate;

/**
 * An implementation of the SAX parser that is capable to parse XML files provided as a result of running the {@code InspectCode} command
 * line tool while minimizing the memory footprint. This class will construct a new {@link InspectCodeIssueDefinitionModel} instance for
 * each issue type definition found within the XML file and verify if it is considered valid for the use case by evaluating the internally
 * stored {@link Predicate} {@link #validInspectCodeIssueDefinitionPredicate} before inserting it into the resulting collection. After the
 * file has been parsed, the resulting collection can be retrieved via a call to {@link #getRuleDefinitions()}.
 */
public class InspectCodeXmlFileParser
    extends DefaultHandler
    implements SonarQubeRuleDefinitionProvider {

  // region XML element and attribute names

  /**
   * Defines the name of the {@code IssueType} XML element.
   */
  private static final String ELEMENT_NAME_ISSUETYPE = "IssueType";

  /**
   * Defines the name of the {@code Id} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_ID = "Id";

  /**
   * Defines the name of the {@code Category} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_CATEGORY = "Category";

  /**
   * Defines the name of the {@code CategoryId} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_CATEGORYID = "CategoryId";

  /**
   * Defines the name of the {@code SubCategory} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_SUBCATEGORY = "SubCategory";

  /**
   * Defines the name of the {@code Description} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_DESCRIPTION = "Description";

  /**
   * Defines the name of the {@code Severity} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_SEVERITY = "Severity";

  /**
   * Defines the name of the {@code WikiUrl} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_WIKIURL = "WikiUrl";

  /**
   * Defines the name of the {@code Global} XML attribute which is set for the {@value ELEMENT_NAME_ISSUETYPE} XML element.
   */
  private static final String ATTRIBUTE_NAME_GLOBAL = "Global";

  // endregion

  /**
   * Gets an implementation of the {@link Logger} interface for this class.
   * <p/>
   * Please note, that message arguments are defined with {@code {}}, but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  private static final Logger LOGGER = Loggers.get(InspectCodeXmlFileParser.class);

  /**
   * Stores a reference to an implementation of the {@link Converter} interface used to convert the parsed instances of class {@link
   * InspectCodeIssueDefinitionModel} to valid {@link SonarQubeRuleDefinitionModel} instances.
   */
  @NotNull
  private final Converter<InspectCodeIssueDefinitionModel, SonarQubeRuleDefinitionModel> sonarQubeRuleDefinitionConverter;

  /**
   * A {@link Collection} of {@link InspectCodeIssueDefinitionModel} instances that have been parsed successfully and considered valid
   * according to the evaluation of the {@link #validInspectCodeIssueDefinitionPredicate}.
   */
  @NotNull
  private final Collection<InspectCodeIssueDefinitionModel> parsedIssueDefinitions = new LinkedHashSet<>(32);

  /**
   * A {@link Predicate} used to check if the value of {@link #currentIssueDefinition} should be considered valid.
   */
  @NotNull
  private final Predicate<InspectCodeIssueDefinitionModel> validInspectCodeIssueDefinitionPredicate;

  /**
   * The {@link InspectCodeIssueDefinitionModel} that is currently being parsed by the SAX parser implementation.
   */
  private InspectCodeIssueDefinitionModel currentIssueDefinition = null;

  /**
   * Creates a new instance of the {@link InspectCodeXmlFileParser} class, which will use the supplied implementation of the {@link
   * Converter} interface to convert the parsed {@link InspectCodeIssueDefinitionModel} instances to valid {@link
   * SonarQubeRuleDefinitionModel} instances after each call to {@link #getRuleDefinitions()}. The supplied {@link Predicate}s are combined
   * using a logical {@code and} and will be used to decide whether the parsed issue definitions are valid.
   *
   * @param ruleDefinitionConverter
   *     An implementation of the {@link Converter} interface used to convert the parsed {@link InspectCodeIssueDefinitionModel} instances
   *     to valid {@link SonarQubeRuleDefinitionModel} instances.
   * @param filterPredicates
   *     All supplied {@link Predicate}s are combined using a logical {@code and} and will be used to decide whether the parsed issue
   *     definitions are valid and should be added to the resulting collection.
   */
  public InspectCodeXmlFileParser(
      @NotNull Converter<InspectCodeIssueDefinitionModel, SonarQubeRuleDefinitionModel> ruleDefinitionConverter,
      @NotNull Collection<Predicate<InspectCodeIssueDefinitionModel>> filterPredicates) {
    // Store a reference to the supplied rule definition converter
    this.sonarQubeRuleDefinitionConverter = ruleDefinitionConverter;

    // Combine all supplied filter predicates
    if (filterPredicates.isEmpty()) {
      this.validInspectCodeIssueDefinitionPredicate = x -> true;
    } else {
      // Helper variable
      Predicate<InspectCodeIssueDefinitionModel> matchAllPredicate = null;

      // Iterate and combine all supplied predicates to a single predicate using and
      for (Predicate<InspectCodeIssueDefinitionModel> filterPredicate : filterPredicates) {
        // Check if the helper variable is not yet set
        if (matchAllPredicate == null) {
          matchAllPredicate = filterPredicate;
        } else {
          // Combine the previous predicate with the current predicate using and
          matchAllPredicate = matchAllPredicate.and(filterPredicate);
        }
      }

      // Store the combined filter predicate within the internal variable
      this.validInspectCodeIssueDefinitionPredicate = matchAllPredicate;
    }
  }

  @NotNull
  @Override
  public Collection<SonarQubeRuleDefinitionModel> getRuleDefinitions() {
    return this.sonarQubeRuleDefinitionConverter.convert(this.parsedIssueDefinitions);
  }

  @Override
  public void startDocument()
      throws SAXException {
    super.startDocument();

    // Clear the resulting collection of rules before parsing the XML document
    this.parsedIssueDefinitions.clear();
  }

  @Override
  public void endDocument()
      throws SAXException {
    super.endDocument();
  }

  @Override
  public void startElement(String uri, String localName, String qualifiedName, Attributes attributes)
      throws SAXException {
    super.startElement(uri, localName, qualifiedName, attributes);

    if (qualifiedName != null && qualifiedName.trim().equals(ELEMENT_NAME_ISSUETYPE)) {
      // Retrieve the value of attribute 'id' from the current 'IssueType' XML element
      final String ruleDefinitionIdentifier = attributes.getValue(ATTRIBUTE_NAME_ID);
      if (ruleDefinitionIdentifier == null || ruleDefinitionIdentifier.trim().isEmpty()) {
        return;
      }

      // Create a new rule definition instance
      this.currentIssueDefinition = new InspectCodeIssueDefinitionModel(ruleDefinitionIdentifier);

      // Parse all attributes of the XML element that just started
      final int length = attributes.getLength();
      for (int index = 0; index < length; index++) {
        // Retrieve the name of the attribute and trim any leading or trailing whitespaces
        switch (attributes.getQName(index).trim()) {
          case ATTRIBUTE_NAME_CATEGORY:
            this.currentIssueDefinition.setCategory(attributes.getValue(index));
            break;
          case ATTRIBUTE_NAME_CATEGORYID:
            this.currentIssueDefinition.setCategoryId(attributes.getValue(index));
            break;
          case ATTRIBUTE_NAME_SUBCATEGORY:
            this.currentIssueDefinition.setSubCategory(attributes.getValue(index));
            break;
          case ATTRIBUTE_NAME_DESCRIPTION:
            this.currentIssueDefinition.setDescription(attributes.getValue(index));
            break;
          case ATTRIBUTE_NAME_SEVERITY:
            this.currentIssueDefinition.setSeverity(attributes.getValue(index));
            break;
          case ATTRIBUTE_NAME_WIKIURL:
            this.currentIssueDefinition.setWikiUrl(attributes.getValue(index));
            break;
          case ATTRIBUTE_NAME_GLOBAL:
            this.currentIssueDefinition.setGlobalRuleDefinition(attributes.getValue(index));
            break;
          default:
            LOGGER.debug("Unhandled XML attribute {} found for element {} while parsing.", attributes.getQName(index), qualifiedName);
            break;
        }
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qName)
      throws SAXException {
    super.endElement(uri, localName, qName);

    // Check if the name of the XML element that just ended matches 'IssueType'
    if (qName != null && qName.trim().equalsIgnoreCase(ELEMENT_NAME_ISSUETYPE)) {
      // Check if the parsed issue definition matches all predicates
      if (this.validInspectCodeIssueDefinitionPredicate.test(this.currentIssueDefinition)) {
        // Add the parsed issue to the set and clear the member variable
        this.parsedIssueDefinitions.add(this.currentIssueDefinition);
      }

      // Reset the internal variable
      this.currentIssueDefinition = null;
    }
  }

  @Override
  public void warning(SAXParseException e)
      throws SAXException {
    super.warning(e);
    this.handleParseException(XmlParserErrorSeverity.Warning, e);
  }

  @Override
  public void error(SAXParseException e)
      throws SAXException {
    super.error(e);
    this.handleParseException(XmlParserErrorSeverity.Error, e);
  }

  @Override
  public void fatalError(SAXParseException e)
      throws SAXException {
    super.fatalError(e);
    this.handleParseException(XmlParserErrorSeverity.Fatal, e);
  }

  @Override
  public String toString() {
    return "InspectCodeXmlFileParser{" +
        "currentIssueDefinition=" + currentIssueDefinition +
        ", size of parsedIssueDefinitions=" + parsedIssueDefinitions.size() +
        '}';
  }

  /**
   * Handles exceptions that occur during the parsing of an XML file in order to log additional information that could help solve the
   * underlying problem, if any.
   *
   * @param errorSeverity
   *     Defines the impact the supplied exception has on the process of parsing.
   * @param saxParseException
   *     The exception that has been thrown while parsing the XML document.
   */
  private void handleParseException(XmlParserErrorSeverity errorSeverity, SAXParseException saxParseException) {
    String systemId = saxParseException.getSystemId();

    // Ensure that the 'systemId' always contains a useful string
    if (systemId == null) {
      systemId = "null";
    } else {
      systemId = systemId.trim();
      if (systemId.isEmpty()) {
        systemId = "(empty)";
      }
    }

    final String errorMessage =
        errorSeverity.name() +
            ": URI=" + systemId +
            ", Line=" + saxParseException.getLineNumber() +
            ", Column=" + saxParseException.getColumnNumber() +
            ": " + saxParseException.getMessage();

    switch (errorSeverity) {
      case Info:
        LOGGER.info(errorMessage, saxParseException);
        break;
      case Warning:
        LOGGER.warn(errorMessage, saxParseException);
        break;
      case Error:
      case Fatal:
      default:
        LOGGER.error(errorMessage, saxParseException);
        break;
    }
  }
}
