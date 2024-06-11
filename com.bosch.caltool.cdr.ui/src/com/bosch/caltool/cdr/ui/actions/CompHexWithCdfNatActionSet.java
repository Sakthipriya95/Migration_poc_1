/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdr.ui.editors.CompHexWithCDFxEditor;
import com.bosch.caltool.icdm.client.bo.comphex.CompHexWithCDFxDataHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.CalDataViewerDialog;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.comphex.CompHexWithCDFParam;

/**
 * Context menu action set of CompHEXWtihCDFParam.
 *
 * @author svj7cob
 */
// ICDM-2498
public class CompHexWithCdfNatActionSet {

  /** the green color to indicate the Reviewed value. */
  private static final int GREEN_COLOR_CODE = 2;
  private final CompHexWithCDFxDataHandler compHexDataHdlr;

  /**
   * @param compHexDataHdlr
   */
  public CompHexWithCdfNatActionSet(final CompHexWithCDFxDataHandler compHexDataHdlr) {
    this.compHexDataHdlr = compHexDataHdlr;
  }

  /**
   * This method for the normal table graph view.
   *
   * @param menuManagr the menu managr
   * @param firstElement the first element
   * @param compHexWithCDFxEditor the comp hex with CD fx editor
   */
  public void showUnSynchronizedTableGraph(final MenuManager menuManagr, final Object firstElement,
      final CompHexWithCDFxEditor compHexWithCDFxEditor) {
    final Action showGraph = new Action() {

      /**
       * To show the un-synchronized table graph dialog for the selection Comp HEX With cdfx parameter
       */
      @Override
      public void run() {
        if (firstElement instanceof CompHexWithCDFParam) {
          showTableGraphAction((CompHexWithCDFParam) firstElement, compHexWithCDFxEditor, false);
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
   * @param compHexWithCDFxEditor the comp hex with CD fx editor
   */
  public void showSynchronizedTableGraph(final MenuManager menuManagr, final Object firstElement,
      final CompHexWithCDFxEditor compHexWithCDFxEditor) {
    final Action showGraph = new Action() {

      /**
       * To show the synchronized table graph dialog for the selection Comp HEX With cdfx parameter
       */
      @Override
      public void run() {
        if (firstElement instanceof CompHexWithCDFParam) {
          showTableGraphAction((CompHexWithCDFParam) firstElement, compHexWithCDFxEditor, true);
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
   * @param compHexWithCDFxEditor the comp hex with CD fx editor
   * @param isSynchDialogNeeded the is synch dialog needed
   */
  // iCDM-1408
  public void showTableGraphAction(final CompHexWithCDFParam firstElement,
      final CompHexWithCDFxEditor compHexWithCDFxEditor, final boolean isSynchDialogNeeded) {
    CompHexWithCDFParam compHEXWtihCDFParam = firstElement;
    ConcurrentMap<String, CalData> calDataMap = new ConcurrentHashMap<>();
    CalData hexCalData = null;
    hexCalData = this.compHexDataHdlr.getHexCalData(compHEXWtihCDFParam);
    CalData rvwdCalData = null;
    rvwdCalData = this.compHexDataHdlr.getCdfxCalData(compHEXWtihCDFParam);

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
    if (isUnSynchDialogNeeded || (null == compHexWithCDFxEditor.getSynchCalDataViewerDialog()) ||
        (null == compHexWithCDFxEditor.getSynchCalDataViewerDialog().getShell())) {
      // case-1 covers
      // case-4 covers
      calDataViewerDialog =
          new CalDataViewerDialog(Display.getCurrent().getActiveShell(), calDataMap, compHEXWtihCDFParam.getParamName(),
              compHexWithCDFxEditor.getEditorInput().getName(), isSynchDialogNeeded, true);
      Map<String, Integer> colorMap = getConstructColorCodeMap();
      calDataViewerDialog.setColorMap(colorMap);
      if (isSynchDialogNeeded) {
        // storing the state of cal_data_viewer_dialog insance in rvwResult editor
        compHexWithCDFxEditor.setSynchCalDataViewerDialog(calDataViewerDialog);
      }
      else {
        compHexWithCDFxEditor.setUnSynchCalDataViewerDialog(calDataViewerDialog);
      }

      // Task 234466 include T/G viewer V1.9.0
      calDataViewerDialog.setCharacteristicsMap(compHexWithCDFxEditor.getEditorInput().getCompHexDataHdlr()
          .getCdrReportData().getA2lEditorDataProvider().getA2lFileInfoBO().getCharacteristicsMap());
      calDataViewerDialog.open();
    }
    else {
      // this condition will be true if the CalDataViewerDialog is already opened for one row(ie, param) in Rvw Result
      // Editor and we are clicking on some other row
      // case-2 & case-3 covers
      if (!compHexWithCDFxEditor.getSynchCalDataViewerDialog().getParamName()
          .equals(compHEXWtihCDFParam.getParamName())) {
        calDataViewerDialog = compHexWithCDFxEditor.getSynchCalDataViewerDialog();

        // to clear the table graph viewer and override with new data
        calDataViewerDialog.getTableGraphComposite().clearTableGraph();

        calDataViewerDialog.setParamName(compHEXWtihCDFParam.getParamName());
        calDataViewerDialog.setCalDataMap(calDataMap);

        Map<String, Integer> colorMap = getConstructColorCodeMap();
        calDataViewerDialog.setColorMap(colorMap);

        // storing the state of cal_data_viewer_dialog instance in rvwResult compHexWithCDFxEditor
        compHexWithCDFxEditor.setSynchCalDataViewerDialog(calDataViewerDialog);

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
