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

/**
 * Defines a property accessible within SonarQube to define a comma-separated list of file name suffixes to indicate which files should be
 * analyzed by the plugin.
 */
public final class CSharpLanguageProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES;
  }

  @Override
  protected String getName() {
    return "File Suffixes";
  }

  @Override
  protected String getDescription() {
    return "Comma-separated list of suffixes for files to analyze.";
  }

  @NotNull
  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_CS_SUBCATEGORY;
  }

  @Override
  protected @NotNull String getDefaultValue() {
    return ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES_DEFAULT_VALUE;
  }
}
