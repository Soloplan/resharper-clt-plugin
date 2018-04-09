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
import org.jetbrains.annotations.Nullable;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;

/**
 * This class defines the C# language, which should not be necessary if the SonarC# code analyzer plugin is already installed.
 *
 * @see <a href="https://www.sonarsource.com/products/codeanalyzers/sonarcsharp.html">SonarC# code analyzer</a>
 */
public final class CSharpLanguage
    extends AbstractLanguage {

  /**
   * The key used to identify this language within SonarQube.
   */
  private static final String LANGUAGE_KEY = "ReSharper-CLT-cs";

  /**
   * The name of the language within SonarQube which should already be defined by the SonarC# plugin.
   *
   * @see <a href="https://www.sonarsource.com/products/codeanalyzers/sonarcsharp.html">SonarC# code analyzer</a> and its
   *     <a href="https://github.com/SonarSource/sonar-csharp/blob/master/sonar-csharp-plugin/src/main/java/org/sonar/plugins/csharp/CSharpPlugin.java">source
   *     code on GitHub</a>.
   */
  public static final String LANGUAGE_NAME = "cs";

  /**
   * An implementation of the {@link Configuration} interface provided to the constructor by SonarQube.
   */
  private final Configuration config;

  /**
   * Creates a new instance of the {@link CSharpLanguage} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param config
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public CSharpLanguage(Configuration config) {
    super(LANGUAGE_KEY, LANGUAGE_NAME);
    this.config = config;
  }

  @Override
  public String[] getFileSuffixes() {
    String[] suffixes = removeEmptyStrings(config.getStringArray(ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES));
    if (suffixes.length == 0) {
      suffixes = StringUtils.split(ReSharperCltConfiguration.PROPERTY_KEY_CS_FILE_SUFFIXES_DEFAULT_VALUE, ",");
    }
    return suffixes;
  }

  /**
   * Returns a new array based on the supplied {@code stringArray} with all entries that are {@code null} or empty strings removed.
   *
   * @param stringArray
   *     An array of {@link String}s to be sanitized. Might be {@code null}.
   *
   * @return A new array based on the supplied {@code stringArray} with all entries that are {@code null} or empty strings removed.
   */
  private String[] removeEmptyStrings(@Nullable String[] stringArray) {
    // Return a new empty array if the supplied array is either null or empty
    if (stringArray == null || stringArray.length == 0) {
      return new String[0];
    }

    // Use the Java stream API to trim all entries of the array and filter out any null or empty strings
    return Arrays.stream(stringArray)
        .map(StringUtils::trimToEmpty)
        .filter(StringUtils::isNotBlank)
        .toArray(String[]::new);
  }

  @Override
  public boolean equals(Object other) {
    // The base class will decide the equality based on the language key
    return super.equals(other);
  }

  @Override
  public int hashCode() {
    // The base class will return the hash code of the language key
    return super.hashCode();
  }
}
