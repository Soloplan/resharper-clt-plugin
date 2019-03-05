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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.languages;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.config.Configuration;

/**
 * This class defines the C# language, which should not be necessary if the SonarC# code analyzer plugin is already installed.
 *
 * @see <a href="https://www.sonarsource.com/products/codeanalyzers/sonarcsharp.html">SonarC# code analyzer</a>
 */
public final class CSharpLanguage
    extends BaseLanguage {

  /**
   * The key used to identify this language within SonarQube.
   */
  private static final String LANGUAGE_KEY = "resharper-clt-cs";

  /**
   * The name of the language within SonarQube which should already be defined by the SonarC# plugin.
   *
   * @see <a href="https://www.sonarsource.com/products/codeanalyzers/sonarcsharp.html">SonarC# code analyzer</a> and its
   *     <a href="https://github.com/SonarSource/sonar-csharp/blob/master/sonar-csharp-plugin/src/main/java/org/sonar/plugins/csharp/CSharpPlugin.java">source
   *     code on GitHub</a>.
   */
  public static final String LANGUAGE_NAME = "cs";

  /**
   * Creates a new instance of the {@link CSharpLanguage} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param configuration
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public CSharpLanguage(@NotNull final Configuration configuration) {
    super(
        new LanguageConfiguration(
            LANGUAGE_KEY,
            LANGUAGE_NAME,
            ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES,
            ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES_DEFAULT_VALUE),
        configuration);
  }
}
