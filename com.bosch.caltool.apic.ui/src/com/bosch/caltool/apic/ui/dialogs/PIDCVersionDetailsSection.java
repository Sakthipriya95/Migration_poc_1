/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

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
public class PIDCVersionDetailsSection {

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
  private Text versionNameTxt;
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
  private Text versionDescEngTxt;
  /**
   * Version desc german text area
   */
  private Text versionDescGerTxt;
  /**
   * edit or not
   */
  private final boolean isedit;

  /**
   * value dialog instance
   */
  private final ValueDialog valueDialog;

  /**
   * the maximum length for the english & german description
   */
  private static final int MAX_LENGTH_DESC_ENG = 200;


  /**
   * @param composite Composite
   * @param formToolKit FormToolkit
   * @param isEdit update or not
   * @param valueDialog
   */
  public PIDCVersionDetailsSection(final Composite composite, final FormToolkit formToolKit, final boolean isEdit,
      final ValueDialog valueDialog) {
    this.composite = composite;
    this.formToolKit = formToolKit;
    this.isedit = isEdit;
    this.valueDialog = valueDialog;
  }

  /**
   * @return ValueDialog
   */
  public ValueDialog getValueDialog() {
    return this.valueDialog;
  }

  /**
   * This method initializes section
   */
  public void createSectionPidcVersion() {

    this.sectionProjVrsn = SectionUtil.getInstance().createSection(this.composite, this.formToolKit,
        GridDataUtil.getInstance().getGridData(), "Version details");
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
    this.formProjVrsn = this.formToolKit.createForm(this.sectionProjVrsn);
    this.formProjVrsn.getBody().setLayout(gridLayout);

    createVrsnNameField();
    createVrsnDescEngField();
    createVrsnDescGerField();
  }

  /**
   *
   */
  private void createVrsnNameField() {
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "Version Name:");
    this.versionNameTxt = this.formToolKit.createText(this.formProjVrsn.getBody(), null, SWT.SINGLE | SWT.BORDER);
    GridData txtGrid = GridDataUtil.getInstance().createGridData();
    this.versionNameTxt.setLayoutData(txtGrid);
    this.versionNameTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc} modify text on versionNameTxt
       */
      @Override
      public void modifyText(final ModifyEvent evnt) {
        Validator.getInstance().validateNDecorate(PIDCVersionDetailsSection.this.txtVrsnNameDec,
            PIDCVersionDetailsSection.this.versionNameTxt, true, false);
        if (PIDCVersionDetailsSection.this.getValueDialog() == null) {
          checkSaveBtnEnable();
        }
        else {
          PIDCVersionDetailsSection.this.getValueDialog().checkSaveBtnEnable();
        }
      }
    });
    this.txtVrsnNameDec = new ControlDecoration(this.versionNameTxt, SWT.LEFT | SWT.TOP);
    this.decorators.showReqdDecoration(this.txtVrsnNameDec, "This field is mandatory.");
  }

  /**
   * Version Desc english
   */
  private void createVrsnDescEngField() {
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "Description(English):");
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.formProjVrsn.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH_DESC_ENG, getTextAreaGridData());
    this.versionDescEngTxt = boxContentDisplay.getText();
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "");
    this.versionDescEngTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc} modify text on versionDescEngTxt
       */
      @Override
      public void modifyText(final ModifyEvent evnt) {
        if (PIDCVersionDetailsSection.this.isedit && (null != PIDCVersionDetailsSection.this.okBtn)) {
          PIDCVersionDetailsSection.this.okBtn.setEnabled(true);
        }
      }
    });
  }

  /**
   * Version Desc German
   */
  private void createVrsnDescGerField() {
    this.formToolKit.createLabel(this.formProjVrsn.getBody(), "Description(German):");
    TextBoxContentDisplay boxContentDisplay = new TextBoxContentDisplay(this.formProjVrsn.getBody(),
        SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL, MAX_LENGTH_DESC_ENG, getTextAreaGridData());
    this.versionDescGerTxt = boxContentDisplay.getText();
    this.versionDescGerTxt.addModifyListener(new ModifyListener() {

      /**
       * {@inheritDoc} modify text on versionDescGerTxt
       */
      @Override
      public void modifyText(final ModifyEvent evnt) {
        if (PIDCVersionDetailsSection.this.isedit && (null != PIDCVersionDetailsSection.this.okBtn)) {
          PIDCVersionDetailsSection.this.okBtn.setEnabled(true);
        }
      }
    });
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
    final String vrsntNameCheck = this.versionNameTxt.getText();

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
    return this.versionNameTxt;
  }


  /**
   * @return the versionDescEngTxt
   */
  public Text getVersionDescEngTxt() {
    return this.versionDescEngTxt;
  }


  /**
   * @return the versionDescGerTxt
   */
  public Text getVersionDescGerTxt() {
    return this.versionDescGerTxt;
  }

  /**
   * @return the txt VrsnNameDec
   */
  public ControlDecoration getTxtVrsnNameDec() {
    return this.txtVrsnNameDec;
  }


}
