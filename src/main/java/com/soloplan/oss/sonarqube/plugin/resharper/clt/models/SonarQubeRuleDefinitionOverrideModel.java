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
public class SonarQubeRuleDefinitionOverrideModel {

  // region Member variables

  /** The unique identifier of the SonarQube rule. */
  private final String ruleDefinitionKey;

  /** The type of the SonarQube rule. */
  private SonarQubeRuleType sonarQubeRuleType = SonarQubeRuleType.getDefaultRuleType();

  /** The SonarQube compatible severity value of the rule. */
  private SonarQubeSeverity sonarQubeSeverity = SonarQubeSeverity.getDefaultSeverity();

  // endregion

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

  /**
   * Gets the SonarQube compatible rule type of this rule definition override. Defaults to {@link SonarQubeRuleType#CODE_SMELL} if not
   * explicitly set via call to {@link #setSonarQubeRuleType(SonarQubeRuleType)}.
   *
   * @return The SonarQube compatible rule type of this rule definition override.
   */
  public SonarQubeRuleType getSonarQubeRuleType() {
    return this.sonarQubeRuleType;
  }

  /**
   * Sets the SonarQube compatible rule type of this rule definition override.
   *
   * @param sonarQubeRuleType
   *     The SonarQube compatible rule type to set for this rule definition override.
   */
  public void setSonarQubeRuleType(@NotNull SonarQubeRuleType sonarQubeRuleType) {
    this.sonarQubeRuleType = sonarQubeRuleType;
  }

  /**
   * Sets the SonarQube compatible rule type of this rule definition override by parsing the supplied string representation to a matching
   * enumeration value.
   *
   * @param ruleTypeValue
   *     The string representation of the {@link SonarQubeRuleType} to set for this rule definition override.
   */
  public void setSonarQubeRuleType(@Nullable String ruleTypeValue) {
    this.sonarQubeRuleType = SonarQubeRuleType.fromRuleTypeValue(ruleTypeValue);
  }

  /**
   * Gets the SonarQube compatible severity value of this rule.
   * <p>
   * Defaults to {@link SonarQubeSeverity#Major} if not explicitly set via call to {@link #setSonarQubeSeverity(SonarQubeSeverity)}.
   * </p>
   *
   * @return The SonarQube compatible severity value of this rule.
   */
  public SonarQubeSeverity getSonarQubeSeverity() {
    return sonarQubeSeverity;
  }

  /**
   * Sets the SonarQube compatible severity value of this rule.
   * <p>
   * Although the SonarQube API defines the severity as parameter of class {@link String}, not all values passed in as arguments are valid
   * and may result in runtime exceptions. This abstraction uses the enumeration {@link SonarQubeSeverity} to ensure, that only valid values
   * will be used when creating a new rule.
   * </p>
   *
   * @param sonarQubeSeverity
   *     The SonarQube compatible severity value for this rule.
   */
  public void setSonarQubeSeverity(@NotNull SonarQubeSeverity sonarQubeSeverity) {
    this.sonarQubeSeverity = sonarQubeSeverity;
  }

  /**
   * Sets the severity value of this issue by parsing the supplied {@link String} to its corresponding {@link SonarQubeSeverity} value.
   *
   * @param severityValue
   *     A {@link String} value that will be parsed to its corresponding {@link SonarQubeSeverity} value and set as the new severity value.
   */
  public void setSonarQubeSeverity(@Nullable String severityValue) {
    this.sonarQubeSeverity = SonarQubeSeverity.fromSeverityValue(severityValue);
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
        ", sonarQubeRuleType=" + sonarQubeRuleType +
        ", sonarQubeSeverity=" + sonarQubeSeverity +
        '}';
  }
}
