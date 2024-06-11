/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.apic.vcdmtransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringJoiner;
import java.util.TreeSet;
import java.util.Vector;

import com.bosch.caltool.apic.vcdminterface.constants.VCDMConstants;
import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.AliasDetailLoader;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcCommand;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionAttributeModel;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVersionLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader;
import com.bosch.caltool.icdm.bo.apic.pidc.ProjectAttributeLoader.LOAD_LEVEL;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.UnAuthorizedAccessException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateUtil;
import com.bosch.caltool.icdm.model.apic.AliasDef;
import com.bosch.caltool.icdm.model.apic.AliasDetail;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.Pidc;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;
import com.bosch.easee.eASEEcdm_Service.EASEEObjClass;
import com.bosch.easee.eASEEcdm_Service.EASEEService;
import com.vector.easee.application.cdmservice.CDMWebServiceException;
import com.vector.easee.application.cdmservice.ObjInfoEntryType;
import com.vector.easee.application.cdmservice.WSAttrMapList;
import com.vector.easee.application.cdmservice.WSProductAttributeValue;

/**
 * Business class for transfer to vcdm
 *
 * @author dmo5cob
 */

public class PidcVcdmTransfer extends AbstractSimpleBusinessObject {

  /**
   * String constant for value
   */
  private static final String VALUE_MSG = " Value: {}";
  private static final long ERR_APRJ_NOT_DEF = 1L;
  private static final String ERR_APRJ_NOT_DEF_TXT = "APRJ name not defined in PIDC";

  private static final long ERR_APRJ_NOT_FOUND = 2L;
  private static final String ERR_APRJ_NOT_FOUND_TXT = "APRJ name not found in vCDM";

  /**
   * Transfer to VCDM error msg -ICDM-1506
   */
  private static final String TRANSFER_TO_VCDM_ERROR =
      "Transfer to vCDM cannot be done since it has been performed with an existing APRJ Name ! Please contact the iCDM Hotline for further assistance .";

  private static final String APRJ_NAME_IN_VCDM_NOT_SET =
      "Transfer to vCDM cannot be done since the APRJ name attribute is not set in the project.";

  private static final String NO_VAR_ERROR = "Transfer to vCDM cannot be done since there are no variants in PIDC";

  private static final String ALL_VARIANTS_DELETED_ERROR =
      "Transfer to vCDM cannot be done since all variants are deleted in PIDC";

  private static final String TRANSFER_TO_ALIAS_DUP_ERROR =
      "Cannot proceed as the attributes to be transferred have same alias.! Please change the alias names in ";
  /**
   * Constant for VCDM Connection Failure when user provides password (Transfer to VCDM,Loading Data from DST into A2l
   * parameter,Takeover variants from VCDM)
   */
  public static final String VCDM_CONN_LOGIN_FAILURE =
      "Connection to vCDM failed. This could be due to one of the following reasons\n1. Incorrect vCDM credentials.\n2. Network Problems.";

  private final EASEEService easeeLoggedInUserService;
  private final EASEEService easeeSuperService;
  private List<String> dupAlaiasNames;

  /**
   * Class Instance
   *
   * @param serviceData initialise the service transaction
   * @param easeeLoggedInUserService EASEEService
   * @param easeeSuperService EASEEService
   */
  public PidcVcdmTransfer(final ServiceData serviceData, final EASEEService easeeLoggedInUserService,
      final EASEEService easeeSuperService) {
    super(serviceData);
    this.easeeLoggedInUserService = easeeLoggedInUserService;
    this.easeeSuperService = easeeSuperService;
  }

  /**
   * @param dataStore VCDMDataStore
   * @throws IcdmException exception
   */
  public void transferPIDC(final VCDMDataStore dataStore) throws IcdmException {

    if (null == this.easeeLoggedInUserService) {
      throw new IcdmException(VCDM_CONN_LOGIN_FAILURE);
    }

    PidcVersion pidcVersion = getActivePIDCVersion(dataStore);

    PidcVersionAttributeModel attrModel =
        new ProjectAttributeLoader(getServiceData()).createModel(pidcVersion.getId(), LOAD_LEVEL.L5_SUBVAR_ATTRS);
    dataStore.setPidcVrsnAttrModel(attrModel);

    Map<Long, AliasDetail> mapOfAliasWithAttrId = getAliasDefenition(pidcVersion);
    dataStore.setMapOfAliasWithAttrId(mapOfAliasWithAttrId);

    validateDuplicateAliasNames(dataStore, attrModel);

    AttributeLoader attrLdr = new AttributeLoader(getServiceData());
    Long aprjAttrId = attrLdr.getLevelAttrId(Long.valueOf(ApicConstants.VCDM_APRJ_NAME_ATTR));

    IProjectAttribute aprjAttribute = attrModel.getPidcVersAttr(aprjAttrId);
    String aprjAttrValue = aprjAttribute.getValue();

    if (aprjAttrValue == null) {
      throw new IcdmException(APRJ_NAME_IN_VCDM_NOT_SET);
    }
    Map<Long, PidcVariant> variantMap = attrModel.getVariantMap();
    if (variantMap.isEmpty()) {
      throw new IcdmException(NO_VAR_ERROR);
    }
    if (isAllVariantsDeleted(variantMap)) {
      throw new IcdmException(ALL_VARIANTS_DELETED_ERROR);
    }


    String aprjId = getAPRJID(aprjAttrValue);
    if (CommonUtils.isEmptyString(aprjId)) {
      throw new IcdmException(
          "Could not find a vCDM project with the given APRJ name '" + aprjAttrValue + "' configured in the PIDC");
    }

    Long newAprjID = Long.valueOf(aprjId);
    dataStore.setNewAprjID(newAprjID);
    Long oldAprjID = dataStore.getPidCard().getAprjId();
    if ((oldAprjID != null) && !CommonUtils.isEqual(oldAprjID, newAprjID)) {
      throw new IcdmException(TRANSFER_TO_VCDM_ERROR);
    }

    getLogger().info("transfering PIDC to vCDM ...");

    // ICDM-490
    dataStore.clearErrors();
    // ICDM-220

    getLogger().info("preparing PIDC specific information ...");
    // set the PIDCard in the DataStore
    dataStore.setPidCard(new PidcLoader(getServiceData()).getDataObjectByID(pidcVersion.getPidcId()));

    // get the APRJ name from the PIDC
    dataStore.setAprj(getAprj(pidcVersion, dataStore));
    if (dataStore.hasAPRJErrors()) {
      // error when getting APRJ
      // ICDM-1344
      throw new IcdmException("Error in retrieving APRJ");
    }

    // get the attributes for which the 'Transfer to vCDM' flag is set , and different values (NO DATE attributes!)
    dataStore.setTransferableAttributes(getTransferableAttributes(attrModel, mapOfAliasWithAttrId));
    if (dataStore.hasAPRJErrors()) {
      // error when getting Variant attributes
      // ICDM-1344
      throw new IcdmException("Error in retrieving Variant Attributes");
    }

    // get all vCDM product attributes and values from the vCDM domain
    getLogger().info("get all Attributes and Values from vCDM ...");
    dataStore.setVcdmAttributes(this.easeeLoggedInUserService.getProductAttributeValues(null));

    // get the vCDM product attributes and values of the APRJ
    getLogger().info("get all active Attributes and Values from the APRJ ...");
    dataStore.setAprjAttributes(
        this.easeeLoggedInUserService.getActivatedProductAttributeValues(dataStore.getAprjVersNumber(), null));

    // get the vCDM product keys of the APRJ
    getLogger().info("get all APRJ ProductKeys from vCDM ...");
    dataStore.setAprjVariants(this.easeeLoggedInUserService.getProductKeys(dataStore.getAprjVersNumber()));

    // get the product key attributes and values
    getLogger().info("get all ProductKey Attributes and Values from vCDM ...");
    dataStore.setAprjVariantsAttributesMap(getProductKeyAttrAndValues(dataStore));

    // add missing product attributes and values in vCDM
    getLogger().info("add missing Product Attributes and Values in the domain ...");
    addAttributesAndValuesInDomain(dataStore, pidcVersion, mapOfAliasWithAttrId);

    // add missing product attributes and values in APRJ
    getLogger().info("activate missing Product Attributes and Values in APRJ ...");
    activateAttributesAndValuesInAPRJ(dataStore, pidcVersion, mapOfAliasWithAttrId, attrModel);

    // add missing product keys / update existing product keys
    getLogger().info("update ProductKeys APRJ...");
    StringBuilder errVariants = new StringBuilder();
    updateProductKeysInAPRJ(pidcVersion, errVariants, dataStore, mapOfAliasWithAttrId, attrModel);

    updatePidc(dataStore);
  }

  /**
   * @param dataStore
   * @throws IcdmException
   */
  private void updatePidc(final VCDMDataStore dataStore) throws IcdmException {
    Pidc pid = dataStore.getPidCard();
    getLogger().debug("Updating vCDM Transfer status in PIDC in iCDM...");
    pid.setAprjId(dataStore.getNewAprjID());
    pid.setVcdmTransferDate(DateUtil.getCurrentUtcTime().toString());
    pid.setVcdmTransferUser(getServiceData().getUsername());
    PidcCommand cmd = new PidcCommand(getServiceData(), pid, true, false);
    getServiceData().getCommandExecutor().execute(cmd);
  }

  /**
   * @param variantMap
   * @return true if all variants are deleted
   */
  private boolean isAllVariantsDeleted(final Map<Long, PidcVariant> variantMap) {
    boolean allVarDeleted = true;
    for (PidcVariant var : variantMap.values()) {
      if (!var.isDeleted()) {
        allVarDeleted = false;
        break;
      }
    }
    return allVarDeleted;
  }

  private void validateDuplicateAliasNames(final VCDMDataStore dataStore, final PidcVersionAttributeModel attrModel)
      throws IcdmException {
    if (!hasDuplicateAliasNames(dataStore)) {
      StringJoiner attrNameStr = new StringJoiner("\n");
      for (String errorName : this.dupAlaiasNames) {
        attrNameStr.add(errorName);
      }

      String errMsg = TRANSFER_TO_ALIAS_DUP_ERROR + getAliasDef(attrModel.getPidcVersion()).getName() +
          "\nAttribute Names :\n" + attrNameStr.toString();

      throw new IcdmException(errMsg);
    }
  }

  /**
   * @return
   * @throws DataException
   */
  private boolean hasDuplicateAliasNames(final VCDMDataStore dataStore) throws DataException {
    this.dupAlaiasNames = new ArrayList<>();

    AliasDef aliasDef = getAliasDef(dataStore.getActivePIDCVrsn());
    if (aliasDef != null) {
      return checkVaraints(dataStore);
    }
    return true;
  }

  /**
   * @param dataStore
   * @param dupAlaiasNames
   * @return
   */
  private boolean checkVaraints(final VCDMDataStore dataStore) {
    // get all varaint map
    Map<Long, PidcVariant> variantsMap = dataStore.getPidcVrsnAttrModel().getVariantMap();
    for (PidcVariant variant : variantsMap.values()) {
      Set<String> attrNameSet = new HashSet<>();
      Map<Long, PidcVariantAttribute> allAttr =
          dataStore.getPidcVrsnAttrModel().getVariantAttributeMap(variant.getId());
      for (PidcVariantAttribute pidcAttrVar : allAttr.values()) {
        String aliasName = null == dataStore.getMapOfAliasWithAttrId().get(pidcAttrVar.getAttrId()) ? null
            : dataStore.getMapOfAliasWithAttrId().get(pidcAttrVar.getAttrId()).getAliasName();
        if (!attrNameSet.add(aliasName) && (null != aliasName)) {
          this.dupAlaiasNames.add(aliasName);
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param dataStore
   * @param pidcVer
   * @param mapOfAliasWithAttrId
   * @throws IcdmException
   */
  private void addAttributesAndValuesInDomain(final VCDMDataStore dataStore, final PidcVersion pidcVer,
      final Map<Long, AliasDetail> mapOfAliasWithAttrId)
      throws IcdmException {
    // the list of values to be added to the domain in vCDM
    List<String> valuesToBeAdded = new Vector<>();
    Set<Attribute> attributesToBeSkipped = new HashSet<>();


    addAttributeValue(VCDMConstants.PIDC_ATTR_NAME, dataStore.getPidCard().getNameEng(), dataStore);
    addAttributeValue(VCDMConstants.PIDC_ID_ATTR_NAME, dataStore.getPidCard().getId().toString(), dataStore);

    // loop over all attributes used in the PIDC as variant attribute
    for (Attribute attribute : dataStore.getTransferableAttributes().keySet()) {
      // clear the list of values to be added
      valuesToBeAdded.clear();


      PidcVersionAttributeLoader pidcVrsnAttrLoader = new PidcVersionAttributeLoader(getServiceData());
      IProjectAttribute ipidcAttribute =
          pidcVrsnAttrLoader.getPidcVersionAttribute(pidcVer.getId()).get(attribute.getId());
      // get the English name of the attribute
      String pidcAttributeName =
          /* ipidcAttribute.getEffectiveAttrAlias() */null == mapOfAliasWithAttrId.get(ipidcAttribute.getAttrId())
              ? ipidcAttribute.getName() : mapOfAliasWithAttrId.get(ipidcAttribute.getAttrId()).getName();
      // get the corresponding vCDM attribute name
      String vcdmAttributeName = dataStore.getVCDMAttribute(pidcAttributeName);

      boolean attrMissingInVCDM = vcdmAttributeName == null;
      if (attrMissingInVCDM) {
        // attribute is missing in vCDM
        getLogger().info("Attribute missing in vCDM: {}", pidcAttributeName);

        // the vCDM attribute name will be the same as in PIDC
        vcdmAttributeName = pidcAttributeName;

        // attribute will not be added here, it will be added along with all values later

      }
      else {
        // attribute available in vCDM
        if (!CommonUtils.isEqual(vcdmAttributeName, pidcAttributeName)) {
          // log an info in case of an attribute mapping not the same name in PIDC and vCDM
          getLogger().info("Attribute mapping in vCDM is different : {}(PIDC) => {}(vCDM)", pidcAttributeName,
              vcdmAttributeName);
        }
      }

      // check if required values are existing in vCDM
      // loop over all values of the attribute used in the PIDC
      Map<Long, String> valuesMap = dataStore.getTransferableAttributes().get(attribute);
      for (Entry<Long, String> valMapEntry : valuesMap.entrySet()) {
        String pidcAttributeValue = valMapEntry.getValue();
        String pidcAttrValue;

        if (pidcAttributeValue == null) {
          if (valMapEntry.getKey().equals(VCDMConstants.SUB_VARIANT_ATTR_VALUE_ID)) {
            // attribute is SUB-VARIANT
            pidcAttrValue = VCDMConstants.SUB_VARIANT_ATTR_VALUE;
          }
          else if (VCDMConstants.NOT_USED_ATTR_VALUE_ID.equals(valMapEntry.getKey())) {
            // attribute is NOT-USED
            pidcAttrValue = VCDMConstants.NOT_USED_ATTR_VALUE;
          }
          else {
            // attribute value is NOT-DEFINED
            pidcAttrValue = null;
          }
        }
        else {
          // get the value of the attribute (in case of TEXT attributes the English value)
          pidcAttrValue = pidcAttributeValue;
        }

        // get the corresponding vCDM attribute value
        String vcdmAttrValue = dataStore.getVCDMAttributeValue(pidcAttributeName, pidcAttrValue);

        if (vcdmAttrValue == null) {
          // attribute value is missing in vCDM
          getLogger().info("AttributeValue missing in vCDM: {} : {}", pidcAttributeName, pidcAttrValue);

          // check, if the value (the String) is still in the list of values to be added
          // duplicates can happen in case of alias definitions!
          boolean valueStillInList = false;

          for (String valueInList : valuesToBeAdded) {
            valueStillInList = valueInList.equals(pidcAttrValue);

            if (valueStillInList) {
              break;
            }
          }

          if ((null != pidcAttrValue) && !valueStillInList) {
            // value is not yet in the list of values to be added
            valuesToBeAdded.add(pidcAttrValue);
          }
        }
        else {
          // attribute value available in vCDM
          if (!vcdmAttrValue.equals(pidcAttrValue)) {
            // log an info in case of an attribute value mapping (not the same value in PIDC and vCDM)
            getLogger().info("AttributeValue mapping in vCDM: {} : {} => {} : {}", pidcAttributeName, pidcAttrValue,
                vcdmAttributeName, vcdmAttrValue);
          }
        }

      }

      // handle new attribute to be created in vCDM without any value list
      if (attrMissingInVCDM && valuesToBeAdded.isEmpty()) {
        // skip the attribute from transferring to vCDM
        attributesToBeSkipped.add(attribute);
      }
      addAttrValuesInVcdm(vcdmAttributeName, valuesToBeAdded, dataStore);
    }

    // remove attributes that need not be transferred to vCDM
    attributesToBeSkipped.forEach(attr -> {
      dataStore.getTransferableAttributes().remove(attr);
      getLogger().info("Attribute {} not considered for vCDM transfer as it has no value list", attr.getName());
    });
  }

  /**
   * @param pidcVer
   * @return
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private Map<Long, AliasDetail> getAliasDefenition(final PidcVersion pidcVer) throws DataException {
    AliasDef aliasDef = getAliasDef(pidcVer);
    if (null != aliasDef) {
      AliasDetailLoader detailsLoader = new AliasDetailLoader(getServiceData());
      Map<Long, AliasDetail> mapOfAlias = detailsLoader.getByAdId(aliasDef.getId());
      Map<Long, AliasDetail> mapOfAliasWithAttrId = new HashMap<>();
      for (AliasDetail details : mapOfAlias.values()) {
        mapOfAliasWithAttrId.put(details.getAttrId(), details);
        mapOfAliasWithAttrId.put(details.getValueId(), details);
      }
      return mapOfAliasWithAttrId;
    }
    return new HashMap<>();

  }

  /**
   * @param pidcVer
   * @return
   * @throws DataException
   */
  private AliasDef getAliasDef(final PidcVersion pidcVer) throws DataException {
    return new PidcLoader(getServiceData()).getAliasDefinition(pidcVer.getPidcId());
  }

  /**
   * @param vcdmAttributeName
   * @param valuesToBeAdded
   * @param dataStore
   */
  private void addAttrValuesInVcdm(final String vcdmAttributeName, final List<String> valuesToBeAdded,
      final VCDMDataStore dataStore) {
    // check, if values to be added
    if (CommonUtils.isNotEmpty(valuesToBeAdded)) {
      // create the attribute and values
      try {
        // ICDM-1520
        this.easeeSuperService.createProductAttributeAndValue(vcdmAttributeName, valuesToBeAdded);
      }
      catch (CDMWebServiceException e) {
        dataStore.setErrorOccured(true);
        getLogger().error("VCDM Transfer ERROR when adding Attribute Value to APRJ: " + e.getMessage(), e);
      }

      // add new attributes and values to the VCDMDataStore
      dataStore.addVcdmAttribute(vcdmAttributeName, valuesToBeAdded);
    }
  }

  /**
   * Check if an attribute value is existing in vCDM If not existing, add the value
   *
   * @param attrName the attribute name in vCDM
   * @param value the value in vCDM
   * @param dataStore
   */
  private void addAttributeValue(final String attrName, final String value, final VCDMDataStore dataStore) {

    // the list of values to be added to the domain in vCDM
    List<String> vcdmAttrValues = dataStore.getVcdmAttributes().get(attrName);

    if ((vcdmAttrValues != null) && (vcdmAttrValues.contains(value))) {
      // value still available in vCDM
      return;
    }

    // attribute value is missing in vCDM
    getLogger().info("Attribute Value missing in vCDM: {} : {}", attrName, value);
    List<String> valuesToBeAdded = new Vector<>();
    valuesToBeAdded.add(value);

    addAttrValuesInVcdm(attrName, valuesToBeAdded, dataStore);

  }

  /**
   * @param dataStore
   * @param pidcVer
   * @param mapOfAliasWithAttrId
   * @param attrModel
   * @throws IcdmException
   */
  private void activateAttributesAndValuesInAPRJ(final VCDMDataStore dataStore, final PidcVersion pidcVer,
      final Map<Long, AliasDetail> mapOfAliasWithAttrId, final PidcVersionAttributeModel attrModel)
      throws IcdmException {
    // the list of attributes and their values to be added to the APRJ
    List<WSProductAttributeValue> attrAndValuesToBeAddedToAPRJ = new Vector<>();

    // the list of values to be added for the current attribute
    List<String> valuesList;

    // loop over all attributes used in the PIDC as variant attribute
    for (Attribute attribute : dataStore.getTransferableAttributes().keySet()) {
      // set the values list to null to identify that there are no new values to be added
      valuesList = null;

      // get the English name of the attribute in PIDC

      IProjectAttribute ipidcAttribute = attrModel.getPidcVersAttr(attribute.getId());
      String pidcAttrName = null == mapOfAliasWithAttrId.get(ipidcAttribute.getAttrId()) ? ipidcAttribute.getName()
          : mapOfAliasWithAttrId.get(ipidcAttribute.getAttrId()).getName();

      // get the corresponding vCDM attribute name
      String vcdmAttrName = dataStore.getVCDMAttribute(pidcAttrName);

      // check if the attribute is available in the APRJ
      if (!dataStore.getAprjAttributes().containsKey(vcdmAttrName)) {
        // attribute is missing in APRJ
        getLogger().info("add Attribute to APRJ: {}", vcdmAttrName);

        // the attribute will be added later together with the values
      }

      // iterate over all used attribute values and add them if necessary
      Map<Long, String> valuesMap = dataStore.getTransferableAttributes().get(attribute);
      for (Entry<Long, String> valMapEntry : valuesMap.entrySet()) {

        String attributeValue = valMapEntry.getValue();
        String pidcAttrValue;

        if (attributeValue == null) {
          if (valMapEntry.getKey().equals(VCDMConstants.SUB_VARIANT_ATTR_VALUE_ID)) {
            // attribute is SUB-VARIANT
            pidcAttrValue = VCDMConstants.SUB_VARIANT_ATTR_VALUE;
          }
          else if (valMapEntry.getKey().equals(VCDMConstants.NOT_DEFINED_ATTR_VALUE_ID)) {
            // attribute is NOT DEFINED
            getLogger().info(" Skipped Value for attribute {} : {}", vcdmAttrName,
                VCDMConstants.NOT_DEFINED_ATTR_VALUE);
            continue;
          }
          else if (valMapEntry.getKey().equals(VCDMConstants.NOT_USED_ATTR_VALUE_ID)) {
            // attribute is NOT-USED
            pidcAttrValue = VCDMConstants.NOT_USED_ATTR_VALUE;
          }
          else {
            // attribute value is invalid
            getLogger().info("invalid ValueID for attribute {} : {}", vcdmAttrName, attributeValue);
            continue;
          }
        }
        else {
          // get the value of the attribute (in case of TEXT attributes the English value)
          pidcAttrValue = attributeValue;
        }

        // get the corresponding vCDM attribute value
        String vcdmAttrValue = dataStore.getVCDMAttributeValue(pidcAttrName, pidcAttrValue);

        // check if value is existing in APRJ
        // if the attribute is missing, the values is also missing
        if ((dataStore.getAprjAttributes().get(vcdmAttrName) == null) ||
            !dataStore.getAprjAttributes().get(vcdmAttrName).contains(vcdmAttrValue)) {
          // value is missing in APRJ
          getLogger().info("add AttributeValue to APRJ: {} : {}", vcdmAttrName, vcdmAttrValue);

          if (valuesList == null) {
            // create a new values list
            valuesList = new Vector<>();

            // add the attribute and the values list to the list of attributes and values to be added
            attrAndValuesToBeAddedToAPRJ.add(new WSProductAttributeValue(vcdmAttrName, valuesList));
          }

          // add the value to the list of values for this attribute
          if (null != vcdmAttrValue) {
            valuesList.add(vcdmAttrValue);
          }

        }

      }

    }

    // PIDC and PIDC_ID

    String pidcAttrName = VCDMConstants.PIDC_ATTR_NAME;

    String pidcAttrValue = new PidcLoader(getServiceData()).getDataObjectByID(pidcVer.getPidcId()).getNameEng();
    String vcdmAttrValue = dataStore.getVCDMAttributeValue(pidcAttrName, pidcAttrValue);

    if (vcdmAttrValue == null) {
      valuesList = new Vector<>();
      valuesList.add(pidcAttrValue);
      attrAndValuesToBeAddedToAPRJ.add(new WSProductAttributeValue(pidcAttrName, valuesList));
    }

    pidcAttrName = VCDMConstants.PIDC_ID_ATTR_NAME;
    pidcAttrValue = pidcVer.getPidcId().toString();
    vcdmAttrValue = dataStore.getVCDMAttributeValue(pidcAttrName, pidcAttrValue);

    if (vcdmAttrValue == null) {
      valuesList = new Vector<>();
      valuesList.add(pidcAttrValue);
      attrAndValuesToBeAddedToAPRJ.add(new WSProductAttributeValue(pidcAttrName, valuesList));
    }

    // check, if any attribute and value is to be added to the APRJ
    if (!attrAndValuesToBeAddedToAPRJ.isEmpty()) {
      // activate the attributes and values in the APRJ
      getLogger().info("activating attributes and values in APRJ ...");

      try {
        this.easeeLoggedInUserService.setProductAttributeValues(dataStore.getAprjVersNumber(),
            attrAndValuesToBeAddedToAPRJ);
      }
      catch (CDMWebServiceException e) {
        dataStore.setErrorOccured(true);
        getLogger().error("VCDM Transfer ERROR when adding Attributes and Values to APRJ: " + e.getMessage(), e);
      }

      // TODO: add the new values to the VCDMDataStore
      // TODO: interim solution, read all attributes
      getLogger().info("getting attributes and values activated in APRJ ...");
      dataStore.setAprjAttributes(
          this.easeeLoggedInUserService.getActivatedProductAttributeValues(dataStore.getAprjVersNumber(), null));

    }
  }

  /**
   * @param dataStore
   * @return
   * @throws UnAuthorizedAccessException
   * @throws DataException
   */
  private PidcVersion getActivePIDCVersion(final VCDMDataStore dataStore) throws DataException {
    PidcVersionLoader pidcVersionLoader = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVer = pidcVersionLoader.getActivePidcVersion(dataStore.getPidCard().getId());
    dataStore.setActivePIDCVrsn(pidcVer);
    return pidcVer;
  }


  /**
   * This method returns the attributes to be transfered along with the values
   *
   * @param pidcVer
   * @param attrModel
   * @param mapOfAliasWithAttrId
   * @return
   * @throws IcdmException
   */
  private Map<Attribute, Map<Long, String>> getTransferableAttributes(final PidcVersionAttributeModel attrModel,
      final Map<Long, AliasDetail> mapOfAliasWithAttrId) {

    // LOG the transfer attributes and values
    getLogger().debug("Transfer attributes:");

    // a temporary variable for attribute values
    Map<Long, String> valuesMap;

    // create the result object
    Map<Attribute, Map<Long, String>> transferAttributes = new HashMap<>();


    for (IProjectAttribute pidcAttr : attrModel.getPidcVersAttrMap().values()) {
      if (attrIsValidForCDM(pidcAttr, attrModel)) {
        Attribute attribute = attrModel.getAllAttrMap().get(pidcAttr.getAttrId());
        // not existing add it with an empty values map
        transferAttributes.put(attribute, new HashMap<Long, String>());
        getLogger().debug(" Attribute: {}", attribute.getNameEng());


        // get the values map of the attribute
        valuesMap = transferAttributes.get(attribute);
        // check if the attribute is defined as sub-variant
        if (pidcAttr.isAtChildLevel()) {
          getVariantValues(valuesMap, pidcAttr, attrModel, mapOfAliasWithAttrId);
        }
        else if (pidcAttr.getUsedFlag().equals(ApicConstants.CODE_NO)) {

          getLogger().info(" Attribute: Used Flag is NO");
          // add the value for NOT USED attribute
          valuesMap.put(VCDMConstants.NOT_USED_ATTR_VALUE_ID, VCDMConstants.NOT_USED_ATTR_VALUE);
        }
        else {
          addToValuesMap(valuesMap, pidcAttr, attrModel);
        }
      }
    }


    return transferAttributes;
  }

  /**
   * Check, if the attribute can be transfered to vCDM Attributes which can be transfered: - attributes with USED FLAG
   * other than ??? - attributes with datatype other than DATE or HYPERLINK NOT to be checked here: - SUB-VARIANT
   * attributes (they are implicitly USED) - USED flag = NO (will be checked at other place)
   *
   * @param varAttr
   * @param attrModel
   * @return TRUE, if transfer is possible
   */
  private boolean attrIsValidForCDM(final IProjectAttribute varAttr, final PidcVersionAttributeModel attrModel) {
    PidcVersionAttribute parentAttr = attrModel.getPidcVersAttr(varAttr.getAttrId());
    if (parentAttr.isTransferToVcdm()) {
      Attribute attr = attrModel.getAllAttrMap().get(varAttr.getAttrId());
      if (attrModel.getPidcVersInvisibleAttrSet().contains(varAttr.getAttrId())) {
        // attribute is not visible
        getLogger().info("Attribute not visible: {}", varAttr.getName());
        return false;
      }
      else if (attr.getValueType().equals(AttributeValueType.DATE.toString())) {
        // attribute has DATE type
        getLogger().info(" non valid attribute (DATE type): {}", varAttr.getName());
        return false;
      }
      else if (attr.getValueType().equals(AttributeValueType.HYPERLINK.toString())) {
        // attribute has HYPERLINK type
        getLogger().info(" non valid attribute (HYPERLINK type): {}", varAttr.getName());
        return false;
      }
      else {
        return parentAttr.isTransferToVcdm();
      }
    }
    return false;

  }

  /**
   * @param pidcVer
   * @param errVariants
   * @param dataStore
   * @param mapOfAliasWithAttrId
   * @param attrModel
   * @throws IcdmException
   */
  private StringBuilder updateProductKeysInAPRJ(final PidcVersion pidcVer, final StringBuilder errVariants,
      final VCDMDataStore dataStore, final Map<Long, AliasDetail> mapOfAliasWithAttrId,
      final PidcVersionAttributeModel attrModel)
      throws IcdmException {


    // loop over all PIDC Variants

    for (PidcVariant pidcVariant : attrModel.getVariantMap().values()) {

      // don't transfer deleted variants
      // ICDM-325
      if (pidcVariant.isDeleted()) {
        getLogger().info("Variant not transferred, because it is marked as deleted: {}", pidcVariant.getName());
        continue;
      }
      getLogger().info("Transfer Product Key attributes for Variant {}", pidcVariant.getName());

      // create the attributes and values Map for the product key
      Map<String, String> attrValuesMap = new HashMap<>();

      Pidc pidc = attrModel.getPidc();
      // add PIDC name and ID
      attrValuesMap.put(VCDMConstants.PIDC_ATTR_NAME, pidc.getNameEng());
      attrValuesMap.put(VCDMConstants.PIDC_ID_ATTR_NAME, pidcVer.getPidcId().toString());

      // add variant attributes
      for (IProjectAttribute variantAttr : attrModel.getAllVariantAttributeMap().get(pidcVariant.getId()).values()) {
        if (!attrIsValidForCDM(variantAttr, attrModel)) {
          // attribute value is not valid for vCDM
          continue;
        }

        String variantAttrName = null == mapOfAliasWithAttrId.get(variantAttr.getAttrId()) ? variantAttr.getName()
            : mapOfAliasWithAttrId.get(variantAttr.getAttrId()).getName();

        String vcdmAttrName = dataStore.getVCDMAttribute(variantAttrName);

        String vcdmAttrValue;

        Attribute attribute = attrModel.getAttribute(variantAttr.getAttrId());
        Map<Long, String> valuesMap = dataStore.getTransferableAttributes().get(attribute);


        if (variantAttr.isAtChildLevel()) {
          // attribute is SUB-VARIANT

          vcdmAttrValue = valuesMap.get(pidcVariant.getId());

          attrValuesMap.put(vcdmAttrName, vcdmAttrValue);

        }
        // attribute is VARIANT
        else if (variantAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
          // attribute is declared as NOT USED
          vcdmAttrValue = VCDMConstants.NOT_USED_ATTR_VALUE;
          attrValuesMap.put(vcdmAttrName, vcdmAttrValue);
        }
        else if (variantAttr.getValue() == null) {
          // attribute has no value - add NULL value
          attrValuesMap.put(vcdmAttrName, null);
        }
        else {
          // get the current value
          vcdmAttrValue = dataStore.getVCDMAttributeValue(variantAttrName, valuesMap.get(variantAttr.getValueId()));
          attrValuesMap.put(vcdmAttrName, vcdmAttrValue);
        }

      }

      // add PIDC attributes which are marked as to be transfered
      for (IProjectAttribute pidcAttr : attrModel.getPidcVersAttrMap().values()) {
        if (!attrIsValidForCDM(pidcAttr, attrModel)) {
          // attribute value is not valid for vCDM
          continue;
        }

        if (pidcAttr.isAtChildLevel()) {
          // attribute is variant, these attributes are handled separately
          continue;
        }

        String pidcAttrName = null == mapOfAliasWithAttrId.get(pidcAttr.getAttrId()) ? pidcAttr.getName()
            : mapOfAliasWithAttrId.get(pidcAttr.getAttrId()).getName();

        String vcdmAttrName = dataStore.getVCDMAttribute(pidcAttrName);

        String vcdmAttrValue;

        Attribute attribute = attrModel.getAllAttrMap().get(pidcAttr.getAttrId());

        Map<Long, String> valuesMap = dataStore.getTransferableAttributes().get(attribute);

        // attribute is on PIDC level
        if (pidcAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {
          // attribute is declared as NOT USED
          vcdmAttrValue = VCDMConstants.NOT_USED_ATTR_VALUE;
          attrValuesMap.put(vcdmAttrName, vcdmAttrValue);
        }
        else if (pidcAttr.getValue() == null) {
          // attribute has no value - add NULL value
          attrValuesMap.put(vcdmAttrName, null);
        }
        else {
          // get the current value
          vcdmAttrValue = dataStore.getVCDMAttributeValue(pidcAttrName, valuesMap.get(pidcAttr.getValueId()));
          attrValuesMap.put(vcdmAttrName, vcdmAttrValue);
        }

      }


      // add existing vCDM attributes, which are not declared as to be transfered
      // this is used to keep existing attributes in vCDM which are not used in iCDM

      // get the vCDM attruibutes for the current variant
      WSAttrMapList vCDMAttrValues = dataStore.getAprjVariantsAttributesMap().get(pidcVariant.getName());

      if (vCDMAttrValues != null) {
        // attributes are existing in vCDM

        // check if the attribute is used in iCDM
        for (Entry<String, List<String>> vAVEntry : vCDMAttrValues.entrySet()) {
          if (attrValuesMap.containsKey(vAVEntry.getKey())) {
            // attribute is defined in iCDM
            continue;
          }

          // attribute is not defined in iCDM

          // take the current value (can have only one)
          attrValuesMap.put(vAVEntry.getKey(), vAVEntry.getValue().get(0));

        }

      }

      // transfer it to vCDM
      if (dataStore.getAprjVariantsMap().get(pidcVariant.getName()) == null) {
        // variant missing in vCDM
        getLogger().info(" add ProductKey to APRJ: {}", pidcVariant.getName());

        // add product key including attributes
        try {
          this.easeeLoggedInUserService.createProductKey(dataStore.getAprjVersNumber(), pidcVariant.getName(),
              attrValuesMap);
        }
        catch (CDMWebServiceException exp) {


          dataStore.setErrorOccured(true);
          throw new IcdmException("Error(s) occured while transferring to vCDM : " + exp.getMessage(), exp);

          // TODO add variant to error list
        }
      }
      else {
        // variant existing in vCDM
        getLogger().info(" update ProductKey Attributes: {}", pidcVariant.getName());

        // update product key attributes and values
        try {
          this.easeeLoggedInUserService.createProductKey(dataStore.getAprjVersNumber(), pidcVariant.getName(),
              attrValuesMap);
        }
        catch (CDMWebServiceException exp) {
          dataStore.setErrorOccured(true);
          throw new IcdmException("Error(s) occured while Transferring to vCDM : " + exp.getMessage(), exp);

          // TODO add variant to error list
        }
      }
    }

    return errVariants;
  }

  /**
   * @param dataStore
   * @return
   */
  private Map<String, WSAttrMapList> getProductKeyAttrAndValues(final VCDMDataStore dataStore) {
    Map<String, WSAttrMapList> aprjVariantsAttributesMap = new HashMap<>();

    for (String productKey : dataStore.getAprjVariantsMap().keySet()) {
      aprjVariantsAttributesMap.put(productKey,
          this.easeeLoggedInUserService.getProductKeyAttributeValues(dataStore.getAprjVersNumber(), productKey));
    }
    return aprjVariantsAttributesMap;
  }

  /**
   * Get the APRJ from vCDM The name of the APRJ must be defined in the PIDC. The APRJ must be checked out.
   *
   * @param apicDataProvider
   * @param pidcVer
   * @param dataStore
   * @param attrModel
   * @return The vCDM APRJ
   * @throws DataException
   */
  private ObjInfoEntryType getAprj(final PidcVersion pidcVer, final VCDMDataStore dataStore) throws IcdmException {
    // get the APRJ attribute from the PIDC
    PidcVersionAttributeLoader loader = new PidcVersionAttributeLoader(getServiceData());


    String aprjName = loader.getAprjName(pidcVer.getId());
    if (aprjName == null) { // APRJ name not defined
      getLogger().error(ERR_APRJ_NOT_DEF_TXT);
      dataStore.setAPRJErrorCode(ERR_APRJ_NOT_DEF);
      return null;
    }
    // get the name of the APRJ defined in the PIDC

    // find the APRJ, it must not be checked in
    List<ObjInfoEntryType> aprjList = this.easeeLoggedInUserService.getAprj(aprjName, false);
    if (aprjList.isEmpty()) {
      // APRJ not found in vCDM
      getLogger().error(ERR_APRJ_NOT_FOUND_TXT);
      dataStore.setAPRJErrorCode(ERR_APRJ_NOT_FOUND);
      return null;
    }
    // get the first APRJ from the result list
    ObjInfoEntryType vCDMAprj = aprjList.get(0);
    getLogger().info("APRJ versionID: {}", vCDMAprj.getVersionNo());

    return vCDMAprj;
  }


  /**
   * @param pidcVer
   * @param valuesMap
   * @param pidcAttr
   * @param attrModel
   * @param mapOfAliasWithAttrId
   */
  private void getVariantValues(final Map<Long, String> valuesMap, final IProjectAttribute pidcAttr,
      final PidcVersionAttributeModel attrModel, final Map<Long, AliasDetail> mapOfAliasWithAttrId) {
    getLogger().debug(" Fetching value of attribute from variants ...");
    for (PidcVariant variant : attrModel.getVariantMap().values()) {
      getLogger().debug(" Fetching value of attribute from variant {} ...", variant.getId());

      PidcVariantAttribute varAttr = attrModel.getVariantAttributeMap(variant.getId()).get(pidcAttr.getAttrId());
      if (null == varAttr) {
        getLogger().debug("  attribute not available at variant level due to attribute dependency");
        continue;
      }
      if (varAttr.isAtChildLevel()) {
        getLogger().debug("  Variant {} : Attribute value defined at sub-variant level..  : {}", variant.getId());

        StringBuilder valuesStr = new StringBuilder();
        SortedSet<String> sortedValues = new TreeSet<>();

        for (PidcSubVariant subVar : attrModel.getSubVariantMap(variant.getId()).values()) {
          getLogger().debug("    Retrieving value for sub Variant : {}", subVar.getId());
          if (subVar.isDeleted()) {
            getLogger().debug("     SubVariant : {} is deleted. Will be ignored for transfer", subVar.getId());
            continue;
          }
          PidcSubVariantAttribute subvarAttr =
              attrModel.getSubVariantAttributeMap(subVar.getId()).get(pidcAttr.getAttrId());

          if (null == subvarAttr) {
            getLogger().debug("   attribute not available at sub-variant level due to attribute dependency");
            continue;
          }

          String usedFlag = CommonUtils.checkNull(subvarAttr.getUsedFlag());
          if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType().equals(usedFlag)) {
            sortedValues.add(VCDMConstants.NOT_USED_ATTR_VALUE);
          }
          else if (ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getDbType().equals(usedFlag) ||
              ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType().equals(usedFlag) ||
              (subvarAttr.getValue() == null)) {
            sortedValues.add(VCDMConstants.UNDEFINED_ATTR_VALUE);
          }
          else {
            // value not null
            if (null != mapOfAliasWithAttrId.get(subvarAttr.getValueId())) {
              sortedValues.add(mapOfAliasWithAttrId.get(subvarAttr.getValueId()).getAliasName());
            }
            else {
              sortedValues.add(null == attrModel.getRelevantAttrValueMap().get(subvarAttr.getValueId()) ? ""
                  : attrModel.getRelevantAttrValueMap().get(subvarAttr.getValueId()).getName());
            }
          }
        }

        for (String string : sortedValues) {
          valuesStr.append(string).append(",");
        }

        if (valuesStr.length() > 0) {
          valuesStr.deleteCharAt(valuesStr.length() - 1);
          if (!valuesMap.containsKey(variant.getId())) {
            // In case of subvariants , add the variant id as the key and the values of each subvariant in the form of a
            // comma separated string
            if (CommonUtils.isEqual(valuesStr.toString(), VCDMConstants.UNDEFINED_ATTR_VALUE)) {
              valuesStr.replace(0, valuesStr.length(), VCDMConstants.NOT_DEFINED_SUB_VAR_VALUE);
            }
            valuesMap.put(variant.getId(), valuesStr.toString());
            getLogger().debug(" Consolidated sub-variant value : {} ", valuesMap.get(variant.getId()));
          }
        }
      }
      else {
        addToValuesMap(valuesMap, varAttr, attrModel);
      }
    }
  }

  /**
   * @param valuesMap
   * @param pidcAttr
   * @param mapOfAliasWithAttrId
   * @param attrModel
   */
  private void addToValuesMap(final Map<Long, String> valuesMap, final IProjectAttribute pidcAttr,
      final PidcVersionAttributeModel attrModel) {
    if (pidcAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NO.getDbType())) {

      // add the value for NOT USED attribute
      valuesMap.put(VCDMConstants.NOT_USED_ATTR_VALUE_ID, null);
      getLogger().info(VALUE_MSG, VCDMConstants.NOT_USED_ATTR_VALUE);
    }
    else if (pidcAttr.getUsedFlag().equals(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getDbType())) {

      // add the value for NOT DEFINED attribute
      valuesMap.put(VCDMConstants.NOT_DEFINED_ATTR_VALUE_ID, null);
      getLogger().info(VALUE_MSG, VCDMConstants.NOT_DEFINED_ATTR_VALUE);
    }
    else if ((pidcAttr.getValue() != null) && !valuesMap.containsKey(pidcAttr.getValueId())) {
      // add the value

      valuesMap.put(pidcAttr.getValueId(), null == attrModel.getRelevantAttrValueMap().get(pidcAttr.getValueId()) ? ""
          : attrModel.getRelevantAttrValueMap().get(pidcAttr.getValueId()).getName());
      getLogger().info(VALUE_MSG, valuesMap.get(pidcAttr.getValueId()));
    }
    else {
      // no value defined
      valuesMap.put(VCDMConstants.NOT_DEFINED_ATTR_VALUE_ID, null);
      getLogger().info(" no Value used in iCDM, using NOT DEFINED as default");
      getLogger().info(VALUE_MSG, VCDMConstants.NOT_DEFINED_ATTR_VALUE);
    }
  }

  /**
   * ICDM 533
   *
   * @param vCDMAprj vCDM APRJ name
   * @return APRJ ID
   */
  public String getAPRJID(final String vCDMAprj) {
    getLogger().info("Fetching ID of APRJ {} . .. ", vCDMAprj);

    final List<ObjInfoEntryType> aprjList =
        this.easeeSuperService.getObjects(EASEEObjClass.APRJ, vCDMAprj, null, null, null);
    String aprjID = "";
    long aprjIDLong = 0;
    long temp;
    // Finding out the APRJ with highest version number
    for (ObjInfoEntryType obj : aprjList) {
      temp = Long.parseLong(obj.getVersionNo());
      if (temp > aprjIDLong) {
        aprjIDLong = temp;
        aprjID = obj.getVersionNo();
      }
    }
    getLogger().info("APRJ ID found : {}", aprjID);
    return aprjID;
  }
}

