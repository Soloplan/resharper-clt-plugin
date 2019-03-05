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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.resources.Qualifiers;

import java.util.List;

public final class CSharpReportPathProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH;
  }

  @Override
  protected String getName() {
    return "ReSharper report path for C#";
  }

  @Override
  protected String getDescription() {
    return "Path to the ReSharper report for C#, i.e. reports/cs-report.xml";
  }

  @NotNull
  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_CS_SUBCATEGORY;
  }

  @Override
  protected @NotNull List<String> getQualifiers() {
    final List<String> qualifiers = super.getQualifiers();
    qualifiers.add(Qualifiers.MODULE);
    return qualifiers;
  }
}
