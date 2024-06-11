/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.Set;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import com.bosch.caltool.cdr.ui.actions.ReviewResultLockUnLockAction;
import com.bosch.caltool.cdr.ui.util.CdrUIConstants;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.QS_STATUS_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespStatusData;
import com.bosch.rcputils.griddata.GridDataUtil;


/**
 * ICDM-995
 *
 * @author bru2cob
 */
public class ReviewCompleteDialog extends Dialog {


  /**
   * Shell size split
   */
  private static final int SHELL_SIZE_SPLIT = 2;
  /**
   * Shell width
   */
  private static final int SHELL_WIDTH = 700;
  /**
   * static variable for Review Completion.
   */
  private static final String REVIEW_COMPLETE_STR = "The Review has been finished! Do you want to lock the review now?";

  /**
   * shell height
   */
  private static final int SHELL_HEIGHT = 150;
  /**
   * no of columns
   */
  private static final int NO_OF_COLS = 2;
  /**
   * Top composite instance
   */
  private Composite top;
  /**
   * composite instance
   */
  private Composite composite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private final CDRReviewResult cdrResult;
  private final ReviewResultClientBO resultData;

  /**
   * @param parentShell shell
   * @param resultData cdrResult
   */
  public ReviewCompleteDialog(final Shell parentShell, final ReviewResultClientBO resultData) {
    super(parentShell);
    this.resultData = resultData;
    this.cdrResult = resultData.getResultBo().getCDRResult();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Review Complete");

    // set the shell size
    int frameX = newShell.getSize().x - newShell.getClientArea().width;
    int frameY = newShell.getSize().y - newShell.getClientArea().height;
    newShell.setSize(SHELL_WIDTH + frameX, SHELL_HEIGHT + frameY);

    // place the dialog in center of the screen
    centerShellPosition(newShell);
    super.configureShell(newShell);
  }


  /**
   * Places the dialog at the center of the screen
   *
   * @param newShell shell to be positioned
   */
  private void centerShellPosition(final Shell newShell) {
    Monitor primary = newShell.getMonitor();
    Rectangle bounds = primary.getBounds();
    Rectangle rect = newShell.getBounds();
    // calculate the position of the dialog
    int xpoint = bounds.x + ((bounds.width - rect.width) / SHELL_SIZE_SPLIT);
    int ypoint = bounds.y + ((bounds.height - rect.height) / SHELL_SIZE_SPLIT);
    // set the location of the dialog
    newShell.setLocation(xpoint, ypoint);
  }

  /**
   * This method initializes formToolkit
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }


  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = true;
    this.top.setLayoutData(gridData);
    createComposite();
    return this.top;
  }

  /**
   * create composite
   */
  private void createComposite() {

    this.composite = getFormToolkit().createComposite(this.top);
    this.composite.setLayout(new GridLayout());
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    this.composite.setLayoutData(gridData);
    createForm();
  }

  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // Only ok button is added to the dialog
    createButton(parent, IDialogConstants.OK_ID, ApicConstants.USED_YES_DISPLAY, true);
    createButton(parent, IDialogConstants.CANCEL_ID, ApicConstants.USED_NO_DISPLAY, false);
  }


  /**
   * create form
   */
  private void createForm() {
    Form form = getFormToolkit().createForm(this.composite);
    form.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    final GridData gridData = GridDataUtil.getInstance().getTextGridData();
    Label imageLabel = getFormToolkit().createLabel(form.getBody(), "");
    imageLabel.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.GREEN_SMILEY_16X16));
    imageLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    imageLabel.setLayoutData(new GridData());

    // Label to display the text to user if they want to lock the result since review is complete
    Label reviewCompLabel = getFormToolkit().createLabel(form.getBody(), REVIEW_COMPLETE_STR);
    reviewCompLabel.setLayoutData(gridData);
    reviewCompLabel.setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NO_OF_COLS;
    form.getBody().setLayout(gridLayout);
    form.getBody().setLayoutData(gridData);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {

    ReviewResultLockUnLockAction lockAction = new ReviewResultLockUnLockAction(null, null, this.resultData);
    ReviewResultBO resulDataBo = this.resultData.getResultBo();
    Set<QnaireRespStatusData> qnaireDataForRvwSet = this.resultData.getResponse().getQnaireDataForRvwSet();

    if (CommonUtils.isEqual(openSimpGnrlQnaire(), 0)) {
      // condition to open qnaire response list dialog
      boolean isQuesNotFilled = areQnairesNotFilled(qnaireDataForRvwSet) && resulDataBo.isAtleastOneParamChecked();
      // Condition to skip qnaire check for Test review and allow check for other reviews
      if (isQuesNotFilled &&
          !resulDataBo.getReviewTypeStr().equalsIgnoreCase(CDRConstants.REVIEW_TYPE.TEST.getUiType())) {
        lockAction.openQnaireRespListDialog(
            lockAction.isDivIdApplicable() ? this.resultData.getResultBo().checkOfficialOrStartReview()
                : CdrUIConstants.WARN_MSG_ABOUT_UNFILLED_QNAIRE_RESP);
      }
      // condition to lock the review result
      if (isQuesNotFilled && lockAction.isDivIdConfigured()) {
        super.okPressed();
        return;
      }
      lockAction.updateReviewResult(ApicConstants.CODE_YES);
    }

    super.okPressed();
  }

  /**
   * @param cdrReviewResult
   * @return
   */
  private int openSimpGnrlQnaire() {

    ReviewResultBO resultBo = this.resultData.getResultBo();
    CDRReviewResult cdrResult = resultBo.getCDRResult();
    // Simplified Qnaire only available for Official Review
    if (this.resultData.isSimpQuesEnab() &&
        CommonUtils.isEqual(cdrResult.getReviewType(), CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType()) &&
        resultBo.isAtleastOneParamChecked()) {
      SimplifiedGeneralQnaireDialog simpGnrlQnaireDialog =
          new SimplifiedGeneralQnaireDialog(Display.getCurrent().getActiveShell(), cdrResult);

      return simpGnrlQnaireDialog.open();
    }

    return 0;
  }

  /**
   * @param qnaireDataForRvwSet
   * @return
   */
  private boolean areQnairesNotFilled(final Set<QnaireRespStatusData> qnaireDataForRvwSet) {
    boolean quesNotFilled = false;
    for (QnaireRespStatusData qnaireData : qnaireDataForRvwSet) {
      QS_STATUS_TYPE typeByDbCode = CDRConstants.QS_STATUS_TYPE.getTypeByDbCode(qnaireData.getStatus());
      if (typeByDbCode == QS_STATUS_TYPE.NOT_ANSWERED) {
        quesNotFilled = true;
        break;
      }
    }
    return quesNotFilled;
  }

}
