/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.client.bo.cdr.ParameterDataProvider;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.IParameter;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.report.excel.ReviewRuleExport;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;


/**
 * @author svj7cob
 */
// ICDM-1539, Excel Export for Review Rule / Rule set / Component package
public class ReviewRuleExcelExportJob extends Job {

  /**
   * location to store the excel
   */
  final private String filePath;
  /**
   * type of excel report
   */
  final private String fileExtn;

  /**
   * Collection of Cdr function parameters
   */
  private final ParamCollection selectedObject;

  /**
   * open automatically flag
   */
  protected final boolean openAutomatically;

  /**
   * if true, then export only the filtered parameters, otherwise all the parameters to be exported
   */
  private final boolean filteredFlag;
  private final ParamCollectionDataProvider paramColDataProvider;
  private final ParameterDataProvider paramDataProvider;


  /**
   * Creates an instance of this class
   *
   * @param rule mutex rule
   * @param selectedObject result
   * @param filePath file location
   * @param fileExtn file type
   * @param openAutomatically opens excel
   * @param filteredFlag flag
   * @param paramDataProvider
   * @param paramColDataProvider
   */
  public ReviewRuleExcelExportJob(final MutexRule rule, final ParamCollection selectedObject, final String filePath,
      final String fileExtn, final boolean openAutomatically, final boolean filteredFlag,
      final ParamCollectionDataProvider paramColDataProvider, final ParameterDataProvider paramDataProvider) {
    super("Exporting Data " + paramColDataProvider.getObjectTypeName(selectedObject) + " :" + selectedObject.getName());
    setRule(rule);
    this.paramColDataProvider = paramColDataProvider;
    this.paramDataProvider = paramDataProvider;
    this.selectedObject = selectedObject;
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.openAutomatically = openAutomatically;
    this.filteredFlag = filteredFlag;
  }

  /**
   * the run method to process the actual Review Rule export
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {

    // describing the type whether Function or Rule set or Component package
    final String objectTypeName = this.paramColDataProvider.getObjectTypeName(this.selectedObject);
    final String excelExportBeginMessage =
        "Exporting " + objectTypeName + " : " + this.selectedObject.getName() + " started";
    monitor.beginTask(excelExportBeginMessage, IProgressMonitor.UNKNOWN);
    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().info(excelExportBeginMessage);
    }

    @SuppressWarnings("rawtypes")
    SortedSet<?> sortedParameters;
    if (this.filteredFlag) {
      // if user selects filtered Radio button in the dialog
      sortedParameters = getFilteredParametersFromTreeViewer();
    }
    else {
      // if user selects All Radio button in the dialog
      sortedParameters = new TreeSet<>(this.paramDataProvider.getParamRulesOutput().getParamMap().values());
    }
    ReviewRuleExport reviewRuleExport = new ReviewRuleExport(monitor, this.paramDataProvider, objectTypeName);

    // Call for excel export of ReviewRule
    reviewRuleExport.exportReviewRule(this.selectedObject, sortedParameters, this.filePath, this.fileExtn);

    // gets the complete file path
    final String completeFilePath = CommonUtils.getCompleteFilePath(this.filePath, this.fileExtn);

    // if user cancelled the export
    if (monitor.isCanceled()) {
      final File file = new File(completeFilePath);
      file.delete();
      CDMLogger.getInstance().info(completeFilePath + " is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();

    // if user checks open automatically in the dialog
    if (this.openAutomatically) {
      CommonUiUtils.openFile(completeFilePath);
    }
    CDMLogger.getInstance().info(objectTypeName + " : " + this.selectedObject.getName() +
        "  exported successfully to the file path " + completeFilePath, Activator.PLUGIN_ID);
    return Status.OK_STATUS;

  }

  /**
   * gets the filtered parameters from the Tree viewer
   *
   * @return paramSet
   */
  private SortedSet<?> getFilteredParametersFromTreeViewer() {
    @SuppressWarnings({ "rawtypes", "unchecked" })
    final SortedSet<IParameter> paramSet = new TreeSet();
    Display.getDefault().syncExec(new Runnable() {

      @Override
      public void run() {
        IEditorPart activeEditor =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
        List inputList =
            ((ReviewParamEditor) activeEditor).getListPage().getCustomGridFilterLayer().getBodyDataProvider().getList();
        for (Object inputData : inputList) {
          if (inputData instanceof IParameter) {
            IParameter item = (IParameter) inputData;
            paramSet.add(item);
          }
        }
      }
    });
    return paramSet;
  }

}
