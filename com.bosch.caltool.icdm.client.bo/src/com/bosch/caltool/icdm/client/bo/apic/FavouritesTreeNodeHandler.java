/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcFavourite;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.FavouritesServiceClient;

/**
 * @author dja7cob
 */
public class FavouritesTreeNodeHandler extends AbstractClientDataHandler {

  /**
   * Map of key - pidc ver id, value - pidc fav object
   */
  private Map<Long, PidcFavourite> favPidcMap = new HashMap<>();

  /**
   * Map of key - pidc fav id, value - pidc fav object
   */
  private final Map<Long, PidcFavourite> favIdPidcFavMap = new HashMap<>();

  /**
   * Map of key - pidc fav node id, value - pidc fav tree node object
   */
  private final Map<String, PidcTreeNode> favPidcNodeIdNodeMap = new HashMap<>();


  /**
   * Pidc tree node nodler instance
   */
  private final PidcTreeNodeHandler pidcTreeHandler;

  /**
   * @param pidcTreeHandler instance
   */
  public FavouritesTreeNodeHandler(final PidcTreeNodeHandler pidcTreeHandler) {
    super();
    this.pidcTreeHandler = pidcTreeHandler;
    getUserFavPidcs();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(this::checkForValidPidcVer, this::refreshPidcVersion, MODEL_TYPE.PIDC_VERSION);
    registerCns(this::checkForValidPidcFav, this::refreshPidc, MODEL_TYPE.PIDC);
    registerCns(this::checkForSdomPverName, this::refreshSdomPver, MODEL_TYPE.PROJ_ATTR);
    registerCns(this::checkForSdomPverNameVar, this::refreshSdomPverVar, MODEL_TYPE.VAR_ATTR);
    registerCns(this::checkForValidFavPidcNames, this::refreshAttrValues, MODEL_TYPE.ATTRIB_VALUE);
    registerCns(this::refreshPidcA2l, MODEL_TYPE.PIDC_A2L);
    registerCns(this::refreshA2lWPRespStatus, MODEL_TYPE.A2L_WP_RESPONSIBILITY_STATUS);
    registerCns(this::refreshCDR, MODEL_TYPE.CDR_RESULT);
    registerCns(this::refreshCDRVariant, MODEL_TYPE.CDR_RES_VARIANTS);
    registerCns(this::refreshA2lWp, MODEL_TYPE.A2L_WORK_PACKAGE);
    registerCns(this::refreshA2lResp, MODEL_TYPE.A2L_RESPONSIBILITY);
    registerCns(this::checkForValidUserFav, this::refreshFavourites, MODEL_TYPE.PIDC_FAVORITE);
    registerCns(this::checkForValidUserFavQnaire, this::refreshQnaires, MODEL_TYPE.RVW_QNAIRE_RESPONSE);
    registerCns(this::refreshQnaireRespVariants, MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT);
    registerCns(this::refreshPidcVariants, MODEL_TYPE.VARIANT);
    registerCns(this::refreshQnaireRespVersion, MODEL_TYPE.RVW_QNAIRE_RESP_VERSION);
    registerCns(this::refreshWpRespOfA2lStructureForChangeOfDefVersion, MODEL_TYPE.A2L_WP_DEFN_VERSION);

  }

  /**
   * This method is to refresh the WP RESP combinations under the A2L whenever theres a change in the version
   *
   * @param chDataInfoMap
   */
  public void refreshWpRespOfA2lStructureForChangeOfDefVersion(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        try {
          A2lWpDefnVersion wpDefnVersion = new A2lWpDefinitionVersionServiceClient().get(Long.valueOf(data.getObjId()));
          if (wpDefnVersion.isActive()) {
            PidcA2lServiceClient client = new PidcA2lServiceClient();
            PidcA2l pidcA2l = client.getById(wpDefnVersion.getPidcA2lId());
            this.pidcTreeHandler.updateA2lStructureModel(pidcA2l.getPidcVersId());
          }
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);

        }

      }
    }

  }

  /**
   * @param chDataInfoMap change info map
   */
  public void refreshA2lResp(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshResponsibilities(chDataInfoMap);
  }

  /**
   * @param chDataInfoMap change info map
   */
  public void refreshA2lWp(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshWorkpackages(chDataInfoMap);
  }

  /**
   * @param chDataInfoMap change info map
   */
  public void refreshA2lWPRespStatus(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshA2lWPRespStatus(chDataInfoMap);
  }

  /**
   * @param chDataInfoMap change info map
   */
  public void refreshQnaireRespVersion(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshQnaireRespVersion(chDataInfoMap);
  }

  /**
   * for update and delete of CDR
   */
  public void refreshPidcVariants(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshPidcVariants(chDataInfoMap);
  }

  /**
   * for update and delete of qnaire resp variants
   */
  public void refreshQnaireRespVariants(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshQnaireRespVariants(chDataInfoMap);
  }

  /**
   * for update and delete of CDR
   */
  public void refreshCDR(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshCDR(chDataInfoMap);
  }

  /**
   * @param chDataInfoMap
   */
  public void refreshCDRVariant(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshCDRVariant(chDataInfoMap);
  }

  /**
  *
  */
  public void refreshSdomPverVar(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshSdomPverVar(chDataInfoMap);
  }

  /**
  *
  */
  public void refreshSdomPver(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshSdomPver(chDataInfoMap);
  }

  /**
   * @param chData
   * @return
   */
  private boolean checkForSdomPverNameVar(final ChangeData<?> chData) {
    return this.pidcTreeHandler.checkForSdomPverNameVar(chData);
  }

  /**
   * @param chData
   * @return
   */
  private boolean checkForSdomPverName(final ChangeData<?> chData) {
    return this.pidcTreeHandler.checkForSdomPverName(chData);
  }


  /**
   * @param chData
   * @return
   */
  private boolean checkForValidUserFavQnaire(final ChangeData<?> chData) {
    if (chData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
      RvwQnaireResponse rvwQnaire = (RvwQnaireResponse) chData.getOldData();
      return this.favPidcMap.containsKey(rvwQnaire.getPidcVersId());
    }
    RvwQnaireResponse rvwQnaire = (RvwQnaireResponse) chData.getNewData();
    return this.favPidcMap.containsKey(rvwQnaire.getPidcVersId());
  }

  /**
   * @param chData
   */
  private void refreshPidcA2l(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshPidcA2l(chDataInfoMap);
  }

  /**
   * @param chData
   */
  private void refreshQnaires(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    this.pidcTreeHandler.refreshQnaires(chDataInfoMap);
  }

  /**
   * @param chData
   * @return
   */
  private boolean checkForValidPidcVer(final ChangeData<?> chData) {
    PidcVersion pidcVer = (PidcVersion) CnsUtils.getModel(chData);
    Set<Long> pidcIds = new HashSet<>();
    for (PidcFavourite fav : this.favPidcMap.values()) {
      pidcIds.add(fav.getPidcId());
    }
    return pidcIds.contains(pidcVer.getPidcId());
  }

  /**
   * @param chDataInfoMap
   */
  private void refreshPidcVersion(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    clearAllCollections();
    getUserFavPidcs();
  }

  /**
   *
   */
  private void clearAllCollections() {
    this.favPidcMap.clear();
    this.favIdPidcFavMap.clear();
    this.favPidcNodeIdNodeMap.clear();
  }


  /**
   * @param chData
   * @return
   */
  private boolean checkForValidPidcFav(final ChangeData<?> chData) {
    Pidc pidc = (Pidc) CnsUtils.getModel(chData);
    Set<Long> pidcIds = new HashSet<>();
    for (PidcFavourite fav : this.favPidcMap.values()) {
      pidcIds.add(fav.getPidcId());
    }
    return pidcIds.contains(pidc.getId());
  }

  /**
  *
  */
  private void refreshPidc(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    Set<Long> pidcIdsUpd = new HashSet<>();
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if (changeData.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        pidcIdsUpd.add(changeData.getObjId());
      }
    }
    if (CommonUtils.isNotEmpty(pidcIdsUpd)) {
      updatePidcFavInTree(pidcIdsUpd);
    }
  }


  /**
   * @param pidcIdsUpd
   */
  private void updatePidcFavInTree(final Set<Long> pidcIdsUpd) {
    try {
      Map<Long, PidcVersionInfo> pidcVerInfoMap =
          new PidcVersionServiceClient().getActiveVersionsWithStructure(pidcIdsUpd);
      Map<Long, PidcTreeNode> verIdTreenodeMap = new HashMap<>();
      verIdTreenodeMap.putAll(this.pidcTreeHandler.getPidcVerIdTreenodeMap());
      for (PidcVersionInfo verInfo : pidcVerInfoMap.values()) {
        for (PidcTreeNode pidcTreeNode : verIdTreenodeMap.values()) {
          if (pidcTreeNode.getPidc().getId().equals(verInfo.getPidc().getId())) {
            this.favPidcNodeIdNodeMap.remove(pidcTreeNode.getNodeId());
          }
        }
      }
      this.pidcTreeHandler.updatePidcChange(pidcVerInfoMap);
      for (Entry<Long, PidcVersionInfo> verInfo : pidcVerInfoMap.entrySet()) {
        PidcFavourite pidcFavourite = this.favPidcMap.get(verInfo.getKey());
        this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(verInfo.getKey()).setPidcFavourite(pidcFavourite);
        PidcTreeNode pidcNode = this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(verInfo.getKey());
        this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()).setPidcFavourite(pidcFavourite);
        this.favPidcNodeIdNodeMap.remove(pidcNode.getNodeId());
        this.favPidcNodeIdNodeMap.put(pidcNode.getNodeId(),
            this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()));
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param chData
   * @return
   */
  private boolean checkForValidUserFav(final ChangeData<?> chData) {
    PidcFavourite pidcFav = (PidcFavourite) CnsUtils.getModel(chData);
    try {
      Set<Long> favIds = new HashSet<>();
      Map<Long, PidcFavourite> pidcFavMap =
          new FavouritesServiceClient().getFavouritePidcForUser(new CurrentUserBO().getUserID());
      for (PidcFavourite fav : pidcFavMap.values()) {
        favIds.add(fav.getId());
      }
      return favIds.contains(pidcFav.getId()) || this.favIdPidcFavMap.containsKey(pidcFav.getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @param chData
   * @return
   */
  private boolean checkForValidFavPidcNames(final ChangeData<?> chData) {
    Set<Long> favPidcNameValIds = new HashSet<>();
    for (PidcTreeNode favPidcNode : this.favPidcNodeIdNodeMap.values()) {
      favPidcNameValIds.add(favPidcNode.getPidc().getNameValueId());
    }
    AttributeValue attrVal = (AttributeValue) CnsUtils.getModel(chData);
    Long attrId = attrVal.getAttributeId();
    ApicDataBO apicBO = new ApicDataBO();

    return favPidcNameValIds.contains(attrVal.getId()) && CommonUtils.isEqual(apicBO.getPidcNameAttrId(), attrId);
  }

  /**
  *
  */
  private void refreshAttrValues(final Map<Long, ChangeDataInfo> chDataInfoMap) {


    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        Pidc modPidc = new PidcServiceClient().getByNameValueId(data.getObjId());
        if (null != modPidc) {
          try {
            Map<Long, PidcVersionInfo> pidcVerInfoMap = new PidcVersionServiceClient()
                .getActiveVersionsWithStructure(new HashSet<>(Arrays.asList(modPidc.getId())));
            this.pidcTreeHandler.createPidcNodeRefresh(pidcVerInfoMap);
            PidcVersion pidcVersion = pidcVerInfoMap.values().iterator().next().getPidcVersion();
            if (this.favPidcMap.containsKey(pidcVersion.getId())) {
              this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcVersion.getId())
                  .setPidcFavourite(this.favPidcMap.get(pidcVersion.getId()));
              PidcTreeNode pidcNode = this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcVersion.getId());
              this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId())
                  .setPidcFavourite(this.favPidcMap.get(pidcVersion.getId()));
              this.favPidcNodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
            }
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }
        }
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }


  /**
  *
  */
  private void refreshFavourites(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if (changeData.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        addFavToTree(changeData);
      }
      else if (changeData.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        removeFavFromTree(changeData);
      }
    }
  }

  /**
   * @param changeData
   */
  private void addFavToTree(final ChangeDataInfo<?> changeData) {
    try {
      PidcFavourite newData = new FavouritesServiceClient().getById(changeData.getObjId());
      PidcTreeNode pidcNode = getFavNodeObj(newData);
      this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()).setPidcFavourite(newData);
      this.favPidcMap.put(pidcNode.getPidcVersion().getId(), newData);
      this.favIdPidcFavMap.put(newData.getId(), newData);
      this.favPidcNodeIdNodeMap.put(pidcNode.getNodeId(),
          this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param changeData
   */
  private void removeFavFromTree(final ChangeDataInfo<?> changeData) {
    PidcFavourite oldData = (PidcFavourite) changeData.getRemovedData();
    try {
      PidcTreeNode pidcNode = getFavNodeObj(oldData);
      this.favPidcMap.remove(pidcNode.getPidcVersion().getId());
      this.favIdPidcFavMap.remove(oldData.getId());
      this.favPidcNodeIdNodeMap.remove(pidcNode.getNodeId());
      this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcNode.getPidcVersion().getId()).setPidcFavourite(null);
      this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()).setPidcFavourite(null);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param newData
   * @return
   * @throws ApicWebServiceException
   */
  private PidcTreeNode getFavNodeObj(final PidcFavourite newData) throws ApicWebServiceException {
    Map<Long, PidcVersionInfo> activeVersionMap = new PidcVersionServiceClient()
        .getActiveVersionsWithStructure(new HashSet<>(Arrays.asList(newData.getPidcId())));
    return createFavPidcTreeNodeObj(activeVersionMap, activeVersionMap.keySet().iterator().next(), newData);
  }

  /**
   * @return
   */
  public void getUserFavPidcs() {
    try {
      this.favPidcMap.putAll(new FavouritesServiceClient().getFavouritePidcForUser(new CurrentUserBO().getUserID()));
      Set<Long> pidcIdSet = new HashSet<>();
      pidcIdSet.addAll(this.favPidcMap.values().stream().map(PidcFavourite::getPidcId).collect(Collectors.toSet()));
      if (CommonUtils.isNotEmpty(pidcIdSet)) {
        updatePidcFavInTree(pidcIdSet);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

    if (this.pidcTreeHandler.getPidcVerIdTreenodeMap().isEmpty() && (null == this.pidcTreeHandler.getRootNode())) {
      this.pidcTreeHandler.getPidcTreeRootNode();
    }
    for (Entry<Long, PidcFavourite> pidcFavEntry : this.favPidcMap.entrySet()) {
      PidcTreeNode pidcNode = this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcFavEntry.getKey());
      this.pidcTreeHandler.getPidcVerIdTreenodeMap().get(pidcFavEntry.getKey())
          .setPidcFavourite(pidcFavEntry.getValue());
      this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()).setPidcFavourite(pidcFavEntry.getValue());
      this.favPidcNodeIdNodeMap.put(pidcNode.getNodeId(),
          this.pidcTreeHandler.getNodeIdNodeMap().get(pidcNode.getNodeId()));
      this.favIdPidcFavMap.put(pidcFavEntry.getValue().getId(), pidcFavEntry.getValue());
    }
  }

  /**
   * @param activeVersionMap
   * @param activeVerId
   * @param pidcFavourite
   * @return
   */
  private PidcTreeNode createFavPidcTreeNodeObj(final Map<Long, PidcVersionInfo> activeVersionMap,
      final Long activeVerId, final PidcFavourite pidcFavourite) {
    PidcTreeNode pidcNode = new PidcTreeNode();
    PidcVersionInfo pidcVersionInfo = activeVersionMap.get(activeVerId);
    PidcVersion pidcVersion = pidcVersionInfo.getPidcVersion();
    pidcNode.setName(pidcVersion.getName());
    pidcNode.setPidcVerInfo(pidcVersionInfo);
    pidcNode.setPidcVersion(pidcVersion);
    pidcNode.setPidc(pidcVersionInfo.getPidc());
    pidcNode.setPidcFavourite(pidcFavourite);
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION);
    ApicDataBO apicBO = new ApicDataBO();
    int pidcStructMaxLvl = apicBO.getPidcStructMaxLvl();
    String parentLvlAttrNodeId = this.pidcTreeHandler.getParentLvlAttrNodeId(pidcVersionInfo, pidcStructMaxLvl);
    pidcNode.setParentNodeId(parentLvlAttrNodeId);
    pidcNode.setNodeId(
        parentLvlAttrNodeId + PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.PIDC_VER_PREFIX + activeVerId);
    return pidcNode;
  }

  /**
   * @return the favIdPidcFavMap
   */
  public Map<Long, PidcFavourite> getFavIdPidcFavMap() {
    return this.favIdPidcFavMap;
  }

  /**
   * @return the favPidcNodeIdNodeMap
   */
  public Map<String, PidcTreeNode> getFavPidcNodeIdNodeMap() {
    return this.favPidcNodeIdNodeMap;
  }


  /**
   * @return the favPidcMap
   */
  public Map<Long, PidcFavourite> getFavPidcMap() {
    return this.favPidcMap;
  }


  /**
   * @param favPidcMap the favPidcMap to set
   */
  public void setFavPidcMap(final Map<Long, PidcFavourite> favPidcMap) {
    this.favPidcMap = favPidcMap;
  }
}
