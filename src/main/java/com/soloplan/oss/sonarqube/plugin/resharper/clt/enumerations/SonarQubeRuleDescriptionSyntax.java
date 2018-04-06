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
