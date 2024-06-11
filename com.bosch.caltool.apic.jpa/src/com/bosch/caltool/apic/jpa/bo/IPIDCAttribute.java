package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;

/**
 * This is an abstract class to have a common interface for attribute values used in PIDC, PIDC-Variants or
 * PIDC-Sub-Variants
 *
 * @author hef2fe
 */
@Deprecated
public abstract class IPIDCAttribute extends ApicObject implements Comparable<IPIDCAttribute> {

  /**
   * the ID of the attribute
   */
  protected Long attrID;

  /**
   * the ID of the PIDC
   */
  protected Long pidcVersID;

  /**
   * Part number. Only for PIDC import
   */
  protected String partNumber;

  /**
   * Spec link. Only for PIDC import
   */
  protected String specLink;

  /**
   * Additional Info desc . Only for PIDC import
   */
  protected String additionalInfoDesc;

  /**
   * Hash code for hashcode() implementation
   */
  private static final int HASHCODE_PRIME = 31;

  /**
   * constructor if the attribute is defined for this PIDC in TabV_xxx_ATTR
   *
   * @param apicDataProvider DataProvider
   * @param pidcAttrID the ID in TabV_xxx_ATTR
   */
  public IPIDCAttribute(final ApicDataProvider apicDataProvider, final Long pidcAttrID) {

    super(apicDataProvider, pidcAttrID);

    // get the ID of the attribute to avoid null pointer exceptions
    this.pidcVersID = getPidcVersion().getID();

    // get the ID of the PIDC to avoid null pointer exceptions
    this.attrID = getAttribute().getAttributeID();
  }

  /**
   * Constructor if the attribute has no value defined in TabV_xxx_ATTR for an attribute.
   *
   * @param apicDataProvider DataProvider
   * @param pidcAttrID always NULL, because not existing
   * @param attrID the ID of the attribute
   * @param pidcID the ID of the PIDC
   */
  public IPIDCAttribute(final ApicDataProvider apicDataProvider, final Long pidcAttrID, final Long attrID,
      final Long pidcID) {

    // the pidcAttrID will be NULL in this case because the attribute is not available in
    // TabV_xxx_ATTR
    super(apicDataProvider, pidcAttrID);

    // the ID of the attribute
    this.attrID = attrID;

    // the ID of the PIDC
    this.pidcVersID = pidcID;
  }

  /**
   * @return Get PidcAttrID
   */
  public Long getPidcAttrID() {
    return getID();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public final String getName() {
    // project attribute's attribute name
    return getAttribute().getName();
  }

  /**
   * {@inheritDoc}
   */
  // iCDM-2094
  @Override
  public final String getDescription() {
    return getAttribute().getDescription();
  }

  // ICDM-367
  @Override
  public boolean isModifiable() {
    boolean isModifiable = false;
    NodeAccessRight curUserAccRight = getPidcVersion().getPidc().getCurrentUserAccessRights();
    // the attribute can be modified if the user can modify the PIDC
    // ICDM-2354
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess() &&
        getPidcVersion().getPidc().isUnlockedInSession()) {
      // structure attributes can not be modified
      isModifiable = !((getAttribute().getAttrLevel() > 0) || getPidcVersion().isDeleted() ||
          (getPidcVersion().getStatus() == PidcVersionStatus.LOCKED));
    }
    return isModifiable;
  }

  /**
   * This method returns the corresponding error message when this attribute is not modifiable
   *
   * @return Error condition describing why it is not modifiable
   */
  public String isModifiableWithError() {
    StringBuilder errorMsg = new StringBuilder();
    NodeAccessRight curUserAccRight = getPidcVersion().getPidc().getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess()) {
      if (getAttribute().getAttrLevel() > 0) {
        errorMsg.append("Structure Attribute cannot be modified;");
      }
      if (getPidcVersion().isDeleted()) {
        errorMsg.append("Deleted Attribute cannot be modified;");
      }
      if (getPidcVersion().getStatus() == PidcVersionStatus.LOCKED) {
        errorMsg.append("Attribute for a locked PIDC cannot be modified;");
      }
    }
    else {
      errorMsg.append("Insufficient Privilege");
    }
    return errorMsg.toString();
  }

  /**
   * ICDM-479 checks whether the attribute is visible in pidcard
   *
   * @return isvisible
   */
  abstract public boolean isVisible();


  /**
   * Get the database entity of the attribute
   *
   * @return TabvAttribute
   */
  abstract protected TabvAttribute getDbAttribute();

  /**
   * Get the database entity of the attribute value
   *
   * @return TabvAttrValue
   */
  abstract protected TabvAttrValue getDbAttrValue();

  /**
   * Get the used flag
   *
   * @return The used flag as a String
   */
  abstract protected String getUsedInfo();

  /**
   * Get the revision of the PIDC
   *
   * @return the revision number of the PIDC
   */
  abstract protected Long getProRevId();

  /**
   * Get the PIDC using this attribute value
   *
   * @return the PIDC
   */
  abstract public PIDCVersion getPidcVersion();

  /**
   * @return the displayed value of this project attribute as string
   */
  public final String getDefaultValueDisplayName() {
    return getDefaultValueDisplayName(false);
  }

  /**
   * @param showUnit shows unit for numeric values, if set to <code>true</code>
   * @return the displayed value of this project attribute as string
   */
  public abstract String getDefaultValueDisplayName(boolean showUnit);

  /**
   * @param showUnit show unit, for numeric values. Appends unit at the end
   * @return the display value from attribute
   */
  protected final String getDisplayValueFromAttribute(final boolean showUnit) {
    if (getAttributeValue() == null) {
      return "";
    }
    return getAttributeValue().getValue(showUnit);
  }

  /**
   * @return the description of the attribute value set
   */
  // iCDM-2094
  public String getValueDescription() {
    if (isVariant()) {
      return "";
    }
    String desc = "";
    if (isHiddenToUser()) {
      desc = ApicConstants.HIDDEN_VALUE;
    }
    else {
      if (ApicConstants.USED_YES_DISPLAY.equals(getIsUsed())) {
        AttributeValue val = getAttributeValue();
        if (val != null) {
          desc = val.getDescription();
        }
      }
    }

    return desc;
  }

  /**
   * @return Attribute object
   */
  public Attribute getAttribute() {
    if (getID() == null) {
      return getDataCache().getAttribute(this.attrID);
    }
    return getDataCache().getAttribute(getDbAttribute().getAttrId());
  }

  /**
   * @return Attribute Value object
   */
  public AttributeValue getAttributeValue() {
    if (getID() == null) {
      return null;
    }
    if (getDbAttrValue() != null) {
      return getDataCache().getAttrValue(getDbAttrValue().getValueId());
    }
    return null;
  }

  /**
   * @return revision id
   */
  public Long getRevisionID() {
    if (getID() == null) {
      return null;
    }
    return getProRevId();
  }

  /**
   * @return true, if the actual definition is at the level below
   */
  public abstract boolean isVariant();

  /**
   * @return String value based on whether it is used,defined
   */
  public String getIsUsed() {

    if (isHidden() && !isReadable()) {
      return "";
    }
    String returnValue;
    // ICDM-179
    if (isVariant()) {
      // variant and sub-variant attributes are always "used"
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType();
    }
    else if (getIsUsedEnum() != null) {
      returnValue = getIsUsedEnum().getUiType();
    }
    else {
      returnValue = ApicConstants.USED_INVALID_DISPLAY;
    }

    return returnValue;
  }

  /**
   * ICDM-2227
   *
   * @return ApicConstants.PROJ_ATTR_USED_FLAG
   */
  public ApicConstants.PROJ_ATTR_USED_FLAG getIsUsedEnum() {
    PROJ_ATTR_USED_FLAG returnValue;
    if (getID() == null) {
      // ICDM-2227
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR;
    }
    else {
      String dbUsedInfo = getUsedInfo();
      returnValue = ApicConstants.PROJ_ATTR_USED_FLAG.getType(dbUsedInfo);
    }
    return returnValue;
  }


  /**
   * iCDM-1240 <br>
   * Gets the tooltip for the USED flag, based on the used flag
   *
   * @return toolTip text
   */
  public String getUsedToolTip() {
    if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equalsIgnoreCase(getIsUsed())) {
      return ApicConstants.TOOLTIP_USED_YES;
    }
    else if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType().equalsIgnoreCase(getIsUsed())) {
      return ApicConstants.TOOLTIP_USED_NO;
    }
    else if (ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType().equalsIgnoreCase(getIsUsed())) {
      return ApicConstants.TOOLTIP_USED_NOT_DEFINED;
    }
    else {
      return ApicConstants.TOOLTIP_USED_FLAG;
    }
  }

  /**
   * {@inheritDoc} return compare result of two PIDC attributes
   */
  @Override
  public int compareTo(final IPIDCAttribute pidcAttribute2) {
    return ApicUtil.compare(getAttribute().getAttributeName(), pidcAttribute2.getAttribute().getAttributeName());
  }

  // ICDM-1023
  @Override
  public boolean equals(final Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    IPIDCAttribute other = (IPIDCAttribute) obj;
    if (getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    }
    else if (!getName().equals(other.getName())) {
      return false;
    }
    else {
      if (getID() == null) {
        if (other.getID() != null) {
          return false;
        }
      }
      else if (!getID().equals(other.getID())) {
        return false;
      }
    }
    return true;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result = (HASHCODE_PRIME * result) + ((getName() == null) ? 0 : getName().hashCode());
    return result;
  }

  /**
   * return compare result of two PIDC attributes based on sort column
   *
   * @param pidcAttribute2 other pidcAttribute
   * @param sortColumn sortColumn
   * @return -1/0/+1
   */
  public int compareTo(final IPIDCAttribute pidcAttribute2, final int sortColumn) {
    int compareResult;

    switch (sortColumn) {
      case ApicConstants.SORT_ATTRNAME:
      case ApicConstants.SORT_ATTRDESCR:
      case ApicConstants.SORT_SUPERGROUP:
      case ApicConstants.SORT_GROUP:
      case ApicConstants.SORT_LEVEL:
      case ApicConstants.SORT_UNIT:
      case ApicConstants.SORT_VALUETYPE:
        // ICDM-179
        // ICDM-2485
      case ApicConstants.SORT_ICONS:
        compareResult = computeAttrVisibMandConditions(pidcAttribute2);
        break;
      case ApicConstants.SORT_CHAR:
        // use compare method of Attribute class
        compareResult = getAttribute().compareTo(pidcAttribute2.getAttribute(), sortColumn);
        break;

      case ApicConstants.SORT_VALUE:
        // compare default values
        compareResult = ApicUtil.compare(getDefaultValueDisplayName(), pidcAttribute2.getDefaultValueDisplayName());

        break;

      case ApicConstants.SORT_USED:
        // use compare method for Strings
        compareResult = ApicUtil.compare(getIsUsed(), pidcAttribute2.getIsUsed());
        break;
      case ApicConstants.SORT_TRANSFERVCDM:
        // use compare method for Strings
        compareResult = ApicUtil.compareBoolean(canTransferToVcdm(), pidcAttribute2.canTransferToVcdm());
        break;
      case ApicConstants.SORT_FM_RELEVANT:
        // use compare method for Strings
        compareResult = ApicUtil.compareBoolean(isFocusMatrixApplicable(), pidcAttribute2.isFocusMatrixApplicable());
        break;
      case ApicConstants.SORT_USED_NOT_DEF:
        compareResult = caseUsedNotDef(pidcAttribute2);
        break;

      case ApicConstants.SORT_USED_NO:
        // compare only NO used information
        compareResult = ApicUtil.compareBoolean(getIsUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()),
            pidcAttribute2.getIsUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType()));
        break;

      case ApicConstants.SORT_USED_YES:
        // compare only YES used information
        compareResult = ApicUtil.compareBoolean(getIsUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType()),
            pidcAttribute2.getIsUsed().equals(ApicConstants.USED_YES_DISPLAY));
        break;
      case ApicConstants.SORT_PART_NUMBER:
        // use compare method for Strings
        compareResult = ApicUtil.compare(getPartNumber(), pidcAttribute2.getPartNumber());
        break;
      case ApicConstants.SORT_SPEC_LINK:
        // use compare method for Strings
        compareResult = ApicUtil.compare(getSpecLink(), pidcAttribute2.getSpecLink());
        break;
      case ApicConstants.SORT_DESC:
      case ApicConstants.SORT_SUMMARY_DESC:
        // use compare method for Strings
        compareResult = ApicUtil.compare(getAdditionalInfoDesc(), pidcAttribute2.getAdditionalInfoDesc());
        break;
      case ApicConstants.SORT_MODIFIED_DATE:
        // compare last modified date information
        compareResult = ApicUtil.compareCalendar(getLastModifiedDate(), pidcAttribute2.getLastModifiedDate());
        break;
      // Sort for Char Value Icdm-956
      case ApicConstants.SORT_CHAR_VAL:
        compareResult = ApicUtil.compare(getCharValStr(), pidcAttribute2.getCharValStr());
        break;
      case ApicConstants.SORT_ATTR_CREATED_DATE_PIDC:
        // compare created date information
        compareResult = ApicUtil.compareCalendar(getCreatedDate(), pidcAttribute2.getCreatedDate());
        break;
      default:
        compareResult = 0;
        break;
    }

    if (compareResult == 0) {
      // compare result is equal, compare the attribute name
      compareResult = getAttribute().compareTo(pidcAttribute2.getAttribute());
    }

    return compareResult;
  }

  /**
   * @param pidcAttribute2
   * @return
   */
  private int caseUsedNotDef(final IPIDCAttribute pidcAttribute2) {
    int compareResult;
    // compare only NOT_DEFINED used information
    compareResult =
        ApicUtil.compareBoolean(getIsUsed().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType()),
            pidcAttribute2.getIsUsed().equals(ApicConstants.USED_NOTDEF_DISPLAY));
    return compareResult;
  }

  /**
   * Computes the condtion for the visible / mandatory for sorting
   *
   * @param pidcAttribute2 the pidc attribute
   * @return 1 or 0
   */
  private int computeAttrVisibMandConditions(final IPIDCAttribute pidcAttribute2) {
    int compareResult;
    // required sorting order Visible & Mandatory, Visible & Attr Dependencies, Invisible, Other Attribute
    // attribute-1 fetching info
    boolean isAttrOneVisible = isVisible();
    boolean isAttrOneVisibleAndMandatory = isAttrOneVisible && isMandatory();
    boolean isAttrOneVisblAndHasAttrDepnd = isAttrOneVisible && hasAttrDependencies();
    boolean isAttrOneInvisible = !isAttrOneVisible;

    // attribute-2 fetching info
    boolean isAttrTwoVisible = pidcAttribute2.isVisible();
    boolean isAttrTwoVisibleAndMandatory = isAttrTwoVisible && pidcAttribute2.isMandatory();
    boolean isAttrTwoVisblAndHasAttrDepnd = isAttrTwoVisible && pidcAttribute2.hasAttrDependencies();
    boolean isAttrTwoInvisible = !isAttrTwoVisible;


    // ICDM-2409
    if (isAttrOneVisibleAndMandatory) {
      if (isAttrTwoVisibleAndMandatory) {
        compareResult = 0;
      }
      else {
        compareResult = -1;
      }
    }
    else if (isAttrOneVisblAndHasAttrDepnd) {
      if (isAttrTwoVisibleAndMandatory) {
        compareResult = 1;
      }
      else if (isAttrTwoVisblAndHasAttrDepnd) {
        compareResult = 0;
      }
      else {
        compareResult = -1;
      }
    }
    else if (isAttrOneInvisible) {
      if (isAttrTwoVisibleAndMandatory || isAttrTwoVisblAndHasAttrDepnd) {
        compareResult = 1;
      }
      else if (isAttrTwoInvisible) {
        compareResult = 0;
      }
      else {
        compareResult = -1;
      }
    }
    else {
      if (isAttrTwoVisibleAndMandatory || isAttrTwoVisblAndHasAttrDepnd || isAttrTwoInvisible) {
        compareResult = 1;
      }
      else {
        compareResult = 0;
      }
    }
    return compareResult;
  }

  /**
   * @return the Char Value String for the given Attribute Value
   */
  public String getCharValStr() {
    final AttributeValue attrValue = getAttributeValue();
    if (CommonUtils.isNull(attrValue)) {
      return "";
    }
    return attrValue.getCharValStr();
  }

  /**
   * ICDM-933
   *
   * @return last modified date of the project attribute
   */
  public Calendar getLastModifiedDate() {


    if (getModifiedDate() != null) {
      return getModifiedDate();
    }

    if (getCreatedDate() != null) {
      return getCreatedDate();
    }

    return null;

  }

  /**
   * @return the partNumber
   */
  public abstract String getPartNumber();

  /**
   * @return the specLink
   */
  public abstract String getSpecLink();

  /**
   * @return the additionalInfoDesc
   */
  public abstract String getAdditionalInfoDesc();

  /**
   * @return the partNumber
   */
  public abstract String getPartNumber(ApicUser user);

  /**
   * @return the specLink
   */
  public abstract String getSpecLink(ApicUser user);

  /**
   * @return the additionalInfoDesc
   */
  public abstract String getAdditionalInfoDesc(ApicUser user);

  /**
   * This method checks for whether PIDCAttribute value is hyper link value or not
   *
   * @return boolean
   */
  // ICDM-322
  public boolean isHyperLinkValue() {
    boolean isHyperLinkValue = false;
    if (!getAttribute().isDeleted() && (getAttributeValue() != null) &&
        (AttributeValueType.HYPERLINK == getAttribute().getValueType())) {
      isHyperLinkValue = true;
    }
    return isHyperLinkValue;
  }


  /**
   * Icdm-883 Say whether the attr is strucured or not
   *
   * @return the boolean saying if the attr is structured
   */
  abstract public boolean isStructuredAttr();

  /**
   * //ICDM 656 : This method checks for whether the attribute can be moved to variant/subvar levels
   *
   * @return moveEnabled
   */
  public abstract boolean canMoveDown();

  /**
   * iCDM-957 <br>
   * Get the tooltip text, if any value is set for the pidCard attribute.<br>
   * Returns null (to disable tool tip) if value is not set!
   *
   * @return the tooltip for the value set along with description, <Variant> as tooltip if moved to variant and as
   *         <sub-variant> if moved to sub-variant.
   */
  public String getValueTooltip() {

    if (!getDefaultValueDisplayName().isEmpty()) {
      StringBuilder tooltip = new StringBuilder("Value : ");
      // iCDM-2094
      String defaultValueDisplayName = getDefaultValueDisplayName(true);
      tooltip.append(defaultValueDisplayName);
      String valDesc = getValueDescription();
      if (!CommonUtils.isEmptyString(valDesc)) {
        tooltip.append("\nDescription : ").append(valDesc);
      }
      return tooltip.toString();
    }
    // return null , to disable tooltip when no value is set
    return null;

  }

  /**
   * Returns ValueID which is set while importing Excel sheet
   *
   * @return value ID
   */
  abstract public Long getValueID();

  /**
   * iCDM-1345 Check if the attribute is dependending on othe attributes
   *
   * @param includeDeleted true to include deleted attrs
   * @return true if has dependencies
   */
  public boolean hasReferentialDependencies(final boolean includeDeleted) {
    return !getAttribute().getReferentialAttrDependencies(includeDeleted).isEmpty();
  }

  /**
   * ICDM-1604 Check if the attr has any ATTRIBUTE dependencies, this skips value dependencies
   *
   * @return true if the attr has ATTRIBUTE dependencies
   */
  public boolean hasAttrDependencies() {
    List<AttrDependency> depenAttr = getAttribute().getReferentialAttrDependencies(false);
    for (AttrDependency attrDependency : depenAttr) {
      if (attrDependency != null) {
        if (attrDependency.getAttribute() != null) {
          return true;
        }
      }
    }
    return false;
  }


  /**
   * @return the attr Alais name
   */
  public String getEffectiveAttrAlias() {
    // Alias definition for the PIDC
    AliasDefinition aliasDefinition = getPidcVersion().getPidc().getAliasDefinition();
    return getAttrAliasForAliasDef(aliasDefinition);
  }

  /**
   * @param aliasDefinition aliasDefinition
   * @return the effective name
   */
  public String getAttrAliasForAliasDef(final AliasDefinition aliasDefinition) {
    String effAttrAliasName = null;
    if (CommonUtils.isNotNull(aliasDefinition)) {
      effAttrAliasName = aliasDefinition.getEffAttrAliasName(getAttribute());
    }
    return effAttrAliasName == null ? getAttribute().getAttributeNameEng() : effAttrAliasName;
  }

  /**
   * @param aliasDefinition aliasDefinition
   * @return true if the attr alias is present
   */
  public boolean isAttrAliasPresent(final AliasDefinition aliasDefinition) {
    // if the alias def is null or attribute alias value is null
    if ((aliasDefinition == null) || (aliasDefinition.getEffAttrAliasName(getAttribute()) == null)) {
      return false;
    }
    return true;
  }

  /**
   * @param aliasDefinition aliasDefinition
   * @return true if the attr alias is present
   */
  public boolean isValueAliasPresent(final AliasDefinition aliasDefinition) {
    // If alias def is null or attr val is null or value is not text type or Value alias is null.
    if ((aliasDefinition == null) || (getAttributeValue() == null) ||
        !(getAttributeValue().getAttribute().getValueType() == AttributeValueType.TEXT) ||
        (aliasDefinition.getEffValAliasName(getAttributeValue()) == null)) {
      return false;
    }
    return true;
  }

  /**
   * @return the Value Alais name
   */
  public String getEffectiveValueAlias() {
    // Alias definition for the PIDC
    AliasDefinition aliasDefinition = getPidcVersion().getPidc().getAliasDefinition();
    return getValueAliasForAliasDef(aliasDefinition);
  }

  /**
   * @param aliasDefinition aliasDefinition
   * @return the value alias for alias defintion
   */
  public String getValueAliasForAliasDef(final AliasDefinition aliasDefinition) {
    String effValAliasName = null;
    // If alias Def is not null and attr Value is not null and type is text then take the alias otherwise take the attr
    // val eng
    if ((aliasDefinition != null) && (getAttributeValue() != null) &&
        (getAttributeValue().getAttribute().getValueType() == AttributeValueType.TEXT)) {
      effValAliasName = aliasDefinition.getEffValAliasName(getAttributeValue());
    }
    return effValAliasName == null ? getAttributeValue() == null ? null : getAttributeValue().getValueEng()
        : effValAliasName;
  }

  /**
   * @return true if the attribute is mandatory in that PIDCard
   */
  public boolean isMandatory() {
    Long mandLvlAttrValId = getPidcVersion().getAttributes(false).get(getDataCache().getMandatoryLvlAttrValId())
        .getAttributeValue().getValueID();
    Set<Attribute> mandAttrSet = getDataCache().getMandatoryAttrsMap().get(mandLvlAttrValId);
    if (CommonUtils.isNotNull(mandAttrSet)) {
      if (mandAttrSet.contains(getAttribute())) {
        return true;
      }
    }
    else {
      return getAttribute().isMandatory();
    }
    return false;
  }

  /**
   * ICDM-1903 checks whether the attribute is hidden in pidc version level
   *
   * @return isHidden
   */
  abstract public boolean isHidden();

  /**
   * Checks whether value is hidden to current user, based on attribute hidden status and user rights
   *
   * @return true, if value is not visible to current user
   */
  // iCDM-2094
  public boolean isHiddenToUser() {
    return isHidden() && !isReadable();
  }


  /**
   * @param user apicUser
   * @return true if the user has access to the card. False for others
   */
  public boolean isHiddenToUser(final ApicUser user) {
    // if the attribute is not hidden do not proceed. It is not hidden
    if (!isHidden()) {
      return false;
    }

    if ((user != null) && user.hasApicWriteAccess()) {
      return false;
    }

    // get all access rights for the card
    SortedSet<NodeAccessRight> accessRights = getPidcVersion().getPidc().getAccessRights();
    NodeAccessRight currentAccessRights = null;
    currentAccessRights = getCurrUserAccessRights(user, accessRights, currentAccessRights);
    // If the curr rights is null or user does not have read access to the card it is hidden
    if ((currentAccessRights == null) || !currentAccessRights.hasReadAccess()) {
      return true;
    }

    return false;
  }

  /**
   * @param user
   * @param accessRights
   * @param currentAccessRights
   * @return
   */
  private NodeAccessRight getCurrUserAccessRights(final ApicUser user, final SortedSet<NodeAccessRight> accessRights,
      NodeAccessRight currentAccessRights) {
    for (NodeAccessRight nodeAccessRight : accessRights) {
      // check for apic user and access rights
      if (CommonUtils.isEqual(nodeAccessRight.getApicUser(), user)) {
        currentAccessRights = nodeAccessRight;
      }
    }
    return currentAccessRights;
  }

  /**
   * ICDM-2234 checks whether the attribute can be transfered to vcdm
   *
   * @return true/false
   */
  abstract public boolean canTransferToVcdm();

  /**
   * @return Returns whether the read access to PIDC is available
   */
  public boolean isReadable() {
    return (getPidcVersion().getPidc().getCurrentUserAccessRights() != null) &&
        getPidcVersion().getPidc().getCurrentUserAccessRights().hasReadAccess();
  }

  /**
   * @return This method returns whether the value set is not cleared or deleted
   */
  public boolean isValueInvalid() {
    return getIsUsed().equals(ApicConstants.USED_YES_DISPLAY) && (getAttributeValue() != null) &&
        (getAttributeValue().isValueInvalid());
  }

  /**
   * Checks whether the focus matrix defintion is applicable for this attribue in this project
   *
   * @return <code>true</code> if focus matrix definition is applicable
   */
  // ICDM-2241
  public abstract boolean isFocusMatrixApplicable();

  /**
   * @return whether the attribute is a predefined attribute of a grouped attribute
   */
  public abstract boolean isPredefinedAttribute();


  /**
   * @param grpAttr Grouped Attribute
   * @return boolean returns whether the pidcattr is a predefined attr of a grpd attr
   */
  protected boolean validatePredefinedAttrs(final IPIDCAttribute grpAttr) {
    if (CommonUtils.isNotNull(grpAttr.getAttributeValue()) &&
        CommonUtils.isNotEmpty(grpAttr.getAttributeValue().getPreDefinedAttrValueSet())) {
      for (PredefinedAttrValue preDfndAttrVal : grpAttr.getAttributeValue().getPreDefinedAttrValueSet()) {
        if (preDfndAttrVal.getPredefinedAttribute().getAttributeID().equals(getAttribute().getAttributeID())) {
          return true;
        }
      }
    }
    return false;
  }
}
