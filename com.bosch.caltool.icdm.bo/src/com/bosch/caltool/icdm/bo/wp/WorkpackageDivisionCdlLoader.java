/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */

package com.bosch.caltool.icdm.bo.wp;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.TWorkpackageDivisionCdl;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.wp.WorkpackageDivisionCdl;


/**
 * Loader class for WPDivCdl
 *
 * @author apj4cob
 */
public class WorkpackageDivisionCdlLoader extends AbstractBusinessObject<WorkpackageDivisionCdl, TWorkpackageDivisionCdl> {

  /**
   * Constructor
   * 
   * @param serviceData Service Data
   */
  public WorkpackageDivisionCdlLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WORKPACKAGE_DIVISION_CDL, TWorkpackageDivisionCdl.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WorkpackageDivisionCdl createDataObject(final TWorkpackageDivisionCdl entity) throws DataException {
    WorkpackageDivisionCdl object = new WorkpackageDivisionCdl();

    setCommonFields(object, entity);

    object.setRegionId(entity.getTRegion().getRegionId());
    object.setWpDivId(entity.getTWorkpackageDivision().getWpDivId());
    object.setUserId(entity.getTabvApicUser().getUserId());

    return object;
  }

  /**
   * @param wpDivId WorkPackageDivision Id
   * @return Set of WorkpackageDivisionCdl
   * @throws DataException error while retrieving data
   */
  public Set<WorkpackageDivisionCdl> getWorkpkgDivCdlByWpDivId(final Long wpDivId) throws DataException {
    Set<WorkpackageDivisionCdl> retSet = new HashSet<>();
    TypedQuery<TWorkpackageDivisionCdl> tQuery = getEntMgr()
        .createNamedQuery(TWorkpackageDivisionCdl.GET_ALL_WPDIVCDL_BY_WPDIVID, TWorkpackageDivisionCdl.class);
    tQuery.setParameter("wpDivId", wpDivId);
    for (TWorkpackageDivisionCdl dbObj : tQuery.getResultList()) {
      retSet.add(createDataObject(dbObj));
    }
    return retSet;
  }

}
