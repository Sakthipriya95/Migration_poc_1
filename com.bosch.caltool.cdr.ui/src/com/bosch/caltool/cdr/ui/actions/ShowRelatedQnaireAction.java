/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.cdr.ui.dialogs.QnaireRespListDialog;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.bo.a2l.ParamWpResponsibility;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireRespVersion;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireResponse;

/**
 * @author hnu1cob
 */
public class ShowRelatedQnaireAction extends Action {


  private final IMenuManager menuManager;
  private final Long paramId;
  private final CdrReportDataHandler cdrReportHandler;
  private final Shell parentShell;
  /**
   * true, if dialog is opened from CDR report -false if dialog is opened from compare Hex
   */
  private final boolean isCdrReport;
  private final CompHexWithCDFxDataHandler compHexDataHdlr;

  /**
   * @param parentShell parent Shell
   * @param menuManager Menu manager
   * @param paramId parameter id
   * @param cdrReportHandler CDR Report Data Handler
   * @param compHexDataHdlr compHexDataHdlr
   * @param isCdrReport true, if dialog is opened from CDR report , else from compare Hex
   */
  public ShowRelatedQnaireAction(final Shell parentShell, final IMenuManager menuManager, final Long paramId,
      final CdrReportDataHandler cdrReportHandler, final CompHexWithCDFxDataHandler compHexDataHdlr,
      final boolean isCdrReport) {
    super();
    this.menuManager = menuManager;
    this.paramId = paramId;
    this.cdrReportHandler = cdrReportHandler;
    this.parentShell = parentShell;
    this.isCdrReport = isCdrReport;
    this.compHexDataHdlr = compHexDataHdlr;
    setProperties();
  }

  /**
   *
   */
  private void setProperties() {
    setText("Show related Questionnaire(s)");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.QUESTIONARE_ICON_16X16);
    setImageDescriptor(imageDesc);
    this.menuManager.add(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    showRelatedQnaire();
  }


  private void showRelatedQnaire() {
    Set<QnaireRespStatusData> statusDataSet = new HashSet<>();
    ParamWpResponsibility respObj = this.cdrReportHandler.getParamWpResp(this.paramId);
    constructQnaireRespStatusDataSet(respObj, statusDataSet);

    QnaireRespListDialog qnaireListDialog =
        new QnaireRespListDialog(this.parentShell, statusDataSet, respObj.getParamName(), this.cdrReportHandler,
            this.compHexDataHdlr, false, this.isCdrReport, !this.isCdrReport);
    qnaireListDialog.open();
  }

  private void constructQnaireRespStatusDataSet(final ParamWpResponsibility respObj,
      final Set<QnaireRespStatusData> statusDataSet) {
    if ((null != respObj) && this.cdrReportHandler.getCdrReportQnaireRespWrapper().getAllWpRespQnaireRespVersMap()
        .containsKey(respObj.getRespId())) {
      for (Entry<Long, Set<RvwQnaireRespVersion>> wpQnaireVersionsEntrySet : this.cdrReportHandler
          .getCdrReportQnaireRespWrapper().getAllWpRespQnaireRespVersMap().get(respObj.getRespId()).entrySet()) {
        if (wpQnaireVersionsEntrySet.getKey().equals(respObj.getWpId())) {
          constructQnaireRespStatusData(statusDataSet, wpQnaireVersionsEntrySet.getValue());
        }
      }
    }
  }

  /**
   * @param statusDataSet
   * @param qnaireRespVers
   * @param respId
   * @param wpId
   */
  private void constructQnaireRespStatusData(final Set<QnaireRespStatusData> statusDataSet,
      final Set<RvwQnaireRespVersion> qnaireRespVers) {
    for (RvwQnaireRespVersion qnaireRespVer : qnaireRespVers) {
      // rvwQnaireRespVersion is null for simpliiedQnaire
      if (CommonUtils.isNotNull(qnaireRespVer)) {
        RvwQnaireResponse rvwQnaireResponse =
            this.cdrReportHandler.getCdrReportQnaireRespWrapper().getQnaireResponseMap().get(qnaireRespVer.getId());
        QnaireRespStatusData statusData = new QnaireRespStatusData();
        // questionnaire response name
        statusData.setQnaireRespName(rvwQnaireResponse.getName());
        // status of questionnaire response version
        statusData.setStatus(qnaireRespVer.getQnaireRespVersStatus());
        statusData.setRevisionNum(qnaireRespVer.getRevNum());
        statusData.setVersionName(qnaireRespVer.getVersionName());
        // linked variant name
        statusData.setPrimaryVarName(rvwQnaireResponse.getPrimaryVarRespWpName());
        // Questionnaire Response id
        statusData.setQuesRespId(qnaireRespVer.getQnaireRespId());
        // Wp name
        statusData.setWpName(this.cdrReportHandler.getCdrReportQnaireRespWrapper().getWpIdAndNameMap()
            .get(rvwQnaireResponse.getA2lWpId()));
        // Responsibility name
        statusData.setRespName(this.cdrReportHandler.getCdrReportQnaireRespWrapper().getRespIdAndNameMap()
            .get(rvwQnaireResponse.getA2lRespId()));
        statusDataSet.add(statusData);
      }
    }
  }
}
