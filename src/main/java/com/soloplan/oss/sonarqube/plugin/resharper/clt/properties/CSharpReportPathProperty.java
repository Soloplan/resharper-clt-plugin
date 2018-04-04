package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class CSharpReportPathProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH;
  }

  @Override
  protected String getName() {
    return "ReSharper report path for C#";
  }

  @Override
  protected String getDescription() {
    return "Path to the ReSharper report for C#, i.e. reports/cs-report.xml";
  }

  @NotNull
  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_CS_SUBCATEGORY;
  }

  @Override
  protected @NotNull List<String> getQualifiers() {
    final List<String> qualifiers = super.getQualifiers();
    qualifiers.add(Qualifiers.MODULE);
    return qualifiers;
  }
}
