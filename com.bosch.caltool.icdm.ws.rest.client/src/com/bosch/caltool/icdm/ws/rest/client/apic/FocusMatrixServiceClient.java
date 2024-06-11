package com.bosch.caltool.icdm.ws.rest.client.apic;

import java.util.List;
import java.util.Map;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrix;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixDetailsModel;
import com.bosch.caltool.icdm.model.apic.pidc.FocusMatrixMappingData;
import com.bosch.caltool.icdm.ws.rest.client.AbstractRestServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * Service Client for Focus Matrix
 *
 * @author MKL2COB
 */
public class FocusMatrixServiceClient extends AbstractRestServiceClient {


  /**
   * Constructor
   */
  public FocusMatrixServiceClient() {
    super(WsCommonConstants.RWS_CONTEXT_APIC, WsCommonConstants.RWS_FOCUSMATRIX);
  }

  /**
   * Get all Focus Matrix records in system
   *
   * @param fmVersId focus matrix version id
   * @return Map. Key - id, Value - FocusMatrix object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrixDetailsModel getFocusMatrixForVersion(final Long fmVersId) throws ApicWebServiceException {
    LOGGER.debug("Get all Focus Matrix records for a focus matrix version ");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_GET_FOCUS_MATRIX_FOR_VERSION)
        .queryParam(WsCommonConstants.RWS_QP_OBJ_ID, fmVersId);
    GenericType<FocusMatrixDetailsModel> type = new GenericType<FocusMatrixDetailsModel>() {};
    FocusMatrixDetailsModel retObj = get(wsTarget, type);
    LOGGER.debug("Focus Matrix records loaded count = {}", retObj.getFocusMatrixMap().size());
    return retObj;
  }

  /**
   * Get Focus Matrix using its id
   *
   * @param objId object's id
   * @return FocusMatrix object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrix getById(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    return get(wsTarget, FocusMatrix.class);
  }

  /**
   * Create a Focus Matrix record
   *
   * @param obj object to create
   * @return created FocusMatrix object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrix create(final FocusMatrix obj) throws ApicWebServiceException {
    return create(getWsBase(), obj);
  }

  /**
   * Update a Focus Matrix record
   *
   * @param obj object to update
   * @return updated FocusMatrix object
   * @throws ApicWebServiceException exception while invoking service
   */
  public FocusMatrix update(final FocusMatrix obj) throws ApicWebServiceException {
    return update(getWsBase(), obj);
  }

  /**
   * Multiple update of focus matrix record
   *
   * @param focusMatrixlst List of FocusMatrix Object to be updated
   * @return Map. Key- FocusMatrixId, Value- FocusMatrix Object
   * @throws ApicWebServiceException
   */
  public Map<Long, FocusMatrix> multipleUpdate(final List<FocusMatrix> focusMatrixlst) throws ApicWebServiceException {
    LOGGER.debug("Update multiple FocusMatrix record");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE_UPDATE_FOCUS_MATRIX);
    GenericType<Map<Long, FocusMatrix>> type = new GenericType<Map<Long, FocusMatrix>>() {};
    return update(wsTarget, focusMatrixlst, type);
  }

  /**
   * Multiple Create of focus matrix record
   *
   * @param fcusMatrixlst
   * @return Map. Key- FocusMatrixId, Value- FocusMatrix Object
   * @throws ApicWebServiceException
   */
  public Map<Long, FocusMatrix> multipleCreate(final List<FocusMatrix> fcusMatrixlst) throws ApicWebServiceException {
    LOGGER.debug("Create multiple FocusMatrix record");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_MULTIPLE_CREATE_FOCUS_MATRIX);
    GenericType<Map<Long, FocusMatrix>> type = new GenericType<Map<Long, FocusMatrix>>() {};
    return create(wsTarget, fcusMatrixlst, type);
  }


  /**
   * Delete a Focus Matrix record
   *
   * @param objId id of object to delete
   * @throws ApicWebServiceException exception while invoking service
   */
  public void delete(final Long objId) throws ApicWebServiceException {
    WebTarget wsTarget = getWsBase().queryParam(WsCommonConstants.RWS_QP_OBJ_ID, objId);
    delete(wsTarget);
  }

  /**
   * @param fmMappingData
   * @return
   * @throws ApicWebServiceException
   */
  public boolean isFocusMatrixAvailableWhileMapping(final FocusMatrixMappingData fmMappingData)
      throws ApicWebServiceException {
    LOGGER.debug("Get whether there are focus matrix entries for the usecase item");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IS_FM_WHILE_MAPPING);
    Boolean retObj = post(wsTarget, fmMappingData, Boolean.class);
    LOGGER.debug("Focus matrix entires in usecase item  = {}", retObj);
    return retObj;
  }

  /**
   * @param fmMappingData
   * @return
   * @throws ApicWebServiceException
   */
  public boolean isFocusMatrixAvailableWhileUnMapping(final FocusMatrixMappingData fmMappingData)
      throws ApicWebServiceException {
    LOGGER.debug("Get whether there are focus matrix entries for the usecase item");
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_IS_FM_WHILE_UNMAPPING);
    Boolean retObj = post(wsTarget, fmMappingData, Boolean.class);
    LOGGER.debug("Focus matrix entires in usecase item  = {}", retObj);
    return retObj;
  }

  /**
   * Checks if pidc focus matrix sheet is empty.
   *
   * @param pidcVersId pidc version id
   * @return boolean true if sheet is empty else false
   * @throws ApicWebServiceException error during service call
   */
  public boolean isFocusMatrixEmpty(final Long pidcVersId) throws ApicWebServiceException {
    LOGGER.debug("Get whether the Focus Matrix sheet is empty or not for the pidc version id: {}" + pidcVersId);
    WebTarget wsTarget = getWsBase().path(WsCommonConstants.RWS_FOCUSMATRIX_EMPTY)
        .queryParam(WsCommonConstants.RWS_PIDC_VERSION_ID, pidcVersId);
    boolean retObj = get(wsTarget, Boolean.class);
    LOGGER.debug("Is Focus Matrix sheet empty: {}" + retObj);
    return retObj;
  }

}
