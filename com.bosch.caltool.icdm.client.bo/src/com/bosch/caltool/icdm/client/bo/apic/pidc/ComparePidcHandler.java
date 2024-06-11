/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic.pidc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcDetailsLoader;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.ProjectHandlerInit;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * @author pdh2cob
 */
public class ComparePidcHandler extends AbstractClientDataHandler {

  /**
   * List of objects to be compared
   */
  private List<IProjectObject> compareObjs = new ArrayList<>();

  /**
   * Map of pidcdata Handlers key - pidc version id, value - pidc data handler
   */
  private Map<Long, AbstractProjectObjectBO> compareObjectsHandlerMap = new HashMap<>();

  private PidcVersionBO pidcVersionBO;

  private final boolean versionCompare;


  /**
   * @param compareObjs - objects for comparison
   * @param projObjBO Project Object BO
   * @param versionCompare if true, then comparison is for pidc versions
   */
  public ComparePidcHandler(final List<IProjectObject> compareObjs, final AbstractProjectObjectBO projObjBO,
      final boolean versionCompare) {
    this.compareObjs = compareObjs;
    this.versionCompare = versionCompare;
    intializeHandler(versionCompare, projObjBO);
  }


  private void intializeHandler(final boolean isVersionCompare, final AbstractProjectObjectBO projObjBO) {

    for (IProjectObject iProjectObject : this.compareObjs) {
      PidcDataHandler dataHandler = new PidcDataHandler();
      PidcDetailsLoader loader = new PidcDetailsLoader(dataHandler);
      if (iProjectObject instanceof PidcVersion) {
        dataHandler = loader.loadDataModel(((PidcVersion) iProjectObject).getId());
        ProjectHandlerInit handlerInit = new ProjectHandlerInit((PidcVersion) iProjectObject,
            (PidcVersion) iProjectObject, dataHandler, ApicConstants.LEVEL_PIDC_VERSION);

        this.compareObjectsHandlerMap.put(((PidcVersion) iProjectObject).getId(), handlerInit.getProjectObjectBO());

      }
      else if (iProjectObject instanceof PidcVariant) {
        ProjectHandlerInit handlerInit =
            new ProjectHandlerInit(projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(), iProjectObject,
                projObjBO.getPidcDataHandler(), ApicConstants.LEVEL_PIDC_VARIANT);
        this.compareObjectsHandlerMap.put(((PidcVariant) iProjectObject).getId(), handlerInit.getProjectObjectBO());
      }
      else if (iProjectObject instanceof PidcSubVariant) {
        ProjectHandlerInit handlerInit =
            new ProjectHandlerInit(projObjBO.getPidcDataHandler().getPidcVersionInfo().getPidcVersion(), iProjectObject,
                projObjBO.getPidcDataHandler(), ApicConstants.LEVEL_PIDC_SUB_VARIANT);
        this.compareObjectsHandlerMap.put(((PidcSubVariant) iProjectObject).getId(), handlerInit.getProjectObjectBO());
      }
    }
    if (!isVersionCompare) {
      this.pidcVersionBO = (PidcVersionBO) projObjBO;
    }
    else {
      this.pidcVersionBO = (PidcVersionBO) this.compareObjectsHandlerMap.values().iterator().next();
    }
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCnsChecker(MODEL_TYPE.PROJ_ATTR, MODEL_TYPE.VAR_ATTR, MODEL_TYPE.SUBVAR_ATTR);
    registerCnsActionLocal(this::refreshProjAttr, MODEL_TYPE.PROJ_ATTR);
    registerCnsActionLocal(this::refreshVarAttr, MODEL_TYPE.VAR_ATTR);
    registerCnsActionLocal(this::refreshSubVarAttr, MODEL_TYPE.SUBVAR_ATTR);

    // pidc version handler for refresh is null during registration of cns. hence it doesnt work
  }

  /**
   * @param chData
   */
  private void refreshProjAttr(final ChangeData<?> chData) {

    if (this.versionCompare) {
      PidcVersionAttribute projAttr;
      
      //refresh when visibility changes 
      if (chData.getNewData() == null) {
        projAttr = (PidcVersionAttribute) chData.getOldData();
      }
      else {
        projAttr = (PidcVersionAttribute) chData.getNewData();
      }

      PidcVersionBO handler = (PidcVersionBO) this.compareObjectsHandlerMap.get(projAttr.getPidcVersId());
     //if there are multiple instances of compare editor opened
      if (handler != null) {
        handler.refreshProjAttr(chData);
      }
    }

  }

  /**
   * @param chData
   */
  private void refreshVarAttr(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshVarAttr(chData);
  }

  /**
   * @param chData
   */
  private void refreshSubVarAttr(final ChangeData<?> chData) {
    this.pidcVersionBO.refreshSubVarAttr(chData);

  }

  /**
   * @return the compareObjs
   */
  public List<IProjectObject> getCompareObjs() {
    return this.compareObjs;
  }


  /**
   * @param compareObjs the compareObjs to set
   */
  public void setCompareObjs(final List<IProjectObject> compareObjs) {
    this.compareObjs = compareObjs;
  }


  /**
   * @return the compareObjectsHandlerMap
   */
  public Map<Long, AbstractProjectObjectBO> getCompareObjectsHandlerMap() {
    return this.compareObjectsHandlerMap;
  }


  /**
   * @param compareObjectsHandlerMap the compareObjectsHandlerMap to set
   */
  public void setCompareObjectsHandlerMap(final Map<Long, AbstractProjectObjectBO> compareObjectsHandlerMap) {
    this.compareObjectsHandlerMap = compareObjectsHandlerMap;
  }


  /**
   * @return the pidcVersionBO
   */
  public PidcVersionBO getPidcVersionBO() {
    return this.pidcVersionBO;
  }

  /**
   * @return the versionCompare
   */
  public boolean isVersionCompare() {
    return this.versionCompare;
  }


}
