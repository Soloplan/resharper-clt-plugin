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

@SuppressWarnings("DeprecatedIsStillUsed")
@Deprecated
public final class InspectCodeVersionProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_INSPECTCODE_VERSION;
  }

  @Override
  protected String getName() {
    return "Version of inspectcode.exe command line tool";
  }

  @Override
  protected String getDescription() {
    return "Examples: 2017.1.2 or 2017.3.3";
  }

  @Override
  protected String getSubCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_SUBCATEGORY;
  }
}