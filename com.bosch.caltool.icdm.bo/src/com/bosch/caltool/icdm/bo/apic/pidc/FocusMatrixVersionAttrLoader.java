package com.bosch.caltool.icdm.bo.apic.pidc;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersionAttr;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixVersionAttr;


/**
 * Loader class for Focus Matrix Version Attribute
 *
 * @author MKL2COB
 */
public class FocusMatrixVersionAttrLoader
    extends AbstractBusinessObject<FocusMatrixVersionAttr, TFocusMatrixVersionAttr> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public FocusMatrixVersionAttrLoader(final ServiceData serviceData) {
    super(serviceData, "Focus Matrix Version Attribute", TFocusMatrixVersionAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FocusMatrixVersionAttr createDataObject(final TFocusMatrixVersionAttr entity) throws DataException {
    FocusMatrixVersionAttr object = new FocusMatrixVersionAttr();

    setCommonFields(object, entity);

    object.setFmVersId(entity.getTFocusMatrixVersion().getFmVersId());
    object.setAttrId(entity.getTabvAttribute().getAttrId());
    if (null != entity.getTabvProjectVariant()) {
      object.setVariantId(entity.getTabvProjectVariant().getVariantId());
    }
    if (null != entity.getTabvProjectSubVariant()) {
      object.setSubVariantId(entity.getTabvProjectSubVariant().getSubVariantId());
    }
    object.setUsed(entity.getUsed());
    if (null != entity.getTabvAttrValue()) {
      object.setValueId(entity.getTabvAttrValue().getValueId());
    }
    if (null != entity.getFmAttrRemark()) {
      object.setFmAttrRemarks(entity.getFmAttrRemark());
    }

    return object;
  }


}
