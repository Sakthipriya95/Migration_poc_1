/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.ssd.icdm.service.utility;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.ssd.icdm.entity.TempLdb2Feaval;
import com.bosch.ssd.icdm.entity.TempNonSDOMNodeList;
import com.bosch.ssd.icdm.entity.VLdb2ConfigRelease;
import com.bosch.ssd.icdm.exception.SSDiCDMInterfaceException;
import com.bosch.ssd.icdm.logger.SSDiCDMInterfaceLogger;
import com.bosch.ssd.icdm.model.ComPkgBcModel;
import com.bosch.ssd.icdm.model.ConfiguredNode;
import com.bosch.ssd.icdm.model.FeaValModel;
import com.bosch.ssd.icdm.model.SSDConfigEnums.SSDConfigParams;
import com.bosch.ssd.icdm.model.SSDMessage;
import com.bosch.ssd.icdm.model.SSDMessageOptions;
import com.bosch.ssd.icdm.service.internal.servinterface.SSDNodeInfoAccessor;

/**
 * Revision History<br>
 * Version Date Name Description<br>
 * 3.2.0 11-08-2014 Renuka SSD-276 to collect the nodes configured for given bc varinats
 */
/**
 * @author apl1cob for creating new release from the interface
 */
public class CreateSSDRelease {

  /**
   * Number of levels in which nodes are configured.
   */
  public static final int NO_OF_LEVELS = 5;
  /**
   * max release id is one becuase only one release is done for the newly created labellist
   */
  private static final BigDecimal MAX_REL_ID = BigDecimal.ONE;

  // Compliance functionality
  // SSD-399
  private final List<ConfiguredNode> compliBCNodes = new ArrayList<>();


  /**
   * Has all the configured nodes
   */
  private final List<List<ConfiguredNode>> bcNodestoConfigure = new ArrayList<>(5);


  /**
   * newly created release id
   */
  private BigDecimal proReleaseId;

  /**
   * to store the node info, if there is a problem with valid labellist
   */
  private ConfiguredNode errNode;

  /**
   * to check if feature value could be continued
   */
  private boolean toContinueRelease = false;

  /**
   * feature value model populated
   */
  private HashMap<BigDecimal, FeaValModel> feaModel;
  /*
   * Logged in username
   */
  private final String userName;
  private final SSDNodeInfoAccessor ssdNodeInfo;
  private final DBQueryUtils dbQueryUtils;
  private final BigDecimal proRevId;

  /**
   * @param proRevId - label list id
   * @param ssdNodeInfo utils
   */
  public CreateSSDRelease(final BigDecimal proRevId, final SSDNodeInfoAccessor ssdNodeInfo) {
    this.proRevId = proRevId;
    this.userName = ssdNodeInfo.getUserName();
    this.dbQueryUtils = ssdNodeInfo.getDbQueryUtils();
    this.ssdNodeInfo = ssdNodeInfo;
  }

  /**
   * @return the errNode
   */
  public ConfiguredNode getErrNode() {
    return this.errNode;
  }


  /**
   * @return the feaModel
   */
  public Map<BigDecimal, FeaValModel> getFeaModel() {
    return this.feaModel;
  }


  /**
   * @return the proRevId
   */
  public BigDecimal getProRevId() {
    return this.proRevId;
  }


  /**
   * @return the proReleaseId
   */
  public BigDecimal getProReleaseId() {
    return this.proReleaseId;
  }

  /**
   * to invoke release from service
   *
   * @param isNonSDOMSwRelease isNonSDOMSwRelease
   * @return - message on failure corrresponding data to be retrieved
   * @throws SSDiCDMInterfaceException exception
   */
  public SSDMessage invokeRelease(final boolean isNonSDOMSwRelease) throws SSDiCDMInterfaceException {
    this.toContinueRelease = false;
    if (this.dbQueryUtils.insertLock(this.proRevId, CreateSSDRelease.MAX_REL_ID, this.userName) == 0) {
      SSDiCDMInterfaceLogger.logMessage("lock could not be acquired for release creation", ILoggerAdapter.LEVEL_INFO,
          null);
      return SSDMessage.FAILEDTOLOCK;
    }
    if (createRelease(isNonSDOMSwRelease) == null) {
      this.toContinueRelease = true;
      return SSDMessage.CONTINUERELEASE;
    }
    return SSDMessage.VALIDLISTNOTFOUND;
  }

  /**
   * To create release for the set label list
   *
   * @return -
   * @throws SSDiCDMInterfaceException exception
   */
  private ConfiguredNode createRelease(final boolean isNonSDOMSwRelease) throws SSDiCDMInterfaceException {

    SSDiCDMInterfaceLogger.logMessage("lock acquired for release creation", ILoggerAdapter.LEVEL_INFO, null);
    this.proReleaseId =
        this.dbQueryUtils.createReleaseEntry(this.proRevId, CreateSSDRelease.MAX_REL_ID, isNonSDOMSwRelease);
    if (this.ssdNodeInfo.getCompliNodeId() != null) {
      this.errNode = processConfigureNodesForCompli();
    }
    else {
      this.errNode = processConfigureNodes();
    }
    // null check because, the above methods return configured node if error. if no error then null
    if (this.errNode == null) {
      processRelease();
    }
    return this.errNode;
  }

  /**
   * logic to proceed with release
   *
   * @throws SSDiCDMInterfaceException exception
   */
  private void processRelease() throws SSDiCDMInterfaceException {
    boolean result = this.dbQueryUtils.hasPopulated(this.proRevId, this.proReleaseId);
    SSDiCDMInterfaceLogger.logMessage("Populated " + result, ILoggerAdapter.LEVEL_INFO, null);
    List<Object[]> feaValList = this.dbQueryUtils.getFeaValForReleaseProcess(this.proReleaseId);
    SSDiCDMInterfaceLogger.logMessage("Feature value size " + feaValList.size(), ILoggerAdapter.LEVEL_INFO, null);
    populateFeaValModel(feaValList);
    // delete lock - next steps with fea val

  }

  /**
   * to process the feature value selection
   *
   * @param feaValMap -
   * @return -
   * @throws SSDiCDMInterfaceException Exception
   */
  public boolean processFeatureValue(final Map<BigDecimal, FeaValModel> feaValMap) throws SSDiCDMInterfaceException {
    if (this.toContinueRelease) {
      this.dbQueryUtils.deleteTempFeaTable(this.proReleaseId);
      for (Map.Entry<BigDecimal, FeaValModel> entry : feaValMap.entrySet()) {

        FeaValModel temp = entry.getValue();
        TempLdb2Feaval fea = new TempLdb2Feaval();
        fea.setFeatureId(temp.getFeatureId());
        fea.setRelId(this.proReleaseId);

        // release should not be continued if the feature value selection is nto complete
        if (temp.getSelValueIdFromIcdm() == null) {
          return false;
        }

        fea.setValueId(temp.getSelValueIdFromIcdm());
        this.dbQueryUtils.getEntityManager().persist(fea);
      }
      this.dbQueryUtils.getEntityManager().flush();
      // ALM-305433
      if (this.ssdNodeInfo.getCompliNodeId() != null) {
        String result = this.dbQueryUtils.callReleaseWrapperCompli(this.proRevId, this.proReleaseId);
        // When the procedure call return 'N', there is an error and false must be returned
        if (result.equalsIgnoreCase("N")) {
          return false;
        }
      }
      else {
        this.dbQueryUtils.callReleaseWrapper(this.proRevId, this.proReleaseId);
      }
      return true;
    }
    return false;
  }

  /**
   * to populate results from ssd into a model for icdm value selection
   *
   * @param feaValList
   * @return - populated model from ssd
   */
  private Map<BigDecimal, FeaValModel> populateFeaValModel(final List<Object[]> feaValList) {
    this.feaModel = new HashMap<>();
    BigDecimal featureId;
    BigDecimal valueId;


    // feature id - 0 , value-id -3
    for (Object[] temp : feaValList) {
      featureId = (BigDecimal) temp[0];
      valueId = (BigDecimal) temp[2];

      if (this.feaModel.containsKey(featureId)) {
        this.feaModel.get(featureId).addValueIdList(valueId);
      }
      else {
        FeaValModel model = new FeaValModel();
        model.setFeatureId(featureId);
        List<BigDecimal> valueList = new ArrayList<>();
        valueList.add(valueId);
        model.setValueIdList(valueList);


        this.feaModel.put(featureId, model);
      }
    }
    return this.feaModel;
  }

  /**
   * to remove lock created for release
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  public void deleteReleaseLock() throws SSDiCDMInterfaceException {
    this.dbQueryUtils.deleteLock(this.proRevId, CreateSSDRelease.MAX_REL_ID);
  }

  /**
   * to configure nodes and get details
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  private ConfiguredNode processConfigureNodes() throws SSDiCDMInterfaceException {
    int lvlint = 1;
    int swPos = 1;
    for (List<ConfiguredNode> nodeLvl : this.bcNodestoConfigure) {
      BigDecimal level = new BigDecimal(lvlint);
      if (lvlint == 1) {
        setMasterNode(false);
        swPos = 2;
      }
      for (ConfiguredNode configuredNode : nodeLvl) {
        configuredNode.setProRevId(this.dbQueryUtils.getValidListforNode(configuredNode.getNodeId()));
        if (configuredNode.getProRevId() == null) {
          return configuredNode;
        }
        persistConfigRelease(swPos, level, configuredNode);
        swPos++;
      }
      lvlint++;
    }
    // To add the QSSD Node to all the releases by default. swPos is 1 by default for level 5 node
    addQSSDNode();

    this.dbQueryUtils.getEntityManager().flush();

    return null;
  }

  /**
   * @param swPos
   * @param level
   * @param configuredNode
   */
  private void persistConfigRelease(final int swPos, final BigDecimal level, final ConfiguredNode configuredNode) {
    // to be added in vldb2configrelease
    VLdb2ConfigRelease config = new VLdb2ConfigRelease();
    config.setProRelId(this.proReleaseId);
    config.setSwLevel(level);
    config.setSwPosition(new BigDecimal(swPos));
    config.setSwProRevId(configuredNode.getProRevId());
    config.setSwVersionId(configuredNode.getNodeId());
    this.dbQueryUtils.getEntityManager().persist(config);
  }

  /**
   * This method will add the qssd node to all the releases at the end. <br>
   * last node in level 5
   *
   * @param swPos
   * @throws SSDiCDMInterfaceException Exception
   */
  private void addQSSDNode() throws SSDiCDMInterfaceException {
    // TO add the Q-SSD node at the end level 5 always.
    BigDecimal level = new BigDecimal(5);
    // QSSD node is the first node in level 5. so SWPOS is 1; ALM-526562
    BigDecimal swPos = new BigDecimal(1);

    // To get the Q-SSD Node stored in PROG_parameter table
    BigDecimal qssdNodeId = new BigDecimal(this.dbQueryUtils.getConfigValue(SSDConfigParams.QSSD_REVISION_NODE_ID));

    VLdb2ConfigRelease config = new VLdb2ConfigRelease();
    config.setProRelId(this.proReleaseId);

    // Level should always be 5 as defined above
    config.setSwLevel(level);
    // Position is as defined above
    config.setSwPosition(swPos);
    // Get the labellist id of the qssd node
    config.setSwProRevId(this.dbQueryUtils.getProRevId(qssdNodeId));
    config.setSwVersionId(qssdNodeId);

    this.dbQueryUtils.getEntityManager().persist(config);
  }

  /**
   * SSD-399 to configure nodes and get details for COMPLIANCE
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  private ConfiguredNode processConfigureNodesForCompli() throws SSDiCDMInterfaceException {
    int lvlint = 1;
    BigDecimal level = new BigDecimal(lvlint);
    int swPos = 1;
    if (lvlint == 1) {
      setMasterNode(true);
      swPos = 2;
    }
    for (ConfiguredNode configuredNodeObj : this.compliBCNodes) {
      configuredNodeObj.setProRevId(this.dbQueryUtils.getValidListforNode(configuredNodeObj.getNodeId()));
      // if null, Return node
      if (configuredNodeObj.getProRevId() == null) {
        return configuredNodeObj;
      }
      persistConfigRelease(swPos, level, configuredNodeObj);
      swPos++;
    }
    // To add the QSSD Node to all the releases by default. swPos is 1 by default for level 5 node
    addQSSDNode();

    this.dbQueryUtils.getEntityManager().flush();

    return null;
  }

  /**
   *
   */
  private void setMasterNode(final boolean isCompliRelease) {
    VLdb2ConfigRelease config = new VLdb2ConfigRelease();
    config.setProRelId(this.proReleaseId);
    config.setSwLevel(BigDecimal.ONE);
    config.setSwPosition(BigDecimal.ONE);
    config.setSwProRevId(this.proRevId);
    // if else condition put in order to set the component or compliance node Id based on the release call either from
    // component or compliance
    // Refer SSD-399

    config.setSwVersionId(isCompliRelease ? this.ssdNodeInfo.getCompliNodeId() : this.ssdNodeInfo.getCompPkgNodeId());

    this.dbQueryUtils.getEntityManager().persist(config);
  }

  /**
   * Method provides the nodes configured in different levels
   *
   * @param variantNodes - list of bc variant,name and level
   * @return - message whether node configuration success or failure
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessageOptions mapBCVersiontoSSDNode(final List<ComPkgBcModel> variantNodes)
      throws SSDiCDMInterfaceException {
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    // set to check nodes
    HashSet<BigDecimal> nodeSet = new HashSet<>();

    for (int i = 0; i < NO_OF_LEVELS; i++) {
      List<ConfiguredNode> temp = new ArrayList<>();
      this.bcNodestoConfigure.add(temp);
    }

    for (ComPkgBcModel comPkgBcModel : variantNodes) {
      List<Object[]> nodeList = this.dbQueryUtils.getBcNodes(comPkgBcModel.getBcName(), comPkgBcModel.getBvVersion());
      if (nodeList.isEmpty()) {
        SSDiCDMInterfaceLogger.logMessage(
            SSDMessage.SSDNODEMISSING + " " + comPkgBcModel.getBcName() + "," + comPkgBcModel.getBvVersion(),
            ILoggerAdapter.LEVEL_DEBUG, null);
        ssdMsgOpt.setSsdMessage(SSDMessage.SSDNODEMISSING);
        return ssdMsgOpt;
      }
      for (Object[] obj : nodeList) {
        if (nodeSet.add(new BigDecimal(obj[0].toString()))) {
          ConfiguredNode node = new ConfiguredNode();
          node.setNodeId(new BigDecimal(obj[0].toString()));
          node.setNodeScope(obj[1].toString());
          this.bcNodestoConfigure.get(comPkgBcModel.getLevel().intValue() - 1).add(node);
        }
      }
    }
    ssdMsgOpt.setSsdMessage(SSDMessage.NODECONFIGSUCCESS);
    return ssdMsgOpt;

  }


  /**
   * SSD-399 Method provides the nodes configured in different levels for compliance
   *
   * @param variantNodes - list of bc variant,name and level
   * @param isOnlyQSSDParameters QSSD Only Check
   * @return - message whether node configuration success or failure
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessageOptions mapBCVersiontoSSDNodeForCompli(final Set<ComPkgBcModel> variantNodes,
      final boolean isOnlyQSSDParameters)
      throws SSDiCDMInterfaceException {
    // ALM-294077-All inputs w.r.to SSDMessageOptions are related to this ticket
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    if (isOnlyQSSDParameters && Objects.nonNull(variantNodes) && variantNodes.isEmpty()) {
      ssdMsgOpt.setSsdMessage(SSDMessage.NODECONFIGSUCCESS);
      return ssdMsgOpt;
    }
    ArrayList<String> missingNodeBCList = new ArrayList<>();
    for (ComPkgBcModel comPkgBcModel : variantNodes) {
      List<Object[]> nodeList = this.dbQueryUtils.getBcNodes(comPkgBcModel.getBcName(), comPkgBcModel.getBvVersion());
      if (nodeList.isEmpty()) {
        SSDiCDMInterfaceLogger.logMessage(
            SSDMessage.SSDNODEMISSING + " " + comPkgBcModel.getBcName() + "," + comPkgBcModel.getBvVersion(),
            ILoggerAdapter.LEVEL_DEBUG, null);
        missingNodeBCList.add(comPkgBcModel.getBcName() + "," + comPkgBcModel.getBvVersion());

        // return SSDMessage.SSDNODEMISSING
      }
      for (Object[] obj : nodeList) {
        ConfiguredNode node = new ConfiguredNode();
        node.setNodeId(new BigDecimal(obj[0].toString()));
        node.setNodeScope(obj[1].toString());
        if (!this.compliBCNodes.contains(node)) {
          this.compliBCNodes.add(node);
        }
      }
    }
    if (this.compliBCNodes.isEmpty()) {
      ssdMsgOpt.setSsdMessage(SSDMessage.SSDNODEMISSING);
      ssdMsgOpt.setNoNodeBcList(missingNodeBCList);
      return ssdMsgOpt;
    }
    // To check why this if block is created - TODO (looks meaningless)
    if (!missingNodeBCList.isEmpty()) {
      ssdMsgOpt.setSsdMessage(SSDMessage.NODECONFIGSUCCESS);
      ssdMsgOpt.setNoNodeBcList(missingNodeBCList);
      return ssdMsgOpt;
    }

    ssdMsgOpt.setSsdMessage(SSDMessage.NODECONFIGSUCCESS);
    return ssdMsgOpt;

  }

  /**
   * @param nodeList - list of nodes
   * @param isOnlyQSSDParameters QSSD Only Check
   * @return - message whether node configuration success or failure
   * @throws SSDiCDMInterfaceException Exception
   */
  public SSDMessageOptions mapSSDNodeForRelease(final List<TempNonSDOMNodeList> nodeList, final boolean isOnlyQSSDParameters)
      throws SSDiCDMInterfaceException {
    // ALM-294077-All inputs w.r.to SSDMessageOptions are related to this ticket
    SSDMessageOptions ssdMsgOpt = new SSDMessageOptions();
    if (isOnlyQSSDParameters && Objects.nonNull(nodeList) && nodeList.isEmpty()) {
      ssdMsgOpt.setSsdMessage(SSDMessage.NODECONFIGSUCCESS);
      return ssdMsgOpt;
    }
    for (TempNonSDOMNodeList obj : nodeList) {
      ConfiguredNode node = new ConfiguredNode();
      node.setNodeId(obj.getNodeId());
      node.setNodeScope(obj.getNodeScope());
      node.setProRevId(obj.getProRevID());
      if (!this.compliBCNodes.contains(node)) {
        this.compliBCNodes.add(node);
      }
    }
    ssdMsgOpt.setSsdMessage(SSDMessage.NODECONFIGSUCCESS);
    return ssdMsgOpt;

  }

  /**
   * to cancel the release means to remove the lock and rollback transaction
   *
   * @throws SSDiCDMInterfaceException Exception
   */
  public void cancelRelease() throws SSDiCDMInterfaceException {
    deleteReleaseLock();

  }

  /**
   * to cancel the release means to remove the lock and rollback transaction
   *
   * @throws SSDiCDMInterfaceException Exception Making class deprecated since it is not called anywhere
   */
  @Deprecated
  private void saveRelease() throws SSDiCDMInterfaceException {
    deleteReleaseLock();
//    SSDEntityManagerUtil.handleTransaction(TransactionState.TRANSACTION_COMMIT, this.dbQueryUtils.getEntityManager())
  }

}
