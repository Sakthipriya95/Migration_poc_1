/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.jpa.bo;

import java.util.Calendar;

import com.bosch.caltool.apic.jpa.bo.Attribute;
import com.bosch.caltool.apic.jpa.bo.AttributeValue;
import com.bosch.caltool.apic.jpa.bo.PIDCAttribute;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.database.entity.cdr.TRvwAttrValue;
import com.bosch.caltool.icdm.model.apic.ApicConstants;


/**
 * Class for Review Result Attribute Values
 *
 * @author rgo7cob
 */
public class CDRReviewAttrValue extends AbstractCdrObject implements Comparable<CDRReviewAttrValue> {

  /**
   * Constructor
   *
   * @param dataProvider CDR data provider
   * @param objID primary key
   */
  protected CDRReviewAttrValue(final CDRDataProvider dataProvider, final Long objID) {
    super(dataProvider, objID);
    dataProvider.getDataCache().getAllReviewAttrVal().put(objID, this);
  }


  /**
   * @return the CDR result object
   */
  public CDRResult getResult() {
    final TRvwAttrValue dbRvwAttrVal = getEntityProvider().getDbRvwAttrVal(getID());
    return getDataCache().getCDRResult(dbRvwAttrVal.getTRvwResult().getResultId());
  }

  /**
   * @return the attribute value
   */
  public AttributeValue getAttrValue() {
    final TRvwAttrValue dbRvwAttrVal = getEntityProvider().getDbRvwAttrVal(getID());
    if (null == dbRvwAttrVal.getTabvAttrValue()) {
      return null;
    }
    return getApicDataProvider().getAttrValue(dbRvwAttrVal.getTabvAttrValue().getValueId());
  }

  /**
   * @return the Attribute associated
   */
  public Attribute getAttribute() {
    // ICDM-1238
    final TRvwAttrValue dbRvwAttrVal = getEntityProvider().getDbRvwAttrVal(getID());
    return getApicDataProvider().getAttribute(dbRvwAttrVal.getTabvAttribute().getAttrId());
  }

  /**
   * Returns the attribute value name, <br>
   * <USED> if used flag is Y and <NOT USED> if used flag is N (iCDM-1317)
   * <p>
   *
   * @return Value
   */
  public String getValue() {
    // Pidc attr can be null only for variant code attr will not be part of all attributes.
    if (getAttribute().getAttrLevel() == ApicConstants.VARIANT_CODE_ATTR) {
      return getAttrValue().getName();
    }
    PIDCAttribute pidcAttr = getResult().getPidcVersion().getAttributesAll().get(getAttribute().getID());
    if (pidcAttr.isHidden() && !pidcAttr.isReadable()) {
      return ApicConstants.HIDDEN_VALUE;
    }
    if (CommonUtils.isNull(getAttrValue())) {
      return CommonUtils.isEqual(getUsedFlag(), ApicConstants.YES) ? ApicConstants.USED : ApicConstants.NOT_USED;
    }
    return getAttrValue().getName();
  }

  /**
   * iCDM-1317 <br>
   * Get the Used flag information "Y" is Used, "N" if not used
   *
   * @return Y if <USED> else N if <NOT USED>
   */
  public String getUsedFlag() {
    final TRvwAttrValue dbRvwAttrVal = getEntityProvider().getDbRvwAttrVal(getID());
    return dbRvwAttrVal.getUsed();
  }

  /**
   * Returns CDREntityType.CDR_RES_ATTR_VALUE
   * <p>
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return CDREntityType.CDR_RES_ATTR_VALUE;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final CDRReviewAttrValue obj) {
    return ApicUtil.compare(getName(), obj.getName());
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
   * Compare using sort columns
   *
   * @param other rvwAttrVal2
   * @param sortColumn sortColumn
   * @return the comparison result
   */
  public int compareTo(final CDRReviewAttrValue other, final int sortColumn) {
    int compareResult;

    switch (sortColumn) {
      // sort on Attr Name
      case ApicConstants.SORT_ATTRNAME:
        compareResult = ApicUtil.compare(getName(), other.getName());
        break;
      // sort on Attr Value
      case ApicConstants.SORT_ATTR_VAL_SEC:
        compareResult = ApicUtil.compare(getValue(), other.getValue());
        break;
      default:
        compareResult = ApicConstants.OBJ_EQUAL_CHK_VAL;
        break;
    }

    if (compareResult == ApicConstants.OBJ_EQUAL_CHK_VAL) {
      // compare result is equal, compare the attribute name
      compareResult = compareTo(other);
    }

    return compareResult;
  }


  /**
   * Returns the attribute value description
   * <p>
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    return getAttribute().getDescription();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbRvwAttrVal(getID()).getCreatedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbRvwAttrVal(getID()).getModifiedUser();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRvwAttrVal(getID()).getCreatedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbRvwAttrVal(getID()).getModifiedDate());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbRvwAttrVal(getID()).getVersion();
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getAttribute().getName();
  }

}
