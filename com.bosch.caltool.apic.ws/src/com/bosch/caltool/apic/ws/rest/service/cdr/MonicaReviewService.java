/*
 * \ * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.calmodel.caldata.CalData;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefaultDefinitionCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpRespStatusUpdationCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lWpResponsibilityStatusLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultCommand;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.DCMCalDataFetcher;
import com.bosch.caltool.icdm.bo.cdr.MonicaReviewInputDataValidator;
import com.bosch.caltool.icdm.bo.cdr.PIDCA2lFileInfoProvider;
import com.bosch.caltool.icdm.bo.cdr.WorkPackageStatusHandler;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.ZipUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpDefnVersion;
import com.bosch.caltool.icdm.model.a2l.A2lWpRespStatusUpdationModel;
import com.bosch.caltool.icdm.model.a2l.A2lWpResponsibilityStatus;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResultLink;
import com.bosch.caltool.icdm.model.cdr.MonicaInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaInputModel;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewFileData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewInputData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutput;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewOutputData;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusOutputInternalModel;

/**
 * @author say8cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_MONICA_REVIEW)
public class MonicaReviewService extends AbstractRestService {


  /**
   * @param multiPart files
   * @param monicaReviewInputData meta data
   * @return review result external link info
   * @throws IcdmException service error
   * @throws IOException file read error
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @CompressData
  public Response monicaReviewResponse(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_QP_MONICA_REVIEW_OBJECT) final MonicaReviewInputData monicaReviewInputData)
      throws IcdmException, IOException {

    handleReviewedBy(monicaReviewInputData);
    MonicaReviewFileData monicaReviewFileData = executeReview(multiPart, monicaReviewInputData);

    CDRReviewResultCommand reviewResultCommand = createMonicaInsertCommand(monicaReviewInputData, monicaReviewFileData);
    CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
    String cdrLinkText =
        cdrReviewResultLoader.getCDRLinkText(reviewResultCommand.getObjId(), monicaReviewInputData.getVariantId());
    String cdrLinkURL =
        cdrReviewResultLoader.getCDRLink(reviewResultCommand.getObjId(), monicaReviewInputData.getVariantId());
    // output of the service as CDR Link
    CDRReviewResultLink cdrResultLink = new CDRReviewResultLink();
    cdrResultLink.setCdrLinkUrl(cdrLinkURL);
    cdrResultLink.setResultId(reviewResultCommand.getObjId());
    cdrResultLink.setCdrLinkDisplayText(cdrLinkText);
    cdrResultLink.setMonicaReviewStatus("Completed");

    // WP finished Updation
    WorkPackageStatusHandler workPackageStatusHandler = new WorkPackageStatusHandler(getServiceData());

    TRvwResult tRvwResult = cdrReviewResultLoader.getEntityObject(reviewResultCommand.getObjId());
    WPRespStatusOutputInternalModel wpRespStatusOutputInternalModel =
        workPackageStatusHandler.calculateWorkpackageRespStatus(tRvwResult);
    PidcVariant variant = cdrReviewResultLoader.getVariant(tRvwResult);

    A2lWpResponsibilityStatusLoader a2lWPRespStatusLoader = new A2lWpResponsibilityStatusLoader(getServiceData());
    Map<Long, A2lWpResponsibilityStatus> a2lWpRespStatusMapBeforeUpd = new HashMap<>();
    List<A2lWpResponsibilityStatus> listOfA2lWPRespStatusToBeCreated = new ArrayList<>();
    for (Entry<A2lWPRespModel, String> wpRespStatusEntrySet : wpRespStatusOutputInternalModel.getWpRespStatus().entrySet()) {
      // Collecting A2lWPResponsibilityStatus Before Update into Map for CNS Refresh
      Long varId = CommonUtils.isNull(variant) || CommonUtils.isEqual(variant.getId(), ApicConstants.NO_VARIANT_ID)
          ? null : variant.getId();
      A2lWPRespModel a2lWpRespModel = wpRespStatusEntrySet.getKey();
      Long wpRespId = a2lWpRespModel.getWpRespId();
      A2lWpResponsibilityStatus a2lWpResponsibilityStatusBeforeUpd =
          a2lWPRespStatusLoader.getA2lWpStatusByVarAndWpRespId(varId, wpRespId,a2lWpRespModel.isInheritedFlag() ? null : a2lWpRespModel.getA2lRespId());

      String wpFinStatus = wpRespStatusEntrySet.getValue();
      if (CommonUtils.isNull(a2lWpResponsibilityStatusBeforeUpd)) {

        A2lWpResponsibilityStatus newA2lWPRespStatus = new A2lWpResponsibilityStatus();
        newA2lWPRespStatus.setVariantId(varId);
        newA2lWPRespStatus.setWpRespId(wpRespId);
        newA2lWPRespStatus.setWpRespFinStatus(wpFinStatus);
        //will be set for customized responsibility
        if(!a2lWpRespModel.isInheritedFlag()) {
          newA2lWPRespStatus.setA2lRespId(a2lWpRespModel.getA2lRespId());
        }
        listOfA2lWPRespStatusToBeCreated.add(newA2lWPRespStatus);
      }
      else {
        // Setting the workpackage responsible status in a2lWpResponsibility object
        a2lWpResponsibilityStatusBeforeUpd.setWpRespFinStatus(wpFinStatus);
        a2lWpRespStatusMapBeforeUpd.put(a2lWpResponsibilityStatusBeforeUpd.getId(), a2lWpResponsibilityStatusBeforeUpd);
      }

    }

    A2lWpRespStatusUpdationModel a2lWpRespStatusUpdModelBeforeUpdate = new A2lWpRespStatusUpdationModel();
    a2lWpRespStatusUpdModelBeforeUpdate.setA2lWpRespStatusToBeUpdatedMap(a2lWpRespStatusMapBeforeUpd);
    a2lWpRespStatusUpdModelBeforeUpdate.setA2lWpRespStatusListToBeCreated(listOfA2lWPRespStatusToBeCreated);

    // Update the 'WP finished Status' in T_A2L_WP_Responsibility_Status
    A2lWpRespStatusUpdationCommand a2lWPRespUpdCmd =
        new A2lWpRespStatusUpdationCommand(getServiceData(), a2lWpRespStatusUpdModelBeforeUpdate);
    executeCommand(a2lWPRespUpdCmd);

    return Response.ok(cdrResultLink).build();
  }

  /**
   * @param monicaReviewInputData
   */
  private void handleReviewedBy(final MonicaReviewInputData monicaReviewInputData) {
    monicaReviewInputData.getMonicaObject().forEach(monicaReviewData -> {
      String comment = monicaReviewData.getComment();
      StringBuilder commentConcat = new StringBuilder();
      if (CommonUtils.isNotEmptyString(comment) && !"-".equals(comment.trim())) {
        commentConcat.append(comment);
      }
      String reviewedBy = monicaReviewData.getReviewedBy();
      if (CommonUtils.isNotEmptyString(reviewedBy) && !"-".equals(reviewedBy.trim())) {
        if (commentConcat.length() > 0) {
          commentConcat.append(System.getProperty("line.separator"));
        }
        commentConcat.append("Reviewed by: " + reviewedBy);
      }
      String finalComment = commentConcat.toString();
      monicaReviewData
          .setComment(finalComment.length() > 4000 ? finalComment.substring(0, 3996) + " ..." : finalComment);
    });
  }

  /**
   * @param multiPart
   * @param monicaReviewInputData
   * @return
   * @throws InvalidInputException
   * @throws IOException
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   * @throws DataException
   * @throws CommandException
   */
  // TODO move business logic to BO layer
  private MonicaReviewFileData executeReview(final FormDataMultiPart multiPart,
      final MonicaReviewInputData monicaReviewInputData)
      throws IOException, IcdmException {
    List<FormDataBodyPart> dcmFileInput = validateDCMFile(multiPart);
    Map<String, CalData> dcmCalDataMap = new HashMap<>();
    String dcmFileName = "";
    InputStream dcmInputStream = null;

    byte[] byteArray = null;

    byte[] dcmByteArray = null;
    // get the dcm Stream
    for (FormDataBodyPart field : dcmFileInput) {
      InputStream unzipIfZippedStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
      dcmInputStream = field.getValueAs(InputStream.class);
      dcmInputStream.available();
      dcmByteArray = IOUtils.toByteArray(field.getValueAs(InputStream.class));
      dcmFileName = getUTFFilePath(field.getContentDisposition().getFileName());

      byteArray = addFileBytesToMap(unzipIfZippedStream);
      DCMCalDataFetcher dataFetcher = new DCMCalDataFetcher();
      dcmCalDataMap = dataFetcher.fectchCalData(byteArray);

      getLogger().info("DCM Name" + dcmFileName);
    }


    List<FormDataBodyPart> monicaFileInput = validateMonicaFile(multiPart);

    StringBuilder moniCaFileName = new StringBuilder();
    byte[] monicaByteArray = null;
    // get the MoniCa Stream
    for (FormDataBodyPart field : monicaFileInput) {

      moniCaFileName.append(monicaReviewInputData.getSelMoniCaSheet());
      moniCaFileName.append(CDRConstants.FILENAME_SEPERATOR);
      moniCaFileName.append(getUTFFilePath(field.getContentDisposition().getFileName()));

      monicaByteArray = IOUtils.toByteArray(field.getValueAs(InputStream.class));

      getLogger().info("MoniCa File Name" + moniCaFileName);
    }

    return createExternalMonicaReviewFileData(monicaReviewInputData, dcmCalDataMap, dcmFileName, dcmByteArray,
        moniCaFileName, monicaByteArray);

  }

  /**
   * @param monicaReviewInputData
   * @param dcmCalDataMap
   * @param dcmFileName
   * @param dcmByteArray
   * @param moniCaFileName
   * @param monicaByteArray
   * @return
   * @throws IcdmException
   */
  public MonicaReviewFileData createExternalMonicaReviewFileData(final MonicaReviewInputData monicaReviewInputData,
      final Map<String, CalData> dcmCalDataMap, final String dcmFileName, final byte[] dcmByteArray,
      final StringBuilder moniCaFileName, final byte[] monicaByteArray)
      throws IcdmException {
    // Added to validate the given pidc A2l id is valid
    // Throws datanotfound if the id is invalid
    new PidcA2lLoader(getServiceData()).validateId(monicaReviewInputData.getPidcA2lId());
    // Logic to check and create active version for the pidc a2l for the first time
    Map<Long, A2lWpDefnVersion> exisitngWpDefMap = new A2lWpDefnVersionLoader(getServiceData())
        .getWPDefnVersionsForPidcA2lId(monicaReviewInputData.getPidcA2lId());

    A2lWpDefnVersion activeWpDefVers = getActiveWPDefnVers(exisitngWpDefMap, monicaReviewInputData.getPidcA2lId());

    MonicaReviewInputDataValidator inputDataValidator = new MonicaReviewInputDataValidator(monicaReviewInputData,
        dcmFileName, moniCaFileName.toString(), monicaByteArray, dcmByteArray, getServiceData(), dcmCalDataMap);
    inputDataValidator.validate();

    MonicaReviewFileData monicaReviewFileData = inputDataValidator.getMonicaReviewFileData(monicaReviewInputData,
        dcmCalDataMap, dcmFileName, dcmByteArray, moniCaFileName, monicaByteArray);
    monicaReviewFileData.setA2lWpDefVersId(activeWpDefVers != null ? activeWpDefVers.getId() : null);

    setWarningMsg(inputDataValidator, monicaReviewFileData);
    return monicaReviewFileData;
  }

  /**
   * @param inputDataValidator
   * @param monicaReviewFileData
   */
  private void setWarningMsg(final MonicaReviewInputDataValidator inputDataValidator,
      final MonicaReviewFileData monicaReviewFileData) {
    StringBuilder warnMsg = new StringBuilder();
    // set the missing labels
    if (inputDataValidator.getLabelsMissingInDcm() != null) {
      warnMsg.append("These labels are existing only in Excel file : ")
          .append(inputDataValidator.getLabelsMissingInDcm());
    }
    if (inputDataValidator.getLabelsMissingInExcel() != null) {
      if (warnMsg.length() > 0) {
        warnMsg.append("\n");
      }
      warnMsg.append("These labels are existing only in DCM file : ")
          .append(inputDataValidator.getLabelsMissingInExcel());
    }
    monicaReviewFileData.setWarnMsg(warnMsg.toString());
  }


  /**
   * @param multiPart file data
   * @param MonicaInputModel input meta data
   * @return review result data
   * @throws IcdmException service error
   * @throws IOException file read error
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @Path(WsCommonConstants.RWS_EXECUTE_REVIEW)
  @CompressData
  public Response executeMonicaReview(final FormDataMultiPart multiPart,
      @FormDataParam(WsCommonConstants.RWS_QP_MONICA_REVIEW_OBJECT) final MonicaInputModel monicaInputModel)
      throws IcdmException, IOException {

    MonicaReviewOutput monicaReviewOutput = new MonicaReviewOutput();
    List<MonicaReviewOutputData> monicaReviewOutputDataList = new ArrayList<>();


    handleReviewedInternal(monicaInputModel);

    for (MonicaInputData monicaInputData : monicaInputModel.getMonicaInputDataList()) {
      MonicaReviewOutputData monicaReviewOutputData = new MonicaReviewOutputData();
      MonicaReviewFileData monicaReviewFileData = null;
      CDRReviewResultCommand reviewResultCommand = null;
      try {
        monicaReviewFileData = executeReviewInternal(multiPart, monicaInputData, monicaInputModel);
        reviewResultCommand =
            createMonicaInsertInternalCommand(monicaInputModel, monicaInputData, monicaReviewFileData);
      }
      catch (Exception e) {
        getLogger().error(e.getLocalizedMessage(), e);
        monicaReviewOutputData.setReviewFailed(true);
        monicaReviewOutputData.setErrorMsg(e.getLocalizedMessage());
        monicaReviewOutputData.setMonicaInputData(monicaInputData);
        monicaReviewOutputDataList.add(monicaReviewOutputData);
        continue;
      }
      monicaReviewOutputData.setMonicaInputData(monicaInputData);
      CDRReviewResultLoader cdrReviewResultLoader = new CDRReviewResultLoader(getServiceData());
      monicaReviewOutputData.setRvwVariant(reviewResultCommand.getRvwVariant());
      monicaReviewOutputData
          .setCdrReviewResult(cdrReviewResultLoader.getDataObjectByID(reviewResultCommand.getObjId()));
      // output of the service as CDR Link
      monicaReviewOutputData.setWarningMsg(monicaReviewFileData.getWarnMsg());
      monicaReviewOutputData.setReviewFailed(false);


      A2lWpRespStatusUpdationModel responseUpdationModel = new A2lWpResponsibilityStatusLoader(getServiceData())
          .getOutputUpdationModel(reviewResultCommand.getListOfNewlyCreatedA2lWpRespStatus(),
              reviewResultCommand.getA2lWpRespStatusBeforeUpd(), reviewResultCommand.getA2lWpRespStatusAfterUpd());

      monicaReviewOutputData.setA2lWpRespStatusBeforeUpd(responseUpdationModel.getA2lWpRespStatusToBeUpdatedMap());
      monicaReviewOutputData.setA2lWpRespStatusAfterUpd(responseUpdationModel.getA2lWpRespStatusMapAfterUpdate());
      monicaReviewOutputData
          .setListOfNewlyCreatedA2lWpRespStatus(responseUpdationModel.getListOfNewlyCreatedA2lWpRespStatus());
      monicaReviewOutputDataList.add(monicaReviewOutputData);

    }
    monicaReviewOutput.setMonicaReviewOutputDataList(monicaReviewOutputDataList);
    return Response.ok(monicaReviewOutput).build();
  }

  /**
   * @param monicaReviewInputData
   */
  private void handleReviewedInternal(final MonicaInputModel monicaInputModel) {
    monicaInputModel.getMonicaInputDataList()
        .forEach(monicaInputData -> monicaInputData.getMonicaObject().forEach(monicaReviewData -> {
          StringBuilder commentConcat = new StringBuilder();
          if (CommonUtils.isNotEmptyString(monicaReviewData.getComment()) &&
              !"-".equals(monicaReviewData.getComment().trim())) {
            commentConcat.append(monicaReviewData.getComment());
          }
          if (CommonUtils.isNotEmptyString(monicaReviewData.getReviewedBy()) &&
              !"-".equals(monicaReviewData.getReviewedBy().trim())) {
            if (commentConcat.length() > 0) {
              commentConcat.append(System.getProperty("line.separator"));
            }
            commentConcat.append("Reviewed by: " + monicaReviewData.getReviewedBy());
          }
          String finalComment = commentConcat.toString();
          monicaReviewData
              .setComment(finalComment.length() > 4000 ? finalComment.substring(0, 3996) + " ..." : finalComment);
        }));

  }


  private MonicaReviewFileData executeReviewInternal(final FormDataMultiPart multiPart,
      final MonicaInputData monicaInputData, final MonicaInputModel monicaInputModel)
      throws IOException, IcdmException {
    List<FormDataBodyPart> dcmFileInput = validateDCMFile(multiPart);
    Map<String, CalData> dcmCalDataMap = new HashMap<>();
    String dcmFileName = "";
    InputStream dcmInputStream = null;

    byte[] byteArray = null;

    byte[] dcmByteArray = null;
    // get the dcm Stream
    for (FormDataBodyPart field : dcmFileInput) {
      if (monicaInputData.getDcmFileName().equals(getUTFFilePath(field.getContentDisposition().getFileName()))) {
        InputStream unzipIfZippedStream = ZipUtils.unzipIfZipped(field.getValueAs(InputStream.class));
        dcmInputStream = field.getValueAs(InputStream.class);
        dcmInputStream.available();
        dcmByteArray = IOUtils.toByteArray(field.getValueAs(InputStream.class));
        dcmFileName = getUTFFilePath(field.getContentDisposition().getFileName());

        byteArray = addFileBytesToMap(unzipIfZippedStream);
        DCMCalDataFetcher dataFetcher = new DCMCalDataFetcher();
        dcmCalDataMap = dataFetcher.fectchCalData(byteArray);

        getLogger().info("DCM Name" + dcmFileName);
        break;
      }
    }


    List<FormDataBodyPart> monicaFileInput = validateMonicaFile(multiPart);

    StringBuilder moniCaFileName = new StringBuilder();
    byte[] monicaByteArray = null;
    // get the MoniCa Stream
    for (FormDataBodyPart field : monicaFileInput) {
      if (monicaInputData.getMonicaExcelFileName()
          .equals(getUTFFilePath(field.getContentDisposition().getFileName()))) {
        moniCaFileName.append(monicaInputData.getSelMoniCaSheet());
        moniCaFileName.append(CDRConstants.FILENAME_SEPERATOR);
        moniCaFileName.append(getUTFFilePath(field.getContentDisposition().getFileName()));
        monicaByteArray = IOUtils.toByteArray(field.getValueAs(InputStream.class));
        getLogger().info("MoniCa File Name" + moniCaFileName);
        break;
      }
    }

    // Added to validate the given pidc A2l id is valid
    // Throws datanotfound if the id is invalid
    new PidcA2lLoader(getServiceData()).validateId(monicaInputModel.getPidcA2lId());
    // Logic to check and create active version for the pidc a2l for the first time
    Map<Long, A2lWpDefnVersion> exisitngWpDefMap =
        new A2lWpDefnVersionLoader(getServiceData()).getWPDefnVersionsForPidcA2lId(monicaInputModel.getPidcA2lId());


    A2lWpDefnVersion activeWpDefVers = getActiveWPDefnVers(exisitngWpDefMap, monicaInputModel.getPidcA2lId());


    MonicaReviewInputDataValidator inputDataValidator =
        new MonicaReviewInputDataValidator(monicaInputData, monicaInputModel, dcmFileName, moniCaFileName.toString(),
            monicaByteArray, dcmByteArray, getServiceData(), dcmCalDataMap);
    inputDataValidator.validate();

    MonicaReviewFileData monicaReviewFileData = inputDataValidator.getMonicaReviewFileDataInternal(monicaInputData,
        monicaInputModel, dcmCalDataMap, dcmFileName, dcmByteArray, moniCaFileName, monicaByteArray);
    monicaReviewFileData.setA2lWpDefVersId(activeWpDefVers != null ? activeWpDefVers.getId() : null);

    setWarningMsg(inputDataValidator, monicaReviewFileData);

    return monicaReviewFileData;
  }

  /**
   * @param exisitngWpDefMap
   * @param pidcA2lId
   * @param activeWpDefVers
   * @return
   * @throws InvalidInputException
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   */
  private A2lWpDefnVersion getActiveWPDefnVers(final Map<Long, A2lWpDefnVersion> exisitngWpDefMap, final Long pidcA2lId)
      throws IcdmException {

    A2lWpDefnVersion activeWpDefVers = null;
    if (!exisitngWpDefMap.isEmpty()) {
      activeWpDefVers = getActiveVersFromExistWpDefnVersMap(exisitngWpDefMap, activeWpDefVers);
    }
    else {
      // Creating working set and active version for the PIDC A2l
      A2lWpDefnVersion a2lWpDefnVersion = new A2lWpDefnVersion();
      a2lWpDefnVersion.setVersionName(ApicConstants.WORKING_SET_NAME);
      a2lWpDefnVersion.setActive(false);
      a2lWpDefnVersion.setWorkingSet(true);
      a2lWpDefnVersion.setParamLevelChgAllowedFlag(false);
      a2lWpDefnVersion.setPidcA2lId(pidcA2lId);

      A2lWpDefaultDefinitionCommand cmd =
          new A2lWpDefaultDefinitionCommand(getServiceData(), a2lWpDefnVersion, false, true);
      executeCommand(cmd);
      activeWpDefVers = cmd.getNewData();
    }
    return activeWpDefVers;
  }

  /**
   * @param exisitngWpDefMap
   * @param activeWpDefVers
   * @param isActive
   * @return
   * @throws InvalidInputException
   */
  private A2lWpDefnVersion getActiveVersFromExistWpDefnVersMap(final Map<Long, A2lWpDefnVersion> exisitngWpDefMap,
      A2lWpDefnVersion activeWpDefVers)
      throws InvalidInputException {

    boolean isActive = false;
    for (A2lWpDefnVersion wpDefVers : exisitngWpDefMap.values()) {
      if (wpDefVers.isActive()) {
        isActive = true;
        activeWpDefVers = wpDefVers;
      }
    }
    if (!isActive) {
      throw new InvalidInputException(
          "Active version is not available for the given Pidc A2L. Please set an active version to continue!");
    }
    return activeWpDefVers;
  }

  /**
   * @param multiPart
   * @return
   * @throws InvalidInputException
   */
  private List<FormDataBodyPart> validateDCMFile(final FormDataMultiPart multiPart) throws InvalidInputException {
    // get the dcm file
    List<FormDataBodyPart> dcmFileInput = multiPart.getFields(WsCommonConstants.DCM_FILE_MULTIPART);

    if (CommonUtils.isNullOrEmpty(dcmFileInput)) {
      throw new InvalidInputException("Dcm file is mandatory");
    }
    return dcmFileInput;
  }

  /**
   * @param multiPart
   * @return
   * @throws InvalidInputException
   */
  private List<FormDataBodyPart> validateMonicaFile(final FormDataMultiPart multiPart) throws InvalidInputException {
    // get the MoniCa file
    List<FormDataBodyPart> monicaFileInput = multiPart.getFields(WsCommonConstants.MONICA_FILE_MULTIPART);
    if (CommonUtils.isNullOrEmpty(monicaFileInput)) {
      throw new InvalidInputException("MoniCa file is mandatory");
    }
    return monicaFileInput;
  }


  /**
   * @param filesStreamMap
   * @throws IcdmException
   */
  private byte[] addFileBytesToMap(final InputStream unzipIfZippedStream) throws IcdmException {
    try {
      return IOUtils.toByteArray(unzipIfZippedStream);
    }
    catch (IOException exp) {
      throw new IcdmException(exp.getLocalizedMessage(), exp);
    }
  }


  /**
   * @param monicaReviewInputData
   * @param monicaReviewFileData
   * @return
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   */
  private CDRReviewResultCommand createMonicaInsertCommand(final MonicaReviewInputData monicaReviewInputData,
      final MonicaReviewFileData monicaReviewFileData)
      throws IcdmException {

    CDRReviewResult cdrReviewResult = new CDRReviewResult();
    cdrReviewResult.setPidcA2lId(monicaReviewInputData.getPidcA2lId());
    cdrReviewResult.setDescription(monicaReviewInputData.getDescription());
    cdrReviewResult.setLockStatus(CDRConstants.REVIEW_LOCK_STATUS.YES.getDbType());
    cdrReviewResult.setReviewType(CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType());
    cdrReviewResult.setRvwStatus(CDRConstants.REVIEW_STATUS.CLOSED.getDbType());
    cdrReviewResult.setSourceType(CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE.getDbType());
    cdrReviewResult.setGrpWorkPkg(CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE.getUIType());
    cdrReviewResult.setDeltaReviewType(monicaReviewInputData.getDeltaReviewType());
    cdrReviewResult.setOrgResultId(monicaReviewInputData.getOrgResultId());
    cdrReviewResult.setWpDefnVersId(monicaReviewFileData.getA2lWpDefVersId());
    A2LFileInfo a2lFileInfo =
        new PIDCA2lFileInfoProvider(getServiceData(), monicaReviewInputData.getPidcA2lId()).getA2lFileInfo();
    CDRReviewResultCommand reviewResultCommand =
        new CDRReviewResultCommand(getServiceData(), cdrReviewResult, monicaReviewFileData, false, false);
    reviewResultCommand.setA2lFileInfo(a2lFileInfo);

    executeCommand(reviewResultCommand);
    return reviewResultCommand;
  }


  private CDRReviewResultCommand createMonicaInsertInternalCommand(final MonicaInputModel monicaInputModel,
      final MonicaInputData monicaInputData, final MonicaReviewFileData monicaReviewFileData)
      throws IcdmException {

    CDRReviewResult cdrReviewResult = new CDRReviewResult();
    cdrReviewResult.setPidcA2lId(monicaInputModel.getPidcA2lId());
    cdrReviewResult.setDescription(monicaInputModel.getDescription());
    cdrReviewResult.setLockStatus(CDRConstants.REVIEW_LOCK_STATUS.YES.getDbType());
    cdrReviewResult.setReviewType(CDRConstants.REVIEW_TYPE.OFFICIAL.getDbType());
    cdrReviewResult.setRvwStatus(CDRConstants.REVIEW_STATUS.CLOSED.getDbType());
    cdrReviewResult.setSourceType(CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE.getDbType());
    cdrReviewResult.setGrpWorkPkg(CDRConstants.CDR_SOURCE_TYPE.MONICA_FILE.getUIType());
    cdrReviewResult.setDeltaReviewType(monicaInputData.getDeltaReviewType());
    cdrReviewResult.setOrgResultId(monicaInputData.getOrgResultId());
    cdrReviewResult.setWpDefnVersId(monicaReviewFileData.getA2lWpDefVersId());
    A2LFileInfo a2lFileInfo =
        new PIDCA2lFileInfoProvider(getServiceData(), monicaInputModel.getPidcA2lId()).getA2lFileInfo();
    CDRReviewResultCommand reviewResultCommand =
        new CDRReviewResultCommand(getServiceData(), cdrReviewResult, monicaReviewFileData, false, false);
    reviewResultCommand.setA2lFileInfo(a2lFileInfo);

    executeCommand(reviewResultCommand);
    return reviewResultCommand;
  }


}
