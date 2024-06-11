/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.util.Collection;
import java.util.Map;
import java.util.function.Supplier;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.logger.CDMLogger;


/**
 * Abstract class for client data handlers
 *
 * @author bne4cob
 */
public abstract class AbstractClientDataHandler implements IClientDataHandler {

  /**
   * CNS Checker True
   */
  public static final ICnsApplicabilityCheckerChangeData CNS_CHKER_TRUE = CnsCheckerManager.CNS_CHKER_TRUE;

  /**
   * CNS Checker manager
   */
  private final CnsCheckerManager cnsCheckerManager = new CnsCheckerManager();

  /**
   * CNS actions manager
   */
  private final CnsActionManager cnsActionManager = new CnsActionManager();

  /**
   * Constructor. Registers CNS applicability checkers and refresh actions
   */
  protected AbstractClientDataHandler() {
    getLogger().debug("{} : CNS registration started", this);

    registerForCns();
    this.cnsActionManager.validateCnsRegistrations(this.cnsCheckerManager);

    getLogger().debug(
        "{} : CNS registration completed. Total checkers = {}, total local actions = {}, total global actions = {}",
        this, this.cnsCheckerManager.getCheckerCount(), this.cnsActionManager.getLocalActionCount(),
        this.cnsActionManager.getActionCount());
  }

  /**
   * Register checkers and actions for CNS event. Use 'registerCns...(...)' methods for registering checkers and
   * actions.
   * <p>
   * IMPORTANT : This is method will be invoked <br>
   * 1. only if super() is invoked from the constructor of the implemenations<br>
   * 2. before all actions in the implementations (e.g. initializing final fields)
   */
  protected void registerForCns() {
    // No default implementation
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final Map<IModelType, ICnsApplicabilityCheckerChangeData> getCnsApplicabilityCheckersChangeData() {
    return this.cnsCheckerManager.getCnsApplicabilityCheckersChangeData();
  }

  /**
   * Register the CNS checker and action for the given model type
   *
   * @param checker CNS applicability checker
   * @param action CNS refresh action
   * @param modelType model type
   */
  protected final void registerCns(final ICnsApplicabilityCheckerChangeData checker, final ICnsRefreshAction action,
      final IModelType modelType) {
    this.cnsCheckerManager.registerCnsChecker(modelType, checker);
    this.cnsActionManager.registerCnsRefreshAction(modelType, action);
  }

  /**
   * Register the CNS checker and action for the given instance of the given model type. Instance is identified using
   * the model provider. Used for a single model instance
   *
   * @param <D> type of IModel
   * @param action CNS refresh action
   * @param modelType model type
   * @param modelProvider Model provider
   */
  protected final <D extends IModel> void registerCns(final ICnsRefreshAction action, final IModelType modelType,
      final Supplier<D> modelProvider) {
    this.cnsCheckerManager.registerCnsChecker(modelType, modelProvider);
    this.cnsActionManager.registerCnsRefreshAction(modelType, action);
  }


  /**
   * Register the CNS checker and action for the given instances of the given model type. Instances are identified using
   * the model provider. Used for a collection of model instances
   *
   * @param <D> model type
   * @param action CNS refresh action
   * @param modelType model type
   * @param modelsProvider Model provider
   */
  protected final <D extends IModel> void registerCnsBulk(final ICnsRefreshAction action, final IModelType modelType,
      final Supplier<Collection<D>> modelsProvider) {
    this.cnsCheckerManager.registerCnsCheckerBulk(modelType, modelsProvider);
    this.cnsActionManager.registerCnsRefreshAction(modelType, action);
  }


  /**
   * Register the CNS action for the given model types, for any instance. CNS checker is defaulted to
   * {@link #CNS_CHKER_TRUE}.
   * <p>
   * This method can be used, if same action is to be triggered for multiple model types. The action will be executed
   * only once.
   *
   * @param action CNS refresh action
   * @param modelTypeArr model types
   */
  protected final void registerCns(final ICnsRefreshAction action, final IModelType... modelTypeArr) {
    this.cnsCheckerManager.registerCnsChecker(modelTypeArr);
    this.cnsActionManager.registerCnsAction(action, modelTypeArr);
  }


  /**
   * Register the CNS action for the given model types, for any instance.
   * <p>
   * Note : CNS checkers should be set separately.
   *
   * @param action CNS refresh action
   * @param modelTypeArr model types
   */
  protected final void registerCnsAction(final ICnsRefreshAction action, final IModelType... modelTypeArr) {
    this.cnsActionManager.registerCnsAction(action, modelTypeArr);
  }

  /**
   * Register the CNS applicability for the given model type, for any instance. CNS checker is defaulted to
   * {@link #CNS_CHKER_TRUE}.
   *
   * @param modelTypeArr model types
   */
  protected final void registerCnsChecker(final IModelType... modelTypeArr) {
    this.cnsCheckerManager.registerCnsChecker(modelTypeArr);
  }

  /**
   * Registers CNS checker for node access change of the current user for the given node ID
   *
   * @param modelType model type instance
   * @param modelProvider model provider
   * @param <D> type of IModel
   */
  protected final <D extends IModel> void registerCnsCheckerForNodeAccess(final IModelType modelType,
      final Supplier<D> modelProvider) {
    this.cnsCheckerManager.registerCnsCheckerForNodeAccess(modelType, modelProvider);
  }

  /**
   * Register the CNS applicability for the given model type. Only one checker is allowed
   *
   * @param modelType model type
   * @param checker CNS applicability checker
   */
  protected final void registerCnsChecker(final IModelType modelType,
      final ICnsApplicabilityCheckerChangeData checker) {
    this.cnsCheckerManager.registerCnsChecker(modelType, checker);
  }

  /**
   * Register the CNS checker for the given instance of the given model type. Instance is identified using the model
   * provider. Used for a single model instance
   *
   * @param <D> type of IModel
   * @param modelType model type
   * @param modelProvider Model provider
   */
  protected final <D extends IModel> void registerCnsChecker(final IModelType modelType,
      final Supplier<D> modelProvider) {
    this.cnsCheckerManager.registerCnsChecker(modelType, modelProvider);
  }

  /**
   * Register the CNS checker for the given instances of the given model type. Instances are identified using the model
   * provider. Used for a collection of model instances
   *
   * @param <D> model type
   * @param modelType model type
   * @param modelsProvider Model provider
   **/
  protected final <D extends IModel> void registerCnsCheckerBulk(final IModelType modelType,
      final Supplier<Collection<D>> modelsProvider) {
    this.cnsCheckerManager.registerCnsCheckerBulk(modelType, modelsProvider);
  }

  /**
   * Register the Local CNS refresh action for the given model type. Only one action is allowed
   *
   * @param modelTypeArr model types
   * @param action Local CNS refresh action
   */
  protected final void registerCnsActionLocal(final ILocalCnsRefreshAction action, final IModelType... modelTypeArr) {
    this.cnsActionManager.registerCnsActionLocal(action, modelTypeArr);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final ICnsRefresherDce getCnsRefresherDce() {
    return this.cnsActionManager.getCnsRefresherDce();
  }

  private ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }

}
