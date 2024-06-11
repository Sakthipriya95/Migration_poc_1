/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * @author bru2cob
 */
public class PidcPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private boolean applyClicked = true;
  /**
   * Composite instance for base layout
   */
  private Composite top;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Group instance
   */
  private Group group;
  /**
   * Checkbox instance
   */
  private Button enableFilterBtn;
  /**
   * Defines PlatformUI preferences
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /**
   * The Default constructor
   */
  public PidcPreferencePage() {
    super();
  }

  /**
   * The Parameterized constructor
   *
   * @param title title
   */
  public PidcPreferencePage(final String title) {
    super(title);

  }

  /**
   * The Parameterized constructor
   *
   * @param title title
   * @param image image
   */
  public PidcPreferencePage(final String title, final ImageDescriptor image) {
    super(title, image);

  }

  @Override
  public void init(final IWorkbench workbench) {
    // Changes made for set Value removing the Prefrence from the Prefrence store
    this.preference.setDefault(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED, ApicConstants.CODE_NO);
    setPreferenceStore(this.preference);
  }

  @Override
  protected Control createContents(final Composite parent) {
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    final GridData gridData = getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    updatePidcFromPref();
    return this.top;
  }

  /**
   * This method returns GridData object
   *
   * @return GridData
   */
  private GridData getGridData() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    final GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.verticalAlignment = GridData.FILL;
    this.composite = new Composite(this.top, SWT.NONE);
    this.composite.setLayout(new GridLayout());
    this.composite.setLayoutData(gridData);
    createGroup();
  }

  /**
   * This method initializes group
   */
  private void createGroup() {
    final GridLayout gridLayout = new GridLayout();
    final GridData gridData = getGridData();
    this.group = new Group(this.composite, SWT.NONE);
    this.group.setLayoutData(gridData);
    this.group.setLayout(gridLayout);
    this.group.setText("PIDC Editor pre-defined filter status");
    this.enableFilterBtn = new Button(this.group, SWT.CHECK);
    this.enableFilterBtn.setText("Display only project use case attributes when a PIDC is opened");
    this.enableFilterBtn.setSelection(true);
    this.enableFilterBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        PidcPreferencePage.this.applyClicked = false;
      }
    });
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaults() {
    this.enableFilterBtn.setSelection(true);
    this.preference.setDefault(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED, ApicConstants.CODE_YES);
    setMessage(null);
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaultDisableFilter() {
    this.enableFilterBtn.setSelection(false);
    this.preference.setDefault(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED, ApicConstants.CODE_NO);
    setMessage(null);
  }

  /**
   * Updates case sensitive flag from preferences.
   */
  private void updatePidcFromPref() {
    String strEnableFilter = "";
    if (this.preference != null) {
      strEnableFilter = this.preference.getString(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED);
    }
    if (ApicConstants.CODE_YES.equals(strEnableFilter)) {
      setDefaults();
    }
    else {
      setDefaultDisableFilter();
    }

  }

  @Override
  public boolean performOk() {
    if (!this.applyClicked) {
      updatePreferences();
      MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info :",
          "Please restart the application for the changes to take effect");
    }

    return super.performOk();
  }

  /**
   * This method updates the PlatformUI preferences
   */
  private void updatePreferences() {

    String selectEnableFilter = "";

    if (this.enableFilterBtn.getSelection()) {
      selectEnableFilter = ApicConstants.CODE_YES;
    }
    else {
      selectEnableFilter = ApicConstants.CODE_NO;
    }
    // Changes made for set Value removing the Prefrence from the Prefrence store
    if (selectEnableFilter.equals(this.preference.getString(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED))) {
      this.preference.putValue(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED, selectEnableFilter);
    }
    else {
      this.preference.setValue(ApicConstants.PREF_PIDC_USECASE_ATTR_FILTER_ENABLED, selectEnableFilter);
    }
  }

  /**
   * Updates PlatformUI preferences when apply is pressed.
   */
  @Override
  protected void performApply() {
    updatePreferences();
    MessageDialog.openInformation(Display.getDefault().getActiveShell(), "Info :",
        "Please restart the application for the changes to take effect");

    this.applyClicked = true;
  }

  @Override
  public boolean performCancel() {
    return true;
  }

  @Override
  protected void performDefaults() {
    setDefaults();
  }

}
