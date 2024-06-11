
/**
 * APICMessageReceiverInOut.java This file was auto-generated from WSDL by the Apache Axis2 version: 1.6.2 Built on :
 * Apr 17, 2012 (05:33:49 IST)
 */
package com.bosch.caltool.apic.ws;

import com.bosch.caltool.apic.ws.db.APICSkeletonImpl;

/**
 * APICMessageReceiverInOut message receiver
 */

public class APICMessageReceiverInOut extends org.apache.axis2.receivers.AbstractInOutMessageReceiver {


  @Override
  public void invokeBusinessLogic(final org.apache.axis2.context.MessageContext msgContext,
      final org.apache.axis2.context.MessageContext newMsgContext)
      throws org.apache.axis2.AxisFault {

    try {

      // get the implementation class for the Web Service
      Object obj = getTheImplementationObject(msgContext);

      APICSkeletonImpl skel = (APICSkeletonImpl) obj;
      // Out Envelop
      org.apache.axiom.soap.SOAPEnvelope envelope = null;
      // Find the axisOperation that has been set by the Dispatch phase.
      org.apache.axis2.description.AxisOperation op = msgContext.getOperationContext().getAxisOperation();
      if (op == null) {
        throw new org.apache.axis2.AxisFault(
            "Operation is not located, if this is doclit style the SOAP-ACTION should specified via the SOAP Action to use the RawXMLProvider");
      }

      java.lang.String methodName;
      if ((op.getName() != null) && ((methodName =
          org.apache.axis2.util.JavaUtils.xmlNameToJavaIdentifier(op.getName().getLocalPart())) != null)) {


        if ("getPidcWebFlowDataV2".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response getPidcWebFlowDataV2Response242 = null;
          com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2 wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcWebFlowDataV2Response242 =


              skel.getPidcWebFlowDataV2(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcWebFlowDataV2Response242, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcWebFlowDataV2"));
        }
        else

        if ("getPidcFavourites".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcFavouritesResponse getPidcFavouritesResponse244 = null;
          com.bosch.caltool.apic.ws.GetPidcFavouritesReq wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcFavouritesReq) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcFavouritesReq.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcFavouritesResponse244 =


              skel.getPidcFavourites(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcFavouritesResponse244, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcFavourites"));
        }
        else

        if ("getPidcScoutResult".equals(methodName)) {

          com.bosch.caltool.apic.ws.PidcScoutResponse pidcScoutResponse246 = null;
          com.bosch.caltool.apic.ws.PidcSearchCondition wrappedParam =
              (com.bosch.caltool.apic.ws.PidcSearchCondition) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.PidcSearchCondition.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          pidcScoutResponse246 =


              skel.getPidcScoutResult(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), pidcScoutResponse246, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcScoutResult"));
        }
        else

        if ("getProjectIdCardV2".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetProjectIdCardV2Response getProjectIdCardV2Response248 = null;
          com.bosch.caltool.apic.ws.GetProjectIdCardV2 wrappedParam =
              (com.bosch.caltool.apic.ws.GetProjectIdCardV2) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetProjectIdCardV2.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getProjectIdCardV2Response248 =


              skel.getProjectIdCardV2(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getProjectIdCardV2Response248, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getProjectIdCardV2"));
        }
        else

        if ("getVcdmLabelStatistics".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse getVcdmLabelStatisticsResponse250 = null;
          com.bosch.caltool.apic.ws.GetVcdmLabelStatReq wrappedParam =
              (com.bosch.caltool.apic.ws.GetVcdmLabelStatReq) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetVcdmLabelStatReq.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getVcdmLabelStatisticsResponse250 =


              skel.getVcdmLabelStatistics(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getVcdmLabelStatisticsResponse250, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getVcdmLabelStatistics"));
        }
        else

        if ("getParameterStatisticsFile".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse getParameterStatisticsFileResponse252 = null;
          com.bosch.caltool.apic.ws.GetParameterStatisticsFile wrappedParam =
              (com.bosch.caltool.apic.ws.GetParameterStatisticsFile) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetParameterStatisticsFile.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getParameterStatisticsFileResponse252 =


              skel.getParameterStatisticsFile(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getParameterStatisticsFileResponse252, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getParameterStatisticsFile"));
        }
        else

        if ("getPidcDiffs".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcDiffsResponse getPidcDiffsResponse254 = null;
          com.bosch.caltool.apic.ws.GetPidcDiffs wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcDiffs) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcDiffs.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcDiffsResponse254 =


              skel.getPidcDiffs(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcDiffsResponse254, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcDiffs"));
        }
        else

        if ("getPidcActiveVersionId".equals(methodName)) {

          com.bosch.caltool.apic.ws.PidcActiveVersionResponse pidcActiveVersionResponse256 = null;
          com.bosch.caltool.apic.ws.PidcActiveVersion wrappedParam =
              (com.bosch.caltool.apic.ws.PidcActiveVersion) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.PidcActiveVersion.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          pidcActiveVersionResponse256 =


              skel.getPidcActiveVersionId(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), pidcActiveVersionResponse256, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcActiveVersionId"));
        }
        else

        if ("getWebServiceVersion".equals(methodName)) {

          com.bosch.caltool.apic.ws.WebServiceVersionResponse webServiceVersionResponse258 = null;
          webServiceVersionResponse258 =

              skel.getWebServiceVersion();

          envelope = toEnvelope(getSOAPFactory(msgContext), webServiceVersionResponse258, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getWebServiceVersion"));
        }
        else

        if ("getPidcVersionStatistics".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse getPidcVersionStatisticsResponse260 = null;
          com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcVersionStatisticsResponse260 =


              skel.getPidcVersionStatistics(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcVersionStatisticsResponse260, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcVersionStatistics"));
        }
        else

        if ("cancelSession".equals(methodName)) {

          com.bosch.caltool.apic.ws.CancelSessionResponse cancelSessionResponse262 = null;
          com.bosch.caltool.apic.ws.CancelSession wrappedParam =
              (com.bosch.caltool.apic.ws.CancelSession) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.CancelSession.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          cancelSessionResponse262 =


              skel.cancelSession(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), cancelSessionResponse262, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "cancelSession"));
        }
        else

        if ("getPidcAttrDiffReportForVersion".equals(methodName)) {

          com.bosch.caltool.apic.ws.AttrDiffVersResponse attrDiffVersResponse264 = null;
          com.bosch.caltool.apic.ws.AttrDiffVers wrappedParam =
              (com.bosch.caltool.apic.ws.AttrDiffVers) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.AttrDiffVers.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          attrDiffVersResponse264 =


              skel.getPidcAttrDiffReportForVersion(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), attrDiffVersResponse264, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcAttrDiffReportForVersion"));
        }
        else

        if ("getAllProjectIdCardVersions".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse getAllProjectIdCardVersionsResponse266 = null;
          com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions wrappedParam =
              (com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getAllProjectIdCardVersionsResponse266 =


              skel.getAllProjectIdCardVersions(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getAllProjectIdCardVersionsResponse266, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAllProjectIdCardVersions"));
        }
        else

        if ("getAllAttributes".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetAllAttributesResponse getAllAttributesResponse268 = null;
          com.bosch.caltool.apic.ws.GetAllAttributes wrappedParam =
              (com.bosch.caltool.apic.ws.GetAllAttributes) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetAllAttributes.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getAllAttributesResponse268 =


              skel.getAllAttributes(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getAllAttributesResponse268, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAllAttributes"));
        }
        else

        if ("getAttrGroups".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetAttrGroupsResponse getAttrGroupsResponse270 = null;
          com.bosch.caltool.apic.ws.GetAttrGroups wrappedParam =
              (com.bosch.caltool.apic.ws.GetAttrGroups) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetAttrGroups.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getAttrGroupsResponse270 =


              skel.getAttrGroups(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getAttrGroupsResponse270, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAttrGroups"));
        }
        else

        if ("getPidcAccessRight".equals(methodName)) {

          com.bosch.caltool.apic.ws.PidcAccessRightResponse pidcAccessRightResponse272 = null;
          com.bosch.caltool.apic.ws.PidcAccessRight wrappedParam =
              (com.bosch.caltool.apic.ws.PidcAccessRight) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.PidcAccessRight.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          pidcAccessRightResponse272 =


              skel.getPidcAccessRight(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), pidcAccessRightResponse272, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcAccessRight"));
        }
        else

        if ("getUseCases".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetUseCasesResponse getUseCasesResponse274 = null;
          com.bosch.caltool.apic.ws.GetUseCases wrappedParam =
              (com.bosch.caltool.apic.ws.GetUseCases) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetUseCases.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getUseCasesResponse274 =


              skel.getUseCases(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getUseCasesResponse274, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getUseCases"));
        }
        else

        if ("getAllProjectIdCards".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse getAllProjectIdCardsResponse276 = null;
          com.bosch.caltool.apic.ws.GetAllProjectIdCards wrappedParam =
              (com.bosch.caltool.apic.ws.GetAllProjectIdCards) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetAllProjectIdCards.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getAllProjectIdCardsResponse276 =


              skel.getAllProjectIdCards(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getAllProjectIdCardsResponse276, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAllProjectIdCards"));
        }
        else

        if ("getPidcAttrDiffReport".equals(methodName)) {

          com.bosch.caltool.apic.ws.AttrDiffResponse attrDiffResponse278 = null;
          com.bosch.caltool.apic.ws.AttrDiff wrappedParam =
              (com.bosch.caltool.apic.ws.AttrDiff) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.AttrDiff.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          attrDiffResponse278 =


              skel.getPidcAttrDiffReport(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), attrDiffResponse278, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcAttrDiffReport"));
        }
        else

        if ("logout".equals(methodName)) {

          com.bosch.caltool.apic.ws.LogoutResponse logoutResponse280 = null;
          com.bosch.caltool.apic.ws.Logout wrappedParam =
              (com.bosch.caltool.apic.ws.Logout) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.Logout.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          logoutResponse280 =


              skel.logout(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), logoutResponse280, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "logout"));
        }
        else

        if ("invalidateWebFlowElement".equals(methodName)) {

          com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse invalidateWebFlowElementResponse282 = null;
          com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq wrappedParam =
              (com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          invalidateWebFlowElementResponse282 =


              skel.invalidateWebFlowElement(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), invalidateWebFlowElementResponse282, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "invalidateWebFlowElement"));
        }
        else

        if ("getUseCaseWithSectionTree".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse getUseCaseWithSectionTreeResponse284 = null;
          com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree wrappedParam =
              (com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getUseCaseWithSectionTreeResponse284 =


              skel.getUseCaseWithSectionTree(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getUseCaseWithSectionTreeResponse284, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getUseCaseWithSectionTree"));
        }
        else

        if ("login".equals(methodName)) {

          com.bosch.caltool.apic.ws.LoginResponse loginResponse286 = null;
          com.bosch.caltool.apic.ws.Login wrappedParam =
              (com.bosch.caltool.apic.ws.Login) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.Login.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          loginResponse286 =


              skel.login(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), loginResponse286, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "login"));
        }
        else

        if ("getProjectIdCardForVersion".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse getProjectIdCardForVersionResponse288 = null;
          com.bosch.caltool.apic.ws.GetProjectIdCardForVersion wrappedParam =
              (com.bosch.caltool.apic.ws.GetProjectIdCardForVersion) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetProjectIdCardForVersion.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getProjectIdCardForVersionResponse288 =


              skel.getProjectIdCardForVersion(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getProjectIdCardForVersionResponse288, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getProjectIdCardForVersion"));
        }
        else

        if ("getStatusForAsyncExecution".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse getStatusForAsyncExecutionResponse290 = null;
          com.bosch.caltool.apic.ws.GetStatusForAsyncExecution wrappedParam =
              (com.bosch.caltool.apic.ws.GetStatusForAsyncExecution) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetStatusForAsyncExecution.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getStatusForAsyncExecutionResponse290 =


              skel.getStatusForAsyncExecution(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getStatusForAsyncExecutionResponse290, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getStatusForAsyncExecution"));
        }
        else

        if ("loadA2LFileData".equals(methodName)) {

          com.bosch.caltool.apic.ws.LoadA2LFileDataResponse loadA2LFileDataResponse292 = null;
          com.bosch.caltool.apic.ws.LoadA2LFileData wrappedParam =
              (com.bosch.caltool.apic.ws.LoadA2LFileData) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.LoadA2LFileData.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          loadA2LFileDataResponse292 =


              skel.loadA2LFileData(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), loadA2LFileDataResponse292, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "loadA2LFileData"));
        }
        else

        if ("getVcdmLabelStatisticsForWP".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1 getVcdmLabelStatisticsForWPResponse1294 = null;
          com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP wrappedParam =
              (com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getVcdmLabelStatisticsForWPResponse1294 =


              skel.getVcdmLabelStatisticsForWP(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getVcdmLabelStatisticsForWPResponse1294, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getVcdmLabelStatisticsForWP"));
        }
        else

        if ("getParameterStatistics".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetParameterStatisticsResponse getParameterStatisticsResponse296 = null;
          com.bosch.caltool.apic.ws.GetParameterStatistics wrappedParam =
              (com.bosch.caltool.apic.ws.GetParameterStatistics) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetParameterStatistics.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getParameterStatisticsResponse296 =


              skel.getParameterStatistics(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getParameterStatisticsResponse296, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getParameterStatistics"));
        }
        else

        if ("getParameterReviewResult".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetParameterReviewResultResponse getParameterReviewResultResponse298 = null;
          com.bosch.caltool.apic.ws.GetParameterReviewResult wrappedParam =
              (com.bosch.caltool.apic.ws.GetParameterReviewResult) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetParameterReviewResult.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getParameterReviewResultResponse298 =


              skel.getParameterReviewResult(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getParameterReviewResultResponse298, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getParameterReviewResult"));
        }
        else

        if ("getParameterStatisticsXML".equals(methodName)) {

          com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse parameterStatisticsXmlResponse300 = null;
          com.bosch.caltool.apic.ws.ParameterStatistisXml wrappedParam =
              (com.bosch.caltool.apic.ws.ParameterStatistisXml) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.ParameterStatistisXml.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          parameterStatisticsXmlResponse300 =


              skel.getParameterStatisticsXML(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), parameterStatisticsXmlResponse300, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getParameterStatisticsXML"));
        }
        else

        if ("getAttributeValues".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetAttributeValuesResponse getAttributeValuesResponse302 = null;
          com.bosch.caltool.apic.ws.GetAttributeValues wrappedParam =
              (com.bosch.caltool.apic.ws.GetAttributeValues) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetAttributeValues.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getAttributeValuesResponse302 =


              skel.getAttributeValues(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getAttributeValuesResponse302, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAttributeValues"));
        }
        else

        if ("getPidcDiffForVersion".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse getPidcDiffForVersionResponse304 = null;
          com.bosch.caltool.apic.ws.GetPidcDiffForVersion wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcDiffForVersion) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcDiffForVersion.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcDiffForVersionResponse304 =


              skel.getPidcDiffForVersion(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcDiffForVersionResponse304, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcDiffForVersion"));
        }
        else

        if ("getPidcStatistic".equals(methodName)) {

          com.bosch.caltool.apic.ws.PidcStatisticResponse pidcStatisticResponse306 = null;
          com.bosch.caltool.apic.ws.PidcStatistic wrappedParam =
              (com.bosch.caltool.apic.ws.PidcStatistic) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.PidcStatistic.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          pidcStatisticResponse306 =


              skel.getPidcStatistic(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), pidcStatisticResponse306, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcStatistic"));
        }
        else

        if ("getPidcWebFlowElement".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse getPidcWebFlowElementResponse308 = null;
          com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcWebFlowElementResponse308 =


              skel.getPidcWebFlowElement(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcWebFlowElementResponse308, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcWebFlowElement"));
        }
        else

        if ("getProjectIdCard".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetProjectIdCardResponse getProjectIdCardResponse310 = null;
          com.bosch.caltool.apic.ws.GetProjectIdCard wrappedParam =
              (com.bosch.caltool.apic.ws.GetProjectIdCard) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetProjectIdCard.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getProjectIdCardResponse310 =


              skel.getProjectIdCard(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getProjectIdCardResponse310, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getProjectIdCard"));
        }
        else

        if ("getAllPidcDiffForVersion".equals(methodName)) {

          com.bosch.caltool.apic.ws.AllPidcDiffVersResponse allPidcDiffVersResponse312 = null;
          com.bosch.caltool.apic.ws.AllPidcDiffVers wrappedParam =
              (com.bosch.caltool.apic.ws.AllPidcDiffVers) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.AllPidcDiffVers.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          allPidcDiffVersResponse312 =


              skel.getAllPidcDiffForVersion(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), allPidcDiffVersResponse312, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAllPidcDiffForVersion"));
        }
        else

        if ("getParameterStatisticsExt".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse getParameterStatisticsExtResponse314 = null;
          com.bosch.caltool.apic.ws.GetParameterStatisticsExt wrappedParam =
              (com.bosch.caltool.apic.ws.GetParameterStatisticsExt) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetParameterStatisticsExt.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          getParameterStatisticsExtResponse314 =


              skel.getParameterStatisticsExt(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getParameterStatisticsExtResponse314, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getParameterStatisticsExt"));
        }
        else

        if ("getAllPidcDiff".equals(methodName)) {

          com.bosch.caltool.apic.ws.AllPidcDiffResponse allPidcDiffResponse316 = null;
          com.bosch.caltool.apic.ws.AllPidcDiff wrappedParam =
              (com.bosch.caltool.apic.ws.AllPidcDiff) fromOM(msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.AllPidcDiff.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          allPidcDiffResponse316 =


              skel.getAllPidcDiff(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), allPidcDiffResponse316, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getAllPidcDiff"));
        }
        else

        if ("getPidcScoutResultForVersion".equals(methodName)) {

          com.bosch.caltool.apic.ws.PidcScoutVersResponse pidcScoutVersResponse318 = null;
          com.bosch.caltool.apic.ws.PidcVersSearchCondition wrappedParam =
              (com.bosch.caltool.apic.ws.PidcVersSearchCondition) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.PidcVersSearchCondition.class,
                  getEnvelopeNamespaces(msgContext.getEnvelope()));

          pidcScoutVersResponse318 =


              skel.getPidcScoutResultForVersion(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), pidcScoutVersResponse318, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcScoutResultForVersion"));
        }
        else

        if ("getPidcWebFlowData".equals(methodName)) {

          com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse getPidcWebFlowDataResponse320 = null;
          com.bosch.caltool.apic.ws.GetPidcWebFlowData wrappedParam =
              (com.bosch.caltool.apic.ws.GetPidcWebFlowData) fromOM(
                  msgContext.getEnvelope().getBody().getFirstElement(),
                  com.bosch.caltool.apic.ws.GetPidcWebFlowData.class, getEnvelopeNamespaces(msgContext.getEnvelope()));

          getPidcWebFlowDataResponse320 =


              skel.getPidcWebFlowData(wrappedParam);

          envelope = toEnvelope(getSOAPFactory(msgContext), getPidcWebFlowDataResponse320, false,
              new javax.xml.namespace.QName("http://ws.apic.caltool.bosch.com", "getPidcWebFlowData"));

        }
        else {
          throw new java.lang.RuntimeException("method not found");
        }


        newMsgContext.setEnvelope(envelope);
      }
    }
    catch (GetParameterStatisticsExtFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getParameterStatisticsExtFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcWebFlowDataFault1Exception e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getPidcWebFlowDataFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcWebFlowElementFault1 e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getPidcWebFlowElementFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcDiffsFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getPidcDiffsFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetUseCasesFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getUseCasesFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcScoutResultFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "PidcScoutFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetVcdmLabelStatisticsFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getVcdmLabelStatisticsFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (LoginFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "LoginFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetAllPidcDiffForVersionFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getAllPidcDiffForVersionFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcWebFlowDataV2FaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getPidcWebFlowDataV2Fault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcVersionStatisticsFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getPidcVersionStatisticsFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetPidcDiffForVersionFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getPidcDiffForVersionFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetAttrGroupsFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getAttrGroupsFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetParameterStatisticsFileFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getParameterStatisticsFileFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetAllProjectIdCardsFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "APICWsRequestFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetParameterStatisticsFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getParameterStatisticsFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetParameterReviewResultFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getParameterReviewResultFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetProjectIdCardFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "APICWsRequestFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetAllAttributesFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "APICWsRequestFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetAttributeValuesFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "APICWsRequestFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetProjectIdCardV2FaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "getProjectIdCardV2Fault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }
    catch (GetAllPidcDiffFaultException e) {

      msgContext.setProperty(org.apache.axis2.Constants.FAULT_NAME, "AllPidcDiffFault");
      org.apache.axis2.AxisFault f = createAxisFault(e);
      if (e.getFaultMessage() != null) {
        f.setDetail(toOM(e.getFaultMessage(), false));
      }
      throw f;
    }

    catch (java.lang.Exception e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  //
  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2 param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcFavouritesReq param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcFavouritesReq.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcFavouritesResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcFavouritesResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcSearchCondition param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcSearchCondition.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcScoutResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcScoutResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcScoutFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcScoutFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCardV2 param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardV2.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCardV2Response param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardV2Response.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCardV2Fault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardV2Fault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetVcdmLabelStatReq param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatReq.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsFile param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsFile.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsFileFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsFileFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcDiffs param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffs.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcDiffsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcDiffsFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffsFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcActiveVersion param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcActiveVersion.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcActiveVersionResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcActiveVersionResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.WebServiceVersionResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.WebServiceVersionResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcVersionStatisticsFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcVersionStatisticsFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.CancelSession param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.CancelSession.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.CancelSessionResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.CancelSessionResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AttrDiffVers param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AttrDiffVers.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AttrDiffVersResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AttrDiffVersResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllAttributes param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllAttributes.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllAttributesResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllAttributesResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.APICWsRequestFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.APICWsRequestFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAttrGroups param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAttrGroups.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAttrGroupsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAttrGroupsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAttrGroupsFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAttrGroupsFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcAccessRight param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcAccessRight.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcAccessRightResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcAccessRightResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetUseCases param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetUseCases.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetUseCasesResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetUseCasesResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetUseCasesFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetUseCasesFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllProjectIdCards param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllProjectIdCards.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AttrDiff param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AttrDiff.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AttrDiffResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AttrDiffResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.Logout param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.Logout.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.LogoutResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.LogoutResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.Login param, final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.Login.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.LoginResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.LoginResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.LoginFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.LoginFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCardForVersion param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardForVersion.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetStatusForAsyncExecution param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetStatusForAsyncExecution.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.LoadA2LFileData param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.LoadA2LFileData.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.LoadA2LFileDataResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.LoadA2LFileDataResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.LoadA2LFileDataFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.LoadA2LFileDataFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1 param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatistics param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatistics.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterReviewResult param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterReviewResult.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterReviewResultResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterReviewResultResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterReviewResultFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterReviewResultFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.ParameterStatistisXml param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.ParameterStatistisXml.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAttributeValues param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAttributeValues.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAttributeValuesResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAttributeValuesResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcDiffForVersion param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffForVersion.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcDiffForVersionFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffForVersionFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcStatistic param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcStatistic.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcStatisticResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcStatisticResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowElementFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowElementFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCard param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCard.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetProjectIdCardResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AllPidcDiffVers param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiffVers.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AllPidcDiffVersResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiffVersResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetAllPidcDiffForVersionFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetAllPidcDiffForVersionFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsExt param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsExt.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetParameterStatisticsExtFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsExtFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AllPidcDiff param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiff.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AllPidcDiffResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiffResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.AllPidcDiffFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiffFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcVersSearchCondition param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcVersSearchCondition.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.PidcScoutVersResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.PidcScoutVersResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowData param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowData.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.om.OMElement toOM(final com.bosch.caltool.apic.ws.GetPidcWebFlowDataFault param,
      final boolean optimizeContent)
      throws org.apache.axis2.AxisFault {


    try {
      return param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataFault.MY_QNAME,
          org.apache.axiom.om.OMAbstractFactory.getOMFactory());
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }


  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response wrapgetPidcWebFlowDataV2() {
    com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcFavouritesResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcFavouritesResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcFavouritesResponse wrapgetPidcFavourites() {
    com.bosch.caltool.apic.ws.GetPidcFavouritesResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcFavouritesResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.PidcScoutResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.PidcScoutResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.PidcScoutResponse wrapgetPidcScoutResult() {
    com.bosch.caltool.apic.ws.PidcScoutResponse wrappedElement = new com.bosch.caltool.apic.ws.PidcScoutResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetProjectIdCardV2Response param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardV2Response.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetProjectIdCardV2Response wrapgetProjectIdCardV2() {
    com.bosch.caltool.apic.ws.GetProjectIdCardV2Response wrappedElement =
        new com.bosch.caltool.apic.ws.GetProjectIdCardV2Response();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse wrapgetVcdmLabelStatistics() {
    com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse wrapgetParameterStatisticsFile() {
    com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcDiffsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcDiffsResponse wrapgetPidcDiffs() {
    com.bosch.caltool.apic.ws.GetPidcDiffsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcDiffsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.PidcActiveVersionResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.PidcActiveVersionResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.PidcActiveVersionResponse wrapgetPidcActiveVersionId() {
    com.bosch.caltool.apic.ws.PidcActiveVersionResponse wrappedElement =
        new com.bosch.caltool.apic.ws.PidcActiveVersionResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.WebServiceVersionResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.WebServiceVersionResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.WebServiceVersionResponse wrapgetWebServiceVersion() {
    com.bosch.caltool.apic.ws.WebServiceVersionResponse wrappedElement =
        new com.bosch.caltool.apic.ws.WebServiceVersionResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse wrapgetPidcVersionStatistics() {
    com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.CancelSessionResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.CancelSessionResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.CancelSessionResponse wrapcancelSession() {
    com.bosch.caltool.apic.ws.CancelSessionResponse wrappedElement =
        new com.bosch.caltool.apic.ws.CancelSessionResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.AttrDiffVersResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.AttrDiffVersResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.AttrDiffVersResponse wrapgetPidcAttrDiffReportForVersion() {
    com.bosch.caltool.apic.ws.AttrDiffVersResponse wrappedElement =
        new com.bosch.caltool.apic.ws.AttrDiffVersResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody().addChild(
          param.getOMElement(com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse wrapgetAllProjectIdCardVersions() {
    com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetAllAttributesResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetAllAttributesResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetAllAttributesResponse wrapgetAllAttributes() {
    com.bosch.caltool.apic.ws.GetAllAttributesResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetAllAttributesResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetAttrGroupsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetAttrGroupsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetAttrGroupsResponse wrapgetAttrGroups() {
    com.bosch.caltool.apic.ws.GetAttrGroupsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetAttrGroupsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.PidcAccessRightResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.PidcAccessRightResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.PidcAccessRightResponse wrapgetPidcAccessRight() {
    com.bosch.caltool.apic.ws.PidcAccessRightResponse wrappedElement =
        new com.bosch.caltool.apic.ws.PidcAccessRightResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetUseCasesResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetUseCasesResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetUseCasesResponse wrapgetUseCases() {
    com.bosch.caltool.apic.ws.GetUseCasesResponse wrappedElement = new com.bosch.caltool.apic.ws.GetUseCasesResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse wrapgetAllProjectIdCards() {
    com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.AttrDiffResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.AttrDiffResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.AttrDiffResponse wrapgetPidcAttrDiffReport() {
    com.bosch.caltool.apic.ws.AttrDiffResponse wrappedElement = new com.bosch.caltool.apic.ws.AttrDiffResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.LogoutResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody().addChild(param.getOMElement(com.bosch.caltool.apic.ws.LogoutResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.LogoutResponse wrapLogout() {
    com.bosch.caltool.apic.ws.LogoutResponse wrappedElement = new com.bosch.caltool.apic.ws.LogoutResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse wrapinvalidateWebFlowElement() {
    com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse wrappedElement =
        new com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse wrapgetUseCaseWithSectionTree() {
    com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.LoginResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody().addChild(param.getOMElement(com.bosch.caltool.apic.ws.LoginResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.LoginResponse wrapLogin() {
    com.bosch.caltool.apic.ws.LoginResponse wrappedElement = new com.bosch.caltool.apic.ws.LoginResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse wrapgetProjectIdCardForVersion() {
    com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse wrapgetStatusForAsyncExecution() {
    com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.LoadA2LFileDataResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.LoadA2LFileDataResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.LoadA2LFileDataResponse wraploadA2LFileData() {
    com.bosch.caltool.apic.ws.LoadA2LFileDataResponse wrappedElement =
        new com.bosch.caltool.apic.ws.LoadA2LFileDataResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1 param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody().addChild(
          param.getOMElement(com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1 wrapgetVcdmLabelStatisticsForWP() {
    com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1 wrappedElement =
        new com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetParameterStatisticsResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetParameterStatisticsResponse wrapgetParameterStatistics() {
    com.bosch.caltool.apic.ws.GetParameterStatisticsResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetParameterStatisticsResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetParameterReviewResultResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetParameterReviewResultResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetParameterReviewResultResponse wrapgetParameterReviewResult() {
    com.bosch.caltool.apic.ws.GetParameterReviewResultResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetParameterReviewResultResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse wrapgetParameterStatisticsXML() {
    com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse wrappedElement =
        new com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetAttributeValuesResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetAttributeValuesResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetAttributeValuesResponse wrapgetAttributeValues() {
    com.bosch.caltool.apic.ws.GetAttributeValuesResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetAttributeValuesResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse wrapgetPidcDiffForVersion() {
    com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.PidcStatisticResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.PidcStatisticResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.PidcStatisticResponse wrapgetPidcStatistic() {
    com.bosch.caltool.apic.ws.PidcStatisticResponse wrappedElement =
        new com.bosch.caltool.apic.ws.PidcStatisticResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse wrapgetPidcWebFlowElement() {
    com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetProjectIdCardResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetProjectIdCardResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetProjectIdCardResponse wrapgetProjectIdCard() {
    com.bosch.caltool.apic.ws.GetProjectIdCardResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetProjectIdCardResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.AllPidcDiffVersResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiffVersResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.AllPidcDiffVersResponse wrapgetAllPidcDiffForVersion() {
    com.bosch.caltool.apic.ws.AllPidcDiffVersResponse wrappedElement =
        new com.bosch.caltool.apic.ws.AllPidcDiffVersResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse wrapgetParameterStatisticsExt() {
    com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.AllPidcDiffResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.AllPidcDiffResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.AllPidcDiffResponse wrapgetAllPidcDiff() {
    com.bosch.caltool.apic.ws.AllPidcDiffResponse wrappedElement = new com.bosch.caltool.apic.ws.AllPidcDiffResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.PidcScoutVersResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.PidcScoutVersResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.PidcScoutVersResponse wrapgetPidcScoutResultForVersion() {
    com.bosch.caltool.apic.ws.PidcScoutVersResponse wrappedElement =
        new com.bosch.caltool.apic.ws.PidcScoutVersResponse();
    return wrappedElement;
  }

  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory,
      final com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse param, final boolean optimizeContent,
      final javax.xml.namespace.QName methodQName)
      throws org.apache.axis2.AxisFault {
    try {
      org.apache.axiom.soap.SOAPEnvelope emptyEnvelope = factory.getDefaultEnvelope();

      emptyEnvelope.getBody()
          .addChild(param.getOMElement(com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse.MY_QNAME, factory));


      return emptyEnvelope;
    }
    catch (org.apache.axis2.databinding.ADBException e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
  }

  private com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse wrapgetPidcWebFlowData() {
    com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse wrappedElement =
        new com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse();
    return wrappedElement;
  }


  /**
   * get the default envelope
   */
  private org.apache.axiom.soap.SOAPEnvelope toEnvelope(final org.apache.axiom.soap.SOAPFactory factory) {
    return factory.getDefaultEnvelope();
  }


  private java.lang.Object fromOM(final org.apache.axiom.om.OMElement param, final java.lang.Class type,
      final java.util.Map extraNamespaces)
      throws org.apache.axis2.AxisFault {

    try {

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Fault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcFavouritesReq.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcFavouritesReq.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcFavouritesResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcFavouritesResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcSearchCondition.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcSearchCondition.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcScoutResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcScoutResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcScoutFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcScoutFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCardV2.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCardV2.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCardV2Response.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCardV2Response.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCardV2Fault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCardV2Fault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetVcdmLabelStatReq.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetVcdmLabelStatReq.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsFile.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsFile.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsFileFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsFileFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcDiffs.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcDiffs.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcDiffsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcDiffsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcDiffsFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcDiffsFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcActiveVersion.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcActiveVersion.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcActiveVersionResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcActiveVersionResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.WebServiceVersionResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.WebServiceVersionResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcVersionStatisticsFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcVersionStatisticsFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.CancelSession.class.equals(type)) {

        return com.bosch.caltool.apic.ws.CancelSession.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.CancelSessionResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.CancelSessionResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AttrDiffVers.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AttrDiffVers.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AttrDiffVersResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AttrDiffVersResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllAttributes.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllAttributes.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllAttributesResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllAttributesResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.APICWsRequestFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.APICWsRequestFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAttrGroups.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAttrGroups.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAttrGroupsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAttrGroupsResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAttrGroupsFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAttrGroupsFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcAccessRight.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcAccessRight.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcAccessRightResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcAccessRightResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetUseCases.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetUseCases.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetUseCasesResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetUseCasesResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetUseCasesFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetUseCasesFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllProjectIdCards.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllProjectIdCards.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.APICWsRequestFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.APICWsRequestFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AttrDiff.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AttrDiff.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AttrDiffResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AttrDiffResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.Logout.class.equals(type)) {

        return com.bosch.caltool.apic.ws.Logout.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.LogoutResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.LogoutResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq.class.equals(type)) {

        return com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.Login.class.equals(type)) {

        return com.bosch.caltool.apic.ws.Login.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.LoginResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.LoginResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.LoginFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.LoginFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCardForVersion.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCardForVersion.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetStatusForAsyncExecution.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetStatusForAsyncExecution.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.LoadA2LFileData.class.equals(type)) {

        return com.bosch.caltool.apic.ws.LoadA2LFileData.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.LoadA2LFileDataResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.LoadA2LFileDataResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.LoadA2LFileDataFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.LoadA2LFileDataFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatistics.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatistics.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterReviewResult.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterReviewResult.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterReviewResultResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterReviewResultResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterReviewResultFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterReviewResultFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.ParameterStatistisXml.class.equals(type)) {

        return com.bosch.caltool.apic.ws.ParameterStatistisXml.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAttributeValues.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAttributeValues.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAttributeValuesResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAttributeValuesResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.APICWsRequestFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.APICWsRequestFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcDiffForVersion.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcDiffForVersion.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcDiffForVersionFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcDiffForVersionFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcStatistic.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcStatistic.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcStatisticResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcStatisticResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowElementFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowElementFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCard.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCard.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetProjectIdCardResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetProjectIdCardResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.APICWsRequestFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.APICWsRequestFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AllPidcDiffVers.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AllPidcDiffVers.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AllPidcDiffVersResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AllPidcDiffVersResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetAllPidcDiffForVersionFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetAllPidcDiffForVersionFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsExt.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsExt.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetParameterStatisticsExtFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetParameterStatisticsExtFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AllPidcDiff.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AllPidcDiff.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AllPidcDiffResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AllPidcDiffResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.AllPidcDiffFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.AllPidcDiffFault.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcVersSearchCondition.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcVersSearchCondition.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.PidcScoutVersResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.PidcScoutVersResponse.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowData.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowData.Factory.parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

      if (com.bosch.caltool.apic.ws.GetPidcWebFlowDataFault.class.equals(type)) {

        return com.bosch.caltool.apic.ws.GetPidcWebFlowDataFault.Factory
            .parse(param.getXMLStreamReaderWithoutCaching());


      }

    }
    catch (java.lang.Exception e) {
      throw org.apache.axis2.AxisFault.makeFault(e);
    }
    return null;
  }


  /**
   * A utility method that copies the namepaces from the SOAPEnvelope
   */
  private java.util.Map getEnvelopeNamespaces(final org.apache.axiom.soap.SOAPEnvelope env) {
    java.util.Map returnMap = new java.util.HashMap();
    java.util.Iterator namespaceIterator = env.getAllDeclaredNamespaces();
    while (namespaceIterator.hasNext()) {
      org.apache.axiom.om.OMNamespace ns = (org.apache.axiom.om.OMNamespace) namespaceIterator.next();
      returnMap.put(ns.getPrefix(), ns.getNamespaceURI());
    }
    return returnMap;
  }

  private org.apache.axis2.AxisFault createAxisFault(final java.lang.Exception e) {
    org.apache.axis2.AxisFault f;
    Throwable cause = e.getCause();
    if (cause != null) {
      f = new org.apache.axis2.AxisFault(e.getMessage(), cause);
    }
    else {
      f = new org.apache.axis2.AxisFault(e.getMessage());
    }

    return f;
  }

}// end of class
