/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.a2l.jpa.bo;

import java.util.Calendar;
import java.util.SortedSet;

import com.bosch.caltool.a2l.jpa.A2LDataProvider;
import com.bosch.caltool.a2l.jpa.A2LEntityType;
import com.bosch.caltool.a2l.jpa.AbstractA2LObject;
import com.bosch.caltool.apic.jpa.bo.ApicDataProvider;
import com.bosch.caltool.apic.jpa.bo.NodeAccessRight;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.model.fc2wp.FC2WPVersion;


/**
 * FC2WPDefBO to handle access rights
 *
 * @author gge6cob
 */
@Deprecated
public class FC2WPDefBO extends AbstractA2LObject {

  /**
   * FC2WPVersion
   */
  private final FC2WPVersion fc2wpVersion;

  /**
   * Constructor
   *
   * @param apicDataProvider Data Provider
   * @param fc2wpVersion Object ID
   * @param fc2wpDefId FC2WPDef
   */
  public FC2WPDefBO(final A2LDataProvider apicDataProvider, final FC2WPVersion fc2wpVersion, final Long fc2wpDefId) {
    super(apicDataProvider, fc2wpDefId);
    this.fc2wpVersion = fc2wpVersion;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * Returns APIC data provider
   *
   * @return ApicDataProvider
   */
  protected ApicDataProvider getApicDataProvider() {
    return getDataCache().getDataProvider().getApicDataProvider();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUserDisplayName() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUserDisplayName() {
    // TODO Auto-generated method stub
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return null;
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
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    boolean isWorkingSet = this.fc2wpVersion.isWorkingSet();
    if (getCurrentUserAccessRights() != null) {
      return getCurrentUserAccessRights().hasWriteAccess() && isWorkingSet;
    }
    return false;
  }

  /**
   * Get a sorted set of the PIDCs AccessRights
   *
   * @return access rights
   */
  public SortedSet<NodeAccessRight> getAccessRights() {
    return getApicDataProvider().getNodeAccessRights(this.fc2wpVersion.getFcwpDefId());
  }

  /**
   * Get the current users access right on this PIDC
   *
   * @return The NodeAccessRight of the current user If the user has no special access rights return NULL
   */
  public NodeAccessRight getCurrentUserAccessRights() {
    return getApicDataProvider().getNodeAccRight(this.fc2wpVersion.getFcwpDefId());
  }

  /**
   * @return long
   */
  public long getFC2WPDefId() {
    return this.fc2wpVersion.getFcwpDefId();
  }


  /**
   * @return the fc2wpVersion
   */
  public FC2WPVersion getFc2wpVersion() {
    return this.fc2wpVersion;
  }

  /**
   * Returns whether the logged in user has privilege to modify access rights to this Project ID Card.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  public boolean canModifyAccessRights() {
    if (getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }
    NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasGrantOption()) {
      return true;
    }
    return false;
  }

  /**
   * @return boolean if the user has the access to change the owner flag
   */
  public boolean canModifyOwnerRights() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if (((curUserAccRight != null) && curUserAccRight.isOwner()) ||
        getApicDataProvider().getCurrentUser().hasApicWriteAccess()) {
      return true;
    }

    return false;
  }

  /**
   * Returns whether the logged in user is owner.
   *
   * @return <code>true</code> if current user can modify the access rights.
   */
  public boolean isOwner() {
    final NodeAccessRight curUserAccRight = getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.isOwner()) {
      return true;
    }
    return false;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return A2LEntityType.FC2WP_DEF;
  }
}
