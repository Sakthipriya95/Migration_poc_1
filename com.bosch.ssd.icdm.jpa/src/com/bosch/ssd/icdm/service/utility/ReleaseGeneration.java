/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.utility;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.entity.TempNonSDOMNodeList;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException.SSDiCDMInterfaceErrorCodes;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;
import com.bosch.ssd.icdm.model.utils.DBEntityCreationUtil;

/**
 * @author SSN9COB
 */
public class ReleaseGeneration {

  private final DBQueryUtils dbQueryUtils;
  private List<String> errorlist;
  private BigDecimal maxRevId;
  private BigDecimal proRevId;

  /**
   * Constructor
   *
   * @param queryUtils service
   */
  public ReleaseGeneration(final DBQueryUtils queryUtils) {
    this.dbQueryUtils = queryUtils;
  }


  /**
   * @return the proRevId
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }

  /**
   * @return the errorlist
   */
  public List<String> getErrorlist() {
    return new ArrayList<>(this.errorlist);
  }

  /**
   * @param map
   * @param errorlist
   * @return
   */
  private boolean validEntry(final Entry<String, String> map) {
    if (map.getKey().length() > 255) {
      this.errorlist.add(map.getKey() + " Label length exceeded 255");
    }
    else if (map.getValue().length() > 32) {
      this.errorlist.add(map.getKey() + " Unit length exceeded 32");
    }
    else {
      return true;
    }
    return false;
  }

  /**
   * method creates a labellist in ssd database
   *
   * @param nodeId -
   * @param labellist -
   * @return List
   * @throws SSDiCDMInterfaceException exception
   */
  public List<String> createLabelList(final BigDecimal nodeId, final Map<String, String> labellist)
      throws SSDiCDMInterfaceException {
    BigDecimal versId = this.dbQueryUtils.getVersId(nodeId);
    this.maxRevId = this.dbQueryUtils.findMaxRevId(versId);

    // next rev number for the new label list
//    maxRevId =maxRevId.add(BigDecimal.ONE)
    this.errorlist = new ArrayList<>();
    this.proRevId = createRevision(versId);
    if (this.proRevId == null) {
      // error
      throw ExceptionUtils.createAndThrowException(null,
          "New labellist revision could not be created " + SSDMessage.LABELLISTCREATIONFAILED,
          SSDiCDMInterfaceErrorCodes.DATABASE_EXCEPTION, true);
    }
    for (Entry<String, String> map : labellist.entrySet()) {
      if (validEntry(map)) {
        this.dbQueryUtils.getEntityManager()
            .persist(DBEntityCreationUtil.createVLDB2ProjectLabel(map.getKey().trim(), map.getValue(), this.proRevId));
      }
    }

    this.dbQueryUtils.deleteLock(versId, this.maxRevId);
    SSDiCDMInterfaceLogger.logMessage("Total Error identified in creation - " + this.errorlist.size(),
        ILoggerAdapter.LEVEL_INFO, null);
    return getErrorlist();
  }

  /**
   * method to create a revision
   *
   * @param versId
   * @return
   * @throws Exception
   */
  private BigDecimal createRevision(final BigDecimal versId) throws SSDiCDMInterfaceException {
    BigDecimal newProRevId = null;
    try {
      boolean lockInserted = insertLock(versId, this.maxRevId);
      if (lockInserted) {
        newProRevId = this.dbQueryUtils.createNewRevision(versId, this.maxRevId);
        SSDiCDMInterfaceLogger.logMessage("New labellist revision created", ILoggerAdapter.LEVEL_INFO, null);

      }
      else {
        SSDiCDMInterfaceLogger.logMessage("wait time....", ILoggerAdapter.LEVEL_INFO, null);

        // to specify wait time here
        Thread.sleep(5000);
        createRevision(versId);
      }
      return newProRevId;
    }
    catch (Exception e) {
      throw ExceptionUtils.createAndThrowException(e, e.getLocalizedMessage(),
          SSDiCDMInterfaceErrorCodes.GENERAL_EXCEPTION, true);
    }
  }


  /**
   * to insert lock on to prevent parallel labellist creation
   *
   * @param revId
   * @param versId
   * @throws SSDiCDMInterfaceException Exception
   */
  private boolean insertLock(final BigDecimal versId, final BigDecimal revId) throws SSDiCDMInterfaceException {
    if (this.dbQueryUtils.insertLock(versId, revId, this.dbQueryUtils.getUserName()) == 0) {
      // if lock not acquired then 0 is returned
      String lockuser = this.dbQueryUtils.getLockHolder(versId, revId);
      String lockerr = "Lock could not be acquired for labellist. Lock is held by : " + lockuser;
      this.errorlist.add(lockerr);
      SSDiCDMInterfaceLogger.logMessage(lockerr, ILoggerAdapter.LEVEL_INFO, null);
      return false;
    }
    SSDiCDMInterfaceLogger.logMessage("lock acquired for labellist by user : " + this.dbQueryUtils.getUserName(),
        ILoggerAdapter.LEVEL_INFO, null);
    return true;
  }

  /**
   * @param compPkgBCs set
   * @param releaseUtils util
   * @param isCompli isCompliance Release
   * @param isQSSDOnlyRelease QSSD Only Check
   * @return SSDMessageOptions
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessageOptions setReleaseNodeConfig(final Set<ComPkgBcModel> compPkgBCs,
      final CreateSSDRelease releaseUtils, final boolean isCompli, final boolean isQSSDOnlyRelease)
      throws SSDiCDMInterfaceException {


    // labellist creation is done as a separate transaction
    // also helpfull in deleting the lock
    SSDMessageOptions nodeMsg = null;
    if (isCompli) {
      nodeMsg = releaseUtils.mapBCVersiontoSSDNodeForCompli(compPkgBCs, isQSSDOnlyRelease);
    }
    else {
      nodeMsg = releaseUtils.mapBCVersiontoSSDNode(new ArrayList<>(compPkgBCs));
    }
    SSDMessageOptions opts = initiateRelease(releaseUtils, nodeMsg, false);
    if (Objects.isNull(opts)) {
      return nodeMsg;
    }
    return opts;
  }


  /**
   * @param releaseUtils
   * @param ssdMsgOpt
   * @param nodeMsg
   * @throws SSDiCDMInterfaceException
   */
  private SSDMessageOptions initiateRelease(final CreateSSDRelease releaseUtils, final SSDMessageOptions nodeMsg,
      final boolean isNonSDOMSwRelease)
      throws SSDiCDMInterfaceException {
    if (nodeMsg.getSsdMessage().equals(SSDMessage.NODECONFIGSUCCESS)) {
      SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
      SSDMessage relMsg = releaseUtils.invokeRelease(isNonSDOMSwRelease);
      ssdMsgOpt.setSsdMessage(relMsg);
      if (!nodeMsg.getNoNodeBcList().isEmpty()) {
        ssdMsgOpt.setNoNodeBcList(nodeMsg.getNoNodeBcList());
      }
      return ssdMsgOpt;
    }
    return null;
  }

  /**
   * @param releaseUtils util
   * @param nodeList nodeList
   * @param isQSSDOnlyRelease QSSD Only Check
   * @return SSDMessageOptions
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessageOptions setReleaseNodeConfigForNonSDOMSWs(final CreateSSDRelease releaseUtils,
      final List<TempNonSDOMNodeList> nodeList, final boolean isQSSDOnlyRelease)
      throws SSDiCDMInterfaceException {
    // labellist creation is done as a separate transaction
    // also helpfull in deleting the lock
    SSDMessageOptions nodeMsg = releaseUtils.mapSSDNodeForRelease(nodeList, isQSSDOnlyRelease);
    SSDMessageOptions opts = initiateRelease(releaseUtils, nodeMsg, true);
    if (Objects.isNull(opts)) {
      return nodeMsg;
    }
    return opts;
  }
}
