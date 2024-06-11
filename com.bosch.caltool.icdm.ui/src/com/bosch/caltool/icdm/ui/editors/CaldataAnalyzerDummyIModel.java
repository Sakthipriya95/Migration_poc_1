/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ui.editors;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.datamodel.core.ModelTypeRegistry;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ui.Activator;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author bne4cob
 */
enum CaldataAnalyzerDummyIModel implements IModel {
                                                   /**
                                                    * Instance
                                                    */
                                                   INSTANCE;

  private Long id;

  /**
   *
   */
  private static final long serialVersionUID = -1506845684915657539L;


  /**
   * @throws ApicWebServiceException error while retrieving ID
   */
  CaldataAnalyzerDummyIModel() {
    ModelTypeRegistry.INSTANCE.register(new IModelType() {

      /**
       *
       */
      private static final long serialVersionUID = -4627235857782121458L;

      @Override
      public String getTypeName() {
        return "Calibration Data Analyzer";
      }

      @Override
      public String getTypeCode() {
        return "CALDATAANALYZER";
      }

      @Override
      public Class<?> getTypeClass() {
        return CaldataAnalyzerDummyIModel.class;
      }

      @Override
      public int getOrder() {
        return -1;
      }
    });
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    if ((this.id == null) || (this.id == 0L)) {
      try {
        this.id = Long.valueOf(new CommonDataBO().getParameterValue(CommonParamKey.CALDATAANALYZER_NODE_ID));
      }
      catch (NumberFormatException | ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, Activator.PLUGIN_ID);

      }
    }
    return this.id;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long objId) {
    // Not applicable
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return 0L;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    // Not applicable
  }

}
