/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.util;

import java.lang.reflect.InvocationTargetException;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;

/**
 * @author rgo7cob
 */
public class LoaderProvider extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData serviceData
   */
  public LoaderProvider(final ServiceData serviceData) {
    super(serviceData);

  }

  /**
   * @param modelType modelType
   * @return the loader type
   * @throws IcdmException IcdmException
   */
  public AbstractBusinessObject<?, ?> createInstance(final String modelType) throws IcdmException {

    IModelType iType = null;
    String loaderName = null;

    try {
      iType = ModelTypeRegistry.INSTANCE.getTypeByCode(modelType);

      String modelClassName = iType.getTypeClass().getName();
      loaderName = modelClassName.replace("model", "bo") + "Loader";
      getLogger().debug("ModelType : {} & Model class : {} - Computed Loader name = {}", modelType, modelClassName,
          loaderName);

      // Create instance
      return (AbstractBusinessObject<?, ?>) Class.forName(loaderName).getConstructor(ServiceData.class)
          .newInstance(getServiceData());

    }
    catch (IllegalArgumentException e) {
      throw new IcdmException(e.getMessage(), e);
    }
    catch (NoSuchMethodException e) {
      throw new IcdmException("Missing constructor for the identified loader '" + loaderName +
          "(ServiceData)' for model type. " + modelType, e);
    }
    catch (InstantiationException | IllegalAccessException | InvocationTargetException | SecurityException e) {
      throw new IcdmException("Error while creating loader '" + loaderName + "'. " + e.getMessage(), e);
    }
    catch (ClassNotFoundException e) {
      throw new IcdmException(
          "Missing loader for the model type " + modelType + " . Attempted with '" + loaderName + "'", e);
    }

  }

}
