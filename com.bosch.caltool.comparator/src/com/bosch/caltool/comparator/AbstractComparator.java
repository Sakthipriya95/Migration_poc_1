/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.comparator;

import java.util.SortedSet;


/**
 * AbstractComparator.java
 *
 * @author adn1cob
 * @param <V> object to compare
 * @param <R> object as a result of comparison
 */
public abstract class AbstractComparator<V, R extends AbstractCompareResult> {

  /**
   * Holds the first object to compare - PIDCard or PIDCvariant or PIDCSubvariant or A2LObject
   */
  private final V object1;

  /**
   * Holds the second object to compare - PIDCard or PIDCvariant or PIDCSubvariant or A2LObject
   */
  private final V object2;

  /**
   * The result of comparison
   */
  private SortedSet<R> compareResult;

  /**
   * Constructor for comparison of objects
   *
   * @param object1 Object1 to compare
   * @param object2 Object2 to compare
   */
  public AbstractComparator(final V object1, final V object2) {
    this.object1 = object1;
    this.object2 = object2;
  }

  /**
   * Compare method
   */
  public abstract void compare();

  /**
   * @return result obj of comparison
   */
  public final SortedSet<R> getResult() {
    return this.compareResult;
  }

  /**
   * Retuns the first object which is compared
   *
   * @return Object
   */
  protected V getObject1() {
    return this.object1;
  }

  /**
   * Retuns the first object which is compared
   *
   * @return Object
   */
  protected V getObject2() {
    return this.object2;
  }

  /**
   * Set the result of comparison
   *
   * @param compareResult resultObject
   */
  protected void setResult(final SortedSet<R> compareResult) {
    this.compareResult = compareResult;
  }
}
