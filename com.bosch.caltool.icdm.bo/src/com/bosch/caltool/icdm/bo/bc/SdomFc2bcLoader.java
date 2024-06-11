package com.bosch.caltool.icdm.bo.bc;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TSdomFc2bc;
import com.bosch.caltool.icdm.model.bc.SdomFc2bc;


/**
 * Loader class for SdomFc2bc
 *
 * @author say8cob
 */
public class SdomFc2bcLoader extends AbstractBusinessObject<SdomFc2bc, TSdomFc2bc> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public SdomFc2bcLoader(final ServiceData serviceData) {
    super(serviceData, "Function Component To Base Component", TSdomFc2bc.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SdomFc2bc createDataObject(final TSdomFc2bc entity) throws DataException {
    SdomFc2bc object = new SdomFc2bc();

    object.setId(entity.getId());
    object.setBcId(entity.getBcId());
    object.setFcId(entity.getFcId());

    return object;
  }

}
