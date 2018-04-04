package com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * An interface that can be implemented by classes that would like to provide rule definitions for SonarQube.
 */
public interface SonarQubeRuleDefinitionProvider {

  /**
   * Returns a {@link Collection} of {@link SonarQubeRuleDefinitionModel} instances.
   *
   * @return A {@link Collection} of {@link SonarQubeRuleDefinitionModel} instances.
   */
  @NotNull
  Collection<SonarQubeRuleDefinitionModel> getRuleDefinitions();
}
