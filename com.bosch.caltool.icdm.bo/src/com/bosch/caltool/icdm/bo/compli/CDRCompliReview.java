/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.compli;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.ParameterLoader;
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.cdr.FeatureAttributeAdapterNew;
import com.bosch.caltool.icdm.bo.cdr.review.IReviewProcessResolver;
import com.bosch.caltool.icdm.bo.cdr.review.ReviewedInfo;
import com.bosch.caltool.icdm.bo.general.MessageLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;

/**
 * @author bru2cob
 */
public class CDRCompliReview implements IReviewProcessResolver {

  SortedSet<Parameter> paramSet;
  ReviewedInfo reviewOutput;
  /*
   * For Compli check
   */
  private CompliReviewData compliRvwData;

  ReviewInput reviewInputData;
  ServiceData serviceData;
  A2LFileInfo a2lFileContents;
  private ComplianceParamReview compliParamRvw;
  FeatureAttributeAdapterNew faAdapter;
  private String dataFileName;


  /**
   * @param paramSet paramter set
   * @param faAdapter FeatureAttributeAdapterNew object
   * @param reviewOutput reviewed info
   * @param reviewInputData review input model
   * @param serviceData service data
   * @param a2lFileContents a2l file info
   */
  public CDRCompliReview(final SortedSet<Parameter> paramSet, final FeatureAttributeAdapterNew faAdapter,
      final ReviewedInfo reviewOutput, final ReviewInput reviewInputData, final ServiceData serviceData,
      final A2LFileInfo a2lFileContents) {
    this.paramSet = paramSet;
    this.reviewOutput = reviewOutput;
    this.reviewInputData = reviewInputData;
    this.serviceData = serviceData;
    this.a2lFileContents = a2lFileContents;
    this.faAdapter = faAdapter;
  }

  /**
   * {@inheritDoc}
   *
   * @throws DataException exception during review process
   */
  @Override
  public void performReview() throws IcdmException {
    // create SSD file for compliance parameters
    Set<String> compliParamSet = identifyCompliParams();
    Set<String> qssdParamSet = identifyQssdParams();

    List<Boolean> isNONSDOMBCRelease = new ArrayList();


    Set<String> compliQssdParamSet = new HashSet<>();

    if (CommonUtils.isNotEmpty(compliParamSet)) {
      compliQssdParamSet.addAll(compliParamSet);
    }

    if (CommonUtils.isNotEmpty(qssdParamSet)) {
      compliQssdParamSet.addAll(qssdParamSet);
    }

    this.compliRvwData = getCompliRvwData();
    this.reviewOutput.setCompliData(this.compliRvwData);
    // Continue review even when COMPLI check fails
    if (!compliQssdParamSet.isEmpty()) {
      String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
      File file = new File(CompliDataReview.SERVER_PATH);
      if (!file.exists()) {
        file.mkdir();
      }
      file = new File(file.getAbsoluteFile() + "\\CompliReview_" + currentDate);
      file.mkdir();

      // Check for QSSD only labels - w/o compli
      boolean isQSSDOnlyRelease = compliParamSet.isEmpty() && !qssdParamSet.isEmpty();
      this.compliParamRvw = new ComplianceParamReview(this.compliRvwData, this.faAdapter, compliQssdParamSet,
          this.serviceData, this.reviewOutput.getPidcDetails(), file.getAbsolutePath(), isQSSDOnlyRelease);
      PidcA2l pidcA2l =
          new PidcA2lLoader(this.serviceData).getDataObjectByID(this.reviewInputData.getPidcData().getPidcA2lId());
      SdomPverLoader pvrLdr = new SdomPverLoader(this.serviceData);
      SortedSet<String> pverVariantNamesSet = pvrLdr.getPverVariantNames(pidcA2l.getSdomPverName());
      if (pverVariantNamesSet.isEmpty()) {
        isNONSDOMBCRelease.add(true);
        AttributeLoader loader = new AttributeLoader(this.serviceData);
        Map<Long, Attribute> attrRetMap = loader.getAllAttributes(false);
        Optional<Attribute> attributeInfo = attrRetMap.values().stream()
            .filter(attribute -> attribute.getName().equals(CDRConstants.PVER_NAME_IN_SDOM)).findAny();
        if (attributeInfo.isPresent()) {
          AttributeValueLoader attributeValueLoader = new AttributeValueLoader(this.serviceData);
          Set<Long> attrIdSet = new HashSet();
          attrIdSet.add(attributeInfo.get().getId());
          // fetch attribute value
          Map<Long, Map<Long, AttributeValue>> attrValretMap = attributeValueLoader.getValuesByAttribute(attrIdSet);
          Optional<AttributeValue> attributeValueInfo = attrValretMap.get(attributeInfo.get().getId()).values().stream()
              .filter(attributeValue -> attributeValue.getName().equals(pidcA2l.getSdomPverName())).findAny();
          if (attributeValueInfo.isPresent() && !attributeValueInfo.get().getClearingStatus().equals("Y")) {
            String hint = new MessageLoader(this.serviceData).getMessage("COMPLI_REVIEW", "PVER_NOT_CLEARED",
                pidcA2l.getSdomPverName());
            CDMLogger.getInstance().error(hint);
          }
          else {
            this.compliParamRvw.invokeSSDReleaseForCompli(isNONSDOMBCRelease);
          }
        }
      }
      else {
        isNONSDOMBCRelease.add(false);
        this.compliParamRvw.invokeSSDReleaseForCompli(isNONSDOMBCRelease);
      }
    }
  }

  /**
   * @return
   */
  private Set<String> identifyQssdParams() {
    // iterate through the param set
    Set<String> qssdParamSet = new HashSet<>();
    ParameterLoader paramLoader = new ParameterLoader(this.serviceData);
    for (Parameter cdrFuncParameter : this.paramSet) {
      if (paramLoader.isQssdParameter(cdrFuncParameter.getName())) {
        // if the parameter is complaince parameter
        qssdParamSet.add(cdrFuncParameter.getName());
      }
    }
    if (CommonUtils.isNotEmpty(qssdParamSet)) {
      this.reviewOutput.setQssdParamsPresent(true);
    }
    return qssdParamSet;
  }

  /**
   * Identify compli params.
   *
   * @param paramSet the param set
   * @return
   */
  private Set<String> identifyCompliParams() {
    // iterate through the param set
    Set<String> compliParamSet = new HashSet<>();
    ParameterLoader paramLoader = new ParameterLoader(this.serviceData);
    for (Parameter cdrFuncParameter : this.paramSet) {
      if (paramLoader.isCompliParameter(cdrFuncParameter.getName())) {
        // if the parameter is complaince parameter
        compliParamSet.add(cdrFuncParameter.getName());
      }
    }
    if (CommonUtils.isNotEmpty(compliParamSet)) {
      this.reviewOutput.setCompliParamsPresent(true);
    }
    return compliParamSet;
  }

  /**
   * @return the Compli reviewData
   * @throws IcdmException data retrieval error
   */
  public CompliReviewData getCompliRvwData() throws IcdmException {

    PidcA2l pidcA2l =
        new PidcA2lLoader(this.serviceData).getDataObjectByID(this.reviewInputData.getPidcData().getPidcA2lId());
    PidcVersion selPidc = new PidcVersionLoader(this.serviceData).getDataObjectByID(pidcA2l.getPidcVersId());
    PidcVariant selPidcVar = null;
    if (null != this.reviewInputData.getPidcData().getSelPIDCVariantId()) {
      selPidcVar = new PidcVariantLoader(this.serviceData)
          .getDataObjectByID(this.reviewInputData.getPidcData().getSelPIDCVariantId());
    }

    A2LFileInfoLoader fileInfoLoader = new A2LFileInfoLoader(this.serviceData);
    Map<String, A2LBaseComponents> a2lBaseComponentsMap = fileInfoLoader.getA2lBaseComponents(pidcA2l.getA2lFileId());
    SortedSet<A2LBaseComponents> a2lBcSet = new TreeSet<>(a2lBaseComponentsMap.values());
    this.compliRvwData = new CompliReviewData(selPidc, selPidcVar, this.a2lFileContents.getAllModulesLabels(),
        this.reviewOutput.getCalDataMap(), this.reviewOutput.getParserWarningsMap(), a2lBcSet);
    return this.compliRvwData;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ReviewedInfo getReviewOutput() {
    // NA
    return null;
  }

  /**
   * ICDM-2440 Review for Compliance Parameters
   *
   * @throws IcdmException exception during checkssd invocation
   */
  public void invokeCheckSSDForCompliParam() throws IcdmException {

    // Invoke CheckSSD here
    if (this.compliRvwData.getCompliSSDFilePath() != null) {
      this.reviewInputData.getFileData().getSelFilesPath()
          .forEach(filePath -> this.dataFileName = new File(filePath).getName());

      CompliReviewSummary compliRvwSummary = this.compliParamRvw.invokeCheckSSD(this.reviewOutput.getA2lFileContents(),
          this.compliRvwData.getCompliSSDFilePath(), this.dataFileName);

      // Result - to be set into SetCommonFields()
      if (compliRvwSummary != null) {
        if ((compliRvwSummary.getCheckSSDOutData() != null) &&
            !compliRvwSummary.getCheckSSDOutData().isReviewHasExp()) {
          this.compliRvwData
              .setCheckSSDCompliParamMap(compliRvwSummary.getCheckSSDOutData().getCheckSSDCompliParamMap());
          this.compliRvwData
              .setCompliCheckSSDOutputFiles(compliRvwSummary.getCheckSSDOutData().getGeneratedCheckSSDFiles());
          this.compliRvwData.setErrorInSSDfile(compliRvwSummary.getCheckSSDOutData().getErrorinSSDFile());
          this.compliRvwData.setSkippedParamsList(compliRvwSummary.getCheckSSDOutData().getLabelsInSsdMap().keySet());
        }
        // Errors
        if ((compliRvwSummary.getCheckSSDOutData() != null) && compliRvwSummary.getCheckSSDOutData().isReviewHasExp() &&
            (compliRvwSummary.getCheckSSDOutData().getReviewExceptionObj() != null)) {
          throw new IcdmException(compliRvwSummary.getCheckSSDOutData().getReviewExceptionObj().getMessage(),
              compliRvwSummary.getCheckSSDOutData().getReviewExceptionObj());

        }
      }
    }
  }
}
