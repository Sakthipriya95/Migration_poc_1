package com.bosch.caltool.apic.ws.rest.service.cdr;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.server.ManagedAsync;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.cdr.DaDataAssessmentCommand;
import com.bosch.caltool.icdm.bo.cdr.DaDataAssessmentLoader;
import com.bosch.caltool.icdm.bo.cdr.DataAssessmentBO;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.FILE_ARCHIVAL_STATUS;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;


/**
 * Service class for DaDataAssessment
 *
 * @author say8cob
 */
@Path(("/" + WsCommonConstants.RWS_CONTEXT_CDR + "/" + WsCommonConstants.RWS_DA_DATA_ASSESSMENT))
public class DaDataAssessmentService extends AbstractRestService {


  /**
   * Get DaDataAssessment using its id
   *
   * @param objId object's id
   * @return Rest response, with DaDataAssessment object
   * @throws IcdmException exception while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @CompressData
  public Response get(@QueryParam(WsCommonConstants.RWS_QP_OBJ_ID) final Long objId) throws IcdmException {
    DaDataAssessmentLoader loader = new DaDataAssessmentLoader(getServiceData());
    DaDataAssessment ret = loader.getDataObjectByID(objId);
    return Response.ok(ret).build();
  }

  /**
   * Create a Data assessment baseline
   *
   * @param dataAssmntReportModel Data Assessment Report model
   * @return Created DaDataAssessment object
   * @throws IcdmException ICDM Exception
   * @throws IOException IO Exception
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_CREATE_BASELINE)
  public Response createBaseline(final DataAssessmentReport dataAssmntReportModel) throws IcdmException, IOException {

    getLogger().info("Create Baseline service started");

    // Fetch DaDataAssessment related info from the common model
    DaDataAssessment dataAssessment = getValuesFrmDataAssmntRprtModel(dataAssmntReportModel);

    // Execute the Data Assessment command to persist data into TDaDataAssessment entity
    DaDataAssessmentCommand dataAssmntCommand =
        new DaDataAssessmentCommand(getServiceData(), dataAssessment, dataAssmntReportModel, false);
    executeCommand(dataAssmntCommand);

    // Get the newly cretaed data assessment ID
    DaDataAssessment ret = dataAssmntCommand.getNewData();

    getLogger().info("Create baseline service completed. Created DaDataAssessment with ID: {}", ret.getId());

    return Response.ok(ret).build();
  }

  /**
   * @param asyncResponse Asynchronous response
   * @param dataAssessmentId Data Assessment/Baseline ID
   * @param dataAssmntReportModel Data Assessment Report Modle
   * @return Updated DaDataAssessment
   * @throws IcdmException ICDM Exception
   * @throws IOException IOException
   */
  @PUT
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  @Path(WsCommonConstants.RWS_CREATE_BASELINE_FILES)
  @ManagedAsync
  public Response createBaselineFiles(@Suspended final AsyncResponse asyncResponse,
      @QueryParam(WsCommonConstants.RWS_BASELINE_ID) final Long dataAssessmentId,
      final DataAssessmentReport dataAssmntReportModel)
      throws IcdmException, IOException {
    // Handle the unique SID(requestId) and method for entire API service call
    CommonUtils.setLogger(UUID.randomUUID().toString(), CommonUtilConstants.DA_CREATE_BASELINE_FILES);
    getLogger().debug("Creation of baseline files for baseline with ID {} started.", dataAssessmentId);

    // Registering the async response
    asyncResponse.register(new CompletionCallback() {

      @Override
      public void onComplete(final Throwable throwable) {
        if (throwable != null) {
          getLogger().error(throwable.getMessage(), throwable);
        }
      }
    });

    getLogger().debug("Loading the data assessment data for baseline with ID: {}", dataAssessmentId);
    DaDataAssessmentLoader dataAssessmentLoader = new DaDataAssessmentLoader(getServiceData());
    DaDataAssessment daDataAssessment = dataAssessmentLoader.getDataObjectByID(dataAssessmentId);

    // Create baseline files to be zipped and saved into TDaFiles entity
    getLogger().debug("Creating baseline files for baseline with ID: {}", dataAssessmentId);
    byte[] baselineFiles = null;
    DataAssessmentBO dataAssmntBo = new DataAssessmentBO(getServiceData(), dataAssmntReportModel, dataAssessmentId);
    try {
      baselineFiles = dataAssmntBo.createBaselineFiles();
      if (baselineFiles != null) {
        dataAssmntReportModel.setBaselineFileName(FilenameUtils.getBaseName(dataAssmntBo.getBaselineDirPath()));
        dataAssmntReportModel.setBaselineFileData(baselineFiles);
        daDataAssessment.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.COMPLETED.getDbType());
      }
      else {
        daDataAssessment.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.FAILED.getDbType());
      }
    }
    catch (IcdmException excep) {
      daDataAssessment.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.FAILED.getDbType());
      getLogger().error("An exception occured during creation of baseline files" + excep.getMessage());
    }
    getLogger().debug("Creation of baseline files for baseline with ID: {} completed", dataAssessmentId);

    getLogger().debug("Updating the status and file data into archival tables");
    DaDataAssessmentCommand dataAssessmentCmd =
        new DaDataAssessmentCommand(getServiceData(), daDataAssessment, dataAssmntReportModel, true);
    executeCommand(dataAssessmentCmd);

    // Getting the data assessment ID from command
    DaDataAssessment ret = dataAssessmentCmd.getNewData();

    asyncResponse.resume(ret);
    getLogger().debug(
        "Creation of baseline files for baseline with ID {} Completed. Data assessment table updated with baseline file data",
        dataAssessmentId);

    return Response.ok(ret).build();

  }

  /**
   * @param pidcA2lId Pidc A2l Id
   * @return Set<DaDataAssessment>
   * @throws IcdmException excpetion while invoking service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DA_BASELINES)
  public Response getBaselinesForPidcA2l(@QueryParam(WsCommonConstants.RWS_PIDC_A2L_ID) final Long pidcA2lId)
      throws IcdmException {
    DaDataAssessmentLoader daDataAssmntLoader = new DaDataAssessmentLoader(getServiceData());
    Set<DaDataAssessment> baselinesForPidcA2l = daDataAssmntLoader.getBaselinesForPidcA2l(pidcA2lId);

    getLogger().info("Data Assessment Baselines for Pidc A2l Id {} is :{}", pidcA2lId, baselinesForPidcA2l.size());
    return Response.ok(baselinesForPidcA2l).build();
  }


  /**
   * Fetch data assessment details from the common model
   *
   * @param dataAssmntReportModel
   * @param version
   * @throws IcdmException
   * @throws UnAuthorizedAccessException
   */
  private DaDataAssessment getValuesFrmDataAssmntRprtModel(final DataAssessmentReport dataAssmntReportModel) {

    getLogger().debug("Fetching Data assessment details from the common model");
    DaDataAssessment dataAssessment = new DaDataAssessment();
    dataAssessment.setBaselineName(dataAssmntReportModel.getBaselineName());
    dataAssessment.setDescription(dataAssmntReportModel.getDescription());
    if (dataAssmntReportModel.getTypeOfAssignment().equalsIgnoreCase("Assessment for series release")) {
      dataAssessment.setTypeOfAssignment(CDRConstants.TYPE_OF_ASSESSMENT.SERIES_RELEASE.getDbType());
    }
    else {
      dataAssessment.setTypeOfAssignment(CDRConstants.TYPE_OF_ASSESSMENT.DEVELOPMENT.getDbType());
    }
    dataAssessment.setPidcVersId(CommonUtils.getBigdecimalFromLong(dataAssmntReportModel.getPidcVersId()));
    dataAssessment.setPidcVersFullname(dataAssmntReportModel.getPidcVersName());
    dataAssessment.setVariantId(CommonUtils.getBigdecimalFromLong(dataAssmntReportModel.getPidcVariantId()));
    dataAssessment.setVariantName(dataAssmntReportModel.getPidcVariantName());
    dataAssessment.setPidcA2lId(CommonUtils.getBigdecimalFromLong(dataAssmntReportModel.getPidcA2lId()));
    dataAssessment.setA2lFilename(dataAssmntReportModel.getA2lFileName());
    dataAssessment.setHexFileName(dataAssmntReportModel.getHexFileName());
    dataAssessment.setWpDefnVersId(CommonUtils.getBigdecimalFromLong(dataAssmntReportModel.getWpDefnVersId()));
    dataAssessment.setWpDefnVersName(dataAssmntReportModel.getWpDefnVersName());
    dataAssessment.setVcdmDstSource(dataAssmntReportModel.getVcdmDstSource());
    dataAssessment.setVcdmDstVersId(CommonUtils.getBigdecimalFromLong(dataAssmntReportModel.getVcdmDstVersId()));
    dataAssessment.setFileArchivalStatus(FILE_ARCHIVAL_STATUS.IN_PROGRESS.getDbType());
    dataAssessment.setPreviousPidcVersConsidered(
        CommonUtils.getBooleanCode(dataAssmntReportModel.getConsiderRvwsOfPrevPidcVers()));
    if (CommonUtils.isNotNull(dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics())) {
      dataAssessment.setCompliParamInA2L(
          dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics().getStatCompliParamInA2L());
      dataAssessment.setCompliParamPassed(
          dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics().getStatCompliParamPassed());
      dataAssessment.setCompliParamCSSDFail(
          dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics().getStatCompliCssdFailed());
      dataAssessment.setCompliParamNoRuleFail(
          dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics().getStatCompliNoRuleFailed());
      dataAssessment.setCompliParamSSD2RVFail(
          dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics().getStatCompliSSDRvFailed());
      dataAssessment.setQssdParamFail(
          dataAssmntReportModel.getDataAssmntCompHexData().getCompareHexStatics().getStatQSSDParamFailed());
    }
    return dataAssessment;

  }


  /**
   * @param dataAssessmentId Data Assessment Baseline Id
   * @return DataAssessmentReport model, with entire Baseline Data
   * @throws ClassNotFoundException ClassNotFoundException while getting CalData Object
   * @throws IOException IOException while getting CalData Object
   * @throws IcdmException Exception in service
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path(WsCommonConstants.RWS_DA_BASELINE)
  public Response getDataAssessmentBaseline(
      @QueryParam(WsCommonConstants.RWS_DA_BASELINE_ID) final Long dataAssessmentId)
      throws ClassNotFoundException, IOException, IcdmException {
    DaDataAssessmentLoader daDataAssessmentLoader = new DaDataAssessmentLoader(getServiceData());

    DataAssessmentReport dataAssessmentReport = daDataAssessmentLoader.getDataAssessmentReportDetails(dataAssessmentId);

    getLogger().info("Data Assessment Report Baseline for Data Assessment Id {}", dataAssessmentId);
    return Response.ok(dataAssessmentReport).build();
  }

}
