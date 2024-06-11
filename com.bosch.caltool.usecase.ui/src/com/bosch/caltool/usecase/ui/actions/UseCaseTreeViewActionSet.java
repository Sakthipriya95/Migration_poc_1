package com.bosch.caltool.usecase.ui.actions;

import java.util.SortedSet;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseGroupClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.actions.IUseCaseTreeViewOpenAction;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroupModel;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.apic.AttributeSuperGroupServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.usecase.ui.Activator;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditor;
import com.bosch.caltool.usecase.ui.editors.UseCaseEditorInput;

/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */


/**
 * @author dmo5cob ICDM-143
 */
public class UseCaseTreeViewActionSet implements IUseCaseTreeViewOpenAction {


  private final UseCaseActionSet actionSet = new UseCaseActionSet();
  

  /**
   * @param manager IMenuManager
   */
  public void addUseCaseRootMenu(final IMenuManager manager) {
    this.actionSet.addUseCaseGroup(manager, null);
  }


  /**
   * @param manager IMenuManager
   * @param useCaseItems
   */
  public void addExportMenu(final IMenuManager manager, final SortedSet<IUseCaseItemClientBO> useCaseItems) {
    this.actionSet.addExportMenu(manager, useCaseItems);
  }

  /**
   * @param manager IMenuManager
   * @param viewer viewer
   * @param useCaseSection useCaseSection
   * @param usecaseEditorModel UsecaseEditorModel
   * @param editorUseCase UsecaseClientBO
   */
  public void addUseCaseSectionMenu(final IMenuManager manager, final UseCaseSectionClientBO useCaseSection,
      final UsecaseEditorModel usecaseEditorModel, final UsecaseClientBO editorUseCase) {
    this.actionSet.addUseCaseSection(manager, useCaseSection, usecaseEditorModel, editorUseCase);
  }


  /**
   * @param manager IMenuManager
   * @param viewer viewer
   * @param useCase useCase
   * @param usecaseEditorModel UsecaseEditorModel
   */
  public void addUseCaseMenu(final IMenuManager manager, final UsecaseClientBO useCase,
      final UsecaseEditorModel usecaseEditorModel) {
    final CommonActionSet commonActionSet = new CommonActionSet();
    this.actionSet.addUseCaseSection(manager, null, usecaseEditorModel, useCase);
    manager.add(new Separator());

    commonActionSet.addLinkAction(manager, useCase.getLinks());
  }

  /**
   * @param manager IMenuManager
   * @param useCaseGroupBO UseCaseGroupClientBO
   */
  public void addUseCaseGroupMenu(final IMenuManager manager, final UseCaseGroupClientBO useCaseGroupBO) {
    this.actionSet.addUseCaseGroup(manager, useCaseGroupBO);

    this.actionSet.addUseCases(manager, useCaseGroupBO);

  }

  /**
   * This method is responsible to open the UseCase editor
   *
   * @param selUseCase Use Case
   */
  @Override
  public void openUseCaseEditor(final UsecaseClientBO selUseCase, final UsecaseEditorModel useCaseEditorDataInput) {
    try {

      AttrGroupModel attrGroupModel = new AttributeSuperGroupServiceClient().getAttrGroupModel();
      // set data to usecaseclientbo
      selUseCase.setUsecaseEditorModel(useCaseEditorDataInput);
      final UseCaseEditorInput input = new UseCaseEditorInput(selUseCase, attrGroupModel);

      CDMLogger.getInstance().info("Opening Use Case Editor for UC : {} ...", selUseCase.getName());
      IEditorPart openEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
          UseCaseEditor.EDITOR_ID);
      UseCaseEditor ucEditor = (UseCaseEditor) openEditor;
      ucEditor.getNatAttrPage().setFocus();
      CDMLogger.getInstance().info("Use Case Editor opened");
    }
    catch (PartInitException | ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
  }
  
  
  

}
