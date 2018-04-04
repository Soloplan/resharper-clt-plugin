package com.soloplan.oss.sonarqube.plugin.resharper.clt.properties;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration.ReSharperCltConfiguration;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.resources.Qualifiers;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract base class used to create {@link PropertyDefinition}s for a SonarQube {@link org.sonar.api.Plugin}.
 */
public abstract class BasePluginProperty {

  /**
   * Gets the {@link String} value to be used as {@link PropertyDefinition#key} when {@link #buildPropertyDefinition()} is called.
   *
   * @return A {@link String} value used to uniquely identify the property of the SonarQube plugin.
   */
  protected abstract String getKey();

  /**
   * Gets the {@link String} value to be used as {@link PropertyDefinition#name} when {@link #buildPropertyDefinition()} is called.
   *
   * @return A {@link String} value used as name of the property of the SonarQube plugin.
   */
  protected abstract String getName();

  /**
   * Gets the {@link String} value to be used as {@link PropertyDefinition#description} when {@link #buildPropertyDefinition()} is called.
   *
   * @return A {@link String} value used as human readable description of the property of the SonarQube plugin.
   */
  protected abstract String getDescription();

  /**
   * Gets the default {@link String} value to be used as {@link PropertyDefinition#defaultValue} for the {@link PropertyDefinition} being
   * generated when {@link #buildPropertyDefinition()} is called.
   *
   * @return A {@link String} value used as default value of the property of the SonarQube plugin. Returns an empty {@link String} by
   *     default.
   */
  @NotNull
  protected String getDefaultValue() {
    return "";
  }

  /**
   * Gets the {@link String} value to be used as {@link PropertyDefinition#category} identifier to group corresponding properties together
   * within SonarQube.
   *
   * @return A {@link String} value used as category identifier of the property of the SonarQube plugin. Returns {@value
   *     ReSharperCltConfiguration#PLUGIN_CONFIGURATION_PROPERTY_CATEGORY} by default.
   */
  protected String getCategory() {
    return ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_CATEGORY;
  }

  /**
   * Gets the {@link String} value to be used as {@link PropertyDefinition#subCategory} identifier to group corresponding properties further
   * together within SonarQube.
   *
   * @return A {@link String} value used as subcategory identifier of the property of the SonarQube plugin. Returns {@code null} by default.
   */
  @Nullable
  protected String getSubCategory() {
    return null;
  }

  /**
   * Gets a {@link List} of {@link String} values defining the exact types of resources to which this property applies.
   *
   * @return A {@link List} of {@link String} values used to determine where the property of the SonarQube plugin can be applied to. Returns
   *     a modifiable {@link List} containing only {@link Qualifiers#PROJECT} by default.
   */
  @NotNull
  protected List<String> getQualifiers() {
    final List<String> qualifiers = new ArrayList<>();
    qualifiers.add(Qualifiers.PROJECT);
    return qualifiers;
  }

  /**
   * Indicates, whether the property defined by this class is considered deprecated.
   *
   * @return A {@link String} value used as category identifier of the property of the SonarQube plugin. The default implementation checks
   *     whether the class defining the property is marked with the {@link Deprecated} annotation.
   */
  @SuppressWarnings("WeakerAccess")
  protected boolean isDeprecatedProperty() {
    return this.getClass().getAnnotation(Deprecated.class) != null;
  }

  /**
   * Builds an instance of the {@link PropertyDefinition} class using the values of this class.
   *
   * @return An instance of the {@link PropertyDefinition} class built from value of this class.
   */
  public PropertyDefinition buildPropertyDefinition() {
    return PropertyDefinition.builder(this.getKey())
        .name(this.isDeprecatedProperty() ? deprecatedName(this.getName()) : this.getName())
        .description(this.isDeprecatedProperty() ? deprecatedDescription(this.getDescription()) : this.getDescription())
        .defaultValue(this.getDefaultValue())
        .category(this.getCategory())
        .subCategory(this.getSubCategory())
        .onQualifiers(this.getQualifiers())
        .build();
  }

  /**
   * Adds a human-readable deprecation note to the supplied description, which will be displayed within SonarQube.
   *
   * @param description
   *     The description to be suffixed with a human-readable deprecation note.
   *
   * @return The supplied description appended with a human-readable deprecation note.
   *
   * @see #getDescription()
   */
  @NotNull
  @Contract(pure = true)
  private static String deprecatedDescription(String description) {
    return description + "<br /><br />" + ReSharperCltConfiguration.PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_DESCRIPTION;
  }

  /**
   * Adds a human-readable deprecation note at the start of the supplied name, which will be displayed within SonarQube.
   *
   * @param name
   *     The name to be prefixed with a human-readable deprecation note.
   *
   * @return The supplied name prepended with a human-readable deprecation note.
   *
   * @see #getName()
   */
  @NotNull
  @Contract(pure = true)
  private static String deprecatedName(String name) {
    return "Deprecated - " + name;
  }
}
