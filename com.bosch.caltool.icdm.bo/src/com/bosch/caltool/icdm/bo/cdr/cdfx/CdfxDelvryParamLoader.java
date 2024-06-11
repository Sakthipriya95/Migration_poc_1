package com.bosch.caltool.icdm.bo.cdr.cdfx;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDeliveryParam;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxDelvryParam;


/**
 * Loader class for CDFx Delivery Parameter
 *
 * @author pdh2cob
 */
public class CdfxDelvryParamLoader extends AbstractBusinessObject<CdfxDelvryParam, TCDFxDeliveryParam> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CdfxDelvryParamLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDFX_DELVRY_PARAM, TCDFxDeliveryParam.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CdfxDelvryParam createDataObject(final TCDFxDeliveryParam entity) throws DataException {
    CdfxDelvryParam object = new CdfxDelvryParam();

    setCommonFields(object, entity);

    object.setCdfxDelvryParamId(entity.getCdfxDelParamId());
    object.setCdfxDelvryWpRespId(entity.gettCDFxDelvryWpResp().getCdfxDelWpRespId());
    object.setParamId(entity.getParam().getId());
    object.setRvwResultId(entity.getRvwResult().getResultId());

    return object;
  }


}
