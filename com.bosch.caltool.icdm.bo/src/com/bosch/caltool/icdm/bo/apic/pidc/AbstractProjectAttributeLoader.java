/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic.pidc;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import com.bosch.caltool.datamodel.core.IModelType;
import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.ApicConstants.PROJ_ATTR_USED_FLAG;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectAttribute;


/**
 * @author bne4cob
 * @param <D> data object
 * @param <E> entity object
 */
public abstract class AbstractProjectAttributeLoader<D extends IProjectAttribute, E>
    extends AbstractBusinessObject<D, E> {

  /**
   * @param serviceData ServiceData
   * @param type model type
   * @param entityType entity type class
   */
  protected AbstractProjectAttributeLoader(final ServiceData serviceData, final IModelType type,
      final Class<E> entityType) {
    super(serviceData, type, entityType);
  }

  /**
   * Check if the attribute is valid (visible) based on the attribute dependencies. This method assumes: - that an
   * attribute depends on only one attribute - that if an attribute depends on the used flag, only one dependency is
   * defined
   *
   * @param nameValueId PIDC Version ID
   * @param expNameValueLevel expected name value level
   * @param attrDepMap Map of valid attribute dependencies, excluding deleted dependencies, deleted attribute and
   *          deleted values. Key - dependency value ID. for dependency to
   * @param refProjAttributeMap the list of attributes defined in the PIDC
   * @param invisibleAttrSet set of attributes, already identified as invisible
   * @return true if the attr is valid
   */
  protected boolean isVisible(final Long nameValueId, final int expNameValueLevel,
      final Map<Long, AttrNValueDependency> attrDepMap, final Map<Long, IProjectAttribute> refProjAttributeMap,
      final Set<Long> invisibleAttrSet) {

    if (attrDepMap.isEmpty()) {
      return true;
    }

    // get the attribute on which this attribute depends on
    AttrNValueDependency firstDependency = attrDepMap.values().iterator().next();
    final Long dependencyAttrID = firstDependency.getDependentAttrId();

    if (invisibleAttrSet.contains(dependencyAttrID)) {
      // Dependency attribute is invisible. Hence this is also invisible
      return false;
    }

    boolean nameAttrFlag = isNameAttribute(dependencyAttrID, expNameValueLevel);

    // ICDM-196 null check placed if the depency is at higher level
    // ICDM-963
    IProjectAttribute depProjAttr = refProjAttributeMap.get(dependencyAttrID);
    if (!nameAttrFlag && ((depProjAttr == null) || depProjAttr.isAtChildLevel())) {
      return true;
    }

    // check, if attribute depends on the used flag ICDM-962
    if (firstDependency.getDependentValueId() == null) {
      // attribute depends on the used flag
      // check the used flag of the dependency attribute in the PIDC
      // ICDM-133
      return PROJ_ATTR_USED_FLAG.YES.getDbType().equals(depProjAttr.getUsedFlag());
    }
    Long projDepAttrValId;
    // If the dependent attr is Project Name - ICDM-2118
    if (nameAttrFlag) {
      projDepAttrValId = nameValueId;
    }
    else {
      // get the value defined in the PIDC for the dependency attribute
      projDepAttrValId = depProjAttr.getValueId();
    }
    // check if a value is defined for the attribute and not a Variant or Sub variant
    if (projDepAttrValId == null) {
      return depProjAttr.isAtChildLevel();
    }

    return attrDepMap.containsKey(projDepAttrValId);

  }

  private boolean isNameAttribute(final Long attrID, final int expNameValueLevel) {
    AttributeLoader attrValLdr = new AttributeLoader(getServiceData());
    BigDecimal attrLvlBg = attrValLdr.getEntityObject(attrID).getAttrLevel();
    Long attrLevel = attrLvlBg == null ? null : attrLvlBg.longValue();

    return CommonUtils.isEqual(attrLevel, Long.valueOf(expNameValueLevel));
  }

  /**
   * @param pidcId PIDC ID
   * @return Returns whether the read access to PIDC is available
   * @throws DataException error file fetching access rights
   */
  protected boolean isReadable(final Long pidcId) throws DataException {
    ApicAccessRightLoader accessRightLoader = new ApicAccessRightLoader(getServiceData());
    return accessRightLoader.isCurrentUserApicWrite() || accessRightLoader.isCurrentUserApicReadAll() ||
        (new NodeAccessLoader(getServiceData())).isCurrentUserRead(pidcId);
  }

  /**
   * @param model model
   * @param attrId attriute ID
   * @return true, if attribute is marked as hidden for this PIDC version
   */
  protected boolean isHiddenAttribute(final PidcVersionAttributeModel model, final Long attrId) {
    return (model != null) && model.isHiddenAttr(attrId);
  }

  /**
   * Note : this is only a check based on the data available in the project attribute object. So it should be used only
   * after the project attribute is created via attribute model loader
   *
   * @param projAttr project attribute
   * @return true, if details are hidden to current user.
   */
  public static boolean isDetailsHiddenToCurrentUser(final IProjectAttribute projAttr) {
    // If attribute is hidden to the user, used flag is null. Else it will be 'not defined' at least
    return projAttr.isAttrHidden() && (projAttr.getUsedFlag() == null);
  }


}
