/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.wizards.pages;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calmodel.a2ldata.Activator;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.client.bo.caldataimport.MultiCalDataImportCompObj;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.bo.a2l.RuleMaturityLevel;
import com.bosch.caltool.icdm.common.bo.cdr.ReviewRuleUtil;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.ui.views.providers.ComboViewerContentPropsalProvider;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.READY_FOR_SERIES;
import com.bosch.caltool.icdm.model.apic.Unit;
import com.bosch.caltool.icdm.model.caldataimport.CalDataImportComparisonModel;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.ParameterType;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.apic.UnitServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.decorators.Decorators;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;
import com.bosch.rcputils.ui.forms.SectionUtil;
import com.bosch.rcputils.ui.validators.Validator;


/**
 * @author mkl2cob
 */
public class RuleDetailsSection {

  /**
   * flag to indicate whether it is multi update
   */
  private boolean multiUpdate;
  /**
   * Exact match button instance
   */
  private Button exactMtch;
  /**
   * maturityLevel Combo instance
   */
  private Combo maturityLevel;

  /**
   * revert button instance
   */
  private Button doneBtn;

  private final FormToolkit toolkit;

  /**
   * CompareRuleImpWizardPage
   */
  private final CompareRuleImpWizardPage wizardPge;
  private Text icdmMaturityText;
  private Text unitICDMText;
  private Text lowerLmtICDM;
  private Text lowerImpFileText;
  private Text upperICDMText;
  private Text upperImpFileText;
  private Text refValICDMText;
  private Text refVaImpFileText;
  private Text rvwMtdIcdmText;
  private Text unitFileText;
  private Combo rvwMtdCombo;
  private Text remarkTxtArea;

  private Button lowLimitImpValue;
  private Button upperLimitImpValue;
  /**
   *
   */
  protected ControlDecoration lowerLimitDecor;
  /**
   *
   */
  protected ControlDecoration upperLimitDecor;
  private Button refVaImpValue;
  private Button useImpValueRvwMtd;
  private Button useImpValueMaturityLvl;
  /**
   * ICDM-1930
   */
  private Text bitWiseICDMText;

  /**
   * upper limit is not set properly without this flag
   */
  private boolean upperLimitSet = true;
  /**
   * info decorator for remark
   */
  private ControlDecoration remarkDecorator;
  /**
   * info decorator for exact match
   */
  private ControlDecoration exactMatchDecorator;

  /**
   * boolean to indicate if the parameter is bitwise
   */
  private boolean isBitwiseRule;
  private ControlDecoration unitDecorator;
  private Button useUnitBtn;
  /**
   * List of available units
   */
  private SortedSet<String> units = new TreeSet<>();


  /**
   * @param toolkit FormToolkit
   * @param compareRuleImpWizardPage CompareRuleImpWizardPage
   */
  public RuleDetailsSection(final FormToolkit toolkit, final CompareRuleImpWizardPage compareRuleImpWizardPage) {
    this.toolkit = toolkit;
    this.wizardPge = compareRuleImpWizardPage;
  }

  /**
   * Create the rule details section
   *
   * @param topSashForm SashForm
   */
  public void createRuleDetailsSection(final SashForm topSashForm) {
    Section ruleDetailsSection = SectionUtil.getInstance().createSection(topSashForm, this.toolkit,
        GridDataUtil.getInstance().getGridData(), "Rule Details", false);

    ruleDetailsSection.setLayoutData(GridDataUtil.getInstance().getGridData());
    Composite detailsComp = this.toolkit.createComposite(ruleDetailsSection);
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 4;
    gridLayout.makeColumnsEqualWidth = false;
    detailsComp.setLayout(gridLayout);
    detailsComp.setLayoutData(GridDataUtil.getInstance().getGridData());
    // add exact match
    addExactMatch(detailsComp);
    // add column labels
    LabelUtil.getInstance().createEmptyLabel(detailsComp);
    LabelUtil.getInstance().createEmptyLabel(detailsComp);
    LabelUtil.getInstance().createEmptyLabel(detailsComp);
    new Label(detailsComp, SWT.NONE).setText("iCDM");
    new Label(detailsComp, SWT.NONE).setText("Imported File");
    new Label(detailsComp, SWT.NONE).setText("Use Import\n Value");
    createUnitUIControls(detailsComp);

    // add lower upper and ref value
    addLowerUpperRefVal(detailsComp);
    // add maturity level
    addMaturityLevel(detailsComp);
    String labelName = "";
    try {
      labelName = new CommonDataBO().getMessage("CDR_RULE", "READY_FOR_SERIES", "");
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    // add ready for series
    new Label(detailsComp, SWT.NONE).setText(labelName);

    addReviewMethod(detailsComp);

    // add Remarks
    addRemarksUIControl(detailsComp);
    // add done button
    addDoneButton(detailsComp);

    detailsComp.layout(true, true);
    // set client
    ruleDetailsSection.setClient(detailsComp);
  }

  /**
   * @param detailsComp
   */
  private void addReviewMethod(final Composite detailsComp) {
    this.rvwMtdIcdmText = createTextField(detailsComp, false);
    this.rvwMtdCombo = new Combo(detailsComp, SWT.READ_ONLY);
    this.rvwMtdCombo.setLayoutData(GridDataUtil.getInstance().getGridData());

    final GridData descData = new GridData(SWT.NONE, SWT.NONE, false, false);
    descData.widthHint = 182;
    descData.verticalIndent = 8;
    this.rvwMtdCombo.setLayoutData(descData);
    this.rvwMtdCombo.add(ApicConstants.USED_YES_DISPLAY);
    this.rvwMtdCombo.add(ApicConstants.USED_NO_DISPLAY);

    this.rvwMtdCombo.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        String rvwMtd = READY_FOR_SERIES.getDbType(
            RuleDetailsSection.this.rvwMtdCombo.getItem(RuleDetailsSection.this.rvwMtdCombo.getSelectionIndex()));
        if (RuleDetailsSection.this.multiUpdate) {
          // ICDM-2178
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setReviewMethod(rvwMtd);
        }
        else {
          RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setReviewMethod(rvwMtd);
        }
      }
    });
    this.useImpValueRvwMtd = createButton(detailsComp);
    this.useImpValueRvwMtd.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean selection = RuleDetailsSection.this.useImpValueRvwMtd.getSelection();
        if (RuleDetailsSection.this.multiUpdate) {
          // ICDM-2178
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setUseNewRvwMtd(selection);
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          selCompObj.setUseNewRvwMtd(selection);
        }
      }
    });
  }

  /**
   * @param detailsComp Composite
   * @return
   */
  private Button createButton(final Composite detailsComp) {
    Button btn = new Button(detailsComp, SWT.CHECK);
    GridData gridData = new GridData();
    gridData.horizontalAlignment = SWT.CENTER;
    return btn;
  }

  /**
   * add UI control for remarks
   *
   * @param detailsComp Composite
   */
  private void addRemarksUIControl(final Composite detailsComp) {
    Label remarkLabel =
        LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "Remarks(Rule/Attribute Specific)");
    GridData gridData = new GridData();
    gridData.horizontalSpan = 2;
    remarkLabel.setLayoutData(gridData);
    LabelUtil.getInstance().createEmptyLabel(detailsComp);
    LabelUtil.getInstance().createEmptyLabel(detailsComp);

    this.remarkTxtArea = new Text(detailsComp, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
    final GridData createGridData = GridDataUtil.getInstance().getGridData();
    createGridData.horizontalSpan = 4;
    createGridData.minimumHeight = 100;

    this.remarkTxtArea.setLayoutData(createGridData);

    this.remarkTxtArea.addModifyListener(e -> {
      if (this.multiUpdate) {
        this.wizardPge.getMultiCompObj().setRemarks(this.remarkTxtArea.getText());
      }
      else {
        this.wizardPge.getSelCompObj().getNewRule().setHint(this.remarkTxtArea.getText());
        this.wizardPge.getSelCompObj().getNewRule().setUnicodeRemarks(this.remarkTxtArea.getText());
      }
    });

    this.remarkDecorator = new ControlDecoration(this.remarkTxtArea, SWT.LEFT | SWT.TOP);
    this.remarkDecorator.setDescriptionText(
        "Remark is a combination of existing remark and new remark of import file. Please edit the remark.");
    Image image =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
    this.remarkDecorator.setImage(image);
  }

  /**
   * @param detailsComp Composite
   */
  private void addLowerUpperRefVal(final Composite detailsComp) {
    addLowerLimitFields(detailsComp);

    addUpperLmtFields(detailsComp);

    createBitwiseAndRefValue(detailsComp);
  }

  /**
   * @param detailsComp
   */
  private void createBitwiseAndRefValue(final Composite detailsComp) {
    // ICDM-1930
    LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "Bitwise Rule");
    this.bitWiseICDMText = createTextField(detailsComp, false);
    LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "");
    LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "");

    LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "Reference Value");
    this.refValICDMText = createTextField(detailsComp, false);
    this.refVaImpFileText = createTextField(detailsComp, true);
    this.refVaImpFileText.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(197));

    this.refVaImpFileText.addModifyListener(event -> {
      if (this.multiUpdate) {
        this.wizardPge.getMultiCompObj().setNewRefVal(this.refVaImpFileText.getText());
        enableDisableExactMatch(null);
      }
      else {
        CalDataImportComparisonModel selCompObj = this.wizardPge.getSelCompObj();
        convertValToCalData(selCompObj);
        this.wizardPge.getTableGraphComp().populateTableGraph();
        enableDisableExactMatch(selCompObj);
        validateUnit();// ICDM-2028
      }
    });

    this.refVaImpValue = createButton(detailsComp);
    this.refVaImpValue.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean selection = RuleDetailsSection.this.refVaImpValue.getSelection();
        if (RuleDetailsSection.this.multiUpdate) {
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setUseNewRefVal(selection);
          enableDisableExactMatch(null);
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          selCompObj.setUseNewRefVal(selection);
          enableDisableExactMatch(selCompObj);
          validateUnit();// ICDM-2028
        }
      }
    });
  }

  /**
   * @param detailsComp
   */
  private void addUpperLmtFields(final Composite detailsComp) {
    LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "Upper Limit");
    this.upperICDMText = createTextField(detailsComp, false);
    this.upperImpFileText = createTextField(detailsComp, true);
    this.upperImpFileText.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(197));
    this.upperLimitDecor = new ControlDecoration(this.upperImpFileText, SWT.RIGHT | SWT.TOP);

    this.upperImpFileText.addModifyListener(event -> {
      if (this.multiUpdate) {
        // TODO check for number validation
        this.wizardPge.getMultiCompObj().setNewUpLmt(this.upperImpFileText.getText().trim());
      }
      else {
        upperLmtValidation();
        lowerLimitValidation();
        validateUnit();// ICDM-2028
      }
    });

    this.upperLimitImpValue = createButton(detailsComp);
    this.upperLimitImpValue.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean selection = RuleDetailsSection.this.upperLimitImpValue.getSelection();
        if (RuleDetailsSection.this.multiUpdate) {
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setUseNewUpLmt(selection);
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          selCompObj.setUseNewUpLmt(selection);
          upperLmtValidation();
          lowerLimitValidation();
          validateUnit();// ICDM-2028
        }
      }
    });
  }

  /**
   * @param detailsComp
   */
  private void addLowerLimitFields(final Composite detailsComp) {
    LabelUtil.getInstance().createLabel(this.toolkit, detailsComp, "Lower Limit");
    this.lowerLmtICDM = createTextField(detailsComp, false);
    this.lowerImpFileText = createTextField(detailsComp, true);
    this.lowerImpFileText.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(197));
    this.lowerLimitDecor = new ControlDecoration(this.lowerImpFileText, SWT.RIGHT | SWT.TOP);

    this.lowerImpFileText.addModifyListener(event -> {
      if (this.multiUpdate) {
        // TODO check for number validation
        this.wizardPge.getMultiCompObj().setNewLowLmt(this.lowerImpFileText.getText().trim());
      }
      else {
        lowerLimitValidation();
        upperLmtValidation();
        validateUnit();// ICDM-2028
      }
    });

    this.lowLimitImpValue = createButton(detailsComp);
    this.lowLimitImpValue.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean selection = RuleDetailsSection.this.lowLimitImpValue.getSelection();
        if (RuleDetailsSection.this.multiUpdate) {
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setUseNewLowLmt(selection);
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          selCompObj.setUseNewLowLmt(selection);
          lowerLimitValidation();
          upperLmtValidation();
          validateUnit();// ICDM-2028
        }
      }
    });
  }

  /**
   * enable disable exact match based on reference value
   *
   * @param selCompObj CalDataImportComparison
   */
  private void enableDisableExactMatch(final CalDataImportComparisonModel selCompObj) {
    if (this.isBitwiseRule) {
      return;
    }

    // do validations only if it is not a bitwise rule
    String refValText = this.refValICDMText.getText();
    if ((this.multiUpdate && this.wizardPge.getMultiCompObj().isUseNewRefVal()) ||
        ((selCompObj != null) && selCompObj.isUseNewRefVal())) {
      refValText = this.refVaImpFileText.getText();
    }
    // change the decorator only if it is not a bitwise rule
    if (CommonUtils.isEmptyString(refValText)) {
      // disable exact match
      this.exactMtch.setEnabled(false);
      this.exactMtch.setSelection(false);
      exactMatchChange(true);
      if ((null != selCompObj) && !this.multiUpdate) {
        selCompObj.setExactMatchEnable(false);
      }
      this.exactMatchDecorator.setDescriptionText("The Exact Match is disabled because of no Reference Value!");
      this.exactMatchDecorator.show();
    }
    else {
      // enable exact match
      this.exactMtch.setEnabled(true);
      if ((null != selCompObj) && !this.multiUpdate) {
        selCompObj.setExactMatchEnable(true);
      }
      this.exactMatchDecorator.hide();
    }

  }

  /**
   * @param selCompObj
   */
  private void convertValToCalData(final CalDataImportComparisonModel selCompObj) {
    String unit;
    // get unit text based on the use new value check box
    if (selCompObj.isUseNewUnit()) {
      unit = RuleDetailsSection.this.unitFileText.getText();
    }
    else {
      unit = RuleDetailsSection.this.unitICDMText.getText();
    }
    ReviewRuleUtil.setRefValueToReviewRule(selCompObj.getNewRule(), RuleDetailsSection.this.refVaImpFileText.getText(),
        selCompObj.getParamType(), unit, ReviewRuleUtil.getRefValue(selCompObj.getNewRule()));


  }

  /**
   * validates new lower limit text
   */
  private void lowerLimitValidation() {
    // do the validation only if the upper limit is set
    if (!this.upperLimitSet) {
      return;
    }

    String lowLmt = RuleDetailsSection.this.lowerImpFileText.getText().trim();
    RuleDetailsSection.this.lowLimitImpValue.setEnabled(true);
    // perform validation for number text
    boolean validateNumericVal = Validator.getInstance().validateNDecorate(RuleDetailsSection.this.lowerLimitDecor,
        RuleDetailsSection.this.lowerImpFileText, null, false, true, ",");

    if (CommonUtils.isEmptyString(lowLmt)) {
      // if empty string
      // set the value to null if the text is cleared
      RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setLowerLimit(null);
    }
    else {
      if (validateNumericVal) {
        BigDecimal lowLmtBigDec = CommonUtils.isEmptyString(lowLmt) ? null : new BigDecimal(lowLmt);
        if (validateLowLmt(lowLmtBigDec)) {
          // if lower limit is less than upper limit
          RuleDetailsSection.this.lowLimitImpValue.setEnabled(true);
        }
        else {
          Decorators decorators = new Decorators();
          decorators.showErrDecoration(RuleDetailsSection.this.lowerLimitDecor,
              "Lower Limit should be less than Upper limit!", true);
          RuleDetailsSection.this.lowLimitImpValue.setEnabled(false);
          RuleDetailsSection.this.lowLimitImpValue.setSelection(false);
          RuleDetailsSection.this.wizardPge.getSelCompObj().setUseNewLowLmt(false);
        }
        RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setLowerLimit(lowLmtBigDec);
      }
      else {
        RuleDetailsSection.this.lowLimitImpValue.setSelection(false);
        RuleDetailsSection.this.lowLimitImpValue.setEnabled(false);
        RuleDetailsSection.this.wizardPge.getSelCompObj().setUseNewLowLmt(false);
      }
    }

  }

  /**
   * validates new upper limit text
   */
  private void upperLmtValidation() {

    // do the validation only if the upper limit is set
    if (!this.upperLimitSet) {
      return;
    }

    String textValue = RuleDetailsSection.this.upperImpFileText.getText().trim();
    RuleDetailsSection.this.upperLimitImpValue.setEnabled(true);
    boolean validateNumericVal = Validator.getInstance().validateNDecorate(RuleDetailsSection.this.upperLimitDecor,
        RuleDetailsSection.this.upperImpFileText, null, false, true, ",");
    if (CommonUtils.isEmptyString(textValue)) {
      RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setUpperLimit(null);
    }
    else {
      if (validateNumericVal) {
        BigDecimal upLmtBigDec = CommonUtils.isEmptyString(textValue) ? null : new BigDecimal(textValue);
        if (validateUpperLmt(upLmtBigDec)) {
          RuleDetailsSection.this.upperLimitImpValue.setEnabled(true);
        }
        else {
          Decorators decorators = new Decorators();
          decorators.showErrDecoration(RuleDetailsSection.this.upperLimitDecor,
              "Lower Limit should be less than upper limit!", true);
          RuleDetailsSection.this.upperLimitImpValue.setEnabled(false);
          RuleDetailsSection.this.upperLimitImpValue.setSelection(false);
          RuleDetailsSection.this.wizardPge.getSelCompObj().setUseNewUpLmt(false);
        }
        RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setUpperLimit(upLmtBigDec);

      }
      else {
        RuleDetailsSection.this.upperLimitImpValue.setSelection(false);
        RuleDetailsSection.this.upperLimitImpValue.setEnabled(false);
        RuleDetailsSection.this.wizardPge.getSelCompObj().setUseNewUpLmt(false);
      }

    }

  }

  private boolean validateLowLmt(final BigDecimal lowLmtBigDec) {
    BigDecimal upLmtBigDec;
    String upLmt;
    if (this.upperLimitImpValue.getSelection()) {
      upLmt = RuleDetailsSection.this.upperImpFileText.getText().trim();

    }
    else {
      upLmt = RuleDetailsSection.this.upperICDMText.getText().trim();
    }
    upLmtBigDec = CommonUtils.isEmptyString(upLmt) ? null : new BigDecimal(upLmt);
    return areLimitsValid(lowLmtBigDec, upLmtBigDec);
  }

  /**
   * @param upLmtBigDec upper limit big decimal
   * @return true if upper limit is greater than lower limit
   */
  private boolean validateUpperLmt(final BigDecimal upLmtBigDec) {
    BigDecimal lowLmtBigDec;
    String lowLmt;
    CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
    if (selCompObj.isUseNewLowLmt()) {
      lowLmt = RuleDetailsSection.this.lowerImpFileText.getText().trim();
    }
    else {
      lowLmt = RuleDetailsSection.this.lowerLmtICDM.getText().trim();
    }
    lowLmtBigDec = CommonUtils.isEmptyString(lowLmt) ? null : new BigDecimal(lowLmt);
    return areLimitsValid(lowLmtBigDec, upLmtBigDec);
  }


  /**
   * @return the lowLimitImpValue
   */
  public Button getLowLimitImpValue() {
    return this.lowLimitImpValue;
  }


  /**
   * @return the upperLimitImpValue
   */
  public Button getUpperLimitImpValue() {
    return this.upperLimitImpValue;
  }

  /**
   * @param lowLmtBigDec lower limit
   * @param upLmtBigDec upper limit
   */
  private boolean areLimitsValid(final BigDecimal lowLmtBigDec, final BigDecimal upLmtBigDec) {
    if ((lowLmtBigDec == null) || (upLmtBigDec == null)) {
      return true;
    }
    return ApicUtil.compareBigDecimal(lowLmtBigDec, upLmtBigDec) < 0;
  }

  /**
   * @param comp
   * @param widthHint
   * @param isEditable
   * @return Text
   */
  private Text createTextField(final Composite comp, final boolean isEditable) {
    Text styledTxt = new Text(comp, SWT.SINGLE | SWT.BORDER);
    styledTxt.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(145));
    styledTxt.setEnabled(isEditable);
    return styledTxt;
  }

  /**
   * @param scComp
   */
  private void createUnitUIControls(final Composite scComp) {
    LabelUtil.getInstance().createLabel(this.toolkit, scComp, "Unit");
    this.unitICDMText = createTextField(scComp, false);
    this.unitFileText = new Text(scComp, SWT.SINGLE | SWT.BORDER);
    this.unitFileText.setLayoutData(GridDataUtil.getInstance().getWidthHintGridData(197));
    this.unitFileText.setEnabled(false);
    setContentPropsalForUnitField();

    this.unitFileText.addModifyListener(event -> {
      String unit = this.unitFileText.getText().trim();
      if (RuleDetailsSection.this.multiUpdate) {
        RuleDetailsSection.this.wizardPge.getMultiCompObj().setUnit(unit);
      }
      else {
        RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setUnit(unit);
        // ICDM-2028
        validateUnit();
      }
    });

    // ICDM-2028
    this.unitDecorator = new ControlDecoration(this.unitFileText, SWT.RIGHT | SWT.TOP);
    Image image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
    this.unitDecorator.setImage(image);
    this.unitDecorator.setDescriptionText("When Unit is specified, min,max or dcm value has to be present");

    this.useUnitBtn = createButton(scComp);
    this.useUnitBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean selection = RuleDetailsSection.this.useUnitBtn.getSelection();
        if (RuleDetailsSection.this.multiUpdate) {
          MultiCalDataImportCompObj multiCompObj = RuleDetailsSection.this.wizardPge.getMultiCompObj();
          multiCompObj.setUseNewUnit(selection);
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          selCompObj.setUseNewUnit(selection);
        }
      }
    });

  }

  /**
   * Set Content Proposal for Unit Text Field
   */
  private void setContentPropsalForUnitField() {
    Set<Unit> unitSet = null;
    // Fetch the units available in icdm
    try {
      unitSet = new UnitServiceClient().getAll();
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
    }
    if (unitSet != null) {
      this.units.addAll(unitSet.stream().map(Unit::getUnitName).collect(Collectors.toSet()));
    }
    IContentProposalProvider provider =
        new ComboViewerContentPropsalProvider(this.units.toArray(new String[this.units.size()]));
    ContentProposalAdapter adapter =
        new ContentProposalAdapter(this.unitFileText, new TextContentAdapter(), provider, null, null);
    adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
    adapter.setProposalPopupFocus();
    adapter.setPopupSize(new Point(200, 200));
    adapter.addContentProposalListener(arg0 -> {
      this.unitFileText.setText(arg0.getContent());
      adapter.getControlContentAdapter().setCursorPosition(this.unitFileText, SWT.END);
    });
  }

  /**
   * ICDM-2028 validate unit if it is present , atleast one of the limit/reference value fields should be entered
   */
  protected void validateUnit() {
    CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
    boolean unitValid = RuleDetailsSection.this.wizardPge.validateUnit(selCompObj);
    if (unitValid) {
      // if the unit is valid
      this.unitDecorator.hide();
      this.useUnitBtn.setEnabled(true);
    }
    else {
      // if the unit is not valid
      this.unitDecorator.show();
      this.useUnitBtn.setEnabled(false);
      this.useUnitBtn.setSelection(false);
      selCompObj.setUseNewUnit(false);
    }

  }


  /**
   * @return the refVaImpValue
   */
  public Button getRefVaImpValue() {
    return this.refVaImpValue;
  }

  /**
   * Create revert button
   *
   * @param btnComp comp
   */
  private void addDoneButton(final Composite btnComp) {
    this.doneBtn = new Button(btnComp, SWT.CHECK);
    GridData gridData = new GridData();
    gridData.horizontalSpan = 5;
    this.doneBtn.setLayoutData(gridData);
    this.doneBtn.setText("Done. The rule will be modified with above changes.");
    this.doneBtn.setFont(new Font(Display.getCurrent(), new FontData("Verdana", 12, SWT.BOLD)));
    this.doneBtn.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        if (RuleDetailsSection.this.multiUpdate) {
          boolean selection = RuleDetailsSection.this.doneBtn.getSelection();
          if (selection) {
            // do the operations only if the done button is selected
            String message = null;
            try {
              message = RuleDetailsSection.this.wizardPge.getMultiCompObj().validateAndUpdateRules();
            }
            catch (DataException e) {
              CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
            RuleDetailsSection.this.wizardPge.getCompareGridTableViewer().refresh();
            MessageDialogUtils.getInfoMessageDialog("Multiple Rules Update", message);
          }
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          if (RuleDetailsSection.this.wizardPge.validateUnit(selCompObj)) {// ICDM-2028
            boolean selection = RuleDetailsSection.this.doneBtn.getSelection();
            selCompObj.setUpdateInDB(selection);
            RuleDetailsSection.this.wizardPge.getCompareGridTableViewer().update(selCompObj, null);
          }
          else {
            RuleDetailsSection.this.doneBtn.setSelection(false);
          }
        }
      }
    });
  }

  /**
   * Add maturity level combo
   *
   * @param detailsComp comp
   */
  private void addMaturityLevel(final Composite detailsComp) {
    new Label(detailsComp, SWT.NONE).setText("Maturity level");
    this.icdmMaturityText = createTextField(detailsComp, false);
    this.maturityLevel = new Combo(detailsComp, SWT.READ_ONLY);
    this.maturityLevel.setLayoutData(GridDataUtil.getInstance().getGridData());

    final GridData descData = new GridData(SWT.NONE, SWT.NONE, false, false);
    descData.widthHint = 182;

    this.maturityLevel.setLayoutData(descData);
    this.maturityLevel.add(RuleMaturityLevel.NONE.getICDMMaturityLevel());
    this.maturityLevel.add(RuleMaturityLevel.START.getICDMMaturityLevel());
    this.maturityLevel.add(RuleMaturityLevel.STANDARD.getICDMMaturityLevel());
    this.maturityLevel.add(RuleMaturityLevel.FIXED.getICDMMaturityLevel());
    this.maturityLevel.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        String maturityLvl =
            RuleDetailsSection.this.maturityLevel.getItem(RuleDetailsSection.this.maturityLevel.getSelectionIndex());
        if (RuleDetailsSection.this.multiUpdate) {
          // multiple update
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setNewMaturityLevel(maturityLvl);
        }
        else {
          RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule()
              .setMaturityLevel(RuleMaturityLevel.getSsdMaturityLevelText(maturityLvl));
        }
      }
    });
    this.useImpValueMaturityLvl = createButton(detailsComp);
    this.useImpValueMaturityLvl.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        boolean selection = RuleDetailsSection.this.useImpValueMaturityLvl.getSelection();
        if (RuleDetailsSection.this.multiUpdate) {
          RuleDetailsSection.this.wizardPge.getMultiCompObj().setUseNewMaturityLvl(selection);
        }
        else {
          CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
          selCompObj.setUseNewMaturityLvl(selection);
        }
      }
    });
  }

  /**
   * Create exatch match feilds
   *
   * @param detailsComp comp
   */
  private void addExactMatch(final Composite detailsComp) {
    new Label(detailsComp, SWT.NONE).setText("Exact Match");
    this.exactMtch = new Button(detailsComp, SWT.CHECK);
    this.exactMtch.setLayoutData(new GridData());
    this.exactMtch.addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        exactMatchChange(true);
      }

    });
    this.exactMatchDecorator = new ControlDecoration(this.exactMtch, SWT.RIGHT | SWT.TOP);
    Image image =
        FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION).getImage();
    this.exactMatchDecorator.setImage(image);
  }


  /**
   * perform these changes when exact match flag is changed
   *
   * @param changeInUI true if the change is from UI
   */
  private void exactMatchChange(final boolean changeInUI) {
    boolean exactMatch = RuleDetailsSection.this.exactMtch.getSelection();
    if (exactMatch) {
      // deselect the check boxes
      setValuesToLowUpFields(false);
    }
    // enable or disable upper and lower limits based on exact match
    enableDisableLowUpLmtFields(!exactMatch, changeInUI);
    if (this.multiUpdate) {
      // ICDM-1999
      RuleDetailsSection.this.wizardPge.getMultiCompObj().setExactMtch(exactMatch);
    }
    else {
      RuleDetailsSection.this.wizardPge.getSelCompObj().getNewRule().setDcm2ssd(exactMatch);
      RuleDetailsSection.this.wizardPge.getSelCompObj().setNewExactMatch(true);// to indicate that the exact match is
                                                                               // modified
    }
  }

  /**
   * @param value
   */
  private void setValuesToLowUpFields(final boolean value) {
    RuleDetailsSection.this.lowLimitImpValue.setSelection(value);
    RuleDetailsSection.this.upperLimitImpValue.setSelection(value);
  }

  /**
   * @param enable
   * @param changeInUI
   */
  private void enableDisableLowUpLmtFields(final boolean enable, final boolean changeInUI) {
    if (!(CommonUtils.isEqual(RuleDetailsSection.this.wizardPge.getSelCompObj().getParamType(),
        ParameterType.ASCII.getText()))) {// ICDM-2028
      // if the parameter type is not ASCII
      RuleDetailsSection.this.lowerImpFileText.setEnabled(enable);
      RuleDetailsSection.this.upperImpFileText.setEnabled(enable);
      if (changeInUI) {// if the changes are done from UI
        RuleDetailsSection.this.lowLimitImpValue.setEnabled(enable);
        RuleDetailsSection.this.upperLimitImpValue.setEnabled(enable);
      }
      else {
        RuleDetailsSection.this.lowLimitImpValue.setEnabled(enable && this.lowLimitImpValue.getEnabled());// ICDM-2028
        RuleDetailsSection.this.upperLimitImpValue.setEnabled(enable && this.upperLimitImpValue.getEnabled());
      }
    }
  }

  /**
   * Set the rule details
   */
  public void populateRuleDetails() {

    // ICDM-1999
    this.multiUpdate = false;
    // enable all fields
    enableDisableFields(true);
    // remove the <DIFF VALUES> string from combo
    if (this.maturityLevel.indexOf(MultiCalDataImportCompObj.DIFFERENT_VALUES) != -1) {
      this.maturityLevel.remove(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    if (this.rvwMtdCombo.indexOf(MultiCalDataImportCompObj.DIFFERENT_VALUES) != -1) {
      this.rvwMtdCombo.remove(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }

    CalDataImportComparisonModel selCompObj = this.wizardPge.getSelCompObj();
    ReviewRule newRule = selCompObj.getNewRule();
    ReviewRule oldRule = selCompObj.getOldRule();

    // upper limit not set at this point
    this.upperLimitSet = false;

    this.exactMtch.setSelection(newRule.isDcm2ssd());

    setValuesForIcdmFields(oldRule);

    // setting values from imported file
    this.unitFileText.setText(newRule.getUnit() == null ? "" : newRule.getUnit());
    this.lowerImpFileText.setText(newRule.getLowerLimit() == null ? "" : newRule.getLowerLimit().toString());
    this.upperImpFileText.setText(newRule.getUpperLimit() == null ? "" : newRule.getUpperLimit().toString());
    // upper limit set at this point
    this.upperLimitSet = true;
    this.refVaImpFileText.setText(newRule.getRefValueDispString());
    // if the value type is not "VALUE" , disable editing
    this.refVaImpFileText.setEnabled(ParameterType.VALUE.getText().equals(selCompObj.getParamType()));
    this.maturityLevel
        .setText(RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(newRule.getMaturityLevel()).getICDMMaturityLevel());
    this.rvwMtdCombo.setText(READY_FOR_SERIES.getType(newRule.getReviewMethod()).getUiType());
    this.remarkTxtArea.setText(newRule.getHint() == null ? "" : newRule.getHint());

    // set the check box values

    // get the Cal check box take over Value from the Home page ane over take the Values
    CalDataFileImpWizardPage calDataFileImpWizardPage = (CalDataFileImpWizardPage) RuleDetailsSection.this.wizardPge
        .getImportWizard().getPage("Import Calibration Data 1");
    if (calDataFileImpWizardPage.getBtnOverTakeValues().getSelection()) {
      boolean enableImportValues = calDataFileImpWizardPage.getBtnOverTakeValues().getSelection();
      this.useUnitBtn.setSelection(enableImportValues);
      this.lowLimitImpValue.setSelection(enableImportValues);
      this.upperLimitImpValue.setSelection(enableImportValues);
      this.refVaImpValue.setSelection(enableImportValues);
      this.useImpValueMaturityLvl.setSelection(enableImportValues);
      this.useImpValueRvwMtd.setSelection(enableImportValues);
    }
    else {
      this.useUnitBtn.setSelection(selCompObj.isUseNewUnit());
      this.lowLimitImpValue.setSelection(selCompObj.isUseNewLowLmt());
      this.upperLimitImpValue.setSelection(selCompObj.isUseNewUpLmt());
      this.refVaImpValue.setSelection(selCompObj.isUseNewRefVal());
      this.useImpValueMaturityLvl.setSelection(selCompObj.isUseNewMaturityLvl());
      this.useImpValueRvwMtd.setSelection(selCompObj.isUseNewRvwMtd());
    }

    // validations for upper and lower limits
    lowerLimitValidation();
    upperLmtValidation();
    // validate unit
    validateUnit();// ICDM-2028
    // lower and upper limit fields have to be changed based on exact match flag
    // making this at the end to make this enabling disabling as final
    exactMatchChange(false);
    // ICDM-1930
    setFieldsForBitwiseRule(selCompObj);
    this.doneBtn.setSelection(selCompObj.isUpdateInDB());
    // remarks info decorator to be shown or hidden
    if (selCompObj.isInfoDecorNeeded()) {
      this.remarkDecorator.show();
    }
    else {
      this.remarkDecorator.hide();
    }
    // set error message to none
    this.wizardPge.setErrorMessage(null);
  }

  /**
   * @param oldRule
   */
  private void setValuesForIcdmFields(final ReviewRule oldRule) {
    if (null == oldRule) {
      // set every field to empty
      this.unitICDMText.setText("");
      this.lowerLmtICDM.setText("");
      this.upperICDMText.setText("");
      this.refValICDMText.setText("");
      this.icdmMaturityText.setText("");
      this.rvwMtdIcdmText.setText("");
    }
    else {
      // set icdm values if old rule exists
      this.unitICDMText.setText(CommonUtils.checkNull(oldRule.getUnit()));
      this.lowerLmtICDM.setText(oldRule.getLowerLimit() == null ? "" : oldRule.getLowerLimit().toString());
      this.upperICDMText.setText(oldRule.getUpperLimit() == null ? "" : oldRule.getUpperLimit().toString());
      this.refValICDMText.setText(oldRule.getRefValueDispString());
      // if maturity level is null , display empty string
      if (null == oldRule.getMaturityLevel()) {
        this.icdmMaturityText.setText("");
      }
      else {
        this.icdmMaturityText.setText(RuleMaturityLevel.getIcdmMaturityLevelText(oldRule.getMaturityLevel()));
      }
      this.rvwMtdIcdmText.setText(READY_FOR_SERIES.getType(oldRule.getReviewMethod()).getUiType());
      this.bitWiseICDMText.setText(CommonUtils.checkNull(oldRule.getBitWiseRule()));
    }
  }

  /**
   * ICDM-1930 disable fields if they are bitwise
   *
   * @param selCompObj CalDataImportComparison
   */
  private void setFieldsForBitwiseRule(final CalDataImportComparisonModel selCompObj) {
    String paramName = selCompObj.getParamName();
    Map<String, String> properties =
        this.wizardPge.getImportWizard().getWizardData().getNewParamPropsMap().get(paramName);
    this.isBitwiseRule = CommonUtils.isEqual(ApicConstants.CODE_WORD_YES, properties.get(CDRConstants.CDIKEY_BIT_WISE));
    if (this.isBitwiseRule ||
        CommonUtils.isEqual(this.wizardPge.getSelCompObj().getParamType(), ParameterType.ASCII.getText())) {// ICDM-2028
      if (this.isBitwiseRule) {
        // disable exact match field, only in case of bitwise rules
        this.exactMtch.setEnabled(false);
        this.exactMatchDecorator
            .setDescriptionText("Exact Match cannot be edited in rule import wizard for Bitwise rules!");
        this.exactMatchDecorator.show();
      }
      // disable lower limit
      this.lowerImpFileText.setEnabled(false);
      this.lowLimitImpValue.setEnabled(false);
      this.lowLimitImpValue.setSelection(false);
      // disable upper limit
      this.upperImpFileText.setEnabled(false);
      this.upperLimitImpValue.setEnabled(false);
      this.upperLimitImpValue.setSelection(false);
    }
    else {
      // enable exact match field
      boolean exactMatchEditable = selCompObj.isExactMatchEditable();
      this.exactMtch.setEnabled(exactMatchEditable);
      if (exactMatchEditable) {
        this.exactMatchDecorator.hide();
      }
      else {
        this.exactMatchDecorator.setDescriptionText("The Exact Match is disabled because of no Reference Value!");
        this.exactMatchDecorator.show();
      }
    }
  }

  /**
   * @return return CalData object from text
   */
  public CalData getCalDataFromTextField() {
    CalDataImportComparisonModel selCompObj = RuleDetailsSection.this.wizardPge.getSelCompObj();
    String unit;
    // get unit text based on the use new value check box
    if (selCompObj.isUseNewUnit()) {
      unit = RuleDetailsSection.this.unitFileText.getText();
    }
    else {
      unit = RuleDetailsSection.this.unitICDMText.getText();
    }
    String dcmStr = com.bosch.caltool.icdm.common.util.CalDataUtil.createDCMStringForText(selCompObj.getParamName(),
        unit, this.refVaImpFileText.getText());
    return CalDataUtil.dcmToCalDataExt(dcmStr, selCompObj.getParamName(), CDMLogger.getInstance());
  }

  /**
   * clears all the values and disables all the fields
   */
  public void disableFields() {
    this.multiUpdate = true;
    // clear all fields
    this.exactMtch.setSelection(false);

    this.unitICDMText.setText("");
    this.lowerLmtICDM.setText("");
    this.upperICDMText.setText("");
    this.refValICDMText.setText("");
    this.icdmMaturityText.setText("");
    this.rvwMtdIcdmText.setText("");

    this.unitFileText.setText("");
    this.lowerImpFileText.setText("");
    this.upperImpFileText.setText("");
    this.refVaImpFileText.setText("");
    this.maturityLevel.setText(RuleMaturityLevel.NONE.getICDMMaturityLevel());
    this.rvwMtdCombo.setText(READY_FOR_SERIES.NO.getUiType());
    this.remarkTxtArea.setText("");
    this.doneBtn.setSelection(false);


    this.useUnitBtn.setSelection(false);
    this.lowLimitImpValue.setSelection(false);
    this.upperLimitImpValue.setSelection(false);
    this.refVaImpValue.setSelection(false);
    this.useImpValueMaturityLvl.setSelection(false);
    this.useImpValueRvwMtd.setSelection(false);
    // hide all the decorators
    this.exactMatchDecorator.hide();
    this.remarkDecorator.hide();
    this.unitDecorator.hide();
    this.lowerLimitDecor.hide();
    this.upperLimitDecor.hide();

    // disable all fields
    enableDisableFields(false);
  }

  /**
   * @param enable if true, enable field
   */
  private void enableDisableFields(final boolean enable) {
    this.exactMtch.setEnabled(enable);
    this.unitFileText.setEnabled(enable);
    this.lowerImpFileText.setEnabled(enable);
    this.upperImpFileText.setEnabled(enable);
    this.refVaImpFileText.setEnabled(enable);
    this.maturityLevel.setEnabled(enable);
    this.rvwMtdCombo.setEnabled(enable);
    this.remarkTxtArea.setEnabled(enable);
    this.doneBtn.setEnabled(enable);


    this.useUnitBtn.setEnabled(enable);
    this.lowLimitImpValue.setEnabled(enable);
    this.upperLimitImpValue.setEnabled(enable);
    this.refVaImpValue.setEnabled(enable);
    this.useImpValueMaturityLvl.setEnabled(enable);
    this.useImpValueRvwMtd.setEnabled(enable);
  }

  /**
   * ICDM-1999 populate rule details section with
   */
  public void populateMultiRuleDetails() {
    this.multiUpdate = true;
    // enable all fields
    enableDisableFields(true);
    MultiCalDataImportCompObj selCompObj = this.wizardPge.getMultiCompObj();

    // upper limit not set at this point
    this.upperLimitSet = false;

    this.exactMtch.setSelection(selCompObj.isExactMatch());

    // set icdm values if old rule exists
    this.unitICDMText.setText(CommonUtils.checkNull(selCompObj.getOldUnit()));
    this.lowerLmtICDM.setText(CommonUtils.checkNull(selCompObj.getOldLowerLimit()));
    this.upperICDMText.setText(CommonUtils.checkNull(selCompObj.getOldUpperLimit()));
    this.refValICDMText.setText(CommonUtils.checkNull(selCompObj.getOldRefValue()));
    // if maturity level is null , display empty string
    if (CommonUtils.isEqual(selCompObj.getOldMaturityLevel(), MultiCalDataImportCompObj.DIFFERENT_VALUES)) {
      this.icdmMaturityText.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    else {
      this.icdmMaturityText.setText(RuleMaturityLevel.getIcdmMaturityLevelText(selCompObj.getOldMaturityLevel()));
    }

    this.rvwMtdIcdmText.setText(READY_FOR_SERIES.getType(selCompObj.getOldReviewMethod()).getUiType());

    this.bitWiseICDMText.setText(CommonUtils.checkNull(selCompObj.getBitWiseRule()));

    // setting values from imported file
    this.unitFileText.setText(CommonUtils.checkNull(selCompObj.getNewUnit()));
    this.lowerImpFileText.setText(selCompObj.getNewLowerLimit());
    this.upperImpFileText.setText(selCompObj.getNewUpperLimit());
    // upper limit set at this point
    this.upperLimitSet = true;
    this.refVaImpFileText.setText(CommonUtils.checkNull(selCompObj.getNewRefValue()));
    // if the value type is not "VALUE" , disable editing
    this.refVaImpFileText.setEnabled(ParameterType.VALUE.getText().equals(selCompObj.getParamType()));
    // maturity level
    if (CommonUtils.isEqual(selCompObj.getNewMaturityLevel(), MultiCalDataImportCompObj.DIFFERENT_VALUES)) {
      this.maturityLevel.add(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      this.maturityLevel.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    else {
      if (this.maturityLevel.indexOf(MultiCalDataImportCompObj.DIFFERENT_VALUES) != -1) {
        this.maturityLevel.remove(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      }
      this.maturityLevel
          .setText(RuleMaturityLevel.getIcdmMaturityLvlEnumForSsdText(selCompObj.getNewMaturityLevel()).getICDMMaturityLevel());
    }
    // ready for series
    if (CommonUtils.isEqual(selCompObj.getNewReviewMethod(), MultiCalDataImportCompObj.DIFFERENT_VALUES)) {
      this.rvwMtdCombo.add(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      this.rvwMtdCombo.setText(MultiCalDataImportCompObj.DIFFERENT_VALUES);
    }
    else {
      if (this.rvwMtdCombo.indexOf(MultiCalDataImportCompObj.DIFFERENT_VALUES) != -1) {
        this.rvwMtdCombo.remove(MultiCalDataImportCompObj.DIFFERENT_VALUES);
      }
      this.rvwMtdCombo.setText(READY_FOR_SERIES.getType(selCompObj.getNewReviewMethod()).getUiType());
    }

    this.remarkTxtArea.setText(selCompObj.getRemarks());

    // set the check box values
    this.useUnitBtn.setSelection(false);
    this.lowLimitImpValue.setSelection(false);
    this.upperLimitImpValue.setSelection(false);
    this.refVaImpValue.setSelection(false);
    this.useImpValueMaturityLvl.setSelection(false);
    this.useImpValueRvwMtd.setSelection(false);

    // lower and upper limit fields have to be changed based on exact match flag
    // making this at the end to make this enabling disabling as final
    exactMatchChange(false);
    this.doneBtn.setSelection(false);
    // remarks info decorator to be hidden
    this.remarkDecorator.hide();
    // set error message to none
    this.wizardPge.setErrorMessage(null);


  }


  /**
   * @return the useImpValueMaturityLvl
   */
  public Button getUseImpValueMaturityLvl() {
    return this.useImpValueMaturityLvl;
  }


  /**
   * @return the useImpValueRvwMtd
   */
  public Button getUseImpValueRvwMtd() {
    return this.useImpValueRvwMtd;
  }


  /**
   * @return the useUnitBtn
   */
  public Button getUseUnitBtn() {
    return this.useUnitBtn;
  }

  /**
   * @return the units
   */
  public SortedSet<String> getUnits() {
    return this.units;
  }

  /**
   * @param units the units to set
   */
  public void setUnits(final SortedSet<String> units) {
    this.units = units;
  }
}
