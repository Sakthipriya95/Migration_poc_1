/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.nebula.widgets.grid.GridColumn;
import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.QnaireRespDetails;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * @author dmr1cob
 */
public class QnaireRespSelectionWizardPage extends WizardPage {


  /**
   * Composite instance
   */
  private Composite composite;

  /**
   * Wixard instance
   */
  private A2lRespMergeWizard wizard;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;

  private Composite topComposite;

  private Section topSection;

  private Form topForm;

  private Composite bottomComposite;

  private Section bottomSection;

  private Form bottomForm;

  private GridTableViewer rvwQnaireTabViewer;

  private GridTableViewer rvwQnaireDuplicateTabViewer;

  private final SortedSet<QnaireRespDetails> retainedQnaireRespDetailsSet = new TreeSet<>();


  /**
   * @param pageName Select Qnaire Response to retain
   * @param description page desciption
   */
  protected QnaireRespSelectionWizardPage(final String pageName, final String description) {
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
    gridLayout.numColumns = 2;
    gridLayout.makeColumnsEqualWidth = true;

    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);

    this.composite = new Composite(scrollComp, SWT.NULL);
    this.composite.setLayout(new GridLayout());

    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);
    scrollComp.setContent(this.composite);
    scrollComp.setMinSize(this.composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);

    this.wizard = (A2lRespMergeWizard) getWizard();

    createTopComposite();
    this.composite.setLayoutData(gridLayout);
    createBottomComposite();
  }

  /**
   *
   */
  private void createTopComposite() {
    this.topComposite = getFormToolkit().createComposite(this.composite);
    this.topComposite.setLayout(new GridLayout());
    createTopSection();
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.topComposite.setLayoutData(gridData);
  }

  /**
  *
  */
  private void createBottomComposite() {
    this.bottomComposite = getFormToolkit().createComposite(this.composite);
    this.bottomComposite.setLayout(new GridLayout());
    createBottomSection();
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.bottomComposite.setLayoutData(gridData);
  }

  /**
   *
   */
  private void createTopSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.topSection =
        getFormToolkit().createSection(this.topComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.topSection.getDescriptionControl().setEnabled(false);
    this.topSection.setExpanded(true);
    this.topSection.setText("Select questionnaire response to see duplicates");
    createTopForm();
    this.topSection.setLayoutData(gridData);
    this.topSection.setClient(this.topForm);
  }

  /**
  *
  */
  private void createBottomSection() {
    GridData gridData = GridDataUtil.getInstance().getGridData();
    this.bottomSection =
        getFormToolkit().createSection(this.bottomComposite, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.bottomSection.getDescriptionControl().setEnabled(false);
    this.bottomSection.setExpanded(true);
    this.bottomSection.setText("Only one questionnaire response can be retained");
    createBottomForm();
    this.bottomSection.setLayoutData(gridData);
    this.bottomSection.setClient(this.bottomForm);
  }

  /**
   *
   */
  private void createTopForm() {
    this.topForm = this.formToolkit.createForm(this.topSection);
    this.topForm.getBody().setLayout(new GridLayout());
    this.topForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    createRvwQnaireTable();
    this.topSection.setClient(this.topForm);
  }

  /**
  *
  */
  private void createBottomForm() {
    this.bottomForm = this.formToolkit.createForm(this.bottomSection);
    this.bottomForm.getBody().setLayout(new GridLayout());
    this.bottomForm.setLayoutData(GridDataUtil.getInstance().getGridData());
    createRvwQnaireDuplicateListTable();
    this.bottomSection.setClient(this.bottomForm);
  }

  /**
   *
   */
  private void createRvwQnaireTable() {
    this.rvwQnaireTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(this.topForm.getBody(),
        SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.rvwQnaireTabViewer.setContentProvider(new ArrayContentProvider());
    this.rvwQnaireTabViewer.getGrid().setLinesVisible(true);
    this.rvwQnaireTabViewer.getGrid().setHeaderVisible(true);
    this.rvwQnaireTabViewer.getControl().setEnabled(CommonUtils.isNotEmpty(this.wizard.getQnaireRespDetailsDupSet()));


    createQnaireRespNameColumn(this.rvwQnaireTabViewer, true);
    createPidcVersionCol(this.rvwQnaireTabViewer, true);
    createVarNameCol(this.rvwQnaireTabViewer, true);
    createWpNameCol(this.rvwQnaireTabViewer, true);


    this.rvwQnaireTabViewer.setInput(this.wizard.getQnaireRespDetailsDupSet());

    this.rvwQnaireTabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        IStructuredSelection selection =
            (IStructuredSelection) QnaireRespSelectionWizardPage.this.rvwQnaireTabViewer.getSelection();
        if (!selection.isEmpty()) {
          QnaireRespDetails qnaireRespDetails = (QnaireRespDetails) selection.getFirstElement();
          List<QnaireRespDetails> dupQnaireRespList =
              QnaireRespSelectionWizardPage.this.wizard.getQnaireRespDeatilsMap().get(qnaireRespDetails);
          if (null != QnaireRespSelectionWizardPage.this.rvwQnaireDuplicateTabViewer) {
            QnaireRespSelectionWizardPage.this.rvwQnaireDuplicateTabViewer.setInput(dupQnaireRespList);
          }
        }
      }


    });
  }


  /**
  *
  */
  private void createRvwQnaireDuplicateListTable() {
    this.rvwQnaireDuplicateTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(
        this.bottomForm.getBody(), SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, GridDataUtil.getInstance().getGridData());
    this.rvwQnaireDuplicateTabViewer.setContentProvider(new ArrayContentProvider());
    this.rvwQnaireDuplicateTabViewer.getGrid().setLinesVisible(true);
    this.rvwQnaireDuplicateTabViewer.getGrid().setHeaderVisible(true);
    this.rvwQnaireDuplicateTabViewer.getControl()
        .setEnabled(CommonUtils.isNotEmpty(this.wizard.getQnaireRespDetailsDupSet()));

    createCheckBoxCol(this.rvwQnaireDuplicateTabViewer);
    createQnaireRespNameColumn(this.rvwQnaireDuplicateTabViewer, false);
    createPidcVersionCol(this.rvwQnaireDuplicateTabViewer, false);
    createVarNameCol(this.rvwQnaireDuplicateTabViewer, false);
    createWpNameCol(this.rvwQnaireDuplicateTabViewer, false);
    createA2lRespCol(this.rvwQnaireDuplicateTabViewer);

  }

  /**
   *
   */
  private void createCheckBoxCol(final GridTableViewer gridTableViewer) {
    GridColumn retainFlagCol = new GridColumn(gridTableViewer.getGrid(), SWT.CHECK | SWT.CENTER);
    GridViewerColumn retainFlagColViewer = new GridViewerColumn(gridTableViewer, retainFlagCol);
    retainFlagColViewer.getColumn().setText("Retain");
    retainFlagColViewer.getColumn().setWidth(45);
    retainFlagColViewer.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public void update(final ViewerCell cell) {
        final Object element = cell.getElement();
        if (element instanceof QnaireRespDetails) {
          QnaireRespDetails qnaireRespDetails = (QnaireRespDetails) element;
          final GridItem gridItem = (GridItem) cell.getViewerRow().getItem();
          gridItem.setChecked(cell.getVisualIndex(), qnaireRespDetails.isRetainFlag());
        }
      }
    });

    retainFlagColViewer.setEditingSupport(new CheckEditingSupport(retainFlagColViewer.getViewer()) {

      @Override
      public void setValue(final Object arg0, final Object arg1) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) arg0;

        // Onle one qnaire resp can be retained. Hence refreshing the exisiting values
        if ((boolean) arg1) {

          if (QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.contains(qnaireResp)) {
            CDMLogger.getInstance().warnDialog("Only one questionnaire response can be retained", Activator.PLUGIN_ID);
            QnaireRespSelectionWizardPage.this.rvwQnaireDuplicateTabViewer
                .setInput(QnaireRespSelectionWizardPage.this.wizard.getQnaireRespDeatilsMap().get(qnaireResp));
            return;
          }
          qnaireResp.setRetainFlag((Boolean) arg1);
          QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.add(qnaireResp);
        }
        else {
          qnaireResp.setRetainFlag((Boolean) arg1);
          QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.remove(qnaireResp);
        }
        QnaireRespSelectionWizardPage.this.rvwQnaireTabViewer
            .setInput(QnaireRespSelectionWizardPage.this.wizard.getQnaireRespDetailsDupSet());
        QnaireRespSelectionWizardPage.this.wizard
            .setRetainedQnaireRespDetailsSet(QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet);
        QnaireRespSelectionWizardPage.this.wizard.getQnaireRespStatisticsWizardPage()
            .getRvwQnaireRespStatisticsTabViewer().getControl()
            .setEnabled(CommonUtils.isNotEmpty(QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet));
        getWizard().getContainer().updateButtons();
      }
    });
  }

  /**
   *
   */
  private void createA2lRespCol(final GridTableViewer gridTableViewer) {
    final GridViewerColumn a2lRespCol = new GridViewerColumn(gridTableViewer, SWT.NONE);
    a2lRespCol.getColumn().setText("A2l Responsibility");
    a2lRespCol.getColumn().setWidth(150);
    setRespNameCol(a2lRespCol);
  }

  /**
   * @param wpNameCol
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
   * @param b
   */
  private void createWpNameCol(final GridTableViewer gridTableViewer, final boolean isReviewQnaireTable) {
    final GridViewerColumn wpNameCol = new GridViewerColumn(gridTableViewer, SWT.NONE);
    wpNameCol.getColumn().setText("Work Package");
    wpNameCol.getColumn().setWidth(130);
    setWpNameCol(wpNameCol, isReviewQnaireTable);
  }

  /**
   * @param wpNameCol
   * @param isReviewQnaireTable
   */
  private void setWpNameCol(final GridViewerColumn wpNameCol, final boolean isReviewQnaireTable) {
    wpNameCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getA2lWorkPackage().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespDetails) {
          QnaireRespDetails qnaireRespDetails = (QnaireRespDetails) element;
          if (isReviewQnaireTable &&
              QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.contains(qnaireRespDetails)) {
            return QnaireRespSelectionWizardPage.this.rvwQnaireTabViewer.getGrid().getDisplay()
                .getSystemColor(SWT.COLOR_DARK_GREEN);
          }
        }

        return null;
      }
    });
  }

  /**
   * @param b
   */
  private void createVarNameCol(final GridTableViewer gridTableViewer, final boolean isReviewQnaireTable) {
    final GridViewerColumn varNameCol = new GridViewerColumn(gridTableViewer, SWT.NONE);
    varNameCol.getColumn().setText("Variant");
    varNameCol.getColumn().setWidth(110);
    setVarNameCol(varNameCol, isReviewQnaireTable);
  }

  /**
   * @param varNameCol
   * @param isReviewQnaireTable
   */
  private void setVarNameCol(final GridViewerColumn varNameCol, final boolean isReviewQnaireTable) {
    varNameCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getPidcVariant().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespDetails) {
          QnaireRespDetails qnaireRespDetails = (QnaireRespDetails) element;
          if (isReviewQnaireTable &&
              QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.contains(qnaireRespDetails)) {
            return QnaireRespSelectionWizardPage.this.rvwQnaireTabViewer.getGrid().getDisplay()
                .getSystemColor(SWT.COLOR_DARK_GREEN);
          }
        }

        return null;
      }
    });

  }

  /**
   * @param b
   */
  private void createPidcVersionCol(final GridTableViewer gridTableViewer, final boolean isReviewQnaireTable) {
    final GridViewerColumn pidcVersCol = new GridViewerColumn(gridTableViewer, SWT.NONE);
    pidcVersCol.getColumn().setText("Pidc Version");
    pidcVersCol.getColumn().setWidth(200);
    setPidcVersNameCol(pidcVersCol, isReviewQnaireTable);
  }

  /**
   * @param pidcVersCol
   * @param isReviewQnaireTable
   */
  private void setPidcVersNameCol(final GridViewerColumn pidcVersCol, final boolean isReviewQnaireTable) {
    pidcVersCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getPidcVersion().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespDetails) {
          QnaireRespDetails qnaireRespDetails = (QnaireRespDetails) element;
          if (isReviewQnaireTable &&
              QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.contains(qnaireRespDetails)) {
            return QnaireRespSelectionWizardPage.this.rvwQnaireTabViewer.getGrid().getDisplay()
                .getSystemColor(SWT.COLOR_DARK_GREEN);
          }
        }

        return null;
      }
    });

  }

  /**
   * @param isReviewQnaireTable
   */
  private void createQnaireRespNameColumn(final GridTableViewer gridTableViewer, final boolean isReviewQnaireTable) {
    final GridViewerColumn qNaireCol = new GridViewerColumn(gridTableViewer, SWT.NONE);
    qNaireCol.getColumn().setText("Questionnaire Response");
    qNaireCol.getColumn().setWidth(200);
    setQnaireRespNameCol(qNaireCol, isReviewQnaireTable);
  }

  /**
   * @param qNaireCol
   * @param isReviewQnaireTable
   */
  private void setQnaireRespNameCol(final GridViewerColumn qNaireCol, final boolean isReviewQnaireTable) {
    qNaireCol.setLabelProvider(new ColumnLabelProvider() {

      /**
       * {@inheritDoc}
       */
      @Override
      public String getText(final Object element) {
        QnaireRespDetails qnaireResp = (QnaireRespDetails) element;
        return qnaireResp.getRvwQnaireResponse().getName();
      }

      @Override
      public Color getForeground(final Object element) {
        if (element instanceof QnaireRespDetails) {
          QnaireRespDetails qnaireRespDetails = (QnaireRespDetails) element;
          if (isReviewQnaireTable &&
              QnaireRespSelectionWizardPage.this.retainedQnaireRespDetailsSet.contains(qnaireRespDetails)) {
            return QnaireRespSelectionWizardPage.this.rvwQnaireTabViewer.getGrid().getDisplay()
                .getSystemColor(SWT.COLOR_DARK_GREEN);
          }
        }

        return null;
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
    return this.wizard.getQnaireRespDetailsDupSet().size() == this.retainedQnaireRespDetailsSet.size();
  }
}
