package com.bosch.caltool.apic.ui.jobs;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrExportModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.report.excel.ProjectIdCardExport;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rvu1cob
 */
public class ProjectIdCardExportJob extends Job {

  private final PidcVersion pidcVer;
  private PidcVersionBO pidcVersionBO;
  private final String filePath;
  private final String fileExtn;
  private final boolean openAutomatically;
  private final boolean filtered;

  private final Set<Long> filteredAttrIdSet = new HashSet<>();
  /**
   * ICDM-485
   */
  private final boolean isReportExternal;

  /**
   * Constructor to pidc version export job
   *
   * @param rule mutex rule
   * @param pidcVer Pidc Version to export
   * @param filePath file path
   * @param fileExtn file extension
   * @param openAutomatically automatically open
   * @param filtered export only filtered attributes or not
   * @param isExternalFlag is export to be shared externally outside Bosch
   */
  public ProjectIdCardExportJob(final MutexRule rule, final PidcVersion pidcVer, final String filePath,
      final String fileExtn, final boolean openAutomatically, final boolean filtered, final boolean isExternalFlag) {
    super("Exporting PIDC :" + pidcVer.getName());
    setRule(rule);
    this.pidcVer = pidcVer;
    this.filePath = filePath;
    this.fileExtn = fileExtn;
    this.openAutomatically = openAutomatically;
    this.filtered = filtered;
    this.isReportExternal = isExternalFlag;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {
    monitor.beginTask("Export PIDC", IProgressMonitor.UNKNOWN);

    getCdmLogger().info("Exporting PIDC to excel job started");

    AttrExportModel attrExpModel = loadAttrExportModel(monitor);

    getDetailsFromEditor();

    if (this.pidcVersionBO == null) {
      loadPidcVersionDetails(monitor);
    }

    getCdmLogger().debug("PIDC-Attribute details retrieved from server");

    // Call for export the PIDC
    final ProjectIdCardExport projectIdCardExport = new ProjectIdCardExport(monitor, this.pidcVersionBO, attrExpModel);
    if (!projectIdCardExport.exportPIDC(this.filePath, this.fileExtn, this.filteredAttrIdSet, this.filtered,
        this.isReportExternal)) {
      return Status.CANCEL_STATUS;
    }

    if (monitor.isCanceled()) {
      final File file = new File(this.filePath + "." + this.fileExtn);
      try {
        Files.deleteIfExists(file.toPath());
      }
      catch (IOException e) {
        getCdmLogger().debug(e.getMessage(), e);
      }

      getCdmLogger().info("Export Project ID Card :  " + getPidcVer().getName() + " is cancelled", Activator.PLUGIN_ID);
      return Status.CANCEL_STATUS;
    }

    monitor.done();

    getCdmLogger().info("Exporting PIDC to excel job completed");

    if (this.openAutomatically) {
      String fileFullPath;
      if (this.filePath.contains(".xlsx") || this.filePath.contains(".xls")) {
        fileFullPath = this.filePath;
      }
      else {
        fileFullPath = this.filePath + "." + this.fileExtn;
      }
      CommonUiUtils.openFile(fileFullPath);
    }

    getCdmLogger().info("Project ID Card " + getPidcVer().getName() + " exported successfully to file " + this.filePath,
        Activator.PLUGIN_ID);

    return Status.OK_STATUS;
  }


  /**
   * @param monitor
   * @return
   */
  private AttrExportModel loadAttrExportModel(final IProgressMonitor monitor) {
    monitor.subTask("Retrieving attribute details");

    AttrExportModel ret = null;
    try {
      ret = new AttributeServiceClient().getAttrExportModel();
    }
    catch (ApicWebServiceException e) {
      getCdmLogger().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return ret;
  }

  /**
   * @param monitor
   */
  private void loadPidcVersionDetails(final IProgressMonitor monitor) {

    monitor.subTask("Retrieving Project ID Card details");
    PidcDataHandler dataHandler = new PidcDataHandler();
    PidcDetailsLoader loader = new PidcDetailsLoader(dataHandler);
    dataHandler = loader.loadDataModel(getPidcVer().getId());

    ProjectHandlerInit handlerInit =
        new ProjectHandlerInit(getPidcVer(), getPidcVer(), dataHandler, ApicConstants.LEVEL_PIDC_VERSION);
    this.pidcVersionBO = (PidcVersionBO) handlerInit.getProjectObjectBO();

  }


  /**
   * Get the details from editor, if is already open
   */
  // ICDM-743
  private void getDetailsFromEditor() {
    Display.getDefault().syncExec(() -> {
      PIDCEditor reqdPidcEditor = getPidcEditor();
      if (reqdPidcEditor != null) {
        // Get pidc version handler from editor
        ProjectIdCardExportJob.this.pidcVersionBO = reqdPidcEditor.getEditorInput().getPidcVersionBO();
        fetchFilteredAttrs(reqdPidcEditor);
      }

    });
  }


  private void fetchFilteredAttrs(final PIDCEditor reqdPidcEditor) {
    if (ProjectIdCardExportJob.this.filtered) {
      ProjectIdCardExportJob.this.filteredAttrIdSet.addAll(reqdPidcEditor.getPidcPage().getFilteredAttributeIds());
    }
  }

  private PIDCEditor getPidcEditor() {
    for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows()) {
      for (IWorkbenchPage page : window.getPages()) {
        for (IEditorReference editor : page.getEditorReferences()) {
          if (editor.getEditor(false) instanceof PIDCEditor) {
            PIDCEditor pidcEditor = (PIDCEditor) editor.getEditor(false);
            if (pidcEditor.getPidcPage().getSelectedPidcVersion().getId()
                .equals(ProjectIdCardExportJob.this.getPidcVer().getId())) {
              return pidcEditor;
            }
          }
        }
      }
    }
    return null;
  }

  /**
   * @return the pidcVer
   */
  private PidcVersion getPidcVer() {
    return this.pidcVer;
  }


  /**
   * @return
   */
  private CDMLogger getCdmLogger() {
    return CDMLogger.getInstance();
  }
}
