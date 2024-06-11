/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors.a2lcomparison;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.a2l.A2LCompareHandler;
import com.bosch.caltool.icdm.client.bo.a2l.A2LEditorDataProvider;
import com.bosch.caltool.icdm.client.bo.a2l.A2LWPInfoBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.jobs.A2LBaseCompFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2LFileDownloadChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2lResponsibilityFetchChildJob;
import com.bosch.caltool.icdm.common.ui.jobs.A2lWPDefnVersionFetchChildJob;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lVariantGroup;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.rcputils.jobs.AbstractChildJob;
import com.bosch.rcputils.jobs.ChildJobFamily;

/**
 * @author bru2cob
 */
public class A2LCompareJob extends Job {

  /**
   * Input map to store selected a2l wp def version and a2l variant group for a wp def version Variant group can be null
   * if default is chosen as a variant group
   */

  Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lWpDefVerVarGrpMap = new HashMap<>();
  // to store wp defn version id for which data fetching via job has been already done
  Set<Long> a2lWpDefnVersionIdSet = new HashSet<>();
  private A2LCompareEditorInput a2lCompareInput;
  private A2LCompareEditor a2lCompareEditor;
  private A2LCompareHandler paramCompareHandler;
  private final Map<Long, A2LEditorDataProvider> a2lEditorDpMap = new HashMap<>();
  private Map<Long, A2LWPInfoBO> a2lWpInfoMap = new HashMap<>();


  /**
   * @param a2lVariantGrp Map<A2lWpDefnVersion, Set<A2lVariantGroup>> to compare a2l variant group of same/different wp
   *          def versn
   */
  public A2LCompareJob(final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> a2lVariantGrp) {
    super("Comparing A2L contents");
    this.a2lWpDefVerVarGrpMap = a2lVariantGrp;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected IStatus run(final IProgressMonitor monitor) {
    Job.getJobManager().resume();
    ChildJobFamily subJobFamily = new ChildJobFamily(this);
    monitor.beginTask("", AbstractChildJob.JOB_TOTAL);
    monitor.worked(AbstractChildJob.JOB_BEGIN);

    for (Entry<A2lWpDefnVersion, Set<A2lVariantGroup>> entrySet : this.a2lWpDefVerVarGrpMap.entrySet()) {
      A2lWpDefnVersion wpDefVer = entrySet.getKey();
      // condition to keep track whether job has been already executed for given wp version
      if (!this.a2lWpDefnVersionIdSet.contains(wpDefVer.getId())) {
        Long pidcA2lId = wpDefVer.getPidcA2lId();
        A2LEditorDataProvider a2lEditorDP;
        try {
          a2lEditorDP = new A2LEditorDataProvider(pidcA2lId, true);
          this.a2lEditorDpMap.put(wpDefVer.getId(), a2lEditorDP);
        }
        catch (IcdmException e) {
          CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
          return Status.CANCEL_STATUS;
        }
        // A2L file download Job
        A2LFileDownloadChildJob downloadJob = new A2LFileDownloadChildJob(a2lEditorDP);
        subJobFamily.add(downloadJob);

        // A2L Base Components fetch Job
        A2LBaseCompFetchChildJob bcFetchJob = new A2LBaseCompFetchChildJob(a2lEditorDP);
        subJobFamily.add(bcFetchJob);

        // A2L WP Definition fetch job
        A2lWPDefnVersionFetchChildJob wpDefnFetchJob = new A2lWPDefnVersionFetchChildJob(a2lEditorDP, false);
        subJobFamily.add(wpDefnFetchJob);

        // Fetch WP Resp for PIDC
        A2lResponsibilityFetchChildJob a2lRespFetchChildJob = new A2lResponsibilityFetchChildJob(a2lEditorDP);
        subJobFamily.add(a2lRespFetchChildJob);
        this.a2lWpDefnVersionIdSet.add(wpDefVer.getId());
      }
    }
    // Execute Jobs and wait until all of them are completed
    try {
      subJobFamily.execute(monitor);
    }
    catch (OperationCanceledException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp);
      return Status.CANCEL_STATUS;
    }
    if (!subJobFamily.isResultOK()) {
      return Status.CANCEL_STATUS;
    }
    if (this.paramCompareHandler != null) {
      this.a2lWpInfoMap = this.paramCompareHandler.getA2lWpInfoMap();
    }
    // initialise the needed a2l data
    this.paramCompareHandler = new A2LCompareHandler(this.a2lEditorDpMap, this.a2lWpInfoMap, this.a2lWpDefVerVarGrpMap);
    monitor.worked(AbstractChildJob.JOB_ADD_END);
    // open the editor if not already open
    if (!isA2lCompareEditorOpen()) {
      openCompareEditor();
    }
    else {
      // store param compare handler instance in editor input so that same instance of handler could available for
      // reconstructing nat table after drag and drop
      this.a2lCompareInput.setParamCompareHandler(this.paramCompareHandler);
      // thread to reconstruct nat table after drag and drop
      reconstructNatTable();
    }
    monitor.done();
    return Status.OK_STATUS;
  }

  private boolean isA2lCompareEditorOpen() {
    final IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    final IEditorReference[] editorRefArr = wbWindows[0].getActivePage().getEditorReferences();

    for (IEditorReference editor : editorRefArr) {
      try {
        if ((editor.getPart(false) instanceof A2LCompareEditor) &&
            editor.getEditorInput().equals(this.a2lCompareInput)) {
          return true;

        }
      }
      catch (PartInitException exp) {
        CDMLogger.getInstance().warn(exp.getMessage(), exp);
      }
    }
    return false;
  }


  private void reconstructNatTable() {
    Display.getDefault().asyncExec(new Runnable() {

      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {
        A2LCompareJob.this.a2lCompareEditor.getA2lComparePage().reconstructNatTable();
      }
    });
  }

  /**
   * @param paramCompareHandler
   */
  private void openCompareEditor() {
    Display.getDefault().asyncExec(new Runnable() {


      /**
       * {@inheritDoc}
       */
      @Override
      public void run() {

        try {
          CDMLogger.getInstance().debug("Opening the A2L compare editor...");
          A2LCompareJob.this.a2lCompareInput = new A2LCompareEditorInput(A2LCompareJob.this.paramCompareHandler);
          IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
              .openEditor(A2LCompareJob.this.a2lCompareInput, A2LCompareEditor.EDITOR_ID);
          if (openEditor instanceof A2LCompareEditor) {
            A2LCompareJob.this.a2lCompareEditor = (A2LCompareEditor) openEditor;
            // set focus to the editor opened
            A2LCompareJob.this.a2lCompareEditor.setFocus();
            // store the job instance in editor input so that same instance can be used in case of drag and drop
            A2LCompareJob.this.a2lCompareEditor.getEditorInput().setA2LCompareJob(A2LCompareJob.this);
            CDMLogger.getInstance().debug("A2L compare editor is opened");
          }

        }
        catch (PartInitException exp) {
          CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
        }

      }
    });
  }

  /**
   * @return the a2lVariantGrp
   */
  public final Map<A2lWpDefnVersion, Set<A2lVariantGroup>> getA2lWpDefVerVarGrpMap() {
    return this.a2lWpDefVerVarGrpMap;
  }

}
