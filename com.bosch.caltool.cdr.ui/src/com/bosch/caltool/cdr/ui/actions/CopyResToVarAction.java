/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.actions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.cdr.ui.dialogs.AttachQnaireSelDialog;
import com.bosch.caltool.cdr.ui.dialogs.CopyResToVarDialog;
import com.bosch.caltool.icdm.client.bo.apic.PidcTreeNode;
import com.bosch.caltool.icdm.client.bo.general.CommonDataBO;
import com.bosch.caltool.icdm.common.ui.utils.ImageKeys;
import com.bosch.caltool.icdm.common.ui.utils.ImageManager;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultInput;
import com.bosch.caltool.icdm.model.cdr.AttachRvwResultResponse;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.CDRReviewResult;
import com.bosch.caltool.icdm.model.cdr.CopyResultToVarData;
import com.bosch.caltool.icdm.model.cdr.ReviewVariantModel;
import com.bosch.caltool.icdm.model.cdr.RvwVariant;
import com.bosch.caltool.icdm.model.general.CommonParamKey;
import com.bosch.caltool.icdm.ws.rest.client.cdr.CDRReviewResultServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.cdr.RvwVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;
import com.bosch.rcputils.message.dialogs.MessageDialogUtils;


/**
 * this class attaches result to another variant
 *
 * @author rgo7cob
 */
public class CopyResToVarAction extends Action {

  /**
   * menu manager
   */
  private final IMenuManager manager;
  /**
   * cdr result element
   */
  private final Object firstElement;
  /**
   * viewer viewer
   */
  private final TreeViewer viewer;
  /**
   * Map which stores which dependant atttributes are not set in a variant Key - variant name Value - attribute name
   */
  private final Map<PidcVariant, Set<String>> varDependantAttr = new HashMap<>();
  /**
   * Set which stores variant with different pver
   */
  private final SortedSet<PidcVariant> varWithDiffPver = new TreeSet<>();


  /**
   * @param manager manager
   * @param firstElement firstElement
   * @param viewer viewer
   */
  public CopyResToVarAction(final IMenuManager manager, final Object firstElement, final TreeViewer viewer) {
    super();
    this.manager = manager;
    this.firstElement = firstElement;
    this.viewer = viewer;
    setProperties();
  }


  /**
   * set the properties
   */
  private void setProperties() {
    setText("Attach result to another variant");
    // get the result obj
    CDRReviewResult result = getCdrResult(this.firstElement);
    setEnabled(false);
    CDRReviewResultServiceClient resultClient = new CDRReviewResultServiceClient();
    // option will be enabled if the result is modifiable
    try {
      if ((result != null) && (null != result.getPrimaryVariantId()) && resultClient.canModify(result.getId())) {
        setEnabled(true);
      }

    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().error(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    // set the menu icon
    final ImageDescriptor imageDesc = ImageManager.getImageDescriptor(ImageKeys.COPY_16X16);
    setImageDescriptor(imageDesc);
    this.manager.add(this);

  }


  /**
   * {@inheritDoc}
   */
  @Override
  public void run() {
    // get the selected review result
    CDRReviewResult result = getCdrResult(this.firstElement);

    CDRReviewResultServiceClient resultClient = new CDRReviewResultServiceClient();
    CopyResultToVarData resultData;
    try {
      if (result != null) {
        // get the details for selected result
        resultData = resultClient.getResultDetailsForAttachVar(result.getId());

        Map<Long, Long> attrValMap = resultData.getAttrValMap();
        Map<Long, String> attrUsedMap = resultData.getAttrUsedMap();
        // get the variants for the selected result
        SortedSet<PidcVariant> variantsSet = resultData.getPidcVariants();
        // get pidc variants which has same sdom pver as selected result
        Set<PidcVariant> varWithoutDepMatch = checkSameVal(variantsSet, attrValMap, attrUsedMap, resultData);
        Set<PidcVariant> linkedVars = new TreeSet<>();

        for (PidcVariant rvwVar : resultData.getReviewPidcVars()) {
          linkedVars.add(rvwVar);
          varWithoutDepMatch.remove(rvwVar);
          this.varDependantAttr.remove(rvwVar);
        }
        // open the dialog to select the variant
        final CopyResToVarDialog variantSelDialog =
            new CopyResToVarDialog(Display.getCurrent().getActiveShell(), resultData.getPidcVersion(),
                new TreeSet<PidcVariant>(resultData.getA2lMappedVariantsMap().values()), linkedVars, varWithoutDepMatch,
                this.varDependantAttr, this.varWithDiffPver, resultData.getSameVarGrpFlag());

        variantSelDialog.setInpData(variantsSet);
        // if variant are not present , show a warn dialog
        if (variantsSet.isEmpty()) {
          CDMLogger.getInstance().warnDialog("No variant available to attach the result", Activator.PLUGIN_ID);
          return;
        }
        variantSelDialog.setStyle(true);
        variantSelDialog.open();
        // once variant is selected , attach the result
        attachResBasedOnRvwTyp(result, variantSelDialog);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    super.run();
  }


  /**
   * @param result
   * @param variantSelDialog
   */
  private void attachResBasedOnRvwTyp(final CDRReviewResult result, final CopyResToVarDialog variantSelDialog) {
    if (variantSelDialog.getReturnCode() == 0) {
      List<PidcVariant> selVariants = variantSelDialog.getSelVariants();
      // For Official and Start reviews when variant is attached, questionnaire
      if (CDRConstants.REVIEW_TYPE.getType(result.getReviewType()) != CDRConstants.REVIEW_TYPE.TEST) {
        AttachQnaireSelDialog attachQnaireSelDialog =
            new AttachQnaireSelDialog(Display.getCurrent().getActiveShell(), selVariants);
        attachQnaireSelDialog.open();

        if (attachQnaireSelDialog.getReturnCode() == 0) {
          attachResToVariants(result, variantSelDialog, selVariants, attachQnaireSelDialog);
        }
      }
      else {
        attachResToVariants(result, variantSelDialog, selVariants, null);
      }
    }
  }


  /**
   * @param result
   * @param variantSelDialog
   * @param selVariants
   * @param attachQnaireSelDialog
   */
  private void attachResToVariants(final CDRReviewResult result, final CopyResToVarDialog variantSelDialog,
      final List<PidcVariant> selVariants, final AttachQnaireSelDialog attachQnaireSelDialog) {
    // attach the result to selected variants
    StringBuilder invalidVariants = new StringBuilder();
    for (PidcVariant pidcVariant : selVariants) {
      if (isInvalidVariant(variantSelDialog, pidcVariant)) {
        invalidVariants.append(pidcVariant.getName()).append(",");
      }
      else {
        createRvwVarCommad(result, pidcVariant,
            (null != attachQnaireSelDialog) && attachQnaireSelDialog.isLinkExistingQnaire());
      }
    }

    if (!invalidVariants.toString().isEmpty()) {
      MessageDialogUtils.getInfoMessageDialog("Variants which are not linked",
          "Linking to variant(s) " + invalidVariants.toString().substring(0, invalidVariants.toString().length() - 1) +
              " is not possible. Those variant(s) will be skipped");
    }
  }

  /**
   * @param variantSelDialog
   * @param pidcVariant
   * @return true if invalid
   */
  private boolean isInvalidVariant(final CopyResToVarDialog variantSelDialog, final PidcVariant pidcVariant) {
    return variantSelDialog.getLinkedVars().contains(pidcVariant) ||
        variantSelDialog.getVarWithoutDepMatch().contains(pidcVariant) ||
        variantSelDialog.getVarWithDiffPver().contains(pidcVariant);
  }


  /**
   * Get the cdr result obj
   *
   * @param element firstElement
   * @return the cdr result.
   */
  private CDRReviewResult getCdrResult(final Object element) {
    if (element instanceof PidcTreeNode) {
      return ((PidcTreeNode) element).getReviewResult();
    }
    else if (element instanceof ReviewVariantModel) {
      return ((ReviewVariantModel) element).getReviewResultData().getCdrReviewResult();
    }
    return null;

  }


  /**
   * create rvw varaint commad
   *
   * @param pidcVariant
   * @param result
   * @param b
   */
  private void createRvwVarCommad(final CDRReviewResult result, final PidcVariant pidcVariant,
      final boolean isLinkExistingQnaire) {
    RvwVariantServiceClient varClient = new RvwVariantServiceClient();
    RvwVariant rvwVar = new RvwVariant();
    rvwVar.setResultId(result.getId());
    rvwVar.setVariantId(pidcVariant.getId());
    // create rvw variant for the selected result
    try {
      // For official and start reviews, check for existing Questionnaire responses and create Questionnaire Responses
      if (CDRConstants.REVIEW_TYPE.getType(result.getReviewType()) != CDRConstants.REVIEW_TYPE.TEST) {
        AttachRvwResultInput inp = new AttachRvwResultInput();
        inp.setRvwVariant(rvwVar);
        inp.setLinkExistingQnaire(isLinkExistingQnaire);
        AttachRvwResultResponse resp = varClient.attachRvwResWithQnaire(inp);
        if (!resp.getQaireRespSkipped().isEmpty()) {
          StringBuilder sb = new StringBuilder();
          resp.getQaireRespSkipped().forEach(qnaireResp -> sb.append(qnaireResp.getName()).append("\n"));
          String message = isLinkExistingQnaire
              ? "The following Questionnaire Response(s) are already linked in the target variant and will be skipped : \n\n"
              : "The following Questionnaire Response(s) are already available in the target variant and will be skipped : \n\n";
          CDMLogger.getInstance().infoDialog(message + sb.toString(), Activator.PLUGIN_ID);
        }
      }
      else {
        varClient.create(rvwVar);
      }
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    // // ICDM-2081
    if (null != this.viewer) {
      this.viewer.refresh();
    }
  }


  /**
   * Checks the values in variants
   *
   * @param varSet varSet
   * @param attrUsedMap
   * @param resultData
   * @param attrValMap2
   * @return
   */
  private Set<PidcVariant> checkSameVal(final SortedSet<PidcVariant> varSet, final Map<Long, Long> attrValMap,
      final Map<Long, String> attrUsedMap, final CopyResultToVarData resultData) {
    this.varDependantAttr.clear();
    Set<PidcVariant> varSetToRemove = new HashSet<>();
    Set<Long> attrNotRelForVarLinkSet = new HashSet<>();
    try {
      // 628188: Allow exceptions when checking for same attribute values when linking reviews to other variants
      String attrNotRelForVarLinkStr =
          new CommonDataBO().getParameterValue(CommonParamKey.RVW_ATTR_IDS_NOT_REL_FOR_LINK);
      attrNotRelForVarLinkSet =
          Arrays.asList(attrNotRelForVarLinkStr.split(",")).stream().map(Long::valueOf).collect(Collectors.toSet());
    }
    catch (ApicWebServiceException exp) {
      CDMLogger.getInstance().errorDialog(exp.getLocalizedMessage(), exp, Activator.PLUGIN_ID);
    }
    for (PidcVariant pidcVariant : varSet) {

      Set<String> varAttrSet = this.varDependantAttr.get(pidcVariant);
      varAttrSet = getVarAttrSet(varAttrSet);


      Map<Long, PidcVariantAttribute> varAttributes = resultData.getAllVariantAttributeMap().get(pidcVariant.getId());

      for (PidcVariantAttribute varAttr : varAttributes.values()) {

        String attrUsedVal = getAttrUsedVal(attrUsedMap, varAttr);
        // Remove the value if the attr used flag is different or variant has different attribute val
        if (isVarLinkingNotAllowed(attrValMap, attrNotRelForVarLinkSet, varAttr, attrUsedVal)) {
          removeVarAttrWithDiffVal(varSetToRemove, pidcVariant, varAttrSet, varAttr);
          break;
        }
      }
    }


    return varSetToRemove;

  }


  /**
   * @param attrValMap
   * @param attrNotRelForVarLinkSet
   * @param varAttr
   * @param attrUsedVal
   * @return
   */
  private boolean isVarLinkingNotAllowed(final Map<Long, Long> attrValMap, final Set<Long> attrNotRelForVarLinkSet,
      final PidcVariantAttribute varAttr, final String attrUsedVal) {
    return !attrNotRelForVarLinkSet.contains(varAttr.getAttrId()) &&
        (isVarAttrWithDiffVal(attrValMap, varAttr) || isVarAttrUsedFlagDiff(varAttr, attrUsedVal));
  }


  /**
   * @param varAttr
   * @param attrUsedVal
   * @return
   */
  private boolean isVarAttrUsedFlagDiff(final PidcVariantAttribute varAttr, final String attrUsedVal) {
    return CommonUtils.isNotEmptyString(attrUsedVal) && CommonUtils.isNotEqual(attrUsedVal, varAttr.getUsedFlag());
  }


  /**
   * @param attrValMap
   * @param varAttr
   * @return
   */
  private boolean isAttrValMapNullOrEmpty(final Map<Long, Long> attrValMap, final PidcVariantAttribute varAttr) {
    return CommonUtils.isNotEmpty(attrValMap) && CommonUtils.isNotNull(attrValMap.get(varAttr.getAttrId()));
  }


  /**
   * @param attrUsedMap
   * @param varAttr
   * @return
   */
  private boolean isAttrUsedMapNullOrEmpty(final Map<Long, String> attrUsedMap, final PidcVariantAttribute varAttr) {
    return CommonUtils.isNotEmpty(attrUsedMap) && CommonUtils.isNotNull(attrUsedMap.get(varAttr.getAttrId()));
  }


  /**
   * @param varAttrSet
   * @return
   */
  private Set<String> getVarAttrSet(final Set<String> varAttrSet) {
    return varAttrSet == null ? new TreeSet<>() : varAttrSet;
  }


  /**
   * @param attrValMap
   * @param attrNotRelForVarLinkSet
   * @param varAttr
   * @return
   */
  private boolean isVarAttrWithDiffVal(final Map<Long, Long> attrValMap, final PidcVariantAttribute varAttr) {
    return isAttrValMapNullOrEmpty(attrValMap, varAttr) &&
        CommonUtils.isNotEqual(attrValMap.get(varAttr.getAttrId()), varAttr.getValueId());
  }


  /**
   * @param attrUsedMap
   * @param varAttr
   * @return
   */
  private String getAttrUsedVal(final Map<Long, String> attrUsedMap, final PidcVariantAttribute varAttr) {
    String attrUsedVal = null;
    if (isAttrUsedMapNullOrEmpty(attrUsedMap, varAttr)) {
      attrUsedVal = attrUsedMap.get(varAttr.getAttrId());


      // The used flag value from the Service us NO and in the data model is N.
      if (ApicConstants.CODE_WORD_NO.equalsIgnoreCase(attrUsedVal)) {
        attrUsedVal = ApicConstants.CODE_NO;
      }
      // The used flag value from the Service us YES and in the data model is N.
      if (ApicConstants.CODE_WORD_YES.equalsIgnoreCase(attrUsedVal)) {
        attrUsedVal = ApicConstants.CODE_YES;
      }
    }
    return attrUsedVal;
  }


  /**
   * Remove variants with different values
   *
   * @param varSetToRemove
   * @param pidcVariant
   * @param varAttrSet
   * @param varAttr
   */
  private void removeVarAttrWithDiffVal(final Set<PidcVariant> varSetToRemove, final PidcVariant pidcVariant,
      final Set<String> varAttrSet, final PidcVariantAttribute varAttr) {
    if ("PVER name in SDOM".equalsIgnoreCase(varAttr.getName())) {
      this.varWithDiffPver.add(pidcVariant);
    }
    varAttrSet.add(varAttr.getName());
    varSetToRemove.add(pidcVariant);
  }
}
