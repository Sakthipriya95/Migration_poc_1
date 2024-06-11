/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.wizards.pages;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.listeners.SSDRuleSelectionPageListener;
import com.bosch.caltool.cdr.ui.sorters.FeaValGridTabViewerSorter;
import com.bosch.caltool.cdr.ui.sorters.SSDRelTabViewerSorter;
import com.bosch.caltool.cdr.ui.table.filters.FeaValTabFilter;
import com.bosch.caltool.cdr.ui.table.filters.SSDRelTabFilter;
import com.bosch.caltool.cdr.ui.wizard.page.validator.SSDRuleSelectionPageValidator;
import com.bosch.caltool.cdr.ui.wizard.pages.resolver.SSDRuleSelectionPageResolver;
import com.bosch.caltool.cdr.ui.wizards.CalDataReviewWizard;
import com.bosch.caltool.icdm.client.bo.cdr.CDRHandler;
import com.bosch.caltool.icdm.common.ui.dragdrop.DropFileListener;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueModel;
import com.bosch.caltool.icdm.model.ssd.FeatureValueICDMModel;
import com.bosch.caltool.icdm.model.ssd.SSDFeatureICDMAttrModel;
import com.bosch.caltool.icdm.model.ssd.SSDReleaseIcdmModel;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;
import com.bosch.rcputils.sorters.AbstractViewerSorter;
import com.bosch.rcputils.text.TextUtil;

/**
 * @author bru2cob
 */
public class SSDRuleSelectionPage extends WizardPage {

  /**
   * Defines AbstractViewerSorter - Wp GridTableViewer sorter
   */
  private AbstractViewerSorter feaValTabSorter;
  /**
   * GridTableViewer instance for FC2WP
   */
  private GridTableViewer feaValTabViewer;
  /**
   * Defines AbstractViewerSorter - Wp division GridTableViewer sorter
   */
  private AbstractViewerSorter ssdTabSorter;
  /**
   * GridTableViewer instance for FC2WP
   */
  private GridTableViewer ssdTabViewer;


  private SSDRuleSelectionPageResolver ssdRuleSelectionPageResolver;

  private SSDRuleSelectionPageValidator ssdRuleSelectionPageValidator;

  private SSDRuleSelectionPageListener ssdRuleSelectionPageListener;

  private final CDRHandler cdrHandler = new CDRHandler();

  /**
   * @return the ssdTabViewer
   */
  public GridTableViewer getSsdTabViewer() {
    return this.ssdTabViewer;
  }


  /**
   * Filter text instance
   */
  private Text ssdRelFilterTxt;
  /**
   * FormToolkit instance
   */
  private FormToolkit toolkit;
  /**
   * Filter text instance
   */
  private Text feaValFilterTxt;
  private SSDRelTabFilter ssdReleaseTabFilter;

  protected FeaValTabFilter feaValTabFilters;
  /**
   * ssdfile label
   */
  private Label ssdFileLabel;
  /**
   * ssd file text
   */
  private Text ssdFileTxt;

  /**
   * ssd file name dropped
   */
  private String currentSsdFilePath;


  /**
   * ssd browse
   */
  private Button ssdFileBrowse;
  /**
   * ssd release
   */
  private SSDReleaseIcdmModel selSSDRel;
  /**
   * ssd path
   */
  protected String selSSDPath;
  private Section sectionFileSel;


  /**
   * Instance of wizard
   */
  private CalDataReviewWizard calDataReviewWizard;
  private DropFileListener ssdFileDropFileListener;


  /**
   * @return the calDataReviewWizard
   */
  public CalDataReviewWizard getCalDataReviewWizard() {
    return this.calDataReviewWizard;
  }


  /**
   * @param calDataReviewWizard the calDataReviewWizard to set
   */
  public void setCalDataReviewWizard(final CalDataReviewWizard calDataReviewWizard) {
    this.calDataReviewWizard = calDataReviewWizard;
  }


  /**
   * @param pageName pageName
   */
  public SSDRuleSelectionPage(final String pageName) {
    super(pageName);
    setTitle(pageName);
    setDescription("Please Select SSD File or SSD Release");

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void createControl(final Composite parent) {
    initializeDialogUnits(parent);
    ScrolledComposite scrollComp = new ScrolledComposite(parent, SWT.H_SCROLL | SWT.V_SCROLL);
    final Composite workArea = new Composite(scrollComp, SWT.NONE);
    // create layout for composite
    workArea.setLayout(new GridLayout());
    workArea.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    scrollComp.setExpandHorizontal(true);
    scrollComp.setExpandVertical(true);


    this.calDataReviewWizard = (CalDataReviewWizard) SSDRuleSelectionPage.this.getWizard();


    this.toolkit = new FormToolkit(parent.getDisplay());


    this.sectionFileSel = this.toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    this.sectionFileSel.getDescriptionControl().setEnabled(true);


    this.sectionFileSel.setText("Select the SSD file");

    this.sectionFileSel.setLayoutData(GridDataUtil.getInstance().getGridData());
    final Composite createFileSelComp = this.toolkit.createComposite(this.sectionFileSel, SWT.NONE);
    createFileSelComp.setLayout(new GridLayout(3, false));
    createFileSelComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    createSSDFileSel(createFileSelComp);
    this.sectionFileSel.setClient(createFileSelComp);


    // create wp section
    final Section sectionSSDRel =
        this.toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);

    sectionSSDRel.getDescriptionControl().setEnabled(false);
    sectionSSDRel.setText("SSD Release Nodes");
    sectionSSDRel.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create wp composite
    final Composite ssdRelComp = this.toolkit.createComposite(sectionSSDRel, SWT.NONE);
    ssdRelComp.setLayout(new GridLayout());
    ssdRelComp.setLayoutData(GridDataUtil.getInstance().getGridData());


    // Create Filter text
    createSSDReleaseFilterTxt(ssdRelComp);

    // create wp table
    this.ssdTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(ssdRelComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(100));

    this.ssdReleaseTabFilter = new SSDRelTabFilter();
    // Add PIDC Attribute TableViewer filter
    this.ssdTabViewer.addFilter(this.ssdReleaseTabFilter);
    this.ssdTabSorter = new SSDRelTabViewerSorter();
    this.ssdTabViewer.setComparator(this.ssdTabSorter);

    // Create GridViewerColumns
    createSSDRelViewerColumns();


    // Set content provider
    this.ssdTabViewer.setContentProvider(ArrayContentProvider.getInstance());


    sectionSSDRel.setClient(ssdRelComp);

    // create assisgnment to division section
    final Section sectionFeaVal =
        this.toolkit.createSection(workArea, Section.DESCRIPTION | ExpandableComposite.TITLE_BAR);
    sectionFeaVal.setText("Feature Values");
    sectionFeaVal.getDescriptionControl().setEnabled(false);


    sectionFeaVal.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create wp div composite
    final Composite feaValComp = this.toolkit.createComposite(sectionFeaVal, SWT.NONE);
    feaValComp.setLayout(new GridLayout());
    feaValComp.setLayoutData(GridDataUtil.getInstance().getGridData());

    sectionFeaVal.setClient(feaValComp);


    this.feaValTabSorter = new FeaValGridTabViewerSorter();

    // Create Filter text
    createFeaValFilterTxt(feaValComp);
    this.feaValTabViewer = GridTableViewerUtil.getInstance().createGridTableViewer(feaValComp,
        SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL,
        GridDataUtil.getInstance().getHeightHintGridData(150));
    // Create GridViewerColumns
    createFeaValGridViewerColumns();
    this.feaValTabFilters = new FeaValTabFilter();

    this.feaValTabViewer.addFilter(this.feaValTabFilters);

    this.feaValTabViewer.setComparator(this.feaValTabSorter);
    // Set content provider
    this.feaValTabViewer.setContentProvider(ArrayContentProvider.getInstance());


    this.ssdRuleSelectionPageValidator =
        new SSDRuleSelectionPageValidator(this.calDataReviewWizard, this.calDataReviewWizard.getSsdRuleSelPage());
    this.ssdRuleSelectionPageListener = new SSDRuleSelectionPageListener(this.calDataReviewWizard);
    this.ssdRuleSelectionPageResolver = new SSDRuleSelectionPageResolver();
    this.ssdRuleSelectionPageListener.createActionListeners();
    scrollComp.setContent(workArea);
    scrollComp.setMinSize(workArea.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    setControl(scrollComp);
  }


  /**
   * @param message message
   */
  public void setWarnMessage(final String message) {
    this.sectionFileSel.setDescription(message);
    this.sectionFileSel.getDescriptionControl().setForeground(Display.getCurrent().getSystemColor(SWT.COLOR_RED));
    this.ssdTabViewer.getGrid().setEnabled(false);
    this.feaValTabViewer.getGrid().setEnabled(false);
  }


  /**
   * @throws ApicWebServiceException
   */
  public void setFeaValInput() {
    final IStructuredSelection selection = (IStructuredSelection) this.ssdTabViewer.getSelection();

    if (selection != null) {
      this.selSSDRel = (SSDReleaseIcdmModel) selection.getFirstElement();
      List<FeatureValueICDMModel> dependencyList = this.selSSDRel.getDependencyList();
      List<FeatureValueICDMModel> feaValueModel = new ArrayList<>();
      for (FeatureValueICDMModel featureValueModel : dependencyList) {
        FeatureValueICDMModel featureValueICDMModel = new FeatureValueICDMModel();
        featureValueICDMModel.setFeatureId(featureValueModel.getFeatureId());
        featureValueICDMModel.setFeatureText(featureValueModel.getFeatureText());
        featureValueICDMModel.setValueId(featureValueModel.getValueId());
        featureValueICDMModel.setValueText(featureValueModel.getValueText());
        feaValueModel.add(featureValueICDMModel);
      }
      List<SSDFeatureICDMAttrModel> ssdFeaAttrList;
      try {
        ssdFeaAttrList = this.cdrHandler.getSSDFeatureICDMAttrModelList(feaValueModel);
        this.feaValTabViewer.setInput(ssdFeaAttrList);

      }
      catch (ApicWebServiceException e) {
        CDMLogger.getInstance().errorDialog(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
      }
    }

  }


  /**
   * @param firstRowComposite
   */
  private void createSSDFileSel(final Composite firstRowComposite) {
    // ssd file label
    this.ssdFileLabel = new Label(firstRowComposite, SWT.NONE);
    this.ssdFileLabel.setText("Select SSD File : ");
    this.ssdFileTxt = new Text(firstRowComposite, SWT.SINGLE | SWT.BORDER);
    this.ssdFileTxt.setEditable(true);

    // Drag drop ssd file
    this.ssdFileDropFileListener = new DropFileListener(this.ssdFileTxt, new String[] { "*.SSD" });
    this.ssdFileDropFileListener.addDropFileListener(true);
    this.ssdFileDropFileListener.setEditable(true);

    this.ssdFileTxt.addModifyListener(new ModifyListener() {

      @Override
      public void modifyText(final ModifyEvent modifyevent) {
        String dropSSdFilePath = SSDRuleSelectionPage.this.ssdFileDropFileListener.getDropFilePath();
        if ((!CommonUtils.isEmptyString(dropSSdFilePath)) &&
            !CommonUtils.isEqual(SSDRuleSelectionPage.this.currentSsdFilePath, dropSSdFilePath)) {
          File file = new File(dropSSdFilePath);
          String simpleFileName = file.getName();
          SSDRuleSelectionPage.this.ssdRuleSelectionPageListener.setSSDfields(dropSSdFilePath, simpleFileName);
        }
      }
    });

    final GridData workingPkgData = new GridData(SWT.FILL, SWT.NONE, true, false);
    workingPkgData.widthHint = new PixelConverter(this.ssdFileTxt).convertWidthInCharsToPixels(25);
    this.ssdFileTxt.setLayoutData(workingPkgData);
    // ssd browse
    this.ssdFileBrowse = new Button(firstRowComposite, SWT.PUSH);
    final Image browseImage = ImageManager.INSTANCE.getRegisteredImage(ImageKeys.BROWSE_BUTTON_ICON);
    this.ssdFileBrowse.setImage(browseImage);
    this.ssdFileBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
    this.ssdFileBrowse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
  }


  /**
   * to update page containers
   */
  public void updatePageContainer() {
    getContainer().updateButtons();
  }


  /**
   * Create wp div table filter text
   *
   * @param createFc2wpComp
   */
  private void createFeaValFilterTxt(final Composite createFc2wpComp) {
    this.feaValFilterTxt = TextUtil.getInstance().createFilterText(this.toolkit, createFc2wpComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);

    this.feaValFilterTxt.setFocus();

  }

  /**
   * Create wp table filter text
   *
   * @param createFc2wpComp
   */
  private void createSSDReleaseFilterTxt(final Composite createFc2wpComp) {
    this.ssdRelFilterTxt = TextUtil.getInstance().createFilterText(this.toolkit, createFc2wpComp,
        GridDataUtil.getInstance().getTextGridData(), CommonUIConstants.TEXT_FILTER);

    this.ssdRelFilterTxt.setFocus();

  }


  /**
   * create wp table columns
   */
  private void createSSDRelViewerColumns() {
    createRelNumCol();


    createDateCol();


    createDescCol();

  }

  /**
   * Desc Col
   */
  private void createDescCol() {
    final GridViewerColumn descColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.ssdTabViewer, "Desciption", 200);

    descColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDReleaseIcdmModel releaseDesc = (SSDReleaseIcdmModel) element;
        return releaseDesc.getReleaseDesc();
      }


    });
    // Add column selection listener
    descColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(descColumn.getColumn(), 2, this.ssdTabSorter, this.ssdTabViewer));
  }

  /**
   * date col
   */
  private void createDateCol() {
    final GridViewerColumn dateColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.ssdTabViewer, "Date", 200);

    dateColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDReleaseIcdmModel release = (SSDReleaseIcdmModel) element;
        return release.getReleaseDate();
      }


    });
    // Add column selection listener
    dateColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(dateColumn.getColumn(), 1, this.ssdTabSorter, this.ssdTabViewer));
  }

  /**
   *
   */
  private void createRelNumCol() {
    final GridViewerColumn nameColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.ssdTabViewer, "Release Number", 200);
    nameColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDReleaseIcdmModel release = (SSDReleaseIcdmModel) element;
        return release.getRelease();
      }


    });
    // Add column selection listener
    nameColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(nameColumn.getColumn(), 0, this.ssdTabSorter, this.ssdTabViewer));
  }

  /**
   * create wp div table columns
   */
  private void createFeaValGridViewerColumns() {

    final GridViewerColumn divisionCol =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.feaValTabViewer, "SSD Feature", 150);

    divisionCol.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDFeatureICDMAttrModel feaModel = (SSDFeatureICDMAttrModel) element;
        return feaModel.getFeaValModel().getFeatureText();
      }


    });
    // Add column selection listener
    divisionCol.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(divisionCol.getColumn(), 0, this.feaValTabSorter, this.feaValTabViewer));


    final GridViewerColumn resourceColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.feaValTabViewer, "SSD Value", 150);

    resourceColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDFeatureICDMAttrModel feaModel = (SSDFeatureICDMAttrModel) element;
        return feaModel.getFeaValModel().getValueText();
      }


    });
    // Add column selection listener
    resourceColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(resourceColumn.getColumn(), 1, this.feaValTabSorter, this.feaValTabViewer));

    final GridViewerColumn wpIDColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.feaValTabViewer, "ICDM Attribute", 150);

    wpIDColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDFeatureICDMAttrModel feaModel = (SSDFeatureICDMAttrModel) element;
        return getAttrName(feaModel.getAttrValModel());
      }


    });
    // Add column selection listener
    wpIDColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(wpIDColumn.getColumn(), 2, this.feaValTabSorter, this.feaValTabViewer));


    final GridViewerColumn primaryContactColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.feaValTabViewer, "ICDM Value", 150);

    primaryContactColumn.setLabelProvider(new ColumnLabelProvider() {

      @Override
      public String getText(final Object element) {
        SSDFeatureICDMAttrModel feaModel = (SSDFeatureICDMAttrModel) element;
        if ((feaModel == null) || (feaModel.getAttrValModel() == null) ||
            (feaModel.getAttrValModel().getValue() == null)) {
          return "";
        }
        return feaModel.getAttrValModel().getValue().getName();
      }

    });
    primaryContactColumn.getColumn().addSelectionListener(GridTableViewerUtil.getInstance()
        .getSelectionAdapter(primaryContactColumn.getColumn(), 3, this.feaValTabSorter, this.feaValTabViewer));

  }

  /**
   * @return the attr name
   */
  private String getAttrName(final AttributeValueModel attrValModel) {
    if (attrValModel != null) {
      Attribute attribute = attrValModel.getAttr();
      if (attribute != null) {
        return attribute.getName();
      }
    }
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean canFlipToNextPage() {
    return this.ssdRuleSelectionPageValidator.checkNextEnabled();
  }

  /**
   * next pressed
   */
  public void nextPressed() {
    // Calling setinput method in resolver
    this.ssdRuleSelectionPageResolver.setInput(this.calDataReviewWizard);
    this.ssdRuleSelectionPageResolver.processNextPressed();
    if (this.calDataReviewWizard.isDeltaReview() && !this.calDataReviewWizard.isProjectDataDeltaReview()) {
      this.calDataReviewWizard.getFilesSelWizPage().getReviewFilesSelectionPageResolver()
          .fillUIData(this.calDataReviewWizard);
    }
  }


  /**
   * Data for cancelled Review
   */
  public void setDataForCancelPressed() {
    this.ssdRuleSelectionPageResolver.setInput(this.calDataReviewWizard);
  }


  /**
   *
   */
  public void backPressed() {
    this.ssdRuleSelectionPageResolver.processBackPressed();
  }


  /**
   * @return the wpTabViewer
   */
  public GridTableViewer getWpTabViewer() {
    return this.ssdTabViewer;
  }


  /**
   * @return the wpDivTabViewer
   */
  public GridTableViewer getWpDivTabViewer() {
    return this.feaValTabViewer;
  }

  /**
   * @param selSSDPath the selSSDPath to set
   */
  public void setSelSSDPath(final String selSSDPath) {
    this.selSSDPath = selSSDPath;
  }


  /**
   * @return the feaValTabSorter
   */
  public AbstractViewerSorter getFeaValTabSorter() {
    return this.feaValTabSorter;
  }


  /**
   * @param feaValTabSorter the feaValTabSorter to set
   */
  public void setFeaValTabSorter(final AbstractViewerSorter feaValTabSorter) {
    this.feaValTabSorter = feaValTabSorter;
  }


  /**
   * @return the feaValTabViewer
   */
  public GridTableViewer getFeaValTabViewer() {
    return this.feaValTabViewer;
  }


  /**
   * @param feaValTabViewer the feaValTabViewer to set
   */
  public void setFeaValTabViewer(final GridTableViewer feaValTabViewer) {
    this.feaValTabViewer = feaValTabViewer;
  }


  /**
   * @return the ssdTabSorter
   */
  public AbstractViewerSorter getSsdTabSorter() {
    return this.ssdTabSorter;
  }


  /**
   * @param ssdTabSorter the ssdTabSorter to set
   */
  public void setSsdTabSorter(final AbstractViewerSorter ssdTabSorter) {
    this.ssdTabSorter = ssdTabSorter;
  }


  /**
   * @return the ssdRuleSelectionPageResolver
   */
  public SSDRuleSelectionPageResolver getSsdRuleSelectionPageResolver() {
    return this.ssdRuleSelectionPageResolver;
  }


  /**
   * @param ssdRuleSelectionPageResolver the ssdRuleSelectionPageResolver to set
   */
  public void setSsdRuleSelectionPageResolver(final SSDRuleSelectionPageResolver ssdRuleSelectionPageResolver) {
    this.ssdRuleSelectionPageResolver = ssdRuleSelectionPageResolver;
  }


  /**
   * @return the ssdRuleSelectionPageValidator
   */
  public SSDRuleSelectionPageValidator getSsdRuleSelectionPageValidator() {
    return this.ssdRuleSelectionPageValidator;
  }


  /**
   * @param ssdRuleSelectionPageValidator the ssdRuleSelectionPageValidator to set
   */
  public void setSsdRuleSelectionPageValidator(final SSDRuleSelectionPageValidator ssdRuleSelectionPageValidator) {
    this.ssdRuleSelectionPageValidator = ssdRuleSelectionPageValidator;
  }


  /**
   * @return the ssdRuleSelectionPageListener
   */
  public SSDRuleSelectionPageListener getSsdRuleSelectionPageListener() {
    return this.ssdRuleSelectionPageListener;
  }


  /**
   * @param ssdRuleSelectionPageListener the ssdRuleSelectionPageListener to set
   */
  public void setSsdRuleSelectionPageListener(final SSDRuleSelectionPageListener ssdRuleSelectionPageListener) {
    this.ssdRuleSelectionPageListener = ssdRuleSelectionPageListener;
  }


  /**
   * @return the ssdRelFilterTxt
   */
  public Text getSsdRelFilterTxt() {
    return this.ssdRelFilterTxt;
  }


  /**
   * @param ssdRelFilterTxt the ssdRelFilterTxt to set
   */
  public void setSsdRelFilterTxt(final Text ssdRelFilterTxt) {
    this.ssdRelFilterTxt = ssdRelFilterTxt;
  }


  /**
   * @return the toolkit
   */
  public FormToolkit getToolkit() {
    return this.toolkit;
  }


  /**
   * @param toolkit the toolkit to set
   */
  public void setToolkit(final FormToolkit toolkit) {
    this.toolkit = toolkit;
  }


  /**
   * @return the feaValFilterTxt
   */
  public Text getFeaValFilterTxt() {
    return this.feaValFilterTxt;
  }


  /**
   * @param feaValFilterTxt the feaValFilterTxt to set
   */
  public void setFeaValFilterTxt(final Text feaValFilterTxt) {
    this.feaValFilterTxt = feaValFilterTxt;
  }


  /**
   * @return the ssdReleaseTabFilter
   */
  public SSDRelTabFilter getSsdReleaseTabFilter() {
    return this.ssdReleaseTabFilter;
  }


  /**
   * @param ssdReleaseTabFilter the ssdReleaseTabFilter to set
   */
  public void setSsdReleaseTabFilter(final SSDRelTabFilter ssdReleaseTabFilter) {
    this.ssdReleaseTabFilter = ssdReleaseTabFilter;
  }


  /**
   * @return the feaValTabFilters
   */
  public FeaValTabFilter getFeaValTabFilters() {
    return this.feaValTabFilters;
  }


  /**
   * @param feaValTabFilters the feaValTabFilters to set
   */
  public void setFeaValTabFilters(final FeaValTabFilter feaValTabFilters) {
    this.feaValTabFilters = feaValTabFilters;
  }


  /**
   * @return the ssdFileLabel
   */
  public Label getSsdFileLabel() {
    return this.ssdFileLabel;
  }


  /**
   * @param ssdFileLabel the ssdFileLabel to set
   */
  public void setSsdFileLabel(final Label ssdFileLabel) {
    this.ssdFileLabel = ssdFileLabel;
  }


  /**
   * @return the ssdFileBrowse
   */
  public Button getSsdFileBrowse() {
    return this.ssdFileBrowse;
  }


  /**
   * @param ssdFileBrowse the ssdFileBrowse to set
   */
  public void setSsdFileBrowse(final Button ssdFileBrowse) {
    this.ssdFileBrowse = ssdFileBrowse;
  }


  /**
   * @return the selSSDRel
   */
  public SSDReleaseIcdmModel getSelSSDRel() {
    return this.selSSDRel;
  }


  /**
   * @param selSSDRel the selSSDRel to set
   */
  public void setSelSSDRel(final SSDReleaseIcdmModel selSSDRel) {
    this.selSSDRel = selSSDRel;
  }


  /**
   * @return the sectionFileSel
   */
  public Section getSectionFileSel() {
    return this.sectionFileSel;
  }


  /**
   * @param sectionFileSel the sectionFileSel to set
   */
  public void setSectionFileSel(final Section sectionFileSel) {
    this.sectionFileSel = sectionFileSel;
  }


  /**
   * @return the cdrHandler
   */
  public CDRHandler getCdrHandler() {
    return this.cdrHandler;
  }


  /**
   * @return the selSSDPath
   */
  public String getSelSSDPath() {
    return this.selSSDPath;
  }


  /**
   * @param ssdTabViewer the ssdTabViewer to set
   */
  public void setSsdTabViewer(final GridTableViewer ssdTabViewer) {
    this.ssdTabViewer = ssdTabViewer;
  }

  /**
   * @return the ssdFileTxt
   */
  public Text getSsdFileTxt() {
    return this.ssdFileTxt;
  }

  /**
   * @param ssdFileTxt the ssdFileTxt to set
   */
  public void setSsdFileTxt(final Text ssdFileTxt) {
    this.ssdFileTxt = ssdFileTxt;
  }


  /**
   * @return the currentSsdFilePath
   */
  public String getCurrentSsdFilePath() {
    return this.currentSsdFilePath;
  }


  /**
   * @param currentSsdFilePath the dropFileName to set
   */
  public void setCurrentSsdFilePath(final String currentSsdFilePath) {
    this.currentSsdFilePath = currentSsdFilePath;
  }

}
