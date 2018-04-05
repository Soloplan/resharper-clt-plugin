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
}
