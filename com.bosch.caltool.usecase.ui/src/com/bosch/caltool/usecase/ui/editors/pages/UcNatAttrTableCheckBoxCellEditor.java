/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.usecase.ui.editors.pages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.nattable.edit.editor.AbstractCellEditor;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer.MoveDirectionEnum;
import org.eclipse.nebula.widgets.nattable.widget.EditModeEnum;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.uc.IUseCaseItemClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseSectionClientBO;
import com.bosch.caltool.icdm.client.bo.uc.UsecaseClientBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.uc.UcpAttr;
import com.bosch.caltool.icdm.model.uc.UsecaseEditorModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.uc.UcpAttrServiceClient;
import com.bosch.caltool.usecase.ui.Activator;

/**
 * Check box cell editor for use case editor's check boxes
 *
 * @author jvi6cob
 */
class UcNatAttrTableCheckBoxCellEditor extends AbstractCellEditor {

  /**
   * The current state of the checkbox having the corresponding value.
   */
  private boolean checkedState;

  /**
   * The editor control which paints the checkbox images.
   */
  private Canvas canvasObj;

  /**
   * When the editor is activated, flip the current data value and commit it. The repaint will pick up the new value and
   * flip the image. This is only done if the mouse click is done within the rectangle of the painted checkbox image.
   */
  @Override
  protected Control activateCell(final Composite parnt, final Object originalCanonicalValue) {
    // if this editor was activated by clicking a letter or digit key, do nothing
    if (originalCanonicalValue instanceof Character) {
      return null;
    }

    setCanonicalValue(originalCanonicalValue);

    invertCheckedState();

    getCanvasAndCommit(parnt);

    closeEditor();

    return this.canvasObj;
  }

  /**
   *
   */
  private void closeEditor() {
    // Close editor so will react to subsequent clicks on the cell
    if ((this.editMode == EditModeEnum.INLINE) && (this.canvasObj != null) && !this.canvasObj.isDisposed()) {
      this.canvasObj.getDisplay().asyncExec(this::close);
    }
  }

  /**
   * @param parnt
   */
  private void getCanvasAndCommit(final Composite parnt) {
    this.canvasObj = createEditorControl(parnt);

    commit(MoveDirectionEnum.NONE, false);
  }

  /**
   *
   */
  private void invertCheckedState() {
    // Boolean inversion
    this.checkedState ^= true;
  }

  /**
   * Get the current state of check box
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Boolean getEditorValue() {
    return getCurrentState();
  }

  /**
   * @return
   */
  private Boolean getCurrentState() {
    return Boolean.valueOf(this.checkedState);
  }

  /**
   * Sets the value to editor control. The only values accepted are null which is interpreted as false and Strings than
   * can be converted to Boolean directly. Every other object will result in setting the editor value to false
   *
   * @param value The display value to set to the wrapped editor control.
   */
  @Override
  public void setEditorValue(final Object value) {
    if (value == null) {
      this.checkedState = false;
    }
    else {
      getCheckedState(value);
    }
  }

  /**
   * @param value
   */
  private void getCheckedState(final Object value) {
    if (value instanceof Boolean) {
      this.checkedState = (Boolean) value;
    }
    else if (value instanceof String) {
      this.checkedState = Boolean.valueOf((String) value);
    }
    else {
      this.checkedState = false;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Canvas getEditorControl() {
    return this.canvasObj;
  }

  /**
   * Create the canvas
   * <p>
   * {@inheritDoc}
   */
  @Override
  public Canvas createEditorControl(final Composite parnt) {
    final Canvas cnvs = new Canvas(parnt, SWT.NONE);

    addMouseListener(cnvs);

    return cnvs;
  }

  /**
   * @param cnvs
   */
  private void addMouseListener(final Canvas cnvs) {
    cnvs.addMouseListener(new MouseAdapter() {

      /**
       * Invert the checkbox's state
       * <p>
       * {@inheritDoc}
       */
      @Override
      public void mouseUp(final MouseEvent mouseEvent) {
        // Boolean inversion
        UcNatAttrTableCheckBoxCellEditor.this.checkedState ^= true;
        cnvs.redraw();
      }
    });
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openMultiEditDialog() {
    // checkbox multi editing is not supported
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean activateAtAnyPosition() {
    // as the checkbox should only change its value this method needs to return false so the IMouseEventMatcher can
    // react on that.
    return false;
  }

  /**
   * @param attributeBO
   * @param notDeletedUCItems
   * @param useCaseNatAttributesPage
   * @param useCaseEditorModel
   */
  void createUcpAttrForAll(final AttributeClientBO attributeBO, final SortedSet<IUseCaseItemClientBO> notDeletedUCItems,
      final UseCaseNatAttributesPage useCaseNatAttributesPage) {
    if (!notDeletedUCItems.isEmpty()) {
      List<UcpAttr> ucpAttrList = new ArrayList<>();
      UsecaseClientBO useCaseBO = useCaseNatAttributesPage.getUseCase();
      for (IUseCaseItemClientBO iUseCaseItemClientBO : notDeletedUCItems) {
        UcpAttr ucpAttr = new UcpAttr();
        ucpAttr.setAttrId(attributeBO.getAttribute().getId());
        ucpAttr.setUseCaseId(useCaseBO.getID());

        if (iUseCaseItemClientBO instanceof UseCaseSectionClientBO) {
          if (iUseCaseItemClientBO.isMapped(attributeBO.getAttribute())) {
            continue;
          }
          ucpAttr.setSectionId(iUseCaseItemClientBO.getID());
        }
        ucpAttrList.add(ucpAttr);
      }
      UcpAttrServiceClient ucpAttrServiceClient = new UcpAttrServiceClient();
      try {
        ucpAttrServiceClient.create(ucpAttrList);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
      useCaseNatAttributesPage.refreshToolBarFilters();
    }

  }

  /**
   * @param attribute
   * @param notDeletedUCItems
   * @param useCaseNatAttributesPage
   * @param useCaseEditorModel
   * @param mappableUcItems
   */
  void deleteAllAttrMappings(final AttributeClientBO attribute, final SortedSet<IUseCaseItemClientBO> notDeletedUCItems,
      final UseCaseNatAttributesPage ucNatAttrPage) {
    // Code Refactoring
    if (!notDeletedUCItems.isEmpty()) {
      deleteAllMappingsOfAttr(attribute, ucNatAttrPage);
    }
  }

  /**
   * @param attribute AttributeClientBO
   * @param useCaseEditorModel UsecaseEditorModel
   * @param mappableUcItems
   * @param notDeletedUCItems SortedSet<IUseCaseItemClientBO>
   */
  private void deleteAllMappingsOfAttr(final AttributeClientBO attribute,
      final UseCaseNatAttributesPage ucNatAttrPage) {
    UcpAttrServiceClient ucpAttrClient = new UcpAttrServiceClient();
    UsecaseEditorModel useCaseEditorModel = ucNatAttrPage.getUCEditor().getEditorInput().getUseCaseEditorModel();
    Map<Long, Long> attrToUcpamap = useCaseEditorModel.getAttrToUcpAttrMap().get(attribute.getAttribute().getId());
    Set<UcpAttr> ucpAttrSet = new HashSet<>();
    SortedSet<IUseCaseItemClientBO> selectedUCItems = ucNatAttrPage.getMappableUCItems();
    if (CommonUtils.isNotEmpty(attrToUcpamap)) {
      for (IUseCaseItemClientBO ucItem : selectedUCItems) {
        Long attrIdToBeUnMapped = attrToUcpamap.get(ucItem.getID());
        if (null != attrIdToBeUnMapped) {
          UcpAttr ucpAttr = useCaseEditorModel.getUcpAttr().get(attrIdToBeUnMapped);
          ucpAttrSet.add(ucpAttr.clone());
        }
      }
    }
    try {
      ucpAttrClient.delete(ucpAttrSet);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param attribute
   * @param ucItem
   * @param useCaseEditorModel
   * @param useCaseNatAttributesPage
   */
  void insertOrDeleteUCPAttr(final AttributeClientBO attribute, final IUseCaseItemClientBO ucItem,
      final UseCaseNatAttributesPage useCaseNatAttributesPage, final UsecaseEditorModel useCaseEditorModel) {
    try {
      UcpAttrServiceClient ucpAttrClient = new UcpAttrServiceClient();
      if (ucItem.isMapped(attribute.getAttribute())) {
        // check if focus matrix content available
        if (ucItem.isFocusMatrixAvailableWhileUnMapping(attribute)) {
          handleDeleteForFMMappedAttr(attribute, ucItem, useCaseEditorModel, ucpAttrClient);
        }
        else {
          deleteUCPAttr(attribute, ucItem, useCaseEditorModel, ucpAttrClient);
        }
      }
      else {
        // Insert operation
        UcpAttr ucpAttr = new UcpAttr();
        List<UcpAttr> ucpAttrList = new ArrayList<>();
        ucpAttrList.add(ucpAttr);
        ucpAttr.setAttrId(attribute.getAttribute().getId());
        ucpAttr.setUseCaseId(useCaseNatAttributesPage.getUseCase().getID());
        if (ucItem instanceof UseCaseSectionClientBO) {
          ucpAttr.setSectionId(ucItem.getID());
        }
        ucpAttrClient.create(ucpAttrList);

      }
      useCaseNatAttributesPage.getSelectionProvider().setSelection(StructuredSelection.EMPTY);
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @param attribute
   * @param ucItem
   * @param useCaseNatAttributesPage
   * @param useCaseEditorModel
   * @param ucpAttrClient
   * @param isInsertDeleteRequired
   * @throws ApicWebServiceException
   */
  private void handleDeleteForFMMappedAttr(final AttributeClientBO attribute, final IUseCaseItemClientBO ucItem,
      final UsecaseEditorModel useCaseEditorModel, final UcpAttrServiceClient ucpAttrClient)
      throws ApicWebServiceException {
    deleteUCPAttr(attribute, ucItem, useCaseEditorModel, ucpAttrClient);

  }

  /**
   * @param attribute
   * @param ucItem
   * @param useCaseEditorModel
   * @param ucpAttrClient
   * @throws ApicWebServiceException
   */
  private void deleteUCPAttr(final AttributeClientBO attribute, final IUseCaseItemClientBO ucItem,
      final UsecaseEditorModel useCaseEditorModel, final UcpAttrServiceClient ucpAttrClient)
      throws ApicWebServiceException {
    Set<UcpAttr> ucpAttrSet = new HashSet<>();
    Map<Long, Long> attrToUcpaMap = useCaseEditorModel.getAttrToUcpAttrMap().get(attribute.getAttribute().getId());
    if (null != attrToUcpaMap) {
      UcpAttr ucpAttr = useCaseEditorModel.getUcpAttr().get(attrToUcpaMap.get(ucItem.getID()));
      ucpAttrSet.add(ucpAttr.clone());
      ucpAttrClient.delete(ucpAttrSet);
    }
  }
}