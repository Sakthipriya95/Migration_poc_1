/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParam;
import com.bosch.caltool.icdm.database.entity.cdr.TRuleSetParamAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameter;
import com.bosch.caltool.icdm.model.cdr.RuleSetParameterAttr;

/**
 * @author rgo7cob
 */
public class RuleSetParameterAttrLoader extends AbstractBusinessObject<RuleSetParameterAttr, TRuleSetParamAttr> {


  /**
   * @param servData Service Data
   */
  public RuleSetParameterAttrLoader(final ServiceData servData) {
    super(servData, MODEL_TYPE.CDR_RULE_SET_PARAM_ATTR, TRuleSetParamAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RuleSetParameterAttr createDataObject(final TRuleSetParamAttr entity) throws DataException {
    RuleSetParameterAttr data = new RuleSetParameterAttr();


    data.setId(entity.getRsetParAttrId());

    AttributeLoader attrloader = new AttributeLoader(getServiceData());
    Attribute attrObj = attrloader.getDataObjectByID(entity.getTabvAttribute().getAttrId());

    data.setName(attrObj.getName());

    TRuleSetParam ruleSetParamEntity = entity.getTRuleSetParam();
    data.setParamId(ruleSetParamEntity.getTParameter().getId());

    data.setAttrId(attrObj.getId());

    data.setRuleSetParamId(ruleSetParamEntity.getRsetParamId());

    data.setRuleSetId(ruleSetParamEntity.getTRuleSet().getRsetId());
    data.setVersion(entity.getVersion());

    data.setDescription(attrObj.getDescription());


    setCommonFields(data, entity);

    return data;
  }


  /**
   * @param rulesetParamMap ruleSetParamMap
   * @return the rule set Params
   * @throws DataException DataException
   */
  public Map<String, List<RuleSetParameterAttr>> getParamAttrMap(final Map<String, RuleSetParameter> rulesetParamMap)
      throws DataException {

    getLogger().debug("Fetching param attributes for rule-set parameters... Input size  = {}", rulesetParamMap.size());

    Map<String, List<RuleSetParameterAttr>> ruleSetParamAttrMap = new HashMap<>();
    RuleSetParameterLoader paramLoader = new RuleSetParameterLoader(getServiceData());
    for (RuleSetParameter rsetParam : rulesetParamMap.values()) {
      TRuleSetParam rsetParamEntity = paramLoader.getEntityObject(rsetParam.getId());
      List<TRuleSetParamAttr> tRuleSetParamAttrs = rsetParamEntity.getTRuleSetParamAttrs();
      // do the addition only for params with attr dep
      if (CommonUtils.isNotEmpty(tRuleSetParamAttrs)) {
        List<RuleSetParameterAttr> paramAttrList = ruleSetParamAttrMap.get(rsetParam.getName());

        if (paramAttrList == null) {
          paramAttrList = new ArrayList<>();
        }

        for (TRuleSetParamAttr tparamAttrList : tRuleSetParamAttrs) {
          paramAttrList.add(createDataObject(tparamAttrList));
        }

        ruleSetParamAttrMap.put(rsetParam.getName(), paramAttrList);
      }
    }

    getLogger().debug("Rule-Set parameter attributes fetch finished");

    return ruleSetParamAttrMap;

  }


  /**
   * fetch the attr dependency to a parameter
   *
   * @param paramSet parameters
   * @return map which contains rule set paramter attr for a particular rule set parameter
   * @throws DataException
   */

  public Map<String, List<RuleSetParameterAttr>> fetchParameterAttrDepn(final Set<IParameter> paramSet)
      throws DataException {

    getLogger().debug("fetching attr dependencies for ruleset parameters...");

    EntityManager em = null;
    Map<Long, String> cdrFuncParamMap = new ConcurrentHashMap<>();
    if (paramSet != null) {
      for (IParameter cdrFuncParameter : paramSet) {
        cdrFuncParamMap.put(cdrFuncParameter.getId(), cdrFuncParameter.getName());
      }
    }
    List<TRuleSetParamAttr> resultList = null;
    if (!cdrFuncParamMap.isEmpty()) {
      if (cdrFuncParamMap.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT) {
        final Query qDbattrDepn =
            getEntMgr().createNamedQuery(TRuleSetParamAttr.NQ_GET_RS_PARAM_ATTR_BY_PARAMLIST, TRuleSetParamAttr.class);
        qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "500");
        qDbattrDepn.setHint(ApicConstants.READ_ONLY, "true");
        qDbattrDepn.setParameter("paramList", cdrFuncParamMap.keySet());

        resultList = qDbattrDepn.getResultList();


      } // For more than 1000 Values we need the temp table
      else {
        try {
          em = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
          em.getTransaction().begin();

          GttObjectName tempParam;
          // Delete the existing records in this temp table, if any
          final Query delQuery = em.createNamedQuery(GttObjectName.NS_DELETE_GTT_OBJ_NAMES);
          delQuery.executeUpdate();

          // Create entities for all the functions
          for (Entry<Long, String> cdrFuncParamMapEntry : cdrFuncParamMap.entrySet()) {
            tempParam = new GttObjectName();
            tempParam.setId(cdrFuncParamMapEntry.getKey());
            tempParam.setObjName(cdrFuncParamMapEntry.getValue());
            em.persist(tempParam);

          }
          em.flush();
          final TypedQuery<TRuleSetParamAttr> qDbattrDepn =
              em.createNamedQuery(TRuleSetParamAttr.NQ_GET_RS_PARAM_ATTR_BY_ID, TRuleSetParamAttr.class);
          qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "2000");

          resultList = qDbattrDepn.getResultList();

          delQuery.executeUpdate();
          em.getTransaction().commit();
        }
        finally {
          if ((em != null) && em.isOpen()) {
            em.close();
          }
        }
      }
    }
    return getParamDependency(resultList);
  }

  /**
   * @param resultList
   */
  private Map<String, List<RuleSetParameterAttr>> getParamDependency(final List<TRuleSetParamAttr> resultList)
      throws DataException {
    Map<String, List<RuleSetParameterAttr>> paramAttrMap = new ConcurrentHashMap<>();

    if (resultList != null) {
      for (TRuleSetParamAttr tParamAttr : resultList) {

        String paramName = tParamAttr.getTRuleSetParam().getTParameter().getName();
        List<RuleSetParameterAttr> paramAttrList = paramAttrMap.get(paramName);
        if (paramAttrList == null) {
          paramAttrList = new ArrayList<>();

        }
        paramAttrList.add(createDataObject(tParamAttr));
        paramAttrMap.put(paramName, paramAttrList);

      }
    }
    return paramAttrMap;

  }

}
