package com.bosch.caltool.icdm.bo.apic.attr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValueType;
import com.bosch.caltool.icdm.model.apic.attr.AttrValueType;


/**
 * Loader class for AttrValueType
 *
 * @author dmo5cob
 */
public class AttrValueTypeLoader extends AbstractBusinessObject<AttrValueType, TabvAttrValueType> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public AttrValueTypeLoader(final ServiceData serviceData) {
    super(serviceData, "Attribute Value Type", TabvAttrValueType.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected AttrValueType createDataObject(final TabvAttrValueType entity) throws DataException {
    AttrValueType object = new AttrValueType();

    object.setId(entity.getValueTypeId());
    object.setValueType(entity.getValueType());

    return object;
  }


}
