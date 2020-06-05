package com.soloplan.oss.sonarqube.plugin.resharper.clt.models;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeRuleType;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeSeverity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RuleOverrideBaseModel {

  /**
   * The type of the SonarQube rule.
   */
  private SonarQubeRuleType sonarQubeRuleType = SonarQubeRuleType.getDefaultRuleType();

  /**
   * The SonarQube compatible severity value of the rule.
   */
  private SonarQubeSeverity sonarQubeSeverity = SonarQubeSeverity.getDefaultSeverity();


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
   * @param sonarQubeRuleType The SonarQube compatible rule type to set for this rule definition override.
   */
  public void setSonarQubeRuleType(@NotNull SonarQubeRuleType sonarQubeRuleType) {
    this.sonarQubeRuleType = sonarQubeRuleType;
  }

  /**
   * Sets the SonarQube compatible rule type of this rule definition override by parsing the supplied string representation to a matching
   * enumeration value.
   *
   * @param ruleTypeValue The string representation of the {@link SonarQubeRuleType} to set for this rule definition override.
   */
  public void setSonarQubeRuleType(@Nullable String ruleTypeValue) {
    this.sonarQubeRuleType = SonarQubeRuleType.fromRuleTypeValue(ruleTypeValue);
  }

  /**
   * Gets the SonarQube compatible severity value of this rule.
   * <p>
   * Defaults to {@link SonarQubeSeverity#MAJOR} if not explicitly set via call to {@link #setSonarQubeSeverity(SonarQubeSeverity)}.
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
   * @param sonarQubeSeverity The SonarQube compatible severity value for this rule.
   */
  public void setSonarQubeSeverity(@NotNull SonarQubeSeverity sonarQubeSeverity) {
    this.sonarQubeSeverity = sonarQubeSeverity;
  }

  /**
   * Sets the severity value of this issue by parsing the supplied {@link String} to its corresponding {@link SonarQubeSeverity} value.
   *
   * @param severityValue A {@link String} value that will be parsed to its corresponding {@link SonarQubeSeverity} value and set as the new severity value.
   */
  public void setSonarQubeSeverity(@Nullable String severityValue) {
    this.sonarQubeSeverity = SonarQubeSeverity.fromSeverityValue(severityValue);
  }
}
