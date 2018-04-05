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
