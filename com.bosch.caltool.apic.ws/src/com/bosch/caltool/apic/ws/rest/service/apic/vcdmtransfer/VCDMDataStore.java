package com.bosch.caltool.apic.ws.rest.service.apic.vcdmtransfer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;
import com.vector.easee.application.cdmservice.WSAttrMapList;
import com.vector.easee.application.cdmservice.WSVariantKey;

/**
 * @author hef2fe This class holds all data necessary to trandfer a PIDC to vCDM. This class can later also been used as
 *         the return type in iCDM which can be modified by the user.
 */
public class VCDMDataStore {

  // APRJ ERROR Code
  private long errorCode = 0l;
  // ICDM-490 Errors Other than APRJ are set here
  private boolean errorOccured = false;

  // the Project-ID-Card
  private Pidc pidCard;

  // the APRJ
  private ObjInfoEntryType aprj;

  // all attributes and values defined in vCDM
  private WSAttrMapList vcdmAttributes = null;

  // all attributes and values used in the APRJ
  private WSAttrMapList aprjAttributes = null;

  private PidcVersion activePIDCVrsn;


  private PidcVersionAttributeModel pidcVrsnAttrModel;

  private Map<Long, AliasDetail> mapOfAliasWithAttrId;
  /**
   * all variants defined in the APRJ The Key is the name of the product key in vCDM The value is the detailed
   * description of the product key
   */
  private Map<String, WSVariantKey> aprjVariantsMap = null;

  /**
   * all variants attribute values used in the APRJ product keys The Key is the name of the product key in vCDM The
   * Value is a map of all attributes and values defined for the product key
   */
  private Map<String, WSAttrMapList> aprjVariantsAttributesMap = null;

  /**
   * all attributes and values used in the PIDC variants The Key is the PIDC attribute The Value is a map with the
   * valueID as the key and the attribute value as the value If the Attribute is defined as SUB-VARIANT the key is the
   * constant SUB_VARIANT_ATTR_VALUE_ID and the value is NULL
   */
  private Map<Attribute, Map<Long, String>> transferableAttributes = null;

  /**
   * the mapping of PIDC attributes and values to vCDM attributes and values In some cases attributes or values are
   * written in different upper/lower case in vCDM This datastructure maps the names in PIDC with the names in vCDM
   */
  private AttrValueMapping attrValueMapping = null;
  private Long newAprjID;


  /**
   * @return the vcdmAttributes
   */
  public WSAttrMapList getVcdmAttributes() {
    return this.vcdmAttributes;
  }

  /**
   * @param vcdmAttributes the vcdmAttributes to set
   */
  protected void setVcdmAttributes(final WSAttrMapList vcdmAttributes) {
    this.vcdmAttributes = vcdmAttributes;

    if (this.transferableAttributes != null) {
      this.attrValueMapping = new AttrValueMapping(this.transferableAttributes, vcdmAttributes, this);
    }

  }

  /**
   * Add a new vCDM attribute to the vCDM attributes Map This method can be used only to add attributes which have been
   * newly created by the VCDMInterface.
   *
   * @param attributeName the attribute to be added to vCDM
   * @param valuesList the list of values to be added to vCDM
   */
  public void addVcdmAttribute(final String attributeName, final List<String> valuesList) {
    // add it to the vCDM attributes Map
    if (!this.vcdmAttributes.containsKey(attributeName)) {
      this.vcdmAttributes.put(attributeName, valuesList);
    }

    // add the attribute to the mapping table
    // vCDM attribute name = PIDC attribute name
    addVCDMAttributeMapping(attributeName, attributeName);

    // add the attribute values to the mapping table
    for (String value : valuesList) {
      addVCDMAttrValueMapping(attributeName, value, value);
    }
  }

  /**
   * @return the aprjAttributes
   */
  public WSAttrMapList getAprjAttributes() {
    return this.aprjAttributes;
  }

  /**
   * @param aprjAttributes the aprjAttributes to set
   */
  public void setAprjAttributes(final WSAttrMapList aprjAttributes) {
    this.aprjAttributes = aprjAttributes;


  }

  /**
   * @return the aprj
   */
  public String getAprjVersNumber() {
    return this.aprj.getVersionNo();
  }

  /**
   * @return the aprj
   */
  public ObjInfoEntryType getAprj() {
    return this.aprj;
  }

  /**
   * @param aprj the aprj to set
   */
  public void setAprj(final ObjInfoEntryType aprj) {
    this.aprj = aprj;
  }

  /**
   * @return the errorCode
   */
  public long getAPRJErrorCode() {
    return this.errorCode;
  }

  /**
   * @param errorCode the errorCode to set
   */
  public void setAPRJErrorCode(final long errorCode) {
    this.errorCode = this.errorCode | errorCode;
  }

  /**
   * Check, if any error available
   *
   * @return true, if errors are available
   */
  public boolean hasAPRJErrors() {
    return this.errorCode != 0l;
  }

  /**
   * @return the variantAttributes
   */
  public Map<Attribute, Map<Long, String>> getTransferableAttributes() {
    return this.transferableAttributes;
  }

  /**
   * @param variantAttributes the variantAttributes to set
   */
  public void setTransferableAttributes(final Map<Attribute, Map<Long, String>> variantAttributes) {
    this.transferableAttributes = variantAttributes;

    if (this.vcdmAttributes != null) {
      this.attrValueMapping = new AttrValueMapping(variantAttributes, this.vcdmAttributes, this);
    }
  }

  /**
   * @return the aprjVariants
   */
  public Map<String, WSVariantKey> getAprjVariantsMap() {
    return this.aprjVariantsMap;
  }

  /**
   * @param aprjVariants the aprjVariants to set
   */
  protected void setAprjVariants(final List<WSVariantKey> aprjVariants) {

    this.aprjVariantsMap = new HashMap<String, WSVariantKey>();

    for (WSVariantKey wsVariantKey : aprjVariants) {
      this.aprjVariantsMap.put(wsVariantKey.getName(), wsVariantKey);

    }

  }


  /**
   * @return the aprjVariantsAttributesMap
   */
  public Map<String, WSAttrMapList> getAprjVariantsAttributesMap() {
    return this.aprjVariantsAttributesMap;
  }

  /**
   * @param aprjVariantsAttributesMap the aprjVariantsAttributesMap to set
   */
  protected void setAprjVariantsAttributesMap(final Map<String, WSAttrMapList> aprjVariantsAttributesMap) {
    this.aprjVariantsAttributesMap = aprjVariantsAttributesMap;
  }

  /**
   * Get the vCDM attribute name for a PIDC attribute name. A name mapping (e.g. due to different upper/lower case) will
   * be considered.
   *
   * @param pidcAttributeName the PIDC attribute name
   * @return the vCDM attribute name
   */
  public String getVCDMAttribute(final String pidcAttributeName) {

    if (this.attrValueMapping == null) {
      return null;
    }
    return this.attrValueMapping.getVCDMAttribute(pidcAttributeName);
  }

  /**
   * Get the vCDM value matching the PIDC value
   *
   * @param pidcAttributeName the attribute name in PIDC
   * @param pidcAttributeValue the attribute value in PIDC
   * @return the matching attribute value in vCDM
   */
  public String getVCDMAttributeValue(final String pidcAttributeName, final String pidcAttributeValue) {

    if (this.attrValueMapping == null) {
      return null;
    }

    return this.attrValueMapping.getVCDMAttributeValue(pidcAttributeName, pidcAttributeValue);
  }

  private void addVCDMAttributeMapping(final String pidcAttributeName, final String vCDMAttributeName) {
    this.attrValueMapping.addVCDMAttribute(pidcAttributeName, vCDMAttributeName);
  }

  private void addVCDMAttrValueMapping(final String pidcAttributeName, final String pidcAttrValue,
      final String vCDMAttrValue) {
    this.attrValueMapping.addVCDMAttrValue(pidcAttributeName, pidcAttrValue, vCDMAttrValue);
  }


  /**
   * @return the pidCard
   */
  public Pidc getPidCard() {
    return this.pidCard;
  }


  /**
   * @param pidCard the pidCard to set
   */
  public void setPidCard(final Pidc pidCard) {
    this.pidCard = pidCard;
  }


  /**
   * Checks whether errors other the APRJ Errors has occurred ICDM-490
   *
   * @return the errorOccured
   */
  public boolean isErrorOccured() {
    return this.errorOccured;
  }


  /**
   * Sets the NON-APRJ error
   *
   * @param errorOccured the errorOccured to set
   */
  public void setErrorOccured(final boolean errorOccured) {
    this.errorOccured = errorOccured;
  }

  /**
   * Clears all the errors
   */
  public void clearErrors() {
    this.errorCode = 0l;
    this.errorOccured = false;
  }


  /**
   * @return the activePIDCVrsn
   */
  public PidcVersion getActivePIDCVrsn() {
    return this.activePIDCVrsn;
  }


  /**
   * @param activePIDCVrsn the activePIDCVrsn to set
   */
  public void setActivePIDCVrsn(final PidcVersion activePIDCVrsn) {
    this.activePIDCVrsn = activePIDCVrsn;
  }


  /**
   * @return the pidcVrsnAttrModel
   */
  public PidcVersionAttributeModel getPidcVrsnAttrModel() {
    return this.pidcVrsnAttrModel;
  }


  /**
   * @param pidcVrsnAttrModel the pidcVrsnAttrModel to set
   */
  public void setPidcVrsnAttrModel(final PidcVersionAttributeModel pidcVrsnAttrModel) {
    this.pidcVrsnAttrModel = pidcVrsnAttrModel;
  }


  /**
   * @return the mapOfAliasWithAttrId
   */
  public Map<Long, AliasDetail> getMapOfAliasWithAttrId() {
    return this.mapOfAliasWithAttrId;
  }


  /**
   * @param mapOfAliasWithAttrId the mapOfAliasWithAttrId to set
   */
  public void setMapOfAliasWithAttrId(final Map<Long, AliasDetail> mapOfAliasWithAttrId) {
    this.mapOfAliasWithAttrId = mapOfAliasWithAttrId;
  }


  /**
   * @return the newAprjID
   */
  public Long getNewAprjID() {
    return this.newAprjID;
  }


  /**
   * @param newAprjID the newAprjID to set
   */
  public void setNewAprjID(final Long newAprjID) {
    this.newAprjID = newAprjID;
  }

}
