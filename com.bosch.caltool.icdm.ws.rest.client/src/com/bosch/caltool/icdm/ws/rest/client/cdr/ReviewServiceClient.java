/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;

import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantWrapper;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.cdr.review.FileData;
import com.bosch.caltool.icdm.model.cdr.review.ReviewInput;
import com.bosch.caltool.icdm.model.cdr.review.ReviewOutput;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author bru2cob
 */
public class ReviewServiceClient extends AbstractRestServiceClient {

  private static final IMapper REVIEW_RESPONSE_MAPPER = data -> {
    ReviewOutput reviewOutput = (ReviewOutput) data;
    return Arrays
        .asList(reviewOutput.getRvwVariant() != null ? reviewOutput.getRvwVariant() : reviewOutput.getCdrResult());
  };

  private static final IMapper CANCELLED_REVIEW_RESPONSE_MAPPER =
      data -> Arrays.asList(((ReviewVariantWrapper) data).getRvwVariant() != null
          ? ((ReviewVariantWrapper) data).getRvwVariant() : ((ReviewVariantWrapper) data).getCdrReviewResult());

  private static final IMapper PERFORM_REVIEW_INPUT_MAPPER = data -> {
    FormDataMultiPart multiPart = (FormDataMultiPart) data;
    FormDataBodyPart field = multiPart.getField(WsCommonConstants.RWS_QP_REVIEW_OBJECT);
    ReviewInput reviewInput = (ReviewInput) field.getEntity();
    return Arrays
        .asList(reviewInput.getRvwVariant() != null ? reviewInput.getRvwVariant() : reviewInput.getCdrReviewResult());
  };

  private static final IMapper CANCELLED_REVIEW_INPUT_MAPPER = data -> {
    FormDataMultiPart multiPart = (FormDataMultiPart) data;
    FormDataBodyPart field = multiPart.getField(WsCommonConstants.RWS_REVIEW_OBJECT);
    ReviewInput reviewInput = (ReviewInput) field.getEntity();
    return Arrays
        .asList(reviewInput.getRvwVariant() != null ? reviewInput.getRvwVariant() : reviewInput.getCdrReviewResult());
  };

  private static final IMapperChangeData RVW_DETAILS_CREATE_AND_UPDATE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    ReviewOutput reviewOutput = (ReviewOutput) data;

    RvwVariant rvwVar = reviewOutput.getRvwVariant();
    changeDataList
        .add(changeDataCreator.createDataForCreate(0L, rvwVar != null ? rvwVar : reviewOutput.getCdrResult()));

    List<A2lWpResponsibilityStatus> listOfNewlyCreatedA2lWPRespStatus =
        reviewOutput.getListOfNewlyCreatedA2lWpRespStatus();
    if (!listOfNewlyCreatedA2lWPRespStatus.isEmpty()) {
      listOfNewlyCreatedA2lWPRespStatus.stream()
          .forEach(a2lWPRespStatus -> changeDataList.add(changeDataCreator.createDataForCreate(0L, a2lWPRespStatus)));
    }

    Map<Long, A2lWpResponsibilityStatus> a2lWpRespAfterUpdateMap = reviewOutput.getA2lWpResponsibilityAfterUpdate();
    if (!a2lWpRespAfterUpdateMap.isEmpty()) {
      reviewOutput.getA2lWpResponsibilityBeforeUpdate().entrySet().forEach(a2lWPRespStatusEntrySet -> {
        if (reviewOutput.getA2lWpResponsibilityAfterUpdate().containsKey(a2lWPRespStatusEntrySet.getKey())) {
          changeDataList.add(changeDataCreator.createDataForUpdate(0L, a2lWPRespStatusEntrySet.getValue(),
              a2lWpRespAfterUpdateMap.get(a2lWPRespStatusEntrySet.getKey())));
        }
      });
    }

    return changeDataList;
  };


  /**
   * client constructor
   */
  public ReviewServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_REVIEW);
  }

  /**
   * Perform calibration data review
   *
   * @param inputData review input data
   * @return review output
   * @throws ApicWebServiceException error in web service call
   */
  public ReviewOutput performReview(final ReviewInput inputData) throws ApicWebServiceException {

    ReviewOutput response = null;

    Set<String> tempFilesSet = new HashSet<>();

    FileData filesPath = inputData.getFileData();
    Set<String> filesToBeReviewedSet = filesPath.getSelFilesPath();

    List<FormDataMultiPart> multiPartList = new ArrayList<>();

    FormDataMultiPart multipart = new FormDataMultiPart();
    multiPartList.add(multipart);

    try {
      for (String fileName : filesToBeReviewedSet) {
        String zippedFile = compressFile(fileName);
        tempFilesSet.add(zippedFile);

        // review file body part
        FileDataBodyPart reviewFilePart =
            new FileDataBodyPart(WsCommonConstants.REVIEW_FILE_MULTIPART, new File(zippedFile));
        multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);

        // add file name to header
        reviewFilePart.getHeaders().add(WsCommonConstants.REVIEW_FILE_NAME, fileName);
        // add desription to header

        multiPartList.add(multipart);
      }

      String ssdRuleFilePath = inputData.getRulesData().getSsdRuleFilePath();
      if (null != ssdRuleFilePath) {

        String zippedFile = compressFile(ssdRuleFilePath);
        tempFilesSet.add(zippedFile);

        FileDataBodyPart reviewFilePart =
            new FileDataBodyPart(WsCommonConstants.SSD_RULE_FILE_MULTIPART, new File(zippedFile));
        // add file name to header
        reviewFilePart.getHeaders().add(WsCommonConstants.SSD_RELEASE_FILE_NAME, ssdRuleFilePath);
        multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);
        multiPartList.add(multipart);
      }

      String labFunFilePath = inputData.getFileData().getFunLabFilePath();
      if (null != labFunFilePath) {

        String zippedFile = compressFile(labFunFilePath);
        tempFilesSet.add(zippedFile);

        FileDataBodyPart reviewFilePart =
            new FileDataBodyPart(WsCommonConstants.LAB_FUN_FILE_MULTIPART, new File(zippedFile));
        // add file name to header
        reviewFilePart.getHeaders().add(WsCommonConstants.LAB_FUN_FILE_NAME, labFunFilePath);
        multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);
        multiPartList.add(multipart);
      }

      multipart.field(WsCommonConstants.RWS_QP_REVIEW_OBJECT, inputData, MediaType.APPLICATION_JSON_TYPE);
      multiPartList.add(multipart);
      WebTarget wsTarget = null;

      if (null != inputData.getResultData().getCanceledResultId()) {
        wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_REVIEW);
        response = update(wsTarget, multipart, ReviewOutput.class, PERFORM_REVIEW_INPUT_MAPPER, REVIEW_RESPONSE_MAPPER);
      }
      else {

        ReviewOutput respModel =
            post((getWsBase().path(WsCommonConstants.RWS_PERFORM_REVIEW)), multipart, ReviewOutput.class);

        Collection<ChangeData<IModel>> rvwOutputModel =
            ModelParser.getChangeData(respModel, RVW_DETAILS_CREATE_AND_UPDATE_MAPPER);

        (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(rvwOutputModel));

        displayMessage("New Review is created and wp status is updated successfully!");

        return respModel;
      }

    }
    finally {
      closeResource(multiPartList);
      deleteFiles(tempFilesSet);
    }

    return response;
  }

  /**
   * Save cancelled result
   *
   * @param inputData review input data
   * @return review output
   * @throws ApicWebServiceException error in web service call
   */
  public ReviewVariantWrapper saveCancelledResult(final ReviewInput inputData) throws ApicWebServiceException {
    ReviewVariantWrapper response = null;

    Set<String> tempFilesSet = new HashSet<>();

    FileData filesPath = inputData.getFileData();
    Set<String> filesToBeReviewedSet = filesPath.getSelFilesPath();

    List<FormDataMultiPart> multiPartList = new ArrayList<>();

    FormDataMultiPart multipart = new FormDataMultiPart();
    multiPartList.add(multipart);

    try {
      if (filesToBeReviewedSet != null) {
        for (String fileName : filesToBeReviewedSet) {
          String zippedFile = compressFile(fileName);
          tempFilesSet.add(zippedFile);

          // review file body part
          FileDataBodyPart reviewFilePart =
              new FileDataBodyPart(WsCommonConstants.REVIEW_FILE_MULTIPART, new File(zippedFile));
          multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);

          // add file name to header
          reviewFilePart.getHeaders().add(WsCommonConstants.REVIEW_FILE_NAME, fileName);
          // add desription to header

          multiPartList.add(multipart);
        }
      }

      String ssdRuleFilePath = inputData.getRulesData().getSsdRuleFilePath();
      if (null != ssdRuleFilePath) {

        String zippedFile = compressFile(ssdRuleFilePath);
        tempFilesSet.add(zippedFile);

        FileDataBodyPart reviewFilePart =
            new FileDataBodyPart(WsCommonConstants.SSD_RULE_FILE_MULTIPART, new File(zippedFile));
        // add file name to header
        reviewFilePart.getHeaders().add(WsCommonConstants.SSD_RELEASE_FILE_NAME, ssdRuleFilePath);
        multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);
        multiPartList.add(multipart);
      }

      String labFunFilePath = inputData.getFileData().getFunLabFilePath();
      if (null != labFunFilePath) {

        String zippedFile = compressFile(labFunFilePath);
        tempFilesSet.add(zippedFile);

        FileDataBodyPart reviewFilePart =
            new FileDataBodyPart(WsCommonConstants.LAB_FUN_FILE_MULTIPART, new File(zippedFile));
        // add file name to header
        reviewFilePart.getHeaders().add(WsCommonConstants.LAB_FUN_FILE_NAME, labFunFilePath);
        multipart = (FormDataMultiPart) multipart.bodyPart(reviewFilePart);
        multiPartList.add(multipart);
      }

      multipart.field(WsCommonConstants.RWS_REVIEW_OBJECT, inputData, MediaType.APPLICATION_JSON_TYPE);
      multiPartList.add(multipart);

      WebTarget wsTarget = null;
      if (inputData.getResultData().getCanceledResultId() != null) {
        wsTarget = getWsBase().path(WsCommonConstants.RWS_UPDATE_CANCELLED_REVIEW);
        response = update(wsTarget, multipart, ReviewVariantWrapper.class, CANCELLED_REVIEW_INPUT_MAPPER,
            CANCELLED_REVIEW_RESPONSE_MAPPER);

      }
      else {
        wsTarget = getWsBase().path(WsCommonConstants.RWS_SAVE_CANCELLED_REVIEW);
        response = create(wsTarget, multipart, ReviewVariantWrapper.class, CANCELLED_REVIEW_RESPONSE_MAPPER);
      }


      return response;
    }
    finally {
      closeResource(multiPartList);
      deleteFiles(tempFilesSet);
    }

  }


  /**
   * Download ssd fiels
   *
   * @param filePaths server file paths
   * @return inputStream input Stream
   * @throws ApicWebServiceException data exception
   */
  public String downloadFilesFromServer(final Set<String> filePaths, final String localFilePath)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FILE_DOWNLOAD);
    GenericType<Map<String, byte[]>> type = new GenericType<Map<String, byte[]>>() {};
    Map<String, byte[]> retMap = post(wsTarget, filePaths, type);
    String dirPath = localFilePath;
    for (Entry<String, byte[]> filePathMap : retMap.entrySet()) {
      String filePath = filePathMap.getKey();
      String fileName = new File(filePath).getName();
      File file = new File(localFilePath + File.separator + fileName);

      String compliReview = "CompliReview";
      if (filePath.contains(compliReview)) {
        String dirName = null;
        String[] fileNames = filePath.split("\\\\");
        if (fileNames[fileNames.length - 3].contains(compliReview)) {
          dirName = fileNames[fileNames.length - 3];
        }
        else if (fileNames[fileNames.length - 2].contains(compliReview)) {
          dirName = fileNames[fileNames.length - 2];
        }
        if (dirName != null) {
          File dirFile = new File(localFilePath + File.separator + dirName);
          if (!dirFile.exists()) {
            dirFile.mkdir();
          }
          file = new File(dirFile.getPath() + File.separator + fileName);
        }
      }

      InputStream inputStream = new ByteArrayInputStream(filePathMap.getValue());
      writeData(fileName, file, inputStream);
      dirPath = file.getParent();
    }

    return dirPath;
  }

  /**
   * Retrieves the simplified General Qnaire declaration file
   *
   * @param dirPath directory path
   * @return byte[]
   * @throws ApicWebServiceException error during webservice call
   */
  public byte[] getSimpQnaireDeclrtnFile(final String dirPath) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_SIMPLIFIED_QNAIRE_DECLARATION_FILE);
    downloadFile(wsTarget, ApicConstants.SIMP_QNAIRE_DECLAR_FILE, dirPath);
    try {
      return IOUtils.toByteArray(new FileInputStream(dirPath + File.separator + ApicConstants.SIMP_QNAIRE_DECLAR_FILE));
    }
    catch (IOException e) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, e.getMessage(), e);
    }
  }
}
