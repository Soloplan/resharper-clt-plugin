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

import org.sonar.api.rule.Severity;

/**
 * Defines severity values that are compatible with SonarQube. In case the actual string representation is required, call {@link
 * SonarQubeSeverity#getSonarQubeSeverityValue()} to retrieve it.
 */
public enum SonarQubeSeverity {

  /** Corresponds to the SonarQube severity value {@value Severity#INFO}. */
  Info(Severity.INFO),

  /** Corresponds to the SonarQube severity value {@value Severity#MINOR}. */
  Minor(Severity.MINOR),

  /** Corresponds to the SonarQube severity value {@value Severity#MAJOR}. */
  Major(Severity.MAJOR),

  /** Corresponds to the SonarQube severity value {@value Severity#CRITICAL}. */
  Critical(Severity.CRITICAL),

  /** Corresponds to the SonarQube severity value {@value Severity#BLOCKER}. */
  Blocker(Severity.BLOCKER);

  /** Contains the {@link String} representation of the SonarQube {@link Severity} value. */
  private final String severity;

  /**
   * Private constructor for the enumerations of {@link SonarQubeSeverity}.
   *
   * @param severity
   *     The string representation of the severity as defined by SonarQube.
   */
  SonarQubeSeverity(String severity) {
    this.severity = severity;
  }

  /**
   * Gets the string representation of the severity as defined by SonarQube.
   *
   * @return The string representation of the severity as defined by SonarQube.
   */
  public String getSonarQubeSeverityValue() {
    return this.severity;
  }

  /**
   * Gets the default SonarQube compatible severity value which corresponds to {@link #Major}.
   *
   * @return The default SonarQube compatible severity value which corresponds to {@link #Major}.
   */
  public static SonarQubeSeverity getDefaultSeverity() {
    return SonarQubeSeverity.Major;
  }

  /**
   * Parses the supplied {@code SonarQube} compatible severity value to its corresponding enumeration. If the supplied {@code severityValue}
   * is either {@code null}, an empty string or could not be parsed, the default value is returned.
   *
   * @param severityValue
   *     The {@code SonarQube} compatible severity value for which the corresponding enumeration is requested.
   *
   * @return The corresponding enumeration of the supplied {@code SonarQube} compatible severity value or the default value if the supplied
   *     {@code severityValue} is either {@code null}, an empty string or could not be parsed.
   *
   * @see #getDefaultSeverity()
   */
  public static SonarQubeSeverity fromSeverityValue(String severityValue) {
    // Return the default value if the supplied severity value is null
    if (severityValue == null) {
      return getDefaultSeverity();
    }

    // Remove all leading and trailing whitespaces from the supplied severity value and check if it is an empty string
    severityValue = severityValue.trim();
    if (severityValue.isEmpty()) {
      return getDefaultSeverity();
    }

    // Iterate all enumeration values and check if the supplied severity value matches any known value
    for (SonarQubeSeverity sonarQubeSeverity : SonarQubeSeverity.values()) {
      if (sonarQubeSeverity.getSonarQubeSeverityValue().equalsIgnoreCase(severityValue)) {
        return sonarQubeSeverity;
      }
    }

    return getDefaultSeverity();
  }
}
