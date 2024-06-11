/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwAttrValue;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.cdr.RvwAttrValue;


/**
 * Loader class for Review Attribute Value
 *
 * @author bru2cob
 */
public class RvwAttrValueLoader extends AbstractBusinessObject<RvwAttrValue, TRvwAttrValue> {


  private PidcVersionAttributeModel pidcAttrModel;

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public RvwAttrValueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.CDR_RES_ATTR_VALUE, TRvwAttrValue.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RvwAttrValue createDataObject(final TRvwAttrValue entity) throws DataException {
    RvwAttrValue object = new RvwAttrValue();

    setCommonFields(object, entity);

    object.setResultId(entity.getTRvwResult() == null ? null : entity.getTRvwResult().getResultId());
    object.setAttrId(entity.getTabvAttribute() == null ? null : entity.getTabvAttribute().getAttrId());
    object.setValueId(entity.getTabvAttrValue() == null ? null : entity.getTabvAttrValue().getValueId());

    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    Attribute attr = attrLoader.getDataObjectByID(entity.getTabvAttribute().getAttrId());
    object.setName(attr.getName());
    object.setUsed(entity.getUsed());

    ProjectAttributeLoader pidcAttrLoader = new ProjectAttributeLoader(getServiceData());
    try {
      this.pidcAttrModel = pidcAttrLoader.createModel(
          entity.getTRvwResult().getTPidcA2l().getTPidcVersion().getPidcVersId(), LOAD_LEVEL.L1_PROJ_ATTRS);
    }
    catch (IcdmException exp) {
      throw new DataException(exp.getMessage(), exp);
    }

    String displayValue = "";
    if (CommonUtils.isNull(entity.getTabvAttrValue())) {
      displayValue =
          CommonUtils.isEqual(entity.getUsed(), ApicConstants.YES) ? ApicConstants.USED : ApicConstants.NOT_USED;
    }
    else {
      if (!attr.isDeleted()) {
        AttributeValueLoader attrValLoader = new AttributeValueLoader(getServiceData());
        PidcVersionAttribute pidcAttr = this.pidcAttrModel.getPidcVersAttr(attr.getId());
        PidcVersionLoader versionLoader = new PidcVersionLoader(getServiceData());
        if (pidcAttr != null) {
          if ((versionLoader
              .isHiddenToCurrentUser(entity.getTRvwResult().getTPidcA2l().getTPidcVersion().getPidcVersId())) ||
              ApicConstants.HIDDEN_VALUE.equals(pidcAttr.getValue())) {
            displayValue = ApicConstants.HIDDEN_VALUE;
          }
          else {
            displayValue = attrValLoader.getDataObjectByID(entity.getTabvAttrValue().getValueId()).getName();
          }
        }
        else {
          displayValue = attrValLoader.getDataObjectByID(entity.getTabvAttrValue().getValueId()).getName();
        }
      }
      else {
        return null;
      }
    }
    object.setValueDisplay(displayValue);
    return object;
  }


  /**
   * Get Review Attribute Value records using ResultId
   *
   * @param entityObject review result
   * @return Map. Key - id, Value - RvwAttrValue object
   * @throws IcdmException
   */
  public Map<Long, RvwAttrValue> getByResultObj(final TRvwResult entityObject) throws IcdmException {
    Map<Long, RvwAttrValue> objMap = new ConcurrentHashMap<>();
    Set<TRvwAttrValue> dbObj = entityObject.getTRvwAttrValue();
    if (null != dbObj) {

      // LOAD_LEVEL.L1_PROJ_ATTRS
      for (TRvwAttrValue entity : dbObj) {
        RvwAttrValue createDataObject = createDataObject(entity);
        if (createDataObject != null) {
          objMap.put(entity.getRvwAttrvalId(), createDataObject);
        }
      }
    }
    return objMap;
  }

}
