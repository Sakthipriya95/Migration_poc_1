/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comppkg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.comppkg.TCpRuleAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;

/**
 * @author dmr1cob
 */
public class CompPkgParamAttrLoader extends AbstractBusinessObject<CompPkgParamAttr, TCpRuleAttr> {

  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public CompPkgParamAttrLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.COMP_PKG_PARAMETER_ATTR, TCpRuleAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompPkgParamAttr createDataObject(final TCpRuleAttr entity) throws DataException {
    CompPkgParamAttr object = new CompPkgParamAttr();
    setCommonFields(object, entity);
    object.setCpRuleAttrId(entity.getCpRuleAttrId());
    object.setCompPkgId(entity.getTCompPkg().getCompPkgId());
    object.setAttrId(entity.getTabvAttribute().getAttrId());
    object.setVersion(entity.getVersion());
    return object;
  }


  /**
   * @param compPkgParamMap compPkgParamMap
   * @param compPkgId compPkgId
   * @return Map<String, List<CompPkgParamAttr>>
   * @throws DataException Exception
   */
  public Map<String, List<CompPkgParamAttr>> getParamAttrMap(final Map<String, CompPkgParameter> compPkgParamMap,
      final Long compPkgId)
      throws DataException {
    Map<String, List<CompPkgParamAttr>> compPkgParamAttrMap = new HashMap<>();

    CompPackageLoader compPkgLoader = new CompPackageLoader(getServiceData());
    for (CompPkgParameter compPkgParam : compPkgParamMap.values()) {
      List<CompPkgParamAttr> compPkgParamAttrList = new ArrayList<>();
      List<TCpRuleAttr> tCpRuleAttrsList = compPkgLoader.getEntityObject(compPkgId).getTCpRuleAttrs();
      if (CommonUtils.isNotEmpty(tCpRuleAttrsList)) {
        for (TCpRuleAttr tCpRuleAttr : tCpRuleAttrsList) {
          compPkgParamAttrList.add(createDataObject(tCpRuleAttr));
        }
        compPkgParamAttrMap.put(compPkgParam.getName(), compPkgParamAttrList);
      }
    }
    return compPkgParamAttrMap;
  }

  /**
   * @param paramAttrMap
   * @return
   * @throws DataException
   */
  public Map<Long, Attribute> getCompPkgAttrObjMap(final Map<String, List<CompPkgParamAttr>> paramAttrMap)
      throws DataException {
    Map<Long, Attribute> attrObjMap = new HashMap<>();
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());
    for (List<CompPkgParamAttr> paramAttr : paramAttrMap.values()) {
      for (CompPkgParamAttr parameterAttribute : paramAttr) {
        if (attrObjMap.get(parameterAttribute.getAttrId()) == null) {
          attrObjMap.put(parameterAttribute.getAttrId(), attrLoader.getDataObjectByID(parameterAttribute.getAttrId()));
        }
      }
    }
    return attrObjMap;
  }
}
