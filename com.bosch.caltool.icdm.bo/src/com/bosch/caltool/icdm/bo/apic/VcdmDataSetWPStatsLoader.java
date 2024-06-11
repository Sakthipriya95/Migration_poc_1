/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Query;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.MvVcdmDatasetsWorkpkgStat;
import com.bosch.caltool.icdm.model.apic.VcdmDataSetWPStats;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bne4cob
 */
public class VcdmDataSetWPStatsLoader extends AbstractBusinessObject<VcdmDataSetWPStats, MvVcdmDatasetsWorkpkgStat> {

  /**
   * @param serviceData Service Data
   */
  public VcdmDataSetWPStatsLoader(final ServiceData serviceData) {
    super(serviceData, "vCDM Data Set Work Package Statistics", MvVcdmDatasetsWorkpkgStat.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected VcdmDataSetWPStats createDataObject(final MvVcdmDatasetsWorkpkgStat entity) throws DataException {
    VcdmDataSetWPStats data = new VcdmDataSetWPStats();

    data.setId(entity.getId());
    data.setAprjId(entity.getAprjId());
    data.setAprjName(entity.getAprjName());
    data.setCalibratedLabels(entity.getCalibratedLabels());
    data.setChangedLabels(entity.getChangedLabels());
    data.setCheckedLabels(entity.getCheckedLabels());
    data.setCompletedLabels(entity.getCompletedLabels());
    data.setEaseeDstId(entity.getEaseeDstId());
    data.setEaseedstModDate(entity.getEaseedstModDate());
    data.setNostateLabels(entity.getNostateLabels());
    data.setPrelimcalibratedLabels(entity.getPrelimcalibratedLabels());
    data.setRevisionNo(entity.getRevision().longValue());
    data.setStatusId(entity.getStatusId());
    data.setVcdmSoftware(entity.getVcdmSoftware());
    data.setVcdmVariant(entity.getVcdmVariant());
    data.setWorkpkgName(entity.getWorkpkgName());

    return data;
  }

  /**
   * Get VCDM data set WP Statistics for the given PIDC
   *
   * @param pidcID pidc ID
   * @param vcdmDstId vCDM DST ID
   * @param timePeriod time period
   * @return Set of VcdmDataSetWPStats
   * @throws DataException unable to create data object
   */
  public Set<VcdmDataSetWPStats> getStatisticsByPidcId(final Long pidcID, final long vcdmDstId, final int timePeriod)
      throws DataException {

    // Find APRJ Name
    PidcVersion activePidcVers = (new PidcVersionLoader(getServiceData())).getActivePidcVersion(pidcID);
    String aprjName = (new PidcVersionAttributeLoader(getServiceData())).getAprjName(activePidcVers.getId());
    if (CommonUtils.isEmptyString(aprjName)) {
      throw new DataException("APRJ Name not defined in the PIDC : " + pidcID);
    }

    // Fetch statistics
    StringBuilder query = new StringBuilder();
    query.append("SELECT * FROM V_VCDM_DATASETS_WORKPKG_STAT where APRJ_NAME= ? ");
    // 221731
    if (vcdmDstId > 0) {
      query.append(" AND EASEEDST_ID= ").append(vcdmDstId);
    }
    if (timePeriod > 0) {
      query.append(" AND TRUNC(EASEEDST_MOD_DATE) > TRUNC(sysdate) - ? ");
    }

    final Query nativeQuery = getEntMgr().createNativeQuery(query.toString(), MvVcdmDatasetsWorkpkgStat.class);
    nativeQuery.setParameter(1, aprjName);
    if (timePeriod > 0) {
      nativeQuery.setParameter(2, timePeriod);
    }
    Set<VcdmDataSetWPStats> retSet = new HashSet<>();
    for (MvVcdmDatasetsWorkpkgStat entity : (List<MvVcdmDatasetsWorkpkgStat>) nativeQuery.getResultList()) {
      retSet.add(createDataObject(entity));
    }

    return retSet;
  }

}
