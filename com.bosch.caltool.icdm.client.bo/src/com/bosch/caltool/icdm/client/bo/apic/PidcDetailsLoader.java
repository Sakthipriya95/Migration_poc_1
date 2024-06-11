/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.apic;

import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetailsInput;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVersionServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author rgo7cob
 */
public class PidcDetailsLoader {


  private final PidcDataHandler pidcDataHandler;
  
  /**
   * @param pidcDataHandler pidcDataHandler
   */
  public PidcDetailsLoader(final PidcDataHandler pidcDataHandler) {
    this.pidcDataHandler = pidcDataHandler;
  }

  /**
   * load the Pidc details
   *
   * @param versionId Long
   * @return PidcDataHandler
   */
  public PidcDataHandler loadDataModel(final Long versionId) {
    PidcVersionWithDetailsInput input = new PidcVersionWithDetailsInput();
    input.setPidcVersionId(versionId);
    loadDataModel(input);
    return this.pidcDataHandler;
  }

  /**
   * @param input PidcVersDtlsLoaderInput
   */
  public void loadDataModel(final PidcVersionWithDetailsInput input) {
    PidcVersionWithDetails pidcVersionWithDetails;
    try {
      pidcVersionWithDetails = new PidcVersionServiceClient().getPidcVersionWithDetails(input);
      if (pidcVersionWithDetails != null) {
        initializeDataHandler(pidcVersionWithDetails);
      }
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }
  }

  /**
   *to intialize the PidcVersionWithDetails if it is null 
   * @param pidcVersionWithDetails as input
   */
  public void loadDataModel(PidcVersionWithDetails pidcVersionWithDetails) {
      initializeDataHandler(pidcVersionWithDetails);
  }
  /**
   * @param pidcVersWithdetails object
   */
  private void initializeDataHandler(final PidcVersionWithDetails pidcVersionWithDetails) {

    // set pidc version
    this.pidcDataHandler.setPidcVersionInfo(pidcVersionWithDetails.getPidcVersionInfo());

    // set alias details
    this.pidcDataHandler.setAliasDefModel(pidcVersionWithDetails.getAliasDefnModel());

    // set all attribute map
    this.pidcDataHandler.setAttributeMap(pidcVersionWithDetails.getAllAttributeMap());

    // set all attribute value map
    this.pidcDataHandler.setAttributeValueMap(pidcVersionWithDetails.getAttributeValueMap());

    // set pidc version attribute map for all attributes
    this.pidcDataHandler.setPidcVersAttrMap(pidcVersionWithDetails.getPidcVersionAttributeMap());

    // set pidc version attribute map for defined attributes
    this.pidcDataHandler.setPidcVersAttrMapDefined(pidcVersionWithDetails.getPidcVersionAttributeDefinedMap());

    // set sub variant map
    this.pidcDataHandler.setSubVariantMap(pidcVersionWithDetails.getPidcSubVariantMap());

    // set variant map
    this.pidcDataHandler.setVariantMap(pidcVersionWithDetails.getPidcVariantMap());

    // set variant attribute map
    this.pidcDataHandler.setVariantAttributeMap(pidcVersionWithDetails.getPidcVariantAttributeMap());

    // set sub variant attribute map
    this.pidcDataHandler.setSubVariantAttributeMap(pidcVersionWithDetails.getPidcSubVariantAttributeMap());

    // set attribue group map
    this.pidcDataHandler.setAttributeGroupMap(pidcVersionWithDetails.getAllAttributeGroupMap());

    // set attribute super group map
    this.pidcDataHandler.setAttributeSuperGroupMap(pidcVersionWithDetails.getAllAttributeSuperGroup());

    // set characterisitc value map
    this.pidcDataHandler.setCharacteristicValueMap(pidcVersionWithDetails.getAllCharactersiticValueMap());

    // set characteristic map
    this.pidcDataHandler.setCharacteristicMap(pidcVersionWithDetails.getAllCharacteristicMap());

    // set pidc detail structure map
    this.pidcDataHandler.setPidcDetStructureMap(pidcVersionWithDetails.getPidcDetailsStructmap());

    // set links set
    this.pidcDataHandler.setLinks(pidcVersionWithDetails.getLinkSet());

    // set Attr val links set
    this.pidcDataHandler.setAttValLinks(pidcVersionWithDetails.getAttValLinks());

    // set predefined validity map
    this.pidcDataHandler.setPredefinedValidityMap(pidcVersionWithDetails.getPredefinedValidityMap());

    // set attribute dependencies map
    this.pidcDataHandler.setAttrDependenciesMap(pidcVersionWithDetails.getAttrDependenciesMap());

    // set referential attribute dependencies map
    this.pidcDataHandler.setAttrRefDependenciesMap(pidcVersionWithDetails.getAttrRefDependenciesMap());
    // set attribute dependency for all attribute
    this.pidcDataHandler.setAttrDependenciesMapForAllAttr(pidcVersionWithDetails.getAttrDependenciesMapForAllAttr());
    // set attribute dependent Value map
    this.pidcDataHandler.setAttributeDepValueMap(pidcVersionWithDetails.getAttrDepValMap());

    // set invisible attributes
    this.pidcDataHandler.setPidcVersInvisibleAttrSet(pidcVersionWithDetails.getPidcVersInvisibleAttrSet());
    this.pidcDataHandler.setVariantInvisbleAttributeMap(pidcVersionWithDetails.getVariantInvisbleAttributeMap());
    this.pidcDataHandler.setSubVariantInvisbleAttributeMap(pidcVersionWithDetails.getSubVariantInvisbleAttributeMap());

    // set mandatory attributes
    this.pidcDataHandler.setMandatoryAttrMap(pidcVersionWithDetails.getMandatoryAttrMap());

    this.pidcDataHandler.setAllLevelAttrMap(pidcVersionWithDetails.getAllLevelAttrMap());
    this.pidcDataHandler.setPreDefAttrValMap(pidcVersionWithDetails.getPreDefAttrValMap());
    this.pidcDataHandler.getPidcDetNodes().clear();

    // set project usecase specific attribute ids
    this.pidcDataHandler.setProjectUsecaseModel(pidcVersionWithDetails.getProjectUsecaseModel());

  }


}
