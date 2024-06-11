/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.a2l.A2lResponsibility;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author dmr1cob
 */
public class QnaireRespStatisticsWizardPage extends WizardPage {


  /**
   * Composite instance
   */
  private Composite mainComposite;

  /**
   * Wixard instance
   */
  private A2lRespMergeWizard wizard;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private Composite composite;

  private Section createQnaireRespSection;

  private Form createQnaireRespForm;

  private GridTableViewer rvwQnaireRespStatisticsTabViewer;

  private Section createA2lRespSection;

  private Form createA2lRespForm;

  /**
   * @param pageName Selected Qnaire Response(s)
   * @param description page description
   */
  protected QnaireRespStatisticsWizardPage(final String pageName, final String description) {
    super(pageName);
    setTitle(pageName);
    setDescription(description);
    this.wizard = (A2lRespMergeWizard) getWizard();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    GridLayout gridLayout = new GridLayout();

    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

    this.mainComposite = new Composite(scrollComp, SWT.NULL);
    this.mainComposite.setLayout(new GridLayout());
    this.mainComposite.setLayoutData(gridLayout);

    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setContent(this.mainComposite);
    scrollComp.setMinSize(this.mainComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);

    this.wizard = (A2lRespMergeWizard) getWizard();

    createComposite();
  }

  /**
   *
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.mainComposite);
    this.composite.setLayout(new GridLayout());
    createA2lRespSection();
    createQnaireRespSection();
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.composite.setLayoutData(gridData);
  }


  /**
   *
   */
  private void createA2lRespSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.createA2lRespSection =
        getFormToolkit().createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.createA2lRespSection.getDescriptionControl().setEnabled(false);
    this.createA2lRespSection.setExpanded(true);
    this.createA2lRespSection.setText("Selected A2l Responsibilities");
    createA2lRespForm();
    this.createA2lRespSection.setLayoutData(gridData);
    this.createA2lRespSection.setClient(this.createA2lRespForm);
  }


  /**
   *
   */
  private void createA2lRespForm() {
    this.createA2lRespForm = this.formToolkit.createForm(this.createA2lRespSection);
    this.createA2lRespForm.getBody().setLayout(new GridLayout());
    this.createA2lRespForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    createA2lRespTable();
    this.createA2lRespSection.setClient(this.createA2lRespForm);
  }


  /**
   *
   */
  private void createA2lRespTable() {

    GridTableViewer a2lRespStatisticsTabViewer =
        GridTableViewerUtil.getInstance().createGridTableViewer(this.createA2lRespForm.getBody(),
            SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    a2lRespStatisticsTabViewer.setContentProvider(new ArrayContentProvider());
    a2lRespStatisticsTabViewer.getGrid().setLinesVisible(true);
    a2lRespStatisticsTabViewer.getGrid().setHeaderVisible(true);

    final GridViewerColumn qNaireCol = new GridViewerColumn(a2lRespStatisticsTabViewer, SWT.NONE);
    qNaireCol.getColumn().setText("A2l Responsibilities");
    qNaireCol.getColumn().setWidth(200);

    qNaireCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        A2lResponsibility a2lResp = (A2lResponsibility) element;
        return a2lResp.getAliasName();
      }
    });


    a2lRespStatisticsTabViewer.setInput(this.wizard.getSelectedA2lWpRespList());

  }


  /**
   *
   */
  private void createQnaireRespSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.createQnaireRespSection =
        getFormToolkit().createSection(this.composite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.createQnaireRespSection.getDescriptionControl().setEnabled(false);
    this.createQnaireRespSection.setExpanded(true);
    this.createQnaireRespSection.setText("Selected Questionare Response to retain");
    createQnaireRespForm();
    this.createQnaireRespSection.setLayoutData(gridData);
    this.createQnaireRespSection.setClient(this.createQnaireRespForm);
  }


  /**
   *
   */
  private void createQnaireRespForm() {
    this.createQnaireRespForm = this.formToolkit.createForm(this.createQnaireRespSection);
    this.createQnaireRespForm.getBody().setLayout(new GridLayout());
    this.createQnaireRespForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    createRvwQnaireRespTable();
    this.createQnaireRespSection.setClient(this.createQnaireRespForm);
  }


  /**
   *
   */
  private void createRvwQnaireRespTable() {
    this.rvwQnaireRespStatisticsTabViewer =
        GridTableViewerUtil.getInstance().createGridTableViewer(this.createQnaireRespForm.getBody(),
            SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.rvwQnaireRespStatisticsTabViewer.setContentProvider(new ArrayContentProvider());
    this.rvwQnaireRespStatisticsTabViewer.getGrid().setLinesVisible(true);
    this.rvwQnaireRespStatisticsTabViewer.getGrid().setHeaderVisible(true);
    this.rvwQnaireRespStatisticsTabViewer.getControl()
        .setEnabled(CommonUtils.isNotEmpty(this.wizard.getRetainedQnaireRespDetailsSet()));

    createQnaireRespNameColumn();
    createPidcVersionCol();
    createVarNameCol();
    createWpNameCol();
    createA2lRespCol();

    this.rvwQnaireRespStatisticsTabViewer.setInput(this.wizard.getRetainedQnaireRespDetailsSet());
  }


  /**
   *
   */
  private void createQnaireRespNameColumn() {
    final GridViewerColumn qNaireCol = new GridViewerColumn(this.rvwQnaireRespStatisticsTabViewer, SWT.NONE);
    qNaireCol.getColumn().setText("Questionnaire Response");
    qNaireCol.getColumn().setWidth(200);
    setQnaireRespNameCol(qNaireCol);
  }


  /**
   * @param qNaireCol
   */
  private void setQnaireRespNameCol(final GridViewerColumn qNaireCol) {
    qNaireCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getRvwQnaireResponse().getName();
      }
    });

  }


  /**
   *
   */
  private void createPidcVersionCol() {
    final GridViewerColumn pidcVersCol = new GridViewerColumn(this.rvwQnaireRespStatisticsTabViewer, SWT.NONE);
    pidcVersCol.getColumn().setText("Pidc Version");
    pidcVersCol.getColumn().setWidth(200);
    setPidcVersNameCol(pidcVersCol);
  }


  /**
   * @param pidcVersCol
   */
  private void setPidcVersNameCol(final GridViewerColumn pidcVersCol) {
    pidcVersCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getPidcVersion().getName();
      }
    });

  }


  /**
   *
   */
  private void createVarNameCol() {
    final GridViewerColumn varNameCol = new GridViewerColumn(this.rvwQnaireRespStatisticsTabViewer, SWT.NONE);
    varNameCol.getColumn().setText("Variant");
    varNameCol.getColumn().setWidth(150);
    setVarNameCol(varNameCol);
  }


  /**
   * @param varNameCol
   */
  private void setVarNameCol(final GridViewerColumn varNameCol) {
    varNameCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getPidcVariant().getName();
      }
    });

  }


  /**
   *
   */
  private void createWpNameCol() {
    final GridViewerColumn wpNameCol = new GridViewerColumn(this.rvwQnaireRespStatisticsTabViewer, SWT.NONE);
    wpNameCol.getColumn().setText("Work Package");
    wpNameCol.getColumn().setWidth(150);
    setWpNameCol(wpNameCol);
  }


  /**
   * @param wpNameCol
   */
  private void setWpNameCol(final GridViewerColumn wpNameCol) {
    wpNameCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getA2lWorkPackage().getName();
      }
    });
  }


  /**
   * @param rvwQnaireRespStatisticsTabViewer2
   */
  private void createA2lRespCol() {
    final GridViewerColumn a2lRespCol = new GridViewerColumn(this.rvwQnaireRespStatisticsTabViewer, SWT.NONE);
    a2lRespCol.getColumn().setText("A2l Responsibility");
    a2lRespCol.getColumn().setWidth(150);
    setRespNameCol(a2lRespCol);
  }


  /**
   * @param a2lRespCol
   */
  private void setRespNameCol(final GridViewerColumn a2lRespCol) {
    a2lRespCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getA2lResponsibility().getName();
      }
    });
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

  @Override
  public boolean canFlipToNextPage() {
    return false;
  }


  /**
   * @return the rvwQnaireRespStatisticsTabViewer
   */
  public GridTableViewer getRvwQnaireRespStatisticsTabViewer() {
    return this.rvwQnaireRespStatisticsTabViewer;
  }


}
