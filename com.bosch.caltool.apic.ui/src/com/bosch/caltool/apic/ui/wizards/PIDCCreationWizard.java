/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.CustomProgressDialog;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.client.bo.apic.PidcCreationHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.pidc.PIDCCreationWizardData;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.uc.UsecaseFavorite;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author dja7cob
 */
public class PIDCCreationWizard extends Wizard {

  /**
   * PIDCDetailsWizardPage
   */
  private PIDCDetailsWizardPage pidcDetailsWizardPge;

  /**
   * VersionsDetailsWizardPage
   */
  private VersionsDetailsWizardPage versionDtlsWizardPage;

  /**
   * ProjectUsecaseSelectionPage
   */
  private ProjectUseCaseSelectionPage projUCWizardPage;

  /**
   * flag set when next of version page is clicked for the first time
   */
  private boolean canFinish;

  private final PidcTreeNode lvlAttrTreeNode;

  private final TreeViewer treeViewer;

  private final boolean isPasteAction;

  private final PidcVersion copiedPidcVer;

  private final PIDCCreationWizardData wizardData;

  private final PidcCreationHandler pidcCreationHandler;

  private Pidc newlyCreatedPidc;

  /**
   * @param pidcNode Pidc Tree Node
   * @param viewer Tree Viewer
   * @param pasteAction paste Action
   * @param pidcVersion Pidc Version
   */
  public PIDCCreationWizard(final PidcTreeNode pidcNode, final TreeViewer viewer, final boolean pasteAction,
      final PidcVersion pidcVersion) {
    super();
    this.lvlAttrTreeNode = pidcNode;
    this.treeViewer = viewer;
    this.isPasteAction = pasteAction;
    this.copiedPidcVer = pidcVersion;
    this.wizardData = new PIDCCreationWizardData();
    this.pidcCreationHandler = new PidcCreationHandler();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    setUcItemsInWizData();
    if (this.isPasteAction) {
      callPastePidcService();
    }
    else {
      callCreatePidcService();
    }
    return true;
  }

  /**
   * @throws ApicWebServiceException
   */
  private void setUcItemsInWizData() {
    for (IUseCaseItemClientBO usecaseBo : this.projUCWizardPage.getSelectedUCItems()) {

      try {
        UsecaseFavorite ucFav = createUcFav(usecaseBo);
        this.wizardData.getSelUcFavList().add(ucFav);
      }
      catch (Exception exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
  }

  /**
   * @param ucGrp
   * @return
   * @throws ApicWebServiceException
   */
  private UsecaseFavorite createUcFav(final IUseCaseItemClientBO ucBO) {
    UsecaseFavorite ucFav = new UsecaseFavorite();
    if (ucBO instanceof UseCaseGroupClientBO) {
      UseCaseGroupClientBO ucGrpObj = (UseCaseGroupClientBO) ucBO;
      ucFav.setGroupId(ucGrpObj.getId());
    }
    else if (ucBO instanceof UsecaseClientBO) {
      UsecaseClientBO ucObj = (UsecaseClientBO) ucBO;
      ucFav.setUseCaseId(ucObj.getId());
    }
    else if (ucBO instanceof UseCaseSectionClientBO) {
      UseCaseSectionClientBO ucSec = (UseCaseSectionClientBO) ucBO;
      ucFav.setSectionId(ucSec.getId());
    }
    return ucFav;
  }

  /**
   *
   */
  private void callCreatePidcService() {
    try {

      ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Creating PIDC...", 100);
        monitor.worked(20);
        PIDCCreationWizard.this.newlyCreatedPidc = PIDCCreationWizard.this.pidcCreationHandler
            .createPidc(PIDCCreationWizard.this.wizardData, PIDCCreationWizard.this.lvlAttrTreeNode);
        monitor.worked(100);
        monitor.done();
      });

    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void callPastePidcService() {
    try {

      ProgressMonitorDialog dialog = new CustomProgressDialog(Display.getDefault().getActiveShell());
      dialog.run(true, true, monitor -> {
        monitor.beginTask("Creating PIDC...", 100);
        monitor.worked(20);
        PIDCCreationWizard.this.newlyCreatedPidc =
            PIDCCreationWizard.this.pidcCreationHandler.pastePidc(PIDCCreationWizard.this.wizardData,
                PIDCCreationWizard.this.lvlAttrTreeNode, PIDCCreationWizard.this.copiedPidcVer);
        monitor.worked(100);
        monitor.done();
      });

    }
    catch (InvocationTargetException | InterruptedException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFinish() {
    return this.versionDtlsWizardPage.isPageComplete() && this.pidcDetailsWizardPge.isPageComplete() && this.canFinish;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages() {
    // Set title
    setWindowTitle(ApicUiConstants.ADD_A_PROJ_ID_CARD);
    this.pidcDetailsWizardPge = new PIDCDetailsWizardPage("PIDC Details");
    addPage(this.pidcDetailsWizardPge);

    this.versionDtlsWizardPage = new VersionsDetailsWizardPage("Version Details");
    addPage(this.versionDtlsWizardPage);

    this.projUCWizardPage = new ProjectUseCaseSelectionPage("Project Usecases");
    addPage(this.projUCWizardPage);
  }

  /**
   * @return the wizardData
   */
  public PIDCCreationWizardData getWizardData() {
    return this.wizardData;
  }

  /**
   * @param canFinish can Finish flag
   */
  public void setFinishFlag(final boolean canFinish) {
    this.canFinish = canFinish;
  }

  /**
   * @return the lvlAttrTreeNode
   */
  public PidcTreeNode getLvlAttrTreeNode() {
    return this.lvlAttrTreeNode;
  }

  /**
   * @return the treeViewer
   */
  public TreeViewer getTreeViewer() {
    return this.treeViewer;
  }

  /**
   * @return the copiedPidcVer
   */
  public PidcVersion getCopiedPidcVer() {
    return this.copiedPidcVer;
  }

  /**
   * @return the pidcCreationHandler
   */
  public PidcCreationHandler getPidcCreationHandler() {
    return this.pidcCreationHandler;
  }

  /**
   * @return the newlyCreatedPidc
   */
  public Pidc getNewlyCreatedPidc() {
    return this.newlyCreatedPidc;
  }

  /**
   * @param newlyCreatedPidc the newlyCreatedPidc to set
   */
  public void setNewlyCreatedPidc(final Pidc newlyCreatedPidc) {
    this.newlyCreatedPidc = newlyCreatedPidc;
  }

}
