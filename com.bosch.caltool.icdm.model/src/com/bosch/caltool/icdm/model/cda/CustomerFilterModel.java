/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.cda;

import java.util.ArrayList;
import java.util.List;

/**
 * @author pdh2cob
 */
public class CustomerFilterModel {

  private List<CustomerFilter> customerFilterList = new ArrayList<CustomerFilter>();

  private boolean inverseFlag;


  /**
   * @return the customerFilterList
   */
  public List<CustomerFilter> getCustomerFilterList() {
    return this.customerFilterList;
  }


  /**
   * @param customerFilterList the customerFilterList to set
   */
  public void setCustomerFilterList(final List<CustomerFilter> customerFilterList) {
    this.customerFilterList = customerFilterList;
  }


  /**
   * @return the inverseFlag
   */
  public boolean isInverseFlag() {
    return this.inverseFlag;
  }


  /**
   * @param inverseFlag the inverseFlag to set
   */
  public void setInverseFlag(final boolean inverseFlag) {
    this.inverseFlag = inverseFlag;
  }


}
