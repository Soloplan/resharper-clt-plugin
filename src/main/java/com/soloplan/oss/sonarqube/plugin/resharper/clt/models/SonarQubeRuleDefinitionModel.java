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

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeRuleDescriptionSyntax;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeRuleType;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeSeverity;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.rule.RuleStatus;

/**
 * A model class that provides an abbreviated abstraction over all properties that can be set when creating new SonarQube rules, including
 * some safeguards to prevent runtime exceptions being thrown by the SonarQube instance when importing new rules that would use obsolete or
 * deprecated values or invalid combinations (e.g. setting HTML and MarkDown descriptions for the same rule).
 */
public final class SonarQubeRuleDefinitionModel {

  // region Member variables

  /** The unique identifier of the SonarQube rule definition. */
  private final String ruleDefinitionKey;

  /** The type of the SonarQube rule definition. */
  private SonarQubeRuleType sonarQubeRuleType = SonarQubeRuleType.getDefaultRuleType();

  /** The name of the rule definition as displayed within SonarQube. */
  private String ruleName = "";

  /** The human-readable description of the rule definition as displayed within SonarQube. */
  private String ruleDescription = "";

  /** The formatting syntax of the description of the rule definition. */
  private SonarQubeRuleDescriptionSyntax ruleDescriptionSyntax = SonarQubeRuleDescriptionSyntax.getDefaultRuleDescriptionSyntax();

  /** The SonarQube compatible severity value of the rule definition. */
  private SonarQubeSeverity sonarQubeSeverity = SonarQubeSeverity.getDefaultSeverity();

  /** The status of the SonarQube rule definition. */
  private RuleStatus ruleStatus = RuleStatus.defaultStatus();

  /** Indicates, whether the SonarQube rule definition is activated by default or not. */
  private boolean activatedByDefault = false;

  // TODO Create a property for 'Rule tags', that accepts only strings containing the following characters: a-z, 0-9, '+', '-', '#', '.'

  // endregion

  /**
   * Creates a new instance of the {@link SonarQubeRuleDefinitionModel} class using the supplied {@code ruleDefinitionKey} to uniquely
   * identify the new instance.
   *
   * @param ruleDefinitionKey
   *     The identifier to uniquely identify the corresponding rule definition within SonarQube. Leading and trailing whitespace characters
   *     are removed from the supplied value. Must not be {@code null}.
   */
  public SonarQubeRuleDefinitionModel(@NotNull String ruleDefinitionKey) {
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
   * Gets the {@code SonarQube} compatible rule type of this rule definition.
   *
   * @return The type to set for this rule definition.
   */
  public SonarQubeRuleType getSonarQubeRuleType() {
    return sonarQubeRuleType;
  }

  /**
   * Sets the {@code SonarQube} compatible rule type of this rule definition.
   */
  public void setSonarQubeRuleType(@NotNull SonarQubeRuleType ruleType) {
    this.sonarQubeRuleType = ruleType;
  }

  /**
   * Gets the human-readable name of this rule which will be displayed within SonarQube.
   *
   * @return The human-readable name of this rule which will be displayed within SonarQube.
   */
  public String getRuleName() {
    return ruleName;
  }

  /**
   * Sets the human-readable name of this rule to be displayed within SonarQube. Passing {@code null} as argument will result in an empty
   * string, leading and trailing whitespace characters will be removed from the supplied argument.
   *
   * @param ruleName
   *     The human-readable name of this rule which will be displayed within SonarQube.
   */
  public void setRuleName(String ruleName) {
    this.ruleName = ruleName == null ? "" : ruleName.trim();
  }

  /**
   * Gets a human-readable text that describes the purpose of this rule in detail. The text might be formatted using either HTML or MarkDown
   * syntax, as defined by the {@link SonarQubeRuleDescriptionSyntax} of this class.
   *
   * @return Returns a human-readable text that describes the purpose of this rule.
   */
  public String getRuleDescription() {
    return ruleDescription;
  }

  /**
   * Sets a human-readable text that describes the purpose of this rule in detail. The text might be formatted using either HTML or MarkDown
   * syntax, as defined by the {@link SonarQubeRuleDescriptionSyntax} of this class.
   * <p>
   * Note: SonarQube provides specific methods for descriptions using HTML and MarkDown syntax when creating a new rule definition, but
   * supplying both leads to a runtime exception, hence the abstraction using the {@link SonarQubeRuleDescriptionSyntax} enumeration.
   * </p>
   *
   * @param ruleDescription
   *     The human-readable text that describes the purpose of this rule in detail. Passing {@code null} as argument will result in an empty
   *     string, leading and trailing whitespace characters will be removed from the supplied argument.
   * @param ruleDescriptionSyntax
   *     Defines the syntax used within the argument passes to parameter {@code ruleDescription}.
   */
  public void setRuleDescription(String ruleDescription, @NotNull SonarQubeRuleDescriptionSyntax ruleDescriptionSyntax) {
    this.ruleDescription = ruleDescription == null ? "" : ruleDescription.trim();
    this.ruleDescriptionSyntax = ruleDescriptionSyntax;
  }

  /**
   * Gets the formatting syntax used within the rule description.
   * <p>
   * Defaults to {@link SonarQubeRuleDescriptionSyntax#HTML} if not explicitly set via call to {@link #setRuleDescription(String,
   * SonarQubeRuleDescriptionSyntax)}.
   * </p>
   *
   * @return The formatting syntax used within the value returned by {@link #getRuleDescription()}.
   *
   * @see #setRuleDescription(String, SonarQubeRuleDescriptionSyntax)
   */
  public SonarQubeRuleDescriptionSyntax getRuleDescriptionSyntax() {
    return ruleDescriptionSyntax;
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
   * Gets the status of this rule.
   * <p>
   * Defaults to {@link RuleStatus#READY} if not explicitly set via call to {@link #setRuleStatus(RuleStatus)}.
   * </p>
   *
   * @return The status of this rule.
   */
  public RuleStatus getRuleStatus() {
    return ruleStatus;
  }

  /**
   * Sets the status of this rule. Might throw an {@link IllegalArgumentException} if {@code ruleStatus} is set to {@link
   * RuleStatus#REMOVED} since this value is no longer supported by SonarQube.
   *
   * @param ruleStatus
   *     The new status of this rule.
   *
   * @throws IllegalArgumentException
   *     If the supplied {@code ruleStatus} matches {@link RuleStatus#REMOVED}, because this value is no longer supported by SonarQube.
   */
  public void setRuleStatus(@NotNull RuleStatus ruleStatus)
      throws IllegalArgumentException {
    if (ruleStatus == RuleStatus.REMOVED) {
      throw new IllegalArgumentException("Rule status " + RuleStatus.REMOVED + " is no longer supported by SonarQube.");
    }
    this.ruleStatus = ruleStatus;
  }

  /**
   * Gets a value indicating whether the rule should be activated by default or not.
   * <p>
   * Defaults to {@code false} if not explicitly set via call to {@link #setActivatedByDefault(boolean)}.
   * </p>
   *
   * @return {@code true} if the rule should be activated by default, otherwise {@code false}.
   */
  public boolean isActivatedByDefault() {
    return activatedByDefault;
  }

  /**
   * Sets a value indicating whether the rule should be activated by default or not.
   *
   * @param activatedByDefault
   *     {@code true} if the rule should be activated by default, otherwise {@code false}.
   */
  public void setActivatedByDefault(boolean activatedByDefault) {
    this.activatedByDefault = activatedByDefault;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    return ruleDefinitionKey.equals(((SonarQubeRuleDefinitionModel) other).ruleDefinitionKey);
  }

  @Override
  public int hashCode() {
    return ruleDefinitionKey.hashCode();
  }

  @Override
  public String toString() {
    return "SonarQubeRuleDefinitionModel{" +
        "ruleDefinitionKey='" + ruleDefinitionKey + '\'' +
        ", sonarQubeRuleType=" + sonarQubeRuleType +
        ", ruleName='" + ruleName + '\'' +
        ", ruleDescription='" + ruleDescription + '\'' +
        ", ruleDescriptionSyntax=" + ruleDescriptionSyntax +
        ", sonarQubeSeverity=" + sonarQubeSeverity +
        ", ruleStatus=" + ruleStatus +
        ", activatedByDefault=" + activatedByDefault +
        '}';
  }
}
