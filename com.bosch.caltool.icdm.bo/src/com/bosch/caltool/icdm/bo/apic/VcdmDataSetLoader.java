/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lFileinfo;
import com.bosch.caltool.icdm.database.entity.apic.MvTabeDataset;
import com.bosch.caltool.icdm.model.apic.VcdmDataSet;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.model.vcdm.VCDMDSTRevision;
import com.bosch.caltool.icdm.model.vcdm.VCDMProductKey;
import com.bosch.caltool.icdm.model.vcdm.VCDMProgramkey;


/**
 * @author bne4cob
 */
public class VcdmDataSetLoader extends AbstractBusinessObject<VcdmDataSet, MvTabeDataset> {

  /**
   * @param serviceData Service Data
   */
  public VcdmDataSetLoader(final ServiceData serviceData) {
    super(serviceData, "vCDM Data Set", MvTabeDataset.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected VcdmDataSet createDataObject(final MvTabeDataset entity) throws DataException {
    VcdmDataSet data = new VcdmDataSet();

    data.setAprjId(entity.getElementId());
    data.setAprjName(entity.getElementName());
    data.setCalibratedLabels(entity.getCalibratedLabels());
    data.setChangedLabels(entity.getChangedLabels());
    data.setCheckedLabels(entity.getCheckedLabels());
    data.setCompletedLabels(entity.getCompletedLabels());
    data.setCreatedDate(timestamp2Date(entity.getCreDate()));
    data.setCreatedUser(entity.getCreUser());
    data.setEaseedstModDate(timestamp2Date(entity.getEaseedstModDate()));
    data.setEaseedstState(entity.getEaseedstState());
    data.setId(entity.getEaseedstId());
    data.setModifiedDate(timestamp2Date(entity.getModDate()));
    data.setModifiedUser(entity.getModUser());
    data.setNoStateLabels(entity.getNoStateLabels());
    data.setParitionLabels(entity.getPartitionLabel());
    data.setPreLimLabels(entity.getPreLimLabels());
    data.setProductKey(entity.getProdKey());
    data.setProgramKey(entity.getProgKey());
    data.setRevisionNo(entity.getRevision().longValue());
    data.setTotalLabels(entity.getTotalLabels());

    return data;
  }

  /**
   * @param fileInfos List<MvTa2lFileinfo>
   * @return List<VCDMApplicationProject>
   */
  public List<VCDMApplicationProject> getDataSets(final List<MvTa2lFileinfo> fileInfos) {
    // set the parameter from the previous query
    if (CommonUtils.isNull(fileInfos)) {
      return Collections.emptyList();
    }

    final TypedQuery<MvTabeDataset> qDst =
        getEntMgr().createNamedQuery(MvTabeDataset.GET_VCDM_DST_BY_A2L_PVER_VERSION_ID, MvTabeDataset.class);
    qDst.setParameter(1, fileInfos.get(0).getSdomPverVersid());
    // get the DSTs
    final List<MvTabeDataset> datasets = qDst.getResultList();

    return convertDSTSTOModel(datasets);
  }


  /**
   * @param datasets MvTabeDataset
   * @return VCDMApplicationProjects
   */
  private List<VCDMApplicationProject> convertDSTSTOModel(final List<MvTabeDataset> datasets) {

    if (CommonUtils.isNull(datasets)) {
      return Collections.emptyList();
    }
    Map<String, VCDMApplicationProject> aprjMap = new ConcurrentHashMap<>();
    for (MvTabeDataset mvTabeDataset : datasets) {

      VCDMApplicationProject vcdmApplicationProject = aprjMap.get(mvTabeDataset.getElementName());
      if (vcdmApplicationProject == null) {
        vcdmApplicationProject = new VCDMApplicationProject();
        vcdmApplicationProject.setAprjName(mvTabeDataset.getElementName());
        aprjMap.put(mvTabeDataset.getElementName(), vcdmApplicationProject);
      }

      VCDMProductKey vcdmVariant = vcdmApplicationProject.getVcdmVariants().get(mvTabeDataset.getProdKey());
      if (vcdmVariant == null) {
        vcdmVariant = new VCDMProductKey();
        vcdmVariant.setVariantName(mvTabeDataset.getProdKey());
        vcdmApplicationProject.getVcdmVariants().put(mvTabeDataset.getProdKey(), vcdmVariant);
      }

      VCDMProgramkey vcdmProgramkey = vcdmVariant.getProgramKeys().get(mvTabeDataset.getProgKey());
      if (vcdmProgramkey == null) {
        vcdmProgramkey = new VCDMProgramkey();
        vcdmProgramkey.setProgramKeyName(mvTabeDataset.getProgKey());
        vcdmVariant.getProgramKeys().put(mvTabeDataset.getProgKey(), vcdmProgramkey);
      }

      VCDMDSTRevision vcdmdstRevision = vcdmProgramkey.getvCDMDSTRevisions().get(mvTabeDataset.getRevision());
      if (vcdmdstRevision == null) {
        vcdmdstRevision = new VCDMDSTRevision();
        vcdmdstRevision.setDstID(mvTabeDataset.getEaseedstId());
        vcdmdstRevision.setRevisionNo(mvTabeDataset.getRevision());
        vcdmdstRevision.setCreatedDate(timestamp2Date(mvTabeDataset.getCreDate()));
        vcdmdstRevision.setVersionName(mvTabeDataset.getVersionName());
        vcdmProgramkey.getvCDMDSTRevisions().put(mvTabeDataset.getRevision(), vcdmdstRevision);
      }
    }
    return new ArrayList<>(aprjMap.values());
  }

  /**
   * Get VCDM data sets for the given PIDC
   *
   * @param pidcID pidc ID
   * @param timePeriod time period
   * @return Set of VcdmDataSet
   * @throws DataException unable to create data object
   */
  public Set<VcdmDataSet> getStatisticsByPidcId(final Long pidcID, final int timePeriod) throws DataException {

    // Find APRJ Name
    PidcVersion activePidcVers = (new PidcVersionLoader(getServiceData())).getActivePidcVersion(pidcID);
    String aprjName = (new PidcVersionAttributeLoader(getServiceData())).getAprjName(activePidcVers.getId());
    if (CommonUtils.isEmptyString(aprjName)) {
      throw new DataException("APRJ Name not defined in the PIDC " + pidcID);
    }

    // Fetch Easee data set statistics
    StringBuilder query = new StringBuilder();
    query.append("Select * from TABE_DATASETS where upper(ELEMENT_NAME) =? ");

    if (timePeriod > 0) {
      query.append("and trunc(EASEEDST_MOD_DATE) > trunc(sysdate) - ?");
    }

    final Query nativeQuery = getEntMgr().createNativeQuery(query.toString(), MvTabeDataset.class);
    nativeQuery.setParameter(1, aprjName.toUpperCase(Locale.GERMAN));
    if (timePeriod > 0) {
      nativeQuery.setParameter(2, timePeriod);
    }
    Set<VcdmDataSet> retSet = new HashSet<>();
    VcdmDataSet data;
    for (MvTabeDataset entity : (List<MvTabeDataset>) nativeQuery.getResultList()) {
      data = createDataObject(entity);
      // Set the aprj name, as obtained from PIDC Version, (case sensitivity, as per existing SOAP service)
      data.setAprjName(aprjName);
      retSet.add(data);
    }

    return retSet;
  }

  /**
   * Returns the DSTS using the same SDOM PVER VersionID as the vcdmA2lFileId passed as a parameter
   *
   * @param sdomPverName pver name
   * @param sdomPverVar pver variant name
   * @param sdomPverRevision pver variant revision
   * @return VCDMApplicationProjects
   */
  public List<VCDMApplicationProject> getDataSets(final String sdomPverName, final String sdomPverVar,
      final String sdomPverRevision) {
    final TypedQuery<MvTa2lFileinfo> qSdomPverVersID =
        getEntMgr().createNamedQuery(MvTa2lFileinfo.GET_A2L_INFO_BY_PVER_INFO, MvTa2lFileinfo.class);
    // set the parameter
    qSdomPverVersID.setParameter("sdomPverName", sdomPverName);
    qSdomPverVersID.setParameter("sdomPverVariant", sdomPverVar);
    qSdomPverVersID.setParameter("sdomPverRevision", Long.valueOf(sdomPverRevision));
    // execute the query
    final List<MvTa2lFileinfo> fileInfos = qSdomPverVersID.getResultList();

    if (!fileInfos.isEmpty()) {
      final TypedQuery<MvTabeDataset> qDst =
          getEntMgr().createNamedQuery(MvTabeDataset.GET_VCDM_DST_BY_A2L_PVER_VERSION_ID, MvTabeDataset.class);
      // set the parameter from the previous query
      qDst.setParameter(1, fileInfos.get(0).getSdomPverVersid());
      // get the DSTs
      List<MvTabeDataset> datasets = qDst.getResultList();
      return convertDSTSTOModel(datasets);
    }
    return Collections.emptyList();
  }

}
