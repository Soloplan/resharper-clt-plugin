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
