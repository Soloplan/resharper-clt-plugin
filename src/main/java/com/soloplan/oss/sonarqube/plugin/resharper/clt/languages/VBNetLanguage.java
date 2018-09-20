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
 * This class defines the Visual Basic .NET language.
 *
 * @see <a href="https://www.sonarsource.com/products/codeanalyzers/sonarvbnet.html">SonarVB code analyzer</a>
 */
public final class VBNetLanguage
    extends BaseLanguage {

  /**
   * The key used to identify this language within SonarQube.
   */
  private static final String LANGUAGE_KEY = "resharper-clt-vbnet";

  /**
   * The name of the language within SonarQube.
   */
  public static final String LANGUAGE_NAME = "vbnet";

  /**
   * Creates a new instance of the {@link VBNetLanguage} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param configuration
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public VBNetLanguage(@NotNull final Configuration configuration) {
    super(
        new LanguageConfiguration(
            LANGUAGE_KEY,
            LANGUAGE_NAME,
            ReSharperCltConfiguration.PROPERTY_KEY_VBNET_FILE_SUFFIXES,
            ReSharperCltConfiguration.PROPERTY_KEY_VBNET_FILE_SUFFIXES_DEFAULT_VALUE),
        configuration);
  }
}
