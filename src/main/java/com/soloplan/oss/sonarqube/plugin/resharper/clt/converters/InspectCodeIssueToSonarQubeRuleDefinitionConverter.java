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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.converters;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.InspectCodeIssueSeverity;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeRuleDescriptionSyntax;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.enumerations.SonarQubeSeverity;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.Converter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeRuleDefinitionModel;
import org.apache.commons.lang.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.rule.RuleStatus;
import org.sonar.api.rules.RuleType;

import java.net.URL;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link Converter} interface specific to instances of class {@link InspectCodeIssueDefinitionModel}, which will
 * be converted to instances of class {@link SonarQubeRuleDefinitionModel}.
 */
public class InspectCodeIssueToSonarQubeRuleDefinitionConverter
    implements Converter<InspectCodeIssueDefinitionModel, SonarQubeRuleDefinitionModel> {

  @Override
  public SonarQubeRuleDefinitionModel convert(@Nullable InspectCodeIssueDefinitionModel inspectCodeIssueDefinitionModel) {
    // Return null if the supplied InspectCode issue definition is null
    if (inspectCodeIssueDefinitionModel == null) {
      return null;
    }

    // Create a new instance of the SonarQubeRuleDefinitionModel class and fill in the values of the supplied InspectCode issue definition
    final SonarQubeRuleDefinitionModel ruleDefinitionModel =
        new SonarQubeRuleDefinitionModel(inspectCodeIssueDefinitionModel.getIssueTypeId());
    ruleDefinitionModel.setRuleName(inspectCodeIssueDefinitionModel.getIssueTypeId());
    ruleDefinitionModel.setActivatedByDefault(false);
    ruleDefinitionModel.setRuleDescription(
        combineRuleDescription(inspectCodeIssueDefinitionModel.getDescription(), inspectCodeIssueDefinitionModel.getWikiUrl()),
        SonarQubeRuleDescriptionSyntax.HTML);
    ruleDefinitionModel.setRuleStatus(RuleStatus.READY);
    ruleDefinitionModel.setRuleType(convertInspectCodeSeverityToRuleType(inspectCodeIssueDefinitionModel.getSeverity()));
    ruleDefinitionModel.setSonarQubeSeverity(convertInspectCodeSeverityToSonarQubeSeverity(inspectCodeIssueDefinitionModel.getSeverity()));

    // TODO Fill more SonarQube properties and metrics like the debt remediation function?

    return ruleDefinitionModel;
  }

  @Override
  public Collection<SonarQubeRuleDefinitionModel> convert(@Nullable Collection<InspectCodeIssueDefinitionModel> issueDefinitionCollection) {
    // Return an empty collection if the supplied collection is either null or empty
    if (issueDefinitionCollection == null || issueDefinitionCollection.isEmpty()) {
      return java.util.Collections.emptySet();
    }

    // Convert the entire collection in parallel using the Java Stream API and return the converted collection
    return issueDefinitionCollection.parallelStream()
        .map(this::convert)
        .collect(Collectors.toSet());
  }

  /**
   * Creates a new HTML formatted description by escaping all HTML entities within the supplied {@code description} and appending a
   * hyperlink targeting the supplied {@code url} to the resulting description if the {@code url} is not {@code null}. If the supplied
   * {@code description} is {@code null} or an empty string, a default description will be returned.
   *
   * @param description
   *     The description of which all HTML entities will be escaped and to which the supplied {@code url} will be appended if not {@code
   *     null}. Passing {@code null} or an empty string as argument will result in a default string to be returned.
   * @param url
   *     A uniform resource locator for which an HTML hyperlink will be created and appended to the supplied {@code description}.
   *
   * @return A new HTML formatted description based on the supplied {@code description}, where all of its HTML entities have been escaped.
   *     If the supplied {@code url} is not {@code null}, an HTML hyperlink will be appended to the description.
   */
  private static String combineRuleDescription(@Nullable String description, @Nullable URL url) {
    // Sanitize the supplied description
    description = (description == null || description.trim().isEmpty())
        ? "(this rule does not provide a description)"
        : StringEscapeUtils.escapeHtml(description.trim());
    if (url == null) {
      return description;
    } else {
      return description + "<br /><a href=\"" + url.toString() + "\">" + url.toString() + "</a>";
    }
  }

  /**
   * Converts the supplied {@link InspectCodeIssueSeverity} value to a SonarQube {@link RuleType}.
   *
   * @param inspectCodeIssueSeverity
   *     The {@link InspectCodeIssueSeverity} for which the corresponding SonarQube {@link RuleType} is requested.
   *
   * @return The corresponding SonarQube {@link RuleType} for the supplied {@link InspectCodeIssueSeverity}.
   */
  private static RuleType convertInspectCodeSeverityToRuleType(@NotNull InspectCodeIssueSeverity inspectCodeIssueSeverity) {
    switch (inspectCodeIssueSeverity) {
      case DoNotShow:
      case InvalidSeverity:
      case Hint:
      case Suggestion:
        return RuleType.CODE_SMELL;
      case Warning:
      case Error:
        return RuleType.BUG;
      default:
        return RuleType.CODE_SMELL;
    }
  }

  /**
   * Converts the supplied {@link InspectCodeIssueSeverity} value to a {@link SonarQubeSeverity} value.
   *
   * @param inspectCodeIssueSeverity
   *     The {@link InspectCodeIssueSeverity} for which the corresponding {@link SonarQubeSeverity} value is requested.
   *
   * @return The corresponding {@link SonarQubeSeverity} value for the supplied {@link InspectCodeIssueSeverity}.
   */
  private static SonarQubeSeverity convertInspectCodeSeverityToSonarQubeSeverity(@NotNull InspectCodeIssueSeverity inspectCodeIssueSeverity) {
    switch (inspectCodeIssueSeverity) {
      case DoNotShow:
      case InvalidSeverity:
      case Hint:
        return SonarQubeSeverity.Info;
      case Suggestion:
        return SonarQubeSeverity.Minor;
      case Warning:
        return SonarQubeSeverity.Major;
      case Error:
        return SonarQubeSeverity.Critical;
      default:
        return SonarQubeSeverity.getDefaultSeverity();
    }
  }
}
