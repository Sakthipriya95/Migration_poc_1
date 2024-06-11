
/**
 * APICSkeletonInterface.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */
    package com.bosch.caltool.apic.ws;
    /**
     *  APICSkeletonInterface java skeleton interface for the axisService
     */
    public interface APICSkeletonInterface {
     
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcWebFlowDataV2
             * @throws GetPidcWebFlowDataV2FaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2Response getPidcWebFlowDataV2
                (
                  com.bosch.caltool.apic.ws.GetPidcWebFlowDataV2 getPidcWebFlowDataV2
                 )
            throws GetPidcWebFlowDataV2FaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcFavouritesReq
         */

        
                public com.bosch.caltool.apic.ws.GetPidcFavouritesResponse getPidcFavourites
                (
                  com.bosch.caltool.apic.ws.GetPidcFavouritesReq getPidcFavouritesReq
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param pidcSearchCondition
             * @throws GetPidcScoutResultFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.PidcScoutResponse getPidcScoutResult
                (
                  com.bosch.caltool.apic.ws.PidcSearchCondition pidcSearchCondition
                 )
            throws GetPidcScoutResultFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getProjectIdCardV2
             * @throws GetProjectIdCardV2FaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetProjectIdCardV2Response getProjectIdCardV2
                (
                  com.bosch.caltool.apic.ws.GetProjectIdCardV2 getProjectIdCardV2
                 )
            throws GetProjectIdCardV2FaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getVcdmLabelStatReq
             * @throws GetVcdmLabelStatisticsFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsResponse getVcdmLabelStatistics
                (
                  com.bosch.caltool.apic.ws.GetVcdmLabelStatReq getVcdmLabelStatReq
                 )
            throws GetVcdmLabelStatisticsFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getParameterStatisticsFile
             * @throws GetParameterStatisticsFileFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetParameterStatisticsFileResponse getParameterStatisticsFile
                (
                  com.bosch.caltool.apic.ws.GetParameterStatisticsFile getParameterStatisticsFile
                 )
            throws GetParameterStatisticsFileFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcDiffs
             * @throws GetPidcDiffsFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetPidcDiffsResponse getPidcDiffs
                (
                  com.bosch.caltool.apic.ws.GetPidcDiffs getPidcDiffs
                 )
            throws GetPidcDiffsFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param pidcActiveVersion
         */

        
                public com.bosch.caltool.apic.ws.PidcActiveVersionResponse getPidcActiveVersionId
                (
                  com.bosch.caltool.apic.ws.PidcActiveVersion pidcActiveVersion
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
         */

        
                public com.bosch.caltool.apic.ws.WebServiceVersionResponse getWebServiceVersion
                (
                  
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcVersionStatisticsRequest
             * @throws GetPidcVersionStatisticsFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetPidcVersionStatisticsResponse getPidcVersionStatistics
                (
                  com.bosch.caltool.apic.ws.GetPidcVersionStatisticsRequest getPidcVersionStatisticsRequest
                 )
            throws GetPidcVersionStatisticsFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param cancelSession
         */

        
                public com.bosch.caltool.apic.ws.CancelSessionResponse cancelSession
                (
                  com.bosch.caltool.apic.ws.CancelSession cancelSession
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param attrDiffVers
         */

        
                public com.bosch.caltool.apic.ws.AttrDiffVersResponse getPidcAttrDiffReportForVersion
                (
                  com.bosch.caltool.apic.ws.AttrDiffVers attrDiffVers
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getAllProjectIdCardVersions
         */

        
                public com.bosch.caltool.apic.ws.GetAllProjectIdCardVersionsResponse getAllProjectIdCardVersions
                (
                  com.bosch.caltool.apic.ws.GetAllProjectIdCardVersions getAllProjectIdCardVersions
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * Get all Attributes defined in the database.
                                    * @param getAllAttributes
             * @throws GetAllAttributesFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetAllAttributesResponse getAllAttributes
                (
                  com.bosch.caltool.apic.ws.GetAllAttributes getAllAttributes
                 )
            throws GetAllAttributesFaultException;
        
         
        /**
         * Auto generated method signature
         * Get the Super Group and Group structure in which the Attributes are organized
                                    * @param getAttrGroups
             * @throws GetAttrGroupsFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetAttrGroupsResponse getAttrGroups
                (
                  com.bosch.caltool.apic.ws.GetAttrGroups getAttrGroups
                 )
            throws GetAttrGroupsFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param pidcAccessRight
         */

        
                public com.bosch.caltool.apic.ws.PidcAccessRightResponse getPidcAccessRight
                (
                  com.bosch.caltool.apic.ws.PidcAccessRight pidcAccessRight
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getUseCases
             * @throws GetUseCasesFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetUseCasesResponse getUseCases
                (
                  com.bosch.caltool.apic.ws.GetUseCases getUseCases
                 )
            throws GetUseCasesFaultException;
        
         
        /**
         * Auto generated method signature
         * Get all ProjectIdCards froam the database.
This returns only the names of the ProjectIdcards, not the attribute values.
The result can be used in an application to select a ProjectIdCard
                                    * @param getAllProjectIdCards
             * @throws GetAllProjectIdCardsFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetAllProjectIdCardsResponse getAllProjectIdCards
                (
                  com.bosch.caltool.apic.ws.GetAllProjectIdCards getAllProjectIdCards
                 )
            throws GetAllProjectIdCardsFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param attrDiff
         */

        
                public com.bosch.caltool.apic.ws.AttrDiffResponse getPidcAttrDiffReport
                (
                  com.bosch.caltool.apic.ws.AttrDiff attrDiff
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param logout
         */

        
                public com.bosch.caltool.apic.ws.LogoutResponse logout
                (
                  com.bosch.caltool.apic.ws.Logout logout
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param invalidateWebFlowElementReq
         */

        
                public com.bosch.caltool.apic.ws.InvalidateWebFlowElementResponse invalidateWebFlowElement
                (
                  com.bosch.caltool.apic.ws.InvalidateWebFlowElementReq invalidateWebFlowElementReq
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getUseCaseWithSectionTree
         */

        
                public com.bosch.caltool.apic.ws.GetUseCaseWithSectionTreeResponse getUseCaseWithSectionTree
                (
                  com.bosch.caltool.apic.ws.GetUseCaseWithSectionTree getUseCaseWithSectionTree
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param login
             * @throws LoginFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.LoginResponse login
                (
                  com.bosch.caltool.apic.ws.Login login
                 )
            throws LoginFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getProjectIdCardForVersion
         */

        
                public com.bosch.caltool.apic.ws.GetProjectIdCardForVersionResponse getProjectIdCardForVersion
                (
                  com.bosch.caltool.apic.ws.GetProjectIdCardForVersion getProjectIdCardForVersion
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getStatusForAsyncExecution
         */

        
                public com.bosch.caltool.apic.ws.GetStatusForAsyncExecutionResponse getStatusForAsyncExecution
                (
                  com.bosch.caltool.apic.ws.GetStatusForAsyncExecution getStatusForAsyncExecution
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param loadA2LFileData
             * @throws LoadA2LFileDataFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.LoadA2LFileDataResponse loadA2LFileData
                (
                  com.bosch.caltool.apic.ws.LoadA2LFileData loadA2LFileData
                 )
            throws LoadA2LFileDataFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getVcdmLabelStatisticsForWP
         */

        
                public com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWPResponse1 getVcdmLabelStatisticsForWP
                (
                  com.bosch.caltool.apic.ws.GetVcdmLabelStatisticsForWP getVcdmLabelStatisticsForWP
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getParameterStatistics
             * @throws GetParameterStatisticsFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetParameterStatisticsResponse getParameterStatistics
                (
                  com.bosch.caltool.apic.ws.GetParameterStatistics getParameterStatistics
                 )
            throws GetParameterStatisticsFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getParameterReviewResult
             * @throws GetParameterReviewResultFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetParameterReviewResultResponse getParameterReviewResult
                (
                  com.bosch.caltool.apic.ws.GetParameterReviewResult getParameterReviewResult
                 )
            throws GetParameterReviewResultFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param parameterStatistisXml
         */

        
                public com.bosch.caltool.apic.ws.ParameterStatisticsXmlResponse getParameterStatisticsXML
                (
                  com.bosch.caltool.apic.ws.ParameterStatistisXml parameterStatistisXml
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * Get the defined values for an attribute
                                    * @param getAttributeValues
             * @throws GetAttributeValuesFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetAttributeValuesResponse getAttributeValues
                (
                  com.bosch.caltool.apic.ws.GetAttributeValues getAttributeValues
                 )
            throws GetAttributeValuesFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcDiffForVersion
             * @throws GetPidcDiffForVersionFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetPidcDiffForVersionResponse getPidcDiffForVersion
                (
                  com.bosch.caltool.apic.ws.GetPidcDiffForVersion getPidcDiffForVersion
                 )
            throws GetPidcDiffForVersionFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param pidcStatistic
         */

        
                public com.bosch.caltool.apic.ws.PidcStatisticResponse getPidcStatistic
                (
                  com.bosch.caltool.apic.ws.PidcStatistic pidcStatistic
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcWebFlowElementReq
             * @throws GetPidcWebFlowElementFault1 : 
         */

        
                public com.bosch.caltool.apic.ws.GetPidcWebFlowElementResponse getPidcWebFlowElement
                (
                  com.bosch.caltool.apic.ws.GetPidcWebFlowElementReq getPidcWebFlowElementReq
                 )
            throws GetPidcWebFlowElementFault1;
        
         
        /**
         * Auto generated method signature
         * Returns the name of the projectIdCard plus all attribute values defined for this ProjectIdCard.
If the ProjectIdCard has variants, also the variants are contained in the result
                                    * @param getProjectIdCard
             * @throws GetProjectIdCardFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetProjectIdCardResponse getProjectIdCard
                (
                  com.bosch.caltool.apic.ws.GetProjectIdCard getProjectIdCard
                 )
            throws GetProjectIdCardFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param allPidcDiffVers
             * @throws GetAllPidcDiffForVersionFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.AllPidcDiffVersResponse getAllPidcDiffForVersion
                (
                  com.bosch.caltool.apic.ws.AllPidcDiffVers allPidcDiffVers
                 )
            throws GetAllPidcDiffForVersionFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getParameterStatisticsExt
             * @throws GetParameterStatisticsExtFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.GetParameterStatisticsExtResponse getParameterStatisticsExt
                (
                  com.bosch.caltool.apic.ws.GetParameterStatisticsExt getParameterStatisticsExt
                 )
            throws GetParameterStatisticsExtFaultException;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param allPidcDiff
             * @throws GetAllPidcDiffFaultException : 
         */

        
                public com.bosch.caltool.apic.ws.AllPidcDiffResponse getAllPidcDiff
                (
                  com.bosch.caltool.apic.ws.AllPidcDiff allPidcDiff
                 )
            throws GetAllPidcDiffFaultException;
        
         
        /**
         * Auto generated method signature
         * Find all PIDCs which have a given set of attribute and value IDs.
                                    * @param pidcVersSearchCondition
         */

        
                public com.bosch.caltool.apic.ws.PidcScoutVersResponse getPidcScoutResultForVersion
                (
                  com.bosch.caltool.apic.ws.PidcVersSearchCondition pidcVersSearchCondition
                 )
            ;
        
         
        /**
         * Auto generated method signature
         * 
                                    * @param getPidcWebFlowData
             * @throws GetPidcWebFlowDataFault1Exception : 
         */

        
                public com.bosch.caltool.apic.ws.GetPidcWebFlowDataResponse getPidcWebFlowData
                (
                  com.bosch.caltool.apic.ws.GetPidcWebFlowData getPidcWebFlowData
                 )
            throws GetPidcWebFlowDataFault1Exception;
        
         }
    