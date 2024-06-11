
/**
 * APICCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package com.bosch.caltool.apic.ws.client;

    /**
     *  APICCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class APICCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public APICCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public APICCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getPidcWebFlowDataV2 method
            * override this method for handling normal response from getPidcWebFlowDataV2 operation
            */
           public void receiveResultgetPidcWebFlowDataV2(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowDataV2Response result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcWebFlowDataV2 operation
           */
            public void receiveErrorgetPidcWebFlowDataV2(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcFavourites method
            * override this method for handling normal response from getPidcFavourites operation
            */
           public void receiveResultgetPidcFavourites(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcFavouritesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcFavourites operation
           */
            public void receiveErrorgetPidcFavourites(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcScoutResult method
            * override this method for handling normal response from getPidcScoutResult operation
            */
           public void receiveResultgetPidcScoutResult(
                    com.bosch.caltool.apic.ws.client.APICStub.PidcScoutResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcScoutResult operation
           */
            public void receiveErrorgetPidcScoutResult(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProjectIdCardV2 method
            * override this method for handling normal response from getProjectIdCardV2 operation
            */
           public void receiveResultgetProjectIdCardV2(
                    com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardV2Response result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProjectIdCardV2 operation
           */
            public void receiveErrorgetProjectIdCardV2(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getVcdmLabelStatistics method
            * override this method for handling normal response from getVcdmLabelStatistics operation
            */
           public void receiveResultgetVcdmLabelStatistics(
                    com.bosch.caltool.apic.ws.client.APICStub.GetVcdmLabelStatisticsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getVcdmLabelStatistics operation
           */
            public void receiveErrorgetVcdmLabelStatistics(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getParameterStatisticsFile method
            * override this method for handling normal response from getParameterStatisticsFile operation
            */
           public void receiveResultgetParameterStatisticsFile(
                    com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsFileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getParameterStatisticsFile operation
           */
            public void receiveErrorgetParameterStatisticsFile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcDiffs method
            * override this method for handling normal response from getPidcDiffs operation
            */
           public void receiveResultgetPidcDiffs(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcDiffs operation
           */
            public void receiveErrorgetPidcDiffs(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcActiveVersionId method
            * override this method for handling normal response from getPidcActiveVersionId operation
            */
           public void receiveResultgetPidcActiveVersionId(
                    com.bosch.caltool.apic.ws.client.APICStub.PidcActiveVersionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcActiveVersionId operation
           */
            public void receiveErrorgetPidcActiveVersionId(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getWebServiceVersion method
            * override this method for handling normal response from getWebServiceVersion operation
            */
           public void receiveResultgetWebServiceVersion(
                    com.bosch.caltool.apic.ws.client.APICStub.WebServiceVersionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getWebServiceVersion operation
           */
            public void receiveErrorgetWebServiceVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcVersionStatistics method
            * override this method for handling normal response from getPidcVersionStatistics operation
            */
           public void receiveResultgetPidcVersionStatistics(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcVersionStatisticsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcVersionStatistics operation
           */
            public void receiveErrorgetPidcVersionStatistics(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for cancelSession method
            * override this method for handling normal response from cancelSession operation
            */
           public void receiveResultcancelSession(
                    com.bosch.caltool.apic.ws.client.APICStub.CancelSessionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from cancelSession operation
           */
            public void receiveErrorcancelSession(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcAttrDiffReportForVersion method
            * override this method for handling normal response from getPidcAttrDiffReportForVersion operation
            */
           public void receiveResultgetPidcAttrDiffReportForVersion(
                    com.bosch.caltool.apic.ws.client.APICStub.AttrDiffVersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcAttrDiffReportForVersion operation
           */
            public void receiveErrorgetPidcAttrDiffReportForVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllProjectIdCardVersions method
            * override this method for handling normal response from getAllProjectIdCardVersions operation
            */
           public void receiveResultgetAllProjectIdCardVersions(
                    com.bosch.caltool.apic.ws.client.APICStub.GetAllProjectIdCardVersionsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllProjectIdCardVersions operation
           */
            public void receiveErrorgetAllProjectIdCardVersions(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllAttributes method
            * override this method for handling normal response from getAllAttributes operation
            */
           public void receiveResultgetAllAttributes(
                    com.bosch.caltool.apic.ws.client.APICStub.GetAllAttributesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllAttributes operation
           */
            public void receiveErrorgetAllAttributes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAttrGroups method
            * override this method for handling normal response from getAttrGroups operation
            */
           public void receiveResultgetAttrGroups(
                    com.bosch.caltool.apic.ws.client.APICStub.GetAttrGroupsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAttrGroups operation
           */
            public void receiveErrorgetAttrGroups(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcAccessRight method
            * override this method for handling normal response from getPidcAccessRight operation
            */
           public void receiveResultgetPidcAccessRight(
                    com.bosch.caltool.apic.ws.client.APICStub.PidcAccessRightResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcAccessRight operation
           */
            public void receiveErrorgetPidcAccessRight(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getUseCases method
            * override this method for handling normal response from getUseCases operation
            */
           public void receiveResultgetUseCases(
                    com.bosch.caltool.apic.ws.client.APICStub.GetUseCasesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getUseCases operation
           */
            public void receiveErrorgetUseCases(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllProjectIdCards method
            * override this method for handling normal response from getAllProjectIdCards operation
            */
           public void receiveResultgetAllProjectIdCards(
                    com.bosch.caltool.apic.ws.client.APICStub.GetAllProjectIdCardsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllProjectIdCards operation
           */
            public void receiveErrorgetAllProjectIdCards(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcAttrDiffReport method
            * override this method for handling normal response from getPidcAttrDiffReport operation
            */
           public void receiveResultgetPidcAttrDiffReport(
                    com.bosch.caltool.apic.ws.client.APICStub.AttrDiffResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcAttrDiffReport operation
           */
            public void receiveErrorgetPidcAttrDiffReport(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for logout method
            * override this method for handling normal response from logout operation
            */
           public void receiveResultlogout(
                    com.bosch.caltool.apic.ws.client.APICStub.LogoutResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from logout operation
           */
            public void receiveErrorlogout(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for invalidateWebFlowElement method
            * override this method for handling normal response from invalidateWebFlowElement operation
            */
           public void receiveResultinvalidateWebFlowElement(
                    com.bosch.caltool.apic.ws.client.APICStub.InvalidateWebFlowElementResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from invalidateWebFlowElement operation
           */
            public void receiveErrorinvalidateWebFlowElement(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getUseCaseWithSectionTree method
            * override this method for handling normal response from getUseCaseWithSectionTree operation
            */
           public void receiveResultgetUseCaseWithSectionTree(
                    com.bosch.caltool.apic.ws.client.APICStub.GetUseCaseWithSectionTreeResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getUseCaseWithSectionTree operation
           */
            public void receiveErrorgetUseCaseWithSectionTree(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for login method
            * override this method for handling normal response from login operation
            */
           public void receiveResultlogin(
                    com.bosch.caltool.apic.ws.client.APICStub.LoginResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from login operation
           */
            public void receiveErrorlogin(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProjectIdCardForVersion method
            * override this method for handling normal response from getProjectIdCardForVersion operation
            */
           public void receiveResultgetProjectIdCardForVersion(
                    com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardForVersionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProjectIdCardForVersion operation
           */
            public void receiveErrorgetProjectIdCardForVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getStatusForAsyncExecution method
            * override this method for handling normal response from getStatusForAsyncExecution operation
            */
           public void receiveResultgetStatusForAsyncExecution(
                    com.bosch.caltool.apic.ws.client.APICStub.GetStatusForAsyncExecutionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getStatusForAsyncExecution operation
           */
            public void receiveErrorgetStatusForAsyncExecution(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for loadA2LFileData method
            * override this method for handling normal response from loadA2LFileData operation
            */
           public void receiveResultloadA2LFileData(
                    com.bosch.caltool.apic.ws.client.APICStub.LoadA2LFileDataResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from loadA2LFileData operation
           */
            public void receiveErrorloadA2LFileData(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getVcdmLabelStatisticsForWP method
            * override this method for handling normal response from getVcdmLabelStatisticsForWP operation
            */
           public void receiveResultgetVcdmLabelStatisticsForWP(
                    com.bosch.caltool.apic.ws.client.APICStub.GetVcdmLabelStatisticsForWPResponse1 result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getVcdmLabelStatisticsForWP operation
           */
            public void receiveErrorgetVcdmLabelStatisticsForWP(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getParameterStatistics method
            * override this method for handling normal response from getParameterStatistics operation
            */
           public void receiveResultgetParameterStatistics(
                    com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getParameterStatistics operation
           */
            public void receiveErrorgetParameterStatistics(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getParameterReviewResult method
            * override this method for handling normal response from getParameterReviewResult operation
            */
           public void receiveResultgetParameterReviewResult(
                    com.bosch.caltool.apic.ws.client.APICStub.GetParameterReviewResultResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getParameterReviewResult operation
           */
            public void receiveErrorgetParameterReviewResult(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getParameterStatisticsXML method
            * override this method for handling normal response from getParameterStatisticsXML operation
            */
           public void receiveResultgetParameterStatisticsXML(
                    com.bosch.caltool.apic.ws.client.APICStub.ParameterStatisticsXmlResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getParameterStatisticsXML operation
           */
            public void receiveErrorgetParameterStatisticsXML(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAttributeValues method
            * override this method for handling normal response from getAttributeValues operation
            */
           public void receiveResultgetAttributeValues(
                    com.bosch.caltool.apic.ws.client.APICStub.GetAttributeValuesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAttributeValues operation
           */
            public void receiveErrorgetAttributeValues(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcDiffForVersion method
            * override this method for handling normal response from getPidcDiffForVersion operation
            */
           public void receiveResultgetPidcDiffForVersion(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcDiffForVersionResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcDiffForVersion operation
           */
            public void receiveErrorgetPidcDiffForVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcStatistic method
            * override this method for handling normal response from getPidcStatistic operation
            */
           public void receiveResultgetPidcStatistic(
                    com.bosch.caltool.apic.ws.client.APICStub.PidcStatisticResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcStatistic operation
           */
            public void receiveErrorgetPidcStatistic(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcWebFlowElement method
            * override this method for handling normal response from getPidcWebFlowElement operation
            */
           public void receiveResultgetPidcWebFlowElement(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowElementResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcWebFlowElement operation
           */
            public void receiveErrorgetPidcWebFlowElement(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getProjectIdCard method
            * override this method for handling normal response from getProjectIdCard operation
            */
           public void receiveResultgetProjectIdCard(
                    com.bosch.caltool.apic.ws.client.APICStub.GetProjectIdCardResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getProjectIdCard operation
           */
            public void receiveErrorgetProjectIdCard(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllPidcDiffForVersion method
            * override this method for handling normal response from getAllPidcDiffForVersion operation
            */
           public void receiveResultgetAllPidcDiffForVersion(
                    com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffVersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllPidcDiffForVersion operation
           */
            public void receiveErrorgetAllPidcDiffForVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getParameterStatisticsExt method
            * override this method for handling normal response from getParameterStatisticsExt operation
            */
           public void receiveResultgetParameterStatisticsExt(
                    com.bosch.caltool.apic.ws.client.APICStub.GetParameterStatisticsExtResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getParameterStatisticsExt operation
           */
            public void receiveErrorgetParameterStatisticsExt(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getAllPidcDiff method
            * override this method for handling normal response from getAllPidcDiff operation
            */
           public void receiveResultgetAllPidcDiff(
                    com.bosch.caltool.apic.ws.client.APICStub.AllPidcDiffResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAllPidcDiff operation
           */
            public void receiveErrorgetAllPidcDiff(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcScoutResultForVersion method
            * override this method for handling normal response from getPidcScoutResultForVersion operation
            */
           public void receiveResultgetPidcScoutResultForVersion(
                    com.bosch.caltool.apic.ws.client.APICStub.PidcScoutVersResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcScoutResultForVersion operation
           */
            public void receiveErrorgetPidcScoutResultForVersion(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getPidcWebFlowData method
            * override this method for handling normal response from getPidcWebFlowData operation
            */
           public void receiveResultgetPidcWebFlowData(
                    com.bosch.caltool.apic.ws.client.APICStub.GetPidcWebFlowDataResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getPidcWebFlowData operation
           */
            public void receiveErrorgetPidcWebFlowData(java.lang.Exception e) {
            }
                


    }
    