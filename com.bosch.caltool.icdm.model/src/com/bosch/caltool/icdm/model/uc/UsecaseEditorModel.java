/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.uc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.Characteristic;

/**
 * @author mkl2cob
 */
public class UsecaseEditorModel {

  private final Set<Long> firstLevelUCSIDSet = new HashSet<>();

  private final Map<Long, UseCaseSection> ucSectionMap = new HashMap<>();

  private final Map<Long, UcpAttr> ucpAttr = new HashMap<>();

  private final Map<Long, Attribute> attrMap = new HashMap<>();

  private final Map<Long, Set<Long>> childSectionsMap = new HashMap<>();

  private final Map<Long, Map<Long, Long>> attrToUcpAttrMap = new HashMap<>();

  /**
   * key = Attribute id, value = referential attribute dependency set
   */
  private final Map<Long, Set<AttrNValueDependency>> attrRefDependenciesMap = new HashMap<>();
  /**
   * key = dep value id, value = AttributeValue object
   */
  private final Map<Long, AttributeValue> attrDepValMap = new HashMap<>();

  /**
   * Let of link ids
   */
  private final Set<Long> linkSet = new HashSet<>();

  /**
   * key - Characteristic Id, value - Characteristic object
   */
  private Map<Long, Characteristic> characteristicMap = new HashMap<>();

  /**
   * key - Wp Id, value - Use case section Ids
   */
  private final Map<String, Set<Long>> wpmlWpIdUcSectionIdMap = new HashMap<>();

  /**
   * key - Wp Id, value - Use case section Ids
   */
  private final Map<String, Set<Long>> wpmlMcrIdUcSectionIdMap = new HashMap<>();


  /**
   * @return the ucSectionMap
   */
  public Map<Long, UseCaseSection> getUcSectionMap() {
    return this.ucSectionMap;
  }

  /**
   * @return the ucpAttr
   */
  public Map<Long, UcpAttr> getUcpAttr() {
    return this.ucpAttr;
  }

  /**
   * @return the attrMap
   */
  public Map<Long, Attribute> getAttrMap() {
    return this.attrMap;
  }

  /**
   * @return the childSectionsMap
   */
  public Map<Long, Set<Long>> getChildSectionsMap() {
    return this.childSectionsMap;
  }

  /**
   * @return the firstLevelUCSIDSet
   */
  public Set<Long> getFirstLevelUCSIDSet() {
    return this.firstLevelUCSIDSet;
  }

  /**
   * @return the attrToUcpAttrMap
   */
  public Map<Long, Map<Long, Long>> getAttrToUcpAttrMap() {
    return this.attrToUcpAttrMap;
  }

  /**
   * @return the attrRefDependenciesMap
   */
  public Map<Long, Set<AttrNValueDependency>> getAttrRefDependenciesMap() {
    return this.attrRefDependenciesMap;
  }

  /**
   * @return the attrDepValMap
   */
  public Map<Long, AttributeValue> getAttrDepValMap() {
    return this.attrDepValMap;
  }


  /**
   * @return the linkSet
   */
  public Set<Long> getLinkSet() {
    return this.linkSet;
  }


  /**
   * @return the characteristicMap
   */
  public Map<Long, Characteristic> getCharacteristicMap() {
    return this.characteristicMap;
  }

  /**
   * @param characteristicMap the characteristicMap to set
   */
  public void setCharacteristicMap(final Map<Long, Characteristic> characteristicMap) {
    this.characteristicMap = characteristicMap;
  }


  /**
   * @return the wpmlWpIdUcSectionIdMap
   */
  public Map<String, Set<Long>> getWpmlWpIdUcSectionIdMap() {
    return this.wpmlWpIdUcSectionIdMap;
  }

  /**
   * @return the wpmlMcrIdUcSectionIdMap
   */
  public Map<String, Set<Long>> getWpmlMcrIdUcSectionIdMap() {
    return this.wpmlMcrIdUcSectionIdMap;
  }


}
