package com.bosch.caltool.icdm.product.preferences;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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

import com.bosch.caltool.icdm.product.util.WelcomePagePref;
import com.bosch.caltool.icdm.ui.util.IUIConstants;

/**
 * @author dmo5cob Icdm-569
 */
public class HomePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

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
   * Radio button instance
   */
  private Button showWelcomeBtn;
  /**
   * Radio button instance
   */
  private Button noWelcomeBtn;
  /**
   * Defines PlatformUI preferences
   */
  private final IPreferenceStore preference = PlatformUI.getPreferenceStore();

  /**
   * The Default constructor
   */
  public HomePreferencePage() {
    super();
  }

  /**
   * The Parameterized constructor
   *
   * @param title title
   */
  public HomePreferencePage(final String title) {
    super(title);

  }

  /**
   * The Parameterized constructor
   *
   * @param title title
   * @param image image
   */
  public HomePreferencePage(final String title, final ImageDescriptor image) {
    super(title, image);

  }

  @Override
  public void init(final IWorkbench workbench) {
    // Changes made for set Value removing the Prefrence from the Prefrence store
    if ((this.preference.getString(IUIConstants.WELCOMEPAGE) == null) ||
        this.preference.getString(IUIConstants.WELCOMEPAGE).isEmpty()) {
      this.preference.setDefault(IUIConstants.WELCOMEPAGE, WelcomePagePref.SHOW_WELCOME_PAGE.getText());
    }
    setPreferenceStore(this.preference);
  }

  @Override
  protected Control createContents(final Composite parent) {
    this.top = new Composite(parent, SWT.NONE);
    this.top.setLayout(new GridLayout());
    final GridData gridData = getGridData();
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
    this.group.setText(IUIConstants.WELCOMEPAGE_SELECT);
    this.showWelcomeBtn = new Button(this.group, SWT.RADIO);
    this.showWelcomeBtn.setText("Show welcome page at start up");
    this.showWelcomeBtn.setSelection(true);
    this.showWelcomeBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        HomePreferencePage.this.applyClicked = false;
        updatePreferences();
      }
    });
    this.noWelcomeBtn = new Button(this.group, SWT.RADIO);
    this.noWelcomeBtn.setText("Do not show welcome page at start up");
    this.noWelcomeBtn.setSelection(true);
    this.noWelcomeBtn.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {
        HomePreferencePage.this.applyClicked = false;
        updatePreferences();
      }
    });
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaults() {
    this.showWelcomeBtn.setSelection(true);
    this.preference.setDefault(IUIConstants.WELCOMEPAGE, WelcomePagePref.SHOW_WELCOME_PAGE.getText());
    setMessage(null);
  }

  /**
   * Sets UI elements to default
   */
  private void setDefaultNoWelcome() {
    this.noWelcomeBtn.setSelection(true);
    this.preference.setDefault(IUIConstants.WELCOMEPAGE, WelcomePagePref.NO_WELCOME_PAGE.getText());
    setMessage(null);
  }

  /**
   * Updates case sensitive flag from preferences.
   */
  private void updateLanguageFromPref() {
    String strWelcome;
    if (this.preference != null) {
      strWelcome = this.preference.getString(IUIConstants.WELCOMEPAGE);
    }
    else {
      strWelcome = "";
    }
    if (strWelcome.equals(WelcomePagePref.SHOW_WELCOME_PAGE.getText())) {
      setDefaults();
    }
    else {
      this.showWelcomeBtn.setSelection(false);
    }
    String strNoWelcome;
    if (this.preference != null) {
      strNoWelcome = this.preference.getString(IUIConstants.WELCOMEPAGE);
    }
    else {
      strNoWelcome = "";
    }
    if (strNoWelcome.equals(WelcomePagePref.NO_WELCOME_PAGE.getText())) {
      setDefaultNoWelcome();
    }
    else {
      this.noWelcomeBtn.setSelection(false);
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

    String selectHomePage = "";

    if (this.showWelcomeBtn.getSelection()) {
      selectHomePage = WelcomePagePref.SHOW_WELCOME_PAGE.getText();
    }
    else if (this.noWelcomeBtn.getSelection()) {
      selectHomePage = WelcomePagePref.NO_WELCOME_PAGE.getText();
    }
    // Changes made for set Value removing the Prefrence from the Prefrence store
    if (selectHomePage.equals(this.preference.getString(IUIConstants.WELCOMEPAGE))) {
      this.preference.putValue(IUIConstants.WELCOMEPAGE, selectHomePage);
    }
    else {
      this.preference.setValue(IUIConstants.WELCOMEPAGE, selectHomePage);
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
    this.showWelcomeBtn.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent event) {
        // Empty
      }

      @Override
      public void mouseDown(final MouseEvent event) {
        final boolean isSelected = HomePreferencePage.this.showWelcomeBtn.getSelection();
        if (isSelected) {
          HomePreferencePage.this.showWelcomeBtn.setSelection(false);
          HomePreferencePage.this.preference.setValue(IUIConstants.WELCOMEPAGE,
              WelcomePagePref.SHOW_WELCOME_PAGE.getText());
        }
        else {
          HomePreferencePage.this.showWelcomeBtn.setSelection(true);
          HomePreferencePage.this.preference.setValue(IUIConstants.WELCOMEPAGE,
              WelcomePagePref.NO_WELCOME_PAGE.getText());
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent event) {
        // Empty
      }
    });
    this.noWelcomeBtn.addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent event) {
        // Empty
      }

      @Override
      public void mouseDown(final MouseEvent event) {

        final boolean isNoWelcome = HomePreferencePage.this.noWelcomeBtn.getSelection();
        if (isNoWelcome) {
          HomePreferencePage.this.noWelcomeBtn.setSelection(false);
          HomePreferencePage.this.preference.setValue(IUIConstants.WELCOMEPAGE,
              WelcomePagePref.NO_WELCOME_PAGE.getText());
        }
        else {
          HomePreferencePage.this.noWelcomeBtn.setSelection(true);
          HomePreferencePage.this.preference.setValue(IUIConstants.WELCOMEPAGE,
              WelcomePagePref.SHOW_WELCOME_PAGE.getText());
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent event) {
        // empty
      }
    });
    return true;
  }

  @Override
  protected void performDefaults() {
    setDefaults();
  }

}
