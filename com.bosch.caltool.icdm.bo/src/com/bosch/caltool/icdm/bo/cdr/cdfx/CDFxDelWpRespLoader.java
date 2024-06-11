package com.bosch.caltool.icdm.bo.cdr.cdfx;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelvryWpResp;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelWpResp;


/**
 * Loader class for CDFxDelWpResp
 *
 * @author pdh2cob
 */
public class CDFxDelWpRespLoader extends AbstractBusinessObject<CDFxDelWpResp, TCDFxDelvryWpResp> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CDFxDelWpRespLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDFX_DEL_WP_RESP, TCDFxDelvryWpResp.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDFxDelWpResp createDataObject(final TCDFxDelvryWpResp entity) throws DataException {
    CDFxDelWpResp object = new CDFxDelWpResp();

    setCommonFields(object, entity);

    object.setCdfxDelWpRespId(entity.getCdfxDelWpRespId());
    object.setCdfxDeliveryId(entity.getTCdfxDelivery().getCdfxDeliveryId());
    object.setWpId(entity.getWp().getA2lWpId());
    object.setRespId(entity.getResp().getA2lRespId());

    return object;
  }

}
