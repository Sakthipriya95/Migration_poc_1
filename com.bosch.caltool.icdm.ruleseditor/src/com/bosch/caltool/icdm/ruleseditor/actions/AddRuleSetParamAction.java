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
import com.bosch.caltool.icdm.ruleseditor.dialogs.AddRuleSetParameterDialog;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;

/**
 * @author dmr1cob
 */
public class AddRuleSetParamAction extends Action {

  private final ParamCollection cdrFunction;
  private final AbstractFormPage detailsPage;
  private final ParamCollectionDataProvider paramCollectionDataProvider;

  /**
   * @param cdrFunction cdrFunction
   * @param detailsPage detailsPage
   * @param paramCollectionDataProvider
   */
  public AddRuleSetParamAction(final ParamCollection cdrFunction, final AbstractFormPage detailsPage,
      final ParamCollectionDataProvider paramCollectionDataProvider) {
    super("Add Parameter");
    this.cdrFunction = cdrFunction;
    this.detailsPage = detailsPage;
    this.paramCollectionDataProvider = paramCollectionDataProvider;

    setImageDescriptor(ImageManager.getImageDescriptor(ImageKeys.ADD_16X16));
    setEnabled(this.paramCollectionDataProvider.isModifiable(cdrFunction));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    AddRuleSetParameterDialog addRuleSetParamDialog =
        new AddRuleSetParameterDialog(Display.getCurrent().getActiveShell(), (RuleSet) this.cdrFunction,
            (ReviewParamEditor) this.detailsPage.getEditor(), this.detailsPage);
    addRuleSetParamDialog.open();
  }
}

