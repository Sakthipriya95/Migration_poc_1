package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaQnaireResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.DaQnaireResp;


/**
 * Loader class for DaQnaireResp
 *
 * @author say8cob
 */
public class DaQnaireRespLoader extends AbstractBusinessObject<DaQnaireResp, TDaQnaireResp> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public DaQnaireRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.DA_QNAIRE_RESP, TDaQnaireResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DaQnaireResp createDataObject(final TDaQnaireResp entity) throws DataException {
    DaQnaireResp object = new DaQnaireResp();

    setCommonFields(object, entity);

    object.setDaWpRespId(entity.getTDaWpResp().getDaWpRespId());
    object.setQnaireRespId(entity.getQnaireRespId());
    object.setQnaireRespName(entity.getQnaireRespName());
    object.setQnaireRespVersId(entity.getQnaireRespVersId());
    object.setQnaireRespVersionName(entity.getQnaireRespVersionName());
    object.setReadyForProductionFlag(entity.getReadyForProductionFlag());
    object.setBaselineExistingFlag(entity.getBaselineExistingFlag());
    object.setNumPositiveAnswers(entity.getNumPositiveAnswers());
    object.setNumNeutralAnswers(entity.getNumNeutralAnswers());
    object.setNumNegativeAnswers(entity.getNumNegativeAnswers());
    object.setReviewedDate(timestamp2String(entity.getReviewedDate()));
    object.setReviewedUser(entity.getReviewedUser());

    return object;
  }

}
