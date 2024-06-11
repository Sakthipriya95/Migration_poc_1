/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.ParamCollection;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.caltool.icdm.ruleseditor.dialogs.ImportRuleSetParamFromA2LDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;

/**
 * @author say8cob
 */
public class ImportRuleSetParamFromA2LAction extends Action {

  private final ParamCollectionDataProvider paramCollectionDataProvider;
  private final AbstractFormPage detailsPage;
  private final ParamCollection cdrFunction;

  /**
   * @param cdrFunction cdrFunction
   * @param detailsPage detailsPage
   * @param paramCollectionDataProvider
   */
  public ImportRuleSetParamFromA2LAction(final ParamCollection cdrFunction, final AbstractFormPage detailsPage,
      final ParamCollectionDataProvider paramCollectionDataProvider) {
    super("Add Parameter from A2L ");
    this.paramCollectionDataProvider = paramCollectionDataProvider;
    this.detailsPage = detailsPage;
    this.cdrFunction = cdrFunction;
    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.UPLOAD_16X16));
    setEnabled(this.paramCollectionDataProvider.isModifiable(cdrFunction));
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    ImportRuleSetParamFromA2LDialog importRuleSetParamFromA2LDialog =
        new ImportRuleSetParamFromA2LDialog(Display.getCurrent().getActiveShell(),
            (ReviewParamEditor) this.detailsPage.getEditor(), (RuleSet) this.cdrFunction, this.detailsPage);
    importRuleSetParamFromA2LDialog.open();
  }


}
