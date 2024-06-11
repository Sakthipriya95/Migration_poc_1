/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.RowChangeDescription;
import oracle.jdbc.dcn.TableChangeDescription;

import org.eclipse.persistence.descriptors.CacheIndex;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.helper.DatabaseTable;
import org.eclipse.persistence.internal.identitymaps.CacheId;
import org.eclipse.persistence.internal.identitymaps.CacheKey;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.platform.database.oracle.dcn.OracleChangeNotificationListener;

import com.bosch.calcomp.adapter.logger.ILoggerAdapter;
import com.bosch.caltool.dmframework.common.ObjectStore;

/**
 * Custom database change listener. Adds the change messages from oracle to iCDM's change notification cache. They would
 * be later processed from the change notification processing mechanism
 * 
 * @author bne4cob
 */
final class CdmDatabaseChangeListener implements DatabaseChangeListener {

  /**
   * Cdm Cqn Listener
   */
  private final CdmCqnListener cdmCqnListener;
  /**
   * List of Database Field
   */
  private final List<DatabaseField> fields;
  /**
   * Database Session
   */
  private final AbstractSession databaseSession;

  /**
   * @param databaseSession database session
   * @param cdmCqnListener CQL listener
   */
  public CdmDatabaseChangeListener(final CdmCqnListener cdmCqnListener, final AbstractSession databaseSession) {
    this.cdmCqnListener = cdmCqnListener;
    this.databaseSession = databaseSession;

    this.fields = new ArrayList<DatabaseField>();
    this.fields.add(new DatabaseField(OracleChangeNotificationListener.ROWID));
  }

  /**
   * Adds the changes to change notificaiton cache. If the changes are created by a local transaction, they are ignored.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void onDatabaseChangeNotification(final DatabaseChangeEvent changeEvent) {
    if (changeEvent.getTableChangeDescription() == null) {
      return;
    }

    final ConcurrentMap<Long, ChangedData> changedData = new ConcurrentHashMap<Long, ChangedData>();

    ClassDescriptor descriptor;

    getLogger().debug("CQN change received");

    for (TableChangeDescription tableChange : changeEvent.getTableChangeDescription()) {
      descriptor = this.cdmCqnListener.getDescriptorsByTable().get(new DatabaseTable(tableChange.getTableName())); // NOPMD

      if (descriptor == null) {
        continue;
      }
      loadRowLevelChanges(changeEvent, changedData, descriptor, tableChange);
    }

    getLogger().debug("CQN : No. of valid changes = {}", changedData.size());

    // During startup of application, notification may come before data provider is created.
    // Null check is done for this.
    if (!changedData.isEmpty()) {
      ObjectStore.getInstance().getChangeNotificationCache().addChangedData(changedData);
    }
  }

  /**
   * Load row level changes to the display change event object
   * 
   * @param changeEvent DB change event
   * @param changedData change event map to be sent to notification
   * @param descriptor descriptor
   * @param tableChange table change description
   */
  private void loadRowLevelChanges(final DatabaseChangeEvent changeEvent, final Map<Long, ChangedData> changedData,
      final ClassDescriptor descriptor, final TableChangeDescription tableChange) {

    ChangedData chItem;
    CacheId cacheID;
    CacheKey key;
    ChangeType chType;

    final CacheIndex index = descriptor.getCachePolicy().getCacheIndex(this.fields);

    for (RowChangeDescription rowChange : tableChange.getRowChangeDescription()) {
      cacheID = new CacheId(new Object[] { rowChange.getRowid().stringValue() }); // NOPMD
      key =
          this.databaseSession.getIdentityMapAccessorInstance().getIdentityMapManager()
              .getCacheKeyByIndex(index, cacheID, false, descriptor);
      if (key == null) {
        continue;
      }
      if ((key.getTransactionId() == null) || !key.getTransactionId().equals(changeEvent.getTransactionId(true))) {


        getLogger().debug("  CQN change Entity - {}, Key - {}", descriptor.getJavaClass().getName(), key.getKey());
        getLogger().debug("     CQN Change event id - {}, key id - {}", changeEvent.getTransactionId(true),
            key.getTransactionId());

        // Check if the transaction is generated locally
        if (ObjectStore.getInstance().isLocalTransaction(changeEvent.getTransactionId(true))) {
          continue;
        }
        chType = ChangeType.getChangeType(rowChange.getRowOperation());
        chItem = new ChangedData(chType, (Long) key.getKey(), descriptor.getJavaClass(), DisplayEventSource.DATABASE);// NOPMD
        changedData.put((Long) key.getKey(), chItem);
      }
    }

  }

  private ILoggerAdapter getLogger() {
    return ObjectStore.getInstance().getLogger();
  }

}
