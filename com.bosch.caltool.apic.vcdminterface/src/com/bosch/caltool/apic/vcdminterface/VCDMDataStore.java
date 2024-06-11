package com.bosch.caltool.apic.vcdminterface;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  // the APRJ
  private ObjInfoEntryType aprj;

  // all attributes and values used in the APRJ
  private WSAttrMapList aprjAttributes = null;

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
   * @return the aprjAttributes
   */
  public WSAttrMapList getAprjAttributes() {
    return this.aprjAttributes;
  }

  /**
   * @param aprjAttributes the aprjAttributes to set
   */
  protected void setAprjAttributes(final WSAttrMapList aprjAttributes) {
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
  protected void setAprj(final ObjInfoEntryType aprj) {
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

}
