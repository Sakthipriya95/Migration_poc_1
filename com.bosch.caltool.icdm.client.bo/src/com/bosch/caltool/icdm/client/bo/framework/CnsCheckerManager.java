/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.framework;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Manages CNS checkers
 *
 * @author bne4cob
 */
class CnsCheckerManager {


  /**
   * CNS Checker True
   */
  public static final ICnsApplicabilityCheckerChangeData CNS_CHKER_TRUE = d -> true;

  /**
   * Map of checkers. Key - model type, value - checker
   */
  private final ConcurrentMap<IModelType, ICnsApplicabilityCheckerChangeData> cnsApplChkrMap =
      new ConcurrentHashMap<>();


  /**
   * @return all registered checkers
   */
  public final Map<IModelType, ICnsApplicabilityCheckerChangeData> getCnsApplicabilityCheckersChangeData() {
    return this.cnsApplChkrMap;
  }


  /**
   * Register the CNS applicability for the given model type, for any instance. CNS checker is defaulted to
   * CNS_CHKER_TRUE.
   *
   * @param modelTypeArr model types
   */
  public final void registerCnsChecker(final IModelType... modelTypeArr) {
    Arrays.stream(modelTypeArr).forEach(type -> registerCnsChecker(type, CNS_CHKER_TRUE));
  }

  /**
   * Registers CNS checker for node access change of the current user for the given node ID
   *
   * @param modelType model type instance
   * @param modelProvider model provider
   * @param <D> type of IModel
   */
  public final <D extends IModel> void registerCnsCheckerForNodeAccess(final IModelType modelType,
      final Supplier<D> modelProvider) {
    registerCnsChecker(MODEL_TYPE.NODE_ACCESS, data -> {
      NodeAccess acc = (NodeAccess) CnsUtils.getModel(data);
      Long curUserId = null;
      try {
        curUserId = new CurrentUserBO().getUserID();
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
      }
      return CommonUtils.isEqual(acc.getNodeId(), modelProvider.get().getId()) &&
          CommonUtils.isEqual(acc.getUserId(), curUserId) &&
          CommonUtils.isEqual(acc.getNodeType(), modelType.getTypeCode());
    });

  }

  /**
   * Register the CNS applicability for the given model type. Only one checker is allowed
   *
   * @param modelType model type
   * @param checker CNS applicability checker
   */
  public final void registerCnsChecker(final IModelType modelType, final ICnsApplicabilityCheckerChangeData checker) {

    assertCheckerNotRegistered(modelType, checker);

    this.cnsApplChkrMap.put(modelType, checker);

    getLogger().debug("{} : CNS Checker registered for model type {}. Action - {}", this, modelType, checker);

  }

  /**
   * Register the CNS checker for the given instance of the given model type. Instance is identified using the model
   * provider. Used for a single model instance
   *
   * @param <D> type of IModel
   * @param modelType model type
   * @param modelProvider Model provider
   */
  public final <D extends IModel> void registerCnsChecker(final IModelType modelType, final Supplier<D> modelProvider) {
    registerCnsChecker(modelType,
        data -> CommonUtils.isEqual(CnsUtils.getModel(data).getId(), modelProvider.get().getId()));
  }

  /**
   * Register the CNS checker for the given instances of the given model type. Instances are identified using the model
   * provider. Used for a collection of model instances
   *
   * @param <D> model type
   * @param modelType model type
   * @param modelsProvider Model provider
   **/
  public final <D extends IModel> void registerCnsCheckerBulk(final IModelType modelType,
      final Supplier<Collection<D>> modelsProvider) {

    registerCnsChecker(modelType, data -> {
      for (IModel model : modelsProvider.get()) {
        if (CommonUtils.isEqual(CnsUtils.getModel(data).getId(), model.getId())) {
          return true;
        }
      }
      return false;
    });
  }


  private void assertCheckerNotRegistered(final IModelType modelType,
      final ICnsApplicabilityCheckerChangeData checker) {
    if (this.cnsApplChkrMap.containsKey(modelType)) {
      throw new IllegalArgumentException("CNS Checker is already registered for the model type - " + modelType);
    }
    assertNotNull(modelType, checker);
  }

  private void assertNotNull(final IModelType modelType, final Object object) {
    if (object == null) {
      throw new IllegalArgumentException("CNS Checker cannot be null. Model type - " + modelType);
    }
  }


  /**
   * @return logger
   */
  private ILoggerAdapter getLogger() {
    return CDMLogger.getInstance();
  }


  /**
   * @return
   */
  int getCheckerCount() {
    return this.cnsApplChkrMap.size();
  }


}
