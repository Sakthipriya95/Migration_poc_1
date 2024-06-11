/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.io.FilenameUtils;

import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.a2l.A2lResponsibilityLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWorkPackageLoader;
import com.bosch.caltool.icdm.bo.a2l.A2lWpDefnVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcA2lLoader;
import com.bosch.caltool.icdm.bo.cdr.CDRReviewResultLoader;
import com.bosch.caltool.icdm.bo.cdr.WpArchivalBO;
import com.bosch.caltool.icdm.bo.cdr.WpArchivalCommand;
import com.bosch.caltool.icdm.bo.cdr.WpArchivalUtil;
import com.bosch.caltool.icdm.bo.cdr.WpFilesCommand;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWorkPackage;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpDefnVersion;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwResult;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2lWPRespModel;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.FILE_ARCHIVAL_STATUS;
import com.bosch.caltool.icdm.model.cdr.WPRespStatusMsgWrapper;
import com.bosch.caltool.icdm.model.cdr.WpArchival;
import com.bosch.caltool.icdm.model.cdr.WpArchivalCommonModel;
import com.bosch.caltool.icdm.model.cdr.WpFiles;
import com.bosch.caltool.icdm.model.general.LoggingContext;


/**
 * Service class for CDRReviewResult
 *
 * @author EKIR1KOR
 */
public class WpArchivalRunnableService extends AbstractRestService implements Runnable {


  private final A2lWPRespModel a2lWpRespModel;
  private final ServiceData serviceData;
  private final List<Long> rvwResultIdList;
  private final ConcurrentMap<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap;
  private final ConcurrentMap<A2lWPRespModel, WPRespStatusMsgWrapper> failedWPRespMap;
  WpArchivalCommonModel wpArchivalModel;
  private final LoggingContext loggingContext;


  /**
   * @param a2lWpRespModel
   * @param serviceData
   * @param completedWPRespMap
   * @param failedWPRespMap
   * @param rvwResultIdList
   * @param wpArchivalModel
   * @param loggingContext
   */
  public WpArchivalRunnableService(final A2lWPRespModel a2lWpRespModel, final ServiceData serviceData,
      final ConcurrentMap<A2lWPRespModel, WPRespStatusMsgWrapper> completedWPRespMap,
      final ConcurrentMap<A2lWPRespModel, WPRespStatusMsgWrapper> failedWPRespMap, final List<Long> rvwResultIdList,
      final WpArchivalCommonModel wpArchivalModel, final LoggingContext loggingContext) {
    this.serviceData = serviceData;
    this.a2lWpRespModel = a2lWpRespModel;
    this.completedWPRespMap = completedWPRespMap;
    this.failedWPRespMap = failedWPRespMap;
    this.rvwResultIdList = rvwResultIdList;
    this.wpArchivalModel = wpArchivalModel;
    this.loggingContext = loggingContext;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    CommonUtils.setLogger(this.loggingContext.getRequestId(), this.loggingContext.getMethod());
    try {
      // create a WP Archive for each Wp/Resp combination
      createWPArchives();
    }
    catch (IOException | IcdmException e) {
      getLogger().error("An exception occured during creation of wp archival files" + e.getMessage());
    }
  }

  /**
   * Create WP folder and archive it
   */
  private void createWPArchives() throws IcdmException, IOException {
    getLogger().debug("Creation of archives for WP Resp with ID {} started.", this.a2lWpRespModel.getWpRespId());
    // create a baseline in table for the WP archive and get the WPArchival ID
    WpArchival newCreatedWpArchive = createWPArchiveBaseline();
    WpArchivalBO wpArchivalBO = new WpArchivalBO(this.serviceData, newCreatedWpArchive, this.rvwResultIdList);
    byte[] wpArchivalFiles = null;
    try {
      // create all the folder structure and return the baseline files
      wpArchivalFiles = wpArchivalBO.createBaselineFiles();
      // set the status for the Archival Status in the DB
      if (CommonUtils.isNotNull(wpArchivalFiles)) {
        newCreatedWpArchive.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.COMPLETED.getDbType());
        this.completedWPRespMap.put(this.a2lWpRespModel,
            getStatusMsgWrapper(newCreatedWpArchive, newCreatedWpArchive.getFileArchivalStatus()));
      }
      else {
        newCreatedWpArchive.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.FAILED.getDbType());
        this.failedWPRespMap.put(this.a2lWpRespModel,
            getStatusMsgWrapper(newCreatedWpArchive, newCreatedWpArchive.getFileArchivalStatus()));
      }
    }
    catch (IcdmException excep) {
      newCreatedWpArchive.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.FAILED.getDbType());
      this.failedWPRespMap.put(this.a2lWpRespModel,
          getStatusMsgWrapper(newCreatedWpArchive, newCreatedWpArchive.getFileArchivalStatus()));
      getLogger().error("An exception occured during creation of baseline files" + excep.getMessage());
    }

    getLogger().debug("Creation of Wp Archive files for WPArchival with ID: {} completed", newCreatedWpArchive.getId());
    CDMLogger.getInstance().info("Creation of Wp Archive files for WPArchival with ID: {} completed",
        newCreatedWpArchive.getId());
    getLogger().debug("Updating the status and file data into archival tables");

    // update the archival file status
    WpArchivalCommand wpArchivalCommand = new WpArchivalCommand(this.serviceData, newCreatedWpArchive, true);
    executeCommand(wpArchivalCommand);

    // if the archival files are created, then add the files to archival files table
    if (CommonUtils.isNotNull(wpArchivalFiles)) {
      getLogger().debug("Inserting the archival file details into twpArchivalfiles table");
      WpFiles wpFiles = new WpFiles();
      wpFiles.setWpArchivalId(newCreatedWpArchive.getId());
      wpFiles.setFileData(wpArchivalFiles);
      wpFiles.setFileName(FilenameUtils.getBaseName(wpArchivalBO.getBaselineDirPath()));
      // add to Db
      WpFilesCommand wpFilesCommand = new WpFilesCommand(this.serviceData, wpFiles);
      executeCommand(wpFilesCommand);
    }

    this.wpArchivalModel.getWpArchival().add(newCreatedWpArchive);
    getLogger().debug(
        "Creation of Wp Archive files for WPArchival with ID: {} completed. Wp Archival table updated with archival file data",
        newCreatedWpArchive.getId());
  }

  /**
   * Create a new baseline for a WP archive
   *
   * @return
   * @throws IcdmException
   */
  private WpArchival createWPArchiveBaseline() throws IcdmException {
    WpArchival wpArchival = getWpArchivalModelFromA2lRespModel();
    // Execute the WpArchival command to persist data into WpArchival entity
    WpArchivalCommand wpArchivalCommand = new WpArchivalCommand(this.serviceData, wpArchival, false);
    executeCommand(wpArchivalCommand);
    // Get the newly created wpArchival model
    return wpArchivalCommand.getNewData();
  }

  /**
   * Prepare a WPRespModel for the WP Archive baseline
   *
   * @return
   */
  private WpArchival getWpArchivalModelFromA2lRespModel() {
    WpArchival wpArchival = new WpArchival();
    CDRReviewResultLoader cdrReviewResultLoader;
    try {
      cdrReviewResultLoader = new CDRReviewResultLoader(this.serviceData);
      TRvwResult tRvwResult = cdrReviewResultLoader.getEntityObject(this.rvwResultIdList.get(0));
      PidcVariant variant = cdrReviewResultLoader.getVariant(tRvwResult);
      TA2lWorkPackage tA2lWorkPackage =
          new A2lWorkPackageLoader(this.serviceData).getEntityObject(this.a2lWpRespModel.getA2lWpId());
      String wpName =
          new A2lWorkPackageLoader(this.serviceData).getDataObjectByID(this.a2lWpRespModel.getA2lWpId()).getName();
      String respName =
          new A2lResponsibilityLoader(this.serviceData).getDataObjectByID(this.a2lWpRespModel.getA2lRespId()).getName();
      PidcA2l pidcA2l = new PidcA2lLoader(this.serviceData).getDataObjectByID(tRvwResult.getTPidcA2l().getPidcA2lId());
      // fetch the active a2l def vers from a2l id
      TA2lWpDefnVersion tA2lWpDefnVersion = new A2lWpDefnVersionLoader(this.serviceData)
          .getActiveA2lWPDefnVersionEntityFromA2l(tRvwResult.getTPidcA2l().getPidcA2lId());

      wpArchival.setBaselineName(WpArchivalUtil.getArchivalBaselineName(wpName, respName));
      wpArchival.setPidcVersId(tA2lWorkPackage.getPidcVersion().getPidcVersId());
      wpArchival.setPidcVersFullname(tA2lWorkPackage.getPidcVersion().getVersName());
      wpArchival.setPidcA2lId(tRvwResult.getTPidcA2l().getPidcA2lId());
      wpArchival.setA2lFilename(pidcA2l.getName());
      wpArchival.setVariantId(
          (CommonUtils.isNull(variant) || CommonUtils.isEqual(variant.getId(), ApicConstants.NO_VARIANT_ID))
              ? ApicConstants.NO_VARIANT_ID : variant.getId());
      wpArchival.setVariantName(CommonUtils.isNull(variant) ? null : variant.getName());
      wpArchival.setRespId(this.a2lWpRespModel.getA2lRespId());
      wpArchival.setRespName(respName);
      wpArchival.setWpId(this.a2lWpRespModel.getA2lWpId());
      wpArchival.setWpName(wpName);
      wpArchival.setWpDefnVersId(tA2lWpDefnVersion.getWpDefnVersId());
      wpArchival.setWpDefnVersName(tA2lWpDefnVersion.getVersionName());
      wpArchival.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType());
    }
    catch (IcdmException e) {
      getLogger().error("Unable to create WpArchival Model from a2lWPRespModel with ID: " +
          this.a2lWpRespModel.getWpRespId() + " " + e.getMessage());
    }
    return wpArchival;
  }

  /**
   * @param wpArchivalModel
   * @param fileArchivalStatus
   * @return
   */
  public WPRespStatusMsgWrapper getStatusMsgWrapper(final WpArchival wpArchivalModel, final String fileArchivalStatus) {
    WPRespStatusMsgWrapper wpRespStatusMsgWrapper = new WPRespStatusMsgWrapper();
    wpRespStatusMsgWrapper.setWpRespId(this.a2lWpRespModel.getWpRespId());
    wpRespStatusMsgWrapper.setOutputStatusMsg(fileArchivalStatus.equals(FILE_ARCHIVAL_STATUS.COMPLETED.getDbType())
        ? CDRConstants.WP_ARCHIVAL_COMPLETED_WPRESP_MSG : CDRConstants.WP_ARCHIVAL_FAILED_WPRESP_MSG);
    wpRespStatusMsgWrapper.setRespName(wpArchivalModel.getRespName());
    wpRespStatusMsgWrapper.setWorkPackageName(wpArchivalModel.getWpName());
    return wpRespStatusMsgWrapper;
  }

}
