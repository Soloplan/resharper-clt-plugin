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

package com.soloplan.oss.sonarqube.plugin.resharper.clt;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.properties.*;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.rules.CSharpRulesDefinition;
import org.sonar.api.Plugin;

/**
 * This class is the main entry point of the SonarQube plugin and responsible for registering all extensions. It is referenced in the {@code
 * pom.xml} file and will be instantiated by the SonarQube instance.
 */
public class ReSharperCltPlugin
    implements Plugin {

  @Override
  public void define(Context context) {
    // Register languages supported by this plugin
    // context.addExtension(CSharpLanguage.class); // NOTE: Not yet finished.

    // Register rules defined by this plugin
    context.addExtension(CSharpRulesDefinition.class);

    // Register plugin properties
    context.addExtensions(
        new CSharpLanguageProperty().buildPropertyDefinition(),
        new CSharpReportPathProperty().buildPropertyDefinition(),
        new VisualBasicNetReportPathProperty().buildPropertyDefinition(),
        new SolutionFileProperty().buildPropertyDefinition(),
        new ProjectNameProperty().buildPropertyDefinition(),
        new InspectCodePathProperty().buildPropertyDefinition(),
        new InspectCodeVersionProperty().buildPropertyDefinition(),
        new InspectCodeXmlFileSchemaValidationProperty().buildPropertyDefinition()
    );
  }
}
