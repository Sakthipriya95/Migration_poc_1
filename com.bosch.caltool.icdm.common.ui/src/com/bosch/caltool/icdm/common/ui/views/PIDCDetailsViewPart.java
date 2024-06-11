/**
 *
 */
package com.bosch.caltool.icdm.common.ui.views;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.services.CommandState;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.wbutils.WorkbenchUtils;

/**
 * The view part of the PIDC Details page. <br>
 * Implementation with PageBookView,Refer PIDCDetailsPage.java has the part controls
 *
 * @author adn1cob
 */
public class PIDCDetailsViewPart extends AbstractPageBookView {


  /**
   * Unique ID for this View Part.
   */
  public static final String VIEW_ID = "com.bosch.caltool.icdm.common.ui.views.PIDCDetailsViewPart";


  /**
   * Constant for storing PIDC Details view menu
   */
  public static final String PIDC_DETAILS_SHOW_DELETED = "pidcDetailsShowDeleted";
  // ICDM-2190
  /**
   * Constant for storing PIDC Details view menu
   */
  public static final String PIDC_DETAILS_SHOW_UNCLEARED = "pidcDetailsShowUncleared";

  /**
   * CommandState instance
   */
  CommandState expReportService = new CommandState();

  // ICDM-1119
  /**
   * If true, then virtual structure is displayed(if present)
   */
  private boolean virStructDispEnbld = true;

  private IStructurePageCreator pageCreator;


  private final Map<IEditorWithStructure, Page> editorPageMap = new HashMap<>();

  /**
   * Enable the 'Export' button in the application toolbar when this view gets focus
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void setFocus() {
    // ICDM-865
    this.expReportService =
        (CommandState) com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils.getInstance().getSourceProvider();
    this.expReportService.setExportService(false);
    super.setFocus();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IPage createDefaultPage(final PageBook book) {
    // creare default page, when no pidc is active
    final MessagePage page = new MessagePage();
    initPage(page);
    page.createControl(book);
    // icdm-530
    setDefaultTitleToolTip();
    page.setMessage("There is no relevant part active!");


    return page;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final PageRec doCreatePage(final IWorkbenchPart part) {

    if (part instanceof IEditorWithStructure) {
      IEditorWithStructure sEditor = (IEditorWithStructure) part;
      this.pageCreator = sEditor.getStrucurePageCreator(this);
      Page page = this.pageCreator.createPage();

      this.editorPageMap.put(sEditor, page);
      PageRec pageRec = new PageRec(part, page);
      // add the page to list of pidc details page
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
    // remove the page from the list
    this.pageRecordList.remove(pageRecord);
    // dispose the page
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


  /**
   * Refresh the pages(for each PIDC) of the View
   */
  public void refreshPages() {
    // refresh all pidc details pages
    for (PageRec pageRec : super.pageRecordList) {

      IPage page = pageRec.page;
      if (page instanceof AbstractPage) {
        ((AbstractPage) page).refreshUI(null);
      }
      // check if instance of details page

    }
  }

  /**
   * iCDM-530 Tool tip text
   */
  private void setDefaultTitleToolTip() {
    String grpName = "PIDC_STRUCTURE";
    String name = "PIDC_STRUCTURE";
    try {
      setTitleToolTip(new CommonDataBO().getMessage(grpName, name));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  // ICDM-1119
  /**
   * @param virStructDispEnbld the virStructDispEnbld to set
   */
  public final void setVirStructDispEnbld(final boolean virStructDispEnbld) {
    this.virStructDispEnbld = virStructDispEnbld;
  }

  // ICDM-1119
  /**
   * @return true, if virtual structure display is enabled, else false
   */
  public static boolean isVirtualStructureDisplayEnabled() {
    // Get the instance of the View part, get the flag value from the instance
    IWorkbenchPart part = WorkbenchUtils.getView(PIDCDetailsViewPart.VIEW_ID);
    if (!(part instanceof PIDCDetailsViewPart)) {
      return true;
    }
    return ((PIDCDetailsViewPart) part).virStructDispEnbld;
  }

  /**
   * @return true, if deleted nodes can be displayed, else false
   */
  public static boolean isDeletedNodesDisplayEnabled() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(PIDC_DETAILS_SHOW_DELETED));
  }

  // ICDM-2198
  /**
   * @return true, if uncleared / missing mandatory variant nodes can be displayed, else false
   */
  public static boolean isUnclearedNodesDisplayEnabled() {
    return CommonUtils.isEqual(ApicConstants.CODE_YES,
        PlatformUI.getPreferenceStore().getString(PIDC_DETAILS_SHOW_UNCLEARED));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void initPage(final IPageBookViewPage page) {
    // TODO Auto-generated method stub
    super.initPage(page);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PageBook getPageBook() {
    // TODO Auto-generated method stub
    return super.getPageBook();
  }


  /**
   * @return the editorPageMap
   */
  public Map<IEditorWithStructure, Page> getEditorPageMap() {
    return this.editorPageMap;
  }


}
