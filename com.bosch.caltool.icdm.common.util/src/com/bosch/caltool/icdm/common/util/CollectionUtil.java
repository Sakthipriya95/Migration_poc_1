/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.util;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author bne4cob
 */
public final class CollectionUtil {

  /**
   * Private constructor for util class
   */
  private CollectionUtil() {
    // Private constructor for util class
  }

  /**
   * Convert a collection to a map
   *
   * @param input input collection
   * @param keyMapper mapping function to identify key from the element
   * @return Map, or null if <code>input</code> is null
   * @throws IllegalArgumentException when keyMapper is null
   */
  public static <K, E> Map<K, E> toMap(final Collection<E> input, final Function<E, K> keyMapper) {
    if (null == input) {
      return null;
    }
    if (keyMapper == null) {
      throw new IllegalArgumentException("Key mapper cannot be null");
    }
    return input.stream().collect(Collectors.toMap(keyMapper, o -> o, (o1, o2) -> o1));
  }
}
