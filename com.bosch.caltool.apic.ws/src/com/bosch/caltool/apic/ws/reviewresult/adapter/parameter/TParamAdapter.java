/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.reviewresult.adapter.parameter;

import com.bosch.caltool.apic.ws.GetParameterReviewResultResponseType;
import com.bosch.caltool.apic.ws.db.IWebServiceAdapter;
import com.bosch.caltool.icdm.database.entity.cdr.TParameter;


/**
 * @author imi2si
 */
public class TParamAdapter extends GetParameterReviewResultResponseType implements IWebServiceAdapter {

  private final TParameter parameter;

  public TParamAdapter(final TParameter parameter) {
    this.parameter = parameter;
  }

  public void adapt() {
    super.setParameterId(this.parameter.getId());
    super.setParameterName(this.parameter.getName());
    super.setParameterLongname(this.parameter.getLongname());
  }

}
