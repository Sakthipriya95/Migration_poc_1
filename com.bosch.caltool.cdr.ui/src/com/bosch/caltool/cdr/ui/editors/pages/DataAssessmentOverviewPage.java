/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.apic.ui.dialogs.PasswordDialog;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssessmentSharePointUploadBO;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.ws.rest.client.cdr.DaDataAssessmentServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;

/**
 * @author ajk2cob
 */
public class DataAssessmentOverviewPage extends FormPage {

  /**
  *
  */
  private static final String SHARE_POINT_UPLOAD_CONFIRM_MSG =
      "Do you want to upload generated Data Assessment files to SharePoint URL configued in Attribute - ";
  /**
  *
  */
  private static final String SHARE_POINT_UPLOAD = "SharePoint Upload";

  /**
   * Editor instance
   */
  private final DataAssessmentReportEditor editor;
  /**
   * Non scrollable form
   */
  private ScrolledForm scrollableForm;
  /**
   * FormToolkit
   */
  private FormToolkit formToolkit;
  /**
   * Project state composite
   */
  private Composite projectStateComposite;
  /**
   * Baseline composite
   */
  private Composite baselineComposite;
  /**
   * Project state section
   */
  private Section projectStateSection;
  /**
   * Baseline section
   */
  private Section baselineSection;
  /**
   * Project state form
   */
  private Form projectStateForm;
  /**
   * Data assessment report baseline form
   */
  private Form baselineForm;
  /**
   * Data assessment report handler
   */
  private final DataAssmntReportDataHandler dataHandler;
  /**
   * Data assessment report
   */
  private DataAssessmentReport dataAssessmentReport;
  /**
   * Total Count of parameters/labels in RB responsibility
   */
  private final int daRbRespParamTotalCount;
  /**
   * Flag to indicate if ready for series
   */
  private final boolean isReadyForSeries;
  /**
   * Series release radio button
   */
  private Button seriesRelAssessmentOption;
  /**
   * Development radio button
   */
  private Button devAssessmentOption;
  /**
   * Baseline name text field
   */
  private Text baselineNameTextField;
  /**
   * Baseline remarks text field
   */
  private Text baselineRemarksTextField;
  /**
   * Instance of create baseline button
   */
  private Button createBaselineBtn;
  /**
   * One baseline creation per data assessment report
   */
  private boolean isBaselineCreated;
  /**
   * String to hold the baseline name
   */
  private String baselineName;
  /**
   * String to hold the description of baselines
   */
  private String remarks;
  /**
   * String to hold type of assessment selected
   */
  private String typeOfAssessment;

  /**
   * Flag to decide if SharePointFile Upload is needed
   */
  private boolean uploadFilesToSharePoint;

  private static final String NAVIGATION_LINK_TEXT = "<a>Click here to see details.</a>";

  /**
   * Data assessment overview page, Constructor.
   *
   * @param editor editor
   */
  public DataAssessmentOverviewPage(final FormEditor editor) {
    super(editor, "dataAssessmentOverview", "Overview");
    this.editor = (DataAssessmentReportEditor) editor;
    this.dataHandler = this.editor.getEditorInput().getDataAssmntReportDataHandler();
    // Get DataAssessmentReport Data(common model) from data handler
    this.dataAssessmentReport = this.dataHandler.getDataAssessmentReport();
    this.daRbRespParamTotalCount = CommonUtils.isNotNull(this.dataAssessmentReport.getDataAssmntCompHexData())
        ? this.dataAssessmentReport.getDataAssmntCompHexData().getDaRbRespParamTotalCount() : 0;
    this.isReadyForSeries = this.dataAssessmentReport.isReadyForSeries();
  }

  @Override
  public void createPartControl(final Composite parent) {

    ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    // create an scrollable form on which widgets are built
    this.scrollableForm = this.editor.getToolkit().createScrolledForm(parent);
    this.scrollableForm.setText("Data Assessment Report Overview");


    if (this.dataAssessmentReport.getConsiderRvwsOfPrevPidcVers()) {
      this.scrollableForm
          .setText(this.editor.getEditorInput().getName() + " \nConsidered Reviews of previous PIDC Versions");
    }
    else {
      this.scrollableForm
          .setText(this.editor.getEditorInput().getName() + " \nNot Considered Reviews of previous PIDC Versions");
    }


    // instead of editor.getToolkit().createScrolledForm(parent); in superclass
    // formToolkit is obtained from managed form to create form within projectStateSection
    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);
    scrolledComposite.setContent(this.scrollableForm);
    scrolledComposite.setExpandHorizontal(true);
    scrolledComposite.setExpandVertical(true);
    scrolledComposite.setMinSize(this.scrollableForm.computeSize(SWT.DEFAULT, SWT.DEFAULT));
  }

  @Override
  public Control getPartControl() {
    return this.scrollableForm;
  }

  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    this.formToolkit = managedForm.getToolkit();
    createProjectStateComposite();
  }

  /**
   * This method initializes project state composite
   */
  private void createProjectStateComposite() {
    this.projectStateComposite = this.scrollableForm.getBody();
    this.projectStateComposite.setLayout(new GridLayout());
    createProjectStateSection();
    this.projectStateComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes project state section
   */
  private void createProjectStateSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    gridData.grabExcessVerticalSpace = false;
    this.projectStateSection =
        this.formToolkit.createSection(this.projectStateComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.projectStateSection.setExpanded(true);
    this.projectStateSection.setText("Project State");
    this.projectStateSection.setDescription("The below requirements must be met to be ready for series");
    this.projectStateSection.getDescriptionControl().setEnabled(false);

    createProjectStateForm();
    this.projectStateSection.setLayoutData(gridData);
    this.projectStateSection.setClient(this.projectStateForm);
  }

  /**
   * This method initializes project state form
   */
  private void createProjectStateForm() {
    final GridLayout projectStateFormGridLayout = new GridLayout();
    // 2 columns for the layout
    projectStateFormGridLayout.numColumns = 2;
    projectStateFormGridLayout.makeColumnsEqualWidth = false;
    // create a form
    this.projectStateForm = this.formToolkit.createForm(this.projectStateSection);
    this.projectStateForm.getBody().setLayout(projectStateFormGridLayout);

    // HEX file valid label
    Label isHexValidImgLabel = new Label(this.projectStateForm.getBody(), SWT.IMAGE_PNG);
    isHexValidImgLabel.setImage(
        ImageManager.getInstance().getRegisteredImage(this.dataAssessmentReport.isHexFileDataEqualWithDataReviews()
            ? ImageKeys.DATA_ASSESSMENT_CHECK : ImageKeys.DATA_ASSESSMENT_TIMES));
    isHexValidImgLabel.setLayoutData(createGridDataForImageLbl());

    Label isHexValidLabel = this.formToolkit.createLabel(this.projectStateForm.getBody(),
        "HEX file is equal to Reviewed data in RB responsibility.");
    isHexValidLabel.setFont(
        new Font(isHexValidLabel.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 12, SWT.LINE_SOLID)));
    isHexValidLabel.setLayoutData(leftAlignGridData());

    String hexMessage = this.dataAssessmentReport.isHexFileDataEqualWithDataReviews() ? ""
        : this.dataAssessmentReport.getHexDataReviewNotEqualCount() + " parameters of " + this.daRbRespParamTotalCount +
            " are not equal. ";
    Link isHexValidLink = new Link(this.projectStateForm.getBody(), SWT.NONE);
    isHexValidLink.setText(hexMessage + NAVIGATION_LINK_TEXT);
    isHexValidLink.setLayoutData(createGridDataForLink());
    isHexValidLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // yet to be implemented

      }

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        DataAssessmentOverviewPage.this.editor
            .setActivePage(DataAssessmentOverviewPage.this.editor.getCompareHexRvwResultsPage().getIndex());
      }
    });

    // parameters valid label
    Label isParametersValidImgLabel = new Label(this.projectStateForm.getBody(), SWT.IMAGE_PNG);
    isParametersValidImgLabel
        .setImage(ImageManager.getInstance().getRegisteredImage(this.dataAssessmentReport.isAllParametersReviewed()
            ? ImageKeys.DATA_ASSESSMENT_CHECK : ImageKeys.DATA_ASSESSMENT_TIMES));
    isParametersValidImgLabel.setLayoutData(createGridDataForImageLbl());

    Label isParametersValidLabel = this.formToolkit.createLabel(this.projectStateForm.getBody(),
        "All parameters in RB responsibility are reviewed.");
    isParametersValidLabel.setFont(new Font(isParametersValidLabel.getDisplay(),
        new FontData(GUIHelper.DEFAULT_FONT.toString(), 12, SWT.LINE_SOLID)));
    isParametersValidLabel.setLayoutData(leftAlignGridData());

    String parametersMessage =
        this.dataAssessmentReport.isAllParametersReviewed() ? "" : this.dataAssessmentReport.getRbParamsNotRvdCount() +
            " parameters of " + this.daRbRespParamTotalCount + " are not reviewed. ";
    Link isParametersValidLink = new Link(this.projectStateForm.getBody(), SWT.NONE);
    isParametersValidLink.setText(parametersMessage + NAVIGATION_LINK_TEXT);
    isParametersValidLink.setLayoutData(createGridDataForLink());
    isParametersValidLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // yet to be implemented

      }

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        DataAssessmentOverviewPage.this.editor
            .setActivePage(DataAssessmentOverviewPage.this.editor.getCompareHexRvwResultsPage().getIndex());
      }
    });

    // is questionnaires answered label
    Label isQniareValidImgLabel = new Label(this.projectStateForm.getBody(), SWT.IMAGE_PNG);
    isQniareValidImgLabel
        .setImage(ImageManager.getInstance().getRegisteredImage(this.dataAssessmentReport.isAllQnairesAnswered()
            ? ImageKeys.DATA_ASSESSMENT_CHECK : ImageKeys.DATA_ASSESSMENT_TIMES));
    isQniareValidImgLabel.setLayoutData(createGridDataForImageLbl());

    Label isQniareValidLabel = this.formToolkit.createLabel(this.projectStateForm.getBody(),
        "All questionnaires in RB responsibility are answered.");
    isQniareValidLabel.setFont(
        new Font(isQniareValidLabel.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 12, SWT.LINE_SOLID)));
    isQniareValidLabel.setLayoutData(leftAlignGridData());

    String questionnaireMessage =
        this.dataAssessmentReport.isAllQnairesAnswered() ? "" : this.dataAssessmentReport.getQnairesNotAnsweredCount() +
            " questionnaires of " + this.dataAssessmentReport.getQnairesRbRespTotalCount() + " are not complete. ";
    Link isQniareValidLink = new Link(this.projectStateForm.getBody(), SWT.NONE);
    isQniareValidLink.setText(questionnaireMessage + NAVIGATION_LINK_TEXT);
    isQniareValidLink.setLayoutData(createGridDataForLink());
    isQniareValidLink.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // yet to be implemented

      }

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        DataAssessmentOverviewPage.this.editor
            .setActivePage(DataAssessmentOverviewPage.this.editor.getQuestionnaireResultsPage().getIndex());
      }
    });

    // series release label
    Label seriesRelImgLabel = new Label(this.projectStateForm.getBody(), SWT.IMAGE_PNG);
    seriesRelImgLabel.setImage(ImageManager.getInstance()
        .getRegisteredImage(this.isReadyForSeries ? ImageKeys.DATA_ASSESSMENT_CHECK : ImageKeys.DATA_ASSESSMENT_TIMES));
    seriesRelImgLabel.setLayoutData(createGridDataForImageLbl());

    String seriesRelLabelMessage =
        this.isReadyForSeries ? "Ready for series. The baseline for series release can be created."
            : "Not ready for series. The baseline for series release cannot be created.";
    Label seriesRelLabel = this.formToolkit.createLabel(this.projectStateForm.getBody(), seriesRelLabelMessage);
    seriesRelLabel.setFont(
        new Font(seriesRelLabel.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 12, SWT.LINE_SOLID)));
    seriesRelLabel.setLayoutData(leftAlignGridData());

    Label seriesRelInfoLabel = this.formToolkit.createLabel(this.projectStateForm.getBody(),
        "All three requirements must be met in order to be ready for the series.");
    seriesRelInfoLabel.setLayoutData(leftAlignGridData());

    createBaselineComposite();
  }

  /**
   * @return
   */
  private GridData createGridDataForImageLbl() {
    final GridData requirementsImgLabelGridData = GridDataUtil.getInstance().createGridData();
    requirementsImgLabelGridData.grabExcessHorizontalSpace = false;
    requirementsImgLabelGridData.grabExcessVerticalSpace = false;
    requirementsImgLabelGridData.verticalSpan = 2;
    return requirementsImgLabelGridData;
  }

  /**
   * @return
   */
  private GridData createGridDataForLink() {
    final GridData wpRespRvwdLinkLabelGridData = GridDataUtil.getInstance().createGridData();
    wpRespRvwdLinkLabelGridData.grabExcessHorizontalSpace = false;
    wpRespRvwdLinkLabelGridData.grabExcessVerticalSpace = false;
    wpRespRvwdLinkLabelGridData.horizontalAlignment = GridData.BEGINNING;
    wpRespRvwdLinkLabelGridData.verticalAlignment = GridData.BEGINNING;
    return wpRespRvwdLinkLabelGridData;
  }

  /**
   * This method initializes data assessment report baseline composite
   */
  private void createBaselineComposite() {
    this.baselineComposite = this.scrollableForm.getBody();
    this.baselineComposite.setLayout(new GridLayout());
    createBaselineSection();
    this.baselineComposite.setLayoutData(GridDataUtil.getInstance().getGridData());
  }

  /**
   * This method initializes data assessment report baseline section
   */
  private void createBaselineSection() {
    final GridData gridData = GridDataUtil.getInstance().getGridData();
    this.baselineSection = this.formToolkit.createSection(this.baselineComposite, ExpandableComposite.TITLE_BAR);
    this.baselineSection.setExpanded(true);
    this.baselineSection.setText("Create a Baseline for Data Assessment Report");
    createBaselineForm();
    this.baselineSection.setLayoutData(gridData);
    this.baselineSection.setClient(this.baselineForm);
  }

  /**
   * This method initializes baseline form
   */
  private void createBaselineForm() {
    final GridLayout baselineFormGridLayout = new GridLayout();
    // 1 columns for the layout
    baselineFormGridLayout.numColumns = 1;
    // create a form
    this.baselineForm = this.formToolkit.createForm(this.baselineSection);
    this.baselineForm.getBody().setLayout(baselineFormGridLayout);

    final GridData assessmentTypeLabelGridData = GridDataUtil.getInstance().createGridData();
    assessmentTypeLabelGridData.horizontalAlignment = GridData.BEGINNING;
    assessmentTypeLabelGridData.grabExcessHorizontalSpace = false;
    assessmentTypeLabelGridData.grabExcessVerticalSpace = false;

    Label assessmentTypeLabel = this.formToolkit.createLabel(this.baselineForm.getBody(), "Type of Assessment");
    assessmentTypeLabel.setFont(new Font(assessmentTypeLabel.getDisplay(),
        new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID)));
    assessmentTypeLabel.setLayoutData(assessmentTypeLabelGridData);

    DaDataAssessment daDataAssessment = prePopulateBaselineForm();
    boolean isDevelopmentType = false;
    if (daDataAssessment != null) {
      isDevelopmentType =
          daDataAssessment.getTypeOfAssignment().equals(CDRConstants.TYPE_OF_ASSESSMENT.DEVELOPMENT.getDbType());
    }

    this.seriesRelAssessmentOption =
        this.formToolkit.createButton(this.baselineForm.getBody(), "Assessment for series release", SWT.RADIO);
    this.seriesRelAssessmentOption.setEnabled(this.isReadyForSeries && !this.editor.isBaseline());
    this.seriesRelAssessmentOption.setSelection(!this.editor.isBaseline() ? this.isReadyForSeries : !isDevelopmentType);
    this.seriesRelAssessmentOption.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // yet to be implemented

      }

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        DataAssessmentOverviewPage.this.typeOfAssessment =
            DataAssessmentOverviewPage.this.seriesRelAssessmentOption.getText().trim();
        checkCreateBaselineBtnEnable();
      }
    });

    this.devAssessmentOption =
        this.formToolkit.createButton(this.baselineForm.getBody(), "Assessment for development", SWT.RADIO);
    this.devAssessmentOption.setEnabled(!this.editor.isBaseline());
    this.devAssessmentOption.setSelection(!this.editor.isBaseline() ? !this.isReadyForSeries : isDevelopmentType);
    this.devAssessmentOption.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // yet to be implemented

      }

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        DataAssessmentOverviewPage.this.typeOfAssessment =
            DataAssessmentOverviewPage.this.devAssessmentOption.getText().trim();
        checkCreateBaselineBtnEnable();
      }
    });
    if (!this.editor.isBaseline()) {
      this.typeOfAssessment = this.isReadyForSeries ? this.seriesRelAssessmentOption.getText().trim()
          : this.devAssessmentOption.getText().trim();
    }

    Composite baselineNameLabelComposite = new Composite(this.baselineForm.getBody(), SWT.NONE);
    GridLayout baselineNameLabelGridLayout = new GridLayout();
    baselineNameLabelGridLayout.numColumns = 2;
    baselineNameLabelComposite.setLayout(baselineNameLabelGridLayout);

    final GridData baselineNameLabelGridData = GridDataUtil.getInstance().createGridData();
    baselineNameLabelGridData.horizontalAlignment = GridData.BEGINNING;
    baselineNameLabelGridData.grabExcessHorizontalSpace = false;
    baselineNameLabelGridData.grabExcessVerticalSpace = false;

    Label baselineNameLabel = this.formToolkit.createLabel(baselineNameLabelComposite, "Baseline Name");
    baselineNameLabel.setFont(
        new Font(baselineNameLabel.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID)));
    baselineNameLabel.setLayoutData(baselineNameLabelGridData);
    Label baselineNameSuffix =
        this.formToolkit.createLabel(baselineNameLabelComposite, !this.editor.isBaseline() ? "(mandatory)" : "");
    baselineNameSuffix.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_RED));

    final GridData baselineNameTextGridData = GridDataUtil.getInstance().getTextGridData();
    this.baselineNameTextField =
        this.formToolkit.createText(this.baselineForm.getBody(), null, SWT.SINGLE | SWT.BORDER);
    this.baselineNameTextField.setLayoutData(baselineNameTextGridData);
    this.baselineNameTextField.setEditable(!this.editor.isBaseline());
    if (this.editor.isBaseline() && (daDataAssessment != null)) {
      this.baselineNameTextField.setText(daDataAssessment.getBaselineName());
    }

    this.baselineNameTextField.addModifyListener(listener -> {
      DataAssessmentOverviewPage.this.baselineName = DataAssessmentOverviewPage.this.baselineNameTextField.getText();
      checkCreateBaselineBtnEnable();
    });

    // remarks label and input
    Composite reamarkLabelComposite = new Composite(this.baselineForm.getBody(), SWT.NONE);
    GridLayout reamarkLabelGridLayout = new GridLayout();
    reamarkLabelGridLayout.numColumns = 2;
    reamarkLabelComposite.setLayout(reamarkLabelGridLayout);

    final GridData reamarkLabelGridData = GridDataUtil.getInstance().createGridData();
    reamarkLabelGridData.horizontalAlignment = GridData.BEGINNING;
    reamarkLabelGridData.grabExcessHorizontalSpace = false;
    reamarkLabelGridData.grabExcessVerticalSpace = false;

    Label remarksLabel = this.formToolkit.createLabel(reamarkLabelComposite, "Remarks");
    remarksLabel.setFont(
        new Font(remarksLabel.getDisplay(), new FontData(GUIHelper.DEFAULT_FONT.toString(), 10, SWT.LINE_SOLID)));
    remarksLabel.setLayoutData(reamarkLabelGridData);
    Label remarksSuffix =
        this.formToolkit.createLabel(reamarkLabelComposite, !this.editor.isBaseline() ? "(optional)" : "");
    remarksSuffix.setForeground(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GREEN));

    GridData remarksTextareaGridData = GridDataUtil.getInstance().getTextGridData();
    remarksTextareaGridData.heightHint = 50;
    this.baselineRemarksTextField =
        this.formToolkit.createText(this.baselineForm.getBody(), null, SWT.MULTI | SWT.BORDER);
    this.baselineRemarksTextField.setLayoutData(remarksTextareaGridData);
    this.baselineRemarksTextField.setEditable(!this.editor.isBaseline());
    if (this.editor.isBaseline() && (daDataAssessment != null) && (daDataAssessment.getDescription() != null)) {
      this.baselineRemarksTextField.setText(daDataAssessment.getDescription());
    }

    this.baselineRemarksTextField.addModifyListener(listener -> DataAssessmentOverviewPage.this.remarks =
        DataAssessmentOverviewPage.this.baselineRemarksTextField.getText());

    if (!this.editor.isBaseline()) {
      createBaselineButton();
    }
  }

  /**
   * Pre-populate data assessment baseline form
   *
   * @return DaDataAssessment
   */
  private DaDataAssessment prePopulateBaselineForm() {
    if (this.editor.isBaseline() && CommonUtils.isNotNull(this.editor.getBaselineId()) &&
        CommonUtils.isNotEmpty(this.dataAssessmentReport.getDataAssmntBaselines())) {
      Optional<DaDataAssessment> daDataAssessment = this.dataAssessmentReport.getDataAssmntBaselines().stream()
          .filter(baseline -> baseline.getId().equals(this.editor.getBaselineId())).findFirst();
      return daDataAssessment.isPresent() ? daDataAssessment.get() : null;
    }
    return null;
  }

  /**
   * create baseline button
   */
  private void createBaselineButton() {
    // create baseline button
    GridData baselineBtnGridData = leftAlignGridData();
    baselineBtnGridData.widthHint = 150;
    this.createBaselineBtn = this.formToolkit.createButton(this.baselineForm.getBody(), "Create Baseline", SWT.NONE);
    this.createBaselineBtn.setLayoutData(baselineBtnGridData);
    this.createBaselineBtn.setToolTipText("Create Baseline");
    this.createBaselineBtn.setEnabled(false);

    this.createBaselineBtn.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetSelected(final SelectionEvent arg0) {
        callCreateBaselineService();
        DataAssessmentOverviewPage.this.createBaselineBtn.setEnabled(false);
        if (DataAssessmentOverviewPage.this.isBaselineCreated) {
          DataAssessmentOverviewPage.this.editor.getBaselinesPage().refreshUiData();
        }
      }

      @Override
      public void widgetDefaultSelected(final SelectionEvent arg0) {
        // Not yet Implemented

      }

    });
  }

  /**
   * Checks if the create baseline button should be enabled
   */
  private void checkCreateBaselineBtnEnable() {
    if (CommonUtils.isNotNull(this.createBaselineBtn)) {
      this.createBaselineBtn.setEnabled(validateTextFields() && !this.isBaselineCreated);
    }
  }

  /**
   * Validates the text fields before enabling the create baseline button
   *
   * @return boolean
   */
  private boolean validateTextFields() {
    boolean typeOfAssessmentValid = CommonUtils.isNotEmptyString(this.typeOfAssessment);
    boolean baselineNameValid = CommonUtils.isNotEmptyString(this.baselineName);
    return typeOfAssessmentValid && baselineNameValid;
  }

  /**
   * @return GridData instance for project state labels and create baseline button
   */
  private GridData leftAlignGridData() {
    final GridData gridData = new GridData();
    gridData.grabExcessHorizontalSpace = false;
    gridData.horizontalAlignment = GridData.BEGINNING;
    gridData.verticalAlignment = GridData.CENTER;
    gridData.grabExcessVerticalSpace = false;
    return gridData;
  }

  /**
   * Make a call to the create baseline service
   */
  private void callCreateBaselineService() {
    CommonDataBO commonBo = new CommonDataBO();
    CDMLogger.getInstance().debug("Creating Baseline - {}", this.baselineName);

    this.uploadFilesToSharePoint = false;
    DataAssessmentSharePointUploadBO sharePointBo =
        new DataAssessmentSharePointUploadBO(null, this.dataAssessmentReport.getPidcVersId(), false);

    isSharePointUploadEnabled(sharePointBo);

    ProgressMonitorDialog progressDialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
    try {
      progressDialog.run(true, true, monitor -> {

        try {
          monitor.beginTask("Creating Baseline...", 100);
          monitor.worked(20);
          // Setting values from Overview page to DataAssessmentReport model
          this.dataAssessmentReport.setBaselineName(this.baselineName);
          this.dataAssessmentReport.setDescription(this.remarks);
          this.dataAssessmentReport.setTypeOfAssignment(this.typeOfAssessment);

          // Service call to create Baseline in Data Assessment tables
          DaDataAssessmentServiceClient serviceClient = new DaDataAssessmentServiceClient();
          DaDataAssessment newlyCreatedBaseline = serviceClient.createBaseline(this.dataAssessmentReport);
          // Adding the newly created baseline to the list of existing baselines
          this.dataAssessmentReport.getDataAssmntBaselines().add(newlyCreatedBaseline);
          this.dataAssessmentReport.setBaselineCreatedDate(newlyCreatedBaseline.getCreatedDate());
          monitor.worked(100);
          monitor.done();

          CDMLogger.getInstance().infoDialog(
              commonBo.getMessage(CDRConstants.DATA_ASSESSMENT, CDRConstants.BASELINE_CREATION), Activator.PLUGIN_ID);

          DataAssessSharePointUploadInputModel inputModel = null;

          if (this.uploadFilesToSharePoint) {
            inputModel = sharePointBo.getDataAssessmentToSharePointInput();
          }

          // Making a service call to create baseline filesfinal Long dataAssessmentId, final Long pidcVersionId,
          // final String dataAssessmentType, final boolean isOverviewPage
          this.dataAssessmentReport =
              serviceClient.createBaselineFiles(this.dataAssessmentReport, newlyCreatedBaseline.getId(), inputModel);

          this.isBaselineCreated = true;

        }
        catch (ApicWebServiceException ex) {
          CDMLogger.getInstance().errorDialog(CommonUIConstants.EXCEPTION + ex.getMessage(), Activator.PLUGIN_ID);
        }
      });
    }
    catch (InvocationTargetException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    catch (InterruptedException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
      Thread.currentThread().interrupt();
    }


  }

  /**
   * @param util
   */
  private void isSharePointUploadEnabled(final DataAssessmentSharePointUploadBO util) {
    if (CDRConstants.TYPE_OF_ASSESSMENT.SERIES_RELEASE.getUiType().equals(this.typeOfAssessment)) {

      String sharePointUrlMsg = util.checkAndLoadPidcSharePointUrl();

      if (sharePointUrlMsg != null) {
        this.uploadFilesToSharePoint = MessageDialog.openConfirm(Display.getDefault().getActiveShell(),
            SHARE_POINT_UPLOAD, SHARE_POINT_UPLOAD_CONFIRM_MSG + sharePointUrlMsg + " ?");
        if (this.uploadFilesToSharePoint) {
          CurrentUserBO currentUserBO = new CurrentUserBO();
          try {
            if (!currentUserBO.hasPassword()) {
              new PasswordDialog(Display.getDefault().getActiveShell()).open();
            }
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().errorDialog(e.getMessage(), Activator.PLUGIN_ID);
          }
        }
      }

    }
  }

}
