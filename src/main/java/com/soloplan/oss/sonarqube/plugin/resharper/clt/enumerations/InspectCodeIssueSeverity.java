package com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations;

import org.jetbrains.annotations.NotNull;

/**
 * Defines severity values that are compatible with the resulting XML file of the {@code InspectCode} command line tool. In case the actual
 * string representation is required, call {@link InspectCodeIssueSeverity#getInspectCodeSeverityValue()} to retrieve it.
 */
public enum InspectCodeIssueSeverity {
  /** Represents the {@code DO_NOT_SHOW} error severity of {@code InspectCode}. */
  DoNotShow("DO_NOT_SHOW"),

  /** Represents the {@code INVALID_SEVERITY} error severity of {@code InspectCode}. */
  InvalidSeverity("INVALID_SEVERITY"),

  /** Represents the {@code HINT} error severity of {@code InspectCode}. */
  Hint("HINT"),

  /** Represents the {@code SUGGESTION} error severity of {@code InspectCode}. */
  Suggestion("SUGGESTION"),

  /** Represents the {@code WARNING} error severity of {@code InspectCode}. */
  Warning("WARNING"),

  /** Represents the {@code ERROR} error severity of {@code InspectCode}. */
  Error("ERROR");

  /** The string representation of the severity as contained within the resulting XML file of the {@code InspectCode} command line tool. */
  private final String severity;

  /**
   * Private constructor for the enumerations of {@link InspectCodeIssueSeverity}.
   *
   * @param severity
   *     The string representation of the severity as contained within the resulting XML file of the {@code InspectCode} command line tool.
   */
  InspectCodeIssueSeverity(@NotNull String severity) {
    this.severity = severity;
  }

  /**
   * Gets the string representation of the severity as contained within the resulting XML file of the {@code InspectCode} command line
   * tool.
   *
   * @return The string representation of the severity as contained within the resulting XML file of the {@code InspectCode} command line
   *     tool.
   */
  public String getInspectCodeSeverityValue() {
    return severity;
  }

  /**
   * Gets the default {@code InspectCode} compatible severity value which corresponds to {@link #Warning}.
   *
   * @return The default {@code InspectCode} compatible severity value which corresponds to {@link #Warning}.
   */
  public static InspectCodeIssueSeverity getDefaultSeverity() {
    return Warning;
  }

  /**
   * Parses the supplied {@code InspectCode} compatible severity value to its corresponding enumeration. If the supplied {@code
   * severityValue} is either {@code null}, an empty string or could not be parsed, the default value is returned.
   *
   * @param severityValue
   *     The {@code InspectCode} compatible severity value for which the corresponding enumeration is requested.
   *
   * @return The corresponding enumeration of the supplied {@code InspectCode} compatible severity value or the default value if the
   *     supplied {@code severityValue} is either {@code null}, an empty string or could not be parsed.
   *
   * @see #getDefaultSeverity()
   */
  public static InspectCodeIssueSeverity fromSeverityValue(String severityValue) {
    // Return the default value if the supplied security value is null
    if (severityValue == null) {
      return getDefaultSeverity();
    }

    // Remove all leading and trailing whitespaces from the supplied severity value and check if it is an empty string
    severityValue = severityValue.trim();
    if (severityValue.isEmpty()) {
      return getDefaultSeverity();
    }

    // Iterate all enumeration values and check if the supplied severity value matches any known value
    for (InspectCodeIssueSeverity inspectCodeIssueSeverity : InspectCodeIssueSeverity.values()) {
      if (inspectCodeIssueSeverity.getInspectCodeSeverityValue().equalsIgnoreCase(severityValue)) {
        return inspectCodeIssueSeverity;
      }
    }

    return getDefaultSeverity();
  }
}
