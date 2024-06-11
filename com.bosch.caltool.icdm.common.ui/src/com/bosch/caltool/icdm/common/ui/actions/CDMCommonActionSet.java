/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.axis2.AxisFault;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.calcomp.caldataanalyzer.vo.LabelInfoVO;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.apic.ws.client.serviceclient.APICWebServiceClient;
import com.bosch.caltool.icdm.client.bo.a2l.A2LParameter;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.providers.ScratchPadDataFetcher;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.IMessageConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.ui.views.FCBCUsageViewPart;
import com.bosch.caltool.icdm.common.ui.views.SeriesStatisticsViewPart;
import com.bosch.caltool.icdm.common.ui.views.data.SeriesStatCache;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.FCBCUsage;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.DefaultRuleDefinition;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVcdmTransferServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.vcdm.VcdmAprjServiceClient;
import com.bosch.rcputils.browser.BrowserUtil;


/**
 * Icdm-521 Moved the class to common UI
 *
 * @author mga1cob
 */
// ICDM-218
public class CDMCommonActionSet {

  /**
   * Define mutex rule for LIS file creation
   */
  final MutexRule createLISRule = new MutexRule();

  /**
   * This method adds righ click FC/BC Usage menu to the tableviewer
   *
   * @param menuMgr defines IMenuManager
   * @param selectedObject selected item instance from FC/BC tableViewer
   * @param isEnable defines whether show FC/BC Usage action should be enable or not
   */
  public void addFCBCUsageMenuAction(final IMenuManager menuMgr, final Object selectedObject, final boolean isEnable) {

    final Action showFCBCUsageAction = new Action() {

      @Override
      public void run() {
        try {
          // initialise FCBCUsageViewPart
          final FCBCUsageViewPart fcBcUsageViewPart = (FCBCUsageViewPart) PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage().showView(CommonUIConstants.FCBC_USAGE_VIEW_ID);
          if (selectedObject != null) {
            String sectionTitle;
            String labelName;
            if (selectedObject instanceof Function) {
              // if the selected object is a Function
              Function function = (Function) selectedObject;
              labelName = function.getName();
              if (labelName != null) {
                // fetch fc bc usage
                fetchFCBCUsageDetails(fcBcUsageViewPart, "FC", labelName);
                // section title
                sectionTitle = "FC Usage - " + labelName;
                fcBcUsageViewPart.fillUI(sectionTitle);
              }
            }
            else if (selectedObject instanceof com.bosch.caltool.icdm.model.a2l.A2LBaseComponents) {
              // if the selected object is A2LBaseComponents
              com.bosch.caltool.icdm.model.a2l.A2LBaseComponents a2lBC =
                  (com.bosch.caltool.icdm.model.a2l.A2LBaseComponents) selectedObject;
              labelName = a2lBC.getBcName();
              if (labelName != null) {
                fetchFCBCUsageDetails(fcBcUsageViewPart, "BC", labelName);
                // section title
                sectionTitle = "BC Usage - " + labelName;
                fcBcUsageViewPart.fillUI(sectionTitle);
              }
            }
          }
        }
        catch (PartInitException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    showFCBCUsageAction.setText("Show FC/BC Usage");
    menuMgr.add(showFCBCUsageAction);
    // get the image for fc/bc usage
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.FC_BC_USAGE_16X16);
    showFCBCUsageAction.setImageDescriptor(imageDesc);
    showFCBCUsageAction.setEnabled(isEnable);

  }


  /**
   * This method fetches FC/BC usage Details information from DB and sets the fcBc usage data in FCBCUsageViewPart
   *
   * @param seriesStatViewPart
   * @param labelName
   */
  // ICDM-218
  private void fetchFCBCUsageDetails(final FCBCUsageViewPart fcBcUsageViewPart, final String type,
      final String labelName) {
    // initialise progress monitor dialog
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
    try {
      dialog.run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          monitor.beginTask("Fetching " + type + " information for - " + labelName, IProgressMonitor.UNKNOWN);
          try {
            A2LFileInfoServiceClient client = new A2LFileInfoServiceClient();
            List<FCBCUsage> fcUsages = null;
            if ("FC".equals(type)) {
              // for FC type
              fcUsages = client.getFCUsage(labelName);
            }
            else if ("BC".equals(type)) {
              // for BC type
              fcUsages = client.getBCUsage(labelName);
            }
            fcBcUsageViewPart.setFCBCUsageDetails(fcUsages);
          }
          catch (Exception exp) {
            CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
          }
          monitor.done();
          if (monitor.isCanceled()) {
            // when the progress monitor is cancelled
            try {
              throw new InterruptedException("Fetching " + type + " information for - " + labelName + " was cancelled");
            }
            catch (InterruptedException exp) {
              CDMLogger.getInstance().info(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
            }
          }
        }
      });
    }
    catch (InvocationTargetException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    catch (InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method adds righ click series statistics menu to the tableviewer
   *
   * @param menuMgr defines IMenuManager
   * @param selectedObjects selected item instance from paramters tableViewer
   * @param isEnable defines whether show series statistics action should be enable or not
   */
  // ICDM-218
  public void addShowSeriesStatisticsMenuAction(final IMenuManager menuMgr, final List<Object> selectedObjects,
      final boolean isEnable) {

    final Action showSeriesStatisticsAction = new Action() {

      @Override
      public void run() {
        try {
          // initialise series statistics view part
          final SeriesStatisticsViewPart seriesStatViewPart = (SeriesStatisticsViewPart) PlatformUI.getWorkbench()
              .getActiveWorkbenchWindow().getActivePage().showView(CommonUIConstants.SERIES_STATISTICS_VIEW_ID);
          if (selectedObjects != null) {
            List<String> labelNames = new ArrayList<>();
            for (Object object : selectedObjects) {
              // add all the selected elements to the list
              String labelName = getLabelName(object);
              // ICDM-1006
              if (labelName != null) {
                labelNames.add(labelName);
              }
            } // ICDM-935
            String className = getClassName(selectedObjects.get(0));

            if (!labelNames.isEmpty()) {
              invokeWSClientForStatistics(seriesStatViewPart, labelNames);

              // ICDM-913
              // fill series statistics cache
              seriesStatViewPart.fillSeriesStatisticsCacheMenu();

              // ICDM-935
              seriesStatViewPart.setParamClassName(className);
              updateSeriesStatisticsViewUI(seriesStatViewPart, labelNames.get(labelNames.size() - 1), false);
            }
          }

        }
        catch (PartInitException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }
      }
    };
    showSeriesStatisticsAction.setText("Show Series Statistics");
    menuMgr.add(showSeriesStatisticsAction);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.SERIES_STATISTICS_16X16);
    showSeriesStatisticsAction.setImageDescriptor(imageDesc);
    showSeriesStatisticsAction.setEnabled(isEnable);

  }

  /**
   * This method invokes Parameter series statistics Web Service and sets the series statiscs information
   *
   * @param seriesStatViewPart
   * @param labelNames
   */
  // ICDM-218
  private void invokeWSClientForStatistics(final SeriesStatisticsViewPart seriesStatViewPart,
      final List<String> labelNames) {
    final ProgressMonitorDialog dialog = new ProgressMonitorDialog(Display.getCurrent().getActiveShell());
    try {
      dialog.run(true, true, new IRunnableWithProgress() {

        @Override
        public void run(final IProgressMonitor monitor) {
          for (String labelName : labelNames) {
            monitor.beginTask("Analyzing statistical information for - " + labelNames, IProgressMonitor.UNKNOWN);
            // invoke web service client to get the statistics
            invokeWSClient(seriesStatViewPart, monitor, labelName);

            if (monitor.isCanceled()) {
              CDMLogger.getInstance().info("Analyzing statistical information for - " + labelNames + " was cancelled",
                  Activator.PLUGIN_ID);
              break;

            }
            if (seriesStatViewPart.getLabelInfoVO() != null) {// If there are no exceptions ,then only cache the series
              // statistics data
              // ICDM-913

              SeriesStatCache.getInstance().getMapOfSeriesStatistics().put(labelName,
                  seriesStatViewPart.getLabelInfoVO());
            }
          }
          monitor.done();
        }
      });
    }
    catch (InvocationTargetException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    catch (InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * This method returns parameter label name
   *
   * @param selectedObject
   * @return String
   */
  // ICDM-218 and Icdm-543
  private String getLabelName(final Object selectedObject) {
    String labelName = null;
    if (selectedObject instanceof Characteristic) {
      labelName = ((Characteristic) selectedObject).getName();
    }
    // Icdm-633
    if (selectedObject instanceof A2LParameter) {
      labelName = ((A2LParameter) selectedObject).getName();
    }
    else if (selectedObject instanceof IParameter) {
      labelName = ((IParameter) selectedObject).getName();
    }
    else if (selectedObject instanceof CDRResultParameter) {
      labelName = ((CDRResultParameter) selectedObject).getName();
    }
    // ICDM-1086
    else if (selectedObject instanceof ReviewRule) {
      labelName = ((ReviewRule) selectedObject).getParameterName();
    }
    // ICDM 1308
    else if (selectedObject instanceof DefaultRuleDefinition) {
      labelName = ((DefaultRuleDefinition) selectedObject).getReviewRule().getParameterName();
    }
    else if (selectedObject instanceof ScratchPadDataFetcher) {
      if (((ScratchPadDataFetcher) selectedObject).getSeriesStatsInfo() != null) {
        labelName = ((ScratchPadDataFetcher) selectedObject).getSeriesStatsInfo().getCalData().getShortName();
      }
      else if (((ScratchPadDataFetcher) selectedObject).getParameter() != null) {
        labelName = ((ScratchPadDataFetcher) selectedObject).getParameter().getName();
      }
    }
    return labelName;
  }

  /**
   * @param seriesStatViewPart
   * @param monitor
   * @param labelName
   */
  private void invokeWSClient(final SeriesStatisticsViewPart seriesStatViewPart, final IProgressMonitor monitor,
      final String labelName) {
    try {
      final APICWebServiceClient apicWsClient = new APICWebServiceClient();
      // ICDM-344
      setLabelInfoVO(seriesStatViewPart, monitor, labelName, apicWsClient);
    }
    catch (Exception exp) {
      // ICDM-351
      seriesStatViewPart.setLabelInfoVO(null);
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param seriesStatViewPart
   * @param monitor
   * @param labelName
   * @param apicWsClient
   * @throws Exception
   */
  private void setLabelInfoVO(final SeriesStatisticsViewPart seriesStatViewPart, final IProgressMonitor monitor,
      final String labelName, final APICWebServiceClient apicWsClient)
      throws Exception {
    try {
      Map<String, LabelInfoVO> mapOfSeriesStatistics = SeriesStatCache.getInstance().getMapOfSeriesStatistics();
      // If cached , then get it from cache
      if (mapOfSeriesStatistics.containsKey(labelName)) {
        LabelInfoVO labelInfoVo = mapOfSeriesStatistics.get(labelName);
        seriesStatViewPart.setLabelInfoVO(labelInfoVo);
      }
      else {
        seriesStatViewPart.setLabelInfoVO(apicWsClient.getParameterStatistics(labelName, monitor));
      }
    }
    catch (AxisFault exp) {
      // ICDM-351
      seriesStatViewPart.setLabelInfoVO(null);
      CDMLogger.getInstance().warn(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * ICDM-935 This method returns parameter class name
   *
   * @param selectedObject sel param
   * @return String class name
   */
  private String getClassName(final Object selectedObject) {
    String className = "";
    if (selectedObject instanceof A2LParameter) {
      className = ((A2LParameter) selectedObject).getPclassString();
    }
    else if (selectedObject instanceof IParameter) {
      className = ((IParameter) selectedObject).getpClassText();
    }
    else if (selectedObject instanceof CDRResultParameter) {
      className = "";
    }
    else if (selectedObject instanceof ScratchPadDataFetcher) {
      if (((ScratchPadDataFetcher) selectedObject).getSeriesStatsInfo() != null) {
        className = ((ScratchPadDataFetcher) selectedObject).getSeriesStatsInfo().getCalData().getCategoryName();
      }

      else if (((ScratchPadDataFetcher) selectedObject).getParameter() != null) {
        className = ((ScratchPadDataFetcher) selectedObject).getParameter().getpClassText();
      }

    }
    return className;
  }

  /**
   * This method will updates series statistics view UI data
   *
   * @param seriesStatViewPart .
   * @param labelName .
   * @param isCached .
   */
  // ICDM-218
  protected void updateSeriesStatisticsViewUI(final SeriesStatisticsViewPart seriesStatViewPart, final String labelName,
      final boolean isCached) {
    // first clear old data (iCDM-1198, supporting changes)
    seriesStatViewPart.resetUIControls();
    // ICDM-351
    if (seriesStatViewPart.getLabelInfoVO() == null) {
      if (!seriesStatViewPart.getScrolledForm().isDisposed()) {
        // Set text to srolled form
        seriesStatViewPart.getScrolledForm().setText("PAR: " + labelName);
      }
    }
    else {
      if (!seriesStatViewPart.getScrolledForm().isDisposed()) {
        // Set text to srolled form
        seriesStatViewPart.getScrolledForm().setText("PAR: " + labelName);
      }
      if (isCached) {
        seriesStatViewPart.fillUIControls(SeriesStatCache.getInstance().getMapOfSeriesStatistics().get(labelName));
      }
      else {
        seriesStatViewPart.fillUIControls(seriesStatViewPart.getLabelInfoVO());
      }
    }
  }

  /**
   * ICDM 533 Menu for opening APRJ in vCDM
   *
   * @param menuMgr MenuManager
   * @param vcdmAprj .
   */
  public void addAPRJMenuAction(final MenuManager menuMgr, final String vcdmAprj, final FCBCUsage usage) {
    final Action openAPRJAction = new Action() {

      @Override
      public void run() {
        openVCDMClientNew(vcdmAprj, usage);
      }
    };
    // set the text and image for the Action
    openAPRJAction.setText(IMessageConstants.OPEN_APRJ_IN_VCDM);
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.VCDM_16X16);
    openAPRJAction.setImageDescriptor(imageDesc);
    menuMgr.add(openAPRJAction);
    openAPRJAction.setEnabled(!vcdmAprj.isEmpty());
  }

  /**
   * @param vcdmAprj APRJ Name
   * @param usage
   */
  public void openVCDMClientNew(final String vcdmAprj, final FCBCUsage usage) {
    String link = "";
    try {
      // initialise vCDMInterface
      if (usage == null) {
        PidcVcdmTransferServiceClient client = new PidcVcdmTransferServiceClient();
        Long aprjID;
        try {
          aprjID = Long.valueOf(client.findAPRJId(vcdmAprj));
          link = CommonUIConstants.VCDM_APRJ_PATH + aprjID;
        }

        catch (ApicWebServiceException | NumberFormatException e) {
          CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
        }

      }
      else {
        link = CommonUIConstants.VCDM_APRJ_PATH + usage.getVcdmAprjId();
      }

      // open the vcdm link
      BrowserUtil.getInstance().openPath(link);
    }
    catch (

    URISyntaxException exec) {
      CDMLogger.getInstance().errorDialog("Failed to open vCDM!", exec, Activator.PLUGIN_ID);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().errorDialog(
          "Failed to open the APRJ. Please check whether the vCDM client is installed in your workstation.", exp,
          Activator.PLUGIN_ID);
    }

  }

  /**
   * @param vcdmAprj APRJ name
   * @param vcdMAprjId id
   */
  public void openVCDMClient(final String vcdmAprj, final String vcdMAprjId) {
    String aprjId = vcdMAprjId;
    if (null != vcdmAprj) {
      try {
        aprjId = new VcdmAprjServiceClient().getAprjId(vcdmAprj);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error("Error while retrieving APRJ ID from vCDM. " + e.getMessage(), e,
            Activator.PLUGIN_ID);
        return;
      }
    }

    String link = CommonUIConstants.VCDM_APRJ_PATH + aprjId;

    try {
      // open the vcdm link
      BrowserUtil.getInstance().openPath(link);
    }
    catch (URISyntaxException exp) {
      CDMLogger.getInstance().errorDialog("Failed to open vCDM! " + exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    catch (IOException exp) {
      CDMLogger.getInstance().errorDialog(
          "Failed to open the APRJ. Please check whether the vCDM client is installed in your workstation. " +
              exp.getMessage(),
          exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Show Review Data - menu action
   *
   * @param menuMgr menu manager
   * @param firstElement parameter
   * @param enable enable flag
   */
  public void addShowReviewDataMenuAction(final IMenuManager menuMgr, final Object firstElement, final boolean enable,
      final ParameterDataProvider paramDataProvider) {
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.REVIEW_DATA_16X16);
    final Action displayReviewDataAction =
        new ShowReviewDataViewAction(enable, imageDesc, firstElement, paramDataProvider);
    // Icdm-697 Disable the action if more than one value is selected

    menuMgr.add(displayReviewDataAction);
  }


}
