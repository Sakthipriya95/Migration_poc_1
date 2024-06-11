/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages;

import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.editors.CdrReportEditorInput;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataStatistics;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.label.LabelUtil;

/**
 * Page to display the Calibration Data Review Statistics
 *
 * @author dja7cob
 */
// ICDM-2170 ; ICDM-2329
public class CdrReportStatisticsPage extends AbstractFormPage implements ISelectionListener {

  /**
   * non scrollable form
   */
  private Form nonScrollableForm;

  /**
   * ScrolledComposite instance
   */
  private ScrolledComposite scrollComp;

  /**
   * Sash form instance
   */
  private SashForm mainComposite;

  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private Text selParamCountText;

  /**
   * @param editor CDR Report Editor
   * @param formID ID
   * @param title Page title
   */
  public CdrReportStatisticsPage(final FormEditor editor, final String formID, final String title) {
    super(editor, formID, title);
  }

  @Override
  public CdrReportEditorInput getEditorInput() {
    return (CdrReportEditorInput) super.getEditorInput();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createPartControl(final Composite parent) {
    final GridData gridData = GridDataUtil.getInstance().getGridData();

    this.nonScrollableForm = getEditor().getToolkit().createForm(parent);
    this.nonScrollableForm.getBody().setLayout(new GridLayout());
    this.nonScrollableForm.getBody().setLayoutData(gridData);
    this.nonScrollableForm.setText(getEditorInput().getName());
    addHelpAction((ToolBarManager) this.nonScrollableForm.getToolBarManager());

    final GridLayout gridLayout = new GridLayout();
    final GridData gridData1 = new GridData();
    gridData1.horizontalAlignment = GridData.FILL;
    gridData1.grabExcessHorizontalSpace = true;

    // create the main composite
    this.mainComposite = new SashForm(this.nonScrollableForm.getBody(), SWT.HORIZONTAL);
    this.mainComposite.setLayout(gridLayout);
    this.mainComposite.setLayoutData(gridData);

    final ManagedForm mform = new ManagedForm(parent);
    createFormContent(mform);

  }

  /**
   * This method initializes CompositeTwo
   */
  public void createComposite() {
    // create scroll composite in the right side
    this.scrollComp = new ScrolledComposite(this.mainComposite, SWT.H_SCROLL | SWT.V_SCROLL);
    this.scrollComp.setLayout(new GridLayout());
    final Composite compositeTwo = new Composite(this.scrollComp, SWT.NONE);

    createRightSection(compositeTwo);

    compositeTwo.setLayout(new GridLayout());
    compositeTwo.setLayoutData(GridDataUtil.getInstance().getGridData());

    this.scrollComp.setContent(compositeTwo);
    this.scrollComp.setExpandHorizontal(true);
    this.scrollComp.setExpandVertical(true);
    this.scrollComp.setDragDetect(true);
    // create the control listener for scrolling
    this.scrollComp.addControlListener(new ControlAdapter() {

      @Override
      public void controlResized(final ControlEvent event) {
        Rectangle rect = CdrReportStatisticsPage.this.scrollComp.getClientArea();
        CdrReportStatisticsPage.this.scrollComp.setMinSize(compositeTwo.computeSize(rect.width, SWT.DEFAULT));
      }
    });
  }

  /**
   * @param compositeTwo
   */
  private void createRightSection(final Composite compositeTwo) {
    Section section = this.formToolkit.createSection(compositeTwo, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    section.setText("Review Statistics");
    section.setLayout(new GridLayout());
    section.setLayoutData(GridDataUtil.getInstance().getGridData());
    section.getDescriptionControl().setEnabled(false);

    section.setClient(createForm(section));
  }

  /**
   * @param sectionRight
   */
  private Form createForm(final Section sectionRight) {

    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    Form form = this.formToolkit.createForm(sectionRight);
    form.getBody().setLayout(gridLayout);
    form.getBody().setLayoutData(GridDataUtil.getInstance().getGridData());

    LabelUtil.getInstance().createLabel(form.getBody(), "Number of Parameters reviewed in A2L file :");
    Text a2lParamCountText = this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData a2lParamCountGridData = GridDataUtil.getInstance().createGridData();
    a2lParamCountGridData.grabExcessVerticalSpace = false;
    a2lParamCountText.setLayoutData(a2lParamCountGridData);
    a2lParamCountText.setFocus();
    a2lParamCountText.setEditable(false);
    CdrReportDataStatistics cdrReportDataStatistics = new CdrReportDataStatistics(getEditorInput().getReportData());
    // Set value for total param count in a2l file
    int[] a2lRvwDetails = cdrReportDataStatistics.getA2lReviewDetails();
    a2lParamCountText.setText(a2lRvwDetails[0] + " of " + a2lRvwDetails[1]);

    LabelUtil.getInstance().createLabel(form.getBody(), "Number of Parameters reviewed in selection :");
    this.selParamCountText = this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData selParamCountGridData = GridDataUtil.getInstance().createGridData();
    selParamCountGridData.grabExcessVerticalSpace = false;
    this.selParamCountText.setLayoutData(selParamCountGridData);
    this.selParamCountText.setFocus();
    this.selParamCountText.setEditable(false);

    // Set value for selected node's param count
    // Initially, no node will be selected. So total count will get displayed
    this.selParamCountText.setText(a2lRvwDetails[0] + " of " + a2lRvwDetails[1]);

    LabelUtil.getInstance().createLabel(form.getBody(), "Number of Parameters in Bosch Responsibility :");
    Text numParamInBoschRespText = this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData numParamInBoschRespGridData = GridDataUtil.getInstance().createGridData();
    numParamInBoschRespGridData.grabExcessVerticalSpace = false;
    numParamInBoschRespText.setLayoutData(numParamInBoschRespGridData);
    numParamInBoschRespText.setFocus();
    numParamInBoschRespText.setEditable(false);
    numParamInBoschRespText.setText(cdrReportDataStatistics.getParameterInBoschResp() + "");

    LabelUtil.getInstance().createLabel(form.getBody(), "Number of Parameters in Bosch Responsibility Reviewed :");
    Text numParamInBoschRespRvwedText = this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData numParamInBoschRespRvwedGridData = GridDataUtil.getInstance().createGridData();
    numParamInBoschRespRvwedGridData.grabExcessVerticalSpace = false;
    numParamInBoschRespRvwedText.setLayoutData(numParamInBoschRespGridData);
    numParamInBoschRespRvwedText.setFocus();
    numParamInBoschRespRvwedText.setEditable(false);
    numParamInBoschRespRvwedText.setText(cdrReportDataStatistics.getParameterInBoschRespRvwed() + "");

    LabelUtil.getInstance().createLabel(form.getBody(), "Percentage of parameters in Bosch responsibility reviewed :");
    Text paramCountWithBoschRespText = this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData paramCountWithBoschRespGridData = GridDataUtil.getInstance().createGridData();
    paramCountWithBoschRespGridData.grabExcessVerticalSpace = false;
    paramCountWithBoschRespText.setLayoutData(paramCountWithBoschRespGridData);
    paramCountWithBoschRespText.setFocus();
    paramCountWithBoschRespText.setEditable(false);
    paramCountWithBoschRespText.setText(
        CommonUiUtils.displayLangBasedPercentage(cdrReportDataStatistics.getReviewedParameterWithBoschResp()) + "%");

    LabelUtil.getInstance().createLabel(form.getBody(),
        "Percentage of parameters in Bosch responsibility with completed questionnaire :");
    Text qnaireCompParamCountWithBoschRespText =
        this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData qnaireCompParamCountWithBoschRespGridData = GridDataUtil.getInstance().createGridData();
    qnaireCompParamCountWithBoschRespGridData.grabExcessVerticalSpace = false;
    qnaireCompParamCountWithBoschRespText.setLayoutData(qnaireCompParamCountWithBoschRespGridData);
    qnaireCompParamCountWithBoschRespText.setFocus();
    qnaireCompParamCountWithBoschRespText.setEditable(false);
    qnaireCompParamCountWithBoschRespText.setText(CommonUiUtils
        .displayLangBasedPercentage(cdrReportDataStatistics.getReviewedParameterWithBoschRespForComplQnaire()) + "%");


    LabelUtil.getInstance().createLabel(form.getBody(), "Number of questionnaires with negative answer included :");
    Text qnaireNagativeAnswerCountText = this.formToolkit.createText(form.getBody(), null, SWT.BORDER | SWT.WRAP);
    GridData qnaireNagativeAnswerCountGridData = GridDataUtil.getInstance().createGridData();
    qnaireNagativeAnswerCountGridData.grabExcessVerticalSpace = false;
    qnaireNagativeAnswerCountText.setLayoutData(qnaireNagativeAnswerCountGridData);
    qnaireNagativeAnswerCountText.setFocus();
    qnaireNagativeAnswerCountText.setEditable(false);
    qnaireNagativeAnswerCountText.setText(cdrReportDataStatistics.getQnaireWithNegativeAnswersCount() + "");

    return form;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Control getPartControl() {
    return this.nonScrollableForm;
  }

  /**
   * creating the content of the form
   */
  @Override
  protected void createFormContent(final IManagedForm managedForm) {
    // initiate the form tool kit
    this.formToolkit = managedForm.getToolkit();

    createComposite();
    // add listeners
    getSite().getPage().addSelectionListener(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IToolBarManager getToolBarManager() {
    return this.nonScrollableForm.getToolBarManager();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void selectionChanged(final IWorkbenchPart iworkbenchpart, final ISelection iselection) {
    if ((getSite().getPage().getActiveEditor() == getEditor()) && (iworkbenchpart instanceof OutlineViewPart)) {

      selectionListener(iselection);
    }
  }

  /**
   * Selection listener implementation for selections on outlineFilter
   *
   * @param selection
   */
  private void selectionListener(final ISelection selection) {
    int[] rvwDetSelection = new int[2];
    if ((selection instanceof IStructuredSelection) && !selection.isEmpty()) {
      rvwDetSelection = new CdrReportDataStatistics(getEditorInput().getReportData())
          .getStatistics(((IStructuredSelection) selection).getFirstElement());
    }
    this.selParamCountText.setText(rvwDetSelection[0] + " of " + rvwDetSelection[1]);
  }
}
