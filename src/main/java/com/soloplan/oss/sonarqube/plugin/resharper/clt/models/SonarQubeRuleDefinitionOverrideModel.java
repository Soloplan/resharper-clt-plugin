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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.models;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeRuleType;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A model class that is used to override specific members of the {@link SonarQubeRuleDefinitionModel} based on their {@link
 * #ruleDefinitionKey} when creating new SonarQube rule definitions.
 */
public class SonarQubeRuleDefinitionOverrideModel extends RuleOverrideBaseModel{

  /** The unique identifier of the SonarQube rule. */
  private final String ruleDefinitionKey;

  /**
   * Creates a new instance of the {@link SonarQubeRuleDefinitionModel} class using the supplied {@code ruleDefinitionKey} to uniquely
   * identify the new instance.
   *
   * @param ruleDefinitionKey
   *     The identifier to uniquely identify the corresponding rule within SonarQube. Leading and trailing whitespace characters are removed
   *     from the supplied value. Must not be {@code null}.
   */
  public SonarQubeRuleDefinitionOverrideModel(@NotNull String ruleDefinitionKey) {
    this.ruleDefinitionKey = ruleDefinitionKey.trim();
  }

  /**
   * Gets the identifier of this rule which is used to uniquely identify this rule within SonarQube.
   *
   * @return The unique identifier of this rule.
   */
  public String getRuleDefinitionKey() {
    return ruleDefinitionKey;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    return ruleDefinitionKey.equals(((SonarQubeRuleDefinitionOverrideModel) other).ruleDefinitionKey);
  }

  @Override
  public int hashCode() {
    return ruleDefinitionKey.hashCode();
  }

  @Override
  public String toString() {
    return "SonarQubeRuleDefinitionOverrideModel{" +
        "ruleDefinitionKey='" + ruleDefinitionKey + '\'' +
        ", sonarQubeRuleType=" + getSonarQubeRuleType() +
        ", sonarQubeSeverity=" + getSonarQubeSeverity() +
        '}';
  }
}
