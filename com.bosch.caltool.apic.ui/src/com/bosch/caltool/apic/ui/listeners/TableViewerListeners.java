/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.listeners;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeDepnDialog;
import com.bosch.caltool.apic.ui.dialogs.EditAttributeDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDepnDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDialog;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.icdm.client.bo.apic.AttrNValueDependencyClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeClientBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;

/**
 * @author dmo5cob
 */
public class TableViewerListeners {

  /**
   * AttributesInfoViewPart instance
   */
  private final AttributesPage attributesPage;

  /**
   * The parameterized constructor
   *
   * @param attributesPage instance
   */
  public TableViewerListeners(final AttributesPage attributesPage) {
    this.attributesPage = attributesPage;
  }

  /**
   * This method listens double click on attribute tableviewer
   */
  public void attrTableDoubleClickListener() {
    // Double click action for Attributes table viewer
    this.attributesPage.getAttrTableViewer().addDoubleClickListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) TableViewerListeners.this.attributesPage.getAttrTableViewer().getSelection();
      if ((selection != null) && !selection.isEmpty()) {
        final Object selectedElement = selection.getFirstElement();
        if (selectedElement instanceof com.bosch.caltool.icdm.model.apic.attr.Attribute) {
          final com.bosch.caltool.icdm.model.apic.attr.Attribute attr =
              (com.bosch.caltool.icdm.model.apic.attr.Attribute) selectedElement;
          if (attr.isDeleted()) {
            // ICDM-340
            CDMLogger.getInstance().errorDialog("Deleted attribute cannot be edited!", Activator.PLUGIN_ID);
          }
          else if (new AttributeClientBO(attr).isGrantAccessEnabled()) {
            // icdm-253
            final EditAttributeDialog editAttrDialog = new EditAttributeDialog(Display.getDefault().getActiveShell(),
                TableViewerListeners.this.attributesPage);
            editAttrDialog.open();

          }
        }
      }

    });
  }

  /**
   * This method listens double click on attribute value tableviewer
   */
  public void attrValTableDoubleClickListener() {
    TableViewerListeners.this.attributesPage.getValueTableViewer().addDoubleClickListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) TableViewerListeners.this.attributesPage.getValueTableViewer().getSelection();
      final Object selectedElement = selection.getFirstElement();
      if (selectedElement instanceof AttributeValue) {
        final AttributeValue val = (AttributeValue) selectedElement;
        if (val.isDeleted()) {
          // ICDM-340
          CDMLogger.getInstance().errorDialog("Deleted attribute value cannot be edited!", Activator.PLUGIN_ID);
        }
        else {
          // icdm-253
          // Code Change made For PMD
          openEditValueDialog(selection);

        }
      }


    });
  }

  /**
   * This method listens double click on value dependency tableviewer
   */
  public void valDepnTableDoubleClickListener() {
    TableViewerListeners.this.attributesPage.getValDepTabViewer().addDoubleClickListener(event -> {
      final IStructuredSelection selection =
          (IStructuredSelection) TableViewerListeners.this.attributesPage.getValDepTabViewer().getSelection();
      // Code Change made For PMD
      openValDepEditDialog(selection);

    });
  }

  /**
   * This method listens double click on attribute dependency tableviewer
   */
  public void attrDepnTableDoubleClickListener() {
    TableViewerListeners.this.attributesPage.getAttrDepTabViewer().addDoubleClickListener(new IDoubleClickListener() {

      @Override
      public void doubleClick(final DoubleClickEvent event) {
        final IStructuredSelection selection =
            (IStructuredSelection) TableViewerListeners.this.attributesPage.getAttrDepTabViewer().getSelection();
        if ((selection != null) && !selection.isEmpty()) {
          // Code Change made For PMD
          openAttrDepnDialog(selection);
        }
      }
    });
  }

  /**
   * @param selection
   */
  private void openEditValueDialog(final IStructuredSelection selection) {
    if ((selection != null) && !selection.isEmpty()) {
      final Object selectedElement = selection.getFirstElement();
      if (selectedElement instanceof AttributeValue) {

        final AttributeValue attrVal = (AttributeValue) selectedElement;
        AttributeValueClientBO clientbo = new AttributeValueClientBO(attrVal);
        if (!attrVal.isDeleted() && !clientbo.getAttribute().isDeleted()) {
          if (clientbo.isGrantAccessEnabled()) {

            // TODO To be checked :Editing of non normalised values from attributes editor
            final EditValueDialog dialog =
                new EditValueDialog(Display.getDefault().getActiveShell(), TableViewerListeners.this.attributesPage);
            dialog.open();
          }
        }
        else {
          // ICDM-340
          CDMLogger.getInstance().errorDialog("Deleted value or deleted attributes value cannot be edited!",
              Activator.PLUGIN_ID);
        }


      }
    }
  }

  /**
   * @param selection
   */
  private void openValDepEditDialog(final IStructuredSelection selection) {
    if ((selection != null) && (!selection.isEmpty())) {
      final Object selectedElement = selection.getFirstElement();
      if (selectedElement instanceof AttrNValueDependency) {
        final AttrNValueDependency attrValDep = (AttrNValueDependency) selectedElement;
        AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(attrValDep);
        if (attrValDep.isDeleted()) {
          // ICDM-340
          CDMLogger.getInstance().errorDialog("Deleted dependency cannot be edited!", Activator.PLUGIN_ID);
        }
        else if ((bo.isGrantAccessEnabled() && !attrValDep.isDeleted() && !bo.getAttributeValue().isDeleted()) ||
            new AttributeClientBO(new AttributeValueClientBO(bo.getAttributeValue()).getAttribute())
                .isGrantAccessEnabled()) {
          final EditValueDepnDialog dialog =
              new EditValueDepnDialog(Display.getDefault().getActiveShell(), TableViewerListeners.this.attributesPage);
          dialog.open();
        }
      }
    }
  }

  /**
   * @param selection
   */
  private void openAttrDepnDialog(final IStructuredSelection selection) {
    final Object selectedElement = selection.getFirstElement();
    if (selectedElement instanceof AttrNValueDependency) {
      final AttrNValueDependency attrDep = (AttrNValueDependency) selectedElement;
      AttrNValueDependencyClientBO bo = new AttrNValueDependencyClientBO(attrDep);
      if (attrDep.isDeleted()) {
        // ICDM-340
        CDMLogger.getInstance().errorDialog("Deleted dependency cannot be edited!", Activator.PLUGIN_ID);
      }
      else if ((bo.isGrantAccessEnabled() && !attrDep.isDeleted() && !bo.getAttribute().isDeleted()) ||
          new AttributeClientBO(bo.getAttribute()).isGrantAccessEnabled()) {

        final EditAttributeDepnDialog dialog = new EditAttributeDepnDialog(Display.getDefault().getActiveShell(),
            TableViewerListeners.this.attributesPage);
        dialog.open();
      }
    }
  }
}
