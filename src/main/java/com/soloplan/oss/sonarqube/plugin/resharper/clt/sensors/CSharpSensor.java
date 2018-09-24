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
import com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.CSharpLanguage;

/** A sensor that will parse issues detected by the {@code InspectCode} command line tool for C# files. */
public class CSharpSensor
    extends BaseSensor {

  /**
   * Creates a new {@link CSharpSensor} instance that will parse the issues detected by the {@code InspectCode} command line tool from the
   * report file located where property {@value ReSharperCltConfiguration#PROPERTY_KEY_CS_REPORT_PATH} points to and creates new issues
   * within SonarQube. This sensor is restricted to {@code C#} files and will only use rules from the SonarQube rule repository identified
   * by key {@value ReSharperCltConfiguration#RULES_REPOSITORY_CSHARP_KEY}.
   */
  public CSharpSensor() {
    super(
        new SensorConfiguration(
            "ReSharper Command line tools (InspectCode) C# Sensor",
            CSharpLanguage.LANGUAGE_NAME,
            ReSharperCltConfiguration.RULES_REPOSITORY_CSHARP_KEY,
            ReSharperCltConfiguration.PROPERTY_KEY_CS_REPORT_PATH
        ));
  }
}
