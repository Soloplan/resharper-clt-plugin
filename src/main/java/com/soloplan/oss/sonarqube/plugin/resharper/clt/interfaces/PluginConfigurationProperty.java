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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces;

import org.sonar.api.config.PropertyDefinition;

/**
 * This interface defines an accessor method that will create an instance of the {@link PropertyDefinition} class, which will be used to
 * create property available within the configuration page of SonarQube.
 */
public interface PluginConfigurationProperty {
  /**
   * Returns an instance of the {@link PropertyDefinition} class which in turn will create a property available within the SonarQube
   * configuration page.
   *
   * @return An instance of the {@link PropertyDefinition} class.
   */
  PropertyDefinition buildPropertyDefinition();
}
