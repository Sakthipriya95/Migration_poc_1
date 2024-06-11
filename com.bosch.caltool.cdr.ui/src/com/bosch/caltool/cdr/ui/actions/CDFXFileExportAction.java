/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.element.DataElement;
import com.bosch.calmodel.caldata.history.CalDataHistory;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.cdr.ui.dialogs.CDFXFileExportDialog;
import com.bosch.caltool.cdr.ui.editors.ReviewResultEditorInput;
import com.bosch.caltool.cdr.ui.editors.pages.ReviewResultParamListPage;
import com.bosch.caltool.icdm.client.bo.cdr.CdrReportDataHandler;
import com.bosch.caltool.icdm.client.bo.cdr.DataReviewScoreUtil;
import com.bosch.caltool.icdm.client.bo.cdr.ReviewResultClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.ui.Activator;
import com.bosch.caltool.icdm.common.ui.jobs.CDFFileExportJob;
import com.bosch.caltool.icdm.common.ui.jobs.rules.MutexRule;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.REVIEW_TYPE;
import com.bosch.caltool.icdm.model.cdr.CDRResultParameter;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.DATA_REVIEW_SCORE;
import com.bosch.caltool.icdm.model.cdr.ParameterReviewDetails;
import com.bosch.caltool.icdm.model.cdr.ReviewDetails;
import com.bosch.caltool.icdm.model.cdr.RvwFile;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.model.user.User;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserServiceClient;


/**
 * @author bru2cob
 */
public class CDFXFileExportAction extends Action {

  /**
   * constant for reviewed file
   */
  private static final String REVIEWED_FILE = "Reviewed file: ";
  /**
   * cdrreport data instance
   */
  private CdrReportDataHandler cdrReportData;
  /**
   * cdfx file export dialog instance
   */
  private CDFXFileExportDialog dialog;

  /**
   * cdr result
   */
  private CDRReviewResult cdrReviewResult;
  /**
   * set of parameters
   */
  private SortedSet<CDRResultParameter> parameters;
  /**
   * set of input files
   */
  private SortedSet<RvwFile> inpFiles;
  private ReviewResultClientBO resultData;

  private final Map<String, User> userMap = new HashMap<>();

  /**
   * Action class to export the cdfx file
   *
   * @param reviewResultNatPageNew result editor page
   */
  public CDFXFileExportAction(final ReviewResultParamListPage reviewResultNatPageNew) {
    super();
    this.cdrReviewResult = reviewResultNatPageNew.getCdrResult();
    this.resultData = ((ReviewResultEditorInput) (reviewResultNatPageNew.getEditorInput())).getResultData();
    setProperties();

  }

  /**
   * @param cdrReportData cdrReportData
   */
  public CDFXFileExportAction(final CdrReportDataHandler cdrReportData) {
    super();
    this.cdrReportData = cdrReportData;
    setProperties();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // if access rights not available , export cannot be done
    if (!validateAccess()) {
      return;
    }
    ConcurrentHashMap<String, CalData> cdfCalDataObjects = new ConcurrentHashMap<>();
    // Check for not null result
    if (CommonUtils.isNotNull(this.cdrReviewResult)) {
      // Create the dialog
      final String selectedFile = getSelectedFile(this.cdrReviewResult, null);
      if ((selectedFile != null) && (this.dialog.getReturnCode() == 0)) {
        // Fill the cal data Objects
        fillCalDataFromResult(cdfCalDataObjects);
        // start the export
        runExportJob(cdfCalDataObjects, selectedFile);
      }
    }
    // if export is to be done from data review report
    else if (this.cdrReportData.isFetchCheckVal()) {
      // get the param review map
      Map<String, List<ParameterReviewDetails>> paramRvwDetMap = this.cdrReportData.getCdrReport().getParamRvwDetMap();
      // get the selected file
      final String selectedFile = getSelectedFile(null, this.cdrReportData);
      if ((selectedFile != null) && (this.dialog.getReturnCode() == 0)) {
        createCalDataObjs(cdfCalDataObjects, paramRvwDetMap);
        runExportJob(cdfCalDataObjects, selectedFile);
      }


    }
    else {
      CDMLogger.getInstance().warnDialog(
          "Review Data cannot be exported since check values were not loaded during report generation!",
          Activator.PLUGIN_ID);
    }

  }

  /**
   * Access validation before allowing CDFx export from review results/report
   *
   * @return true, if access is available
   */
  private boolean validateAccess() {
    // Check Apic/Pidc rights
    boolean canExport = canExportCDFx(getPidcVersion());

    if (canExport || ((this.resultData != null) && this.resultData.getResultBo().isModifiable())) {
      return true;
    }

    // ICDM-2418 - Check Review-Result privilege
    if (this.cdrReviewResult != null) {
      CDMLogger.getInstance().warnDialog(
          "Insufficient privileges. Either 'READ' access on the PID card or 'MODIFY' access on the selected review result is required.",
          Activator.PLUGIN_ID);
    }
    // Data Review Report - Export privilege
    else {
      CDMLogger.getInstance().warnDialog("Insufficient privileges. 'READ' access on the PID card is required.",
          Activator.PLUGIN_ID);
    }
    return false;
  }

  /**
   * Checks whether the logged in user has privileges to export CDFx files from Data review report, review result etc.
   *
   * @param pidcVersion PIDC Version
   * @return true if usre has privilges
   */
  public boolean canExportCDFx(final PidcVersion pidcVersion) {
    boolean status = false;
    CurrentUserBO currentUser = new CurrentUserBO();
    try {
      if (currentUser.hasApicWriteAccess()) {
        status = true;
      }
      else if (pidcVersion != null) {
        NodeAccess curUserAccRight = currentUser.getNodeAccessRight(pidcVersion.getPidcId());
        if ((curUserAccRight != null) && (curUserAccRight.isRead())) {
          status = true;
        }
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return status;
  }

  /**
   * Find PIDC Version from the inputs
   *
   * @return PIDC Version
   */
  private PidcVersion getPidcVersion() {
    PidcVersion vers = null;
    // get version from report data
    if (this.cdrReportData != null) {
      vers = this.cdrReportData.getPidcVers();
    }
    // or from result based on where the export is to be done
    else if (this.cdrReviewResult != null) {
      vers = this.resultData.getResponse().getPidcVers();
    }
    return vers;
  }

  /**
   * Call to export job
   *
   * @param cdfCalDataObjects
   * @param selectedFile
   */
  private void runExportJob(final ConcurrentHashMap<String, CalData> cdfCalDataObjects, final String selectedFile) {
    Job exportJob = new CDFFileExportJob(new MutexRule(), cdfCalDataObjects, selectedFile, "*.cdfx", true);
    CommonUiUtils.getInstance().showView(CommonUIConstants.PROGRESS_VIEW);
    exportJob.schedule();
  }

  /**
   * Returns the selected file name
   *
   * @param object
   * @param CDRReviewResult2
   * @return the selected file
   */
  private String getSelectedFile(final CDRReviewResult result, final CdrReportDataHandler reportData) {
    if (reportData == null) {
      this.dialog = new CDFXFileExportDialog(Display.getCurrent().getActiveShell(), result);
    }
    else {
      this.dialog = new CDFXFileExportDialog(Display.getCurrent().getActiveShell(), reportData);
    }
    this.dialog.open();
    return this.dialog.getFileSelected();
  }

  /**
   * Fill caldata objects from the selected result
   *
   * @param cdfCalDataObjects
   */
  private void fillCalDataFromResult(final ConcurrentHashMap<String, CalData> cdfCalDataObjects) {
    // get all params
    SortedSet<CDRResultParameter> cdrParameters = this.resultData.getResultBo().getParameters();
    boolean checkCalDataEligible;
    for (CDRResultParameter resParam : cdrParameters) {
      // check for the cal data objects
      CalData checkedValueObj = this.resultData.getCheckedValueObj(resParam);
      if (checkedValueObj != null) {
        // ICDM-2479
        if (CommonUtils.isEmptyString(checkedValueObj.getFunctionName())) {
          checkedValueObj.setFunctionName(this.resultData.getFunctionName(resParam));
        }
        checkCalDataEligible = checkCalDataEligibleForRvwResult(this.resultData.getScore(resParam));
        if (checkCalDataEligible) {
          // Fill the map
          cdfCalDataObjects.put(resParam.getName(), checkedValueObj);
          addCheckValue(cdfCalDataObjects, resParam.getName(), checkedValueObj);
        }
      }
    }
  }


  /**
   * Creates caldata objects for export
   *
   * @param cdfCalDataObjects
   * @param paramRvwDetMap
   */
  private void createCalDataObjs(final Map<String, CalData> cdfCalDataObjects,
      final Map<String, List<ParameterReviewDetails>> paramRvwDetMap) {
    for (String paramName : paramRvwDetMap.keySet()) {
      CalData paramCheckedVal = null;
      boolean checkCalDataEligible = false;
      paramCheckedVal = this.cdrReportData.getParamCheckedVal(paramName, 0);
      // Icdm -2218- CDFX Export from Review Report without parameter with "reset state"
      checkCalDataEligible = checkCalDataEligibleForDataRvwReport(paramName);

      if ((null != paramCheckedVal) && checkCalDataEligible) {
        addCheckValue(cdfCalDataObjects, paramName, paramCheckedVal);
      }
    }
  }

  /**
   * @param reviewScoreEnum
   * @param paramName
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  // ICDM-2584
  private boolean checkCalDataEligibleForRvwResult(final DATA_REVIEW_SCORE reviewScoreEnum) {
    // make eligibilty false by default
    boolean checkCalDataEligible = false;
    // Status from review score
    if (this.dialog.isRevParamsWithstatusScore()) {
      // Only 8 and 9.
      if (CommonUtils.isNotNull(reviewScoreEnum) && reviewScoreEnum.isChecked()) {
        // Set status
        this.dialog.setSelectedStatus(reviewScoreEnum.getRatingDesc());
        checkCalDataEligible = true;
      }
      else {
        // Make others not eligible
        checkCalDataEligible = false;
      }
    }
    // All param Status from review score
    else if (this.dialog.isAllParamsStatusScore()) {
      // It should be reviewed latest
      if (CommonUtils.isNotNull(reviewScoreEnum)) {
        // set status
        this.dialog.setSelectedStatus(reviewScoreEnum.getRatingDesc());
        checkCalDataEligible = true;
      }
      else {
        checkCalDataEligible = false;
      }
    }
    // all params status from the file
    else if (this.dialog.isAllParamStatusFile()) {
      if (CommonUtils.isNull(reviewScoreEnum)) {
        checkCalDataEligible = false;
      }
      else {
        checkCalDataEligible = true;
      }
    }
    return checkCalDataEligible;
  }

  /**
   * @param paramName
   * @param reviewScoreEnum
   * @return
   * @throws IOException
   * @throws ClassNotFoundException
   */
  // ICDM-2584
  private boolean checkCalDataEligibleForDataRvwReport(final String paramName) {
    DATA_REVIEW_SCORE reviewScoreEnum = this.cdrReportData.getReviewScore(paramName);
    // make eligibilty false by default
    boolean checkCalDataEligible = false;
    // Status from review score
    if (this.dialog.isRevParamsWithstatusScore()) {
      // Only 8 and 9, Official & Locked
      if (reviewScoreEnum.isChecked() && this.cdrReportData.isOfficialAndLocked(paramName)) {
        // Set status
        this.dialog.setSelectedStatus(reviewScoreEnum.getRatingDesc());
        checkCalDataEligible = true;
      }
      else {
        // Make others not eligible
        checkCalDataEligible = false;
      }
    }
    // All param Status from review score
    else if (this.dialog.isAllParamsStatusScore()) {
      // It should be reviewed latest
      if (CommonUtils.isNotNull(reviewScoreEnum)) {
        // set status
        this.dialog.setSelectedStatus(reviewScoreEnum.getRatingDesc());
        checkCalDataEligible = true;
      }
      else {
        checkCalDataEligible = false;
      }
    }
    // all params status from the file
    else if (this.dialog.isAllParamStatusFile()) {
      if (CommonUtils.isNull(reviewScoreEnum)) {
        checkCalDataEligible = false;
      }
      else {
        checkCalDataEligible = true;
      }
    }
    return checkCalDataEligible;
  }


  /**
   * @param cdfCalDataObjects
   * @param paramName
   * @param paramCheckedVal
   */
  private void addCheckValue(final Map<String, CalData> cdfCalDataObjects, final String paramName,
      final CalData paramCheckedVal) {
    CalData paramCheckedValClone = null;
    try {
      // get a copy of the Checked Value object
      paramCheckedValClone = paramCheckedVal.clone();

      cdfCalDataObjects.put(paramName, paramCheckedValClone);
      if (!this.dialog.isAllParamStatusFile() ||
          (this.dialog.isAllParamStatusFile() && this.dialog.getIncludeReviewComment())) {
        if (paramCheckedValClone.getCalDataHistory() == null) {
          paramCheckedValClone.setCalDataHistory(new CalDataHistory());
        }

        paramCheckedValClone.getCalDataHistory().getHistoryEntryList().add(getReviewHistEntry(paramName));
      }
    }
    catch (CloneNotSupportedException excep) {
      CDMLogger.getInstance().error(excep.getLocalizedMessage(), excep, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param paramName
   * @return
   */
  private HistoryEntry getReviewHistEntry(final String paramName) {

    HistoryEntry histEntry = new HistoryEntry();
    ParameterReviewDetails parRevDetails = null;
    ReviewDetails reviewDetails = null;
    CDRReviewResult parResult = null;
    DATA_REVIEW_SCORE reviewScoreEnum = null;
    if (CommonUtils.isNull(this.cdrReviewResult)) {
      reviewScoreEnum = this.cdrReportData.getReviewScore(paramName);
      parRevDetails = this.cdrReportData.getCdrReport().getParamRvwDetMap().get(paramName).get(0);
      reviewDetails = this.cdrReportData.getReviewDetailsLatest(paramName);
      parResult = this.cdrReportData.getReviewResult(paramName, 0);
      String rvwComment;

      if (this.dialog.isAllParamStatusFile() &&
          (this.cdrReportData.getCdrReport().getParamRvwDetMap().get(paramName).get(0).getRvwComment() != null)) {
        rvwComment = this.cdrReportData.getCdrReport().getParamRvwDetMap().get(paramName).get(0).getRvwComment();

      }
      else {
        rvwComment = CommonUtils.concatenate(DataReviewScoreUtil.getInstance().getDescription(reviewScoreEnum), '\n');
        if (this.cdrReportData.getCdrReport().getParamRvwDetMap().get(paramName).get(0).getRvwComment() != null) {
          rvwComment = CommonUtils.concatenate(rvwComment,
              this.cdrReportData.getCdrReport().getParamRvwDetMap().get(paramName).get(0).getRvwComment());

        }

      }


      histEntry.setRemark(getDataElement(rvwComment));


    }
    else {
      parResult = this.cdrReviewResult;
      if (CommonUtils.isNullOrEmpty(this.parameters)) {
        this.parameters = this.resultData.getResultBo().getParameters();
      }
    }


    // TODO add review comment


    if (null == reviewDetails) {

      fillDataFromRevRes(histEntry, parResult);

      PidcVersion resultPidcVers = this.resultData.getResponse().getPidcVers();
      histEntry.setDataIdentifier(getDataElement(REVIEWED_FILE + resultPidcVers.getVersionName()));
      histEntry.setState(getDataElement(this.dialog.getSelectedStatus()));
      histEntry.setProject(getDataElement("PIDC: " + resultPidcVers.getName()));
      StringBuilder inputFiles = new StringBuilder();
      if (CommonUtils.isNullOrEmpty(this.inpFiles)) {
        this.inpFiles = this.resultData.getResultBo().getInputFiles();
      }

      for (RvwFile icdmFile : this.inpFiles) {
        inputFiles.append(icdmFile.getName() + '\n');
      }
      histEntry.setDataIdentifier(getDataElement("Reviewed files: " + inputFiles.toString()));
      // set remarks based on score and if checkbox is set
      if (this.dialog.getIncludeReviewComment()) {
        getScoreForParamResult(paramName, this.parameters, histEntry);
      }
    }
    else {
      fillDataFromRevDetails(histEntry, reviewDetails, paramName);
      histEntry.setDataIdentifier(getDataElement(REVIEWED_FILE + reviewDetails.getPidcVersName()));
      histEntry.setState(getDataElement(this.dialog.getSelectedStatus()));

      histEntry.setProject(getDataElement("PIDC: " + this.cdrReportData.getPidcVers().getName()));

      // add reviewed files
      if (null == reviewDetails.getRvwInputFileMap().get(parRevDetails.getRvwFileID())) {
        // old review where exact input file per parameter not known
        StringBuilder inputFiles = new StringBuilder();
        for (String inputFile : reviewDetails.getRvwInputFileMap().values()) {
          inputFiles.append(inputFile + '\n');

        }
        histEntry.setDataIdentifier(getDataElement("Reviewed files: " + inputFiles.toString()));
      }
      else {
        histEntry.setDataIdentifier(
            getDataElement(REVIEWED_FILE + reviewDetails.getRvwInputFileMap().get(parRevDetails.getRvwFileID())));
      }
    }

    return histEntry;
  }

  /**
   * @param histEntry
   * @param reviewDetails
   * @param paramName
   */
  private void fillDataFromRevDetails(final HistoryEntry histEntry, final ReviewDetails reviewDetails,
      final String paramName) {
    String formattedDate;
    try {
      formattedDate = ApicUtil.formatDate(DateFormat.DATE_FORMAT_16, reviewDetails.getRvwDate());
      histEntry.setDate(getDataElement(formattedDate));
    }
    catch (ParseException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp.getCause(), Activator.PLUGIN_ID);
    }

    histEntry.setPerformedBy(getDataElement(getCreatedUser(reviewDetails.getCreatedUser())));
    histEntry.setContext(getDataElement(this.cdrReportData.getWpName(paramName)));
    histEntry.setTargetVariant(getDataElement(reviewDetails.getVariantName()));
    histEntry.setTestObject(getDataElement(
        "Review Description: " + reviewDetails.getRvwDesc() + " (" + REVIEW_TYPE.OFFICIAL.getUiType() + ")"));
    histEntry.setProgramIdentifier(getDataElement("Review A2L: " + reviewDetails.getA2lFileName()));

  }

  /**
   * @param createdUser
   */
  private String getCreatedUser(final String userName) {
    if (CommonUtils.isEmptyString(userName)) {
      return "";
    }
    // Using the user NT ID, find the apic user object
    // Return the display name of the user
    User user = this.userMap.computeIfAbsent(userName.toUpperCase(Locale.getDefault()), k -> getUserByName(userName));
    return user == null ? userName : user.getDescription();

  }

  private User getUserByName(final String name) {
    User ret = null;
    try {
      ret = new UserServiceClient().getApicUserByUsername(name);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
    return ret;
  }

  /**
   * @param paramName
   * @param resultParamSet
   * @param histEntry
   */
  private void getScoreForParamResult(final String paramName, final SortedSet<CDRResultParameter> resultParamSet,
      final HistoryEntry histEntry) {

    String reviewRemark = "";
    for (CDRResultParameter cdrResultParameter : resultParamSet) {
      if (paramName.equals(cdrResultParameter.getName())) {
        DATA_REVIEW_SCORE reviewScoreEnum = this.resultData.getScore(cdrResultParameter);
        reviewRemark = setReviewComments(reviewScoreEnum, cdrResultParameter);


        break;
      }
    }


    histEntry.setRemark(getDataElement(reviewRemark));
  }

  /**
   * @param reviewScoreEnum
   * @param cdrResultParameter
   * @return
   */
  private String setReviewComments(final DATA_REVIEW_SCORE reviewScoreEnum,
      final CDRResultParameter cdrResultParameter) {
    String reviewRemark;
    if (this.dialog.isAllParamStatusFile() && (cdrResultParameter.getRvwComment() != null)) {
      reviewRemark = cdrResultParameter.getRvwComment();
    }
    else {
      reviewRemark = CommonUtils.concatenate(DataReviewScoreUtil.getInstance().getDescription(reviewScoreEnum), '\n');
      if (cdrResultParameter.getRvwComment() != null) {
        reviewRemark = CommonUtils.concatenate(reviewRemark, cdrResultParameter.getRvwComment());
      }
    }
    return reviewRemark;
  }

  /**
   * Method common to both review result and review report
   *
   * @param histEntry
   * @param parResult
   */
  private void fillDataFromRevRes(final HistoryEntry histEntry, final CDRReviewResult parResult) {
    try {
      String createdDateStr = parResult.getCreatedDate();
      Calendar cal = DateFormat.convertStringToCalendar(DateFormat.DATE_FORMAT_15, createdDateStr);
      String cdfxFormattedDate = DateFormat.formatDateToString(cal.getTime(), DateFormat.DATE_FORMAT_16);

      histEntry.setDate(getDataElement(cdfxFormattedDate));
    }
    catch (IcdmException e) {
      CDMLogger.getInstance().errorDialog(
          "Date Format can not be set to CDF standard. The creation date won't be exported to the CDFx file. You can nevertheless continue. Please inform the iCDM hotline abou this error.",
          e, "com.bosch.caltool.cdr.ui.actions");
    }

    histEntry.setPerformedBy(getDataElement(parResult.getCreatedUser()));
    histEntry.setContext(getDataElement(parResult.getGrpWorkPkg()));
    String varName = null;
    String a2lFileName = null;
    String reviewTypeStr = null;
    // resultdata is available then its from review result editor
    if (this.resultData != null) {
      varName = this.resultData.getResultBo().getVariant() == null ? ""
          : this.resultData.getResultBo().getVariant().getName();
      reviewTypeStr = this.resultData.getResultBo().getReviewTypeStr();
    }

    histEntry.setTargetVariant(getDataElement(varName));
    histEntry.setTestObject(
        getDataElement("Review Description: " + parResult.getDescription() + " (" + reviewTypeStr + ")"));
    histEntry.setProgramIdentifier(getDataElement("Review A2L: " + a2lFileName));
  }

  private DataElement getDataElement(final String value) {
    DataElement dataElement;

    String myValue;

    if (value == null) {
      myValue = "";
    }
    else {
      myValue = value;
    }

    dataElement = new DataElement();
    dataElement.setValue(myValue);

    return dataElement;
  }


  /**
   * set the properties
   */
  private void setProperties() {

    setText("Create CDFX file");

    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.EXPORT_DATA_16X16));
  }
}
