/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.actions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.PIDCSearchEditor;
import com.bosch.caltool.apic.ui.editors.PIDCSearchEditorInput;
import com.bosch.caltool.apic.ui.editors.pages.PIDCSearchPage;
import com.bosch.caltool.icdm.client.bo.apic.PidcDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcSearchEditorDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;

/**
 * @author mkl2cob
 */
public class PIDCSearchAction extends Action {

  /**
   * IStructuredSelection
   */
  private final List<IProjectAttribute> selectedAttributes;

  /**
   * PIDCVersion instance
   */
  private final PidcVersion pidcVrsn;

  /**
   * PidcDataHandler instance
   */
  private final PidcDataHandler pidcDataHandler;

  /**
   * Constructor
   *
   * @param selectedAttributes : list of pidcAttrs
   * @param pidcVrsn : Project version
   * @param pidcDataHndlr PidcDataHandler
   */
  public PIDCSearchAction(final List<IProjectAttribute> selectedAttributes, final PidcVersion pidcVrsn,
      final PidcDataHandler pidcDataHndlr) {
    this.selectedAttributes = new ArrayList<>(selectedAttributes);
    this.pidcVrsn = pidcVrsn;
    this.pidcDataHandler = pidcDataHndlr;
    setText("Find projects with same configuration ");
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.PIDC_SEARCH_16X16);
    setImageDescriptor(imageDesc);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    openPIDCScoutWithSearchResults(this.selectedAttributes);
  }

  /**
   * @param selectedAttributes : list of pidcAttrs
   */
  private void openPIDCScoutWithSearchResults(final List<IProjectAttribute> selectedAttributes) {

    // get editor input
    PIDCSearchEditorInput input = new PIDCSearchEditorInput();
    try {
      if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isEditorAreaVisible()) {
        IEditorPart searchEditor =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(input);

        if (null != searchEditor) {
          input = (PIDCSearchEditorInput) searchEditor.getEditorInput();
          ((PIDCSearchEditor) searchEditor).getSearchPage().setActive(true);
        }
        // open pidc search editor
        searchEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input,
            PIDCSearchEditor.SEARCH_EDITOR_ID);

        PIDCSearchPage pidcSearchPage = (PIDCSearchPage) ((PIDCSearchEditor) searchEditor).getActivePageInstance();
        pidcSearchPage.clearAll();

        for (IProjectAttribute pidcAttr : selectedAttributes) {
          fillMapsForSearch(input.getDataHandler(), pidcAttr);
        }
        // check the used flag and attr values
        pidcSearchPage.getAttrTreeUtil().setUsedFlagStateOnRefresh();
        pidcSearchPage.getAttrTreeUtil().setCheckedStateOnRefresh();
        // update the count once the checkstate changes
        pidcSearchPage.updateCount();
        // the tree has to be refreshed to display entries with proper filter conditions
        pidcSearchPage.getAttrSection().getSummaryTreeViewer().refresh();
        pidcSearchPage.pidcSearch();
      }
    }
    catch (PartInitException exception) {
      CDMLogger.getInstance().error(exception.getLocalizedMessage(), exception);
    }


  }

  /**
   * @param input PIDCSearchEditorInput
   * @param pidcAttr IPIDCAttribute
   * @param attrValSet Set<AttributeValue>
   */
  private void fillMapsForSearch(final PidcSearchEditorDataHandler dataHandler, final IProjectAttribute pidcAttr) {
    if (pidcAttr.isAtChildLevel()) {
      fillMapForVarSubVarAttr(dataHandler, pidcAttr);
    }
    else if (null == pidcAttr.getValue()) {
      // used flag value is used for search condition if there is no attr-value

      PROJ_ATTR_USED_FLAG usedFlag = ApicConstants.PROJ_ATTR_USED_FLAG.getType(pidcAttr.getUsedFlag());
      dataHandler.setSelAttrUsed(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()),
          usedFlag.getUiType());

    }
    else {
      dataHandler.selectAttributeValue(this.pidcDataHandler.getAttributeValueMap().get(pidcAttr.getValueId()));
    }
  }

  /**
   * @param input PIDCSearchEditorInput
   * @param pidcAttr IPIDCAttribute
   */
  private void fillMapForVarSubVarAttr(final PidcSearchEditorDataHandler dataHandler,
      final IProjectAttribute pidcAttr) {
    // TODO instanceof PIDCDetailsNode
    if (pidcAttr instanceof PidcVariantAttribute) {
      // variant level attributes marked as <SUB-VAR>
      getAttributesFromSubVariantLevel(pidcAttr, dataHandler);
    }
    else {
      // if the attribute is variant, values or used flag has to be got from variant/sub variants level
      getAttributeFromVariantLevel(pidcAttr, dataHandler);
    }
  }

  /**
   * ICDM-1414
   *
   * @param pidcAttr IPIDCAttribute
   * @param input PIDCSearchEditorInput
   */
  private void getAttributesFromSubVariantLevel(final IProjectAttribute pidcAttr,
      final PidcSearchEditorDataHandler dataHandler) {

    Set<Long> attrValSet = new HashSet<>();
    boolean usedFlagYes = false;
    boolean usedFlagNo = false;
    PidcVariantAttribute varAttr = (PidcVariantAttribute) pidcAttr;
    if (varAttr != null) {

      PidcVariant var = this.pidcDataHandler.getVariantMap().get(varAttr.getVariantId());
      Object[] valueArrayFromSubVar = getValuesFromSubVariants(var, varAttr);
      Set<Long> attrValSetFromSubVar = (Set<Long>) valueArrayFromSubVar[CommonUIConstants.COLUMN_INDEX_0];
      attrValSet.addAll(attrValSetFromSubVar);
      usedFlagYes = (boolean) valueArrayFromSubVar[CommonUIConstants.COLUMN_INDEX_1];
      usedFlagNo = (boolean) valueArrayFromSubVar[CommonUIConstants.COLUMN_INDEX_2];
    }
    if (pidcAttr != null) {
      setInputValOrUsedFlagMap(pidcAttr, dataHandler, attrValSet, usedFlagYes, usedFlagNo);
    }

  }

  /**
   * @param pidcAttr IPIDCAttribute
   * @param input PIDCSearchEditorInput
   * @param attrValSet Set<AttributeValue>
   * @param usedFlagYes boolean
   * @param usedFlagNo boolean
   */
  private void setInputValOrUsedFlagMap(final IProjectAttribute pidcAttr, final PidcSearchEditorDataHandler dataHandler,
      final Set<Long> attrValSet, final boolean usedFlagYes, final boolean usedFlagNo) {
    if (attrValSet.isEmpty()) {
      // if there are no values defined in any of the variant attributes related to the pidcattr , take the used flag
      // for search
      if (usedFlagYes) {
        dataHandler.setSelAttrUsed(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()),
            ApicConstants.USED_YES_DISPLAY);
      }
      else if (usedFlagNo) {
        dataHandler.setSelAttrUsed(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()),
            ApicConstants.USED_NO_DISPLAY);
      }
      else {
        dataHandler.setSelAttrUsed(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()),
            ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType());
      }
    }
    else {
      Set<AttributeValue> attrVals = new HashSet<>();
      Map<Long, AttributeValue> attributeValueMap = this.pidcDataHandler.getAttributeValueMap();
      attrValSet.forEach(attrValId -> attrVals.add(attributeValueMap.get(attrValId)));
      dataHandler.selectAttributeValue(this.pidcDataHandler.getAttributeMap().get(pidcAttr.getAttrId()), attrVals);
    }
  }

  /**
   * ICDM-1291
   *
   * @param pidcAttr IPIDCAttribute
   * @param input PIDCSearchEditorInput
   */
  private void getAttributeFromVariantLevel(final IProjectAttribute pidcAttr,
      final PidcSearchEditorDataHandler dataHandler) {

    Set<Long> attrValSet = new HashSet<>();
    boolean usedFlagYes = false;
    boolean usedFlagNo = false;

    SortedSet<PidcVariant> variantsSet = new TreeSet<>(this.pidcDataHandler.getVariantsMap(false).values());
    for (PidcVariant var : variantsSet) {
      PidcVariantAttribute varAttr =
          this.pidcDataHandler.getVariantAttributeMap().get(var.getId()).get(pidcAttr.getAttrId());
      if (varAttr != null) {
        if (varAttr.isAtChildLevel()) {
          // if the attribute is marked as sub-variant
          Object[] valueArrayFromSubVar = getValuesFromSubVariants(var, varAttr);
          Set<Long> attrValSetFromSubVar = (Set<Long>) valueArrayFromSubVar[CommonUIConstants.COLUMN_INDEX_0];
          attrValSet.addAll(attrValSetFromSubVar);
          usedFlagYes = (boolean) valueArrayFromSubVar[CommonUIConstants.COLUMN_INDEX_1];
          usedFlagNo = (boolean) valueArrayFromSubVar[CommonUIConstants.COLUMN_INDEX_2];
        }
        else {
          // get the variant attribute same as that of the pidc attribute
          AttributeValue attributeValue = this.pidcDataHandler.getAttributeValueMap().get(varAttr.getValueId());
          if (CommonUtils.isNotNull(attributeValue)) {
            // if there is attr value , add it to the attr val set
            attrValSet.add(attributeValue.getId());
          }
          else if (!usedFlagYes &&
              CommonUtils.isEqual(varAttr.getUsedFlag(), ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) {
            // make the usedFlagYes true even if used flag is true in one of the variant attrs corresponding to the pidc
            // attr
            usedFlagYes = true;
          }
          else if (!usedFlagNo && CommonUtils.isEqual(varAttr.getUsedFlag(), ApicConstants.USED_NO_DISPLAY)) {
            // make the usedFlagNo true even if used flag is true in one of the variant attrs corresponding to the pidc
            // attr
            usedFlagNo = true;
          }

        }
      }
    }

    setInputValOrUsedFlagMap(pidcAttr, dataHandler, attrValSet, usedFlagYes, usedFlagNo);

  }

  /**
   * @param varAttr PIDCVariant
   * @param var PIDCAttributeVar
   */
  private Object[] getValuesFromSubVariants(final PidcVariant var, final PidcVariantAttribute varAttr) {
    Set<Long> attrValSet = new HashSet<>();
    boolean usedFlagYes = false;
    boolean usedFlagNo = false;

    PidcVariantBO variantHndlr = new PidcVariantBO(this.pidcVrsn, var, this.pidcDataHandler);
    SortedSet<PidcSubVariant> subVariantsSet = variantHndlr.getSubVariantsSet(false);

    for (PidcSubVariant subVar : subVariantsSet) {
      PidcSubVariantAttribute subVarAttr =
          this.pidcDataHandler.getSubVariantAttributeMap().get(subVar.getId()).get(varAttr.getAttrId());
      // get the variant attribute same as that of the pidc attribute
      AttributeValue attributeValue = this.pidcDataHandler.getAttributeValueMap().get(subVarAttr.getValueId());
      if (CommonUtils.isNotNull(attributeValue)) {
        // if there is attr value , add it to the attr val set
        attrValSet.add(attributeValue.getId());
      }
      else if (!usedFlagYes && CommonUtils.isEqual(subVarAttr.getUsedFlag(), ApicConstants.USED_YES_DISPLAY)) {
        // make the usedFlagYes true even if used flag is true in one of the variant attrs corresponding to the
        // pidc
        // attr
        usedFlagYes = true;
      }
      else if (!usedFlagNo && CommonUtils.isEqual(subVarAttr.getUsedFlag(), ApicConstants.USED_NO_DISPLAY)) {
        // make the usedFlagNo true even if used flag is true in one of the variant attrs corresponding to the
        // pidc
        // attr
        usedFlagNo = true;
      }
    }

    return new Object[] { attrValSet, usedFlagYes, usedFlagNo };
  }

}