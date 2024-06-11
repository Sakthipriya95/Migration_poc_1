/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TPidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2LFileInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2LFiles;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;


/**
 * @author bru2cob
 */
public class PidcA2LFilesServiceDataLoader extends AbstractSimpleBusinessObject {

  /**
   * pidc version id
   */
  private static final int PIDC_VERS_ID = 4;
  /**
   * pidc version name
   */
  private static final int PIDC_VERS_NAME = 5;
  /**
   * a2l assigned date
   */
  private static final int ASSIGNED_DATE = 3;
  /**
   * a2l created date
   */
  private static final int CREATED_DATE = 6;
  /**
   * pver variant name
   */
  private static final int SDOM_PVER_VARIANT = 1;
  /**
   * relevant a2l file name
   */
  private static final int A2L_FILENAME = 2;
  /**
   * a2l created date
   */
  private static final int A2L_FILEID = 7;
  /**
   * pidc a2l file id
   */
  private static final int PIDC_A2L_FILEID = 8;
  /**
   * sdom pver name
   */
  private static final int SDOM_PVER_NAME = 0;

  /**
   * @param serviceData Service Data
   */
  public PidcA2LFilesServiceDataLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Fetch the project's a2l data
   *
   * @param projectID Project ID
   * @return project's a2l data
   * @throws IcdmException error while fetching data
   */
  public PidcA2LFiles getPidcA2lFiles(final Long projectID) throws IcdmException {
    TypedQuery<Object[]> pidcA2LQry = getEntMgr().createNamedQuery(TPidcA2l.GET_RELEVANT_A2L_FILES, Object[].class);
    pidcA2LQry.setParameter(1, projectID);

    return createDataObject(pidcA2LQry);
  }

  /**
   * Create data object
   *
   * @param pidcA2LQry query result
   * @throws IcdmException
   */
  private PidcA2LFiles createDataObject(final TypedQuery<Object[]> pidcA2LQry) throws IcdmException {
    // list of a2l files which has been reviewed
    List<Object[]> resultList = pidcA2LQry.getResultList();
    PidcA2LFiles ret = new PidcA2LFiles();

    for (Object[] resObj : resultList) {
      long versID = ((BigDecimal) resObj[PIDC_VERS_ID]).longValue();

      if (!ret.getPidcVersMap().containsKey(versID)) {
        ret.getPidcVersMap().put(versID, (String) resObj[PIDC_VERS_NAME]);
      }

      List<PidcA2LFileInfo> pidcA2LFilesList = ret.getPidcA2LInfo().get(versID);
      if (pidcA2LFilesList == null) {
        pidcA2LFilesList = new ArrayList<>();
        ret.getPidcA2LInfo().put(versID, pidcA2LFilesList);
      }
      // create the pidca2lfileinfo objs
      PidcA2LFileInfo a2lInfo = new PidcA2LFileInfo();
      a2lInfo.setAssignedDate((Date) resObj[ASSIGNED_DATE]);
      a2lInfo.setCreatedDate((Date) resObj[CREATED_DATE]);
      String a2lFileName = (String) resObj[A2L_FILENAME];
      a2lInfo.setFileName(a2lFileName);
      a2lInfo.setPverName((String) resObj[SDOM_PVER_NAME]);
      String pverVarNameEng = (String) resObj[SDOM_PVER_VARIANT];
      a2lInfo.setPverVariant(pverVarNameEng);
      a2lInfo.setA2lFileID(((BigDecimal) resObj[A2L_FILEID]).longValue());
      a2lInfo.setPidcA2lFileId(((BigDecimal) resObj[PIDC_A2L_FILEID]).longValue());
      pidcA2LFilesList.add(a2lInfo);
    }

    loadPidcVariants(ret);

    return ret;
  }

  /**
   * sets the variants
   *
   * @throws IcdmException
   */
  private void loadPidcVariants(final PidcA2LFiles pidcA2lFiles) throws IcdmException {
    Set<Long> pidcA2lIdSet = new HashSet<>();
    pidcA2lFiles.getPidcA2LInfo().values().stream()
        .map(lst -> lst.stream().map(PidcA2LFileInfo::getPidcA2lFileId).collect(Collectors.toList()))
        .forEach(pidcA2lIdSet::addAll);

    Map<Long, Map<Long, PidcVariant>> a2lMappedVariantsMap =
        new PidcVariantLoader(getServiceData()).getA2lMappedVariants(pidcA2lIdSet,false);
    for (Long versID : pidcA2lFiles.getPidcVersMap().keySet()) {
      for (PidcA2LFileInfo pidcA2l : pidcA2lFiles.getPidcA2LInfo().get(versID)) {
        getMappedVariants(pidcA2lFiles, pidcA2l, a2lMappedVariantsMap.get(pidcA2l.getPidcA2lFileId()));
      }
    }
  }

  /**
   * @param pidcVers
   * @param pidcA2l
   * @param a2lFile
   * @throws IcdmException
   */
  private void getMappedVariants(final PidcA2LFiles pidcA2lFiles, final PidcA2LFileInfo pidcA2l,
      final Map<Long, PidcVariant> a2lMappedVariantsMap) {
    for (PidcVariant vars : a2lMappedVariantsMap.values()) {
      Long varID = vars.getId();
      if (!pidcA2lFiles.getPidcVarsMap().containsKey(varID)) {
        pidcA2lFiles.getPidcVarsMap().put(varID, vars.getName());
      }
      pidcA2l.getVariantIDList().add(varID);
    }
  }
}
