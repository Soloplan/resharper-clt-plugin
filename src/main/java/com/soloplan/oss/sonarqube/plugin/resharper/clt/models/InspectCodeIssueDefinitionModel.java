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

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.InspectCodeIssueSeverity;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Data model class for {@code InspectCode} issue definitions.
 */
public class InspectCodeIssueDefinitionModel {

  // region Member variables

  /** The issue identifier that should be unique within {@code InspectCode}. */
  private final String issueId;

  /** The category name of the issue within {@code InspectCode}. */
  private String category;

  /** The category identifier of the issue within {@code InspectCode}. */
  private String categoryId;

  /** The human-readable description of the {@code InspectCode} issue. */
  private String description;

  /** Indicates, whether the {@code InspectCode} issue is defined in a global ({@code true}) or local ({@code false}) scope. */
  private boolean isGlobalRuleDefinition = false;

  /** The severity value as defined by {@code InspectCode}. */
  private InspectCodeIssueSeverity severity = InspectCodeIssueSeverity.getDefaultSeverity();

  /** The subcategory name of the issue within {@code InspectCode}. */
  private String subCategory;

  /** A uniform resource locator referring to a website where more information about this issue can be found. */
  private URL wikiUrl;

  // endregion

  /**
   * Creates a new instance of the {@link InspectCodeIssueDefinitionModel} class using the supplied {@code issueId} as unique identifier.
   *
   * @param issueId
   *     The unique identifier of this issue as defined by {@code InspectCode}.
   */
  public InspectCodeIssueDefinitionModel(@NotNull String issueId) {
    this.issueId = issueId.trim();
  }

  /**
   * Gets the unique identifier of this issue as defined by {@code InspectCode}.
   *
   * @return The unique identifier of this issue as defined by {@code InspectCode}.
   */
  public String getIssueId() {
    return issueId;
  }

  /**
   * Gets the category name of this issue.
   *
   * @return The category name of this issue.
   */
  public String getCategory() {
    return category;
  }

  /**
   * Sets the category name of this issue.
   *
   * @param category
   *     The new category name of this issue.
   */
  public void setCategory(String category) {
    this.category = category;
  }

  /**
   * Gets the category identifier of this issue.
   *
   * @return The category identifier of this issue.
   */
  public String getCategoryId() {
    return categoryId;
  }

  /**
   * Sets the category identifier of this issue.
   *
   * @param categoryId
   *     The new category identifier of this issue.
   */
  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }

  /**
   * Gets the human-readable description of this issue.
   *
   * @return The human-readable description of this issue.
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the human-readable description of this issue.
   *
   * @param description
   *     The new human-readable description of this issue.
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets a {@code boolean} value indicating, whether the issue is defined by {@code InspectCode} in a global ({@code true}) or local
   * ({@code false}) scope.
   *
   * @return A {@code boolean} value indicating, whether the issue is defined by {@code InspectCode} in a global ({@code true}) or local *
   *     ({@code false}) scope.
   */
  public boolean isGlobalRuleDefinition() {
    return isGlobalRuleDefinition;
  }

  /**
   * Sets the {@code boolean} value that indicates whether the issue is defined by {@code InspectCode} in a global ({@code true}) or local
   * ({@code false}) scope.
   *
   * @param globalRuleDefinition
   *     The new {@code boolean} scope indicator value.
   */
  public void setGlobalRuleDefinition(boolean globalRuleDefinition) {
    this.isGlobalRuleDefinition = globalRuleDefinition;
  }

  /**
   * Sets the {@code boolean} value that indicates whether the issue is defined by {@code InspectCode} in a global ({@code true}) or local
   * ({@code false}) scope by parsing the supplied {@link String} argument to its corresponding {@link Boolean} representation.
   *
   * @param booleanValue
   *     A {@link String} value that will be parsed to its {@link Boolean} representation and set as the new {@code boolean} scope indicator
   *     value.
   */
  public void setGlobalRuleDefinition(String booleanValue) {
    this.isGlobalRuleDefinition = Boolean.parseBoolean(booleanValue);
  }

  /**
   * Gets the severity value of this issue as defined by {@code InspectCode}.
   *
   * @return The severity value of this issue as defined by {@code InspectCode}.
   */
  public InspectCodeIssueSeverity getSeverity() {
    return severity;
  }

  /**
   * Sets the severity value of this issue as defined by {@code InspectCode}.
   *
   * @param severity
   *     The new severity value of this issue as defined by {@code InspectCode}.
   */
  public void setSeverity(InspectCodeIssueSeverity severity) {
    this.severity = severity;
  }

  /**
   * Sets the severity value of this issue by parsing the supplied {@link String} to its corresponding {@link InspectCodeIssueSeverity}
   * value.
   *
   * @param severityValue
   *     A {@link String} value that will be parsed to its corresponding {@link InspectCodeIssueSeverity} value and set as the new new
   *     severity value of this issue as defined by {@code InspectCode}.
   */
  public void setSeverity(String severityValue) {
    this.severity = InspectCodeIssueSeverity.fromSeverityValue(severityValue);
  }

  /**
   * Gets the sub category name of this issue.
   *
   * @return The sub category name of this issue.
   */
  public String getSubCategory() {
    return subCategory;
  }

  /**
   * Sets the sub category name of this issue.
   *
   * @param subCategory
   *     The new sub category name of this issue.
   */
  public void setSubCategory(String subCategory) {
    this.subCategory = subCategory;
  }

  /**
   * Gets the uniform resource locator, referring to a website where more information about this issue can be found.
   *
   * @return The uniform resource locator, referring to a website where more information about this issue can be found.
   */
  public URL getWikiUrl() {
    return wikiUrl;
  }

  /**
   * Sets the uniform resource locator by parsing the supplied {@link String} to a valid {@link URL} instance, setting {@link #wikiUrl} to
   * {@code null} if the supplied {@link String} is not a valid uniform resource locator.
   *
   * @param wikiUrl
   *     The {@link String} representation of the new uniform resource locator, which is converted to a valid {@link URL} instance. If the
   *     supplied {@link String} is not a valid uniform resource locator, the the internal {@link #wikiUrl} value is set to {@code null}.
   */
  public void setWikiUrl(String wikiUrl) {
    // Reset the member variable if the supplied argument is null or an empty string
    if (wikiUrl == null || wikiUrl.trim().isEmpty()) {
      this.wikiUrl = null;
    } else {
      try {
        // Try to parse the supplied string to a valid URL, ignoring any exception
        this.wikiUrl = new URL(wikiUrl);
      } catch (MalformedURLException exception) { /* ignore */ }
    }
  }

  /**
   * Sets the uniform resource locator, referring to a website where more information about this issue can be found.
   *
   * @param wikiUrl
   *     The new uniform resource locator, referring to a website where more information about this issue can be found.
   */
  public void setWikiUrl(URL wikiUrl) {
    this.wikiUrl = wikiUrl;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    return this.issueId.equals(((InspectCodeIssueDefinitionModel) other).issueId);
  }

  @Override
  public int hashCode() {
    return this.issueId.hashCode();
  }

  @Override
  public String toString() {
    return "InspectCodeIssueDefinitionModel{" +
        "issueId='" + issueId + '\'' +
        ", category='" + category + '\'' +
        ", categoryId='" + categoryId + '\'' +
        ", description='" + description + '\'' +
        ", isGlobalRuleDefinition=" + isGlobalRuleDefinition +
        ", severity=" + severity +
        ", subCategory='" + subCategory + '\'' +
        ", wikiUrl=" + wikiUrl +
        '}';
  }
}
