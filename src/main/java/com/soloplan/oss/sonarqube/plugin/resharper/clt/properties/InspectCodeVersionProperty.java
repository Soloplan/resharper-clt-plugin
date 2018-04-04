package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class InspectCodeVersionProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_INSPECTCODE_VERSION;
  }

  @Override
  protected String getName() {
    return "Version of inspectcode.exe command line tool";
  }

  @Override
  protected String getDescription() {
    return "Examples: 2017.1.2 or 2017.3.3";
  }

  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_SUBCATEGORY;
  }
}