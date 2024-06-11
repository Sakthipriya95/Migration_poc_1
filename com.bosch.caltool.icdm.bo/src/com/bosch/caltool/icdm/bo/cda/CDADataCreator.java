/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cda;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;

import com.bosch.calcomp.adapter.logger.Log4JLoggerAdapterImpl;
import com.bosch.calcomp.caldataanalyzer.CalDataAnalyzer;
import com.bosch.calcomp.caldataanalyzer.exception.CommandLineException;
import com.bosch.calcomp.caldataanalyzer.filter.IFilter;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.ICommonDatasetAttributeFilter;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.A2LFunctionFilter;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.A2LSysConstFilter;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.DatasetAttributeFilterImpl;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.Flow5DatasetFilterImpl;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.LabelExistDatasetFilterImpl;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.SeriesDatasetFilterImpl;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.VillaDatasetFilterImpl;
import com.bosch.calcomp.caldataanalyzer.filter.datasetfilter.impl.WebFlowDatasetFilterImpl;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cda.CaldataAnalyzerFilterModel;
import com.bosch.caltool.icdm.model.cda.CustomerFilter;
import com.bosch.caltool.icdm.model.cda.FunctionFilter;
import com.bosch.caltool.icdm.model.cda.ParameterFilterLabel;
import com.bosch.caltool.icdm.model.cda.PlatformFilter;
import com.bosch.caltool.icdm.model.cda.SystemConstantFilter;
import com.bosch.caltool.security.Decryptor;

/**
 * @author say8cob
 */
public class CDADataCreator {

  // database username for CalDataAnalyzer
  private static final String DB_PASSWORD =
      Decryptor.getInstance().decrypt(Messages.getString("CommonUtils.ANALYZER_DB_USER_PASS"), CDMLogger.getInstance());

  // database password for CalDataAnalyzer
  private static final String DB_USERNAME = Messages.getString("CommonUtils.ANALYZER_DB_USER");

  // database connection for CalDataAnalyzer
  private static final String DB_CONNECTION = Messages.getString("CommonUtils.ANALYZER_DB_CONNECTION");

  /**
   *
   */
  private static final String OUTPUT_FILE_NAME = "Analyser";

  /**
   * @param caldataAnalyzerFilterModel filter model
   * @return analyzer
   * @throws CommandLineException when file name corrupted
   */
  public CalDataAnalyzer initializeCaldataAnalyzer(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel)
      throws CommandLineException {

    Log4JLoggerAdapterImpl cdaLogger = new Log4JLoggerAdapterImpl(LogManager.getLogger("CDA"));

    CalDataAnalyzer calDataAnalyzer = new CalDataAnalyzer();
    setDBProperties(calDataAnalyzer);
    calDataAnalyzer.setLogger(cdaLogger);
    List<IFilter> filterList = setFilterData(caldataAnalyzerFilterModel, calDataAnalyzer);

    calDataAnalyzer.setFilterList(filterList);

    calDataAnalyzer.setOutputReportFileName(OUTPUT_FILE_NAME);
    return calDataAnalyzer;

  }

  /**
   * @param caldataAnalyzerFilterModel
   * @param calDataAnalyzer
   * @return
   */
  private List<IFilter> setFilterData(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final CalDataAnalyzer calDataAnalyzer) {
    List<IFilter> filterList = new ArrayList<>();
    filterList.add(new SeriesDatasetFilterImpl(true));
    filterList.add(new WebFlowDatasetFilterImpl());
    filterList.add(new VillaDatasetFilterImpl());
    filterList.add(new Flow5DatasetFilterImpl());

    // Label Filter
    addLabelFilter(caldataAnalyzerFilterModel, calDataAnalyzer, filterList);

    // Customer Filter
    addCustomerFilter(caldataAnalyzerFilterModel, filterList);

    // ECU Platform Filter
    addECUPlatformFilter(caldataAnalyzerFilterModel, filterList);

    // Function Filter
    addFunctionFilter(caldataAnalyzerFilterModel, filterList);

    // SysConst Filter
    addSystemConstantFilter(caldataAnalyzerFilterModel, filterList);

    return filterList;
  }

  /**
   * @param caldataAnalyzerFilterModel
   * @param filterList
   */
  private void addSystemConstantFilter(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final List<IFilter> filterList) {
    List<SystemConstantFilter> sysConstsFilterList = caldataAnalyzerFilterModel.getSysconFilterList();
    if (!sysConstsFilterList.isEmpty()) {
      for (SystemConstantFilter sysConstFilter : sysConstsFilterList) {
        if (sysConstFilter.getSystemConstantValue().isEmpty()) {
          filterList.add(new A2LSysConstFilter(sysConstFilter.getSystemConstantName()));
        }
        else if (("*").equals(sysConstFilter.getSystemConstantValue())) {
          filterList.add(new A2LSysConstFilter(sysConstFilter.getSystemConstantName(), sysConstFilter.isInverseFlag()));
        }
        else {
          filterList.add(new A2LSysConstFilter(sysConstFilter.getSystemConstantName(),
              Double.parseDouble(sysConstFilter.getSystemConstantValue()), sysConstFilter.isInverseFlag()));
        }
      }
    }
  }

  /**
   * @param caldataAnalyzerFilterModel
   * @param filterList
   */
  private void addFunctionFilter(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final List<IFilter> filterList) {
    List<FunctionFilter> functionFilterList = caldataAnalyzerFilterModel.getFunctionFilterList();
    if (!functionFilterList.isEmpty()) {
      for (FunctionFilter functionFilter : functionFilterList) {
        filterList.add(new A2LFunctionFilter(functionFilter.getFunctionName(), functionFilter.getFunctionVersion()));
      }
    }
  }

  /**
   * @param caldataAnalyzerFilterModel
   * @param filterList
   */
  private void addECUPlatformFilter(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final List<IFilter> filterList) {
    List<String> platformList = new ArrayList<>();
    if (caldataAnalyzerFilterModel.getPlatformFilterModel() != null) {
      List<PlatformFilter> ecuPlatformFilterList =
          caldataAnalyzerFilterModel.getPlatformFilterModel().getPlatformFilterList();

      if (!ecuPlatformFilterList.isEmpty()) {
        for (PlatformFilter platformFilter : ecuPlatformFilterList) {
          platformList.add(platformFilter.getEcuPlatformName());
        }
        filterList.add(new DatasetAttributeFilterImpl(ICommonDatasetAttributeFilter.ECU_PLATFORM,
            platformList.toArray(new String[platformList.size()]),
            caldataAnalyzerFilterModel.getPlatformFilterModel().isInverseFlag()));
      }
    }
  }

  /**
   * @param caldataAnalyzerFilterModel
   * @param filterList
   */
  private void addCustomerFilter(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final List<IFilter> filterList) {
    List<String> customerList = new ArrayList<>();
    if (caldataAnalyzerFilterModel.getCustomerFilterModel() != null) {
      List<CustomerFilter> customerFilterList =
          caldataAnalyzerFilterModel.getCustomerFilterModel().getCustomerFilterList();

      if (!customerFilterList.isEmpty()) {
        for (CustomerFilter customerFilter : customerFilterList) {
          customerList.add(customerFilter.getCustomerName());
        }
        filterList.add(new DatasetAttributeFilterImpl(ICommonDatasetAttributeFilter.CUSTOMER_NAME,
            customerList.toArray(new String[customerList.size()]),
            caldataAnalyzerFilterModel.getCustomerFilterModel().isInverseFlag()));
      }
    }
  }

  /**
   * @param caldataAnalyzerFilterModel
   * @param calDataAnalyzer
   * @param filterList
   */
  private void addLabelFilter(final CaldataAnalyzerFilterModel caldataAnalyzerFilterModel,
      final CalDataAnalyzer calDataAnalyzer, final List<IFilter> filterList) {
    // Label Filter
    List<ParameterFilterLabel> paramFilterList = caldataAnalyzerFilterModel.getParamFilterList();
    List<String> labelList = new ArrayList<>();
    List<String> labelMustExistList = new ArrayList<>();
    if (!paramFilterList.isEmpty()) {
      for (ParameterFilterLabel parameterFilterLabel : paramFilterList) {
        labelList.add(parameterFilterLabel.getLabel());
        if (parameterFilterLabel.isMustExist()) {
          labelMustExistList.add(parameterFilterLabel.getLabel());
          filterList.add(new LabelExistDatasetFilterImpl(parameterFilterLabel.getLabel(), false));
        }
      }
      calDataAnalyzer.setLabelList(labelList);
    }
  }

  /**
   * @param calDataAnalyzer
   */
  private void setDBProperties(final CalDataAnalyzer calDataAnalyzer) {
    // Messages.getString("CommonUtils.DB_URL")
    calDataAnalyzer.setDataBase(DB_CONNECTION);
    calDataAnalyzer.setDbUsername(DB_USERNAME);
    calDataAnalyzer.setDbpassword(DB_PASSWORD);
  }


}
