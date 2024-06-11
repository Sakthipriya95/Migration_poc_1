package com.bosch.caltool.icdm.client.bo.framework.impl;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;

public enum SAMPLE_MODEL_TYPE implements IModelType {
                                                     TYPE_A(A.class, "Type A"),
                                                     TYPE_B(B.class, "Type B");

  private Class<?> classz;
  private final String typeName;

  SAMPLE_MODEL_TYPE(final Class<?> classz, final String name) {
    this.classz = classz;
    this.typeName = name;
  }


  /**
   * @return the entity class of this enumeration value
   */
  @Override
  public Class<?> getTypeClass() {
    return this.classz;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getTypeName() {
    return this.typeName;
  }

  /**
   * Retrieves the entity type value for the given entity class
   *
   * @param clazz entity class
   * @return the entity type
   */
  public static SAMPLE_MODEL_TYPE getTypeByClass(final Class<?> clazz) {
    for (SAMPLE_MODEL_TYPE eType : SAMPLE_MODEL_TYPE.values()) {
      if (eType.getTypeClass().equals(clazz)) {
        return eType;
      }
    }
    return null;
  }

  /**
   * @param model IModel
   * @return IModelType
   */
  public static IModelType getTypeOfModel(final IModel model) {
    return model == null ? null : getTypeByClass(model.getClass());
  }

  /**
   * {@inheritDoc}
   */

  @Override
  public String getTypeCode() {
    return name();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int getOrder() {
    return -1;
  }

}