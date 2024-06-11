/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.PIDCVariant;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwVariant;


/**
 * Class for Review Result Variant
 *
 * @author rgo7cob
 */
public class CDRReviewVariant extends AbstractCdrObject implements Comparable<CDRReviewVariant> {

  /**
   * Tooltip buffer initial size
   */
  private static final int TOOLTIP_INITIAL_SIZE = 50;

  /**
   * Constructor
   *
   * @param dataProvider CDR data provider
   * @param objID primary key
   */
  protected CDRReviewVariant(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllReviewVariants().put(objID, this);
  }


  /**
   * @return the CDR result object
   */
  public CDRResult getResult() {
    final TRvwVariant dbRvwVar = getEntityProvider().getDbRvwVaraint(getID());
    return getDataCache().getCDRResult(dbRvwVar.getTRvwResult().getResultId());
  }

  /**
   * @return the Variant associated
   */
  public PIDCVariant getVariant() {
    final Long variantID = getEntityProvider().getDbRvwVaraint(getID()).getTabvProjectVariant().getVariantId();
    return getApicDataProvider().getPidcVaraint(variantID);
  }


  /**
   * Returns CDREntityType.CDR_RES_VARIANTS
   * <p>
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RES_VARIANTS;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRReviewVariant obj) {
    return ApicUtil.compare(getName(), obj.getName()) == 0 ? ApicUtil.compare(getID(), obj.getID())
        : ApicUtil.compare(getName(), obj.getName());
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
   * @return true if the review varaint is deleted
   */
  public boolean isDeleted() {
    return getEntityProvider().getDbRvwVaraint(getID()) == null;
  }

  /**
   * Compare using sort columns
   *
   * @param other CDRReviewVaraints
   * @param sortColumn sortColumn
   * @return the comparison result
   */
  public int compareTo(final CDRReviewVariant other, final int sortColumn) {
    return 0;
  }

  /**
   * @return true if the review variant is mapped one.
   */
  public boolean isMappedVariant() {

    // If only one Review variant return false
    if (getResult().getReviewVarMap().size() == 1) {
      return false;
    }


    for (CDRReviewVariant rvwVar : getResult().getReviewVarMap().values()) {
      // If current date is greater than any of the dates then return true.
      if (ApicUtil.compare(getCreatedDate(), rvwVar.getCreatedDate()) == 1) {
        return true;
      }
    }

    return false;
  }

  /**
   * Returns the attribute value description
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getResult().getDescription();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbRvwVaraint(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbRvwVaraint(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRvwVaraint(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRvwVaraint(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbRvwVaraint(getID()).getVersion();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getResult().getName();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getToolTip() {
    CDRResult result = getResult();

    StringBuilder toolTip = new StringBuilder(TOOLTIP_INITIAL_SIZE);
    toolTip.append(result.getToolTip());

    if (!CommonUtils.isEqual(getVariant(), result.getVariant())) {
      toolTip.append("\nReview Variant : ").append(result.getVariant().getName()).append("\nMapped Variant : ")
          .append(getVariant().getName());
    }

    return toolTip.toString();
  }
}
