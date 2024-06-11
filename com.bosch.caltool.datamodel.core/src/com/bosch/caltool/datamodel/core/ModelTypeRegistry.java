/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author bne4cob
 */
public enum ModelTypeRegistry {
                               /**
                                * Singleton instance
                                */
                               INSTANCE;


  private static final String ERR_NULL_INPUT = "Input cannot be null";

  private final ConcurrentMap<Class<?>, IModelType> modelTypeMap = new ConcurrentHashMap<>();


  /**
   * Register this model type
   *
   * @param type model type
   */
  public void register(final IModelType type) {
    if (type == null) {
      throw new NullPointerException(ERR_NULL_INPUT);
    }

    String typeCode = type.getTypeCode();
    IModelType existing = doGetTypeByCode(typeCode);
    if (existing != null) {
      throw new IllegalArgumentException(
          "Another model type '" + existing + "' already registered with same type code - " + typeCode);
    }

    this.modelTypeMap.put(type.getTypeClass(), type);
  }

  /**
   * Retrieves the model type for the given class
   *
   * @param clazz model class
   * @return the entity type
   * @throws NullPointerException if <code>clazz</code> is null
   * @throws IllegalArgumentException if <code>clazz</code> is not registered
   */
  public IModelType getTypeByClass(final Class<?> clazz) {
    if (clazz == null) {
      throw new NullPointerException(ERR_NULL_INPUT);
    }
    IModelType ret = this.modelTypeMap.get(clazz);
    if (ret == null) {
      throw new IllegalArgumentException("Model type not registered for class " + clazz.getName());
    }
    return ret;
  }

  /**
   * Retrieves the model type for the given object
   *
   * @param obj IModel
   * @return IModelType
   * @throws NullPointerException if <code>obj</code> is null
   * @throws IllegalArgumentException if class of <code>obj</code> is not registered
   */
  public IModelType getTypeOfModel(final IModel obj) {
    if (obj == null) {
      throw new NullPointerException(ERR_NULL_INPUT);
    }
    return getTypeByClass(obj.getClass());
  }

  /**
   * Retrieves the model type for the given type code
   *
   * @param typeCode type code
   * @return model type
   */
  public IModelType getTypeByCode(final String typeCode) {
    if (typeCode == null) {
      throw new NullPointerException(ERR_NULL_INPUT);
    }
    IModelType ret = doGetTypeByCode(typeCode);
    if (ret == null) {
      throw new IllegalArgumentException("Invalid model type code " + typeCode);
    }
    return ret;
  }

  private IModelType doGetTypeByCode(final String typeCode) {
    return this.modelTypeMap.values().stream().filter(t -> Objects.equals(typeCode, t.getTypeCode())).findFirst()
        .orElse(null);
  }


}
