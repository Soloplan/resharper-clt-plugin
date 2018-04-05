package com.soloplan.oss.sonarqube.plugin.resharper.clt.configuration;

public class ReSharperCltConfiguration {

  public static final String PROPERTY_KEY_PROJECT_NAME = "resharper.clt.projectName";
  public static final String PROPERTY_KEY_SOLUTION_FILE = "resharper.clt.solutionFile";
  public static final String PROPERTY_KEY_INSPECTCODE_PATH = "resharper.clt.inspectCode.path";
  public static final String PROPERTY_KEY_INSPECTCODE_VERSION = "resharper.clt.inspectCode.version";

  public static final String PROPERTY_KEY_CS_FILE_SUFFIXES = "sonar.csharp.file.suffixes";
  public static final String PROPERTY_KEY_CS_FILE_SUFFIXES_DEFAULT_VALUE = ".cs";

  public static final String PROPERTY_KEY_VBNET_FILE_SUFFIXES = "sonar.vbnet.file.suffixes";
  public static final String PROPERTY_KEY_VBNET_FILE_SUFFIXES_DEFAULT_VALUE = ".vb";

  public static final String PROPERTY_KEY_CS_REPORT_PATH = "resharper.clt.cs.reportPath";
  public static final String PROPERTY_KEY_VBNET_REPORT_PATH = "resharper.clt.vbnet.reportPath";

  /**
   * This property defines a {@code boolean} value, indicating whether any resulting XML file of the {@code InspectCode} command line tool
   * should be validated using the XML Schema Definition file included with this plugin.
   */
  public static final String PROPERTY_KEY_ENABLE_XML_SCHEMA_VALIDATION = "resharper.clt.xsd.validation";

  //region Definitions for supported rule repositories

  /**
   * Defines the unique identifier of the rule repository for the C# language.
   *
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.rules.CSharpRulesDefinition
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.CSharpLanguage
   */
  public static final String RULES_REPOSITORY_CSHARP_KEY = "resharper-clt-cs";

  /**
   * Defines the human-readable name of the rule repository for the C# language. This string will be displayed within the {@code Rules} tab
   * of SonarQube and have the string {@code C#} next to it.
   *
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.rules.CSharpRulesDefinition
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.CSharpLanguage
   */
  public static final String RULES_REPOSITORY_CSHARP_NAME = "InspectCode";

  /**
   * Defines the unique identifier of the rule repository for the Visual Basic .NET language.
   *
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.rules.VBNetRulesDefinition
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.VBNetLanguage
   */
  public static final String RULES_REPOSITORY_VBNET_KEY = "resharper-clt-vbnet";

  /**
   * Defines the human-readable name of the rule repository for the Visual Basic .NET language. This string will be displayed within the
   * {@code Rules} tab of SonarQube and have the string {@code VB.NET} next to it.
   *
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.rules.VBNetRulesDefinition
   * @see com.soloplan.oss.sonarqube.plugin.resharper.clt.languages.VBNetLanguage
   */
  public static final String RULES_REPOSITORY_VBNET_NAME = "InspectCode";

  //endregion

  //region Definition of categories and subcategories for plugin settings

  /**
   * This constant defines the default category to group each property affecting the behavior of the {@link
   * com.soloplan.oss.sonarqube.plugin.resharper.clt.ReSharperCltPlugin}.
   */
  public static final String PLUGIN_CONFIGURATION_PROPERTY_CATEGORY = "ReSharper CLT";

  /**
   * Defines the subcategory for all properties related to the C# language.
   */
  public static final String PLUGIN_CONFIGURATION_PROPERTY_CS_SUBCATEGORY = "C#";

  /**
   * Defines the subcategory for all properties related to Visual Basic .NET.
   */
  public static final String PLUGIN_CONFIGURATION_PROPERTY_VBNET_SUBCATEGORY = "Visual Basic .NET";

  /**
   * Defines the subcategory for deprecated properties that will be removed in future versions of the plugin.
   */
  public static final String PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_SUBCATEGORY = "Deprecated";

  /**
   * Defines an additional human-readable note that will be attached to the description of each deprecated property.
   */
  public static final String PLUGIN_CONFIGURATION_PROPERTY_DEPRECATED_DESCRIPTION =
      "This property is deprecated and will be removed in a future version.<br />"
          + "You should stop using it as soon as possible.<br />"
          + "Consult the migration guide for guidance.";

  //endregion
}
