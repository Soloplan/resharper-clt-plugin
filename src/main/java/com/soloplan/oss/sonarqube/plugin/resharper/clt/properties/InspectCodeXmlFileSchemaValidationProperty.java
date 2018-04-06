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
 * Defines a property accessible within SonarQube to indicate whether all {@code InspectCode} XML file analyzed by this plugin should be
 * validated using an XML Schema Definition file.
 */
public class InspectCodeXmlFileSchemaValidationProperty
    extends BasePluginProperty {

  @Override
  protected String getKey() {
    return ReSharperCltConfiguration.PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION;
  }

  @Override
  protected String getName() {
    return "InspectCode XML file validation";
  }

  @Override
  protected String getDescription() {
    return "Enable validation of InspectCode XML files using an XML Schema Definition file.";
  }

  @Override
  protected @NotNull String getDefaultValue() {
    return Boolean.FALSE.toString();
  }
}
