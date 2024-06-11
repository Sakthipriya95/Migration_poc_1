/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode.PIDC_TREE_NODE_TYPE;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.a2l.A2lWorkPackage;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcReviewDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.ReviewResultDeleteValidation;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class ReviewListEditorDataHandler extends AbstractClientDataHandler {

  /**
   *
   */
  private static final String EDITORNAME_DELIMITER = " >> ";


  /**
  *
  */
  private static final String A2L_RESPONSIBILITY = "A2L Responsibility";


  /**
   * input set
   */
  private SortedSet<ReviewVariantModel> pidcRvwsNatInputs = new TreeSet<>();

  private PidcVersion pidcVersion;


  /**
   * PIDCVariant instance
   */
  private PidcVariant pidcVariant;


  private PidcReviewDetails pidcReviewDetails;

  private A2lWorkPackage a2lWorkpackage;

  private A2lResponsibility a2lResponsibility;

  private String pidcNodeName;

  private PIDC_TREE_NODE_TYPE pidcNodeType;

  private String effectiveName;

  private final PidcTreeNode pidcTreeNode;


  /**
   * Constructor
   *
   * @param pidcTreeNode PidcTreeNode
   */
  public ReviewListEditorDataHandler(final PidcTreeNode pidcTreeNode) {
    super();
    this.pidcTreeNode = pidcTreeNode;
    fillDataForReviewListEditor(this.pidcTreeNode);

  }


  private void fillDataForReviewListEditor(final PidcTreeNode pidcTreeNode) {

    this.pidcNodeType = pidcTreeNode.getNodeType();
    this.pidcVersion = pidcTreeNode.getPidcVersion();
    this.pidcVariant = pidcTreeNode.getPidcVariant();
    this.a2lWorkpackage = pidcTreeNode.getA2lWorkpackage();
    this.a2lResponsibility = pidcTreeNode.getA2lResponsibility();
    this.pidcNodeName = pidcTreeNode.getName();

    getReviewDetailsAndEffectiveName(pidcTreeNode);

  }


  /**
   * @param pidcTreeNode
   */
  private void getReviewDetailsAndEffectiveName(final PidcTreeNode pidcTreeNode) {
    // For Review Result Node Level 0
    if (PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.equals(pidcTreeNode.getNodeType())) {
      this.pidcReviewDetails = pidcTreeNode.getPidcRvwDetails();
      this.effectiveName = getEffectiveName(0l, null);
    } // For Variant Node Level 1
    else if (PIDC_TREE_NODE_TYPE.CDR_VAR_NODE.equals(pidcTreeNode.getNodeType())) {
      this.pidcReviewDetails = pidcTreeNode.getParentNode().getPidcRvwDetails();
      this.effectiveName = getEffectiveName(1l, null);

    } // For Variant Node Level 1 at A2L structure
    else if (PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE.equals(pidcTreeNode.getNodeType())) {
      // Get Review results for the selected variant from the review result node
      this.pidcReviewDetails = pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcChildrenMap()
          .get(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType()).get(0).getPidcRvwDetails();
      PidcA2l pidcA2l = pidcTreeNode.getParentNode().getPidcA2l();
      this.effectiveName = getEffectiveName(1l, getA2lName(pidcA2l));
    } // For Workpackage/Responsible/OtherScope nodes Level 2
    else if (PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.equals(pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.equals(pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE.equals(pidcTreeNode.getNodeType())) {
      this.pidcReviewDetails = pidcTreeNode.getParentNode().getParentNode().getPidcRvwDetails();
      this.effectiveName = getEffectiveName(2l, null);
    } // For Level 3 node WP names and responsible
    else if (PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE.equals(pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE.equals(pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE.equals(pidcTreeNode.getNodeType())) {
      this.pidcReviewDetails = pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcRvwDetails();
      this.effectiveName = getEffectiveName(3l, null);
    } // For Level 3 node - Responsibilities in A2L Structure
    else if (PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE.equals(pidcTreeNode.getNodeType())) {
      // Get Review results for the selected responsibility from the review result node
      this.pidcReviewDetails = pidcTreeNode.getParentNode().getParentNode().getParentNode().getParentNode()
          .getPidcChildrenMap().get(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType()).get(0).getPidcRvwDetails();
      PidcA2l pidcA2l = pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
      this.effectiveName = getEffectiveName(3l, getA2lName(pidcA2l));
    } // For Level 4 for wp name
    else if (PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE.equals(pidcTreeNode.getNodeType())) {
      this.pidcReviewDetails =
          pidcTreeNode.getParentNode().getParentNode().getParentNode().getParentNode().getPidcRvwDetails();
      this.effectiveName = getEffectiveName(4l, null);
    } // For Level 4 for wp name in A2L structure
    else if (PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE.equals(pidcTreeNode.getNodeType())) {
      // Get Review results for the selected workpackage from the review result node
      this.pidcReviewDetails = pidcTreeNode.getParentNode().getParentNode().getParentNode().getParentNode()
          .getParentNode().getPidcChildrenMap().get(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType()).get(0)
          .getPidcRvwDetails();
      PidcA2l pidcA2l = pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcA2l();
      this.effectiveName = getEffectiveName(4l, getA2lName(pidcA2l));
    }
    else if (PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION.equals(pidcTreeNode.getNodeType())) {
      getRvwDtlsAndEffctvNameForActvPidcVersion(pidcTreeNode);
    }
  }


  /**
   * @param pidcTreeNode
   */
  private void getRvwDtlsAndEffctvNameForActvPidcVersion(final PidcTreeNode pidcTreeNode) {
    if ((null != pidcTreeNode.getPidcChildrenMap()) && !pidcTreeNode.getPidcChildrenMap().isEmpty() &&
        pidcTreeNode.getPidcChildrenMap().containsKey(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType())) {
      List<PidcTreeNode> reviewResultTreeNode =
          pidcTreeNode.getPidcChildrenMap().get(PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.getUiType());
      this.pidcReviewDetails = reviewResultTreeNode.get(0).getPidcRvwDetails();
    }
    else if (null != pidcTreeNode.getPidcVersion()) {
      try {
        this.pidcReviewDetails = new CDRReviewResultServiceClient().getReviewResultInfo(getPidcVersion().getId());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }
    // Level 0 for active pidc version
    this.effectiveName = getEffectiveName(0l, null);
  }


  /**
   * @param pidcA2l
   */
  private String getA2lName(final PidcA2l pidcA2l) {
    if (CommonUtils.isNotNull(pidcA2l) && CommonUtils.isNotEmptyString(pidcA2l.getDescription())) {
      return pidcA2l.getDescription();
    }
    return null;
  }


  private String getEffectiveName(final Long treeLevel, final String a2lName) {
    StringBuilder editorName = new StringBuilder();
    for (int i = 0; i <= treeLevel; i++) {
      switch (i) {
        case 0:
          editorName.append(this.pidcVersion.getName());
          if (CommonUtils.isNotEmptyString(a2lName)) {
            editorName.append(EDITORNAME_DELIMITER);
            editorName.append(a2lName);
          }
          break;

        case 1:
          editorName.append(EDITORNAME_DELIMITER);
          editorName.append(this.pidcVariant.getName());
          break;
        case 2:
          formLevel2EditorName(editorName);
          break;

        case 3:
          formLevel3EditorName(editorName);
          break;

        case 4:
          editorName.append(" (" + this.pidcNodeName + ")");
          break;

        default:
          break;
      }
    }
    return editorName.toString();
  }


  /**
   * @param editorName
   */
  private void formLevel3EditorName(final StringBuilder editorName) {
    if (PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE.equals(this.pidcTreeNode.getNodeType())) {
      editorName.append(EDITORNAME_DELIMITER);
      editorName.append(this.pidcNodeName);
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE.equals(this.pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE.equals(this.pidcTreeNode.getNodeType())) {
      editorName.append(EDITORNAME_DELIMITER);
      editorName.append(this.pidcTreeNode.getParentNode().getName());
    }
    else {
      editorName.append(" (" + this.pidcNodeName + ")");
    }
  }


  /**
   * @param editorName
   */
  private void formLevel2EditorName(final StringBuilder editorName) {
    editorName.append(EDITORNAME_DELIMITER);
    if (PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE.equals(this.pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE.equals(this.pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE.equals(this.pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE.equals(this.pidcTreeNode.getNodeType())) {
      editorName.append(this.pidcTreeNode.getParentNode().getNodeType().getUiType());
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE.equals(this.pidcTreeNode.getNodeType()) ||
        PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE.equals(this.pidcTreeNode.getNodeType())) {
      editorName.append(A2L_RESPONSIBILITY);
    }
    else {
      editorName.append(this.pidcTreeNode.getNodeType().getUiType());
    }
  }


  /**
   * @param sourceDateStr sourceDate
   * @return the formatted date.
   */
  public String getFormattedDate(final String sourceDateStr) {
    SimpleDateFormat dateFormatsource = new SimpleDateFormat(DateFormat.DATE_FORMAT_15);
    Date date = null;

    try {
      date = dateFormatsource.parse(sourceDateStr);
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    SimpleDateFormat dateFormatDest = new SimpleDateFormat(DateFormat.DATE_FORMAT_04);
    return dateFormatDest.format(date);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {


    registerCnsChecker(MODEL_TYPE.CDR_RESULT, chData -> {
      Long pidcVers = ((CDRReviewResult) CnsUtils.getModel(chData)).getPidcVersionId();
      return CommonUtils.isEqual(pidcVers, this.pidcVersion.getId());
    });
    registerCnsChecker(MODEL_TYPE.CDR_RES_VARIANTS, chData -> {
      Long varId = ((RvwVariant) CnsUtils.getModel(chData)).getVariantId();
      Long resultId = ((RvwVariant) CnsUtils.getModel(chData)).getResultId();
      Long pidcVersionId = null;
      try {
        pidcVersionId = new CDRReviewResultServiceClient().getById(resultId).getPidcVersionId();
      }
      catch (ApicWebServiceException exp) {
        if (CommonUtils.isEqual(exp.getErrorCode(), "DATA_NOT_FOUND")) {
          CDMLogger.getInstance().warn(exp.getMessage(), exp);
        }
        else {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
        }
      }

      return isPidcVarSame(varId) || isPidcVersionSame(pidcVersionId);
    });

    registerCnsAction(this::refreshReviewListForRemoteChangesforRvwResult, MODEL_TYPE.CDR_RESULT);
    registerCnsAction(this::refreshReviewListForRemoteChangesforRvwVariant, MODEL_TYPE.CDR_RES_VARIANTS);
  }


  /**
   * @param varId
   * @return
   */
  private boolean isPidcVarSame(final Long varId) {
    return (this.pidcVariant != null) && CommonUtils.isEqual(varId, this.pidcVariant.getId());
  }


  /**
   * @param pidcVersionId
   * @return
   */
  private boolean isPidcVersionSame(final Long pidcVersionId) {
    return (this.pidcVariant == null) && (pidcVersionId != null) &&
        CommonUtils.isEqual(pidcVersionId, this.pidcVersion.getId());
  }


  private void refreshReviewListForRemoteChangesforRvwResult(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    Set<Long> cdrIdSet = new HashSet<>();
    Set<ReviewVariantModel> removableReviewVaiantModelSet = new HashSet<>();
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE) ||
          (changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        cdrIdSet.add(changeData.getObjId());
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        CDRReviewResult cdrReviewResult = (CDRReviewResult) changeData.getRemovedData();

        for (ReviewVariantModel reviewVariantModel : this.pidcRvwsNatInputs) {
          if (cdrReviewResult.equals(reviewVariantModel.getReviewResultData().getCdrReviewResult())) {
            removableReviewVaiantModelSet.add(reviewVariantModel);
          }
        }
        this.pidcRvwsNatInputs.removeAll(removableReviewVaiantModelSet);
      }
    }
    addToPidcRvwsNatInputs(cdrIdSet);
  }

  /**
   * @param cdrIdSet
   */
  private void addToPidcRvwsNatInputs(final Set<Long> cdrIdSet) {
    if (!cdrIdSet.isEmpty()) {
      try {
        Set<ReviewVariantModel> reviewVariantModelSet =
            new RvwVariantServiceClient().getReviewVariantModelSet(cdrIdSet);
        reviewVariantModelSet.forEach(rvwVarModel -> {
          if (this.pidcRvwsNatInputs.contains(rvwVarModel)) {
            this.pidcRvwsNatInputs.remove(rvwVarModel);
          }
          this.pidcRvwsNatInputs.add(rvwVarModel);
        });
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
  }


  private void refreshReviewListForRemoteChangesforRvwVariant(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    Set<Long> cdrIdSet = new HashSet<>();
    Set<ReviewVariantModel> removableReviewVaiantModelSet = new HashSet<>();
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE) ||
          (changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        try {
          cdrIdSet.add(new RvwVariantServiceClient().getById(changeData.getObjId()).getResultId());
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
        }
      }
      else if (changeData.getChangeType() == CHANGE_OPERATION.DELETE) {
        RvwVariant cdrVariant = (RvwVariant) changeData.getRemovedData();

        for (ReviewVariantModel reviewVariantModel : this.pidcRvwsNatInputs) {
          if (cdrVariant.equals(reviewVariantModel.getRvwVariant())) {
            removableReviewVaiantModelSet.add(reviewVariantModel);
          }
        }
        this.pidcRvwsNatInputs.removeAll(removableReviewVaiantModelSet);
      }
    }
    addToPidcRvwsNatInputs(cdrIdSet);
  }


  /**
   * @param resultId review result id
   * @return
   * @throws ApicWebServiceException error caused during data fetch
   */
  public ReviewResultDeleteValidation reviewResultDeleteValidation(final Long resultId) throws ApicWebServiceException {
    return new CDRReviewResultServiceClient().reviewResultDeleteValidation(resultId);
  }

  /**
   * @return treeset of reviewvariant
   */
  public void createTableInputSet() {
    Set<Long> cdrIdSet = resolveCdrResultsForSelectedPidcTreeNode();
    Set<ReviewVariantModel> reviewVariantModelSet = new TreeSet<>();
    try {
      reviewVariantModelSet = new RvwVariantServiceClient().getReviewVariantModelSet(cdrIdSet);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    if (null != this.pidcTreeNode.getPidcVariant()) {
      for (ReviewVariantModel reviewVariantModel : reviewVariantModelSet) {
        if ((null == reviewVariantModel.getRvwVariant().getVariantId()) ||
            ((null != reviewVariantModel.getRvwVariant().getVariantId()) &&
                reviewVariantModel.getRvwVariant().getVariantId().equals(this.pidcTreeNode.getPidcVariant().getId()))) {
          this.pidcRvwsNatInputs.add(reviewVariantModel);
        }
      }
    }
    else {
      this.pidcRvwsNatInputs.addAll(reviewVariantModelSet);
    }
  }


  private Set<Long> resolveCdrResultsForSelectedPidcTreeNode() {
    Set<Long> cdrreviewIds = new HashSet<>();
    if (varRespWpMapContainsRespId()) {
      cdrreviewIds.addAll(resolveCdrResultsForSelectedRespNode());
    }
    else if (varWpMapContainsWpId()) {
      cdrreviewIds
          .addAll(this.pidcReviewDetails.getVarWpMap().get(this.pidcVariant.getId()).get(this.a2lWorkpackage.getId()));
    }
    else if (isA2lRespWpRespAvailInMap()) {
      cdrreviewIds.addAll(resolveCdrResultsForWpNode());
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.equals(this.pidcNodeType) &&
        this.pidcReviewDetails.getVarWpMap().containsKey(this.pidcVariant.getId())) {
      resolveCdrResultsForWorkpackagesTitleNode(cdrreviewIds);
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.equals(this.pidcNodeType) &&
        this.pidcReviewDetails.getVarRespWpMap().containsKey(this.pidcVariant.getId())) {
      resolveCdrResultsForRvwRespTitleNode(cdrreviewIds);
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_TITLE_NODE.equals(this.pidcNodeType) &&
        this.pidcReviewDetails.getOtherSrcTypeResults().containsKey(this.pidcVariant.getId())) {
      resolveCdrResultsForOthrRvwScopesTitleNode(cdrreviewIds);
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_OTHER_RVW_SCOPES_NODE.equals(this.pidcNodeType) &&
        this.pidcReviewDetails.getOtherSrcTypeResults().containsKey(this.pidcVariant.getId()) &&
        this.pidcReviewDetails.getOtherSrcTypeResults().get(this.pidcVariant.getId()).containsKey(this.pidcNodeName)) {
      cdrreviewIds
          .addAll(this.pidcReviewDetails.getOtherSrcTypeResults().get(this.pidcVariant.getId()).get(this.pidcNodeName));
    }
    else if (PIDC_TREE_NODE_TYPE.CDR_VAR_NODE.equals(this.pidcNodeType) ||
        PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE.equals(this.pidcNodeType)) {
      cdrreviewIds.addAll(resolveCdrResultsForVariantNodes());
    }
    else if (PIDC_TREE_NODE_TYPE.RVW_RES_TITLE_NODE_NEW.equals(this.pidcNodeType) ||
        PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION.equals(this.pidcNodeType)) {
      resolveCdrResultsForRvwResultsAndActvPidcVersNode(cdrreviewIds);
    }

    return cdrreviewIds;
  }


  /**
   * @return
   */
  private Set<Long> resolveCdrResultsForWpNode() {
    Set<Long> cdrreviewIds = new HashSet<>();
    cdrreviewIds.addAll(this.pidcReviewDetails.getVarRespWpMap().get(this.pidcVariant.getId())
        .get(this.a2lResponsibility.getId()).get(this.a2lWorkpackage.getId()));

    if (PIDC_TREE_NODE_TYPE.RVW_QNAIRE_WP_NODE.equals(this.pidcNodeType)) {
      PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcA2l();
      return resolveCdrResultsForA2lNode(cdrreviewIds, pidcA2l);
    }
    return cdrreviewIds;
  }


  /**
   * @return
   */
  private boolean varRespWpMapContainsRespId() {
    return ((this.a2lResponsibility != null) && (this.a2lWorkpackage == null) &&
        this.pidcReviewDetails.getVarRespWpMap().containsKey(this.pidcVariant.getId()) && this.pidcReviewDetails
            .getVarRespWpMap().get(this.pidcVariant.getId()).containsKey(this.a2lResponsibility.getId()));
  }


  /**
   * @return
   */
  private boolean varWpMapContainsWpId() {
    return ((this.a2lResponsibility == null) && (this.a2lWorkpackage != null) &&
        this.pidcReviewDetails.getVarWpMap().containsKey(this.pidcVariant.getId()) &&
        this.pidcReviewDetails.getVarWpMap().get(this.pidcVariant.getId()).containsKey(this.a2lWorkpackage.getId()));
  }


  /**
   * @param cdrreviewIds
   */
  private Set<Long> resolveCdrResultsForSelectedRespNode() {
    Set<Long> cdrreviewIds = new HashSet<>();
    Map<Long, Set<Long>> wpMap =
        this.pidcReviewDetails.getVarRespWpMap().get(this.pidcVariant.getId()).get(this.a2lResponsibility.getId());
    for (Set<Long> cdrIdSet : wpMap.values()) {
      cdrreviewIds.addAll(cdrIdSet);
    }

    if (PIDC_TREE_NODE_TYPE.RVW_QNAIRE_RESPONSIBILITY_NODE.equals(this.pidcNodeType)) {
      PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
      return resolveCdrResultsForA2lNode(cdrreviewIds, pidcA2l);
    }
    return cdrreviewIds;
  }


  /**
   * @param cdrreviewIds
   */
  private void resolveCdrResultsForWorkpackagesTitleNode(final Set<Long> cdrreviewIds) {
    Map<Long, Set<Long>> wpMap = this.pidcReviewDetails.getVarWpMap().get(this.pidcVariant.getId());
    for (Set<Long> cdrIdSet : wpMap.values()) {
      cdrreviewIds.addAll(cdrIdSet);
    }
  }


  /**
   * @param cdrreviewIds
   */
  private void resolveCdrResultsForOthrRvwScopesTitleNode(final Set<Long> cdrreviewIds) {
    Map<String, Set<Long>> otherWpMap = this.pidcReviewDetails.getOtherSrcTypeResults().get(this.pidcVariant.getId());
    for (Set<Long> cdrIdSet : otherWpMap.values()) {
      cdrreviewIds.addAll(cdrIdSet);
    }
  }


  /**
   * @param cdrreviewIds
   */
  private void resolveCdrResultsForRvwResultsAndActvPidcVersNode(final Set<Long> cdrreviewIds) {
    if ((this.pidcReviewDetails.getVarRespWpMap() != null) && !this.pidcReviewDetails.getVarRespWpMap().isEmpty()) {
      getCdrRvwIdsFromVarRespWpMap(cdrreviewIds);
    }
    if ((this.pidcReviewDetails.getOtherSrcTypeResults() != null) &&
        !this.pidcReviewDetails.getOtherSrcTypeResults().isEmpty()) {
      for (Map<String, Set<Long>> otherWpMap : this.pidcReviewDetails.getOtherSrcTypeResults().values()) {
        for (Set<Long> cdrIdSet : otherWpMap.values()) {
          cdrreviewIds.addAll(cdrIdSet);
        }
      }
    }
  }


  /**
   * @param cdrreviewIds
   */
  private void getCdrRvwIdsFromVarRespWpMap(final Set<Long> cdrreviewIds) {
    for (Map<Long, Map<Long, Set<Long>>> varRespWpMap : this.pidcReviewDetails.getVarRespWpMap().values()) {
      for (Map<Long, Set<Long>> respWpMap : varRespWpMap.values()) {
        for (Set<Long> cdrIdSet : respWpMap.values()) {
          cdrreviewIds.addAll(cdrIdSet);
        }
      }
    }
  }


  /**
   * @return
   */
  private Set<Long> resolveCdrResultsForVariantNodes() {
    Set<Long> cdrreviewIds = new HashSet<>();
    Map<Long, Map<Long, Set<Long>>> respWpMap = this.pidcReviewDetails.getVarRespWpMap().get(this.pidcVariant.getId());
    if ((respWpMap != null) && !respWpMap.isEmpty()) {
      for (Map<Long, Set<Long>> wpMap : respWpMap.values()) {
        for (Set<Long> cdrIdSet : wpMap.values()) {
          cdrreviewIds.addAll(cdrIdSet);
        }
      }
    }
    if (this.pidcReviewDetails.getOtherSrcTypeResults().containsKey(this.pidcVariant.getId())) {
      for (Set<Long> cdrIdSet : this.pidcReviewDetails.getOtherSrcTypeResults().get(this.pidcVariant.getId())
          .values()) {
        cdrreviewIds.addAll(cdrIdSet);
      }
    }

    if (PIDC_TREE_NODE_TYPE.QNAIRE_VAR_NODE.equals(this.pidcNodeType)) {
      PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getPidcA2l();
      return resolveCdrResultsForA2lNode(cdrreviewIds, pidcA2l);
    }

    return cdrreviewIds;
  }

  /**
   * @param pidcA2l
   * @param cdrreviewIds
   */
  private Set<Long> resolveCdrResultsForA2lNode(final Set<Long> cdrIds, final PidcA2l pidcA2l) {

    Long pidcA2lId;

    if (CommonUtils.isNotNull(pidcA2l) && CommonUtils.isNotNull(pidcA2l.getId())) {
      pidcA2lId = pidcA2l.getId();

      return cdrIds.stream()
          .filter(id -> (this.pidcReviewDetails.getCdrResultMap().get(id).getPidcA2lId().equals(pidcA2lId)))
          .collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  /**
   * @param cdrreviewIds
   */
  private void resolveCdrResultsForRvwRespTitleNode(final Set<Long> cdrreviewIds) {
    Map<Long, Map<Long, Set<Long>>> respWpMap = this.pidcReviewDetails.getVarRespWpMap().get(this.pidcVariant.getId());
    for (Map<Long, Set<Long>> wpMap : respWpMap.values()) {
      for (Set<Long> cdrIdSet : wpMap.values()) {
        cdrreviewIds.addAll(cdrIdSet);
      }
    }
  }


  /**
   * @return
   */
  private boolean isA2lRespWpRespAvailInMap() {

    return ((this.a2lWorkpackage != null) && (this.a2lResponsibility != null) &&
        this.pidcReviewDetails.getVarRespWpMap().containsKey(this.pidcVariant.getId()) &&
        this.pidcReviewDetails.getVarRespWpMap().get(this.pidcVariant.getId())
            .containsKey(this.a2lResponsibility.getId()) &&
        this.pidcReviewDetails.getVarRespWpMap().get(this.pidcVariant.getId()).get(this.a2lResponsibility.getId())
            .containsKey(this.a2lWorkpackage.getId()));
  }


  /**
   * @return the pidcRvwsNatInputs
   */
  public SortedSet<ReviewVariantModel> getPidcRvwsNatInputs() {
    if ((this.pidcRvwsNatInputs == null) || this.pidcRvwsNatInputs.isEmpty()) {
      createTableInputSet();
    }
    return this.pidcRvwsNatInputs;
  }


  /**
   * @return the pidcVersion
   */
  public PidcVersion getPidcVersion() {
    return this.pidcVersion;
  }

  /**
   * @return the pidcVar
   */
  public PidcVariant getPidcVariant() {
    return this.pidcVariant;
  }

  /**
   * @return the pidcReviewDetails
   */
  public PidcReviewDetails getPidcReviewDetails() {
    return this.pidcReviewDetails;
  }


  /**
   * @param pidcReviewDetails the pidcReviewDetails to set
   */
  public void setPidcReviewDetails(final PidcReviewDetails pidcReviewDetails) {
    this.pidcReviewDetails = pidcReviewDetails;
  }


  /**
   * @return the a2lWorkpackage
   */
  public A2lWorkPackage getA2lWorkpackage() {
    return this.a2lWorkpackage;
  }


  /**
   * @param a2lWorkpackage the a2lWorkpackage to set
   */
  public void setA2lWorkpackage(final A2lWorkPackage a2lWorkpackage) {
    this.a2lWorkpackage = a2lWorkpackage;
  }


  /**
   * @return the a2lResponsibility
   */
  public A2lResponsibility getA2lResponsibility() {
    return this.a2lResponsibility;
  }


  /**
   * @param a2lResponsibility the a2lResponsibility to set
   */
  public void setA2lResponsibility(final A2lResponsibility a2lResponsibility) {
    this.a2lResponsibility = a2lResponsibility;
  }


  /**
   * @return the pidcNodeName
   */
  public String getPidcNodeName() {
    return this.pidcNodeName;
  }


  /**
   * @param pidcNodeName the pidcNodeName to set
   */
  public void setPidcNodeName(final String pidcNodeName) {
    this.pidcNodeName = pidcNodeName;
  }

  /**
   * @return the pidcNodeType
   */
  public PIDC_TREE_NODE_TYPE getPidcNodeType() {
    return this.pidcNodeType;
  }


  /**
   * @param pidcNodeType the pidcNodeType to set
   */
  public void setPidcNodeType(final PIDC_TREE_NODE_TYPE pidcNodeType) {
    this.pidcNodeType = pidcNodeType;
  }


  /**
   * @param pidcRvwsNatInputs the pidcRvwsNatInputs to set
   */
  public void setPidcRvwsNatInputs(final SortedSet<ReviewVariantModel> pidcRvwsNatInputs) {
    this.pidcRvwsNatInputs = pidcRvwsNatInputs;
  }


  /**
   * @param pidcVersion the pidcVersion to set
   */
  public void setPidcVersion(final PidcVersion pidcVersion) {
    this.pidcVersion = pidcVersion;
  }


  /**
   * @param pidcVariant the pidcVariant to set
   */
  public void setPidcVariant(final PidcVariant pidcVariant) {
    this.pidcVariant = pidcVariant;
  }


  /**
   * @param effectiveName the effectiveName to set
   */
  public void setEffectiveName(final String effectiveName) {
    this.effectiveName = effectiveName;
  }


  /**
   * @return the effectiveName
   */
  public String getEffectiveName() {
    return this.effectiveName;
  }

}
