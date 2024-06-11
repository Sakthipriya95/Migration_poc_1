/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;

import com.bosch.caltool.icdm.client.bo.fm.FocusMatrixColorCode;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;

/**
 * @author mkl2cob
 */
public class ColorSelectionDialogBtnGroup {


  /**
   * green button
   */
  private Button greenBtn;
  /**
   * yellow button
   */
  private Button yellowBtn;

  /**
   * red button
   */
  private Button redBtn;

  /**
   * grey button
   */
  private Button whiteBtn;

  /**
   * orange button
   */
  private Button orngBtn;

  /**
   * diff value button
   */
  private Button diffValBtn;

  /**
   * old color code string
   */
  public FocusMatrixColorCode oldColorCode;

  /**
   * new color code str
   */
  public FocusMatrixColorCode newColorCode;

  /**
   * boolean to indicate diff value radio button is selected
   */
  private boolean diffValBtnSelcted;


  /**
   * ColorCodeSelectionDialog instance
   */
  private final AbstractFMEditComponent colorCodeSelectionDialog;

  /**
   * @param colorCodeSelectionDialog ColorCodeSelectionDialog
   */
  public ColorSelectionDialogBtnGroup(final AbstractFMEditComponent colorCodeSelectionDialog) {
    this.colorCodeSelectionDialog = colorCodeSelectionDialog;
  }

  /**
   * set background color for buttons
   */
  public void setBackgroundColorForButtons() {
    // setting background colors for buttons
    this.greenBtn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GREEN));
    this.yellowBtn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_YELLOW));
    this.redBtn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    this.whiteBtn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    // RGB color code for orange color
    this.orngBtn.setBackground(GUIHelper.getColor(255, 153, 0));
  }

  /**
   * @param grp Group
   */
  public void createRadioButtons(final Group grp) {


    // create radio buttons
    createRedButton(grp);

    createOrangeButton(grp);

    createYellowButton(grp);

    createGreenButton(grp);

    createWhiteButton(grp);

    final GridData remarksGridLayout = new GridData();
    remarksGridLayout.horizontalAlignment = GridData.FILL;
    remarksGridLayout.grabExcessHorizontalSpace = true;
    grp.setLayoutData(remarksGridLayout);

  }

  /**
   * @param grp
   */
  private void createWhiteButton(final Group grp) {
    // add grey radio button
    this.whiteBtn = this.colorCodeSelectionDialog.getFormToolkit().createButton(grp, "  ", SWT.RADIO);
    this.whiteBtn.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
    this.whiteBtn.setToolTipText("No connection");
    this.whiteBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ColorSelectionDialogBtnGroup.this.newColorCode = FocusMatrixColorCode.NOT_DEFINED;
        ColorSelectionDialogBtnGroup.this.colorCodeSelectionDialog.validateRemarks();
        setDiffValBtnSelcted(false);
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   * @param grp
   */
  private void createGreenButton(final Group grp) {
    // add green radio button
    this.greenBtn = this.colorCodeSelectionDialog.getFormToolkit().createButton(grp, "  ", SWT.RADIO);
    this.greenBtn.setBackground(CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.GREEN));
    this.greenBtn.setToolTipText("Cause effect relationships known , working range observed  \n -> no risk ");
    this.greenBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ColorSelectionDialogBtnGroup.this.newColorCode = FocusMatrixColorCode.GREEN;
        ColorSelectionDialogBtnGroup.this.colorCodeSelectionDialog.validateRemarks();
        setDiffValBtnSelcted(false);
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   * @param grp
   */
  private void createYellowButton(final Group grp) {
    // add yellow radio button
    this.yellowBtn = this.colorCodeSelectionDialog.getFormToolkit().createButton(grp, "  ", SWT.RADIO);
    this.yellowBtn.setBackground(CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.YELLOW));
    this.yellowBtn
        .setToolTipText("Cause effect relationships known , working range expanded, not confirmed \n -> low risk ");
    this.yellowBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ColorSelectionDialogBtnGroup.this.newColorCode = FocusMatrixColorCode.YELLOW;
        ColorSelectionDialogBtnGroup.this.colorCodeSelectionDialog.validateRemarks();
        setDiffValBtnSelcted(false);
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   * @param grp
   */
  private void createOrangeButton(final Group grp) {
    // add orange radio button
    this.orngBtn = this.colorCodeSelectionDialog.getFormToolkit().createButton(grp, "  ", SWT.RADIO);
    this.orngBtn.setBackground(CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.ORANGE));
    this.orngBtn.setToolTipText(
        "Cause effect relationships and work range not entirely known , basic design can be further used \n -> high risk ");
    this.orngBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ColorSelectionDialogBtnGroup.this.newColorCode = FocusMatrixColorCode.ORANGE;
        ColorSelectionDialogBtnGroup.this.colorCodeSelectionDialog.validateRemarks();
        setDiffValBtnSelcted(false);
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   * @param grp
   */
  private void createRedButton(final Group grp) {
    // add red radio button
    this.redBtn = this.colorCodeSelectionDialog.getFormToolkit().createButton(grp, "  ", SWT.RADIO);
    this.redBtn.setBackground(CommonUiUtils.getFocusMatrixColor(FocusMatrixColorCode.RED));
    this.redBtn.setToolTipText(
        "New development, cause effect relationships and working range unknown  \n -> Risk cannot be evaluated ");
    this.redBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        ColorSelectionDialogBtnGroup.this.newColorCode = FocusMatrixColorCode.RED;
        ColorSelectionDialogBtnGroup.this.colorCodeSelectionDialog.validateRemarks();
        setDiffValBtnSelcted(false);
      }


      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // TODO Auto-generated method stub

      }
    });
  }

  /**
   * @return the diffValBtnSelcted
   */
  public boolean isDiffValBtnSelcted() {
    return this.diffValBtnSelcted;
  }

  /**
   * @param diffValBtnSelcted the diffValBtnSelcted to set
   */
  public void setDiffValBtnSelcted(final boolean diffValBtnSelcted) {
    this.diffValBtnSelcted = diffValBtnSelcted;
  }


  /**
   * @return the greenBtn
   */
  public Button getGreenBtn() {
    return this.greenBtn;
  }


  /**
   * @return the yellowBtn
   */
  public Button getYellowBtn() {
    return this.yellowBtn;
  }


  /**
   * @return the redBtn
   */
  public Button getRedBtn() {
    return this.redBtn;
  }


  /**
   * @return the whiteBtn
   */
  public Button getWhiteBtn() {
    return this.whiteBtn;
  }


  /**
   * @return the orngBtn
   */
  public Button getOrngBtn() {
    return this.orngBtn;
  }


  /**
   * @return the diffValBtn
   */
  public Button getDiffValBtn() {
    return this.diffValBtn;
  }


  /**
   * @param diffValBtn the diffValBtn to set
   */
  public void setDiffValBtn(final Button diffValBtn) {
    this.diffValBtn = diffValBtn;
  }

  /**
   * this method clears all the button values
   */
  public void clearAllButtons() {
    this.redBtn.setSelection(false);
    this.orngBtn.setSelection(false);
    this.yellowBtn.setSelection(false);
    this.greenBtn.setSelection(false);
    this.whiteBtn.setSelection(false);
    if ((null != this.diffValBtn) && !this.diffValBtn.isDisposed()) {
      this.diffValBtn.setSelection(false);
    }
  }


}