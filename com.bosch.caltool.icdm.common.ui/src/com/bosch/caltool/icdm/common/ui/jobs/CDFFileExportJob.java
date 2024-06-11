package com.bosch.caltool.icdm.common.ui.jobs;

import java.io.File;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.reports.CDFFileExport;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.FileIOUtil;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * Job class to export CDF file
 *
 * @author mga1cob
 */
// ICDM-242
public class CDFFileExportJob extends Job {

  private final Map<String, CalData> cdfCalDataObjects;
  private final String filePath;
  private final String fileExtn;
  private final boolean openCDFx;

  /**
   * Creates an instance of this class
   *
   * @param rule instance
   * @param cdfCalDataObjects defines CDF CalData objects
   * @param filePath defines
   * @param fileExtn defines file extensions
   * @param openCDFx boolean
   */
  public CDFFileExportJob(final MutexRule rule, final Map<String, CalData> cdfCalDataObjects, final String filePath,
      final String fileExtn, final boolean openCDFx) {
    // Icdm-932 change the name to CDFX
    super("Exporting as CDFX :");
    this.openCDFx = openCDFx;
    setRule(rule);
    this.cdfCalDataObjects = cdfCalDataObjects;
    this.filePath = filePath;
    this.fileExtn = fileExtn;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {
    // iCDM-1455
    // check if file is used by any other process (Eg; CDM Studio..)
    if (FileIOUtil.checkIfFileIsLocked(this.filePath)) {
      CDMLogger.getInstance().errorDialog(
          "CDFX Export Error : File is already used by another process. \n Please provide a different filename or close the file before export. \n File path: " +
              this.filePath,
          Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }

    // Icdm-932 change the name to CDFX
    monitor.beginTask("Export as CDFX: ", IProgressMonitor.UNKNOWN);
    final CDFFileExport cdfExport = new CDFFileExport(monitor);

    cdfExport.exportCDF(this.cdfCalDataObjects, this.filePath);

    if (monitor.isCanceled()) {
      // when the progress monitor is cancelled
      final File file = new File(this.filePath + "." + this.fileExtn);
      file.delete();
      CDMLogger.getInstance().info("Export as CDFX is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }
    monitor.done();
    CDMLogger.getInstance().info("CDFX exported successfully to file  " + this.filePath, Activator.PLUGIN_ID);
    if (this.openCDFx) {
      CommonUiUtils.openCdfxFile(this.filePath);
    }
    return Status.OK_STATUS;
  }

}
