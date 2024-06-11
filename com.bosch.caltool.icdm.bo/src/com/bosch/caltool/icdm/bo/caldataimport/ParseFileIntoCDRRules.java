/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.caldataimport;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.calmodel.caldata.history.HistoryEntry;
import com.bosch.caltool.icdm.common.exception.CaldataFileException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler;
import com.bosch.caltool.icdm.common.util.CaldataFileParserHandler.CALDATA_FILE_TYPE;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.plausibelparser.cdrruleimport.PlausibelCDRRules;
import com.bosch.caltool.icdm.plausibelparser.cdrruleimport.PlausibelClasses;
import com.bosch.caltool.icdm.plausibelparser.cdrruleimport.PlausibelFile;
import com.bosch.ssd.icdm.model.CDRRule;
import com.bosch.ssd.icdm.model.SSDCase;
import com.bosch.ssd.icdm.model.SSDConfigEnums.ParameterClass;


/**
 * ICDM-1892 This class is to parse File into CDRRules
 *
 * @author mkl2cob
 */
public class ParseFileIntoCDRRules {

  /**
   * Map of parameter name and list of cdr rules
   */
  private final Map<String, CDRRule> cdrRuleMap = new HashMap<>();
  /**
   * logger instance
   */
  private final ILoggerAdapter parserLogger;
  /**
   * file name
   */
  private final Map<String, InputStream> fileNames;
  /**
   * SSDCase to set in the CDRRule object
   */
  private final SSDCase ssdCase;
  /**
   * ParameterClass to set in CDRRule object
   */
  private final ParameterClass paramClass;

  /**
   * Set for param name
   */
  private Set<String> paramNameSet = new HashSet<>();

  /**
   * ICDM-2352 list of parameters repeated in several files
   */
  private final Set<String> repeatedParamNamesSet = new HashSet<>();
  /**
   * flag to know the type of the file
   */
  private boolean isPlausibelFile;
  /**
   * Plausibel file instance
   */
  private PlausibelFile plausibelFile;
  /**
   * map of param name and type
   */
  private Map<String, String> paramNameType;
  /**
   * initialise the map to accomodate multiple parsing ICDM-2352
   */
  private final Map<String, CalData> calDataMapFromFile = new HashMap<>();
  /**
   * name of the function
   */
  private String funcName;
  /**
   * map of parameter classes
   */
  private Map<String, String> paramClasses;
  /**
   * map of parameter hint
   */
  private Map<String, String> paramHint;
  /**
   * server location
   */
  public static final String SERVER_PATH = Messages.getString("SERVICE_WORK_DIR") + "//PLAUSIBLE_FILE//";

  /**
   * @param parserLogger ILoggerAdapter
   * @param set name of the file
   * @param ssdCase SSDCase
   * @param paramClass ParameterClass
   */
  public ParseFileIntoCDRRules(final ILoggerAdapter parserLogger, final Map<String, InputStream> inputStremSet,
      final SSDCase ssdCase, final ParameterClass paramClass) {
    this.parserLogger = parserLogger;
    this.fileNames = inputStremSet;
    this.ssdCase = ssdCase;
    this.paramClass = paramClass;
  }

  /**
   * Reads file, collects basic inputs
   */
  public void readFile() {

    try {
      for (Entry<String, InputStream> fileEntry : this.fileNames.entrySet()) {
        // if file type is .xls or .xlsx (PLAUSIBLE FILE)
        String upperCaseFileName = fileEntry.getKey().toUpperCase(Locale.getDefault());
        this.isPlausibelFile = upperCaseFileName.endsWith(CommonUtilConstants.XLS_EXTN) ||
            upperCaseFileName.endsWith(CommonUtilConstants.XLSX_EXTN);
        if (this.isPlausibelFile) {
          String filePath = createOutputFolder(fileEntry.getValue());
          this.plausibelFile = new PlausibelFile(this.parserLogger, filePath);
          this.paramNameSet = this.plausibelFile.getParamNames();
          this.funcName = this.plausibelFile.getFunctionName();
        }
        else {
          // for other file types use the
          // Since HEX file is not supported, a2lfileinfo is not required and is set to null
          CaldataFileParserHandler parser = new CaldataFileParserHandler(this.parserLogger, null);

          // ICDM-2352
          // iterate and find out the repeated parameters

          CALDATA_FILE_TYPE fileType = CALDATA_FILE_TYPE.getTypeFromFileName(fileEntry.getKey());
          for (Entry<String, CalData> caldataEntry : parser.getCalDataObjects(fileType, fileEntry.getValue())
              .entrySet()) {
            // duplicate entry
            if (this.calDataMapFromFile.keySet().contains(caldataEntry.getKey())) {
              this.repeatedParamNamesSet.add(caldataEntry.getKey());
            }
            else {
              // non duplicate entry
              this.calDataMapFromFile.put(caldataEntry.getKey(), caldataEntry.getValue());
              this.paramNameSet.add(caldataEntry.getKey());
            }
          }

        }
      }
    }
    catch (IOException | IcdmException e) {
      this.parserLogger.error(e.getMessage(), e);
    }
  }

  /**
   * create Output folder
   */
  private String createOutputFolder(final InputStream inputStream) {

    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);

    // create the file only for the first time
    File file = new File(ParseFileIntoCDRRules.SERVER_PATH);
    if (!file.exists()) {
      file.mkdir();
    }
    file = new File(file.getAbsoluteFile() + "\\PlausibelFile_" + currentDate + ".xlsx");
    OutputStream outputStream = null;
    try {
      outputStream = new FileOutputStream(file);
      IOUtils.copy(inputStream, outputStream);
      outputStream.close();
    }
    catch (IOException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
    return file.getAbsolutePath();
  }

  /**
   * parameters available in the file
   */
  public Set<String> getParameters() {
    return new HashSet<>(this.paramNameSet);
  }

  /**
   * @return Map<? extends String, ? extends CalData>
   * @throws CaldataFileException exception
   */
  public Map<String, CDRRule> getCDRRuleObjects() {
    if (this.isPlausibelFile) {
      PlausibelCDRRules rules = new PlausibelCDRRules(this.plausibelFile, this.paramNameType);
      // get the rules

      for (CDRRule cdrRule : rules.getRules()) {
        this.cdrRuleMap.put(cdrRule.getParameterName(), cdrRule);
      }
      PlausibelClasses plausibelClass = new PlausibelClasses(this.plausibelFile);
      this.paramClasses = plausibelClass.getClasses();
      this.paramHint = plausibelClass.getHint();
      // close the file after use to free the excel file
      try {
        this.plausibelFile.close();
      }
      catch (IOException e) {
        CDMLogger.getInstance().error(e.getMessage(), e);
      }
    }
    else {
      convertCalDataToCDRRule(this.calDataMapFromFile, this.paramClass);
    }

    return this.cdrRuleMap;
  }


  /**
   * @param calDataMapFromFile
   * @param paramClass ParameterClass
   */
  private void convertCalDataToCDRRule(final Map<String, CalData> calDataMapFromFile, final ParameterClass paramClass) {

    for (Entry<String, CalData> calData : calDataMapFromFile.entrySet()) {
      CDRRule rule = new CDRRule();
      rule.setParameterName(calData.getKey());
      // will have the cal data from file
      CalData newCalData = calData.getValue();
      rule.setRefValCalData(newCalData);
      rule.setValueType(newCalData.getCalDataPhy().getType());
      // maturity level will be set while creating comparison objects
      if (((newCalData.getCalDataHistory() != null) &&
          (newCalData.getCalDataHistory().getHistoryEntryList() != null)) &&
          !newCalData.getCalDataHistory().getHistoryEntryList().isEmpty()) {
        HistoryEntry historyEntry = newCalData.getCalDataHistory().getHistoryEntryList()
            .get(newCalData.getCalDataHistory().getHistoryEntryList().size() - 1);
        if (historyEntry.getState() != null) {
          String maturityLvl = historyEntry.getState().getValue();
          rule.setMaturityLevel(maturityLvl);
          rule.setHint(CommonUtils.isNotNull(historyEntry.getRemark()) ? historyEntry.getRemark().getValue() : null);
        }
      }

      rule.setUnit(CommonUtils.checkNull(newCalData.getCalDataPhy().getUnit()));
      rule.setReviewMethod("");
      rule.setLabelFunction(newCalData.getFunctionName());
      rule.setSsdCase(this.ssdCase);
      rule.setParamClass(paramClass);

      this.cdrRuleMap.put(calData.getKey(), rule);
    }

  }

  /**
   * @param paramNameType map of parameter name and type
   */
  public void setParamDetails(final Map<String, String> paramNameType) {
    this.paramNameType = paramNameType;
  }

  /**
   * @return function name
   */
  public String getFuncName() {
    return this.funcName;
  }

  /**
   * @return map of param name and class
   */
  public Map<String, String> getParamClasses() {
    return this.paramClasses;
  }

  /**
   * @return the paramHint
   */
  public Map<String, String> getParamHint() {
    return this.paramHint;
  }

  /**
   * @return the repeatedParamNamesList
   */
  public Set<String> getRepeatedParamNamesSet() {
    return new HashSet<>(this.repeatedParamNamesSet);
  }

}
