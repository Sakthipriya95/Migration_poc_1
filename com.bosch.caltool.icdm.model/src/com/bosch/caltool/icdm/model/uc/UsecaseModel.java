/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author dmo5cob
 */
public class UsecaseModel extends UsecaseEditorModel {


  private Map<Long, Set<Long>> childSectionsMap = new HashMap<>();

  private Map<Long, Set<Long>> ucItemAttrMap = new HashMap<>();

  private Map<Long, Set<Long>> ucItemAttrMapIncDel = new HashMap<>();


  /**
   * @return the childSectionsMap
   */
  @Override
  public Map<Long, Set<Long>> getChildSectionsMap() {
    return this.childSectionsMap;
  }


  /**
   * @return the ucItemAttrMap
   */
  public Map<Long, Set<Long>> getUcItemAttrMap() {
    return this.ucItemAttrMap;
  }


  /**
   * @param childSectionsMap the childSectionsMap to set
   */
  public void setChildSectionsMap(final Map<Long, Set<Long>> childSectionsMap) {
    this.childSectionsMap = childSectionsMap;
  }


  /**
   * @param ucItemAttrMap the ucItemAttrMap to set
   */
  public void setUcItemAttrMap(final Map<Long, Set<Long>> ucItemAttrMap) {
    this.ucItemAttrMap = ucItemAttrMap;
  }


  /**
   * @return the ucItemAttrMapIncDel
   */
  public Map<Long, Set<Long>> getUcItemAttrMapIncDel() {
    return this.ucItemAttrMapIncDel;
  }


  /**
   * @param ucItemAttrMapIncDel the ucItemAttrMapIncDel to set
   */
  public void setUcItemAttrMapIncDel(final Map<Long, Set<Long>> ucItemAttrMapIncDel) {
    this.ucItemAttrMapIncDel = ucItemAttrMapIncDel;
  }


}
