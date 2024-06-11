/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.cdfwriter.CDFWriter;
import com.bosch.caltool.cdfwriter.exception.CDFWriterException;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CalDataUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDFWriterLogger;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.ReviewRule;
import com.bosch.ssd.icdm.model.CDRRule;

/**
 * @author ukt1cob
 */
public class RuleSetHandler extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData service data
   */
  public RuleSetHandler(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * @param cdrRules cdrRules Map
   * @return true if export is successful
   * @throws CDFWriterException exception
   * @throws IcdmException exception
   */
  public byte[] exportCaldataFromCdrRuleAsCdfxFile(final Map<String, List<CDRRule>> cdrRules)
      throws CDFWriterException, IcdmException {

    // get CalData obj for CDRRule
    // filtering rule that has no Ref Value or ref Value CalData
    Map<String, CalData> cdfCalDataObjects = cdrRules.entrySet().stream()
        .filter(cdrRulesEntrySet -> CommonUtils.isNotNull(getCalData(cdrRulesEntrySet.getValue().get(0))))
        .collect(Collectors.toMap(Entry<String, List<CDRRule>>::getKey,
            cdrRulesEntrySet -> getCalData(cdrRulesEntrySet.getValue().get(0))));

    if (CommonUtils.isNotEmpty(cdfCalDataObjects)) {
      return writeToCdfxFile(cdfCalDataObjects);
    }

    return new byte[] {};
  }

  /**
   * @param cdrRule cdrRule
   * @return CalData
   */
  public static CalData getCalData(final CDRRule cdrRule) {

    CalData caldata = null;

    CalData refValueCalData = cdrRule.getRefValueCalData();
    if (CommonUtils.isNotNull(refValueCalData)) {
      return refValueCalData;
    }
    else if ((CommonUtils.isNotNull(cdrRule.getRefValue())) && CommonUtils.isEqual(cdrRule.getValueType(), "VALUE")) {
      return CalDataUtil.dcmToCalData(CalDataUtil.createDCMStringForNumber(cdrRule.getParameterName(),
          cdrRule.getUnit(), cdrRule.getRefValue().toString()), cdrRule.getParameterName(), CDMLogger.getInstance());
    }

    return caldata;
  }


  /**
   * @param exportInputData
   * @param cdfCalDataObjects
   * @return
   * @throws CDFWriterException
   * @throws IcdmException
   */
  private byte[] writeToCdfxFile(final Map<String, CalData> cdfCalDataObjects)
      throws CDFWriterException, IcdmException {

    String cdfxExportfilePath = CommonUtils.getSystemUserTempDirPath() + File.separator + getExportFileName();
    CDFWriter cdfWriter = new CDFWriter(cdfCalDataObjects, cdfxExportfilePath, CDFWriterLogger.getInstance());
    // invoke this method to generate CDFx file
    cdfWriter.writeCalDataToXML();

    return getByteArrayForFile(cdfxExportfilePath);
  }


  /**
   * @return export File Path
   */
  private String getExportFileName() {
    StringBuilder fileName = new StringBuilder();
    fileName.append("Ruleset_Cdfx_Export").append("_")
        .append(DateFormat.formatDateToString(new Date(), DateFormat.DATE_FORMAT_19));

    return fileName.toString();
  }

  /**
   * @param cdfxFilePath
   * @return
   * @throws IcdmException
   */
  private byte[] getByteArrayForFile(final String cdfxFilePath) throws IcdmException {
    try {
      return FileUtils.readFileToByteArray(new File(cdfxFilePath));
    }
    catch (IOException e) {
      throw new IcdmException("Error While trying to convert file to byte stream : \n" + e.getMessage(), e);
    }
  }


  /**
   * Method to create
   *
   * @param linksMap rules to be created
   * @throws IcdmException exception from cmd
   */
  public void createUpdDelLinks(final Map<CDRRule, ReviewRule> linksMap) throws IcdmException {
    if (CommonUtils.isNullOrEmpty(linksMap)) {
      getLogger().info("No rule Links to create/update");
    }
    else {
      RuleLinkMasterCommand ruleLinkMasterCmd = new RuleLinkMasterCommand(getServiceData(), linksMap);
      getServiceData().getCommandExecutor().execute(ruleLinkMasterCmd);

      getLogger().info("Rule create/Update completed with Links.");
    }
  }

  public void getUpdInputRvwRule(final ReviewRule inputReviewRule, final boolean ruleUpdateRequired) {
    if (ruleUpdateRequired) {
      // Clearing the updated list inorder to avoid changes in existing version
      inputReviewRule.getRuleLinkWrapperData().getListOfRuleLinksToBeUpd().clear();
      // Populating the data in existing selection list from created list for addition in new version
      inputReviewRule.getRuleLinkWrapperData().getListOfRuleLinksToBeCreated().stream().forEach(x -> {
        inputReviewRule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule().add(x);
      });
      // Clearing the created list inorder to avoid changes in existing version
      inputReviewRule.getRuleLinkWrapperData().getListOfRuleLinksToBeCreated().clear();
      // Removing the data to be deleted in selection list as it is not needed in new version
      inputReviewRule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule()
          .removeAll(inputReviewRule.getRuleLinkWrapperData().getListOfRuleLinksToBeDel());
      // Clearing the deleted list inorder to avoid changes in existing version
      inputReviewRule.getRuleLinkWrapperData().getListOfRuleLinksToBeDel().clear();
    }
    else {
      inputReviewRule.getRuleLinkWrapperData().getListOfExistingLinksForSelRule().clear();
    }
  }
}
