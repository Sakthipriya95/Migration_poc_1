package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaWpResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;


/**
 * Loader class for DaWpResp
 *
 * @author say8cob
 */
public class DaWpRespLoader extends AbstractBusinessObject<DaWpResp, TDaWpResp> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public DaWpRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.DA_WP_RESP, TDaWpResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DaWpResp createDataObject(final TDaWpResp entity) throws DataException {
    DaWpResp object = new DaWpResp();

    setCommonFields(object, entity);

    object.setDataAssessmentId(entity.getTDaDataAssessment().getDataAssessmentId());
    object.setA2lWpId(entity.getA2lWpId());
    object.setA2lWpName(entity.getA2lWpName());
    object.setA2lRespId(entity.getA2lRespId());
    object.setA2lRespAliasName(entity.getA2lRespAliasName());
    object.setA2lRespName(entity.getA2lRespName());
    object.setA2lRespType(entity.getA2lRespType());
    object.setWpReadyForProductionFlag(entity.getWpReadyForProductionFlag());
    object.setWpFinishedFlag(entity.getWpFinishedFlag());
    object.setQnairesAnsweredFlag(entity.getQnairesAnsweredFlag());
    object.setParameterReviewedFlag(entity.getParameterReviewedFlag());
    object.setHexRvwEqualFlag(entity.getHexRvwEqualFlag());

    return object;
  }

}
