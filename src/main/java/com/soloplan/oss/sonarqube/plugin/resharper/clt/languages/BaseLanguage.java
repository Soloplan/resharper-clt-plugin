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

import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.sonar.api.config.Configuration;
import org.sonar.api.resources.AbstractLanguage;

import java.util.Arrays;

/** This class provides a common base class for languages supported by this plugin. */
public abstract class BaseLanguage
    extends AbstractLanguage {

  /** Stores a reference to the {@link LanguageConfiguration} instance supplied to the constructor. */
  private final LanguageConfiguration languageConfiguration;

  /** An implementation of the {@link Configuration} interface provided to the constructor. */
  protected final Configuration configuration;

  /**
   * Creates a new instance of the {@link BaseLanguage} class using the supplied arguments.
   *
   * @param languageConfiguration
   *     The {@link LanguageConfiguration} providing configuration values for the language.
   * @param configuration
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  BaseLanguage(@NotNull final LanguageConfiguration languageConfiguration, Configuration configuration) {
    super(languageConfiguration.languageKey, languageConfiguration.languageName);
    this.languageConfiguration = languageConfiguration;
    this.configuration = configuration;
  }

  @Override
  public String[] getFileSuffixes() {
    // Remove all null or empty strings from the array retrieved from the configuration
    String[] suffixes =
        Arrays.stream(StringUtils.stripAll(this.configuration.getStringArray(this.languageConfiguration.fileSuffixesPropertyIdentifier)))
            .filter(StringUtils::isNotBlank)
            .toArray(String[]::new);
    // Use the default file suffixes for the language if the configuration did not provide any
    if (suffixes.length == 0) {
      suffixes = StringUtils.split(this.languageConfiguration.fileSuffixesPropertyIdentifierDefaultValues, ",");
    }
    return suffixes;
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

  /**
   * A package private class containing required information for setting up an instance of the {@link BaseLanguage} class. Using this class
   * helps keeping the parameter list of the constructor concise and readable.
   */
  static final class LanguageConfiguration {

    /** The key used to uniquely identify the language within SonarQube. */
    final String languageKey;

    /** The human-readable name of the language as can be seen within the SonarQube web dashboard. */
    final String languageName;

    /** The property identifier used to retrieve the valid file suffixes for this language from the SonarQube {@link Configuration}. */
    final String fileSuffixesPropertyIdentifier;

    /**
     * The property identifier used to retrieve the default file suffixes for this language from the SonarQube {@link Configuration} if no
     * valid file suffixes could be retrieved using the {@link #fileSuffixesPropertyIdentifier}.
     */
    final String fileSuffixesPropertyIdentifierDefaultValues;

    /**
     * Creates a new instance of the {@link LanguageConfiguration} class with the supplied arguments. All supplied {@link String} arguments
     * will be trimmed before stored internally.
     *
     * @param languageKey
     *     The key used to uniquely identify the language within SonarQube.
     * @param languageName
     *     The human-readable name of the language as can be seen within the SonarQube web dashboard.
     * @param fileSuffixesPropertyIdentifier
     *     The property identifier used to retrieve the valid file suffixes for this language from the SonarQube {@link Configuration}.
     * @param fileSuffixesPropertyIdentifierDefaultValues
     *     The property identifier used to retrieve the default file suffixes for this language from the SonarQube {@link Configuration} if
     *     no valid file suffixes could be retrieved using the {@link #fileSuffixesPropertyIdentifier}.
     */
    LanguageConfiguration(
        @NotNull final String languageKey,
        @NotNull final String languageName,
        @NotNull final String fileSuffixesPropertyIdentifier,
        @NotNull final String fileSuffixesPropertyIdentifierDefaultValues) {
      this.languageKey = languageKey.trim();
      this.languageName = languageName.trim();
      this.fileSuffixesPropertyIdentifier = fileSuffixesPropertyIdentifier.trim();
      this.fileSuffixesPropertyIdentifierDefaultValues = fileSuffixesPropertyIdentifierDefaultValues.trim();
    }
  }
}
