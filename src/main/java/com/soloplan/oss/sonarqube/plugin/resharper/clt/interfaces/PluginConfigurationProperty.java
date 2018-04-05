package com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces;

import org.sonar.api.config.PropertyDefinition;

/**
 * This interface defines an accessor method that will create an instance of the {@link PropertyDefinition} class, which will be used to
 * create property available within the configuration page of SonarQube.
 */
public interface PluginConfigurationProperty {
  /**
   * Returns an instance of the {@link PropertyDefinition} class which in turn will create a property available within the SonarQube
   * configuration page.
   *
   * @return An instance of the {@link PropertyDefinition} class.
   */
  PropertyDefinition buildPropertyDefinition();
}
