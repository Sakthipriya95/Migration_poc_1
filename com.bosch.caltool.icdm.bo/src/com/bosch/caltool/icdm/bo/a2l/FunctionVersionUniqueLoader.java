/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TFuncversUnique;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.FunctionVersionUnique;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author rgo7cob
 */
public class FunctionVersionUniqueLoader extends AbstractBusinessObject<FunctionVersionUnique, TFuncversUnique> {


  /**
   * @param serviceData serviceData
   */
  public FunctionVersionUniqueLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.FUNC_VERS_UNIQUE, TFuncversUnique.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected FunctionVersionUnique createDataObject(final TFuncversUnique tfuncVers) throws DataException {
    FunctionVersionUnique funcVerUniq = new FunctionVersionUnique();
    funcVerUniq.setId(tfuncVers.getId());
    funcVerUniq.setFuncName(tfuncVers.getFuncname());
    funcVerUniq.setFuncVersion(tfuncVers.getFuncversion());
    funcVerUniq.setFuncNameUpper(tfuncVers.getUpperName());
    return funcVerUniq;
  }


  /**
   * @param funcName funcName
   * @return the Function version unique
   * @throws DataException DataException
   */
  public List<FunctionVersionUnique> getAllVersions(final String funcName) throws DataException {

    List<FunctionVersionUnique> funcVerList = new ArrayList<>();

    final TypedQuery<TFuncversUnique> typeQuery =
        getEntMgr().createNamedQuery(TFuncversUnique.NQ_GET_VERS_BY_FUNCNAME, TFuncversUnique.class);

    typeQuery.setParameter("fname", funcName.toUpperCase(Locale.GERMAN));

    typeQuery.setHint(ApicConstants.FETCH_SIZE, "2000");
    typeQuery.setHint(ApicConstants.SHARED_CACHE, "true");

    final List<TFuncversUnique> dbVerList = typeQuery.getResultList();


    for (TFuncversUnique dbFuncVer : dbVerList) {
      funcVerList.add(createDataObject(dbFuncVer));
    }
    return funcVerList;
  }

}
