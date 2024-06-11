/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespDetailsPage;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespSummaryPage;
import com.bosch.caltool.cdr.ui.editors.pages.QnaireRespVersionPage;
import com.bosch.caltool.cdr.ui.views.QnaireRespOutlinePageCreator;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * icdm-1983 this class is the editor for entering the response to a questionaire of a review result
 *
 * @author mkl2cob
 */
public class QnaireResponseEditor extends AbstractFormEditor {

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  /**
   * QuestionResponseDetailsPage
   */
  private QnaireRespDetailsPage quesDetailsPage;

  /**
   * QuestionResponseSummaryPage
   */
  private QnaireRespSummaryPage quesSummaryPge;
  /**
   * Defines ReviewResultEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.QnaireResponseEditor";
  /**
   * Questionnaire editor input instance
   */
  private QnaireRespEditorInput editorInput;

  /**
   * defines the outline page creator for questionnaire response
   */
  // iCDM-1991
  private QnaireRespOutlinePageCreator outlinePageCreator;

  private QnaireRespVersionPage qnaireRespVersionPage;


  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    this.quesSummaryPge = new QnaireRespSummaryPage(this, "", "List View");
    this.quesDetailsPage = new QnaireRespDetailsPage(this, "", "Details View");
    this.qnaireRespVersionPage = new QnaireRespVersionPage(this, "", "Versions");
    try {
      // add ques summary page
      addPage(this.quesSummaryPge);
      // add details page
      addPage(this.quesDetailsPage);
      // add Versions Page
      addPage(this.qnaireRespVersionPage);

    }
    catch (PartInitException exception) {
      CDMLogger.getInstance().error(exception.getMessage(), exception, Activator.PLUGIN_ID);
    }
  }

  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    if (input instanceof QnaireRespEditorInput) {
      this.editorInput = (QnaireRespEditorInput) input;
      this.outlinePageCreator = new QnaireRespOutlinePageCreator(this.editorInput.getQnaireRespEditorDataHandler());

      // set part name
      setPartName(this.editorInput.getQnaireRespEditorDataHandler().getNameExt() + " - Response");

      super.init(site, input);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    return this.outlinePageCreator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSave(final IProgressMonitor monitor) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void doSaveAs() {
    // Not applicable
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSaveAsAllowed() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    setStatusMessage();
    // ICDM-1994
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(true);
  }

  /**
   * Status of the focussed page is set
   */
  public void setStatusMessage() {
    if (getActivePage() == 0) {
      this.quesSummaryPge.setStatusBarMessage(this.quesSummaryPge.getGroupHeaderLayer(), false);
    }
    if (getActivePage() == 1) {
      final IStatusLineManager editorStatusLine = getEditorSite().getActionBars().getStatusLineManager();
      editorStatusLine.setErrorMessage(null);
      editorStatusLine.setMessage("");
      IViewPart findView = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(ApicUiConstants.OUTLINE_TREE_VIEW);
      // ICDM-2096 (Disable view and editor status line manager for details page)
      IStatusLineManager outlineViewStatusLine;
      if (null != findView) {
        final IViewSite viewPartSite = (IViewSite) findView.getSite();
        outlineViewStatusLine = viewPartSite.getActionBars().getStatusLineManager();
      }
      else {
        outlineViewStatusLine = getEditorSite().getActionBars().getStatusLineManager();
      }
      outlineViewStatusLine.setErrorMessage(null);
      outlineViewStatusLine.setMessage("");
    }

  }

  /**
   * getter method for the outline page of questionnaire response
   *
   * @return the QuestionnaireResponseOutlinePageCreator
   */
  // iCDM-1991
  public QnaireRespOutlinePageCreator getQuestionnaireResOutlinePageCreator() {
    return this.outlinePageCreator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public QnaireRespEditorInput getEditorInput() {
    return (QnaireRespEditorInput) super.getEditorInput();
  }

  /**
   * Updating the status bar
   *
   * @param outlineSelection flag set according to selection made in viewPart or editor.
   * @param totalItemCount total attributes in the table
   * @param filteredItemCount filtered attributes in the table
   */
  public void updateStatusBar(final boolean outlineSelection, final int totalItemCount, final int filteredItemCount) {

    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView("com.bosch.caltool.icdm.common.ui.views.OutlineViewPart").getSite();
      statusLine = viewPartSite.getActionBars().getStatusLineManager();
    }
    // Updation of status based on selection in editor
    else {
      statusLine = getEditorSite().getActionBars().getStatusLineManager();
    }
    if (totalItemCount == filteredItemCount) {
      statusLine.setErrorMessage(null);
      statusLine.setMessage(buf.toString());
    }
    else {
      statusLine.setErrorMessage(buf.toString());
    }
    statusLine.update(true);
  }

  /**
   * ICDM-2096
   *
   * @return the quesDetailsPage
   */
  public QnaireRespDetailsPage getQuesDetailsPage() {
    return this.quesDetailsPage;
  }


  /**
   * ICDM-2096
   *
   * @return the quesSummaryPge
   */
  public QnaireRespSummaryPage getQuesSummaryPge() {
    return this.quesSummaryPge;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void setActivePage(final int pageIndex) {
    if (pageIndex == this.quesSummaryPge.getIndex()) {
      this.quesSummaryPge.modifyContentProviderAndSelection();
    }
    super.setActivePage(pageIndex);
  }

  /**
   * @return the qnaireRespVersionPage
   */
  public QnaireRespVersionPage getQnaireRespVersionPage() {
    return this.qnaireRespVersionPage;
  }


  /**
   * @param qnaireRespVersionPage the qnaireRespVersionPage to set
   */
  public void setQnaireRespVersionPage(final QnaireRespVersionPage qnaireRespVersionPage) {
    this.qnaireRespVersionPage = qnaireRespVersionPage;
  }

}
