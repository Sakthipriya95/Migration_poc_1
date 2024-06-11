/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cdr;

import com.bosch.caltool.datamodel.core.IDataObject;


/**
 * @author rgo7cob
 */
public interface IParameterAttribute extends IDataObject {


  /**
   * @return the attr id
   */
  Long getAttrId();

  /**
   * @return the param id
   */
  Long getParamId();

  /**
   * set the attribute id
   */
  void setAttrId(Long attrId);

  /**
   * set the parameter id
   */
  void setParamId(Long paramId);


}
