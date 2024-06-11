/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo; // NOPMD by bne4cob on 6/20/14 10:27 AM


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.database.entity.apic.TAliasDetail;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectidcard;

/**
 * This class represents BO for alias definition
 *
 * @author rgo7cob
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
public class AliasDefinition extends ApicObject implements Comparable<AliasDefinition> {

  private final List<PIDCard> projects = new ArrayList<>();
  private Map<Long, AliasDetail> aliasDetAttrMap;
  private Map<Long, AliasDetail> aliasDetAttrValMap;


  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getName();
  }


  /**
   * the one and only constructor
   *
   * @param apicDataProvider data provider
   * @param aliasDefID ID
   */
  public AliasDefinition(final ApicDataProvider apicDataProvider, final Long aliasDefID) {
    super(apicDataProvider, aliasDefID);
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbAliasDefinition(getID()).getVersion();
  }

  /**
   * Get the creation date of the Char
   *
   * @return The date when the Char has been created in the database
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbAliasDefinition(getID()).getCreatedDate());

  }

  /**
   * Get the ID of the user who has created the Char
   *
   * @return The ID of the user who has created the Char
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbAliasDefinition(getID()).getCreatedUser();
  }

  /**
   * Get the date when the Char has been modified the last time
   *
   * @return The date when the Char has been modified the last time
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbAliasDefinition(getID()).getModifiedDate());
  }

  /**
   * Get the user who has modified the Char the last time
   *
   * @return The user who has modified the Char the last time
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbAliasDefinition(getID()).getModifiedUser();
  }


  /**
   * @return the alias Defnition name
   */
  public String getAliasDefName() {
    return getEntityProvider().getDbAliasDefinition(getID()).getAdName();


  }

  /**
   * {@inheritDoc} returns compare result of two chars
   */
  @Override
  public int compareTo(final AliasDefinition arg0) {

    return ApicUtil.compare(getName(), arg0.getName());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * {@inheritDoc} return object details in Map
   */
  @Override
  public Map<String, String> getObjectDetails() {
    return new ConcurrentHashMap<>();
  }

  /**
   * @return the projects associated to the Alias Definition
   */
  public List<PIDCard> getProjects() {
    List<TabvProjectidcard> tabvProjectidcards =
        getEntityProvider().getDbAliasDefinition(getID()).getTabvProjectidcards();
    for (TabvProjectidcard tabvPIDC : tabvProjectidcards) {
      this.projects.add(getDataCache().getPidc(tabvPIDC.getProjectId()));
    }

    return this.projects;
  }

  /**
   * fille the alias details map
   */
  public void fillAliasDetMap(final boolean doRefresh) {
    if ((this.aliasDetAttrMap == null) || doRefresh) {
      // Initialize the fields
      this.aliasDetAttrMap = new ConcurrentHashMap<>();
      this.aliasDetAttrValMap = new ConcurrentHashMap<>();
      List<TAliasDetail> tAliasDetails = getEntityProvider().getDbAliasDefinition(getID()).getTAliasDetails();

      if (tAliasDetails != null) {
        for (TAliasDetail tAliasDetail : tAliasDetails) {
          AliasDetail aliasDetail = new AliasDetail(getDataCache().getDataProvider(), tAliasDetail.getAliasDetailsId());
          addAliasDetailToMap(aliasDetail);
        }
      }
    }
  }


  /**
   * Fill the map with the attr id or value id as key. Method made as public since the alais detail will also be added
   * to the map
   *
   * @param aliasDetail aliasDetail
   */
  public void addAliasDetailToMap(final AliasDetail aliasDetail) {
    if (aliasDetail.getAttrValue() == null) {
      this.aliasDetAttrMap.put(aliasDetail.getAttribute().getAttributeID(), aliasDetail);
    }
    else {
      this.aliasDetAttrValMap.put(aliasDetail.getAttrValue().getValueID(), aliasDetail);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {

    return getAliasDefName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.ALIAS_DEFINITION;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * @return the aliasDetAttrMap
   */
  public Map<Long, AliasDetail> getAliasDetAttrMap() {
    fillAliasDetMap(false);
    return this.aliasDetAttrMap;
  }


  /**
   * @return the aliasDetAttrValMap
   */
  public Map<Long, AliasDetail> getAliasDetAttrValMap() {
    fillAliasDetMap(false);
    return this.aliasDetAttrValMap;
  }


  /**
   * @param attr attribute
   * @return the effective alias name for attribute
   */
  public String getEffAttrAliasName(final Attribute attr) {
    AliasDetail aliasDetail = getAliasDetAttrMap().get(attr.getID());
    if (aliasDetail != null) {
      return aliasDetail.getAliasName();
    }
    return null;
  }

  /**
   * @param attrVal attrVal
   * @return the effective alias name for attr Value
   */
  public String getEffValAliasName(final AttributeValue attrVal) {
    AliasDetail aliasDetail = getAliasDetAttrValMap().get(attrVal.getID());
    if (aliasDetail != null) {
      return aliasDetail.getAliasName();
    }
    return null;
  }


  /**
   * @return the sorted set based on attributes
   */
  public SortedSet<AliasDetail> getSortedAttrAlias() {
    SortedSet<AliasDetail> attrSortedSet = new TreeSet<>(new Comparator<AliasDetail>() {

      @Override
      public int compare(final AliasDetail det1, final AliasDetail det2) {
        return det1.getAttribute().compareTo(det2.getAttribute());
      }

    });
    attrSortedSet.addAll(getAliasDetAttrMap().values());
    return attrSortedSet;
  }

  /**
   * @return the sorted set based on attribute of the attr Values to disply in the UI
   */
  public SortedSet<AliasDetail> getSortedValueAlias() {
    SortedSet<AliasDetail> attrValSorted = new TreeSet<>(new Comparator<AliasDetail>() {

      @Override
      public int compare(final AliasDetail det1, final AliasDetail det2) {
        return det1.getAttrValue().getAttribute().compareTo(det2.getAttrValue().getAttribute());
      }

    });
    attrValSorted.addAll(getAliasDetAttrValMap().values());
    return attrValSorted;
  }


  /**
   * @return the access rights of the given Id
   */
  public SortedSet<NodeAccessRight> getAccessRights() {
    return getDataLoader().getNodeAccessRights(getID());
  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  public boolean canModifyAccessRights() {
    // ICDM-1007
    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasGrantOption()) {
      return true;
    }
    return false;
  }


  @Override
  public boolean isModifiable() {
    if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess()) {
      return true;
    }
    return false;
  }

  /**
   * Get the current users access right on this PIDC
   *
   * @return The NodeAccessRight of the current user If the user has no special access rights return NULL
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    // Icdm-346
    return getDataCache().getNodeAccRights(getID());
  }
}