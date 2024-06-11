/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectSubVariant;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;


/**
 * @author bne4cob
 */
public class PidcSubVariantLoader extends AbstractBusinessObject<PidcSubVariant, TabvProjectSubVariant> {

  /**
   * @param serviceData Service Data
   */
  public PidcSubVariantLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.SUB_VARIANT, TabvProjectSubVariant.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected PidcSubVariant createDataObject(final TabvProjectSubVariant entity) throws DataException {
    PidcSubVariant data = new PidcSubVariant();
    setCommonFields(data, entity);

    data.setNameValueId(entity.getTabvAttrValue().getValueId());

    AttributeValueLoader attrValLdr = new AttributeValueLoader(getServiceData());
    AttributeValue attrVal = attrValLdr.getDataObjectByID(entity.getTabvAttrValue().getValueId());
    data.setName(attrVal.getName());
    data.setDescription(ApicUtil.getLangSpecTxt(getServiceData().getLanguageObj(), attrVal.getDescriptionEng(),
        attrVal.getDescriptionGer(), null));

    data.setDeleted(isDeleted(entity));
    data.setPidcVariantId(entity.getTabvProjectVariant().getVariantId());
    data.setPidcVersionId(entity.getTPidcVersion().getPidcVersId());
    data.setVersion(entity.getVersion());
    return data;
  }

  /**
   * @param variantId variantId
   * @param includeDeleted include Deleted sub variants
   * @return map of sub variants. Key - sub variant ID; value - data object
   * @throws DataException any error during sub variant creation
   */
  public Map<Long, PidcSubVariant> getSubVariants(final Long variantId, final boolean includeDeleted)
      throws DataException {
    getLogger().debug("Loading subvariants of PIDC Variant {}", variantId);

    PidcSubVariant data;
    Map<Long, PidcSubVariant> retMap = new HashMap<>();

    for (TabvProjectSubVariant entity : new PidcVariantLoader(getServiceData()).getEntityObject(variantId)
        .getTabvProjectSubVariants()) {
      if (includeDeleted || !isDeleted(entity)) {
        data = createDataObject(entity);
        retMap.put(data.getId(), data);
      }
    }

    getLogger().debug("Pidc Variant: {}. SubVariants Count = {}", variantId, retMap.size());

    return retMap;
  }

  private boolean isDeleted(final TabvProjectSubVariant entity) {
    return CommonUtilConstants.CODE_YES.equals(entity.getDeletedFlag());
  }


  /**
   * @param pidcVersionId Long
   * @param includeDeleted boolean
   * @return Map<Long, PidcSubVariant>
   * @throws DataException
   */
  public Map<Long, PidcSubVariant> getSubVariantsForVersion(final Long pidcVersionId, final boolean includeDeleted)
      throws DataException {
    Map<Long, PidcSubVariant> subVarMap = new HashMap<>();
    PidcVersionLoader pidcVersLoader = new PidcVersionLoader(getServiceData());
    TPidcVersion tabvPidcVersion = pidcVersLoader.getEntityObject(pidcVersionId);
    if (null != tabvPidcVersion.getTabvProjectVariants()) {
      for (TabvProjectVariant variant : tabvPidcVersion.getTabvProjectVariants()) {
        subVarMap.putAll(getSubVariants(variant.getVariantId(), includeDeleted));
      }
    }
    return subVarMap;
  }

  /**
   * @param subvarIds
   * @return nameValIdVarIdMap
   * @throws DataException
   */
  public Map<Long, Long> getVarIdsForNameValIds(final Set<Long> subvarIds) throws DataException {
    Map<Long, Long> nameValIdVarIdMap = new HashMap<>();
    getDataObjectByID(subvarIds).values().stream().forEach(var -> {
      nameValIdVarIdMap.put(var.getNameValueId(), var.getId());
    });
    return nameValIdVarIdMap;
  }

  /**
   * @param subVariantId subVariantId
   * @return
   * @throws DataException - Error while fetching subvariant
   */
  public String getExtendedName(final Long subVariantId) throws DataException {
    PidcSubVariant pidcSubVariant = getDataObjectByID(subVariantId);
    return EXTERNAL_LINK_TYPE.SUB_VARIANT.getTypeDisplayText() + ": " + getPidcTreePath(pidcSubVariant) +
        pidcSubVariant.getName();
  }

  /**
   * @return the extend path - the tree structure of the pidcSubVariant
   * @throws DataException error while retrieving data
   */
  private String getPidcTreePath(final PidcSubVariant pidcSubVariant) throws DataException {
    PidcVersionLoader pidcVersLdr = new PidcVersionLoader(getServiceData());
    PidcVersion pidcVers = pidcVersLdr.getDataObjectByID(pidcSubVariant.getPidcVersionId());
    PidcVariant pidcVariant =
        new PidcVariantLoader(getServiceData()).getDataObjectByID(pidcSubVariant.getPidcVariantId());
    return pidcVersLdr.getPidcTreePath(pidcVers.getId()) + pidcVers.getName() + "->" + pidcVariant.getName() + "->";
  }
}
