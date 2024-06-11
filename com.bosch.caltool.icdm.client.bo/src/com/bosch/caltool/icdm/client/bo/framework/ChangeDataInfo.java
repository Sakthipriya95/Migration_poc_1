/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import com.bosch.caltool.datamodel.core.IMasterRefreshable;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelInfo;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;

/**
 * @author bne4cob
 * @param <D> I model
 */
public class ChangeDataInfo<D extends IModel> {

  /**
   * The change event ID against which this data changed
   */
  private final long changeEventId;

  /**
   * Primary key of the object being changed
   */
  private final long objId;

  /**
   * Old data for delete/'marked as invisible' operations.
   */
  private final D removedData;

  /**
   * Object type
   */
  private final IModelType type;

  /**
   * Reference object to identify the object, in case object id does not refer a valid entity ID
   */
  private final ModelInfo reference;

  /**
   * Parent object of the change data object. Can be used to identify the object, in case object id does not refer a
   * valid entity ID
   */
  private final ModelInfo parent;

  /**
   * Change operation
   */
  private final CHANGE_OPERATION changeType;
  /**
   * true if master refresh is applicable
   */
  private boolean isMasterRefreshApplicable;

  /**
   * create a change data info from change data
   *
   * @param chData change data
   */
  public ChangeDataInfo(final ChangeData<D> chData) {
    this.changeEventId = chData.getChangeEventId();
    this.changeType = chData.getChangeType();
    this.objId = chData.getObjId();
    this.removedData = chData.getOldData();
    this.parent = chData.getParent();
    this.reference = chData.getReference();
    this.type = chData.getType();
    if (chData.getNewData() instanceof IMasterRefreshable){
      this.isMasterRefreshApplicable = ((IMasterRefreshable) chData.getNewData()).isMasterRefreshApplicable();
    }
  }

  /**
   * @return the changeEventId
   */
  public long getChangeEventId() {
    return this.changeEventId;
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
   * @return the removedData
   */
  public D getRemovedData() {
    return this.removedData;
  }

  /**
   * @return the type
   */
  public IModelType getType() {
    return this.type;
  }

  /**
   * @return the reference
   */
  public ModelInfo getReference() {
    return this.reference;
  }

  /**
   * @return the parent
   */
  public ModelInfo getParent() {
    return this.parent;
  }

  /**
   * @return the changeType
   */
  public CHANGE_OPERATION getChangeType() {
    return this.changeType;
  }

  /**
   * @return the isMasterRefreshApplicable
   */
  public boolean isMasterRefreshApplicable() {
    return this.isMasterRefreshApplicable;
  }

}

