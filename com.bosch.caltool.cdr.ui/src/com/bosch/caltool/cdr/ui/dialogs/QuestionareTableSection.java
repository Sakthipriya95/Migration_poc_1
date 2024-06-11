/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.dialogs;

import java.util.Iterator;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;


/**
 * @author rgo7cob
 */
public class QuestionareTableSection {

  // Icdm-1955 to get the Questionare table section.


  /**
   * function sel dialog
   */
  private final FunctionSelectionDialog dialog;

  /**
   * @param folder tab folder
   * @param functionSelectionDialog functionSelectionDialog
   */
  public QuestionareTableSection(final TabFolder folder, final FunctionSelectionDialog functionSelectionDialog) {
    super();
    this.folder = folder;
    this.dialog = functionSelectionDialog;
  }

  /**
   * TabFolder
   */
  private final TabFolder folder;
  /**
   * Section
   */
  private Section sectionThree;
  /**
   * Form
   */
  private Form formThree;


  /**
   * Rule set tab Viewer
   */
  private GridTableViewer questabViewer;

  /**
   * Check box to show qnaire with write access
   */
  private Button showQnaireWithWriteAccess;
  private Text filterTxt;

  /**
   * @return the questabViewer
   */
  public GridTableViewer getQuestabViewer() {
    return this.questabViewer;
  }


  /**
   * @param toolkit toolkit
   * @param gridLayout gridLayout
   */
  public void createSectionThree(final FormToolkit toolkit, final GridLayout gridLayout) {
    // create section three
    this.sectionThree = toolkit.createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionThree.setText("Select Questionnaire");
    this.sectionThree.setDescription("Select data review questionnaire(s) to open in the editor");
    this.sectionThree.setExpanded(true);
    this.sectionThree.getDescriptionControl().setEnabled(false);
    this.sectionThree.setLayout(gridLayout);
    this.sectionThree.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    // create from
    createFormThree(toolkit);

    this.sectionThree.setClient(this.formThree);

  }

  /**
   * @return the section after section
   */
  public Section getQuestionTabSec() {
    return this.sectionThree;
  }

  /**
   * @return the grid data
   */
  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }

  /**
   * @param toolkit
   */
  private void createFormThree(final FormToolkit toolkit) {
    // create foem three
    this.formThree = toolkit.createForm(this.sectionThree);
    final GridLayout gridLayout = new GridLayout();
    this.formThree.getBody().setLayout(gridLayout);
    this.formThree.getBody().setLayoutData(getGridDataWithOutGrabExcessVSpace());

    // check box to show rule sets with write access

    setShowQnaireWithWriteAccess(new Button(this.formThree.getBody(), SWT.CHECK));
    getShowQnaireWithWriteAccess().setText("Show Questionnaire with 'Write' access");
    getShowQnaireWithWriteAccess().setSelection(true);

    QuestionnaireTabViewer tableviewer = new QuestionnaireTabViewer(this.formThree, toolkit);
    // Icdm-1368 - new table for Rule set
    this.questabViewer = tableviewer.createTable();

    this.filterTxt = tableviewer.getFilterTxt();

    this.questabViewer.setContentProvider(ArrayContentProvider.getInstance());
    // create sel listener
    addSelectionListener();
    // add double click listener
    QuestionareTableSection.this.dialog.addDoubleClickListener(this.questabViewer);
  }

  /**
   * @return typeFilterTxbBx
   */
  public Text getFilterTxt() {
    return this.filterTxt;
  }

  /**
   * add the selection listener
   */
  private void addSelectionListener() {

    this.questabViewer.getGrid().addSelectionListener(new SelectionAdapter() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void widgetSelected(final SelectionEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) QuestionareTableSection.this.questabViewer.getSelection();
        // ICDM-853
        if ((selection != null) && (!selection.isEmpty())) {
          QuestionareTableSection.this.dialog.getOkBtn().setEnabled(true);
          QuestionareTableSection.this.dialog.getSelectedQues().clear();
          final Iterator<Questionnaire> quesList = selection.iterator();
          while (quesList.hasNext()) {
            QuestionareTableSection.this.dialog.getSelectedQues().add(quesList.next());
          }

        }
      }

    });
  }


  /**
   * @return the showQnaireWithWriteAccess
   */
  public Button getShowQnaireWithWriteAccess() {
    return this.showQnaireWithWriteAccess;
  }


  /**
   * @param showQnaireWithWriteAccess the showQnaireWithWriteAccess to set
   */
  public void setShowQnaireWithWriteAccess(final Button showQnaireWithWriteAccess) {
    this.showQnaireWithWriteAccess = showQnaireWithWriteAccess;
  }


}
