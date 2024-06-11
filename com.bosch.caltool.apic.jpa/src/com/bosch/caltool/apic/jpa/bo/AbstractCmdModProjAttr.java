/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.ChildCommandStack;
import com.bosch.caltool.dmframework.bo.CommandException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;


/**
 * Abstract class as base for all commands regarding project attributes
 *
 * @author bne4cob
 */
public abstract class AbstractCmdModProjAttr extends AbstractCommand {

  /**
   * The PIDC Attribute that is to be modified.
   */
  protected transient IPIDCAttribute modifyPidcAttr;

  /**
   * Used for displaying the log in the getString method.
   */
  private final transient String attributeName;

  /**
   * Previous value of the used flag.
   */
  private transient String oldUsed;

  /**
   * New value of the used flag.
   */
  private transient String newUsed;

  /**
   * Previous attribute value.
   */
  private transient AttributeValue oldValue;

  /**
   * New attribute value.
   */
  private transient AttributeValue newValue;
  /**
   * Previous part number.
   */
  private transient String oldPartNumber;

  /**
   * New PartNumber.
   */
  private transient String newPartNumber;
  /**
   * Previous spec.
   */
  private transient String oldSpecLink;

  /**
   * New spec.
   */
  private transient String newSpecLink;
  /**
   * New desc.
   */
  private transient String newDesc;
  /**
   * Previous desc.
   */
  private transient String oldDesc;


  /**
   * the old isVariant flag
   */
  private boolean oldIsVariant;

  /**
   * the new isVariant flag
   */
  private boolean newIsVariant;
  /**
   * Previous value of the hidden flag.
   */
  private transient String oldHiddenFlag;

  /**
   * New value of the hidden flag.
   */
  private transient String newHiddenFlag;
  /**
   * Previous value of the transfer to vcdm flag.
   */
  private transient String oldTrnfrVcdmFlag;

  /**
   * New value of the transfer to vcdm flag.
   */
  private transient String newTrnfrVcdmFlag;
  /**
   * Previous value of the transfer to vcdm flag.
   */
  private transient String oldFMRelevantFlag;

  /**
   * New value of the transfer to vcdm flag.
   */
  private transient String newFMRelevantFlag;
  /**
   * CmdModAttributeValue instance
   */
  // ICDM-108
  protected CmdModAttributeValue cmdAttrVal;

  /**
   * pro Rev Id
   */
  private long proRevId;

  /**
   * Project attribute's entity ID
   */
  protected static final String PATTR_ENTITY_ID = "PATTR_ENTITY_ID";

  /**
   * Child commands
   */
  protected final ChildCommandStack childCmdStack = new ChildCommandStack(this);

  /**
   * Constructor of this command. Should be used for insert and update actions.
   *
   * @param apicDataProvider the APIC data provider
   * @param pidcAttribute original PIDC attribute
   */
  public AbstractCmdModProjAttr(final ApicDataProvider apicDataProvider, final IPIDCAttribute pidcAttribute) {

    super(apicDataProvider);

    this.modifyPidcAttr = pidcAttribute;

    this.attributeName = pidcAttribute.getAttribute().getAttributeName();
    this.proRevId = pidcAttribute.getPidcVersion().getPidcRevision();

    if (pidcAttribute.getID() == null) {
      this.commandMode = COMMAND_MODE.INSERT;
    }
    else {
      this.commandMode = COMMAND_MODE.UPDATE;

      this.oldUsed = pidcAttribute.getIsUsed();
      this.newUsed = this.oldUsed;

      this.oldValue = pidcAttribute.getAttributeValue();
      this.newValue = this.oldValue;

      this.oldPartNumber = pidcAttribute.getPartNumber();
      this.newPartNumber = this.oldPartNumber;

      this.oldSpecLink = pidcAttribute.getSpecLink();
      this.newSpecLink = this.oldSpecLink;

      this.oldDesc = pidcAttribute.getAdditionalInfoDesc();
      this.newDesc = this.oldDesc;

      this.oldIsVariant = pidcAttribute.isVariant();
      this.newIsVariant = this.oldIsVariant;

      this.oldTrnfrVcdmFlag = pidcAttribute.canTransferToVcdm() ? ApicConstants.YES : null;
      this.newTrnfrVcdmFlag = this.oldTrnfrVcdmFlag;

      this.oldFMRelevantFlag = pidcAttribute.isFocusMatrixApplicable() ? ApicConstants.YES : null;
      this.newFMRelevantFlag = this.oldFMRelevantFlag;
    }

  }

  /**
   * Set the isVariant flag
   *
   * @param isVariant is variant
   */
  protected void setIsVariant(final boolean isVariant) {
    this.newIsVariant = isVariant;
  }

  /**
   * Get the String of the new isVariant flag
   *
   * @return the String to be stored in the database
   */
  protected String getNewIsVariant() {

    if (this.newIsVariant) {
      return ApicConstants.YES;
    }

    return ApicConstants.CODE_NO;

  }

  /**
   * Get the String of the old isVariant flag
   *
   * @return the String to be stored in the database
   */
  protected String getOldIsVariant() {

    if (this.oldIsVariant) {
      return ApicConstants.YES;
    }
    return ApicConstants.CODE_NO;

  }

  /**
   * @return the oldUsed
   */
  protected final String getOldUsed() {
    return this.oldUsed;
  }


  /**
   * @return the newUsed
   */
  protected final String getNewUsed() {
    return this.newUsed;
  }


  /**
   * @param newUsed new used flag
   */
  protected final void setNewUsed(final String newUsed) {
    this.newUsed = newUsed;
  }

  /**
   * @return the oldValue
   */
  protected final AttributeValue getOldValue() {
    return this.oldValue;
  }

  /**
   * @return the newValue
   */
  protected final AttributeValue getNewValue() {
    return this.newValue;
  }

  /**
   * Sets the used flag.
   *
   * @param used the used flag. Should be one of the following : - ApicConstants.USED_YES_DISPLAY,
   *          ApicConstants.USED_NO_DISPLAY, ApicConstants.NOT_DEFINED
   */
  public final void setUsed(final String used) {
    this.newUsed = used;

    // ICDM-92 If the used flag is YES and a value is defined and the user sets the used flag to NO or ???, the value
    // needs to be cleared (set to NULL)
    if ((used.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType())) ||
        (used.equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()))) {
      this.newValue = null;
    }
    // ICDM-92 If the used flag is changed to YES and the attribute has the datatype BOOLEAN, the value TRUE needs to be
    // set automatically
    else if ((used.equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType())) && (this.oldValue == null) &&
        (this.newValue == null)) {
      final Attribute attr = this.modifyPidcAttr.getAttribute();
      if (AttributeValueType.BOOLEAN == attr.getValueType()) {
        this.newValue = getBooleanTrueValue(attr);
      }
    }

  }

  /**
   * Gets the 'true' value of this attribute, if it is of type boolean. If true value is not defined for this attribute,
   * null is returned. <br>
   * Note : Only boolean type attributes should be passed to this method.
   *
   * @param attribute the attribute
   * @return the true value
   */
  private AttributeValue getBooleanTrueValue(final Attribute attribute) { // ICDM-92
    for (AttributeValue value : attribute.getAttrValues()) {
      if (ApicConstants.BOOLEAN_TRUE_STRING.equals(value.getValue())) {
        return value;
      }
    }
    return null;

  }

  /**
   * Sets the value of this PIDC attribute.
   *
   * @param value the value to set
   */
  public final void setValue(final AttributeValue value) {
    this.newValue = value;

    // ICDM-92 If value is set, then explicitly set the used flag as yes.
    if ((value != null) && (value.getValueID() != AttributeValueDummy.VALUE_ID)) {
      this.newUsed = ApicConstants.USED_YES_DISPLAY;
    }
  }


  /**
   * @param oldUsed the oldUsed to set
   */
  protected final void setOldUsed(final String oldUsed) {
    this.oldUsed = oldUsed;
  }


  /**
   * @param oldValue the oldValue to set
   */
  protected final void setOldValue(final AttributeValue oldValue) {
    this.oldValue = oldValue;
  }


  /**
   * @param newValue the newValue to set
   */
  protected final void setNewValue(final AttributeValue newValue) {
    this.newValue = newValue;
  }

  /**
   * {@inheritDoc} returns true if used flag,part number,specification link,description,isVariant flag or value is<br>
   * changed
   */
  @Override
  protected final boolean dataChanged() {
    if (isStringChanged(this.oldUsed, this.newUsed)) {
      return true;
    }
    if (isStringChanged(this.oldHiddenFlag, this.newHiddenFlag)) {
      return true;
    }
    if (isStringChanged(this.oldTrnfrVcdmFlag, this.newTrnfrVcdmFlag)) {
      return true;
    }
    if (isStringChanged(this.oldFMRelevantFlag, this.newFMRelevantFlag)) {
      return true;
    }
    // ICDM-260
    if (isStringChanged(this.oldPartNumber, this.newPartNumber)) {
      return true;
    }
    if (isStringChanged(this.oldSpecLink, this.newSpecLink)) {
      return true;
    }
    if (isStringChanged(this.oldDesc, this.newDesc)) {
      return true;
    }
    if (this.newIsVariant != this.oldIsVariant) {
      return true;
    }

    final long oldValueID = this.oldValue == null ? -1 : this.oldValue.getValueID();
    final long newValueID = this.newValue == null ? -1 : this.newValue.getValueID();
    // ICDM-108
    if (oldValueID != newValueID) {
      return true;
    }

    if (this.cmdAttrVal != null) {
      return true;
    }

    return false;

  }

  /**
   * Get the used flag value in the DB for the given UI flag value.
   *
   * @param modelUsedFlag UI flag value
   * @return DB flag value
   */
  protected final String getDbUsedFlag(final String modelUsedFlag) {
    if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(modelUsedFlag)) {
      return ApicConstants.PROJ_ATTR_USED_FLAG.YES.getDbType();
    }
    if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType().equals(modelUsedFlag)) {
      return ApicConstants.CODE_NO;
    }
    if (ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType().equals(modelUsedFlag)) {
      return ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType();
    }

    return null;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getString() {
    return super.getString("", getPrimaryObjectIdentifier() + " on " + this.modifyPidcAttr.getPidcVersion().getName());

  }

  /**
   * Returns the DB value corresponding to the data model attribute value.
   *
   * @param value the data model value
   * @return the DB value
   */
  protected final TabvAttrValue getDbValue(final AttributeValue value) {
    TabvAttrValue dbValue = null;
    if ((value != null) && (value.getValueID().longValue() != 0L)) {
      dbValue = getEntityProvider().getDbValue(value.getValueID());
    }
    return dbValue;
  }

  /**
   * @param cmdAttrVal the cmdAttrVal to set
   */
  // ICDM-108
  public void setCmdAttrVal(final CmdModAttributeValue cmdAttrVal) {
    this.cmdAttrVal = cmdAttrVal;
  }

  /**
   * iCDM-834
   *
   * @return CmdModAttributeValue
   */
  public CmdModAttributeValue getCmdAttrVal() {
    return this.cmdAttrVal;
  }


  /**
   * @return the project revision ID
   */
  protected final BigDecimal getProRevId() {
    return new BigDecimal(this.proRevId);
  }

  /**
   * @param proRevId the new Project Revision ID
   */
  protected final void setProRevId(final long proRevId) {
    this.proRevId = proRevId;
  }


  /**
   * @return the oldPartNumber
   */
  public String getOldPartNumber() {
    return this.oldPartNumber;
  }


  /**
   * @param oldPartNumber the oldPartNumber to set
   */
  public void setOldPartNumber(final String oldPartNumber) {
    this.oldPartNumber = oldPartNumber;
  }


  /**
   * @return the newPartNumber
   */
  public String getNewPartNumber() {
    return this.newPartNumber;
  }


  /**
   * @param newPartNumber the newPartNumber to set
   */
  public void setNewPartNumber(final String newPartNumber) {
    this.newPartNumber = newPartNumber;
  }


  /**
   * @return the oldSpecLink
   */
  public String getOldSpecLink() {
    return this.oldSpecLink;
  }


  /**
   * @param oldSpecLink the oldSpecLink to set
   */
  public void setOldSpecLink(final String oldSpecLink) {
    this.oldSpecLink = oldSpecLink;
  }


  /**
   * @return the newSpecLink
   */
  public String getNewSpecLink() {
    return this.newSpecLink;
  }


  /**
   * @param newSpecLink the newSpecLink to set
   */
  public void setNewSpecLink(final String newSpecLink) {
    this.newSpecLink = newSpecLink;
  }


  /**
   * @return the newDesc
   */
  public String getNewDesc() {
    return this.newDesc;
  }


  /**
   * @param newDesc the newDesc to set
   */
  public void setNewDesc(final String newDesc) {
    this.newDesc = newDesc;
  }


  /**
   * @return the oldDesc
   */
  public String getOldDesc() {
    return this.oldDesc;
  }


  /**
   * @param oldDesc the oldDesc to set
   */
  public void setOldDesc(final String oldDesc) {
    this.oldDesc = oldDesc;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getPrimaryObjectID() {
    if (this.modifyPidcAttr.getID() == null) {
      return this.modifyPidcAttr.getPidcVersion().getID();
    }
    return this.modifyPidcAttr.getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getPrimaryObjectIdentifier() {
    String objectIdentifier = " INVALID!";

    switch (this.commandMode) {
      case INSERT:
      case UPDATE:
      case DELETE:
        objectIdentifier = this.attributeName;
        break;

      default:
        break;
    }
    return objectIdentifier;
  }

  /**
   * ICDM-1583, Deleting the unmapped A2L File (PIDC level - Changing the pver name) <br>
   * 1. checking attribute level to be SDOM PVER <br>
   * 2. getting old pver and new pver name <br>
   * 3. getting pidc_a2l for both mapped and unmapped a2l files<br>
   * 4. adding pidc_a2l having version as null to a pidcA2lList i.e unmapped a2l files<br>
   * 5. checking if old pver name not null and pidcA2lList not empty<br>
   *
   * @throws CommandException
   */
  protected void changeInPidcA2L() throws CommandException {
    if (this.modifyPidcAttr.getAttribute().getAttrLevel() == ApicConstants.SDOM_PROJECT_NAME_ATTR) {
      String oldPverName = null;
      if (null != getOldValue()) {
        oldPverName = getOldValue().getValue();
      }
      Map<Long, PIDCA2l> definedPidcA2lMap = this.modifyPidcAttr.getPidcVersion().getPidc().getDefinedPidcA2lMap();
      List<PIDCA2l> pidcA2lList = new ArrayList<PIDCA2l>();
      for (PIDCA2l pidcA2l : definedPidcA2lMap.values()) {
        if ((pidcA2l.getPidcVersion() == null) && pidcA2l.getSdomPverName().equals(oldPverName)) { // extracting
                                                                                                   // only unmapped
                                                                                                   // pidc_a2l
          pidcA2lList.add(pidcA2l);
        }
      }
      if ((null != oldPverName) && CommonUtils.isNotEmpty(pidcA2lList)) {
        String newPverName = null;
        if (null != getNewValue()) {
          newPverName = getNewValue().getValue();
        }
        // new method to resolve sonarqube issue (not more than 10 if/for/switch statement to be used
        changeInActualPidcA2L(oldPverName, newPverName, pidcA2lList);

      }
    }
  }

  /**
   * 1. checking not null for old pver and old pver not be equal to new pver<br>
   * 2. getting pver set of all version <br>
   * 3. Iterating pver set from point-2 and checking if old pver is available<br>
   * 4. If not available, delete unmapped a2l files from pidcA2lList<br>
   *
   * @param oldPverName
   * @param newPverName
   * @param
   */
  private void changeInActualPidcA2L(final String oldPverName, final String newPverName,
      final List<PIDCA2l> pidcA2lList) throws CommandException {
    if (!oldPverName.equals(newPverName)) {
      SortedSet<PIDCVersion> pidcVers = this.modifyPidcAttr.getPidcVersion().getPidc().getAllVersions(); // get the sdom
                                                                                                         // pver of all
                                                                                                         // version of
                                                                                                         // all
      SortedSet<SdomPver> allPVerSet = new TreeSet<SdomPver>();
      for (PIDCVersion pidcVersion : pidcVers) {
        if (!pidcVersion.getPVerSet().isEmpty()) {
          allPVerSet.addAll(pidcVersion.getPVerSet());
        }
      }
      boolean isPverAvailable = false;
      for (SdomPver sdomPver : allPVerSet) {
        if (CommonUtils.isEqualIgnoreCase(sdomPver.getPVERName(), oldPverName)) { // checking old pver has referred
          // in same version
          isPverAvailable = true;
          break;
        }
      }
      if (!isPverAvailable) { // if old pver not available in existing pver set, delete the pver in T_PIDC_A2L
        for (PIDCA2l pidcA2l : pidcA2lList) {
          CmdModPidcA2l cmdModPidcA2l = new CmdModPidcA2l(getDataProvider(), pidcA2l, true, true);
          this.childCmdStack.addCommand(cmdModPidcA2l); // actual deletion
        }
      }
    }
  }


  /**
   * @return the oldHiddenFlag
   */
  public String getOldHiddenFlag() {
    return this.oldHiddenFlag;
  }


  /**
   * @param oldHiddenFlag the oldHiddenFlag to set
   */
  public void setOldHiddenFlag(final String oldHiddenFlag) {
    this.oldHiddenFlag = oldHiddenFlag;
  }


  /**
   * @return the newHiddenFlag
   */
  public String getNewHiddenFlag() {
    return this.newHiddenFlag;
  }


  /**
   * @param newHiddenFlag the newHiddenFlag to set
   */
  public void setNewHiddenFlag(final String newHiddenFlag) {
    this.newHiddenFlag = newHiddenFlag;
  }


  /**
   * @return the oldTrnfrVcdmFlag
   */
  public String getOldTrnfrVcdmFlag() {
    return this.oldTrnfrVcdmFlag;
  }


  /**
   * @param oldTrnfrVcdmFlag the oldTrnfrVcdmFlag to set
   */
  public void setOldTrnfrVcdmFlag(final String oldTrnfrVcdmFlag) {
    this.oldTrnfrVcdmFlag = oldTrnfrVcdmFlag;
  }


  /**
   * @return the newTrnfrVcdmFlag
   */
  public String getNewTrnfrVcdmFlag() {
    return this.newTrnfrVcdmFlag;
  }


  /**
   * @param newTrnfrVcdmFlag the newTrnfrVcdmFlag to set
   */
  public void setNewTrnfrVcdmFlag(final String newTrnfrVcdmFlag) {
    this.newTrnfrVcdmFlag = newTrnfrVcdmFlag;
  }


  /**
   * @return the oldFMRelevantFlag
   */
  public String getOldFMRelevantFlag() {
    return this.oldFMRelevantFlag;
  }


  /**
   * @param oldFMRelevantFlag the oldFMRelevantFlag to set
   */
  public void setOldFMRelevantFlag(final String oldFMRelevantFlag) {
    this.oldFMRelevantFlag = oldFMRelevantFlag;
  }


  /**
   * @return the newFMRelevantFlag
   */
  public String getNewFMRelevantFlag() {
    return this.newFMRelevantFlag;
  }


  /**
   * @param newFMRelevantFlag the newFMRelevantFlag to set
   */
  public void setNewFMRelevantFlag(final String newFMRelevantFlag) {
    this.newFMRelevantFlag = newFMRelevantFlag;
  }

  /**
   * If a value of an attribute changes in the PIDC, the color in the FM must be reseted. The comment can be kept.
   */
  protected void resetFocusMatrixDetails() {

    try {
      final PIDCVersion pidcVersion = this.modifyPidcAttr.getPidcVersion();
      ConcurrentMap<Long, FocusMatrixDetails> fmDetailsMap = pidcVersion.getFocusMatrixWorkingSetVersion()
          .getFocusMatrixItemMap().get(this.modifyPidcAttr.getAttribute().getID());
      ArrayList<FocusMatrixDetails> listFm = new ArrayList<>();
      if ((null != fmDetailsMap) && !fmDetailsMap.isEmpty()) {
        listFm.addAll(fmDetailsMap.values());

        CmdModMultipleFocusMatrix cmdModMultipleFocusMatrix = new CmdModMultipleFocusMatrix(getDataProvider(),
            CmdModMultipleFocusMatrix.UPDATE_MODE.FM_DETAILS_UPDATE, listFm);
        // the color in the FM is reset
        cmdModMultipleFocusMatrix.setColorCode(FocusMatrixColorCode.NOT_DEFINED.getColor());
        cmdModMultipleFocusMatrix.setLink("");

        this.childCmdStack.addCommand(cmdModMultipleFocusMatrix);
        // reset the review details
        CmdModFocusMatrixVersion cmd = new CmdModFocusMatrixVersion(pidcVersion.getFocusMatrixWorkingSetVersion());
        cmd.setReviewStatus(FocusMatrixVersion.FM_REVIEW_STATUS.NO);
        // When Review flag is set to NO, clear Review Date and Review User and other fields
        cmd.setReviewedBy(null);
        cmd.setReviewedOn(null);
        cmd.setLink(null);
        cmd.setRemarks(null);

        this.childCmdStack.addCommand(cmd);
      }
    }
    catch (CommandException e) {
      getDataProvider().getLogger().error(e.getMessage(), e);
    }
  }

}
