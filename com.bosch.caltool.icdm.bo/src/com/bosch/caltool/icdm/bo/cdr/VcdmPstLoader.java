/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPst;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.VcdmPst;


/**
 * Loader class for Vcdm Pst
 *
 * @author bru2cob
 */
public class VcdmPstLoader extends AbstractBusinessObject<VcdmPst, TvcdmPst> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public VcdmPstLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PST, TvcdmPst.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected VcdmPst createDataObject(final TvcdmPst entity) throws DataException {
    VcdmPst object = new VcdmPst();

    object.setId(entity.getPstId());
    object.setPstName(entity.getPstName());
    object.setPstVariant(entity.getPstVariant());
    object.setPstRevision(entity.getPstRevision());
    object.setPverName(entity.getPverName());
    object.setPverVariante(entity.getPverVariante());
    object.setPverRevision(entity.getPverRevision());
    object.setA2lInfoId(entity.getA2lInfoId());
    object.setCreDate(DateFormat.formatDateToString(entity.getCreDate(), DateFormat.DATE_FORMAT_15));
    object.setCreUser(entity.getCreUser());
    object.setModDate(DateFormat.formatDateToString(entity.getModDate(), DateFormat.DATE_FORMAT_15));
    object.setModUser(entity.getModUser());
    object.setVcdmCreDate(DateFormat.formatDateToString(entity.getVcdmCreDate(), DateFormat.DATE_FORMAT_15));
    object.setPstForIcdm(entity.getPstForIcdm());

    return object;
  }

  /**
   * Fetch the vCDM psts based on SDOM PVER name, variant and revision
   *
   * @param a2lFile A2LFile id
   * @return SortedSet<vCDMPST>
   * @throws DataException
   */
  public VcdmPst fetchvCDMPST(final Long a2lFileId) throws DataException {
    try {
      getLogger().debug("fetching VCDM PSTs...");
      final TypedQuery<TvcdmPst> query =
          getServiceData().getEntMgr().createNamedQuery(TvcdmPst.NQ_GET_PST_BY_A2LFILE_INFO_ID, TvcdmPst.class);
      query.setParameter("a2lInfoId", a2lFileId);

      List<TvcdmPst> dbPST = query.getResultList();
      if (dbPST.isEmpty()) {
        return null;
      }
      return createDataObject(dbPST.get(0));
    }
    catch (NoResultException e) {
      getLogger().error(e.getMessage(), e);
      return null;
    }
  }
}
