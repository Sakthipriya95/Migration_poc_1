/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPst;
import com.bosch.caltool.icdm.database.entity.a2l.TvcdmPstCont;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.cdr.VcdmPstContent;


/**
 * Loader class for Vcdm Pst Contents
 *
 * @author bru2cob
 */
public class VcdmPstContentLoader extends AbstractBusinessObject<VcdmPstContent, TvcdmPstCont> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public VcdmPstContentLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.PST_CONT, TvcdmPstCont.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected VcdmPstContent createDataObject(final TvcdmPstCont entity) throws DataException {
    VcdmPstContent object = new VcdmPstContent();

    object.setId(entity.getId());
    object.setPstId(entity.getPstId());
    object.setFileId(entity.getFileId());
    object.setVcdmClass(entity.getVcdmClass());
    object.setVcdmName(entity.getVcdmName());
    object.setVcdmVariant(entity.getVcdmVariant());
    object.setVcdmRevision(entity.getVcdmRevision());
    object.setFileName(entity.getFileName());

    return object;
  }

  /**
   * @param pstId pst id
   * @return SortedSet vCDMPSTContents
   * @throws DataException error while fetching data
   */
  public SortedSet<VcdmPstContent> getPSTContents(final Long pstId) throws DataException {
    SortedSet<VcdmPstContent> vcdmPSTContentsSet = new TreeSet<>();
    VcdmPstLoader pstLoader = new VcdmPstLoader(getServiceData());

    for (TvcdmPstCont iterable_element : pstLoader.getEntityObject(pstId).getTvcdmPstCont()) {
      VcdmPstContent pstConst = createDataObject(iterable_element);
      vcdmPSTContentsSet.add(pstConst);
    }
    return vcdmPSTContentsSet;
  }

  /**
   * Get VcdmPstContent records for the given A2L object ID
   *
   * @param a2lId a2lId
   * @return set of vCDM PST Contents
   * @throws IcdmException error while fetching data
   */
  public Set<VcdmPstContent> getVcdmPstContentsForA2l(final Long a2lId) throws IcdmException {
    Set<VcdmPstContent> retSet = new HashSet<>();

    A2LFile a2l = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lId);

    final TypedQuery<TvcdmPst> query =
        getEntMgr().createNamedQuery(TvcdmPst.NQ_GET_PST_BY_A2LFILE_INFO_ID, TvcdmPst.class);
    query.setParameter("a2lInfoId", a2l.getId());

    List<TvcdmPst> dbPstList = query.getResultList();
    if (dbPstList.isEmpty()) {
      VcdmPstContent obj = new VcdmPstContent();
      obj.setFileId(a2l.getVcdmA2lfileId());
      obj.setFileName(a2l.getFilename());
      obj.setVcdmClass("A2L");
      retSet.add(obj);
    }
    else {
      retSet.addAll(getPSTContents(dbPstList.get(0).getPstId()));
    }

    return retSet;
  }
}
