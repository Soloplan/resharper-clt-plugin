package com.soloplan.oss.sonarqube.plugin.resharper.clt;

import com.soloplan.oss.sonarqube.plugin.resharper.clt.properties.*;
import com.soloplan.oss.sonarqube.plugin.resharper.clt.rules.CSharpRulesDefinition;
import org.sonar.api.Plugin;

/**
 * This class is the main entry point of the SonarQube plugin and responsible for registering all extensions. It is referenced in the {@code
 * pom.xml} file and will be instantiated by the SonarQube instance.
 */
public class ReSharperCltPlugin
    implements Plugin {

  @Override
  public void define(Context context) {
    // Register languages supported by this plugin
    // context.addExtension(CSharpLanguage.class); // NOTE: Not yet finished.

    // Register rules defined by this plugin
    context.addExtension(CSharpRulesDefinition.class);

    // Register plugin properties
    context.addExtensions(
        new CSharpLanguageProperty().buildPropertyDefinition(),
        new CSharpReportPathProperty().buildPropertyDefinition(),
        new VisualBasicNetReportPathProperty().buildPropertyDefinition(),
        new SolutionFileProperty().buildPropertyDefinition(),
        new ProjectNameProperty().buildPropertyDefinition(),
        new InspectCodePathProperty().buildPropertyDefinition(),
        new InspectCodeVersionProperty().buildPropertyDefinition(),
        new InspectCodeXmlFileSchemaValidationProperty().buildPropertyDefinition()
    );
  }
}
