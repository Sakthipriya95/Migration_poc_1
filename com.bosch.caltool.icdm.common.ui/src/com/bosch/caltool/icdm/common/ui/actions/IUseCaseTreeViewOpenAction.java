/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.actions;

import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;

/**
 * @author and4cob
 */
public interface IUseCaseTreeViewOpenAction {


  /**
   * @param selUseCase
   * @param useCaseEditorDataInput
   */
  void openUseCaseEditor(UsecaseClientBO selUseCase, UsecaseEditorModel useCaseEditorDataInput);
  
  
}
