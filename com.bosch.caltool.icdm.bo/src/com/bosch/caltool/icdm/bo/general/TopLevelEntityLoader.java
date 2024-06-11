/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.general;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvTopLevelEntity;


/**
 * @author bne4cob
 */
class TopLevelEntityLoader extends AbstractBusinessObject<TopLevelEntityInternal, TabvTopLevelEntity> {

  /**
   * @param serviceData ServiceData
   */
  public TopLevelEntityLoader(final ServiceData serviceData) {
    super(serviceData, "Top Level Entity", TabvTopLevelEntity.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected TopLevelEntityInternal createDataObject(final TabvTopLevelEntity entity) throws DataException {
    TopLevelEntityInternal data = new TopLevelEntityInternal();
    data.setId(entity.getEntId());
    data.setEntityName(entity.getEntityName());
    data.setLastModDate(entity.getLastModDate());
    data.setVersion(entity.getVersion());
    return data;
  }

}
