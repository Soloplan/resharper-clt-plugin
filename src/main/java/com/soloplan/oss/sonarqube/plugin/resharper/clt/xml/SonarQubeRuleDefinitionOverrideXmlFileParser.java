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
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.SonarQubeRuleDefinitionOverrideProvider;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionOverrideModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Collection;
import java.util.LinkedHashSet;

/**
 * An implementation of the SAX parser that is capable to parse XML files that contain information about specific properties of the
 * SonarQube rule definitions which should be updated according to the values of the parsed XML file in order to override the default values
 * or behavior of the SonarQube rule definitions. This class will construct a new {@link SonarQubeRuleDefinitionOverrideModel} instance for
 * each rule definition found within the XML file, inserting it into the resulting collection. After the file has been parsed, the resulting
 * collection can be retrieved via a call to {@link #getRuleDefinitionOverrides()}.
 */
public class SonarQubeRuleDefinitionOverrideXmlFileParser
    extends DefaultHandler
    implements SonarQubeRuleDefinitionOverrideProvider {

  // region XML element and attribute names

  /**
   * Defines the name of the {@code SonarRuleOverride} XML element.
   */
  private static final String ELEMENT_NAME_SONARRULEOVERRIDE = "SonarRuleOverride";

  /**
   * Defines the name of the {@code SonarRuleKey} XML attribute which is a mandatory attribute for the {@value
   * ELEMENT_NAME_SONARRULEOVERRIDE} XML element.
   */
  private static final String ATTRIBUTE_NAME_SONARRULEKEY = "SonarRuleKey";

  /**
   * Defines the name of the {@code Severity} XML attribute which might be set for the {@value ELEMENT_NAME_SONARRULEOVERRIDE} XML element.
   */
  private static final String ATTRIBUTE_NAME_SONARRULETYPE = "SonarRuleType";

  /**
   * Defines the name of the {@code Severity} XML attribute which might be set for the {@value ELEMENT_NAME_SONARRULEOVERRIDE} XML element.
   */
  private static final String ATTRIBUTE_NAME_SONARSEVERITY = "SonarSeverity";

  // endregion XML element and attribute names

  /**
   * Gets an implementation of the {@link Logger} interface for this class.
   * <p/>
   * Please note, that message arguments are defined with {@code {}}, but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  private static final Logger LOGGER = Loggers.get(InspectCodeXmlFileParser.class);

  /** A {@link Collection} of {@link SonarQubeRuleDefinitionOverrideModel} instances that have been parsed successfully. */
  @NotNull
  private final Collection<SonarQubeRuleDefinitionOverrideModel> parsedSonarRuleDefinitionOverrides = new LinkedHashSet<>(32);

  /** The {@link SonarQubeRuleDefinitionOverrideModel} that is currently being parsed by the SAX parser implementation. */
  private SonarQubeRuleDefinitionOverrideModel currentRuleDefinitionOverride = null;

  @Override
  public @NotNull Collection<SonarQubeRuleDefinitionOverrideModel> getRuleDefinitionOverrides() {
    return this.parsedSonarRuleDefinitionOverrides;
  }

  @Override
  public void startDocument()
      throws SAXException {
    super.startDocument();

    // Clear the resulting collection of rule definition overrides before parsing the XML document
    this.parsedSonarRuleDefinitionOverrides.clear();
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

      if (ELEMENT_NAME_SONARRULEOVERRIDE.equals(qualifiedName)) {
        this.currentRuleDefinitionOverride = this.parseXmlElementIssueType(attributes);
      } else {
        LOGGER.debug("The unhandled XML element <{}> has started.", qualifiedName);
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

      if (ELEMENT_NAME_SONARRULEOVERRIDE.equals(qualifiedName)) {
        // Add the parsed issue definition to the set and reset the internal variable
        this.parsedSonarRuleDefinitionOverrides.add(this.currentRuleDefinitionOverride);
        this.currentRuleDefinitionOverride = null;
      } else {
        LOGGER.debug("The unhandled XML element <{}> has ended.", qualifiedName);
      }
    }
  }

  @Override
  public void warning(SAXParseException e)
      throws SAXException {
    super.warning(e);
    this.handleParseException(XmlParserErrorSeverity.WARNING, e);
  }

  @Override
  public void error(SAXParseException e)
      throws SAXException {
    super.error(e);
    this.handleParseException(XmlParserErrorSeverity.ERROR, e);
  }

  @Override
  public void fatalError(SAXParseException e)
      throws SAXException {
    super.fatalError(e);
    this.handleParseException(XmlParserErrorSeverity.FATAL, e);
  }

  @Override
  public String toString() {
    return "SonarQubeRuleDefinitionOverrideXmlFileParser{" +
        "parsedSonarRuleDefinitionOverrides[" + parsedSonarRuleDefinitionOverrides.size() + "]" +
        ", currentRuleDefinitionOverride=" + currentRuleDefinitionOverride +
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
      case INFO:
        LOGGER.info(errorMessage, saxParseException);
        break;
      case WARNING:
        LOGGER.warn(errorMessage, saxParseException);
        break;
      case ERROR:
      case FATAL:
      default:
        LOGGER.error(errorMessage, saxParseException);
        break;
    }
  }

  /**
   * Creates a new instance of the {@link SonarQubeRuleDefinitionOverrideModel} class populated with values provided as argument {@code
   * attributes}, which contains the XML attributes declared for an XML element of name {@value #ELEMENT_NAME_SONARRULEOVERRIDE}.
   *
   * @param attributes
   *     An implementation of the {@link Attributes} interface, containing a collection of XML attributes declared for an {@value
   *     #ELEMENT_NAME_SONARRULEOVERRIDE} XML element.
   *
   * @return A new instance of class {@link SonarQubeRuleDefinitionOverrideModel} populated with values retrieved from the supplied {@code
   *     attributes} of the XML element {@value #ELEMENT_NAME_SONARRULEOVERRIDE}. Might return {@code null}, if the value of attribute
   *     {@value #ATTRIBUTE_NAME_SONARRULEKEY} is either {@code null} or an empty string.
   */
  @Nullable
  private SonarQubeRuleDefinitionOverrideModel parseXmlElementIssueType(@NotNull final Attributes attributes) {
    // Retrieve the value of attribute 'SonarRuleKey' from the current 'SonarRuleOverride' XML element
    final String ruleDefinitionIdentifier = attributes.getValue(ATTRIBUTE_NAME_SONARRULEKEY);

    // Check if the value of attribute 'SonarRuleKey' is neither null, nor an empty string or return null
    if (ruleDefinitionIdentifier == null || ruleDefinitionIdentifier.trim().isEmpty()) {
      return null;
    }

    // Create a new sonar rule override definition model instance
    final SonarQubeRuleDefinitionOverrideModel sonarQubeRuleDefinitionOverrideModel =
        new SonarQubeRuleDefinitionOverrideModel(ruleDefinitionIdentifier.trim());

    // Parse all attributes of the XML element that just started
    final int length = attributes.getLength();
    for (int index = 0; index < length; index++) {
      // Retrieve the name of the attribute and trim any leading or trailing whitespaces
      switch (attributes.getQName(index).trim()) {
        case ATTRIBUTE_NAME_SONARRULETYPE:
          sonarQubeRuleDefinitionOverrideModel.setSonarQubeRuleType(attributes.getValue(index));
          break;
        case ATTRIBUTE_NAME_SONARSEVERITY:
          sonarQubeRuleDefinitionOverrideModel.setSonarQubeSeverity(attributes.getValue(index));
          break;
        default:
          LOGGER.debug(
              "XML element <{}>: Unhandled XML attribute {} found while parsing.",
              ELEMENT_NAME_SONARRULEOVERRIDE,
              attributes.getQName(index));
          break;
      }
    }

    // Return the populated sonar rule definition override model
    return sonarQubeRuleDefinitionOverrideModel;
  }
}
