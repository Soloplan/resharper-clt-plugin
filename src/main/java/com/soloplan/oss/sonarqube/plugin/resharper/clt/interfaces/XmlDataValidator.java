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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.interfaces;

import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/** Simple interface for XML data validation. */
public interface XmlDataValidator {

  /**
   * Validates the supplied XML data input stream.
   *
   * @param xmlDataInputStream
   *     The {@link InputStream} of the XML data to be validated.
   *
   * @return {@code True}, if the supplied XML data has been validated successfully, {@code false} otherwise.
   */
  boolean validateXmlData(@NotNull final InputStream xmlDataInputStream);
}
