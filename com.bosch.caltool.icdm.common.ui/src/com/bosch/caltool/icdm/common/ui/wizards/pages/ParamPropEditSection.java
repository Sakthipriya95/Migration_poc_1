/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.client.bo.caldataimport.MultiCalDataImportCompObj;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.ui.wizards.CalDataFileImportWizardData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.Function;
import com.bosch.caltool.icdm.model.a2l.ParameterClass;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * @author mkl2cob
 */
public class ParamPropEditSection {

  /**
   * boolean for multiple update
   */
  boolean multiUpdate;
  /**
   * FormToolkit instance
   */
  private final FormToolkit formToolkit;

  /**
   * top composite from RuleInfoSection
   */
  private final Composite top;

  /**
   * text field to display param name
   */
  private Text nameTxt;
  /**
   * text field to display long name
   */
  private Text longNameTxt;
  /**
   * text field to display func name
   */
  private Text funcNameTxt;

  /**
   * combo to display parameter class
   */
  private Combo comboClass;


  /**
   * check box to display code word
   */
  private Button codeWordChkBox;
  /**
   * check box to display bitwise
   */
  private Button bitwiseRuleChkBox;

  /**
   * Key for parameter class in parameter properties map
   */
  private static final String KEY_PARAM_CLASS = "pclass";


  /**
   * Key for parameter iscodeword in parameter properties map
   */
  private static final String KEY_CODE_WORD = "codeword";

  /**
   * Key for parameter long name in parameter properties map
   */
  private static final String KEY_LONG_NAME = "longname";

  /**
   * Key for parameter iscodeword in parameter properties map
   */
  private static final String KEY_CAL_HINT = "calhint";

  /**
   * Key for use check box of class
   */
  private static final String KEY_USE_CLASS = "usenewclass";


  /**
   * @return the bitwiseRuleChkBox
   */
  public Button getBitwiseRuleChkBox() {
    return this.bitwiseRuleChkBox;
  }

  /**
   * text area to display calibration hint
   */
  private Text hintTxtArea;

  /**
   * RuleInfoSection instance
   */
  private final CompareRuleImpWizardPage wizardPage;

  /**
   * use import parameter class check box
   */
  private Button useImpValForPClass;

  private static final int TEXT_FIELD_WIDTHHINT_2 = 250;

  /**
   * Param properties section cols
   */
  private static final int PARAM_PROP_COLS = 4;

  /**
   * class text field in iCDM
   */
  private Text classTxt;

  /**
   * map of parameter properties
   */
  private Map<String, Map<String, String>> paramProps;

  /**
   * icdm-1930 boolean to indicate whether editing is possible
   */
  private final boolean enableEditing;
  /**
   * code word label
   */
  private Label lblCodeWord;

  /**
   * bitwise label
   */
  private Label lblBitWise;
  /**
   * whether tristate transition is needed
   */
  private boolean triState;


  /**
   * @param top Composite
   * @param formToolkit2 FormToolkit
   * @param wizardPage CompareRuleImpWizardPage
   */
  public ParamPropEditSection(final Composite top, final FormToolkit formToolkit2,
      final CompareRuleImpWizardPage wizardPage) {
    this.top = top;
    this.formToolkit = formToolkit2;
    this.wizardPage = wizardPage;
    // ICDM-1930
    this.enableEditing = this.wizardPage.getImportWizard().getWizardData().getImportObject() instanceof Function;
  }

  /**
   * This method initializes group1
   */
  public void createsectionParamProperties() {

    final Section sectionParamProp =
        this.formToolkit.createSection(this.top, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    final GridLayout gridLayout1 = new GridLayout();
    gridLayout1.numColumns = PARAM_PROP_COLS;
    sectionParamProp.setLayout(gridLayout1);
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    sectionParamProp.setLayoutData(gridData);
    sectionParamProp.setText("Parameter Properties");
    sectionParamProp.setDescription("This section is common for all rules of the parameter");

    final Form formParamProperties = this.formToolkit.createForm(sectionParamProp);
    formParamProperties.getBody().setLayout(gridLayout1);
    formParamProperties.setLayoutData(gridData);
    // controls are added to the section
    createNameUIControls(formParamProperties);

    // controls for long name
    createLongNameUIControls(formParamProperties);
    // create function name text only for function editor
    if (this.wizardPage.getImportWizard().getWizardData().getImportObject() instanceof Function) {
      // controls for func name
      createFuncNameUIControls(formParamProperties);
      // controls for calibration hint
      createHintUIControls(formParamProperties);
      // controls for code word
      createCodeWordUIControls(formParamProperties);
    }
    else {
      // controls for code word
      createCodeWordUIControls(formParamProperties);
      // controls for calibration hint
      createHintUIControls(formParamProperties);
    }
    // controls for param class
    createClassUIControls(formParamProperties);
    // ICDM-1930
    createBitwiseRule(formParamProperties);
    sectionParamProp.setClient(formParamProperties);
    sectionParamProp.getDescriptionControl().setEnabled(false);

  }

  /**
   * icdm-1930
   *
   * @param formParamProperties
   */
  private void createBitwiseRule(final Form form) {
    LabelUtil.getInstance().createEmptyLabel(form.getBody());
    createLabelControl(form.getBody(), "Bitwise Rule");
    Composite bwComposite = new Composite(form.getBody(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    bwComposite.setLayout(layout);
    bwComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.bitwiseRuleChkBox = new Button(bwComposite, SWT.CHECK);
    this.bitwiseRuleChkBox.setEnabled(false);

    this.lblBitWise = new Label(bwComposite, SWT.NONE);
    this.lblBitWise.setLayoutData(GridDataUtil.getInstance().createGridData());
    this.lblBitWise.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
  }

  /**
   * @param form
   */
  private void createNameUIControls(final Form form) {
    // name field is not disabled to make copy to clipboard available
    LabelUtil.getInstance().createLabel(form.getBody(), "Name");
    this.nameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, false);
    this.nameTxt.setEnabled(true);
  }

  /**
   * @param form
   */
  private void createClassUIControls(final Form form) {
    Label classLabel = LabelUtil.getInstance().createLabel(this.formToolkit, form.getBody(), "Class");
    GridData btmGridData = new GridData();
    btmGridData.verticalAlignment = SWT.CENTER;
    classLabel.setLayoutData(btmGridData);
    Composite classComp1 = new Composite(form.getBody(), SWT.NONE);
    createLabelControl(classComp1, "iCDM");
    createLabelControl(classComp1, "Imported Class");
    GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    classComp1.setLayout(layout);
    GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessHorizontalSpace = true;
    gridData.horizontalAlignment = SWT.LEFT;
    classComp1.setLayoutData(gridData);
    GridData layoutData = new GridData();
    layoutData.horizontalAlignment = SWT.FILL;
    layoutData.grabExcessHorizontalSpace = true;
    Label useLabel = LabelUtil.getInstance().createLabel(this.formToolkit, classComp1, " ");
    useLabel.setLayoutData(layoutData);

    this.classTxt = createStyledTextField(classComp1, 90, false);
    this.comboClass = new Combo(classComp1, SWT.READ_ONLY);
    final GridData descData = new GridData(SWT.NONE, SWT.NONE, false, false);
    descData.widthHint = 80;
    this.comboClass.setLayoutData(descData);
    this.comboClass.add("");
    this.comboClass.add(ParameterClass.SCREW.getText());
    this.comboClass.add(ParameterClass.RIVET.getText());
    this.comboClass.add(ParameterClass.NAIL.getText());
    // ICDM-916 stat Rivet
    this.comboClass.add(ParameterClass.STATRIVET.getText());


    // Selection listener
    this.comboClass.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        String selection =
            ParamPropEditSection.this.comboClass.getItem(ParamPropEditSection.this.comboClass.getSelectionIndex());
        // if the selection is STAT-RIVET
        if (ParameterClass.STATRIVET.getText().equals(selection)) {
          MessageDialogUtils.getErrorMessageDialog("Statistical Rivet cannot be set manually",
              "Statistical Rivet class cannot be set manually");
          ParamPropEditSection.this.comboClass.select(0);
        }
        selection =
            ParamPropEditSection.this.comboClass.getItem(ParamPropEditSection.this.comboClass.getSelectionIndex());
        if (ParamPropEditSection.this.useImpValForPClass.getSelection()) {
          if (ParamPropEditSection.this.multiUpdate) {
            // ICDM-2179
            ParamPropEditSection.this.wizardPage.getMultiCompObj().setNewParamClass(selection);
          }
          else {
            // set the class value if the check box is checked
            Map<String, String> map =
                ParamPropEditSection.this.paramProps.get(ParamPropEditSection.this.nameTxt.getText());
            map.put(KEY_PARAM_CLASS, selection);
          }
        }
      }
    });
    // icdm-1930
    this.comboClass.setEnabled(this.enableEditing);

    createUseNewPClassChkBox(classComp1, layoutData);
  }

  /**
   * @param classComp1
   * @param layoutData
   */
  private void createUseNewPClassChkBox(final Composite classComp1, final GridData layoutData) {
    this.useImpValForPClass = new Button(classComp1, SWT.CHECK);
    this.useImpValForPClass.setText("Use");
    this.useImpValForPClass.setToolTipText("Use Imported Value");
    this.useImpValForPClass.setLayoutData(layoutData);
    this.useImpValForPClass.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        boolean selection = ParamPropEditSection.this.useImpValForPClass.getSelection();
        String selectedPClass =
            ParamPropEditSection.this.comboClass.getItem(ParamPropEditSection.this.comboClass.getSelectionIndex());
        if (ParamPropEditSection.this.multiUpdate) {
          // ICDM-2179
          ParamPropEditSection.this.wizardPage.getMultiCompObj().setUseNewParamClass(selection);
          ParamPropEditSection.this.wizardPage.getMultiCompObj().setNewParamClass(selectedPClass);
        }
        else {
          Map<String, String> map =
              ParamPropEditSection.this.paramProps.get(ParamPropEditSection.this.nameTxt.getText());
          if (selection) {
            map.put(KEY_PARAM_CLASS, selectedPClass);
            map.put(KEY_USE_CLASS, "true");
          }
          else {
            map.put(KEY_PARAM_CLASS, ParamPropEditSection.this.classTxt.getText());
            map.put(KEY_USE_CLASS, "false");
          }
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent event) {
        // TODO Auto-generated method stub

      }

    });

    // ICDM-1930
    this.useImpValForPClass.setEnabled(this.enableEditing);
  }

  /**
   * @param form
   */
  private void createLongNameUIControls(final Form form) {

    createLabelControl(form.getBody(), "Long Name");
    this.longNameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, false);
    this.longNameTxt.setLayoutData(GridDataUtil.getInstance().createGridData());
    this.longNameTxt.setEnabled(true);
  }

  /**
   * @param form
   */
  private void createFuncNameUIControls(final Form form) {

    createLabelControl(form.getBody(), "Func Name");
    this.funcNameTxt = createStyledTextField(form.getBody(), TEXT_FIELD_WIDTHHINT_2, false);
    this.funcNameTxt.setEnabled(true);

  }

  /**
   * @param form
   */
  private void createCodeWordUIControls(final Form form) {
    createLabelControl(form.getBody(), "Codeword");
    Composite cwComposite = new Composite(form.getBody(), SWT.NONE);
    GridLayout layout = new GridLayout();
    layout.numColumns = 2;
    cwComposite.setLayout(layout);
    cwComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
    this.codeWordChkBox = new Button(cwComposite, SWT.CHECK);
    this.codeWordChkBox.setEnabled(this.enableEditing);
    this.codeWordChkBox.addSelectionListener(new SelectionAdapter() {

      private int state;

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (ParamPropEditSection.this.multiUpdate) {
          // ICDM-2179
          if (ParamPropEditSection.this.triState) {
            // in case of tri state
            this.state++;
            if (this.state > 2) {
              this.state = 0;
            }
            Button button = (Button) event.widget;
            switch (this.state) {
              case 0:
                button.setSelection(false);
                button.setGrayed(false);
                ParamPropEditSection.this.lblCodeWord.setText("Set Codeword to No");
                ParamPropEditSection.this.wizardPage.getMultiCompObj().setCodeWord("No");
                break;
              case 1:
                button.setSelection(true);
                button.setGrayed(false);
                ParamPropEditSection.this.lblCodeWord.setText("Set Codeword to Yes");
                ParamPropEditSection.this.wizardPage.getMultiCompObj().setCodeWord("Yes");
                break;
              case 2:
                caseDiffVal(button);
                break;
              default:
                // do nothing
            }
          }
          else {
            // if there is no tri state
            ParamPropEditSection.this.wizardPage.getMultiCompObj()
                .setCodeWord(ParamPropEditSection.this.codeWordChkBox.getSelection() ? "Yes" : "No");
          }
        }
        else {
          Map<String, String> map =
              ParamPropEditSection.this.paramProps.get(ParamPropEditSection.this.nameTxt.getText());
          map.put(KEY_CODE_WORD, ParamPropEditSection.this.codeWordChkBox.getSelection() ? "Yes" : "No");
        }
      }

      /**
       * @param button
       */
      private void caseDiffVal(final Button button) {
        button.setSelection(true);
        button.setGrayed(true);
        ParamPropEditSection.this.lblCodeWord.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
        ParamPropEditSection.this.wizardPage.getMultiCompObj().setCodeWord(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      }

    });
    this.lblCodeWord = new Label(cwComposite, SWT.NONE);
    this.lblCodeWord.setLayoutData(GridDataUtil.getInstance().createGridData());
    this.lblCodeWord.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY));
    if (this.wizardPage.getImportWizard().getWizardData().getImportObject() instanceof Function) {
      createLabelControl(form.getBody(), "");
    }
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
    this.hintTxtArea = new Text(form.getBody(), SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData createGridData = GridDataUtil.getInstance().getGridData();
    createGridData.grabExcessHorizontalSpace = false;
    createGridData.verticalSpan = 4;

    this.hintTxtArea.setLayoutData(createGridData);
    this.hintTxtArea.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent arg0) {
        String name = ParamPropEditSection.this.nameTxt.getText();
        String text = ParamPropEditSection.this.hintTxtArea.getText();
        if (ParamPropEditSection.this.multiUpdate) {
          // ICDM-2179
          ParamPropEditSection.this.wizardPage.getMultiCompObj().setCalHint(text);
        }
        else if (CommonUtils.isNotEmptyString(name)) {
          // to avoid null pointer exception in case of multi select
          Map<String, String> map = ParamPropEditSection.this.paramProps.get(name);
          if ((null == map.get(KEY_CAL_HINT)) || !map.get(KEY_CAL_HINT).equals(text)) {
            map.put(KEY_CAL_HINT, text);
          }
        }
      }
    });
    // ICDM-1930
    this.hintTxtArea.setEditable(this.enableEditing);
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
  private Text createStyledTextField(final Composite comp, final int widthHint, final boolean isEditable) {
    Text styledTxt = new Text(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(widthHint));
    styledTxt.setEditable(isEditable);
    return styledTxt;
  }


  /**
   * populate the section with details of the selected parameter from the table
   */
  public void populateParamDetails() {
    this.multiUpdate = false;
    // enable the disabled fields
    enableDisableFields(true);
    if (this.comboClass.indexOf(MultiCalDataImportCompObj.DIFFERENT_VALUES) != -1) {
      this.comboClass.remove(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    CalDataFileImportWizardData wizardData = this.wizardPage.getImportWizard().getWizardData();
    CalDataImportComparisonModel selCompObj = this.wizardPage.getSelCompObj();
    String paramName = selCompObj.getParamName();
    this.nameTxt.setText(paramName);
    Map<String, String> properties = getParamProps().get(paramName);
    if (properties != null) {
      this.longNameTxt.setText(CommonUtils.checkNull(properties.get(CalDataFileImportWizardData.KEY_LONG_NAME)));
      String funcName = this.wizardPage.getImportWizard().getCalImportData().getParamFuncMap().get(paramName);
      if ((this.funcNameTxt != null) && (funcName != null)) {
        this.funcNameTxt.setText(funcName);
      }
      String calHint = properties.get(CalDataFileImportWizardData.KEY_CAL_HINT);
      this.hintTxtArea.setText(CommonUtils.checkNull(calHint));

      this.codeWordChkBox
          .setSelection(CommonUtils.isEqual(properties.get(CalDataFileImportWizardData.KEY_CODE_WORD), "Yes"));
      this.codeWordChkBox.setGrayed(false);
      this.lblCodeWord.setText("");

      Map<String, String> oldParamPropsMap = wizardData.getOldParamClassMap();
      // set value from old map
      this.classTxt.setText(CommonUtils.checkNull(oldParamPropsMap.get(paramName)));

      // set the value in the combo box
      this.comboClass.setText(properties.get(CalDataFileImportWizardData.KEY_PARAM_CLASS));

      // set the use check box selection
      this.useImpValForPClass
          .setSelection(CommonUtils.isEqual(properties.get(CalDataFileImportWizardData.KEY_USE_CLASS), "true"));

      this.bitwiseRuleChkBox.setSelection(
          CommonUtils.isEqual(properties.get(CalDataFileImportWizardData.KEY_BIT_WISE), ApicConstants.CODE_WORD_YES));
    }
    // ICDM-1930

    this.bitwiseRuleChkBox.setGrayed(false);
    this.lblBitWise.setText("");
  }

  /**
   * @return the paramProps
   */
  public Map<String, Map<String, String>> getParamProps() {
    if (this.paramProps == null) {
      this.paramProps = this.wizardPage.getImportWizard().getWizardData().getNewParamPropsMap();
    }
    return this.paramProps;
  }

  /**
   * disable all fields and empty the contents
   */
  public void disableFields() {
    this.nameTxt.setText("");
    this.longNameTxt.setText("");
    this.hintTxtArea.setText("");
    this.codeWordChkBox.setSelection(false);
    this.classTxt.setText("");
    this.comboClass.setText("");
    this.bitwiseRuleChkBox.setSelection(false);
    this.useImpValForPClass.setSelection(false);

    enableDisableFields(false);
  }

  /**
   * @param enable
   */
  private void enableDisableFields(final boolean enable) {
    boolean enableField = enable && this.enableEditing;
    this.hintTxtArea.setEnabled(enableField);
    this.hintTxtArea.setEditable(enableField);
    this.codeWordChkBox.setEnabled(enableField);
    this.comboClass.setEnabled(enableField);
    this.useImpValForPClass.setEnabled(enableField);
  }

  /**
   * ICDM-1999 populate multiple parameter details
   */
  public void populateMultiParamDetails() {
    this.multiUpdate = true;
    // create multi comparison object
    MultiCalDataImportCompObj multiCompObj = this.wizardPage.getMultiCompObj();
    String paramName = multiCompObj.getParamName();
    // set name
    this.nameTxt.setText(paramName);
    // set long name
    if (CommonUtils.isEqual(paramName, MultiCalDataImportCompObj.DIFFERENT_VALUES)) {
      this.longNameTxt.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    else {
      Map<String, String> properties = getParamProps().get(paramName);
      this.longNameTxt.setText(CommonUtils.checkNull(properties.get(KEY_LONG_NAME)));
    }
    // set code word
    setCodeWordForMultiEdit(multiCompObj);
    // set class fields
    this.classTxt.setText(CommonUtils.checkNull(multiCompObj.getOldParamClass()));
    // class combo
    if (CommonUtils.isEqual(multiCompObj.getNewParamClass(), MultiCalDataImportCompObj.DIFFERENT_VALUES)) {
      this.comboClass.add(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      this.comboClass.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    else if (this.comboClass.indexOf(MultiCalDataImportCompObj.DIFFERENT_VALUES) != -1) {
      this.comboClass.remove(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      this.comboClass
          .setText(RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(multiCompObj.getNewParamClass()).getICDMMaturityLevel());
    }
    // set bitwise field
    setBitwiseRuleChkBox(multiCompObj);
    // set hint fields
    this.hintTxtArea.setText(CommonUtils.checkNull(multiCompObj.getCalHint()));
    this.hintTxtArea.setEnabled(this.enableEditing);
    this.codeWordChkBox.setEnabled(this.enableEditing);
    this.comboClass.setEnabled(this.enableEditing);

  }

  /**
   * @param multiCompObj
   */
  private void setBitwiseRuleChkBox(final MultiCalDataImportCompObj multiCompObj) {
    // set code word flag
    switch (multiCompObj.isCodeWord()) {
      case "Yes":
        // if codeword is yes, set true
        caseYes();
        break;
      case "No":
        // if codeword is no, set false
        caseNo();
        break;
      case MultiCalDataImportCompObj.DIFFERENT_VALUES:
        // set the third state
        caseDiffValues();
        break;
      default:
        // do nothing

    }
  }

  /**
   *
   */
  private void caseDiffValues() {
    this.codeWordChkBox.setSelection(true);
    this.codeWordChkBox.setGrayed(true);
    this.triState = true;
    this.lblCodeWord.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
  }

  /**
   *
   */
  private void caseNo() {
    this.codeWordChkBox.setSelection(false);
    this.codeWordChkBox.setGrayed(false);
    this.triState = false;
    this.lblCodeWord.setText("");
  }

  /**
   *
   */
  private void caseYes() {
    this.codeWordChkBox.setSelection(true);
    this.codeWordChkBox.setGrayed(false);
    this.triState = false;
    this.lblBitWise.setText("");
  }

  /**
   * @param multiCompObj
   */
  private void setCodeWordForMultiEdit(final MultiCalDataImportCompObj multiCompObj) {
    if ((multiCompObj != null) && (multiCompObj.getParamBitwise() != null)) {
      // set bitwise flag
      switch (multiCompObj.getParamBitwise()) {
        case "Yes":
          // if codeword is yes, set true
          this.bitwiseRuleChkBox.setSelection(true);
          this.bitwiseRuleChkBox.setGrayed(false);
          this.lblBitWise.setText("");
          break;
        case "No":
          // if codeword is no, set false
          this.bitwiseRuleChkBox.setSelection(false);
          this.bitwiseRuleChkBox.setGrayed(false);
          this.lblBitWise.setText("");
          break;
        case MultiCalDataImportCompObj.DIFFERENT_VALUES:
          // set the third state
          this.bitwiseRuleChkBox.setSelection(true);
          this.bitwiseRuleChkBox.setGrayed(true);
          this.lblBitWise.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
          break;
        default:
          // do nothing
      }
    }

  }
}
