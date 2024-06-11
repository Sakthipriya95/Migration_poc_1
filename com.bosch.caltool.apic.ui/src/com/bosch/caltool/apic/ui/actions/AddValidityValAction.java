/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.SortedSet;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.events.HelpListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

import com.bosch.caltool.apic.ui.dialogs.AddValidityAttrValDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDialog;
import com.bosch.caltool.apic.ui.editors.pages.PredefinedAttributesPage;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;


/**
 * ICDM-2593 Action to choose attribute values for the selected level attribute (Validity Attribute in predefined page)
 *
 * @author dja7cob
 */
public class AddValidityValAction implements IAction {

  /**
   * PredefinedAttributesPage instance
   */
  private final PredefinedAttributesPage predefinedAttrPage;
  /**
   * Validity attribute selected
   */
  private final Attribute selValidityAttribute;
  /**
   * Validity attribute values selected
   */
  private final SortedSet<AttributeValue> selValidityAttrValues;
  /**
   * EditValueDialog instance
   */
  private final EditValueDialog editValDialog;

  /**
   * @param dependentAttributesPage PredefinedAttributesPage instance
   * @param selectedLevelAttr Level attribute selected in PredefinedAttributesPage
   * @param selLevAttrValues Attribute values chosen for 'selectedLevelAttr' from AddLevelAttrValDialog
   * @param editValDialog instance
   */
  public AddValidityValAction(final PredefinedAttributesPage dependentAttributesPage, final Attribute selectedLevelAttr,
      final SortedSet<AttributeValue> selLevAttrValues, final EditValueDialog editValDialog) {
    this.selValidityAttribute = selectedLevelAttr;
    this.selValidityAttrValues = selLevAttrValues;
    this.predefinedAttrPage = dependentAttributesPage;
    this.editValDialog = editValDialog;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addPropertyChangeListener(final IPropertyChangeListener arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getAccelerator() {
    // Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getActionDefinitionId() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getDisabledImageDescriptor() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public HelpListener getHelpListener() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getHoverImageDescriptor() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getId() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ImageDescriptor getImageDescriptor() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IMenuCreator getMenuCreator() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getStyle() {
    // Auto-generated method stub
    return 0;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getText() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTipText() {
    // Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isChecked() {
    // Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isEnabled() {
    // Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isHandled() {
    // Auto-generated method stub
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void removePropertyChangeListener(final IPropertyChangeListener arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // Dialog to select the validity values for the validity attribute
    AddValidityAttrValDialog attrValDialog = new AddValidityAttrValDialog(Display.getCurrent().getActiveShell(),
        this.predefinedAttrPage, this.selValidityAttribute, this.selValidityAttrValues, this.editValDialog);
    attrValDialog.open();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void runWithEvent(final Event arg0) {
    // No action
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setAccelerator(final int arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setActionDefinitionId(final String arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setChecked(final boolean arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDescription(final String arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setDisabledImageDescriptor(final ImageDescriptor arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setEnabled(final boolean arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setHelpListener(final HelpListener arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setHoverImageDescriptor(final ImageDescriptor arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final String arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setImageDescriptor(final ImageDescriptor arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setMenuCreator(final IMenuCreator arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setText(final String arg0) {
    // Auto-generated method stub

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setToolTipText(final String arg0) {
    // Auto-generated method stub

  }

}
