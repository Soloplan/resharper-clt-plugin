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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.rules;

import org.sonar.api.config.Configuration;
import org.sonar.api.server.rule.RulesDefinition;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

/**
 * An implementation of the {@link RulesDefinition} interface which will create a new implementation of the {@link
 * org.sonar.api.server.rule.RulesDefinition.Repository} in order to provide additional rules for the Visual Basic .NET language to
 * SonarQube.
 */
public class VBNetRulesDefinition
    implements RulesDefinition {

  /**
   * Gets an implementation of the {@link Logger} interface for this class. Please note, that message arguments are defined with {@code {}},
   * but not with
   * <a href="https://docs.oracle.com/javase/8/docs/api/java/util/Formatter.html">Formatter</a> syntax.
   *
   * @see Logger
   */
  private static final Logger LOGGER = Loggers.get(VBNetRulesDefinition.class);

  /** Stores a reference to an instance of the {@link Configuration} class provided to the constructor by the SonarQube instance. */
  private Configuration configuration;

  /**
   * Creates a new instance of the {@link VBNetRulesDefinition} class storing a reference to the supplied {@link Configuration} instance
   * internally. The {@link Configuration} instance is provided via dependency injection. Visit the
   * <a href="https://docs.sonarqube.org/display/DEV/API+Basics#APIBasics-Configuration">official SonarQube API documentation</a> for more
   * information.
   *
   * @param config
   *     An instance of the {@link Configuration} class provided by the SonarQube instance.
   */
  public VBNetRulesDefinition(Configuration config) {
    this.configuration = config;
  }

  @Override
  public void define(Context context) {
    // TODO Implement this using a base class common to CSharpRulesDefinition and VBNetRulesDefinition
  }
}
