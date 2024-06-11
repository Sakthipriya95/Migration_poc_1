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
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVarPasteOutput;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mkl2cob
 */
public class PidcSubVarCopyServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData COPY_OUTPUT_CH_DATA_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcSubVarPasteOutput varData = (PidcSubVarPasteOutput) data;
    changeDataList.add(changeDataCreator.createDataForCreate(0L, varData.getPastedSubVariant()));
    changeDataList.add(
        changeDataCreator.createDataForUpdate(0L, varData.getPidcVarBeforeUpdate(), varData.getPidcVarAfterUpdate()));
    changeDataList.add(
        changeDataCreator.createDataForUpdate(0L, varData.getPidcVersBeforeUpdate(), varData.getPidcVersAfterUpdate()));

    return changeDataList;
  };

  private static final IMapperChangeData COPY_OUTPUT_RESPONSE_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();

    PidcSubVarPasteOutput varData = (PidcSubVarPasteOutput) data;
    changeDataList.add(changeDataCreator.createDataForCreate(0L, varData.getPastedSubVariant()));
    changeDataList.add(
        changeDataCreator.createDataForUpdate(0L, varData.getPidcVarBeforeUpdate(), varData.getPidcVarAfterUpdate()));
    changeDataList.add(
        changeDataCreator.createDataForUpdate(0L, varData.getPidcVersBeforeUpdate(), varData.getPidcVersAfterUpdate()));

    return changeDataList;
  };

  /**
   * @param moduleBase
   * @param serviceBase
   */
  public PidcSubVarCopyServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_SUB_VAR_COPY);
  }

  /**
   * Create a PIDC Variant record
   *
   * @param obj object to create
   * @return created PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVarPasteOutput create(final PidcSubVariantData obj) throws ApicWebServiceException {

    PidcSubVarPasteOutput ret = post(getWsBase(), obj, PidcSubVarPasteOutput.class);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, COPY_OUTPUT_CH_DATA_MAPPER);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));
    displayMessage("Project sub-variant copied !");
    return ret;
  }

  /**
   * Update a PIDC Variant record
   *
   * @param obj object to update
   * @return updated PidcVariant object
   * @throws ApicWebServiceException exception while invoking service
   */
  public PidcSubVarPasteOutput update(final PidcSubVariantData obj) throws ApicWebServiceException {
    PidcSubVarPasteOutput ret = put(getWsBase(), obj, PidcSubVarPasteOutput.class);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, COPY_OUTPUT_RESPONSE_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Project sub-variant copied !");

    return ret;
  }
}
