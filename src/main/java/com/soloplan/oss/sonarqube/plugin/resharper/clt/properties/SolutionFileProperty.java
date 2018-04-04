package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class SolutionFileProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE;
  }

  @Override
  protected String getName() {
    return "Solution file";
  }

  @Override
  protected String getDescription() {
    return "The absolute path to the solution or project file given as input to inspectcode.exe. Example: C:/Projects/MyProject/MySolution.sln.";
  }

  @Override
  protected @NotNull List<String> getQualifiers() {
    final List<String> qualifiers = super.getQualifiers();
    qualifiers.add(Qualifiers.MODULE);
    return qualifiers;
  }
}