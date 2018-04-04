package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a property accessible within SonarQube to define a comma-separated list of file name suffixes to indicate which files should be
 * analyzed by the plugin.
 */
public final class CSharpLanguageProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES;
  }

  @Override
  protected String getName() {
    return "File Suffixes";
  }

  @Override
  protected String getDescription() {
    return "Comma-separated list of suffixes for files to analyze.";
  }

  @NotNull
  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_CS_SUBCATEGORY;
  }

  @Override
  protected @NotNull String getDefaultValue() {
    return ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES_DEFAULT_VALUE;
  }
}
