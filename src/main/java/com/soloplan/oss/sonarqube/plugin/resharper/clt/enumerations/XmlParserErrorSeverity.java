package com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations;

/**
 * Defines values indicating the severity of an error that occurred during parsing of an XML file.
 */
public enum XmlParserErrorSeverity {
  /** This value does not really indicate an error, but may be used for debugging purposes or deprecation warnings. */
  Info,

  /** Indicates a warning, which is not necessarily an error, but could lead to unintended behavior and therefore should be handled. */
  Warning,

  /** Indicates a real problem, that the program itself is unable to handle on its own. */
  Error,

  /** Indicates a fatal problem, which renders the program unstable and prevents its further execution. */
  Fatal
}
