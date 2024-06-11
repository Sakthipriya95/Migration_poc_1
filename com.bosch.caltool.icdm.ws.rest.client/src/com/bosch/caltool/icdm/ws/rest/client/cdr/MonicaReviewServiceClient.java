/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResultLink;
import com.bosch.caltool.icdm.model.cdr.MonicaInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaInputModel;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutput;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutputData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class MonicaReviewServiceClient extends AbstractRestServiceClient {


  private static final IMapperChangeData REVIEW_RESPONSE_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    MonicaReviewOutput monicaReviewOutput = (MonicaReviewOutput) data;

    for (MonicaReviewOutputData monicaReviewOutputData : monicaReviewOutput.getMonicaReviewOutputDataList()) {

      if (null != monicaReviewOutputData.getRvwVariant()) {
        changeDataList.add(changeDataCreator.createDataForCreate(0L, monicaReviewOutputData.getRvwVariant()));
      }
      else if (null != monicaReviewOutputData.getCdrReviewResult()) {
        changeDataList.add(changeDataCreator.createDataForCreate(0L, monicaReviewOutputData.getCdrReviewResult()));
      }

      List<A2lWpResponsibilityStatus> listofNewlyCreatedA2lWpRespStatus =
          monicaReviewOutputData.getListOfNewlyCreatedA2lWpRespStatus();
      if (!listofNewlyCreatedA2lWpRespStatus.isEmpty()) {
        listofNewlyCreatedA2lWpRespStatus.stream()
            .forEach(a2lWpRespStatus -> changeDataList.add(changeDataCreator.createDataForCreate(0L, a2lWpRespStatus)));
      }


      // changeData for WP finished Status
      Map<Long, A2lWpResponsibilityStatus> a2lWPRespStatusAfterUpdMap =
          monicaReviewOutputData.getA2lWpRespStatusAfterUpd();
      if (!a2lWPRespStatusAfterUpdMap.isEmpty()) {
        monicaReviewOutputData.getA2lWpRespStatusBeforeUpd().entrySet().forEach(a2lWPRespStatusEntrySet -> {
          if (a2lWPRespStatusAfterUpdMap.containsKey(a2lWPRespStatusEntrySet.getKey())) {
            changeDataList.add(changeDataCreator.createDataForUpdate(0L, a2lWPRespStatusEntrySet.getValue(),
                a2lWPRespStatusAfterUpdMap.get(a2lWPRespStatusEntrySet.getKey())));
          }
        });
      }
    }
    return changeDataList;
  };


  /**
   * initialize client constructor with the compli-param url
   */
  public MonicaReviewServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_MONICA_REVIEW);
  }

  /**
   * @param monicaResultInputData Monica Result Input Data
   * @param dcmFilePath DCM File Path
   * @param monicaExcelFilePath monica Excel File Path
   * @return CDRReviewResultLink
   * @throws ApicWebServiceException exception
   */
  public CDRReviewResultLink executeMonicaReview(final MonicaReviewInputData monicaResultInputData,
      final String dcmFilePath, final String monicaExcelFilePath)
      throws ApicWebServiceException {

    // dcm file body part
    final FileDataBodyPart dcmFile = new FileDataBodyPart(WsCommonConstants.DCM_FILE_MULTIPART, new File(dcmFilePath));
    // MoniCa excel file body part
    final FileDataBodyPart monicaFile =
        new FileDataBodyPart(WsCommonConstants.MONICA_FILE_MULTIPART, new File(monicaExcelFilePath));

    FormDataMultiPart multipart = new FormDataMultiPart();

    List<FormDataMultiPart> multiPartList = new ArrayList<>();
    multiPartList.add(multipart);
    CDRReviewResultLink response = new CDRReviewResultLink();

    try {
      // add dcm file
      multipart = (FormDataMultiPart) multipart.bodyPart(dcmFile);
      multiPartList.add(multipart);
      // Adding MoniCa Input Data in FormDataMultipart
      multipart.field(WsCommonConstants.RWS_QP_MONICA_REVIEW_OBJECT, monicaResultInputData,
          MediaType.APPLICATION_JSON_TYPE);
      // add MoniCa files
      multipart = (FormDataMultiPart) multipart.bodyPart(monicaFile);
      multiPartList.add(multipart);

      response = post(getWsBase(), multipart, CDRReviewResultLink.class);

      LOGGER.info("Result ID = {}", response.getResultId());
      LOGGER.info("MoniCa Review Status = {}", response.getMonicaReviewStatus());
      LOGGER.info("CDR Link Display Text = {}", response.getCdrLinkDisplayText());
      LOGGER.info("CDR Link = {}", response.getCdrLinkUrl());
    }
    finally {
      closeResource(multiPartList);
    }

    return response;
  }

  /**
   * @param monicaInputModel monicaInputModel
   * @return MonicaReviewOutput
   * @throws ApicWebServiceException ApicWebServiceException
   */
  public MonicaReviewOutput executeMonicaReviewInternal(final MonicaInputModel monicaInputModel)
      throws ApicWebServiceException {


    FormDataMultiPart multipart = new FormDataMultiPart();

    List<FormDataMultiPart> multiPartList = new ArrayList<>();
    multiPartList.add(multipart);

    // Adding MoniCa Input Stream in FormDataMultipart
    multipart.field(WsCommonConstants.RWS_QP_MONICA_REVIEW_OBJECT, monicaInputModel, MediaType.APPLICATION_JSON_TYPE);

    for (MonicaInputData monicaInputData : monicaInputModel.getMonicaInputDataList()) {
      // dcm file body part
      final FileDataBodyPart dcmFile =
          new FileDataBodyPart(WsCommonConstants.DCM_FILE_MULTIPART, new File(monicaInputData.getDcmFilePath()));
      multipart = (FormDataMultiPart) multipart.bodyPart(dcmFile);
      multiPartList.add(multipart);
      // MoniCa excel file body part
      final FileDataBodyPart monicaFile = new FileDataBodyPart(WsCommonConstants.MONICA_FILE_MULTIPART,
          new File(monicaInputData.getMonicaExcelFilePath()));
      multipart = (FormDataMultiPart) multipart.bodyPart(monicaFile);
      multiPartList.add(multipart);
    }
    MonicaReviewOutput response = new MonicaReviewOutput();
    try {
      // add dcm file
      response = post((getWsBase().path(WsCommonConstants.RWS_EXECUTE_REVIEW)), multipart, MonicaReviewOutput.class);

      Collection<ChangeData<IModel>> monicaReviewOutputModel =
          ModelParser.getChangeData(response, REVIEW_RESPONSE_MAPPER);

      (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(monicaReviewOutputModel));

    }
    finally {
      closeResource(multiPartList);
    }

    return response;
  }

}
