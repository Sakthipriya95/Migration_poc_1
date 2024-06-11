/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarChangeModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author DMR1COB
 */
public class PidcVariantCopyServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData COPY_OUTPUT_CH_DATA_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcVarChangeModel varchData = (PidcVarChangeModel) data;
    changeDataList.add(changeDataCreator.createDataForCreate(0L, varchData.getPidcVarAfterUpdate()));
    changeDataList.add(changeDataCreator.createDataForUpdate(0L, varchData.getPidcVersBeforeUpdate(),
        varchData.getPidcVersAfterUpdate()));

    return changeDataList;
  };

  /**
   * Constructor
   */
  public PidcVariantCopyServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_VAR_COPY);
  }


  /**
   * Create a PIDC Variant record
   *
   * @param obj object to create
   * @return created PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVarPasteOutput create(final PidcVariantData obj) throws ApicWebServiceException {
    PidcVarChangeModel varChangeModel = new PidcVarChangeModel();
    varChangeModel.setPidcVersBeforeUpdate(obj.getPidcVersion());
    PidcVarPasteOutput ret = post(getWsBase(), obj, PidcVarPasteOutput.class);

    varChangeModel.setPidcVarAfterUpdate(ret.getPastedVariant());
    varChangeModel.setPidcVersAfterUpdate(ret.getPidcVersion());
    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(varChangeModel, COPY_OUTPUT_CH_DATA_MAPPER);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));
    displayMessage("Project variant copied !");
    return ret;
  }

  /**
   * Update a PIDC Variant record
   *
   * @param obj object to update
   * @return updated PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcVarPasteOutput update(final PidcVariantData obj) throws ApicWebServiceException {
    PidcVarChangeModel varChangeModel = new PidcVarChangeModel();
    varChangeModel.setPidcVarBeforeUpdate(obj.getDestPidcVar());
    varChangeModel.setPidcVersBeforeUpdate(obj.getPidcVersion());

    PidcVarPasteOutput ret = put(getWsBase(), obj, PidcVarPasteOutput.class);

    varChangeModel.setPidcVarAfterUpdate(ret.getPastedVariant());
    varChangeModel.setPidcVersAfterUpdate(ret.getPidcVersion());

    Collection<ChangeData<IModel>> newDataModelSet =
        ModelParser.getChangeData(varChangeModel, COPY_OUTPUT_CH_DATA_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Project variant copied !");

    return ret;

  }


}
