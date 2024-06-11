package com.bosch.caltool.icdm.ws.rest.client.cdr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.cdr.CDRConstants.FILE_ARCHIVAL_STATUS;
import com.bosch.caltool.icdm.model.cdr.DaDataAssessment;
import com.bosch.caltool.icdm.model.cdr.DataAssessSharePointUploadInputModel;
import com.bosch.caltool.icdm.model.dataassessment.DataAssessmentReport;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.Activator;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for DaDataAssessment
 *
 * @author say8cob
 */
public class DaDataAssessmentServiceClient extends AbstractRestServiceClient {


  private static final IMapperChangeData DATAASSESSMENT_MAPPER = data -> {
    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    DaDataAssessment daDataAssessment = (DaDataAssessment) data;

    changeDataList.add(changeDataCreator.createDataForUpdate(0L, daDataAssessment, daDataAssessment));

    return changeDataList;
  };

  /**
   * Constructor
   */
  public DaDataAssessmentServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_CDR, WsCommonConstants.RWS_DA_DATA_ASSESSMENT);
  }

  /**
   * Get DaDataAssessment using its id
   *
   * @param objId object's id
   * @return DaDataAssessment object
   * @throws ApicWebServiceException exception while invoking service
   */
  public DaDataAssessment get(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, DaDataAssessment.class);
  }


  /**
   * Create a DaDataAssessment Record
   *
   * @param dataAssessmentReport - Input model containing details of Data Assessment
   * @return Data Assessment Report
   * @throws ApicWebServiceException exception while invoking service
   */
  public DaDataAssessment createBaseline(final DataAssessmentReport dataAssessmentReport)
      throws ApicWebServiceException {
    LOGGER.info("Call for creation of new baseline started");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_BASELINE);
    DaDataAssessment daDataAssessment = post(wsTarget, dataAssessmentReport, DaDataAssessment.class);
    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(daDataAssessment, DATAASSESSMENT_MAPPER);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<>(newDataModelSet));
    return daDataAssessment;
  }


  /**
   * @param dataAssessmentReport Data Assessment Report Model
   * @param dataAssessmntId Data assessment/Baseline ID
   * @param inputModel DataAssessSharePointUploadInputModel
   * @return Data assessment Report model
   */
  public DataAssessmentReport createBaselineFiles(final DataAssessmentReport dataAssessmentReport,
      final Long dataAssessmntId, final DataAssessSharePointUploadInputModel inputModel) {

    LOGGER.info("Asynchronous service for creation of baseline files for the baseline {} started",
        dataAssessmentReport.getBaselineName());

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_CREATE_BASELINE_FILES)
        .queryParam(WsCommonConstants.RWS_BASELINE_ID, dataAssessmntId);

    LOGGER.debug("Asynchronous Service : target - {}", wsTarget);

    Future<Response> future = createFuturePut(wsTarget, dataAssessmentReport);

    new Thread(() -> {
      try {
        DaDataAssessment daDataAssessment = getResponseAsync(future, DaDataAssessment.class);
        dataAssessmentReport.getDataAssmntBaselines().add(daDataAssessment);
        if (daDataAssessment.getFileArchivalStatus().equalsIgnoreCase(FILE_ARCHIVAL_STATUS.COMPLETED.getDbType())) {
          CDMLogger.getInstance().infoDialog("Data Assessment Baseline files for '" +
              daDataAssessment.getBaselineName() + "' is created successfully.", Activator.PLUGIN_ID);
          if (inputModel != null) {
            inputModel.setBaselineId(daDataAssessment.getId());
            new DataAssessmentSharePointUploadServiceClient().uploadFileToSharePoint(inputModel);
          }
        }
        else {
          CDMLogger.getInstance()
              .infoDialog("Error occured during the creation of Data Assessment Baseline files for '" +
                  daDataAssessment.getBaselineName() + "'", Activator.PLUGIN_ID);
        }
        Collection<ChangeData<IModel>> newDataModelSet =
            ModelParser.getChangeData(daDataAssessment, DATAASSESSMENT_MAPPER);
        (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<>(newDataModelSet));
      }
      catch (ApicWebServiceException excep) {
        CDMLogger.getInstance().errorDialog(excep.getMessage(), excep, Activator.PLUGIN_ID);
      }

    }).start();

    LOGGER.info("Asynchronous service for creation of baseline files for the baseline {} completed",
        dataAssessmentReport.getBaselineName());

    return dataAssessmentReport;

  }

  /**
   * @param pidcA2lId Pidc A2l Id
   * @return Set<DaDataAssessment>
   * @throws ApicWebServiceException excpetion while invoking service
   */
  public Set<DaDataAssessment> getBaselinesForPidcA2l(final Long pidcA2lId) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_DA_BASELINES).queryParam(WsCommonConstants.RWS_PIDC_A2L_ID, pidcA2lId);

    GenericType<Set<DaDataAssessment>> type = new GenericType<Set<DaDataAssessment>>() {};

    return get(wsTarget, type);
  }

  /**
   * @param dataAssessmentId Data Assessment Baseline Id
   * @return DataAssessmentReport model
   * @throws ApicWebServiceException exception while invoking service
   */
  public DataAssessmentReport getDataAssessmentBaseline(final Long dataAssessmentId) throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_DA_BASELINE)
        .queryParam(WsCommonConstants.RWS_DA_BASELINE_ID, dataAssessmentId);

    return get(wsTarget, DataAssessmentReport.class);
  }

}
