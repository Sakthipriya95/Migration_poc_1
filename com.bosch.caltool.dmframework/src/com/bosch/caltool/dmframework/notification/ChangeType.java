/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.dmframework.notification;

import oracle.jdbc.dcn.RowChangeDescription.RowOperation;


/**
 * Type of change of the record
 * 
 * @author bne4cob
 */
public enum ChangeType {
  /**
   * Insert operation
   */
  INSERT(RowOperation.INSERT),
  /**
   * Update operation
   */
  UPDATE(RowOperation.UPDATE),
  /**
   * Delete operation
   */
  DELETE(RowOperation.DELETE);

  /**
   * The CQN row operation
   */
  private RowOperation rowOperation;

  /**
   * Constructor
   * 
   * @param rowOperaton the CQN row operation
   */
  ChangeType(final RowOperation rowOperaton) {
    this.rowOperation = rowOperaton;
  }

  /**
   * Returns the change type for the given CQN row operation
   * 
   * @param rowOperaton DCN row operation
   * @return change type value
   */
  public static ChangeType getChangeType(final RowOperation rowOperaton) {
    for (ChangeType chType : ChangeType.values()) {
      if (chType.rowOperation == rowOperaton) {
        return chType;
      }
    }
    return null;
  }
}
