/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.editors.DataAssessmentReportEditor;
import com.bosch.caltool.icdm.client.bo.cdr.DataAssmntReportDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.dataassessment.DaCompareHexParam;

/**
 * Context menu action set of CompHEXWtihCDFParam.
 *
 * @author svj7cob
 */
// ICDM-2498
public class DaCompHexNatActionSet {

  /** the green color to indicate the Reviewed value. */
  private static final int GREEN_COLOR_CODE = 2;
  private final DataAssmntReportDataHandler dataAssmntReportDataHandler;

  /**
   * @param dataAssmntReportDataHandler CompHexWithCDFxDataHandler
   */
  public DaCompHexNatActionSet(final DataAssmntReportDataHandler dataAssmntReportDataHandler) {
    this.dataAssmntReportDataHandler = dataAssmntReportDataHandler;
  }

  /**
   * This method for the normal table graph view.
   *
   * @param menuManagr the menu managr
   * @param firstElement the first element
   * @param dataAssessmentReportEditor the comp hex with CD fx editor
   */
  public void showUnSynchronizedTableGraph(final MenuManager menuManagr, final Object firstElement,
      final DataAssessmentReportEditor dataAssessmentReportEditor) {
    final Action showGraph = new Action() {

      /**
       * To show the un-synchronized table graph dialog for the selection Comp HEX With cdfx parameter
       */
      @Override
      public void run() {
        if (firstElement instanceof DaCompareHexParam) {
          showTableGraphAction((DaCompareHexParam) firstElement, dataAssessmentReportEditor, false);
        }
      }
    };
    showGraph.setText("Show in Table/Graph viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    showGraph.setEnabled(true);
    showGraph.setImageDescriptor(imageDesc);
    menuManagr.add(showGraph);
  }

  /**
   * This method for the synchronized table graph view.
   *
   * @param menuManagr the menu managr
   * @param firstElement the first element
   * @param dataAssessmentReportEditor the comp hex with CD fx editor
   */
  public void showSynchronizedTableGraph(final MenuManager menuManagr, final Object firstElement,
      final DataAssessmentReportEditor dataAssessmentReportEditor) {
    final Action showGraph = new Action() {

      /**
       * To show the synchronized table graph dialog for the selection Comp HEX With cdfx parameter
       */
      @Override
      public void run() {
        if (firstElement instanceof DaCompareHexParam) {
          showTableGraphAction((DaCompareHexParam) firstElement, dataAssessmentReportEditor, true);
        }
      }
    };
    showGraph.setText("Show in Synchronized Table/Graph viewer");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.TABLE_GRAPH_16X16);
    showGraph.setEnabled(true);
    showGraph.setImageDescriptor(imageDesc);
    menuManagr.add(showGraph);
  }

  /**
   * Show table graph action.
   *
   * @param firstElement the first element
   * @param dataAssessmentReportEditor the comp hex with CD fx editor
   * @param isSynchDialogNeeded the is synch dialog needed
   */
  // iCDM-1408
  public void showTableGraphAction(final DaCompareHexParam firstElement,
      final DataAssessmentReportEditor dataAssessmentReportEditor, final boolean isSynchDialogNeeded) {
    DaCompareHexParam compHEXWtihCDFParam = firstElement;
    ConcurrentMap<String, CalData> calDataMap = new ConcurrentHashMap<>();

    CalData hexCalData = null;
    CalData rvwdCalData = null;
    try {
      hexCalData = CalDataUtil.getCalDataObj(firstElement.getHexValue());
      rvwdCalData = CalDataUtil.getCalDataObj(firstElement.getReviewedValue());
    }
    catch (ClassNotFoundException | IOException exp) {
      CDMLogger.getInstance().error("Exception occured while creating byte array for caldata obj" + exp.getMessage(),
          exp);
    }

    // constructing cal data map
    if (hexCalData != null) {
      calDataMap.put(CommonUIConstants.HEX_VALUE, hexCalData);
    }
    if (rvwdCalData != null) {
      calDataMap.put(CommonUIConstants.RVWD_VALUE, rvwdCalData);
    }
    // case-1 : no dialog had opened, opening thro' context menu
    // case-2 : Dialog already opened position, opening thro' context menu->Table/graph view right mouse click
    // case-3 : Dialog already opened position, opening thro' param selection left mouse click

    // case-4 : if individual dialog needed, then new dialog will be opened
    CalDataViewerDialog calDataViewerDialog;
    boolean isUnSynchDialogNeeded = !isSynchDialogNeeded;
    if (isUnSynchDialogNeeded || (null == dataAssessmentReportEditor.getSynchCalDataViewerDialog()) ||
        (null == dataAssessmentReportEditor.getSynchCalDataViewerDialog().getShell())) {
      // case-1 covers
      // case-4 covers
      calDataViewerDialog =
          new CalDataViewerDialog(Display.getCurrent().getActiveShell(), calDataMap, compHEXWtihCDFParam.getParamName(),
              dataAssessmentReportEditor.getEditorInput().getName(), isSynchDialogNeeded, true);
      Map<String, Integer> colorMap = getConstructColorCodeMap();
      calDataViewerDialog.setColorMap(colorMap);
      if (isSynchDialogNeeded) {
        // storing the state of cal_data_viewer_dialog insance in rvwResult editor
        dataAssessmentReportEditor.setSynchCalDataViewerDialog(calDataViewerDialog);
      }
      else {
        dataAssessmentReportEditor.setUnSynchCalDataViewerDialog(calDataViewerDialog);
      }

      // Task 234466 include T/G viewer V1.9.0
      calDataViewerDialog.setCharacteristicsMap(
          dataAssessmentReportEditor.getEditorInput().getDataAssmntReportDataHandler().getCharacteristicsMap());
      calDataViewerDialog.open();
    }
    else {
      // this condition will be true if the CalDataViewerDialog is already opened for one row(ie, param) in Rvw Result
      // Editor and we are clicking on some other row
      // case-2 & case-3 covers
      if (!dataAssessmentReportEditor.getSynchCalDataViewerDialog().getParamName()
          .equals(compHEXWtihCDFParam.getParamName())) {
        calDataViewerDialog = dataAssessmentReportEditor.getSynchCalDataViewerDialog();

        // to clear the table graph viewer and override with new data
        calDataViewerDialog.getTableGraphComposite().clearTableGraph();

        calDataViewerDialog.setParamName(compHEXWtihCDFParam.getParamName());
        calDataViewerDialog.setCalDataMap(calDataMap);

        Map<String, Integer> colorMap = getConstructColorCodeMap();
        calDataViewerDialog.setColorMap(colorMap);

        // storing the state of cal_data_viewer_dialog instance in rvwResult DataAssessmentReportEditor
        dataAssessmentReportEditor.setSynchCalDataViewerDialog(calDataViewerDialog);

        // giving as input the dialog
        calDataViewerDialog.populateData(calDataViewerDialog.getArea());
      }
    }
  }

  /**
   * This method return the color code for the hex value and the cdfx value.
   *
   * @return maps of color code
   */
  private Map<String, Integer> getConstructColorCodeMap() {
    Map<String, Integer> colorMap = new HashMap<>();
    // given 0 to indicate the HEX Value in blue color
    colorMap.put(CommonUIConstants.HEX_VALUE, 0);

    // given 2 to indicate the Reviewed Value in green color
    colorMap.put(CommonUIConstants.RVWD_VALUE, GREEN_COLOR_CODE);
    return colorMap;
  }
}
