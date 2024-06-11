/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.apic.jpa.comparison.PIDCImportCompareResult;
import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.dmframework.transactions.TransactionSummary;
import com.bosch.caltool.dmframework.transactions.TransactionSummaryDetails;
import com.bosch.caltool.icdm.common.util.MailHotline;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * CmdPIDCImport.java - Command to perform PIDC Import
 *
 * @author jvi6cob
 */
public class CmdPIDCImport extends AbstractCommand {


  private final PIDCImportParsedResult pidcImportParsedResult;
  /**
   * Stack for storing child commands executed
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  private final ApicDataProvider apicDataProvider;

  // ICDM-751
  private final TransactionSummary summaryData = new TransactionSummary(this);

  // iCDM-834
  private final List<CmdModAttributeValue> attrVal4Clearance = new ArrayList<CmdModAttributeValue>();

  /**
   * ICDM-1770. Map which contains newly inserted Attribute Values mapped against a Value text which in turn is mapped
   * to an Attribute id. This map is used to prevent repeated insert of attribute values when an attribute value is
   * newly added for more than one variant attribute or between sub-variant attributes or mix of both. </br>
   * </br>
   * APPLICABLE ONLY FOR VARIANTS AND SUB-VARIANT SINCE DUPLICATES ARE POSSIBLE ONLY AT THIS LEVEL
   */
  // AttributeID ->Value,ValueID
  private final Map<Long, Map<String, AttributeValue>> newAttrValuesMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param apicDataProvider ApicDataProvider
   * @param pidcImportParsedResult PIDCImportParsedResult
   */
  public CmdPIDCImport(final ApicDataProvider apicDataProvider, final PIDCImportParsedResult pidcImportParsedResult) {
    super(apicDataProvider);
    // when using this constructor, the commandMode is INSERT
    this.commandMode = COMMAND_MODE.INSERT;
    this.apicDataProvider = apicDataProvider;
    this.pidcImportParsedResult = pidcImportParsedResult;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeInsertCommand() throws CommandException {

    this.childCmdStack.clear();
    // icdm-229
    persistAttributeChanges();
    persistVariantChanges();
    persistSubVariantChanges();


  }

  /**
   * This method is to persist the valid attribute changes which are not variant or sub-variant
   */
  private void persistAttributeChanges() {
    LinkedHashMap<Long, IPIDCAttribute> linkedHashMap = new LinkedHashMap<Long, IPIDCAttribute>();
    calculateInsertionOrder(this.pidcImportParsedResult.getModifiedValidImportPIDCAttributes(), linkedHashMap, null);
    persistValidChanges(linkedHashMap);
  }

  /**
   * This method is to persist the valid variant attribute changes
   */
  private void persistVariantChanges() {
    for (Map<Long, IPIDCAttribute> validAttrMap : this.pidcImportParsedResult.getModifiedValidImportVarAttributes()
        .values()) {
      LinkedHashMap<Long, IPIDCAttribute> linkedHashMap = new LinkedHashMap<Long, IPIDCAttribute>();
      // Calculate the insertion order
      calculateInsertionOrder(validAttrMap, linkedHashMap, null);
      persistValidChanges(linkedHashMap);
    }
  }


  /**
   * This method is to persist the valid sub-variant attribute changes
   */
  private void persistSubVariantChanges() {
    for (Map<Long, IPIDCAttribute> validAttrMap : this.pidcImportParsedResult.getModifiedValidImportSubVarAttributes()
        .values()) {
      LinkedHashMap<Long, IPIDCAttribute> linkedHashMap = new LinkedHashMap<Long, IPIDCAttribute>();
      // Calculate the insertion order
      calculateInsertionOrder(validAttrMap, linkedHashMap, null);
      persistValidChanges(linkedHashMap);
    }
  }

  /**
   * Calculates the proper order in which attribute changes are to be updated in the database taking into account the
   * dependencies
   *
   * @param validAttrMap
   * @param orderedAttrMap
   * @param lastToPresent
   */
  private void calculateInsertionOrder(final Map<Long, IPIDCAttribute> validAttrMap,
      final Map<Long, IPIDCAttribute> orderedAttrMap, final Boolean lastToPresent) {

    for (Entry<Long, IPIDCAttribute> attrEntry : validAttrMap.entrySet()) {
      Long attrID = attrEntry.getKey();
      IPIDCAttribute iPidcAttribute = attrEntry.getValue();
      if (orderedAttrMap.containsKey(attrID)) {
        continue;
      }
      if ((lastToPresent == null) || lastToPresent) {
        // Fill from last to present for this list
        List<AttrDependency> attrDependencies = iPidcAttribute.getAttribute().getAttrDependencies(false);
        if ((attrDependencies != null) && !attrDependencies.isEmpty()) {
          Map<Long, IPIDCAttribute> validAttrDependenciesInImport = new HashMap<Long, IPIDCAttribute>();
          validAttrDependenciesInImport.putAll(validAttrMap);
          validAttrDependenciesInImport.remove(attrID);
          boolean recursionRequired = false;
          for (AttrDependency attrDependency : attrDependencies) {
            Attribute dependencyAttribute = attrDependency.getDependencyAttribute();
            if (dependencyAttribute == null) {
              continue;
            }
            Long attributeID = dependencyAttribute.getAttributeID();
            if (validAttrMap.containsKey(attributeID)) {
              recursionRequired = true;
            }
          }
          if (recursionRequired) {
            calculateInsertionOrder(validAttrDependenciesInImport, orderedAttrMap, true);
          }
          orderedAttrMap.put(attrID, iPidcAttribute);
        }
        else {
          orderedAttrMap.put(attrID, iPidcAttribute);
        }
      }

      if ((lastToPresent == null) || !lastToPresent) {
        // Fill from present+1 to first for this list
        List<AttrDependency> referentialAttrDependencies =
            iPidcAttribute.getAttribute().getReferentialAttrDependencies(false);
        if ((referentialAttrDependencies != null) && !referentialAttrDependencies.isEmpty()) {
          Map<Long, IPIDCAttribute> validAttrRefDepn = new HashMap<Long, IPIDCAttribute>();
          validAttrRefDepn.putAll(validAttrMap);
          validAttrRefDepn.remove(attrID);
          boolean recursionRequired = false;
          for (AttrDependency attrRefDependency : referentialAttrDependencies) {
            Attribute attribute = attrRefDependency.getAttribute();
            if (attribute == null) {
              continue;
            }
            Long attributeID = attribute.getAttributeID();
            if (validAttrMap.containsKey(attributeID)) {
              recursionRequired = true;
            }
          }
          if (!orderedAttrMap.containsKey(attrID)) {
            orderedAttrMap.put(attrID, iPidcAttribute);
          }
          if (recursionRequired) {
            calculateInsertionOrder(validAttrRefDepn, orderedAttrMap, false);
          }
        }
        else {
          if (!orderedAttrMap.containsKey(attrID)) {
            orderedAttrMap.put(attrID, iPidcAttribute);
          }
        }
      }
    }
  }

  /**
   * Updates the database with the valid changes that is done during the import
   *
   * @param modifiedValidAttributes
   */
  private void persistValidChanges(final Map<Long, IPIDCAttribute> modifiedValidAttributes) {
    if (modifiedValidAttributes.isEmpty()) {
      return;
    }

    for (Long attrID : modifiedValidAttributes.keySet()) {
      if (modifiedValidAttributes.get(attrID) instanceof PIDCAttribute) {
        PIDCAttribute pidcAttribute = (PIDCAttribute) modifiedValidAttributes.get(attrID);
        AttributeValue attrValue = null;
        if (pidcAttribute.getValueID() != null) {
          attrValue = this.apicDataProvider.getAttrValue(pidcAttribute.getValueID());
        }
        CmdModAttributeValue cmdModAttrValue = null;

        PIDCImportCompareResult pidcImportCompareResult =
            this.pidcImportParsedResult.getPidcImportCompareResultMap().get(attrID);
        if (pidcImportCompareResult.isNewValueAdded() && !pidcImportCompareResult.isInvalidModify()) {
          // Create an attribute value cmd
          cmdModAttrValue = new CmdModAttributeValue(this.apicDataProvider, this.apicDataProvider.getAttribute(attrID));
          cmdModAttrValue.setNewAttrValEng(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValGer(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValDescEng(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValDescGer(pidcImportCompareResult.getValueOfImportedPIDC());
        }

        // Add child commands to command stack
        addToCommandStack(attrValue, pidcAttribute, cmdModAttrValue, pidcImportCompareResult);
      }
      else if (modifiedValidAttributes.get(attrID) instanceof PIDCAttributeVar) {
        PIDCAttributeVar pidcAttributeVar = (PIDCAttributeVar) modifiedValidAttributes.get(attrID);
        AttributeValue attrValue = null;
        if (pidcAttributeVar.getValueID() != null) {
          attrValue = this.apicDataProvider.getAttrValue(pidcAttributeVar.getValueID());
        }
        CmdModAttributeValue cmdModAttrValue = null;

        // PIDC comparison result map
        Map<Long, PIDCImportCompareResult> varCompResMap =
            this.pidcImportParsedResult.getPidcImportVariantCompareResultMaps().get(pidcAttributeVar.getVariantId());
        PIDCImportCompareResult pidcImportCompareResult = varCompResMap.get(attrID);

        // ICDM-1770
        for (Entry<Long, Map<Long, PIDCImportCompareResult>> allVarAttrValEntry : this.pidcImportParsedResult
            .getPidcImportVariantCompareResultMaps().entrySet()) {
          Long variantID = allVarAttrValEntry.getKey();
          PIDCImportCompareResult othrSVarCompRslt = allVarAttrValEntry.getValue().get(attrID);
          // ICDM-2530
          if ((null != othrSVarCompRslt) && !pidcAttributeVar.getVariantId().equals(variantID) &&
              othrSVarCompRslt.getValueOfImportedPIDC().equals(pidcImportCompareResult.getValueOfImportedPIDC())) {
            // Newly inserted value repeating in other
            // variants.Prevent checking itself
            Map<String, AttributeValue> newVarValMap = this.newAttrValuesMap.get(attrID);
            if (newVarValMap != null) {
              attrValue = newVarValMap.get(pidcImportCompareResult.getValueOfImportedPIDC());
              if (attrValue != null) {
                pidcAttributeVar.setValueID(attrValue.getID());
                pidcImportCompareResult.setIsNewValueAdded(false);
                break;
              }
            }
          }

        }


        if (pidcImportCompareResult.isNewValueAdded() && !pidcImportCompareResult.isInvalidModify()) {
          // Create an attribute value cmd
          cmdModAttrValue = new CmdModAttributeValue(this.apicDataProvider, this.apicDataProvider.getAttribute(attrID));
          cmdModAttrValue.setNewAttrValEng(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValGer(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValDescEng(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValDescGer(pidcImportCompareResult.getValueOfImportedPIDC());
        }

        addToCommandStack(attrValue, pidcAttributeVar, cmdModAttrValue, pidcImportCompareResult);

        // ICDM-1770
        if (cmdModAttrValue != null) {
          Map<String, AttributeValue> newValMap = this.newAttrValuesMap.get(attrID);
          if (newValMap == null) {
            newValMap = new HashMap<>();
            this.newAttrValuesMap.put(attrID, newValMap);
          }
          newValMap.put(pidcImportCompareResult.getValueOfImportedPIDC(), cmdModAttrValue.getAttrValue());
        }
      }
      else if (modifiedValidAttributes.get(attrID) instanceof PIDCAttributeSubVar) {
        PIDCAttributeSubVar pidcAttributeSubVar = (PIDCAttributeSubVar) modifiedValidAttributes.get(attrID);
        AttributeValue attrValue = null;
        if (pidcAttributeSubVar.getValueID() != null) {
          attrValue = this.apicDataProvider.getAttrValue(pidcAttributeSubVar.getValueID());
        }
        CmdModAttributeValue cmdModAttrValue = null;

        Map<Long, PIDCImportCompareResult> subVarCompResMap = this.pidcImportParsedResult
            .getPidcImportSubVariantCompareResultMaps().get(pidcAttributeSubVar.getSubVariantId());
        PIDCImportCompareResult pidcImportCompareResult = subVarCompResMap.get(attrID);

        // ICDM-1770
        for (Entry<Long, Map<Long, PIDCImportCompareResult>> allSubVarAttrValEntry : this.pidcImportParsedResult
            .getPidcImportSubVariantCompareResultMaps().entrySet()) {
          Long subVariantID = allSubVarAttrValEntry.getKey();
          PIDCImportCompareResult othrSVarCompRslt = allSubVarAttrValEntry.getValue().get(attrID);
          if (!pidcAttributeSubVar.getSubVariantId().equals(subVariantID) &&
              othrSVarCompRslt.getValueOfImportedPIDC().equals(pidcImportCompareResult.getValueOfImportedPIDC())) {// Newly
                                                                                                                   // inserted
                                                                                                                   // value
                                                                                                                   // repeating
                                                                                                                   // in
                                                                                                                   // other
                                                                                                                   // variants.Prevent
                                                                                                                   // checking
                                                                                                                   // itself
            Map<String, AttributeValue> newSubVarValMap = this.newAttrValuesMap.get(attrID);
            if (newSubVarValMap != null) {
              attrValue = newSubVarValMap.get(pidcImportCompareResult.getValueOfImportedPIDC());
              if (attrValue != null) {
                pidcAttributeSubVar.setValueID(attrValue.getID());
                pidcImportCompareResult.setIsNewValueAdded(false);
                break;
              }
            }
          }
        }


        if (pidcImportCompareResult.isNewValueAdded() && !pidcImportCompareResult.isInvalidModify()) {
          // Create an attribute value cmd
          cmdModAttrValue = new CmdModAttributeValue(this.apicDataProvider, this.apicDataProvider.getAttribute(attrID));
          cmdModAttrValue.setNewAttrValEng(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValGer(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValDescEng(pidcImportCompareResult.getValueOfImportedPIDC());
          cmdModAttrValue.setNewAttrValDescGer(pidcImportCompareResult.getValueOfImportedPIDC());
        }

        addToCommandStack(attrValue, pidcAttributeSubVar, cmdModAttrValue, pidcImportCompareResult);

        // ICDM-1770
        if (cmdModAttrValue != null) {
          Map<String, AttributeValue> newValMap = this.newAttrValuesMap.get(attrID);
          if (newValMap == null) {
            newValMap = new HashMap<>();
            this.newAttrValuesMap.put(attrID, newValMap);
          }
          newValMap.put(pidcImportCompareResult.getValueOfImportedPIDC(), cmdModAttrValue.getAttrValue());
        }
      }
    }
  }

  /**
   * adding the actions to be done to command stack
   *
   * @param attributeValue
   * @param editableAttr
   * @param cmdAttrVal
   * @param pidcImportCompareResult
   */
  private void addToCommandStack(final AttributeValue attributeValue, final IPIDCAttribute editableAttr,
      final CmdModAttributeValue cmdAttrVal, final PIDCImportCompareResult pidcImportCompareResult) {
    AbstractCmdModProjAttr cmdModProjAttr = null;
    if (editableAttr instanceof PIDCAttribute) {
      cmdModProjAttr = new CmdModProjectAttr(this.apicDataProvider, (PIDCAttribute) editableAttr);
      // if value is deleted in excel sheet it is made undefined in db
      // the used flag value combination check does not affect this
      if ((((PIDCAttribute) editableAttr).getValueID() == null) && (cmdAttrVal == null)) {
        cmdModProjAttr.setUsed(pidcImportCompareResult.getUsedFlagOfImportedPIDC());
        if (pidcImportCompareResult.getUsedFlagOfImportedPIDC()
            .equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) {
          cmdModProjAttr.setValue(null);
        }
      }
      else {
        cmdModProjAttr.setValue(attributeValue);
        cmdModProjAttr.setCmdAttrVal(cmdAttrVal);
      }
    }
    else if (editableAttr instanceof PIDCAttributeVar) {
      cmdModProjAttr = new CmdModProjVariantAttr(this.apicDataProvider, (PIDCAttributeVar) editableAttr);
      // if value is deleted in excel sheet it is made undefined in db
      // the used flag value combination check does not affect this
      if ((((PIDCAttributeVar) editableAttr).getValueID() == null) && (cmdAttrVal == null)) {
        cmdModProjAttr.setUsed(pidcImportCompareResult.getUsedFlagOfImportedPIDC());
        if (pidcImportCompareResult.getUsedFlagOfImportedPIDC()
            .equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) {
          cmdModProjAttr.setValue(null);
        }
      }
      else {
        cmdModProjAttr.setValue(attributeValue);
        cmdModProjAttr.setCmdAttrVal(cmdAttrVal);
      }
    }
    else if (editableAttr instanceof PIDCAttributeSubVar) {
      cmdModProjAttr = new CmdModProjSubVariantAttr(this.apicDataProvider, (PIDCAttributeSubVar) editableAttr);
      // if value is deleted in excel sheet it is made undefined in db
      // the used flag value combination check does not affect this
      if ((((PIDCAttributeSubVar) editableAttr).getValueID() == null) && (cmdAttrVal == null)) {
        cmdModProjAttr.setUsed(pidcImportCompareResult.getUsedFlagOfImportedPIDC());
        if (pidcImportCompareResult.getUsedFlagOfImportedPIDC()
            .equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) {
          cmdModProjAttr.setValue(null);
        }
      }
      else {
        cmdModProjAttr.setValue(attributeValue);
        cmdModProjAttr.setCmdAttrVal(cmdAttrVal);
      }
    }

    if (cmdModProjAttr != null) {
      setAdditionalInfo(cmdModProjAttr, pidcImportCompareResult);

      try {
        // Next commands project attribute are executed even if this command fails.

        this.childCmdStack.addCommand(cmdModProjAttr);
      }
      catch (Exception exp) {
        getDataProvider().getLogger().warn(exp.getMessage(), exp);
        cmdModProjAttr.rollBackDataModel();
      }

      if (cmdModProjAttr.getErrorCause() == ERROR_CAUSE.NONE) {
        pidcImportCompareResult.setImportStatus("SUCCESS");
        // iCDM-834
        CmdModAttributeValue cmdAttrValue = cmdModProjAttr.getCmdAttrVal();
        if ((cmdAttrValue != null) && cmdAttrValue.isClearanceRequired()) {
          this.attrVal4Clearance.add(cmdAttrValue);
        }
      }
      else {
        // Stop filling child when command fails
        pidcImportCompareResult.setImportStatus("FAILED");
      }
    }


  }

  /**
   * setting the part number,specification link and description of the attribute
   *
   * @param cmdModProjAttr
   */
  private void setAdditionalInfo(final AbstractCmdModProjAttr cmdModProjAttr,
      final PIDCImportCompareResult pidcImportCompareResult) {
    // setting the part number,specification link and description of the attribute
    String partNumberOfExistingPIDC = pidcImportCompareResult.getPartNumberOfExistingPIDC();
    String partNumberOfImportedPIDC = pidcImportCompareResult.getPartNumberOfImportedPIDC();

    // Check for differences in the additional information
    boolean isPartNumberDifferent = isAdditionalInfoDataDifferent(partNumberOfExistingPIDC, partNumberOfImportedPIDC);
    if (isPartNumberDifferent) {
      cmdModProjAttr.setNewPartNumber(partNumberOfImportedPIDC);
    }

    String specLinkOfExistingPIDC = pidcImportCompareResult.getSpecLinkOfExistingPIDC();
    String specLinkOfImportedPIDC = pidcImportCompareResult.getSpecLinkOfImportedPIDC();
    boolean isSpecLinkDifferent = isAdditionalInfoDataDifferent(specLinkOfExistingPIDC, specLinkOfImportedPIDC);
    if (isSpecLinkDifferent) {
      cmdModProjAttr.setNewSpecLink(specLinkOfImportedPIDC);
    }

    String descriptionOfExistingPIDC = pidcImportCompareResult.getDescriptionOfExistingPIDC();
    String descriptionOfImportedPIDC = pidcImportCompareResult.getDescriptionOfImportedPIDC();
    boolean isDescDifferent = isAdditionalInfoDataDifferent(descriptionOfExistingPIDC, descriptionOfImportedPIDC);
    if (isDescDifferent) {
      cmdModProjAttr.setNewDesc(descriptionOfImportedPIDC);
    }
  }


  /**
   * @param cmdModProjAttr
   * @param existngPIDCInfo
   * @param importedPIDCInfo
   * @param isPartNumberDifferent
   * @return true if the additional information is modified during the import
   */
  private boolean isAdditionalInfoDataDifferent(final String existngPIDCInfo, final String importedPIDCInfo) {
    boolean isPartNumberDifferent = false;
    if (((existngPIDCInfo == null) && (importedPIDCInfo != null)) || (existngPIDCInfo != null)) {
      if ((existngPIDCInfo != null) && (importedPIDCInfo != null)) {
        if (!existngPIDCInfo.equals(importedPIDCInfo)) {
          isPartNumberDifferent = true;
        }
      }
      else {
        isPartNumberDifferent = true;
      }
    }
    return isPartNumberDifferent;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeUpdateCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void executeDeleteCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoInsertCommand() throws CommandException {
    // UNDO Import PIDC
    this.childCmdStack.undoAll();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoUpdateCommand() throws CommandException {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void undoDeleteCommand() {
    // Not applicable

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() {
    // Not applicable
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getString() {


    return super.getString("", getPrimaryObjectIdentifier());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() {
    // iCDM-834
    sendAttrValues4Clearance();
  }

  /**
   * Check and send hotline mail request new attr values which require clearance
   */
  private void sendAttrValues4Clearance() {
    HashMap<String, String> nameValMap = new HashMap<String, String>();
    // ICDM-2624
    Boolean isGroupedAttr = false;
    for (CmdModAttributeValue cmdAttrVal : this.attrVal4Clearance) {
      if ((cmdAttrVal != null) && cmdAttrVal.isClearanceRequired()) {
        AttributeValue attrValue = cmdAttrVal.getAttrValue();
        nameValMap.put(attrValue.getAttribute().getAttributeName(), attrValue.getValue());
        // ICDM-2624
        isGroupedAttr = attrValue.getAttribute().isGrouped();
      }
    }
    // /Send mail only if there are values which require clearance
    if (!nameValMap.isEmpty()) {
      // get hotline notifier and appropriate SUBJECT from table
      MailHotline mailHotline = getHotlineNotifier();
      String subject = getDataProvider().getParameterValue(ApicConstants.ICDM_HOTLINE_SUBJECT);
      mailHotline.setSubject(subject);
      // ICDM-2624
      mailHotline.send4Clearance(nameValMap, isGroupedAttr);
    }
  }

  /**
   * {@inheritDoc} icdm-177
   */
  @Override
  protected void rollBackDataModel() {
    this.childCmdStack.rollbackAll(getExecutionMode());
  }

  /**
   * {@inheritDoc} return the id of the PIDC that is being imported
   */
  @Override
  public Long getPrimaryObjectID() {
    // Since this command involves operation on multiple objects, the parent object's(PIDC) id is returned as the
    // primary object id
    if (this.pidcImportParsedResult != null) {
      return this.pidcImportParsedResult.getPidcVersion() == null ? null
          : this.pidcImportParsedResult.getPidcVersion().getID();
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectType() {
    return "PIDC Import";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public TransactionSummary getTransactionSummary() {

    final SortedSet<TransactionSummaryDetails> detailsList = new TreeSet<TransactionSummaryDetails>();
    switch (this.commandMode) {
      // Insert command
      case INSERT:
        caseCmdIns(detailsList);
        break;
      default:
        // Do nothing
        break;
    }
    // set the details to summary data
    this.summaryData.setTrnDetails(detailsList);
    // return the filled summary data
    return super.getTransactionSummary(this.summaryData);
  }

  /**
   * @param detailsList
   */
  private void caseCmdIns(final SortedSet<TransactionSummaryDetails> detailsList) {
    final TransactionSummaryDetails details;
    details = new TransactionSummaryDetails();
    details.setOldValue("");
    details.setNewValue(getPrimaryObjectIdentifier());
    details.setModifiedItem(getPrimaryObjectType());
    detailsList.add(details);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {

    String objectIdentifier;
    switch (this.commandMode) {
      case INSERT:
        objectIdentifier = "PIDC imported from excel";
        break;
      default:
        objectIdentifier = " INVALID!";
        break;
    }
    return objectIdentifier;
  }

}
