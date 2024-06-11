/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.window.ToolTip;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.actions.PIDCGroupedAttrActionSet;
import com.bosch.caltool.apic.ui.editors.PIDCCompareEditorInput;
import com.bosch.caltool.apic.ui.editors.compare.ComparePIDCPage;
import com.bosch.caltool.apic.ui.editors.pages.PIDCAttrPage;
import com.bosch.caltool.apic.ui.views.providers.PIDCGrpdAttrChangesContentProvider;
import com.bosch.caltool.apic.ui.views.providers.PIDCGrpdAttrChangesLabelProvider;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionAttributeBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVersionStatus;
import com.bosch.caltool.icdm.client.bo.apic.pidc.ProjectAttributesMovementModel;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.dialogs.AbstractDialog;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.PredefinedAttrValue;
import com.bosch.caltool.icdm.model.apic.attr.ProjectAttributesUpdationModel;
import com.bosch.caltool.icdm.model.apic.pidc.GroupdAttrPredefAttrModel;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.caltool.icdm.model.user.NodeAccess;
import com.bosch.caltool.icdm.ws.rest.client.apic.ProjectAttributesUpdationServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.CustomTreeViewer;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.ui.forms.SectionUtil;


/**
 * @author dmo5cob
 */
public class PIDCGrpdAttrChangesDialog extends AbstractDialog {

  /**
   * ICDM-2625
   */
  private static final String OK_LABEL = "OK";


  /**
   * constant for left mouse click
   */
  protected static final int LEFT_MOUSE_CLICK = 1;

  /**
   * Composite instance
   */
  private Composite top;
  /**
   * FormToolkit instance
   */
  private FormToolkit formToolkit;
  /**
   * Composite instance
   */
  private Composite composite;
  /**
   * Section instance
   */
  private Section section;
  /**
   * Form instance
   */
  private Form form;
  /**
   * CustomTreeViewer
   */
  private CustomTreeViewer attrsTreeViewer;
  private final AbstractFormPage formPage;
  private final AttributeValue selAttrValue;
  private final Set<AttributeValue> attrValuesToBeUpdated = new HashSet<>();
  private final Set<IProjectAttribute> ipidcAttrToBeUpdated = new HashSet<>();
  private Button saveBtn;
  /**
   * PIDCGrpdAttrChangesDialog width
   */
  private static final int DIALOG_WIDTH = 900;
  /**
   * PIDCGrpdAttrChangesDialog height
   */
  private static final int DIALOG_NORM_HEIGHT = 700;

  /**
   * Constant
   */
  private static final String PIDC_LEVEL = "PIDC Level";
  /**
   * Constant
   */
  private static final String INVISIBLE = "Invisible";
  /**
   * Constant
   */
  private static final String VARIANT_LEVEL = "Variant Level";
  /**
   * Constant
   */
  private static final String SUB_VARIANT_LEVEL = "SubVariant Level";

  private IProjectAttribute grpdAttr;

  private boolean saveSuccess;

  // ICDM-2625
  private final boolean isAcceptChangesPossible;

  // ICDM-2625
  private final PidcVersion pidcVersion;

  // ICDM-2625
  private List<GroupdAttrPredefAttrModel> grpAttrPredefAttrModelList = new ArrayList<>();

  // ICDM-2625
  private final Map<Long, PidcVersionAttribute> allPIDCAttrMap;
  // key -IProjectAttribute : value - value id
  private final Map<IProjectAttribute, Long> grpAttrValMap = new ConcurrentHashMap<>();
  private final Map<PredefinedAttrValue, String> attrValPidcValMap = new ConcurrentHashMap<>();
  private final Map<PredefinedAttrValue, String> attrValPidcLvlMap = new ConcurrentHashMap<>();
  // key -grouped attribute id : value - set of id of common predefined attribute,to identify existing relevant
  // predefined attribute for a grouped attribute value
  private final Map<Long, Set<Long>> commonPredefAttrMap = new HashMap<>();
  private ArrayList<PredefinedAttrValue> predefAttrValList = new ArrayList<>();

  private final AttributeValueClientBO selAttrValueBO;

  private final Set<PredefinedAttrValue> preDefinedAttrValueSet;

  private PidcDataHandler pidcDataHandler;

  private final Map<Long, Map<Long, PidcVariantAttribute>> variantAttributeMap;

  private final Map<Long, Map<Long, PidcSubVariantAttribute>> subVariantAttributeMap;

  private final Map<Long, Long> existingGrpAttrValMap;

  private final PidcVersionBO pidcVersionBO;

  /**
   * @param shell Main shell
   * @param page AbstractFormPage
   * @param attrValue The grouped attrs value
   * @param pidcAttr IProjectAttribute
   * @param pidcVersion PIDC Version
   * @param isAcceptChangesPossible boolean for slection
   * @param allPIDCAttrMap Map<Long, PidcVersionAttribute>
   * @param preDefinedAttrValueSet Set<PredefinedAttrValue>
   * @param grpAttrPredefAttrModelList List<GroupdAttrPredefAttrModel>
   * @param existingGrpAttrValMap map of existing group attribute value
   */
  public PIDCGrpdAttrChangesDialog(final Shell shell, final AbstractFormPage page, final AttributeValue attrValue,
      final IProjectAttribute pidcAttr, final PidcVersion pidcVersion, final boolean isAcceptChangesPossible,
      final Map<Long, PidcVersionAttribute> allPIDCAttrMap, final Set<PredefinedAttrValue> preDefinedAttrValueSet,
      final List<GroupdAttrPredefAttrModel> grpAttrPredefAttrModelList, final Map<Long, Long> existingGrpAttrValMap) {
    super(shell); // calling parent constructor
    this.formPage = page;
    if (this.formPage instanceof PIDCAttrPage) {
      this.pidcDataHandler = ((PIDCAttrPage) this.formPage).getPidcDataHandler();
    }
    this.existingGrpAttrValMap = existingGrpAttrValMap;
    if (this.formPage instanceof ComparePIDCPage) {
      PIDCCompareEditorInput editorInput = (PIDCCompareEditorInput) ((ComparePIDCPage) this.formPage).getEditorInput();
      if (editorInput.getComparePidcHandler().isVersionCompare()) {
        this.pidcDataHandler = editorInput.getComparePidcHandler().getCompareObjectsHandlerMap()
            .get(pidcVersion.getId()).getPidcDataHandler();
      }
      else {
        this.pidcDataHandler = editorInput.getComparePidcHandler().getPidcVersionBO().getPidcDataHandler();
      }

    }
    this.variantAttributeMap = this.pidcDataHandler.getVariantAttributeMap();
    this.subVariantAttributeMap = this.pidcDataHandler.getSubVariantAttributeMap();
    this.selAttrValue = attrValue;
    this.selAttrValueBO = new AttributeValueClientBO(attrValue);
    this.preDefinedAttrValueSet =
        null == preDefinedAttrValueSet ? this.selAttrValueBO.getPreDefinedAttrValueSet() : preDefinedAttrValueSet;
    this.grpdAttr = pidcAttr;
    // ICDM-2625
    this.isAcceptChangesPossible = isAcceptChangesPossible;
    // ICDM-2625
    this.pidcVersion = pidcVersion;
    this.pidcVersionBO = new PidcVersionBO(this.pidcVersion, this.pidcDataHandler);
    this.allPIDCAttrMap = allPIDCAttrMap;
    // ICDM-2625
    if (null == this.selAttrValue) {
      if (grpAttrPredefAttrModelList == null) {
        PIDCGroupedAttrActionSet actionSet =
            new PIDCGroupedAttrActionSet(this.pidcDataHandler, ((PIDCAttrPage) this.formPage).getPidcVersionBO());
        this.grpAttrPredefAttrModelList = actionSet.getAllPidcGrpAttrVal(this.pidcVersion, this.allPIDCAttrMap, null);
      }
      else {
        this.grpAttrPredefAttrModelList = grpAttrPredefAttrModelList;
      }
    }
    else if (null != this.preDefinedAttrValueSet) {
      for (PredefinedAttrValue predefAttrVal : this.preDefinedAttrValueSet) {
        List<String> valLvl = new ArrayList<>();
        valLvl = getValueForGrpdAndPredefinedAttrs(predefAttrVal);
        this.attrValPidcValMap.put(predefAttrVal, valLvl.get(0));
        this.attrValPidcLvlMap.put(predefAttrVal, valLvl.get(1));
      }
      loadChildItems();
    }
  }

  /**
   *
   */
  private void loadChildItems() {
    this.predefAttrValList = new ArrayList<>();
    for (PredefinedAttrValue predefAttrVal : this.preDefinedAttrValueSet) {
      PidcVersionAttribute pidAttr = this.allPIDCAttrMap.get(predefAttrVal.getPredefinedAttrId());
      PidcVersionAttributeBO pidcVersionHandler =
          new PidcVersionAttributeBO(pidAttr, new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
      if (pidAttr.isAtChildLevel()) {
        if ((this.grpdAttr instanceof PidcVersionAttribute) &&
            !this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefAttrVal.getPredefinedAttrId())) {
          this.predefAttrValList.add(predefAttrVal);
        }
        else if ((this.grpdAttr instanceof PidcVariantAttribute) &&
            pidcVersionHandler.isNotInvisibleVarAttr(predefAttrVal.getPredefinedAttrId(), this.grpdAttr)) {
          PidcVariantAttribute varAttr = this.variantAttributeMap
              .get(((PidcVariantAttribute) this.grpdAttr).getVariantId()).get(pidAttr.getAttrId());

          if (null != varAttr) {
            if (varAttr.isAtChildLevel()) {
              this.predefAttrValList.add(predefAttrVal);
            }
            else {
              if (null != predefAttrVal.getPredefinedValueId()) {
                if (!varAttr.getName().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
                  this.predefAttrValList.add(predefAttrVal);
                }
              }
              else if (!varAttr.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
                // Task 229131
                this.predefAttrValList.add(predefAttrVal);
              }
            }
          }
        }
        else if ((this.grpdAttr instanceof PidcSubVariantAttribute) &&
            pidcVersionHandler.isNotInvisibleSubVarAttr(predefAttrVal.getPredefinedAttrId(), this.grpdAttr)) {
          PidcSubVariantAttribute subvarAttr = this.subVariantAttributeMap
              .get(((PidcSubVariantAttribute) this.grpdAttr).getSubVariantId()).get(pidAttr.getAttrId());

          if (null != subvarAttr) {

            if (null != predefAttrVal.getPredefinedValueId()) {
              if (!subvarAttr.getName().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
                this.predefAttrValList.add(predefAttrVal);
              }
            }
            else if (!subvarAttr.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
              this.predefAttrValList.add(predefAttrVal);
            }

          }
          else {
            this.predefAttrValList.add(predefAttrVal);
          }
        }
      }
      else {
        if ((this.grpdAttr instanceof PidcVersionAttribute) &&
            !this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefAttrVal.getPredefinedAttrId())) {
          if (null != predefAttrVal.getPredefinedValueId()) {
            if (!pidAttr.getName().equalsIgnoreCase(predefAttrVal.getPredefinedValue())) {
              this.predefAttrValList.add(predefAttrVal);
            }
          }
          else if (!pidAttr.getUsedFlag().equalsIgnoreCase(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
            this.predefAttrValList.add(predefAttrVal);
          }
        }
        else if (pidcVersionHandler.isNotInvisibleAttr(predefAttrVal.getPredefinedAttrId(), this.grpdAttr)) {
          this.predefAttrValList.add(predefAttrVal);
        }
      }
    }

  }

  /**
   * @param predefAttrVal
   * @return
   */
  private List<String> getValueForGrpdAndPredefinedAttrs(final PredefinedAttrValue predefAttrVal) {
    List<String> result = new ArrayList<>();
    PidcVersionAttribute pidAttr = this.allPIDCAttrMap.get(predefAttrVal.getPredefinedAttrId());
    PidcVersionAttributeBO handler =
        new PidcVersionAttributeBO(pidAttr, new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
    if (null != pidAttr) {
      // Grp attr at PIDC level
      if (this.grpdAttr instanceof PidcVersionAttribute) {
        if (pidAttr.isAtChildLevel()) {
          result.add("<Different Values>");
          if (handler.isVisible()) {
            result.add(VARIANT_LEVEL + " : " + "* To be moved to PIDC Level");
          }
          else {
            result.add(INVISIBLE);
          }
        }
        else {
          result.add(null == pidAttr.getValue() ? "" : pidAttr.getValue());
          if (handler.isVisible()) {
            result.add(PIDC_LEVEL);
          }
          else {
            result.add(INVISIBLE);
          }
        }

      }
      else if (this.grpdAttr instanceof PidcVariantAttribute) {
        // Grp attr at variant level
        if (!pidAttr.isAtChildLevel()) {
          if (null == pidAttr.getValue()) {
            result.add("");
          }
          else {
            result.add(pidAttr.getValue());
          }
          if (handler.isVisible()) {
            result.add(PIDC_LEVEL + "->" + VARIANT_LEVEL);
          }
          else {
            result.add(INVISIBLE);
          }
        }
        else if (pidAttr.isAtChildLevel()) {
          PidcVariantAttribute varAttr = this.variantAttributeMap
              .get(((PidcVariantAttribute) this.grpdAttr).getVariantId()).get(pidAttr.getAttrId());
          PidcVariantAttributeBO varhandler = new PidcVariantAttributeBO(varAttr,
              new PidcVariantBO(this.pidcVersion,
                  this.pidcDataHandler.getVariantMap().get(((PidcVariantAttribute) this.grpdAttr).getVariantId()),
                  this.pidcDataHandler));

          if ((null != varAttr) && varAttr.isAtChildLevel()) {
            result.add("<Different Values>");
            if (varhandler.isVisible()) {
              result.add(SUB_VARIANT_LEVEL + " : " + "* To be moved to Variant Level");
            }
            else {
              result.add(INVISIBLE);
            }
          }
          else {
            if ((null != varAttr) && (null == varAttr.getValue())) {
              result.add("");
            }
            else {
              if (null != varAttr) {
                result.add(varAttr.getValue());
              }
            }
            if ((null != varAttr) && varhandler.isVisible()) {
              result.add(VARIANT_LEVEL);
            }
            else {
              result.add(INVISIBLE);
            }
          }
        }
      }
      else if (this.grpdAttr instanceof PidcSubVariantAttribute) {
        // Grp attr at subvariant level
        if (!pidAttr.isAtChildLevel()) {

          if (null == pidAttr.getValue()) {
            result.add("");
          }
          else {
            result.add(pidAttr.getValue());
          }
          if (handler.isVisible()) {
            result.add(PIDC_LEVEL + "->" + SUB_VARIANT_LEVEL);
          }
          else {
            result.add(INVISIBLE);
          }
        }
        else if (pidAttr.isAtChildLevel()) {
          PidcVariantAttribute varAttr = this.variantAttributeMap
              .get(((PidcSubVariantAttribute) this.grpdAttr).getVariantId()).get(pidAttr.getAttrId());
          PidcVariantAttributeBO varhandler = new PidcVariantAttributeBO(varAttr,
              new PidcVariantBO(this.pidcVersion,
                  this.pidcDataHandler.getVariantMap().get(((PidcSubVariantAttribute) this.grpdAttr).getVariantId()),
                  this.pidcDataHandler));

          if (varAttr.isAtChildLevel()) {
            PidcSubVariantAttribute subVarAttr = this.subVariantAttributeMap
                .get(((PidcSubVariantAttribute) this.grpdAttr).getSubVariantId()).get(pidAttr.getAttrId());


            if (null == subVarAttr.getValue()) {
              result.add("");
            }
            else {
              result.add(subVarAttr.getValue());
            }
            if (varhandler.isVisible()) {
              result.add(SUB_VARIANT_LEVEL);
            }
            else {
              result.add(INVISIBLE);
            }
          }
          else {
            if (null == varAttr.getValue()) {
              result.add("");
            }
            else {
              result.add(varAttr.getValue());
            }
            if (varhandler.isVisible()) {
              result.add(VARIANT_LEVEL + "->" + SUB_VARIANT_LEVEL);
            }
            else {
              result.add(INVISIBLE);
            }
          }
        }
      }
    }
    return result;
  }

  /**
   * create contents
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);

    // ICDM-2625
    if (!this.isAcceptChangesPossible) {
      // If the selection is from PIDC page

      // Set title
      setTitle("Grouped Attributes");
      // Set the message
      setMessage("Check the current Values of Grouped Attributes.");
    }
    else {
      // ICDM-2636
      // If the selection is from compare PIDc page
      if (this.formPage instanceof ComparePIDCPage) {
        // Set title
        setTitle("Copied Grouped Attribute value Changes");
        // Set the message
        setMessage(
            "You have copied a grouped attribute. Some values of grouped attributes in your PIDC have been modified.");
      }
      else {
        // Set title
        setTitle("Accept changes in Grouped Attributes");
        // Set the message
        setMessage("Some values of grouped attributes in your PIDC have been modified.");
      }
    }
    return contents;
  }

  /**
   * @see org.eclipse.jface.dialogs.Dialog#createButtonsForButtonBar(org.eclipse .swt.widgets.Composite)
   */
  @Override
  protected void createButtonsForButtonBar(final Composite parent) {
    // NA
  }

  /**
   *
   */
  private void createButtons() {
    final Composite buttonGroup = new Composite(this.top, SWT.NONE);

    buttonGroup.setLayout(new GridLayout());

    // ICDM-2625
    if (this.isAcceptChangesPossible) {

      this.saveBtn =
          createButton(buttonGroup, IDialogConstants.OK_ID, "Save, with values of Grouped Attributes", false);
      PIDCGrpdAttrChangesDialog.this.saveBtn.setEnabled(false);
      // creating cancel button
      createButton(buttonGroup, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, true);
    }
    else {
      // creating cancel button
      createButton(buttonGroup, IDialogConstants.CANCEL_ID, OK_LABEL, true);
    }
  }


  /**
   * configure the shell and set the title
   */
  @Override
  protected void configureShell(final Shell newShell) {
    // ICDM-2625
    if (!this.isAcceptChangesPossible) {
      newShell.setText("PIDC Grouped Attributes");
    }
    else {
      if (this.formPage instanceof ComparePIDCPage) {
        newShell.setText("Copy Grouped Attribute");
      }
      else {
        newShell.setText("Edit Value");
      }
    }
    final Point newSize = newShell.computeSize(DIALOG_WIDTH, DIALOG_NORM_HEIGHT, true);
    newShell.setSize(newSize);

    newShell.layout(true, true);
    super.configureShell(newShell);
  }

  /**
   * Creates the gray area
   *
   * @param parent the parent composite
   * @return Control
   */
  @Override
  protected Control createDialogArea(final Composite parent) {
    this.top = (Composite) super.createDialogArea(parent);
    this.top.setLayout(new GridLayout());
    createComposite();
    createButtons();

    parent.layout(true, true);
    return this.top;
  }


  /**
   * This method initializes composite
   */
  private void createComposite() {
    this.composite = getFormToolkit().createComposite(this.top);
    GridLayout gridLayout = new GridLayout();
    this.composite.setLayout(gridLayout);

    this.composite.setLayoutData(GridDataUtil.getInstance().getGridData());
    // create section
    createSection(gridLayout);


  }

  /**
   * This method initializes section
   */
  private void createSection(final GridLayout gridLayout) {
    this.section = SectionUtil.getInstance().createSection(this.composite, getFormToolkit(),
        "Changes in grouped attributes", ExpandableComposite.TITLE_BAR);
    this.section.setLayoutData(GridDataUtil.getInstance().getGridData());

    // create form
    createForm(gridLayout);


    this.section.setClient(this.form);
  }

  /**
   * This method initializes form
   */
  private void createForm(final GridLayout gridLayout) {
    this.form = getFormToolkit().createForm(this.section);
    // set the layout
    this.form.getBody().setLayout(gridLayout);


    createTableViewer();
  }


  /**
   * Creates the summary tree tableviewer
   *
   * @param attrComp base composite
   */
  private void createTableViewer() {
    this.attrsTreeViewer =
        new CustomTreeViewer(this.form.getBody(), SWT.FULL_SELECTION | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
    this.formPage.initializeEditorStatusLineManager(this.attrsTreeViewer);

    this.attrsTreeViewer.getTree().setHeaderVisible(true);
    this.attrsTreeViewer.getTree().setLayoutData(GridDataUtil.getInstance().getGridData());

    if (null == this.selAttrValue) {
      this.attrsTreeViewer.setAutoExpandLevel(2);
    }

    this.attrsTreeViewer
        .setContentProvider(new PIDCGrpdAttrChangesContentProvider(this.grpAttrPredefAttrModelList, this.grpAttrValMap,
            this.predefAttrValList, this.pidcDataHandler.getVariantMap(), this.pidcDataHandler.getSubVariantMap()));
    // ICDM-2625
    this.attrsTreeViewer.setLabelProvider(
        new PIDCGrpdAttrChangesLabelProvider(this.grpdAttr, this.pidcVersion, this.grpAttrPredefAttrModelList,
            this.grpAttrValMap, this.attrValPidcValMap, this.attrValPidcLvlMap, this.pidcDataHandler));
    // Create GridTableViewer columns
    createColumns(this.attrsTreeViewer);
    // enable tooltip
    ColumnViewerToolTipSupport.enableFor(this.attrsTreeViewer, ToolTip.NO_RECREATE);
    // set comparator
    // set input and refresh

    ArrayList<Object> details = new ArrayList<>();
    // ICDM-2625
    if (null != this.selAttrValue) {
      details.add(this.selAttrValue);
      PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated.add(this.selAttrValue);
    }
    else if (null != this.pidcVersion) {
      details.addAll(this.grpAttrPredefAttrModelList);

    }

    this.attrsTreeViewer.setInput(details);
    addMouseListener();

  }

  /**
   * adds mouse listener to tah attributes table
   */
  private void addMouseListener() {
    this.attrsTreeViewer.getTree().addMouseListener(new MouseListener() {

      @Override
      public void mouseUp(final MouseEvent mouseevent) {
        // NA
      }

      @Override
      public void mouseDown(final MouseEvent event) {

        // ICDM-2625
        if (PIDCGrpdAttrChangesDialog.this.isAcceptChangesPossible) {
          final Point point = new Point(event.x, event.y);
          // Determine which row was selected
          mouseLeftClickAction(event, PIDCGrpdAttrChangesDialog.this.attrsTreeViewer.getTree().getItem(point));
        }
      }

      @Override
      public void mouseDoubleClick(final MouseEvent mouseevent) {
        // TO-DO
      }
    });
  }

  /**
   * @param event
   * @param selectedRowItem
   */
  private void mouseLeftClickAction(final MouseEvent event, final TreeItem selectedRowItem) {
    if ((selectedRowItem != null) && !selectedRowItem.isDisposed() && (event.button == LEFT_MOUSE_CLICK)) {
      final Object data = selectedRowItem.getData();
      if (data instanceof AttributeValue) {
        leftClickActnForAttrValData(event, selectedRowItem, data);
      }
      else if (data instanceof IProjectAttribute) {
        leftClickActnForPrjAttrData(event, selectedRowItem, data);
      }
    }
  }

  /**
   * @param event
   * @param selectedRowItem
   * @param data
   */
  private void leftClickActnForPrjAttrData(final MouseEvent event, final TreeItem selectedRowItem, final Object data) {
    if (PIDCGrpdAttrChangesDialog.this.grpAttrValMap.keySet().contains(data)) {

      final IProjectAttribute attr = (IProjectAttribute) data;

      final int columnIndex =
          GridTableViewerUtil.getInstance().getTabColIndex(event, PIDCGrpdAttrChangesDialog.this.attrsTreeViewer);
      if (columnIndex == CommonUIConstants.COLUMN_INDEX_4) {
        if (selectedRowItem.getImage(columnIndex)
            .equals(ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16))) {
          selectedRowItem.setImage(columnIndex,
              ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16));
          PIDCGrpdAttrChangesDialog.this.ipidcAttrToBeUpdated.add(attr);
          PIDCGrpdAttrChangesDialog.this.saveBtn.setEnabled(
              CommonUtils.isNotEmpty(PIDCGrpdAttrChangesDialog.this.ipidcAttrToBeUpdated) && (checkWriteAccess()));
        }
        else if (selectedRowItem.getImage(columnIndex)
            .equals(ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16))) {
          selectedRowItem.setImage(columnIndex,
              ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
          PIDCGrpdAttrChangesDialog.this.ipidcAttrToBeUpdated.remove(attr);
        }
      }
    }
  }

  /**
   * @param event
   * @param selectedRowItem
   * @param data
   */
  private void leftClickActnForAttrValData(final MouseEvent event, final TreeItem selectedRowItem, final Object data) {
    AttributeValue attrVal = (AttributeValue) data;
    final int columnIndex =
        GridTableViewerUtil.getInstance().getTabColIndex(event, PIDCGrpdAttrChangesDialog.this.attrsTreeViewer);
    if (columnIndex == CommonUIConstants.COLUMN_INDEX_4) {
      if (selectedRowItem.getImage(columnIndex)
          .equals(ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16))) {
        selectedRowItem.setImage(columnIndex,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16));
        PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated.add(attrVal);
        PIDCGrpdAttrChangesDialog.this.saveBtn.setEnabled(
            CommonUtils.isNotEmpty(PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated) && (checkWriteAccess()));
      }
      else if (selectedRowItem.getImage(columnIndex)
          .equals(ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_YES_16X16))) {
        selectedRowItem.setImage(columnIndex,
            ImageManager.getInstance().getRegisteredImage(ImageKeys.CHECKBOX_NO_16X16));
        PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated.remove(attrVal);
      }
    }
  }

  private void createColumns(final CustomTreeViewer tableTreeView) {
    // create pidc col
    createGrpdAttributeViewer(tableTreeView);
    // Create variant gridviewer column
    createValInPIDCColViewer(tableTreeView);

    // Creates date gridviewer column
    createNewValueColViewer(tableTreeView);
    // create review result column
    createLevelInPIDCColViewer(tableTreeView);

    // ICDM-2625
    if (this.isAcceptChangesPossible) {

      createValSelColumn(tableTreeView);
    }
  }

  /**
   * @param tableTreeView
   */
  private void createValSelColumn(final CustomTreeViewer tableTreeView) {


    final TreeColumn rvwResCol = new TreeColumn(tableTreeView.getTree(), SWT.LEFT);
    tableTreeView.getTree().setLinesVisible(true);
    rvwResCol.setAlignment(SWT.CENTER);
    rvwResCol.setText("Update");

    rvwResCol.setWidth(75);
    // create review result

  }

  /**
   * @param tableTreeView
   */
  private void createValInPIDCColViewer(final CustomTreeViewer tableTreeView) {

    final TreeColumn varNameCol = new TreeColumn(tableTreeView.getTree(), SWT.LEFT);
    tableTreeView.getTree().setLinesVisible(true);
    varNameCol.setAlignment(SWT.LEFT);
    varNameCol.setText("Value in PIDC");
    varNameCol.setWidth(150);


  }

  /**
   * @param pidcTableTreeView
   */
  private void createNewValueColViewer(final CustomTreeViewer pidcTableTreeView) {
    // create date of review
    final TreeColumn dateRvwCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    dateRvwCol.setAlignment(SWT.LEFT);
    dateRvwCol.setText("New Value");
    dateRvwCol.setWidth(150);

  }

  /**
   * @param pidcTableTreeView
   */
  private void createLevelInPIDCColViewer(final CustomTreeViewer pidcTableTreeView) {


    final TreeColumn rvwResCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    rvwResCol.setAlignment(SWT.LEFT);
    rvwResCol.setText("Level in PIDC");
    rvwResCol.setWidth(250);

    // create review result

  }

  /**
   * @param pidcTableTreeView
   */
  private void createGrpdAttributeViewer(final CustomTreeViewer pidcTableTreeView) {

    final TreeColumn pidcNameCol = new TreeColumn(pidcTableTreeView.getTree(), SWT.LEFT);
    pidcTableTreeView.getTree().setLinesVisible(true);
    pidcNameCol.setAlignment(SWT.LEFT);
    pidcNameCol.setText("Grouped Attribute");
    pidcNameCol.setWidth(200);

  }


  /**
   * This method initializes formToolkit
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
   * after clicking ok in dialog
   */
  @Override
  protected void okPressed() {
    Map<Long, Set<PredefinedAttrValue>> grpdAttrNInvisiblePredefAttrMap = new HashMap<>();
    if (null != this.selAttrValue) {
      PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated.add(this.selAttrValue);
    }

    if ((null != PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated) &&
        !PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated.isEmpty()) {
      for (AttributeValue attrVal : PIDCGrpdAttrChangesDialog.this.attrValuesToBeUpdated) {
        Set<PredefinedAttrValue> preDfndSet = this.preDefinedAttrValueSet;
        getGroupedAttrForMultipleUpdate(attrVal);
        // to include attribute ids of attribute which will be enabled by setting grouped attribute value
        Set<Long> depAttrIdSet =
            getDepAttrForGrpdAttr(this.pidcDataHandler.getAttrRefDependenciesMap().get(this.grpdAttr.getAttrId()));
        ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
        updationModel.setPidcVersion(this.pidcVersion);
        ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(this.pidcVersionBO);
        moveModel.setPidcVersion(this.pidcVersion);
        // list of pre-defined attribute in project (parent level) to be moved to variant level
        List<IProjectAttribute> attrToBeMovedToVariant = new ArrayList<>();
        // key - predefined attribute in project or in variant level to be moved to sub variant level
        // value - grouped attribute in subvariant level
        Map<IProjectAttribute, PidcSubVariant> attrSubVarMap = new ConcurrentHashMap<>();

        try {
          if (null != preDfndSet) {

            for (PredefinedAttrValue predefinedAttrValue : preDfndSet) {

              PidcVersionAttribute preDefndAttr = this.allPIDCAttrMap.get(predefinedAttrValue.getPredefinedAttrId());
              // check whether the grouped attr and the predefined attrs are at the Variant level. If not they need to
              // // be moved to Variant level
              PIDCGroupedAttrActionSet actionSet =
                  new PIDCGroupedAttrActionSet(this.pidcDataHandler, this.pidcVersionBO);
              PidcVersionAttributeBO pidcVersionHandler =
                  new PidcVersionAttributeBO(preDefndAttr, new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
              if (pidcVersionHandler.isNotInvisibleAttr(predefinedAttrValue.getPredefinedAttrId(), this.grpdAttr)) {
                if (actionSet.checkIfGrpdAttrsAtVarLevel(this.grpdAttr, preDefndAttr)) {
                  if ((this.grpdAttr instanceof PidcVariantAttribute) && !preDefndAttr.isAtChildLevel()) {
                    attrToBeMovedToVariant.add(preDefndAttr);
                  }
                }
                // check whether the grouped attr and the predefined attrs are at the SubVariant level. If not they need
                // // to be moved to
                // SubVariant level
                if (actionSet.checkIfGrpdAttrsAtSubVarLevel(this.grpdAttr, preDefndAttr)) {

                  if (this.grpdAttr instanceof PidcSubVariantAttribute) {
                    // check if at subvar level
                    PidcSubVariant subVar = this.pidcDataHandler.getSubVariantMap()
                        .get(((PidcSubVariantAttribute) this.grpdAttr).getSubVariantId());
                    PidcSubVariantBO handler = new PidcSubVariantBO(this.pidcVersion, subVar, this.pidcDataHandler);
                    PidcSubVariantAttribute subVarAttr = handler.getAttributesAll().get(preDefndAttr.getAttrId());

                    if (null == subVarAttr) {
                      PidcVariant var = this.pidcDataHandler.getVariantMap().get(subVar.getPidcVariantId());
                      PidcVariantBO varHandlr = new PidcVariantBO(this.pidcVersion, var, this.pidcDataHandler);
                      PidcVariantAttribute varAttribute = varHandlr.getAllVarAttribute(preDefndAttr.getAttrId());
                      if (null == varAttribute) {
                        PidcVersionAttribute pidcAttribute = this.allPIDCAttrMap.get(preDefndAttr.getAttrId());
                        attrSubVarMap.put(pidcAttribute, subVar);
                      }
                      else {
                        attrSubVarMap.put(varAttribute, subVar);
                      }
                    }
                  }
                }
              }
              // is invisible attribute and will be enabled by grouped attribute which is also a dependent attribute
              else if (!pidcVersionHandler.isNotInvisibleAttr(predefinedAttrValue.getPredefinedAttrId(),
                  this.grpdAttr) && depAttrIdSet.contains(predefinedAttrValue.getPredefinedAttrId())) {
                fillGrpdAttrNPredefValMap(grpdAttrNInvisiblePredefAttrMap, predefinedAttrValue, this.grpdAttr);
              }
            }
          }
          if (CommonUtils.isNotEmpty(attrToBeMovedToVariant)) {
            moveToVariant(moveModel, attrToBeMovedToVariant);
          }
          if (CommonUtils.isNotEmpty(attrSubVarMap)) {
            moveToSubVariant(moveModel, attrSubVarMap);
          }
          if (CommonUtils.isNotEmpty(attrToBeMovedToVariant) || CommonUtils.isNotEmpty(attrSubVarMap)) {
            updationModel = fillMoveModel(moveModel);
            if (CommonUtils.isNotEmpty(attrToBeMovedToVariant)) {
              setValueForAttributesToBeMoved(updationModel, preDfndSet, true);
            }
            if (CommonUtils.isNotEmpty(attrSubVarMap)) {
              setValueForAttributesToBeMoved(updationModel, preDfndSet, false);
            }
          }

          // edit the values of predefined attrs and the grped attr (for the case when both of them in same level)
          addValueEditCommands(updationModel, preDfndSet, this.grpdAttr);

          Set<IProjectAttribute> grpAttrSet = new HashSet<>();
          grpAttrSet.add(this.grpdAttr);

          // Reset predefined attributes of previously set group attr value.
          resetExistingPredefAttr(updationModel, grpAttrSet);

          ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();

          upClient.updatePidcAttrs(updationModel);
          this.saveSuccess = true;
        }
        catch (ApicWebServiceException e) {
          CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
          this.saveSuccess = false;
        }
      }
    }
    // set value action - where multiple attributes at variant/subvariant level are updated
    else if ((null != this.ipidcAttrToBeUpdated) && !this.ipidcAttrToBeUpdated.isEmpty()) {

      // loop through the list of project attributes to be updated
      for (IProjectAttribute ipidcGrpAttr : this.ipidcAttrToBeUpdated) {

        // loop through grp attr predef model - contains grp attribute n its predef attr value map
        for (GroupdAttrPredefAttrModel model : this.grpAttrPredefAttrModelList) {

          if (ipidcGrpAttr.equals(model.getGroupedAttribute())) {
            // to include attribute ids of attribute which will be enabled by setting grouped attribute value
            Set<Long> depAttrIdSet =
                getDepAttrForGrpdAttr(this.pidcDataHandler.getAttrRefDependenciesMap().get(ipidcGrpAttr.getAttrId()));

            ProjectAttributesUpdationModel updationModel = new ProjectAttributesUpdationModel();
            updationModel.setPidcVersion(this.pidcVersion);
            ProjectAttributesMovementModel moveModel = new ProjectAttributesMovementModel(this.pidcVersionBO);
            moveModel.setPidcVersion(this.pidcVersion);

            List<IProjectAttribute> attrToBeMovedToVariant = new ArrayList<>();
            Map<IProjectAttribute, PidcSubVariant> attrSubVarMap = new ConcurrentHashMap<>();

            if (CommonUtils.isNotEmpty(model.getPredefAttrValMap())) {

              for (IProjectAttribute modelElemnt : model.getPredefAttrValMap().keySet()) {
                PidcVersionAttributeBO pidcVersionHandler =
                    new PidcVersionAttributeBO(this.allPIDCAttrMap.get(modelElemnt.getAttrId()),
                        new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
                if (ipidcGrpAttr instanceof PidcVariantAttribute) {

                  if ((modelElemnt instanceof PidcVersionAttribute) &&
                      (!this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(modelElemnt.getAttrId())) &&
                      (null == this.pidcDataHandler.getVariantAttributeMap()
                          .get(((PidcVariantAttribute) ipidcGrpAttr).getVariantId()).get(modelElemnt.getAttrId()))) {
                    attrToBeMovedToVariant.add(modelElemnt);
                  }
                  // is an invisible attribute and will be enabled by grouped attribute
                  else if (((modelElemnt instanceof PidcVersionAttribute) &&
                      this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(modelElemnt.getAttrId())) &&
                      depAttrIdSet.contains(modelElemnt.getAttrId())) {
                    fillGrpdAttrNPredefValMap(grpdAttrNInvisiblePredefAttrMap,
                        model.getPredefAttrValMap().get(modelElemnt), model.getGroupedAttribute());
                  }
                }
                else if (ipidcGrpAttr instanceof PidcSubVariantAttribute) {

                  if (((modelElemnt instanceof PidcVersionAttribute) &&
                      !this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(modelElemnt.getAttrId())) ||
                      ((modelElemnt instanceof PidcVariantAttribute) && !this.pidcDataHandler
                          .getVariantInvisbleAttributeMap().get(((PidcVariantAttribute) modelElemnt).getVariantId())
                          .contains(modelElemnt.getAttrId()))) {
                    if (null == this.pidcDataHandler.getSubVariantAttributeMap()
                        .get(((PidcSubVariantAttribute) ipidcGrpAttr).getSubVariantId()).get(modelElemnt.getAttrId())) {
                      attrSubVarMap.put(modelElemnt, this.pidcDataHandler.getSubVariantMap()
                          .get(((PidcSubVariantAttribute) ipidcGrpAttr).getSubVariantId()));
                    }
                  }
                  else if (!pidcVersionHandler.isNotInvisibleAttr(modelElemnt.getAttrId(), ipidcGrpAttr) &&
                      depAttrIdSet.contains(modelElemnt.getAttrId())) {
                    fillGrpdAttrNPredefValMap(grpdAttrNInvisiblePredefAttrMap,
                        model.getPredefAttrValMap().get(modelElemnt), model.getGroupedAttribute());
                  }
                }
              }
            }

            if (CommonUtils.isNotEmpty(attrToBeMovedToVariant)) {
              moveToVariant(moveModel, attrToBeMovedToVariant);
            }
            if (CommonUtils.isNotEmpty(attrSubVarMap)) {
              moveToSubVariant(moveModel, attrSubVarMap);
            }
            if (CommonUtils.isNotEmpty(attrToBeMovedToVariant) || CommonUtils.isNotEmpty(attrSubVarMap)) {
              // execute command to move the attributes to same level as grouped attr
              updationModel = fillMoveModel(moveModel);
              if (CommonUtils.isNotEmpty(attrToBeMovedToVariant)) {
                setValueForAttributesToBeMoved(updationModel, model.getPredefAttrValMap(), ipidcGrpAttr, true);
              }
              if (CommonUtils.isNotEmpty(attrSubVarMap)) {
                setValueForAttributesToBeMoved(updationModel, model.getPredefAttrValMap(), ipidcGrpAttr, false);
              }
            }
            addValueEditCommandsLvl(updationModel, model.getPredefAttrValMap(), model.getGroupedAttribute());

            try {
              // Reset predefined attributes of previously set group attr value
              resetExistingPredefAttr(updationModel, this.ipidcAttrToBeUpdated);
              // call service if there are predefined attributes to be created or updated
              if (!isEmpty(updationModel)) {
                ProjectAttributesUpdationServiceClient upClient = new ProjectAttributesUpdationServiceClient();
                upClient.updatePidcAttrs(updationModel);
              }
              this.saveSuccess = true;
            }
            catch (ApicWebServiceException e) {
              CDMLogger.getInstance().error(e.getLocalizedMessage(), e, Activator.PLUGIN_ID);
              this.saveSuccess = false;
            }
          }
        }
      }
    }
    if (CommonUtils.isNotEmpty(grpdAttrNInvisiblePredefAttrMap)) {
      String msgStr = getWarnDialogString(grpdAttrNInvisiblePredefAttrMap);
      CDMLogger.getInstance()
          .warnDialog("The following predefined attributes were enabled by setting\n\n" + msgStr +
              "\n Attribute values can be viewed and set by re-opening the pidc editor or using the toolbar action.",
              Activator.PLUGIN_ID);
    }
    super.okPressed();
  }

  /**
   * @param depAttrValSet set of attribute n value dependency for a grouped attribute
   * @return
   */
  private Set<Long> getDepAttrForGrpdAttr(final Set<AttrNValueDependency> depAttrValSet) {
    Set<Long> depAttrValId = new HashSet<>();
    if (!CommonUtils.isNullOrEmpty(depAttrValSet)) {
      for (AttrNValueDependency attrValDep : depAttrValSet) {
        depAttrValId.add(attrValDep.getAttributeId());
      }
    }
    return depAttrValId;
  }

  /**
   * @param grpdAttrNInvisiblePredefAttrMap
   * @param predefinedAttrValue
   * @param grpdAttrbute
   */
  private void fillGrpdAttrNPredefValMap(final Map<Long, Set<PredefinedAttrValue>> grpdAttrNInvisiblePredefAttrMap,
      final PredefinedAttrValue predefinedAttrValue, final IProjectAttribute grpdAttrbute) {
    if (grpdAttrNInvisiblePredefAttrMap
        .get(null != this.selAttrValue ? this.selAttrValue.getId() : grpdAttrbute.getValueId()) == null) {
      Set<PredefinedAttrValue> predDefAttrValSet = new HashSet<>();
      predDefAttrValSet.add(predefinedAttrValue);

      grpdAttrNInvisiblePredefAttrMap
          .put(null != this.selAttrValue ? this.selAttrValue.getId() : grpdAttrbute.getValueId(), predDefAttrValSet);

    }
    else {
      grpdAttrNInvisiblePredefAttrMap
          .get(null != this.selAttrValue ? this.selAttrValue.getId() : grpdAttrbute.getValueId())
          .add(predefinedAttrValue);
    }
  }


  /**
   * @param grpdAttrNInvisiblePredefAttrMap key->map of grouped attribute value id ,value->prdefined attribute value
   *          enabled by grouped attribute. 1.) This warning dialog will appear only when we are editing grouped
   *          attribute which is also dependent attribute from Pidc attributes Page.2.)As when we edit from pidc
   *          attributes page first predefined attribute is updated then grouped attribute is updated.3.Thus,attributes
   *          enabled by grouped attribute will still be invisible as grouped attribute value is not updated
   */
  private String getWarnDialogString(final Map<Long, Set<PredefinedAttrValue>> grpdAttrNInvisiblePredefAttrMap) {
    StringBuilder strMsg = new StringBuilder();
    for (Entry<Long, Set<PredefinedAttrValue>> entryInMap : grpdAttrNInvisiblePredefAttrMap.entrySet()) {
      Long attrValId = entryInMap.getKey();
      AttributeValue attrVal = this.pidcDataHandler.getAttributeValue(attrValId);
      // pidcDataHandler will contain attribute value relevant for pidc,so in case existing value is modified,attribute
      // value will not be available in pidc data handler
      if ((attrVal == null) && (this.selAttrValue != null)) {
        attrVal = this.selAttrValue;
      }
      if (attrVal != null) {
        Attribute attr = this.pidcDataHandler.getAttribute(attrVal.getAttributeId());
        strMsg.append("Grouped Attribute->").append(attr.getName()).append("=").append(attrVal.getName())
            .append("\n\n");
      }
      for (PredefinedAttrValue predefAttrVal : entryInMap.getValue()) {
        strMsg.append(predefAttrVal.getPredefinedAttrName()).append("->").append(predefAttrVal.getPredefinedValue())
            .append("\n\n");
      }
    }
    return strMsg.toString();
  }

  private Long getPreviousGrpAttrValueId(final IProjectAttribute grpAttr) {
    Long grpAttrValueId = null;

    if (grpAttr instanceof PidcVersionAttribute) {
      grpAttrValueId = this.pidcDataHandler.getPidcVersAttrMap().get(grpAttr.getAttrId()).getValueId();
    }
    else if (grpAttr instanceof PidcVariantAttribute) {
      Long varId = ((PidcVariantAttribute) grpAttr).getVariantId();
      // if not empty, set value action has been performed
      if ((this.existingGrpAttrValMap != null) && !this.existingGrpAttrValMap.isEmpty()) {
        grpAttrValueId = this.existingGrpAttrValMap.get(varId);
      }
      else {
        grpAttrValueId = this.variantAttributeMap.get(((PidcVariantAttribute) grpAttr).getVariantId())
            .get(grpAttr.getAttrId()).getValueId();
      }
    }
    else if (grpAttr instanceof PidcSubVariantAttribute) {
      Long subVarId = ((PidcSubVariantAttribute) grpAttr).getSubVariantId();
      if ((this.existingGrpAttrValMap != null) && !this.existingGrpAttrValMap.isEmpty()) {
        grpAttrValueId = this.existingGrpAttrValMap.get(subVarId);
      }
      else {
        grpAttrValueId = this.subVariantAttributeMap.get(((PidcSubVariantAttribute) grpAttr).getSubVariantId())
            .get(grpAttr.getAttrId()).getValueId();
      }
    }
    return grpAttrValueId;
  }

  private void updatePidcAtrr(final ProjectAttributesUpdationModel updateModel,
      final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {
    PidcVersionAttribute versAttr =
        this.pidcDataHandler.getPidcVersAttrMap().get(predefAttrValEntry.getValue().getPredefinedAttrId());
    versAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
    versAttr.setValue(null);
    versAttr.setValueId(null);
    updateModel.getPidcAttrsToBeUpdated().put(versAttr.getAttrId(), versAttr);
  }

  private void updateSubVarLevelPredefAttr(final IProjectAttribute grpAttr,
      final ProjectAttributesUpdationModel updateModel, final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {

    boolean notExistsInUpdateMap = true;
    boolean notExistsInCreateMap = true;
    for (Entry<Long, Map<Long, PidcSubVariantAttribute>> entry : updateModel.getPidcSubVarAttrsToBeUpdated()
        .entrySet()) {

      Map<Long, PidcSubVariantAttribute> subVarAttrMap = entry.getValue();

      // check in update map
      if (CommonUtils.isNotEmpty(subVarAttrMap) &&
          entry.getKey().equals(((PidcSubVariantAttribute) grpAttr).getSubVariantId()) &&
          subVarAttrMap.containsKey(predefAttrValEntry.getValue().getPredefinedAttrId())) {
        notExistsInUpdateMap = false;
        break;
      }
    }

    // check in create map
    for (Entry<Long, Map<Long, PidcSubVariantAttribute>> entry : updateModel.getPidcSubVarAttrsToBeCreated()
        .entrySet()) {
      Map<Long, PidcSubVariantAttribute> subVarAttrMap = entry.getValue();
      if (CommonUtils.isNotEmpty(subVarAttrMap) &&
          entry.getKey().equals(((PidcSubVariantAttribute) grpAttr).getSubVariantId()) &&
          subVarAttrMap.containsKey(predefAttrValEntry.getValue().getPredefinedAttrId())) {
        notExistsInCreateMap = false;
        break;
      }
    }
    // if not present in update model, reset to not defined
    if (notExistsInUpdateMap && notExistsInCreateMap) {
      setValueForSubVarLevelPredefAttr(grpAttr, updateModel, predefAttrValEntry);
    }

  }


  private void setValueForSubVarLevelPredefAttr(final IProjectAttribute grpAttr,
      final ProjectAttributesUpdationModel updateModel, final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {

    PidcSubVariantAttribute subvarAttr =
        this.pidcDataHandler.getSubVariantAttributeMap().get(((PidcSubVariantAttribute) grpAttr).getSubVariantId())
            .get(predefAttrValEntry.getValue().getPredefinedAttrId());

    if (subvarAttr != null) {
      subvarAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
      subvarAttr.setValue(null);
      subvarAttr.setValueId(null);

      Map<Long, PidcSubVariantAttribute> subvarAttrMap;
      if (updateModel.getPidcSubVarAttrsToBeUpdated().get(subvarAttr.getSubVariantId()) == null) {
        subvarAttrMap = new HashMap<>();
        updateModel.getPidcSubVarAttrsToBeUpdated().put(subvarAttr.getSubVariantId(), subvarAttrMap);
      }
      else {
        subvarAttrMap = updateModel.getPidcSubVarAttrsToBeUpdated().get(subvarAttr.getSubVariantId());
      }
      subvarAttrMap.put(subvarAttr.getAttrId(), subvarAttr);
    }
  }

  private void setValueForVarLevelPredefAttr(final IProjectAttribute grpAttr,
      final ProjectAttributesUpdationModel updateModel, final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {

    PidcVariantAttribute varAttr = this.pidcDataHandler.getVariantAttributeMap()
        .get(((PidcVariantAttribute) grpAttr).getVariantId()).get(predefAttrValEntry.getValue().getPredefinedAttrId());
    if (varAttr != null) {
      varAttr.setUsedFlag(ApicConstants.NOT_DEFINED);
      varAttr.setValue(null);
      varAttr.setValueId(null);

      Map<Long, PidcVariantAttribute> varAttrMap;
      if (updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId()) == null) {
        varAttrMap = new HashMap<>();
        updateModel.getPidcVarAttrsToBeUpdated().put(varAttr.getVariantId(), varAttrMap);
      }
      else {
        varAttrMap = updateModel.getPidcVarAttrsToBeUpdated().get(varAttr.getVariantId());
      }
      varAttrMap.put(varAttr.getAttrId(), varAttr);
    }

  }

  private void updateVarLevelPredefAttr(final IProjectAttribute grpAttr,
      final ProjectAttributesUpdationModel updateModel, final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {

    boolean notExistsInUpdateMap = true;
    boolean notExistsInCreateMap = true;
    // check in update map
    for (Entry<Long, Map<Long, PidcVariantAttribute>> entry : updateModel.getPidcVarAttrsToBeUpdated().entrySet()) {
      Map<Long, PidcVariantAttribute> varAttrMap = entry.getValue();
      if (CommonUtils.isNotEmpty(varAttrMap) &&
          entry.getKey().equals(((PidcVariantAttribute) grpAttr).getVariantId()) &&
          varAttrMap.containsKey(predefAttrValEntry.getValue().getPredefinedAttrId())) {
        notExistsInUpdateMap = false;
        break;
      }
    }
    // check in create map
    for (Entry<Long, Map<Long, PidcVariantAttribute>> entry : updateModel.getPidcVarAttrsToBeCreated().entrySet()) {
      Map<Long, PidcVariantAttribute> varAttrMap = entry.getValue();
      if (CommonUtils.isNotEmpty(varAttrMap) &&
          entry.getKey().equals(((PidcVariantAttribute) grpAttr).getVariantId()) &&
          varAttrMap.containsKey(predefAttrValEntry.getValue().getPredefinedAttrId())) {
        notExistsInCreateMap = false;
        break;
      }
    }

    // if not present in update model, reset to not defined
    if (notExistsInCreateMap && notExistsInUpdateMap) {
      setValueForVarLevelPredefAttr(grpAttr, updateModel, predefAttrValEntry);
    }

  }

  /**
   * Reset predefined attributes of previously set group attr value. Steps: 1. Fetch previous group attr value 2. Get
   * predef attrbutes of previous group attr value 3. If previous predef attributes are not part of current update
   * model, reset to NOT-DEFINED
   *
   * @param updateModel - update model with values filled from Grp attr changes dialog
   * @param grpAttrList - list of grp attr list to be updated
   */
  private void resetExistingPredefAttr(final ProjectAttributesUpdationModel updateModel,
      final Set<IProjectAttribute> grpAttrList) {

    // loop through all grp atrs - multiple grp attrs will be available when set value action is performed
    for (IProjectAttribute grpAttr : grpAttrList) {

      Long prevGrpAttrValueId = getPreviousGrpAttrValueId(grpAttr);
      // to identify relevant predefined attribute for current grouped attribute when set value action has been
      // performed
      Set<Long> currPreDefAttrIdSet = null;
      // if not empty, set value action has been performed
      if ((this.existingGrpAttrValMap != null) && !this.existingGrpAttrValMap.isEmpty()) {
        currPreDefAttrIdSet = new HashSet<>();
        for (PredefinedAttrValue predefAttrVal : this.pidcDataHandler.getPreDefAttrValMap().get(grpAttr.getValueId())
            .values()) {
          currPreDefAttrIdSet.add(predefAttrVal.getPredefinedAttrId());
        }
      }
      // if null, predefined attributes values need not be reset
      if (prevGrpAttrValueId != null) {
        // to identify relevant predefined attribute for grouped attributein scenarios other than set value action
        Set<Long> commonPreDefAttr = getCommonPreDefAttrForGrpAttr(grpAttr);
        if (null != this.pidcDataHandler.getPreDefAttrValMap().get(prevGrpAttrValueId)) {
          for (Entry<Long, PredefinedAttrValue> predefAttrValEntry : this.pidcDataHandler.getPreDefAttrValMap()
              .get(prevGrpAttrValueId).entrySet()) {
            PidcVersionAttributeBO pidcVersionHandler =
                new PidcVersionAttributeBO(this.allPIDCAttrMap.get(predefAttrValEntry.getValue().getPredefinedAttrId()),
                    new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
            if (pidcVersionHandler.isNotInvisibleAttr(predefAttrValEntry.getValue().getPredefinedAttrId(), grpAttr)) {
              // check if update model contains previously set predefined attr, if so, ignore
              // else reset to not defined
              if ((grpAttr instanceof PidcVersionAttribute) &&
                  !updateModel.getPidcAttrsToBeCreated()
                      .containsKey(predefAttrValEntry.getValue().getPredefinedAttrId()) &&
                  !updateModel.getPidcAttrsToBeUpdated()
                      .containsKey(predefAttrValEntry.getValue().getPredefinedAttrId()) &&
                  isIrrelevantPreDefAttr(currPreDefAttrIdSet, commonPreDefAttr, predefAttrValEntry)) {
                updatePidcAtrr(updateModel, predefAttrValEntry);
              }
              else if ((grpAttr instanceof PidcVariantAttribute) &&
                  isIrrelevantPreDefAttr(currPreDefAttrIdSet, commonPreDefAttr, predefAttrValEntry)) {
                updateVarLevelPredefAttr(grpAttr, updateModel, predefAttrValEntry);
              }
              else if ((grpAttr instanceof PidcSubVariantAttribute) &&
                  isIrrelevantPreDefAttr(currPreDefAttrIdSet, commonPreDefAttr, predefAttrValEntry)) {
                updateSubVarLevelPredefAttr(grpAttr, updateModel, predefAttrValEntry);
              }
            }
          }
        }
      }
    }
  }

  /**
   * @param currPreDefAttrIdSet
   * @param commonPreDefAttr
   * @param predefAttrValEntry
   * @return
   */
  private boolean isIrrelevantPreDefAttr(final Set<Long> currPreDefAttrIdSet, final Set<Long> commonPreDefAttr,
      final Entry<Long, PredefinedAttrValue> predefAttrValEntry) {
    return ((currPreDefAttrIdSet != null) &&
        !currPreDefAttrIdSet.contains(predefAttrValEntry.getValue().getPredefinedAttrId())) ||
        ((null != commonPreDefAttr) && !commonPreDefAttr.contains(predefAttrValEntry.getValue().getPredefinedAttrId()));
  }


  /**
   * @param grpAttr
   * @return
   */
  private Set<Long> getCommonPreDefAttrForGrpAttr(final IProjectAttribute grpAttr) {
    if (grpAttr instanceof PidcVersionAttribute) {
      return this.commonPredefAttrMap.get(((PidcVersionAttribute) grpAttr).getPidcVersId());
    }
    if (grpAttr instanceof PidcVariantAttribute) {
      return this.commonPredefAttrMap.get(((PidcVariantAttribute) grpAttr).getVariantId());
    }
    if (grpAttr instanceof PidcSubVariantAttribute) {
      return this.commonPredefAttrMap.get(((PidcSubVariantAttribute) grpAttr).getSubVariantId());
    }
    return new HashSet<>();
  }

  /**
   * @param model
   * @return
   */
  private boolean isEmpty(final ProjectAttributesUpdationModel model) {
    return !(isPidcAttrsMapNotEmpty(model) || isPidcSubvarAttrsMapNotEmpty(model) || isPidcVarAttrsMapNotEmpty(model));
  }

  /**
   * @param model
   * @return
   */
  private boolean isPidcSubvarAttrsMapNotEmpty(final ProjectAttributesUpdationModel model) {
    return !model.getPidcSubVarAttrsToBeCreated().isEmpty() || !model.getPidcSubVarAttrsToBeUpdated().isEmpty();
  }

  /**
   * @param model
   * @return
   */
  private boolean isPidcVarAttrsMapNotEmpty(final ProjectAttributesUpdationModel model) {
    return !model.getPidcVarAttrsToBeCreated().isEmpty() || !model.getPidcVarAttrsToBeUpdated().isEmpty();
  }

  /**
   * @param model
   * @return
   */
  private boolean isPidcAttrsMapNotEmpty(final ProjectAttributesUpdationModel model) {
    return !model.getPidcAttrsToBeCreated().isEmpty() || !model.getPidcAttrsToBeUpdated().isEmpty();
  }

  /**
   * @param moveModel
   */
  private ProjectAttributesUpdationModel fillMoveModel(final ProjectAttributesMovementModel moveModel) {
    return moveModel.loadUpdationModel();
  }


  /**
   * @param updationModel
   * @param predefAttrValMap
   * @param groupedAttribute
   * @throws CommandException
   */
  private void addValueEditCommandsLvl(final ProjectAttributesUpdationModel updationModel,
      final Map<IProjectAttribute, PredefinedAttrValue> predefAttrValMap, final IProjectAttribute groupedAttribute) {
    // check if grouped attribute is at pidc version level
    if (groupedAttribute instanceof PidcVersionAttribute) {

      // loop through predefined attribute value map
      for (Entry<IProjectAttribute, PredefinedAttrValue> predefAttrValEntry : predefAttrValMap.entrySet()) {

        if (predefAttrValEntry.getKey() instanceof PidcVersionAttribute) {
          PidcVersionAttribute predefAttr = (PidcVersionAttribute) predefAttrValEntry.getKey();
          if (!this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefAttr.getAttrId())) {
            // set updation model with predefined attribute and its value
            cmdPredefAttr(updationModel, predefAttrValEntry, predefAttr);
          }
        }
        else if (predefAttrValEntry.getKey() instanceof PidcVariantAttribute) {
          PidcVariantAttribute predefAttrVar = (PidcVariantAttribute) predefAttrValEntry.getKey();
          if (!this.pidcDataHandler.getVariantInvisbleAttributeMap().get(predefAttrVar.getVariantId())
              .contains(predefAttrVar.getAttrId())) {
            // set updation model with predefined attribute and its value
            cmdPredefAttrVar(updationModel, predefAttrValEntry, predefAttrVar);
          }
        }
        else if (predefAttrValEntry.getKey() instanceof PidcSubVariantAttribute) {
          PidcSubVariantAttribute predefAttrSubVar = (PidcSubVariantAttribute) predefAttrValEntry.getKey();
          if (!this.pidcDataHandler.getSubVariantInvisbleAttributeMap().get(predefAttrSubVar.getSubVariantId())
              .contains(predefAttrSubVar.getAttrId())) {
            // set updation model with predefined attribute and its value
            cmdPredefAttrSubVar(updationModel, predefAttrValEntry, predefAttrSubVar);
          }
        }
      }
    }
    else if (groupedAttribute instanceof PidcVariantAttribute) {

      for (Entry<IProjectAttribute, PredefinedAttrValue> predefAttrValEntry : predefAttrValMap.entrySet()) {
        if (predefAttrValEntry.getKey() instanceof PidcVersionAttribute) {

          PidcVariantAttribute predefAttrVar = this.pidcDataHandler.getVariantAttributeMap()
              .get(((PidcVariantAttribute) groupedAttribute).getVariantId())
              .get(predefAttrValEntry.getKey().getAttrId());

          if (predefAttrVar == null) {
            predefAttrVar = new PidcVariantAttribute();
            predefAttrVar.setVariantId(((PidcVariantAttribute) groupedAttribute).getVariantId());
            predefAttrVar.setAtChildLevel(false);
            // set attr id
            predefAttrVar.setAttrId(predefAttrValEntry.getKey().getAttrId());
          }

          if (!this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefAttrVar.getAttrId()) &&
              !CommonUtils.isEqual(predefAttrVar.getValueId(), predefAttrValEntry.getValue().getPredefinedValueId())) {
            cmdPredefAttrVar(updationModel, predefAttrValEntry, predefAttrVar);
          }
        }
        else if (predefAttrValEntry.getKey() instanceof PidcVariantAttribute) {
          PidcVariantAttribute predefAttrVar = (PidcVariantAttribute) predefAttrValEntry.getKey();
          if (!this.pidcDataHandler.getVariantInvisbleAttributeMap().get(predefAttrVar.getVariantId())
              .contains(predefAttrVar.getAttrId())) {
            cmdPredefAttrVar(updationModel, predefAttrValEntry, predefAttrVar);
          }
        }
        else if (predefAttrValEntry.getKey() instanceof PidcSubVariantAttribute) {
          PidcSubVariantAttribute predefAttrSubVar = (PidcSubVariantAttribute) predefAttrValEntry.getKey();
          if (!this.pidcDataHandler.getSubVariantInvisbleAttributeMap().get(predefAttrSubVar.getSubVariantId())
              .contains(predefAttrSubVar.getAttrId())) {
            cmdPredefAttrSubVar(updationModel, predefAttrValEntry, predefAttrSubVar);
          }
        }
      }
    }
    else if (groupedAttribute instanceof PidcSubVariantAttribute) {
      for (Entry<IProjectAttribute, PredefinedAttrValue> predefAttrValEntry : predefAttrValMap.entrySet()) {
        if ((predefAttrValEntry.getKey() instanceof PidcVersionAttribute) ||
            (predefAttrValEntry.getKey() instanceof PidcVariantAttribute)) {

          PidcSubVariantAttribute predefAttrSubVar = this.pidcDataHandler.getSubVariantAttributeMap()
              .get(((PidcSubVariantAttribute) groupedAttribute).getSubVariantId())
              .get(predefAttrValEntry.getKey().getAttrId());

          if (predefAttrSubVar == null) {
            predefAttrSubVar = new PidcSubVariantAttribute();
            predefAttrSubVar.setSubVariantId(((PidcSubVariantAttribute) groupedAttribute).getSubVariantId());
            predefAttrSubVar.setAtChildLevel(false);
            // set attr id
            predefAttrSubVar.setAttrId(predefAttrValEntry.getKey().getAttrId());
          }
          if ((!CommonUtils.isEqual(predefAttrSubVar.getValueId(),
              predefAttrValEntry.getValue().getPredefinedValueId()) &&
              !this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefAttrSubVar.getAttrId())) ||
              this.pidcDataHandler.getVariantInvisbleAttributeMap().get(predefAttrSubVar.getVariantId())
                  .contains(predefAttrSubVar.getAttrId())) {

            cmdPredefAttrSubVar(updationModel, predefAttrValEntry, predefAttrSubVar);
          }
        }
        else if (predefAttrValEntry.getKey() instanceof PidcSubVariantAttribute) {

          PidcSubVariantAttribute predefAttrSubVar = (PidcSubVariantAttribute) predefAttrValEntry.getKey();
          if (!this.pidcDataHandler.getSubVariantInvisbleAttributeMap().get(predefAttrSubVar.getSubVariantId())
              .contains(predefAttrSubVar.getAttrId())) {
            cmdPredefAttrSubVar(updationModel, predefAttrValEntry, predefAttrSubVar);
          }
        }
      }
    }
  }


  /**
   * Method to set values for those attributes which needs to be moved
   *
   * @param updationModel - updation model with movement information
   * @param predefAttrValMap - map with project attribute and predefined value to be set
   * @param grpAttr
   * @param toBeMovedToVariant - if true, attrs need to be moved to variant, if false, to subvariant
   */
  private void setValueForAttributesToBeMoved(final ProjectAttributesUpdationModel updationModel,
      final Map<IProjectAttribute, PredefinedAttrValue> predefAttrValMap, final IProjectAttribute grpAttr,
      final boolean toBeMovedToVariant) {

    if (toBeMovedToVariant) {
      setValForVarAttribute(updationModel, predefAttrValMap, grpAttr);
    }
    else {
      setValForSubvarAttr(updationModel, predefAttrValMap, grpAttr);
    }
  }

  /**
   * @param updationModel
   * @param predefAttrValMap
   * @param grpAttr
   */
  private void setValForSubvarAttr(final ProjectAttributesUpdationModel updationModel,
      final Map<IProjectAttribute, PredefinedAttrValue> predefAttrValMap, final IProjectAttribute grpAttr) {
    // loop through each predefined value entry
    for (Entry<IProjectAttribute, PredefinedAttrValue> predefValEntry : predefAttrValMap.entrySet()) {
      if (!this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefValEntry.getKey().getAttrId())) {
        // for each predefined value entry , match the sub variant attribute n set value for the same
        for (Map<Long, PidcSubVariantAttribute> subVariantAttrMap : updationModel.getPidcSubVarAttrsToBeCreated()
            .values()) {

          // loop through varAttr Map for each sub variant
          for (Entry<Long, PidcSubVariantAttribute> subVariantAttrEntry : subVariantAttrMap.entrySet()) {
            PidcSubVariantAttribute subvarAttr = subVariantAttrEntry.getValue();
            PidcSubVariantAttribute subvarGrpAttr = (PidcSubVariantAttribute) grpAttr;
            if (CommonUtils.isNotNull(subvarAttr)) {
              setValForSubvarAttribute(predefValEntry, subVariantAttrEntry, subvarAttr, subvarGrpAttr);
            }
          }
        }
      }
    }
  }

  /**
   * @param predefValEntry
   * @param subVariantAttrEntry
   * @param subvarAttr
   * @param subvarGrpAttr
   */
  private void setValForSubvarAttribute(final Entry<IProjectAttribute, PredefinedAttrValue> predefValEntry,
      final Entry<Long, PidcSubVariantAttribute> subVariantAttrEntry, final PidcSubVariantAttribute subvarAttr,
      final PidcSubVariantAttribute subvarGrpAttr) {
    if (subvarAttr.getSubVariantId().equals(subvarGrpAttr.getSubVariantId())) {
      // set value and value id
      subVariantAttrEntry.getValue().setValue(predefValEntry.getValue().getPredefinedValue());
      subVariantAttrEntry.getValue().setValueId(predefValEntry.getValue().getPredefinedValueId());
      subVariantAttrEntry.getValue().setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    }
    else {
      // set value and value id
      subVariantAttrEntry.getValue().setValue("");
      subVariantAttrEntry.getValue().setValueId(null);
      subVariantAttrEntry.getValue().setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
    }
  }

  /**
   * @param updationModel
   * @param predefAttrValMap
   * @param grpAttr
   */
  private void setValForVarAttribute(final ProjectAttributesUpdationModel updationModel,
      final Map<IProjectAttribute, PredefinedAttrValue> predefAttrValMap, final IProjectAttribute grpAttr) {
    // loop through each predefined value entry
    for (Entry<IProjectAttribute, PredefinedAttrValue> predefValEntry : predefAttrValMap.entrySet()) {
      if (!this.pidcDataHandler.getPidcVersInvisibleAttrSet().contains(predefValEntry.getKey().getAttrId())) {
        // for each predefined value entry , match the variant attribute n set value for the same
        for (Map<Long, PidcVariantAttribute> variantAttrMap : updationModel.getPidcVarAttrsToBeCreated().values()) {
          setValForVarAttributes(grpAttr, predefValEntry, variantAttrMap);
        }
      }
    }
  }

  /**
   * @param grpAttr
   * @param predefValEntry
   * @param variantAttrMap
   */
  private void setValForVarAttributes(final IProjectAttribute grpAttr,
      final Entry<IProjectAttribute, PredefinedAttrValue> predefValEntry,
      final Map<Long, PidcVariantAttribute> variantAttrMap) {
    // loop through varAttr Map for each variant
    for (Entry<Long, PidcVariantAttribute> variantAttrEntry : variantAttrMap.entrySet()) {

      PidcVariantAttribute varAttr = variantAttrEntry.getValue();
      PidcVariantAttribute varGrpAttr = (PidcVariantAttribute) grpAttr;

      // match variant attribute in updation model and predef value entry's attribute
      // set value to variant attributes in updation model
      if (predefValEntry.getKey().getAttrId().equals(variantAttrEntry.getKey()) && CommonUtils.isNotNull(varAttr)) {
        if (varAttr.getVariantId().equals(varGrpAttr.getVariantId())) {
          // set value and value id
          variantAttrEntry.getValue().setValue(predefValEntry.getValue().getPredefinedValue());
          variantAttrEntry.getValue().setValueId(predefValEntry.getValue().getPredefinedValueId());
          variantAttrEntry.getValue().setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
        }
        else {
          // set value and value id
          variantAttrEntry.getValue().setValue("");
          variantAttrEntry.getValue().setValueId(null);
          variantAttrEntry.getValue().setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
        }
      }
    }
  }

  /**
   * Method to set values for those attributes which needs to be moved
   *
   * @param updationModel - updation model with movement information
   * @param predefAttrValSet - predefined attribute values to be set
   * @param toBeMovedToVariant - if true, attrs need to be moved to variant, if false, to subvariant
   */
  private void setValueForAttributesToBeMoved(final ProjectAttributesUpdationModel updationModel,
      final Set<PredefinedAttrValue> preDfndSet, final boolean toBeMovedToVariant) {

    // if attributes are to be moved to variant
    if (toBeMovedToVariant) {
      setValueForVarAttr(updationModel, preDfndSet);
    }
    // if attributes are to be moved to subvariant
    else {
      setValForSubvarAttr(updationModel, preDfndSet);
    }
  }

  /**
   * @param updationModel
   * @param preDfndSet
   */
  private void setValForSubvarAttr(final ProjectAttributesUpdationModel updationModel,
      final Set<PredefinedAttrValue> preDfndSet) {
    // loop through predefined attribute value set
    for (PredefinedAttrValue predefinedAttrValue : preDfndSet) {
      PidcVersionAttributeBO pidcVersionHandler =
          new PidcVersionAttributeBO(this.allPIDCAttrMap.get(predefinedAttrValue.getPredefinedAttrId()),
              new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
      if (pidcVersionHandler.isNotInvisibleAttr(predefinedAttrValue.getPredefinedAttrId(), this.grpdAttr)) {
        // for each predefined attribute value, loop throug variant attributers to be created in updation model
        for (Map<Long, PidcSubVariantAttribute> subVariantAttrMap : updationModel.getPidcSubVarAttrsToBeCreated()
            .values()) {
          PidcSubVariantAttribute subVariantAttr = subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId());
          PidcSubVariantAttribute grpSubvarAttr = (PidcSubVariantAttribute) this.grpdAttr;
          if (CommonUtils.isNotNull(subVariantAttr)) {
            setSubvarAttrVal(predefinedAttrValue, subVariantAttrMap, subVariantAttr, grpSubvarAttr);
          }
        }
      }
    }
  }

  /**
   * @param predefinedAttrValue
   * @param subVariantAttrMap
   * @param subVariantAttr
   * @param grpSubvarAttr
   */
  private void setSubvarAttrVal(final PredefinedAttrValue predefinedAttrValue,
      final Map<Long, PidcSubVariantAttribute> subVariantAttrMap, final PidcSubVariantAttribute subVariantAttr,
      final PidcSubVariantAttribute grpSubvarAttr) {
    if (subVariantAttr.getSubVariantId().equals(grpSubvarAttr.getSubVariantId())) {
      // set value id and value for variant attribute
      subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setValueId(predefinedAttrValue.getPredefinedValueId());
      subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setValue(predefinedAttrValue.getPredefinedValue());
      subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    }
    else {
      // set value id and value for variant attribute
      subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId()).setValueId(null);
      subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId()).setValue("");
      subVariantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
    }
  }

  /**
   * @param updationModel
   * @param preDfndSet
   */
  private void setValueForVarAttr(final ProjectAttributesUpdationModel updationModel,
      final Set<PredefinedAttrValue> preDfndSet) {
    // loop through predefined attribute value set
    for (PredefinedAttrValue predefinedAttrValue : preDfndSet) {
      PidcVersionAttributeBO pidcVersionHandler =
          new PidcVersionAttributeBO(this.allPIDCAttrMap.get(predefinedAttrValue.getPredefinedAttrId()),
              new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
      if (pidcVersionHandler.isNotInvisibleAttr(predefinedAttrValue.getPredefinedAttrId(), this.grpdAttr)) {

        // for each predefined attribute value, loop throug variant attributers to be created in updation model
        for (Map<Long, PidcVariantAttribute> variantAttrMap : updationModel.getPidcVarAttrsToBeCreated().values()) {
          PidcVariantAttribute varAttr = variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId());
          PidcVariantAttribute grpVarAttr = (PidcVariantAttribute) this.grpdAttr;
          if (CommonUtils.isNotNull(varAttr)) {
            setVarAttrVal(predefinedAttrValue, variantAttrMap, varAttr, grpVarAttr);
          }
        }
      }
    }
  }

  /**
   * @param predefinedAttrValue
   * @param variantAttrMap
   * @param varAttr
   * @param grpVarAttr
   */
  private void setVarAttrVal(final PredefinedAttrValue predefinedAttrValue,
      final Map<Long, PidcVariantAttribute> variantAttrMap, final PidcVariantAttribute varAttr,
      final PidcVariantAttribute grpVarAttr) {
    if (varAttr.getVariantId().equals(grpVarAttr.getVariantId())) {
      // set value id and value for variant attribute
      variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setValueId(predefinedAttrValue.getPredefinedValueId());
      variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId()).setValue(predefinedAttrValue.getPredefinedValue());
      variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    }
    else {
      // set value id and value for variant attribute
      variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId()).setValueId(null);
      variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId()).setValue("");
      variantAttrMap.get(predefinedAttrValue.getPredefinedAttrId())
          .setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType());
    }
  }


  /**
   * @param updationModel
   * @param predefAttrValEntry
   * @param predefAttr
   * @throws CommandException
   */
  private void cmdPredefAttr(final ProjectAttributesUpdationModel updationModel,
      final Entry<IProjectAttribute, PredefinedAttrValue> predefAttrValEntry, final PidcVersionAttribute predefAttr) {
    if (null != predefAttrValEntry.getValue().getPredefinedValue()) {
      predefAttr.setValue(predefAttrValEntry.getValue().getPredefinedValue());
      predefAttr.setValueId(predefAttrValEntry.getValue().getPredefinedValueId());
      predefAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    }
    else {
      predefAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType());
    }

    if (null == predefAttr.getId()) {
      updationModel.getPidcAttrsToBeCreated().put(predefAttr.getAttrId(), predefAttr);
    }
    else {
      updationModel.getPidcAttrsToBeUpdated().put(predefAttr.getAttrId(), predefAttr);
    }
  }


  /**
   * @param updationModel
   * @param predefAttrValEntry
   * @param predefAttrVar
   * @throws CommandException
   */
  private void cmdPredefAttrVar(final ProjectAttributesUpdationModel updationModel,
      final Entry<IProjectAttribute, PredefinedAttrValue> predefAttrValEntry,
      final PidcVariantAttribute predefAttrVar) {
    if (null != predefAttrValEntry.getValue()) {
      predefAttrVar.setValue(predefAttrValEntry.getValue().getPredefinedValue());
      predefAttrVar.setValueId(predefAttrValEntry.getValue().getPredefinedValueId());
      predefAttrVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    }
    else {
      predefAttrVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
    }


    if (null == predefAttrVar.getId()) {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeCreated().get(predefAttrVar.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(predefAttrVar.getAttrId())) {
        varAttrMap.put(predefAttrVar.getAttrId(), predefAttrVar);
        updationModel.getPidcVarAttrsToBeCreated().put(predefAttrVar.getVariantId(), varAttrMap);
      }
    }
    else {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeUpdated().get(predefAttrVar.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(predefAttrVar.getAttrId())) {
        varAttrMap.put(predefAttrVar.getAttrId(), predefAttrVar);
        updationModel.getPidcVarAttrsToBeUpdated().put(predefAttrVar.getVariantId(), varAttrMap);
      }
    }


  }


  /**
   * @param updationModel
   * @param predefAttrValEntry
   * @param predefAttrSubVar
   * @throws CommandException
   */
  private void cmdPredefAttrSubVar(final ProjectAttributesUpdationModel updationModel,
      final Entry<IProjectAttribute, PredefinedAttrValue> predefAttrValEntry,
      final PidcSubVariantAttribute predefAttrSubVar) {

    if (null != predefAttrValEntry.getValue()) {
      predefAttrSubVar.setValueId(predefAttrValEntry.getValue().getPredefinedValueId());
      predefAttrSubVar.setValue(predefAttrValEntry.getValue().getPredefinedValue());
      predefAttrSubVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    }
    else {
      predefAttrSubVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
    }


    if (null == predefAttrSubVar.getId()) {
      Map<Long, PidcSubVariantAttribute> varAttrMap =
          updationModel.getPidcSubVarAttrsToBeCreated().get(predefAttrSubVar.getSubVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(predefAttrSubVar.getAttrId())) {
        varAttrMap.put(predefAttrSubVar.getAttrId(), predefAttrSubVar);
        updationModel.getPidcSubVarAttrsToBeCreated().put(predefAttrSubVar.getSubVariantId(), varAttrMap);
      }
    }
    else {
      Map<Long, PidcSubVariantAttribute> varAttrMap =
          updationModel.getPidcSubVarAttrsToBeUpdated().get(predefAttrSubVar.getSubVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(predefAttrSubVar.getAttrId())) {
        varAttrMap.put(predefAttrSubVar.getAttrId(), predefAttrSubVar);
        updationModel.getPidcSubVarAttrsToBeUpdated().put(predefAttrSubVar.getSubVariantId(), varAttrMap);
      }
    }

  }


  /**
   * This method is to fetch the grouped attr in case of accepting the values of multiple grpd attrs
   *
   * @param attrVal
   */
  private void getGroupedAttrForMultipleUpdate(final AttributeValue attrVal) {
    if (null == this.grpdAttr) {
      PidcVersionAttribute pidAttr = this.allPIDCAttrMap.get(attrVal.getAttributeId());
      this.grpdAttr = pidAttr;
      if (pidAttr.isAtChildLevel()) {
        PidcVariantAttribute varAttr = this.pidcDataHandler.getVariantAttributeMap()
            .get(this.pidcDataHandler.getVariantMap().keySet().iterator().next()).get(attrVal.getAttributeId());

        this.grpdAttr = varAttr;
        if (varAttr.isAtChildLevel()) {
          PidcSubVariantAttribute subVarAttr = this.pidcDataHandler.getSubVariantAttributeMap()
              .get(this.pidcDataHandler.getSubVariantMap().keySet().iterator().next()).get(attrVal.getAttributeId());

          this.grpdAttr = subVarAttr;
        }
      }
    }
  }

  /**
   * @param updationModel
   * @param grpdAttribute
   * @param sortedSet
   * @param predefinedAttrValue
   * @param preDefndAttr
   * @throws CommandException
   */
  private void addValueEditCommands(final ProjectAttributesUpdationModel updationModel,
      final Set<PredefinedAttrValue> preDfndSet, final IProjectAttribute grpdAttribute) {
    this.commonPredefAttrMap.clear();
    if (null != preDfndSet) {

      PIDCGroupedAttrActionSet actionSet = new PIDCGroupedAttrActionSet(this.pidcDataHandler, this.pidcVersionBO);

      addToCommonPreDefAttrMap(grpdAttribute);
      // loop through predefined attribute value set
      for (PredefinedAttrValue predefinedAttrValue : preDfndSet) {

        // fetch pidc version attribute for predefined attribute value's attribute
        PidcVersionAttribute preDefndAttr = this.allPIDCAttrMap.get(predefinedAttrValue.getPredefinedAttrId());
        PidcVersionAttributeBO pidcVersionHandler = new PidcVersionAttributeBO(this.allPIDCAttrMap.get(preDefndAttr),
            new PidcVersionBO(this.pidcVersion, this.pidcDataHandler));
        if (pidcVersionHandler.isNotInvisibleAttr(preDefndAttr.getAttrId(), this.grpdAttr)) {
          // check if the edited grouped attribute is instance of pidc version attribute
          if ((grpdAttribute instanceof PidcVersionAttribute) &&
              actionSet.checkIfGrpdAttrsAtPIDCLevel(grpdAttribute, preDefndAttr)) {

            if (null == predefinedAttrValue.getPredefinedValue()) {

              preDefndAttr.setPartNumber("");
              preDefndAttr.setSpecLink("");
              preDefndAttr.setAdditionalInfoDesc("");
              preDefndAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
              preDefndAttr.setAttrHidden(preDefndAttr.isAttrHidden());
              if (null == preDefndAttr.getId()) {
                updationModel.getPidcAttrsToBeCreated().put(preDefndAttr.getAttrId(), preDefndAttr);
              }
              else {
                updationModel.getPidcAttrsToBeUpdated().put(preDefndAttr.getAttrId(), preDefndAttr);
              }
            }
            else if ((null == preDefndAttr.getValue()) ||
                ((null != preDefndAttr.getValue()) && (null != predefinedAttrValue.getPredefinedValue()) &&
                    !CommonUtils.isEqual(preDefndAttr.getValueId(), predefinedAttrValue.getPredefinedValueId()))) {
              preDefndAttr.setValueId(predefinedAttrValue.getPredefinedValueId());
              preDefndAttr.setValue(predefinedAttrValue.getPredefinedValue());
              preDefndAttr.setPartNumber("");
              preDefndAttr.setSpecLink("");
              preDefndAttr.setAdditionalInfoDesc("");
              preDefndAttr.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
              preDefndAttr.setAttrHidden(preDefndAttr.isAttrHidden());
              if (null == preDefndAttr.getId()) {
                updationModel.getPidcAttrsToBeCreated().put(preDefndAttr.getAttrId(), preDefndAttr);
              }
              else {
                updationModel.getPidcAttrsToBeUpdated().put(preDefndAttr.getAttrId(), preDefndAttr);
              }
            }
            else if ((null != preDefndAttr.getValue()) && (null != predefinedAttrValue.getPredefinedValue()) &&
                CommonUtils.isEqual(preDefndAttr.getValueId(), predefinedAttrValue.getPredefinedValueId())) {
              this.commonPredefAttrMap.get(((PidcVersionAttribute) grpdAttribute).getPidcVersId())
                  .add(preDefndAttr.getAttrId());
              this.saveSuccess = true;
            }
          }
          else if (grpdAttribute instanceof PidcVariantAttribute) {

            if (null == this.grpdAttr) {
              for (PidcVariant pidcVar : this.pidcDataHandler.getVariantMap().values()) {
                PidcVariantAttribute pidcAttrVar =
                    this.pidcDataHandler.getVariantAttributeMap().get(pidcVar.getId()).get(preDefndAttr.getAttrId());

                setValForPredefAttrInVar(updationModel, predefinedAttrValue, pidcAttrVar);
              }
            }
            else {
              PidcVariantAttribute pidcAttrVar = this.pidcDataHandler.getVariantAttributeMap()
                  .get(((PidcVariantAttribute) grpdAttribute).getVariantId()).get(preDefndAttr.getAttrId());
              if (pidcAttrVar == null) {
                pidcAttrVar = new PidcVariantAttribute();
                pidcAttrVar.setVariantId(((PidcVariantAttribute) grpdAttribute).getVariantId());
                pidcAttrVar.setAtChildLevel(false);
                pidcAttrVar.setAttrId(preDefndAttr.getAttrId());
              }
              setValForPredefAttrInVar(updationModel, predefinedAttrValue, pidcAttrVar);
            }
          }
          else if (grpdAttribute instanceof PidcSubVariantAttribute) {
            if (null == this.grpdAttr) {
              PidcVariantBO vHandler = new PidcVariantBO(this.pidcVersion,
                  this.pidcDataHandler.getVariantMap().get(((PidcSubVariantAttribute) grpdAttribute).getVariantId()),
                  this.pidcDataHandler);

              for (PidcSubVariant pidcSubVar : vHandler.getSubVariantsSet(false)) {
                PidcSubVariantAttribute pidcAttrSubVar = this.pidcDataHandler.getSubVariantAttributeMap()
                    .get(pidcSubVar.getId()).get(preDefndAttr.getAttrId());
                setValueForPredefAttrInSubVar(updationModel, predefinedAttrValue, pidcAttrSubVar);
              }
            }
            else {
              PidcSubVariantAttribute pidcAttrSubVar = this.pidcDataHandler.getSubVariantAttributeMap()
                  .get(((PidcSubVariantAttribute) grpdAttribute).getSubVariantId()).get(preDefndAttr.getAttrId());

              if (pidcAttrSubVar == null) {
                pidcAttrSubVar = new PidcSubVariantAttribute();
                pidcAttrSubVar.setVariantId(((PidcSubVariantAttribute) grpdAttribute).getVariantId());
                pidcAttrSubVar.setSubVariantId(((PidcSubVariantAttribute) grpdAttribute).getSubVariantId());
                pidcAttrSubVar.setAtChildLevel(false);
                pidcAttrSubVar.setAttrId(preDefndAttr.getAttrId());
              }
              setValueForPredefAttrInSubVar(updationModel, predefinedAttrValue, pidcAttrSubVar);
            }
          }
        }
      }
    }
  }


  /**
   * @param commonPredefAttrMap2
   * @param grpAttr
   */
  private void addToCommonPreDefAttrMap(final IProjectAttribute grpAttr) {
    Set<Long> commonPredDefIdSet = new HashSet<>();
    if (grpAttr instanceof PidcVersionAttribute) {
      this.commonPredefAttrMap.put(((PidcVersionAttribute) grpAttr).getPidcVersId(), commonPredDefIdSet);
    }
    if (grpAttr instanceof PidcVariantAttribute) {
      this.commonPredefAttrMap.put(((PidcVariantAttribute) grpAttr).getVariantId(), commonPredDefIdSet);
    }
    if (grpAttr instanceof PidcSubVariantAttribute) {
      this.commonPredefAttrMap.put(((PidcSubVariantAttribute) grpAttr).getSubVariantId(), commonPredDefIdSet);
    }
  }

  /**
   * @param updationModel
   * @param predefinedAttrValue
   * @param pidcAttrSubVar
   * @throws CommandException
   */
  private void setValueForPredefAttrInSubVar(final ProjectAttributesUpdationModel updationModel,
      final PredefinedAttrValue predefinedAttrValue, final PidcSubVariantAttribute pidcAttrSubVar) {
    if (null == predefinedAttrValue.getPredefinedValue()) {
      setEmptyValForPredefSubvarAttr(updationModel, pidcAttrSubVar);
    }
    else if ((null != pidcAttrSubVar) && ((null == pidcAttrSubVar.getValue()) || ((null != pidcAttrSubVar.getValue()) &&
        !CommonUtils.isEqual(pidcAttrSubVar.getValueId(), predefinedAttrValue.getPredefinedValueId())))) {
      setPredefValForPredefSubvarAttr(updationModel, predefinedAttrValue, pidcAttrSubVar);
    }

    else if ((null != pidcAttrSubVar) &&
        CommonUtils.isEqual(pidcAttrSubVar.getValueId(), predefinedAttrValue.getPredefinedValueId())) {
      this.commonPredefAttrMap.get(pidcAttrSubVar.getSubVariantId()).add(predefinedAttrValue.getPredefinedAttrId());
      this.saveSuccess = true;
    }
  }

  /**
   * @param updationModel
   * @param predefinedAttrValue
   * @param pidcAttrSubVar
   */
  private void setPredefValForPredefSubvarAttr(final ProjectAttributesUpdationModel updationModel,
      final PredefinedAttrValue predefinedAttrValue, final PidcSubVariantAttribute pidcAttrSubVar) {
    pidcAttrSubVar.setValueId(predefinedAttrValue.getPredefinedValueId());
    pidcAttrSubVar.setValue(predefinedAttrValue.getPredefinedValue());
    pidcAttrSubVar.setPartNumber("");
    pidcAttrSubVar.setSpecLink("");
    pidcAttrSubVar.setAdditionalInfoDesc("");
    pidcAttrSubVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    if (null == pidcAttrSubVar.getId()) {
      Map<Long, PidcSubVariantAttribute> varAttrMap =
          updationModel.getPidcSubVarAttrsToBeCreated().get(pidcAttrSubVar.getSubVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrSubVar.getAttrId())) {
        varAttrMap.put(pidcAttrSubVar.getAttrId(), pidcAttrSubVar);
        updationModel.getPidcSubVarAttrsToBeCreated().put(pidcAttrSubVar.getSubVariantId(), varAttrMap);
      }
    }
    else {
      Map<Long, PidcSubVariantAttribute> varAttrMap =
          updationModel.getPidcSubVarAttrsToBeUpdated().get(pidcAttrSubVar.getSubVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrSubVar.getAttrId())) {
        varAttrMap.put(pidcAttrSubVar.getAttrId(), pidcAttrSubVar);
        updationModel.getPidcSubVarAttrsToBeUpdated().put(pidcAttrSubVar.getSubVariantId(), varAttrMap);
      }
    }
  }

  /**
   * @param updationModel
   * @param pidcAttrSubVar
   */
  private void setEmptyValForPredefSubvarAttr(final ProjectAttributesUpdationModel updationModel,
      final PidcSubVariantAttribute pidcAttrSubVar) {
    pidcAttrSubVar.setPartNumber("");
    pidcAttrSubVar.setSpecLink("");
    pidcAttrSubVar.setAdditionalInfoDesc("");
    pidcAttrSubVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
    if (null == pidcAttrSubVar.getId()) {
      Map<Long, PidcSubVariantAttribute> varAttrMap =
          updationModel.getPidcSubVarAttrsToBeCreated().get(pidcAttrSubVar.getSubVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      varAttrMap.put(pidcAttrSubVar.getAttrId(), pidcAttrSubVar);
      updationModel.getPidcSubVarAttrsToBeCreated().put(pidcAttrSubVar.getSubVariantId(), varAttrMap);
    }
    else {
      Map<Long, PidcSubVariantAttribute> varAttrMap =
          updationModel.getPidcSubVarAttrsToBeUpdated().get(pidcAttrSubVar.getSubVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrSubVar.getAttrId())) {
        varAttrMap.put(pidcAttrSubVar.getAttrId(), pidcAttrSubVar);
        updationModel.getPidcSubVarAttrsToBeUpdated().put(pidcAttrSubVar.getSubVariantId(), varAttrMap);
      }
    }
  }

  /**
   * @param updationModel
   * @param predefinedAttrValue
   * @param pidcAttrVar
   * @throws CommandException
   */
  private void setValForPredefAttrInVar(final ProjectAttributesUpdationModel updationModel,
      final PredefinedAttrValue predefinedAttrValue, final PidcVariantAttribute pidcAttrVar) {
    if (null == predefinedAttrValue.getPredefinedValue()) {
      setEmptyValForPredefVarAttr(updationModel, pidcAttrVar);
    }
    else if ((null != pidcAttrVar) && ((null == pidcAttrVar.getValue()) || ((null != pidcAttrVar.getValue()) &&
        !CommonUtils.isEqual(pidcAttrVar.getValueId(), predefinedAttrValue.getPredefinedValueId())))) {
      setPredefValForPredefVarAttr(updationModel, predefinedAttrValue, pidcAttrVar);
    }
    else if ((null != pidcAttrVar) &&
        CommonUtils.isEqual(pidcAttrVar.getValueId(), predefinedAttrValue.getPredefinedValueId())) {
      this.commonPredefAttrMap.get(pidcAttrVar.getVariantId()).add(predefinedAttrValue.getPredefinedAttrId());
      this.saveSuccess = true;
    }
  }

  /**
   * @param updationModel
   * @param predefinedAttrValue
   * @param pidcAttrVar
   */
  private void setPredefValForPredefVarAttr(final ProjectAttributesUpdationModel updationModel,
      final PredefinedAttrValue predefinedAttrValue, final PidcVariantAttribute pidcAttrVar) {
    pidcAttrVar.setValueId(predefinedAttrValue.getPredefinedValueId());
    pidcAttrVar.setValue(predefinedAttrValue.getPredefinedValue());
    pidcAttrVar.setPartNumber("");
    pidcAttrVar.setSpecLink("");
    pidcAttrVar.setAdditionalInfoDesc("");
    pidcAttrVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType());
    if (null == pidcAttrVar.getId()) {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeCreated().get(pidcAttrVar.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrVar.getAttrId())) {
        varAttrMap.put(pidcAttrVar.getAttrId(), pidcAttrVar);
        updationModel.getPidcVarAttrsToBeCreated().put(pidcAttrVar.getVariantId(), varAttrMap);
      }
    }
    else {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeUpdated().get(pidcAttrVar.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrVar.getAttrId())) {
        varAttrMap.put(pidcAttrVar.getAttrId(), pidcAttrVar);
        updationModel.getPidcVarAttrsToBeUpdated().put(pidcAttrVar.getVariantId(), varAttrMap);
      }
    }
  }

  /**
   * @param updationModel
   * @param pidcAttrVar
   */
  private void setEmptyValForPredefVarAttr(final ProjectAttributesUpdationModel updationModel,
      final PidcVariantAttribute pidcAttrVar) {
    pidcAttrVar.setPartNumber("");
    pidcAttrVar.setSpecLink("");
    pidcAttrVar.setAdditionalInfoDesc("");
    pidcAttrVar.setUsedFlag(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType());
    if (null == pidcAttrVar.getId()) {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeCreated().get(pidcAttrVar.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrVar.getAttrId())) {
        varAttrMap.put(pidcAttrVar.getAttrId(), pidcAttrVar);
        updationModel.getPidcVarAttrsToBeCreated().put(pidcAttrVar.getVariantId(), varAttrMap);
      }
    }
    else {
      Map<Long, PidcVariantAttribute> varAttrMap =
          updationModel.getPidcVarAttrsToBeUpdated().get(pidcAttrVar.getVariantId());
      if (null == varAttrMap) {
        varAttrMap = new HashMap<>();
      }
      if (!varAttrMap.containsKey(pidcAttrVar.getAttrId())) {
        varAttrMap.put(pidcAttrVar.getAttrId(), pidcAttrVar);
        updationModel.getPidcVarAttrsToBeUpdated().put(pidcAttrVar.getVariantId(), varAttrMap);
      }
    }
  }


  /**
   * @param moveModel
   * @param attrToBeMovedToVariant
   * @throws CommandException
   */
  private void moveToVariant(final ProjectAttributesMovementModel moveModel,
      final List<IProjectAttribute> attrToBeMovedToVariant) {

    for (IProjectAttribute ipidcAttribute : attrToBeMovedToVariant) {
      if (ipidcAttribute instanceof PidcVersionAttribute) {
        PidcVersionAttribute pidcAttribute = (PidcVersionAttribute) ipidcAttribute;

        PidcVersionAttributeBO handler = new PidcVersionAttributeBO(pidcAttribute, this.pidcVersionBO);
        if (handler.canMoveDown() && this.pidcVersionBO.isModifiable()) {
          moveModel.getPidcAttrsToBeMovedDown().put(pidcAttribute.getAttrId(), pidcAttribute);
        }
        else {
          CDMLogger.getInstance().info(pidcAttribute.getName() + "cannot be moved to variant", Activator.PLUGIN_ID);
        }
      }
      else if (ipidcAttribute instanceof PidcSubVariantAttribute) {
        PidcSubVariantAttribute pidcSubVarAttr = (PidcSubVariantAttribute) ipidcAttribute;

        PidcSubVariantAttributeBO subhandler =
            new PidcSubVariantAttributeBO(pidcSubVarAttr, new PidcSubVariantBO(this.pidcVersion,
                this.pidcDataHandler.getSubVariantMap().get(pidcSubVarAttr.getSubVariantId()), this.pidcDataHandler));
        if (subhandler.isModifiable() && this.pidcVersionBO.isModifiable()) {

          moveModel.getPidcSubVarAttrsToBeMovedToVariant().put(pidcSubVarAttr.getAttrId(), pidcSubVarAttr);
        }
      }
    }
  }


  /**
   * @param moveModel
   * @param attrToBeMovedToVariant
   * @throws CommandException
   */
  private void moveToSubVariant(final ProjectAttributesMovementModel moveModel,
      final Map<IProjectAttribute, PidcSubVariant> attrSubVarMap) {
    for (IProjectAttribute ipidcAttribute : attrSubVarMap.keySet()) {
      if (ipidcAttribute instanceof PidcVersionAttribute) {
        movePidcVersAttrToSubVar(moveModel, attrSubVarMap, ipidcAttribute);
      }
      else if (ipidcAttribute instanceof PidcVariantAttribute) {
        PidcVariantAttribute pidcAttrVar = (PidcVariantAttribute) ipidcAttribute;
        moveToSubVarFromVariant(moveModel, pidcAttrVar);
      }
    }
  }

  /**
   * @param moveModel
   * @param attrSubVarMap
   * @param ipidcAttribute
   */
  private void movePidcVersAttrToSubVar(final ProjectAttributesMovementModel moveModel,
      final Map<IProjectAttribute, PidcSubVariant> attrSubVarMap, final IProjectAttribute ipidcAttribute) {
    PidcVersionAttribute pidcAttribute = (PidcVersionAttribute) ipidcAttribute;
    PidcVersionAttributeBO handler = new PidcVersionAttributeBO(pidcAttribute, this.pidcVersionBO);
    if (handler.canMoveDown() && this.pidcVersionBO.isModifiable()) {
      // first move to variant
      moveModel.getPidcAttrsToBeMovedDown().put(pidcAttribute.getId(), pidcAttribute);
      // then move to subvar
      PidcVariant pidcVarOfGrpAttr =
          this.pidcDataHandler.getVariantMap().get(attrSubVarMap.get(pidcAttribute).getPidcVariantId());
      if (null != pidcVarOfGrpAttr) {
            PidcVariantAttribute pidcAttrVar = this.pidcDataHandler.getVariantAttributeMap()
                .get(pidcVarOfGrpAttr.getId()).get(pidcAttribute.getAttrId());
        if (pidcAttrVar == null) {
          pidcAttrVar =
              createVariantAttributeForMovement(pidcAttribute, attrSubVarMap.get(pidcAttribute).getPidcVariantId());
        }
        moveToSubVarFromVariant(moveModel, pidcAttrVar);
      }
      else {
        CDMLogger.getInstance().info(pidcAttribute.getName() + "cannot be moved to variant", Activator.PLUGIN_ID);
      }
    }
  }


  /**
   * Create variant attribute using values from pidc version attribute
   *
   * @param pidcVersionAttribute
   * @param variantId
   * @return created variant attribute
   */
  private PidcVariantAttribute createVariantAttributeForMovement(final PidcVersionAttribute pidcVersionAttribute,
      final Long variantId) {
    PidcVariantAttribute varAttr = new PidcVariantAttribute();
    varAttr.setName(pidcVersionAttribute.getName());
    varAttr.setAdditionalInfoDesc(pidcVersionAttribute.getAdditionalInfoDesc());
    varAttr.setAtChildLevel(false);
    varAttr.setAttrHidden(pidcVersionAttribute.isAttrHidden());
    varAttr.setCreatedDate(pidcVersionAttribute.getCreatedDate());
    try {
      varAttr.setCreatedUser(new CurrentUserBO().getUserName());
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().error(e.getMessage(), e, Activator.PLUGIN_ID);
    }

    varAttr.setDescription(pidcVersionAttribute.getDescription());
    varAttr.setAttrId(pidcVersionAttribute.getAttrId());
    varAttr.setPartNumber(pidcVersionAttribute.getPartNumber());
    varAttr.setPidcVersionId(pidcVersionAttribute.getPidcVersId());
    varAttr.setSpecLink(pidcVersionAttribute.getSpecLink());
    varAttr.setUsedFlag(pidcVersionAttribute.getUsedFlag());
    varAttr.setVariantId(variantId);
    varAttr.setValueId(pidcVersionAttribute.getValueId());
    varAttr.setValue(pidcVersionAttribute.getValue());


    return varAttr;
  }


  /**
   * @param moveModel
   * @param pidcAttrVar
   * @throws CommandException
   */
  private void moveToSubVarFromVariant(final ProjectAttributesMovementModel moveModel,
      final PidcVariantAttribute pidcAttrVar) {

    if (!pidcAttrVar.isAtChildLevel() &&
        !this.pidcDataHandler.getVariantMap().get(pidcAttrVar.getVariantId()).isDeleted() && isVarModifiable() &&
        this.pidcVersionBO.isModifiable()) {
      moveModel.getPidcVarAttrsToBeMovedDown().put(pidcAttrVar.getId(), pidcAttrVar);
    }
  }

  /**
   * @param pidcAttrVar
   * @return
   */
  private boolean isVarModifiable() {
    boolean isModifiable = false;

    // the attribute can be modified if the user can modify the PIDC
    // ICDM-2354
    if (checkWriteAccess()) {
      // structure attributes can not be modified
      isModifiable = !(this.pidcVersionBO.isDeleted() ||
          CommonUtils.isEqualIgnoreCase(this.pidcVersion.getPidStatus(), PidcVersionStatus.LOCKED.getDbStatus()));
    }
    else {
      CDMLogger.getInstance().infoDialog("User does not have WRITE access to edit attribute value",
          Activator.PLUGIN_ID);
    }
    return isModifiable;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean isResizable() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void setShellStyle(final int newShellStyle) {
    super.setShellStyle(SWT.CLOSE | SWT.BORDER | SWT.RESIZE | SWT.TITLE);
  }


  /**
   * @return the saveSuccess
   */
  public boolean isSaveSuccess() {
    return this.saveSuccess;
  }

  /**
   * returns if the user has write access on the pidcversion
   */
  private boolean checkWriteAccess() {

    NodeAccess access;
    try {
      access = new CurrentUserBO().getNodeAccessRight(this.pidcVersion.getPidcId());


      if ((null != access) && access.isWrite()) {
        return true;
      }
    }
    catch (ApicWebServiceException ex) {
      CDMLogger.getInstance().errorDialog(ex.getMessage(), ex, Activator.PLUGIN_ID);
    }
    return false;
  }
}

