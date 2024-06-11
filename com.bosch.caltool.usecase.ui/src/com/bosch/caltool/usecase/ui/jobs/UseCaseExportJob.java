package com.bosch.caltool.usecase.ui.jobs;

import java.io.File;
import java.util.SortedSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.report.excel.UseCaseExport;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * @author dmo5cob
 */
public class UseCaseExportJob extends Job {


  /**
   * Full path of the file. eg : c:\temp\filename
   */
  final private String filePath;
  /**
   * File extension
   */
  final private String fileExtn;
  /**
   * Use case Items
   */
  private final SortedSet<IUseCaseItemClientBO> items;


  /**
   * Creates an instance of this class
   *
   * @param rule Mutex rule
   * @param items UseCaseGroup/UseCase
   * @param filePath FilePath
   * @param fileExtn Extn of file chosen
   */
  public UseCaseExportJob(final MutexRule rule, final SortedSet<IUseCaseItemClientBO> items, final String filePath,
      final String fileExtn) {
    super("Exporting " + ((items.size() == 1) ? "UseCase : " + items.first().getName() : "All Use cases"));
    setRule(rule);
    this.items = items;
    this.filePath = filePath;
    this.fileExtn = fileExtn;
  }

  /**
   * {@inheritDoc} Run the Use case Export Job.
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {

    monitor.beginTask("Exporting use case", IProgressMonitor.UNKNOWN);
    // Call for export the Usecase
    final UseCaseExport useCaseExport = new UseCaseExport(monitor);
    useCaseExport.exportUseCase(this.items, this.filePath, this.fileExtn);
    // Check for cancellation of Job.
    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Export  of " +
          ((this.items.size() == 1) ? " UseCase : " + this.items.first().getName() : "All use cases") + " is cancelled",
          Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    // Log the message after the Completion
    CDMLogger.getInstance()
        .info(((this.items.size() == 1) ? " UseCase : " + this.items.first().getName() : "All use cases") +
            " exported successfully to file " + this.filePath, Activator.PLUGIN_ID);
    return Status.OK_STATUS;

  }
}
