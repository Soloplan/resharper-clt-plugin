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

import org.jetbrains.annotations.Contract;

import java.util.Collection;

/**
 * An interface defining methods to convert from instances of type {@link I} to instances of type {@link O}.
 *
 * @param <I>
 *     The generic type parameter that defines the input of the implementation.
 * @param <O>
 *     The generic type parameter that defines the output of the implementation.
 */
public interface Converter<I, O> {

  /**
   * Converts a single instance of the generic type {@link I} to an instance of the generic type {@link O}. If argument {@code instance} is
   * {@code null}, {@code null} will be returned.
   *
   * @param instance
   *     An instance of the generic type {@link I}, which will be used to create a new instance of type {@link O}. Might be {@code null}.
   *
   * @return A new instance of type {@link O}, of which its properties are populated with values of the supplied {@code instance} of the
   *     generic type {@link I}. Might be {@code null} if the supplied {@code instance} is {@code null}.
   */
  @Contract("null -> null; !null -> !null")
  O convert(I instance);

  /**
   * Converts all instances contained within the supplied {@code collection} of generic type {@link I} to instances of the generic type
   * {@link O} and returns the {@link Collection} of converted instances.
   *
   * @param collection
   *     A {@link Collection} of the generic type {@link I}, of which each entry will be used to create a new instance of the generic type
   *     {@link O}. Might be {@code null} or an empty {@link Collection}.
   *
   * @return A new {@link Collection} of instances of the generic type {@link O}, of which each instance has been populated with values of
   *     the supplied {@code collection} of the generic type {@link I}. Should never be {@code null}, instead an empty {@link Collection}
   *     should be returned, even if the supplied {@code collection} is {@code null}.
   */
  @Contract("null -> !null; !null -> !null")
  Collection<O> convert(Collection<I> collection);
}
