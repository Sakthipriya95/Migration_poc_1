/**
 *
 */
package com.bosch.caltool.icdm.product.menuactions;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.actions.ContributionItemFactory;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.product.Activator;
import com.bosch.caltool.icdm.product.actions.CaldataAnalyzerAction;
import com.bosch.caltool.icdm.product.actions.CitiToolHotlineOpenAction;
import com.bosch.caltool.icdm.product.actions.AdminDeleteReviewResultAction;
import com.bosch.caltool.icdm.product.actions.IcdmLinkOpenDialogAction;
import com.bosch.caltool.icdm.product.actions.ImportA2lRespMergeDataDialogAction;
import com.bosch.caltool.icdm.product.actions.NodeAccessMgmtEditorOpenAction;
import com.bosch.caltool.icdm.product.actions.OSSComponentInfoDisplayAction;
import com.bosch.caltool.icdm.product.actions.OSSFileOpenAction;
import com.bosch.caltool.icdm.product.actions.OpenCreateRuleSetDialogAction;
import com.bosch.caltool.icdm.product.actions.OpenHotLineMailAction;
import com.bosch.caltool.icdm.product.actions.OpenLinkAction;
import com.bosch.caltool.icdm.product.actions.OpenUnmapA2lEditorAction;
import com.bosch.caltool.icdm.product.util.ICDMConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * MenuBarActions.java This class configures and handles all MenuBar Actions.
 *
 * @author adn1cob
 */
public class MenuBarActions extends ActionBarAdvisor {

  /**
   * Member variable referencing EXIT Action.
   */
  private IWorkbenchAction exitAction;

  /**
   * Member variable referencing PREFERENCE Action.
   */
  private IWorkbenchAction preferenceAction;

  /**
   * Member variable referencing ABOUT Action.
   */
  private IWorkbenchAction aboutAction;

  /**
   * Member variable referencing HELP Action.
   */
  private IWorkbenchAction helpAction;

  /**
   * Member variable referencing SHOW-VIEW Action.
   */
  private IContributionItem ciShowView;

  /**
   * Show perspective Contribution item.
   */
  private IContributionItem ciShowPersp;

  /**
   * Reset perspective action.
   */
  private IWorkbenchAction resetPerspAction;

  /**
   * Welcome page action.
   */
  private IWorkbenchAction introAction;

  /**
   * Hot line mail action.
   */
  private IWorkbenchAction hotLineMailAction;

  /**
   * Hot line mail action.
   */
  private IWorkbenchAction citiToolHotlineAction;

  /*
   * OSS Document download action
   */
  private IWorkbenchAction ossFileOpenAction;
  /**
   * ICDM Link open dialog Action
   */
  // ICDM-2242
  private IcdmLinkOpenDialogAction icdmLinkOpnDlgAction;

  // iCDM-1306
  /**
   * Action for opening caldata analyzer
   */
  private CaldataAnalyzerAction caldataAnalyzerAction;

  /**
   * Action for opening administrator action
   */
  private NodeAccessMgmtEditorOpenAction openAdministrationAction;

  /**
   * Action for opening unmap a2l admin
   */
  private OpenUnmapA2lEditorAction openUnmapA2lAdminEditorAction;

  /**
   * Action to enter execution id to import a2l resp merge data
   */
  private ImportA2lRespMergeDataDialogAction importA2lRespMergeDataDialogAction;

  /**
   * Action to create ruleset
   */
  private OpenCreateRuleSetDialogAction openCreateRuleSetDialogAction;

  /**
   * Action for opening iCDM Wiki page
   */
  private IWorkbenchAction openiCDMWikiAction;
  /**
   * Support dashboard link action
   */
  private IWorkbenchAction openSupportLinkAction;
  /**
   * Action for opening iCDM Contacts page
   */
  private IWorkbenchAction openiCDMContactsAction;

  private OSSComponentInfoDisplayAction ossComponentInfoDisplayAction;

  private AdminDeleteReviewResultAction deleteReviewAction;

  /**
   * Parameterized Constructor.
   *
   * @param configurer the action bar configurer
   */
  public MenuBarActions(final IActionBarConfigurer configurer) {
    super(configurer);
  }

  /**
   * Method to Configure Menu Bar.
   *
   * @param menuBar the menu bar instance
   */
  public final void setMenuBar(final IMenuManager menuBar) {

    /* ====Configure Menu items========= */

    /* ----- File Menu ----- */
    final MenuManager fileMenu = new MenuManager(ICDMConstants.FILE, IWorkbenchActionConstants.M_FILE);
    // add EXIT action to FILE menu

    // ICDM-2242
    fileMenu.add(this.icdmLinkOpnDlgAction);
    fileMenu.add(new Separator());

    if (checkIfUserHasCalDataAnalyzerAccess()) {
      fileMenu.add(this.caldataAnalyzerAction);
      fileMenu.add(new Separator());
    }

    fileMenu.add(this.exitAction);

    menuBar.add(fileMenu);

    if (hasSpecialAdminAccess()) {
      /* ----- Admin Menu ----- */
      final MenuManager adminMenu = new MenuManager(ICDMConstants.ADMIN, IWorkbenchActionConstants.M_FILE);
      adminMenu.add(this.openAdministrationAction);
      adminMenu.add(new Separator());
      adminMenu.add(this.openUnmapA2lAdminEditorAction);
      adminMenu.add(this.importA2lRespMergeDataDialogAction);
      adminMenu.add(new Separator());
      adminMenu.add(this.openCreateRuleSetDialogAction);
      adminMenu.add(new Separator());
      adminMenu.add(this.deleteReviewAction);

      menuBar.add(adminMenu);
    }


    /* ------- Window Menu ---- */
    final MenuManager windowMenu = new MenuManager(ICDMConstants.WINDOW, IWorkbenchActionConstants.M_WINDOW);
    // Create menu for SHOW-VIEW
    final MenuManager showViewMenuMgr = new MenuManager("Show View", "showView");
    showViewMenuMgr.add(this.ciShowView);
    windowMenu.add(showViewMenuMgr);

    // Add separator
    windowMenu.add(new Separator());

    // Create menu for Open Perspective
    final MenuManager showPerspMenuMgr = new MenuManager("Open Perspective", "openPersp");
    showPerspMenuMgr.add(this.ciShowPersp);
    windowMenu.add(showPerspMenuMgr);

    // Reset Perspective action
    windowMenu.add(this.resetPerspAction);

    // Add separator
    windowMenu.add(new Separator());

    // Add PREFERENCE action
    windowMenu.add(this.preferenceAction);

    menuBar.add(windowMenu);

    /* ------ Help Menu ------ */
    final MenuManager helpMenu = new MenuManager(ICDMConstants.HELP, IWorkbenchActionConstants.M_HELP);

    // add INTRO action
    helpMenu.add(this.introAction);

    helpMenu.add(new Separator());

    // add CITI-Tool Hotline Support action
    try {
      if (new CurrentUserBO().canAccessCitiToolHotline()) {
        helpMenu.add(this.citiToolHotlineAction);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
    }

    // add Hotline Mail action
    helpMenu.add(this.hotLineMailAction);

    // add link to 'Support Dashboard Tool'
    try {
      if (canAccessSupportDashBoard()) {
        helpMenu.add(this.openSupportLinkAction);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), Activator.PLUGIN_ID);
    }

    helpMenu.add(new Separator());

    // iCDM-1306
    // Add icdm help links
    helpMenu.add(this.openiCDMWikiAction);
    helpMenu.add(this.openiCDMContactsAction);

    helpMenu.add(new Separator());

    // Oss report action
    helpMenu.add(this.ossFileOpenAction);

    helpMenu.add(new Separator());

    // Oss Information display action
    helpMenu.add(this.ossComponentInfoDisplayAction);

    helpMenu.add(new Separator());

    // add HELP action
    helpMenu.add(this.helpAction);

    helpMenu.add(new Separator());

    // add ABOUT action to HELP menu
    helpMenu.add(this.aboutAction);


    // Finally add HELP to MenuBar
    menuBar.add(helpMenu);

  }

  /**
   * Method to Trigger and Register MENU Actions.
   *
   * @param window the workbench window instance
   */
  public final void setActions(final IWorkbenchWindow window) {
    // Implement and Register EXIT Action
    this.exitAction = ActionFactory.QUIT.create(window);
    register(this.exitAction);

    // ICDM-2242
    this.icdmLinkOpnDlgAction = new IcdmLinkOpenDialogAction();
    register(this.icdmLinkOpnDlgAction);

    // caldata analyzer action
    this.caldataAnalyzerAction = new CaldataAnalyzerAction();
    register(this.caldataAnalyzerAction);

    // Open Admin action
    this.openAdministrationAction = new NodeAccessMgmtEditorOpenAction();
    register(this.openAdministrationAction);

    // Open Unmap a2l Admin Editor action
    this.openUnmapA2lAdminEditorAction = new OpenUnmapA2lEditorAction();
    register(this.openUnmapA2lAdminEditorAction);

    // Merge A2l Responsibility
    this.importA2lRespMergeDataDialogAction = new ImportA2lRespMergeDataDialogAction();
    register(this.importA2lRespMergeDataDialogAction);

    // Create Ruleset
    this.openCreateRuleSetDialogAction = new OpenCreateRuleSetDialogAction();
    register(this.openCreateRuleSetDialogAction);

    // Show Views
    this.ciShowView = ContributionItemFactory.VIEWS_SHORTLIST.create(window);

    // Show perspectives
    this.ciShowPersp = ContributionItemFactory.PERSPECTIVES_SHORTLIST.create(window);

    // Reset perspective action
    this.resetPerspAction = ActionFactory.RESET_PERSPECTIVE.create(window);
    register(this.resetPerspAction);

    // Implement and Register WINDOW Action
    this.preferenceAction = ActionFactory.PREFERENCES.create(window);
    register(this.preferenceAction);


    // Implement and register HOME Action
    this.introAction = ActionFactory.INTRO.create(window);
    register(this.introAction);

    // Implement and Register HELP Action
    this.helpAction = ActionFactory.HELP_CONTENTS.create(window);
    register(this.helpAction);

    this.citiToolHotlineAction = new CitiToolHotlineOpenAction();
    register(this.citiToolHotlineAction);

    this.hotLineMailAction = new OpenHotLineMailAction();
    register(this.hotLineMailAction);

    // Implement and register open link actions
    this.openiCDMWikiAction = new OpenLinkAction(CommonParamKey.ICDM_WIKI_LINK, "iCDM Wiki page",
        ImageManager.getImageDescriptor(ImageKeys.ICDM_WIKI_16X16));
    register(this.openiCDMWikiAction);

    this.openiCDMContactsAction = new OpenLinkAction(CommonParamKey.ICDM_CONTACTS_LINK, "iCDM Contacts page",
        ImageManager.getImageDescriptor(ImageKeys.ICDM_CONTACTS_16X16));
    register(this.openiCDMContactsAction);

    // Implement and register OSS document download action
    this.ossFileOpenAction = new OSSFileOpenAction();
    register(this.ossFileOpenAction);

    // Implement and register Support Dashboard link action
    this.openSupportLinkAction = new OpenLinkAction(CommonParamKey.SUPPORT_DASHBOARD_LINK, "Support Dashboard",
        ImageManager.getImageDescriptor(ImageKeys.SUPPORT_DASHBOARD_16X16));
    register(this.openSupportLinkAction);

    // Implement and Register ABOUT Action
    this.aboutAction = ActionFactory.ABOUT.create(window);
    this.aboutAction.setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ICDM_LOGO_16X16));
    register(this.aboutAction);

    // create a action object that to dispaly oss information
    this.ossComponentInfoDisplayAction = new OSSComponentInfoDisplayAction();
    register(this.ossComponentInfoDisplayAction);

    this.deleteReviewAction = new AdminDeleteReviewResultAction();
    register(this.deleteReviewAction);

  }


  private boolean checkIfUserHasCalDataAnalyzerAccess() {
    boolean isRead = false;
    try {
      Long cdaNodeId = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.CALDATAANALYZER_NODE_ID));
      isRead = new CurrentUserBO().hasNodeReadAccess(cdaNodeId);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return isRead;
  }

  private boolean hasSpecialAdminAccess() {
    try {
      return new CurrentUserBO().hasSpecialAdminAccess();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * @return boolean true is Support Dashboard configured with 'Y'
   * @throws ApicWebServiceException Exception from service
   */
  public boolean canAccessSupportDashBoard() throws ApicWebServiceException {
    String supportVisibility = new CommonDataBO().getParameterValue(CommonParamKey.SUPPORT_DASHBOARD_VISIBILITY);

    return ApicConstants.SUPPORT_DASHBOARD_VISIBLE.equals(supportVisibility);
  }

}
