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

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class ProjectNameProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_PROJECT_NAME;
  }

  @Override
  protected String getName() {
    return "Visual Studio project name";
  }

  @Override
  protected String getDescription() {
    return "Example: MyLibrary. File extensions .csproj and .vbproj are assumed here.";
  }

  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_SUBCATEGORY;
  }

  @Override
  protected @NotNull List<String> getQualifiers() {
    final List<String> qualifiers = super.getQualifiers();
    qualifiers.add(Qualifiers.MODULE);
    return qualifiers;
  }
}