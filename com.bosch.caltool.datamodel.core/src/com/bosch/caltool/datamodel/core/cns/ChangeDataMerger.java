/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core.cns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;

/**
 * @author bne4cob
 */
public class ChangeDataMerger {

  private final Map<IModelType, Set<Long>> removedRecords = new HashMap<>();

  /**
   * Merge the change data to the change data map
   *
   * @param incomingData incoming data
   * @param changeDataMap change data map. key - primary key, value - change data
   */
  public void merge(final ChangeData<?> incomingData, final Map<Long, ChangeData<?>> changeDataMap) {
    long objId = incomingData.getObjId();
    IModelType type = incomingData.getType();

    ChangeData<?> existingData = changeDataMap.get(objId);
    if (existingData == null) {
      // If the data is already available in removedRecords, it means, the record is deleted.
      // Then other change data records can be skipped
      if (!this.removedRecords.computeIfAbsent(type, k -> new HashSet<>()).contains(objId)) {
        changeDataMap.put(objId, incomingData);
      }
    }
    else {
      ChangeData<?> mergedData = merge(incomingData, existingData);
      if (mergedData == null) {
        changeDataMap.remove(objId);
        this.removedRecords.computeIfAbsent(type, k -> new HashSet<>()).add(objId);
      }
      else {
        changeDataMap.put(objId, mergedData);
      }
    }
  }

  private ChangeData<?> merge(final ChangeData<?> incomingData, final ChangeData<?> existingData) {
    ChangeData<?> retData = null;

    switch (existingData.getChangeType()) {
      case CREATE:
        retData = mergeWithCreateOperation(incomingData, existingData);
        break;
      case UPDATE:
        retData = mergeWithUpdateOperation(incomingData, existingData);
        break;
      case DELETE:
        retData = mergeWithDeleteOperation(incomingData, existingData);
        break;
      case VISIBLE:
        // TODO
        break;
      case INVISIBLE:
        retData = mergeWithInvisibleOperation(incomingData, existingData);
        break;
      default:
        // No actions
        break;
    }

    return retData;
  }


  /**
   * @param incomingData
   * @param existingData
   * @return
   */
  private ChangeData<?> mergeWithInvisibleOperation(final ChangeData<?> incomingData,
      final ChangeData<?> existingData) {
    ChangeData<?> retData = null;
    long eventId = incomingData.getChangeEventId();
    CHANGE_OPERATION icOper = incomingData.getChangeType();
    ChangeDataCreator creator = new ChangeDataCreator();
    if (icOper == CHANGE_OPERATION.CREATE) {
      retData = creator.createDataForInvisibleMode(eventId, incomingData.getNewData(), existingData.getParent(),
          existingData.getReference());
    }
    else if (icOper == CHANGE_OPERATION.UPDATE) {
      IModel incmingOldData = incomingData.getOldData();
      IModel existingOldData = existingData.getOldData();
      // include latest object when both update and invisible operation is applicable for same data
      IModel oldData = incmingOldData.getVersion() > existingOldData.getVersion() ? incmingOldData : existingOldData;
      retData =
          creator.createDataForInvisibleMode(eventId, oldData, existingData.getParent(), existingData.getReference());
    }
    else if (icOper == CHANGE_OPERATION.DELETE) {
      retData = creator.createDataForDelete(eventId, existingData.getOldData());
    }
    else {
      // is this duplicate? a valid situation?
      retData = existingData;
      // No merging required
    }
   return retData;
  }


  private ChangeData<?> mergeWithDeleteOperation(final ChangeData<?> incomingData, final ChangeData<?> existingData) {
    ChangeData<?> retData = null;
    long eventId = incomingData.getChangeEventId();
    CHANGE_OPERATION icOper = incomingData.getChangeType();
    ChangeDataCreator creator = new ChangeDataCreator();

    if (icOper == CHANGE_OPERATION.CREATE) {
      // Ret data is null. No change data is required. Since data was created at one stage, which was removed later
    }
    else if ((icOper == CHANGE_OPERATION.UPDATE) || (icOper == CHANGE_OPERATION.INVISIBLE)) {
      retData = creator.createDataForDelete(eventId, incomingData.getOldData());
    }
    else {
      // TODO is this duplicate? a valid situation?
      retData = existingData;
      // No merging required
    }

    return retData;
  }

  private ChangeData<?> mergeWithUpdateOperation(final ChangeData<?> incomingData, final ChangeData<?> existingData) {
    ChangeData<?> retData = null;
    long eventId = incomingData.getChangeEventId();
    CHANGE_OPERATION icOper = incomingData.getChangeType();
    ChangeDataCreator creator = new ChangeDataCreator();

    if (icOper == CHANGE_OPERATION.CREATE) {
      retData = creator.createDataForCreate(eventId, existingData.getNewData());
    }
    else if (icOper == CHANGE_OPERATION.UPDATE) {
      IModel icOldData = incomingData.getOldData();
      IModel esOldData = existingData.getOldData();
      IModel oldData = icOldData.getVersion() < esOldData.getVersion() ? icOldData : esOldData;
      retData = creator.createDataForUpdate(eventId, oldData, incomingData.getNewData());
    }
    else if (icOper == CHANGE_OPERATION.INVISIBLE) {
      IModel icOldData = incomingData.getOldData();
      IModel esOldData = existingData.getOldData();
      // include latest object when both update and invisible operation is applicable for same data
      IModel oldData = icOldData.getVersion() > esOldData.getVersion() ? icOldData : esOldData;
      retData =
          creator.createDataForInvisibleMode(eventId, oldData, incomingData.getParent(), incomingData.getReference());
    }
    else {
      retData = creator.createDataForDelete(eventId, existingData.getOldData());
    }

    return retData;
  }

  /**
   * @param incomingData
   * @param existingData
   * @param retData
   * @param eventId
   * @param icOper
   * @param creator
   * @return
   */
  private ChangeData<?> mergeWithCreateOperation(final ChangeData<?> incomingData, final ChangeData<?> existingData) {
    ChangeData<?> retData = null;
    long eventId = incomingData.getChangeEventId();
    CHANGE_OPERATION icOper = incomingData.getChangeType();
    ChangeDataCreator creator = new ChangeDataCreator();

    if (icOper == CHANGE_OPERATION.CREATE) {
      // TODO is this duplicate? a valid situation?
      retData = existingData;
      // No merging required
    }
    else if (icOper == CHANGE_OPERATION.UPDATE) {
      retData = creator.createDataForCreate(eventId, incomingData.getNewData());
    }
    else if (icOper == CHANGE_OPERATION.INVISIBLE) {
      retData = creator.createDataForInvisibleMode(eventId, existingData.getNewData(), incomingData.getParent(),
          incomingData.getReference());
    }
    else {
      // Ret data is null. No change data is required. Since data was created at one stage, which was removed later
    }
    return retData;
  }
}
