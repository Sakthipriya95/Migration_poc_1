/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.statistics.adapter;

import com.bosch.caltool.apic.ws.AttributeWithValueType;
import com.bosch.caltool.apic.ws.ProjectIdCardVariantType;


/**
 * A statistical representation of the attributes on different levels like PIDC, Variant and Sub-Variant
 *
 * @author imi2si
 */
public abstract class AbstractIcdmLevelStatistics {

  /**
   * number of attributes on this level
   */
  protected int levelElements; // NOPMD

  /**
   * number of variants on this level
   */
  protected int noOfVariants; // NOPMD

  /**
   * the number of attributes marked with Usage: ???
   */
  protected int unknownUsedAttr; // NOPMD

  /**
   * the number of attributes marked with Usage: No
   */
  protected int notUsedAttr; // NOPMD

  /**
   * the number of attributes marked with Usage: Yes and have a value
   */
  protected int usedAttrWithValues; // NOPMD

  /**
   * the number of attributes marked with Usage: Yes and have no value
   */
  protected int usedAttrWoValues; // NOPMD

  /**
   * the number of attributes marked with Usage: lower level
   */
  protected int noAttrLowerVariant; // NOPMD

  /**
   * the number of mandatory attributes with value
   */
  protected int noAttrMandWithVal; // NOPMD

  /**
   * the number of mandatory attributes with no value
   */
  protected int noAttrMandWoVal; // NOPMD

  /**
   * the maximal number of mandatory attributes to maintain in this PIDC
   */
  protected int noAttrMandMax; // NOPMD

  /**
   * When adding a variant either the noOfVariants member is increased. Also, this method can call the addAttributes
   * method with the attributes belonging to the variant
   *
   * @param variants the variants to be added
   */
  public abstract void addVariants(final ProjectIdCardVariantType[] variants);

  /**
   * Adds attributes to the object. Each attribute is analyzed if it fits in one of the categories
   * <p>
   * <ul>
   * <li>Unknown Usage</li>
   * <li>Not Used</li>
   * <li>Used with Value</li>
   * <li>Used without Value</li>
   * <li>Number of attributes on next lower leverl</li>
   * </ul>
   * The overall number of elements is increased by 1 for each attribute.
   *
   * @param attributes the attributes to be added
   */
  public abstract void addAttributes(final AttributeWithValueType[] attributes);

  /**
   * Returns the overall number of elements on this level.
   *
   * @return the number of elements on this level
   */
  public final int getLevelElements() {
    return this.levelElements;
  }

  /**
   * Returns the number of attributes for which Usage is set to '???' (unknown)
   *
   * @return the number of attributes marked with Usage: ???
   */
  public final int getUnknownUsedAttr() {
    return this.unknownUsedAttr;
  }

  /**
   * Returns the number of attributes for which Usage is set to 'No'
   *
   * @return the number of attributes marked with Usage: No
   */
  public final int getNotUsedAttr() {
    return this.notUsedAttr;
  }

  /**
   * Returns the number of attributes for which Usage is set to 'Yes' and a value is entered. Attributes that are set to
   * Yes but are maintained on a lower level are not included
   *
   * @return the number of attributes marked with Usage: Yes and have a value
   */
  public final int getUsedAttrWithValues() {
    return this.usedAttrWithValues;
  }

  /**
   * Returns the number of attributes for which Usage is set to 'Yes' and no value is entered.
   *
   * @return the number of attributes marked with Usage: Yes and have no value
   */
  public final int getUsedAttrWoValues() {
    return this.usedAttrWoValues;
  }

  /**
   * Returns the number of attributes for which Usage is set to 'Lower Level', that means attributes whose values are
   * entered on a lower level.
   *
   * @return the number of attributes marked with Usage: lower level
   */
  public final int getNoAttrLowerVariant() {
    return this.noAttrLowerVariant;
  }

  /**
   * Returns the number of mandatory attributes that have a value
   *
   * @return the number of mandatory attributes that have a value
   */
  public final int getNoAttrMandWithVal() {
    return this.noAttrMandWithVal;
  }

  /**
   * Returns the number of mandatory attributes that have no value
   *
   * @return the number of mandatory attributes that have no value
   */
  public final int getNoAttrMandWoVal() {
    return this.noAttrMandWoVal;
  }

  /**
   * Returns the max number of mandatory attributes possible to maintain for this PIDC
   *
   * @return the max number of mandatory attributes
   */
  public final int getNoAttrMandMax() {
    return this.noAttrMandMax;
  }

  /**
   * Returns the number of variants of this level
   *
   * @return the number of variants of this level
   */
  public final int getNoOfVariants() {
    return this.noOfVariants;
  }

  /**
   * Returns the percentage of filled mandatory attributes for this level
   *
   * @return the percentage of filled attributes
   */
  public final double getPercMandAttr() {
    double withValue = getNoAttrMandWithVal();
    double numberOfMandatoryAttr = getNoAttrMandMax();

    return withValue / numberOfMandatoryAttr;
  }
}
