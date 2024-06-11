/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.datamodel.core.cns;


/**
 * @author bne4cob
 */
public enum CHANGE_OPERATION {
                              /**
                               * Create Operation
                               */
                              CREATE,
                              /**
                               * Update Operation
                               */
                              UPDATE,
                              /**
                               * Delete Operation
                               */
                              DELETE,
                              /**
                               * Becomes visible
                               */
                              VISIBLE,
                              /**
                               * Becomes invisible
                               */
                              INVISIBLE;
}
