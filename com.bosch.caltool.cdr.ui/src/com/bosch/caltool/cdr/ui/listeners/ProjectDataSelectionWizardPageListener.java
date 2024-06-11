/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.listeners;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.PidcVariantSelectionDialog;
import com.bosch.caltool.cdr.ui.dialogs.VersionVaraintSelDialog;
import com.bosch.caltool.cdr.ui.wizard.page.validator.ProjectDataSelectionWizardPageValidator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.ProjectDataSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.dialogs.PIDCVaraintSelDialog;
import com.bosch.caltool.icdm.common.ui.dialogs.UserSelectionDialog;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRWizardUIModel;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.ui.validators.Validator;

/**
 * @author say8cob
 */
public class ProjectDataSelectionWizardPageListener {

  private final CalDataReviewWizard calDataReviewWizard;
  private final ProjectDataSelectionWizardPageValidator projectSelecValidator;
  private final ProjectDataSelectionWizardPage prjDataSelWizPage;


  /**
   * @param calDataReviewWizardNew
   * @param dataSelectionWizardPageValidator
   */
  public ProjectDataSelectionWizardPageListener(final CalDataReviewWizard calDataReviewWizardNew,
      final ProjectDataSelectionWizardPageValidator dataSelectionWizardPageValidator) {
    this.calDataReviewWizard = calDataReviewWizardNew;
    this.projectSelecValidator = dataSelectionWizardPageValidator;
    this.prjDataSelWizPage = calDataReviewWizardNew.getProjectSelWizPage();
  }


  /**
   *
   */
  public void createActionListeners() {

    if (this.prjDataSelWizPage.getDescriptions() != null) {
      this.prjDataSelWizPage.getDescriptions().addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(final ModifyEvent event) {
          Validator.getInstance().validateNDecorate(
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getTxtDescription(),
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getDescriptions(), true, false);
          ProjectDataSelectionWizardPageListener.this.projectSelecValidator.checkNextBtnEnable();
          if (!ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getDescriptions().getText()
              .equalsIgnoreCase(ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                  .getDescription())) {
            ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
          }
        }
      });
    }

    if (this.prjDataSelWizPage.getParticipantsList() != null) {

      this.prjDataSelWizPage.getParticipantsList().addListener(SWT.Selection, new Listener() {

        @Override
        public void handleEvent(final Event event) {
          ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getPartDelButton().setEnabled(true);
          ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.setSelOthParts(
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().getSelection());
        }

      });

      this.prjDataSelWizPage.getParticipantsList().addKeyListener(new KeyAdapter() {

        @Override
        public void keyPressed(final KeyEvent event) {

          final char character = event.character;
          if (character == SWT.DEL) {
            if ((ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelOthParts() != null) &&
                (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelOthParts().length != 0)) {
              for (String partiName : ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelOthParts()) {
                ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().remove(partiName);
                final Iterator<User> othParticipants =
                    ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelParticipants().iterator();
                while (othParticipants.hasNext()) {
                  final User user = othParticipants.next();
                  final String userName = user.getDescription();
                  if (userName.equalsIgnoreCase(partiName)) {
                    othParticipants.remove();
                    break;
                  }
                }
              }
            }
            if (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList()
                .getItemCount() == 0) {
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelParticipants().clear();
            }
            ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getPartDelButton().setEnabled(false);
          }

        }
      });
    }


    if (this.prjDataSelWizPage.getPartDelButton() != null) {


      this.prjDataSelWizPage.getPartDelButton().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          if ((ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelOthParts() != null) &&
              (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelOthParts().length != 0)) {
            for (String partiName : ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelOthParts()) {
              final Iterator<User> participants =
                  ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelParticipants().iterator();
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().remove(partiName);
              while (participants.hasNext()) {
                final User user = participants.next();
                final String userName = user.getDescription();
                if (userName.equalsIgnoreCase(partiName)) {
                  participants.remove();
                  if (!ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                      .getParticipantUserNameList().isEmpty() &&
                      ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                          .getParticipantUserNameList().contains(user.getName())) {
                    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                        .getParticipantUserNameList().remove(user.getName());
                  }

                  // ICDM-687
                  ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
                  break;
                }
              }
            }
          }
          if (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().getItemCount() == 0) {
            ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelParticipants().clear();
          }
          ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getPartDelButton().setEnabled(false);
        }

      });

    }


    if (this.prjDataSelWizPage.getAddPartsBtn() != null) {

      this.prjDataSelWizPage.getAddPartsBtn().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          final UserSelectionDialog partsDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
              "Add User", "Add user", "Add user", "Add", true, false);
          partsDialog.setSelectedMultipleUser(null);
          partsDialog.open();
          ArrayList<User> selectedUsers = (ArrayList<User>) partsDialog.getSelectedMultipleUser();
          if ((selectedUsers != null) && !selectedUsers.isEmpty()) {
            final Iterator<User> users = selectedUsers.iterator();
            while (users.hasNext()) {
              final User selectedUser = users.next();
              final String selUserName = selectedUser.getDescription();
              if ((ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList()
                  .indexOf(selUserName) == -1) &&
                  (!ProjectDataSelectionWizardPage.STR_NULL_NULL.equalsIgnoreCase(selUserName))) {


                ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getSelParticipants().add(selectedUser);


                ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().add(selUserName);


                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                    .getParticipantUserNameList().add(selectedUser.getName());

                // ICDM-687
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
              }
            }

            // sorting of list items
            final String[] items =
                ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().getItems();
            java.util.Arrays.sort(items);
            ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getParticipantsList().setItems(items);

          }
        }

      });

    }

    if (this.prjDataSelWizPage.getAuditor() != null) {

      this.prjDataSelWizPage.getAuditor().addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(final ModifyEvent event) {
          Validator.getInstance().validateNDecorate(
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getTxtAuditorNameDec(),
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getAuditor(), true, false);
          ProjectDataSelectionWizardPageListener.this.projectSelecValidator.checkNextBtnEnable();
        }
      });
    }

    if (this.prjDataSelWizPage.getAuditorBrowse() != null) {

      this.prjDataSelWizPage.getAuditorBrowse().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          final UserSelectionDialog selAuditorDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
              ProjectDataSelectionWizardPage.getSelcUser(), "Select Auditor",
              ProjectDataSelectionWizardPage.getSelcUser(), "Select", false, true);
          selAuditorDialog.setSelectedUser(null);
          selAuditorDialog.open();
          final User selectedUser = selAuditorDialog.getSelectedUser();
          if (selectedUser != null) {
            final String selUserName = selectedUser.getDescription();
            if (!ProjectDataSelectionWizardPage.STR_NULL_NULL.equalsIgnoreCase(selUserName)) {

              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getAuditor().setText(selUserName);

              ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                  .setAuditorUserName(selectedUser.getName());
              // ICDM-687
              ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
            }
          }
        }

      });
    }

    if (this.prjDataSelWizPage.getCalibrationBrowse() != null) {

      this.prjDataSelWizPage.getCalibrationBrowse().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          final UserSelectionDialog calibratnDialog = new UserSelectionDialog(Display.getCurrent().getActiveShell(),
              ProjectDataSelectionWizardPage.getSelcUser(), "Select Calibration Engineer",
              ProjectDataSelectionWizardPage.getSelcUser(), "Select", false, false);
          calibratnDialog.setSelectedUser(null);
          calibratnDialog.open();
          final User selectedUser = calibratnDialog.getSelectedUser();
          if (selectedUser != null) {
            final String selUserName = selectedUser.getDescription();
            if (!ProjectDataSelectionWizardPage.STR_NULL_NULL.equalsIgnoreCase(selUserName)) {

              ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                  .setCalEngUserName(selectedUser.getName());
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getCalEngineer().setText(selUserName);
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.setDefaultUsrSel(false);
              // ICDM-687
              ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
            }
          }
        }
      });
    }

    if (this.prjDataSelWizPage.getVariantName() != null) {

      this.prjDataSelWizPage.getVariantName().addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(final ModifyEvent event) {
          Validator.getInstance().validateNDecorate(
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getTxtVariantNameDec(),
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getVariantName(), true, false);
          ProjectDataSelectionWizardPageListener.this.projectSelecValidator.checkNextBtnEnable();
        }
      });
    }

    if (this.prjDataSelWizPage.getVariantBrowse() != null) {

      this.prjDataSelWizPage.getVariantBrowse().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {

          CDRWizardUIModel cdrUIModel =
              ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel();

          final PidcVariantSelectionDialog variantSelDialog = new PidcVariantSelectionDialog(
              Display.getCurrent().getActiveShell(), cdrUIModel.getSelectedPidcVerId(), cdrUIModel.getPidcA2lId());
          variantSelDialog.open();
          final PidcVariant selectedVariant = variantSelDialog.getSelectedVariant();

          if (selectedVariant != null) {
            // if a variant is changed , reset the wp's already selected for another variant
            resetOnVariantChange(selectedVariant);
            // To get the SDOMPverName for the Selected Variant
            try {
              ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                  .setSelectedReviewPverName(
                      new CDRHandler().getSDOMPverName(cdrUIModel.getSelectedPidcVerId(), selectedVariant.getId()));

              if (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameComboViewer() != null) {
                ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameCombo().removeAll();
              }
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
            }
            ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSelectedPidcVariantId(selectedVariant.getId());
            ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSelectedPidcVariantName(selectedVariant.getName());
            ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .getSelectedReviewPverName();
            ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getVariantName()
                .setText(selectedVariant.getName());
            // ICDM-687
            ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);

            if (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.isDeltaReview() ||
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.isProjectDataDeltaReview()) {
              fillA2lCombo(ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel());
            }

          }
        }
      });
    }

    if (this.prjDataSelWizPage.getProjectName() != null) {
      this.prjDataSelWizPage.getProjectName().addModifyListener(new ModifyListener() {

        @Override
        public void modifyText(final ModifyEvent modifyevent) {

          if (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.isDeltaReview()) {
            if (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameComboViewer() != null) {
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameCombo().removeAll();
            }

            if (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.isDeltaReview() ||
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.isProjectDataDeltaReview()) {
              fillA2lCombo(ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel());
            }
          }
          ProjectDataSelectionWizardPageListener.this.projectSelecValidator.checkNextBtnEnable();

        }


      });
    }

    if (this.prjDataSelWizPage.getPidcBrowse() != null) {
      this.prjDataSelWizPage.getPidcBrowse().addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent event) {
          if (CommonUtils.isEqual(ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getCalDtReviewWizard()
              .getCdrWizardUIModel().getReviewStatus(), CDRConstants.REVIEW_STATUS.OPEN.getUiType())) {
            // display warn dialog that the operation cannot be done for cancelled reviews
            CDMLogger.getInstance().warnDialog("Project details cannot be modified in a cancelled review!!",
                Activator.PLUGIN_ID);
          }
          else {
            PIDCVaraintSelDialog dialog = new VersionVaraintSelDialog(Display.getDefault().getActiveShell(),
                ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage);
            dialog.open();
          }
        }


      });
    }

    getA2lFileForA2lCombo();

  }

  /**
   * @param cdrWizardUIModel
   */
  private void fillA2lCombo(final CDRWizardUIModel cdrWizardUIModel) {
    try {
      SortedSet<String> a2lFileNames = new TreeSet<>();
      String selA2lFileForDisp = "";
      Map<Long, PidcA2l> a2lFileBySdom = new CDRHandler().getA2LFileBySdom(cdrWizardUIModel.getSelectedPidcVerId(),
          cdrWizardUIModel.getSelectedReviewPverName());
      for (PidcA2l pidcA2l : a2lFileBySdom.values()) {
        if (pidcA2l.isActive()) {
          a2lFileNames
              .add(pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " + pidcA2l.getName());

          if ((cdrWizardUIModel.getA2lFileId() != null) &&
              cdrWizardUIModel.getA2lFileId().equals(pidcA2l.getA2lFileId())) {
            selA2lFileForDisp =
                pidcA2l.getSdomPverName() + " : " + pidcA2l.getSdomPverVarName() + " : " + pidcA2l.getName();
          }
        }
      }
      ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameComboViewer()
          .setContentProvider(ArrayContentProvider.getInstance());
      // Adding a2l file name to the combo
      ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameComboViewer().setInput(a2lFileNames);
      // enable filtering A2lFile name when A2lFile name is typed on the combo
      ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.setA2lComboContentProposal();
      if (CommonUtils.isEmptyString(selA2lFileForDisp) && (cdrWizardUIModel.getSelectedPidcVariantName() != null) &&
          !a2lFileNames.isEmpty()) {
        CDMLogger.getInstance()
            .warnDialog(CommonUtils.concatenate("Please select the A2L file for the selected variant  \"",
                cdrWizardUIModel.getSelectedPidcVariantName(), "\""), Activator.PLUGIN_ID);
      }
      else {
        // Set selection of a2l file in combo
        if (ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameComboViewer() != null) {
          String[] items = ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameCombo().getItems();
          for (int count = 0; count < items.length; count++) {
            if (items[count].equals(selA2lFileForDisp)) {
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameCombo().select(count);
            }
          }
        }
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   *
   */
  private void getA2lFileForA2lCombo() {
    if (this.prjDataSelWizPage.getA2lNameComboViewer() != null) {

      this.prjDataSelWizPage.getA2lNameCombo().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent evnt) {
          int index =
              ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameCombo().getSelectionIndex();
          String item = ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage.getA2lNameCombo().getItem(index);
          if (CommonUtils.isNotNull(item)) {
            String[] pverStrArr = item.split(":");
            if (CommonUtils.isNotNull(pverStrArr) &&
                (pverStrArr.length > ProjectDataSelectionWizardPage.getA2lComboStrArrLen())) {
              CDRWizardUIModel cdrWizardUIModel = ProjectDataSelectionWizardPageListener.this.prjDataSelWizPage
                  .getCalDtReviewWizard().getCdrWizardUIModel();
              Map<Long, PidcA2l> a2lFileBySdom;
              try {
                a2lFileBySdom = new CDRHandler().getA2LFileBySdom(cdrWizardUIModel.getSelectedPidcVerId(),
                    cdrWizardUIModel.getSelectedReviewPverName());
                for (PidcA2l pidcA2l : a2lFileBySdom.values()) {
                  if (pidcA2l.getName()
                      .equals(pverStrArr[ProjectDataSelectionWizardPage.getA2lComboStrArrLen()].trim())) {
                    cdrWizardUIModel.setA2lFileId(pidcA2l.getA2lFileId());
                    cdrWizardUIModel.setPidcA2lId(pidcA2l.getId());
                    cdrWizardUIModel.setSsdSWVersionId(pidcA2l.getSsdSoftwareVersionId());
                  }
                }
              }
              catch (ApicWebServiceException e) {
                CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
              }

              if ((null == ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getPidcA2LBO()) ||
                  !ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getPidcA2LBO().getPidcA2lId()
                      .equals(cdrWizardUIModel.getPidcA2lId())) {
                // Download a2L file
                PidcA2LBO pidcA2LBO = new PidcA2LBO(cdrWizardUIModel.getPidcA2lId(), null);
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setPidcA2LBO(pidcA2LBO);
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setA2lEditorDP(null);
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setA2lEditiorDataHandler(null);
                resetWpRelatedData();
                ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.setA2lFileChanged(true);
              }
              ProjectDataSelectionWizardPageListener.this.projectSelecValidator.checkNextBtnEnable();
            }
          }
        }
      });
    }
  }


  /**
   * clear the already set data , if different variant is selected
   *
   * @param selectedVariant
   */
  private void resetOnVariantChange(final PidcVariant selectedVariant) {
    if (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWpSelWizPage().getWorkPackageRadio()
        .getSelection() &&
        (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
            .getSelectedPidcVariantId() != null) &&
        !selectedVariant.getId().equals(ProjectDataSelectionWizardPageListener.this.calDataReviewWizard
            .getCdrWizardUIModel().getSelectedPidcVariantId())) {
      resetWpRelatedData();
    }
  }


  /**
   *
   */
  private void resetWpRelatedData() {
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWpSelWizPage().getReviewFuncList().getList()
        .removeAll();
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWpSelWizPage().getWrkPkgFuncsList().getList()
        .removeAll();
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWpSelWizPage().getWorkingPkg().setText("");
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().getRvwWpAndRespModelSet()
        .clear();
    if (null != ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
        .getAvailableFunctions()) {
      ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().getAvailableFunctions()
          .clear();
    }
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setWpRespName(null);
    if (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWpSelWizPage().getFunctions() != null) {
      ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWpSelWizPage().getFunctions().clear();
    }
    if (ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
        .getSelReviewFuncs() != null) {
      ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().getSelReviewFuncs().clear();
    }
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getWorkPackageRespMap().clear();
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setMulWPRespNames(null);
    ProjectDataSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().getFunctionMap().clear();
  }


}
