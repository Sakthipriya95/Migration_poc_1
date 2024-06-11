/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.dmframework.bo.AbstractDataProvider;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.Language;

// ICDM-763
/**
 * Link Business object
 *
 * @author bru2cob
 */
@Deprecated
public class Link extends ApicObject implements Comparable<Link> {

  /**
   * Constructor
   *
   * @param apicDataProvider AbstractDataProvider
   * @param objID Long
   */
  protected Link(final AbstractDataProvider apicDataProvider, final Long objID) {
    super(apicDataProvider, objID);
  }

  /**
   * returns compare result of two links
   * <p>
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final Link arg0) {
    int compareResult = ApicUtil.compare(getDesc(), arg0.getDesc());
    if (compareResult == 0) {
      compareResult = ApicUtil.compareLong(getID(), arg0.getID());
    }
    return compareResult;

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
   * @return Node ID
   */
  public Long getNodeID() {
    return getEntityProvider().getDbLink(getID()).getNodeId().longValue();
  }

  /**
   * @return Node Type String
   */
  public String getNodeType() {
    return getEntityProvider().getDbLink(getID()).getNodeType();
  }

  /**
   * @return link URL
   */
  public String getLink() {
    return getEntityProvider().getDbLink(getID()).getLinkUrl();
  }


  /**
   * @return description in English
   */
  public String getDescEng() {
    return getEntityProvider().getDbLink(getID()).getDescEng();
  }

  /**
   * description in german is returned. if null, then english description is given
   *
   * @return description in German
   */
  public String getDescGer() {
    if (getEntityProvider().getDbLink(getID()).getDescGer() != null) {
      return getEntityProvider().getDbLink(getID()).getDescGer();
    }
    // return empty string if German desc is null
    return "";
  }

  /**
   * Description based on the current language.
   * <p>
   * If current language is german and german description is null, then english description is given
   *
   * @return desc based on the language
   */
  public String getDesc() {
    if (linkIdValid()) {
      String returnValue = "";

      if (getDataCache().getLanguage() == Language.ENGLISH) {
        returnValue = getEntityProvider().getDbLink(getID()).getDescEng();
      }
      else if (getDataCache().getLanguage() == Language.GERMAN) {
        if (getEntityProvider().getDbLink(getID()).getDescGer() == null) {
          returnValue = getEntityProvider().getDbLink(getID()).getDescEng();
        }
        else {
          returnValue = getEntityProvider().getDbLink(getID()).getDescGer();
        }
      }
      return returnValue;
    }
    return "";
  }

  /**
   * checks whether the id is valid or not
   *
   * @return true if link ID is valid
   */
  private boolean linkIdValid() {
    return getEntityProvider().getDbLink(getID()) != null;
  }

  /**
   * @return created Date
   */
  @Override
  public Calendar getCreatedDate() {
    if (linkIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbLink(getID()).getCreatedDate());
    }
    return null;
  }

  /**
   * @return created User
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbLink(getID()).getCreatedUser();
  }

  /**
   * @return modified User
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbLink(getID()).getModifiedUser();
  }


  /**
   * @return modified Date
   */
  @Override
  public Calendar getModifiedDate() {
    if (linkIdValid()) {
      return ApicUtil.timestamp2calendar(getEntityProvider().getDbLink(getID()).getModifiedDate());
    }
    return null;
  }

  /**
   * @return version
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbLink(getID()).getVersion().longValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    // description is returned as it is more meaningful
    return getDesc();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.T_LINK;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getDesc();
  }


}
