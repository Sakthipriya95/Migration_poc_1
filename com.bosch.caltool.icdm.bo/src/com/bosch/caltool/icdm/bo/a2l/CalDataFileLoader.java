/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.util.List;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.database.entity.apic.MvTabvCaldatafile;

/**
 * @author apj4cob
 */
public class CalDataFileLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData Service Data
   */
  public CalDataFileLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param dstId dst id
   * @return a2l file Info Id
   * @throws InvalidInputException if a2l is missing
   */
  public List<Long> getA2lFileInfoByDstId(final Long dstId) throws InvalidInputException {
    TypedQuery<Long> tQuery =
        getEntMgr().createNamedQuery(MvTabvCaldatafile.GET_A2L_FILE_INFO_ID_BY_DST_ID, Long.class);
    tQuery.setParameter("easeedstId", dstId);
    List<Long> a2lFileIdInDst = tQuery.getResultList();
    if (a2lFileIdInDst.isEmpty()) {
      throw new InvalidInputException("COMPLI_REVIEW.A2L_MISSING");
    }
    return a2lFileIdInDst;
  }
}
