/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.bosch.calcomp.a2lparser.a2l.A2LParser;
import com.bosch.calcomp.parser.a2l.exception.A2lParserException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.MvTa2lFileinfo;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * A2L file uploader. Supporting datamodel class for A2LFileUploaderJob class in UI
 *
 * @author adn1cob
 */
@Deprecated
public class A2LDetailsUploader {

  /**
   * a2l file path to be uploaded
   */
  private final String a2lFilePath;
  /**
   * Data provider
   */
  private final ApicDataProvider apicDataProvider;

  /**
   * @param apicDataProvider dataProvider
   * @param a2lFilePath file path of the a2l
   */
  public A2LDetailsUploader(final ApicDataProvider apicDataProvider, final String a2lFilePath) {
    this.apicDataProvider = apicDataProvider;
    this.a2lFilePath = a2lFilePath;
  }


  /**
   * Parses a2l file to check if it is valid
   */
  public void validateA2LFile() {

    this.apicDataProvider.getLogger().debug("A2L file validation started... " + this.a2lFilePath);
    A2LParser a2lParser = new A2LParser(this.apicDataProvider.getLogger());
    a2lParser.setFileName(this.a2lFilePath);
    // Parse the A2L file and create the A2LFileInfo object.
    a2lParser.parse();
    if (a2lParser.getA2LFileInfo() == null) {
      String strErr = "Invalid a2l file : " + this.a2lFilePath;
      this.apicDataProvider.getLogger().error(strErr);
      throw new A2lParserException(strErr);
    }

  }

  /**
   * iCDM-778 <br>
   * Add BC info to A2L in database. Calls a stored procedure Get_BC_Info
   *
   * @param a2lInfoId a2l id returned from A2lLoader
   */
  public void addBCInfo(final long a2lInfoId) {
    this.apicDataProvider.getLogger().debug("Starting to add BC details to A2L in db for a2l file id : " + a2lInfoId);
    try {
      this.apicDataProvider.getEntityProvider().startTransaction();
      final EntityManager entityMgr = this.apicDataProvider.getEntityProvider().getEm();
      Query query = entityMgr.createNativeQuery("BEGIN Get_BC_Info(?); END;");
      query.setParameter(1, a2lInfoId);
      query.executeUpdate();
      this.apicDataProvider.getEntityProvider().commitChanges();
    }
    finally {
      this.apicDataProvider.getEntityProvider().endTransaction();
    }
  }

  /**
   * @return the a2lFilePath
   */
  public String getA2lFilePath() {
    return this.a2lFilePath;
  }

  /**
   * Set SDOM Pver details to A2L in db
   *
   * @param a2lId a2lFileID
   * @param pverName sdom pver name
   * @param varName sdom variant name
   * @param varRev sdom variant revision
   * @param vcdmA2lfileId the VERSION_NUBER of the file in vCDM
   */
  public void addPverData(final Long a2lId, final String pverName, final String varName, final Long varRev,
      final String vcdmA2lfileId) {
    // TA2L_FILEINFO view
    final MvTa2lFileinfo dbA2lInfo = this.apicDataProvider.getEntityProvider().getDbA2LFileInfo(a2lId);
    if (CommonUtils.isNotNull(dbA2lInfo)) {
      try {
        this.apicDataProvider.getEntityProvider().startTransaction();
        // ICDM - 1942
        final EntityManager entityMgr = this.apicDataProvider.getEntityProvider().getEm();
        Query query = entityMgr.createNativeQuery("BEGIN SET_SDOM_INFO_FOR_A2L(?,?,?,?,?,?); END;");
        query.setParameter(ApicConstants.COLUMN_INDEX_1, a2lId);
        query.setParameter(ApicConstants.COLUMN_INDEX_2, pverName);
        query.setParameter(ApicConstants.COLUMN_INDEX_3, varName);
        query.setParameter(ApicConstants.COLUMN_INDEX_4, varRev);
        query.setParameter(ApicConstants.COLUMN_INDEX_5, this.apicDataProvider.getPVERId(pverName, varName, varRev));
        query.setParameter(ApicConstants.COLUMN_INDEX_6, vcdmA2lfileId);
        query.executeUpdate();
        this.apicDataProvider.getEntityProvider().commitChanges();
        // refresh the entity object
        this.apicDataProvider.getEntityProvider().refreshCacheObject(dbA2lInfo);

      }
      finally {
        this.apicDataProvider.getEntityProvider().endTransaction();

      }

    }
    else {
      // normally this would not happen, log a debug message if here..
      this.apicDataProvider.getLogger().debug("A2l File info not found for the a2l file id : " + a2lId);
    }
  }

  /**
   * Add the new A2L file PVER a2l files map
   *
   * @param a2lFile A2L file
   * @param pver PVER
   */
  public void addA2LInfoToCache(final A2LFile a2lFile, final SdomPver pver) {

    Map<Long, A2LFile> retMap = this.apicDataProvider.getDataCache()
        .getPverAllA2LMap(pver.getPIDCVersion().getPidc().getID(), pver.getName(), false);
    // Add the new A2L file to the cache, only if the a2l files are retrieved from DB
    if (retMap != null) {
      retMap.put(a2lFile.getID(), a2lFile);
    }

  }
}
