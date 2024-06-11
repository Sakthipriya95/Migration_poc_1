/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionDetailsPage;
import com.bosch.caltool.cdr.ui.editors.pages.QuestionareVersionsPage;
import com.bosch.caltool.cdr.ui.views.ReviewQuestionnaireOutlinePageCreator;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormEditor;
import com.bosch.caltool.icdm.common.ui.providers.SelectionProviderMediator;
import com.bosch.caltool.icdm.common.ui.views.IPageCreator;
import com.bosch.caltool.icdm.common.ui.views.OutlineViewPart;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * Editor for Questionnaire maintenance
 *
 * @author dmo5cob
 */
public class ReviewQuestionaireEditor extends AbstractFormEditor {

  /**
   * Defines ReviewResultEditor id
   */
  public static final String EDITOR_ID = "com.bosch.caltool.cdr.ui.editors.ReviewQuestionaireEditor";
  /**
   * SelectionProviderMediator
   */
  private SelectionProviderMediator selectionProviderMediator = new SelectionProviderMediator();
  /**
   * ReviewQuestionaireEditorInput
   */
  private ReviewQuestionaireEditorInput input;
  /**
   * Instance of question details page
   */
  private QuestionDetailsPage qDetailsPage;
  private NodeAccessRightsPage nodeAccessRightsPage;


  /**
   * {@inheritDoc}
   */
  @Override
  public void init(final IEditorSite site, final IEditorInput input) throws PartInitException {
    if (input instanceof ReviewQuestionaireEditorInput) {
      // get the editor input
      this.input = (ReviewQuestionaireEditorInput) input;
      changeInput(this.input);
      super.init(site, input);

    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IPageCreator getOutlinePageCreator() {
    return new ReviewQuestionnaireOutlinePageCreator(getEditorInput());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewQuestionaireEditorInput getEditorInput() {
    return (ReviewQuestionaireEditorInput) super.getEditorInput();
  }

  /**
   * @param editorInput PIDCEditorInput
   */
  public void changeInput(final ReviewQuestionaireEditorInput editorInput) {
    // set part name
    super.setPartName(editorInput.getQnaireDefBo().getNameForPart());
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void addPages() {
    this.qDetailsPage = new QuestionDetailsPage(this, getEditorInput().getQnaireDefBo());
    QuestionareVersionsPage versionPage = new QuestionareVersionsPage(this, getEditorInput().getQnaireDefBo());
    this.nodeAccessRightsPage = new NodeAccessRightsPage(this, this.input.getNodeAccessBO());

    try {
      // add details page
      addPage(this.qDetailsPage);
      // add versions page
      addPage(versionPage);
      // add rights page
      addPage(this.nodeAccessRightsPage);
    }
    catch (PartInitException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
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

  // ICDM-1965
  /**
   * @return the selectionProviderMediator
   */
  public SelectionProviderMediator getSelectionProviderMediator() {
    return this.selectionProviderMediator;
  }


  /**
   * @param selectionProviderMediator the selectionProviderMediator to set
   */
  public void setSelectionProviderMediator(final SelectionProviderMediator selectionProviderMediator) {
    this.selectionProviderMediator = selectionProviderMediator;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell().setFocus();
    setStatusMessage();
  }

  /**
   * Status of the focussed page is set
   */
  public void setStatusMessage() {
    if (getActivePage() == 0) {
      this.qDetailsPage.setStatusBarMessage(this.qDetailsPage.getGroupByHeaderLayer(), false);
    }
    if (getActivePage() == 1) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setErrorMessage(null);
      statusLine.setMessage("");
    }
    if (getActivePage() == 2) {
      final IStatusLineManager statusLine = getEditorSite().getActionBars().getStatusLineManager();
      statusLine.setErrorMessage(null);
      statusLine.setMessage("");
    }
  }


  /**
   * Set status bar message
   *
   * @param outlineSelection
   * @param totalItemCount
   * @param filteredItemCount
   */
  public void updateStatusBar(final boolean outlineSelection, final int totalItemCount, final int filteredItemCount) {

    final StringBuilder buf = new StringBuilder("Displaying : ");
    buf.append(filteredItemCount).append(" out of ").append(totalItemCount);
    IStatusLineManager statusLine;
    // Updation of status based on selection in view part
    if (outlineSelection) {
      final IViewSite viewPartSite = (IViewSite) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
          .findView(OutlineViewPart.PART_ID).getSite();
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
}
