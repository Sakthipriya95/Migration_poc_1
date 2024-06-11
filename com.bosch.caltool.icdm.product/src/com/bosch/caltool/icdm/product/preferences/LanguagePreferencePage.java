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

import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * Language preferences page in preference dialog
 *
 * @author dmo5cob
 */
public class LanguagePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

  private static final String LBL_LANG_SELECT = "Select the language";
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
   * Radio button instance
   */
  private Button englishLanguageBtn;
  /**
   * Radio button instance
   */
  private Button germanLanguageBtn;
  /**
   * Defines PlatformUI preferences
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /**
   * The Default constructor
   */
  public LanguagePreferencePage() {
    // The Default constructor
    super();
  }

  /**
   * The Parameterized constructor
   *
   * @param title page title
   */
  public LanguagePreferencePage(final String title) {
    super(title);

  }

  /**
   * The Parameterized constructor
   *
   * @param title page title
   * @param image image
   */
  public LanguagePreferencePage(final String title, final ImageDescriptor image) {
    super(title, image);

  }

  /**
   * {@inheritDoc}
   * <p>
   * Initialise language from store
   */
  @Override
  public void init(final IWorkbench workbench) {
    this.preference.setDefault(ApicConstants.LANGUAGE, Language.ENGLISH.getText());
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
    updateLanguageFromPref();
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
    group.setText(LBL_LANG_SELECT);

    // English language option
    this.englishLanguageBtn = new Button(group, SWT.RADIO);
    this.englishLanguageBtn.setText(Language.ENGLISH.getText());
    this.englishLanguageBtn.setSelection(true);
    this.englishLanguageBtn.addSelectionListener(new LanguageSelectionAdapter());

    // German language option
    this.germanLanguageBtn = new Button(group, SWT.RADIO);
    this.germanLanguageBtn.setText(Language.GERMAN.getText());
    this.germanLanguageBtn.setSelection(true);
    this.germanLanguageBtn.addSelectionListener(new LanguageSelectionAdapter());
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaults() {
    this.englishLanguageBtn.setSelection(true);
    this.germanLanguageBtn.setSelection(false);
    this.preference.setDefault(ApicConstants.LANGUAGE, Language.ENGLISH.getText());
    setMessage(null);
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaultGerman() {
    this.germanLanguageBtn.setSelection(true);
    this.englishLanguageBtn.setSelection(false);
    this.preference.setDefault(ApicConstants.LANGUAGE, Language.GERMAN.getText());
    setMessage(null);
  }

  /**
   * Updates case sensitive flag from preferences.
   */
  private void updateLanguageFromPref() {

    String currentLanguage = "";
    if (this.preference != null) {
      currentLanguage = this.preference.getString(ApicConstants.LANGUAGE);
    }

    if (Language.getLanguage(currentLanguage) == Language.ENGLISH) {
      setDefaults();
    }
    else {
      setDefaultGerman();
    }

  }

  /**
   * {@inheritDoc}
   */
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
    String language = "";

    if (this.englishLanguageBtn.getSelection()) {
      language = Language.ENGLISH.getText();
    }
    else if (this.germanLanguageBtn.getSelection()) {
      language = Language.GERMAN.getText();
    }
    // Changes made for set Value removing the Prefrence from the Prefrence store
    if (language.equals(this.preference.getString(ApicConstants.LANGUAGE))) {
      this.preference.putValue(ApicConstants.LANGUAGE, language);
    }
    else {
      this.preference.setValue(ApicConstants.LANGUAGE, language);
    }
    new CommonDataBO().setLanguage(Language.getLanguage(language));
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
    setDefaults();
    this.applyClicked = false;
  }

  /**
   * Language radio option selection adapter
   *
   * @author bne4cob
   */
  private class LanguageSelectionAdapter extends SelectionAdapter {

    /**
     * Update the preference
     * <p>
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(final SelectionEvent event) {
      LanguagePreferencePage.this.applyClicked = false;
      updatePreferences();
    }

  }

}
