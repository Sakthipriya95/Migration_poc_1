/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.apic.jpa.bo.PIDCard;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;


/**
 * @author rgo7cob class acts as a wrapper for pidc Favourite Object
 */
public class PidcFavourite implements Comparable<PidcFavourite> {

  /**
   * Defines constant for hash code prime
   */
  private static final int HASH_CODE_PRIME_31 = 31;
  /**
   * pid card
   */
  private final PIDCard pidCard;

  /**
   * string user id nt user id
   */
  private final String userName;


  /**
   * @param pidCard pidCard
   * @param userName nt user id
   */
  public PidcFavourite(final PIDCard pidCard, final String userName) {
    super();
    this.pidCard = pidCard;
    this.userName = userName;
  }


  /**
   * @return the pidCard
   */
  public PIDCard getPidCard() {
    return this.pidCard;
  }


  /**
   * @return the userName
   */
  public String getUserName() {
    return this.userName;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PidcFavourite obj) {
    // Fisrt compare based on pidcard name
    int result = ApicUtil.compare(getPidCard().getName(), obj.getPidCard().getName());
    // based on User id
    if (result == 0) {
      result = ApicUtil.compare(getUserName(), obj.getUserName());
    }
    return result;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    int result = 1;
    result= (HASH_CODE_PRIME_31 * result) + ((getPidCard() == null) ? 0 : getPidCard().getName().hashCode());
   return result;
  }


  /**
   * {@inheritDoc}
   */
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
    PidcFavourite other = (PidcFavourite) obj;
    return CommonUtils.isEqualIgnoreCase(getPidCard().getName(), other.getPidCard().getName());
  }

  /**
   * @param user user
   * @return true if the pidc is hidden
   */
  // ICDM-2400
  public boolean isHidden(final ApicUser user) {
    if (getPidCard().getActiveVersion().isHidden(user)) {
      return true;
    }
    return false;
  }


}
