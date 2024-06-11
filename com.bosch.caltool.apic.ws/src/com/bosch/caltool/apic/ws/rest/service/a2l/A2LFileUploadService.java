/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.a2l;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import com.bosch.calcomp.a2lloader.A2LLoader;
import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.IcdmBoConstants;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoFetcher;
import com.bosch.caltool.icdm.bo.a2l.A2LFileInfoLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lFileUploadCommand;
import com.bosch.caltool.icdm.bo.a2l.A2lFileUploader;
import com.bosch.caltool.icdm.bo.a2l.PidcA2lCommand;
import com.bosch.caltool.icdm.bo.apic.SdomPverLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.PasswordServiceWrapper;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.logger.ICDMLoggerConstants;
import com.bosch.caltool.icdm.logger.WSLogger;
import com.bosch.caltool.icdm.model.a2l.A2LFile;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.security.Decryptor;
import com.google.common.io.Files;

/**
 * Rest service to upload an A2L to iCDM, vCDM
 *
 * @author gge6cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_A2L + "/" + WsCommonConstants.RWS_FILE_UPLOAD)
public class A2LFileUploadService extends AbstractRestService {

  /**
   * Temp/Work dir for A2L Loader
   */
  private static final String A2L_LDR_WORK_DIR_NAME = "//A2L_LOADER//";


  /**
   * Upload A2L file.
   *
   * @param multiPart the multi part
   * @return the response
   * @throws IcdmException the icdm exception
   */
  @POST
  @Consumes({ MediaType.MULTIPART_FORM_DATA })
  @CompressData
  public Response uploadA2LFile(final FormDataMultiPart multiPart) throws IcdmException {
    getLogger().debug("A2L File upload process started...");

    // get the a2l file
    List<FormDataBodyPart> a2lFileInput = multiPart.getFields(WsCommonConstants.A2L_FILE_MULTIPART);
    if (CommonUtils.isNullOrEmpty(a2lFileInput)) {
      throw new InvalidInputException("A2L file is mandatory");
    }

    // PidcVersionId
    FormDataBodyPart pidcVersionIdPart = multiPart.getField(WsCommonConstants.RWS_QP_PIDC_VERS_ID);
    String pidcVersIdStr = pidcVersionIdPart.getValue();
    long pidcVersionId = Long.parseLong(pidcVersIdStr);

    // validate pidc version ID
    validateInput(pidcVersionId);

    InputStream a2lInputStream = null;
    String a2lFileName = null;

    // get the A2l Stream
    for (FormDataBodyPart field : a2lFileInput) {
      a2lInputStream = field.getValueAs(InputStream.class);
      a2lFileName = field.getContentDisposition().getFileName();
      getLogger().info("A2L input - {}, file name - {}", a2lInputStream.toString(), a2lFileName);
    }

    /* STEP-1 */
    // validate the file, check if input is valid a2l
    getLogger().debug("STEP-1: Validating A2L file...");
    File targetA2lFile = validateAndSaveA2lFile(a2lFileName, a2lInputStream);
    if (!targetA2lFile.exists()) {
      String errorMsg = "Error saving A2L file from filepath: " + a2lFileName;
      getLogger().error(errorMsg);
      // throw exception to cancel the job
      throw new IcdmException(errorMsg);
    }

    /* STEP-2 */
    // Check if the file already exists and UPLOAD the file
    getLogger().debug("STEP-2: Uploading the A2L file to vCDM...");
    A2lFileUploader a2lUploader = new A2lFileUploader(getServiceData());
    String a2lVersion = a2lUploader.uploadA2LToVcdm(targetA2lFile.getPath());
    if (CommonUtils.isEmptyString(a2lVersion)) {
      getLogger().error(CommonUtilConstants.VCDM_CONN_SERVER_FAILURE);
    }

    /* STEP-3 */
    // Load A2L details to database using A2LLoader
    getLogger().debug("STEP-3: Saving A2L contents to database using A2LLoader...");
    Long a2lFileId = loadVcdmFileViaA2lLoader(String.valueOf(a2lVersion));

    /* STEP-4 */
    // Check the a2l file exists for the same pvername/variant/revision
    getLogger().debug("STEP-4: Check whether the same A2L file exists...");
    // details for A2L file from TA2L_FileInfo
    A2LFile a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lFileId);

    // Sdom PVer
    FormDataBodyPart sdomPverPart = multiPart.getField(WsCommonConstants.RWS_QP_SDOM_PVER_NAME);
    String sdomPverName = sdomPverPart.getValue();

    FormDataBodyPart pverVariantPart = multiPart.getField(WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT);
    String pverVariant = pverVariantPart.getValue();

    FormDataBodyPart pverVarRevPart = multiPart.getField(WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT_REV);
    String pverVariantRevIdString = pverVarRevPart.getValue();
    long pverVarRevision = Long.parseLong(pverVariantRevIdString);

    a2lUploader.checkA2lExists(a2lFile, sdomPverName, pverVariant, pverVarRevision);

    /* STEP-5 */
    // Check the a2l file exists for the particular pidc version
    getLogger().debug("STEP-5: Check the A2L file exists for the particular pidc version...");
    checkA2lInPidcVer(pidcVersionId, a2lFileId);

    /* STEP-6 */
    // only if pver exists in MvSDOMPver table , SDOM and BC details should be saved (i.e details need not be saved
    // for non-SDOM project)
    boolean canSavePver = new SdomPverLoader(getServiceData()).checkPverNameExists(sdomPverName);
    getLogger().debug("STEP-6: Starting to save SDOM details for A2L file id : {}", a2lFile.getId());
    if ((a2lFile.getSdomPverName() == null) && (a2lFile.getSdomPverVariant() == null) &&
        (a2lFile.getSdomPverRevision() == null)) {
      // no SDOM information exists
      // save the SDOM information
      a2lFile.setSdomPverName(sdomPverName);
      a2lFile.setSdomPverVariant(pverVariant);
      a2lFile.setSdomPverRevision(pverVarRevision);
      a2lFile.setVcdmA2lfileId(Long.parseLong(a2lVersion));
    }


    /* STEP-7 */
    // add BC details & SdomPVer to the A2L in the database
    getLogger().debug("STEP-7: Check the A2L file exists for the particular pidc version...");
    A2lFileUploadCommand cmd = new A2lFileUploadCommand(getServiceData(), a2lFile, canSavePver);
    executeCommand(cmd);

    /* STEP-8 */
    // assign a2l to pidc version
    getLogger().debug("STEP-8: Create/Update PidcA2l for given A2L file {}", a2lFile.getId());
    PidcA2l pidcA2l = new PidcA2l();
    pidcA2l.setA2lFileId(a2lFileId);
    pidcA2l.setPidcVersId(pidcVersionId);
    pidcA2l.setSdomPverName(sdomPverName);
    pidcA2l.setSdomPverRevision(pverVarRevision);
    pidcA2l.setVcdmA2lName(a2lFileName);
    pidcA2l.setActive(true);
    pidcA2l.setProjectId(new PidcVersionLoader(getServiceData()).getDataObjectByID(pidcVersionId).getPidcId());
    PidcA2l mappedPidcA2l = assignA2lToPidcVersion(pidcA2l);

    // Refresh entity for database updates
    getLogger().debug("Fetching new A2L model");
    a2lFile = new A2LFileInfoLoader(getServiceData()).getDataObjectByID(a2lFile.getId());

    getLogger().debug(
        "A2L contents loaded successfully for vcdm A2L file id: {}, with A2L file id as: {}, with SDOM Pver: {}, SDOM Variant: {}",
        a2lFile.getVcdmA2lfileId(), a2lFile.getId(), a2lFile.getSdomPverName(), a2lFile.getSdomPverVariant());

    return Response.ok(mappedPidcA2l).build();
  }


  /**
   * @param pidcVersionId pidc version ID
   * @throws IcdmException when validation fails
   */
  private void validateInput(final Long pidcVersionId) throws IcdmException {
    new PidcVersionLoader(getServiceData()).validateId(pidcVersionId);
  }


  // Check whether a2l is available for particular pidc version
  /**
   * @param pidcVersionId pidcVersion id
   * @param a2lFileId a2l file id
   * @throws UnAuthorizedAccessException Exception
   * @throws DataException Exception
   * @throws IcdmException Exception
   */
  public void checkA2lInPidcVer(final long pidcVersionId, final Long a2lFileId) throws IcdmException {
    String a2lExistingInVers = new PidcA2lLoader(getServiceData()).hasA2lPidcVerMapping(pidcVersionId, a2lFileId);
    if (a2lExistingInVers != null) {
      String errorMsg = "The A2L file is already assigned to the pidc version - " + a2lExistingInVers + "!";
      getLogger().error(errorMsg);
      throw new IcdmException(errorMsg);
    }
  }

  /**
   * Assign A2L to pidc version.
   *
   * @param pidcA2l the pidc A2L
   * @return
   * @throws IcdmException the icdm exception
   */
  private PidcA2l assignA2lToPidcVersion(final PidcA2l newPidcA2l) throws IcdmException {
    PidcA2lLoader pidcA2lLoader = new PidcA2lLoader(getServiceData());
    PidcA2l pidcA2lInDb = pidcA2lLoader.getPidcA2l(newPidcA2l.getA2lFileId(), newPidcA2l.getProjectId());

    PidcA2l mappedPidcA2l = null;

    // a2l file is already available in the pid card and is to be mapped to a version
    if (CommonUtils.isNotNull(pidcA2lInDb)) {
      // Update Pidc Version in Pidc A2l available for given project id card
      pidcA2lInDb.setPidcVersId(newPidcA2l.getPidcVersId());
      pidcA2lInDb.setActive(true);
      PidcA2lCommand pidcA2lUpdateCmd = new PidcA2lCommand(getServiceData(), pidcA2lInDb, true, false, false);
      executeCommand(pidcA2lUpdateCmd);
      mappedPidcA2l = pidcA2lUpdateCmd.getNewData();

      getLogger().debug("PidcA2l already exists, hence mapped to given pidcVersion. pidcA2lId : {}",
          pidcA2lInDb.getId());

    }
    else {
      // Create PidcA2l record if a2l is not available in pidc
      PidcA2lCommand pidcA2lCreateCmd = new PidcA2lCommand(getServiceData(), newPidcA2l);
      executeCommand(pidcA2lCreateCmd);
      mappedPidcA2l = pidcA2lCreateCmd.getNewData();

      getLogger().debug("New PidcA2l entry created and mapped to given pidcVersion. pidcA2lId : {}",
          mappedPidcA2l.getId());
    }

    return mappedPidcA2l;
  }


  /**
   * @param a2lFileName
   * @param a2lInputStream
   * @return File
   * @throws IcdmException
   */
  private File validateAndSaveA2lFile(final String a2lFileName, final InputStream a2lInputStream) throws IcdmException {
    File targetA2lFile = new File(System.getenv("TEMP") + File.separator + a2lFileName);
    byte[] byteArray;
    try {
      byteArray = IOUtils.toByteArray(a2lInputStream);
      if ((a2lInputStream != null) && (byteArray.length > 0)) {
        Files.write(byteArray, targetA2lFile);
      }
    }
    catch (IOException e) {
      throw new IcdmException(e.getMessage(), e);
    }

    if (A2LFileInfoFetcher.INSTANCE.getA2lFileInfo(byteArray, getLogger()) == null) {
      String strErr = "Invalid a2l file uploaded : " + a2lFileName;
      getLogger().error(strErr);
      throw new IcdmException(strErr);
    }

    return targetA2lFile;
  }

  /**
   * @param vcdmVersionNumber
   * @param httpRequest
   * @return
   * @throws IcdmException
   */
  private Long loadVcdmFileViaA2lLoader(final String vcdmVersionNumber) throws IcdmException {
    // vCDM Login Information
    String vcdmUser = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_NAME);

    String passwordKey = Messages.getString(CommonUtilConstants.EASEE_SERVICE_USER_PASS);
    final PasswordServiceWrapper passWordWrapper = new PasswordServiceWrapper(getLogger());
    String vcdmPasswort = Decryptor.getInstance().decrypt(passWordWrapper.getPassword(passwordKey), getLogger());

    String vcdmDomain = Messages.getString(CommonUtilConstants.EASEE_SERVICE_DOMAIN_NAME);
    String vcdmServerString = Messages.getString(CommonUtilConstants.EASEE_SERVICE_WS_SERVER);

    // Villa Login Information
    String villaUser = Messages.getString(CommonUtilConstants.VILLA_DB_USER);

    String villaPasswort =
        Decryptor.getInstance().decrypt(Messages.getString(CommonUtilConstants.VILLA_DB_USER_PASS), getLogger());

    String villaURL = Messages.getString(CommonUtilConstants.VILLA_DB_URL);

    int vcdmServer = 0;

    // Store the int value for the vCDM server
    if ("DGS_CDM_PRO".equals(vcdmServerString)) {
      vcdmServer = 1;
    }
    else if ("DGS_CDM_QUA".equals(vcdmServerString)) {
      vcdmServer = 2;
    }

    ILoggerAdapter loaderLggr = WSLogger.getLogger(ICDMLoggerConstants.A2L_LDR_LOGGER);

    try {
      A2LLoader a2lLoader = new A2LLoader(villaUser, villaPasswort, villaURL, getA2LLoaderWrkDir(), loaderLggr,
          vcdmUser, vcdmPasswort, vcdmDomain, vcdmServer);
      return a2lLoader.loadVcdmFile(vcdmVersionNumber);
    }
    catch (IllegalArgumentException e) {
      throw new IcdmException("Error occurred from A2L Loader - " + e.getMessage(), e);
    }
  }

  /**
   * Gives the work directory path for A2L Loader
   *
   * @return
   */
  private String getA2LLoaderWrkDir() {
    String serverTempDir = Messages.getString(IcdmBoConstants.PROPKEY_SERVICE_WORK_DIR) + A2L_LDR_WORK_DIR_NAME;

    if (!new File(serverTempDir).exists()) {
      new File(serverTempDir).mkdir();
    }
    return serverTempDir;
  }
}
