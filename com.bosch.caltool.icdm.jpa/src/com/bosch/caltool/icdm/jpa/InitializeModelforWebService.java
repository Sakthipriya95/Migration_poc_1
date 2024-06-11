/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.jpa;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.cdr.jpa.bo.CDRDataProvider;
import com.bosch.caltool.comppkg.jpa.bo.CPDataProvider;
import com.bosch.caltool.icdm.logger.CDMLogger;

/**
 * ICDM-2640 Loding data in the data model in a single thread for web service
 *
 * @author dja7cob
 */
public class InitializeModelforWebService {

  /**
   *
   */
  public void fetchData() {
    // Load all users
    fetchAllUsers();

    // Load units of parameters
    fetchUnits();

    fetchAllFeatures();

    fetchLinkNodes();

    // Load attributes, super groups, groups, values
    fetchAllAttrStructure();

    // After loading attributes, load use case item attribute mappings
    loadUseCaseItemAttributeMapping();

    // Load the a2l system constants details from db
    loadA2lData();

    // Load the data for CDR
    loadCDRData();

    // Load the data for Cmp Pkg
    loadCmpPkgData();
  }

  /**
   * Load the data for Component Package
   */
  private void loadCmpPkgData() {

    // Synchronization is enabled to load the a2l data, before allowing the information to be used by UI
    synchronized (IcdmJpaConstants.A2L_DATA_SYNC_LOCK) {
      CDMLogger.getInstance().debug("Loading ComponentPackageConfigurations started");

      // create cmp pkg data provider
      final CPDataProvider cpDataProvider = new CPDataProvider(CDMDataProvider.getInstance().getApicDataProvider(),
          CDMDataProvider.getInstance().getCdrDataProvider());
      // Set the data
      CDMDataProvider.getInstance().setCpDataProvider(cpDataProvider);

      CDMLogger.getInstance().debug("Loading ComponentPackageConfigurations completed");
    }
  }

  /**
   * Load the data for CDR
   */
  private void loadCDRData() {

    CDMLogger.getInstance().debug("Loading CDR configurations started");

    // create CDR data provider
    final CDRDataProvider cdrDataProvider = new CDRDataProvider(CDMDataProvider.getInstance().getApicDataProvider());
    // Set the data
    CDMDataProvider.getInstance().setCdrDataProvider(cdrDataProvider);

    CDMLogger.getInstance().debug("Loading CDR configurations completed");
  }

  /**
   * Load the a2l system constants details from db
   */
  private void loadA2lData() {

    // Synchronization is enabled to load the a2l data, before allowing the information to be used by UI
    synchronized (IcdmJpaConstants.A2L_DATA_SYNC_LOCK) {
      CDMLogger.getInstance().debug("Loading A2LConfigurations started");

      A2LDataProvider a2lDataProvider = new A2LDataProvider(CDMDataProvider.getInstance().getApicDataProvider());
      CDMDataProvider.getInstance().setA2lDataProvider(a2lDataProvider);

      CDMLogger.getInstance().debug("Loading A2LConfigurations completed");
    }
  }

  /**
   * Load Mapping between use case items and attributes
   */
  private void loadUseCaseItemAttributeMapping() {

    CDMLogger.getInstance().debug("Loading AllUseCaseItemAttributeMapping started");

    // Mapping between use case items and attributes
    CDMDataProvider.getInstance().getApicDataProvider().loadUseCaseItemAttributeMapping();

    CDMLogger.getInstance().debug("Loading AllUseCaseItemAttributeMapping completed");
  }

  /**
   * Load attributes, super groups, groups, values
   */
  private void fetchAllAttrStructure() {

    CDMLogger.getInstance().debug("Loading AllAttributes started");

    CDMDataProvider.getInstance().getApicDataProvider().loadAllAttrStructure();

    CDMLogger.getInstance().debug("Loading AllAttributes completed");
  }

  /**
   * Fetch nodetype and Id's which has links
   */
  private void fetchLinkNodes() {

    CDMLogger.getInstance().debug("Loading AlllinkNodes started");

    // Invoking this method, for the first time, fetches the nodeid's which has links from DB
    CDMDataProvider.getInstance().getApicDataProvider().getLinkNodeIds();

    CDMLogger.getInstance().debug("Loading AlllinkNodes started");
  }

  /**
   * Fetch SSD features
   */
  private void fetchAllFeatures() {

    CDMLogger.getInstance().debug("Loading AllSSDFeatures started");

    // Invoking this method, for the first time, fetches the features from DB
    CDMDataProvider.getInstance().getApicDataProvider().getAllFeatures();

    CDMLogger.getInstance().debug("Loading AllSSDFeatures completed");
  }

  /**
   * Fetch units of parameters
   */
  private void fetchUnits() {

    CDMLogger.getInstance().debug("Loading AllUnits started");

    // Invoking this method, for the first time, fetches the units from DB
    CDMDataProvider.getInstance().getApicDataProvider().getUnits();

    CDMLogger.getInstance().debug("Loading AllUnits completed");
  }

  /**
   * Fetch all users
   */
  private void fetchAllUsers() {

    CDMLogger.getInstance().debug("Loading AllAPICUsers started");

    CDMDataProvider.getInstance().getApicDataProvider().loadAllApicUsers();

    CDMLogger.getInstance().debug("Loading AllAPICUsers completed");
  }

}
