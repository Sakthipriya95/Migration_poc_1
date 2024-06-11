/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.ApicUser;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtilConstants;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TabvApicUser;


/**
 * Class with feilds related to open points
 *
 * @author bru2cob
 */
public class QnaireAnsOpenPoint extends AbstractCdrObject implements Comparable<ReviewQnaireAnswer> {

  /**
   * NOT_APPL
   */
  private static final String NOT_APPL = "<NOT APPLICABLE>";
  /**
   * id
   */
  private Long qnaireAnsOPObjID;

  /**
   * review qnaire ans instance
   */
  private final ReviewQnaireAnswer reviewQsAns;

  /**
   * @param cdrDataProvider CDRDataProvider
   * @param qnaireAnsOPObjID Long id of ReviewQnaireAnswer
   * @param reviewQsAns ReviewQnaireAnswer obj
   */
  public QnaireAnsOpenPoint(final CDRDataProvider cdrDataProvider, final ReviewQnaireAnswer reviewQsAns,
      final Long qnaireAnsOPObjID) {
    super(cdrDataProvider, qnaireAnsOPObjID);
    this.reviewQsAns = reviewQsAns;
    this.qnaireAnsOPObjID = qnaireAnsOPObjID;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.QNAIRE_ANS_OPEN_POINTS;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return "";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return "";
  }

  /**
   * @return Open points answer
   */
  public String getOpenPoints() {
    return CommonUtils.isNotNull(getEntityProvider().getDbQnaireAnsOpenPoint(getID()))
        ? getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getOpenPoints() : "";
  }

  /**
   * @return open points display string
   */
  public String getOpenPointsUIString() {
    if (isHeading()) {
      return "";
    }
    if (this.reviewQsAns.showOpenPoints()) {
      return getOpenPoints();
    }
    return NOT_APPL;
  }

  /**
   * @return Measure
   */
  public String getMeasure() {
    return CommonUtils.isNotNull(getEntityProvider().getDbQnaireAnsOpenPoint(getID()))
        ? getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getMeasure() : "";
  }

  /**
   * @return open points display string
   */
  public String getMeasureUIString() {
    if (isHeading()) {
      return "";
    }
    if (this.reviewQsAns.showMeasures()) {
      return getMeasure();
    }
    return NOT_APPL;
  }

  /**
   * @return Measure
   */
  public Calendar getCompletionDate() {
    return CommonUtils.isNotNull(getEntityProvider().getDbQnaireAnsOpenPoint(getID()))
        ? ApicUtil.timestamp2calendar(getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getCompletionDate()) : null;
  }

  /**
   * @return open points display string
   */
  public String getCompletionDateUIString() {
    if (isHeading()) {
      return "";
    }
    if (this.reviewQsAns.showCompletionDate()) {
      return getCompletionDateAsString();
    }
    return NOT_APPL;
  }

  /**
   * @return completion date as string
   */
  public String getCompletionDateAsString() {
    return getCompletionDate() == null ? "" : ApicUtil.getFormattedDate(DateFormat.DATE_FORMAT_09, getCompletionDate());

  }

  /**
   * @return Measure
   */
  public String getResponsibleAsString() {
    TabvApicUser tabvApicUser = getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getTabvApicUser();
    if (null == tabvApicUser) {
      return "";
    }
    return getApicDataProvider().getApicUser(tabvApicUser.getUsername()).getDisplayName();
  }

  /**
   * @return responsible display string
   */
  public String getResponsibleUIString() {
    if (isHeading()) {
      return "";
    }
    if (this.reviewQsAns.showResponsible()) {
      return getResponsibleAsString();
    }
    return NOT_APPL;
  }

  /**
   * @return Measure
   */
  public ApicUser getResponsible() {
    return getEntityProvider().getDbQnaireAnsOpenPoint(getID()) == null ? null
        : getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getTabvApicUser() == null ? null : getApicDataProvider()
            .getApicUser(getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getTabvApicUser().getUsername());
  }

  /**
   * @return Measure
   */
  public String getResult() {
    return CommonUtils.isNotNull(getEntityProvider().getDbQnaireAnsOpenPoint(getID()))
        ? getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getResult() : "";
  }

  /**
   * @return open points display string
   */
  public Object getResultUIString() {
    if (isHeading()) {
      return "";
    }
    return isResultSet() ? CommonUtilConstants.DISPLAY_YES : CommonUtilConstants.DISPLAY_NO;

  }

  /**
   * @return true if result
   */
  public boolean isResultSet() {
    return CommonUtils.getBooleanType(getResult());
  }

  /**
   * Sets the ID of this {@link ReviewQnaireAnswer}
   *
   * @param revQnaireAnsID Primary Key ID
   */
  void setID(final Long qnaireAnsOPObjID) {
    this.qnaireAnsOPObjID = qnaireAnsOPObjID;
    if (null != qnaireAnsOPObjID) {
      getDataCache().getQnaireAnsOpenPointMap().put(qnaireAnsOPObjID, this);
    }
  }

  /**
   * @return ReviewQnaireAnsObjID
   */
  public Long getReviewQnaireAnsObjID() {
    return getEntityProvider().getDbQnaireAnsOpenPoint(getID()).getTRvwQnaireAnswer().getRvwAnswerId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getID() {
    return this.qnaireAnsOPObjID;
  }


  /**
   * @return true, if this is a heading
   */
  public boolean isHeading() {
    return this.reviewQsAns.isHeading();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final ReviewQnaireAnswer arg0) {
    // TODO Auto-generated method stub
    return 0;
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
}
