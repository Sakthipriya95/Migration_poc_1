/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.listeners;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.a2ldata.module.calibration.group.Function;
import com.bosch.calmodel.a2ldata.module.labels.Characteristic;
import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.ReviewWrkPkgSelectionDialog;
import com.bosch.caltool.cdr.ui.wizard.page.validator.WorkPackageSelectionWizardPageValidator;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.cdr.ui.wizards.pages.WorkpackageSelectionWizardPage;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.a2l.WpRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.RvwWpAndRespModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * @author say8cob
 */
public class WorkPackageSelectionWizardPageListener {


  private final CalDataReviewWizard calDataReviewWizard;
  private final WorkPackageSelectionWizardPageValidator workPackageSelectionWizardPageValidator;
  private final WorkpackageSelectionWizardPage wpSelWizPage;

  private Map<String, Characteristic> characteristicsMap = new HashMap<>();

  Map<String, Set<String>> functionParamMap = new HashMap<>();


  /**
   * @param calDataReviewWizardNew CalDataReviewWizard
   * @param dataSelectionWizardPageValidator WorkPackageSelectionWizardPageValidator
   */
  public WorkPackageSelectionWizardPageListener(final CalDataReviewWizard calDataReviewWizardNew,
      final WorkPackageSelectionWizardPageValidator dataSelectionWizardPageValidator) {
    this.calDataReviewWizard = calDataReviewWizardNew;
    this.workPackageSelectionWizardPageValidator = dataSelectionWizardPageValidator;
    this.wpSelWizPage = calDataReviewWizardNew.getWpSelWizPage();
  }

  /**
   *
   */
  public void createActionListeners() {
    if (this.wpSelWizPage.getSsdRuleButton() != null) {
      this.wpSelWizPage.getSsdRuleButton().addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent event) {

          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setCommonRulesSecondary(
              WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().getSelection() ||
                  WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCommonRuleRadio().getSelection());
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
          if (!WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSsdRuleButton().getSelection()) {
            WorkPackageSelectionWizardPageListener.this.wpSelWizPage.removeRuleSetData(
                WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel());
          }
        }
      });
    }

    if (this.wpSelWizPage.getWrkPkgFuncsList() != null) {

      this.wpSelWizPage.getWrkPkgFuncsList().addDoubleClickListener(event -> moveLeftToRight());

    }

    if (this.wpSelWizPage.getReviewFuncList() != null) {

      this.wpSelWizPage.getReviewFuncList().addDoubleClickListener(event -> moveRightToLeft());

      this.wpSelWizPage.getReviewFuncList().getList().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setSelectedFuncsToReview(
              WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFuncList().getList().getSelection());
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getAddButton().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getRemoveButton().setEnabled(true);
        }
      });
    }

    if (this.wpSelWizPage.getWorkPackageRadio() != null) {

      this.wpSelWizPage.getWorkPackageRadio().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          clearCDRWizardModelForWorkPackagePage();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpLabel().setText("Work Package");
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getFileName() != null) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setFunLabFilePath("");
          }
          resetWpgSelection();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunFileDropFileListener().setEditable(false);

          // Icdm-729 reset all the values
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkg().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkgBrowse().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCalDataReviewWizard().setContentChanged(true);
          clearReviewFuncs();
          clearWPFuncs();
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setSourceType(CDRConstants.CDR_SOURCE_TYPE.WP.getDbType());
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setFunLabPath("");
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setLabelList(new ArrayList<String>());


          enableReviewFunctionLists();
        }

      });

    }

    if (this.wpSelWizPage.getA2lRadio() != null) {

      this.wpSelWizPage.getA2lRadio().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          clearCDRWizardModelForWorkPackagePage();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkg().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkgBrowse().setEnabled(false);


          final ReviewWrkPkgSelectionDialog wrkPkgSelDialog = new ReviewWrkPkgSelectionDialog(
              Display.getCurrent().getActiveShell(), WorkPackageSelectionWizardPageListener.this.calDataReviewWizard
                  .getA2lEditiorDataHandler().getA2lFileInfo(),
              null);

          wrkPkgSelDialog.checkIfWPAvailable();
          resetWpgSelection();
          clearReviewFuncs();
          clearWPFuncs();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setFunLabPath("");
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setAllFucntions();
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getA2lRadio().getSelection()) {

            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.A2L_FILE.getDbType());
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setA2lGroupName(null);
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setA2lGroupNameList(null);
          }
          else {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());
          }
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setLabelList(new ArrayList<String>());

          enableReviewFunctionLists();

        }
      });
    }

    if (this.wpSelWizPage.getAddButton() != null) {

      this.wpSelWizPage.getAddButton().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          moveLeftToRight();
        }
      });
    }

    if (this.wpSelWizPage.getRemoveButton() != null) {

      this.wpSelWizPage.getRemoveButton().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          moveRightToLeft();
        }
      });
    }

    if (this.wpSelWizPage.getWrkPkgFuncsList() != null) {

      this.wpSelWizPage.getWrkPkgFuncsList().getList().addListener(SWT.Selection, event -> {

        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setSelectedWrkPkgFuncs(
            WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWrkPkgFuncsList().getList().getSelection());
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getRemoveButton().setEnabled(false);
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getAddButton().setEnabled(true);

      });
    }

    if (this.wpSelWizPage.getLabFunRadio() != null) {

      this.wpSelWizPage.getLabFunRadio().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {

          clearCDRWizardModelForWorkPackagePage();
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getFileName() != null) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setFunLabFilePath("");
          }
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpLabel().setText("LAB/FUN file");
          resetWpgSelection();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunFileDropFileListener().setEditable(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkg().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkgBrowse().setEnabled(true);
          // Icdm-729 reset all the values
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
          clearReviewFuncs();
          clearWPFuncs();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().setEnabled(true);
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunRadio().getSelection()) {

            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.LAB_FILE.getDbType());
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setA2lGroupName(null);
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setA2lGroupNameList(null);
          }
          else {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());
          }
          enableReviewFunctionLists();
        }

      });
    }

    if (this.wpSelWizPage.getParamRadio() != null) {
      this.wpSelWizPage.getParamRadio().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          clearCDRWizardModelForWorkPackagePage();
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getFileName() != null) {

            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setFunLabFilePath("");
          }
          resetWpgSelection();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkg().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkgBrowse().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setPageComplete(true);
          // Icdm-729 reset all the values
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getParamRadio().getSelection()) {


            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.REVIEW_FILE.getDbType());
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setA2lGroupName(null);
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setA2lGroupNameList(null);
          }
          else {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());

          }
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
          clearReviewFuncs();
          clearWPFuncs();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setFunLabPath("");
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setFilesToBeReviewed(
              WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getParamRadio().getSelection());
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setLabelList(new ArrayList<String>());

        }


      });
    }

    if (this.wpSelWizPage.getCompliRadio() != null) {
      this.wpSelWizPage.getCompliRadio().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          clearCDRWizardModelForWorkPackagePage();
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getFileName() != null) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setFunLabFilePath("");
          }
          resetWpgSelection();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkg().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkgBrowse().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setPageComplete(true);

          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.setContentChanged(true);
          clearReviewFuncs();
          clearWPFuncs();
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().setEnabled(false);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setFunLabPath("");
          // TODO

          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCompliRadio().getSelection()) {

            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.COMPLI_PARAM.getDbType());
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setA2lGroupName(null);
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setA2lGroupNameList(null);

          }
          else {

            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setSourceType(CDRConstants.CDR_SOURCE_TYPE.NOT_DEFINED.getDbType());

          }
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setLabelList(new ArrayList<String>());

        }
      });
    }

    if (this.wpSelWizPage.getSecondaryRulesButton() != null) {

      this.wpSelWizPage.getSecondaryRulesButton().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {

          if (!WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().getSelection() &&
              " include RuleSet Rules".equals(
                  WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().getText())) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .getSecondaryRuleSetIds().clear();

          }


          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setCommonRulesSecondary(
              (!WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCommonRuleRadio().getSelection() &&
                  WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().getSelection()));

        }

      });

    }
    if (this.wpSelWizPage.getCommonRuleRadio() != null) {

      this.wpSelWizPage.getCommonRuleRadio().addSelectionListener(new SelectionAdapter() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void widgetSelected(final SelectionEvent event) {
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.commonRuleSelection(
              WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel());
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCommonRuleRadio().getSelection() &&
              !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().getSelection()) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setPrimaryRuleSetId(null);
            if (WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .getSecondaryRuleSetIds() != null) {
              WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                  .getSecondaryRuleSetIds().clear();
            }
          }
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setCommonRulesPrimary(true);
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setCommonRulesSecondary(false);

          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton()
              .setText(" include RuleSet Rules");
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().setEnabled(true);
        }
      });
    }

    if (this.wpSelWizPage.getRuleSetRadio() != null) {
      this.wpSelWizPage.getRuleSetRadio().addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkPackageRadio().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getA2lRadio().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunRadio().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getParamRadio().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCompliRadio().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().setEnabled(true);
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard
              .setContentChanged(WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                  .getPrimaryRuleSetId() == null);
          WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
              .setCommonRulesPrimary(false);

          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton()
              .setText(" include Common Rules");
          WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getSecondaryRulesButton().setEnabled(true);
          if (checkA2lLabFunParamRadioSelection() && checkWPCompliRadioSelection()) {
            WorkPackageSelectionWizardPageListener.this.wpSelWizPage.setPageComplete(false);
          }
        }

        /**
         * @return
         */
        private boolean checkWPCompliRadioSelection() {
          return !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkPackageRadio().getSelection() &&
              !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getCompliRadio().getSelection();
        }

        /**
         * @return
         */
        private boolean checkA2lLabFunParamRadioSelection() {
          return !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getA2lRadio().getSelection() &&
              !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunRadio().getSelection() &&
              !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getParamRadio().getSelection();
        }

      });
    }


    if (this.wpSelWizPage.getReviewFunFilterTxt() != null) {
      this.wpSelWizPage.getReviewFunFilterTxt().addModifyListener(event -> {
        final String text =
            WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilterTxt().getText().trim();
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFuncList().resetFilters();
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilters().setFilterText(text);
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFuncList()
            .addFilter(WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFunFilters());
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getReviewFuncList().refresh();
      });

    }

    if (this.wpSelWizPage.getWpFilterTxt() != null) {

      this.wpSelWizPage.getWpFilterTxt().addModifyListener(event -> {

        final String text = WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilterTxt().getText().trim();
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilters().setFilterText(text);
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWrkPkgFuncsList()
            .addFilter(WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWpFilters());
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWrkPkgFuncsList().refresh();
        WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunFileDropFileListener()
            .setEditable(WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunRadio().getSelection());
      });
    }


    this.wpSelWizPage.getWorkingPkg().addModifyListener(event -> {
      if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunRadio().getSelection()) {
        String fileSelected =
            WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunFileDropFileListener().getDropFilePath();
        if ((fileSelected != null) &&
            !WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getWorkingPkg().getText().isEmpty()) {
          readLabFunFile();
        }
      }
    });


    this.wpSelWizPage.getWorkingPkgBrowse().addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getLabFunRadio().getSelection()) {
          createFunLabelDialog();
        }
        else {
          createWorkPkgSelDialog();
        }
        enableReviewFunctionLists();
      }
    });

    Button onlyOBDRadio = this.wpSelWizPage.getOnlyOBDRadio();
    if (CommonUtils.isNotNull(onlyOBDRadio)) {

      onlyOBDRadio.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getOnlyOBDRadio().getSelection()) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setObdFlag(CDRConstants.OBD_OPTION.ONLY_OBD_LABELS.getDbType());
          }
        }
      });
    }

    Button bothOBDNonOBDRadio = this.wpSelWizPage.getBothOBDNonOBDRadio();
    if (CommonUtils.isNotNull(bothOBDNonOBDRadio)) {

      bothOBDNonOBDRadio.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getBothOBDNonOBDRadio().getSelection()) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setObdFlag(CDRConstants.OBD_OPTION.BOTH_OBD_AND_NON_OBD_LABELS.getDbType());
          }
        }
      });
    }

    Button noOBDRadioBtn = this.wpSelWizPage.getNoOBDRadio();
    if (CommonUtils.isNotNull(noOBDRadioBtn)) {

      noOBDRadioBtn.addSelectionListener(new SelectionAdapter() {

        @Override
        public void widgetSelected(final SelectionEvent event) {
          if (WorkPackageSelectionWizardPageListener.this.wpSelWizPage.getNoOBDRadio().getSelection()) {
            WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel()
                .setObdFlag(CDRConstants.OBD_OPTION.NO_OBD_LABELS.getDbType());
          }
        }
      });
    }
  }


  /**
   * create the file dialog to select the lab file dialog for the Lab fun file selection
   */
  protected void createFunLabelDialog() {
    final FileDialog fileDialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
    fileDialog.setText("Select the function/label file");
    // icdm=1131
    CommonUtils.swapArrayElement(this.wpSelWizPage.getFilExtensions(),
        this.wpSelWizPage.getPreference().getString(CommonUtils.IMPORT_LAB_FUN_FILES_EXTN), 0);

    CommonUtils.swapArrayElement(WorkpackageSelectionWizardPage.getFilenames(),
        this.wpSelWizPage.getPreference().getString(CommonUtils.IMPORT_LAB_FUN_FILES_NAME), 0);
    fileDialog.setFilterExtensions(this.wpSelWizPage.getFilExtensions());
    fileDialog.setFilterNames(WorkpackageSelectionWizardPage.getFilenames());
    final String fileSelected = fileDialog.open();
    // store preferences
    this.wpSelWizPage.getPreference().setValue(CommonUtils.IMPORT_LAB_FUN_FILES_EXTN,
        this.wpSelWizPage.getFilExtensions()[fileDialog.getFilterIndex()]);

    this.wpSelWizPage.getPreference().setValue(CommonUtils.IMPORT_LAB_FUN_FILES_NAME,
        WorkpackageSelectionWizardPage.getFilenames()[fileDialog.getFilterIndex()]);

    this.wpSelWizPage.getWorkingPkg().setText(fileSelected);
    readLabFunFile();
  }


  /**
   * read the selected file
   */
  private void readLabFunFile() {

    String filepath = this.wpSelWizPage.getWorkingPkg().getText();
    this.wpSelWizPage.setFileName(filepath);
    if (!CommonUtils.isValidFilePath().test(filepath)) {
      MessageDialogUtils.getErrorMessageDialog("Error : Invalid File Path",
          "The selected file path is not a valid one. Check whether the following conditions are satisfied\n\n1. Parent directory length should not exceed 247 characters excluding backslash(\\) at the end.\n2. Total file path length should not exceed 259 characters.\n3. File name should not contain invalid characters \\ / : * ? \" < > |.");
      this.wpSelWizPage.clearFields();
      return;
    }

    final File selectedFile = new File(filepath);
    if (selectedFile.exists()) {
      // Adding the Fun/lab file path. Also remove the Functions list
      this.wpSelWizPage.setFunLabPath(filepath);
      WorkPackageSelectionWizardPageListener.this.calDataReviewWizard.getCdrWizardUIModel().setFunLabFilePath(filepath);
      this.calDataReviewWizard.setContentChanged(true);
      clearReviewFuncs();
      clearWPFuncs();
      if (filepath.contains(".")) {
        this.calDataReviewWizard.getFunLabFromParser(this.wpSelWizPage.getFileName());
      }
      this.workPackageSelectionWizardPageValidator.checkNextBtnEnable();
    }
  }


  /**
   * @param cdrReviewData
   */
  private void createWorkPkgSelDialog() {

    A2LFileInfo a2lFileInfo = this.calDataReviewWizard.getA2lEditiorDataHandler().getA2lFileInfo();
    BusyIndicator.showWhile(Display.getDefault().getActiveShell().getDisplay(), () -> {

      final ReviewWrkPkgSelectionDialog wrkPkgSelDialog = new ReviewWrkPkgSelectionDialog(
          Display.getCurrent().getActiveShell(), a2lFileInfo, this.calDataReviewWizard.getWorkPackageRespMap());
      wrkPkgSelDialog.open();
      final List selectedElement = wrkPkgSelDialog.getSelectedElement();
      if ((selectedElement != null) && (selectedElement.get(0) instanceof WpRespModel)) {
        setSelForWorkPackage(selectedElement);
      }
      this.workPackageSelectionWizardPageValidator.checkNextBtnEnable();
    });
  }


  /**
   * @param cdrReviewData
   * @param selectedElement
   * @throws ApicWebServiceException
   */
  private void setSelForWorkPackage(final List selectedElement) {
    setWrkPkgFuncList(true);
    setReviewFuncList(true);
    clearWPFuncs();
    Set<RvwWpAndRespModel> wpAndRespModelSet = new HashSet<>();
    StringJoiner wpJoiner = new StringJoiner(", ");

    this.functionParamMap.clear();
    List<WpRespModel> selectedWpResp = new ArrayList<>(selectedElement);
    selectedWpResp.forEach(wpRespModel -> {
      RvwWpAndRespModel rvWpAndRespModel = new RvwWpAndRespModel();
      rvWpAndRespModel.setA2lRespId(wpRespModel.getA2lResponsibility().getId());
      rvWpAndRespModel.setA2lWpId(wpRespModel.getA2lWpId());
      wpAndRespModelSet.add(rvWpAndRespModel);
    });
    this.calDataReviewWizard.getCdrWizardUIModel().setSelectedWpRespList(selectedWpResp);
    for (WpRespModel wpRespModel : selectedWpResp) {
      this.wpSelWizPage.setSelectedWrkPkg(wpRespModel);
      StringBuilder wpRespStrBuild = new StringBuilder();
      wpRespStrBuild.append(this.wpSelWizPage.getSelectedWrkPkg().getWpName());
      wpRespStrBuild.append(CDRConstants.OPEN_BRACES);
      wpRespStrBuild.append(this.wpSelWizPage.getSelectedWrkPkg().getA2lResponsibility().getName());
      wpRespStrBuild.append(CDRConstants.CLOSE_BRACES);

      if (selectedElement.size() == WorkpackageSelectionWizardPage.SING_WORK_PACK_SEL_SIZE) {
        wpJoiner.add(wpRespStrBuild.toString());
        this.calDataReviewWizard.getCdrWizardUIModel().setWpRespName(this.wpSelWizPage.getSelectedWrkPkg().getWpName());
      }
      else {
        this.calDataReviewWizard.getCdrWizardUIModel().setA2lGroupName(null);
        wpJoiner.add(wpRespStrBuild.toString());
        removeElementsFromBase();
        if (selectedWpResp.size() == 1) {
          this.calDataReviewWizard.getCdrWizardUIModel()
              .setWpRespName(this.wpSelWizPage.getSelectedWrkPkg().getWpName());
        }
        else {
          this.calDataReviewWizard.getCdrWizardUIModel().setWpRespName(null);
        }
      }
      clearReviewFuncs();

      setWrkPkgFuncs();
      this.wpSelWizPage.getWorkingPkg().setText(wpJoiner.toString());
      this.calDataReviewWizard.getCdrWizardUIModel().setSourceType(CDRConstants.CDR_SOURCE_TYPE.WP.getDbType());
    }
    this.calDataReviewWizard.getCdrWizardUIModel().setFunctionMap(this.functionParamMap);
    this.calDataReviewWizard.getCdrWizardUIModel().setRvwWpAndRespModelSet(wpAndRespModelSet);

  }


  /**
   * This method sets the functions of selected workpackage in the list
   */
  protected void setWrkPkgFuncs() {
    setCharacteristicMap();

    final WpRespModel wpRespModel = this.wpSelWizPage.getSelectedWrkPkg();
    Map<WpRespModel, List<Long>> workPackageRespMap = this.calDataReviewWizard.getWorkPackageRespMap();
    if ((wpRespModel != null) && workPackageRespMap.containsKey(wpRespModel)) {
      Set<Long> paramIdSet = new HashSet<>(workPackageRespMap.get(wpRespModel));
      Map<Long, Parameter> paramMap = new HashMap<>();
      try {
        paramMap = new CDRHandler().getParamMapUsingParamIdSet(paramIdSet);
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
      if (!paramMap.isEmpty()) {
        SortedSet<String> functionSet = resolveFunctionsForA2lWorkPackageResp(paramMap);
        this.wpSelWizPage.setFunctions(functionSet);
        for (String functionName : this.wpSelWizPage.getFunctions()) {
          if (functionName != null) {
            this.wpSelWizPage.getWpFuncsSet().add(functionName);
          }
        }
        this.wpSelWizPage.getWrkPkgFuncsList().refresh();
      }
    }
  }

  /**
   * Added to improve performance (To avoid repetative calling of characteristicsMap)
   */
  private void setCharacteristicMap() {
    if (this.characteristicsMap.isEmpty()) {
      this.characteristicsMap = this.calDataReviewWizard.getA2lEditiorDataHandler().getCharacteristicsMap();
    }
  }

  private SortedSet<String> resolveFunctionsForA2lWorkPackageResp(final Map<Long, Parameter> paramMap) {
    SortedSet<String> funcNameSet = new TreeSet<>();


    for (Parameter parameter : paramMap.values()) {
      Characteristic charObj = this.characteristicsMap.get(parameter.getName());
      Function func = charObj.getDefFunction();
      String functionName = "";
      if (func == null) {
        if (CommonUtils.isNotEmpty(this.calDataReviewWizard.getA2lEditiorDataHandler().getUnassignedParams())) {
          // Not assigned will be added if the parameter is not assigned to any parameters
          functionName = ApicConstants.NOT_ASSIGNED;
          funcNameSet.add(functionName);
        }
      }
      else {
        functionName = func.getName();
      }
      if (!funcNameSet.contains(functionName)) {
        funcNameSet.add(functionName);
      }
      createFuncParamMap(functionName, parameter.getName());

    }
    return funcNameSet;
  }

  private void createFuncParamMap(final String function, final String paramName) {

    if (this.functionParamMap.containsKey(function)) {
      Set<String> paramSet = this.functionParamMap.get(function);
      paramSet.add(paramName);
      this.functionParamMap.put(function, paramSet);
    }
    else {
      Set<String> paramSet = new HashSet<>();
      paramSet.add(paramName);
      this.functionParamMap.put(function, paramSet);
    }

  }


  /**
   * remove the Other functions
   */
  public void removeElementsFromBase() {
    java.util.List<String> funcNames = new ArrayList<>();
    for (String funcStr : this.wpSelWizPage.getReviewFuncsSet()) {
      if (this.wpSelWizPage.getWpFuncsSet().contains(funcStr)) {
        funcNames.add(funcStr);
      }
    }
    removeWPFuncName(funcNames);
  }

  /**
   * when the Radio button is selected reset the List, label etc Icdm-715
   */
  private void resetWpgSelection() {
    this.wpSelWizPage.getWorkingPkg().setEditable(false);
    this.wpSelWizPage.getWorkingPkg().setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));
    this.wpSelWizPage.getWorkingPkg().setText("");
    clearWPFuncs();
    clearReviewFuncs();
    this.wpSelWizPage.setPageComplete(false);
  }


  /**
   * Moving the function from left to right
   */
  private void moveLeftToRight() {
    if ((this.wpSelWizPage.getSelectedWrkPkgFuncs() != null) &&
        (this.wpSelWizPage.getSelectedWrkPkgFuncs().length != 0)) {
      addReviewFuncName(Arrays.asList(this.wpSelWizPage.getSelectedWrkPkgFuncs()));
      removeWPFuncName(Arrays.asList(this.wpSelWizPage.getSelectedWrkPkgFuncs()));
      // Icdm-729 if add button is selected then set the Content Changed to true
      this.calDataReviewWizard.setContentChanged(true);
      this.wpSelWizPage.getAddButton().setEnabled(false);
      this.wpSelWizPage.setSelectedWrkPkgFuncs(null);
      enableReviewFunctionLists();
      this.calDataReviewWizard.getWpSelWizPage().setReviewFunctions();
      this.workPackageSelectionWizardPageValidator.checkNextBtnEnable();
    }
  }

  /**
   * Moving the function from right to left
   */
  private void moveRightToLeft() {
    if ((this.wpSelWizPage.getSelectedFuncsToReview() != null) &&
        (this.wpSelWizPage.getSelectedFuncsToReview().length != 0)) {

      addWPFuncName(Arrays.asList(this.wpSelWizPage.getSelectedFuncsToReview()));
      removeReviewFuncName(Arrays.asList(this.wpSelWizPage.getSelectedFuncsToReview()));
      this.wpSelWizPage.getRemoveButton().setEnabled(false);

      this.wpSelWizPage.setSelectedFuncsToReview(null);
      // Icdm-729 if the remove button is selected then set the content changed as true
      this.calDataReviewWizard.setContentChanged(true);
      enableReviewFunctionLists();
      this.workPackageSelectionWizardPageValidator.checkNextBtnEnable();
      // If the Review Function list is empty then do not enable Next Button
      if (this.wpSelWizPage.getReviewFuncsSet().isEmpty()) {
        this.wpSelWizPage.setPageComplete(false);
      }
    }
  }

  /**
   * Add the funcs to review fun set and refreshes the reviewfun viewer
   *
   * @param funcNames list of function names
   */
  public void addReviewFuncName(final List<String> funcNames) {
    this.wpSelWizPage.getReviewFuncsSet().addAll(funcNames);
    this.wpSelWizPage.getReviewFuncList().refresh();
  }

  /**
   * Removes the funcs from review fun set and refreshes the reviewfun viewer
   *
   * @param funcNames list of function names
   */
  public void removeReviewFuncName(final List<String> funcNames) {
    this.wpSelWizPage.getReviewFuncsSet().removeAll(funcNames);
    this.wpSelWizPage.getReviewFuncList().refresh();
  }

  /**
   * Clears the review funs set and refreshes the reviewfun viewer
   */
  public void clearReviewFuncs() {
    this.wpSelWizPage.getReviewFuncsSet().clear();
    this.wpSelWizPage.getReviewFuncList().refresh();
  }

  /**
   * Add the funcs to workpkg fun set and refreshes the wp viewer
   *
   * @param funcNames list of function names
   */
  public void addWPFuncName(final List<String> funcNames) {
    this.wpSelWizPage.getWpFuncsSet().addAll(funcNames);
    this.wpSelWizPage.getWrkPkgFuncsList().refresh();
  }

  /**
   * Removes the funcs from workpkg fun set and refreshes the wp viewer
   *
   * @param funcNames list of function names
   */
  public void removeWPFuncName(final java.util.List<String> funcNames) {
    this.wpSelWizPage.getWpFuncsSet().removeAll(funcNames);
    this.wpSelWizPage.getWrkPkgFuncsList().refresh();
  }

  /**
   * Clears the workpkg funs set and refreshes the wp viewer
   */
  public void clearWPFuncs() {
    this.wpSelWizPage.getWpFuncsSet().clear();
    this.wpSelWizPage.getWrkPkgFuncsList().refresh();
  }


  /**
   * enable the review function lists
   */
  public void enableReviewFunctionLists() {
    setWrkPkgFuncList(!this.wpSelWizPage.getWpFuncsSet().isEmpty());
    setReviewFuncList(!this.wpSelWizPage.getReviewFuncsSet().isEmpty());
    this.wpSelWizPage.getAddButton().setEnabled(false);
    this.wpSelWizPage.getRemoveButton().setEnabled(false);

  }

  /**
   * @param listState state of list
   */
  public void setWrkPkgFuncList(final boolean listState) {
    this.wpSelWizPage.getWrkPkgFuncsList().getList().setEnabled(listState);
  }

  /**
   * @param listState state of list
   */
  public void setReviewFuncList(final boolean listState) {
    this.wpSelWizPage.getReviewFuncList().getList().setEnabled(listState);
  }


  /**
   * Clear the fields when different a2l file is selected
   */
  public void clearFields() {
    if (!this.wpSelWizPage.getWorkingPkg().getText().isEmpty()) {
      this.wpSelWizPage.getWorkingPkg().setText("");
    }
    clearWPFuncs();
    clearReviewFuncs();
  }


  private void clearCDRWizardModelForWorkPackagePage() {
    this.calDataReviewWizard.getCdrWizardUIModel().setSelReviewFuncs(new TreeSet<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setAvailableFunctions(new TreeSet<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setFunLabFilePath(null);
    this.calDataReviewWizard.getCdrWizardUIModel().setLabelList(new ArrayList<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setCompliLabelList(new ArrayList<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setUnassignedParamsInReview(new TreeSet<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setSourceType(null);
    this.calDataReviewWizard.getCdrWizardUIModel().setFunctionMap(new HashMap<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setWpRespName(null);
    this.calDataReviewWizard.getCdrWizardUIModel().setRvwWpAndRespModelSet(new HashSet<>());
    this.calDataReviewWizard.getCdrWizardUIModel().setMulWPRespNames(null);
    this.calDataReviewWizard.getCdrWizardUIModel().setObdFlag(null);
  }

}
