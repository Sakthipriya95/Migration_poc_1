/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.rules.bo;

import java.util.Map;
import java.util.SortedSet;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;


/**
 * Interface for Parameter Attribute
 *
 * @author adn1cob
 */
public interface IParameterAttribute {

  /**
   * @return AbstractParameterAttribute
   */
  Attribute getAttribute();

  /**
   * @return IParameter
   * @throws SsdInterfaceException
   */
  IParameter<?> getParameter() throws SsdInterfaceException;

  /**
   * @return Name
   */
  String getName();

  /**
   * @return Description
   */
  String getDescription();

  /**
   * @return AttributeValue
   */
  SortedSet<AttributeValue> getAttributeValues();

  /**
   * @return true if the Param Attr has Attr Values
   */
  boolean hasAttrValues();

  /**
   * @return object details
   */
  Map<String, String> getObjectDetails();

  /**
   * @param paramAttr
   * @return int
   */
  int compareTo(final IParameterAttribute paramAttr);

  /**
   * @param arg0 other
   * @param sortColumn sort column
   * @return compare result based on sort column
   */
  int compareTo(final IParameterAttribute arg0, final int sortColumn);

  /**
   * @return primary key
   */
  Long getID();


}