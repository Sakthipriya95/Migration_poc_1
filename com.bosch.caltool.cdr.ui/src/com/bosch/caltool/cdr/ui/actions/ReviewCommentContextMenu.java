/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultBO;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.RvwUserCmntHistory;
import com.bosch.caltool.icdm.model.cdr.RvwCommentTemplate;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author PDH2COB
 */
public class ReviewCommentContextMenu {

  private final ReviewResultParamListPage page;


  /**
   * @param page Instance of ReviewResultParamListPage
   */
  public ReviewCommentContextMenu(ReviewResultParamListPage page) {
    this.page = page;
  }

  /**
   * @param menuManagr menu manager
   * @param selection  selection by user
   */
  public void setReviewCommentMenu(final MenuManager menuManagr, IStructuredSelection selection) {
    IMenuManager subMenuMgr = new MenuManager("Set Review Comment as");
    subMenuMgr.setRemoveAllWhenShown(true);
    ReviewResultBO resultBo = ((ReviewResultEditorInput) page.getEditorInput()).getResultData().getResultBo();

    subMenuMgr.addMenuListener(mgr -> {
      // add context menu option to all the scores available
      Map<Long, RvwUserCmntHistory> rvwCmntHistoryMap = null;
      Set<RvwCommentTemplate> rvwCmntTemplateSet = null;
      try {
        rvwCmntHistoryMap = new CommonDataBO().getRvwCommentHistoryForUser();
        rvwCmntTemplateSet = new CommonDataBO().getAllRvwCommentTemplate();

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }

      if (CommonUtils.isNotEmpty(rvwCmntHistoryMap)) {
        List<RvwUserCmntHistory> rvwCmntHistoryList = new ArrayList<>(rvwCmntHistoryMap.values());

        Collections.sort(rvwCmntHistoryList);
        Collections.reverse(rvwCmntHistoryList);

        for (RvwUserCmntHistory hist : rvwCmntHistoryList) {
          mgr.add(createSetCommentsAction(selection, hist.getRvwComment()));
        }
      }
      subMenuMgr.add(new Separator());
      
      //add templates as menu
      if (CommonUtils.isNotEmpty(rvwCmntTemplateSet)) {
        for (RvwCommentTemplate rvwCommentTemplate : rvwCmntTemplateSet) {
          mgr.add(createSetCommentsAction(selection, rvwCommentTemplate.getName()));
        }
      }

    });
    // visible only for users with access rights
    subMenuMgr.setVisible(resultBo.isModifiable());
    menuManagr.add(subMenuMgr);

  }

  /**
   * Create score action
   *
   * @param selection selcted params
   * @param score     score
   * @param cdrResult instance
   * @param page      page
   * @return action
   */
  private IAction createSetCommentsAction(IStructuredSelection selection, final String reviewCmnt) {
    Action lastCmntAction = new Action() {

      @Override
      public void run() {
        new ReviewResultNATActionSet().updateCommentsValueNew(selection, page, reviewCmnt);

      }
    };
    lastCmntAction.setEnabled(true);
    lastCmntAction.setText(reviewCmnt.length() > 200 ? reviewCmnt.substring(0, 200) + "..." : reviewCmnt);
    return lastCmntAction;
  }

}
