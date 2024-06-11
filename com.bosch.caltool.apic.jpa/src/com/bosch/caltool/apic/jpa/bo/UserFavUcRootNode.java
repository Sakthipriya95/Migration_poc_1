/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import com.bosch.caltool.dmframework.bo.IBasicObject;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * this class represents the user favorite usecase root node
 * 
 * @author mkl2cob
 */
public class UserFavUcRootNode implements IBasicObject {

  /**
   * @return rootname
   */
  @Override
  public String getName() {
    return ApicConstants.USER_USE_CASE_ROOT_NODE_NAME;
  }


  /**
   * @return description
   */
  @Override
  public String getDescription() {
    return ApicConstants.USER_USECASE_ROOT_DESC;
  }

  /**
   * @return tooltip of this node
   */
  @Override
  public String getToolTip() {
    return getDescription();
  }

}
