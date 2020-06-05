package com.soloplan.oss.sonarqube.plugin.resharper.clt.models;

import org.jetbrains.annotations.NotNull;

public class InspectCodeCategoryOverrideModel extends RuleOverrideBaseModel {

  private final String categoryId;

  public InspectCodeCategoryOverrideModel(@NotNull String categoryId) {
    this.categoryId = categoryId;
  }

  public String getCategoryId() {
    return this.categoryId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other == null || getClass() != other.getClass()) {
      return false;
    }

    return categoryId.equals(((InspectCodeCategoryOverrideModel) other).categoryId);
  }

  @Override
  public int hashCode() {
    return categoryId.hashCode();
  }

  @Override
  public String toString() {
    return "SonarQubeRuleDefinitionOverrideModel{" +
            "categoryId='" + categoryId + '\'' +
            ", sonarQubeRuleType=" + this.getSonarQubeRuleType() +
            ", sonarQubeSeverity=" + this.getSonarQubeSeverity() +
            '}';
  }
}
