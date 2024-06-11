/**
 *
 */
package com.bosch.caltool.usecase.ui.views;

import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;

import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.views.AbstractPageBookView;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditor;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;

/**
 * @author adn1cob
 */
public class UseCaseDetailsViewPart extends AbstractPageBookView {

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IPage createDefaultPage(final PageBook book) {
    final MessagePage page = new MessagePage();
    initPage(page);
    page.createControl(book);
    page.setMessage("There is no relevant part active!");
    return page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final PageRec doCreatePage(final IWorkbenchPart part) {

    if (part instanceof UseCaseEditor) {
      IEditorInput editorInput = ((IEditorPart) part).getEditorInput();
      UseCaseEditorInput ucEditorInput = (UseCaseEditorInput) editorInput;
      UsecaseClientBO useCase = ucEditorInput.getSelectedUseCase();
      Page page = new UseCaseDetailsPage(useCase, this, ucEditorInput.getUseCaseEditorModel());
      initPage(page);
      page.createControl(getPageBook());
      // Data Base Notification For Use case
      PageRec pageRec = new PageRec(part, page);
      this.pageRecordList.add(pageRec);

      return pageRec;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final void doDestroyPage(final IWorkbenchPart part, final PageRec pageRecord) {
    this.pageRecordList.remove(pageRecord);
    pageRecord.page.dispose();
    pageRecord.dispose();

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IWorkbenchPart getBootstrapPart() {
    if (getSite().getWorkbenchWindow().getActivePage() != null) {
      return getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final boolean isImportant(final IWorkbenchPart part) {
    return part instanceof IEditorPart;
  }

}
