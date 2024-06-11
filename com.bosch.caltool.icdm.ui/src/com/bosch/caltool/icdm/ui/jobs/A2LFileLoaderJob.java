/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.jobs;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.ui.jobs.A2LBaseCompFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2LFileDownloadChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2LParamPropsFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2LSysConstantFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2lResponsibilityFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2lWPDefnVersionFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.WorkPackageLoadChildJob;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditor;
import com.bosch.caltool.icdm.ui.editors.A2LContentsEditorInput;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.jobs.AbstractChildJob;
import com.bosch.rcputils.jobs.ChildJobFamily;


/**
 * Job to download, parse and display the A2L contents in an A2L editor.
 *
 * @author bne4cob
 */
public class A2LFileLoaderJob extends Job {

  /**
   * A2L file for starting the job.
   */
  private final PidcA2LBO pidcA2LBO;
  private final boolean openA2lEditor;
  private boolean uploadA2lImportGrps;

  /**
   * Instantiates a new a 2 L file loader job.
   *
   * @param pidcA2LBO the pidc A 2 LBO
   */
  public A2LFileLoaderJob(final PidcA2LBO pidcA2LBO) {
    super("Loading A2L contents " + pidcA2LBO.getA2LFileName());
    this.pidcA2LBO = pidcA2LBO;
    this.openA2lEditor = false;
  }

  /**
   * Instantiates a new a 2 L file loader job.
   *
   * @param pidcA2LBO the pidc A 2 LBO
   * @param openA2lEditor the open A 2 l editor
   */
  public A2LFileLoaderJob(final PidcA2LBO pidcA2LBO, final boolean openA2lEditor) {
    super("Loading A2L contents " + pidcA2LBO.getA2LFileName());
    this.pidcA2LBO = pidcA2LBO;
    this.openA2lEditor = openA2lEditor;
  }

  /**
   * Open the a2l file. Steps <br>
   * 1. Download the a2l file usign vCDM web service <br>
   * 2. Fetch parameter properties using iCDM web service<br>
   * 3. Open the a2l file in A2L file editor as an asynch execution.
   * <p>
   * {@inheritDoc}
   */
  @Override
  protected final IStatus run(final IProgressMonitor monitor) {

    Job.getJobManager().resume();

    CDMLogger.getInstance().debug("Opening A2L file in editor :  " + this.pidcA2LBO.getA2LFileName());
    A2LContentsEditorInput input = new A2LContentsEditorInput(this.pidcA2LBO, null, null);
    A2LEditorDataProvider a2lEditorDp = new A2LEditorDataProvider(this.pidcA2LBO, true);
    final boolean a2lLoaded = isA2lOpenInEditor(input);
    if (null != a2lEditorDp.getA2lFileInfoBO().getA2lFileInfo()) {
      if (!a2lLoaded) {
        ChildJobFamily subJobFamily = new ChildJobFamily(this);
        monitor.beginTask("", AbstractChildJob.JOB_TOTAL);
        monitor.worked(AbstractChildJob.JOB_BEGIN);
        // A2L file download Job
        A2LFileDownloadChildJob downloadJob = new A2LFileDownloadChildJob(a2lEditorDp);
        subJobFamily.add(downloadJob);
        // A2L System constants fetch Job
        A2LSysConstantFetchChildJob sysConstFetchJob = new A2LSysConstantFetchChildJob();
        subJobFamily.add(sysConstFetchJob);
        // A2L Base Components fetch Job
        A2LBaseCompFetchChildJob bcFetchJob = new A2LBaseCompFetchChildJob(a2lEditorDp);
        subJobFamily.add(bcFetchJob);

        // A2L WP Definition fetch job
        A2lWPDefnVersionFetchChildJob wpDefnFetchJob =
            new A2lWPDefnVersionFetchChildJob(a2lEditorDp, this.uploadA2lImportGrps);
        subJobFamily.add(wpDefnFetchJob);


        // A2L additional parameter fetch job
        A2LParamPropsFetchChildJob propFetchJob = new A2LParamPropsFetchChildJob(a2lEditorDp);
        subJobFamily.add(propFetchJob);

        // Fetch WP Resp for PIDC
        A2lResponsibilityFetchChildJob a2lRespFetchChildJob = new A2lResponsibilityFetchChildJob(a2lEditorDp);
        subJobFamily.add(a2lRespFetchChildJob);

        WorkPackageLoadChildJob workPackLoadChildJob = new WorkPackageLoadChildJob(a2lEditorDp);
        subJobFamily.add(workPackLoadChildJob);
        // Execute Jobs and wait until all of them are completed
        try {
          subJobFamily.execute(monitor);
          PidcA2l newData = new PidcA2lServiceClient().getById(this.pidcA2LBO.getPidcA2lId());
          CommonUtils.shallowCopy(a2lEditorDp.getA2lWpInfoBO().getPidcA2lBo().getPidcA2l(), newData);

        }
        catch (OperationCanceledException | InterruptedException exp) {
          CDMLogger.getInstance().error(exp.getMessage(), exp);
          Thread.currentThread().interrupt();
          return Status.CANCEL_STATUS;
        }
        catch (ApicWebServiceException exp) {
          CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
          return Status.CANCEL_STATUS;
        }
        if (!subJobFamily.isResultOK()) {
          return Status.CANCEL_STATUS;
        }
      }

      // Re-create to enable CNS refresh
      input = new A2LContentsEditorInput(this.pidcA2LBO, a2lEditorDp.getA2lFileInfoBO(), a2lEditorDp.getA2lWpInfoBO());

      monitor.worked(AbstractChildJob.JOB_ADD_END);
      // Display contents in editor
      if (this.openA2lEditor) {

        openA2lEditor(input);
      }
      monitor.done();

      return Status.OK_STATUS;
    }
    return Status.CANCEL_STATUS;
  }


  /**
   * Open A 2 l editor.
   *
   * @param input
   */
  private void openA2lEditor(final A2LContentsEditorInput input) {
    CommonUiUtils.getInstance().getDisplay().asyncExec(() -> {
      try {
        IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
            A2LContentsEditor.PART_ID);
        A2LContentsEditor a2lEditor = (A2LContentsEditor) openEditor;
        a2lEditor.setFocus();
        // ICDM-1254, 1249
        CommonUiUtils.forceActive(a2lEditor.getEditorSite().getShell());
        CDMLogger.getInstance().debug("A2L file opened in editor. " + A2LFileLoaderJob.this.pidcA2LBO.getA2LFileName());
      }
      catch (PartInitException e) {
        CDMLogger.getInstance().warn(e.getMessage(), e);
      }
    });
  }

  /**
   * Checks whether this A2L file is already opened in editor.
   * <p>
   * This check is done in the beginning since the a2l loading is performance intense. The normal eclipse level editor
   * opened check is not used here, as it will perform the check only just before opening the editor, after loading the
   * data model
   *
   * @param input the editor input
   * @return true if file is loaded, false otherwise
   */
  private boolean isA2lOpenInEditor(final A2LContentsEditorInput input) {
    boolean loaded = false;
    if (!this.openA2lEditor) {
      return loaded;
    }
    final IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    if (wbWindows.length == 0) {
      return loaded;
    }

    final IEditorReference[] editorRefArr = wbWindows[0].getActivePage().getEditorReferences();
    for (IEditorReference editor : editorRefArr) {
      try {
        if ((editor.getPart(false) instanceof A2LContentsEditor) && editor.getEditorInput().equals(input)) {
          loaded = true;
          break;
        }
      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().warn(exp.getMessage(), exp);
      }
    }
    return loaded;
  }


  /**
   * @return the uploadA2lImportGrps
   */
  public boolean isUploadA2lImportGrps() {
    return this.uploadA2lImportGrps;
  }


  /**
   * @param uploadA2lImportGrps the uploadA2lImportGrps to set
   */
  public void setUploadA2lImportGrps(final boolean uploadA2lImportGrps) {
    this.uploadA2lImportGrps = uploadA2lImportGrps;
  }


}
