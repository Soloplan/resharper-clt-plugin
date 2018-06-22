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
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.SonarQubeIssueProvider;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.SonarQubeRuleDefinitionProvider;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeIssueModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * An implementation of the SAX parser that is capable to parse XML files provided as a result of running the {@code InspectCode} command
 * line tool while minimizing the memory footprint. This class will construct a new {@link InspectCodeIssueDefinitionModel} instance for
 * each issue type definition found within the XML file and verify if it is considered valid for the use case by evaluating the internally
 * stored {@link Predicate} {@link #validInspectCodeIssueDefinitionPredicate} before inserting it into the resulting collection. After the
 * file has been parsed, the resulting collection can be retrieved via a call to {@link #getRuleDefinitions()}.
 */
public class InspectCodeXmlFileParser
    extends DefaultHandler
    implements SonarQubeRuleDefinitionProvider, SonarQubeIssueProvider {

  // region XML element and attribute names

  // region Issue definition XML element and attribute names

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

  // endregion Issue definition XML element and attribute names

  // region InspectCode report XML element and attribute names

  /**
   * Defines the name of the {@code Information} XML element.
   */
  private static final String ELEMENT_NAME_INFORMATION = "Information";

  /**
   * Defines the name of the {@code Solution} XML element.
   */
  private static final String ELEMENT_NAME_SOLUTION = "Solution";

  /**
   * Defines the name of the {@code InspectionScope} XML element.
   */
  private static final String ELEMENT_NAME_INSPECTIONSCOPE = "InspectionScope";

  /**
   * Defines the name of the {@code Element} XML element.
   */
  private static final String ELEMENT_NAME_ELEMENT = "Element";

  /**
   * Defines the name of the {@code Issues} XML element containing a collection of {@code Project} elements.
   */
  private static final String ELEMENT_NAME_ISSUES = "Issues";

  /**
   * Defines the name of the {@code Project} XML element.
   */
  private static final String ELEMENT_NAME_PROJECT = "Project";

  /**
   * Defines the name of the {@code Issue} XML element which contains information about the detected issue within its attributes.
   */
  private static final String ELEMENT_NAME_ISSUE = "Issue";

  /**
   * Defines the name of the {@code TypeId} XML attribute which is set for the {@value ELEMENT_NAME_ISSUE} XML element.
   */
  private static final String ATTRIBUTE_NAME_TYPEID = "TypeId";

  /**
   * Defines the name of the {@code File} XML attribute which is set for the {@value ELEMENT_NAME_ISSUE} XML element.
   */
  private static final String ATTRIBUTE_NAME_FILE = "File";

  /**
   * Defines the name of the {@code Offset} XML attribute which is set for the {@value ELEMENT_NAME_ISSUE} XML element.
   */
  private static final String ATTRIBUTE_NAME_OFFSET = "Offset";

  /**
   * Defines the name of the {@code Line} XML attribute which is set for the {@value ELEMENT_NAME_ISSUE} XML element.
   */
  private static final String ATTRIBUTE_NAME_LINE = "Line";

  /**
   * Defines the name of the {@code Message} XML attribute which is set for the {@value ELEMENT_NAME_ISSUE} XML element.
   */
  private static final String ATTRIBUTE_NAME_MESSAGE = "Message";

  /**
   * Defines the name of the {@code Name} XML attribute which is set for the {@value ELEMENT_NAME_PROJECT} XML element.
   */
  private static final String ATTRIBUTE_NAME_NAME = "Name";

  // endregion InspectCode report XML element and attribute names

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
   * Stores a reference to an implementation of the {@link Converter} interface used to convert the parsed instances of class {@link
   * InspectCodeIssueModel} to valid {@link SonarQubeIssueModel} instances.
   */
  @NotNull
  private final Converter<InspectCodeIssueModel, SonarQubeIssueModel> sonarQubeIssueConverter;

  /**
   * A {@link Collection} of {@link InspectCodeIssueDefinitionModel} instances that have been parsed successfully and considered valid
   * according to the evaluation of the {@link #validInspectCodeIssueDefinitionPredicate}.
   */
  @NotNull
  private final Collection<InspectCodeIssueDefinitionModel> parsedIssueDefinitions = new LinkedHashSet<>(32);

  /**
   * A {@link Map} of {@link InspectCodeIssueModel} instances that have been parsed successfully and considered valid according to the
   * evaluation of the {@link #validInspectCodeIssuePredicate}, bundled to the name of the project the issues belong to.
   */
  @NotNull
  private final Map<String, Collection<InspectCodeIssueModel>> parsedIssuesMap = new LinkedHashMap<>(32);

  /** A {@link Predicate} used to check if the value of {@link #currentIssueDefinition} should be considered valid. */
  @NotNull
  private final Predicate<InspectCodeIssueDefinitionModel> validInspectCodeIssueDefinitionPredicate;

  /** A {@link Predicate} used to check if the value of {@link #currentIssue} should be considered valid. */
  @NotNull
  private final Predicate<InspectCodeIssueModel> validInspectCodeIssuePredicate;

  /** A {@link Predicate} used to check if the XML elements of the {@value ELEMENT_NAME_PROJECT} node should be parsed or ignored. */
  @NotNull
  private final Predicate<String> validProjectNamesPredicate;

  /**
   * An indication if all XML elements within the {@value ELEMENT_NAME_PROJECT} node should be ignored due to not matching the {@link
   * #validProjectNamesPredicate}.
   */
  private boolean doSkipProjectElement = false;

  /** The {@link InspectCodeIssueDefinitionModel} that is currently being parsed by the SAX parser implementation. */
  private InspectCodeIssueDefinitionModel currentIssueDefinition = null;

  /** The {@link InspectCodeIssueModel} that is currently being parsed by the SAX parser implementation. */
  private InspectCodeIssueModel currentIssue = null;

  /**
   * A {@link Collection} of {@link InspectCodeIssueModel} instances that have been parsed successfully and considered valid according to
   * the evaluation of the {@link #validInspectCodeIssuePredicate}. This {@link Collection} is part of the {@link #parsedIssuesMap} and
   * solely stored in a private reference variable for easier access.
   */
  private Collection<InspectCodeIssueModel> currentIssuesCollection = null;

  /**
   * Creates a new instance of the {@link InspectCodeXmlFileParser} class, which will use the supplied implementation of the {@link
   * Converter} interface to convert the parsed {@link InspectCodeIssueDefinitionModel} instances to valid {@link
   * SonarQubeRuleDefinitionModel} instances after each call to {@link #getRuleDefinitions()}. The supplied {@link Predicate}s are combined
   * using a logical {@code and} and will be used to decide whether the parsed issue definitions are valid.
   *
   * @param ruleDefinitionConverter
   *     An implementation of the {@link Converter} interface used to convert the parsed {@link InspectCodeIssueDefinitionModel} instances
   *     to valid {@link SonarQubeRuleDefinitionModel} instances.
   * @param issueModelConverter
   *     An implementation of the {@link Converter} interface used to convert the parsed {@link InspectCodeIssueModel} instances to valid
   *     {@link SonarQubeIssueModel} instances.
   * @param ruleDefinitionFilterPredicateCollection
   *     A {@link Collection} of {@link Predicate}s that are combined using a logical {@code and} and will be used to decide whether the
   *     parsed issue definitions are valid and should be added to the resulting collection. Might be {@code null} if no filter predicate
   *     should be applied.
   * @param issueFilterPredicateCollection
   *     A {@link Collection} of {@link Predicate}s that are combined using a logical {@code and} and will be used to decide whether the
   *     parsed InspectCode issues are valid and should be added to the resulting collection. Might be {@code null} if no filter predicate
   *     should be applied.
   * @param projectNamePredicateCollection
   *     A {@link Collection} of {@link Predicate}s that are combined using a logical {@code and} and will be used to decide whether the
   *     children of the {@value ELEMENT_NAME_PROJECT} XML node should be parsed or completely ignored. Ignoring certain XML nodes which are
   *     not required will speed up parsing and need less memory.
   */
  public InspectCodeXmlFileParser(
      @NotNull Converter<InspectCodeIssueDefinitionModel, SonarQubeRuleDefinitionModel> ruleDefinitionConverter,
      @NotNull Converter<InspectCodeIssueModel, SonarQubeIssueModel> issueModelConverter,
      @Nullable Collection<Predicate<InspectCodeIssueDefinitionModel>> ruleDefinitionFilterPredicateCollection,
      @Nullable Collection<Predicate<InspectCodeIssueModel>> issueFilterPredicateCollection,
      @Nullable Collection<Predicate<String>> projectNamePredicateCollection) {
    // Store a reference to the supplied converter implementations
    this.sonarQubeRuleDefinitionConverter = ruleDefinitionConverter;
    this.sonarQubeIssueConverter = issueModelConverter;

    // Combine all supplied filter predicates
    this.validInspectCodeIssueDefinitionPredicate = this.combinePredicates(ruleDefinitionFilterPredicateCollection);
    this.validInspectCodeIssuePredicate = this.combinePredicates(issueFilterPredicateCollection);
    this.validProjectNamesPredicate = this.combinePredicates(projectNamePredicateCollection);
  }

  @NotNull
  @Override
  public Collection<SonarQubeRuleDefinitionModel> getRuleDefinitions() {
    return this.sonarQubeRuleDefinitionConverter.convert(this.parsedIssueDefinitions);
  }

  @Override
  public @NotNull Collection<SonarQubeIssueModel> getIssues() {
    // Concatenate all issue collections for each project
    Stream<InspectCodeIssueModel> stream = Stream.of();
    for (Collection<InspectCodeIssueModel> issueCollection : this.parsedIssuesMap.values()) {
      stream = Stream.concat(stream, issueCollection.stream());
    }

    // Collect and convert the results of the stream to instances of the required class
    return this.sonarQubeIssueConverter.convert(stream.collect(Collectors.toList()));
  }

  @Override
  public void startDocument()
      throws SAXException {
    super.startDocument();

    // Clear the resulting collection of rules before parsing the XML document
    this.parsedIssueDefinitions.clear();
    this.parsedIssuesMap.clear();
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

    // Check if the qualified name of the XML element is not null
    if (qualifiedName != null) {
      // Remove any leading or trailing whitespace characters from the qualified name
      qualifiedName = qualifiedName.trim();

      // Check if all elements except the 'Project' node should be skipped
      if (this.doSkipProjectElement && !ELEMENT_NAME_PROJECT.equals(qualifiedName)) {
        return;
      }

      switch (qualifiedName) {
        case ELEMENT_NAME_ISSUETYPE:
          this.currentIssueDefinition = this.parseXmlElementIssueType(attributes);
          break;
        case ELEMENT_NAME_PROJECT:
          // Retrieve the name of the project from the current XML 'Project' node
          final String parsedProjectName = attributes.getValue(ATTRIBUTE_NAME_NAME).trim();

          // Evaluate if the project should be skipped and store the result (will be reset at the end of the 'Project' node)
          this.doSkipProjectElement = !this.validProjectNamesPredicate.test(parsedProjectName);
          if (this.doSkipProjectElement) {
            return;
          }

          // Create a new collection of InspectCodeIssueModel instances and store it within the resulting map
          this.currentIssuesCollection = new ArrayList<>(32);
          this.parsedIssuesMap.put(parsedProjectName, this.currentIssuesCollection);
          break;
        case ELEMENT_NAME_ISSUE:
          this.currentIssue = this.parseXmlElementIssue(attributes);
          break;
        default:
          LOGGER.debug("The unhandled XML element <{}> has started.", qualifiedName);
          break;
      }
    }
  }

  @Override
  public void endElement(String uri, String localName, String qualifiedName)
      throws SAXException {
    super.endElement(uri, localName, qualifiedName);

    // Check if the qualified name of the XML element is not null
    if (qualifiedName != null) {
      // Remove any leading or trailing whitespace characters from the qualified name
      qualifiedName = qualifiedName.trim();

      // Check if all elements except for the 'Project' node should be skipped
      if (this.doSkipProjectElement && !ELEMENT_NAME_PROJECT.equals(qualifiedName)) {
        return;
      }

      switch (qualifiedName) {
        case ELEMENT_NAME_ISSUETYPE:
          // Check if the parsed issue definition matches all predicates
          if (this.validInspectCodeIssueDefinitionPredicate.test(this.currentIssueDefinition)) {
            // Add the parsed issue definition to the set and clear the member variable
            this.parsedIssueDefinitions.add(this.currentIssueDefinition);
          }
          // Reset the internal variable
          this.currentIssueDefinition = null;
          break;
        case ELEMENT_NAME_PROJECT:
          // Reset the internal variables
          this.currentIssuesCollection = null;
          this.doSkipProjectElement = false;
          break;
        case ELEMENT_NAME_ISSUE:
          // Check if the parsed issue matches all predicates
          if (this.validInspectCodeIssuePredicate.test(this.currentIssue)) {
            // Add the parsed issue to the set and clear the member variable
            this.currentIssuesCollection.add(this.currentIssue);
          }
          // Reset the internal variable
          this.currentIssue = null;
          break;
        default:
          LOGGER.debug("The unhandled XML element <{}> has ended.", qualifiedName);
          break;
      }
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
        "parsedIssueDefinitions[" + parsedIssueDefinitions.size() + "]" +
        ", parsedIssuesMap[" + parsedIssuesMap.size() + "]" +
        ", currentIssueDefinition=" + currentIssueDefinition +
        ", currentIssue=" + currentIssue +
        ", currentIssuesCollection=" + currentIssuesCollection +
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

  /**
   * Combines the supplied {@code predicatesCollection} to a single {@link Predicate} instance using a logical {@code and} operator, so that
   * each of the supplied {@link Predicate} instances must pass.
   *
   * @param predicatesCollection
   *     A {@link Collection} of {@link Predicate} instances from which a combined {@link Predicate} of each entry will be generated using a
   *     logical {@code and}. If the {@link Collection} is either {@code null} or empty, a generic {@link Predicate} instance of type {@link
   *     T} is returned, that will accept all values.
   * @param <T>
   *     The generic type definition on which the {@link Predicate} instances can be applied.
   *
   * @return A logical {@code and} combined {@link Predicate} of each entry of the supplied {@code predicatesCollection}. If the {@link
   *     Collection} is either {@code null} or empty, a generic {@link Predicate} instance of type {@link T} is returned, that will accept
   *     all values.
   */
  @NotNull
  private <T> Predicate<T> combinePredicates(@Nullable Collection<Predicate<T>> predicatesCollection) {
    // Combine all supplied filter predicates
    if (predicatesCollection == null || predicatesCollection.isEmpty()) {
      return x -> true;
    } else {
      // Helper variable
      Predicate<T> matchAllPredicate = null;

      // Iterate and combine all supplied predicates to a single predicate using a logical 'and'
      for (Predicate<T> filterPredicate : predicatesCollection) {
        // Check if the helper variable is not yet set
        if (matchAllPredicate == null) {
          matchAllPredicate = filterPredicate;
        } else {
          // Combine the previous predicate with the current predicate using a logical 'and'
          matchAllPredicate = matchAllPredicate.and(filterPredicate);
        }
      }

      // Return the combined filter predicate
      return matchAllPredicate != null ? matchAllPredicate : item -> false;
    }
  }

  /**
   * Creates a new instance of the {@link InspectCodeIssueDefinitionModel} class populated with values provided as argument {@code
   * attributes}, which contains the XML attributes declared for an XML element of name {@value #ELEMENT_NAME_ISSUETYPE}.
   *
   * @param attributes
   *     An implementation of the {@link Attributes} interface, containing a collection of XML attributes declared for an {@value
   *     #ELEMENT_NAME_ISSUETYPE} XML element.
   *
   * @return A new instance of class {@link InspectCodeIssueDefinitionModel} populated with values retrieved from the supplied {@code
   *     attributes} of the XML element {@value #ELEMENT_NAME_ISSUETYPE}. Might return {@code null}, if the value of attribute {@value
   *     #ATTRIBUTE_NAME_ID} is either {@code null} or an empty string.
   */
  @Nullable
  private InspectCodeIssueDefinitionModel parseXmlElementIssueType(@NotNull final Attributes attributes) {
    // Retrieve the value of attribute 'id' from the current 'IssueType' XML element
    final String ruleDefinitionIdentifier = attributes.getValue(ATTRIBUTE_NAME_ID);

    // Check if the value of attribute 'Id' is neither null, nor an empty string or return null
    if (ruleDefinitionIdentifier == null || ruleDefinitionIdentifier.trim().isEmpty()) {
      return null;
    }

    // Create a new issue definition model instance
    final InspectCodeIssueDefinitionModel inspectCodeIssueDefinitionModel =
        new InspectCodeIssueDefinitionModel(ruleDefinitionIdentifier.trim());

    // Parse all attributes of the XML element that just started
    final int length = attributes.getLength();
    for (int index = 0; index < length; index++) {
      // Retrieve the name of the attribute and trim any leading or trailing whitespaces
      switch (attributes.getQName(index).trim()) {
        case ATTRIBUTE_NAME_CATEGORY:
          inspectCodeIssueDefinitionModel.setCategory(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_CATEGORYID:
          inspectCodeIssueDefinitionModel.setCategoryId(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_SUBCATEGORY:
          inspectCodeIssueDefinitionModel.setSubCategory(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_DESCRIPTION:
          inspectCodeIssueDefinitionModel.setDescription(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_SEVERITY:
          inspectCodeIssueDefinitionModel.setSeverity(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_WIKIURL:
          inspectCodeIssueDefinitionModel.setWikiUrl(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_GLOBAL:
          inspectCodeIssueDefinitionModel.setGlobalRuleDefinition(attributes.getValue(index));
          break;
        default:
          LOGGER.debug(
              "XML element <{}>: Unhandled XML attribute {} found while parsing.",
              ELEMENT_NAME_ISSUETYPE,
              attributes.getQName(index));
          break;
      }
    }

    // Return the populated issue definition model
    return inspectCodeIssueDefinitionModel;
  }

  /**
   * Creates a new instance of the {@link InspectCodeIssueModel} class populated with values provided as argument {@code attributes}, which
   * contains the XML attributes declared for an XML element of name {@value #ELEMENT_NAME_ISSUE}.
   *
   * @param attributes
   *     An implementation of the {@link Attributes} interface, containing a collection of XML attributes declared for an {@value
   *     #ELEMENT_NAME_ISSUE} XML element.
   *
   * @return A new instance of class {@link InspectCodeIssueModel} populated with values retrieved from the supplied {@code attributes} of
   *     the XML element {@value #ELEMENT_NAME_ISSUE}.
   */
  @NotNull
  private InspectCodeIssueModel parseXmlElementIssue(@NotNull Attributes attributes) {
    // Create a new InspectCode issue instance
    final InspectCodeIssueModel inspectCodeIssueModel = new InspectCodeIssueModel();

    // Parse all attributes of the XML element that just started
    final int attributesLength = attributes.getLength();
    for (int index = 0; index < attributesLength; index++) {
      // Retrieve the name of the attribute and trim any leading or trailing whitespaces
      switch (attributes.getQName(index).trim()) {
        case ATTRIBUTE_NAME_TYPEID:
          inspectCodeIssueModel.setIssueTypeId(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_FILE:
          inspectCodeIssueModel.setFile(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_OFFSET:
          try {
            inspectCodeIssueModel.setOffset(attributes.getValue(index));
          } catch (IllegalArgumentException iae) { // NumberFormatException is a subclass of IllegalArgumentException
            LOGGER.warn(
                "XML element <" + ELEMENT_NAME_ISSUE + ">: Value " + attributes.getValue(index) + " of XML attribute "
                    + ATTRIBUTE_NAME_OFFSET + "is not a valid range.",
                iae);
          }
          break;
        case ATTRIBUTE_NAME_LINE:
          try {
            final int lineNumber = Integer.parseInt(attributes.getValue(index), 10);
            inspectCodeIssueModel.setLine(lineNumber);
          } catch (NumberFormatException nfe) {
            LOGGER.warn(
                "XML element <" + ELEMENT_NAME_ISSUE + ">: Value " + attributes.getValue(index) + " of XML attribute "
                    + ATTRIBUTE_NAME_LINE + "is not a valid integer.",
                nfe);
          }
          break;
        case ATTRIBUTE_NAME_MESSAGE:
          inspectCodeIssueModel.setMessage(attributes.getValue(index));
          break;
        default:
          LOGGER.debug(
              "XML element <{}>: Unhandled XML attribute {} found while parsing.",
              ELEMENT_NAME_ISSUE,
              attributes.getQName(index));
          break;
      }
    }

    // Return the populated issue model instance
    return inspectCodeIssueModel;
  }
}
