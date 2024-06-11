/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.emr;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.emr.TEmrEmissionStandard;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.emr.EmrEmissionStandard;

/**
 * @author bru2cob
 */
public class EmrEmissionStandardLoader extends AbstractBusinessObject<EmrEmissionStandard, TEmrEmissionStandard> {

  /**
   * @param serviceData Service Data
   */
  public EmrEmissionStandardLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.EMR_EMIS_STD, TEmrEmissionStandard.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected EmrEmissionStandard createDataObject(final TEmrEmissionStandard entity) throws DataException {
    EmrEmissionStandard emissionStd = new EmrEmissionStandard();
    emissionStd.setId(entity.getEmsId());
    emissionStd.setEmissionStandardFlag(ApicConstants.CODE_YES.equalsIgnoreCase(entity.getEmissionStandardFlag()));
    emissionStd.setEmissionStandardName(entity.getEmissionStandardName());
    emissionStd.setMeasuresFlag(ApicConstants.CODE_YES.equalsIgnoreCase(entity.getMeasuresFlag()));
    emissionStd.setTestcaseFlag(ApicConstants.CODE_YES.equalsIgnoreCase(entity.getTestcaseFlag()));
    emissionStd.setVersion(entity.getVersion());
    if (entity.getTEmrEmissionStandard() != null) {
      emissionStd.setParentId(entity.getTEmrEmissionStandard().getEmsId());
    }
    return emissionStd;
  }

  /**
   * @return the Map of emission standard with id as key
   * @throws DataException DataException
   */
  public Map<String, EmrEmissionStandard> getAllEmissionStd() throws DataException {
    Map<String, EmrEmissionStandard> emissionStdlMap = new ConcurrentHashMap<>();

    TypedQuery<TEmrEmissionStandard> tQuery =
        getEntMgr().createNamedQuery(TEmrEmissionStandard.GET_ALL, TEmrEmissionStandard.class);

    List<TEmrEmissionStandard> dbEmissionStd = tQuery.getResultList();

    for (TEmrEmissionStandard dbEmrEmissionStd : dbEmissionStd) {
      emissionStdlMap.put(dbEmrEmissionStd.getEmissionStandardName().toUpperCase(), createDataObject(dbEmrEmissionStd));
    }
    return emissionStdlMap;
  }
}
