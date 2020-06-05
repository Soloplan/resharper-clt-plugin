package com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.models.InspectCodeCategoryOverrideModel;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface InspectCodeCategoryOverrideProvider {
  @NotNull
  public Collection<InspectCodeCategoryOverrideModel> getCategoryOverrides();
}
