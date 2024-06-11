/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.a2l;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LDeletionResponse;
import com.bosch.caltool.icdm.model.a2l.UnmapA2LResponse;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class UnmapA2LServiceClient extends AbstractRestServiceClient {

  /**
   * constructor - initializes the base path
   */
  public UnmapA2LServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_A2L, "");
  }

  private static final IMapper UNMAP_A2L_MAPPER = data -> {

    Collection<IModel> changeDataList = new HashSet<>();

    UnmapA2LDeletionResponse response = (UnmapA2LDeletionResponse) data;
    changeDataList.add(response.getPidcA2l());
    return changeDataList;
  };

  /**
   * Get A2lResponsibility using its id
   *
   * @param pidcA2LId pidc a2l id which is to be deleted
   * @return UnmapA2LResponse related data
   * @throws ApicWebServiceException exception while invoking service
   */
  public UnmapA2LResponse getRelatedDbEntries(final Long pidcA2LId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_A2L_RELATED_OBJECTS)
        .queryParam(WsCommonConstants.RWS_QP_PIDC_A2L_ID, pidcA2LId);
    return get(wsTarget, UnmapA2LResponse.class);
  }

  /**
   * @param pidcA2LId pidc a2l id which is to be deleted
   * @throws ApicWebServiceException exception while invoking service
   */
  public void deleteA2lrelatedEntries(final Long pidcA2LId) throws ApicWebServiceException {
    PidcA2l input = new PidcA2lServiceClient().getById(pidcA2LId);
    UnmapA2LDeletionResponse deletionResponse = post(getWsBase().path(WsCommonConstants.RWS_DELETE_A2L_RELATED_OBJECTS),
        pidcA2LId, UnmapA2LDeletionResponse.class);

    Map<Long, IModel> newDataModelMap = ModelParser.getModel(deletionResponse, UNMAP_A2L_MAPPER);

    List<ChangeData<IModel>> chgDataList =
        (new ChangeDataCreator<IModel>()).createDataForUpdate(0L, ModelParser.getModel(input), newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));
    displayMessage(CHANGE_OPERATION.UPDATE, deletionResponse.getPidcA2l());
  }
}
