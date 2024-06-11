/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Set;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.VcdmDataSet;
import com.bosch.caltool.icdm.model.cdr.VcdmHexFileData;
import com.bosch.caltool.icdm.model.vcdm.VCDMApplicationProject;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
public class VcdmDataSetServiceClient extends AbstractRestServiceClient {

  /**
   * Constructor
   */
  public VcdmDataSetServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_VCDM_DATASET);
  }

  /**
   * @param pidcID PIDC ID
   * @param timePeriod time period
   * @return set of vCDM data sets
   * @throws ApicWebServiceException error from web service call
   */
  public Set<VcdmDataSet> getStatisticsByPidcId(final Long pidcID, final int timePeriod)
      throws ApicWebServiceException {

    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_DATA_BY_PIDCID).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, pidcID)
            .queryParam(WsCommonConstants.RWS_QP_TIME_PERIOD, timePeriod);

    GenericType<Set<VcdmDataSet>> type = new GenericType<Set<VcdmDataSet>>() {};

    Set<VcdmDataSet> retSet = get(wsTarget, type);

    LOGGER.debug("VcdmDataSetByPidcId data loaded. No. of records : {}", retSet.size());

    return retSet;
  }

  /**
   * Returns the DSTS using this sdom info
   *
   * @param sdomPverName pver name
   * @param sdomPverVar pver variant name
   * @param sdomPverRevision pver variant revision
   * @return VCDMApplicationProjects
   * @throws ApicWebServiceException error from web service call
   */
  public Set<VCDMApplicationProject> getDataSet(final String sdomPverName, final String sdomPverVar,
      final String sdomPverRevision)
      throws ApicWebServiceException {

    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_VCDM_DATASETS)
        .queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_NAME, sdomPverName)
        .queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT, sdomPverVar)
        .queryParam(WsCommonConstants.RWS_QP_SDOM_PVER_VARIANT_REV, sdomPverRevision);

    GenericType<Set<VCDMApplicationProject>> type = new GenericType<Set<VCDMApplicationProject>>() {};

    Set<VCDMApplicationProject> retSet = get(wsTarget, type);

    LOGGER.debug("VcdmDataSetByPidcId data loaded. No. of records : {}", retSet.size());

    return retSet;
  }

  /**
   * Download hex file for given EMR file Id
   *
   * @param versNum vers number
   * @param filePath
   * @return inputStream input Stream
   * @throws ApicWebServiceException data exception
   */
  public String loadHexFile(final long versNum, final String filePath) throws ApicWebServiceException {
    WebTarget wsTarget =
        getWsBase().path(WsCommonConstants.RWS_LOAD_HEX_FILE).queryParam(WsCommonConstants.RWS_QP_OBJ_ID, versNum);
    GenericType<VcdmHexFileData> type = new GenericType<VcdmHexFileData>() {};

    VcdmHexFileData retSet = get(wsTarget, type);

    File file = new File(filePath + File.separator + retSet.getFileName());

    InputStream inputStream = new ByteArrayInputStream(retSet.getHexFileBytes());
    writeData(retSet.getFileName(), file, inputStream);
    return file.getPath();
  }

}
