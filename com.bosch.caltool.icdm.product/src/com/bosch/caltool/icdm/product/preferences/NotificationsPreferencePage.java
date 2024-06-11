/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Notifications preferences page in preference dialog
 *
 * @author bne4cob
 */
public class NotificationsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private static final String LBL_CONSTAT_SYSTRAY_SELECT = "Server connection status change notification";

  /**
   * flag to verify whether apply button is already clicked
   */
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
   * Enabled checkbox
   */
  private Button cbNotifEnabled;

  /**
   * Defines PlatformUI preferences
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /**
   * The Default constructor
   */
  public NotificationsPreferencePage() {
    // The Default constructor
    super();
  }

  /**
   * The Parameterized constructor
   *
   * @param title page title
   */
  public NotificationsPreferencePage(final String title) {
    super(title);

  }

  /**
   * The Parameterized constructor
   *
   * @param title page title
   * @param image image
   */
  public NotificationsPreferencePage(final String title, final ImageDescriptor image) {
    super(title, image);

  }

  /**
   * {@inheritDoc}
   * <p>
   * Initialise preference from store
   */
  @Override
  public void init(final IWorkbench workbench) {
    this.preference.setDefault(CommonUIConstants.PREF_SHOW_CON_STATE_NOTIF_ENABLED, ApicConstants.CODE_YES);
    setPreferenceStore(this.preference);
  }

  /**
   * {@inheritDoc}
   * <p>
   * create pref page
   */
  @Override
  protected Control createContents(final Composite parent) {
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    GridData gridData = getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
    updateConStateNotifEnabledFlagFromPref();
    return this.top;
  }

  /**
   * This method returns GridData object
   *
   * @return GridData
   */
  private GridData getGridData() {
    GridData gridData = new GridData();
    gridData.horizontalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.verticalAlignment = GridData.CENTER;
    return gridData;
  }

  /**
   * This method initializes composite
   */
  private void createComposite() {
    GridData gridData = new GridData();
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
    GridLayout gridLayout = new GridLayout();
    GridData gridData = getGridData();
    Group group = new Group(this.composite, SWT.NONE);
    group.setLayoutData(gridData);
    group.setLayout(gridLayout);
    group.setText(LBL_CONSTAT_SYSTRAY_SELECT);

    this.cbNotifEnabled = new Button(group, SWT.CHECK);
    this.cbNotifEnabled.setText("Show notifications in System Tray");
  }

  /**
   * Sets UI elements to default
   */
  private void setShowNotif() {
    this.cbNotifEnabled.setSelection(true);
    this.preference.setDefault(CommonUIConstants.PREF_SHOW_CON_STATE_NOTIF_ENABLED, ApicConstants.CODE_YES);
    setMessage(null);
  }

  /**
   * Sets UI elements to default
   */
  private void setDoNotShowNotif() {
    this.cbNotifEnabled.setSelection(false);
    this.preference.setDefault(CommonUIConstants.PREF_SHOW_CON_STATE_NOTIF_ENABLED, ApicConstants.CODE_NO);
    setMessage(null);
  }

  /**
   * Updates case sensitive flag from preferences.
   */
  private void updateConStateNotifEnabledFlagFromPref() {

    String notifEnabled = "";
    if (this.preference != null) {
      notifEnabled = this.preference.getString(CommonUIConstants.PREF_SHOW_CON_STATE_NOTIF_ENABLED);
    }

    if (CommonUtils.isEmptyString(notifEnabled)) {
      notifEnabled = ApicConstants.CODE_YES;
    }

    if (ApicConstants.CODE_YES.equals(notifEnabled)) {
      setShowNotif();
    }
    else {
      setDoNotShowNotif();
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performOk() {
    if (!this.applyClicked) {
      updatePreferences();
    }
    return super.performOk();
  }

  /**
   * This method updates the PlatformUI preferences
   */
  private void updatePreferences() {
    String notifEnabled = this.cbNotifEnabled.getSelection() ? ApicConstants.CODE_YES : ApicConstants.CODE_NO;
    // Changes made for set Value removing the Prefrence from the Prefrence store
    this.preference.setValue(CommonUIConstants.PREF_SHOW_CON_STATE_NOTIF_ENABLED, notifEnabled);
  }

  /**
   * Updates PlatformUI preferences when apply is pressed.
   */
  @Override
  protected void performApply() {
    updatePreferences();
    this.applyClicked = true;
    super.performApply();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean performCancel() {
    // No action required
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void performDefaults() {
    setShowNotif();
    this.applyClicked = false;
  }

}
