/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Set;

import com.bosch.caltool.icdm.common.exception.SsdInterfaceException;


/**
 * Any implementation to which attributes can be mapped (e.g. CDR Parameter)
 *
 * @author bne4cob
 */
// ICDM-1366
public interface IAttributeMappedObject {

  /**
   * Retrieve the mapped attributes to this object
   *
   * @return the mapped attributes
   * @throws SsdInterfaceException
   */
  Set<Attribute> getAttributes() throws SsdInterfaceException;

  /**
   * Return name of the object
   *
   * @return the mapped attributes
   */
  String getName();
}
