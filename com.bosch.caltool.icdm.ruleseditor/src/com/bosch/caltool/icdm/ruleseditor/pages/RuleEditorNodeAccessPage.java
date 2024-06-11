/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.pages;

import org.eclipse.ui.forms.editor.FormEditor;

import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.icdm.client.bo.cdr.ParamCollectionDataProvider;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditor;
import com.bosch.caltool.icdm.ruleseditor.editor.ReviewParamEditorInput;

/**
 * @author nip4cob
 */
public class RuleEditorNodeAccessPage extends NodeAccessRightsPage {

  /**
   * Editor instance
   */
  private final FormEditor editor;

  /**
   * @param editor ReviewParamEditor
   */
  public RuleEditorNodeAccessPage(final FormEditor editor) {
    super(editor, ((ReviewParamEditorInput) editor.getEditorInput()).getNodeAccessBO());
    this.editor = editor;
  }

  @Override
  public void refreshUI(final com.bosch.caltool.icdm.ws.rest.client.cns.DisplayChangeEvent dce) {
    // Overriding refreshUI method to add method to enable disable actions based on access rights
    // because CNS is not implemented in List Page and Details Page
    super.refreshUI(dce);
    enableDisableActions();
  }

  /**
   * @param pages
   */
  private void enableDisableActions() {
    // enable disable actions based on access rights
    ReviewParamEditor rvwParamEditor = (ReviewParamEditor) this.editor;
    ParamCollectionDataProvider dataProvider = rvwParamEditor.getEditorInput().getDataProvider();
    boolean modifiable = dataProvider.isModifiable(rvwParamEditor.getEditorInput().getCdrObject());
    // get the instance of ListPage
    ListPage listPage = rvwParamEditor.getListPage();
    enabledisableAddDeleteExportAction(modifiable, listPage);
    // get the instance of DetailsPage
    DetailsPage detailsPage = rvwParamEditor.getDetailsPage();
    // null check added for actions in details since it might be null if page is not opened yet
    enDisAddDelForDetPage(modifiable, detailsPage);
  }

  /**
   * @param modifiable
   * @param detailsPage
   */
  private void enDisAddDelForDetPage(final boolean modifiable, final DetailsPage detailsPage) {
    if (null != detailsPage.getAddRuleSetParamAction()) {
      detailsPage.getAddRuleSetParamAction().setEnabled(modifiable);
    }
    if (null != detailsPage.getDeleteRuleSetParamAction()) {
      detailsPage.getDeleteRuleSetParamAction().setEnabled(modifiable);
    }
  }

  /**
   * @param modifiable
   * @param listPage
   */
  private void enabledisableAddDeleteExportAction(final boolean modifiable, final ListPage listPage) {
    // If the add param Set is null. then cannot be modified.
    if (listPage.getAddRuleSetParamAction() != null) {
      listPage.getAddRuleSetParamAction().setEnabled(modifiable);
    }
    // If the delete action is null.then cannot be modified.
    if (listPage.getDeleteRuleSetParamAction() != null) {
      listPage.getDeleteRuleSetParamAction().setEnabled(modifiable);
    }

    // If the export action is null.then cannot be modified.
    if (CommonUtils.isNotNull(listPage.getExpRulesetAsCdfxAction())) {
      listPage.getExpRulesetAsCdfxAction().setEnabled(modifiable);
    }
    // If the Import action is null.then cannot be modified.
    if (CommonUtils.isNotNull(listPage.getImportRuleSetParamFromA2LAction())) {
      listPage.getImportRuleSetParamFromA2LAction().setEnabled(modifiable);
    }
  }
}
