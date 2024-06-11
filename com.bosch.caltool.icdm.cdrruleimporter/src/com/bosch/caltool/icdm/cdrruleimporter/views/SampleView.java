package com.bosch.caltool.icdm.cdrruleimporter.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;


/**
 * This sample class demonstrates how to plug-in a new workbench view. The view shows data obtained from the model. The
 * sample creates a dummy model on the fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be presented in the view. Each view can present the
 * same model objects using different labels and icons, if needed. Alternatively, a single label provider can be shared
 * between views in order to ensure that objects of the same type are presented in the same way everywhere.
 * <p>
 */

public class SampleView extends ViewPart {

  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "com.bosch.caltool.icdm.cdrruleimporter.views.SampleView";

  private TableViewer viewer;
  private Action action1;
  private Action action2;
  private Action doubleClickAction;

  private FormToolkit toolkit;
  private Section sectionType;
  private Section sectionOptions;
  private Section sectionFiles;
  private Form form;
  private Composite client;
  private Composite clientOptions;
  private Composite clientFiles;

  /*
   * The content provider class is responsible for providing objects to the view. It can wrap existing objects in
   * adapters or simply return objects as-is. These objects may be sensitive to the current input of the view, or ignore
   * it and always show the same content (like Task List, for example).
   */

  class ViewContentProvider implements IStructuredContentProvider {

    public void inputChanged(final Viewer v, final Object oldInput, final Object newInput) {}

    public void dispose() {}

    public Object[] getElements(final Object parent) {
      return new String[] { "One", "Two", "Three" };
    }
  }

  class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {

    public String getColumnText(final Object obj, final int index) {
      return getText(obj);
    }

    public Image getColumnImage(final Object obj, final int index) {
      return getImage(obj);
    }

    @Override
    public Image getImage(final Object obj) {
      return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
    }
  }

  class NameSorter extends ViewerSorter {}

  /**
   * The constructor.
   */
  public SampleView() {}

  /**
   * This is a callback that will allow us to create the viewer and initialize it.
   */
  @Override
  public void createPartControl(final Composite parent) {

    this.toolkit = new FormToolkit(parent.getDisplay());
    createScrolledForm(parent);

    this.viewer = new TableViewer(this.client, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
    this.viewer.setContentProvider(new ViewContentProvider());
    this.viewer.setLabelProvider(new ViewLabelProvider());
    this.viewer.setSorter(new NameSorter());
    this.viewer.setInput(getViewSite());

    makeActions();
    hookContextMenu();
    hookDoubleClickAction();
    contributeToActionBars();
  }

  private void createScrolledForm(final Composite parent) {
    this.form = this.toolkit.createForm(parent);
    this.form.setText("CDR Rule Importer");
    this.toolkit.decorateFormHeading(this.form);
    this.form.getBody().setLayout(new GridLayout(2, true));
    this.form.getBody().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));


    this.sectionType = this.toolkit.createSection(this.form.getBody(), Section.DESCRIPTION |
        ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
    this.sectionType.setText("CDR-Template Type");
    this.sectionType.setDescription("Choose the kind of CDR-Template you want to import");
    this.sectionType.setLayout(new GridLayout(1, true));
    this.sectionType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    // The composite within the section contains the table
    this.client = this.toolkit.createComposite(this.sectionType, SWT.FILL);
    this.client.setLayout(new GridLayout(1, true));
    this.client.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    this.sectionFiles = this.toolkit.createSection(this.form.getBody(), Section.DESCRIPTION |
        ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
    this.sectionFiles.setText("Files to load");
    this.sectionFiles.setDescription("CHoose the folder or files to load");
    this.sectionFiles.setLayout(new GridLayout(1, true));
    this.sectionFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

    this.clientFiles = this.toolkit.createComposite(this.sectionFiles, SWT.FILL);
    this.clientFiles.setLayout(new GridLayout(1, true));
    this.clientFiles.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    ImageHyperlink addFilesLink = this.toolkit.createImageHyperlink(this.clientFiles, SWT.NONE);
    addFilesLink.setText("Choose the files, that should be loaded");
    addFilesLink.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE));

    ImageHyperlink addFoldersLink = this.toolkit.createImageHyperlink(this.clientFiles, SWT.DOUBLE_BUFFERED);
    addFoldersLink.setText("Choose the folders, that should be loaded");
    addFoldersLink.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER));

    addFoldersLink.addHyperlinkListener(new IHyperlinkListener() {

      @Override
      public void linkActivated(final HyperlinkEvent arg0) {
        MessageDialog valueExisting = new MessageDialog(new Shell(), "Axis Point already existing", null,
            "This axis point is already existing and can't be defined a second time. Do you want to restore the original value?",
            MessageDialog.QUESTION, new String[] { "Yes", "No", "Cancel" }, 1);
        valueExisting.setBlockOnOpen(true);
        valueExisting.open();
        valueExisting.getReturnCode();

      }

      @Override
      public void linkEntered(final HyperlinkEvent arg0) {
        // TODO Auto-generated method stub

      }

      @Override
      public void linkExited(final HyperlinkEvent arg0) {
        // TODO Auto-generated method stub

      }

    });


    this.sectionOptions =
        this.toolkit.createSection(this.form.getBody(), Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionOptions.setText("Options for loading data");
    this.sectionOptions.setText("Set the options for loading data");
    this.sectionOptions.setLayout(new GridLayout(1, true));
    this.sectionOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));


    this.clientOptions = this.toolkit.createComposite(this.sectionOptions, SWT.FILL);
    this.clientOptions.setLayout(new GridLayout(10, true));
    this.clientOptions.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));


    Button defaultButton = this.toolkit.createButton(this.client, "Default Template", SWT.RADIO);
    Button customStyle = this.toolkit.createButton(this.client, "Template without Functions", SWT.RADIO);

    Button chkOverwriteCdrRule =
        this.toolkit.createButton(this.clientOptions, "Overwrite existing CDR-Rule", SWT.BUTTON1);
    chkOverwriteCdrRule.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
    chkOverwriteCdrRule.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(final SelectionEvent selectionevent) {
        // TODO Auto-generated method stub

      }

      @Override
      public void widgetSelected(final SelectionEvent selectionevent) {
        // Create a file dialog
        FileDialog exportDCMFile = new FileDialog(new Shell(), SWT.OPEN);

        // Show File Extensions in the export dialog
        exportDCMFile.setFilterExtensions(new String[] { "*.dcm", "*.cdfx" });
        exportDCMFile.setFilterNames(new String[] { "DCM-File (*.dcm)", "CDFX-File (*.cdfx)" });

        // Prompt the user for file overwrite
        exportDCMFile.setOverwrite(true);


        // Get the filename
        String longFileName = exportDCMFile.open();


      }
    });

    Text test = new Text(this.clientOptions, SWT.MULTI | SWT.BORDER);
    test.setText("Test");
    test.setLayoutData(new GridData(SWT.NONE, SWT.FILL, true, true, 8, 1));

    Text test2 = new Text(this.clientOptions, SWT.MULTI | SWT.BORDER);
    test2.setText("Test2");
    test2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));

    // Layout for the client object (which contains the label table)
    GridData gdClient = new GridData(GridData.FILL_BOTH);
    gdClient.heightHint = 20;
    gdClient.widthHint = 10;
    this.client.setLayoutData(gdClient);

    GridData gdClientOptions = new GridData(GridData.FILL_BOTH);
    gdClientOptions.heightHint = 20;
    gdClientOptions.widthHint = 10;
    this.clientOptions.setLayoutData(gdClientOptions);

    GridData gdClientFiles = new GridData(GridData.FILL_BOTH);
    gdClientFiles.heightHint = 20;
    gdClientFiles.widthHint = 10;
    this.clientFiles.setLayoutData(gdClientFiles);

    // Attach the client to the section
    this.sectionType.setClient(this.client);
    this.sectionOptions.setClient(this.clientOptions);
    this.sectionFiles.setClient(this.clientFiles);
  }

  private void hookContextMenu() {
    MenuManager menuMgr = new MenuManager("#PopupMenu");
    menuMgr.setRemoveAllWhenShown(true);
    menuMgr.addMenuListener(new IMenuListener() {

      public void menuAboutToShow(final IMenuManager manager) {
        SampleView.this.fillContextMenu(manager);
      }
    });
    Menu menu = menuMgr.createContextMenu(this.viewer.getControl());
    this.viewer.getControl().setMenu(menu);
    getSite().registerContextMenu(menuMgr, this.viewer);
  }

  private void contributeToActionBars() {
    IActionBars bars = getViewSite().getActionBars();
    fillLocalPullDown(bars.getMenuManager());
    fillLocalToolBar(bars.getToolBarManager());
  }

  private void fillLocalPullDown(final IMenuManager manager) {
    manager.add(this.action1);
    manager.add(new Separator());
    manager.add(this.action2);
  }

  private void fillContextMenu(final IMenuManager manager) {
    manager.add(this.action1);
    manager.add(this.action2);
    // Other plug-ins can contribute there actions here
    manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
  }

  private void fillLocalToolBar(final IToolBarManager manager) {
    manager.add(this.action1);
    manager.add(this.action2);
  }

  private void makeActions() {
    this.action1 = new Action() {

      @Override
      public void run() {
        showMessage("Action 1 executed");
      }
    };
    this.action1.setText("Action 1");
    this.action1.setToolTipText("Action 1 tooltip");
    this.action1.setImageDescriptor(
        PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));

    this.action2 = new Action() {

      @Override
      public void run() {
        showMessage("Action 2 executed");
      }
    };
    this.action2.setText("Action 2");
    this.action2.setToolTipText("Action 2 tooltip");
    this.action2.setImageDescriptor(
        PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
    this.doubleClickAction = new Action() {

      @Override
      public void run() {
        ISelection selection = SampleView.this.viewer.getSelection();
        Object obj = ((IStructuredSelection) selection).getFirstElement();
        showMessage("Double-click detected on " + obj.toString());
      }
    };
  }

  private void hookDoubleClickAction() {
    this.viewer.addDoubleClickListener(new IDoubleClickListener() {

      public void doubleClick(final DoubleClickEvent event) {
        SampleView.this.doubleClickAction.run();
      }
    });
  }

  private void showMessage(final String message) {
    MessageDialog.openInformation(this.viewer.getControl().getShell(), "Sample View", message);
  }

  /**
   * Passing the focus request to the viewer's control.
   */
  @Override
  public void setFocus() {
    this.viewer.getControl().setFocus();
  }
}