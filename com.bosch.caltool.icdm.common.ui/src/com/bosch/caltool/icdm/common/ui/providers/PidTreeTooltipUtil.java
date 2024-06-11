/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.providers;

import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PidTreeTooltipUtil {

  /**
  *
  */
  private static final String VERSION_NAME = "Version Name";
  /**
  *
  */
  private static final String SPACE_STRING = " ";
  /**
  *
  */
  private static final String PIDC = "PIDC";
  /**
  *
  */
  private static final String VALUE_DESCRIPTION = "Value Description";
  /**
  *
  */
  private static final String VALUE = "Value";
  /**
  *
  */
  private static final String NAME = "Name";
  /**
  *
  */
  private static final String DESCRIPTION = "Description";
  /**
  *
  */
  private static final String ATTRIBUTE = "Attribute";
  /**
  *
  */
  private static final String SEPARATOR = ":";
  /**
  *
  */
  private static final String NEW_LINE = "\n";

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getValDetailsTooltip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(NEW_LINE);
    toolTipText.append(VALUE);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getNodeAttrValue().getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append(VALUE_DESCRIPTION);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getNodeAttrValue().getDescription());
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getPidcVerToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(PIDC);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidc().getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append(PIDC + SPACE_STRING + DESCRIPTION);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidc().getDescription());
    toolTipText.append(NEW_LINE);
    toolTipText.append(VERSION_NAME);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidcVersion().getVersionName());
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getAttrDetailsTooltip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(ATTRIBUTE);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getNodeAttr().getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append(DESCRIPTION);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getNodeAttr().getDescription());
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getSdomPverToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append("PVER Name");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getSdomPver().getPverName());
    toolTipText.append(NEW_LINE);
    toolTipText.append(DESCRIPTION);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getSdomPver().getDescription());
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getPidcA2lToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    try {
      if (treeNode.getPidcA2l() != null) {
        if (treeNode.getPidcA2l().isWorkingSetModified() && treeNode.getPidcA2l().isActiveWpParamPresentFlag()) {
          toolTipText.append((new CommonDataBO()).getMessage("A2L", "PIDCA2L_MODIFIED_TOOL_TIP"));
        }
        if (!treeNode.getPidcA2l().isActiveWpParamPresentFlag()) {
          toolTipText.append((new CommonDataBO()).getMessage("A2L", "NO_WP_DEF_VERSION"));
        }
        if (!treeNode.getPidcA2l().isActive()) {
          toolTipText.append("\nStatus : Inactive ");
        }

      }

    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    toolTipText.append(NEW_LINE);
    toolTipText.append(NAME);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidcA2l().getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append("Compliance Parameter(s)");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText
        .append(null != treeNode.getPidcA2l().getCompliParamCount() ? treeNode.getPidcA2l().getCompliParamCount() : 0);
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getRevResToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(NAME);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getReviewResult().getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append(DESCRIPTION);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getReviewResult().getDescription());
    toolTipText.append(NEW_LINE);
    toolTipText.append("Review Type");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(CDRConstants.REVIEW_TYPE.getType(treeNode.getReviewResult().getReviewType()).getUiType());
    toolTipText.append(NEW_LINE);
    toolTipText.append("Status");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(CDRConstants.REVIEW_STATUS.getType(treeNode.getReviewResult().getRvwStatus()).getUiType());
    toolTipText.append(NEW_LINE);
    toolTipText.append("Lock Status");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText
        .append(null == treeNode.getReviewResult().getLockStatus() ? CDRConstants.REVIEW_LOCK_STATUS.NO.getUiType()
            : CDRConstants.REVIEW_LOCK_STATUS.getType(treeNode.getReviewResult().getLockStatus()).getUiType());
    if (null != treeNode.getReviewVarResult()) {
      toolTipText.append("\nReview Variant : ");
      toolTipText.append(treeNode.getReviewVarResult().getPrimaryVariantName());
      toolTipText.append("\nMapped Variant : ");
      toolTipText.append(treeNode.getReviewVarResult().getName());
    }
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getQnaireVarToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(NAME);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidcVariant().getName());
    if (null != treeNode.getPidcVariant().getDescription()) {
      toolTipText.append(NEW_LINE);
      toolTipText.append(DESCRIPTION);
      toolTipText.append(SPACE_STRING);
      toolTipText.append(SEPARATOR);
      toolTipText.append(SPACE_STRING);
      toolTipText.append(treeNode.getPidcVariant().getDescription());
    }
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getQnaireRespToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(NAME);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append("PIDC Version");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidcVersion().getName());
    toolTipText.append(NEW_LINE);
    toolTipText.append("Variant");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidcVariant().getName());
    if (null != treeNode.getPidcVariant().getDescription()) {
      toolTipText.append(NEW_LINE);
      toolTipText.append(DESCRIPTION);
      toolTipText.append(SPACE_STRING);
      toolTipText.append(SEPARATOR);
      toolTipText.append(SPACE_STRING);
      toolTipText.append(treeNode.getPidcVariant().getDescription());
    }
    RvwQnaireRespVersion rvwQnaireRespVersion =
        treeNode.getQnaireInfo().getRvwQnaireRespVersMap().get(treeNode.getQnaireResp().getId());
    toolTipText.append(NEW_LINE);
    toolTipText.append("Status");
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(
        CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(rvwQnaireRespVersion.getQnaireRespVersStatus()).getUiType());
  }

  /**
   * @param toolTipText tool tip text
   * @param treeNode tree node
   */
  public void getCdrVarToolTip(final StringBuilder toolTipText, final PidcTreeNode treeNode) {
    toolTipText.append(NAME);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(SEPARATOR);
    toolTipText.append(SPACE_STRING);
    toolTipText.append(treeNode.getPidcVariant().getName());
    if (null != treeNode.getPidcVariant().getDescription()) {
      toolTipText.append(NEW_LINE);
      toolTipText.append(DESCRIPTION);
      toolTipText.append(SPACE_STRING);
      toolTipText.append(SEPARATOR);
      toolTipText.append(SPACE_STRING);
      toolTipText.append(treeNode.getPidcVariant().getDescription());
    }
  }

  /**
   * @param toolTipText tool tip text
   */
  public void getOtherScopeToolTip(final StringBuilder toolTipText) {
    toolTipText.append("Review scopes other than Work Packages");

  }
}
