package com.bosch.caltool.icdm.bo.cdr.cdfx;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.cdfx.TCDFxDelivery;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.cdfx.CDFxDelivery;


/**
 * Loader class for CDFxDelivery
 *
 * @author pdh2cob
 */
public class CDFxDeliveryLoader extends AbstractBusinessObject<CDFxDelivery, TCDFxDelivery> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CDFxDeliveryLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDFX_DELIVERY, TCDFxDelivery.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CDFxDelivery createDataObject(final TCDFxDelivery entity) throws DataException {
    CDFxDelivery object = new CDFxDelivery();

    setCommonFields(object, entity);

    object.setPidcA2lId(entity.getPidcA2l().getPidcA2lId());
    object.setVariantId(entity.getVariant() == null ? null : entity.getVariant().getVariantId());
    object.setWpDefnVersId(entity.getWpDefnVersion().getWpDefnVersId());
    object.setScope(entity.getScope());
    object.setReadinessYn(entity.getReadinessYn());

    return object;
  }

}
