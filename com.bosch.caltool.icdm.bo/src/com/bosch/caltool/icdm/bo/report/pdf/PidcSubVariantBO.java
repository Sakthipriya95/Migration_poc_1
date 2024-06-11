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
import com.bosch.caltool.icdm.bo.apic.pidc.PidcSubVariantAttributeLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantAttribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionWithDetails;

/**
 * @author TRL1COB
 */
public class PidcSubVariantBO extends AbstractSimpleBusinessObject {

  private final PidcVersionWithDetails pidcVersionWithDetails;

  private final PidcSubVariant pidcSubVariant;

  private boolean childrenLoaded;

  private final ConcurrentMap<Long, PidcSubVariantAttribute> allAttrMap = new ConcurrentHashMap<>();

  private final ConcurrentMap<Long, PidcSubVariantAttribute> subVarAttrs = new ConcurrentHashMap<>();


  /**
   * @param serviceData
   * @param pidcVersionWithDetails
   * @param pidcSubVariant
   */
  public PidcSubVariantBO(final ServiceData serviceData, final PidcVersionWithDetails pidcVersionWithDetails,
      final PidcSubVariant pidcSubVariant) {
    super(serviceData);
    this.pidcVersionWithDetails = pidcVersionWithDetails;
    this.pidcSubVariant = pidcSubVariant;
  }


  /**
   * Get a sorted set of attributes using the given sort column
   *
   * @param sortColumn column to sort
   * @return the sorted set
   * @throws IcdmException
   */
  public SortedSet<PidcSubVariantAttribute> getAttributes(final int sortColumn) throws IcdmException {
    SortedSet<PidcSubVariantAttribute> resultSet =
        new TreeSet<>((p1, p2) -> new AbstractPidcVarSubVarBO(this.pidcVersionWithDetails).compare(p1, p2, sortColumn));
  
    resultSet.addAll(getAttributes().values());
  
    return resultSet;
  
  }


  /**
   * Get all Sub Variant attributes
   *
   * @return
   * @throws IcdmException
   */
  private Map<Long, PidcSubVariantAttribute> getAttributes() throws IcdmException {

    if (!this.childrenLoaded) {

      this.allAttrMap.clear();
      this.subVarAttrs.clear();

      fillAllSubVariantAttributes();

      this.subVarAttrs.putAll(this.allAttrMap);

      this.childrenLoaded = true;
    }

    return this.allAttrMap;
  }

  /**
   * Fill Variant attributes
   *
   * @param includeDeleted
   * @throws IcdmException
   */
  private void fillAllSubVariantAttributes() throws IcdmException {

    PidcSubVariantAttribute pidcSubVariantAttr;
    for (Attribute attribute : this.pidcVersionWithDetails.getAllAttributeMap().values()) {
      if (attribute != null) {
        // skip attributes marked as deleted
        if (attribute.isDeleted()) {
          continue;
        }
        Map<Long, PidcSubVariantAttribute> subvarAttrMap =
            this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().get(this.pidcSubVariant.getId());
        if (null == subvarAttrMap) {
          PidcSubVariantAttributeLoader pidcVariantLoader = new PidcSubVariantAttributeLoader(getServiceData());
          subvarAttrMap = pidcVariantLoader.getSubVarAttrForSubVarId(this.pidcSubVariant.getId());
          this.pidcVersionWithDetails.getPidcSubVariantAttributeMap().put(this.pidcSubVariant.getId(), subvarAttrMap);

        }
        pidcSubVariantAttr = subvarAttrMap.get(attribute.getId());
        if (pidcSubVariantAttr != null) {
          this.allAttrMap.put(pidcSubVariantAttr.getAttrId(), pidcSubVariantAttr);
        }
      }
    }
  }

}
