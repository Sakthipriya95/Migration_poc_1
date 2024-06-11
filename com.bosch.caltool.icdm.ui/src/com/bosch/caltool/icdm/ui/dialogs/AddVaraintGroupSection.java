/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.dialogs;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.text.TextBoxContentDisplay;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * Creates pidc version details section in PIDCAttrValueEditDialog and AddValueDialog in case of creating a new PIDC
 *
 * @author dmo5cob
 */
public class AddVaraintGroupSection {

  /**
   * Min width text area
   */
  private static final int MIN_WIDTH_TXT_AREA = 160;
  /**
   * height hint text area
   */
  private static final int HEIGHT_HINT_TXT_AREA = 35;
  /**
   * vertical span text area
   */
  private static final int VERTICAL_SPAN_TXT_AREA = 2;
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


  /**
   * the maximum length for the english & german description
   */
  private static final int MAX_LENGTH_DESC_ENG = 200;

  private String changedName;

  private String changedDesc;


  /**
   * @param composite Composite
   * @param formToolKit FormToolkit
   * @param isEdit update or not
   */
  public AddVaraintGroupSection(final Composite composite, final FormToolkit formToolKit) {
    this.composite = composite;
    this.formToolKit = formToolKit;


  }


  /**
   * This method initializes section
   */
  public void createVarGroupSection() {

    this.sectionProjVrsn = SectionUtil.getInstance().createSection(this.composite, this.formToolKit,
        GridDataUtil.getInstance().getGridData(), "Variant Group details");
    this.sectionProjVrsn.getDescriptionControl().setEnabled(false);

    createVarGroupForm();
    this.sectionProjVrsn.setClient(this.formProjVrsn);
  }

  /**
   * create the form of version details
   */
  private void createVarGroupForm() {
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = NMBR_COLS_SECTION;
    this.formProjVrsn = this.formToolKit.createForm(this.sectionProjVrsn);
    this.formProjVrsn.getBody().setLayout(gridLayout);

    createNameField();
    createDescField();

  }

  /**
   *
   */
  private void createNameField() {
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "Name:");
    this.nameTxt = this.formToolKit.createText(this.formProjVrsn.getBody(), null, SWT.SINGLE | SWT.BORDER);
    GridData txtGrid = GridDataUtil.getInstance().createGridData();
    this.nameTxt.setLayoutData(txtGrid);
    this.nameTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc} modify text on versionNameTxt
       */
      @Override
      public void modifyText(final ModifyEvent evnt) {
        Validator.getInstance().validateNDecorate(AddVaraintGroupSection.this.txtVrsnNameDec,
            AddVaraintGroupSection.this.nameTxt, true, false);
        checkSaveBtnEnable();
        AddVaraintGroupSection.this.changedName = AddVaraintGroupSection.this.nameTxt.getText();

      }
    });
    this.txtVrsnNameDec = new ControlDecoration(this.nameTxt, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtVrsnNameDec, "This field is mandatory.");
  }

  /**
   * Version Desc english
   */
  private void createDescField() {
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "Description(English):");
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.formProjVrsn.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH_DESC_ENG, getTextAreaGridData());
    this.descTxt = boxContentDisplay.getText();
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "");
    this.descTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc} modify text on versionDescEngTxt
       */
      @Override
      public void modifyText(final ModifyEvent evnt) {
        if (null != AddVaraintGroupSection.this.okBtn) {
          AddVaraintGroupSection.this.okBtn.setEnabled(true);
          AddVaraintGroupSection.this.changedDesc = AddVaraintGroupSection.this.descTxt.getText();
        }
      }
    });
  }


  public void setNameDesc(final A2lVariantGroup a2lVarGrp) {

    this.nameTxt.setText(a2lVarGrp.getName());
    if (a2lVarGrp.getDescription() != null) {
      this.descTxt.setText(a2lVarGrp.getDescription());
    }
  }


  /**
   * @return GridData
   */
  private GridData getTextAreaGridData() {
    GridData gridData2 = new GridData();
    gridData2.grabExcessHorizontalSpace = true;
    gridData2.horizontalAlignment = GridData.FILL;
    gridData2.verticalAlignment = GridData.FILL;
    gridData2.verticalSpan = VERTICAL_SPAN_TXT_AREA;
    gridData2.heightHint = HEIGHT_HINT_TXT_AREA;
    gridData2.minimumWidth = MIN_WIDTH_TXT_AREA;
    gridData2.grabExcessVerticalSpace = true;
    return gridData2;
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
   * Validates save button enable or disable
   */
  private void checkSaveBtnEnable() {
    // check is all mandatory fields are filled
    if (validateFields() && (this.okBtn != null)) {
      this.okBtn.setEnabled(true);
    }
    else if (this.okBtn != null) {
      this.okBtn.setEnabled(false);
    }
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


  /**
   * @return the changedName
   */
  public String getChangedName() {
    return this.changedName;
  }


  /**
   * @param changedName the changedName to set
   */
  public void setChangedName(final String changedName) {
    this.changedName = changedName;
  }


  /**
   * @return the changedDesc
   */
  public String getChangedDesc() {
    return this.changedDesc;
  }


  /**
   * @param changedDesc the changedDesc to set
   */
  public void setChangedDesc(final String changedDesc) {
    this.changedDesc = changedDesc;
  }


}
