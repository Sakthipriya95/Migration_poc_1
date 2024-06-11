/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
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
import com.bosch.caltool.icdm.database.entity.apic.GttObjectName;
import com.bosch.caltool.icdm.database.entity.cdr.TParamAttr;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.ParameterAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.cdr.IParameter;

/**
 * @author svj7cob
 */
public class ParameterAttributeLoader extends AbstractBusinessObject<ParameterAttribute, TParamAttr> {


  /**
   * @param servData Service Data
   */
  public ParameterAttributeLoader(final ServiceData servData) {
    super(servData, MODEL_TYPE.PARAMETER_ATTR, TParamAttr.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected ParameterAttribute createDataObject(final TParamAttr entity) throws DataException {
    ParameterAttribute paramAttr = new ParameterAttribute();
    paramAttr.setAttrId(entity.getTabvAttribute().getAttrId());

    paramAttr.setId(entity.getParamAttrId());
    AttributeLoader attrloader = new AttributeLoader(getServiceData());
    Attribute attrObj = attrloader.getDataObjectByID(entity.getTabvAttribute().getAttrId());

    paramAttr.setName(attrObj.getName());

    paramAttr.setParamId(entity.getTParameter().getId());

    paramAttr.setVersion(entity.getVersion());


    paramAttr.setDescription(attrObj.getDescription());
    setCommonFields(paramAttr, entity);

    return paramAttr;
  }


  /**
   * @param paramSet
   * @param functionName
   * @param funcVer
   * @return the param Attrs
   * @throws DataException
   */
  public Map<String, List<ParameterAttribute>> getParamAttrs(final Set<Parameter> paramSet, final String functionName,
      final String funcVer)
      throws DataException {


    getLogger().debug("getParamAttrs() - fetching attr dependencies for parameter(s)...");
    List<TParamAttr> resultList = null;
    Map<Long, String> cdrFuncParamMap = new ConcurrentHashMap<>();
    for (Parameter parameter : paramSet) {
      cdrFuncParamMap.put(parameter.getId(), parameter.getName());
    }
    if (!cdrFuncParamMap.isEmpty()) {
      if (cdrFuncParamMap.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT) {
        final Query qDbattrDepn = getEntMgr().createNamedQuery(TParamAttr.NQ_GET_PARAM_ATTR_BY_ID, TParamAttr.class);
        qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "500");
        qDbattrDepn.setHint(ApicConstants.READ_ONLY, "true");
        qDbattrDepn.setParameter("paramList", cdrFuncParamMap.keySet());

        resultList = qDbattrDepn.getResultList();


      } // For more than 1000 parameters JOIN query is used
      else if ((functionName != null) && (funcVer != null)) {
        String query =
            "SELECT distinct(pattr) FROM TParamAttr pattr join pattr.TParameter param,TFunctionversion funver" +
                " where pattr.TParameter.name = funver.defcharname and funver.funcNameUpper = :functionName" +
                " and funver.funcversion=:funcVer";

        final Query qDbattrDepn = getEntMgr().createQuery(query, TParamAttr.class);
        qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "500");
        qDbattrDepn.setHint(ApicConstants.READ_ONLY, "true");
        qDbattrDepn.setParameter("functionName", functionName.toUpperCase(Locale.GERMAN));
        qDbattrDepn.setParameter("funcVer", funcVer);
        resultList = qDbattrDepn.getResultList();

      }
    }
    return getParamDependency(resultList);
  }

  /**
   * fetch the attr dependency to a parameter
   *
   * @param paramSet parameters
   * @return map which contains paramter attr for a particular parameter
   * @throws DataException
   */

  public Map<String, List<ParameterAttribute>> fetchParameterAttrDepn(final Set<IParameter> paramSet)
      throws DataException {

    getLogger().debug("fetchParameterAttrDepn() - fetching attr dependencies for parameter(s)");

    EntityManager em = null;
    Map<Long, String> cdrFuncParamMap = new ConcurrentHashMap<>();
    if (paramSet != null) {
      for (IParameter cdrFuncParameter : paramSet) {
        cdrFuncParamMap.put(cdrFuncParameter.getId(), cdrFuncParameter.getName());
      }
    }
    List<TParamAttr> resultList = null;
    if (!cdrFuncParamMap.isEmpty()) {
      if (cdrFuncParamMap.size() <= ApicConstants.JPA_IN_CLAUSE_LIMIT) {
        final Query qDbattrDepn =
            getEntMgr().createNamedQuery(TParamAttr.NQ_GET_PARAM_ATTR_BY_PARAMLIST, TParamAttr.class);
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
          final TypedQuery<TParamAttr> qDbattrDepn =
              em.createNamedQuery(TParamAttr.NQ_GET_PARAM_ATTR_BY_ID, TParamAttr.class);
          qDbattrDepn.setHint(ApicConstants.FETCH_SIZE, "2000");

          resultList = qDbattrDepn.getResultList();

          delQuery.executeUpdate();
          em.getTransaction().commit();
        }
        finally {
          closeEm(em);
        }
      }
    }
    return getParamDependency(resultList);
  }

  /**
   * @param resultList
   */
  private Map<String, List<ParameterAttribute>> getParamDependency(final List<TParamAttr> resultList)
      throws DataException {
    Map<String, List<ParameterAttribute>> paramAttrMap = new ConcurrentHashMap<>();

    if (resultList != null) {
      for (TParamAttr tParamAttr : resultList) {

        String paramName = tParamAttr.getTParameter().getName();
        List<ParameterAttribute> paramAttrList = paramAttrMap.get(paramName);
        if (paramAttrList == null) {
          paramAttrList = new ArrayList<>();

        }
        paramAttrList.add(createDataObject(tParamAttr));
        paramAttrMap.put(paramName, paramAttrList);

      }
    }
    return paramAttrMap;

  }

  /**
   * Method to get the parameter names which has dependencies
   *
   * @param baseParamSet baseParamSet
   * @return the param name list which have dep
   */
  public Set<String> fetchParamsWithDep(final Set<String> baseParamSet) {
    // Icdm-1255- Variant coded Parameter
    Set<String> paramNameWithDep = new HashSet<>();
    EntityManager em = null;

    try {
      em = ObjectStore.getInstance().getEntityManagerFactory().createEntityManager();
      em.getTransaction().begin();
      GttObjectName tempParam;
      long recID = 1;
      // Delete the existing records in this temp table, if any
      final Query delQuery = em.createNamedQuery(GttObjectName.NS_DELETE_GTT_OBJ_NAMES);
      delQuery.executeUpdate();

      // Create entities for all the functions
      for (String label : baseParamSet) {
        tempParam = new GttObjectName();
        tempParam.setId(recID);
        tempParam.setObjName(label);
        em.persist(tempParam);
        recID++;
      }

      em.flush();
      final TypedQuery<String> typeQuery = em.createNamedQuery(TParamAttr.NQ_GET_PARAM_ATTRS, String.class);
      typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");


      paramNameWithDep.addAll(typeQuery.getResultList());


      delQuery.executeUpdate();

      em.getTransaction().commit();
    }
    finally {
      closeEm(em);
    }
    return paramNameWithDep;
  }

  /**
   * @param em
   */
  private void closeEm(final EntityManager em) {
    if ((em != null) && em.isOpen()) {
      em.close();
    }
  }

}
