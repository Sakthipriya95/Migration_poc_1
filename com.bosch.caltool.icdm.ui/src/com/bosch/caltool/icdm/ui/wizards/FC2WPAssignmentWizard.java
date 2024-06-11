/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPDef;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPMappingWithDetails;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditor;
import com.bosch.caltool.icdm.ui.editors.FC2WPEditorInput;
import com.bosch.caltool.icdm.ui.wizards.pages.FC2WPCreationWizPage;
import com.bosch.caltool.icdm.ui.wizards.pages.FC2WPSelectionWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.FC2WPWizardPage;
import com.bosch.caltool.icdm.ui.wizards.pages.FCWorkPackageCreationWizPage;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPDefinitionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPMappingServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.FC2WPVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class FC2WPAssignmentWizard extends Wizard {

  /**
   * Wizard Page - 1 - fc2wp creation or wp creation selection page
   */
  private FC2WPWizardPage fc2wpWizPage;
  /**
   * Wizard Page - 2 - fc2wp creation page
   */
  private FC2WPSelectionWizardPage fc2wpSelWizPage;
  /**
   * Wizard Page - 3 - workpackage creation page
   */
  private FCWorkPackageCreationWizPage wpCreationWizPage;
  /**
   *
   */
  private FC2WPCreationWizPage fc2wpCreationPage;
  /**
   * Create instance of wizard data
   */
  private FC2WPAssignmentWizardData wizData = new FC2WPAssignmentWizardData();

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    if (!FC2WPAssignmentWizard.this.wizData.isCreateWP()) {
      try {
        getContainer().run(true, false, new IRunnableWithProgress() {

          @Override
          public void run(final IProgressMonitor monitor) {

            monitor.beginTask("Saving Results ...", 100);
            monitor.worked(50);
            FC2WPMappingWithDetails fc2wpMapping = null;
            FC2WPVersion versToLoad = null;
            FC2WPDef newDef = null;
            try {
              // create a webservice client
              FC2WPDefinitionServiceClient servClient = new FC2WPDefinitionServiceClient();
              if (FC2WPAssignmentWizard.this.wizData.getOpenExistingFC2WP() != null) {
                newDef = FC2WPAssignmentWizard.this.wizData.getOpenExistingFC2WP();
              }
              else if (FC2WPAssignmentWizard.this.wizData.isCreateNewFC2WP()) {
                // create fc2wp
                FC2WPDef fc2wp = new FC2WPDef();
                fc2wp.setName(FC2WPAssignmentWizard.this.wizData.getFc2wpName());
                fc2wp.setDivisionValId(FC2WPAssignmentWizard.this.wizData.getSelDivision().getId());
                fc2wp.setRelvForQnaire(false);
                // create fc2wpDef
                newDef = servClient.create(fc2wp);
              }
              else if (FC2WPAssignmentWizard.this.wizData.getExistingFC2WP() != null) {
                FC2WPDef fc2wp = new FC2WPDef();
                fc2wp.setName(FC2WPAssignmentWizard.this.wizData.getFc2wpName());
                fc2wp.setDivisionValId(FC2WPAssignmentWizard.this.wizData.getSelDivision().getId());
                fc2wp.setRelvForQnaire(false);
                fc2wp.setRefFcwpDefId(FC2WPAssignmentWizard.this.wizData.getExistingFC2WP().getId());
                // create fc2wpDef
                newDef = servClient.create(fc2wp);
              }
              if (newDef != null) {
                FC2WPAssignmentWizard.this.wizData.setNewDef(newDef);
                // get the active version
                FC2WPVersionServiceClient verClient = new FC2WPVersionServiceClient();

                CurrentUserBO currentUser = new CurrentUserBO();

                boolean hasAccessRights =
                    currentUser.hasApicWriteAccess() || currentUser.hasNodeWriteAccess(newDef.getId());

                Set<FC2WPVersion> fc2wpversSet = verClient.getVersionsByDefID(newDef.getId());
                for (FC2WPVersion fc2wpVersion : fc2wpversSet) {
                  // working set version loaded for users having APIC_WRITE or Node Write Access
                  // active version loaded for users not having the above rights
                  if ((!hasAccessRights && fc2wpVersion.isActive()) ||
                      (hasAccessRights && fc2wpVersion.isWorkingSet())) {
                    versToLoad = fc2wpVersion;
                    break;
                  }

                }
                // if the user soes not have access rights and the active version is also not present, working set
                // version is loaded in read only mode
                if (null == versToLoad) {
                  CDMLogger.getInstance().infoDialog(
                      "Insufficient access rights. Opening the working set in read only mode due to the absence of active version...",
                      Activator.PLUGIN_ID);
                  versToLoad = verClient.getWorkingSetVersionByDefID(newDef.getId());
                }
                // get the fc2wp version mapping
                FC2WPMappingServiceClient mapClient = new FC2WPMappingServiceClient();
                fc2wpMapping = mapClient.getFC2WPMappingByVersion(versToLoad.getId());
                FC2WPAssignmentWizard.this.wizData.setNewActiveVers(versToLoad);
                FC2WPAssignmentWizard.this.wizData.setNewFc2wpMapping(fc2wpMapping);
                monitor.worked(80);
                monitor.done();

                CommonUiUtils.getInstance().getDisplay().asyncExec(new Runnable() {

                  /**
                   * {@inheritDoc}
                   */
                  @Override
                  public void run() {
                    try {
                      FC2WPEditorInput input = new FC2WPEditorInput(FC2WPAssignmentWizard.this.wizData.getNewDef(),
                          FC2WPAssignmentWizard.this.wizData.getNewActiveVers());

                      IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                          .openEditor(input, FC2WPEditor.PART_ID);
                      FC2WPEditor a2lEditor = (FC2WPEditor) openEditor;
                      a2lEditor.setFocus();
                    }
                    catch (PartInitException excep) {
                      CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
                    }
                  }
                });
              }
            }
            catch (ApicWebServiceException excep) {
              CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);

            }
          }
        });
      }

      catch (InvocationTargetException exp) {
        CDMLogger.getInstance().errorDialog("Error occured while opening/creating FC2WP", exp, Activator.PLUGIN_ID);
      }
      catch (InterruptedException exp) {
        CDMLogger.getInstance().errorDialog("Error occured while opening/creating FC2WP", exp, Activator.PLUGIN_ID);
      }
    }
    return true;

  }

  @Override
  public void addPages() {
    // Set tile
    setWindowTitle("FC2WP");

    this.fc2wpWizPage = new FC2WPWizardPage("FC2WP");

    this.fc2wpSelWizPage = new FC2WPSelectionWizardPage("Create/Select FC2WP");

    this.wpCreationWizPage = new FCWorkPackageCreationWizPage("Create WorkPackage");

    this.fc2wpCreationPage = new FC2WPCreationWizPage("Create FC2WP");
    addPage(this.fc2wpWizPage);
    addPage(this.fc2wpSelWizPage);
    addPage(this.wpCreationWizPage);
    addPage(this.fc2wpCreationPage);
  }

  @Override
  public IWizardPage getNextPage(final IWizardPage currentPage) {
    // ICDM-1800
    if (currentPage instanceof FC2WPWizardPage) {
      if (this.wizData.isCreateWP()) {
        return this.wpCreationWizPage;
      }
      return this.fc2wpSelWizPage;

    }
    else if (currentPage instanceof FC2WPSelectionWizardPage) {
      return this.fc2wpCreationPage;
    }

    return super.getNextPage(currentPage);

  }


  /**
   * @return the wizData
   */
  public FC2WPAssignmentWizardData getWizData() {
    return this.wizData;
  }

  /**
   * @param wizData the wizData to set
   */
  public void setWizData(final FC2WPAssignmentWizardData wizData) {
    this.wizData = wizData;
  }

  @Override
  public boolean canFinish() {

    return getContainer().getCurrentPage() != this.fc2wpWizPage;

  }

  /**
   * @return the fc2wpWizPage
   */
  public FC2WPWizardPage getFc2wpWizPage() {
    return this.fc2wpWizPage;
  }

  /**
   * @return the fc2wpSelWizPage
   */
  public FC2WPSelectionWizardPage getFc2wpSelWizPage() {
    return this.fc2wpSelWizPage;
  }

  /**
   * @return the wpCreationWizPage
   */
  public FCWorkPackageCreationWizPage getWpCreationWizPage() {
    return this.wpCreationWizPage;
  }

  /**
   * @return the fc2wpCreationPage
   */
  public FC2WPCreationWizPage getFc2wpCreationPage() {
    return this.fc2wpCreationPage;
  }

}
