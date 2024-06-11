/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamDetPage;
import com.bosch.caltool.icdm.client.bo.ss.CalDataType;
import com.bosch.caltool.icdm.client.bo.ss.SeriesStatisticsInfo;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.ruleseditor.actions.ReviewRuleToolBarActionSet;
import com.bosch.caltool.icdm.ruleseditor.pages.DetailsPage;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;


/**
 * Action class for rules editor
 *
 * @author rgo7cob
 */
// ICDM-500
public class ReviewParamToolBarActionSet extends ReviewRuleToolBarActionSet { // NOPMD by dmo5cob on 4/2/14 2:29 PM

  /**
   * Assesment Action
   */
  private Action saveAssesment;

  /**
   * @param statusLineManager statusLineManager
   */
  public ReviewParamToolBarActionSet(final IStatusLineManager statusLineManager, final AbstractFormPage page) {
    super(statusLineManager, page);
  }


  /**
   * ICdm-1056 Edit rule dialog
   *
   * @return is saved or not
   */
  @Override
  public boolean saveReviewDataChanges() {
    return true;
  }

  /**
   * Icdm-552
   *
   * @return saveReviewData
   */
  public Action getAssesmentAction() {
    return this.saveAssesment;
  }


  /**
   * adds dragging to strachpad feature
   *
   * @param transferTypes transferTypes
   * @param textField Text
   * @param appendField Fields name to be appended
   * @param cdrResult cdrResult
   */

  public void dragToScratchPad(final Transfer[] transferTypes, final StyledText textField, final String appendField,
      final CDRReviewResult cdrResult) { // NOPMD

    final int operations = DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_LINK;
    final DragSource source = new DragSource(textField, operations);
    source.setTransfer(transferTypes);
    source.addDragListener(new DragSourceListener() {

      @Override
      public void dragStart(final DragSourceEvent event) {
        final String value = textField.getText();
        // set drag false , if the dragged value is empty or n.a
        if (("").equals(value) || ("n.a.").equalsIgnoreCase(value) || value.endsWith("%")) {
          event.doit = false;
        }
        else {
          event.doit = true;
        }
      }

      @Override
      public void dragSetData(final DragSourceEvent event) {

        CalData calData = null;
        // only ref value can be dragged from details page
        // get the ref value obj
        if (ReviewParamToolBarActionSet.this.page instanceof DetailsPage) {
          calData = ((DetailsPage) ReviewParamToolBarActionSet.this.page).getRefValCalDataObj();
        }
        // if param details page then all four objs can be dropped into scratchpad
        ReviewResultParamDetPage reviewResultParamDetPage =
            (ReviewResultParamDetPage) ReviewParamToolBarActionSet.this.page;
        if (ReviewParamToolBarActionSet.this.page instanceof ReviewResultParamDetPage) {
          // get the ref value obj
          if (CommonUtils.isEqual(reviewResultParamDetPage.getRefValueTxt(), textField)) {
            calData = reviewResultParamDetPage.getResultData()
                .getRefValueObj(reviewResultParamDetPage.getCdrResultParameter());
          }
          // get the parent ref value obj
          else if (CommonUtils.isEqual(reviewResultParamDetPage.getParRefValText(), textField)) {
            calData = reviewResultParamDetPage.getResultData()
                .getParentRefVal(reviewResultParamDetPage.getCdrResultParameter());
          }
          // get the parent check value obj
          else if (CommonUtils.isEqual(reviewResultParamDetPage.getParChkValText(), textField)) {
            calData = reviewResultParamDetPage.getResultData()
                .getParentCheckedVal(reviewResultParamDetPage.getCdrResultParameter());
          }
          // get the check value obj
          else {
            calData = reviewResultParamDetPage.getResultData()
                .getCheckedValueObj(reviewResultParamDetPage.getCdrResultParameter());
          }

        }
        if (null != calData) {
          CalData calDataObject;
          try {
            calDataObject = CalDataUtil.getCalDataHistoryDetails(new UserServiceClient().getCurrentApicUser().getName(),
                calData, appendField, null, null);
            event.data = calDataObject;
            SeriesStatisticsInfo calDataProvider = null;
            // create series statistics info object based on the dragged obj type
            if (ReviewParamToolBarActionSet.this.page instanceof DetailsPage) {
              calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.REF_VALUE);
            }
            else if (ReviewParamToolBarActionSet.this.page instanceof ReviewResultParamDetPage) {
              if (CommonUtils.isEqual(reviewResultParamDetPage.getRefValueTxt(), textField) ||
                  CommonUtils.isEqual(reviewResultParamDetPage.getParRefValText(), textField)) {
                calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.REF_VALUE);
              }
              else {
                calDataProvider = new SeriesStatisticsInfo(calDataObject, CalDataType.CHECK_VALUE);
              }
            }
            if (null != calDataProvider) {
              // set the result name
              calDataProvider.setDataSetName(cdrResult.getName());
              // set the selection obj
              final StructuredSelection struSelection = new StructuredSelection(calDataProvider);
              LocalSelectionTransfer.getTransfer().setSelection(struSelection);
            }
          }
          catch (ApicWebServiceException e) {
            CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
          }
        }
      }

      @Override
      public void dragFinished(final DragSourceEvent event) {
        // TODO:
      }
    });
  }
}
