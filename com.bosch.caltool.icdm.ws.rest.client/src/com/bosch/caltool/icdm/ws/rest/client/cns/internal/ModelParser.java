/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ws.rest.client.cns.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.ws.rest.client.ClientConfiguration;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceRuntimeException;

/**
 * @author bne4cob
 */
public final class ModelParser {

  private static final ILoggerAdapter LOGGER = ClientConfiguration.getDefault().getLogger();

  private ModelParser() {
    // Pivate constructor
  }

  /**
   * @param data input data
   * @return Map of model. Key - model id, value - model
   */
  public static Map<Long, IModel> getModel(final Object data) {
    return getModelIMapper(data, null);
  }

  /**
   * @param data data to be converted to model. Could be an instance of <code>IModel</code>, an object, that can be
   *          converted to <code>IModel</code> using the mapper, or the collection/map of these two
   * @param mapper input model mapper, to convert a non Model to a model. See {@link IMapper} for details
   * @return Map of model. Key - model id, value - model
   */
  public static Map<Long, IModel> getModel(final Object data, final IMapper mapper) {

    return getModelIMapper(data, mapper);
  }

  /**
   * @param data
   * @param mapper
   * @return
   */
  private static Map<Long, IModel> getModelIMapper(final Object data, final IMapper mapper) {
    Map<Long, IModel> modelMap = new HashMap<>();

    Class<?> dataType = data.getClass();
    LOGGER.debug("Model Parser getting model. data type = {}", dataType);

    if (data instanceof IModel) {
      // Data is IModel
      IModel newObj = (IModel) data;
      modelMap.put(newObj.getId(), newObj);
    }
    else if (dataType.isArray()) {
      // Data is array
      modelMap.putAll(processCollection(Arrays.asList((Object[]) data), mapper));
    }
    else if (Collection.class.isAssignableFrom(dataType)) {
      // Data is collection
      modelMap.putAll(processCollection((Collection<?>) data, mapper));
    }
    else if (Map.class.isAssignableFrom(dataType)) {
      // Data is map
      modelMap.putAll(processCollection(((Map<?, ?>) data).values(), mapper));
    }
    else if (mapper != null) {
      // Data is a object, that can be mapped to model
      mapper.map(data).forEach((item) -> modelMap.put(item.getId(), item));
    }
    else {
      throw new ApicWebServiceRuntimeException(
          "Could not resolve type. Mapper not provided. If the data is not IModel/Array/Collection/Map, a mapper is required");
    }

    LOGGER.debug("Model Parser getting model. Data processed. Number of records = {}", modelMap.size());

    return modelMap;
  }

  /**
   * @param data to be converted to collection of ChangeData.
   * @param mapper input model mapper
   * @return Collection<ChangeData<IModel>>
   */
  public static Collection<ChangeData<IModel>> getChangeData(final Object data, final IMapperChangeData mapper) {

    Set<ChangeData<IModel>> changeDataSet = new HashSet<>();

    Class<?> dataType = data.getClass();
    LOGGER.debug("Model Parser getting model. data type = {}", dataType);

    if (dataType.isArray()) {
      // Data is array
      changeDataSet.addAll(processCollection(Arrays.asList((Object[]) data), mapper));
    }
    else if (Collection.class.isAssignableFrom(dataType)) {
      // Data is collection
      changeDataSet.addAll(processCollection((Collection<?>) data, mapper));
    }
    else if (Map.class.isAssignableFrom(dataType)) {
      // Data is map
      changeDataSet.addAll(processCollection(((Map<?, ?>) data).values(), mapper));
    }
    else if (mapper != null) {
      // Data is a object, that can be mapped to model
      changeDataSet.addAll(mapper.map(data));
    }
    else {
      throw new ApicWebServiceRuntimeException(
          "Could not resolve type. Mapper not provided. If the data is not IModel/Array/Collection/Map, a mapper is required");
    }

    LOGGER.debug("Model Parser getting changedata. Data processed. Number of records = {}", changeDataSet.size());

    return changeDataSet;
  }

  private static Map<Long, IModel> processCollection(final Collection<?> dataCol, final IMapper mapper) {
    Map<Long, IModel> modelMap = new HashMap<>();
    for (Object obj : dataCol) {
      if (obj instanceof IModel) {
        IModel newObj = (IModel) obj;
        modelMap.put(newObj.getId(), newObj);
      }
      else if (mapper != null) {
        mapper.map(obj).forEach(item -> modelMap.put(item.getId(), item));
      }
      else {
        throw new ApicWebServiceRuntimeException(
            "Could not resolve data element type. Mapper not provided. If the data is not IModel, a mapper is required");
      }
    }
    return modelMap;
  }

  private static Set<ChangeData<IModel>> processCollection(final Collection<?> dataCol,
      final IMapperChangeData mapper) {
    Set<ChangeData<IModel>> changeDataSet = new HashSet<>();
    for (Object obj : dataCol) {
      if (mapper != null) {
        changeDataSet.addAll(mapper.map(obj));
      }
      else {
        throw new ApicWebServiceRuntimeException(
            "Could not resolve data element type. Mapper not provided. If the data is not IModel, a mapper is required");
      }
    }
    return changeDataSet;
  }
}
