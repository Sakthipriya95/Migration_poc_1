package com.bosch.caltool.icdm.common.ui.views;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;

import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;

/**
 * View Part for the Outline View.
 *
 * @author bne4cob
 */
// iCDM-250
public class OutlineViewPart extends AbstractPageBookView {

  /**
   * Unique ID for this View Part.
   */
  public static final String PART_ID = "com.bosch.caltool.icdm.common.ui.views.OutlineViewPart";
  /**
   * Holds the page creator instance (PIDC or A2L)
   */
  private IPageCreator iPageCreator;
  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  @Override
  public void setFocus() {
    // ICDM-865
    // when outline view is clicked , the excel export button is deactivated
    this.expReportService = (CommandState) CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(false);
    super.setFocus();
  }

  /**
   * Sets which page to create
   *
   * @param iPageCreator page creator
   */
  public void setPageCreator(final IPageCreator iPageCreator) {
    this.iPageCreator = iPageCreator;
  }

  @Override
  protected IPage createDefaultPage(final PageBook book) {
    MessagePage page = new MessagePage();
    initPage(page);
    page.createControl(book);
    // Set page message
    page.setMessage("There is no relevant part active!");
    return page;

  }

  @Override
  protected PageRec doCreatePage(final IWorkbenchPart part) {
    // Check if the page creator object is not set
    if ((part instanceof IEditorPart) && (this.iPageCreator != null)) {
      // iCDM-250
      AbstractPage page = this.iPageCreator.createPage();
      initPage(page);
      page.createControl(getPageBook());
      // add the pages to page record list
      PageRec pageRec = new PageRec(part, page);
      this.pageRecordList.add(pageRec);

      return pageRec;

    }

    return null;
  }

  @Override
  protected void doDestroyPage(final IWorkbenchPart part, final PageRec pageRecord) {
    this.pageRecordList.remove(pageRecord);
    pageRecord.page.dispose();
    pageRecord.dispose();
  }

  @Override
  protected IWorkbenchPart getBootstrapPart() {
    // iCDM-69
    if (getSite().getWorkbenchWindow().getActivePage() != null) {
      return getSite().getWorkbenchWindow().getActivePage().getActiveEditor();
    }
    return null;
  }

  @Override
  protected boolean isImportant(final IWorkbenchPart part) {
    return part instanceof IEditorPart;
  }

  /**
   * ICDM-931 Refreshes the tree viewer
   *
   * @param deselectAll - true/false : whether or not to clear the selection on the tree while refreshing
   */
  public void refreshTreeViewer(final boolean deselectAll) {
    for (PageRec pageRec : super.pageRecordList) {
      // trees in the outlinepages are refreshed to reflect the link decorator
      if (pageRec.page instanceof AbstractPage) {
        ((AbstractPage) pageRec.page).refreshTreeViewer(deselectAll);
      }
    }
  }

  /**
   * Sets the title tooltip
   *
   * @param toolTip tooltip text
   */
  public void setTitleTooltip(final String toolTip) {
    setTitleToolTip(toolTip);
  }

}
