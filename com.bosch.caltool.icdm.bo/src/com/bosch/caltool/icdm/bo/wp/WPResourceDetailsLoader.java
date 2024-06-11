/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.wp;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.a2l.TWpResource;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.wp.WPResourceDetails;


/**
 * @author dja7cob Loader class for the service to fetch Wp resources
 */
public class WPResourceDetailsLoader extends AbstractBusinessObject<WPResourceDetails, TWpResource> {

  /**
   * @param serviceData service Data
   */
  public WPResourceDetailsLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WORK_PKG_RESOURCE, TWpResource.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WPResourceDetails createDataObject(final TWpResource dbWpRes) throws DataException {

    WPResourceDetails wpRes = new WPResourceDetails();
    wpRes.setWpResId(dbWpRes.getWpResId());
    wpRes.setWpResCode(dbWpRes.getResourceCode());
    return wpRes;

  }

  /**
   * @return set of WorkPackageDetails
   * @throws DataException when object cannot be created
   */
  public Set<WPResourceDetails> getAllWpRes() throws DataException {
    Set<WPResourceDetails> retSet = new HashSet<>();

    TypedQuery<TWpResource> tQuery = getEntMgr().createNamedQuery(TWpResource.NQ_FIND_ALL, TWpResource.class);

    List<TWpResource> dbWPResources = tQuery.getResultList();

    for (TWpResource dbWpRes : dbWPResources) {
      retSet.add(createDataObject(dbWpRes));
    }
    return retSet;
  }

}
