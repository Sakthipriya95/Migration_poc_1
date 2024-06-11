/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.wizards.CDFXExportWizardData;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.cdfx.CdfxExportOutput;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;

/**
 * @author and4cob
 */
public class CdfxExportStatisticsPage extends WizardPage {

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * Section instance
   */
  private Section section;

  /**
   * Form instance
   */
  private Form form;

  private List cdfxFileNamesList;

  private List excelFileNamesList;

  private Label numberOfParamsLabel;

  private final CDFXExportWizardData cdfxWizardData;

  private Button autoOpenDirCheckbox;

  private Label outputFileLabel;

  private Label numberOfCompletedParams;

  private Label numberOfWpRespParams;

  private Label numberOfCheckedParams;

  private Label numberOfCalibratedParams;

  private Label numberOfPreCalParams;

  private Label numberOfNotRevParams;

  private Composite reviewParamFieldsComp;

  private Composite innerComposite;

  private static final int LIST_ROW_COUNT = 7;


  /**
   * @param pageName as input
   * @param cdfxWizardData CDFXExportWizardData
   */
  public CdfxExportStatisticsPage(final String pageName, final CDFXExportWizardData cdfxWizardData) {
    super(pageName);
    setTitle(pageName);
    setDescription("");
    this.cdfxWizardData = cdfxWizardData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {

    initializeDialogUnits(parent);

    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite workArea = new Composite(scrollComp, SWT.NONE);
    createComposite(workArea);
    final GridLayout layout = new GridLayout();
    workArea.setLayout(layout);
    workArea.setLayoutData(GridDataUtil.getInstance().createGridData());
    workArea.layout();
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setContent(workArea);
    scrollComp.setMinSize(workArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
  }

  /**
   * @param parent parent
   */
  private void createComposite(final Composite parent) {

    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite = getFormToolkit().createComposite(parent);
    this.composite.setLayout(new GridLayout());
    createSection();
    this.composite.setLayoutData(gridData);
    this.section.getDescriptionControl().setEnabled(false);
  }

  /**
   *
   */
  private void createSection() {
    GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
    this.section =
        SectionUtil.getInstance().createSection(this.composite, getFormToolkit(), gridData, "Export Summary");
    this.section.setLayout(new GridLayout());
    this.section.getDescriptionControl().setEnabled(false);
    // create form
    createForm();
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());
    // set the client
    this.section.setClient(this.form);
  }


  private void createForm() {

    this.form = getFormToolkit().createForm(this.section);
    final GridLayout gridLayout = new GridLayout();
    Composite formBody = this.form.getBody();
    formBody.setLayout(gridLayout);

    this.formToolkit.createLabel(formBody, "CDFx export completed successfully!");
    createEmptyLabel(formBody);

    createInnerComposite(formBody);
  }

  /**
   * @param innerComp innerComp
   * @param textGridData textGridData
   */
  private void createInnerComposite(final Composite formBody) {

    this.innerComposite = new Composite(formBody, SWT.NONE);
    final GridLayout layout = new GridLayout();
    layout.numColumns = 3;
    this.innerComposite.setLayout(layout);
    this.innerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    this.formToolkit.createLabel(this.innerComposite, "Output File");
    createEmptyLabel(this.innerComposite);
    this.outputFileLabel = new Label(this.innerComposite, SWT.NONE);
    GridData textGridData = new GridData(SWT.FILL, SWT.FILL, true, false);
    this.outputFileLabel.setLayoutData(textGridData);

    // To Create Empty row for proper allignment
    createEmptyRow(this.innerComposite);

    // CDFx File Name Row
    getFormToolkit().createLabel(this.innerComposite, "CDFx File Name(s): ");
    createEmptyLabel(this.innerComposite);
    this.cdfxFileNamesList = new List(this.innerComposite, SWT.V_SCROLL | SWT.BORDER);
    final GridData data = new GridData(GridData.FILL_HORIZONTAL);
    data.heightHint = LIST_ROW_COUNT * 5;
    this.cdfxFileNamesList.setLayoutData(data);

    // To Create Empty row for proper allignment
    createEmptyRow(this.innerComposite);

    // Excel File Name Row
    this.formToolkit.createLabel(this.innerComposite, "Excel File Name(s): ");
    createEmptyLabel(this.innerComposite);
    this.excelFileNamesList = new List(this.innerComposite, SWT.V_SCROLL | SWT.BORDER);
    this.excelFileNamesList.setLayoutData(data);

    // To Create Empty row for proper allignment
    createEmptyRow(this.innerComposite);

    // Parameters in A2L Row
    this.formToolkit.createLabel(this.innerComposite, "Parameters in A2L");
    createEmptyLabel(this.innerComposite);
    this.numberOfParamsLabel = new Label(this.innerComposite, SWT.NONE);
    this.numberOfParamsLabel.setLayoutData(textGridData);

    // Parameters in Bosch responsiblit Row
    this.formToolkit.createLabel(this.innerComposite, "Parameters in Bosch responsiblity");
    createEmptyLabel(this.innerComposite);
    this.numberOfWpRespParams = new Label(this.innerComposite, SWT.NONE);
    this.numberOfWpRespParams.setLayoutData(textGridData);
    this.numberOfWpRespParams.setToolTipText("Bosch Responibility, includes(Reviewed, Not Reviewed, Never Reviewed)");

    // To Create Empty row for proper allignment
    createEmptyRow(this.innerComposite);

    // Composite for Review Param Details Section
    createReviewParamDetailsSection();

    // To Create Empty row for proper allignment
    createEmptyRow(this.reviewParamFieldsComp);

    // Open Output file composite creation
    createOpenOutputFileComp(this.reviewParamFieldsComp);
  }

  private void createEmptyRow(final Composite comp) {

    createEmptyLabel(comp);
    createEmptyLabel(comp);
    createEmptyLabel(comp);
  }


  private void createReviewParamDetailsSection() {

    this.reviewParamFieldsComp = getFormToolkit().createComposite(this.innerComposite);
    GridLayout reviewParamsLayout = new GridLayout();
    reviewParamsLayout.numColumns = 3;
    this.reviewParamFieldsComp.setLayout(reviewParamsLayout);
    this.reviewParamFieldsComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    GridData textGridData = new GridData(SWT.FILL, SWT.FILL, true, false);

    this.formToolkit.createLabel(this.reviewParamFieldsComp, "Reviewed parameters in Bosch responsibility :");
    createEmptyLabel(this.reviewParamFieldsComp);
    createEmptyLabel(this.reviewParamFieldsComp);

    this.formToolkit.createLabel(this.reviewParamFieldsComp, "Parameters with 100% status");
    createEmptyLabel(this.reviewParamFieldsComp);
    this.numberOfCompletedParams = new Label(this.reviewParamFieldsComp, SWT.NONE);
    this.numberOfCompletedParams.setLayoutData(textGridData);
    this.numberOfCompletedParams.setToolTipText("Only Bosch type, Reviewed");

    this.formToolkit.createLabel(this.reviewParamFieldsComp, "Parameters with 75% status");
    createEmptyLabel(this.reviewParamFieldsComp);
    this.numberOfCheckedParams = new Label(this.reviewParamFieldsComp, SWT.NONE);
    this.numberOfCheckedParams.setLayoutData(textGridData);

    this.formToolkit.createLabel(this.reviewParamFieldsComp, "Parameters with 50% status");
    createEmptyLabel(this.reviewParamFieldsComp);
    this.numberOfCalibratedParams = new Label(this.reviewParamFieldsComp, SWT.NONE);
    this.numberOfCalibratedParams.setLayoutData(textGridData);

    this.formToolkit.createLabel(this.reviewParamFieldsComp, "Parameters with 25% status");
    createEmptyLabel(this.reviewParamFieldsComp);
    this.numberOfPreCalParams = new Label(this.reviewParamFieldsComp, SWT.NONE);
    this.numberOfPreCalParams.setLayoutData(textGridData);

    this.formToolkit.createLabel(this.reviewParamFieldsComp, "Parameters with 0% status");
    createEmptyLabel(this.reviewParamFieldsComp);
    this.numberOfNotRevParams = new Label(this.reviewParamFieldsComp, SWT.NONE);
    this.numberOfNotRevParams.setLayoutData(textGridData);
  }


  /**
   * Fill details for Export summary details page
   */
  public void fillDetails() {

    CdfxExportOutput cdfxExportOutput = this.cdfxWizardData.getCdfxExportOutput();
    this.outputFileLabel.setText(this.cdfxWizardData.getCdfxExportOutput().getExportFilePath());
    this.outputFileLabel.setToolTipText(this.cdfxWizardData.getCdfxExportOutput().getExportFilePath());
    this.cdfxFileNamesList.setItems(cdfxExportOutput.getCdfxFileName().split(","));
    this.excelFileNamesList.setItems(cdfxExportOutput.getExcelFileName().split(","));
    this.numberOfParamsLabel.setText(Integer.toString(cdfxExportOutput.getA2lParamCount()));
    this.numberOfCompletedParams.setText(Integer.toString(cdfxExportOutput.getCompletedParamCount()));
    this.numberOfCheckedParams.setText(Integer.toString(cdfxExportOutput.getCheckedParamCount()));
    this.numberOfCalibratedParams.setText(Integer.toString(cdfxExportOutput.getCalibratedParamCount()));
    this.numberOfPreCalParams.setText(Integer.toString(cdfxExportOutput.getPreCalParamCount()));
    this.numberOfNotRevParams.setText(Integer.toString(cdfxExportOutput.getNotRevParamCount()));
    this.numberOfWpRespParams.setText(Integer.toString(cdfxExportOutput.getWpRespParamCount()));
    // Hide the ReviewParam Details Composite Fields incase of multiple variants selected.
    if (CommonUtils.isNull(this.cdfxWizardData.getPidcVariant()) && CommonUtils.isNotEmpty(this.cdfxWizardData.getPidcVariants())) {
      hideReviewParamDetailsCompFields();
    }
  }

  /**
   * Hide fileds for multiple variants
   */
  private void hideReviewParamDetailsCompFields() {

    // To hide the review param details in CDFX Export Summary Details page:
    GridData gridData = (GridData) this.reviewParamFieldsComp.getLayoutData();
    // Exclude the grid data spae
    gridData.exclude = true;
    // To hide the fields in summary details page
    this.reviewParamFieldsComp.setVisible(false);

    // Open Output file composite creation
    createOpenOutputFileComp(this.innerComposite);
  }

  /**
   * Create open output file checkbox
   */
  private void createOpenOutputFileComp(final Composite comp) {

    final Composite outputFileCheckboxComp = getFormToolkit().createComposite(comp, SWT.NONE);
    outputFileCheckboxComp.setLayout(new GridLayout());
    outputFileCheckboxComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

    // Open Output file checbox
    this.autoOpenDirCheckbox = new Button(outputFileCheckboxComp, SWT.CHECK);
    this.autoOpenDirCheckbox.setLayoutData(new GridData());
    this.autoOpenDirCheckbox.setText("Open output folder");
    this.autoOpenDirCheckbox.setSelection(true);
  }

  /**
   * @param comp composite
   */
  private void createEmptyLabel(final Composite comp) {
    this.formToolkit.createLabel(comp, "");
  }

  /**
   * @return the formToolkit
   */
  public FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      // create the form toolkit if its not available
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * @return the autoOpenDirCheckbox
   */
  public Button getAutoOpenDirCheckbox() {
    return this.autoOpenDirCheckbox;
  }


  /**
   * @param autoOpenDirCheckbox the autoOpenDirCheckbox to set
   */
  public void setAutoOpenDirCheckbox(final Button autoOpenDirCheckbox) {
    this.autoOpenDirCheckbox = autoOpenDirCheckbox;
  }
}
