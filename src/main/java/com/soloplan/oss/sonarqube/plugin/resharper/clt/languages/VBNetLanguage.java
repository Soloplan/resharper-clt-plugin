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
import org.apache.commons.lang.StringUtils;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;

/**
 * This class defines the Visual Basic .NET language, but is not yet finished.
 *
 * @see <a href="https://www.sonarsource.com/products/codeanalyzers/sonarcsharp.html">SonarC# code analyzer</a>
 */
public class VBNetLanguage
    extends AbstractLanguage {

  /**
   * The key used to identify this language within SonarQube.
   */
  private static final String LANGUAGE_KEY = "ReSharper-CLT-vbnet";

  /**
   * The name of the language within SonarQube.
   */
  public static final String LANGUAGE_NAME = "vbnet";

  /**
   * An implementation of the {@link Configuration} interface provided to the constructor by SonarQube.
   */
  private final Configuration configuration;

  /**
   * Creates a new instance of the {@link VBNetLanguage} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param configuration
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public VBNetLanguage(Configuration configuration) {
    super(LANGUAGE_KEY, LANGUAGE_NAME);
    this.configuration = configuration;
  }

  @Override
  public String[] getFileSuffixes() {
    // Remove all null or empty strings from the array retrieved from the configuration
    String[] suffixes =
        Arrays.stream(StringUtils.stripAll(this.configuration.getStringArray(ReSharperCltConfiguration.PROPERTY_KEY_VBNET_FILE_SUFFIXES)))
            .filter(StringUtils::isNotBlank)
            .toArray(String[]::new);
    // Use the default file suffixes for the language if the configuration did not provide any
    if (suffixes.length == 0) {
      suffixes = StringUtils.split(ReSharperCltConfiguration.PROPERTY_KEY_VBNET_FILE_SUFFIXES_DEFAULT_VALUE, ",");
    }
    return suffixes;
  }
}
