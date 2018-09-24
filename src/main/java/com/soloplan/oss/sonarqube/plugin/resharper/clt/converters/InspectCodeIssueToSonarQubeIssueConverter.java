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

import com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces.Converter;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeIssueModel;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.SonarQubeIssueModel;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * An implementation of the {@link Converter} interface specific to instances of class {@link InspectCodeIssueModel}, which will be
 * converted to instances of class {@link SonarQubeIssueModel}.
 */
public class InspectCodeIssueToSonarQubeIssueConverter
    implements Converter<InspectCodeIssueModel, SonarQubeIssueModel> {

  @Override
  public SonarQubeIssueModel convert(final InspectCodeIssueModel instance) {
    // Return null if the supplied InspectCode issue model is null
    if (instance == null) {
      return null;
    }

    // Create a new instance of the SonarQubeIssueModel class and fill in the values of the supplied InspectCode issue
    final SonarQubeIssueModel sonarQubeIssueModel = new SonarQubeIssueModel();
    sonarQubeIssueModel.setFilePath(instance.getFile());
    sonarQubeIssueModel.setMessage(instance.getMessage());
    sonarQubeIssueModel.setRuleKey(instance.getIssueTypeId());
    sonarQubeIssueModel.setTextRange(instance.getLine(), instance.getOffsetStart(), instance.getOffsetEnd());

    // TODO Fill more SonarQube properties like the gap for the debt remediation function?

    return sonarQubeIssueModel;
  }

  @Override
  public Collection<SonarQubeIssueModel> convert(final Collection<InspectCodeIssueModel> issueCollection) {
    // Return an empty collection if the supplied collection is either null or empty
    if (issueCollection == null || issueCollection.isEmpty()) {
      return java.util.Collections.emptySet();
    }

    // Convert the entire collection in parallel using the Java Stream API and return the converted collection
    return issueCollection.parallelStream()
        .map(this::convert)
        .collect(Collectors.toSet());
  }
}
