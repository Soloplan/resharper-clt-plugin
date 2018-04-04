package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class InspectCodePathProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_INSPECTCODE_PATH;
  }

  @Override
  protected String getName() {
    return "Path to inspectcode.exe";
  }

  @Override
  protected String getDescription() {
    return "Example: C:/jetbrains-commandline-tools/inspectcode.exe.";
  }

  @NotNull
  @Override
  protected String getDefaultValue() {
    return "C:/jetbrains-commandline-tools/inspectcode.exe";
  }

  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_SUBCATEGORY;
  }
}