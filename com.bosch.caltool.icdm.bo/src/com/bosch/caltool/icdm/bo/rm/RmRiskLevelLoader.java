/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.rm;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TRmRiskLevel;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.rm.RmRiskLevel;


/**
 * Load the Risk levels
 *
 * @author rgo7cob
 */
public class RmRiskLevelLoader extends AbstractBusinessObject<RmRiskLevel, TRmRiskLevel> {

  /**
   * @param serviceData serviceData
   */
  public RmRiskLevelLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.RM_RISK_LEVEL, TRmRiskLevel.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected RmRiskLevel createDataObject(final TRmRiskLevel dbRiskLevel) throws DataException {
    RmRiskLevel riskLvl = new RmRiskLevel();
    riskLvl.setId(dbRiskLevel.getRiskLevelId());
    riskLvl.setCode(dbRiskLevel.getRiskCode());
    riskLvl.setEngName(dbRiskLevel.getRiskTextEng());
    riskLvl.setGerName(dbRiskLevel.getRiskTextGer());
    riskLvl.setEngDesc(dbRiskLevel.getRiskDescEng());
    riskLvl.setGerDesc(dbRiskLevel.getRiskDescGer());
    riskLvl.setRiskWeight(dbRiskLevel.getRiskWeight());
    String name = getLangSpecTxt(dbRiskLevel.getRiskTextEng(), dbRiskLevel.getRiskTextGer());
    riskLvl.setName(name);
    String desc = getLangSpecTxt(dbRiskLevel.getRiskDescEng(), dbRiskLevel.getRiskDescGer());
    riskLvl.setDesc(desc);
    return riskLvl;
  }

  /**
   * @return set of FunctionDetails
   * @throws DataException DataException
   */
  public Map<Long, RmRiskLevel> getRiskLevels() throws DataException {
    Map<Long, RmRiskLevel> riskLvlMap = new ConcurrentHashMap<>();


    TypedQuery<TRmRiskLevel> tQuery = getEntMgr().createNamedQuery(TRmRiskLevel.GET_ALL, TRmRiskLevel.class);


    List<TRmRiskLevel> dbRiskLevels = tQuery.getResultList();

    for (TRmRiskLevel dbRiskLvl : dbRiskLevels) {
      riskLvlMap.put(dbRiskLvl.getRiskLevelId(), createDataObject(dbRiskLvl));
    }
    return riskLvlMap;
  }


}
