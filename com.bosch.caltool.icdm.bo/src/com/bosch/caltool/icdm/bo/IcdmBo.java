/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import com.bosch.caltool.dmframework.common.ObjectStore;
import com.bosch.caltool.icdm.cns.client.CnsDataProducerServiceClient;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.database.DatabaseInitializer;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.logger.JPALogger;

/**
 * @author bne4cob
 */
public final class IcdmBo {

  /**
   * @throws IcdmException error while initializing BO layer
   */
  public void initialize() throws IcdmException {
    ObjectStore store = ObjectStore.getInstance();

    EntityManagerFactory emf = store.getEntityManagerFactory();

    if ((emf == null) || !emf.isOpen()) {
      Properties dmProps = getDmProps();
      store.initialise(CDMLogger.getInstance(), JPALogger.getInstance(), dmProps);
      store.setCnsMessageType(CnsDataProducerServiceClient.class);

      DatabaseInitializer dbConnector = new DatabaseInitializer(CDMLogger.getInstance());
      emf = dbConnector.connect();
      store.setEntityManagerFactory(emf);
    }

  }

  /**
   *
   */
  public final void dispose() {
    // Close the database objects stored in ObjectStore
    ObjectStore.getInstance().closeDatabaseResources();
  }


  /**
   * Get the data model properties, to initialise the data model
   *
   * @return properties
   */
  private Properties getDmProps() {
    Properties dmProps = new Properties();

    dmProps.setProperty(ObjectStore.P_DB_SERVER, Messages.getString(ObjectStore.P_DB_SERVER));
    dmProps.setProperty(ObjectStore.P_DB_PORT, Messages.getString(ObjectStore.P_DB_PORT));
    dmProps.setProperty(ObjectStore.P_USER_NAME, "WebService");

    return dmProps;
  }

}
