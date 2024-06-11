/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.product.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.bosch.calcomp.adapter.logger.Activator;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.UserPreference;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.caltool.icdm.ws.rest.client.general.UserPreferenceServiceClient;

/**
 * @author bru2cob
 */
public class RvwResultDecimalPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private boolean applyClicked = true;

  private static final String LIMIT_DECIMALS = "Limit decimals: ";

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

  private ComboViewer limitDecimalComboViewer;

  /**
   * Logged in user information
   */
  protected final CurrentUserBO currentUser = new CurrentUserBO();

  private UserPreference userPreference = new UserPreference();

  boolean prefExistsForUser = true;

  /**
   * The Default constructor
   */
  public RvwResultDecimalPreferencePage() {
    super();
  }

  /**
   * The Parameterized constructor
   *
   * @param title title
   */
  public RvwResultDecimalPreferencePage(final String title) {
    super(title);
  }

  /**
   * The Parameterized constructor
   *
   * @param title title
   * @param image image
   */
  public RvwResultDecimalPreferencePage(final String title, final ImageDescriptor image) {
    super(title, image);
  }

  @Override
  public void init(final IWorkbench workbench) {
    // not applicable
  }

  @Override
  protected Control createContents(final Composite parent) {
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    final GridData gridData = getGridData();
    this.top.setLayoutData(gridData);
    createComposite();
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
    this.group.setText("Set decimal limit for the numeric values in Review Result");
    createLimitDecimalsControl();
  }

  private void createLimitDecimalsControl() {
    // create a composite inside parenet composite to store the label and text field
    Composite limitDecimalComp = new Composite(this.group, SWT.NONE);
    limitDecimalComp.setLayout(new GridLayout(2, false));
    limitDecimalComp.setLayoutData(new GridData());
    Label limitDecimalLabel = new Label(limitDecimalComp, SWT.NONE);
    limitDecimalLabel.setText(LIMIT_DECIMALS);

    this.limitDecimalComboViewer = new ComboViewer(limitDecimalComp, SWT.READ_ONLY);
    this.limitDecimalComboViewer.getCombo().setLayoutData(new GridData());
    this.limitDecimalComboViewer.getCombo().setEnabled(true);
    this.limitDecimalComboViewer.getCombo().setBackground(Display.getDefault().getSystemColor(SWT.COLOR_WHITE));
    fillLimitDecimalCombo();
    // get the previously entered precison from eclipse preference store
    fillLimitDecimalComboWithStoredValue();
    addLimitDecimalComboListener();
  }

  private void addLimitDecimalComboListener() {
    this.limitDecimalComboViewer.addSelectionChangedListener(event -> this.applyClicked = false);
  }

  /**
   * fill the combo box with values from 0 to 19. By default value is set as '<No Limit>'
   */
  private void fillLimitDecimalCombo() {
    this.limitDecimalComboViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.limitDecimalComboViewer.setLabelProvider(new LabelProvider() {

      @Override
      public String getText(final Object element) {
        return (String) element;
      }
    });
    this.limitDecimalComboViewer.getCombo().add(CDRConstants.DECIMAL_PREF_NO_LIMIT, 0);
    for (int index = 1; index <= 20; index++) {
      String limitToStore = Integer.toString(index - 1);
      this.limitDecimalComboViewer.getCombo().add(limitToStore, index);
      this.limitDecimalComboViewer.getCombo().setData(Integer.toString(index), limitToStore);
    }
  }

  // fetch value from db
  private void fillLimitDecimalComboWithStoredValue() {
    String prefVal = null;
    try {
      this.userPreference = this.currentUser.getUserPreference(CDRConstants.DECIMAL_PREF_LIMIT_KEY);
      if (CommonUtils.isNotNull(this.userPreference)) {
        prefVal = this.userPreference.getUserPrefVal();
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    if (CommonUtils.isEmptyString(prefVal)) {
      prefVal = CDRConstants.DECIMAL_PREF_NO_LIMIT;
      this.prefExistsForUser = false;
    }
    this.limitDecimalComboViewer.getCombo().setText(prefVal);
  }


  /**
   * This method updates the decimal preference
   */
  private void updateDecimalLimitPreferences() {
    try {
      UserPreferenceServiceClient userPreferenceServiceClient = new UserPreferenceServiceClient();
      userPreferenceServiceClient.update(getUserPreferenceForUpdate());
      this.currentUser.clearCurrentUserCacheADGroupDelete();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }

  }

  /**
   * creates preferences
   */
  public void createDecimalLimitPreferences() {
    try {
      UserPreferenceServiceClient userPreferenceServiceClient = new UserPreferenceServiceClient();
      userPreferenceServiceClient.create(createUserPreferenceModel());
      this.currentUser.clearCurrentUserCacheADGroupDelete();
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
  }

  /**
   * @return {@link UserPreference}
   */
  public UserPreference createUserPreferenceModel() {
    UserPreference userPref = new UserPreference();
    try {
      userPref.setUserId(this.currentUser.getUserID());
      userPref.setUserPrefKey(CDRConstants.DECIMAL_PREF_LIMIT_KEY);
      userPref.setUserPrefVal(this.limitDecimalComboViewer.getCombo().getText());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
    }
    return userPref;
  }

  /**
   * @return {@link UserPreference}
   */
  public UserPreference getUserPreferenceForUpdate() {
    this.userPreference.setUserPrefVal(this.limitDecimalComboViewer.getCombo().getText());
    return this.userPreference;
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaults() {
    this.limitDecimalComboViewer.getCombo().setText(CDRConstants.DECIMAL_PREF_NO_LIMIT);
    this.applyClicked = false;
  }

  @Override
  public boolean performOk() {
    if (!this.applyClicked) {
      if (this.prefExistsForUser) {
        updateDecimalLimitPreferences();
      }
      else {
        createDecimalLimitPreferences();
      }
    }
    return super.performOk();
  }

  /**
   * Updates PlatformUI preferences when apply is pressed.
   */
  @Override
  protected void performApply() {
    if (this.prefExistsForUser) {
      updateDecimalLimitPreferences();
    }
    else {
      createDecimalLimitPreferences();
    }
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
