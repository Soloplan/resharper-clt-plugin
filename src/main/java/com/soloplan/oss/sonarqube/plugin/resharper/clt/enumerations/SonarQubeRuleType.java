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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.rules.RuleType;

/**
 * Defines rule type values that are compatible with SonarQube. In case the actual string representation as defined by {@code SonarQube} is
 * required, call {@link SonarQubeRuleType#getSonarQubeRuleTypeName()} to retrieve it.
 *
 * @see <a href="https://jira.sonarsource.com/browse/MMF-184">[MMF-184] SonarQube Quality Model with three characteristics - SonarSource</a>
 * @see <a href="https://docs.sonarqube.org/display/SONAR/Rules+-+types+and+severities">Rules - types and severities - SonarQube
 *     Documentation - Doc SonarQube</a>
 */
public enum SonarQubeRuleType {

  /** Corresponds to the SonarQube rule type {@link RuleType#CODE_SMELL}. */
  CODE_SMELL(RuleType.CODE_SMELL),

  /** Corresponds to the SonarQube rule type {@link RuleType#BUG}. */
  BUG(RuleType.BUG),

  /** Corresponds to the SonarQube rule type {@link RuleType#VULNERABILITY}. */
  VULNERABILITY(RuleType.VULNERABILITY);

  /** Contains the representation of the SonarQube {@link RuleType} value. */
  private final RuleType ruleType;

  /**
   * Private constructor for the enumerations of {@link SonarQubeSeverity}.
   *
   * @param ruleType
   *     The string representation of the rule type as defined by SonarQube.
   */
  SonarQubeRuleType(@NotNull RuleType ruleType) {
    this.ruleType = ruleType;
  }

  /**
   * Gets the default SonarQube compatible rule type value which corresponds to {@link #CODE_SMELL}.
   *
   * @return The default SonarQube compatible rule type value which corresponds to {@link #CODE_SMELL}.
   */
  public static SonarQubeRuleType getDefaultRuleType() {
    return SonarQubeRuleType.CODE_SMELL;
  }

  /**
   * Gets the string representation of the rule type as defined by {@code SonarQube}.
   *
   * @return The string representation of the rule type as defined by {@code SonarQube}.
   */
  public String getSonarQubeRuleTypeName() {
    return this.ruleType.name();
  }

  /**
   * Gets the SonarQube compatible {@link RuleType} as defined by the {@code SonarQube} SDK.
   *
   * @return The SonarQube compatible {@link RuleType} as defined by the {@code SonarQube} SDK.
   */
  public RuleType getSonarQubeRuleType() {
    return this.ruleType;
  }

  /**
   * Parses the supplied {@code SonarQube} compatible severity value to its corresponding enumeration. If the supplied {@code severityValue}
   * is either {@code null}, an empty string or could not be parsed, the default value is returned.
   *
   * @param ruleTypeValue
   *     The {@code SonarQube} compatible rule type value for which the corresponding enumeration is requested.
   *
   * @return The corresponding enumeration of the supplied {@code SonarQube} compatible rule type value or the default value if the supplied
   *     {@code ruleTypeValue} is either {@code null}, an empty string or could not be parsed.
   *
   * @see #getDefaultRuleType()
   */
  public static SonarQubeRuleType fromRuleTypeValue(@Nullable String ruleTypeValue) {
    // Return the default value if the supplied rule type value is null
    if (ruleTypeValue == null) {
      return getDefaultRuleType();
    }

    // Remove all leading and trailing whitespaces from the supplied rule type value and check if it is an empty string
    ruleTypeValue = ruleTypeValue.trim();
    if (ruleTypeValue.isEmpty()) {
      return getDefaultRuleType();
    }

    // Iterate all enumeration values and check if the supplied rule type value matches any known value
    for (SonarQubeRuleType sonarQubeRuleType : SonarQubeRuleType.values()) {
      if (sonarQubeRuleType.getSonarQubeRuleTypeName().equalsIgnoreCase(ruleTypeValue)
          || sonarQubeRuleType.name().equalsIgnoreCase(ruleTypeValue)) {
        return sonarQubeRuleType;
      }
    }

    return getDefaultRuleType();
  }
}
