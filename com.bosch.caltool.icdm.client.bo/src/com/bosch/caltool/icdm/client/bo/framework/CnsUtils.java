/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.PropertyUtils;

import com.bosch.caltool.datamodel.core.IDataObject;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.datamodel.core.cns.CHANGE_OPERATION;
import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.datamodel.core.cns.ChangeEvent;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.cns.CHANGE_SOURCE;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;

/**
 * @author bne4cob
 */
public final class CnsUtils {

  /**
   * Properties excluded from direct changes check
   */
  private static final Set<String> EXCLUDED_PROPS = new HashSet<>(
      Arrays.asList("VERSION", "OBJID", "ID", "CREATEDDATE", "MODIFIEDDATE", "CREATEDUSER", "MODIFIEDUSER", "CLASS"));


  private CnsUtils() {
    // private constructor for utility class
  }

  /**
   * Checks whether change data has changes w.r.t. local data
   *
   * @param chgData Change Data
   * @param localData Local Data
   * @return true if there are changes
   */
  public static boolean hasChanges(final ChangeData<?> chgData, final IModel localData) {

    if (chgData == null) {
      return false;
    }
    if ((localData != null) && (chgData.getObjId() != localData.getId().longValue())) {
      // change data is not for local data. The change is not relevant
      return false;
    }

    CHANGE_OPERATION changeType = chgData.getChangeType();
    if (changeType == CHANGE_OPERATION.CREATE) {
      return localData == null;
    }
    else if (changeType == CHANGE_OPERATION.DELETE) {
      return localData != null;
    }
    else if (changeType == CHANGE_OPERATION.UPDATE) {
      return (localData != null) && (chgData.getNewData().getVersion() > localData.getVersion());
    }
    return false;

  }

  /**
   * @param dce DisplayChangeEvent
   * @param type IModelType
   * @return true, if there are change data with direct changes of the given type
   */
  public static boolean hasDirectChanges(final DisplayChangeEvent dce, final IModelType type) {
    boolean localChange = dce.getSource() == CHANGE_SOURCE.LOCAL;
    Map<Long, ChangeData<?>> typeBasedChanges = dce.getConsChangeData().get(type);
    if (typeBasedChanges != null) {
      for (ChangeData<?> changeData : typeBasedChanges.values()) {
        if (CnsUtils.hasDirectChanges(changeData, localChange)) {
          return true;
        }
      }
    }
    return false;
  }

  /**
   * @param chgData ChangeData
   * @return true, if there are direct changes
   */
  public static boolean hasDirectChanges(final ChangeData<?> chgData) {
    return hasDirectChanges(chgData, false);
  }

  /**
   * @param chgData ChangeData
   * @return true, if there are direct changes
   */
  private static boolean hasDirectChanges(final ChangeData<?> chgData, final boolean localChange) {

    if ((chgData.getChangeType() == CHANGE_OPERATION.CREATE) || (chgData.getChangeType() == CHANGE_OPERATION.DELETE)) {
      return true;
    }

    if (((chgData.getNewData() == null) && (chgData.getOldData() != null)) ||
        ((chgData.getNewData() != null) && (chgData.getOldData() == null))) {
      return true;
    }

    PropertyDescriptor[] descArr = PropertyUtils.getPropertyDescriptors(chgData.getType().getTypeClass());

    Map<String, Object> oldDataProps = new HashMap<>();
    Map<String, Object> newDataProps = new HashMap<>();
    try {
      if (chgData.getOldData() != null) {
        oldDataProps = PropertyUtils.describe(chgData.getOldData());
      }
      if (chgData.getNewData() != null) {
        newDataProps = PropertyUtils.describe(chgData.getNewData());
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      CDMLogger.getInstance().warn(e.getMessage(), e);
    }


    for (PropertyDescriptor prop : descArr) {
      String propName = prop.getName();
      // Work around : for local changes, old data is updated data sent as input to service, which is not exactly old
      // data. So all fields are considered
      if (!localChange && EXCLUDED_PROPS.contains(propName.toUpperCase(Locale.getDefault()))) {
        continue;
      }
      if (!Objects.equals(oldDataProps.get(propName), newDataProps.get(propName))) {
        return true;
      }
    }

    return false;
  }

  /**
   * @param chgData change data
   * @return Map of changes<br>
   *         Key : property name<br>
   *         Value : Object array[2]. element[0] - new data, element[1] - old data
   */
  public static Map<String, Object[]> getDirectChanges(final ChangeData<?> chgData) {
    Map<String, Object[]> retMap = new HashMap<>();

    if (chgData.getChangeType() == CHANGE_OPERATION.DELETE) {
      return retMap;
    }

    Map<String, Object> oldDataProps = new HashMap<>();
    Map<String, Object> newDataProps = new HashMap<>();
    try {
      if (chgData.getOldData() != null) {
        oldDataProps = PropertyUtils.describe(chgData.getOldData());
      }
      if (chgData.getNewData() != null) {
        newDataProps = PropertyUtils.describe(chgData.getNewData());
      }
    }
    catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      CDMLogger.getInstance().warn(e.getMessage(), e);
    }

    PropertyDescriptor[] descArr = PropertyUtils.getPropertyDescriptors(chgData.getType().getTypeClass());
    for (PropertyDescriptor prop : descArr) {
      String propName = prop.getName();
      if (EXCLUDED_PROPS.contains(propName.toUpperCase(Locale.getDefault()))) {
        continue;
      }
      if (!Objects.equals(oldDataProps.get(propName), newDataProps.get(propName))) {
        retMap.put(propName, new Object[] { newDataProps.get(propName), oldDataProps.get(propName) });
      }
    }


    return retMap;
  }

  /**
   * Retrieve the model object inside the change data. If new data is available, it is retrieved, else the old data
   *
   * @param chData ChangeData
   * @return model model inside change data
   */
  public static <D extends IModel> D getModel(final ChangeData<D> chData) {
    return chData.getNewData() == null ? chData.getOldData() : chData.getNewData();
  }

  /**
   * @param event change event
   * @return summary of change event
   */
  public static String getChangeSummary(final ChangeEvent event) {
    return buildChangeInfo(event, false);
  }

  private static String buildChangeInfo(final ChangeEvent event, final boolean addAll) {

    String ret;
    Set<Map<Long, ChangeData<?>>> chDataSet = new HashSet<>(event.getChangeDataMap().values());

    if (chDataSet.size() == 1) {
      Map<Long, ChangeData<?>> typeChDataMap = chDataSet.iterator().next();
      if (typeChDataMap.size() == 1) {
        ret = buildSingleRecChangeDataInfo(typeChDataMap);
      }
      else {
        ret = buildMultipleRecChangeDataInfo(event, addAll);
      }
    }
    else {
      ret = buildMultipleRecChangeDataInfo(event, addAll);
    }

    return ret;
  }

  private static String buildSingleRecChangeDataInfo(final Map<Long, ChangeData<?>> typeChDataMap) {
    ChangeData<?> data = typeChDataMap.values().iterator().next();
    StringBuilder retStr = new StringBuilder(data.getType().getTypeName());

    IModel model = getModel(data);
    if (model instanceof IDataObject) {
      retStr.append(" : Name = ").append(((IDataObject) model).getName());
    }
    else {
      retStr.append(", ID = ").append(model.getId());
    }

    retStr.append(", Operation = ").append(data.getChangeType().name());

    return retStr.toString();
  }

  private static String buildMultipleRecChangeDataInfo(final ChangeEvent event, final boolean addAll) {

    // Sort model types
    TreeSet<IModelType> modelTypeSet = event.getChangeDataMap().keySet().stream()
        .map(ModelTypeRegistry.INSTANCE::getTypeByCode).collect(Collectors.toCollection(TreeSet::new));

    StringBuilder retStr = new StringBuilder("Multiple changes : ");
    if (addAll) {
      retStr.append("\n ");
    }
    int recCount = 0;
    for (IModelType modelType : modelTypeSet) {
      if (recCount > 0) {
        if (addAll) {
          retStr.append("\n ");
        }
        else {
          retStr.append(" ..");
          break;
        }
      }

      retStr.append(modelType.getTypeName()).append('(')
          .append(event.getChangeDataMap().get(modelType.getTypeCode()).size()).append(')');

      recCount++;
    }

    return retStr.toString();
  }

  /**
   * @param event change event
   * @return details of change event
   */
  public static String getChangeDetails(final ChangeEvent event) {
    return buildChangeInfo(event, true);
  }

}
