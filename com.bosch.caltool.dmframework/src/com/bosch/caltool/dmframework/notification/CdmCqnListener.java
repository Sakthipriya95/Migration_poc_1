/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import java.sql.SQLException;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener;
import org.eclipse.persistence.sessions.Session;

import com.bosch.caltool.dmframework.common.ObjectStore;


/**
 * Custom Chnage notificaion listener for iCDM. In addition to executing default functions, this listener also gives the
 * notifications to iCDM's notification framework by registering a custom listener
 * <code>CdmDatabaseChangeListener</code>
 * 
 * @author bne4cob
 */
public class CdmCqnListener extends OracleChangeNotificationListener {

  /**
   * {@inheritDoc}
   * <p>
   * Adds a new notification listener to trigger the data model changes and GUI notification.
   */
  @Override
  public final void register(final Session session) {
    super.register(session);

    final AbstractSession databaseSession = (AbstractSession) session;
    try {
      this.register.addListener(new CdmDatabaseChangeListener(this, databaseSession));
      ObjectStore.getInstance().getLogger()
          .info("Collective query notification listener initialised. Registration ID : " + this.register.getRegId());
    }
    catch (SQLException exception) {
      throw DatabaseException.sqlException(exception, databaseSession.getAccessor(), databaseSession, false);
    }

  }
}
