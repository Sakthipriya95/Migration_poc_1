/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.editor;

import java.util.List;

import org.eclipse.ui.forms.editor.FormEditor;

import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;


/**
 * @author dmo5cob
 */
public interface IRulesEditorCustomization {

  /**
   * @param editor
   * @return List<AbstractPage> in the required order
   */
  List<AbstractFormPage> getPages();

  /**
   * @param editor FormEditor
   */
  void setEditor(FormEditor editor);

}
