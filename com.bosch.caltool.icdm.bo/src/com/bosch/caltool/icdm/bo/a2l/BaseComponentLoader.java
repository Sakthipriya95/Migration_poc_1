/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TBaseComponent;
import com.bosch.caltool.icdm.model.a2l.BaseComponent;


/**
 * @author bne4cob
 */
public class BaseComponentLoader extends AbstractBusinessObject<BaseComponent, TBaseComponent> {

  /**
   * @param serviceData service Data
   */
  public BaseComponentLoader(final ServiceData serviceData) {
    super(serviceData, "Base Component", TBaseComponent.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected BaseComponent createDataObject(final TBaseComponent dbBc) throws DataException {
    BaseComponent bcomp = new BaseComponent();
    bcomp.setBcId(dbBc.getBcId());
    bcomp.setName(dbBc.getBcName());
    bcomp.setDescription(dbBc.getBcDesc());
    return bcomp;
  }

}
