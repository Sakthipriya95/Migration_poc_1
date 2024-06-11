/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.common.exception.IcdmRuntimeException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.ws.rest.client.cns.CHANGE_SOURCE;
import com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent;

/**
 * Manages CNS actions
 *
 * @author bne4cob
 */
class CnsActionManager {

  /**
   * Map of refresh actions. Key - model type, value - refresh action
   */
  private final ConcurrentMap<IModelType, ICnsRefreshAction> cnsRefresherActionMap = new ConcurrentHashMap<>();

  /**
   * Map of refresh actions for local notifications. Key - model type, value - refresh action
   */
  private final ConcurrentMap<IModelType, ILocalCnsRefreshAction> cnsRefresherActionLocalMap =
      new ConcurrentHashMap<>();


  /**
   * Validations : Checkers should be registered for all registered actions
   *
   * @param checkerMgr CNS checker manager
   */
  public void validateCnsRegistrations(final CnsCheckerManager checkerMgr) {
    Set<IModelType> actions = new HashSet<>(this.cnsRefresherActionMap.keySet());
    actions.addAll(this.cnsRefresherActionLocalMap.keySet());

    Set<IModelType> missingCheckers =
        CommonUtils.getDifference(actions, checkerMgr.getCnsApplicabilityCheckersChangeData().keySet());
    if (!missingCheckers.isEmpty()) {
      throw new IcdmRuntimeException("CNS checker(s) missing for the registered action(s) of type(s) : " +
          missingCheckers.stream().map(IModelType::toString).collect(Collectors.joining(", ")));
    }

  }

  /**
   * Register the CNS action for the given model types, for any instance.
   * <p>
   * Note : CNS checkers should be set separately.
   *
   * @param action CNS refresh action
   * @param modelTypeArr model types
   */
  public final void registerCnsAction(final ICnsRefreshAction action, final IModelType... modelTypeArr) {
    Arrays.stream(modelTypeArr).forEach(type -> registerCnsRefreshAction(type, action));
  }

  /**
   * Register the CNS refresh action for the given model type. Only one action is allowed
   *
   * @param modelType model type
   * @param action CNS refresh action
   */
  public final void registerCnsRefreshAction(final IModelType modelType, final ICnsRefreshAction action) {
    assertActionNotRegistered(modelType, action);
    this.cnsRefresherActionMap.put(modelType, action);

    getLogger().debug("{} : CNS refresh action registered for model type {}. Action - {}", this, modelType, action);

  }

  /**
   * Register the Local CNS refresh action for the given model type. Only one action is allowed
   *
   * @param modelTypeArr model types
   * @param action Local CNS refresh action
   */
  public final void registerCnsActionLocal(final ILocalCnsRefreshAction action, final IModelType... modelTypeArr) {
    Arrays.stream(modelTypeArr).forEach(type -> registerCnsRefreshAction(type, action));
  }

  /**
   * Register the Local CNS refresh action for the given model type. Only one action is allowed
   *
   * @param modelType model type
   * @param action Local CNS refresh action
   */
  public final void registerCnsRefreshAction(final IModelType modelType, final ILocalCnsRefreshAction action) {
    assertLocalActionNotRegistered(modelType, action);
    this.cnsRefresherActionLocalMap.put(modelType, action);

    getLogger().debug("{} : Local CNS refresh action registered for model type {}. Action - {}", this, modelType,
        action);

  }

  /**
   * Action cannot be null. Only one action can be registered for a model type. Both refresh action and scoped refresh
   * action cannot be registered together
   *
   * @param modelType model type
   * @param action action global or scoped refresh action
   */
  private void assertActionNotRegistered(final IModelType modelType, final Object action) {
    assertNotNull("CNS Refresh Action", modelType, action);

    if (this.cnsRefresherActionMap.containsKey(modelType)) {
      throw new IllegalArgumentException(
          "Another CNS refresh action is already registered for the model type - " + modelType);
    }
  }

  /**
   * Action cannot be null. Only one local action can be registered for a model type
   *
   * @param modelType model type
   * @param action action
   */
  private void assertLocalActionNotRegistered(final IModelType modelType, final ILocalCnsRefreshAction action) {
    assertNotNull("CNS Local Refresh Action", modelType, action);
    if (this.cnsRefresherActionLocalMap.containsKey(modelType)) {
      throw new IllegalArgumentException(
          "Another local CNS refresh action is already registered for the model type - " + modelType);
    }
  }

  private void assertNotNull(final String objType, final IModelType modelType, final Object object) {
    if (object == null) {
      throw new IllegalArgumentException(objType + " cannot be null. Model type - " + modelType);
    }
  }

  /**
   * @return refresher
   */
  public final ICnsRefresherDce getCnsRefresherDce() {

    // sorted based on the order of enum definition
    SortedSet<IModelType> modelTypesWithActions = new TreeSet<>(new Comparator<IModelType>() {

      /**
       * {@inheritDoc}
       */
      @Override
      public int compare(final IModelType o1, final IModelType o2) {
        return ApicUtil.compare(o1.getOrder(), o2.getOrder());
      }
    });
    modelTypesWithActions.addAll(this.cnsRefresherActionMap.keySet());
    modelTypesWithActions.addAll(this.cnsRefresherActionLocalMap.keySet());

    // Filters : objects of the required model type is present in DCE
    Set<Object> executedActions = new HashSet<>();
    return dce -> modelTypesWithActions.stream()
        .filter(type -> !CommonUtils.isNullOrEmpty(dce.getConsChangeData().get(type)))
        .forEach(type -> executeAction(type, dce, executedActions));

  }

  /**
   * Execute the action for the model type
   *
   * @param type model type
   * @param dce display change event
   */
  private void executeAction(final IModelType type, final DisplayChangeEvent dce, final Set<Object> executedActions) {

    Object action = (dce.getSource() == CHANGE_SOURCE.LOCAL) && (this.cnsRefresherActionLocalMap.get(type) != null)
        ? this.cnsRefresherActionLocalMap.get(type) : this.cnsRefresherActionMap.get(type);

    getLogger().debug("Model type - {}. Action to execute - {}", type, action);

    // 2. One action is executed only once
    if ((action != null) && !executedActions.contains(action)) {
      if (action instanceof ILocalCnsRefreshAction) {
        dce.getConsChangeData().get(type).values().forEach(((ILocalCnsRefreshAction) action)::execute);
      }
      else {
        executeGlobalAction(type, (ICnsRefreshAction) action, dce);
      }

      executedActions.add(action);

      getLogger().debug("Action executed successfully - {}", action);
    }

  }

  /**
   * Executes global refresh action. Converts ChangeData to ChangeDataInfo
   *
   * @param type model type
   * @param action refresh action
   * @param dce input DCE
   */
  private void executeGlobalAction(final IModelType type, final ICnsRefreshAction action,
      final DisplayChangeEvent dce) {
    Map<Long, ChangeDataInfo> chInfoMap = dce.getConsChangeData().get(type).values().stream().map(ChangeDataInfo::new)
        .collect(Collectors.toMap(ChangeDataInfo::getObjId, i -> i));
    action.execute(chInfoMap);
  }

  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }

  int getLocalActionCount() {
    return this.cnsRefresherActionLocalMap.size();
  }

  int getActionCount() {
    return this.cnsRefresherActionMap.size();
  }

}
