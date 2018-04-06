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
