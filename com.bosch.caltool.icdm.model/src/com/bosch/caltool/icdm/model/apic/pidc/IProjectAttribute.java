/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.apic.pidc;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author bne4cob
 */
public interface IProjectAttribute extends IDataObject, Comparable<IProjectAttribute> {

  /**
   * @return flag
   */
  String getUsedFlag();

  /**
   * @param flag
   */
  void setUsedFlag(String flag);

  /**
   * @return attrId
   */
  Long getAttrId();

  /**
   * @param attrId
   */
  void setAttrId(Long attrId);

  /**
   * @return valueId
   */
  Long getValueId();

  /**
   * @param valueId
   */
  void setValueId(Long valueId);

  /**
   * @return value
   */
  String getValue();

  /**
   * @param value
   */
  void setValue(String value);

  /**
   * @return the atChildLevel
   */
  boolean isAtChildLevel();

  /**
   * @param atChildLevel the atChildLevel to set
   */
  void setAtChildLevel(final boolean atChildLevel);

  /**
   * @return true if attribute is hidden
   */
  boolean isAttrHidden();

  /**
   * @param attrHidden
   */
  void setAttrHidden(boolean attrHidden);

  /**
   * @param specLink
   */
  void setSpecLink(final String specLink);

  /**
   * @return Spec Link
   */
  String getSpecLink();

  /**
   * @param partNumber
   */
  void setPartNumber(String partNumber);

  /**
   * @return Part Number
   */
  String getPartNumber();

  /**
   * @param additionalInfoDesc
   */
  void setAdditionalInfoDesc(String additionalInfoDesc);

  /**
   * @return Additional Info Desc
   */
  String getAdditionalInfoDesc();


  /**
   * @param projectAttr
   * @param sortColumn
   * @return comparison result
   */
  int compareTo(IProjectAttribute projectAttr, int sortColumn);


}
