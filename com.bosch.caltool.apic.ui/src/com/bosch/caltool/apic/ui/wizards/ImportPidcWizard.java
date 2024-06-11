/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.wizards;

import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.poi.ss.usermodel.DateUtil;
import org.eclipse.jface.wizard.Wizard;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.icdm.client.bo.apic.PidcImportHandler;
import com.bosch.caltool.icdm.common.bo.apic.AttributeCommon;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateAndNumValidator;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcImportCompareData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.ProjectImportAttr;
import com.bosch.caltool.icdm.report.compare.PIDCImportCompareExport;

/**
 * The Class ImportPidcWizard.
 *
 * @author jvi6cob
 */
public class ImportPidcWizard extends Wizard {

  /** The excel wizard page. */
  private ImportExcelWizardPage excelWizardPage;

  /** The compare import wizard page. */
  private CompareImportWizardPage compareImportWizardPage;

  /** The pidc version. */
  private final PidcVersion pidcVersion;

  /** The is import finished. */
  private boolean isImportFinished = false;


  /**
   * Instantiates a new import pidc wizard.
   *
   * @param pidcVersion the pidc version
   */
  public ImportPidcWizard(final PidcVersion pidcVersion) {
    super();
    this.pidcVersion = pidcVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPages() {
    setNeedsProgressMonitor(true);
    setWindowTitle("Import PIDC");
    this.excelWizardPage = new ImportExcelWizardPage("Import Excel Page", this.pidcVersion);
    addPage(this.excelWizardPage);
    this.compareImportWizardPage = new CompareImportWizardPage("Compare PIDC", this.pidcVersion);
    addPage(this.compareImportWizardPage);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performFinish() {
    try {
      final String filePathText = this.excelWizardPage.getFilePathText().getText();
      if ((filePathText != null) && !filePathText.isEmpty()) {
        getContainer().run(true, true, monitor -> {

          monitor.beginTask("Importing PIDC ...", 100);
          monitor.worked(50);
          PidcImportCompareData compareData = ImportPidcWizard.this.compareImportWizardPage.getCompareData();
          ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
          updationModel.setPidcVersion(ImportPidcWizard.this.pidcVersion);

          compareData.getVerAttrImportData().values().stream().filter(impAttr -> impAttr.isValidImport())
              .forEach(validImpAttr -> updateVerAttr(validImpAttr, updationModel));

          updateVariantData(compareData, updationModel);
          updateSubvarData(compareData, updationModel);

          PidcImportHandler importHandler = new PidcImportHandler();
          this.isImportFinished = importHandler.updatePidcAttr(updationModel);
          if (this.isImportFinished) {
            String fileSelected = ImportPidcWizard.this.compareImportWizardPage.getFileSelected();
            String fileExtn = ImportPidcWizard.this.compareImportWizardPage.getFileExtn();
            // ICDM-1445 Error occurred in parent cmd should mark child cmds as failed
            // No export is done when there is no difference between imported and existing PIDCard
            List<ProjectImportAttr<?>> compareAttrs = getPIDCImportData(compareData);
            if ((fileSelected != null) && !fileSelected.isEmpty() && (CommonUtils.isNotEmpty(compareAttrs))) {
              monitor.setTaskName("Saving Results");
              PIDCImportCompareExport pidcImportCompareExport = new PIDCImportCompareExport(compareAttrs);
              pidcImportCompareExport.exportPIDC(ImportPidcWizard.this.pidcVersion, fileSelected, fileExtn);
            }
          }
          monitor.worked(70);

          monitor.done();
        });
      }
    }
    catch (InvocationTargetException | InterruptedException e) {
      CDMLogger.getInstance().warn(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return this.isImportFinished;
  }

  /**
   * Gets the PIDC import data.
   *
   * @param compareData the compare data
   * @return the PIDC import data
   */
  private List<ProjectImportAttr<?>> getPIDCImportData(final PidcImportCompareData compareData) {
    if ((null != compareData) && (!compareData.getVerAttrImportData().isEmpty() ||
        !compareData.getVarAttrImportData().isEmpty() || !compareData.getSubvarAttrImportData().isEmpty())) {
      List<ProjectImportAttr<?>> compareAttrs = new ArrayList<>();
      compareAttrs.addAll(compareData.getVerAttrImportData().values());
      compareData.getVarAttrImportData().values().forEach(varMap -> compareAttrs.addAll(varMap.values()));
      compareData.getSubvarAttrImportData().values().forEach(varMap -> compareAttrs.addAll(varMap.values()));
      return compareAttrs;
    }
    return Collections.emptyList();
  }

  /**
   * Update subvar data.
   *
   * @param compareData the compare data
   * @param updationModel the updation model
   */
  private void updateSubvarData(final PidcImportCompareData compareData,
      final ProjectAttributesUpdationModel updationModel) {
    Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubvarAttrsToBeCreated = new HashMap<>();
    Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubvarAttrsToBeCreatedWithNewVal = new HashMap<>();
    Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubvarAttrsToBeUpdated = new HashMap<>();
    Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubvarAttrsToBeUpdWithNewVal = new HashMap<>();
    compareData.getSubvarAttrImportData().entrySet().stream()
        .forEach(entry -> updateValidSubvarAttr(entry, pidcSubvarAttrsToBeUpdated, pidcSubvarAttrsToBeUpdWithNewVal));
    updationModel.getPidcSubVarAttrsToBeCreated().putAll(pidcSubvarAttrsToBeCreated);
    updationModel.getPidcSubVarAttrsToBeCreatedWithNewVal().putAll(pidcSubvarAttrsToBeCreatedWithNewVal);
    updationModel.getPidcSubVarAttrsToBeUpdated().putAll(pidcSubvarAttrsToBeUpdated);
    updationModel.getPidcSubVarAttrsToBeUpdatedWithNewVal().putAll(pidcSubvarAttrsToBeUpdWithNewVal);
  }

  /**
   * Update variant data.
   *
   * @param compareData the compare data
   * @param updationModel the updation model
   */
  private void updateVariantData(final PidcImportCompareData compareData,
      final ProjectAttributesUpdationModel updationModel) {
    Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreated = new HashMap<>();
    Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreatedWithNewVal = new HashMap<>();
    Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdated = new HashMap<>();
    Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdWithNewVal = new HashMap<>();
    compareData.getVarAttrImportData().entrySet().stream()
        .forEach(entry -> updateValidVarAttr(entry, pidcVarAttrsToBeUpdated, pidcVarAttrsToBeUpdWithNewVal,
            pidcVarAttrsToBeCreated, pidcVarAttrsToBeCreatedWithNewVal));
    updationModel.getPidcVarAttrsToBeCreated().putAll(pidcVarAttrsToBeCreated);
    updationModel.getPidcVarAttrsToBeCreatedWithNewVal().putAll(pidcVarAttrsToBeCreatedWithNewVal);
    updationModel.getPidcVarAttrsToBeUpdated().putAll(pidcVarAttrsToBeUpdated);
    updationModel.getPidcVarAttrsToBeUpdatedWithNewVal().putAll(pidcVarAttrsToBeUpdWithNewVal);
  }

  /**
   * Update valid subvar attr.
   *
   * @param entry the entry
   * @param pidcSubvarAttrsToBeUpdated the pidc subvar attrs to be updated
   * @param pidcSubvarAttrsToBeUpdWithNewVal the pidc subvar attrs to be upd with new val
   * @param pidcSubvarAttrsToBeCreated the pidc subvar attrs to be created
   * @param pidcSubvarAttrsToBeCreatedWithNewVal the pidc subvar attrs to be created with new val
   */
  private void updateValidSubvarAttr(final Entry<Long, Map<Long, ProjectImportAttr<PidcSubVariantAttribute>>> entry,
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubvarAttrsToBeUpdated,
      final Map<Long, Map<Long, PidcSubVariantAttribute>> pidcSubvarAttrsToBeUpdWithNewVal) {
    Map<Long, PidcSubVariantAttribute> subvarAttrMap = new HashMap<>();
    Map<Long, PidcSubVariantAttribute> subvarAttrMapWithNewVal = new HashMap<>();
    for (Entry<Long, ProjectImportAttr<PidcSubVariantAttribute>> subvarAttr : entry.getValue().entrySet()) {
      if (subvarAttr.getValue().isValidImport()) {
        if (subvarAttr.getValue().isNewlyAddedVal()) {
          parseIfDateValue(subvarAttr.getValue().getCompareAttr(), subvarAttr.getValue().getAttr());
          subvarAttrMapWithNewVal.put(subvarAttr.getKey(), subvarAttr.getValue().getCompareAttr());
        }
        else {
          subvarAttrMap.put(subvarAttr.getKey(), subvarAttr.getValue().getCompareAttr());
        }
      }
    }
    pidcSubvarAttrsToBeUpdated.put(entry.getKey(), subvarAttrMap);
    pidcSubvarAttrsToBeUpdWithNewVal.put(entry.getKey(), subvarAttrMapWithNewVal);
  }

  /**
   * Update valid var attr.
   *
   * @param entry the entry
   * @param pidcVarAttrsToBeUpdated the pidc var attrs to be updated
   * @param pidcVarAttrsToBeUpdWithNewVal the pidc var attrs to be upd with new val
   * @param pidcVarAttrsToBeCreated the pidc var attrs to be created
   * @param pidcVarAttrsToBeCreatedWithNewVal the pidc var attrs to be created with new val
   */
  private void updateValidVarAttr(final Entry<Long, Map<Long, ProjectImportAttr<PidcVariantAttribute>>> entry,
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdated,
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeUpdWithNewVal,
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreated,
      final Map<Long, Map<Long, PidcVariantAttribute>> pidcVarAttrsToBeCreatedWithNewVal) {
    Map<Long, PidcVariantAttribute> newvarAttrMap = new HashMap<>();
    Map<Long, PidcVariantAttribute> newvarAttrwithNewValMap = new HashMap<>();
    Map<Long, PidcVariantAttribute> varAttrMap = new HashMap<>();
    Map<Long, PidcVariantAttribute> varAttrwithNewValMap = new HashMap<>();
    for (Entry<Long, ProjectImportAttr<PidcVariantAttribute>> varAttr : entry.getValue().entrySet()) {
      ProjectImportAttr<PidcVariantAttribute> validVarAttr = varAttr.getValue();
      if (validVarAttr.isValidImport()) {
        if (validVarAttr.isCreateAttr()) {
          if (validVarAttr.isNewlyAddedVal()) {
            parseIfDateValue(varAttr.getValue().getCompareAttr(), varAttr.getValue().getAttr());
            newvarAttrwithNewValMap.put(varAttr.getKey(), validVarAttr.getCompareAttr());
          }
          else {
            newvarAttrMap.put(varAttr.getKey(), validVarAttr.getCompareAttr());
          }
        }
        else if (validVarAttr.isNewlyAddedVal()) {
          parseIfDateValue(varAttr.getValue().getCompareAttr(), varAttr.getValue().getAttr());
          varAttrwithNewValMap.put(varAttr.getKey(), validVarAttr.getCompareAttr());
        }
        else {
          varAttrMap.put(varAttr.getKey(), validVarAttr.getCompareAttr());
        }
      }
    }
    pidcVarAttrsToBeCreated.put(entry.getKey(), newvarAttrMap);
    pidcVarAttrsToBeCreatedWithNewVal.put(entry.getKey(), newvarAttrwithNewValMap);
    pidcVarAttrsToBeUpdated.put(entry.getKey(), varAttrMap);
    pidcVarAttrsToBeUpdWithNewVal.put(entry.getKey(), varAttrwithNewValMap);
  }

  /**
   * Update ver attr.
   *
   * @param validImpAttr the valid imp attr
   * @param updationModel the updation model
   */
  private void updateVerAttr(final ProjectImportAttr<PidcVersionAttribute> validImpAttr,
      final ProjectAttributesUpdationModel updationModel) {

    if (validImpAttr.isCreateAttr()) {
      if (validImpAttr.isNewlyAddedVal()) {
        parseIfDateValue(validImpAttr.getCompareAttr(), validImpAttr.getAttr());
        updationModel.getPidcAttrsToBeCreatedwithNewVal().put(validImpAttr.getCompareAttr().getAttrId(),
            validImpAttr.getCompareAttr());
      }
      else {
        updationModel.getPidcAttrsToBeCreated().put(validImpAttr.getCompareAttr().getAttrId(),
            validImpAttr.getCompareAttr());
      }
    }
    else if (validImpAttr.isNewlyAddedVal()) {
      parseIfDateValue(validImpAttr.getCompareAttr(), validImpAttr.getAttr());
      updationModel.getPidcAttrsToBeUpdatedwithNewVal().put(validImpAttr.getCompareAttr().getAttrId(),
          validImpAttr.getCompareAttr());
    }
    else if (validImpAttr.getPidcAttr().getId() != null) {
      updationModel.getPidcAttrsToBeUpdated().put(validImpAttr.getCompareAttr().getAttrId(),
          validImpAttr.getCompareAttr());
    }
  }


  /**
   * @param compareAttr
   * @param attribute
   */
  private void parseIfDateValue(final IProjectAttribute compareAttr, final Attribute attribute) {
    IProjectAttribute projAttr = compareAttr;
    if (projAttr instanceof PidcVersionAttribute) {
      PidcVersionAttribute pidcVersAttr = (PidcVersionAttribute) projAttr;
      if (pidcVersAttr.getValueType().equals(AttributeValueType.DATE.getDisplayText())) {
        setNewDateValueToAttr(attribute, projAttr);
      }
    }
    if (projAttr instanceof PidcVariantAttribute) {
      PidcVariantAttribute varAttr = (PidcVariantAttribute) projAttr;
      if (varAttr.getValueType().equals(AttributeValueType.DATE.getDisplayText())) {
        setNewDateValueToAttr(attribute, projAttr);
      }
    }
    if (projAttr instanceof PidcSubVariantAttribute) {
      PidcSubVariantAttribute subVarAttr = (PidcSubVariantAttribute) projAttr;
      if (subVarAttr.getValueType().equals(AttributeValueType.DATE.getDisplayText())) {
        setNewDateValueToAttr(attribute, projAttr);
      }
    }
  }

  /**
   * @param attribute
   * @param projAttr
   */
  private void setNewDateValueToAttr(final Attribute attribute, final IProjectAttribute projAttr) {
    String format = attribute.getFormat();
    SimpleDateFormat sdf;
    if ((format == null) || " ".equals(format)) {
      format = DateFormat.DFLT_DATE_FORMAT;
    }
    String temp = DateAndNumValidator.getInstance().formatDate(format);
    sdf = new SimpleDateFormat(temp, Locale.getDefault(Locale.Category.FORMAT));
    sdf.setLenient(false);
    // For newly added date values with attribute format "yyyy.MM.dd", parsing should be done as below
    // because the value is read as double value by apache poi
    if (!projAttr.getValue().contains(".")) {
      Date parsedDate = DateUtil.getJavaDate(Double.parseDouble(projAttr.getValue()));
      projAttr.setValue(sdf.format(parsedDate));
    }
    try {
      String formattedDate = AttributeCommon.convertAttrDateStringToDefaultDateFormat(temp, projAttr.getValue());
      projAttr.setValue(formattedDate);
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * Gets the excel wizard page.
   *
   * @return the excelWizardPage
   */
  public ImportExcelWizardPage getExcelWizardPage() {
    return this.excelWizardPage;
  }

  /**
   * Gets the compare import wizard page.
   *
   * @return the compareImportWizardPage
   */
  public CompareImportWizardPage getCompareImportWizardPage() {
    return this.compareImportWizardPage;
  }

}