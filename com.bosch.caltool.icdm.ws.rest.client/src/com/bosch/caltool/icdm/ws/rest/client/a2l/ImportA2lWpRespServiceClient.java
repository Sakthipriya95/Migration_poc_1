package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.Collection;
import java.util.HashSet;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespData;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespGrpsResponse;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespInput;
import com.bosch.caltool.icdm.model.a2l.ImportA2lWpRespResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractA2lWpRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for A2lWpParamMapping
 *
 * @author pdh2cob
 */
public class ImportA2lWpRespServiceClient extends AbstractA2lWpRestServiceClient {

  private static final IMapper WP_RESP_IMPORT_MAPPER = obj -> {
    Collection<IModel> changeDataList = new HashSet<>();
    changeDataList.addAll(((ImportA2lWpRespResponse) obj).getWpRespPalSet());
    changeDataList.addAll(((ImportA2lWpRespResponse) obj).getA2lWpParamMappingSet());
    changeDataList.addAll(((ImportA2lWpRespResponse) obj).getRespSet());
    changeDataList.addAll(((ImportA2lWpRespResponse) obj).getWrkPkgSet());
    changeDataList.addAll(((ImportA2lWpRespResponse) obj).getA2lWpDefnVersSet());
    changeDataList.addAll(((ImportA2lWpRespResponse) obj).getPidcA2lSet());
    return changeDataList;
  };

  private static final IMapper WP_RESP_GRPS_IMPORT_MAPPER = obj -> {
    Collection<IModel> changeDataList = new HashSet<>();
    changeDataList.addAll(((ImportA2lWpRespGrpsResponse) obj).getWpRespPalSet());
    changeDataList.addAll(((ImportA2lWpRespGrpsResponse) obj).getA2lWpParamMappingSet());
    changeDataList.addAll(((ImportA2lWpRespGrpsResponse) obj).getRespSet());
    changeDataList.addAll(((ImportA2lWpRespGrpsResponse) obj).getWrkPkgSet());
    return changeDataList;
  };

  /**
   * Constructor
   */
  public ImportA2lWpRespServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, WsCommonConstants.RWS_A2LWPRESP_IMPORT);
  }

  /**
   * Import FC2WP.
   *
   * @param input the input
   * @param pidcA2l
   * @return the import A 2 l wp resp response
   * @throws ApicWebServiceException the apic web service exception
   */
  public ImportA2lWpRespResponse importA2lWpRespFromFC2WP(final ImportA2lWpRespInput input, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IMPORT_FROM_FC2WP);
    ImportA2lWpRespResponse response =
        create(wsTarget, input, ImportA2lWpRespResponse.class, WP_RESP_IMPORT_MAPPER, pidcA2l);
    LOGGER.debug(
        "ImportA2lWpRespFromFC2WP Completed, No. of A2lWpResponsibility Created = {}, No. of A2lWpParamMapping Created = {}",
        response.getWpRespPalSet().size(), response.getA2lWpParamMappingSet().size());
    return response;
  }

  /**
   * Import A 2 l wp resp from excel.
   *
   * @param excelInput the excel input
   * @param pidcA2l
   * @return the import A 2 l wp resp response
   * @throws ApicWebServiceException the apic web service exception
   */
  public ImportA2lWpRespResponse importA2lWpRespFromExcel(final ImportA2lWpRespData excelInput, final PidcA2l pidcA2l)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IMPORT_FROM_EXCEL);
    ImportA2lWpRespResponse response =
        create(wsTarget, excelInput, ImportA2lWpRespResponse.class, WP_RESP_IMPORT_MAPPER, pidcA2l);
    LOGGER.debug(
        "importA2lWpRespFromExcel Completed, No. of A2lWpResponsibility Created = {}, No. of A2lWpParamMapping Created = {}",
        response.getWpRespPalSet().size(), response.getA2lWpParamMappingSet().size());
    return response;
  }


  /**
   * @param pidcA2l PidcA2l object
   * @param wpDefVersId wp definition version ID
   * @return a2lWpRespGrpsImport response
   * @throws ApicWebServiceException web service error
   */
  public ImportA2lWpRespGrpsResponse a2lWpRespGrpsImport(final PidcA2l pidcA2l, final Long wpDefVersId)
      throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IMPORT_FROM_A2L_GROUP);
    ImportA2lWpRespGrpsResponse response =
        create(wsTarget, wpDefVersId, ImportA2lWpRespGrpsResponse.class, WP_RESP_GRPS_IMPORT_MAPPER, pidcA2l);

    LOGGER.debug("A2L groups imported successfully");
    return response;
  }
}
