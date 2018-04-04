package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class ProjectNameProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_PROJECT_NAME;
  }

  @Override
  protected String getName() {
    return "Visual Studio project name";
  }

  @Override
  protected String getDescription() {
    return "Example: MyLibrary. File extensions .csproj and .vbproj are assumed here.";
  }

  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_SUBCATEGORY;
  }

  @Override
  protected @NotNull List<String> getQualifiers() {
    final List<String> qualifiers = super.getQualifiers();
    qualifiers.add(Qualifiers.MODULE);
    return qualifiers;
  }
}