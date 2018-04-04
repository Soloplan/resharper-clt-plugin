package com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces;

import org.sonar.api.config.PropertyDefinition;

/**
 * This interface defines an accessor method which will create an instance of the {@link PropertyDefinition} class.
 */
public interface PluginConfigurationProperty {
  /**
   * Returns an instance of the {@link PropertyDefinition} class containing the name, description, category and availability of the property
   * within the SonarQube settings.
   *
   * @return An instance of the {@link PropertyDefinition} class.
   */
  PropertyDefinition getProperty();
}
