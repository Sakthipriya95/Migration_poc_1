/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client;

import java.util.Arrays;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeDataCreator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2l;
import com.bosch.caltool.icdm.model.util.ModelUtil;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cns.ChangeHandler;
import com.bosch.caltool.icdm.ws.rest.client.cns.internal.IMapper;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Abstract class for A2L WP Responsibility related service clients
 *
 * @author bru2cob
 */
public abstract class AbstractA2lWpRestServiceClient extends AbstractRestServiceClient {

  /**
   * @param moduleBase module base URI
   * @param serviceBase service base URI
   */
  protected AbstractA2lWpRestServiceClient(final String moduleBase, final String serviceBase) {
    super(moduleBase, serviceBase);
  }

  /**
   * @param wsTarget web target
   * @param input object to be created
   * @param oldPidcA2l oldPidcA2l
   * @return new object created
   * @throws ApicWebServiceException any exception in the server
   */
  protected <D extends IModel> D create(final WebTarget wsTarget, final D input, final PidcA2l oldPidcA2l)
      throws ApicWebServiceException {

    D newData = create(wsTarget, input);

    triggerPidcA2lChangeAsync(oldPidcA2l);

    return newData;

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
   * @param oldPidcA2l pidc A2L object available
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R create(final WebTarget wsTarget, final I input, final Class<R> retTypeClass,
      final IMapper responseMapper, final PidcA2l oldPidcA2l)
      throws ApicWebServiceException {

    R response = create(wsTarget, input, retTypeClass, responseMapper);

    triggerPidcA2lChangeAsync(oldPidcA2l);

    return response;
  }

  /**
   * @param wsTarget web target
   * @param input - input
   * @param genericRetType - custom return type
   * @param pidcA2l - pidc A2L object available
   * @return updated model
   * @throws ApicWebServiceException - exception from server
   */
  protected final <R, I> R updateModel(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final PidcA2l pidcA2l)
      throws ApicWebServiceException {

    R response = post(wsTarget, input, genericRetType);

    triggerPidcA2lChangeAsync(pidcA2l);

    return response;
  }


  /**
   * create the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param oldPidcA2l pidc A2L object available
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R create(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final PidcA2l oldPidcA2l)
      throws ApicWebServiceException {

    R newData = create(wsTarget, input, genericRetType);

    triggerPidcA2lChangeAsync(oldPidcA2l);

    return newData;
  }

  /**
   * Trigger a PIDC A2L data change event, asynchronously, if the given object has been updated in the server.
   *
   * @param oldPidcA2l pidc A2L object available
   */
  protected final void triggerPidcA2lChangeAsync(final PidcA2l oldPidcA2l) {
    new Thread(() -> doTriggerPidcA2lChange(oldPidcA2l)).start();
  }

  private void doTriggerPidcA2lChange(final PidcA2l oldPidcA2l) {
    try {
      PidcA2l newPidcA2l = new PidcA2lServiceClient().getById(oldPidcA2l.getId());
      if (!ModelUtil.isEqual(newPidcA2l.getVersion(), oldPidcA2l.getVersion())) {
        ChangeData<PidcA2l> pidcChgData =
            (new ChangeDataCreator<PidcA2l>()).createDataForUpdate(0L, oldPidcA2l, newPidcA2l);
        (new ChangeHandler()).triggerLocalChangeEvent(Arrays.asList(pidcChgData));
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }


  /**
   * @param wsTarget web target
   * @param input object to be updated
   * @param oldPidcA2l oldPidcA2l
   * @return updated object
   * @throws ApicWebServiceException any exception in the server
   */
  protected <D extends IModel> D update(final WebTarget wsTarget, final D input, final PidcA2l oldPidcA2l)
      throws ApicWebServiceException {

    D newData = update(wsTarget, input);

    triggerPidcA2lChangeAsync(oldPidcA2l);

    return newData;
  }

  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param oldPidcA2l pidc A2L object available
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R update(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final PidcA2l oldPidcA2l)
      throws ApicWebServiceException {

    R newData = update(wsTarget, input, genericRetType);

    triggerPidcA2lChangeAsync(oldPidcA2l);

    return newData;
  }


  /**
   * Update the data
   *
   * @param <R> response type
   * @param <I> the generic type
   * @param wsTarget web target to hit
   * @param input input put input of type I
   * @param genericRetType the generic ret type
   * @param pidcA2l PidcA2l object to update
   * @return response of type R
   * @throws ApicWebServiceException if any error occurs during the webservice call
   */
  protected final <R, I> R delete(final WebTarget wsTarget, final I input, final GenericType<R> genericRetType,
      final PidcA2l pidcA2l)
      throws ApicWebServiceException {

    R newData = post(wsTarget, input, genericRetType);

    triggerPidcA2lChangeAsync(pidcA2l);

    return newData;
  }


  /**
   * @param wsTarget target
   * @param input object to be deleted
   * @param oldPidcA2l oldPidcA2l
   * @throws ApicWebServiceException any exception in the server
   */
  protected <D extends IModel> void delete(final WebTarget wsTarget, final D input, final PidcA2l oldPidcA2l)
      throws ApicWebServiceException {

    delete(wsTarget, input);
    triggerPidcA2lChangeAsync(oldPidcA2l);
  }

}
