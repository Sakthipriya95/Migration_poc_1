/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.editors;

import org.eclipse.ui.IEditorInput;

import com.bosch.caltool.icdm.client.bo.framework.IClientDataHandler;

/**
 * @author bne4cob
 */
public interface IIcdmEditorInput extends IEditorInput {

  /**
   * @return client data handler
   */
  IClientDataHandler getDataHandler();
}
