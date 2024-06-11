/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.InvocationCallback;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPart;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.apic.ws.common.WSErrorCodes;
import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.datamodel.core.IBasicObject;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapperChangeData;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.ModelParser;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceRuntimeException;
import com.bosch.caltool.icdm.ws.rest.client.service.ResponseHandler;
import com.bosch.caltool.icdm.ws.rest.client.util.ZipUtils;

/**
 * @author bne4cob
 */
public abstract class AbstractRestServiceClient {


  /**
   *
   */
  private static final String RESPONSE_STATUS_CODE = "Response status code = {}";


  /**
   *
   */
  private static final String ID_EQUAL_TO = "ID = '";


  /**
   *
   */
  private static final String NAME_EQUAL_TO = "Name = '";


  private static final String ERROR_WHILE_DOWNLOADING_FILE_MSG = "Error while downloading file ";


  private static final String INVOKING_POST_RESPONSE_RECEIVED_MSG =
      "Invoking post(): response received. Time taken = {} ms";

  /**
   * Logger
   */
  protected static final ILoggerAdapter LOGGER = ClientConfiguration.getDefault().getLogger();

  /**
   * Base Web target for this service
   */
  private WebTarget wsBase;

  private ClientConfiguration clientConf = ClientConfiguration.getDefault();

  private final String moduleBase;

  private final MultivaluedMap<String, Object> responseHeaders = new MultivaluedHashMap<>();

  private final String serviceBase;

  /**
   * @param moduleBase URI relative base path of module
   * @param serviceBase URI relative base path of the service
   */
  protected AbstractRestServiceClient(final String moduleBase, final String serviceBase) {
    this.moduleBase = moduleBase;
    this.serviceBase = serviceBase;

    // Dummy call, to make sure that all model types are registered
    MODEL_TYPE.ATTRIBUTE.name();
  }


  /**
   * Set the client configuration to use. If not set, the default configuration would be used.
   * <p>
   * IMPORTANT : this method should be invoked before any service calls
   *
   * @param clientConfig the client configuration to set
   */
  public void setClientConfiguration(final ClientConfiguration clientConfig) {
    if (clientConfig == null) {
      throw new ApicWebServiceRuntimeException("Client configuration cannot be null");
    }
    if (this.wsBase != null) {
      throw new ApicWebServiceRuntimeException("Client configuration should be set before any service calls");
    }
    this.clientConf = clientConfig;
  }

  /**
   * @return the client configuration being used by this client
   */
  public ClientConfiguration getClientConfiguration() {
    return this.clientConf;
  }


  /**
   * @return Root web target for iCDM services
   */
  private WebTarget createRestWebTarget() {
    Client restClient = IcdmRestClientProvider.getClient(this.clientConf);

    URI restURI = UriBuilder.fromUri(this.clientConf.getBaseUri()).build();
    return restClient.target(restURI).path(WsCommonConstants.RWS_CONTEXT_ROOT);
  }


  /**
   * @return the base web target for this service
   */
  protected final WebTarget getWsBase() {
    if (this.wsBase == null) {
      this.wsBase = createRestWebTarget().path(this.moduleBase).path(this.serviceBase);
    }
    return this.wsBase;
  }

  /**
   * Execute get() request for POJO type response
   *
   * @param wsTarget web target to hit
   * @param retTypeClass class of response type R
   * @param <R> response type
   * @return R response data object
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R get(final WebTarget wsTarget, final Class<R> retTypeClass) throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking get(): target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    ResponseHandler<R> responseHandler = new ResponseHandler<>(this.clientConf, wsTarget, retTypeClass);
    R response = getResponseFromHandler(responseHandler);

    LOGGER.debug("Invoking get(): response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return response;
  }


  /**
   * @param responseHandler
   * @return
   * @throws ApicWebServiceException
   */
  private <R> R getResponseFromHandler(final ResponseHandler<R> responseHandler) throws ApicWebServiceException {
    R response;
    try {
      response = responseHandler.getResponse();
    }
    finally {
      retriveResponseHeaders(responseHandler.getServiceResposne());
    }
    return response;
  }

  /**
   * Execute get() request for generic type response
   *
   * @param wsTarget web target to hit
   * @param genericRetType generic type for response type R's collection
   * @param <R> response type
   * @return R response data object
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R get(final WebTarget wsTarget, final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking get(): target - {}; response type - {}", wsTarget, genericRetType);

    long startTime = System.currentTimeMillis();

    ResponseHandler<R> responseHandler = new ResponseHandler<>(this.clientConf, wsTarget, genericRetType);

    R response = getResponseFromHandler(responseHandler);

    LOGGER.debug("Invoking get(): response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return response;
  }

  /**
   * Execute post() request for POJO response
   *
   * @param wsTarget web target to hit
   * @param input input post input of type I
   * @param retTypeClass class of response type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R post(final WebTarget wsTarget, final I input, final Class<R> retTypeClass)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking post(): target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(retTypeClass);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug(INVOKING_POST_RESPONSE_RECEIVED_MSG, System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * create the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R create(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    return create(wsTarget, input, genericRetType, null);
  }

  /**
   * create the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param retTypeClass response type class
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R create(final WebTarget wsTarget, final I input, final Class<R> retTypeClass,
      final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = post(wsTarget, input, retTypeClass);

    Collection<IModel> newDataCollection = ModelParser.getModel(ret, responseMapper).values();
    List<ChangeData<IModel>> chgDataList = (new ChangeDataCreator<IModel>()).createDataForCreate(0L, newDataCollection);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.CREATE, newDataCollection);

    return ret;
  }

  /**
   * create the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R create(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = post(wsTarget, input, genericRetType);

    Collection<IModel> newDataCollection = ModelParser.getModel(ret, responseMapper).values();
    List<ChangeData<IModel>> chgDataList = (new ChangeDataCreator<IModel>()).createDataForCreate(0L, newDataCollection);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.CREATE, newDataCollection);

    return ret;
  }


  /**
   * @param wsTarget web target
   * @param input object to be created
   * @return new object created
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <D extends IModel> D create(final WebTarget wsTarget, final D input) throws ApicWebServiceException {

    @SuppressWarnings("unchecked")
    Class<D> clazz = (Class<D>) input.getClass();
    return create(wsTarget, input, clazz);
  }

  /**
   * @param wsTarget web target
   * @param input object to be created
   * @param clazzR response class type
   * @return new object created
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <D extends IModel, I> D create(final WebTarget wsTarget, final I input, final Class<D> clazzR)
      throws ApicWebServiceException {

    D newData = post(wsTarget, input, clazzR);

    ChangeData<D> chgData = (new ChangeDataCreator<D>()).createDataForCreate(0L, newData);
    (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(chgData));

    displayMessage(CHANGE_OPERATION.CREATE, newData);

    return newData;
  }

  /**
   * Create the data for 'form data multipart' input
   *
   * @param wsTarget web target
   * @param input object to be created
   * @param clazzR response class type
   * @return new object created
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <D extends IModel> D create(final WebTarget wsTarget, final FormDataMultiPart input,
      final Class<D> clazzR)
      throws ApicWebServiceException {

    D newData = post(wsTarget, input, clazzR);

    ChangeData<D> chgData = (new ChangeDataCreator<D>()).createDataForCreate(0L, newData);
    (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(chgData));

    displayMessage(CHANGE_OPERATION.CREATE, newData);

    return newData;
  }

  /**
   * Create the data for 'form data multipart' input
   *
   * @param <R> response type
   * @param wsTarget web target to hit
   * @param input Form data multipart input
   * @param retTypeClass response type class
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R create(final WebTarget wsTarget, final FormDataMultiPart input, final Class<R> retTypeClass,
      final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = post(wsTarget, input, retTypeClass);
    Collection<IModel> newDataCollection = ModelParser.getModel(ret, responseMapper).values();
    List<ChangeData<IModel>> chgDataList = (new ChangeDataCreator<IModel>()).createDataForCreate(0L, newDataCollection);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.CREATE, newDataCollection);

    return ret;
  }


  /**
   * Execute post() request for POJO response.
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input post input of type I
   * @param genericRetType the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R post(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking post(): target - {}; response type - {}", wsTarget, genericRetType);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(genericRetType);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug(INVOKING_POST_RESPONSE_RECEIVED_MSG, System.currentTimeMillis() - startTime);

    return ret;
  }


  /**
   * Execute post() with form data request for POJO response.
   *
   * @param <R> response type
   * @param wsTarget web target to hit
   * @param multipart input post input
   * @param genericRetType the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R post(final WebTarget wsTarget, final FormDataMultiPart multipart,
      final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking post() with form data: target - {}; response type - {}", wsTarget, genericRetType);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(genericRetType);
      response.close();
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug(INVOKING_POST_RESPONSE_RECEIVED_MSG, System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R update(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    return update(wsTarget, input, genericRetType, null);
  }


  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param retTypeClass return type class
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R update(final WebTarget wsTarget, final I input, final Class<R> retTypeClass,
      final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = put(wsTarget, input, retTypeClass);

    Map<Long, IModel> newDataModelMap = ModelParser.getModel(ret, responseMapper);

    List<ChangeData<IModel>> chgDataList =
        (new ChangeDataCreator<IModel>()).createDataForUpdate(0L, ModelParser.getModel(input), newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.UPDATE, newDataModelMap.values());

    return ret;
  }

  /**
   * Update the data for 'form data multi-part' input
   *
   * @param wsTarget web target
   * @param input object to be created
   * @param clazzR response class type
   * @param inputMapper input Mapper
   * @param responseMapper response Mapper
   * @return new object created
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <R> R update(final WebTarget wsTarget, final FormDataMultiPart input, final Class<R> clazzR,
      final IMapper inputMapper, final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = put(wsTarget, input, clazzR);

    Map<Long, IModel> newDataModelMap = ModelParser.getModel(ret, responseMapper);

    List<ChangeData<IModel>> chgDataList = (new ChangeDataCreator<IModel>()).createDataForUpdate(0L,
        ModelParser.getModel(input, inputMapper), newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.UPDATE, newDataModelMap.values());

    return ret;
  }


  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param retTypeClass return type class
   * @param inputMapper input mapper
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R update(final WebTarget wsTarget, final I input, final Class<R> retTypeClass,
      final IMapper inputMapper, final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = put(wsTarget, input, retTypeClass);

    Map<Long, IModel> newDataModelMap = ModelParser.getModel(ret, responseMapper);

    List<ChangeData<IModel>> chgDataList = (new ChangeDataCreator<IModel>()).createDataForUpdate(0L,
        ModelParser.getModel(input, inputMapper), newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.UPDATE, newDataModelMap.values());

    return ret;
  }

  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param inputMapper input mapper
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R update(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final IMapper inputMapper, final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = put(wsTarget, input, genericRetType);

    Map<Long, IModel> newDataModelMap = ModelParser.getModel(ret, responseMapper);

    List<ChangeData<IModel>> chgDataList = (new ChangeDataCreator<IModel>()).createDataForUpdate(0L,
        ModelParser.getModel(input, inputMapper), newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.UPDATE, newDataModelMap.values());

    return ret;
  }

  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R update(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final IMapper responseMapper)
      throws ApicWebServiceException {

    R ret = put(wsTarget, input, genericRetType);

    Map<Long, IModel> newDataModelMap = ModelParser.getModel(ret, responseMapper);

    List<ChangeData<IModel>> chgDataList =
        (new ChangeDataCreator<IModel>()).createDataForUpdate(0L, ModelParser.getModel(input), newDataModelMap);

    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.UPDATE, newDataModelMap.values());

    return ret;
  }

  /**
   * @param wsTarget web target
   * @param input object to be updated
   * @return updated object
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <D extends IModel> D update(final WebTarget wsTarget, final D input) throws ApicWebServiceException {

    @SuppressWarnings("unchecked")
    Class<D> clazz = (Class<D>) input.getClass();

    D newData = put(wsTarget, input, clazz);

    ChangeData<D> chgData = (new ChangeDataCreator<D>()).createDataForUpdate(0L, input, newData);
    (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(chgData));

    displayMessage(CHANGE_OPERATION.UPDATE, newData);

    return newData;
  }

  /**
   * Execute put() request for POJO response
   *
   * @param wsTarget web target to hit
   * @param input input post input of type I
   * @param retTypeClass class of response type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R put(final WebTarget wsTarget, final I input, final Class<R> retTypeClass)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking put(): target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.put(Entity.entity(input, MediaType.APPLICATION_JSON));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(retTypeClass);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug("Invoking put(): response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Execute put() request for Generic type response.
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R put(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking put(): target - {}; response type - {}", wsTarget, genericRetType);

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.put(Entity.entity(input, MediaType.APPLICATION_JSON));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(genericRetType);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug("Invoking put(): response received");

    return ret;
  }


  /**
   * Execute put() with multipart/form-data request for POJO response.
   *
   * @param <R> response type
   * @param wsTarget web target to hit
   * @param multipart form data input
   * @param retTypeClass class of response type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R put(final WebTarget wsTarget, final FormDataMultiPart multipart, final Class<R> retTypeClass)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking put() with form data: target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret = null;
    Response response = null;
    try {
      response = builder.put(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(retTypeClass);
      response.close();
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug(INVOKING_POST_RESPONSE_RECEIVED_MSG, System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Execute put() with multipart/form-data request for generic type response.
   *
   * @param <R> response type
   * @param wsTarget web target to hit
   * @param multipart form data input
   * @param genericRetType the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R put(final WebTarget wsTarget, final FormDataMultiPart multipart,
      final GenericType<R> genericRetType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking put() with form data: target - {}; response type - {}", wsTarget, genericRetType);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.put(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(genericRetType);
      response.close();
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug(INVOKING_POST_RESPONSE_RECEIVED_MSG, System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Execute delete() request for POJO response
   *
   * @param wsTarget web target to hit
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final void delete(final WebTarget wsTarget) throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking delete(): target - {}; response type - {}", wsTarget);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);
    Response response = null;

    try {
      response = builder.delete();
      ResponseHandler.checkErrors(response);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug("Invoking delete(): completed. Time taken = {} ms", System.currentTimeMillis() - startTime);
  }

  /**
   * @param wsTarget target
   * @param inputCollection objects to be deleted
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <D extends IModel> void delete(final WebTarget wsTarget, final Collection<D> inputCollection)
      throws ApicWebServiceException {
    WebTarget webTarget = wsTarget;
    for (D input : inputCollection) {
      webTarget = webTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, input.getId());
    }
    delete(webTarget);

    List<ChangeData<D>> chgDataList = (new ChangeDataCreator<D>()).createDataForDelete(0L, inputCollection);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chgDataList));

    displayMessage(CHANGE_OPERATION.DELETE, new ArrayList<IModel>(inputCollection));

  }

  /**
   * @param wsTarget target
   * @param input object to be deleted
   * @throws ApicWebServiceException any exception in the server
   */
  protected final <D extends IModel> void delete(final WebTarget wsTarget, final D input)
      throws ApicWebServiceException {

    delete(wsTarget.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, input.getId()));

    ChangeData<D> chgData = (new ChangeDataCreator<D>()).createDataForDelete(0L, input);
    (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(chgData));

    displayMessage(CHANGE_OPERATION.DELETE, input);

  }

  /**
   * Create 'Future' object instance for asynchronous delete service calls
   *
   * @param target Web Target
   * @param obj model object
   * @return Future object
   */
  protected final <D extends IModel> Future<Response> createFutureDelete(final WebTarget target, final D obj) {
    LOGGER.debug("Asynchronous DELETE service : target - {}", target);

    WebTarget wsTarget = target.queryParam(WsCommonConstants.RWS_QP_OBJ_ID, obj.getId());

    Builder reqBuilder = wsTarget.request().header(HttpHeaders.ACCEPT_ENCODING, WsCommonConstants.ENCODING_GZIP);
    reqBuilder.header(WsCommonConstants.REQ_LANGUAGE, this.clientConf.getLanguage());
    reqBuilder.header(WsCommonConstants.REQ_TIMEZONE, this.clientConf.getTimezone());
    String cnsSessionId = this.clientConf.getCnsSessionId();
    if (cnsSessionId != null) {
      reqBuilder.header(WsCommonConstants.REQ_CNS_SESSION_ID, cnsSessionId);
    }

    return reqBuilder.async().delete(new InvocationCallback<Response>() {

      @Override
      public void completed(final Response arg0) {
        LOGGER.info(RESPONSE_STATUS_CODE, arg0.getStatus());
        displayMessage(CHANGE_OPERATION.DELETE, obj);
        ChangeData<D> chgData = (new ChangeDataCreator<D>()).createDataForDelete(0L, obj);
        (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(chgData));
      }

      @Override
      public void failed(final Throwable err) {
        String objIdentifier =
            obj instanceof IBasicObject ? AbstractRestServiceClient.NAME_EQUAL_TO + ((IBasicObject) obj).getName() + "'"
                : AbstractRestServiceClient.ID_EQUAL_TO + obj.getId() + "'";
        CDMLogger.getInstance().errorDialog("Deletion of data object [" + objIdentifier + "] failed", err,
            Activator.PLUGIN_ID);

      }
    });
  }

  /**
   * Create 'Future' object instance for asynchronous update service calls
   *
   * @param target Web Target
   * @param obj Obj to be updated
   * @return Future object
   */
  protected <D extends IModel> Future<Response> createFutureUpdate(final WebTarget target, final D obj) {
    LOGGER.debug("Asynchronous GET service : target - {}", target);

    Builder reqBuilder = target.request().header(HttpHeaders.ACCEPT_ENCODING, WsCommonConstants.ENCODING_GZIP);
    reqBuilder.header(WsCommonConstants.REQ_LANGUAGE, this.clientConf.getLanguage());
    reqBuilder.header(WsCommonConstants.REQ_TIMEZONE, this.clientConf.getTimezone());
    String cnsSessionId = this.clientConf.getCnsSessionId();
    if (cnsSessionId != null) {
      reqBuilder.header(WsCommonConstants.REQ_CNS_SESSION_ID, cnsSessionId);
    }

    return reqBuilder.async().get(new InvocationCallback<Response>() {


      @Override
      public void completed(final Response arg0) {
        LOGGER.info(RESPONSE_STATUS_CODE, arg0.getStatus());
      }

      @Override
      public void failed(final Throwable err) {
        String objIdentifier =
            obj instanceof IBasicObject ? AbstractRestServiceClient.NAME_EQUAL_TO + ((IBasicObject) obj).getName() + "'"
                : AbstractRestServiceClient.ID_EQUAL_TO + obj.getId() + "'";
        CDMLogger.getInstance().errorDialog("Asynchronous Update of [" + objIdentifier + "] failed", err,
            Activator.PLUGIN_ID);

      }
    });
  }

  /**
   * Create 'Future' object instance for asynchronous update service calls
   *
   * @param target Web Target
   * @param obj input model object
   * @return Future object
   */
  protected <I> Future<Response> createFuturePut(final WebTarget target, final I obj) {
    LOGGER.debug("Asynchronous PUT service : target - {}", target);

    Builder reqBuilder = createBuilder(target);

    return reqBuilder.async().put(Entity.entity(obj, MediaType.APPLICATION_JSON), new InvocationCallback<Response>() {


      @Override
      public void completed(final Response arg0) {
        LOGGER.info(RESPONSE_STATUS_CODE, arg0.getStatus());
      }

      @Override
      public void failed(final Throwable err) {

        String objIdentifier = getObjectIdentifier(obj);

        CDMLogger.getInstance().errorDialog("Asynchronous Update of [" + objIdentifier + "] failed", err,
            Activator.PLUGIN_ID);

      }

    });
  }

  /**
   * Create future handle for asynchronous services
   * 
   * @param target web service target
   * @param input input
   * @param callback calback
   * @return Future
   */
  protected Future<Response> createFuturePost(final WebTarget target, final Object input,
      final InvocationCallback<Response> callback) {
    LOGGER.debug("Asynchronous POST service : target - {}", target);

    Builder reqBuilder = createBuilder(target);

    return reqBuilder.async().post(Entity.entity(input, MediaType.APPLICATION_JSON), callback);
  }


  /**
   * Execute post() with form data request for POJO response.
   *
   * @param <R> response type
   * @param wsTarget web target to hit
   * @param multipart input post input
   * @param retTypeClass the generic ret type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R post(final WebTarget wsTarget, final FormDataMultiPart multipart, final Class<R> retTypeClass)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking post() with form data: target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget);

    R ret;
    Response response = null;
    try {
      response = builder.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(retTypeClass);
      response.close();
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug(INVOKING_POST_RESPONSE_RECEIVED_MSG, System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Execute post() request
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input post input
   * @param retTypeClass the generic ret type
   * @param mediaType media type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R post(final WebTarget wsTarget, final I input, final Class<R> retTypeClass,
      final String mediaType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking post() : target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget, mediaType);

    R ret;
    Response response = null;
    try {
      response = builder.post(Entity.entity(input, MediaType.APPLICATION_JSON));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(retTypeClass);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug("Invoking post() : response received. Time taken = {} ms", System.currentTimeMillis() - startTime);

    return ret;
  }


  /**
   * Execute post() with form data request for POJO response.
   *
   * @param <R> response type
   * @param wsTarget web target to hit
   * @param multipart post input
   * @param retTypeClass the generic ret type
   * @param mediaType media type
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R> R post(final WebTarget wsTarget, final FormDataMultiPart multipart, final Class<R> retTypeClass,
      final String mediaType)
      throws ApicWebServiceException {
    clearResponseHeaderInformation();
    LOGGER.debug("Invoking post() with form data: target - {}; response type - {}", wsTarget, retTypeClass);

    long startTime = System.currentTimeMillis();

    Builder builder = createBuilder(wsTarget, mediaType);

    R ret;
    Response response = null;
    try {
      response = builder.post(Entity.entity(multipart, MediaType.MULTIPART_FORM_DATA));
      ResponseHandler.checkErrors(response);
      ret = response.readEntity(retTypeClass);
    }
    catch (ApicWebServiceException exp) {
      throw exp;
    }
    catch (Exception exp) {
      throw new ApicWebServiceException(WSErrorCodes.INT_SERVER_ERROR, exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(response);
    }
    LOGGER.debug("Invoking post() with form data: response received. Time taken = {} ms",
        System.currentTimeMillis() - startTime);

    return ret;
  }

  /**
   * Download a file using post
   *
   * @param target as input
   * @param input object for post
   * @param destFileName as input
   * @param destDirectory as input
   * @return path of the downloaded file
   * @throws ApicWebServiceException exception
   */
  protected String downloadFilePost(final WebTarget target, final Object input, final String destFileName,
      final String destDirectory)
      throws ApicWebServiceException {

    LOGGER.debug("Downloading file with post(). Inputs : File Name - {}; directory - {}", destFileName, destDirectory);

    long startTime = System.currentTimeMillis();
    File file = new File(destDirectory + File.separator + destFileName);

    try (InputStream inputStream = post(target, input, InputStream.class, MediaType.APPLICATION_OCTET_STREAM)) {
      // write the content from Input stream
      writeData(destFileName, file, inputStream);
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR,
          ERROR_WHILE_DOWNLOADING_FILE_MSG + destFileName + " : " + exp.getMessage(), exp);
    }

    LOGGER.debug("File download completed. File size = {}, Time taken = {} ms", file.length(),
        System.currentTimeMillis() - startTime);

    return file.getPath();
  }

  /**
   * Download file available in web target
   *
   * @param target web target
   * @param destFileName name of the file
   * @param destDirectory file path
   * @return file path including file name
   * @throws ApicWebServiceException any error while downloading file
   */
  protected String downloadFile(final WebTarget target, final String destFileName, final String destDirectory)
      throws ApicWebServiceException {

    LOGGER.debug("Invoking get() to download file: target - {}", target);
    LOGGER.debug("  Inputs : File Name - {}; directory - {}", destFileName, destDirectory);

    long startTime = System.currentTimeMillis();

    ResponseHandler<InputStream> responseHandler = new ResponseHandler<>(this.clientConf, target, InputStream.class);
    File file = new File(destDirectory + File.separator + destFileName);

    try (InputStream inputStream = responseHandler.getResponse(MediaType.APPLICATION_OCTET_STREAM)) {
      writeData(destFileName, file, inputStream);
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(exp.getMessage(),
          ERROR_WHILE_DOWNLOADING_FILE_MSG + destFileName + " : " + exp.getMessage(), exp);
    }
    finally {
      retriveResponseHeaders(responseHandler.getServiceResposne());
    }

    LOGGER.debug("File download completed. File size = {} bytes, Time taken = {} ms", file.length(),
        System.currentTimeMillis() - startTime);

    return file.getPath();
  }


  /**
   * Write the data from the response input stream to file
   *
   * @param fileName file name
   * @param file ouput file
   * @param inputStream response stream
   * @throws ApicWebServiceException error while reading the stream
   */
  protected void writeData(final String fileName, final File file, final InputStream inputStream)
      throws ApicWebServiceException {
    try (FileOutputStream outputStream = new FileOutputStream(file)) {
      byte[] buffer = new byte[1024];

      int len;
      while ((len = inputStream.read(buffer)) > 0) {
        outputStream.write(buffer, 0, len);
      }
    }
    catch (Exception e) {
      throw new ApicWebServiceException(e.getMessage(),
          ERROR_WHILE_DOWNLOADING_FILE_MSG + fileName + " : " + e.getMessage(), e);
    }
  }

  /**
   * @param wsTarget
   * @return
   */
  private Builder createBuilder(final WebTarget wsTarget) {
    return createBuilder(wsTarget, MediaType.APPLICATION_JSON);
  }


  /**
   * @param wsTarget
   * @param mediaType
   * @return
   */
  private Builder createBuilder(final WebTarget wsTarget, final String mediaType) {
    Builder builder =
        wsTarget.request().header(HttpHeaders.ACCEPT_ENCODING, WsCommonConstants.ENCODING_GZIP).accept(mediaType);
    builder.header(WsCommonConstants.REQ_LANGUAGE, this.clientConf.getLanguage());
    builder.header(WsCommonConstants.REQ_TIMEZONE, this.clientConf.getTimezone());
    String cnsSessionId = this.clientConf.getCnsSessionId();
    if (cnsSessionId != null) {
      builder.header(WsCommonConstants.REQ_CNS_SESSION_ID, cnsSessionId);
    }
    return builder;
  }


  /**
   * Close the Multipart resource
   *
   * @param resource Multipart resource
   */
  protected void closeResource(final MultiPart resource) {
    if (resource != null) {
      try {
        resource.close();
      }
      catch (IOException exp) {
        LOGGER.error("Error while closing the resource. " + exp.getMessage(), exp);
      }
    }
  }

  /**
   * Close the Multipart resources
   *
   * @param resources Multipart resource
   */
  protected void closeResource(final Collection<? extends MultiPart> resources) {
    resources.forEach(this::closeResource);
  }


  /**
   * @param operation change operation
   * @param newDataCollection data collection, from which the message to be built
   */
  protected void displayMessage(final CHANGE_OPERATION operation, final Collection<IModel> newDataCollection) {

    if ((newDataCollection == null) || newDataCollection.isEmpty()) {
      return;
    }

    IModel model = newDataCollection.iterator().next();

    if (newDataCollection.size() == 1) {
      displayMessage(operation, model);
      return;
    }

    // Display multiple modification message
    StringBuilder message = new StringBuilder("Multiple ");
    message.append(ModelTypeRegistry.INSTANCE.getTypeOfModel(model).getTypeName()).append('s');

    // Append operation
    if (operation == CHANGE_OPERATION.CREATE) {
      message.append(" created");
    }
    else if (operation == CHANGE_OPERATION.UPDATE) {
      message.append(" updated");
    }
    else {
      message.append(" deleted");
    }

    message.append(" successfully");

    displayMessage(message.toString());

  }

  /**
   * @param operation operation
   * @param model model
   */
  protected void displayMessage(final CHANGE_OPERATION operation, final IModel model) {
    StringBuilder message = new StringBuilder(ModelTypeRegistry.INSTANCE.getTypeOfModel(model).getTypeName());
    message.append(" [").append(getObjectIdentifier(model)).append(']');

    // Append operation
    if (operation == CHANGE_OPERATION.CREATE) {
      message.append(" created");
    }
    else if (operation == CHANGE_OPERATION.UPDATE) {
      message.append(" modified");
    }
    else {
      message.append(" deleted");
    }

    message.append(" successfully");

    displayMessage(message.toString());

  }

  /**
   * Display message based on change data
   *
   * @param chDataSet change Data Set
   */
  protected void displayMessage(final Collection<ChangeData<IModel>> chDataSet) {
    // TODO interpret change data for display message
    displayMessage("Data modified successfully");
  }

  /**
   * Write a log message to the log view. For external tools, message is written to normal log
   *
   * @param message message
   */
  protected final void displayMessage(final String message) {
    if (LOGGER instanceof CDMLogger) {
      ((CDMLogger) LOGGER).info(message, Activator.PLUGIN_ID);
    }
    else {
      LOGGER.info(message);
    }
  }

  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param responseMapper response mapper
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R modify(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final IMapperChangeData responseMapper)
      throws ApicWebServiceException {
    R ret = post(wsTarget, input, genericRetType);

    Collection<ChangeData<IModel>> chDataSet = ModelParser.getChangeData(ret, responseMapper);
    (new ChangeHandler()).triggerLocalChangeEvent(new ArrayList<ChangeData<?>>(chDataSet));

    displayMessage(chDataSet);
    return ret;
  }


  /**
   * Define the object identifier to be added in the display message. <br>
   * Format : <code>&lt;property&gt; = '&lt;value&gt;', &lt;property2&gt; = '&lt;value2&gt;', ... </code> <br>
   * e.g. <code>Name = 'Attribute 1'</code>
   * <p>
   * Default Value : if model is of type <code>IDataObject</code>, then identifier is <br>
   * <code>Name = '&lt;getName()&gt;'</code><br>
   * else <br>
   * <code>ID = '&lt;getId()&gt;'</code><br>
   * <p>
   * Implementations can override the method to provide custom identifiers
   *
   * @param object model
   * @return object identifier
   */
  protected <O> String getObjectIdentifier(final O object) {
    String objIdentifier = object.toString();
    if (object instanceof IBasicObject) {
      objIdentifier = AbstractRestServiceClient.NAME_EQUAL_TO + ((IBasicObject) object).getName() + "'";
    }
    else if (object instanceof IModel) {
      objIdentifier = AbstractRestServiceClient.ID_EQUAL_TO + ((IModel) object).getId() + "'";
    }
    return objIdentifier;
  }


  /**
   * Compress the given file
   *
   * @param filePath file to be compressed, including path
   * @return full path of the compressed file
   * @throws ApicWebServiceException error during compression
   */
  protected final String compressFile(final String filePath) throws ApicWebServiceException {
    String zippedFilePath = null;
    try {
      zippedFilePath = ZipUtils.getZipPath(filePath);
      Files.deleteIfExists(Paths.get(zippedFilePath));
      ZipUtils.zip(filePath, zippedFilePath);
    }
    catch (IOException exp) {
      throw new ApicWebServiceException(WSErrorCodes.CLIENT_ERROR, "Error while zipping file - " + exp.getMessage(),
          exp);
    }
    return zippedFilePath;
  }

  /**
   * Delete files
   *
   * @param fileSet set of files to delete
   */
  protected final void deleteFiles(final Set<String> fileSet) {
    fileSet.forEach(file -> {
      try {
        Files.deleteIfExists(Paths.get(file));
      }
      catch (IOException exp) {
        LOGGER.warn(exp.getMessage(), exp);
      }
    });
  }

  /**
   * Wrapper method to get the async response
   *
   * @param <R> response type
   * @param future Asynchronous future object
   * @param retTypeClass return type class
   * @return response
   * @throws ApicWebServiceException error while retrieving response
   */
  protected final <R> R getResponseAsync(final Future<Response> future, final Class<R> retTypeClass)
      throws ApicWebServiceException {
    ResponseHandler<R> respHndlr = new ResponseHandler<>(this.clientConf, future, retTypeClass);
    return respHndlr.getResponseAsync();
  }

  /**
   * @param header header name
   * @return the headerMap
   */
  protected List<Object> getResponseHeader(final String header) {
    return null == this.responseHeaders.get(header) ? null : new ArrayList<>(this.responseHeaders.get(header));
  }


  /**
   * Method to provide the service ID
   *
   * @return the last service ID
   */
  public final String getLastServiceId() {
    List<Object> responseHeaderList = getResponseHeader(WsCommonConstants.RESP_HEADER_SERVICE_ID);
    return null != responseHeaderList ? responseHeaderList.get(0).toString() : null;
  }


  /**
   * Method to add response header information
   *
   * @param response as input
   */
  private void retriveResponseHeaders(final Response response) {
    if (response != null) {
      this.responseHeaders.putAll(response.getHeaders());
      LOGGER.debug("  Service ID = {}", getLastServiceId());
    }
  }

  /**
   * Clear response header inormation
   */
  private void clearResponseHeaderInformation() {
    this.responseHeaders.clear();
  }

}
