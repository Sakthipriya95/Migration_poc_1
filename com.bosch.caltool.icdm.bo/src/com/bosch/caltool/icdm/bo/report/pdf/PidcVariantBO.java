/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.report.pdf;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.pidc.PidcVariantAttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author TRL1COB
 */
public class PidcVariantBO extends AbstractSimpleBusinessObject {

  private final PidcVersionWithDetails pidcVersionWithDetails;

  private final PidcVariant pidcVariant;

  private boolean childrenLoaded;

  private final ConcurrentMap<Long, PidcVariantAttribute> allAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcVariantAttribute> varAttrs = new ConcurrentHashMap<>();

  /**
   * @param serviceData
   * @param pidcVersionWithDetails
   * @param pidcVariant
   */
  public PidcVariantBO(final ServiceData serviceData, final PidcVersionWithDetails pidcVersionWithDetails,
      final PidcVariant pidcVariant) {
    super(serviceData);
    this.pidcVersionWithDetails = pidcVersionWithDetails;
    this.pidcVariant = pidcVariant;
  }

  /**
   * @param sortColumn
   * @return
   * @throws IcdmException
   */
  public SortedSet<PidcVariantAttribute> getAttributes(final int sortColumn) throws IcdmException {
    final SortedSet<PidcVariantAttribute> resultSet =
        new TreeSet<>((p1, p2) -> new AbstractPidcVarSubVarBO(this.pidcVersionWithDetails).compare(p1, p2, sortColumn));
    resultSet.addAll(getAttributes().values());
    return resultSet;
  }


  /**
   * Get all variant attributes
   *
   * @return
   * @throws IcdmException
   */
  private Map<Long, PidcVariantAttribute> getAttributes() throws IcdmException {

    if (!this.childrenLoaded) {

      this.allAttrMap.clear();
      this.varAttrs.clear();

      fillAllVariantAttributes();

      this.varAttrs.putAll(this.allAttrMap);

      this.childrenLoaded = true;
    }

    return this.allAttrMap;

  }

  /**
   * Fill Variant attributes
   *
   * @throws IcdmException
   */
  private void fillAllVariantAttributes() throws IcdmException {

    PidcVariantAttribute pidcVariantAttr;

    for (Attribute attribute : this.pidcVersionWithDetails.getAllAttributeMap().values()) {
      if (attribute != null) {
        // skip attributes marked as deleted
        if (attribute.isDeleted()) {
          continue;
        }
        Long pidcVarId = this.pidcVariant.getId();
        Map<Long, PidcVariantAttribute> varAttrMap =
            this.pidcVersionWithDetails.getPidcVariantAttributeMap().get(pidcVarId);
        if (null == varAttrMap) {
          PidcVariantAttributeLoader pidcVariantAttrLoader = new PidcVariantAttributeLoader(getServiceData());
          varAttrMap = pidcVariantAttrLoader.getVarAttrForVariant(pidcVarId);
          this.pidcVersionWithDetails.getPidcVariantAttributeMap().put(pidcVarId, varAttrMap);
        }
        pidcVariantAttr = varAttrMap.get(attribute.getId());
        if (pidcVariantAttr != null) {
          this.allAttrMap.put(pidcVariantAttr.getAttrId(), pidcVariantAttr);
        }
      }
    }
  }

  /**
   * Get a sorted set of the PIDCs Sub Variants
   *
   * @return PIDCSubVariant sorted set
   */
  public synchronized SortedSet<PidcSubVariant> getSubVariantsSet() {

    final SortedSet<PidcSubVariant> resultSet = new TreeSet<>();

    resultSet.addAll(getSubVariantsMap().values());

    return resultSet;
  }

  /**
   * Get a MAP of the PIDCs Sub Variants including deleted sub variants
   *
   * @return PIDCSubVariant
   */
  public synchronized Map<Long, PidcSubVariant> getSubVariantsMap() {

    final Map<Long, PidcSubVariant> subVariantsMap = new ConcurrentHashMap<>();

    for (PidcSubVariant subVar : this.pidcVersionWithDetails.getPidcSubVariantMap().values()) {

      if (subVar.getPidcVariantId().equals(this.pidcVariant.getId())) {
        subVariantsMap.put(subVar.getId(), subVar);
      }
    }

    this.childrenLoaded = true;
    return subVariantsMap;
  }

}
