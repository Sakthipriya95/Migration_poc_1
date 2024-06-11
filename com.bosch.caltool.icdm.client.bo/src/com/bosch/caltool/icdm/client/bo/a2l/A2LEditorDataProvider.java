/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.client.bo.a2l;

import java.util.Map;

import com.bosch.calmodel.a2ldata.A2LFileInfo;
import com.bosch.caltool.icdm.client.bo.Activator;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.A2LBaseComponents;
import com.bosch.caltool.icdm.model.a2l.A2lWpMapping;
import com.bosch.caltool.icdm.model.a2l.ParamProperties;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LFileInfoServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.a2l.A2LParamPropsServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcA2lServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Initialises all relevant information related to A2l file.
 *
 * @author gge6cob
 */
public class A2LEditorDataProvider {

  /**
   * To display error message A2l Editor.
   *
   * @deprecated not used
   */
  @Deprecated
  private static final String A2L_WP_WARNING_MESSAGE =
      "The attributes 'Assignment SW2CAL (technical)' and 'Assignment SW2CAL Root Group' are not set in your PIDC. Therefore you cannot use any work package information in the A2L or review. If you want to use work packages (either FC2WP or PAL work packages), fill out those attributes.";

  /** The a 2 l file id. */
  private final long a2lFileId;

  /** The pidc A 2 LBO. */
  private final PidcA2LBO pidcA2LBO;

  /** The a 2 l editor data BO. */
  private final A2LFileInfoBO a2lFileInfoBO;

  private final A2LWPInfoBO a2lWpInfoBO;

  /**                                                 */
  private boolean childJobStatus;

  private boolean fetchParamPropflag;

  /**
   * Instantiates a new a 2 L editor data provider.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @param isToFetchParamProp if true, fetch param properties
   * @throws IcdmException the icdm exception
   */
  public A2LEditorDataProvider(final long pidcA2lId, final boolean isToFetchParamProp) throws IcdmException {
    this.pidcA2LBO = new PidcA2LBO(pidcA2lId, null);
    this.a2lFileId = this.pidcA2LBO.getPidcA2lId();
    this.a2lFileInfoBO = new A2LFileInfoBO(this.pidcA2LBO.getA2lFile(), null);
    this.a2lWpInfoBO = new A2LWPInfoBO(this.a2lFileInfoBO, this.pidcA2LBO);
    this.fetchParamPropflag = isToFetchParamProp;
    initializeA2lFileData();
  }

  /**
   * Instantiates a new a 2 L editor data provider.
   *
   * @param pidcA2LBO the pidc A 2 LBO
   * @throws IcdmException the icdm exception
   */
  public A2LEditorDataProvider(final PidcA2LBO pidcA2LBO) throws IcdmException {
    this.pidcA2LBO = pidcA2LBO;
    this.a2lFileId = this.pidcA2LBO.getPidcA2lId();
    this.a2lFileInfoBO = new A2LFileInfoBO(this.pidcA2LBO.getA2lFile(), null);
    this.a2lWpInfoBO = new A2LWPInfoBO(this.a2lFileInfoBO, this.pidcA2LBO);
    initializeA2lFileData();
  }

  /**
   * Instantiates a new a 2 L editor data provider.
   *
   * @param pidcA2LBO the pidc A 2 LBO
   * @param childJobStatus boolean
   */
  public A2LEditorDataProvider(final PidcA2LBO pidcA2LBO, final boolean childJobStatus) {
    this.pidcA2LBO = pidcA2LBO;
    this.a2lFileId = this.pidcA2LBO.getPidcA2lId();
    this.a2lFileInfoBO = new A2LFileInfoBO(this.pidcA2LBO.getA2lFile(), null);
    this.a2lWpInfoBO = new A2LWPInfoBO(this.a2lFileInfoBO, this.pidcA2LBO);
    this.childJobStatus = childJobStatus;
  }

  /**
   * Instantiates a new a 2 L editor data provider.
   *
   * @param pidcA2lId the pidc A 2 l id
   * @param pidcA2lFileExt the pidc A 2 l file ext
   * @throws IcdmException the icdm exception
   */
  public A2LEditorDataProvider(final long pidcA2lId, final PidcA2lFileExt pidcA2lFileExt) throws IcdmException {
    this.pidcA2LBO = new PidcA2LBO(pidcA2lId, pidcA2lFileExt);
    this.a2lFileId = this.pidcA2LBO.getPidcA2lId();
    this.a2lFileInfoBO = new A2LFileInfoBO(this.pidcA2LBO.getA2lFile(), null);
    this.a2lWpInfoBO = new A2LWPInfoBO(this.a2lFileInfoBO, this.pidcA2LBO);
    initializeA2lFileData();
  }

  /**
   * Initialize A 2 l file data.
   *
   * @throws IcdmException the icdm exception
   */
  private void initializeA2lFileData() throws IcdmException {
    getBaseComponents();
    if (this.fetchParamPropflag) {
      getA2lParamProperties();
    }
  }


  /**
   * @return the pidcA2LBO
   */
  public PidcA2LBO getPidcA2LBO() {
    return this.pidcA2LBO;
  }

  /**
   * Gets the a 2 l editor data BO.
   *
   * @return the a 2 l editor data BO
   */
  public A2LFileInfoBO getA2lFileInfoBO() {
    return this.a2lFileInfoBO;
  }

  /**
   * Gets the a 2 l param properties.
   *
   * @throws IcdmException the icdm exception
   */
  public void getA2lParamProperties() throws IcdmException {

    // Fetch a2l parameter properties using iCDM web service
    CDMLogger.getInstance().info("Fetching a2l parameter properties using iCDM web service. " + this.a2lFileId);

    try {
      A2LParamPropsServiceClient servClient = new A2LParamPropsServiceClient();
      // Fetch the data using iCDM web service
      Map<String, ParamProperties> paramProps = servClient.getA2LParamProps(this.pidcA2LBO.getA2lFile().getId());
      this.a2lFileInfoBO.setParamProps(paramProps);
      CDMLogger.getInstance().info("A2L parameter properties retrieved. Number of parameters = " + paramProps.size());
    }
    catch (ApicWebServiceException exp) {
      // If web service is failed, return cancel status
      CDMLogger.getInstance().error("A2L parameter properties not loaded!! " + exp.getMessage(), exp);
      throw new IcdmException("A2L parameter properties not loaded!! " + exp.getMessage(), exp);
    }
  }

  /**
   * Gets the a 2 l wp mapping.
   *
   * @throws IcdmException the icdm exception
   * @deprecated not used
   */
  @Deprecated
  public void getA2lWpMapping() throws IcdmException {
    A2lWpMapping a2lWpMapping = new A2lWpMapping();
    // Fetch a2l workpackages using iCDM web service
    CDMLogger.getInstance()
        .info("Fetching A2L Workpackage responsible details for pidcA2lId :" + this.pidcA2LBO.getPidcA2lId());
    try {
      a2lWpMapping = new PidcA2lServiceClient().getA2lWorkpackageMapping(this.pidcA2LBO.getPidcA2lId());
      this.a2lFileInfoBO.setA2lWpMapping(a2lWpMapping);
      if (a2lWpMapping.isFC2WPConfigMissingError() && CommonUtils.isNotEmptyString(a2lWpMapping.getErrorMessage())) {
        CDMLogger.getInstance().info(a2lWpMapping.getErrorMessage(), Activator.PLUGIN_ID);
      }
      else if (a2lWpMapping.isWPAttrMissingError() && CommonUtils.isNotEmptyString(a2lWpMapping.getErrorMessage())) {
        CDMLogger.getInstance().info(A2L_WP_WARNING_MESSAGE, Activator.PLUGIN_ID);
      }
    }
    catch (ApicWebServiceException exp) {
      // If web service is failed, return cancel status
      CDMLogger.getInstance().error(A2L_WP_WARNING_MESSAGE + exp.getMessage(), exp);
      throw new IcdmException(A2L_WP_WARNING_MESSAGE + exp.getMessage(), exp);
    }
  }

  /**
   * Gets the base components.
   *
   * @throws IcdmException the icdm exception
   */
  public void getBaseComponents() throws IcdmException {

    // Fetch a2l basecomponents using iCDM web service
    CDMLogger.getInstance().info("Fetching a2l BaseComponents using iCDM web service. A2L File ID = " + this.a2lFileId);

    try {
      // Fetch the data using iCDM web service
      Map<String, A2LBaseComponents> retMap =
          new A2LFileInfoServiceClient().getA2LBaseComponents(this.pidcA2LBO.getA2lFile().getId());
      this.a2lFileInfoBO.setA2lBcMap(retMap);
      CDMLogger.getInstance().info("A2L BaseComponents retrieved. Number of BaseComponents = " + retMap.size());
    }
    catch (ApicWebServiceException exp) {
      // If web service is failed, return cancel status
      CDMLogger.getInstance().error("A2L BaseComponents(BC-FCs) not loaded !! " + exp.getMessage(), exp);
      throw new IcdmException("A2L BaseComponents(BC-FCs) not loaded !! " + exp.getMessage(), exp);
    }
  }


  /**
   * Gets the a 2 l file info.
   *
   * @return the a 2 l file info
   * @throws IcdmException the icdm exception
   */
  public void getA2lFileInfo() throws IcdmException {
    CDMLogger.getInstance().info("Downloading A2L file started. A2L file : " + this.pidcA2LBO.getA2LFileName());

    A2LFileInfo a2lFileContents;
    if (this.pidcA2LBO.getA2lFile() != null) {
      try {
        a2lFileContents = new A2LFileInfoProviderClient().fetchA2LFileInfo(this.pidcA2LBO.getA2lFile());
        this.a2lFileInfoBO.setA2lFileInfo(a2lFileContents);
      }
      catch (DataException e) {
        CDMLogger.getInstance().error("Could not retrieve the A2L file!" + CommonUtilConstants.VCDM_CONN_SERVER_FAILURE,
            e);
        throw new IcdmException("Could not retrieve the A2L file!" + CommonUtilConstants.VCDM_CONN_SERVER_FAILURE, e);
      }
    }
  }

  /**
   * @return the childJobStatus
   */
  public boolean isChildJobStatus() {
    return this.childJobStatus;
  }

  /**
   * @param childJobStatus the childJobStatus to set
   */
  public void setChildJobStatus(final boolean childJobStatus) {
    this.childJobStatus = childJobStatus;
  }


  /**
   * @return the a2lWpInfoBO
   */
  public A2LWPInfoBO getA2lWpInfoBO() {
    return this.a2lWpInfoBO;
  }


  /**
   * @return the a2lFileId
   */
  public long getA2lFileId() {
    return this.a2lFileId;
  }


}
