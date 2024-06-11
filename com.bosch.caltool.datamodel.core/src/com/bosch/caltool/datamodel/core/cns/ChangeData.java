/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core.cns;

import java.io.Serializable;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelInfo;

/**
 * @author bne4cob
 * @param <D> data type
 */
public class ChangeData<D extends IModel> implements Serializable {

  /**
   *
   */
  private static final long serialVersionUID = 4502501327955777743L;

  /**
   * The change event ID against which this data changed
   */
  private long changeEventId;

  /**
   * Primary key of the object being changed
   */
  private long objId;

  /**
   * Object type
   */
  private IModelType type;

  private ModelInfo reference;

  private ModelInfo parent;

  /**
   * Old Data - before change. For 'create', old data is null
   */
  private D oldData;

  /**
   * New data - after change. For 'delete', new data is null
   */
  private D newData;

  /**
   * Change operation
   */
  private CHANGE_OPERATION changeType;

  /**
   * @return the changeEventId
   */
  public long getChangeEventId() {
    return this.changeEventId;
  }


  /**
   * @param changeEventId the changeEventId to set
   */
  public void setChangeEventId(final long changeEventId) {
    this.changeEventId = changeEventId;
  }


  /**
   * @return the objId
   */
  public long getObjId() {
    return this.objId;
  }

  /**
   * Returns whether the object ID is real or dummy (generated)
   *
   * @return true if object Id is original
   */
  public boolean isRealObjId() {
    return this.objId > 0;
  }


  /**
   * @param objId the objId to set
   */
  public void setObjId(final long objId) {
    this.objId = objId;
  }


  /**
   * @return the type
   */
  public IModelType getType() {
    return this.type;
  }


  /**
   * @param type the type to set
   */
  public void setType(final IModelType type) {
    this.type = type;
  }


  /**
   * @return the reference
   */
  public ModelInfo getReference() {
    return this.reference;
  }


  /**
   * @param reference the reference to set
   */
  public void setReference(final ModelInfo reference) {
    this.reference = reference;
  }


  /**
   * @return the parent
   */
  public ModelInfo getParent() {
    return this.parent;
  }


  /**
   * @param parent the parent to set
   */
  public void setParent(final ModelInfo parent) {
    this.parent = parent;
  }


  /**
   * @return the oldData
   */
  public D getOldData() {
    return this.oldData;
  }


  /**
   * @param oldData the oldData to set
   */
  public void setOldData(final D oldData) {
    this.oldData = oldData;
  }


  /**
   * @return the newData
   */
  public D getNewData() {
    return this.newData;
  }


  /**
   * @param newData the newData to set
   */
  public void setNewData(final D newData) {
    this.newData = newData;
  }


  /**
   * @return the changeType
   */
  public CHANGE_OPERATION getChangeType() {
    return this.changeType;
  }


  /**
   * @param changeType the changeType to set
   */
  public void setChangeType(final CHANGE_OPERATION changeType) {
    this.changeType = changeType;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return super.toString() + " [changeEventId=" + this.changeEventId + ", objId=" + this.objId + ", type=" +
        this.type + ", oldDataPresent=" + (this.oldData != null) + ", newDataPresent=" + (this.newData != null) +
        ", changeType=" + this.changeType + "]";
  }


}
