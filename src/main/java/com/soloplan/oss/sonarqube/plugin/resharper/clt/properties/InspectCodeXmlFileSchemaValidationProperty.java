package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a property accessible within SonarQube to indicate whether all {@code InspectCode} XML file analyzed by this plugin should be
 * validated using an XML Schema Definition file.
 */
public class InspectCodeXmlFileSchemaValidationProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION;
  }

  @Override
  protected String getName() {
    return "InspectCode XML file validation";
  }

  @Override
  protected String getDescription() {
    return "Enable validation of InspectCode XML files using an XML Schema Definition file.";
  }

  @Override
  protected @NotNull String getDefaultValue() {
    return Boolean.FALSE.toString();
  }
}
