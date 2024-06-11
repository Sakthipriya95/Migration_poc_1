/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.db;


import java.util.Optional;
import java.util.UUID;

import org.apache.axis2.databinding.types.HexBinary;
import org.apache.logging.log4j.ThreadContext;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.*;
import com.bosch.caltool.apic.ws.applicationlog.ServerEventLogger;
import com.bosch.caltool.apic.ws.applicationlog.request.AbstractRequest;
import com.bosch.caltool.apic.ws.applicationlog.request.factory.RequestFactory;

/**
 * APICSkeleton java skeleton for the axisService
 */
public class APICSkeletonImpl implements APICSkeletonInterface {


  /**
   * String Constant for "requestId"
   */
  private static final String REQUEST_ID = "requestId";

  /**
   * String Constant for "method"
   */
  private static final String CONSTANT_METHOD = "method";

  private static ILoggerAdapter logger;

  private static ApicWebServiceDBImpl wsDbImpl;

  static {
    logger = WSObjectStore.getLogger();
    wsDbImpl = WSObjectStore.getApicWebServiceDBImpl();
  }

  /**
   * Logout from WebService. Invalidate the session ID. Currently, no sessions are handled. Thus, method will return
   * TRUE always.
   *
   * @param logout logout info
   * @return logoutResponse
   */
  @Override
  public com.bosch.caltool.apic.ws.LogoutResponse logout(final com.bosch.caltool.apic.ws.Logout logout) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "login");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      LogoutResponse response = getApicWebServiceDBImpl().logout(logout);

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * Auto generated method signature
   *
   * @param getAllAttributes input
   * @return getAllAttributesResponse
   */
  @Override
  public com.bosch.caltool.apic.ws.GetAllAttributesResponse getAllAttributes(
      final com.bosch.caltool.apic.ws.GetAllAttributes getAllAttributes)
      throws GetAllAttributesFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getAllAttributes");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetAllAttributesResponse response = new GetAllAttributesResponse();
      response.setGetAllAttributesResponse(getApicWebServiceDBImpl().getAllAttr(getAllAttributes));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetAllAttributesResponse().getAttributesList().getAttributes())
              .orElse(new Attribute[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetAllAttributesFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * Check login credentials and get a session ID Login credentials are currently not checked. Only a dummy session ID
   * will be returned.
   *
   * @param login the login parameters
   * @return loginResponse
   */
  @Override
  public com.bosch.caltool.apic.ws.LoginResponse login(final com.bosch.caltool.apic.ws.Login login)
      throws LoginFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "login");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      LoginResponse response = getApicWebServiceDBImpl().login(login);

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * Get all Values for a given attribute
   *
   * @param getAttributeValues parameters
   * @return the response
   */
  @Override
  public GetAttributeValuesResponse getAttributeValues(final GetAttributeValues getAttributeValues)
      throws GetAttributeValuesFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getAttributeValues");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getAttributeValues);

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetAttributeValuesResponse response = new GetAttributeValuesResponse();
      response.setGetAttributeValuesResponse(getApicWebServiceDBImpl().getAttrValues(getAttributeValues));

      ServerEventLogger.INSTANCE.logEndOfReq(Optional
          .ofNullable(response.getGetAttributeValuesResponse().getValueLists()).orElse(new ValueList[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetAttributeValuesFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * Get a Project ID Card with all values and variants
   *
   * @param getProjectIdCard parameters
   * @return the response
   */
  @Override
  public GetProjectIdCardResponse getProjectIdCard(final GetProjectIdCard getProjectIdCard)
      throws GetProjectIdCardFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getProjectIdCard");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getProjectIdCard);

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetProjectIdCardResponse response = new GetProjectIdCardResponse();
      response.setGetProjectIdCardResponse(getApicWebServiceDBImpl().getProjIdCard(getProjectIdCard));

      ServerEventLogger.INSTANCE.logEndOfReq(response.getGetProjectIdCardResponse().isProjectIdCardSpecified() ? 1 : 0);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetProjectIdCardFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * Get all Project ID Cards (without values and variants)
   *
   * @param getAllProjectIdCards parameters
   * @return the response
   */
  @Override
  public GetAllProjectIdCardsResponse getAllProjectIdCards(final GetAllProjectIdCards getAllProjectIdCards)
      throws GetAllProjectIdCardsFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getAllProjectIdCards");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getAllProjectIdCards);

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetAllProjectIdCardsResponse repsonse = new GetAllProjectIdCardsResponse();
      repsonse.setGetAllProjectIdCardsResponse(getApicWebServiceDBImpl().getAllProjIdCard(getAllProjectIdCards));


      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(repsonse.getGetAllProjectIdCardsResponse().getProjectIdCards())
              .orElse(new ProjectIdCardInfoType[0]).length);
      return repsonse;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetAllProjectIdCardsFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetParameterStatisticsResponse getParameterStatistics(final GetParameterStatistics getParameterStatistics)
      throws GetParameterStatisticsFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getParameterStatistics");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetParameterStatisticsResponse response = new GetParameterStatisticsResponse();
      response
          .setGetParameterStatisticsResponse(getApicWebServiceDBImpl().getParameterStatistics(getParameterStatistics));

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetParameterStatisticsFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetParameterStatisticsExtResponse getParameterStatisticsExt(
      final GetParameterStatisticsExt getParameterStatisticsExt)
      throws GetParameterStatisticsExtFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getParameterStatisticsExt");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetParameterStatisticsExtResponse response = new GetParameterStatisticsExtResponse();
      response.setGetParameterStatisticsExtResponse(
          getApicWebServiceDBImpl().getParameterStatisticsExt(getParameterStatisticsExt));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetParameterStatisticsExtResponse().getParameterStatistics())
              .orElse(new HexBinary[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetParameterStatisticsExtFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetAttrGroupsResponse getAttrGroups(final GetAttrGroups getAttrGroups) throws GetAttrGroupsFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getAttrGroups");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetAttrGroupsResponse response = new GetAttrGroupsResponse();
      response.setGetAttrGroupsResponse(getApicWebServiceDBImpl().getAttrGroups(getAttrGroups));

      ServerEventLogger.INSTANCE.logEndOfReq(Optional.ofNullable(response.getGetAttrGroupsResponse().getSuperGroups())
          .orElse(new SuperGroupType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetAttrGroupsFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetUseCasesResponse getUseCases(final GetUseCases getUseCases) throws GetUseCasesFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getUseCases");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetUseCasesResponse response = new GetUseCasesResponse();
      response.setUseCases(getApicWebServiceDBImpl().getUseCases(getUseCases));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getUseCases()).orElse(new UseCaseType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetUseCasesFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcDiffsResponse getPidcDiffs(final GetPidcDiffs getPidcDiffsParameter) throws GetPidcDiffsFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcDiffs");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getPidcDiffsParameter.getGetPidcDiffs());

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcDiffsResponse response = new GetPidcDiffsResponse();
      response.setGetPidcDiffsResponse(getApicWebServiceDBImpl().getPidcDiffs(getPidcDiffsParameter.getGetPidcDiffs()));


      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetPidcDiffsResponse().getChangedAttributes())
              .orElse(new ProjectIdCardChangedAttributeType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetPidcDiffsFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  @Override
  public com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse getStatusForAsyncExecution(
      final com.bosch.caltool.apic.ws.GetStatusForAsyncExecution getStatusForAsyncExecution) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getStatusForAsyncExecution");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetStatusForAsyncExecutionResponse response = new GetStatusForAsyncExecutionResponse();
      response.setOut(ApicWebServiceDBImpl.getStatusAsync(getStatusForAsyncExecution.getIn()));

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  @Override
  public com.bosch.caltool.apic.ws.LoadA2LFileDataResponse loadA2LFileData(
      final com.bosch.caltool.apic.ws.LoadA2LFileData loadA2LFileData) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "loadA2LFileData");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      LoadA2LFileDataResponse response = getApicWebServiceDBImpl().loadA2LFileData(loadA2LFileData);

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CancelSessionResponse cancelSession(final CancelSession cancelSession) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "cancelSession");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      CancelSessionResponse response = getApicWebServiceDBImpl().cancelSession(cancelSession);

      ServerEventLogger.INSTANCE.logEndOfReq(response.getCancelSessionResponse().getIsCancelled() ? 1 : 0);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws GetParameterReviewResultFaultException service error
   */
  @Override
  public GetParameterReviewResultResponse getParameterReviewResult(
      final GetParameterReviewResult getParameterReviewResult)
      throws GetParameterReviewResultFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getParameterReviewResult");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetParameterReviewResultResponse response =
          getApicWebServiceDBImpl().getParameterReviewResult(getParameterReviewResult);

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetParameterReviewResultResponse().getParameterReviewResults())
              .orElse(new GetParameterReviewResultResponseType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetParameterReviewResultFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated service is no longer available
   */
  @Override
  @Deprecated
  public WebServiceVersionResponse getWebServiceVersion() {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getWebServiceVersion");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      WebServiceVersionResponse response = getApicWebServiceDBImpl().getWebServiceVersion();

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetParameterStatisticsFileResponse getParameterStatisticsFile(
      final GetParameterStatisticsFile getParameterStatisticsFile)
      throws GetParameterStatisticsFileFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getParameterStatisticsFile");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetParameterStatisticsFileResponse response =
          getApicWebServiceDBImpl().getParameterStatisticsFile(getParameterStatisticsFile);

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetParameterStatisticsFileFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcScoutResponse getPidcScoutResult(final PidcSearchCondition pidcSearchCondition)
      throws GetPidcScoutResultFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcScoutResult");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      ServerEventLogger.INSTANCE.logServerEvent("getPidcScoutResult");

      PidcScoutResponse response =
          getApicWebServiceDBImpl().getPidcScoutResult(pidcSearchCondition.getPidcSearchCondition());

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getPidcScoutResponse().getPidcIds()).orElse(new long[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcStatisticResponse getPidcStatistic(final PidcStatistic pidcStatistic) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcStatistic");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      ServerEventLogger.INSTANCE.logServerEvent("getPidcStatistic");

      PidcStatisticResponse response =
          getApicWebServiceDBImpl().getPidcStatisticResult(pidcStatistic.getPidcStatistic());

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PidcAccessRightResponse getPidcAccessRight(final PidcAccessRight pidcAccessRight) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcAccessRight");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(pidcAccessRight.getPidcAccessRight());

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      PidcAccessRightResponse pidcAccessRightResponse = getApicWebServiceDBImpl().getPidcAccessRight(pidcAccessRight);

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(pidcAccessRightResponse.getPidcAccessRightResponse().getAccessRights())
              .orElse(new PidcAccessRightResponseType[0]).length);
      return pidcAccessRightResponse;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated service is no longer available
   */
  @Override
  @Deprecated
  public ParameterStatisticsXmlResponse getParameterStatisticsXML(final ParameterStatistisXml parameterStatistisXml) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getParameterStatisticsXML");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      ParameterStatisticsXmlResponse response = getApicWebServiceDBImpl().getParameterStatisticsXML();

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated service is no longer available
   */
  @Override
  @Deprecated
  public AllPidcDiffResponse getAllPidcDiff(final AllPidcDiff allPidcDiff) throws GetAllPidcDiffFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getAllPidcDiff");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(allPidcDiff.getAllPidcDiff());

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      AllPidcDiffResponse response = getApicWebServiceDBImpl().getAllPidcDiff(allPidcDiff.getAllPidcDiff());

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getAllPidcDiffResponse().getChangedAttributes())
              .orElse(new ProjectIdCardChangedAttributeType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated service is no longer available
   */
  @Override
  @Deprecated
  public AttrDiffResponse getPidcAttrDiffReport(final AttrDiff attrDiff) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcAttrDiffReportForVersion");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(attrDiff.getAttrDiff());

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      AttrDiffResponse response = getApicWebServiceDBImpl().getPidcAttrDiffReport(attrDiff.getAttrDiff());

      ServerEventLogger.INSTANCE.logEndOfReq(
          Optional.ofNullable(response.getAttrDiffResponse().getDifferences()).orElse(new AttrDiffType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public AttrDiffVersResponse getPidcAttrDiffReportForVersion(final AttrDiffVers attrDiff) {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcAttrDiffReportForVersion");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      AttrDiffVersResponse response =
          getApicWebServiceDBImpl().getPidcAttrDiffReportForVersion(attrDiff.getAttrDiffVers());

      ServerEventLogger.INSTANCE.logEndOfReq(
          Optional.ofNullable(response.getAttrDiffVersResponse().getDifferences()).orElse(new AttrDiffType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @deprecated service is no longer available
   */
  @Override
  @Deprecated
  public GetPidcDiffForVersionResponse getPidcDiffForVersion(final GetPidcDiffForVersion getPidcDiffsParameter)
      throws GetPidcDiffForVersionFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcDiffForVersion");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcDiffForVersionResponse response = new GetPidcDiffForVersionResponse();
      response.setGetPidcDiffForVersionResponse(
          getApicWebServiceDBImpl().getPidcDiffForVersion(getPidcDiffsParameter.getGetPidcDiffForVersion()));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetPidcDiffForVersionResponse().getChangedAttributes())
              .orElse(new ProjectIdCardChangedAttributeType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * @param pidcVersionSearchCond pidcVersionSearchCond
   * @return PidcScoutVersResponse
   */
  @Override
  public PidcScoutVersResponse getPidcScoutResultForVersion(final PidcVersSearchCondition pidcVersionSearchCond) {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcScoutResultForVersion");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      PidcScoutVersResponse response =
          getApicWebServiceDBImpl().getPidcScoutResultForVersion(pidcVersionSearchCond.getPidcVersSearchCondition());

      ServerEventLogger.INSTANCE.logEndOfReq(
          Optional.ofNullable(response.getPidcScoutVersResponse().getPidcIds()).orElse(new long[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * @param getAllProjectIdVersions getAllProjectIdVersions
   * @return GetAllProjectIdCardVersionsResponse
   */
  @Override
  public GetAllProjectIdCardVersionsResponse getAllProjectIdCardVersions(
      final GetAllProjectIdCardVersions getAllProjectIdVersions) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getAllProjectIdCardVersions");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getAllProjectIdVersions);

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetAllProjectIdCardVersionsResponse repsonse = new GetAllProjectIdCardVersionsResponse();

      repsonse.setGetAllProjectIdCardVersionsResponse(
          getApicWebServiceDBImpl().getAllProjectIdCardVersions(getAllProjectIdVersions));


      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(repsonse.getGetAllProjectIdCardVersionsResponse().getProjectIdCards())
              .orElse(new ProjectIdCardAllVersInfoType[0]).length);
      return repsonse;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * @param getPidcActiveVersionId input
   * @return PidcActiveVersionResponse
   */
  @Override
  public PidcActiveVersionResponse getPidcActiveVersionId(final PidcActiveVersion getPidcActiveVersionId) {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcActiveVersionId");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      PidcActiveVersionResponse response = new PidcActiveVersionResponse();
      response.setPidcActiveVersionResponse(getApicWebServiceDBImpl().getPidcActiveVersionId(getPidcActiveVersionId));

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * @param getProjectIdCardForVersion wrapped Param
   * @return GetProjectIdCardForVersionResponse
   */
  @Override
  public GetProjectIdCardForVersionResponse getProjectIdCardForVersion(
      final GetProjectIdCardForVersion getProjectIdCardForVersion) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getProjectIdCardForVersion");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getProjectIdCardForVersion);

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetProjectIdCardForVersionResponse response = new GetProjectIdCardForVersionResponse();
      response.setGetProjectIdCardForVersionResponse(
          getApicWebServiceDBImpl().getProjIdCardForVersion(getProjectIdCardForVersion));

      ServerEventLogger.INSTANCE
          .logEndOfReq(response.getGetProjectIdCardForVersionResponse().isProjectIdCardSpecified() ? 1 : 0);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * @param allPidcDiffVers allPidcDiffVers
   * @return AllPidcDiffVersResponse
   */
  @Override
  public AllPidcDiffVersResponse getAllPidcDiffForVersion(final AllPidcDiffVers allPidcDiffVers)
      throws GetAllPidcDiffForVersionFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getAllPidcDiff");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      AllPidcDiffVersResponse response =
          getApicWebServiceDBImpl().getAllPidcDiffForVersion(allPidcDiffVers.getAllPidcDiffVers());

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getAllPidcDiffVersResponse().getChangedAttributes())
              .orElse(new ProjectIdCardChangedAttributeType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetAllPidcDiffForVersionFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @throws GetVcdmLabelStatisticsFaultException GetVcdmLabelStatisticsFaultException
   */
  // Icdm-1485 - new method for Getting Vcdm Label Statistics.
  @Override
  public GetVcdmLabelStatisticsResponse getVcdmLabelStatistics(final GetVcdmLabelStatReq getVcdmLabelStatReq)
      throws GetVcdmLabelStatisticsFaultException {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getVcdmLabelStatistics");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetVcdmLabelStatisticsResponse response =
          getApicWebServiceDBImpl().getVcdmLabelStatistics(getVcdmLabelStatReq.getGetVcdmLabelStatReq());

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getLabelStats()).orElse(new VcdmLabelStats[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetVcdmLabelStatisticsFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }


  /**
   * @return the logger
   */
  static ILoggerAdapter getLogger() {
    return logger;
  }

  /**
   * @return the apicWebServiceDBImpl
   */
  private static ApicWebServiceDBImpl getApicWebServiceDBImpl() {
    return wsDbImpl;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcVersionStatisticsResponse getPidcVersionStatistics(
      final GetPidcVersionStatisticsRequest getPidcVersionStatisticsRequest)
      throws GetPidcVersionStatisticsFaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcVersionStatistics");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcVersionStatisticsResponse response = getApicWebServiceDBImpl()
          .getPIdcVersionStatistics(getPidcVersionStatisticsRequest.getGetPidcVersionStatisticsRequest());

      ServerEventLogger.INSTANCE.logEndOfReq(1);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetPidcVersionStatisticsFaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcFavouritesResponse getPidcFavourites(final GetPidcFavouritesReq getPidcFavouritesReq) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcFavourites");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcFavouritesResponse response =
          getApicWebServiceDBImpl().getPidcFavourites(getPidcFavouritesReq.getGetPidcFavouritesReq());

      ServerEventLogger.INSTANCE.logEndOfReq(
          Optional.ofNullable(response.getPidcFavouriteResponse()).orElse(new PidcFavouriteResponseType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcWebFlowDataResponse getPidcWebFlowData(final GetPidcWebFlowData getPidcWebFlowData)
      throws GetPidcWebFlowDataFault1Exception {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcWebFlowData");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcWebFlowDataResponse response = new GetPidcWebFlowDataResponse();
      response.setGetPidcWebFlowDataResponse(getApicWebServiceDBImpl().getPidcWebFlowData(getPidcWebFlowData));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetPidcWebFlowDataResponse().getWebFlowAttributes())
              .orElse(new WebflowAttributesType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetPidcWebFlowDataFault1Exception(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcWebFlowElementResponse getPidcWebFlowElement(final GetPidcWebFlowElementReq getPidcWebFlowElementReq)
      throws GetPidcWebFlowElementFault1 {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcWebFlowElement [Element Id :" +
          getPidcWebFlowElementReq.getGetPidcWebFlowElementReq().getElementID() + "}");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcWebFlowElementResponse response = new GetPidcWebFlowElementResponse();
      response
          .setGetPidcWebFlowElementResponse(getApicWebServiceDBImpl().getPidcWebFlowElement(getPidcWebFlowElementReq));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetPidcWebFlowElementResponse().getWebFlowAttributes())
              .orElse(new WebflowAttributesType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetPidcWebFlowElementFault1(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * This method returns list of vcdm label statistics for work package
   */
  // ICDM-2469
  @Override
  public GetVcdmLabelStatisticsForWPResponse1 getVcdmLabelStatisticsForWP(
      final GetVcdmLabelStatisticsForWP getVcdmLabelStatisticsForWP) {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getVcdmLabelStatisticsForWP");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetVcdmLabelStatisticsForWPResponse1 response =
          getApicWebServiceDBImpl().getVcdmLabelStatisticsForWP(getVcdmLabelStatisticsForWP);

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetVcdmLabelStatisticsForWPResponse1().getLabelStatsWp())
              .orElse(new VcdmLabelStatsWPType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetUseCaseWithSectionTreeResponse getUseCaseWithSectionTree(
      final GetUseCaseWithSectionTree getUseCaseWithSectionTree) {

    try {
      ThreadContext.put(CONSTANT_METHOD, "getUseCases");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetUseCaseWithSectionTreeResponse response = new GetUseCaseWithSectionTreeResponse();
      response.setUseCases(getApicWebServiceDBImpl().getUseCaseWithSectionTree(getUseCaseWithSectionTree));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getUseCases()).orElse(new UseCaseSectionType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new IllegalStateException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetProjectIdCardV2Response getProjectIdCardV2(final GetProjectIdCardV2 getProjectIdCardV2)
      throws GetProjectIdCardV2FaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getProjectIdCardV2");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest(getProjectIdCardV2);

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetProjectIdCardV2Response response = new GetProjectIdCardV2Response();
      response.setGetProjectIdCardV2Response(getApicWebServiceDBImpl().getProjectIdCardV2(getProjectIdCardV2));

      ServerEventLogger.INSTANCE
          .logEndOfReq(response.getGetProjectIdCardV2Response().isProjectIdCardSpecified() ? 1 : 0);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetProjectIdCardV2FaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public InvalidateWebFlowElementResponse invalidateWebFlowElement(
      final InvalidateWebFlowElementReq invalidateWebFlowElementReq) {
    try {
      ThreadContext.put(CONSTANT_METHOD, "invalidateWebFlowElement");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      InvalidateWebFlowElementResponse response = new InvalidateWebFlowElementResponse();
      response.setInvalidateWebFlowElementResponse(
          getApicWebServiceDBImpl().invalidateWebFlowElement(invalidateWebFlowElementReq));

      return response;
    }
    finally {
      ThreadContext.clearAll();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public GetPidcWebFlowDataV2Response getPidcWebFlowDataV2(final GetPidcWebFlowDataV2 getPidcWebFlowDataV2)
      throws GetPidcWebFlowDataV2FaultException {
    try {
      ThreadContext.put(CONSTANT_METHOD, "getPidcWebFlowDataV2");
      ThreadContext.put(REQUEST_ID, UUID.randomUUID().toString());
      AbstractRequest request = RequestFactory.getRequest();

      ServerEventLogger.INSTANCE.logStartOfReq(request);

      GetPidcWebFlowDataV2Response response = new GetPidcWebFlowDataV2Response();
      response.setGetPidcWebFlowDataV2Response(getApicWebServiceDBImpl().getPidcWebFlowDataV2(getPidcWebFlowDataV2));

      ServerEventLogger.INSTANCE
          .logEndOfReq(Optional.ofNullable(response.getGetPidcWebFlowDataV2Response().getWebFlowAttributes())
              .orElse(new WebflowAttributesType[0]).length);
      return response;
    }
    catch (Exception exp) {
      ServerEventLogger.INSTANCE.logError(exp);
      throw new GetPidcWebFlowDataV2FaultException(exp.getMessage(), exp);
    }
    finally {
      ThreadContext.clearAll();
    }
  }


}
