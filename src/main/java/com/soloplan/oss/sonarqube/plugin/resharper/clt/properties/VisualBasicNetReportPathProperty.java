package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

import static com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_VBNET_SUBCATEGORY;

public final class VisualBasicNetReportPathProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_VBNET_REPORT_PATH;
  }

  @Override
  protected String getName() {
    return "ReSharper report path for VB.NET";
  }

  @Override
  protected String getDescription() {
    return "Path to the ReSharper report for VB.NET, i.e. reports/vbnet-report.xml";
  }

  @NotNull
  @Override
  protected String getSubCategory() {
    return PLUGIN_CONFIGURATION_PROPERTY_VBNET_SUBCATEGORY;
  }

  @Override
  protected @NotNull List<String> getQualifiers() {
    final List<String> qualifiers = super.getQualifiers();
    qualifiers.add(Qualifiers.MODULE);
    return qualifiers;
  }
}
