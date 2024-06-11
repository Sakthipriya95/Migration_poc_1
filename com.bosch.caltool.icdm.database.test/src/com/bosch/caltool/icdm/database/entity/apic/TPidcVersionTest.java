/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.database.entity.apic;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import javax.persistence.TypedQuery;

import org.junit.Test;

import com.bosch.caltool.icdm.database.AbstractDbTest;

/**
 * @author bne4cob
 */
public class TPidcVersionTest extends AbstractDbTest {

  /**
   * Test 'named query' {@link TPidcVersion#NQ_GET_PIDC_VERS_BY_APRJ_N_A2L}
   */
  @Test
  public void testNqGetPidcVersByAprjNA2l() {
    TypedQuery<Long> qry = getEntMgr().createNamedQuery(TPidcVersion.NQ_GET_PIDC_VERS_BY_APRJ_N_A2L, Long.class);
    qry.setParameter("a2lFileId", Arrays.asList(1119623851L));
    qry.setParameter("aprjValId", 6349L);

    List<Long> resultList = qry.getResultList();

    assertTrue("Results not empty", !resultList.isEmpty());

    LOG.debug("Results : {}", resultList.size());
    for (Long pidcVersId : resultList) {
      LOG.debug("  {}", pidcVersId);
    }

  }
}
