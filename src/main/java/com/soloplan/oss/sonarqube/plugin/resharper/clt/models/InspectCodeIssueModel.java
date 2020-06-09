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

/** Data model class for issues detected by the {@code InspectCode} command line tool. */
public class InspectCodeIssueModel {

  // region Member variables

  /** The issue type identifier, which should correspond to {@link InspectCodeIssueDefinitionModel#getIssueTypeId()}. */
  private String issueTypeId;

  /** The relative path and file name of the source file where the issue occurred. */
  private String file;

  /** The message further describing the issue. */
  private String message;

  /** The index position of the first character within the {@link #line} of the source code where the issue occurred. */
  private int offsetStart = -1;

  /** The index position of the last character within the {@link #line} of the source code where the issue occurred. */
  private int offsetEnd = -1;

  /** The line within the source code file where the issue occurred. */
  private int line = -1;

  // endregion

  /**
   * Gets the issue type identifier, which should correspond to {@link InspectCodeIssueDefinitionModel#getIssueTypeId()}.
   *
   * @return The issue type identifier, which should correspond to {@link InspectCodeIssueDefinitionModel#getIssueTypeId()}.
   */
  public String getIssueTypeId() {
    return issueTypeId;
  }

  /**
   * Sets the issue type identifier, which should correspond to {@link InspectCodeIssueDefinitionModel#getIssueTypeId()}.
   *
   * @param issueTypeId
   *     The issue type identifier, which should correspond to {@link InspectCodeIssueDefinitionModel#getIssueTypeId()}.
   */
  public void setIssueTypeId(String issueTypeId) {
    this.issueTypeId = issueTypeId.replace(',', '_').trim();
  }

  /**
   * Gets the relative path and file name of the source file where the issue occurred.
   *
   * @return The relative path and file name of the source file where the issue occurred.
   */
  public String getFile() {
    return file;
  }

  /**
   * Sets the relative path and file name of the source file where the issue occurred.
   *
   * @param file
   *     The relative path and file name of the source file where the issue occurred.
   */
  public void setFile(String file) {
    this.file = file;
  }

  /**
   * Gets the message further describing the issue.
   *
   * @return The message further describing the issue.
   */
  public String getMessage() {
    return message;
  }

  /**
   * Sets the message further describing the issue.
   *
   * @param message
   *     The message further describing the issue.
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * Sets the index position of the first and last character within the {@link #line} of the source code where the issue occurred by parsing
   * the supplied {@code offsetRange}, which should be in the form of {@code "[start]-[end]"}, where both values are present and valid
   * integer values.
   *
   * @param offsetRange
   *     An {@link String} value in the form {@code "[start]-[end]"}, where both values are present and valid integer values and will be
   *     used to {@link #setOffsetStart(int)} and {@link #setOffsetEnd(int)}.
   *
   * @throws IllegalArgumentException
   *     If the supplied {@code offsetRange} value is {@code null}, an empty {@link String}, does not contain the range delimiter {@code -}
   *     or the {@code [end]} value is not defined.
   * @throws NumberFormatException
   *     If either {@code [start]}, {@code [end]} or both could not be converted to integer values.
   */
  public void setOffset(String offsetRange)
      throws IllegalArgumentException, NumberFormatException {
    // Sanitize the supplied value
    offsetRange = offsetRange == null ? "" : offsetRange.trim();

    // Check if the supplied value is valid
    if (offsetRange.isEmpty()) {
      throw new IllegalArgumentException("The supplied offset range is invalid. Empty strings or (null) are not supported.");
    } else if (!offsetRange.contains("-")) {
      throw new IllegalArgumentException(
          "The supplied offset range '" + offsetRange + "' is invalid because it does not contain the range delimiter '-'. " +
              "Use methods 'setOffsetStart(int)' and 'setOffsetEnd(int)' instead.");
    } else {
      final String[] rangeValues = offsetRange.split("-", 2);
      if (rangeValues.length < 2) {
        throw new IllegalArgumentException("The supplied offset range '" + offsetRange + "' could not be split into two parts. " +
            "Please ensure that the supplied value matches the expected format: [offsetStart]-[offsetEnd].");
      } else {
        // Parse the string values to integer values potentially throwing NumberFormatExceptions
        this.offsetStart = Integer.parseInt(rangeValues[0], 10);
        this.offsetEnd = Integer.parseInt(rangeValues[1], 10);
      }
    }
  }

  /**
   * Gets the index position of the first character within the {@link #line} of the source code where the issue occurred. Negative values
   * indicate an invalid index position.
   *
   * @return The index position of the first character within the {@link #line} of the source code where the issue occurred. Negative values
   *     indicate an invalid index position.
   */
  public int getOffsetStart() {
    return offsetStart;
  }

  /**
   * Sets the index position of the first character within the {@link #line} of the source code where the issue occurred.
   *
   * @param offsetStart
   *     The index position of the first character within the {@link #line} of the source code where the issue occurred.
   */
  public void setOffsetStart(int offsetStart) {
    this.offsetStart = offsetStart;
  }

  /**
   * Gets the index position of the last character within the {@link #line} of the source code where the issue occurred. Negative values
   * indicate an invalid index position.
   *
   * @return The index position of the last character within the {@link #line} of the source code where the issue occurred. Negative values
   *     indicate an invalid index position.
   */
  public int getOffsetEnd() {
    return offsetEnd;
  }

  /**
   * Sets the index position of the last character within the {@link #line} of the source code where the issue occurred.
   *
   * @param offsetEnd
   *     The index position of the last character within the {@link #line} of the source code where the issue occurred.
   */
  public void setOffsetEnd(int offsetEnd) {
    this.offsetEnd = offsetEnd;
  }

  /**
   * Gets the line within the source code file where the issue occurred. Negative values indicate an invalid line number.
   *
   * @return The line within the source code file where the issue occurred. Negative values indicate an invalid line number.
   */
  public int getLine() {
    return line;
  }

  /**
   * Sets the line within the source code file where the issue occurred.
   *
   * @param line
   *     The line within the source code file where the issue occurred.
   */
  public void setLine(int line) {
    this.line = line;
  }

  /**
   * Sets the line within the source code file where the issue occurred.
   *
   * @param lineValue
   *     The line within the source code file where the issue occurred.
   *
   * @throws NumberFormatException
   *     If the supplied {@code lineValue} could not be parsed to an integer value.
   */
  public void setLine(String lineValue)
      throws NumberFormatException {
    this.line = Integer.parseInt(lineValue, 10);
  }

  // region equals(), hashCode(), toString()

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    final InspectCodeIssueModel that = (InspectCodeIssueModel) other;

    if (offsetStart != that.offsetStart) {
      return false;
    }
    if (offsetEnd != that.offsetEnd) {
      return false;
    }
    if (line != that.line) {
      return false;
    }
    if (issueTypeId != null ? !issueTypeId.equals(that.issueTypeId) : that.issueTypeId != null) {
      return false;
    }
    if (file != null ? !file.equals(that.file) : that.file != null) {
      return false;
    }
    return message != null ? message.equals(that.message) : that.message == null;
  }

  @Override
  public int hashCode() {
    int result = issueTypeId != null ? issueTypeId.hashCode() : 0;
    result = 31 * result + (file != null ? file.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + offsetStart;
    result = 31 * result + offsetEnd;
    result = 31 * result + line;
    return result;
  }

  @Override
  public String toString() {
    return "InspectCodeIssueModel{" +
        "issueTypeId='" + issueTypeId + '\'' +
        ", file='" + file + '\'' +
        ", message='" + message + '\'' +
        ", offsetStart=" + offsetStart +
        ", offsetEnd=" + offsetEnd +
        ", line=" + line +
        '}';
  }
  // endregion
}
