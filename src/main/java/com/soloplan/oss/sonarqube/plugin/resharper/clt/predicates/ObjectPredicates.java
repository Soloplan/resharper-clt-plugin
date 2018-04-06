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

package com.soloplan.oss.sonarqube.plugin.resharper.clt.predicates;

import java.util.function.Predicate;

/**
 * A factory class that produces {@link Predicate}s for instances generic types that derive from {@link java.lang.Object}.
 */
public final class ObjectPredicates {

  /**
   * Creates a new generic {@link Predicate} to filter out {@code null} values.
   *
   * @param <T>
   *     Defines the type of the instance to check for {@code null}. Ensures type safety.
   *
   * @return A new generic {@link Predicate} instance that will filter out {@code null} values.
   */
  public static <T> Predicate<T> isNotNullPredicate() {
    return java.util.Objects::nonNull;
  }
}
