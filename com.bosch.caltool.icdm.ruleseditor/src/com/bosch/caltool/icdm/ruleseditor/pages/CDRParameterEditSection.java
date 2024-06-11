/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.ruleseditor.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;


/**
 * ICDM-1244 Section to edit parameter details like class,codeword,hint
 *
 * @author mkl2cob
 */
public class CDRParameterEditSection {

  /**
   * FormToolkit instance
   */
  private final FormToolkit formToolkit;

  /**
   * top composite from RuleInfoSection
   */
  private final Composite paramPropComp;

  /**
   * Class combo weight
   */
  private static final int CLASS_COMBO_WIDTH = 50;
  /**
   * Class combo height
   */
  private static final int CLASS_COMBO_HEIGHT = 100;

  /**
   * parameter long name text field
   */
  private StyledText longNameTxt;
  /**
   * instance of combo
   */
  private Combo comboClass;

  /**
   * code word checkbox instance
   */
  private Button codeWordChkBox;
  /**
   * bitwise rule checkbox instance
   */
  private Button bitwiseRuleChkBox;
  /**
   * bitwise rule checkbox instance
   */
  private Button blackListChkBox;


  /**
   * @return the blackListChkBox
   */
  public Button getBlackListChkBox() {
    return this.blackListChkBox;
  }

  /**
   * @return the bitwiseRuleChkBox
   */
  public Button getBitwiseRuleChkBox() {
    return this.bitwiseRuleChkBox;
  }

  /**
   * hint text area
   */
  private StyledText hintTxtArea;
  /**
   * Parameter instance
   */
  private final IParameter param;


  /**
   * RuleInfoSection instance
   */
  private final RuleInfoSection ruleInfoSection;
  /**
   * width of name text field
   */
  private static final int TEXT_FIELD_WIDTHHINT_2 = 250;

  /**
   * Param properties section cols
   */
  private static final int PARAM_PROP_COLS = 5;


  /**
   * @param paramPropComp Composite
   * @param formToolkit2 FormToolkit
   * @param ruleInfoSection RuleInfoSection
   */
  public CDRParameterEditSection(final Composite paramPropComp, final FormToolkit formToolkit,
      final RuleInfoSection ruleInfoSection) {
    this.paramPropComp = paramPropComp;
    this.formToolkit = formToolkit;
    this.ruleInfoSection = ruleInfoSection;
    this.param = ruleInfoSection.getSelectedParam();
  }

  /**
   * This method initializes Param Properties Section
   */
  public void createsectionParamProperties() {

    final Section sectionParamProp =
        this.formToolkit.createSection(this.paramPropComp, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = PARAM_PROP_COLS;
    sectionParamProp.setLayout(gridLayout1);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    sectionParamProp.setLayoutData(gridData);
    sectionParamProp.setText("Parameter Properties");
    sectionParamProp.setDescription("This section is common for all rules of the parameter");

    final Form formParamProperties = this.formToolkit.createForm(sectionParamProp);
    formParamProperties.getBody().setLayout(gridLayout1);
    formParamProperties.setLayoutData(gridData);
    // controls are added to the section
    createNameUIControls(formParamProperties);
    fillerLabels(formParamProperties.getBody(), 1);


    createLongNameUIControls(formParamProperties);
    createClassUIControls(formParamProperties);
    fillerLabels(formParamProperties.getBody(), 1);
    createHintUIControls(formParamProperties);
    createCodeWordUIControls(formParamProperties);
    fillerLabels(formParamProperties.getBody(), 2);
    createBitwiseRule(formParamProperties);
    fillerLabels(formParamProperties.getBody(), 2);
    createBlackListChkBox(formParamProperties);
    sectionParamProp.setClient(formParamProperties);
    sectionParamProp.getDescriptionControl().setEnabled(false);

  }

  /**
   * @param formParamProperties
   */
  private void createBitwiseRule(final Form form) {
    createLabelControl(form.getBody(), "Bitwise Rule");
    this.bitwiseRuleChkBox = new Button(form.getBody(), SWT.CHECK);
    if (this.param.getType().equalsIgnoreCase(ParameterType.VALUE.getText())) {
      this.bitwiseRuleChkBox.setEnabled(true);
      this.bitwiseRuleChkBox.setSelection(CommonUtils.isEqual(
          this.ruleInfoSection.getParamDataProvider().getBitWiseRule(this.param), ApicConstants.CODE_WORD_YES));
    }
    else {
      this.bitwiseRuleChkBox.setEnabled(false);
      this.bitwiseRuleChkBox.setSelection(false);
    }
    this.bitwiseRuleChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDMLogger.getInstance().infoDialog("Please update the existing rules accordingly", Activator.PLUGIN_ID);
        CDRParameterEditSection.this.ruleInfoSection.setLimitsToNonEditable();
        CDRParameterEditSection.this.ruleInfoSection.toggleComplexRulesForBitwiseParam();
        CDRParameterEditSection.this.ruleInfoSection.enableSave();
      }
    });

  }

  /**
   * @param formParamProperties
   */
  private void createBlackListChkBox(final Form form) {
    createLabelControl(form.getBody(), "Black List ");
    this.blackListChkBox = new Button(form.getBody(), SWT.CHECK);
    this.blackListChkBox.setSelection(this.param.isBlackList());
    CommonDataBO dataBo = new CommonDataBO();
    try {
      this.blackListChkBox.setToolTipText(dataBo.getMessage(CDRConstants.PARAM, CDRConstants.BLACK_LIST_TOOLTIP));
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    this.blackListChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDRParameterEditSection.this.ruleInfoSection.enableSave();
      }
    });

  }

  /**
   * @param form
   */
  private void createNameUIControls(final Form form) {
    // name field is not disabled to make copy to clipboard available
    LabelUtil.getInstance().createLabel(form.getBody(), "Name");
    StyledText nameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, false);
    nameTxt.setText(CommonUtils.isNull(this.param.getName()) ? "" : this.param.getName());
    nameTxt.setEnabled(true);
  }

  /**
   * @param form
   */
  private void createClassUIControls(final Form form) {
    createLabelControl(form.getBody(), "Class");
    this.comboClass = new Combo(form.getBody(), SWT.READ_ONLY);
    this.comboClass.setSize(CLASS_COMBO_WIDTH, CLASS_COMBO_HEIGHT);
    this.comboClass.add("");
    this.comboClass.add(ParameterClass.SCREW.getText());
    this.comboClass.add(ParameterClass.RIVET.getText());
    this.comboClass.add(ParameterClass.NAIL.getText());
    // ICDM-916 stat Rivet
    this.comboClass.add(ParameterClass.STATRIVET.getText());

    this.comboClass.select(this.ruleInfoSection.getParamDataProvider().getPclass(this.param) == null ? 0
        : this.ruleInfoSection.getParamDataProvider().getPclass(this.param).ordinal() + 1);

    // Selection listener
    this.comboClass.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        // Icdm-2583- logic for prevent setting the s-Rivet Value.
        String selItem = CDRParameterEditSection.this.comboClass
            .getItem(CDRParameterEditSection.this.comboClass.getSelectionIndex());
        if (CommonUtils.isEqual(ParameterClass.STATRIVET.getText(), selItem)) {
          CDMLogger.getInstance().errorDialog(ApicConstants.STAT_RIVET_ERR_MESS, Activator.PLUGIN_ID);
          CDRParameterEditSection.this.comboClass.select(CDRParameterEditSection.this.ruleInfoSection
              .getParamDataProvider().getPclass(CDRParameterEditSection.this.param) == null ? 0
                  : CDRParameterEditSection.this.ruleInfoSection.getParamDataProvider()
                      .getPclass(CDRParameterEditSection.this.param).ordinal() + 1);
        }
        CDRParameterEditSection.this.ruleInfoSection.enableSave();
      }
    });
  }

  /**
   * @param form
   */
  private void createLongNameUIControls(final Form form) {

    createLabelControl(form.getBody(), "Long Name");
    this.longNameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, true);
    this.longNameTxt.setText(CommonUtils.isNull(this.param.getLongName()) ? "" : this.param.getLongName());
    this.longNameTxt.setEnabled(true);
    this.longNameTxt.addModifyListener(arg0 -> CDRParameterEditSection.this.ruleInfoSection.enableSave());
  }

  /**
   * @param form
   */
  private void createCodeWordUIControls(final Form form) {
    createLabelControl(form.getBody(), "Codeword");
    this.codeWordChkBox = new Button(form.getBody(), SWT.CHECK);
    this.codeWordChkBox.setSelection(CommonUtils.isEqual(this.param.getCodeWord(), ApicConstants.CODE_YES));
    this.codeWordChkBox.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        CDRParameterEditSection.this.ruleInfoSection.enableSave();
      }
    });
  }

  /**
   * Icdm-1087
   *
   * @param scComp
   */
  private void createHintUIControls(final Form form) {

    // Icdm-621
    createLabelControl(form.getBody(), "Calibration Hint");
    // ICDM-759
    this.hintTxtArea = new StyledText(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData createGridData = new GridData();

    createGridData.grabExcessHorizontalSpace = true;
    createGridData.horizontalAlignment = GridData.FILL;
    createGridData.verticalAlignment = GridData.FILL;
    createGridData.grabExcessVerticalSpace = true;
    createGridData.verticalSpan = 3;

    this.hintTxtArea.setLayoutData(createGridData);
    this.hintTxtArea.setText(CommonUtils.isNull(this.param.getParamHint()) ? "" : this.param.getParamHint());
    this.hintTxtArea.addModifyListener(arg0 -> CDRParameterEditSection.this.ruleInfoSection.enableSave());

  }


  /**
   * This method create Label instance for statisctical values
   *
   * @param caldataComp
   * @param lblName
   */
  private void createLabelControl(final Composite composite, final String lblName) {
    LabelUtil.getInstance().createLabel(this.formToolkit, composite, lblName);
  }

  /**
   * @param comp
   * @param widthHint
   * @param isEditable
   * @return
   */
  private StyledText createStyledTextField(final Composite comp, final int widthHint, final boolean isEditable) {
    StyledText styledTxt = new StyledText(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(widthHint));
    styledTxt.setEditable(isEditable);
    return styledTxt;
  }

  /**
   * @param grp
   * @param limit
   */
  private void fillerLabels(final Composite grp, final int limit) {
    for (int i = 0; i < limit; i++) {
      new Label(grp, SWT.NONE);
    }
  }

  /**
   * @return true if the CDRFuncParameter properties are updated
   */
  public boolean isParamPropertiesUpdated() {
    boolean classFieldModified = true;
    boolean codeWordModified = true;
    boolean hintFieldModified = true;
    boolean bitWiseModified = true;
    boolean blackListModified = true;

    int ordinalOfPClass = -1;
    if (CommonUtils.isNotNull(this.ruleInfoSection.getParamDataProvider().getPclass(this.param))) {
      // getting the index for the particular class type
      ordinalOfPClass = this.ruleInfoSection.getParamDataProvider().getPclass(this.param).ordinal();
    }
    // check if class field is modified
    classFieldModified ^= (this.comboClass.getSelectionIndex() - 1) == ordinalOfPClass;
    boolean isCodeWordYes = this.param.getCodeWord().equals(ApicConstants.CODE_YES);
    // check if code word field is modified
    codeWordModified ^= this.codeWordChkBox.getSelection() == isCodeWordYes;
    // check if hint field is modified
    hintFieldModified ^=
        CommonUtils.isEqual(this.hintTxtArea.getText(), CommonUtils.checkNull(this.param.getParamHint()));
    // check if bitwise field is modified
    boolean isBitWiseYes =
        this.ruleInfoSection.getParamDataProvider().getBitWiseRule(this.param).equals(ApicConstants.CODE_WORD_YES);
    bitWiseModified ^= this.bitwiseRuleChkBox.getSelection() == isBitWiseYes;
    // check if blacklist field is modified
    blackListModified ^= this.blackListChkBox.getSelection() == this.param.isBlackList();
    // check if long name for parameter is modified
    boolean isParamLongNameModified =
        !this.longNameTxt.getText().equals(CommonUtils.checkNull(this.param.getLongName(), ApicConstants.EMPTY_STRING));
    return classFieldModified || codeWordModified || hintFieldModified || bitWiseModified || blackListModified ||
        isParamLongNameModified;
  }

  /**
   * @return the comboClass
   */
  public Combo getComboClass() {
    return this.comboClass;
  }


  /**
   * @return the codeWordChkBox
   */
  public Button getCodeWordChkBox() {
    return this.codeWordChkBox;
  }


  /**
   * @return the hintTxtArea
   */
  public StyledText getHintTxtArea() {
    return this.hintTxtArea;
  }

  /**
   * Disable all the fields when the dialog is opened in the read only mode
   */
  public void disableAllFields() {
    this.codeWordChkBox.setEnabled(false);
    this.comboClass.setEnabled(false);
    this.bitwiseRuleChkBox.setEnabled(false);
    this.blackListChkBox.setEnabled(false);
  }

  /**
   * this method enables the parameter properties edit section
   *
   * @param setEnable
   */
  public void enableFields(final boolean setEnable) {
    this.comboClass.setEnabled(setEnable);
    this.hintTxtArea.setEnabled(setEnable);
    this.codeWordChkBox.setEnabled(setEnable);
    this.bitwiseRuleChkBox.setEnabled(setEnable);
    this.blackListChkBox.setEnabled(setEnable);
  }

  /**
   * this method refreshes the parameter properties
   */
  public void refreshData() {
    // the UI is updated as there could be changes from other dialogs
    this.hintTxtArea.setText(CommonUtils.isNull(this.param.getParamHint()) ? "" : this.param.getParamHint());
    this.codeWordChkBox.setSelection(CommonUtils.isEqual(this.param.getCodeWord(), ApicConstants.CODE_WORD_YES));
    this.comboClass.select(this.ruleInfoSection.getParamDataProvider().getPclass(this.param) == null ? 0
        : this.ruleInfoSection.getParamDataProvider().getPclass(this.param).ordinal() + 1);
  }

  /**
   * @return true if this section is enabled or not through checking code word check box
   */
  public boolean isEnabled() {
    return this.codeWordChkBox.isEnabled();
  }


  /**
   * @return the longNameTxt
   */
  public StyledText getLongNameTxt() {
    return this.longNameTxt;
  }
}
