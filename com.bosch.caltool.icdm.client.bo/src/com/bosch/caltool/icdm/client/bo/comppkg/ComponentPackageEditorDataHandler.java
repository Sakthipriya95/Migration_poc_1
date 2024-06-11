/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.comppkg;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.cns.ChangeData;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.bc.SdomBc;
import com.bosch.caltool.icdm.model.bc.SdomFc2bc;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.comppkg.CompPkgBc;
import com.bosch.caltool.icdm.model.comppkg.CompPkgData;
import com.bosch.caltool.icdm.model.comppkg.CompPkgFc;
import com.bosch.caltool.icdm.ws.rest.client.bc.SdomBcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.bc.SdomFc2bcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgBcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgFcServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author say8cob
 */
public class ComponentPackageEditorDataHandler extends AbstractClientDataHandler {

  /**
   * Only atmost 5 BC's can be mapped for a comp package
   */
  private static final int MAX_BC_COUNT = 5;

  /**
   * Only atmost 1 BC's can be mapped for a NE type comp package
   */
  private static final int MAX_BC_COUNT_NE = 1;

  /**
   * String for NE type
   */
  private static final String NE_TYPE = "NE";

  private CompPkgData compPkgData;

  private final CompPackage componentPackage;

  private final SortedSet<SdomBc> sdomBcSet = new TreeSet<>();

  /**
   * Constructor
   *
   * @param compPackage Component Package
   */
  public ComponentPackageEditorDataHandler(final CompPackage compPackage) {
    this.componentPackage = compPackage;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {

    registerCns(this::fetchCompPackage, MODEL_TYPE.COMP_PKG, this::getComponentPackage);

    registerCns(data -> ((CompPkgBc) CnsUtils.getModel(data)).getCompPkgId().longValue() == getComponentPackage()
        .getId().longValue(), this::loadCompPkgBCFCMap, MODEL_TYPE.COMP_PKG_BC);

    registerCns(this::checkCnsForCpFc, this::loadCompPkgBCFCMap, MODEL_TYPE.COMP_PKG_BC_FC);

    registerCnsCheckerForNodeAccess(MODEL_TYPE.COMP_PKG, this::getComponentPackage);
  }

  private boolean checkCnsForCpFc(final ChangeData<?> data) {
    Long chBcId = ((CompPkgFc) CnsUtils.getModel(data)).getCompBcId();
    boolean ret = false;
    for (CompPkgBc cpBc : getCompPkgData().getBcSet()) {
      if (CommonUtils.isEqual(cpBc.getId(), chBcId)) {
        ret = true;
        break;
      }
    }
    return ret;
  }

  /**
   * @return the componentPackage
   */
  public CompPackage getComponentPackage() {
    return this.componentPackage;
  }

  /**
  *
  */
  private void fetchCompPackage(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    CompPackage def;
    try {
      def = new CompPkgServiceClient().getCompPackageById(getComponentPackage().getId());
      CommonUtils.shallowCopy(getComponentPackage(), def);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

  }

  /**
   * @param bcId bc Id
   * @return SdomFc2bc
   * @throws ApicWebServiceException service error
   */
  public SdomFc2bc getSdomFc2Bc(final long bcId) throws ApicWebServiceException {
    return new SdomFc2bcServiceClient().getById(bcId);
  }

  /**
   * @return true if the component package is modifiable
   */
  public boolean isModifiable() {
    return new CompPkgBO(getComponentPackage()).isModifiable();
  }

  /**
   * Check if any more BC's can be added to this comp package
   *
   * @param compPackage
   * @return true if number of bc's is less than max limit
   */
  public boolean canAddBCs() {
    int limit = NE_TYPE.equals(getComponentPackage().getCompPkgType()) ? MAX_BC_COUNT_NE : MAX_BC_COUNT;
    return getCompPkgBCSize() < limit;
  }

  private int getCompPkgBCSize() {
    return getCompPkgData().getBcSet().size();
  }

  /**
   * @param compPkgBc
   * @throws ApicWebServiceException
   */
  public void deleteBC(final CompPkgBc compPkgBc) throws ApicWebServiceException {
    new CompPkgBcServiceClient().delete(compPkgBc);
  }

  /**
   * @param compPkgFc
   * @throws ApicWebServiceException
   */
  public void deleteFC(final CompPkgFc compPkgFc) throws ApicWebServiceException {
    new CompPkgFcServiceClient().delete(compPkgFc);
  }


  /**
   * @param compPkgFc CompPkgFc to create
   * @throws ApicWebServiceException error in service call
   */
  public void insertFC(final CompPkgFc compPkgFc) throws ApicWebServiceException {
    new CompPkgFcServiceClient().create(compPkgFc);
  }

  /**
   * @param compPkgBc
   * @return
   */
  public Set<CompPkgFc> getFCSetforSelectedBC(final CompPkgBc compPkgBc) {
    if (getCompPkgData().getFcMap().containsKey(compPkgBc.getId())) {
      return getCompPkgData().getFcMap().get(compPkgBc.getId());
    }
    return null;
  }

  /**
   * Method for CNS
   *
   * @param chDataInfoMap
   * @throws ApicWebServiceException
   */
  private void loadCompPkgBCFCMap(final Map<Long, ChangeDataInfo> chDataInfoMap) {
    try {
      this.compPkgData = new CompPkgBcServiceClient().getCompBcFcByCompId(getComponentPackage().getId());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getLocalizedMessage(), e);
    }
  }

  /**
   * To Add the BC to CompPkg
   *
   * @param pkgBc as input
   * @throws ApicWebServiceException as exception
   */
  public void insertBC(final CompPkgBc pkgBc) throws ApicWebServiceException {
    new CompPkgBcServiceClient().create(pkgBc);
  }

  /**
   * To Update the sq no of BCs
   *
   * @param pkgBc as input
   * @param isUp as input
   * @throws ApicWebServiceException as Exception
   */
  public void updateBCSeqNo(final CompPkgBc pkgBc, final boolean isUp) throws ApicWebServiceException {
    new CompPkgBcServiceClient().update(pkgBc, isUp);
  }


  /**
   * @return the compPkgData
   */
  public CompPkgData getCompPkgData() {
    if (this.compPkgData == null) {
      loadCompPkgBCFCMap(null);
    }
    return this.compPkgData;
  }

  /**
   * @return the sdomBcSet
   */
  public SortedSet<SdomBc> getSdomBcSet() {
    if (this.sdomBcSet.isEmpty()) {
      try {
        this.sdomBcSet.addAll(new SdomBcServiceClient().getAllDistinctBcName());
      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      }
    }
    return this.sdomBcSet;
  }


}
