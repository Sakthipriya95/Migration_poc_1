/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bne4cob
 * @param <P> project object
 * @param <A> project attribute
 */
public class ProjectObjectWithAttributes<P extends IProjectObject, A extends IProjectAttribute> {

  private P projectObject;

  private Map<Long, A> projectAttrMap = new HashMap<>();


  /**
   * @return the projectObject
   */
  public P getProjectObject() {
    return this.projectObject;
  }


  /**
   * @param projectObject the projectObject to set
   */
  public void setProjectObject(final P projectObject) {
    this.projectObject = projectObject;
  }


  /**
   * @return the projectAttrMap
   */
  public Map<Long, A> getProjectAttrMap() {
    return this.projectAttrMap;
  }


  /**
   * @param projectAttrMap the projectAttrMap to set
   */
  public void setProjectAttrMap(final Map<Long, A> projectAttrMap) {
    this.projectAttrMap = projectAttrMap;
  }

}
