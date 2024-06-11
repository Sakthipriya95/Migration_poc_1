/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.uc;

import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * this class represents the user favorite usecase root node
 *
 * @author dmo5cob
 */
public class UserFavUcRootNode {


  /**
   *
   */
  public UserFavUcRootNode() {
    // TODO Auto-generated constructor stub
  }

  /**
   * @return rootname
   */
  public String getName() {
    return ApicConstants.USER_USE_CASE_ROOT_NODE_NAME;
  }


  /**
   * @return description
   */
  public String getDescription() {
    return ApicConstants.USER_USECASE_ROOT_DESC;
  }

  /**
   * @return tooltip of this node
   */
  public String getToolTip() {
    return getDescription();
  }


}
