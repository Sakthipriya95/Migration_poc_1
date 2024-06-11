/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.comppkg;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.client.bo.framework.AbstractClientDataHandler;
import com.bosch.caltool.icdm.client.bo.framework.ChangeDataInfo;
import com.bosch.caltool.icdm.client.bo.framework.CnsUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.comppkg.CompPackage;
import com.bosch.caltool.icdm.model.general.Link;
import com.bosch.caltool.icdm.ws.rest.client.comppkg.CompPkgServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.LinkServiceClient;

/**
 * @author bne4cob
 */
public class ComponentPackagesListViewClientDataHandler extends AbstractClientDataHandler {

  /**
   * sorted set of component package
   */
  private final SortedSet<CompPackage> allCompPkgSortedSet = new TreeSet<>();
  /**
   * set of comp pkg with linkset
   */
  private final Set<Long> compPkgWithLinkSet = new HashSet<>();

  /**
   * {@inheritDoc}
   */
  @Override
  protected void registerForCns() {
    registerCns(this::loadAllCompPackages, MODEL_TYPE.COMP_PKG);

    // Temporary arrangement, to refresh list, even if one child is refreshed
    registerCns(this::loadAllCompPackages, MODEL_TYPE.COMP_PKG_BC);
    registerCns(this::loadAllCompPackages, MODEL_TYPE.COMP_PKG_BC_FC);

    registerCns(data -> MODEL_TYPE.COMP_PKG.getTypeCode().equals(((Link) (CnsUtils.getModel(data))).getNodeType()),
        this::loadCpWithLinks, MODEL_TYPE.LINK);

  }

  /**
   * Method to load all comp pkg
   *
   * @param chDataMap
   */
  private void loadAllCompPackages(final Map<Long, ChangeDataInfo> chDataMap) {
    CDMLogger.getInstance().debug("Loading component packages .. ChDataSize = {}",
        chDataMap == null ? null : chDataMap.size());

    try {
      SortedSet<CompPackage> retSet = new CompPkgServiceClient().getAll();
      // Clear and refill only if service call is successfull
      this.allCompPkgSortedSet.clear();
      this.allCompPkgSortedSet.addAll(retSet);

      CDMLogger.getInstance().debug("All component packages loaded. Count = {}", retSet.size());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   * Method to get all comp pkgs
   *
   * @return all component packages in the system
   */
  public SortedSet<CompPackage> getAllCompPackages() {
    if (this.allCompPkgSortedSet.isEmpty()) {
      loadAllCompPackages(null);
    }
    return this.allCompPkgSortedSet;

  }

  /**
   * To create Component Package
   *
   * @param compPkg as input
   * @return compPackage created
   * @throws ApicWebServiceException exception
   */
  public CompPackage createCompPkg(final CompPackage compPkg) throws ApicWebServiceException {
    CompPackage ret = new CompPkgServiceClient().create(compPkg).getCompPackage();
    this.allCompPkgSortedSet.add(ret);
    return ret;
  }

  /**
   * To Update the component Package
   *
   * @param compPkg as input
   * @return CompPackage updated
   * @throws ApicWebServiceException exception
   */
  public CompPackage updateCompPkg(final CompPackage compPkg) throws ApicWebServiceException {
    CompPackage ret = new CompPkgServiceClient().update(compPkg);

    for (CompPackage cp : this.allCompPkgSortedSet) {
      if (CommonUtils.isEqual(cp.getId(), ret.getId())) {
        CommonUtils.shallowCopy(cp, ret);
        break;
      }
    }

    return ret;
  }


  /**
   * @return set of component package IDs with links
   */
  public Set<Long> getNodeWithLinks() {
    if (this.compPkgWithLinkSet.isEmpty()) {
      loadCpWithLinks(null);
    }
    return this.compPkgWithLinkSet;
  }

  /**
   * Method to load cp with links
   *
   * @param chDataMap
   */
  private void loadCpWithLinks(final Map<Long, ChangeDataInfo> chDataMap) {
    try {
      // clear and reload
      this.compPkgWithLinkSet.clear();
      this.compPkgWithLinkSet.addAll(new LinkServiceClient().getNodesWithLink(MODEL_TYPE.COMP_PKG));
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }
}
