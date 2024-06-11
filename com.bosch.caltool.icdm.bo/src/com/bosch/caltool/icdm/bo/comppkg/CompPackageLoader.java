/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.comppkg;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.cdr.ReviewRuleAdapter;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.comppkg.TCompPkg;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParamAttr;
import com.bosch.caltool.icdm.model.comppkg.CompPkgParameter;
import com.bosch.caltool.icdm.model.comppkg.CompPkgRuleResponse;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author say8cob
 */
public class CompPackageLoader extends AbstractBusinessObject<CompPackage, TCompPkg> {

  /**
   * @param inputData ServiceData
   */
  public CompPackageLoader(final ServiceData inputData) {
    super(inputData, MODEL_TYPE.COMP_PKG, TCompPkg.class);
  }

  /**
   * @return Set of FC2WPDef
   */
  public SortedSet<CompPackage> getAllCompPkg() {
    SortedSet<CompPackage> compSet = new TreeSet<>();

    TypedQuery<TCompPkg> tQuery = getEntMgr().createNamedQuery("TCompPkg.findAll", TCompPkg.class);

    for (TCompPkg compPkg : tQuery.getResultList()) {
      compSet.add(createDataObject(compPkg));
    }

    return compSet;
  }

  /**
   * @param compPackage compPackage
   * @return ReviewRuleMap
   * @throws DataException Exception
   */
  public Map<String, List<ReviewRule>> getBySSDNodeId(final CompPackage compPackage) throws IcdmException {
    SSDServiceHandler serviceHandler = new SSDServiceHandler(getServiceData());
    Map<String, List<CDRRule>> cdrRuleMap = new CompPkgRuleManager(serviceHandler, compPackage,getServiceData()).readAllRules();

    return new ReviewRuleAdapter(getServiceData()).fetchReviewRuleForCdr(cdrRuleMap);
  }

  /**
   * @param paramMap paramMap
   * @param compPkgId compPkgId
   * @return Set<CompPkgParameter>
   * @throws DataException Exception
   */
  public Map<String, CompPkgParameter> getCompPkgParamMap(final Map<String, Parameter> paramMap, final Long compPkgId)
      throws DataException {
    Map<String, CompPkgParameter> compPkgParamMap = new HashMap<>();
    for (Map.Entry<String, Parameter> entry : paramMap.entrySet()) {
      CompPkgParameter compPkgParameter = new CompPkgParameter();
      setCompPkgParameter(compPkgId, entry.getValue(), compPkgParameter);
      compPkgParamMap.put(entry.getKey(), compPkgParameter);
    }

    return compPkgParamMap;
  }

  /**
   * @param compPkgId
   * @param entry
   * @param compPkgParameter
   * @throws DataException
   */
  private void setCompPkgParameter(final Long compPkgId, final Parameter parameter,
      final CompPkgParameter compPkgParameter)
      throws DataException {

    compPkgParameter.setId(parameter.getId());
    compPkgParameter.setName(parameter.getName());
    compPkgParameter.setDescription(parameter.getDescription());
    compPkgParameter.setVersion(parameter.getVersion());
    compPkgParameter.setSsdClass(parameter.getSsdClass());
    compPkgParameter.setCodeWord(parameter.getCodeWord());
    compPkgParameter.setLongName(parameter.getLongName());
    compPkgParameter.setLongNameGer(parameter.getLongNameGer());
    compPkgParameter.setParamHint(parameter.getParamHint());
    compPkgParameter.setType(parameter.getType());
    compPkgParameter.setCreatedDate(parameter.getCreatedDate());
    compPkgParameter.setCreatedUser(parameter.getCreatedUser());
    compPkgParameter.setModifiedDate(parameter.getModifiedDate());
    compPkgParameter.setModifiedUser(parameter.getModifiedUser());
    compPkgParameter.setpClassText(parameter.getpClassText());
    compPkgParameter.setCustPrm(parameter.getCustPrm());
    compPkgParameter.setQssdFlag(parameter.isQssdFlag());
    compPkgParameter.setCompPackage(getDataObjectByID(compPkgId));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected CompPackage createDataObject(final TCompPkg tCompPkg) {
    CompPackage data = new CompPackage();

    setCommonFields(data, tCompPkg);
    // loading the model
    data.setName(tCompPkg.getCompPkgName());

    data.setDescEng(tCompPkg.getDescEng());
    data.setDescGer(tCompPkg.getDescGer());
    data.setDescription(getLangSpecTxt(tCompPkg.getDescEng(), tCompPkg.getDescGer()));

    data.setCompPkgType(tCompPkg.getCompPkgType());
    data.setDeleted(yOrNToBoolean(tCompPkg.getDeletedFlag()));

    data.setSsdNodeId(tCompPkg.getSsdNodeId());
    data.setSsdParamClass(tCompPkg.getSsdParamClass());
    data.setSsdUsecase(tCompPkg.getSsdUsecase());
    data.setSsdVersNodeId(tCompPkg.getSsdVersNodeId());

    return data;
  }

  /**
   * @param compPkgId compPkgId
   * @return CompPkgRuleResponse
   * @throws DataException Exception
   */
  public CompPkgRuleResponse getCompPkgRule(final Long compPkgId) throws IcdmException {

    Map<String, List<ReviewRule>> reviewRuleMap = getBySSDNodeId(getDataObjectByID(compPkgId));

    ParameterLoader parameterLoader = new ParameterLoader(getServiceData());
    Map<String, CompPkgParameter> compPkgParamMap =
        getCompPkgParamMap(parameterLoader.getCompPkgParamsByName(reviewRuleMap.keySet()), compPkgId);

    CompPkgParamAttrLoader compPkgParamAttrLoader = new CompPkgParamAttrLoader(getServiceData());
    Map<String, List<CompPkgParamAttr>> paramAttrMap =
        compPkgParamAttrLoader.getParamAttrMap(compPkgParamMap, compPkgId);

    CompPkgRuleResponse compPkgRuleResponse = new CompPkgRuleResponse();
    compPkgRuleResponse.setAttrMap(paramAttrMap);
    compPkgRuleResponse.setRulesMap(reviewRuleMap);
    compPkgRuleResponse.setParamMap(compPkgParamMap);
    compPkgRuleResponse.setAttrValModelMap(compPkgParamAttrLoader.getCompPkgAttrObjMap(paramAttrMap));

    return compPkgRuleResponse;
  }
}
