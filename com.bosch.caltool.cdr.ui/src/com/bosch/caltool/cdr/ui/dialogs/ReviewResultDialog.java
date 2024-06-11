package com.bosch.caltool.cdr.ui.dialogs;


import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.actions.CdrActionSet;
import com.bosch.caltool.icdm.client.bo.apic.FavouritesTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNodeHandler;
import com.bosch.caltool.icdm.client.bo.apic.TreeViewFlagValueProvider;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.FavoritesTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewContentProvider;
import com.bosch.caltool.icdm.common.ui.providers.PIDTreeViewLabelProvider;
import com.bosch.caltool.icdm.common.ui.table.filters.PidcNameFilter;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;

/**
 * Icdm-567 This class provides a dialog to select a Review Result
 */
public class ReviewResultDialog extends AbstractDialog {


  /**
   ** Constant for Favorites
   */
  private static final String FAVORITES = "Favorites";

  /**
   *
   */
  protected PidcTreeNodeHandler treeHandler;

  /**
   * the ok button
   */
  private Button okBtn;

  /**
   * Review Result Seleceted
   */
  protected PidcTreeNode pidcTreeNode;

  /**
   * The folder.
   **/
  private TabFolder folder;

  /** The form toolkit. */
  private FormToolkit formToolkit;

  /**
   * favouritesTreeHandler
   */
  private FavouritesTreeNodeHandler favouritesTreeHandler;
  /**
   * favourites tree viewer
   */
  private TreeViewer favouritesViewer;

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   */
  public ReviewResultDialog(final Shell parentShell) {
    super(parentShell);
  }

  /**
   * configures the shell
   */
  @Override
  protected void configureShell(final Shell newShell) {
    newShell.setText("Open Review Result");
    newShell.setSize(600, 650);
    super.configureShell(newShell);
    super.setHelpAvailable(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    // Not applicable
    return true;
  }

  /**
   * Creates the dialog's contents
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control createContents = super.createContents(parent);
    // Set the title
    setTitle("Open a Review Result");
    // Set the message
    setMessage("Select a calibration data review result", IMessageProvider.INFORMATION);
    return createContents;
  }


  /**
   * creates the dialog area
   */
  @Override
  protected Control createDialogArea(final Composite parent) {

    // initialise the composite
    Composite composite = new Composite(parent, SWT.None);

    composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    composite.setLayout(new GridLayout());

    final GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 1;
    composite.setLayout(gridLayout);
    composite.setLayoutData(GridDataUtil.getInstance().getGridData());

    // initialise the folder
    this.folder = new TabFolder(composite, SWT.NONE);
    this.folder.setLayout(gridLayout);
    this.folder.setLayoutData(GridDataUtil.getInstance().getGridData());

    // Create Tab for PID Card
    final TabItem pidcTab = new TabItem(this.folder, SWT.NONE);
    pidcTab.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    pidcTab.setText("PID_CARD");

    // Create Section for PID Card
    Section sectionOne =
        getFormToolkit().createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionOne.setText("PID_CARD");
    sectionOne.getDescriptionControl().setEnabled(false);
    sectionOne.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    pidcTab.setControl(sectionOne);

    Composite childCompositePIDC = getFormToolkit().createComposite(sectionOne, SWT.NONE);
    childCompositePIDC.setLayout(gridLayout);
    childCompositePIDC.setLayoutData(GridDataUtil.getInstance().getGridData());
    sectionOne.setClient(childCompositePIDC);

    // Creating the result tree for PID Card view
    createResultTreeForPIDC(childCompositePIDC);

    // Create Tab for Favorites
    final TabItem favTab = new TabItem(this.folder, SWT.NONE);
    favTab.setImage(ImageManager.getInstance().getRegisteredImage(ImageKeys.FUNCTION_28X30));
    favTab.setText(FAVORITES);

    // Create Section for Favorites
    Section sectionTwo =
        getFormToolkit().createSection(this.folder, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionTwo.setText(FAVORITES);
    sectionTwo.getDescriptionControl().setEnabled(false);
    sectionTwo.setLayoutData(getGridDataWithOutGrabExcessVSpace());
    favTab.setControl(sectionTwo);

    Composite childCompositeFav = getFormToolkit().createComposite(sectionTwo, SWT.NONE);
    childCompositeFav.setLayout(gridLayout);
    childCompositeFav.setLayoutData(GridDataUtil.getInstance().getGridData());

    // creating the result tree for Favorite
    createResultTreeForFavorites(childCompositeFav);
    folderSelectionListeners();
    sectionTwo.setClient(childCompositeFav);

    return composite;
  }


  /**
   * Listeners for folderselection
   */
  private void folderSelectionListeners() {

    this.folder.addSelectionListener(new SelectionAdapter() {

      @Override
      public void widgetSelected(final SelectionEvent event) {

        if (FAVORITES.equals(ReviewResultDialog.this.folder.getSelection()[0].getText()) &&
            (ReviewResultDialog.this.favouritesTreeHandler == null)) {
          ReviewResultDialog.this.favouritesTreeHandler =
              new FavouritesTreeNodeHandler(CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent());

          // Set Content provider for the tree
          FavoritesTreeViewContentProvider favTreeViewContentProvider =
              new FavoritesTreeViewContentProvider(CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent(),
                  ReviewResultDialog.this.favouritesTreeHandler, false, true, false, false, false);

          ReviewResultDialog.this.favouritesViewer.setContentProvider(favTreeViewContentProvider);


          // Set Label provider for the tree
          ReviewResultDialog.this.favouritesViewer
              .setLabelProvider(new FavoritesTreeViewLabelProvider(ReviewResultDialog.this.favouritesViewer));
          // Call to build tree using setInput(), EMPTY string object indicates to
          // create root node in Content provider
          ReviewResultDialog.this.favouritesViewer.setInput("");

        }
      }

    });

  }

  /**
   * @param favChildComposite
   */
  private void createResultTreeForFavorites(final Composite favChildComposite) {
    PidcNameFilter filter = new PidcNameFilter();
    // Filtered initialisation
    final FilteredTree tree = new FilteredTree(favChildComposite, SWT.BORDER, filter, true);


    // hiding the filter control due to performance issues
    Text filterControl = tree.getFilterControl();
    filterControl.setVisible(true);
    filterControl.getParent().setVisible(true);

    // gridData initialisation
    final GridData gridData = new GridData();

    applyGridDataStyles(gridData, favChildComposite);

    this.favouritesViewer = tree.getViewer();

    this.favouritesViewer.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      final Object selected = selection.getFirstElement();
      if ((selected instanceof PidcTreeNode) && (((PidcTreeNode) selected).getReviewResult() != null)) {
        ReviewResultDialog.this.okBtn.setEnabled(true);
        ReviewResultDialog.this.pidcTreeNode = (PidcTreeNode) selected;
      }
      else {
        ReviewResultDialog.this.okBtn.setEnabled(false);
        ReviewResultDialog.this.pidcTreeNode = null;
      }
    });
    this.favouritesViewer
        .addDoubleClickListener(event -> Display.getDefault().asyncExec(ReviewResultDialog.this::okPressed));
  }

  /**
   * This method initializes formToolkit.
   *
   * @return org.eclipse.ui.forms.widgets.FormToolkit
   */
  private FormToolkit getFormToolkit() {
    if (this.formToolkit == null) {
      this.formToolkit = new FormToolkit(Display.getCurrent());
    }
    return this.formToolkit;
  }

  /**
   * Gets the grid data with out grab excess V space.
   *
   * @return the grid data with out grab excess V space
   */
  private GridData getGridDataWithOutGrabExcessVSpace() {
    final GridData gridDataFour = new GridData();
    gridDataFour.horizontalAlignment = GridData.FILL;
    gridDataFour.grabExcessHorizontalSpace = true;
    gridDataFour.grabExcessVerticalSpace = true;
    gridDataFour.verticalAlignment = GridData.FILL;
    return gridDataFour;
  }


  /**
   * creates the result tree for Rvw Result dialog
   *
   * @param pidcChildComposite
   */
  private void createResultTreeForPIDC(final Composite pidcChildComposite) {
    // patternfilter initialisation for the filtered tree

    ReviewResultDialog.this.treeHandler = CommonUiUtils.getInstance().getPidcViewPartHandlerIfPresent();

    PidcNameFilter filter = new PidcNameFilter();
    // FilteredT initialisation
    final FilteredTree tree = new FilteredTree(pidcChildComposite, SWT.BORDER, filter, true);

    // Review 286997
    // hiding the filter control due to performance issues

    Text filterControl = tree.getFilterControl();
    filterControl.setVisible(true);
    filterControl.getParent().setVisible(true);
    // gridData initialisation
    final GridData gridData = new GridData();

    applyGridDataStyles(gridData, pidcChildComposite);

    // Get viewer and set styled layout for tree
    TreeViewer viewer = tree.getViewer();
    viewer.getTree().setLayoutData(gridData);

    // set auto expand level
    viewer.setAutoExpandLevel(2);

    // Set Content provider for the tree
    viewer.setContentProvider(new PIDTreeViewContentProvider(this.treeHandler,
        new TreeViewFlagValueProvider(false, true, false, false, false, false, false)));

    // Set Label provider for the tree
    viewer.setLabelProvider(new PIDTreeViewLabelProvider(this.treeHandler, viewer));

    // Call to build tree using setInput(), EMPTY string object indicates to
    // create root node in Content provider
    viewer.setInput("");

    // adding the selection changed listener for the viewer
    viewer.addSelectionChangedListener(event -> {
      final IStructuredSelection selection = (IStructuredSelection) event.getSelection();
      final Object selected = selection.getFirstElement();
      if ((selected instanceof PidcTreeNode) && (((PidcTreeNode) selected).getReviewResult() != null)) {
        ReviewResultDialog.this.okBtn.setEnabled(true);
        ReviewResultDialog.this.pidcTreeNode = (PidcTreeNode) selected;
      }
      else {
        ReviewResultDialog.this.okBtn.setEnabled(false);
        ReviewResultDialog.this.pidcTreeNode = null;
      }
    });

    // ICDM - 574
    // adding the double click listener for the viewer
    viewer.addDoubleClickListener(event -> Display.getDefault().asyncExec(ReviewResultDialog.this::okPressed));

  }

  /**
   * Applies styles to GridData
   *
   * @param gridData
   * @param childComposite
   */
  private void applyGridDataStyles(final GridData gridData, final Composite childComposite) {
    // Apply the standard styles
    gridData.verticalAlignment = GridData.FILL;
    gridData.grabExcessHorizontalSpace = true;
    gridData.grabExcessVerticalSpace = true;
    gridData.horizontalAlignment = GridData.FILL;
    // Create GridLayout
    final GridLayout gridLayout = new GridLayout();
    childComposite.setLayoutData(gridData);
    childComposite.setLayout(gridLayout);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    this.okBtn = createButton(parent, IDialogConstants.OK_ID, "Open", false);
    this.okBtn.setEnabled(false);

    // creates the button with cancel id
    createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
  }

  /**
   * after clicking ok button
   */
  @Override
  protected void okPressed() {

    // get the review result details from the PIDC tree node
    CDRReviewResult reviewResult = this.pidcTreeNode.getReviewResult();
    // show error dialog if the review status is open
    if (reviewResult.getRvwStatus().equalsIgnoreCase(CDRConstants.REVIEW_STATUS.OPEN.getDbType())) {
      MessageDialogUtils.getErrorMessageDialog("ERROR", "Review result does not exist for a review in Open state!");
      return;
    }
    CdrActionSet cdrActionSet = new CdrActionSet();
    cdrActionSet.openRvwRestEditorBasedOnObjInstance(this.pidcTreeNode);

    super.okPressed();

  }
}