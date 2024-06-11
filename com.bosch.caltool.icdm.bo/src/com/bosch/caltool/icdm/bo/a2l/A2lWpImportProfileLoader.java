/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.a2l;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.TypedQuery;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.database.entity.a2l.TA2lWpImportProfile;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.a2l.A2lImportProfileDetails;
import com.bosch.caltool.icdm.model.a2l.A2lWpImportProfile;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author and4cob
 */
public class A2lWpImportProfileLoader extends AbstractBusinessObject<A2lWpImportProfile, TA2lWpImportProfile> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public A2lWpImportProfileLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.A2L_WP_IMPORT_PROFILE, TA2lWpImportProfile.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected A2lWpImportProfile createDataObject(final TA2lWpImportProfile entity) throws DataException {
    A2lWpImportProfile object = new A2lWpImportProfile();

    setCommonFields(object, entity);

    object.setProfileName(entity.getProfileName());
    object.setProfileOrder(entity.getProfileOrder());
    A2lImportProfileDetails importedProfileDetails = convertJsonToJavaObject(entity.getProfileDetails());
    object.setProfileDetails(importedProfileDetails);
    return object;
  }

  /**
   * @param profileDetails
   * @throws InvalidInputException
   */
  private A2lImportProfileDetails convertJsonToJavaObject(final String profileDetails) throws InvalidInputException {

    A2lImportProfileDetails input = null;
    try {
      ObjectMapper mapper = new ObjectMapper();
      input = mapper.readValue(profileDetails, A2lImportProfileDetails.class);

    }
    catch (IOException exp) {
      throw new InvalidInputException("GENERAL.JSON_INVALID", exp);
    }

    return input;

  }

  /**
   * Get all A2lWpImportProfile records in system
   *
   * @return Map. Key - id, Value - A2lWpImportProfile object
   * @throws DataException error while retrieving data
   */
  public Map<Long, A2lWpImportProfile> getAll() throws DataException {
    Map<Long, A2lWpImportProfile> objMap = new ConcurrentHashMap<>();
    TypedQuery<TA2lWpImportProfile> tQuery =
        getEntMgr().createNamedQuery(TA2lWpImportProfile.GET_ALL, TA2lWpImportProfile.class);
    List<TA2lWpImportProfile> dbObj = tQuery.getResultList();
    for (TA2lWpImportProfile entity : dbObj) {
      objMap.put(entity.getProfileId(), createDataObject(entity));
    }
    return objMap;
  }

}

