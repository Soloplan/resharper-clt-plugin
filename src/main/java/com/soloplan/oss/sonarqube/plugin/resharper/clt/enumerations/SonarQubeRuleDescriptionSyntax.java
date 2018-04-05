package com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations;

/**
 * Defines all valid formatting syntax values available for human-readable descriptions of SonarQube rule definitions.
 */
public enum SonarQubeRuleDescriptionSyntax {
  /** Indicates, that the description uses HTML syntax formatting. */
  HTML,

  /** Indicates, that the description uses Markdown syntax formatting. */
  MARKDOWN;

  /**
   * Gets the default formatting syntax for SonarQube rule definitions: {@link #HTML}.
   *
   * @return The default formatting syntax for SonarQube rule definitions: {@link #HTML}.
   */
  public static SonarQubeRuleDescriptionSyntax getDefaultRuleDescriptionSyntax() {
    return SonarQubeRuleDescriptionSyntax.HTML;
  }
}
