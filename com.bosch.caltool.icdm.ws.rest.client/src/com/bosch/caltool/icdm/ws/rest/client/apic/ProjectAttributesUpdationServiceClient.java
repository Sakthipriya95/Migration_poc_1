/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.ModelInfo;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */
public class ProjectAttributesUpdationServiceClient extends AbstractRestServiceClient {

  private static final IMapperChangeData PIDC_ATTRS_CREATION_MAPPER = data -> {

    Collection<ChangeData<IModel>> changeDataList = new HashSet<>();
    ChangeDataCreator<IModel> changeDataCreator = new ChangeDataCreator<>();
    ProjectAttributesUpdationModel updationModel = (ProjectAttributesUpdationModel) data;

    // pidc version change
    changeDataList.add(
        changeDataCreator.createDataForUpdate(0L, updationModel.getPidcVersion(), updationModel.getNewPidcVersion()));

    // pidc variant and sub variant changes
    updationModel.getPidcVarsToBeUpdated().values().forEach(var -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, var, updationModel.getPidcVarsToBeUpdatedWithNewVal().get(var.getId()))));
    updationModel.getPidcSubVarsToBeUpdated().values().forEach(var -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, var, updationModel.getPidcSubVarsToBeUpdatedWithNewVal().get(var.getId()))));


    updationModel.getPidcAttrsToBeCreated().values()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForCreate(0L, attr)));

    updationModel.getPidcAttrsToBeCreatedwithNewVal().values()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForCreate(0L, attr)));

    updationModel.getPidcAttrsAfterUpdation().values()
        .forEach(attr -> changeDataList.add(
            changeDataCreator.createDataForUpdate(0L, updationModel.getPidcAttrsToBeUpdated().get(attr.getAttrId()),
                updationModel.getPidcAttrsAfterUpdation().get(attr.getAttrId()))));

    updationModel.getPidcAttrsToBeUpdatedwithNewVal().values().forEach(attr -> changeDataList.add(changeDataCreator
        .createDataForUpdate(0L, attr, updationModel.getPidcAttrsToBeUpdatedwithNewVal().get(attr.getAttrId()))));

    updationModel.getPidcVarAttrsToBeCreated().values().forEach(mapAttr -> mapAttr.values()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForCreate(0L, attr))));

    updationModel.getPidcVarAttrsToBeCreatedWithNewVal().values().forEach(mapAttr -> mapAttr.values()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForCreate(0L, attr))));

    updationModel.getPidcVarAttrsToBeUpdated().values().forEach(
        mapAttr -> mapAttr.values().forEach(attr -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, attr,
            updationModel.getPidcVarAttrsToBeUpdated().get(attr.getVariantId()).get(attr.getAttrId())))));

    updationModel.getPidcVarAttrsToBeUpdatedWithNewVal().values().forEach(
        mapAttr -> mapAttr.values().forEach(attr -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, attr,
            updationModel.getPidcVarAttrsToBeUpdatedWithNewVal().get(attr.getVariantId()).get(attr.getAttrId())))));

    updationModel.getPidcSubVarAttrsToBeCreated().values().forEach(mapAttr -> mapAttr.values()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForCreate(0L, attr))));

    updationModel.getPidcSubVarAttrsToBeCreatedWithNewVal().values().forEach(mapAttr -> mapAttr.values()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForCreate(0L, attr))));

    updationModel.getPidcSubVarAttrsToBeUpdated().values().forEach(
        mapAttr -> mapAttr.values().forEach(attr -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, attr,
            updationModel.getPidcSubVarAttrsToBeUpdated().get(attr.getSubVariantId()).get(attr.getAttrId())))));

    updationModel.getPidcSubVarAttrsToBeUpdatedWithNewVal().values()
        .forEach(mapAttr -> mapAttr.values()
            .forEach(attr -> changeDataList.add(changeDataCreator.createDataForUpdate(0L, attr, updationModel
                .getPidcSubVarAttrsToBeUpdatedWithNewVal().get(attr.getSubVariantId()).get(attr.getAttrId())))));

    updationModel.getPidcVersInvisibleAttrSet()
        .forEach(pidcAttr -> changeDataList.add(changeDataCreator.createDataForInvisibleMode(0L, pidcAttr,
            new ModelInfo(pidcAttr.getPidcVersId(), MODEL_TYPE.PIDC_VERSION),
            new ModelInfo(pidcAttr.getAttrId(), MODEL_TYPE.ATTRIBUTE))));

    updationModel.getPidcVersVisibleAttrSet()
        .forEach(attr -> changeDataList.add(changeDataCreator.createDataForVisibleMode(0L, attr,
            new ModelInfo(attr.getPidcVersId(), MODEL_TYPE.PIDC_VERSION),
            new ModelInfo(attr.getAttrId(), MODEL_TYPE.ATTRIBUTE))));

    updationModel.getVariantInvisbleAttributeMap().values().forEach(
        varAttrSet -> varAttrSet.forEach(varAttr -> changeDataList.add(changeDataCreator.createDataForInvisibleMode(0L,
            varAttr, new ModelInfo(varAttr.getVariantId(), MODEL_TYPE.VARIANT),
            new ModelInfo(varAttr.getAttrId(), MODEL_TYPE.ATTRIBUTE)))));

    updationModel.getVariantVisbleAttributeMap().values().forEach(
        varAttrSet -> varAttrSet.forEach(varAttr -> changeDataList.add(changeDataCreator.createDataForVisibleMode(0L,
            varAttr, new ModelInfo(varAttr.getVariantId(), MODEL_TYPE.VARIANT),
            new ModelInfo(varAttr.getAttrId(), MODEL_TYPE.ATTRIBUTE)))));

    updationModel.getSubVariantInvisbleAttributeMap().values().forEach(
        varAttrSet -> varAttrSet.forEach(svarAttr -> changeDataList.add(changeDataCreator.createDataForInvisibleMode(0L,
            svarAttr, new ModelInfo(svarAttr.getSubVariantId(), MODEL_TYPE.SUB_VARIANT),
            new ModelInfo(svarAttr.getAttrId(), MODEL_TYPE.ATTRIBUTE)))));

    updationModel.getSubVariantVisbleAttributeMap().values().forEach(
        varAttrSet -> varAttrSet.forEach(svarAttr -> changeDataList.add(changeDataCreator.createDataForVisibleMode(0L,
            svarAttr, new ModelInfo(svarAttr.getSubVariantId(), MODEL_TYPE.SUB_VARIANT),
            new ModelInfo(svarAttr.getAttrId(), MODEL_TYPE.ATTRIBUTE)))));


    for (Entry<Long, Map<Long, PidcVariantAttribute>> entry : updationModel.getPidcVariantAttributeMap().entrySet()) {
      for (Entry<Long, PidcVariantAttribute> innerEntry : entry.getValue().entrySet()) {
        changeDataList.add(changeDataCreator.createDataForCreate(0L, innerEntry.getValue()));
      }
    }
    for (Entry<Long, Map<Long, PidcSubVariantAttribute>> entry : updationModel.getPidcSubVariantAttributeMap()
        .entrySet()) {
      for (Entry<Long, PidcSubVariantAttribute> innerEntry : entry.getValue().entrySet()) {
        changeDataList.add(changeDataCreator.createDataForCreate(0L, innerEntry.getValue()));
      }
    }
    for (Entry<Long, Map<Long, PidcVariantAttribute>> entry : updationModel.getPidcVariantAttributeDeletedMap()
        .entrySet()) {
      for (Entry<Long, PidcVariantAttribute> innerEntry : entry.getValue().entrySet()) {
        changeDataList.add(changeDataCreator.createDataForDelete(0L, innerEntry.getValue()));
      }
    }
    for (Entry<Long, Map<Long, PidcSubVariantAttribute>> entry : updationModel.getPidcSubVariantAttributeDeletedMap()
        .entrySet()) {
      for (Entry<Long, PidcSubVariantAttribute> innerEntry : entry.getValue().entrySet()) {
        changeDataList.add(changeDataCreator.createDataForDelete(0L, innerEntry.getValue()));
      }
    }

    return changeDataList;

  };


  /**
   * Constructor
   */
  public ProjectAttributesUpdationServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_PIDC_ATTRS_CREATION);
  }


  /**
   * @param input ProjectAttributesUpdationModel
   * @return ProjectAttributesUpdationModel
   * @throws ApicWebServiceException exception
   */
  public ProjectAttributesUpdationModel updatePidcAttrs(final ProjectAttributesUpdationModel input)
      throws ApicWebServiceException {

    GenericType<ProjectAttributesUpdationModel> type = new GenericType<ProjectAttributesUpdationModel>() {};

    ProjectAttributesUpdationModel ret = post(getWsBase(), input, type);

    Collection<ChangeData<IModel>> newDataModelSet = ModelParser.getChangeData(ret, PIDC_ATTRS_CREATION_MAPPER);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(newDataModelSet));

    displayMessage("Project attributes updated!");

    return ret;
  }


}
