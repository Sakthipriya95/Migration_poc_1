/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.cdr.RvwResEditorInputData;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.a2l.PidcA2lTreeStructureModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcQnaireInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSdomA2lInfo;
import com.bosch.caltool.icdm.model.apic.pidc.PidcTreeNodeChildren;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionInfo;
import com.bosch.caltool.icdm.model.apic.pidc.SdomPVER;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVariant;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWorkPackageServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpDefinitionVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2lWpResponsibilityStatusServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.PidcA2lTreeStructureServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcTreeViewServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionAttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireRespVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwQnaireResponseServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidcTreeNodeHandler extends AbstractClientDataHandler {

  /**
   * Pidc tree root node ID
   */
  public static final String ROOT_NODE_ID = "VL0";

  /**
   * Value ID Prefix
   */
  public static final String VAL_PREFIX = "VL";

  /**
   * PIDC version ID Prefix
   */
  public static final String PIDC_VER_PREFIX = "PVER";

  /**
   * Other PIDC version ID Prefix
   */
  public static final String OTHER_VER_PREFIX = "OTHVER";

  /**
   * Separator
   */
  public static final String LEVEL_SEPARATOR = "__";

  /**
   * Sdom Pver node Id Prefix
   */
  public static final String SDOM_PVER_PREFIX = "SDOM";

  /**
   * Pidc A2l Id Prefix
   */
  public static final String PIDC_A2L_PREFIX = "A2L";

  /**
   * WP group node Id Prefix
   */
  public static final String WP_GRP_PVER_PREFIX = "WPGRP";

  /**
   * Review result node Id Prefix
   */
  public static final String CDR_PREFIX = "REVRES";
  /**
   * Review result node Id Prefix
   */
  public static final String CDR_VARRES_PREFIX = "REVVARRES";
  /**
   * Questionnaire variant Id prefix
   */
  public static final String QNAIRE_VAR_PVER_PREFIX = "QNAIREVAR";
  /**
   * pidc a2l variant Id prefix
   */
  public static final String PIDC_A2L_VAR_PVER_PREFIX = "PIDCA2LVAR";
  /**
   * Questionnaire response Id prefix
   */
  public static final String QNAIRE_RESP_PVER_PREFIX = "QNAIRERESP";

  /**
   * CDR variant Id prefix
   */
  public static final String CDR_VAR_PREFIX = "CDRVAR";

  private static final String PIDC_VAR_PREFIX = "PIDCVAR";

  private PidcTreeNode rootTreeNode;

  private PidcTreeNode selectedNode;

  private String oldActiveVerNodeId;

  private boolean isVarNodeEnabled;

  private boolean isFilterText;

  /**
   * Map of key - Pidc tree node Id ; value - PidcTreeNode object
   */
  private Map<String, PidcTreeNode> nodeIdNodeMap = new HashMap<>();

  /**
   * Map of key - parent Pidc tree node Id ; value - Set of child nodes
   */
  private Map<String, SortedSet<PidcTreeNode>> parentIdChildNodesMap = new HashMap<>();

  /**
   * Map of key - pidc version id; value - corresponding pidc tree node object
   */
  private final Map<Long, PidcTreeNode> pidcVerIdTreenodeMap = new HashMap<>();

  /**
   * No variant node name
   */
  public static final String NO_VARIANT = "<NO-VARIANT>";

  public static final String CDR_OTHER_SCOPE = "OTHERSCOPE";

  public static final String RESP_PREFIX = "RESP";

  public static final String RESP_WP_PREFIX = "RESPWP";

  /**
   */
  public PidcTreeNodeHandler() {
    super();
  }

  /**
   * @param isPidcTreeView if invoked from pidc tree view, set to true
   */
  public PidcTreeNodeHandler(final boolean isPidcTreeView) {
    super();
    if (isPidcTreeView) {
      this.rootTreeNode = getPidcTreeRootNode();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(this::refreshWorkpackages, MODEL_TYPE.A2L_WORK_PACKAGE);
    registerCns(this::refreshResponsibilities, MODEL_TYPE.A2L_RESPONSIBILITY);
    registerCns(this::refreshA2lWPRespStatus, MODEL_TYPE.A2L_WP_RESPONSIBILITY_STATUS);
    registerCns(this::mergeNewPidcVerWithTree, MODEL_TYPE.PIDC_VERSION);
    registerCns(this::refreshPidc, MODEL_TYPE.PIDC);
    registerCns(this::checkForSdomPverName, this::refreshSdomPver, MODEL_TYPE.PROJ_ATTR);
    registerCns(this::checkForSdomPverNameVar, this::refreshSdomPverVar, MODEL_TYPE.VAR_ATTR);
    registerCns(this::checkForValidPidcNames, this::refreshAttrValue, MODEL_TYPE.ATTRIB_VALUE);
    registerCns(this::refreshPidcA2l, MODEL_TYPE.PIDC_A2L);
    registerCns(this::refreshCDR, MODEL_TYPE.CDR_RESULT);
    registerCns(this::refreshCDRVariant, MODEL_TYPE.CDR_RES_VARIANTS);
    registerCns(this::refreshQnaires, MODEL_TYPE.RVW_QNAIRE_RESPONSE);
    registerCns(this::refreshPidcVariants, MODEL_TYPE.VARIANT);
    registerCns(this::refreshQnaireRespVariants, MODEL_TYPE.RVW_QNAIRE_RESP_VARIANT);
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
          PidcA2lServiceClient client = new PidcA2lServiceClient();
          if (wpDefnVersion.isActive()) {
            PidcA2l pidcA2l = client.getById(wpDefnVersion.getPidcA2lId());
            updateA2lStructureModel(pidcA2l.getPidcVersId());
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
  public void refreshQnaireRespVersion(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.CREATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        try {
          RvwQnaireRespVersion rvwQnaireRespVers = new RvwQnaireRespVersionServiceClient().getById(data.getObjId());
          List<RvwQnaireRespVariant> rvwQnaireRespVariantList =
              new RvwQnaireRespVariantServiceClient().getRvwQnaireRespVariantList(rvwQnaireRespVers.getQnaireRespId());
          PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(rvwQnaireRespVariantList.get(0).getPidcVersId());
          String nodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR +
              PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + pidcNode.getPidcVersion().getId();
          PidcTreeNode pidcVerNode = this.nodeIdNodeMap.get(nodeId);
          // status and tooltip details will be taken from latest(newly created or updated) working set, so updating the
          // map with working
          // set for the
          // corresponding ques resp id
          if ((null != pidcVerNode) &&
              CommonUtils.isEqual(rvwQnaireRespVers.getRevNum(), CDRConstants.WORKING_SET_REV_NUM)) {
            pidcVerNode.getQnaireInfo().getRvwQnaireRespVersMap().put(rvwQnaireRespVers.getQnaireRespId(),
                rvwQnaireRespVers);
          }
          // to add the new RvwQnaireRespVersion in new a2l structure model
          createOrUpdateRvwQnaireRespVerInA2lStructureModel(rvwQnaireRespVers, rvwQnaireRespVariantList);

        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
        }

      }
    }
  }

  /**
   * @param rvwQnaireRespVers
   * @param rvwQnaireRespVariantList
   */
  private void createOrUpdateRvwQnaireRespVerInA2lStructureModel(final RvwQnaireRespVersion rvwQnaireRespVers,
      final List<RvwQnaireRespVariant> rvwQnaireRespVariantList) {
    // for updating A2lStructureModel in new a2l structures
    // constructing and fetching the a2l node ids
    Set<PidcTreeNode> allA2lNodesForPidcVersion =
        getAllA2lNodesForPidcVersion(rvwQnaireRespVariantList.get(0).getPidcVersId());
    // Update Qnaire response in getA2lStructureModel Object in pidctreenode
    for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
      // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
      if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel()) &&
          CommonUtils.isEqual(rvwQnaireRespVers.getRevNum(), CDRConstants.WORKING_SET_REV_NUM)) {
        a2lPidcTreeNode.getA2lStructureModel().getRvwQnaireRespVersMap().put(rvwQnaireRespVers.getQnaireRespId(),
            rvwQnaireRespVers);
      }
    }
  }

  /**
   * Method to refresh Review Qnaire Response Variants
   *
   * @param chDataInfoMap as input
   */
  public void refreshQnaireRespVariants(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        refreshQnaireRespVariantsForCreate(data);
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        RvwQnaireRespVariant qnaireRespVar = (RvwQnaireRespVariant) data.getRemovedData();
        PidcTreeNode pidcVerNode = getQnaireTitleNode(qnaireRespVar.getPidcVersId());
        if ((null != pidcVerNode) && (null != pidcVerNode.getQnaireInfo())) {
          pidcVerNode.getQnaireInfo().getVarRespWpQniareMap()
              .get(qnaireRespVar.getVariantId() == null ? ApicConstants.NO_VARIANT_ID : qnaireRespVar.getVariantId())
              .get(qnaireRespVar.getA2lRespId()).get(qnaireRespVar.getA2lWpId())
              .remove(qnaireRespVar.getQnaireRespId());
        }
        // method to remove the QnaireResp in A2lStructureModel
        addOrRemoveQnaireRespInA2lStructureModel(qnaireRespVar, false);
      }
    }
  }

  /**
   * @param data
   */
  private void refreshQnaireRespVariantsForCreate(final ChangeDataInfo data) {
    RvwQnaireRespVariantServiceClient qnaireRespvarServiceClient = new RvwQnaireRespVariantServiceClient();
    try {
      RvwQnaireRespVariant rvwQnaireRespVariant = qnaireRespvarServiceClient.getRvwQnaireRespVariant(data.getObjId());

      PidcTreeNode pidcVerNode = getQnaireTitleNode(rvwQnaireRespVariant.getPidcVersId());
      if ((null != pidcVerNode) && (null != pidcVerNode.getQnaireInfo())) {
        pidcVerNode.getQnaireInfo().getVarRespWpQniareMap()
            .get(rvwQnaireRespVariant.getVariantId() == null ? ApicConstants.NO_VARIANT_ID
                : rvwQnaireRespVariant.getVariantId())
            .get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
            .add(rvwQnaireRespVariant.getId());
      }
      // method to create Or Update the QnaireResp in A2lStructureModel
      addOrRemoveQnaireRespInA2lStructureModel(rvwQnaireRespVariant, true);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param qnaireRespVar as RvwQnaireRespVariant
   * @param addValueFlag as boolean value
   */
  private void addOrRemoveQnaireRespInA2lStructureModel(final RvwQnaireRespVariant qnaireRespVar,
      final boolean addValueFlag) {
    // for updating A2lStructureModel in new a2l structures
    // constructing and fetching the a2l node ids
    Set<PidcTreeNode> allA2lNodesForPidcVersion = getAllA2lNodesForPidcVersion(qnaireRespVar.getPidcVersId());
    // Update Qnaire response in getA2lStructureModel Object in pidctreenode
    for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
      // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
      if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel())) {
        Long qnaireRespId = qnaireRespVar.getQnaireRespId();
        // qnaireRespId is Null for Simplified Qnaire
        if (CommonUtils.isNotNull(qnaireRespId)) {
          RvwQnaireResponse rvwQnaireResponse =
              a2lPidcTreeNode.getA2lStructureModel().getRvwQnaireRespMap().get(qnaireRespId);
          Map<Long, Map<Long, Set<Long>>> varRespWpQnaireMap =
              a2lPidcTreeNode.getA2lStructureModel().getVarRespWpQniareMap().get(
                  qnaireRespVar.getVariantId() == null ? ApicConstants.NO_VARIANT_ID : qnaireRespVar.getVariantId());
          addOrRemoveQnaireRespFromMapForA2lStructure(qnaireRespVar, addValueFlag, rvwQnaireResponse,
              varRespWpQnaireMap);
        }
      }
    }
  }

  /**
   * @param qnaireRespVar
   * @param addValueFlag
   * @param rvwQnaireResponse
   * @param varRespWpQnaireMap
   */
  private void addOrRemoveQnaireRespFromMapForA2lStructure(final RvwQnaireRespVariant qnaireRespVar,
      final boolean addValueFlag, final RvwQnaireResponse rvwQnaireResponse,
      final Map<Long, Map<Long, Set<Long>>> varRespWpQnaireMap) {
    if (CommonUtils.isNotEmpty(varRespWpQnaireMap) && varRespWpQnaireMap.containsKey(qnaireRespVar.getA2lRespId()) &&
        varRespWpQnaireMap.get(qnaireRespVar.getA2lRespId()).containsKey(qnaireRespVar.getA2lWpId())) {
      if (addValueFlag) {
        varRespWpQnaireMap.get(qnaireRespVar.getA2lRespId()).get(qnaireRespVar.getA2lWpId())
            .add(rvwQnaireResponse.getId());
      }
      else {
        varRespWpQnaireMap.get(qnaireRespVar.getA2lRespId()).get(qnaireRespVar.getA2lWpId())
            .remove(rvwQnaireResponse.getId());
      }

    }
  }

  /**
   * @param rvwQnaireRespVariant
   * @return
   */
  private PidcTreeNode getQnaireTitleNode(final Long pidcVersId) {
    PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(pidcVersId);

    // Construct Review Questionnaire node id
    String nodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() +
        pidcNode.getPidcVersion().getId();
    return this.nodeIdNodeMap.get(nodeId);
  }

  /**
   * @param chDataInfoMap
   */
  public void refreshPidcVariants(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        refreshPIDCVariantForUpdate(changeData);
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.CREATE) {
        PidcVariantServiceClient varServiceClient = new PidcVariantServiceClient();
        try {
          PidcVariant var = varServiceClient.getById(changeData.getObjId());
          if (varServiceClient.getVariantsForVersion(var.getPidcVersionId(), true).size() == 1) {
            updatePidcQnaireInfo(var.getPidcVersionId());
            updateA2lStructureModel(var.getPidcVersionId());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
  }

  public void updateA2lStructureModel(final Long pidcVerId) {
    try {
      // for updating A2lStructureModel in new a2l structures
      // constructing and fetching the a2l node ids
      Set<PidcTreeNode> allA2lNodesForPidcVersion = getAllA2lNodesForPidcVersion(pidcVerId);
      // Update Qnaire response in getA2lStructureModel Object in pidctreenode
      for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
        // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
        if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel())) {
          // Update Qnaire response in PidcQnaireInfo Object in pidctreenode
          PidcA2lTreeStructureModel pidcA2lTreeStructuresModel = new PidcA2lTreeStructureServiceClient()
              .getPidcA2lTreeStructuresModel(a2lPidcTreeNode.getPidcA2l().getId());
          a2lPidcTreeNode.setA2lStructureModel(pidcA2lTreeStructuresModel);
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param changeData
   */
  private void refreshPIDCVariantForUpdate(final ChangeDataInfo changeData) {
    try {
      PidcVariantServiceClient varService = new PidcVariantServiceClient();
      PidcVariant var = varService.getById(changeData.getObjId());
      // get the review results title node
      PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(var.getPidcVersionId());
      if (null != pidcVerWithChange) {
        String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
            PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();
        PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
        if (null != rvwResultTitleNode) {
          PidcReviewDetails pidcRvwDetails = rvwResultTitleNode.getPidcRvwDetails();
          pidcRvwDetails.getPidcVarMap().put(var.getId(), var);
        }
        // for updating PidcQnaireInfo
        PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(var.getPidcVersionId());
        String rvwQnaireNodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR +
            PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + pidcNode.getPidcVersion().getId();
        PidcTreeNode pidcVerNode = this.nodeIdNodeMap.get(rvwQnaireNodeId);
        if (null != pidcVerNode) {
          PidcQnaireInfo qnaireInfo =
              new RvwQnaireResponseServiceClient().getPidcQnaireVariants(var.getPidcVersionId());
          pidcVerNode.setQnaireInfo(qnaireInfo);
        }

        // for updating the a2l structure model
        updateA2lStructureModel(var.getPidcVersionId());
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param chDataInfoMap
   */
  public void refreshWorkpackages(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        A2lWorkPackage a2lWorkPackage;
        try {
          a2lWorkPackage = new A2lWorkPackageServiceClient().getById(changeData.getObjId());
          // get the review results title node
          PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(a2lWorkPackage.getPidcVersId());
          if (null != pidcVerWithChange) {
            String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
                PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();
            PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
            if (null != rvwResultTitleNode) {
              PidcReviewDetails pidcRvwDetails = rvwResultTitleNode.getPidcRvwDetails();
              pidcRvwDetails.getA2lWpMap().put(changeData.getObjId(), a2lWorkPackage);
            }

            refreshWpForQnaire(changeData, a2lWorkPackage, pidcVerWithChange);
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * @param changeData
   * @param a2lWorkPackage
   * @param pidcVerWithChange
   */
  private void refreshWpForQnaire(final ChangeDataInfo changeData, final A2lWorkPackage a2lWorkPackage,
      final PidcTreeNode pidcVerWithChange) {
    String rvwQnaireTitleNodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + pidcVerWithChange.getPidcVersion().getId();
    PidcTreeNode rvwQnaireTitleNode = this.nodeIdNodeMap.get(rvwQnaireTitleNodeId);
    if (CommonUtils.isNotNull(rvwQnaireTitleNode) && (null != rvwQnaireTitleNode.getQnaireInfo()) &&
        rvwQnaireTitleNode.getQnaireInfo().getA2lWpMap().containsKey(a2lWorkPackage.getId())) {
      rvwQnaireTitleNode.getQnaireInfo().getA2lWpMap().put(changeData.getObjId(), a2lWorkPackage);
    }

    Set<PidcTreeNode> allA2lNodesForPidcVersion =
        getAllA2lNodesForPidcVersion(pidcVerWithChange.getPidcVersion().getId());
    // Update Qnaire response in getA2lStructureModel Object in pidctreenode
    for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
      // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
      if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel()) &&
          a2lPidcTreeNode.getA2lStructureModel().getA2lWpMap().containsKey(a2lWorkPackage.getId())) {
        // Update Qnaire response in getA2lStructureModel Object in pidctreenode
        a2lPidcTreeNode.getA2lStructureModel().getA2lWpMap().put(changeData.getObjId(), a2lWorkPackage);
      }
    }

  }

  /**
   * @param chDataInfoMap
   */
  public void refreshResponsibilities(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        refreshResponsibilityForUpdate(changeData);
      }
    }
  }

  /**
   * @param chDataInfoMap
   */
  public void refreshA2lWPRespStatus(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE)) {
        refreshA2lWpRespStatusForCreate(changeData);

      }
      if ((changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        refreshA2lWPRespStatusForUpdate(changeData);
      }
    }
  }

  /**
   * @param changeData
   */
  private void refreshA2lWpRespStatusForCreate(final ChangeDataInfo changeData) {
    A2lWpResponsibilityStatus a2lWpRespStatus;
    try {

      a2lWpRespStatus = new A2lWpResponsibilityStatusServiceClient().get(Long.valueOf(changeData.getObjId()));
      A2lWpResponsibility a2lWpResp = new A2lWpResponsibilityServiceClient().get(a2lWpRespStatus.getWpRespId());
      PidcA2l pidcA2l = new PidcA2lServiceClient()
          .getById(new A2lWpDefinitionVersionServiceClient().get(a2lWpResp.getWpDefnVersId()).getPidcA2lId());
      Set<PidcTreeNode> allA2lNodesForPidcVersion = getAllA2lNodesForPidcVersion(pidcA2l.getPidcVersId());
      Long pidcVarId = resolvePidcVarId(a2lWpRespStatus);

      for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
        // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
        if (CommonUtils.isNotNull(a2lPidcTreeNode) && CommonUtils.isNotNull(a2lPidcTreeNode.getA2lStructureModel()) &&
            CommonUtils.isEqual(pidcA2l, a2lPidcTreeNode.getPidcA2l())) {

          refreshA2lWpRespStatusMapForCreate(a2lWpRespStatus, a2lPidcTreeNode, pidcVarId);
          break;
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param a2lWpRespStatus
   * @param a2lWpResp
   * @param a2lPidcTreeNode
   * @param pidcVarId
   */
  private void refreshA2lWpRespStatusMapForCreate(final A2lWpResponsibilityStatus a2lWpRespStatus,
      final PidcTreeNode a2lPidcTreeNode, final Long pidcVarId) {

    Map<Long, Map<Long, String>> respWPRespStatusMap;
    Map<Long, String> wpIDWPRespStatusMap;
    Map<Long, Map<Long, Map<Long, String>>> pidcVarWpRespStatusMap =
        a2lPidcTreeNode.getA2lStructureModel().getWpRespStatusMap();
    Long a2lRespId = a2lWpRespStatus.getA2lRespId();
    Long a2lWpId = a2lWpRespStatus.getA2lWpId();
    String wpFinStatus = a2lWpRespStatus.getWpRespFinStatus();

    if (pidcVarWpRespStatusMap.containsKey(pidcVarId)) {
      addCreatedWPRespStatusForVar(a2lWpRespStatus, pidcVarId, pidcVarWpRespStatusMap, a2lRespId, a2lWpId, wpFinStatus);
    }
    else {
      respWPRespStatusMap = new HashMap<>();
      wpIDWPRespStatusMap = new HashMap<>();
      wpIDWPRespStatusMap.put(a2lWpId, wpFinStatus);
      respWPRespStatusMap.put(a2lRespId, wpIDWPRespStatusMap);
      pidcVarWpRespStatusMap.put(pidcVarId, respWPRespStatusMap);
    }
  }

  /**
   * @param a2lWpRespStatus
   * @param pidcVarId
   * @param pidcVarWpRespStatusMap
   * @param a2lRespId
   * @param a2lWpId
   * @param wpFinStatus
   */
  private void addCreatedWPRespStatusForVar(final A2lWpResponsibilityStatus a2lWpRespStatus, final Long pidcVarId,
      final Map<Long, Map<Long, Map<Long, String>>> pidcVarWpRespStatusMap, final Long a2lRespId, final Long a2lWpId,
      final String wpFinStatus) {

    Map<Long, Map<Long, String>> respWPRespStatusMap;
    Map<Long, String> wpIDWPRespStatusMap;
    respWPRespStatusMap = pidcVarWpRespStatusMap.get(pidcVarId);

    if (respWPRespStatusMap.containsKey(a2lRespId)) {
      respWPRespStatusMap.get(a2lRespId).put(a2lWpId, wpFinStatus);
    }
    else {
      wpIDWPRespStatusMap = new HashMap<>();
      wpIDWPRespStatusMap.put(a2lWpRespStatus.getA2lWpId(), a2lWpRespStatus.getWpRespFinStatus());
      respWPRespStatusMap.put(a2lWpRespStatus.getA2lRespId(), wpIDWPRespStatusMap);
    }
  }

  /**
   * @param changeData
   */
  private void refreshA2lWPRespStatusForUpdate(final ChangeDataInfo changeData) {
    try {
      A2lWpResponsibilityStatus a2lWpRespStatus =
          new A2lWpResponsibilityStatusServiceClient().get(Long.valueOf(changeData.getObjId()));
      A2lWpResponsibility a2lWpResp = new A2lWpResponsibilityServiceClient().get(a2lWpRespStatus.getWpRespId());
      PidcA2l pidcA2l = new PidcA2lServiceClient()
          .getById(new A2lWpDefinitionVersionServiceClient().get(a2lWpResp.getWpDefnVersId()).getPidcA2lId());
      Set<PidcTreeNode> allA2lNodesForPidcVersion = getAllA2lNodesForPidcVersion(pidcA2l.getPidcVersId());
      Long pidcVarId = resolvePidcVarId(a2lWpRespStatus);

      // Update wp responsibility in getA2lStructureModel Object in pidctreenode
      for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {

        // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
        if (CommonUtils.isNotNull(a2lPidcTreeNode) && CommonUtils.isNotNull(a2lPidcTreeNode.getA2lStructureModel()) &&
            CommonUtils.isEqual(pidcA2l, a2lPidcTreeNode.getPidcA2l())) {

          refreshA2lWpRespStatusMapForUpd(a2lWpRespStatus, a2lPidcTreeNode, pidcVarId);
          break;
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * to handle no variant case
   *
   * @param a2lWpRespStatus
   * @return pidc Variant ID handling no variant case
   */
  private Long resolvePidcVarId(final A2lWpResponsibilityStatus a2lWpRespStatus) {
    return CommonUtils.isNull(a2lWpRespStatus.getVariantId()) ? ApicConstants.NO_VARIANT_ID
        : a2lWpRespStatus.getVariantId();
  }

  /**
   * @param a2lWpRespStatus
   * @param a2lWpResp
   * @param a2lPidcTreeNode
   * @param pidcVarId
   */
  private void refreshA2lWpRespStatusMapForUpd(final A2lWpResponsibilityStatus a2lWpRespStatus,
      final PidcTreeNode a2lPidcTreeNode, final Long pidcVarId) {

    Map<Long, Map<Long, Map<Long, String>>> wpRespStatusMap =
        a2lPidcTreeNode.getA2lStructureModel().getWpRespStatusMap();
    Map<Long, Map<Long, String>> pidcVarWpRespStatusMap = wpRespStatusMap.get(pidcVarId);

    if (wpRespStatusMap.containsKey(pidcVarId) && pidcVarWpRespStatusMap.containsKey(a2lWpRespStatus.getA2lRespId()) &&
        pidcVarWpRespStatusMap.get(a2lWpRespStatus.getA2lRespId()).containsKey(a2lWpRespStatus.getA2lWpId())) {

      // Update wp responsibility in getA2lStructureModel Object in pidctreenode
      pidcVarWpRespStatusMap.get(a2lWpRespStatus.getA2lRespId()).put(a2lWpRespStatus.getA2lWpId(),
          a2lWpRespStatus.getWpRespFinStatus());
    }
  }


  /**
   * @param changeData
   */
  private void refreshResponsibilityForUpdate(final ChangeDataInfo changeData) {
    try {
      A2lResponsibility a2lResponsibility =
          new A2lResponsibilityServiceClient().get(Long.valueOf(changeData.getObjId()));
      Map<Long, PidcVersion> pidcVersMap =
          new PidcVersionServiceClient().getAllPidcVersionForPidc(a2lResponsibility.getProjectId());
      for (PidcVersion pidcVersion : pidcVersMap.values()) {
        // get the review results title node
        PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(pidcVersion.getId());
        if (pidcVerWithChange != null) {
          String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
              PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();
          PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
          if (null != rvwResultTitleNode) {
            PidcReviewDetails pidcRvwDetails = rvwResultTitleNode.getPidcRvwDetails();
            pidcRvwDetails.getA2lRespMap().put(changeData.getObjId(), a2lResponsibility);
          }

          refreshRespForQnaire(changeData, a2lResponsibility, pidcVerWithChange);
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param changeData
   * @param a2lResponsibility
   * @param pidcVerWithChange
   */
  private void refreshRespForQnaire(final ChangeDataInfo changeData, final A2lResponsibility a2lResponsibility,
      final PidcTreeNode pidcVerWithChange) {
    String rvwQnaireTitleNodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + pidcVerWithChange.getPidcVersion().getId();
    PidcTreeNode rvwQnaireTitleNode = this.nodeIdNodeMap.get(rvwQnaireTitleNodeId);
    if ((null != rvwQnaireTitleNode) && (null != rvwQnaireTitleNode.getQnaireInfo()) &&
        rvwQnaireTitleNode.getQnaireInfo().getA2lRespMap().containsKey(a2lResponsibility.getId())) {
      rvwQnaireTitleNode.getQnaireInfo().getA2lRespMap().put(changeData.getObjId(), a2lResponsibility);
    }

    Set<PidcTreeNode> allA2lNodesForPidcVersion =
        getAllA2lNodesForPidcVersion(pidcVerWithChange.getPidcVersion().getId());
    // Update Qnaire response in getA2lStructureModel Object in pidctreenode
    for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
      // getA2lStructureModel null check is done to avoid loading the model for not expanded a2l in pidctreeview
      if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel()) &&
          a2lPidcTreeNode.getA2lStructureModel().getA2lRespMap().containsKey(a2lResponsibility.getId())) {
        // Update Qnaire response in getA2lStructureModel Object in pidctreenode
        a2lPidcTreeNode.getA2lStructureModel().getA2lRespMap().put(changeData.getObjId(), a2lResponsibility);
      }
    }

  }

  /**
   * @param chDataInfoMap
   */
  public void refreshCDRVariant(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    // insert of cdr result
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      CDRReviewResultServiceClient cdrResultClient = new CDRReviewResultServiceClient();
      if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE) ||
          (changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        try {
          PidcReviewDetails newReviewResultInfo = cdrResultClient.getNewReviewResultInfo(null, changeData.getObjId());
          CDRReviewResult cdrResult = newReviewResultInfo.getCdrResultMap().values().iterator().next();
          mergeRvwResultInsert(newReviewResultInfo, cdrResult.getPidcVersionId());

          // Refresh Wp, Resp, Qnaire Resp in tree view by updating pidcqnaireinfo object for start and official reviews
          if (!cdrResult.getReviewType().equals(CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
            updatePidcQnaireInfo(cdrResult.getPidcVersionId());
            updateA2lStructureModel(cdrResult.getPidcVersionId());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        handleDeleteOfRvwVariant(changeData, cdrResultClient);
      }
    }
  }

  /**
   * @param changeData
   * @param cdrResultClient
   */
  private void handleDeleteOfRvwVariant(final ChangeDataInfo changeData,
      final CDRReviewResultServiceClient cdrResultClient) {
    RvwVariant cdrVariant = (RvwVariant) changeData.getRemovedData();
    CDRReviewResult cdrResult = null;
    try {
      // get review result object
      cdrResult = cdrResultClient.getById(cdrVariant.getResultId());
    }
    catch (ApicWebServiceException exp) {
      if (CommonUtils.isEqual(exp.getErrorCode(), "DATA_NOT_FOUND")) {
        CDMLogger.getInstance().warn(exp.getMessage(), exp);
      }
      else {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    if (null != cdrResult) {
      mergeRvwVariantDelete(cdrResult, cdrVariant);
    }
  }

  /**
   * @param cdrResult
   * @param cdrVariant
   */
  private void mergeRvwVariantDelete(final CDRReviewResult cdrResult, final RvwVariant cdrVariant) {
    // get the review results title node
    PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(cdrResult.getPidcVersionId());
    String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();
    PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
    if (null != rvwResultTitleNode) {
      PidcReviewDetails pidcRvwDetails = rvwResultTitleNode.getPidcRvwDetails();
      Long variantId = cdrVariant.getVariantId();

      RvwVariant removedRvwVariant = pidcRvwDetails.getRvwVariantMap().remove(cdrVariant.getId());
      if (removedRvwVariant != null) {
        // remove from maps only if already not removed
        StringBuilder entryToBeDeleted = new StringBuilder();
        // remove from other source type results map
        if (null != pidcRvwDetails.getOtherSrcTypeResults().get(variantId)) {
          removeFromOtherSrcTypeMap(cdrResult, pidcRvwDetails, variantId, entryToBeDeleted);
        }

        // remove from var responsibility map
        Map<Long, Set<Long>> respToBeDeleted = new HashMap<>();
        pidcRvwDetails.getVarRespWpMap().get(variantId).entrySet().forEach(entry -> entry.getValue().entrySet()
            .forEach(mapEntry -> computeRespToBeDeleted(cdrResult, respToBeDeleted, entry, mapEntry)));
        respToBeDeleted.entrySet().forEach(delEntry -> removeResp(pidcRvwDetails, variantId, delEntry));

        // remove from wp map
        Set<Long> setToBeDeleted = new HashSet<>();
        pidcRvwDetails.getVarWpMap().get(variantId).entrySet()
            .forEach(entry -> computeItemsToBeDeletedFromWPMap(cdrResult, setToBeDeleted, entry));
        setToBeDeleted.forEach(wpId -> removeFromWPMap(pidcRvwDetails, variantId, wpId));
        // remove from other maps
        pidcRvwDetails.getResToVarMap().get(cdrResult.getId()).remove(cdrVariant.getVariantId());
        // to update pidc qnaire info only if there are no review result under the variant
        long inputVarId = null != cdrVariant.getVariantId() ? cdrVariant.getVariantId() : -1L;
        Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpMap =
            rvwResultTitleNode.getPidcRvwDetails().getVarRespWpMap();
        if (varRespWpMap.isEmpty() ||
            (!varRespWpMap.containsKey(inputVarId) || varRespWpMap.get(inputVarId).isEmpty())) {
          updatePidcQnaireInfo(cdrResult);
        }
      }
    }
  }

  /**
   * @param cdrResult
   * @param setToBeDeleted
   * @param entry
   */
  private void computeItemsToBeDeletedFromWPMap(final CDRReviewResult cdrResult, final Set<Long> setToBeDeleted,
      final Entry<Long, Set<Long>> entry) {
    Set<Long> cdrIdSet = entry.getValue();
    Set<Long> duplicateSet = new HashSet<>(cdrIdSet);
    for (Long cdrId : duplicateSet) {
      if (cdrId.longValue() == cdrResult.getId().longValue()) {
        cdrIdSet.remove(cdrId);
        if (cdrIdSet.isEmpty()) {
          setToBeDeleted.add(entry.getKey());
        }
      }
    }
  }

  /**
   * @param pidcRvwDetails
   * @param variantId
   * @param wpId
   */
  private void removeFromWPMap(final PidcReviewDetails pidcRvwDetails, final Long variantId, final Long wpId) {
    Map<Long, Set<Long>> wpMap = pidcRvwDetails.getVarWpMap().get(variantId);
    wpMap.remove(wpId);
    if (wpMap.isEmpty()) {
      pidcRvwDetails.getVarWpMap().remove(variantId);
    }
  }

  /**
   * @param pidcRvwDetails
   * @param variantId
   * @param delEntry
   */
  private void removeResp(final PidcReviewDetails pidcRvwDetails, final Long variantId,
      final Entry<Long, Set<Long>> delEntry) {
    Map<Long, Set<Long>> wpResMap = pidcRvwDetails.getVarRespWpMap().get(variantId).get(delEntry.getKey());
    delEntry.getValue().forEach(wpId -> {
      wpResMap.remove(wpId);
      if (wpResMap.isEmpty()) {
        pidcRvwDetails.getVarRespWpMap().get(variantId).remove(delEntry.getKey());
        if (pidcRvwDetails.getVarRespWpMap().get(variantId).isEmpty()) {
          pidcRvwDetails.getVarRespWpMap().remove(variantId);
        }
      }
    });
  }

  /**
   * @param cdrResult
   * @param respToBeDeleted
   * @param entry
   * @param mapEntry
   */
  private void computeRespToBeDeleted(final CDRReviewResult cdrResult, final Map<Long, Set<Long>> respToBeDeleted,
      final Entry<Long, Map<Long, Set<Long>>> entry, final Entry<Long, Set<Long>> mapEntry) {
    Set<Long> cdrIdSet = mapEntry.getValue();
    Set<Long> duplicateSet = new HashSet<>(cdrIdSet);
    for (Long cdrId : duplicateSet) {
      if (cdrId.longValue() == cdrResult.getId().longValue()) {
        cdrIdSet.remove(cdrId);
        if (cdrIdSet.isEmpty()) {
          Set<Long> wpSet = respToBeDeleted.get(entry.getKey());
          if (null == wpSet) {
            wpSet = new HashSet<>();
          }
          wpSet.add(mapEntry.getKey());
          respToBeDeleted.put(entry.getKey(), wpSet);
        }
      }
    }
  }

  /**
   * @param cdrResult
   * @param pidcRvwDetails
   * @param variantId
   * @param entryToBeDeleted
   */
  private void removeFromOtherSrcTypeMap(final CDRReviewResult cdrResult, final PidcReviewDetails pidcRvwDetails,
      final Long variantId, final StringBuilder entryToBeDeleted) {
    pidcRvwDetails.getOtherSrcTypeResults().get(variantId).entrySet().forEach(entry -> {
      Set<Long> cdrIdSet = entry.getValue();
      Set<Long> duplicateSet = new HashSet<>(cdrIdSet);
      for (Long cdrId : duplicateSet) {
        if (cdrId.longValue() == cdrResult.getId().longValue()) {
          cdrIdSet.remove(cdrId);
          if (cdrIdSet.isEmpty()) {
            entryToBeDeleted.append(entry.getKey());
          }
        }
      }
    });
    pidcRvwDetails.getOtherSrcTypeResults().get(variantId).remove(entryToBeDeleted.toString());
    if (pidcRvwDetails.getOtherSrcTypeResults().get(variantId).isEmpty()) {
      // remove it from the main map if there are no other values
      pidcRvwDetails.getOtherSrcTypeResults().remove(variantId);
    }
  }

  /**
   * @param cdrResult
   */
  private void updatePidcQnaireInfo(final CDRReviewResult cdrResult) {
    // for updating PidcQnaireInfo
    try {
      PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(cdrResult.getPidcVersionId());
      String rvwQnaireNodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR +
          PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + pidcNode.getPidcVersion().getId();
      PidcTreeNode pidcVerNode = this.nodeIdNodeMap.get(rvwQnaireNodeId);
      if (null != pidcVerNode) {
        PidcQnaireInfo qnaireInfo =
            new RvwQnaireResponseServiceClient().getPidcQnaireVariants(cdrResult.getPidcVersionId());
        pidcVerNode.setQnaireInfo(qnaireInfo);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param newReviewResultInfo PidcReviewDetails
   * @param long1
   */
  private void mergeRvwResultInsert(final PidcReviewDetails newReviewResultInfo, final Long pidcVersId) {

    // get the review results title node
    PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(pidcVersId);
    if (null != pidcVerWithChange) {
      String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
          PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();
      PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
      if (null != rvwResultTitleNode) {
        PidcReviewDetails pidcRvwDetails = rvwResultTitleNode.getPidcRvwDetails();

        if (null == pidcRvwDetails) {
          rvwResultTitleNode.setPidcRvwDetails(newReviewResultInfo);
        }
        else {

          // combine var wp map
          BinaryOperator<Set<Long>> combineSets = (final Set<Long> oldSet, final Set<Long> newSet) -> {
            oldSet.addAll(newSet);
            return oldSet;
          };


          BinaryOperator<Map<Long, Set<Long>>> combineMaps =
              (final Map<Long, Set<Long>> oldMap, final Map<Long, Set<Long>> newMap) -> {
                newMap.entrySet().forEach(entry -> oldMap.merge(entry.getKey(), entry.getValue(), combineSets));
                return oldMap;
              };


          newReviewResultInfo.getVarWpMap().entrySet()
              .forEach(entry -> pidcRvwDetails.getVarWpMap().merge(entry.getKey(), entry.getValue(), combineMaps));

          // combine Var Resp map
          BiFunction<? super Map<Long, Map<Long, Set<Long>>>, ? super Map<Long, Map<Long, Set<Long>>>, ? extends Map<Long, Map<Long, Set<Long>>>> combineWps =
              (oldMap, newMap) -> {
                newMap.entrySet().forEach(entry -> oldMap.merge(entry.getKey(), entry.getValue(), combineMaps));
                return oldMap;
              };

          newReviewResultInfo.getVarRespWpMap().entrySet()
              .forEach(entry -> pidcRvwDetails.getVarRespWpMap().merge(entry.getKey(), entry.getValue(), combineWps));


          // combine other source map
          BiFunction<? super Map<String, Set<Long>>, ? super Map<String, Set<Long>>, ? extends Map<String, Set<Long>>> combineOtherSrcMaps =
              (oldMap, newMap) -> {
                newMap.entrySet().forEach(entry -> oldMap.merge(entry.getKey(), entry.getValue(), combineSets));
                return oldMap;
              };
          newReviewResultInfo.getOtherSrcTypeResults().entrySet().forEach(entry -> pidcRvwDetails
              .getOtherSrcTypeResults().merge(entry.getKey(), entry.getValue(), combineOtherSrcMaps));

          pidcRvwDetails.getA2lRespMap().putAll(newReviewResultInfo.getA2lRespMap());
          pidcRvwDetails.getA2lWpMap().putAll(newReviewResultInfo.getA2lWpMap());
          pidcRvwDetails.getCdrResultMap().putAll(newReviewResultInfo.getCdrResultMap());
          pidcRvwDetails.getRvwVariantMap().putAll(newReviewResultInfo.getRvwVariantMap());
          pidcRvwDetails.getPidcVarMap().putAll(newReviewResultInfo.getPidcVarMap());

          // combine var to review variant mapping
          BiFunction<? super Map<Long, Long>, ? super Map<Long, Long>, ? extends Map<Long, Long>> combineRvwToRvwVarMap =
              (oldMap, newMap) -> {
                newMap.entrySet().forEach(entry -> oldMap.putAll(newMap));
                return oldMap;
              };

          newReviewResultInfo.getResToVarMap().entrySet().forEach(
              entry -> pidcRvwDetails.getResToVarMap().merge(entry.getKey(), entry.getValue(), combineRvwToRvwVarMap));
        }
      }
    }
  }

  /**
   * for update and delete of CDR
   */
  public void refreshCDR(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        mergeReviewResultDelete(changeData);
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.UPDATE) {
        // update cdr result details
        updateRvwResult(changeData);
      }
      else {
        CDRReviewResultServiceClient cdrResultClient = new CDRReviewResultServiceClient();
        PidcReviewDetails newReviewResultInfo;
        try {
          newReviewResultInfo = cdrResultClient.getNewReviewResultInfo(changeData.getObjId(), null);
          CDRReviewResult cdrResult = newReviewResultInfo.getCdrResultMap().values().iterator().next();
          mergeRvwResultInsert(newReviewResultInfo, cdrResult.getPidcVersionId());

          // Refresh Wp, Resp, Qnaire Resp in tree view by updating pidcqnaireinfo object for start and official reviews
          if (!cdrResult.getReviewType().equals(CDRConstants.REVIEW_TYPE.TEST.getDbType())) {
            updatePidcQnaireInfo(cdrResult.getPidcVersionId());
            updateA2lStructureModel(cdrResult.getPidcVersionId());
          }
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    }
  }

  /**
   * @param cdrResult
   * @throws ApicWebServiceException
   */
  private void updatePidcQnaireInfo(final Long pidcVersId) throws ApicWebServiceException {
    PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(pidcVersId);
    String nodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() +
        pidcNode.getPidcVersion().getId();
    PidcTreeNode pidcVerNode = this.nodeIdNodeMap.get(nodeId);
    if (null != pidcVerNode) {
      PidcQnaireInfo qnaireInfo = new RvwQnaireResponseServiceClient().getPidcQnaireVariants(pidcVersId);
      pidcVerNode.setQnaireInfo(qnaireInfo);
    }
  }

  /**
   * @param pidcVersId
   * @throws ApicWebServiceException
   */
  private void updateRvwResults(final Long pidcVersId) throws ApicWebServiceException {
    PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(pidcVersId);
    String nodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() +
        pidcNode.getPidcVersion().getId();
    PidcTreeNode pidcVerNode = this.nodeIdNodeMap.get(nodeId);
    if (null != pidcVerNode) {
      pidcVerNode.setPidcRvwDetails(new CDRReviewResultServiceClient().getReviewResultInfo(pidcVersId));
    }
  }

  /**
   * @param changeData
   */
  private void mergeReviewResultDelete(final ChangeDataInfo changeData) {
    CDRReviewResult deletedCdrResult = (CDRReviewResult) changeData.getRemovedData();
    long resultId = changeData.getObjId();

    // get the review results title node
    PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(deletedCdrResult.getPidcVersionId());
    String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();
    PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
    if (null != rvwResultTitleNode) {
      // delete from the maps
      // cdr Result map
      rvwResultTitleNode.getPidcRvwDetails().getCdrResultMap().remove(deletedCdrResult.getId());
      // review variant map
      rvwResultTitleNode.getPidcRvwDetails().getRvwVariantMap().remove(deletedCdrResult.getPrimaryVariantId());
      // responsibility map
      Map<Long, Map<Long, Set<Long>>> respToBeDeleted = new HashMap<>();
      rvwResultTitleNode.getPidcRvwDetails().getVarRespWpMap().entrySet()
          .forEach(entry -> entry.getValue().entrySet().forEach(respMap -> respMap.getValue().entrySet()
              .forEach(wpResMap -> computeRespToBeDeleted(resultId, respToBeDeleted, entry, respMap, wpResMap))));
      respToBeDeleted.entrySet().forEach(delEntry -> removeResp(rvwResultTitleNode, delEntry));

      // workpackage map
      Map<Long, Set<Long>> wpMapToBeDeleted = new HashMap<>();
      rvwResultTitleNode.getPidcRvwDetails().getVarWpMap().entrySet().forEach(varWpResMap -> varWpResMap.getValue()
          .entrySet().forEach(wpResMap -> computeWPtoBeDeleted(resultId, wpMapToBeDeleted, varWpResMap, wpResMap)));
      wpMapToBeDeleted.entrySet().forEach(entry -> removeWP(rvwResultTitleNode, entry));

      // other source type map
      Map<Long, String> mapToDelete = new HashMap<>();
      rvwResultTitleNode.getPidcRvwDetails().getOtherSrcTypeResults().entrySet()
          .forEach(othrSrcMap -> othrSrcMap.getValue().entrySet().forEach(
              srcTypeResMap -> computeOtherSrcTypeMapToBeDeleted(resultId, mapToDelete, othrSrcMap, srcTypeResMap)));
      mapToDelete.entrySet().forEach(delEntry -> deleteOtherSrcTypeMap(rvwResultTitleNode, delEntry));
      // to update pidc qnaire info only if there are no review result under the variant
      long inputVarId = null != deletedCdrResult.getPrimaryVariantId() ? deletedCdrResult.getPrimaryVariantId() : -1L;
      Map<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpMap =
          rvwResultTitleNode.getPidcRvwDetails().getVarRespWpMap();
      if (varRespWpMap.isEmpty() || (!varRespWpMap.containsKey(inputVarId) || varRespWpMap.get(inputVarId).isEmpty())) {
        updatePidcQnaireInfo(deletedCdrResult);
        updateA2lStructureModel(deletedCdrResult.getPidcVersionId());
      }
    }

  }

  /**
   * @param rvwResultTitleNode
   * @param delEntry
   */
  private void deleteOtherSrcTypeMap(final PidcTreeNode rvwResultTitleNode, final Entry<Long, String> delEntry) {
    Map<String, Set<Long>> othrSrcMap =
        rvwResultTitleNode.getPidcRvwDetails().getOtherSrcTypeResults().get(delEntry.getKey());
    othrSrcMap.remove(delEntry.getValue());
    if (othrSrcMap.isEmpty()) {
      rvwResultTitleNode.getPidcRvwDetails().getOtherSrcTypeResults().remove(delEntry.getKey());
    }
  }

  /**
   * @param resultId
   * @param mapToDelete
   * @param othrSrcMap
   * @param srcTypeResMap
   */
  private void computeOtherSrcTypeMapToBeDeleted(final long resultId, final Map<Long, String> mapToDelete,
      final Entry<Long, Map<String, Set<Long>>> othrSrcMap, final Entry<String, Set<Long>> srcTypeResMap) {
    Set<Long> cdrSet = srcTypeResMap.getValue();
    Set<Long> duplicateCdrSet = new HashSet<>(cdrSet);
    for (Long cdrId : duplicateCdrSet) {
      if (cdrId == resultId) {
        cdrSet.remove(cdrId);
        if (cdrSet.isEmpty()) {
          mapToDelete.put(othrSrcMap.getKey(), srcTypeResMap.getKey());
        }
      }
    }
  }

  /**
   * @param rvwResultTitleNode
   * @param entry
   */
  private void removeWP(final PidcTreeNode rvwResultTitleNode, final Entry<Long, Set<Long>> entry) {
    Map<Long, Set<Long>> wpResultMap = rvwResultTitleNode.getPidcRvwDetails().getVarWpMap().get(entry.getKey());
    entry.getValue().forEach(wpId -> {
      wpResultMap.remove(wpId);
      if (wpResultMap.isEmpty()) {
        rvwResultTitleNode.getPidcRvwDetails().getVarWpMap().remove(entry.getKey());
      }
    });
  }

  /**
   * @param resultId
   * @param wpMapToBeDeleted
   * @param varWpResMap
   * @param wpResMap
   */
  private void computeWPtoBeDeleted(final long resultId, final Map<Long, Set<Long>> wpMapToBeDeleted,
      final Entry<Long, Map<Long, Set<Long>>> varWpResMap, final Entry<Long, Set<Long>> wpResMap) {
    Set<Long> cdrSet = wpResMap.getValue();
    Set<Long> duplicateCdrSet = new HashSet<>(cdrSet);
    for (Long cdrId : duplicateCdrSet) {
      if (cdrId == resultId) {
        cdrSet.remove(cdrId);
        if (cdrSet.isEmpty()) {
          Set<Long> wpSet = wpMapToBeDeleted.get(varWpResMap.getKey());
          if (null == wpSet) {
            wpSet = new HashSet<>();
          }
          wpSet.add(wpResMap.getKey());
          wpMapToBeDeleted.put(varWpResMap.getKey(), wpSet);
        }
      }
    }
  }

  /**
   * @param rvwResultTitleNode
   * @param delEntry
   */
  private void removeResp(final PidcTreeNode rvwResultTitleNode, final Entry<Long, Map<Long, Set<Long>>> delEntry) {
    Map<Long, Map<Long, Set<Long>>> respWpMap =
        rvwResultTitleNode.getPidcRvwDetails().getVarRespWpMap().get(delEntry.getKey());
    delEntry.getValue().entrySet().forEach(delWpEntry -> {
      Map<Long, Set<Long>> wpMap = respWpMap.get(delWpEntry.getKey());
      for (Long wpId : delWpEntry.getValue()) {
        wpMap.remove(wpId);
      }
      if (wpMap.isEmpty()) {
        respWpMap.remove(delWpEntry.getKey());
        if (respWpMap.isEmpty()) {
          rvwResultTitleNode.getPidcRvwDetails().getVarRespWpMap().remove(delEntry.getKey());
        }
      }
    });
  }

  /**
   * @param resultId
   * @param respToBeDeleted
   * @param entry
   * @param respMap
   * @param wpResMap
   */
  private void computeRespToBeDeleted(final long resultId, final Map<Long, Map<Long, Set<Long>>> respToBeDeleted,
      final Entry<Long, Map<Long, Map<Long, Set<Long>>>> entry, final Entry<Long, Map<Long, Set<Long>>> respMap,
      final Entry<Long, Set<Long>> wpResMap) {
    Set<Long> cdrSet = wpResMap.getValue();
    Set<Long> duplicateCdrSet = new HashSet<>(cdrSet);
    for (Long cdrId : duplicateCdrSet) {
      if (cdrId == resultId) {
        cdrSet.remove(cdrId);
        if (cdrSet.isEmpty()) {
          Map<Long, Set<Long>> respWpMap = respToBeDeleted.get(entry.getKey());
          if (null == respWpMap) {
            respWpMap = new HashMap<>();
          }
          Set<Long> wpSet = respWpMap.get(respMap.getKey());
          if (null == wpSet) {
            wpSet = new HashSet<>();
          }
          wpSet.add(wpResMap.getKey());
          respWpMap.put(respMap.getKey(), wpSet);
          respToBeDeleted.put(entry.getKey(), respWpMap);
        }
      }
    }
  }

  /**
   * @param changeData
   */
  private void updateRvwResult(final ChangeDataInfo changeData) {
    long resultId = changeData.getObjId();
    CDRReviewResultServiceClient cdrRvwResClient = new CDRReviewResultServiceClient();
    try {
      // get the updated cdr result
      CDRReviewResult cdrResult = cdrRvwResClient.getById(resultId);
      PidcTreeNode pidcVerWithChange = this.pidcVerIdTreenodeMap.get(cdrResult.getPidcVersionId());
      if (null != pidcVerWithChange) {
        String nodeId = pidcVerWithChange.getNodeId() + LEVEL_SEPARATOR +
            PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + pidcVerWithChange.getPidcVersion().getId();

        PidcTreeNode rvwResultTitleNode = this.nodeIdNodeMap.get(nodeId);
        if (null != rvwResultTitleNode) {
          rvwResultTitleNode.getPidcRvwDetails().getCdrResultMap().put(cdrResult.getId(), cdrResult);
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
  *
  */
  public void refreshSdomPverVar(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        PidcVariantAttribute varAttr;
        if (data.getChangeType().equals(CHANGE_OPERATION.INVISIBLE) ||
            data.getChangeType().equals(CHANGE_OPERATION.VISIBLE)) {
          continue;
        }
        if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
          varAttr = (PidcVariantAttribute) data.getRemovedData();
        }
        else {
          varAttr = new PidcVariantAttributeServiceClient().getbyId(data.getObjId());
        }
        if ((null != varAttr) && (null != varAttr.getPidcVersionId()) &&
            (null != this.pidcVerIdTreenodeMap.get(varAttr.getPidcVersionId()))) {
          PidcTreeNode pidcVerNode = this.pidcVerIdTreenodeMap.get(varAttr.getPidcVersionId());
          PidcTreeNode parentNode = this.nodeIdNodeMap.get(pidcVerNode.getNodeId());
          PidcTreeViewServiceClient pidcTreeViewSer = new PidcTreeViewServiceClient();
          Map<String, PidcSdomA2lInfo> sdomPverA2lMap = pidcTreeViewSer.getPidcSdomPvers(varAttr.getPidcVersionId());
          parentNode.getSdomA2lMap().clear();
          List<PidcTreeNode> sdomPverList = new ArrayList<>();
          for (PidcSdomA2lInfo sdomA2lInfo : sdomPverA2lMap.values()) {
            PidcTreeNode sdomPverNode = createSdomTreeNodeObject(parentNode, sdomA2lInfo.getSdomPver());
            sdomPverList.add(sdomPverNode);
            parentNode.getSdomA2lMap().put(sdomA2lInfo.getSdomPver().getPverName(), sdomA2lInfo.getA2lMap());
          }
          parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.SDOM_PVER.getUiType(), sdomPverList);
        }
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
  *
  */
  public void refreshSdomPver(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        PidcVersionAttribute versattr;
        if (data.getChangeType().equals(CHANGE_OPERATION.INVISIBLE) ||
            data.getChangeType().equals(CHANGE_OPERATION.VISIBLE)) {
          continue;
        }
        if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
          versattr = (PidcVersionAttribute) data.getRemovedData();
        }
        else {
          versattr = new PidcVersionAttributeServiceClient().getById(data.getObjId());
        }
        if ((null != versattr) && (null != versattr.getPidcVersId()) &&
            (null != this.pidcVerIdTreenodeMap.get(versattr.getPidcVersId()))) {
          PidcTreeNode pidcVerNode = this.pidcVerIdTreenodeMap.get(versattr.getPidcVersId());
          PidcTreeNode parentNode = this.nodeIdNodeMap.get(pidcVerNode.getNodeId());
          PidcTreeViewServiceClient pidcTreeViewSer = new PidcTreeViewServiceClient();
          Map<String, PidcSdomA2lInfo> sdomPverA2lMap = pidcTreeViewSer.getPidcSdomPvers(versattr.getPidcVersId());
          parentNode.getSdomA2lMap().clear();
          List<PidcTreeNode> sdomPverList = new ArrayList<>();
          for (PidcSdomA2lInfo sdomA2lInfo : sdomPverA2lMap.values()) {
            PidcTreeNode sdomPverNode = createSdomTreeNodeObject(parentNode, sdomA2lInfo.getSdomPver());
            sdomPverList.add(sdomPverNode);
            parentNode.getSdomA2lMap().put(sdomA2lInfo.getSdomPver().getPverName(), sdomA2lInfo.getA2lMap());
          }
          parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.SDOM_PVER.getUiType(), sdomPverList);
        }
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param chData
   * @return
   */
  public boolean checkForSdomPverNameVar(final ChangeData<?> chData) {
    PidcVariantAttribute varAttr = (PidcVariantAttribute) CnsUtils.getModel(chData);
    return varAttr.getAttrId().equals(
        new ApicDataBO().getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR)).getId());
  }

  /**
   * @param chData
   * @return
   */
  public boolean checkForSdomPverName(final ChangeData<?> chData) {
    PidcVersionAttribute verAttr = (PidcVersionAttribute) CnsUtils.getModel(chData);
    return verAttr.getAttrId().equals(
        new ApicDataBO().getAllLvlAttrByLevel().get(Long.valueOf(ApicConstants.SDOM_PROJECT_NAME_ATTR)).getId());
  }

  /**
   * @param chData
   * @return
   */
  private boolean checkForValidPidcNames(final ChangeData<?> chData) {
    AttributeValue attrVal = (AttributeValue) chData.getNewData();
    return attrVal.getAttributeId().equals(new ApicDataBO().getPidcNameAttrId());
  }

  /**
  *
  */
  public void refreshPidcA2l(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        createUpdateA2l(data);
        if (data.isMasterRefreshApplicable()) {
          Long pidcVersId = ((PidcA2l) data.getRemovedData()).getPidcVersId();
          try {
            updateRvwResults(pidcVersId);
            updatePidcQnaireInfo(pidcVersId);
            updateA2lStructureModel(pidcVersId);
          }
          catch (ApicWebServiceException exp) {
            CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
          }

        }

      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        deleteA2l(data);
      }
    }
  }


  /**
   * @param data
   */
  private void deleteA2l(final ChangeDataInfo data) {
    PidcA2l delPidcA2l = (PidcA2l) data.getRemovedData();
    PidcTreeNode pidcVerNode = this.pidcVerIdTreenodeMap.get(delPidcA2l.getPidcVersId());
    if (isPidcA2lAvailInMap(delPidcA2l, pidcVerNode)) {
      pidcVerNode.getSdomA2lMap().get(delPidcA2l.getSdomPverName()).remove(delPidcA2l.getId());
    }
  }

  /**
   * @param pidcVerNode
   * @return
   */
  private boolean isSdomA2lMapAvailable(final PidcTreeNode pidcVerNode) {
    return (null != pidcVerNode) && (null != pidcVerNode.getSdomA2lMap()) && !pidcVerNode.getSdomA2lMap().isEmpty();
  }

  /**
   * @param pidcA2l
   * @param pidcVerNode
   * @return
   */
  private boolean isPidcA2lSdomPverAvailInMap(final PidcA2l pidcA2l, final PidcTreeNode pidcVerNode) {
    return pidcVerNode.getSdomA2lMap().containsKey(pidcA2l.getSdomPverName()) &&
        (null != pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName()));
  }

  /**
   * @param data
   */
  private void createUpdateA2l(final ChangeDataInfo data) {
    PidcA2lServiceClient a2lSerClient = new PidcA2lServiceClient();
    try {
      PidcA2l pidcA2l = a2lSerClient.getById(data.getObjId());
      unAssignA2l(pidcA2l);
      PidcTreeNode pidcVerNode = this.pidcVerIdTreenodeMap.get(pidcA2l.getPidcVersId());
      if (isSdomA2lMapAvailable(pidcVerNode)) {
        if (pidcVerNode.getSdomA2lMap().containsKey(pidcA2l.getSdomPverName())) {
          if (null != pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName())) {
            pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName()).put(pidcA2l.getId(), pidcA2l);
          }
          else {
            Map<Long, PidcA2l> pidcA2lMap = new HashMap<>();
            pidcA2lMap.put(pidcA2l.getId(), pidcA2l);
            pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName()).putAll(pidcA2lMap);
          }
        }
        else {
          Map<Long, PidcA2l> pidcA2lMap = new HashMap<>();
          pidcA2lMap.put(pidcA2l.getId(), pidcA2l);
          pidcVerNode.getSdomA2lMap().put(pidcA2l.getSdomPverName(), pidcA2lMap);
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcA2l
   */
  private void unAssignA2l(final PidcA2l pidcA2l) {
    PidcVersionServiceClient verService = new PidcVersionServiceClient();
    try {
      for (Long pidcVerId : verService.getAllPidcVersionForPidc(pidcA2l.getProjectId()).keySet()) {
        PidcTreeNode pidcVerNode = this.pidcVerIdTreenodeMap.get(pidcVerId);
        if (isPidcA2lAvailInMap(pidcA2l, pidcVerNode)) {
          pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName()).remove(pidcA2l.getId());
          pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName());
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcA2l
   * @param pidcVerNode
   * @return
   */
  private boolean isPidcA2lAvailInMap(final PidcA2l pidcA2l, final PidcTreeNode pidcVerNode) {
    return isSdomA2lMapAvailable(pidcVerNode) && isPidcA2lSdomPverAvailInMap(pidcA2l, pidcVerNode) &&
        pidcVerNode.getSdomA2lMap().get(pidcA2l.getSdomPverName()).containsKey(pidcA2l.getId());
  }

  /**
  *
  */
  public void refreshQnaires(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE) ||
          data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        createUpdateQnaire(data);
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.DELETE)) {
        deleteQnaire(data);
      }
    }
  }

  /**
   * @param data
   */
  private void deleteQnaire(final ChangeDataInfo data) {
    RvwQnaireResponse delQnaireResp = (RvwQnaireResponse) data.getRemovedData();
    try {
      // Get all variants linked to the questionnaire response
      List<RvwQnaireRespVariant> rvwQnaireRespVariantList =
          new RvwQnaireRespVariantServiceClient().getRvwQnaireRespVariantList(delQnaireResp.getId());
      // method to update the deleted questionnaire response in a2l structure model
      updateDeletedQnaireRespInA2lStructureModel(delQnaireResp, rvwQnaireRespVariantList);

      PidcTreeNode pidcVerNode = this.pidcVerIdTreenodeMap.get(delQnaireResp.getPidcVersId());
      if ((null != pidcVerNode) && (null != pidcVerNode.getQnaireInfo())) {

        for (RvwQnaireRespVariant rvwQnaireRespVariant : rvwQnaireRespVariantList) {
          if (null != rvwQnaireRespVariant.getVariantId()) {
            pidcVerNode.getQnaireInfo().getVarRespWpQniareMap().get(rvwQnaireRespVariant.getVariantId())
                .get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
                .remove(delQnaireResp.getId());
            pidcVerNode.getQnaireInfo().getRvwQnaireRespMap().remove(delQnaireResp.getId());
          }
          else {
            // update Qnaire Response for no variant
            pidcVerNode.getQnaireInfo().getVarRespWpQniareMap().get(ApicConstants.NO_VARIANT_ID)
                .get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
                .remove(delQnaireResp.getId());
            pidcVerNode.getQnaireInfo().getRvwQnaireRespMap().remove(delQnaireResp.getId());
          }
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param delQnaireResp
   * @param rvwQnaireRespVariantList
   */
  private void updateDeletedQnaireRespInA2lStructureModel(final RvwQnaireResponse delQnaireResp,
      final List<RvwQnaireRespVariant> rvwQnaireRespVariantList) {
    Set<PidcTreeNode> allA2lNodesForPidcVersion = getAllA2lNodesForPidcVersion(delQnaireResp.getPidcVersId());
    // Update Qnaire response in getA2lStructureModel Object in pidctreenode
    for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
      if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel())) {
        for (RvwQnaireRespVariant rvwQnaireRespVariant : rvwQnaireRespVariantList) {
          if (null != rvwQnaireRespVariant.getVariantId()) {
            handleUpdateAndDeleteQnaireRespOfVarNode(delQnaireResp, a2lPidcTreeNode, rvwQnaireRespVariant);
          }
          else {
            handleUpdateAndDeleteQnaireRespOfNoVarNode(delQnaireResp, a2lPidcTreeNode, rvwQnaireRespVariant);
          }
        }
      }
    }
  }

  /**
   * @param delQnaireResp
   * @param a2lPidcTreeNode
   * @param rvwQnaireRespVariant
   */
  private void handleUpdateAndDeleteQnaireRespOfVarNode(final RvwQnaireResponse delQnaireResp,
      final PidcTreeNode a2lPidcTreeNode, final RvwQnaireRespVariant rvwQnaireRespVariant) {
    Map<Long, Map<Long, Set<Long>>> varRespWpQnaireMap =
        a2lPidcTreeNode.getA2lStructureModel().getVarRespWpQniareMap().get(rvwQnaireRespVariant.getVariantId() == null
            ? ApicConstants.NO_VARIANT_ID : rvwQnaireRespVariant.getVariantId());
    if (CommonUtils.isNotEmpty(varRespWpQnaireMap) &&
        varRespWpQnaireMap.containsKey(rvwQnaireRespVariant.getA2lRespId()) &&
        varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).containsKey(rvwQnaireRespVariant.getA2lWpId())) {
      varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
          .remove(delQnaireResp.getId());
    }
    a2lPidcTreeNode.getA2lStructureModel().getRvwQnaireRespMap().remove(delQnaireResp.getId());
  }

  /**
   * @param delQnaireResp
   * @param a2lPidcTreeNode
   */
  private void handleUpdateAndDeleteQnaireRespOfNoVarNode(final RvwQnaireResponse delQnaireResp,
      final PidcTreeNode a2lPidcTreeNode, final RvwQnaireRespVariant rvwQnaireRespVariant) {
    Map<Long, Map<Long, Set<Long>>> varRespWpQnaireMap =
        a2lPidcTreeNode.getA2lStructureModel().getVarRespWpQniareMap().get(ApicConstants.NO_VARIANT_ID);
    if (CommonUtils.isNotEmpty(varRespWpQnaireMap) &&
        varRespWpQnaireMap.containsKey(rvwQnaireRespVariant.getA2lRespId()) &&
        varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).containsKey(rvwQnaireRespVariant.getA2lWpId())) {
      // update Qnaire Response for no variant
      varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
          .remove(delQnaireResp.getId());
    }
    a2lPidcTreeNode.getA2lStructureModel().getRvwQnaireRespMap().remove(delQnaireResp.getId());
  }

  /**
   * @param data
   */
  private void createUpdateQnaire(final ChangeDataInfo data) {
    RvwQnaireResponseServiceClient qnaireRespServiceClient = new RvwQnaireResponseServiceClient();
    RvwQnaireRespVariantServiceClient qnaireRespVariantServiceClient = new RvwQnaireRespVariantServiceClient();
    RvwQnaireRespVersionServiceClient qnaireRespVersionServiceClient = new RvwQnaireRespVersionServiceClient();
    try {
      RvwQnaireResponse qnaireResp = qnaireRespServiceClient.getById(data.getObjId());
      // Get all variants linked to the questionnaire response
      List<RvwQnaireRespVariant> rvwQnaireRespVariantList =
          qnaireRespVariantServiceClient.getRvwQnaireRespVariantList(qnaireResp.getId());

      Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersionMap =
          qnaireRespVersionServiceClient.getQnaireRespVersionsByRespId(qnaireResp.getId());

      if (CommonUtils.isNotEmpty(rvwQnaireRespVariantList)) {
        RvwQnaireRespVariant rvwQnaireRespVariant = rvwQnaireRespVariantList.get(0);
        PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(rvwQnaireRespVariant.getPidcVersId());
        // constructing and fetching the a2l node ids
        Set<PidcTreeNode> allA2lNodesForPidcVersion =
            getAllA2lNodesForPidcVersion(rvwQnaireRespVariant.getPidcVersId());
        // Update Qnaire response in getA2lStructureModel Object in pidctreenode
        for (PidcTreeNode a2lPidcTreeNode : allA2lNodesForPidcVersion) {
          if ((null != a2lPidcTreeNode) && (null != a2lPidcTreeNode.getA2lStructureModel())) {
            // Update Qnaire response in PidcQnaireInfo Object in pidctreenode
            updateQnaireResponseForA2lStructure(qnaireResp, rvwQnaireRespVariantList, rvwQnaireRespVersionMap,
                a2lPidcTreeNode);
          }
        }

        // Construct Review Questionnaire node id
        String nodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() +
            pidcNode.getPidcVersion().getId();
        PidcTreeNode pidcVerNode = this.nodeIdNodeMap.get(nodeId);
        if ((null != pidcVerNode) && (null != pidcVerNode.getQnaireInfo())) {
          // Update Qnaire response in PidcQnaireInfo Object in pidctreenode
          updateQnaireResponse(qnaireResp, rvwQnaireRespVariantList, rvwQnaireRespVersionMap, pidcVerNode);
        }

      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  private void updateQnaireResponseForA2lStructure(final RvwQnaireResponse qnaireResp,
      final List<RvwQnaireRespVariant> rvwQnaireRespVariantList,
      final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersionMap, final PidcTreeNode pidcVerNode) {
    for (RvwQnaireRespVariant rvwQnaireRespVariant : rvwQnaireRespVariantList) {
      Map<Long, Map<Long, Set<Long>>> varRespWpQnaireMap;
      if (null != rvwQnaireRespVariant.getVariantId()) {
        varRespWpQnaireMap =
            pidcVerNode.getA2lStructureModel().getVarRespWpQniareMap().get(rvwQnaireRespVariant.getVariantId());
      }
      else {
        // Update Qnaire response for no variant
        varRespWpQnaireMap =
            pidcVerNode.getA2lStructureModel().getVarRespWpQniareMap().get(ApicConstants.NO_VARIANT_ID);
      }
      // to add the qnaire responsibility only when a matching variant is identified
      if (CommonUtils.isNotEmpty(varRespWpQnaireMap) &&
          varRespWpQnaireMap.containsKey(rvwQnaireRespVariant.getA2lRespId()) &&
          varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).containsKey(rvwQnaireRespVariant.getA2lWpId())) {
        varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
            .add(qnaireResp.getId());
      }
      else {
        break;
      }
      Optional<RvwQnaireRespVersion> rvwQnaireRespWSVers = rvwQnaireRespVersionMap.values().stream()
          .filter(
              rvwQnaireRespVer -> CommonUtils.isEqual(rvwQnaireRespVer.getRevNum(), CDRConstants.WORKING_SET_REV_NUM))
          .collect(Collectors.toList()).stream().findFirst();
      // map only working set version for the resp id in the map bcy status and tooltip is based on the working set
      // version
      if (rvwQnaireRespWSVers.isPresent()) {
        pidcVerNode.getA2lStructureModel().getRvwQnaireRespVersMap().put(qnaireResp.getId(), rvwQnaireRespWSVers.get());
      }
      pidcVerNode.getA2lStructureModel().getRvwQnaireRespMap().put(qnaireResp.getId(), qnaireResp);
    }
  }

  private Set<PidcTreeNode> getAllA2lNodesForPidcVersion(final Long pidcVersionId) {
    Set<PidcTreeNode> a2lPidcTreeNodes = new HashSet<>();
    try {
      PidcTreeNode pidcNode = this.pidcVerIdTreenodeMap.get(pidcVersionId);
      if (pidcNode.getSdomA2lMap().isEmpty()) {
        Map<String, PidcSdomA2lInfo> sdomPverA2lMap = new PidcTreeViewServiceClient().getPidcSdomPvers(pidcVersionId);
        for (PidcSdomA2lInfo sdomA2lInfo : sdomPverA2lMap.values()) {
          pidcNode.getSdomA2lMap().put(sdomA2lInfo.getSdomPver().getPverName(), sdomA2lInfo.getA2lMap());
        }
      }
      for (Entry<String, Map<Long, PidcA2l>> sdomPverA2lEntrySet : pidcNode.getSdomA2lMap().entrySet()) {
        for (Entry<Long, PidcA2l> a2lEntrySet : sdomPverA2lEntrySet.getValue().entrySet()) {
          String a2lNodeId = pidcNode.getNodeId() + LEVEL_SEPARATOR + SDOM_PVER_PREFIX + sdomPverA2lEntrySet.getKey() +
              LEVEL_SEPARATOR + PIDC_A2L_PREFIX + a2lEntrySet.getKey();
          if (this.nodeIdNodeMap.containsKey(a2lNodeId)) {
            a2lPidcTreeNodes.add(this.nodeIdNodeMap.get(a2lNodeId));
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
    return a2lPidcTreeNodes;
  }

  /**
   * @param qnaireResp
   * @param rvwQnaireRespVariantList
   * @param rvwQnaireRespVersionMap
   * @param pidcVerNode
   */
  private void updateQnaireResponse(final RvwQnaireResponse qnaireResp,
      final List<RvwQnaireRespVariant> rvwQnaireRespVariantList,
      final Map<Long, RvwQnaireRespVersion> rvwQnaireRespVersionMap, final PidcTreeNode pidcVerNode) {
    for (RvwQnaireRespVariant rvwQnaireRespVariant : rvwQnaireRespVariantList) {

      if (null != rvwQnaireRespVariant.getVariantId()) {
        Map<Long, Map<Long, Set<Long>>> varRespWpQnaireMap =
            pidcVerNode.getQnaireInfo().getVarRespWpQniareMap().get(rvwQnaireRespVariant.getVariantId());
        if (CommonUtils.isNotEmpty(varRespWpQnaireMap) &&
            varRespWpQnaireMap.containsKey(rvwQnaireRespVariant.getA2lRespId()) && varRespWpQnaireMap
                .get(rvwQnaireRespVariant.getA2lRespId()).containsKey(rvwQnaireRespVariant.getA2lWpId())) {
          varRespWpQnaireMap.get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId())
              .add(qnaireResp.getId());
        }
      }
      else {
        // Update Qnaire response for no variant
        pidcVerNode.getQnaireInfo().getVarRespWpQniareMap().get(ApicConstants.NO_VARIANT_ID)
            .get(rvwQnaireRespVariant.getA2lRespId()).get(rvwQnaireRespVariant.getA2lWpId()).add(qnaireResp.getId());
      }
      Optional<RvwQnaireRespVersion> rvwQnaireRespWSVers = rvwQnaireRespVersionMap.values().stream()
          .filter(
              rvwQnaireRespVer -> CommonUtils.isEqual(rvwQnaireRespVer.getRevNum(), CDRConstants.WORKING_SET_REV_NUM))
          .collect(Collectors.toList()).stream().findFirst();
      // map only working set version for the resp id in the map bcy status and tooltip is based on the working set
      // version
      if (rvwQnaireRespWSVers.isPresent()) {
        pidcVerNode.getQnaireInfo().getRvwQnaireRespVersMap().put(qnaireResp.getId(), rvwQnaireRespWSVers.get());
      }
      pidcVerNode.getQnaireInfo().getRvwQnaireRespMap().put(qnaireResp.getId(), qnaireResp);
    }
  }

  /**
  *
  */
  private void refreshAttrValue(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      try {
        Pidc modPidc = new PidcServiceClient().getByNameValueId(data.getObjId());
        if (null != modPidc) {
          createPidcInTree(new HashSet<>(Arrays.asList(modPidc.getId())));
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
  private void refreshPidc(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    Set<Long> pidcIds = new HashSet<>();
    Set<Long> pidcIdsUpd = new HashSet<>();
    for (ChangeDataInfo data : chDataInfoMap.values()) {
      if (data.getChangeType().equals(CHANGE_OPERATION.CREATE)) {
        pidcIds.add(data.getObjId());
      }
      else if (data.getChangeType().equals(CHANGE_OPERATION.UPDATE)) {
        pidcIdsUpd.add(data.getObjId());
      }
    }
    if (!pidcIds.isEmpty()) {
      createPidcInTree(pidcIds);
    }
    if (!pidcIdsUpd.isEmpty()) {
      updatePidcInTree(pidcIdsUpd);
    }
  }

  /**
   * @param pidcIds
   */
  private void updatePidcInTree(final Set<Long> pidcIds) {
    try {
      Map<Long, PidcVersionInfo> pidcVerInfoMap =
          new PidcVersionServiceClient().getActiveVersionsWithStructure(pidcIds);
      updatePidcChange(pidcVerInfoMap);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param pidcVerInfoMap
   */
  void updatePidcChange(final Map<Long, PidcVersionInfo> pidcVerInfoMap) {
    Map<Long, PidcTreeNode> verIdTreenodeMap = new HashMap<>();
    verIdTreenodeMap.putAll(this.pidcVerIdTreenodeMap);
    for (PidcVersionInfo verInfo : pidcVerInfoMap.values()) {
      for (PidcTreeNode pidcTreeNode : verIdTreenodeMap.values()) {
        if (pidcTreeNode.getPidc().getId().equals(verInfo.getPidc().getId())) {
          this.pidcVerIdTreenodeMap.remove(pidcTreeNode.getPidcVersion().getId());
          if ((null != pidcTreeNode.getParentNodeId()) &&
              (null != this.nodeIdNodeMap.get(pidcTreeNode.getParentNodeId())) &&
              (null != this.nodeIdNodeMap.get(pidcTreeNode.getParentNodeId()).getChildNodesMap())) {
            this.nodeIdNodeMap.get(pidcTreeNode.getParentNodeId()).getChildNodesMap().remove(pidcTreeNode.getNodeId());
          }
          this.nodeIdNodeMap.remove(pidcTreeNode.getNodeId());
        }
      }
      createPidcNodeForLvlAttrNode(verInfo);
    }
  }

  /**
   * @param pidcIds
   */
  private void createPidcInTree(final Set<Long> pidcIds) {
    try {
      Map<Long, PidcVersionInfo> pidcVerInfoMap =
          new PidcVersionServiceClient().getActiveVersionsWithStructure(pidcIds);
      createPidcNodeRefresh(pidcVerInfoMap);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcVerInfoMap
   */
  public void createPidcNodeRefresh(final Map<Long, PidcVersionInfo> pidcVerInfoMap) {
    for (PidcVersionInfo verInfo : pidcVerInfoMap.values()) {
      createPidcNodeForLvlAttrNode(verInfo);
    }
  }

  /**
  *
  */
  private void mergeNewPidcVerWithTree(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      try {
        PidcVersion newValue = new PidcVersionServiceClient().getById(changeData.getObjId());
        int pidcStructMaxLvl = new ApicDataBO().getPidcStructMaxLvl();
        Map<Long, PidcVersionInfo> pidcVerInfoMap = getPidcVerInfoMap(newValue);
        mergeNewPidcVersSubMethod(pidcStructMaxLvl, pidcVerInfoMap);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param pidcStructMaxLvl
   * @param pidcVerInfoMap
   */
  private void mergeNewPidcVersSubMethod(final int pidcStructMaxLvl, final Map<Long, PidcVersionInfo> pidcVerInfoMap) {
    if (null != pidcVerInfoMap) {
      PidcVersionInfo currVerNode = pidcVerInfoMap.values().iterator().next();
      String parentNodeId = getParentLvlAttrNodeId(currVerNode, pidcStructMaxLvl);
      PidcTreeNode parentNode = this.nodeIdNodeMap.get(parentNodeId);
      if (null != this.oldActiveVerNodeId) {
        if (null != this.nodeIdNodeMap.get(this.oldActiveVerNodeId)) {
          this.pidcVerIdTreenodeMap.remove(this.nodeIdNodeMap.get(this.oldActiveVerNodeId).getPidcVersion().getId());
        }
        this.nodeIdNodeMap.remove(this.oldActiveVerNodeId);
        if ((null != parentNode) && (null != parentNode.getChildNodesMap())) {
          parentNode.getChildNodesMap().remove(this.oldActiveVerNodeId);
        }
      }
      PidcTreeNode newActiveVerNode = createPidcVerTreeNodeObject(pidcStructMaxLvl + 1, parentNodeId, currVerNode);
      addPidcNodesToMap(parentNodeId, newActiveVerNode);
      updatePidcInTree(new HashSet<>(Arrays.asList(newActiveVerNode.getPidc().getId())));
    }
  }

  /**
   * @param newValue
   * @param pidcVerInfoMap
   * @return
   */
  private Map<Long, PidcVersionInfo> getPidcVerInfoMap(final PidcVersion newValue) {
    try {
      return new PidcVersionServiceClient()
          .getActiveVersionsWithStructure(new HashSet<>(Arrays.asList(newValue.getPidcId())));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return null;
  }

  /**
   * Method to get the root node of Pidc Tree
   *
   * @return PidcTree Root Node element
   */
  PidcTreeNode getPidcTreeRootNode() {
    ApicDataBO apicDataBO = new ApicDataBO();
    Map<Long, Attribute> allLvlAttrByLevel = apicDataBO.getAllLvlAttrByLevel();
    PidcTreeNode rootNodeObj = createLvlAttrTreeNodeObject(allLvlAttrByLevel.get(ApicConstants.PIDC_ROOT_LEVEL + 1L),
        new AttributeValue(), ApicConstants.PIDC_ROOT_LEVEL, null);
    loadLevelAttrNodeChildren(rootNodeObj, allLvlAttrByLevel, apicDataBO.getAllPidTreeLvlAttrValueMap(),
        apicDataBO.getLvlAttrValDepMap());
    loadPidcVersions();
    return rootNodeObj;
  }

  /**
   * Method to load the Pidc version nodes
   */
  private void loadPidcVersions() {
    Map<Long, PidcVersionInfo> pidcActVerMap = new HashMap<>();
    PidcVersionServiceClient pidcVerSerClient = new PidcVersionServiceClient();
    try {
      pidcActVerMap = pidcVerSerClient.getAllActiveVersionsWithStructure();
      createPidcNodeRefresh(pidcActVerMap);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param pidcStructMaxLvl
   * @param pidcVer
   * @return
   */
  private PidcTreeNode createPidcNodeForLvlAttrNode(final PidcVersionInfo pidcVer) {
    ApicDataBO apicBO = new ApicDataBO();
    int pidcStructMaxLvl = apicBO.getPidcStructMaxLvl();
    String parentNodeId = getParentLvlAttrNodeId(pidcVer, pidcStructMaxLvl);
    PidcTreeNode pidcVerNode = createPidcVerTreeNodeObject(pidcStructMaxLvl + 1, parentNodeId, pidcVer);
    addPidcNodesToMap(parentNodeId, pidcVerNode);
    return pidcVerNode;
  }

  /**
   * @param pidcVer Pidc version info
   * @param pidcStructMaxLvl pidc max struct attr level
   * @return parent level attr node id
   */
  public String getParentLvlAttrNodeId(final PidcVersionInfo pidcVer, final int pidcStructMaxLvl) {
    Map<Long, PidcVersionAttribute> pidcLvlAttrMap = pidcVer.getLevelAttrMap();
    StringBuilder lvlAttrNodesId = new StringBuilder();
    lvlAttrNodesId.append(ROOT_NODE_ID).append(LEVEL_SEPARATOR);

    for (int lvl = 1; lvl <= pidcStructMaxLvl; lvl++) {
      lvlAttrNodesId.append(VAL_PREFIX);
      lvlAttrNodesId.append(pidcLvlAttrMap.get(Long.valueOf(lvl)).getValueId());
      if (lvl != pidcStructMaxLvl) {
        lvlAttrNodesId.append(LEVEL_SEPARATOR);
      }
    }
    return lvlAttrNodesId.toString();
  }

  /**
   * Method to add the Pidc version nodes to map
   *
   * @param parentNodeId
   * @param pidcVerNode
   */
  public void addPidcNodesToMap(final String parentNodeId, final PidcTreeNode pidcVerNode) {
    PidcTreeNode parentNode = this.nodeIdNodeMap.get(parentNodeId);
    if (null != parentNode) {
      fillUnDelChildMap(pidcVerNode, parentNode);
      if (null != parentNode.getChildNodesMap()) {
        if (!parentNode.getChildNodesMap().isEmpty()) {
          parentNode.getChildNodesMap().put(pidcVerNode.getNodeId(), pidcVerNode);
        }
        else {
          createMapIfEmpty(pidcVerNode, parentNode);
        }
      }
      else {
        createMapIfEmpty(pidcVerNode, parentNode);
      }
    }
  }

  /**
   * @param pidcVerNode
   * @param parentNode
   */
  private void fillUnDelChildMap(final PidcTreeNode pidcVerNode, final PidcTreeNode parentNode) {
    if (!pidcVerNode.getPidcVersion().isDeleted()) {
      if (null != parentNode.getUnDelchildNodesMap()) {
        if (!parentNode.getUnDelchildNodesMap().isEmpty()) {
          parentNode.getUnDelchildNodesMap().put(pidcVerNode.getNodeId(), pidcVerNode);
        }
        else {
          createUnDelChildMapIfEmpty(pidcVerNode, parentNode);
        }
      }
      else {
        createUnDelChildMapIfEmpty(pidcVerNode, parentNode);
      }
    }
  }

  /**
   * @param pidcVerNode
   * @param parentNode
   */
  private void createUnDelChildMapIfEmpty(final PidcTreeNode pidcVerNode, final PidcTreeNode parentNode) {
    Map<String, PidcTreeNode> childPidcVerNodes = new TreeMap<>();
    childPidcVerNodes.put(pidcVerNode.getNodeId(), pidcVerNode);
    parentNode.setUnDelchildNodesMap(childPidcVerNodes);
  }

  /**
   * @param pidcVerNode
   * @param parentNode
   */
  private void createMapIfEmpty(final PidcTreeNode pidcVerNode, final PidcTreeNode parentNode) {
    Map<String, PidcTreeNode> childPidcVerNodes = new TreeMap<>();
    childPidcVerNodes.put(pidcVerNode.getNodeId(), pidcVerNode);
    parentNode.setChildNodesMap(childPidcVerNodes);
  }

  /**
   * Method to create the tree node object for Pidc version level nodes
   *
   * @param nodeLevel pidc version node level
   * @param parentNodeId lvl attr node id
   * @param pidcVer Pidc version info
   * @return Treenode object for pidc version node
   */
  public PidcTreeNode createPidcVerTreeNodeObject(final int nodeLevel, final String parentNodeId,
      final PidcVersionInfo pidcVer) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNodeId + LEVEL_SEPARATOR + PIDC_VER_PREFIX + pidcVer.getPidcVersion().getId());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    pidcNode.setName(pidcVer.getPidcVersion().getName());
    pidcNode.setLevel(nodeLevel);
    pidcNode.setParentNodeId(parentNodeId);
    pidcNode.setPidcVerInfo(pidcVer);
    pidcNode.setPidcVersion(pidcVer.getPidcVersion());
    pidcNode.setPidc(pidcVer.getPidc());
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION);
    pidcNode.setNodeId(parentNodeId + LEVEL_SEPARATOR + PIDC_VER_PREFIX + pidcVer.getPidcVersion().getId());
    this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    this.pidcVerIdTreenodeMap.put(pidcVer.getPidcVersion().getId(), pidcNode);
    return pidcNode;
  }

  /**
   * Method to load the structure attribute level children
   *
   * @param rootNode
   * @param levelAttrMap key- attr level, value - attribute
   * @param attrValMap key- attr id, value - set of attribute values
   * @param map
   */
  private void loadLevelAttrNodeChildren(final PidcTreeNode rootNode, final Map<Long, Attribute> levelAttrMap,
      final Map<Long, Map<Long, AttributeValue>> attrValMap, final Map<Long, Map<Long, Set<Long>>> map) {
    for (AttributeValue attrVal : attrValMap.get(rootNode.getNodeAttr().getId()).values()) {
      PidcTreeNode currNode = createLvlAttrTreeNodeObject(rootNode.getNodeAttr(), attrVal,
          rootNode.getNodeAttr().getLevel().intValue(), rootNode);
      this.nodeIdNodeMap.put(currNode.getNodeId(), currNode);
      addLvlAttrInfoToMap(rootNode, currNode);
      createChildNodes(currNode, levelAttrMap, attrValMap, map);
    }
  }

  /**
   * Method to add level attribute information for the parent nodes
   *
   * @param rootNode
   * @param currNode
   */
  private void addLvlAttrInfoToMap(final PidcTreeNode rootNode, final PidcTreeNode currNode) {
    if (null != this.parentIdChildNodesMap) {
      if (null != this.parentIdChildNodesMap.get(rootNode.getNodeId())) {
        if (!this.parentIdChildNodesMap.get(rootNode.getNodeId()).isEmpty()) {
          SortedSet<PidcTreeNode> valSet = new TreeSet<>();
          valSet.addAll(this.parentIdChildNodesMap.get(rootNode.getNodeId()));
          valSet.add(currNode);
          this.parentIdChildNodesMap.put(rootNode.getNodeId(), valSet);
        }
        else {
          createTreeNodeSetIfEmpty(rootNode, currNode);
        }
      }
      else {
        createTreeNodeSetIfEmpty(rootNode, currNode);
      }
    }
  }

  /**
   * Method to create a new set of pidc tree node objects if not available
   *
   * @param rootNode
   * @param currNode
   */
  private void createTreeNodeSetIfEmpty(final PidcTreeNode rootNode, final PidcTreeNode currNode) {
    SortedSet<PidcTreeNode> valSet = new TreeSet<>();
    valSet.add(currNode);
    this.parentIdChildNodesMap.put(rootNode.getNodeId(), valSet);
  }

  /**
   * Create child tree nodes (structure attribute level)
   *
   * @param currNode
   * @param attrValMap
   * @param levelAttrMap
   * @param map
   */
  private void createChildNodes(final PidcTreeNode currNode, final Map<Long, Attribute> levelAttrMap,
      final Map<Long, Map<Long, AttributeValue>> attrValMap, final Map<Long, Map<Long, Set<Long>>> map) {
    ApicDataBO apicBO = new ApicDataBO();
    if (currNode.getLevel() < apicBO.getPidcStructMaxLvl()) {
      Attribute nodeAttr = levelAttrMap.get(currNode.getLevel() + 1L);
      for (AttributeValue attrVal : attrValMap.get(nodeAttr.getId()).values()) {
        createChildNodeExtractedMethod(currNode, levelAttrMap, attrValMap, map, nodeAttr, attrVal);
      }
    }
  }

  /**
   * @param currNode
   * @param levelAttrMap
   * @param attrValMap
   * @param map
   * @param nodeAttr
   * @param attrVal
   */
  private void createChildNodeExtractedMethod(final PidcTreeNode currNode, final Map<Long, Attribute> levelAttrMap,
      final Map<Long, Map<Long, AttributeValue>> attrValMap, final Map<Long, Map<Long, Set<Long>>> map,
      final Attribute nodeAttr, final AttributeValue attrVal) {
    // flag to indicate whether the node needs to be created
    boolean validNode = true;
    boolean parentLvlAttrAsDepn = false;
    Map<Long, Set<Long>> depnAttrValMap = map.get(attrVal.getId());
    if (CommonUtils.isNotEmpty(depnAttrValMap)) {
      validNode = false;
      // only if the attr value has dependencies
      PidcTreeNode parentNode = currNode;
      while (parentNode != null) {
        // iterate the parent node till the top of the tree
        Set<Long> valueIds = depnAttrValMap.get(parentNode.getNodeAttr().getId());
        if (CommonUtils.isNotEmpty(valueIds)) {
          parentLvlAttrAsDepn = true;
          validNode = checkValueIDs(validNode, parentNode, valueIds);
        }
        parentNode = parentNode.getParentNode();
      }
    }

    if (validNode || (!parentLvlAttrAsDepn)) {
      createNode(currNode, levelAttrMap, attrValMap, map, nodeAttr, attrVal);
    }
  }

  /**
   * @param validNode as boolean
   * @param parentNode as parentNode
   * @param valueIds as valueIds
   * @return
   */
  private boolean checkValueIDs(final boolean validNode, final PidcTreeNode parentNode, final Set<Long> valueIds) {
    boolean validNod = validNode;
    for (Long valId : valueIds) {
      if ((parentNode.getNodeAttrValue().getId() != null) &&
          (parentNode.getNodeAttrValue().getId().longValue() == valId.longValue())) {
        validNod = true;
        break;
      }
    }
    return validNod;
  }

  /**
   * @param currNode
   * @param levelAttrMap
   * @param attrValMap
   * @param map
   * @param nodeAttr
   * @param attrVal
   */
  private void createNode(final PidcTreeNode currNode, final Map<Long, Attribute> levelAttrMap,
      final Map<Long, Map<Long, AttributeValue>> attrValMap, final Map<Long, Map<Long, Set<Long>>> map,
      final Attribute nodeAttr, final AttributeValue attrVal) {
    // create the node only if the attribute dependency
    PidcTreeNode childNode = createLvlAttrTreeNodeObject(nodeAttr, attrVal, nodeAttr.getLevel().intValue(), currNode);
    this.nodeIdNodeMap.put(childNode.getNodeId(), childNode);
    if (null != this.parentIdChildNodesMap.get(currNode.getNodeId())) {
      if (!this.parentIdChildNodesMap.get(currNode.getNodeId()).isEmpty()) {
        SortedSet<PidcTreeNode> valSet = new TreeSet<>();
        valSet.addAll(this.parentIdChildNodesMap.get(currNode.getNodeId()));
        valSet.add(childNode);
        this.parentIdChildNodesMap.put(currNode.getNodeId(), valSet);
      }
      else {
        createTreeNodeSetIfEmpty(currNode, childNode);
      }
    }
    else {
      createTreeNodeSetIfEmpty(currNode, childNode);
    }
    createChildNodes(childNode, levelAttrMap, attrValMap, map);
  }


  /**
   * @param parentNode parent pidc version node
   * @param loadA2l is a2l to be loaded
   * @param loadVariants is variant to be loaded
   * @param loadCdr is cdr to be loaded
   * @param loadQnaires is questionnaires to be loaded
   * @return PidcTreeNodeChildren
   */
  public PidcTreeNodeChildren getPidcNodeChildAvailblty(final PidcTreeNode parentNode) {
    PidcTreeViewServiceClient pidcTreeViewSerClient = new PidcTreeViewServiceClient();
    PidcTreeNodeChildren nodeChildrenAvalblty = null;
    try {
      nodeChildrenAvalblty = pidcTreeViewSerClient.getPidcNodeChildAvailblty(parentNode.getPidcVersion().getPidcId(),
          parentNode.getPidcVersion().getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return nodeChildrenAvalblty;
  }

  /**
   * @param parentNode parent pidc version node
   * @param pidcSdomPverSet sdom pvers of pidc version
   * @return Set of sdom pver tree nodes
   */
  public Set<PidcTreeNode> getSdomPverNodes(final PidcTreeNode parentNode, final Set<SdomPVER> pidcSdomPverSet) {
    SortedSet<PidcTreeNode> sdomPverNodes = new TreeSet<>();
    PidcA2lServiceClient pidcA2lSerClient = new PidcA2lServiceClient();
    for (SdomPVER sdomPver : pidcSdomPverSet) {
      Map<Long, PidcA2l> pidcA2lMap = new HashMap<>();
      try {
        pidcA2lMap = pidcA2lSerClient.getA2LFileBySdom(parentNode.getPidcVersion().getId(), sdomPver.getPverName());
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }

      PidcTreeNode sdomPverNode = createSdomTreeNodeObject(parentNode, sdomPver);
      parentNode.getSdomA2lMap().put(sdomPver.getPverName(), pidcA2lMap);
      sdomPverNodes.add(sdomPverNode);

    }
    List<PidcTreeNode> sdomPverList = new ArrayList<>();
    sdomPverList.addAll(sdomPverNodes);
    parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.SDOM_PVER.getUiType(), sdomPverList);
    return sdomPverNodes;
  }

  /**
   * @param parentNode
   * @param sdomPver
   * @return
   */
  private PidcTreeNode createSdomTreeNodeObject(final PidcTreeNode parentNode, final SdomPVER sdomPver) {

    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + SDOM_PVER_PREFIX + sdomPver.getPverName());

    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    pidcNode.setName(sdomPver.getPverName());
    pidcNode.setLevel(parentNode.getLevel() + 1);
    pidcNode.setParentNodeId(parentNode.getNodeId());
    pidcNode.setParentNode(parentNode);
    pidcNode.setPidcVersion(parentNode.getPidcVersion());
    pidcNode.setPidc(parentNode.getPidc());
    pidcNode.setSdomPver(sdomPver);
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.SDOM_PVER);
    pidcNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + SDOM_PVER_PREFIX + sdomPver.getPverName());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * Method to get set of otehr pidc version nodes
   *
   * @param parentNode parent node for which other pidc version nodes to be fetched
   * @return set of other pidc version nodes
   */
  public SortedSet<PidcTreeNode> getOtherPidcVerNodes(final PidcTreeNode parentNode) {
    SortedSet<PidcTreeNode> otherPidVerNodes = new TreeSet<>();
    PidcTreeNode activeVerNode = this.nodeIdNodeMap.get(parentNode.getParentNodeId());
    Set<PidcVersion> allPidcVerSet = getOtherPidcVer(activeVerNode.getPidc());
    for (PidcVersion pidVer : allPidcVerSet) {
      otherPidVerNodes.add(createOtherVerTreeNodeObject(pidVer, activeVerNode.getPidc(), parentNode));
    }
    return otherPidVerNodes;
  }

  /**
   * @param parentNode parent pidc node
   * @return boolen to indicate presence of a2l files
   */
  public boolean isA2lFilePresent(final PidcTreeNode parentNode) {
    PidcA2lServiceClient pidcA2lSerClient = new PidcA2lServiceClient();
    try {
      return pidcA2lSerClient.isPidcA2lPresent(parentNode.getPidcVersion().getId(),
          parentNode.getSdomPver().getPverName());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @param parentNode paren tree node
   * @param includeInactiveA2L true if inactive A2L Files are to be dispalyed
   * @return set of a2l nodes
   */
  public SortedSet<PidcTreeNode> getA2lNodeList(final PidcTreeNode parentNode, final boolean includeInactiveA2L) {
    SortedSet<PidcTreeNode> pidcA2lSet = new TreeSet<>();
    PidcTreeNode pidcVerNode = parentNode.getParentNode();
    if ((pidcVerNode.getSdomA2lMap().get(parentNode.getName()) != null) && includeInactiveA2L) {
      for (PidcA2l pidcA2l : pidcVerNode.getSdomA2lMap().get(parentNode.getName()).values()) {
        pidcA2lSet.add(createPidcA2lTreeNodeObject(parentNode, pidcA2l));
      }
    }
    else if ((pidcVerNode.getSdomA2lMap().get(parentNode.getName()) != null) && !includeInactiveA2L) {
      for (PidcA2l pidcA2l : pidcVerNode.getSdomA2lMap().get(parentNode.getName()).values()) {
        if (pidcA2l.isActive()) {
          pidcA2lSet.add(createPidcA2lTreeNodeObject(parentNode, pidcA2l));
        }
      }
    }
    return pidcA2lSet;
  }

  /**
   * @param parentNode sdom pver node
   * @param pidcA2l pidc a2l obj instance
   * @return Tree node object for Pidc a2l
   */
  private PidcTreeNode createPidcA2lTreeNodeObject(final PidcTreeNode parentNode, final PidcA2l pidcA2l) {

    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_A2L_PREFIX + pidcA2l.getId());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    StringBuilder pidcA2lName = new StringBuilder();
    if (null != pidcA2l.getSdomPverVarName()) {
      pidcA2lName.append(pidcA2l.getSdomPverVarName());
      pidcA2lName.append(" : ");
    }
    pidcA2lName.append(pidcA2l.getName());
    pidcNode.setName(pidcA2lName.toString());
    pidcNode.setLevel(parentNode.getLevel() + 1);
    pidcNode.setParentNodeId(parentNode.getNodeId());
    pidcNode.setPidcVersion(parentNode.getPidcVersion());
    pidcNode.setPidc(parentNode.getPidc());
    pidcNode.setPidcA2l(pidcA2l);
    pidcNode.setParentNode(parentNode);
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.PIDC_A2L);
    pidcNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_A2L_PREFIX + pidcA2l.getId());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * Method to fetch other pidc versions for a pidc from web service
   *
   * @param pidc pidc
   * @return set of other pidc versions
   */
  public Set<PidcVersion> getOtherPidcVer(final Pidc pidc) {
    Set<PidcVersion> allPidcVerSet = new HashSet<>();
    try {
      Collection<PidcVersion> otherPidcVersions =
          (new PidcVersionServiceClient().getOtherVersionsForPidc(pidc.getId())).values();
      allPidcVerSet.addAll(otherPidcVersions);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return allPidcVerSet;
  }

  /**
   * Method to create tree node object at the other pidc version node level
   *
   * @param pidVer other pidc version
   * @param pidc
   * @param parentNode parent PidcTreeNode
   * @return PidcTreeNode
   */
  private PidcTreeNode createOtherVerTreeNodeObject(final PidcVersion pidVer, final Pidc pidc,
      final PidcTreeNode parentNode) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + OTHER_VER_PREFIX + pidVer.getId());

    if (pidcNode == null) {

      pidcNode = new PidcTreeNode();
    }
    pidcNode.setName(pidVer.getVersionName());
    pidcNode.setLevel(parentNode.getLevel() + 1);
    pidcNode.setParentNodeId(parentNode.getNodeId());
    pidcNode.setPidcVersion(pidVer);
    pidcNode.setPidc(pidc);
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.OTHER_PIDC_VERSION);
    pidcNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + OTHER_VER_PREFIX + pidVer.getId());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    if (null == this.pidcVerIdTreenodeMap.get(pidVer.getId())) {
      this.pidcVerIdTreenodeMap.put(pidVer.getId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * Create pidc tree nodes (structure attribute level)
   *
   * @param rootNodeAttr
   * @param object
   * @param rootNodeLevel
   * @return level attr tree node object
   */
  private PidcTreeNode createLvlAttrTreeNodeObject(final Attribute nodeAttr, final AttributeValue nodeAttrVal,
      final int nodeLevel, final PidcTreeNode parentNode) {
    PidcTreeNode pidcNode = new PidcTreeNode();
    pidcNode.setName(nodeAttrVal.getName());
    pidcNode.setNodeAttr(nodeAttr);
    pidcNode.setNodeAttrValue(nodeAttrVal);
    pidcNode.setLevel(nodeLevel);
    if (null != parentNode) {
      pidcNode.setParentNodeId(parentNode.getNodeId());
      pidcNode.setParentNode(parentNode);
    }
    else {
      pidcNode.setParentNodeId(ROOT_NODE_ID);
    }
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE);
    if (nodeLevel == ApicConstants.PIDC_ROOT_LEVEL) {
      pidcNode.setNodeId(ROOT_NODE_ID);
    }
    else {
      pidcNode.setNodeId(pidcNode.getParentNodeId() + LEVEL_SEPARATOR + VAL_PREFIX + nodeAttrVal.getId());
    }
    this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    return pidcNode;
  }

  /**
   * @param pidcNode parent pidc version node
   * @return boolean to indicate presence of cdr results
   */
  public boolean hasCdrResults(final PidcTreeNode pidcNode) {
    try {
      return new CDRReviewResultServiceClient().hasPidcRevResults(pidcNode.getPidcVersion().getId());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @param pidcA2lId as PIDCA2LID
   * @return PidcA2lTreeStructureModel
   * @throws ApicWebServiceException as Exception
   */
  public PidcA2lTreeStructureModel getPidcA2lStructureModel(final Long pidcA2lId) throws ApicWebServiceException {
    return new PidcA2lTreeStructureServiceClient().getPidcA2lTreeStructuresModel(pidcA2lId);
  }

  /**
   * @param parentNode
   */
  public void loadPidcA2lStructureModel(final PidcTreeNode parentNode) {
    if (CommonUtils.isNull(parentNode.getA2lStructureModel())) {
      try {
        parentNode.setA2lStructureModel(getPidcA2lStructureModel(parentNode.getPidcA2l().getId()));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * Get PidcVariants for PIDC A2l
   *
   * @param pidcA2lNodeElement
   * @param includeDeletedVariant
   * @return
   */
  public Object[] getVariantsForPidcA2l(final PidcTreeNode pidcA2lNodeElement, final boolean includeDeletedVariant) {
    SortedSet<PidcTreeNode> qnaireVariantNodes = new TreeSet<>();
    Map<Long, PidcVariant> pidcVariantMap = pidcA2lNodeElement.getA2lStructureModel().getPidcVariantMap();
    if (pidcVariantMap.isEmpty() &&
        pidcA2lNodeElement.getA2lStructureModel().getVarRespWpQniareMap().containsKey(ApicConstants.NO_VARIANT_ID)) {
      String noVariant = NO_VARIANT;
      PidcVariant var = new PidcVariant();
      var.setId(ApicConstants.NO_VARIANT_ID);
      var.setName(noVariant);
      addVariantToPIDCA2LTreeNode(pidcA2lNodeElement, qnaireVariantNodes, var);
    }
    else {
      pidcVariantMap.entrySet().forEach(pidcVar -> {
        PidcVariant var = pidcVar.getValue();
        if (includeDeletedVariant) {
          addVariantToPIDCA2LTreeNode(pidcA2lNodeElement, qnaireVariantNodes, var);
        }
        else {
          // To Display only not deleted questionnaire response
          if (!var.isDeleted()) {
            addVariantToPIDCA2LTreeNode(pidcA2lNodeElement, qnaireVariantNodes, var);
          }
        }
      });
    }
    return qnaireVariantNodes.toArray();
  }

  /**
   * @param parentNodeElement parent pidc version node
   * @param includeDeletedVariant to send deleted status
   * @return set of qnaire variant nodes
   */
  public Object[] getQuestionnaireVariants(final PidcTreeNode parentNodeElement, final boolean includeDeletedVariant,
      final Object[] variantListForSelectedA2L) {
    if (null == parentNodeElement.getQnaireInfo()) {
      PidcTreeNode rvwQnaire = new PidcTreeNode();
      if (parentNodeElement.getParentNode().getNodeType().equals(PIDC_TREE_NODE_TYPE.PIDC_A2L)) {
        rvwQnaire = parentNodeElement.getParentNode().getParentNode().getParentNode().getPidcChildrenMap()
            .get(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType()).get(0);
      }
      else if (parentNodeElement.getParentNode().getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE)) {
        rvwQnaire = parentNodeElement.getParentNode();
      }
      else if (parentNodeElement.getParentNode().getNodeType().equals(PIDC_TREE_NODE_TYPE.SDOM_PVER)) {
        rvwQnaire = parentNodeElement.getParentNode().getParentNode().getPidcChildrenMap()
            .get(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType()).get(0);
      }
      if (CommonUtils.isNotNull(rvwQnaire.getQnaireInfo())) {
        PidcQnaireInfo qnaireInfo = rvwQnaire.getQnaireInfo();
        parentNodeElement.setQnaireInfo(qnaireInfo);
      }
    }
    return getQnaireVariantNodes(parentNodeElement.getQnaireInfo(), parentNodeElement, includeDeletedVariant,
        variantListForSelectedA2L).toArray();
  }

  private SortedSet<PidcTreeNode> getQnaireVariantNodes(final PidcQnaireInfo qnaireResp,
      final PidcTreeNode parentNodeElement, final boolean includeDeletedVariant,
      final Object[] variantListForSelectedA2L) {
    SortedSet<PidcTreeNode> qnaireVariantNodes = new TreeSet<>();
    qnaireResp.getVarRespWpQniareMap().entrySet().forEach(varRespWpQnaireMap -> {
      // Condition for No-Variant case
      if (varRespWpQnaireMap.getKey().equals(ApicConstants.NO_VARIANT_ID)) {
        String noVariant = NO_VARIANT;
        PidcVariant var = new PidcVariant();
        var.setId(ApicConstants.NO_VARIANT_ID);
        var.setName(noVariant);
        addVariantToPIDCTreeNode(parentNodeElement, qnaireVariantNodes, var);
      }
      else {
        if (CommonUtils.isEqualIgnoreCase(parentNodeElement.getName(),
            PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType()) ||
            CommonUtils.isEqualIgnoreCase(parentNodeElement.getParentNode().getName(),
                PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType())) {
          fetchVariantsForRvwQnaireView(qnaireResp, parentNodeElement, includeDeletedVariant, qnaireVariantNodes,
              varRespWpQnaireMap);
        }
        else {
          fetchVariantsForA2LView(qnaireResp, parentNodeElement, includeDeletedVariant, variantListForSelectedA2L,
              qnaireVariantNodes, varRespWpQnaireMap);
        }
      }
    });

    return qnaireVariantNodes;
  }

  /**
   * @param parentNode parent qnaire var node
   * @param includeDeletedResponse as input
   * @return set of qnaire response nodes
   */
  public Object[] getRvwQnaireRespNodesForPidcA2l(final PidcTreeNode parentNode, final boolean includeDeletedResponse) {

    if (CommonUtils.isEqual(parentNode.getName(), ApicConstants.DEFAULT_A2L_WP_NAME)) {
      return new Object[] {};
    }
    SortedSet<PidcTreeNode> qnaireRespNodeSet = new TreeSet<>();
    PidcTreeNode rvwRespNode = parentNode.getParentNode();
    PidcTreeNode varNode = rvwRespNode.getParentNode();
    PidcTreeNode pidcA2lNode = varNode.getParentNode();

    Map<Long, Map<Long, Set<Long>>> respWpMap =
        pidcA2lNode.getA2lStructureModel().getVarRespWpQniareMap().get(parentNode.getPidcVariant().getId());

    Map<Long, Set<Long>> wpMap = respWpMap.get(rvwRespNode.getA2lResponsibility().getId());

    Set<Long> qnaireRespIdSet = wpMap.get(parentNode.getA2lWorkpackage().getId());

    for (Long qnaireRespId : qnaireRespIdSet) {
      if (CommonUtils.isNotNull(qnaireRespId) &&
          CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId)) {
        RvwQnaireResponse rvwQnaireResponse =
            pidcA2lNode.getA2lStructureModel().getRvwQnaireRespMap().get(qnaireRespId);
        if (includeDeletedResponse) {
          // Display all the questionnaire response
          qnaireRespNodeSet.add(createQnaireResponseTreeNodeObjForPIDCA2l(rvwQnaireResponse, parentNode, pidcA2lNode));
        }
        else {
          // To Display only not deleted questionnaire response
          if (!rvwQnaireResponse.isDeletedFlag()) {
            qnaireRespNodeSet
                .add(createQnaireResponseTreeNodeObjForPIDCA2l(rvwQnaireResponse, parentNode, pidcA2lNode));
          }
        }
      }
    }

    return qnaireRespNodeSet.toArray();
  }

  /**
   * @param response
   * @param parentNode
   * @return
   */
  private PidcTreeNode createQnaireResponseTreeNodeObjForPIDCA2l(final RvwQnaireResponse response,
      final PidcTreeNode parentNode, final PidcTreeNode pidcA2lNode) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + QNAIRE_RESP_PVER_PREFIX + response.getId());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }

    final StringBuilder resultName = new StringBuilder(response.getName());

    // Append primary variant name for linked qnaire response
    if (isDifferentPrimaryQnaireVarRespWp(response, parentNode)) {
      resultName.append(" - ");
      resultName.append(response.getPrimaryVarRespWpName());
    }
    A2lResponsibility a2lResponsibility =
        pidcA2lNode.getA2lStructureModel().getA2lRespMap().get(response.getA2lRespId());
    pidcNode.setA2lResponsibility(a2lResponsibility);
    A2lWorkPackage a2lWorkPackage = pidcA2lNode.getA2lStructureModel().getA2lWpMap().get(response.getA2lWpId());
    pidcNode.setA2lWorkpackage(a2lWorkPackage);
    pidcNode.setName(resultName.toString());
    pidcNode.setLevel(parentNode.getLevel() + 1);
    pidcNode.setParentNodeId(parentNode.getNodeId());
    pidcNode.setParentNode(parentNode);
    pidcNode.setPidcVariant(parentNode.getPidcVariant());
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.PIDC_A2L_QNAIRE_RESP_NODE);
    pidcNode.setQnaireRespId(response.getId());
    pidcNode.setQnaireResp(response);
    pidcNode.setA2lStructureModel(pidcA2lNode.getA2lStructureModel());
    pidcNode.setPidcA2l(parentNode.getPidcA2l());
    pidcNode.setPidcVersion(this.pidcVerIdTreenodeMap.get(response.getPidcVersId()).getPidcVersion());
    pidcNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + QNAIRE_RESP_PVER_PREFIX + response.getId());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  private boolean isDifferentPrimaryQnaireVarRespWp(final RvwQnaireResponse qnaireResp, final PidcTreeNode parentNode) {
    return CommonUtils.isNotEqual(qnaireResp.getVariantId(), parentNode.getPidcVariant().getId()) ||
        CommonUtils.isNotEqual(qnaireResp.getA2lWpId(), parentNode.getA2lWorkpackage().getId()) ||
        CommonUtils.isNotEqual(qnaireResp.getA2lRespId(), parentNode.getA2lResponsibility().getId());
  }


  /**
   * @param qnaireResp
   * @param parentNodeElement
   * @param includeDeletedVariant
   * @param qnaireVariantNodes
   * @param varRespWpQnaireMap
   */
  private void fetchVariantsForRvwQnaireView(final PidcQnaireInfo qnaireResp, final PidcTreeNode parentNodeElement,
      final boolean includeDeletedVariant, final SortedSet<PidcTreeNode> qnaireVariantNodes,
      final Entry<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQnaireMap) {
    PidcVariant var = qnaireResp.getVarIdVarMap().get(varRespWpQnaireMap.getKey());

    if (includeDeletedVariant) {
      addVariantToPIDCTreeNode(parentNodeElement, qnaireVariantNodes, var);
    }
    else {
      // To Display only not deleted questionnaire response
      if (!var.isDeleted()) {
        addVariantToPIDCTreeNode(parentNodeElement, qnaireVariantNodes, var);
      }
    }
  }

  /**
   * @param qnaireResp
   * @param parentNodeElement
   * @param includeDeletedVariant
   * @param variantListForSelectedA2L
   * @param qnaireVariantNodes
   * @param varRespWpQnaireMap
   */
  private void fetchVariantsForA2LView(final PidcQnaireInfo qnaireResp, final PidcTreeNode parentNodeElement,
      final boolean includeDeletedVariant, final Object[] variantListForSelectedA2L,
      final SortedSet<PidcTreeNode> qnaireVariantNodes,
      final Entry<Long, Map<Long, Map<Long, Set<Long>>>> varRespWpQnaireMap) {
    for (Object VarFromList : variantListForSelectedA2L) {
      PidcVariant var = qnaireResp.getVarIdVarMap().get(varRespWpQnaireMap.getKey());
      if (((PidcTreeNode) VarFromList).getPidcVariant().getId().equals(var.getId())) {
        if (includeDeletedVariant) {
          addVariantToPIDCTreeNode(parentNodeElement, qnaireVariantNodes, var);
        }
        else {
          // To Display only not deleted questionnaire response
          if (!var.isDeleted()) {
            addVariantToPIDCTreeNode(parentNodeElement, qnaireVariantNodes, var);
          }
        }
      }
    }
  }

  /**
   * @param parentNodeElement
   * @param qnaireVariantNodes
   * @param var
   */
  private void addVariantToPIDCTreeNode(final PidcTreeNode parentNodeElement,
      final SortedSet<PidcTreeNode> qnaireVariantNodes, final PidcVariant var) {
    PidcTreeNode qnaireVarTreeNodeObj = createQnaireVarTreeNodeObject(var, parentNodeElement);
    qnaireVarTreeNodeObj.setPidcVersion(parentNodeElement.getPidcVersion());
    qnaireVariantNodes.add(qnaireVarTreeNodeObj);
  }

  private void addVariantToPIDCA2LTreeNode(final PidcTreeNode parentNodeElement,
      final SortedSet<PidcTreeNode> qnaireVariantNodes, final PidcVariant var) {
    PidcTreeNode qnaireVarTreeNodeObj = createPidcA2lVarTreeNodeObject(var, parentNodeElement);
    qnaireVarTreeNodeObj.setPidcVersion(parentNodeElement.getPidcVersion());
    qnaireVariantNodes.add(qnaireVarTreeNodeObj);
  }

  /**
   * @param parentNode treenode
   * @param pidcA2lVarList questionnaire variants
   * @return A2L Responsibilities for the Pidc A2l Responsibility
   */
  public Object[] getA2LRespForPidcA2l(final PidcTreeNode parentNode, final Object[] pidcA2lVarList) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    for (Object pidcA2lVar : pidcA2lVarList) {

      PidcA2lTreeStructureModel a2lStructureModel = ((PidcTreeNode) pidcA2lVar).getParentNode().getA2lStructureModel();
      Map<Long, Map<Long, Set<Long>>> respWpMap =
          a2lStructureModel.getVarRespWpQniareMap().get(parentNode.getPidcVariant().getId());
      if (respWpMap != null) {
        respWpMap.entrySet().forEach(respWpEntry -> {
          PidcTreeNode respNode =
              this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_PREFIX + respWpEntry.getKey());
          if (respNode == null) {
            respNode = new PidcTreeNode();
          }
          respNode.setLevel(parentNode.getLevel() + 1);
          respNode.setParentNodeId(parentNode.getNodeId());
          respNode.setParentNode(parentNode);
          respNode.setNodeType(PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE);
          respNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_PREFIX + respWpEntry.getKey());
          A2lResponsibility a2lResponsibility = a2lStructureModel.getA2lRespMap().get(respWpEntry.getKey());
          respNode.setA2lResponsibility(a2lResponsibility);
          respNode.setName(a2lResponsibility.getName());
          respNode.setPidc(parentNode.getParentNode().getPidc());
          respNode.setPidcA2l(parentNode.getPidcA2l());
          respNode.setPidcVersion(parentNode.getPidcVersion());
          respNode.setPidcVariant(parentNode.getPidcVariant());
          respNode.setA2lStructureModel(a2lStructureModel);
          this.nodeIdNodeMap.put(respNode.getNodeId(), respNode);
          childSet.add(respNode);

        });
      }
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode pidc tree node
   * @return WP for questionnaire response
   */
  public Object[] getA2lWPForPidcA2l(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode pidcA2LNode = parentNode.getParentNode().getParentNode();
    PidcTreeNode varNode = parentNode.getParentNode();
    Map<Long, Set<Long>> wpMap = new HashMap<>();
    if (null != pidcA2LNode.getA2lStructureModel()) {
      Map<Long, Map<Long, Set<Long>>> respWpMap =
          pidcA2LNode.getA2lStructureModel().getVarRespWpQniareMap().get(varNode.getPidcVariant().getId());
      wpMap = respWpMap.get(parentNode.getA2lResponsibility().getId());
    }
    if (!wpMap.isEmpty()) {
      for (Map.Entry<Long, Set<Long>> wpEntry : wpMap.entrySet()) {
        PidcTreeNode respWpNode =
            this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_WP_PREFIX + wpEntry.getKey());
        if (respWpNode == null) {
          respWpNode = new PidcTreeNode();
        }
        respWpNode.setParentNodeId(parentNode.getNodeId());
        respWpNode.setParentNode(parentNode);
        respWpNode.setNodeType(PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE);
        respWpNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_WP_PREFIX + wpEntry.getKey());
        A2lResponsibility a2lResponsibility =
            pidcA2LNode.getA2lStructureModel().getA2lRespMap().get(parentNode.getA2lResponsibility().getId());
        respWpNode.setA2lResponsibility(a2lResponsibility);
        A2lWorkPackage a2lWorkPackage = pidcA2LNode.getA2lStructureModel().getA2lWpMap().get(wpEntry.getKey());
        respWpNode.setA2lWorkpackage(a2lWorkPackage);
        respWpNode.setName(a2lWorkPackage.getName());
        respWpNode.setPidc(parentNode.getPidc());
        respWpNode.setPidcA2l(parentNode.getPidcA2l());
        respWpNode.setPidcVersion(parentNode.getPidcVersion());
        respWpNode.setA2lStructureModel(pidcA2LNode.getA2lStructureModel());
        respWpNode.setPidcVariant(varNode.getPidcVariant());
        respWpNode.setQnaireInfo(varNode.getParentNode().getQnaireInfo());
        this.nodeIdNodeMap.put(respWpNode.getNodeId(), respWpNode);
        childSet.add(respWpNode);
      }
    }
    return childSet.toArray();
  }


  /**
   * @param parentNode treenode
   * @param qnaireVars questionnaire variants
   * @return A2L Responsibilities for the questionaire Responsibility
   */
  public Object[] getA2LRespForQnaireResp(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    Map<Long, Map<Long, Set<Long>>> respWpMap =
        parentNode.getParentNode().getQnaireInfo().getVarRespWpQniareMap().get(parentNode.getPidcVariant().getId());
    if (respWpMap != null) {
      respWpMap.entrySet().forEach(respWpEntry -> {
        Long wpId = respWpEntry.getKey();
        PidcTreeNode respNode = this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_PREFIX + wpId);
        if (respNode == null) {
          respNode = new PidcTreeNode();
        }
        respNode.setLevel(parentNode.getLevel() + 1);
        respNode.setParentNodeId(parentNode.getNodeId());
        respNode.setParentNode(parentNode);
        respNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE);
        respNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_PREFIX + wpId);
        A2lResponsibility a2lResponsibility = parentNode.getParentNode().getQnaireInfo().getA2lRespMap().get(wpId);
        respNode.setA2lResponsibility(a2lResponsibility);
        respNode.setName(a2lResponsibility.getName());
        respNode.setPidc(parentNode.getParentNode().getPidc());
        respNode.setPidcA2l(parentNode.getPidcA2l());
        respNode.setPidcVersion(parentNode.getPidcVersion());
        respNode.setPidcVariant(parentNode.getPidcVariant());
        respNode.setQnaireInfo(parentNode.getParentNode().getQnaireInfo());
        this.nodeIdNodeMap.put(respNode.getNodeId(), respNode);
        childSet.add(respNode);

      });
    }
    return childSet.toArray();
  }


  /**
   * @param parentNode
   * @return
   */
  public Object[] getA2lVariantList(final PidcTreeNode parentNode, final PidcQnaireInfo qnaireInfo,
      final boolean includeDeletedVariant) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcVariantServiceClient pidcVariantServiceClient = new PidcVariantServiceClient();
    try {
      Map<Long, PidcVariant> a2LVariantMap =
          pidcVariantServiceClient.getVariantsForVersion(parentNode.getPidcVersion().getId(), includeDeletedVariant);

      if (!a2LVariantMap.isEmpty()) {
        a2LVariantMap.entrySet().forEach(a2LVariantMapEntry -> {
          PidcTreeNode varNode = this.nodeIdNodeMap
              .get(parentNode.getNodeId() + LEVEL_SEPARATOR + QNAIRE_VAR_PVER_PREFIX + a2LVariantMapEntry.getKey());
          if (varNode == null) {
            varNode = new PidcTreeNode();
          }
          parentNode.setQnaireInfo(qnaireInfo);
          varNode.setName(a2LVariantMapEntry.getValue().getName());
          varNode.setLevel(parentNode.getLevel() + 1);
          varNode.setParentNodeId(parentNode.getNodeId());
          varNode.setParentNode(parentNode);
          varNode.setNodeType(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE);
          varNode.setNodeId(
              parentNode.getNodeId() + LEVEL_SEPARATOR + QNAIRE_VAR_PVER_PREFIX + a2LVariantMapEntry.getKey());
          varNode.setPidcVersion(parentNode.getPidcVersion());
          varNode.setPidcVariant(a2LVariantMapEntry.getValue());
          this.nodeIdNodeMap.put(varNode.getNodeId(), varNode);
          childSet.add(varNode);
        });
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @throws ApicWebServiceException
   */
  public PidcQnaireInfo getQnaireInfoForPidc(final PidcTreeNode parentNode) {
    PidcQnaireInfo qnaireInfo = null;
    try {
      qnaireInfo = new RvwQnaireResponseServiceClient().getPidcQnaireVariants(parentNode.getPidcVersion().getId());
    }

    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return qnaireInfo;

  }

  /**
   * @param parentNode pidc tree node
   * @return WP for questionnaire response
   */
  public Object[] getA2lWPForQnaireResponse(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode varNode = parentNode.getParentNode();
    Map<Long, Set<Long>> wpMap = new HashMap<>();
    if (null != varNode.getParentNode().getQnaireInfo()) {
      Map<Long, Map<Long, Set<Long>>> respWpMap =
          varNode.getParentNode().getQnaireInfo().getVarRespWpQniareMap().get(varNode.getPidcVariant().getId());
      wpMap = respWpMap.get(parentNode.getA2lResponsibility().getId());
    }
    if (!wpMap.isEmpty()) {
      for (Map.Entry<Long, Set<Long>> wpEntry : wpMap.entrySet()) {
        Long wpId = wpEntry.getKey();
        PidcTreeNode respWpNode =
            this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_WP_PREFIX + wpId);
        if (respWpNode == null) {
          respWpNode = new PidcTreeNode();
        }
        respWpNode.setParentNodeId(parentNode.getNodeId());
        respWpNode.setParentNode(parentNode);
        respWpNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE);
        respWpNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_WP_PREFIX + wpId);
        A2lResponsibility a2lResponsibility =
            varNode.getParentNode().getQnaireInfo().getA2lRespMap().get(parentNode.getA2lResponsibility().getId());
        respWpNode.setA2lResponsibility(a2lResponsibility);
        A2lWorkPackage a2lWorkPackage = varNode.getParentNode().getQnaireInfo().getA2lWpMap().get(wpId);
        respWpNode.setA2lWorkpackage(a2lWorkPackage);
        respWpNode.setName(a2lWorkPackage.getName());
        respWpNode.setPidc(parentNode.getPidc());
        respWpNode.setPidcA2l(parentNode.getPidcA2l());
        respWpNode.setPidcVersion(parentNode.getPidcVersion());
        respWpNode.setPidcVariant(varNode.getPidcVariant());
        respWpNode.setQnaireInfo(varNode.getParentNode().getQnaireInfo());
        this.nodeIdNodeMap.put(respWpNode.getNodeId(), respWpNode);
        childSet.add(respWpNode);
      }
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode parent qnaire var node
   * @param includeDeletedResponse as input
   * @return set of qnaire response nodes
   */
  public Object[] getRvwQnaireRespNodes(final PidcTreeNode parentNode, final boolean includeDeletedResponse) {
    if (CommonUtils.isEqual(parentNode.getName(), ApicConstants.DEFAULT_A2L_WP_NAME)) {
      return new Object[] {};
    }
    SortedSet<PidcTreeNode> qnaireRespNodeSet = new TreeSet<>();
    PidcTreeNode rvwRespNode = parentNode.getParentNode();
    PidcTreeNode varNode = rvwRespNode.getParentNode();
    PidcTreeNode rvwQnaire = new PidcTreeNode();

    // if the review questionnaire has to be viewed from Review Questionnaires node
    if (varNode.getParentNode().getNodeType().equals(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE)) {
      rvwQnaire = varNode.getParentNode();
    }
    Map<Long, Map<Long, Set<Long>>> respWpMap =
        rvwQnaire.getQnaireInfo().getVarRespWpQniareMap().get(parentNode.getPidcVariant().getId());

    Map<Long, Set<Long>> wpMap = respWpMap.get(rvwRespNode.getA2lResponsibility().getId());
    if (CommonUtils.isNotEmpty(wpMap)) {
      Set<Long> qnaireRespIdSet = wpMap.get(parentNode.getA2lWorkpackage().getId());

      getRvwQnaireRespNodesFromIdSet(parentNode, includeDeletedResponse, qnaireRespNodeSet, rvwQnaire, qnaireRespIdSet);
    }

    return qnaireRespNodeSet.toArray();
  }

  /**
   * @param parentNode
   * @param includeDeletedResponse
   * @param qnaireRespNodeSet
   * @param rvwQnaire
   * @param qnaireRespIdSet
   */
  private void getRvwQnaireRespNodesFromIdSet(final PidcTreeNode parentNode, final boolean includeDeletedResponse,
      final SortedSet<PidcTreeNode> qnaireRespNodeSet, final PidcTreeNode rvwQnaire, final Set<Long> qnaireRespIdSet) {
    for (Long qnaireRespId : qnaireRespIdSet) {
      // qnaireRespId is Null if there is simplified General Qnaire
      if (CommonUtils.isNotNull(qnaireRespId) &&
          CommonUtils.isNotEqual(ApicConstants.SIMP_QUES_RESP_ID, qnaireRespId)) {
        RvwQnaireResponse rvwQnaireResponse = rvwQnaire.getQnaireInfo().getRvwQnaireRespMap().get(qnaireRespId);
        if (includeDeletedResponse) {
          // Display all the questionnaire response
          qnaireRespNodeSet.add(createQnaireResponseTreeNodeObj(rvwQnaireResponse, parentNode));
        }
        else {
          // To Display only not deleted questionnaire response
          if (!rvwQnaireResponse.isDeletedFlag()) {
            qnaireRespNodeSet.add(createQnaireResponseTreeNodeObj(rvwQnaireResponse, parentNode));
          }
        }
      }
    }
  }


  /**
   * @param parentNode parent qnaire var node
   * @return set of qnaire response nodes
   */
  public SortedSet<PidcTreeNode> getQnaireRespNodes(final PidcTreeNode parentNode) {
    SortedSet<PidcTreeNode> qnaireRespNodeSet = new TreeSet<>();
    for (RvwQnaireResponse resp : parentNode.getQnaireResponseMap().values()) {
      qnaireRespNodeSet.add(createQnaireResponseTreeNodeObj(resp, parentNode));
    }
    return qnaireRespNodeSet;
  }

  /**
   * @param response
   * @param parentNode
   * @return
   */
  private PidcTreeNode createQnaireResponseTreeNodeObj(final RvwQnaireResponse response,
      final PidcTreeNode parentNode) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + QNAIRE_RESP_PVER_PREFIX + response.getId());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }

    final StringBuilder resultName = new StringBuilder(response.getName());

    // Append primary variant name for linked qnaire response
    if (isDifferentPrimaryQnaireVarRespWp(response, parentNode)) {
      resultName.append(" - ");
      resultName.append(response.getPrimaryVarRespWpName());
    }

    A2lResponsibility a2lResponsibility = parentNode.getQnaireInfo().getA2lRespMap().get(response.getA2lRespId());
    pidcNode.setA2lResponsibility(a2lResponsibility);
    A2lWorkPackage a2lWorkPackage = parentNode.getQnaireInfo().getA2lWpMap().get(response.getA2lWpId());
    pidcNode.setA2lWorkpackage(a2lWorkPackage);
    pidcNode.setName(resultName.toString());
    pidcNode.setLevel(parentNode.getLevel() + 1);
    pidcNode.setParentNodeId(parentNode.getNodeId());
    pidcNode.setParentNode(parentNode);
    pidcNode.setPidcVariant(parentNode.getPidcVariant());
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.QNAIRE_RESP_NODE);
    pidcNode.setQnaireRespId(response.getId());
    pidcNode.setQnaireResp(response);
    pidcNode.setQnaireInfo(parentNode.getParentNode().getQnaireInfo());
    pidcNode.setPidcA2l(parentNode.getPidcA2l());
    pidcNode.setPidcVersion(this.pidcVerIdTreenodeMap.get(response.getPidcVersId()).getPidcVersion());
    pidcNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + QNAIRE_RESP_PVER_PREFIX + response.getId());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * @param key
   * @param parentNodeElement
   * @return
   */
  private PidcTreeNode createQnaireVarTreeNodeObject(final PidcVariant var, final PidcTreeNode parentNodeElement) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNodeElement.getNodeId() + LEVEL_SEPARATOR + QNAIRE_VAR_PVER_PREFIX + var.getId());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    pidcNode.setName(var.getName());
    pidcNode.setLevel(parentNodeElement.getLevel() + 1);
    pidcNode.setParentNodeId(parentNodeElement.getNodeId());
    pidcNode.setParentNode(parentNodeElement);
    pidcNode.setPidcVariant(var);
    pidcNode.setPidcA2l(parentNodeElement.getPidcA2l());

    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE);
    pidcNode.setNodeId(parentNodeElement.getNodeId() + LEVEL_SEPARATOR + QNAIRE_VAR_PVER_PREFIX + var.getId());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * @param key
   * @param parentNodeElement
   * @return
   */
  private PidcTreeNode createPidcA2lVarTreeNodeObject(final PidcVariant var, final PidcTreeNode parentNodeElement) {
    PidcTreeNode pidcNode = this.nodeIdNodeMap
        .get(parentNodeElement.getNodeId() + LEVEL_SEPARATOR + PIDC_A2L_VAR_PVER_PREFIX + var.getId());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    pidcNode.setPidc(parentNodeElement.getPidc());
    pidcNode.setName(var.getName());
    pidcNode.setLevel(parentNodeElement.getLevel() + 1);
    pidcNode.setParentNodeId(parentNodeElement.getNodeId());
    pidcNode.setParentNode(parentNodeElement);
    pidcNode.setPidcVariant(var);
    pidcNode.setPidcA2l(parentNodeElement.getPidcA2l());
    pidcNode.setA2lStructureModel(parentNodeElement.getA2lStructureModel());
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE);
    pidcNode.setNodeId(parentNodeElement.getNodeId() + LEVEL_SEPARATOR + PIDC_A2L_VAR_PVER_PREFIX + var.getId());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * @param parentNode parent pidc version ndoe
   * @param pidcVariants pidc variants of pidc version
   * @return set of pidc var tree nodes
   */
  public SortedSet<PidcTreeNode> getPidcVarNodes(final PidcTreeNode parentNode, final Set<PidcVariant> pidcVariants) {
    SortedSet<PidcTreeNode> pidcVarNodes = new TreeSet<>();
    for (PidcVariant var : pidcVariants) {
      PidcTreeNode variantTreeNodeObj = createVariantTreeNodeObj(parentNode, var);
      pidcVarNodes.add(variantTreeNodeObj);
    }
    List<PidcTreeNode> pidcVarNodesList = new ArrayList<>();
    pidcVarNodesList.addAll(pidcVarNodes);
    parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.PIDC_VAR_NODE.getUiType(), pidcVarNodesList);
    return pidcVarNodes;
  }

  /**
   * @param parentNode
   * @param var
   * @return
   */
  private PidcTreeNode createVariantTreeNodeObj(final PidcTreeNode parentNode, final PidcVariant var) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_VAR_PREFIX + var.getId());

    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    pidcNode.setName(var.getName());
    pidcNode.setLevel(parentNode.getLevel() + 1);
    pidcNode.setParentNodeId(parentNode.getNodeId());
    pidcNode.setPidcVersion(parentNode.getPidcVersion());
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.PIDC_VAR_NODE);
    pidcNode.setPidcVariant(var);
    pidcNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_VAR_PREFIX + var.getId());
    this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    return pidcNode;
  }

  /**
   * @param parentNode parent pidc version node
   * @return return other version title node
   */
  public PidcTreeNode createOtherVerTitleNode(final PidcTreeNode parentNode) {
    PidcTreeNode othrVerTitleNode = this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType() + parentNode.getPidcVersion().getId());
    if (othrVerTitleNode == null) {
      othrVerTitleNode = new PidcTreeNode();
    }
    othrVerTitleNode.setParentNodeId(parentNode.getNodeId());
    othrVerTitleNode.setPidcVersion(parentNode.getPidcVersion());
    othrVerTitleNode.setName(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType());
    othrVerTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE);
    othrVerTitleNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType() + parentNode.getPidcVersion().getId());
    this.nodeIdNodeMap.put(othrVerTitleNode.getNodeId(), othrVerTitleNode);
    return othrVerTitleNode;
  }

  /**
   * @param parentNode pidc version node
   * @return Rvw questionire title node
   */
  public PidcTreeNode createRvwQnaireTitleNode(final PidcTreeNode parentNode) {
    // get pidc tree node from node id map and use it. create new instance only if its not available
    PidcTreeNode rvwQnaireTitleNode = this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + parentNode.getPidcVersion().getId());

    if (rvwQnaireTitleNode == null) {
      rvwQnaireTitleNode = new PidcTreeNode();
    }
    rvwQnaireTitleNode.setParentNodeId(parentNode.getNodeId());
    rvwQnaireTitleNode.setPidcVersion(parentNode.getPidcVersion());
    rvwQnaireTitleNode.setName(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType());
    rvwQnaireTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE);
    rvwQnaireTitleNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType() + parentNode.getPidcVersion().getId());
    this.nodeIdNodeMap.put(rvwQnaireTitleNode.getNodeId(), rvwQnaireTitleNode);
    return rvwQnaireTitleNode;
  }

  /**
   * @param parentNode pidc version node
   * @return set of pidc variant nodes
   */
  public Set<PidcTreeNode> getPidcVariantNodes(final PidcTreeNode parentNode) {
    Set<PidcVariant> pidcVariants = new HashSet<>();
    try {
      pidcVariants.addAll(
          new PidcVariantServiceClient().getVariantsForVersion(parentNode.getPidcVersion().getId(), false).values());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return getPidcVarNodes(parentNode, pidcVariants);
  }

  /**
   * @param pidcNode pidc version node
   * @return flag to indicate the presence of variants
   */
  public boolean hasVariants(final PidcTreeNode pidcNode) {
    boolean pidcVariants = false;
    try {
      pidcVariants = new PidcVariantServiceClient().hasVariant(pidcNode.getPidcVersion().getId(), false);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return pidcVariants;
  }

  /**
   * @return the parentIdChildNodesMap
   */
  public Map<String, SortedSet<PidcTreeNode>> getParentIdChildNodesMap() {
    return this.parentIdChildNodesMap;
  }


  /**
   * Child nodes
   *
   * @param parent parent Node
   * @param includeEmptyNodes if true, empty nodes are also returned
   * @return the sorted set of child nodes
   */
  public SortedSet<PidcTreeNode> getChildNodesSorted(final PidcTreeNode parent, final boolean includeEmptyNodes) {
    return this.parentIdChildNodesMap.get(parent.getNodeId()).stream()
        .filter(node -> includeEmptyNodes || hasPidCardsBelow(node)).collect(Collectors.toCollection(TreeSet::new));
  }

  /**
   * @param parentNode parent Pidc version node
   * @param childNodesAvailblty child nodes availablity
   * @param returnList children list
   * @param loadA2l is a2l to be loaded
   * @param loadCdr is cdr to be loaded
   * @param loadVariants is variants to be loaded
   * @param loadQnaires is questionnaires to be loaded
   */
  public void getPidcActVerNodeChildren(final PidcTreeNode parentNode, final PidcTreeNodeChildren childNodesAvailblty) {
    parentNode.getChildNodesMap().clear();
    if (childNodesAvailblty.isOtherPidcVerPresent() &&
        parentNode.getNodeType().equals(PidcTreeNode.PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION)) {
      PidcTreeNode otherVerTitleNode = createOtherVerTitleNode(parentNode);
      parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType(),
          new ArrayList<>(Arrays.asList(otherVerTitleNode)));
    }
    if (childNodesAvailblty.isSdomPversPresent()) {
      getSdomPverNodes(parentNode, childNodesAvailblty.getPidcSdomPverSet());
    }
    if (childNodesAvailblty.isCdrPresent()) {
      PidcTreeNode rvwResTitleNodeNew = createRvwResTitleNodeNew(parentNode);
      parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType(),
          new ArrayList<>(Arrays.asList(rvwResTitleNodeNew)));
    }
    if (childNodesAvailblty.isQuestionnairesPresent()) {
      PidcTreeNode rvwQnaireTitleNode = createRvwQnaireTitleNode(parentNode);
      parentNode.getPidcChildrenMap().put(PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType(),
          new ArrayList<>(Arrays.asList(rvwQnaireTitleNode)));
    }
    if (null != childNodesAvailblty.getPidcVariants()) {
      getPidcVarNodes(parentNode, childNodesAvailblty.getPidcVariants());
    }
  }

  /**
   * @param parentNode
   * @return
   */
  private PidcTreeNode createRvwResTitleNodeNew(final PidcTreeNode parentNode) {
    // get pidc tree node from node id map and use it. create new instance only if its not available
    PidcTreeNode rvwResTitleNode = this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + parentNode.getPidcVersion().getId());
    if (rvwResTitleNode == null) {
      rvwResTitleNode = new PidcTreeNode();
    }
    rvwResTitleNode.setParentNodeId(parentNode.getNodeId());
    rvwResTitleNode.setPidcVersion(parentNode.getPidcVersion());
    rvwResTitleNode.setName(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType());
    rvwResTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW);
    rvwResTitleNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR +
        PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType() + parentNode.getPidcVersion().getId());
    this.nodeIdNodeMap.put(rvwResTitleNode.getNodeId(), rvwResTitleNode);
    return rvwResTitleNode;
  }

  /**
   * @param parentNode
   * @param loadVariants
   * @param loadQnaires
   * @param loadCdr
   * @param loadA2l
   * @return
   */
  public SortedSet<PidcTreeNode> getPidcChildren(final PidcTreeNode parentNode, final boolean loadA2l,
      final boolean loadCdr, final boolean loadQnaires, final boolean loadVariants) {
    SortedSet<PidcTreeNode> childSet = new TreeSet<>();
    if (parentNode.getPidcChildrenMap()
        .containsKey(PidcTreeNode.PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType())) {
      childSet.addAll(
          parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType()));
    }

    if (loadA2l &&
        parentNode.getPidcChildrenMap().containsKey(PidcTreeNode.PIDC_TREE_NODE_TYPE.SDOM_PVER.getUiType())) {
      childSet.addAll(parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.SDOM_PVER.getUiType()));
    }

    if (loadCdr && parentNode.getPidcChildrenMap()
        .containsKey(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType())) {

      refreshReviewResult(parentNode, childSet);
    }
    if (loadQnaires && parentNode.getPidcChildrenMap()
        .containsKey(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType())) {
      try {
        // set qnaire info to Review Questionnaire tree node
        PidcQnaireInfo qnaireInfo =
            new RvwQnaireResponseServiceClient().getPidcQnaireVariants(parentNode.getPidcVersion().getId());
        parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType()).get(0)
            .setQnaireInfo(qnaireInfo);

        childSet.addAll(
            parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_QNAIRE_TITLE_NODE.getUiType()));
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    if (loadVariants) {
      if (parentNode.getPidcChildrenMap().containsKey(PidcTreeNode.PIDC_TREE_NODE_TYPE.PIDC_VAR_NODE.getUiType())) {
        childSet
            .addAll(parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.PIDC_VAR_NODE.getUiType()));
      }
      if (parentNode.getPidcChildrenMap()
          .containsKey(PidcTreeNode.PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType())) {
        childSet.addAll(
            parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.OTHER_VER_TITLE_NODE.getUiType()));
      }
    }
    return childSet;
  }

  /**
   * @param parentNode
   * @param childSet
   */
  private void refreshReviewResult(final PidcTreeNode parentNode, final SortedSet<PidcTreeNode> childSet) {
    try {
      // set PidcReviewDetails to Review Result tree node
      PidcReviewDetails pidcReviewDetails =
          new CDRReviewResultServiceClient().getReviewResultInfo(parentNode.getPidcVersion().getId());
      parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType()).get(0)
          .setPidcRvwDetails(pidcReviewDetails);
      childSet.addAll(
          parentNode.getPidcChildrenMap().get(PidcTreeNode.PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType()));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param parentNode parent pidc version node
   * @param loadA2l is a2l to be loaded
   * @param loadCdr is cdr to be loaded
   * @param loadVariants is variants to be loaded
   * @param loadQnaires is questionnaires to be loaded
   * @return child nodes of pidc version
   */
  public void getPidcVerNodeChildren(final PidcTreeNode parentNode) {
    // if the parent node level is pidc version level, render the other pidc version nodes
    PidcTreeNodeChildren childNodesAvailblty = getPidcNodeChildAvailblty(parentNode);
    // if the pid version is the active versions & if there are other versions , add the other versions node
    getPidcActVerNodeChildren(parentNode, childNodesAvailblty);
  }

  /**
   * @param id review result id
   * @return fc2wp name
   */
  public String resolveFc2WpName(final Long id) {
    try {
      return new CDRReviewResultServiceClient().resolveFc2WpName(id);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return "";
  }


  /**
   * @param parentIdChildNodesMap the parentIdChildNodesMap to set
   */
  public void setParentIdChildNodesMap(final Map<String, SortedSet<PidcTreeNode>> parentIdChildNodesMap) {
    this.parentIdChildNodesMap = parentIdChildNodesMap;
  }

  /**
   * @return the nodeIdNodeMap
   */
  public Map<String, PidcTreeNode> getNodeIdNodeMap() {
    return this.nodeIdNodeMap;
  }

  /**
   * @param nodeIdNodeMap the nodeIdNodeMap to set
   */
  public void setNodeIdNodeMap(final Map<String, PidcTreeNode> nodeIdNodeMap) {
    this.nodeIdNodeMap = nodeIdNodeMap;
  }

  /**
   * @return the rootNode
   */
  public PidcTreeNode getRootNode() {
    return this.rootTreeNode;
  }

  /**
   * @return the selectedNode
   */
  public PidcTreeNode getSelectedNode() {
    return this.selectedNode;
  }

  /**
   * @param selectedNode the selectedNode to set
   */
  public void setSelectedNode(final PidcTreeNode selectedNode) {
    this.selectedNode = selectedNode;
  }


  /**
   * @return the oldActiveVerNodeId
   */
  public String getOldActiveVerNodeId() {
    return this.oldActiveVerNodeId;
  }


  /**
   * @param oldActiveVerNodeId the oldActiveVerNodeId to set
   */
  public void setOldActiveVerNodeId(final String oldActiveVerNodeId) {
    this.oldActiveVerNodeId = oldActiveVerNodeId;
  }

  /**
   * @param pidcNode PIDC Node
   * @return true, if this tree node has pidcs below it
   */
  public boolean hasPidCardsBelow(final PidcTreeNode pidcNode) {

    boolean ret = false;

    // Only applicable for level attribute nodes
    if (pidcNode.getNodeType() == PIDC_TREE_NODE_TYPE.LEVEL_ATTRIBUTE) {

      // Last level attribute node. PIDCs are directly under it
      if (pidcNode.getLevel() == new ApicDataBO().getPidcStructMaxLvl()) {
        ret = !pidcNode.getChildNodesMap().isEmpty();
      }
      else {
        // parent level attribute nodes. Check for PIDCs in child nodes
        for (PidcTreeNode child : this.parentIdChildNodesMap.get(pidcNode.getNodeId())) {
          if (hasPidCardsBelow(child)) {
            ret = true;
            break;
          }
        }
      }
    }

    return ret;
  }

  /**
   * Find the parent node's name of the given review result. Parent node could be a group, work package etc.
   *
   * @param rvwResult data review result
   * @return parent node display name of this review result
   */
  public String getResultGroupNodeName(final CDRReviewResult rvwResult) {
    String grpWpName = CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getTreeDispName();
    if (CommonUtils.isNotEmptyString(rvwResult.getSourceType())) {
      if (CDRConstants.CDR_SOURCE_TYPE.WORK_PACKAGE.getDbType().equals(rvwResult.getSourceType())) {
        if (CommonUtils.isNotEmptyString(rvwResult.getGrpWorkPkg())) {
          grpWpName = getGrpWpName(rvwResult);
        }
      }
      else if (CDRConstants.CDR_SOURCE_TYPE.GROUP.getDbType().equals(rvwResult.getSourceType())) {
        if (CommonUtils.isNotEmptyString(rvwResult.getGrpWorkPkg())) {
          grpWpName = rvwResult.getGrpWorkPkg();
        }
      }
      else if (CDRConstants.CDR_SOURCE_TYPE.WP.getDbType().equals(rvwResult.getSourceType())) {
        if (CommonUtils.isNotEmptyString(rvwResult.getGrpWorkPkg())) {
          grpWpName = rvwResult.getGrpWorkPkg();
        }
      }
      else {
        grpWpName = CDRConstants.CDR_SOURCE_TYPE.getType(rvwResult.getSourceType()).getTreeDispName();
      }
    }
    return grpWpName;
  }

  /**
   * @param rvwResult
   * @return
   */
  private String getGrpWpName(final CDRReviewResult rvwResult) {
    String grpWpName;
    if (CDRConstants.FC2WP.equals(rvwResult.getGrpWorkPkg())) {
      grpWpName = resolveFc2WpName(rvwResult.getId());
    }
    else {
      grpWpName = rvwResult.getGrpWorkPkg();
    }
    return grpWpName;
  }

  /**
   * @return the pidcVerIdTreenodeMap
   */
  public Map<Long, PidcTreeNode> getPidcVerIdTreenodeMap() {
    return this.pidcVerIdTreenodeMap;
  }

  /**
   * @return the isVarNodeEnabled
   */
  public boolean isVarNodeEnabled() {
    return this.isVarNodeEnabled;
  }

  /**
   * @param isVarNodeEnabled the isVarNodeEnabled to set
   */
  public void setVarNodeEnabled(final boolean isVarNodeEnabled) {
    this.isVarNodeEnabled = isVarNodeEnabled;
  }

  /**
   * @return the isFilterText
   */
  public boolean isFilterText() {
    return this.isFilterText;
  }

  /**
   * @param isFilterText the isFilterText to set
   */
  public void setFilterText(final boolean isFilterText) {
    this.isFilterText = isFilterText;
  }

  /**
   * @param parentNode
   */
  public void getReviewDetails(final PidcTreeNode parentNode) {
    try {
      if (null == parentNode.getPidcRvwDetails()) {
        parentNode.setPidcRvwDetails(
            new CDRReviewResultServiceClient().getReviewResultInfo(parentNode.getPidcVersion().getId()));
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param parentNode
   * @return
   */
  public Set<PidcTreeNode> getRvwVariantNodes(final PidcTreeNode pidcVersionNode, final boolean includeDeletedVariant) {
    SortedSet<PidcTreeNode> rvwVariantNodes = new TreeSet<>();

    if (null != pidcVersionNode.getPidcRvwDetails()) {
      fetchRvwVarNodes(pidcVersionNode, includeDeletedVariant, rvwVariantNodes);
    }

    return rvwVariantNodes;
  }

  /**
   * @param pidcVersionNode
   * @param includeDeletedVariant
   * @param rvwVariantNodes
   */
  private void fetchRvwVarNodes(final PidcTreeNode pidcVersionNode, final boolean includeDeletedVariant,
      final SortedSet<PidcTreeNode> rvwVariantNodes) {
    if (!pidcVersionNode.getPidcRvwDetails().getVarRespWpMap().isEmpty()) {
      pidcVersionNode.getPidcRvwDetails().getVarRespWpMap().entrySet().forEach(entry -> {
        PidcVariant var = pidcVersionNode.getPidcRvwDetails().getPidcVarMap().get(entry.getKey());
        if (entry.getKey().equals(ApicConstants.NO_VARIANT_ID)) {
          var = createNoVariantNode();
        }

        includeDeletedPidcVariants(pidcVersionNode, includeDeletedVariant, rvwVariantNodes, var);
      });
    }
    if (!pidcVersionNode.getPidcRvwDetails().getOtherSrcTypeResults().isEmpty()) {
      // if resp map is not filled , use this map to load variants
      // TODO shall be removed after migration
      pidcVersionNode.getPidcRvwDetails().getOtherSrcTypeResults().entrySet().forEach(entry -> {
        PidcVariant var = pidcVersionNode.getPidcRvwDetails().getPidcVarMap().get(entry.getKey());
        if (entry.getKey().equals(ApicConstants.NO_VARIANT_ID)) {
          var = createNoVariantNode();
        }
        if (null != var) {
          includeDeletedPidcVariants(pidcVersionNode, includeDeletedVariant, rvwVariantNodes, var);
        }
      });
    }
  }

  /**
   * @return
   */
  private PidcVariant createNoVariantNode() {
    PidcVariant var;
    // no variant
    var = new PidcVariant();
    var.setName(NO_VARIANT);
    var.setId(ApicConstants.NO_VARIANT_ID);
    return var;
  }

  /**
   * @param pidcVersionNode
   * @param includeDeletedVariant
   * @param rvwVariantNodes
   * @param var
   */
  private void includeDeletedPidcVariants(final PidcTreeNode pidcVersionNode, final boolean includeDeletedVariant,
      final SortedSet<PidcTreeNode> rvwVariantNodes, final PidcVariant var) {
    if (includeDeletedVariant) {
      addVariantToPidcTree(pidcVersionNode, rvwVariantNodes, var);
    }
    else {
      // To Display only not deleted questionnaire response
      if (!var.isDeleted()) {
        addVariantToPidcTree(pidcVersionNode, rvwVariantNodes, var);
      }
    }
  }

  /**
   * @param pidcVersionNode
   * @param rvwVariantNodes
   * @param var
   */
  private void addVariantToPidcTree(final PidcTreeNode pidcVersionNode, final SortedSet<PidcTreeNode> rvwVariantNodes,
      final PidcVariant var) {
    PidcTreeNode rvwVarTreeNodeObj = createRvwVarTreeNodeObj(pidcVersionNode, var);
    rvwVariantNodes.add(rvwVarTreeNodeObj);
  }

  /**
   * @param pidcVersionNode
   * @param var
   * @return
   */
  private PidcTreeNode createRvwVarTreeNodeObj(final PidcTreeNode pidcVerNode, final PidcVariant pidcVariant) {
    PidcTreeNode pidcNode =
        this.nodeIdNodeMap.get(pidcVerNode.getNodeId() + LEVEL_SEPARATOR + CDR_VAR_PREFIX + pidcVariant.getName());
    if (pidcNode == null) {
      pidcNode = new PidcTreeNode();
    }
    pidcNode.setName(pidcVariant.getName());
    pidcNode.setLevel(pidcVerNode.getLevel() + 1);
    pidcNode.setParentNodeId(pidcVerNode.getNodeId());
    pidcNode.setParentNode(pidcVerNode);
    pidcNode.setNodeType(PIDC_TREE_NODE_TYPE.CDR_VAR_NODE);
    pidcNode.setPidcVariant(pidcVariant);

    pidcNode.setPidcVersion(pidcVerNode.getPidcVersion());
    pidcNode.setNodeId(pidcVerNode.getNodeId() + LEVEL_SEPARATOR + CDR_VAR_PREFIX + pidcVariant.getName());
    if (null == this.nodeIdNodeMap.get(pidcNode.getNodeId())) {
      this.nodeIdNodeMap.put(pidcNode.getNodeId(), pidcNode);
    }
    return pidcNode;
  }

  /**
   * @param parentNode
   * @return
   */
  public Set<PidcTreeNode> getTitlesForRvwResults(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new LinkedHashSet<>();
    PidcTreeNode rvwResTitleNode = parentNode.getParentNode();
    PidcReviewDetails pidcRvwDetails = rvwResTitleNode.getPidcRvwDetails();
    if (CommonUtils.isNotEmpty(pidcRvwDetails.getVarWpMap().get(parentNode.getPidcVariant().getId()))) {
      PidcTreeNode wpTitleNode = createWpTitleNode(parentNode);
      childSet.add(wpTitleNode);
    }
    if (CommonUtils.isNotEmpty(pidcRvwDetails.getVarRespWpMap().get(parentNode.getPidcVariant().getId()))) {
      PidcTreeNode respTitleNode = createRespTitleNode(parentNode);
      childSet.add(respTitleNode);
    }
    if (CommonUtils.isNotEmpty(pidcRvwDetails.getOtherSrcTypeResults().get(parentNode.getPidcVariant().getId()))) {
      PidcTreeNode othrScopeTitleNode = createOthrScopeTitleNode(parentNode);
      childSet.add(othrScopeTitleNode);
    }
    return childSet;
  }


  /**
   * @param parentNode
   * @return
   */
  private PidcTreeNode createOthrScopeTitleNode(final PidcTreeNode parentNode) {
    // get pidc tree node from node id map and use it. create new instance only if its not available
    PidcTreeNode othrScopeTitleNode = this.nodeIdNodeMap.get(
        parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE.getUiType());
    if (othrScopeTitleNode == null) {
      othrScopeTitleNode = new PidcTreeNode();
    }
    othrScopeTitleNode.setParentNodeId(parentNode.getNodeId());
    othrScopeTitleNode.setParentNode(parentNode);
    othrScopeTitleNode.setLevel(parentNode.getLevel() + 1);
    othrScopeTitleNode.setPidcVersion(parentNode.getPidcVersion());
    othrScopeTitleNode.setName(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE.getUiType());
    othrScopeTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE);
    othrScopeTitleNode.setNodeId(
        parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE.getUiType());
    this.nodeIdNodeMap.put(othrScopeTitleNode.getNodeId(), othrScopeTitleNode);
    othrScopeTitleNode.setPidcVariant(parentNode.getPidcVariant());

    return othrScopeTitleNode;
  }

  /**
   * @param parentNode
   * @return
   */
  private PidcTreeNode createRespTitleNode(final PidcTreeNode parentNode) {
    // get pidc tree node from node id map and use it. create new instance only if its not available
    PidcTreeNode rvwRespTitleNode = this.nodeIdNodeMap.get(
        parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.getUiType());
    if (rvwRespTitleNode == null) {
      rvwRespTitleNode = new PidcTreeNode();
    }
    rvwRespTitleNode.setParentNodeId(parentNode.getNodeId());
    rvwRespTitleNode.setParentNode(parentNode);
    rvwRespTitleNode.setLevel(parentNode.getLevel() + 1);
    rvwRespTitleNode.setPidcVersion(parentNode.getPidcVersion());
    rvwRespTitleNode.setName(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.getUiType());
    rvwRespTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE);
    rvwRespTitleNode.setNodeId(
        parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.getUiType());
    this.nodeIdNodeMap.put(rvwRespTitleNode.getNodeId(), rvwRespTitleNode);
    rvwRespTitleNode.setPidcVariant(parentNode.getPidcVariant());

    return rvwRespTitleNode;
  }

  /**
   * @param parentNode
   * @return
   */
  private PidcTreeNode createWpTitleNode(final PidcTreeNode parentNode) {
    // get pidc tree node from node id map and use it. create new instance only if its not available
    PidcTreeNode rvwWPTitleNode = this.nodeIdNodeMap
        .get(parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.getUiType());
    if (rvwWPTitleNode == null) {
      rvwWPTitleNode = new PidcTreeNode();
    }
    rvwWPTitleNode.setParentNodeId(parentNode.getNodeId());
    rvwWPTitleNode.setParentNode(parentNode);
    rvwWPTitleNode.setPidcVariant(parentNode.getPidcVariant());
    rvwWPTitleNode.setLevel(parentNode.getLevel() + 1);
    rvwWPTitleNode.setPidcVersion(parentNode.getPidcVersion());
    rvwWPTitleNode.setName(PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.getUiType());
    rvwWPTitleNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE);
    rvwWPTitleNode.setNodeId(
        parentNode.getNodeId() + LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.getUiType());
    this.nodeIdNodeMap.put(rvwWPTitleNode.getNodeId(), rvwWPTitleNode);
    return rvwWPTitleNode;
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getOtherRvwScopesNodes(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode varNode = parentNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Map<String, Set<Long>> otherScopeResultsMap =
        rvwResTitleNode.getPidcRvwDetails().getOtherSrcTypeResults().get(varNode.getPidcVariant().getId());
    if (!otherScopeResultsMap.isEmpty()) {
      otherScopeResultsMap.entrySet().forEach(othrScopeRvwEntry -> {

        PidcTreeNode othrScopeNode = this.nodeIdNodeMap
            .get(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_OTHER_SCOPE + othrScopeRvwEntry.getKey());
        if (othrScopeNode == null) {
          othrScopeNode = new PidcTreeNode();
        }

        othrScopeNode.setLevel(parentNode.getLevel() + 1);
        othrScopeNode.setParentNodeId(parentNode.getNodeId());
        othrScopeNode.setParentNode(parentNode);
        othrScopeNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE);
        othrScopeNode
            .setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_OTHER_SCOPE + othrScopeRvwEntry.getKey());
        othrScopeNode.setName(othrScopeRvwEntry.getKey());
        othrScopeNode.setPidcVersion(parentNode.getPidcVersion());
        othrScopeNode.setPidcVariant(varNode.getPidcVariant());
        this.nodeIdNodeMap.put(othrScopeNode.getNodeId(), othrScopeNode);
        childSet.add(othrScopeNode);
      });

    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getOtherScopeRvwRes(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode othrSrcNode = parentNode.getParentNode();
    PidcTreeNode varNode = othrSrcNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Long varId = varNode.getPidcVariant().getId();
    Set<Long> rvwResSet =
        rvwResTitleNode.getPidcRvwDetails().getOtherSrcTypeResults().get(varId).get(parentNode.getName());
    rvwResSet.forEach(revResId -> createRevResNode(parentNode, childSet, rvwResTitleNode, revResId, varId));
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @param childSet
   * @param rvwResTitleNode
   * @param revResId
   * @param varId
   */
  private void createRevResNode(final PidcTreeNode parentNode, final Set<PidcTreeNode> childSet,
      final PidcTreeNode rvwResTitleNode, final Long revResId, final Long varId) {
    CDRReviewResult cdrResult = rvwResTitleNode.getPidcRvwDetails().getCdrResultMap().get(revResId);
    PidcTreeNode othrScopeResNode =
        this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_PREFIX + revResId);
    if (othrScopeResNode == null) {
      othrScopeResNode = new PidcTreeNode();
    }

    othrScopeResNode.setLevel(parentNode.getLevel() + 1);
    othrScopeResNode.setParentNodeId(parentNode.getNodeId());
    othrScopeResNode.setParentNode(parentNode);
    othrScopeResNode.setNodeType(PIDC_TREE_NODE_TYPE.REV_RES_NODE);
    othrScopeResNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_PREFIX + revResId);
    String cdrRvwResultName = cdrResult.getName();
    if ((null != cdrResult.getPrimaryVariantId()) &&
        !cdrResult.getPrimaryVariantId().equals(parentNode.getPidcVariant().getId())) {
      cdrRvwResultName = cdrRvwResultName + " - " + parentNode.getPidcVariant().getName();
    }
    othrScopeResNode.setName(cdrRvwResultName);
    othrScopeResNode.setReviewResult(cdrResult);
    Map<Long, Long> resToVarMap = rvwResTitleNode.getPidcRvwDetails().getResToVarMap().get(revResId);
    if (CommonUtils.isNotEmpty(resToVarMap) && (resToVarMap.get(varId) != null)) {
      // set review variant
      othrScopeResNode
          .setReviewVarResult(rvwResTitleNode.getPidcRvwDetails().getRvwVariantMap().get(resToVarMap.get(varId)));
    }
    this.nodeIdNodeMap.put(othrScopeResNode.getNodeId(), othrScopeResNode);
    childSet.add(othrScopeResNode);
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getWorkpackages(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode varNode = parentNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Map<Long, Set<Long>> wpMap =
        rvwResTitleNode.getPidcRvwDetails().getVarWpMap().get(varNode.getPidcVariant().getId());
    if (!wpMap.isEmpty()) {
      wpMap.entrySet().forEach(wpEntry -> {
        PidcTreeNode wpGrpNode =
            this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + WP_GRP_PVER_PREFIX + wpEntry.getKey());
        if (wpGrpNode == null) {
          wpGrpNode = new PidcTreeNode();
        }
        wpGrpNode.setLevel(parentNode.getLevel() + 1);
        wpGrpNode.setParentNodeId(parentNode.getNodeId());
        wpGrpNode.setParentNode(parentNode);
        wpGrpNode.setNodeType(PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE);
        wpGrpNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + WP_GRP_PVER_PREFIX + wpEntry.getKey());
        A2lWorkPackage a2lWorkPackage = rvwResTitleNode.getPidcRvwDetails().getA2lWpMap().get(wpEntry.getKey());
        wpGrpNode.setName(a2lWorkPackage.getName());
        wpGrpNode.setA2lWorkpackage(a2lWorkPackage);
        wpGrpNode.setPidcVersion(parentNode.getPidcVersion());
        wpGrpNode.setPidcVariant(varNode.getPidcVariant());
        this.nodeIdNodeMap.put(wpGrpNode.getNodeId(), wpGrpNode);
        childSet.add(wpGrpNode);
      });
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getResponsibilities(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode varNode = parentNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Map<Long, Map<Long, Set<Long>>> respWpMap =
        rvwResTitleNode.getPidcRvwDetails().getVarRespWpMap().get(varNode.getPidcVariant().getId());
    if (!respWpMap.isEmpty()) {
      respWpMap.entrySet().forEach(respWpEntry -> {
        Long respId = respWpEntry.getKey();
        PidcTreeNode respNode = this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_PREFIX + respId);
        if (respNode == null) {
          respNode = new PidcTreeNode();
        }
        respNode.setLevel(parentNode.getLevel() + 1);
        respNode.setParentNodeId(parentNode.getNodeId());
        respNode.setParentNode(parentNode);
        respNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE);
        respNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_PREFIX + respId);
        A2lResponsibility a2lResponsibility = rvwResTitleNode.getPidcRvwDetails().getA2lRespMap().get(respId);
        respNode.setA2lResponsibility(a2lResponsibility);
        respNode.setName(a2lResponsibility.getName());
        respNode.setPidcVersion(parentNode.getPidcVersion());
        respNode.setPidcVariant(varNode.getPidcVariant());
        this.nodeIdNodeMap.put(respNode.getNodeId(), respNode);
        childSet.add(respNode);

      });
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getRespWPNodes(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode respTitleNode = parentNode.getParentNode();
    PidcTreeNode varNode = respTitleNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Map<Long, Map<Long, Set<Long>>> respWpMap =
        rvwResTitleNode.getPidcRvwDetails().getVarRespWpMap().get(varNode.getPidcVariant().getId());
    Map<Long, Set<Long>> wpMap = respWpMap.get(parentNode.getA2lResponsibility().getId());
    if (!wpMap.isEmpty()) {
      wpMap.entrySet().forEach(wpEntry -> {
        PidcTreeNode respWpNode =
            this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_WP_PREFIX + wpEntry.getKey());
        if (respWpNode == null) {
          respWpNode = new PidcTreeNode();
        }
        respWpNode.setParentNodeId(parentNode.getNodeId());
        respWpNode.setParentNode(parentNode);
        respWpNode.setNodeType(PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE);
        respWpNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + RESP_WP_PREFIX + wpEntry.getKey());
        A2lWorkPackage a2lWorkPackage = rvwResTitleNode.getPidcRvwDetails().getA2lWpMap().get(wpEntry.getKey());
        respWpNode.setA2lWorkpackage(a2lWorkPackage);
        respWpNode.setName(a2lWorkPackage.getName());
        respWpNode.setPidcVersion(parentNode.getPidcVersion());
        respWpNode.setPidcVariant(varNode.getPidcVariant());
        this.nodeIdNodeMap.put(respWpNode.getNodeId(), respWpNode);
        childSet.add(respWpNode);
      });
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getRespWPResults(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();
    PidcTreeNode respNode = parentNode.getParentNode();
    PidcTreeNode respTitleNode = respNode.getParentNode();
    PidcTreeNode varNode = respTitleNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Long varId = varNode.getPidcVariant().getId();
    Map<Long, Map<Long, Set<Long>>> respWpMap = rvwResTitleNode.getPidcRvwDetails().getVarRespWpMap().get(varId);
    Set<Long> resultsMap =
        respWpMap.get(respNode.getA2lResponsibility().getId()).get(parentNode.getA2lWorkpackage().getId());
    if (!resultsMap.isEmpty()) {
      resultsMap.forEach(revResId -> {
        PidcTreeNode respWpNode =
            this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_PREFIX + revResId);
        if (respWpNode == null) {
          respWpNode = new PidcTreeNode();
        }
        respWpNode.setLevel(parentNode.getLevel() + 1);
        respWpNode.setParentNodeId(parentNode.getNodeId());
        respWpNode.setParentNode(parentNode);
        respWpNode.setNodeType(PIDC_TREE_NODE_TYPE.REV_RES_NODE);
        respWpNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_PREFIX + revResId);
        CDRReviewResult cdrResult = rvwResTitleNode.getPidcRvwDetails().getCdrResultMap().get(revResId);
        respWpNode.setReviewResult(cdrResult);
        Map<Long, Long> resToVarMap = rvwResTitleNode.getPidcRvwDetails().getResToVarMap().get(revResId);
        if (CommonUtils.isNotEmpty(resToVarMap) && (resToVarMap.get(varId) != null)) {
          // set review variant
          respWpNode
              .setReviewVarResult(rvwResTitleNode.getPidcRvwDetails().getRvwVariantMap().get(resToVarMap.get(varId)));
        }
        String cdrRvwResultName = cdrResult.getName();
        if ((null != cdrResult.getPrimaryVariantId()) &&
            !cdrResult.getPrimaryVariantId().equals(respNode.getPidcVariant().getId())) {
          cdrRvwResultName = cdrRvwResultName + " - " + respNode.getPidcVariant().getName();
        }
        respWpNode.setName(cdrRvwResultName);
        this.nodeIdNodeMap.put(respWpNode.getNodeId(), respWpNode);
        childSet.add(respWpNode);
      });
    }
    return childSet.toArray();
  }

  /**
   * @param parentNode
   * @return
   */
  public Object[] getWPResults(final PidcTreeNode parentNode) {
    Set<PidcTreeNode> childSet = new TreeSet<>();

    PidcTreeNode wpTitleNode = parentNode.getParentNode();
    PidcTreeNode varNode = wpTitleNode.getParentNode();
    PidcTreeNode rvwResTitleNode = varNode.getParentNode();
    Map<Long, Set<Long>> wpResultsMap =
        rvwResTitleNode.getPidcRvwDetails().getVarWpMap().get(varNode.getPidcVariant().getId());
    Set<Long> resultsMap = wpResultsMap.get(parentNode.getA2lWorkpackage().getId());
    if (!resultsMap.isEmpty()) {
      resultsMap.forEach(revResId -> {
        PidcTreeNode respWpNode =
            this.nodeIdNodeMap.get(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_PREFIX + revResId);
        if (respWpNode == null) {
          respWpNode = new PidcTreeNode();
        }
        respWpNode.setLevel(parentNode.getLevel() + 1);
        respWpNode.setParentNodeId(parentNode.getNodeId());
        respWpNode.setParentNode(parentNode);
        respWpNode.setNodeType(PIDC_TREE_NODE_TYPE.REV_RES_NODE);
        respWpNode.setNodeId(parentNode.getNodeId() + LEVEL_SEPARATOR + CDR_PREFIX + revResId);
        CDRReviewResult cdrResult = rvwResTitleNode.getPidcRvwDetails().getCdrResultMap().get(revResId);
        respWpNode.setReviewResult(cdrResult);
        Map<Long, Long> resToVarMap = rvwResTitleNode.getPidcRvwDetails().getResToVarMap().get(revResId);
        Long varId = varNode.getPidcVariant().getId();
        if (CommonUtils.isNotEmpty(resToVarMap) && (resToVarMap.get(varId) != null)) {
          // set review variant
          respWpNode
              .setReviewVarResult(rvwResTitleNode.getPidcRvwDetails().getRvwVariantMap().get(resToVarMap.get(varId)));
        }
        String cdrRvwResultName = cdrResult.getName();
        if ((null != cdrResult.getPrimaryVariantId()) &&
            !cdrResult.getPrimaryVariantId().equals(wpTitleNode.getPidcVariant().getId())) {
          cdrRvwResultName = cdrRvwResultName + " - " + wpTitleNode.getPidcVariant().getName();
        }
        respWpNode.setName(cdrRvwResultName);
        this.nodeIdNodeMap.put(respWpNode.getNodeId(), respWpNode);
        childSet.add(respWpNode);
      });
    }
    return childSet.toArray();
  }


  /**
   * @param editorInputLinkedObj
   * @return
   */

  public PidcTreeNode getReviewResultNode(final Object editorInputLinkedObj) {
    RvwResEditorInputData rvwResEditorInput = (RvwResEditorInputData) editorInputLinkedObj;
    CDRReviewResult rvwResult = rvwResEditorInput.getRvwResult();
    PidcTreeNode pidcVersionNode = getPidcVerIdTreenodeMap().get(rvwResult.getPidcVersionId());
    pidcVersionNode = getPidcVersionNode(rvwResult, pidcVersionNode);
    String varName = PidcTreeNodeHandler.NO_VARIANT;
    if (null != rvwResEditorInput.getRvwVariant()) {
      RvwVariant rvwVar = rvwResEditorInput.getRvwVariant();
      varName = rvwVar.getVariantName();
    }
    StringBuilder linkNodeId = new StringBuilder();
    PidcTreeNode linkedNode = null;
    if (null != pidcVersionNode) {
      String varNodeId;
      linkNodeId.append(pidcVersionNode.getNodeId());
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR);
      linkNodeId.append(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType());
      linkNodeId.append(rvwResult.getPidcVersionId());
      String rvwResTiteNodeId = linkNodeId.toString();

      // get the review result title node
      Map<String, PidcTreeNode> nodeIdMap = getNodeIdNodeMap();
      PidcTreeNode rvwResTitleNode = nodeIdMap.get(rvwResTiteNodeId);
      if (null == rvwResTitleNode) {
        fetchReviewResNodeChildren(pidcVersionNode, rvwResTiteNodeId, nodeIdMap);
        rvwResTitleNode = nodeIdMap.get(rvwResTiteNodeId);
      }
      PidcReviewDetails pidcRvwDetails = rvwResTitleNode.getPidcRvwDetails();
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.CDR_VAR_PREFIX + varName);
      varNodeId = linkNodeId.toString();
      PidcTreeNode varNode = nodeIdMap.get(varNodeId);
      if (null == varNode) {
        fetchReviewResNodeChildren(pidcVersionNode, rvwResTiteNodeId, nodeIdMap);
        varNode = nodeIdMap.get(varNodeId);
      }
      // fill linked node id based on the workpackages / Responsibilities / Other nodes
      fillLinkedNodeIdBasedOnWpResp(rvwResEditorInput, rvwResult, linkNodeId, nodeIdMap, pidcRvwDetails, varNode);
      linkedNode = nodeIdMap.get(linkNodeId.toString());
    }
    return linkedNode;

  }

  /**
   * @param rvwResEditorInput
   * @param rvwResult
   * @param linkNodeId
   * @param nodeIdMap
   * @param pidcRvwDetails
   * @param varNode
   */
  private void fillLinkedNodeIdBasedOnWpResp(final RvwResEditorInputData rvwResEditorInput,
      final CDRReviewResult rvwResult, final StringBuilder linkNodeId, final Map<String, PidcTreeNode> nodeIdMap,
      final PidcReviewDetails pidcRvwDetails, final PidcTreeNode varNode) {
    if ((rvwResEditorInput.getA2lWorkpackageId() != null) && (rvwResEditorInput.getA2lRespId() == null)) {
      // WP res node
      linkNodeId
          .append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.getUiType());
      String wpTitleNodeId = linkNodeId.toString();
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.WP_GRP_PVER_PREFIX +
          rvwResEditorInput.getA2lWorkpackageId());
      String wpNodeId = linkNodeId.toString();
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.CDR_PREFIX + rvwResult.getId());

      if (null == this.nodeIdNodeMap.get(linkNodeId.toString())) {
        getTitlesForRvwResults(varNode);
        getWorkpackages(this.nodeIdNodeMap.get(wpTitleNodeId));
        getWPResults(this.nodeIdNodeMap.get(wpNodeId));
      }
    }
    else if ((rvwResEditorInput.getA2lWorkpackageId() != null) && (rvwResEditorInput.getA2lRespId() != null)) {
      // Resp WP node
      linkNodeId.append(
          PidcTreeNodeHandler.LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.getUiType());
      String respTitleNodeId = linkNodeId.toString();
      linkNodeId.append(
          PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.RESP_PREFIX + rvwResEditorInput.getA2lRespId());
      String respNodeId = linkNodeId.toString();
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.RESP_WP_PREFIX +
          rvwResEditorInput.getA2lWorkpackageId());
      if (null == this.nodeIdNodeMap.get(linkNodeId.toString())) {
        getTitlesForRvwResults(varNode);
        getResponsibilities(this.nodeIdNodeMap.get(respTitleNodeId));
        getRespWPNodes(this.nodeIdNodeMap.get(respNodeId));
        getRespWPResults(this.nodeIdNodeMap.get(linkNodeId.toString()));
      }
      linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.CDR_PREFIX + rvwResult.getId());
    }
    else {
      // other scopes
      linkToOtherScopes(rvwResEditorInput, rvwResult, linkNodeId, nodeIdMap, pidcRvwDetails, varNode);
    }
  }

  /**
   * @param rvwResult
   * @param pidcVersionNode
   * @return
   */
  private PidcTreeNode getPidcVersionNode(final CDRReviewResult rvwResult, PidcTreeNode pidcVersionNode) {
    if (null == pidcVersionNode) {
      PidcVersionServiceClient pidcVerSer = new PidcVersionServiceClient();
      Set<Long> pidcVerIdSet = new HashSet<>();
      pidcVerIdSet.add(rvwResult.getPidcVersionId());
      try {
        Map<Long, PidcVersionInfo> verMap = pidcVerSer.getActiveVersWithStrByOtherVerId(pidcVerIdSet);
        PidcVersionInfo verInfo = verMap.values().iterator().next();
        PidcTreeNode activeVerNode = getPidcVerIdTreenodeMap().get(verInfo.getPidcVersion().getId());
        PidcTreeNode otherVerTitleNode = createOtherVerTitleNode(activeVerNode);
        getOtherPidcVerNodes(otherVerTitleNode);
        pidcVersionNode = getPidcVerIdTreenodeMap().get(rvwResult.getPidcVersionId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return pidcVersionNode;
  }

  /**
   * @param pidcVersionNode
   * @param rvwResTiteNodeId
   * @param nodeIdNodeMap
   */
  private void fetchReviewResNodeChildren(final PidcTreeNode pidcVersionNode, final String rvwResTiteNodeId,
      final Map<String, PidcTreeNode> nodeIdNodeMap) {
    PidcTreeNode rvwResTitleNode;
    getPidcVerNodeChildren(pidcVersionNode);
    rvwResTitleNode = nodeIdNodeMap.get(rvwResTiteNodeId);
    getRvwVariantNodes(rvwResTitleNode, true);
  }

  /**
   * @param rvwResEditorInput
   * @param rvwResult
   * @param linkNodeId
   * @param nodeIdNodeMap
   * @param pidcRvwDetails
   * @param varNode
   * @return
   */
  private boolean linkToOtherScopes(final RvwResEditorInputData rvwResEditorInput, final CDRReviewResult rvwResult,
      final StringBuilder linkNodeId, final Map<String, PidcTreeNode> nodeIdNodeMap,
      final PidcReviewDetails pidcRvwDetails, final PidcTreeNode varNode) {
    RvwVariant rvwVariant = rvwResEditorInput.getRvwVariant();
    Long varId = rvwVariant == null ? ApicConstants.NO_VARIANT_ID : rvwVariant.getVariantId();
    Map<String, Set<Long>> othrScopeResMap = pidcRvwDetails.getOtherSrcTypeResults().get(varId);
    if (null != othrScopeResMap) {
      for (Entry<String, Set<Long>> othrScopeEntry : othrScopeResMap.entrySet()) {
        if (othrScopeEntry.getValue().contains(rvwResult.getId())) {
          linkNodeId.append(
              PidcTreeNodeHandler.LEVEL_SEPARATOR + PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE.getUiType());
          String othrScopeTitleNodeId = linkNodeId.toString();
          linkNodeId.append(
              PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.CDR_OTHER_SCOPE + othrScopeEntry.getKey());
          String othrScopeNodeId = linkNodeId.toString();
          linkNodeId.append(PidcTreeNodeHandler.LEVEL_SEPARATOR + PidcTreeNodeHandler.CDR_PREFIX + rvwResult.getId());
          if (null == nodeIdNodeMap.get(linkNodeId.toString())) {
            getTitlesForRvwResults(varNode);
            getOtherRvwScopesNodes((nodeIdNodeMap.get(othrScopeTitleNodeId)));
            getOtherScopeRvwRes((nodeIdNodeMap.get(othrScopeNodeId)));
          }

          return true;
        }
      }
    }
    return false;
  }

}
