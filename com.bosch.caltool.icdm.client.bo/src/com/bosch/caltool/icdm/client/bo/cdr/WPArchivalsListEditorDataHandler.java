/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.cdr;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

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
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.ws.rest.client.cdr.WpArchivalServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author ukt1cob
 */
public class WPArchivalsListEditorDataHandler extends AbstractClientDataHandler {

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
  private SortedSet<WpArchival> wpArchivalsNatInputs = new TreeSet<>();

  private PidcVersion pidcVersion;


  /**
   * PIDCVariant instance
   */
  private PidcVariant pidcVariant;


  private Set<WpArchival> wpArchivals = new HashSet<>();

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
  public WPArchivalsListEditorDataHandler(final PidcTreeNode pidcTreeNode) {
    super();
    this.pidcTreeNode = pidcTreeNode;
    fillDataForWPArchivalsListEditor();
  }


  private void fillDataForWPArchivalsListEditor() {

    this.pidcNodeType = this.pidcTreeNode.getNodeType();
    this.pidcVersion = this.pidcTreeNode.getPidcVersion();
    this.pidcVariant = this.pidcTreeNode.getPidcVariant();
    this.a2lWorkpackage = this.pidcTreeNode.getA2lWorkpackage();
    this.a2lResponsibility = this.pidcTreeNode.getA2lResponsibility();
    this.pidcNodeName = this.pidcTreeNode.getName();

    getWPArchivalDetailsAndEffectiveName();
  }

  /**
  *
  */
  private static final String RVW_WORKPACAKGES_TITLE_NODE = "RVW_WORKPACAKGES_TITLE_NODE";

  /**
   *
   */
  private static final String RVW_RESPONSIBILITIES_TITLE_NODE = "RVW_RESPONSIBILITIES_TITLE_NODE";

  private void getWpForWpsAndResps(final WpArchivalServiceClient wpArchivalServiceClient, final Long varId)
      throws ApicWebServiceException {
    /* Below logic is for work package under review results */
    if (PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.equals(this.pidcTreeNode.getNodeType())) {
      /* If no variant case we need only the WP archivals under the PIDC version */
      if ((varId != null) && varId.equals(-1L)) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(
            this.pidcTreeNode.getPidcVersion().getId(), null, varId, null, null, RVW_WORKPACAKGES_TITLE_NODE);
      }
      else {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(null, null, varId, null, null,
            RVW_WORKPACAKGES_TITLE_NODE);
      }

      PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getPidcA2l();
      this.effectiveName = getEffectiveName(2l, getA2lName(pidcA2l));
    } /* Below logic is for Resposibility under review results */
    else if (PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.equals(this.pidcTreeNode.getNodeType())) {
      /* If no variant case we need only the WP archivals under the PIDC version */
      if ((varId != null) && varId.equals(-1L)) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(
            this.pidcTreeNode.getPidcVersion().getId(), null, varId, null, null, RVW_RESPONSIBILITIES_TITLE_NODE);
      }
      else {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(null, null, varId, null, null,
            RVW_RESPONSIBILITIES_TITLE_NODE);
      }

      PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
      this.effectiveName = getEffectiveName(2l, getA2lName(pidcA2l));
    }
  }

  /**
   * @param pidcTreeNode
   */
  private void getWPArchivalDetailsAndEffectiveName() {

    try {
      WpArchivalServiceClient wpArchivalServiceClient = new WpArchivalServiceClient();
      Long varId = CommonUtils.isNull(getPidcVariant()) ? null : getPidcVariant().getId();

      if (PIDC_TREE_NODE_TYPE.RVW_WORKPACAKGES_TITLE_NODE.equals(this.pidcTreeNode.getNodeType()) ||
          PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITIES_TITLE_NODE.equals(this.pidcTreeNode.getNodeType())) {
        getWpForWpsAndResps(wpArchivalServiceClient, varId);
      }
      /* Below logic is for responsible under Resposibility under review results */
      else if (PIDC_TREE_NODE_TYPE.RVW_RESPONSIBILITY_NODE.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(null, null, varId,
            getA2lResponsibility().getId(), null, "RVW_RESPONSIBILITY_NODE");

        PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
        this.effectiveName = getEffectiveName(3l, getA2lName(pidcA2l));
      } /* Below logic is for WP inder a responsible under Resposibility under review results */
      else if (PIDC_TREE_NODE_TYPE.RVW_RESP_WP_NODE.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(null, null, varId,
            this.pidcTreeNode.getParentNode().getA2lResponsibility().getId(), this.pidcTreeNode.getName(),
            "RVW_RESP_WP_NODE");

        PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
        this.effectiveName = getEffectiveName(4l, getA2lName(pidcA2l));
      }
      else if (PIDC_TREE_NODE_TYPE.PIDC_A2L_VAR_NODE.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(null,
            this.pidcTreeNode.getPidcA2l().getId(), varId, null, null, "PIDC_A2L_VAR_NODE");

        PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getPidcA2l();
        this.effectiveName = getEffectiveName(1l, getA2lName(pidcA2l));
      } // For Level 3 node - Responsibilities in A2L Structure
      else if (PIDC_TREE_NODE_TYPE.PIDC_A2L_RESPONSIBILITY_NODE.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals =
            wpArchivalServiceClient.getFilteredBaselinesForPidc(null, this.pidcTreeNode.getPidcA2l().getId(), varId,
                getA2lResponsibility().getId(), null, "PIDC_A2L_RESPONSIBILITY_NODE");

        PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getPidcA2l();
        this.effectiveName = getEffectiveName(3l, getA2lName(pidcA2l));
      }
      /* Below logic is for WP inder workpackages under review results */
      else if (PIDC_TREE_NODE_TYPE.REV_RES_WP_GRP_NODE.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(null, null, varId, null,
            this.pidcTreeNode.getName(), "REV_RES_WP_GRP_NODE");

        PidcA2l pidcA2l = this.pidcTreeNode.getPidcA2l();
        this.effectiveName = getEffectiveName(3l, getA2lName(pidcA2l));
      } // For Level 4 for wp name in A2L structure
      else if (PIDC_TREE_NODE_TYPE.PIDC_A2L_WP_NODE.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals =
            wpArchivalServiceClient.getFilteredBaselinesForPidc(null, this.pidcTreeNode.getPidcA2l().getId(), varId,
                getA2lResponsibility().getId(), getA2lWorkpackage().getName(), "PIDC_A2L_WP_NODE");

        PidcA2l pidcA2l = this.pidcTreeNode.getParentNode().getParentNode().getParentNode().getPidcA2l();
        this.effectiveName = getEffectiveName(4l, getA2lName(pidcA2l));
      }
      /* This case used when A2L node is clicked */
      else if (PIDC_TREE_NODE_TYPE.PIDC_A2L.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals =
            wpArchivalServiceClient.getFilteredBaselinesForPidc(this.pidcTreeNode.getPidcVersion().getId(),
                this.pidcTreeNode.getPidcA2l().getId(), null, null, null, "PIDC_A2L");

        PidcA2l pidcA2l = this.pidcTreeNode.getPidcA2l();
        this.effectiveName = getEffectiveName(0l, getA2lName(pidcA2l));
      }
      /* This case used when PIDC node is clicked */
      else if (PIDC_TREE_NODE_TYPE.ACTIVE_PIDC_VERSION.equals(this.pidcTreeNode.getNodeType())) {
        this.wpArchivals = wpArchivalServiceClient.getFilteredBaselinesForPidc(
            this.pidcTreeNode.getPidcVersion().getId(), null, null, null, null, "ACTIVE_PIDC_VERSION");

        PidcA2l pidcA2l = this.pidcTreeNode.getPidcA2l();
        this.effectiveName = getEffectiveName(0l, getA2lName(pidcA2l));
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
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
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    registerCnsChecker(MODEL_TYPE.WP_ARCHIVAL, chData -> {
      WpArchival wparchival = ((WpArchival) CnsUtils.getModel(chData));

      return CommonUtils.isEqual(wparchival.getVariantId(), this.pidcVariant.getId()) &&
          CommonUtils.isEqual(wparchival.getWpId(), this.a2lWorkpackage.getId()) &&
          CommonUtils.isEqual(wparchival.getPidcA2lId(), this.pidcTreeNode.getPidcA2l().getId()) &&
          CommonUtils.isEqual(wparchival.getRespId(), this.a2lResponsibility.getId());
    });

    registerCnsAction(this::refreshReviewListForRemoteChangesforWPArchival, MODEL_TYPE.WP_ARCHIVAL);
  }


  private void refreshReviewListForRemoteChangesforWPArchival(final Map<Long, ChangeDataInfo> chDataInfoMap) {

    Set<Long> wpArchivalIdSet = new HashSet<>();
    for (ChangeDataInfo changeData : chDataInfoMap.values()) {
      if ((changeData.getChangeType() == CHANGE_OPERATION.CREATE) ||
          (changeData.getChangeType() == CHANGE_OPERATION.UPDATE)) {
        wpArchivalIdSet.add(changeData.getObjId());
      }
    }
    addToWPArchivalNatInputs(wpArchivalIdSet);
  }

  /**
   * @param wpArchivalIdSet
   */
  private void addToWPArchivalNatInputs(final Set<Long> wpArchivalIdSet) {
    if (CommonUtils.isNotEmpty(wpArchivalIdSet)) {
      Set<WpArchival> wpArchivalSet = new HashSet<>();

      try {
        wpArchivalSet.addAll(new WpArchivalServiceClient().getWpArchivalsMap(wpArchivalIdSet).values());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }

      wpArchivalSet.forEach(wpArchival -> {
        if (this.wpArchivalsNatInputs.contains(wpArchival)) {
          this.wpArchivalsNatInputs.remove(wpArchival);
        }
        this.wpArchivalsNatInputs.add(wpArchival);
      });
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
   * @return the pidcRvwsNatInputs
   */
  public SortedSet<WpArchival> getWPArchivalsNatInputs() {
    if (CommonUtils.isNullOrEmpty(this.wpArchivalsNatInputs)) {
      this.wpArchivalsNatInputs.addAll(this.wpArchivals);
    }
    return this.wpArchivalsNatInputs;
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
   * @param wpArchivalsNatInputs the pidcRvwsNatInputs to set
   */
  public void setPidcRvwsNatInputs(final SortedSet<WpArchival> wpArchivalsNatInputs) {
    this.wpArchivalsNatInputs = wpArchivalsNatInputs;
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


  /**
   * @return the wpArchivals
   */
  public Set<WpArchival> getWpArchivals() {
    return this.wpArchivals;
  }


  /**
   * @param wpArchivals the wpArchivals to set
   */
  public void setWpArchivals(final Set<WpArchival> wpArchivals) {
    this.wpArchivals = wpArchivals;
  }

}
