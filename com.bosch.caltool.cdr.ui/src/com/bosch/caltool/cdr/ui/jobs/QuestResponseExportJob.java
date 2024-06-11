/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.jobs;

import java.io.File;
import java.util.List;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.client.bo.qnaire.QnaireRespEditorDataHandler;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.qnaire.RvwQnaireAnswer;
import com.bosch.caltool.icdm.report.excel.QuestResponseExport;

// ICDM-1994
/**
 * This class exports the questionnaire response editor's nat-table.
 *
 * @author svj7cob
 */
public class QuestResponseExportJob extends Job {

  /** location to store the excel. */
  final private String filePath;

  /** extension of excel report. */
  final private String fileExtn;

  /** set of review questionnaire answer. */
  final private SortedSet<RvwQnaireAnswer> rvwQnaireAnsSet;

  /** The data handler. */
  private final QnaireRespEditorDataHandler dataHandler;

  private final List<String> visibleColmns;

  /**
   * Instantiates a new quest response export job.
   *
   * @param rule mutex rule
   * @param sortedSet set of result of review Qnaire Answer
   * @param qnaireRespEditorDataHandler the qnaire resp editor data handler
   * @param visibleColmns Visible Columns in editor page
   * @param filePath file location to store the excel
   * @param fileExtn file type of excel
   */
  public QuestResponseExportJob(final MutexRule rule, final SortedSet<RvwQnaireAnswer> sortedSet,
      final QnaireRespEditorDataHandler qnaireRespEditorDataHandler, final List<String> visibleColmns,
      final String filePath, final String fileExtn) {
    super("Exporting questionnaire response report :");
    setRule(rule);
    this.rvwQnaireAnsSet = sortedSet;
    this.dataHandler = qnaireRespEditorDataHandler;
    this.visibleColmns = visibleColmns;
    this.filePath = filePath;
    this.fileExtn = fileExtn;
  }

  /**
   * Runs the job to export the nat-table content of questionnaire response editor.
   *
   * @param monitor the monitor
   * @return the i status
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {

    if (CDMLogger.getInstance().isDebugEnabled()) {
      CDMLogger.getInstance().debug("Export of questionnaire response report started");
    }
    // Call for export the Questionnaire response
    final QuestResponseExport questResponseExport = new QuestResponseExport(monitor);
    questResponseExport.exportQuestionnaireResponseReport(this.rvwQnaireAnsSet, this.dataHandler, this.visibleColmns,
        this.filePath, this.fileExtn);

    monitor.beginTask("Fetching data for the export of questionnaire response report finished",
        IProgressMonitor.UNKNOWN);

    // if the export is cancelled
    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Export of questionnaire response report  is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info("Questionnaire response report exported successfully to file  " + this.filePath,
        Activator.PLUGIN_ID);
    if (CommonUtils.isFileAvailable(this.filePath)) {
      CommonUiUtils.openFile(this.filePath);
    }
    return Status.OK_STATUS;
  }
}
