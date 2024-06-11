/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core.cns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.ModelInfo;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;

/**
 * Change data creator class
 *
 * @author bne4cob
 * @param <D> IModel
 */
public final class ChangeDataCreator<D extends IModel> {

  /**
   * Create change data for CREATE operation
   *
   * @param eventId change event ID
   * @param newData new Data
   * @return change data
   */
  public ChangeData<D> createDataForCreate(final Long eventId, final D newData) {
    ChangeData<D> chgData = new ChangeData<>();
    chgData.setChangeEventId(eventId);
    chgData.setChangeType(CHANGE_OPERATION.CREATE);
    chgData.setNewData(newData);
    chgData.setObjId(newData.getId());
    chgData.setType(ModelTypeRegistry.INSTANCE.getTypeOfModel(newData));

    return chgData;
  }

  /**
   * Create change data for CREATE operation for a collection of objects
   *
   * @param eventId change event ID
   * @param dataCollection collection of created data
   * @return list of change data
   */
  public List<ChangeData<D>> createDataForCreate(final Long eventId, final Collection<D> dataCollection) {
    return dataCollection.stream().map(d -> createDataForCreate(eventId, d)).collect(Collectors.toList());
  }

  /**
   * Create change data for UPDATE operation
   *
   * @param eventId change event ID
   * @param oldData old Data
   * @param newData new Data
   * @return change data
   */
  public ChangeData<D> createDataForUpdate(final Long eventId, final D oldData, final D newData) {
    ChangeData<D> chgData = new ChangeData<>();
    chgData.setChangeEventId(eventId);
    chgData.setChangeType(CHANGE_OPERATION.UPDATE);
    chgData.setOldData(oldData);
    chgData.setNewData(newData);
    chgData.setObjId(newData.getId());
    chgData.setType(ModelTypeRegistry.INSTANCE.getTypeOfModel(newData));

    return chgData;
  }

  /**
   * Create change data for UPDATE operation for a collection of objects
   *
   * @param eventId change event ID
   * @param oldDataMap map of old data
   * @param newDataMap map of new data
   * @return list of change data
   */
  public List<ChangeData<D>> createDataForUpdate(final Long eventId, final Map<Long, D> oldDataMap,
      final Map<Long, D> newDataMap) {
    if (oldDataMap.size() != newDataMap.size()) {
      throw new IllegalArgumentException("Old data map and new data map size is different");
    }

    List<ChangeData<D>> retList = new ArrayList<>();
    for (Entry<Long, D> oldEntry : oldDataMap.entrySet()) {
      D newData = newDataMap.get(oldEntry.getKey());
      if (newData == null) {
        throw new IllegalArgumentException("New data is not available in update mode for id " + oldEntry.getKey());
      }
      retList.add(createDataForUpdate(eventId, oldEntry.getValue(), newData));
    }
    return retList;
  }

  /**
   * Create change data for VISIBLE operation. If object's id is not available, a random negative number is assigned as
   * ID.
   *
   * @param newData new data
   * @param eventId change Event ID
   * @param parent parent object details
   * @param reference reference object details
   * @return change data
   */
  public ChangeData<D> createDataForVisibleMode(final Long eventId, final D newData, final ModelInfo parent,
      final ModelInfo reference) {

    return createDataForSpecialOperations(eventId, CHANGE_OPERATION.VISIBLE, null, newData, parent, reference);
  }

  /**
   * Create change data for INVISIBLE operation. If object's id is not available, a random negative number is assigned
   * as ID.
   *
   * @param eventId change Event ID
   * @param oldData old data
   * @param parent parent object details
   * @param reference reference object details
   * @return change data
   */
  public ChangeData<D> createDataForInvisibleMode(final Long eventId, final D oldData, final ModelInfo parent,
      final ModelInfo reference) {

    return createDataForSpecialOperations(eventId, CHANGE_OPERATION.INVISIBLE, oldData, null, parent, reference);
  }

  /**
   * Change data creation for special operations. Supported operations are - VISIBLE, INVISIBLE.
   *
   * @param eventId
   * @param oper
   * @param oldData
   * @param newData
   * @param parent
   * @param reference
   * @return change data
   */
  private ChangeData<D> createDataForSpecialOperations(final Long eventId, final CHANGE_OPERATION oper, final D oldData,
      final D newData, final ModelInfo parent, final ModelInfo reference) {

    ChangeData<D> chgData = new ChangeData<>();

    chgData.setChangeEventId(eventId);
    chgData.setChangeType(oper);
    chgData.setParent(parent);
    chgData.setReference(reference);

    // Validate and find the data object
    D data = null;
    if (oper == CHANGE_OPERATION.INVISIBLE) {
      if (oldData == null) {
        throw new IllegalArgumentException("Old data is mandatory for change operation " + oper);
      }
      chgData.setOldData(oldData);
      data = oldData;
    }
    else if (oper == CHANGE_OPERATION.VISIBLE) {
      if (newData == null) {
        throw new IllegalArgumentException("New data is mandatory for change operation " + oper);
      }
      chgData.setNewData(newData);
      data = newData;
    }
    else {
      throw new IllegalArgumentException("Operation " + oper + " is unsupported for this type of change data creation");
    }

    chgData.setObjId(data.getId() == null ? generatedDummyId() : data.getId());
    chgData.setType(ModelTypeRegistry.INSTANCE.getTypeOfModel(data));

    return chgData;
  }

  /**
   * @return a random ID in negative range
   */
  private long generatedDummyId() {
    return new Random().longs(Long.MIN_VALUE, 0).findFirst().getAsLong();
  }

  /**
   * Create change data for DELETE operation
   *
   * @param eventId change event ID
   * @param oldData old Data
   * @return change data
   */
  public ChangeData<D> createDataForDelete(final Long eventId, final D oldData) {
    ChangeData<D> chgData = new ChangeData<>();
    chgData.setChangeEventId(eventId);
    chgData.setChangeType(CHANGE_OPERATION.DELETE);
    chgData.setOldData(oldData);
    chgData.setObjId(oldData.getId() == null ? generatedDummyId() : oldData.getId());
    chgData.setType(ModelTypeRegistry.INSTANCE.getTypeOfModel(oldData));

    return chgData;
  }

  /**
   * Create change data for DELETE operation for a collection of objects
   *
   * @param eventId change event ID
   * @param dataCollection collection of deleted data
   * @return list of change data
   */
  public List<ChangeData<D>> createDataForDelete(final Long eventId, final Collection<D> dataCollection) {
    return dataCollection.stream().map(d -> createDataForDelete(eventId, d)).collect(Collectors.toList());
  }


}
