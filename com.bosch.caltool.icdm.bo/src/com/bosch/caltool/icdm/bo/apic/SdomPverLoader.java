/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.apic.MvSdomPver;
import com.bosch.caltool.icdm.model.apic.SdomPverRevision;


/**
 * @author bne4cob
 */
public class SdomPverLoader extends AbstractBusinessObject<SdomPverRevision, MvSdomPver> {

  /**
   * @param serviceData Service Data
   */
  public SdomPverLoader(final ServiceData serviceData) {
    super(serviceData, "SDOM PVER", MvSdomPver.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected SdomPverRevision createDataObject(final MvSdomPver entity) throws DataException {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Get all PVER names in the system
   *
   * @return all PVER names
   */
  public SortedSet<String> getAllPverNames() {
    SortedSet<String> retSet = new TreeSet<>();

    final TypedQuery<String> query = getEntMgr().createNamedQuery(MvSdomPver.NQ_GET_ALL_PVER_NAMES, String.class);
    retSet.addAll(query.getResultList());

    return retSet;
  }

  /**
   * Get all PVER Variants, for the given input
   *
   * @param pver PVER name
   * @return sorted set of variants
   */
  public SortedSet<String> getPverVariantNames(final String pver) {
    SortedSet<String> retSet = new TreeSet<>();

    final TypedQuery<String> query = getEntMgr().createNamedQuery(MvSdomPver.NQ_GET_PVER_VARS, String.class);
    query.setParameter("pver", pver.toUpperCase(Locale.getDefault()));

    retSet.addAll(query.getResultList());

    return retSet;
  }

  /**
   * Get all PVER Variant versions, for the given inputs
   *
   * @param pver PVER name
   * @param pverVariant PVER variant
   * @return sorted set of versions
   */
  public SortedSet<Long> getPverVariantVersions(final String pver, final String pverVariant) {
    SortedSet<Long> retSet = new TreeSet<>();

    final TypedQuery<Long> query = getEntMgr().createNamedQuery(MvSdomPver.NQ_GET_PVER_VAR_VERSIONS, Long.class);
    query.setParameter("pver", pver.toUpperCase(Locale.getDefault()));
    query.setParameter("pverVariant", pverVariant.toUpperCase(Locale.getDefault()));

    retSet.addAll(query.getResultList());

    return retSet;
  }

  /**
   * Get the pVER id for the sdomPver name, variant, revision
   *
   * @param sdomPverName sdom pver name
   * @param variant variant name
   * @param revision revision
   * @return Long sdom pver vers ID
   */
  public Long getPVERId(final String sdomPverName, final String variant, final Long revision) {
    getLogger().debug("fetching PVER ID for sdom pver name " + sdomPverName + " and variant " + variant +
        " and revision " + revision + "...");
    // ICDM-2511
    final TypedQuery<Long> typeQuery = getEntMgr().createNamedQuery(MvSdomPver.NQ_GET_PVER_VERS_NUM, Long.class);
    typeQuery.setParameter("pver", sdomPverName.toUpperCase(Locale.getDefault()));
    typeQuery.setParameter("pverVariant", variant.toUpperCase(Locale.getDefault()));
    typeQuery.setParameter("pverRevision", revision);
    // Get the results
    final List<Long> resultList = typeQuery.getResultList();
    Long pverID = 0L;
    if ((resultList != null) && !resultList.isEmpty()) {
      pverID = resultList.get(0);
    }
    getLogger().debug("Sdom pver id available for SDOM Pver " + sdomPverName + " and variant" + variant +
        " and revision " + revision + " is " + pverID);
    return pverID;
  }


  // ICDM-1456
  /**
   * Check whether the sdom pver name already exists.
   *
   * @param pverName the pver name
   * @return true if sdomname exists in MVSdomPver
   */
  public boolean checkPverNameExists(final String sdomPverName) {
    return getAllPverNames().contains(sdomPverName);
  }
}
