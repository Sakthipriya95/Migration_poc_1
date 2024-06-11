/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;

/**
 * Abstract class for project.
 *
 * @author bne4cob
 * @param <A> type of IPIDCAttribute
 */
@Deprecated
public abstract class AbstractProjectObject<A extends IPIDCAttribute> extends ApicObject {

  /**
   * if true, children are loaded
   */
  protected boolean childrenLoaded;

  /**
   * @param apicDataProvider ApicDataProvider
   * @param objID primary Key
   */
  protected AbstractProjectObject(final ApicDataProvider apicDataProvider, final Long objID) {
    super(apicDataProvider, objID);
  }

  /**
   * @return true, if children are loaded
   */
  public final boolean isChildrenLoaded() {
    return this.childrenLoaded;
  }

  /**
   * Provides Map of all attributes of this object, including invisible attributes
   *
   * @return all atributes as a map. <br>
   *         Key - Attribute ID<br>
   *         Value - project attribute implementation
   */
  // ICDM-2299
  public abstract Map<Long, A> getAttributesAll();

  /**
   * Provides all relevant attributes of this object.
   * <p>
   * Note : The attribute collection is reloaded during invocation
   *
   * @return all atributes as a map. <br>
   *         Key - Attribute ID<br>
   *         Value - project attribute implementation
   */
  // ICDM-2299
  public abstract Map<Long, A> getAttributes();

  /**
   * Get all attributes of this object
   * <p>
   * The return map includes : <br>
   * a) all new attributes(if applicable)<br>
   * b) all attributes not defined yet<br>
   * b) all defined attributes<br>
   * c) attributes at this level, when attributes are definded at child level<br>
   * <p>
   * The return does NOT include : <br>
   * a) invisible attributes.<br>
   * b) deleted attributes<br>
   * c) name attributes for PIDC, Variant, Sub-Variant
   *
   * @param refresh if <code>true</code> loads freshly, refresh false takes existing map
   * @return map of all atrributes <br>
   *         Key - attribute ID <br>
   *         Value - project attribute implementation
   */
  // ICDM-2299
  public abstract Map<Long, A> getAttributes(final boolean refresh);

  /**
   * Provides all attributes with used flag 'YES'
   *
   * @return map of atrributes <br>
   *         Key - attribute ID <br>
   *         Value - project attribute implementation
   */
  // ICDM-2299
  public abstract Map<Long, A> getAttributesUsed();

  /**
   * Provides all attributes with used flag 'NO'
   *
   * @return map of atrributes <br>
   *         Key - attribute ID <br>
   *         Value - project attribute implementation
   */
  // ICDM-2299
  public abstract Map<Long, A> getAttributesNotUsed();

  /**
   * Provides all attributes with used flag 'NOT DEFINED'
   *
   * @return map of atrributes <br>
   *         Key - attribute ID <br>
   *         Value - project attribute implementation
   */
  // ICDM-2299
  public abstract Map<Long, A> getAttributesNotDefined();

  /**
   * @return pidc version
   */
  public abstract PIDCVersion getPidcVersion();


  /**
   * Resets the childrenLoaded flag
   */
  public void resetChildrenLoaded() {
    this.childrenLoaded = false;
  }

  /**
   * @return returns if the attributes have uncleared or deleted values
   */
  public abstract boolean hasInvalidAttrValues();

  /**
   * @return returns if the mandatory attributes are defined
   */
  public abstract boolean isAllMandatoryAttrDefined();

  /**
   * @return project object statistics
   */
  public abstract ProjectObjectStatistics<?> getProjectStatistics();
}
