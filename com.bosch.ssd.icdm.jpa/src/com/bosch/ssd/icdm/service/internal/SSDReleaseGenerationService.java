/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.internal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.bosch.ssd.icdm.entity.TempNonSDOMNodeList;
import com.bosch.ssd.icdm.exception.ExceptionUtils;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.FeaValModel;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDServiceMethod;
import com.bosch.ssd.icdm.service.utility.CreateSSDRelease;
import com.bosch.ssd.icdm.service.utility.DBQueryUtils;
import com.bosch.ssd.icdm.service.utility.ReleaseGeneration;
import com.bosch.ssd.icdm.service.utility.ServiceLogAndTransactionUtil;

/**
 * SSD Release Generation Service
 *
 * @author SSN9COB
 */
public class SSDReleaseGenerationService implements SSDServiceMethod {

  private final SSDNodeInfoAccessor ssdNodeInfo;
  private final DBQueryUtils dbQueryUtils;

  private final ReleaseGeneration releaseGenUtils;
  private CreateSSDRelease releaseUtils;

  /**
   * Constructor for SSD Release Service
   *
   * @param ssdNodeInfo Node Info
   */
  public SSDReleaseGenerationService(final SSDNodeInfoAccessor ssdNodeInfo) {
    this.ssdNodeInfo = ssdNodeInfo;
    this.dbQueryUtils = ssdNodeInfo.getDbQueryUtils();
    this.releaseGenUtils = new ReleaseGeneration(this.dbQueryUtils);
  }


  /**
   * Invoke the first step in Compliance Release
   *
   * @param labelList label list
   * @param compPkgBCs BC Mapping
   * @param isQSSDOnlyRelease TRUE When labellist contains only QSSD Parameters, else FALSE
   * @return node config status
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessageOptions invokeComplianceRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs, final boolean isQSSDOnlyRelease)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    try {
      if (createLabelListForRelease(labelList, ssdMsgOpt)) {
        this.releaseUtils = new CreateSSDRelease(this.releaseGenUtils.getProRevId(), this.ssdNodeInfo);
        ssdMsgOpt = this.releaseGenUtils.setReleaseNodeConfig(compPkgBCs, this.releaseUtils, true, isQSSDOnlyRelease);
      }
      else {
        return ssdMsgOpt;
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    // TODO : Commit commented for the issue in SSD File generation. To be checked with Bebith on usage of invokecompli
    // & contwithfeaval mathod as single service call

//    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
//        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
    return ssdMsgOpt;

  }


  /**
   * @param labelList
   * @param ssdMsgOpt
   * @throws SSDiCDMInterfaceException
   */
  private boolean createLabelListForRelease(final Map<String, String> labelList, final SSDMessageOptions ssdMsgOpt)
      throws SSDiCDMInterfaceException {
    List<String> errorList = this.releaseGenUtils.createLabelList(this.ssdNodeInfo.getCompliNodeId(), labelList);
    if (!errorList.isEmpty()) {
      // to Check if lock already exists. if so delete lock
      // this.labelListUtil.deleteLockIfExists(this.compliNodeId)
      ssdMsgOpt.setSsdMessage(SSDMessage.LABELLISTCREATIONFAILED.appendDescription(errorList.get(0)));
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);
      return false;
    }
    return true;
  }

  /**
   * Invoke the first step in Compliance Release for Non SDOM SW
   *
   * @param labelList label list
   * @param isQSSDOnlyRelease TRUE When labellist contains only QSSD Parameters, else FALSE
   * @return node config status
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessageOptions invokeComplianceReleaseForNonSDOMSWs(final Map<String, String> labelList,
      final boolean isQSSDOnlyRelease)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    try {
      // Step 1 - Create labellist
      if (createLabelListForRelease(labelList, ssdMsgOpt)) {
        // step 2 : create Release

        
      this.releaseUtils = new CreateSSDRelease(this.releaseGenUtils.getProRevId(), this.ssdNodeInfo);
 
        // Step 3 - Identify list of nodes from the rules
        List<TempNonSDOMNodeList> nodeList =
            getNodeListForCompliLabels(this.releaseGenUtils.getProRevId(), this.releaseUtils.getProReleaseId());
        // Step 4 - Assign Nodes to release
        ssdMsgOpt =
            this.releaseGenUtils.setReleaseNodeConfigForNonSDOMSWs(this.releaseUtils, nodeList, isQSSDOnlyRelease);
      }
      else {
        // Labellist creation failed msg
        return ssdMsgOpt;
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    return ssdMsgOpt;
  }

  /**
   * @return
   */
  private List<TempNonSDOMNodeList> getNodeListForCompliLabels(final BigDecimal proRevId, final BigDecimal proRelId)
      throws SSDiCDMInterfaceException {
    //  based on the availble labellist, check in DB for all matching QSSD & COMPLI Label and fetch the node details
    // where the rules are located
    return   this.dbQueryUtils.checkAndGetNodesForReleaseConfig(proRevId, proRelId);
    
  }


  /**
   *
   */
  private List<Object[]> getStubData() {
    List<Object[]> nodesList = new ArrayList<>();

    // Node 1
    Object[] node1 = new Object[3];
    node1[0] = "3528446410";
    node1[1] = "Common+Platform (SSD)+SSD Customer (Training)+SY45 SSD Testing+M5 Test";
    node1[2] = "3552643809";

    Object[] node2 = new Object[3];
    node2[0] = "3534818338";
    node2[1] = "Common+Platform (SSD)+SSD Customer (Training)+SY45 SSD Testing+VaLongRule Long Rule Test";
    node2[2] = "3534818341";

    nodesList.add(node1);
    nodesList.add(node2);
    return nodesList;
  }

  /**
  *
  */
  private List<Object[]> getiCDMStubData() {
    List<Object[]> nodesList = new ArrayList<>();

    // Node 1
    Object[] node1 = new Object[3];
    node1[0] = "3532524799";
    node1[1] =
        "Common+Platform (SSD)+DGS BC ExhMgT+TE31 ExhMgT_ComplianceSSD+11.x.x SSD rule for Diesel ExhMgT EGTCond";
    node1[2] = null;

    Object[] node2 = new Object[3];
    node2[0] = "6808787";
    node2[1] = "Common+Platform (SSD)+Device Encapsulation+SX99 IntDev+01_11.2.0 B_DE_ElecThrVlv.11.2.0";
    node2[2] = null;

    Object[] node3 = new Object[3];
    node3[0] = "8433683";
    node3[1] = "Common+Platform (SSD)+Device Encapsulation+SX99 IntDev+03_12.2.0 B_DE_ElecEGRVlv.12.2.0";
    node3[2] = null;

    Object[] node4 = new Object[3];
    node4[0] = "3532168667";
    node4[1] = "Common+Platform (SSD)+DGS BC ChrCtl+TD64 ChrCtlFCA+TMRBSD AirCtl_Mon ShutOff";
    node4[2] = null;

    Object[] node5 = new Object[3];
    node5[0] = "3531717915";
    node5[1] = "Common+Platform (SSD)+DGS BC SCRCat+SC38 SubC SCRCat+1.24.0 1.24.0 SCRCat";
    node5[2] = null;

    Object[] node6 = new Object[3];
    node6[0] = "4644213";
    node6[1] = "Common+Platform (SSD)+Device Encapsulation+SX99 IntDev+04_14.2.0 B_DE_AFS_HFM6.14.2.0";
    node6[2] = null;

    Object[] node7 = new Object[3];
    node7[0] = "3530080559";
    node7[1] = "Common+Platform (SSD)+AirSystem+TA00 AirMod+4.12.0 AirMod";
    node7[2] = null;

    nodesList.add(node1);
    nodesList.add(node2);
    nodesList.add(node3);
    nodesList.add(node4);
    nodesList.add(node5);
    nodesList.add(node6);
    nodesList.add(node7);
    return nodesList;
  }


  /**
   * Invoke the first step in Comp Pckg Release
   *
   * @param labelList list
   * @param compPkgBCs BC Mapping
   * @return msg
   * @throws SSDiCDMInterfaceException e
   */
  public SSDMessageOptions invokeCompPckgRelease(final Map<String, String> labelList,
      final Set<ComPkgBcModel> compPkgBCs)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    try {
      List<String> errorList = this.releaseGenUtils.createLabelList(this.ssdNodeInfo.getCompPkgNodeId(), labelList);
      if (!errorList.isEmpty()) {
        // to Check if lock already exists. if so delete lock
        // this.labelListUtil.deleteLockIfExists(this.compliNodeId)
        ssdMsgOpt.setSsdMessage(SSDMessage.LABELLISTCREATIONFAILED.appendDescription(errorList.get(0)));
      }
      else {
        this.releaseUtils = new CreateSSDRelease(this.releaseGenUtils.getProRevId(), this.ssdNodeInfo);
        ssdMsgOpt = this.releaseGenUtils.setReleaseNodeConfig(compPkgBCs, this.releaseUtils, false, false);
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
        getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
    return ssdMsgOpt;
  }

  /**
   * to get feature value selected from release
   *
   * @return - feature value from ssd for release
   */
  public Map<BigDecimal, FeaValModel> getFeaValueForSelection() {
    return this.releaseUtils.getFeaModel();

  }

  /**
   * Get error list from the labellist for the release
   *
   * @return List
   */
  public List<String> getErrorListFromLabellist() {
    return this.releaseGenUtils.getErrorlist();
  }

  /**
   * Continue next step of Release with FeaVal Selection
   *
   * @param feaValMap map
   * @return SSDMessage
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessage contReleaseWithfeaValSelection(final Map<BigDecimal, FeaValModel> feaValMap)
      throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    SSDMessage message;
    try {
      boolean relStatus = this.releaseUtils.processFeatureValue(feaValMap);

      if (!relStatus) {
        performCancel();
        message = SSDMessage.CANTCONTINUERELEASE;
        ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
            getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);

      }
      else {
        this.releaseUtils.deleteReleaseLock();
        message = SSDMessage.RELEASECREATED;
        ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
            getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), false);
      }
    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
    return message;

  }


  /**
   * Cancel Release
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  public void cancelRelease() throws SSDiCDMInterfaceException {
    ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
        getCurrentMethodName(), true, this.dbQueryUtils.getEntityManager(), false);
    try {
      performCancel();
      ServiceLogAndTransactionUtil.handleLoggingAndTransaction(SSDReleaseGenerationService.class.getSimpleName(),
          getCurrentMethodName(), false, this.dbQueryUtils.getEntityManager(), true);

    }
    catch (SSDiCDMInterfaceException e) {
      throw ExceptionUtils.rollbackAndThrowException(e, e.getDetailedErrorMessage(), e.getErrorCode(),
          this.dbQueryUtils.getEntityManager(), false);
    }
  }


  /**
   * @throws SSDiCDMInterfaceException
   */
  private void performCancel() throws SSDiCDMInterfaceException {
    if (this.releaseUtils != null) {
      this.releaseUtils.cancelRelease();
    }
  }

  /**
   * Get Pro Release Id
   *
   * @return release id
   */
  public BigDecimal getProReleaseID() {
    if (this.releaseUtils != null) {
      return this.releaseUtils.getProReleaseId();
    }
    return null;
  }

  /**
   * @return the releaseUtils
   */
  public CreateSSDRelease getReleaseUtils() {
    return this.releaseUtils;
  }

}
