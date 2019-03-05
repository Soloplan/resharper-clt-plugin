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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.rules;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.VBNetLanguage;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueDefinitionModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates.InspectCodePredicates;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates.ObjectPredicates;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.config.Configuration;
import org.sonar.api.server.rule.RulesDefinition;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * An implementation of the {@link RulesDefinition} interface which will create a new implementation of the {@link
 * org.sonar.api.server.rule.RulesDefinition.Repository} in order to provide additional rules for the Visual Basic .NET language to
 * SonarQube.
 */
public class VBNetRulesDefinition
    extends BaseRulesDefinition {

  /**
   * Creates a new instance of the {@link VBNetRulesDefinition} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param configuration
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public VBNetRulesDefinition(Configuration configuration) {
    super(
        new RulesRepositoryConfiguration(
            ReSharperCltConfiguration.RULES_REPOSITORY_VBNET_KEY,
            ReSharperCltConfiguration.RULES_REPOSITORY_VBNET_NAME,
            VBNetLanguage.LANGUAGE_NAME),
        configuration);
  }

  @Override
  protected @Nullable Collection<Predicate<InspectCodeIssueDefinitionModel>> getIssueDefinitionFilterPredicates() {
    return Arrays.asList(
        ObjectPredicates.isNotNullPredicate(),
        InspectCodePredicates.hasValidIssueSeverity(),
        InspectCodePredicates.hasNonEmptyIssueDescription(),
        InspectCodePredicates.isVisualBasicIssueDefinition(),
        InspectCodePredicates.isWebRelatedCategory().negate());
  }
}
