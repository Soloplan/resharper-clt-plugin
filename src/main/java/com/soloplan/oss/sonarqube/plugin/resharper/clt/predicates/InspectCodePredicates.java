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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.InspectCodeIssueSeverity;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueDefinitionModel;

import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * A factory class that produces {@link Predicate}s for instances of the {@link InspectCodeIssueDefinitionModel} class.
 */
public final class InspectCodePredicates {

  /**
   * A case-insensitive, pre-compiled regular expression {@link Pattern} that excludes specific strings, which are set as value of {@link
   * InspectCodeIssueDefinitionModel#getIssueId()} and known to not belong to the C# language. More specifically, this {@link Pattern} will
   * filter out any string that starts with any of the values defined within the round brackets, ignoring whitespace characters at the start
   * of the string and ensuring that the match will consist of at least three characters.
   */
  private static final Pattern PATTERN_ISSUE_IDENTIFIER_CSHARP =
      Pattern.compile("^\\s*(?!AngularHtml\\.|Asp\\.|Cpp|Css|Es\\dFeature|Html\\.|VB|Web\\.|WebConfig\\.)\\S{3,}", Pattern.CASE_INSENSITIVE);

  /**
   * Creates a new {@link Predicate} that will filter out all {@link InspectCodeIssueDefinitionModel} instances having an {@link
   * InspectCodeIssueSeverity} of {@link InspectCodeIssueSeverity#DoNotShow} or {@link InspectCodeIssueSeverity#InvalidSeverity}.
   *
   * @return A {@link Predicate} that will filter out all {@link InspectCodeIssueDefinitionModel} instances with a severity value of {@link
   *     InspectCodeIssueSeverity#DoNotShow} and {@link InspectCodeIssueSeverity#InvalidSeverity}.
   */
  public static Predicate<InspectCodeIssueDefinitionModel> hasValidIssueSeverity() {
    return inspectCodeIssueDefinitionModel ->
        inspectCodeIssueDefinitionModel.getSeverity() != InspectCodeIssueSeverity.DoNotShow
            && inspectCodeIssueDefinitionModel.getSeverity() != InspectCodeIssueSeverity.InvalidSeverity;
  }

  /**
   * Creates a new {@link Predicate} that will filter out all {@link InspectCodeIssueDefinitionModel} instances returning an empty string
   * when querying {@link InspectCodeIssueDefinitionModel#getDescription()}, ignoring any leading or trailing whitespace characters.
   *
   * @return A {@link Predicate} that will filter out all {@link InspectCodeIssueDefinitionModel} instances returning an empty string as
   *     their description.
   */
  public static Predicate<InspectCodeIssueDefinitionModel> hasNonEmptyIssueDescription() {
    return inspectCodeIssueDefinitionModel ->
        inspectCodeIssueDefinitionModel.getDescription() != null
            && !inspectCodeIssueDefinitionModel.getDescription().trim().isEmpty();
  }

  /**
   * Creates a new {@link Predicate} that will filter out all {@link InspectCodeIssueDefinitionModel} instances having an issue identifier
   * that is not associated with the C# language.
   *
   * @return A {@link Predicate} that will filter out all {@link InspectCodeIssueDefinitionModel} instances having an issue identifier that
   *     is not associated with the C# language.
   */
  public static Predicate<InspectCodeIssueDefinitionModel> isCSharpIssueDefinition() {
    return inspectCodeIssueDefinitionModel ->
        PATTERN_ISSUE_IDENTIFIER_CSHARP.matcher(inspectCodeIssueDefinitionModel.getIssueId()).matches();
  }

  /**
   * Creates a new {@link Predicate} that can be used to keep all {@link InspectCodeIssueDefinitionModel} instances of a category associated
   * with web development.
   *
   * @return A {@link Predicate} that will allow all {@link InspectCodeIssueDefinitionModel} instances of a category associated with web
   *     development.
   */
  public static Predicate<InspectCodeIssueDefinitionModel> isWebRelatedCategory() {
    return inspectCodeIssueDefinitionModel ->
        inspectCodeIssueDefinitionModel.getCategory() != null
            && inspectCodeIssueDefinitionModel.getCategory().equalsIgnoreCase("JsStrictModeErrors");
  }
}
