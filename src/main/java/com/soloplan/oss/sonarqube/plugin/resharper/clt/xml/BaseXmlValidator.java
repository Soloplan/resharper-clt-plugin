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

import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.XmlDataValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLFilterImpl;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.Validator;
import java.io.InputStream;

/** Basic implementation of a class that is capable of validating XML data using an XML Schema Definition based {@link Validator}. */
public abstract class BaseXmlValidator
    implements XmlDataValidator {

  /**
   * Defines the base path within the resources of the JAR file where the XML Schema Definition (XSD) file is located, which purposely
   * matches the package name of this class using slashes as separators instead of full stops. Additionally, this value is used within the
   * XSD file to define the namespace using the Uniform Resource Name (URN) scheme.
   * <p/>
   * The value of this literal should correspond to {@code /com/soloplan/oss/sonarqube/plugin/resharper/clt/xml}.
   */
  protected static final String RESOURCE_URN_BASE =
      "/" + XmlDataValidator.class.getPackage().getName().replaceAll("\\.", "/");

  /**
   * Defines the XML namespace used within the XML Schema Definition (XSD) including the Uniform Resource Name scheme ({@code urn:}).
   * <p/>
   * The value of this constant should correspond to {@code urn:/com/soloplan/oss/sonarqube/plugin/resharper/clt/xml}.
   */
  protected static final String RESOURCE_URN_NAMESPACE =
      "urn:" + RESOURCE_URN_BASE;

  /**
   * Gets an implementation of the {@link Logger} interface for this class.
   * <p/>
   * Please note, that message arguments are defined with {@code {}}, but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  protected final Logger logger;

  /** Create a new instance of the {@link BaseXmlValidator} validator class. */
  public BaseXmlValidator() {
    this.logger = Loggers.get(this.getClass());
  }

  /**
   * Retrieves the {@link Validator} used during validation of the XML data supplied to {@link #validateXmlData(InputStream)}.
   *
   * @return A {@link Validator} instance used during validation of the XML data.
   */
  @Nullable
  protected abstract Validator getValidator();

  @Override
  public boolean validateXmlData(@NotNull final InputStream xmlDataInputStream) {
    try {
      // Retrieve the validator and check if it null
      final Validator validator = getValidator();
      if (validator == null) {
        this.logger.warn("Could not validate the supplied XML input stream because no validator is available.");
        return false;
      }

      // TODO Validate XML file without updating it: https://stackoverflow.com/questions/2991091/java-xsd-validation-of-xml-without-namespace

      // Create a SAXParserFactory instance and set it up to support XML namespaces
      final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setNamespaceAware(true);

      final SAXSource source = new SAXSource(
          new NamespaceFilter(saxParserFactory.newSAXParser().getXMLReader()),
          new InputSource(xmlDataInputStream));
      final Result validationResult = new SAXResult();
      validator.validate(source, validationResult);

      return true;

    } catch (Exception e) {
      this.logger.error("An exception occurred while trying to validate the supplied XML input stream.", e);
    }

    return false;
  }

  /**
   * This class should infer the namespace of the xml schema definition for each XML file, because the output of the {@code InspectCode}
   * command line tool does not include such a definition.
   * <p/>
   * Apparently, this simply does not work, and I have no clue why... :-/
   */
  static class NamespaceFilter
      extends XMLFilterImpl {

    /**
     * Creates a new instance of the {@link InspectCodeXmlFileValidator.NamespaceFilter} class, supplying the argument {@code xmlReader} to
     * the base class.
     *
     * @param xmlReader
     *     The implementation of the {@link XMLReader} interface to be used within this instance.
     */
    private NamespaceFilter(XMLReader xmlReader) {
      super(xmlReader);
    }

    @Override
    public void startElement(String uri, String localName, String qualifiedName, Attributes attributes)
        throws SAXException {
      // Apply default XML namespace if no namespace is set
      if (uri == null || !uri.trim().equalsIgnoreCase(RESOURCE_URN_NAMESPACE)) {
        uri = RESOURCE_URN_NAMESPACE;
      }
      super.startElement(uri, localName, qualifiedName, attributes);
    }
  }
}
