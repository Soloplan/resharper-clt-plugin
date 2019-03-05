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

import org.jetbrains.annotations.Nullable;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.net.URL;

public class SonarQubeRuleDefinitionOverrideXmlFileValidator
    extends BaseXmlValidator {

  /**
   * Defines the full path within the resources of the JAR file where the XML Schema Definition (XSD) file is located, including its file
   * name.
   * <p/>
   * The value of this constant should correspond to {@code /com/soloplan/oss/sonarqube/plugin/resharper/clt/xml/sonarqube_rule_overrides-schema_definition.xsd}.
   */
  private static final String INSPECTCODE_OVERRIDE_XSD_RESOURCE =
      RESOURCE_URN_BASE + "/sonarqube_rule_overrides-schema_definition.xsd";

  @Override
  protected @Nullable Validator getValidator() {
    // Create a validator instance using the XML schema definition from the resources of the plugin
    final SchemaFactory factory = javax.xml.validation.SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
    final URL xmlSchemaUrl = InspectCodeXmlFileValidator.class.getResource(INSPECTCODE_OVERRIDE_XSD_RESOURCE);

    Validator validator;
    try {
      validator = factory.newSchema(xmlSchemaUrl).newValidator();
    } catch (SAXException saxException) {
      validator = null;
      this.logger.error("Could not create validator from XML schema definition url " + xmlSchemaUrl.toString() + ".", saxException);
    }

    return validator;
  }
}
