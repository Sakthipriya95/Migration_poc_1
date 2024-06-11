/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * Creates pidc version details section in PIDCAttrValueEditDialog and AddValueDialog in case of creating a new PIDC
 *
 * @author dmo5cob
 */
public class WorkingSetSelectionSection {

  /**
   * Number of columns in the section
   */
  private static final int NMBR_COLS_SECTION = 2;
  /**
   * Section version details
   */
  private Section sectionProjVrsn;
  /**
   * Composite version details
   */
  private final Composite composite;
  /**
   * Form version details
   */
  private Form formProjVrsn;
  /**
   * Version name Text
   */
  private Text nameTxt;
  /**
   * ControlDecoration for version name
   */
  private ControlDecoration txtVrsnNameDec;

  /**
   * Decorators instance
   */
  private final Decorators decorators = new Decorators();
  /**
   * FormToolkit instance
   */
  private final FormToolkit formToolKit;
  /**
   * Ok button
   */
  private Button okBtn;
  /**
   * Version desc english text area
   */
  private Text descTxt;
  private Combo workingSeCombo;
  private Button editVersionButton;


  /**
   * @param composite Composite
   * @param formToolKit FormToolkit
   * @param isEdit update or not
   */
  public WorkingSetSelectionSection(final Composite composite, final FormToolkit formToolKit) {
    this.composite = composite;
    this.formToolKit = formToolKit;


  }


  /**
   * This method initializes section
   */
  public void createSectionPidcVersion() {

    this.sectionProjVrsn = SectionUtil.getInstance().createSection(this.composite, this.formToolKit,
        GridDataUtil.getInstance().getGridData(), "Work Package version");
    this.sectionProjVrsn.getDescriptionControl().setEnabled(false);

    createFormPidcVersion();
    this.sectionProjVrsn.setClient(this.formProjVrsn);
  }

  /**
   * create the form of version details
   */
  private void createFormPidcVersion() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NMBR_COLS_SECTION;


    GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;
    this.sectionProjVrsn.setLayoutData(gridData1);
    this.formProjVrsn = this.formToolKit.createForm(this.sectionProjVrsn);

    gridLayout.numColumns = 2;
    this.workingSeCombo = new Combo(this.formProjVrsn.getBody(), SWT.READ_ONLY);
    this.workingSeCombo.add("Working Set", 0);
    this.workingSeCombo.select(0);
    this.editVersionButton = new Button(this.formProjVrsn.getBody(), SWT.PUSH);
    this.editVersionButton.setText("Edit Versions");
    this.formProjVrsn.getBody().setLayout(gridLayout);
    new Label(this.formProjVrsn.getBody(), SWT.NONE);


  }

  /**
   * This method validates the mandatory fields
   *
   * @return boolean
   */
  public boolean validateFields() {
    boolean result = false;
    final String vrsntNameCheck = this.nameTxt.getText();

    if (!CommonUtils.isEmptyString(vrsntNameCheck.trim())) {
      result = true;
    }
    return result;
  }

  /**
   * @return the okBtn
   */
  public Button getOkBtn() {
    return this.okBtn;
  }


  /**
   * @param okBtn the okBtn to set
   */
  public void setOkBtn(final Button okBtn) {
    this.okBtn = okBtn;
  }


  /**
   * @return the versionNameTxt
   */
  public Text getVersionNameTxt() {
    return this.nameTxt;
  }


  /**
   * @return the versionDescEngTxt
   */
  public Text getVersionDescEngTxt() {
    return this.descTxt;
  }


  /**
   * @return the txt VrsnNameDec
   */
  public ControlDecoration getTxtVrsnNameDec() {
    return this.txtVrsnNameDec;
  }


}
