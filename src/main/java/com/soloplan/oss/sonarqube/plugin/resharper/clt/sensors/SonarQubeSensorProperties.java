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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.sensors;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.batch.sensor.Sensor;
import org.sonar.api.config.Configuration;
import org.sonar.api.utils.log.Logger;

/**
 * Package visible class used internally for implementations of the {@link Sensor} interface to provide centralized access to specific
 * properties that are mandatory for the usage of the plugin. In order to be safely used, method {@link #validatePropertyValues(Logger)}
 * should be called prior to accessing the properties.
 */
class SonarQubeSensorProperties {
  // region Member variables
  /** The value of property {@value ReSharperCltConfiguration#PROPERTY_KEY_PROJECT_NAME}. */
  final String projectName;
  /** The value of property {@value ReSharperCltConfiguration#PROPERTY_KEY_SOLUTION_FILE}. */
  final String solutionFileName;
  /** The value of property {@value ReSharperCltConfiguration#PROPERTY_KEY_USER_DIRECTORY}. */
  final String userDir;
  //endregion

  /**
   * Retrieves values from properties defined by the supplied {@code configuration} and stores their values in members of the {@link
   * SonarQubeSensorProperties} class. Theses members should be validated before being used within the code through calling {@link
   * #validatePropertyValues(Logger)}.
   *
   * @param configuration
   *     An instance of the SonarQube {@link Configuration} class from which the values of the internal properties should be retrieved.
   */
  SonarQubeSensorProperties(@NotNull final Configuration configuration) {
    // Read property values from the SonarQube configuration
    this.projectName = configuration.get(ReSharperCltConfiguration.PROPERTY_KEY_SONAR_PROJECT_NAME).orElse("");
    this.solutionFileName = configuration.get(ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE).orElse("");
    this.userDir = configuration.get(ReSharperCltConfiguration.PROPERTY_KEY_USER_DIRECTORY).orElse("");
  }

  /**
   * Validates the values of the members of this {@link SonarQubeSensorProperties} instance, printing log messages using the supplied {@code
   * logger} if any of the property values is invalid. Returns {@code false} if any of the mandatory property values are invalid.
   *
   * @param logger
   *     An implementation of the {@link Logger} interface used to print log messages about invalid property values.
   *
   * @return {@code True} if the validation succeeded, {@code false} if any of the mandatory property values are invalid.
   */
  boolean validatePropertyValues(@NotNull Logger logger) {
    // Initialize the resulting variable
    boolean isAnyPropertyNotSet = false;

    // Verify that the mandatory properties are set
    if (this.projectName.isEmpty()) {
      logger.warn("Property {} is not defined. Could not get the name of the current project.",
          ReSharperCltConfiguration.PROPERTY_KEY_SONAR_PROJECT_NAME);
      isAnyPropertyNotSet = true;
    }
    if (this.userDir.isEmpty()) {
      logger.warn("Property {} is not defined. Could not get the base directory of the InspectCode analysis.",
          ReSharperCltConfiguration.PROPERTY_KEY_USER_DIRECTORY);
      isAnyPropertyNotSet = true;
    }
    if (this.solutionFileName.isEmpty()) {
      logger.warn("Property {} is not defined. Could not get the path to the solution file inspected by InspectCode.",
          ReSharperCltConfiguration.PROPERTY_KEY_SOLUTION_FILE);
      isAnyPropertyNotSet = true;
    }

    // Return true if all of the mandatory properties are set to indicate a successful validation
    return !isAnyPropertyNotSet;
  }

  @Override
  public String toString() {
    return "SonarQubeProperties{" +
        "projectName='" + this.projectName + '\'' +
        ", solutionFileName='" + this.solutionFileName + '\'' +
        ", userDir='" + this.userDir + '\'' +
        '}';
  }
}
