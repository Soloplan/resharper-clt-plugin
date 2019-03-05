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

import org.jetbrains.annotations.NotNull;
import org.sonar.api.batch.fs.TextRange;
import org.sonar.api.batch.fs.internal.DefaultTextPointer;
import org.sonar.api.batch.fs.internal.DefaultTextRange;

/**
 * A model class that provides an abbreviated abstraction over all properties that can be set when creating new SonarQube issues.
 */
public class SonarQubeIssueModel {

  // region Member variables

  /** The SonarQube rule identifier, which should correspond to {@link InspectCodeIssueModel#getIssueTypeId()}. */
  private String ruleKey;

  /** The relative path and file name of the source file where the issue occurred. */
  private String filePath;

  /** The message further describing the issue. */
  private String message;

  /**
   * Declares the line and index position of the first and the line and index position of the last character within the source code file
   * where the issue occurred.
   */
  private TextRange textRange;

  // endregion

  /**
   * Gets the SonarQube rule identifier, as declared within any of the SonarQube rule repositories.
   *
   * @return The rule identifier for this issue, as declared within any of the SonarQube rule repositories.
   */
  public String getRuleKey() {
    return ruleKey;
  }

  /**
   * Sets the SonarQube rule identifier, which should correspond to {@link InspectCodeIssueModel#getIssueTypeId()}.
   *
   * @param ruleKey
   *     The SonarQube rule identifier of this issue, which should correspond to {@link InspectCodeIssueModel#getIssueTypeId()}.
   */
  public void setRuleKey(@NotNull String ruleKey) {
    this.ruleKey = ruleKey.trim();
  }

  /**
   * Gets the path to the source code file where the issue occurred, relative to the workspace directory. Includes the name of the file.
   *
   * @return The path to the source code file where the issue occurred, relative to the workspace directory. Includes the name of the file.
   */
  public String getFilePath() {
    return filePath;
  }

  /**
   * Sets the path to the source code file where the issue occurred, relative to the workspace directory. Should includes the file name.
   *
   * @param filePath
   *     The path to the source code file where the issue occurred, relative to the workspace directory. Should includes the file name.
   */
  public void setFilePath(@NotNull String filePath) {
    this.filePath = filePath.trim();
  }

  /**
   * Gets a message further describing the issue.
   *
   * @return The message that further describes the issue.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message of this issue, further describing it.
   *
   * @param message
   *     The message that further describes the issue.
   */
  public void setMessage(@NotNull String message) {
    this.message = message.trim();
  }

  /**
   * Gets an implementation of the {@link TextRange} interface, which describes the line, first and last character within the source code
   * file where the issue occurred.
   *
   * @return An implementation of the {@link TextRange} interface, which describes the line, first and last character within the source code
   *     file where the issue occurred.
   */
  public TextRange getTextRange() {
    return textRange;
  }

  /**
   * Sets an implementation of the {@link TextRange} interface, which describes the line, first and last character within the source code
   * file where the issue occurred.
   *
   * @param textRange
   *     The implementation of the {@link TextRange} interface, which describes the line, first and last character within the source code
   *     file where the issue occurred.
   */
  public void setTextRange(@NotNull TextRange textRange) {
    this.textRange = textRange;
  }

  /**
   * Creates and sets an implementation of the {@link TextRange} interface from the supplied {@code line}, {@code start} and {@code end}
   * character values.
   *
   * @param line
   *     The line within the source file where the issue occurred.
   * @param start
   *     The first character within the supplied {@code link} of the source file where the issue occurred.
   * @param end
   *     The last character within the supplied {@code link} of the source file where the issue occurred.
   */
  public void setTextRange(int line, int start, int end) {
    this.textRange = new DefaultTextRange(
        new DefaultTextPointer(line, start),
        new DefaultTextPointer(line, end));
  }
}
