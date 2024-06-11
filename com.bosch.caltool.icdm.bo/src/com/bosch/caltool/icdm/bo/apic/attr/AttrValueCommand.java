package com.bosch.caltool.icdm.bo.apic.attr;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.general.UserDetailsLoader;
import com.bosch.caltool.icdm.bo.user.ApicAccessRightLoader;
import com.bosch.caltool.icdm.bo.user.NodeAccessLoader;
import com.bosch.caltool.icdm.bo.user.UserDetails;
import com.bosch.caltool.icdm.bo.user.UserLoader;
import com.bosch.caltool.icdm.common.bo.apic.AttributeCommon;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants.CLEARING_STATUS;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValueType;
import com.bosch.caltool.icdm.model.user.User;


/**
 * Command class for AttributeValue
 *
 * @author dmo5cob
 */
public class AttrValueCommand extends AbstractCommand<AttributeValue, AttributeValueLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public AttrValueCommand(final ServiceData serviceData, final AttributeValue input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new AttributeValueLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TabvAttrValue entity = new TabvAttrValue();
    setDetails(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }


  private void setDetails(final TabvAttrValue entity) throws IcdmException {
    AttributeLoader attrLoader = new AttributeLoader(getServiceData());

    Attribute attr = attrLoader.getDataObjectByID(getInputData().getAttributeId());
    TabvAttribute dbAttribute = attrLoader.getEntityObject(getInputData().getAttributeId());

    entity.setTabvAttribute(dbAttribute);

    AttributeValueType valueType = AttributeValueType.getType(attr.getValueType());
    setValueByType(entity, valueType);

    if (valueType != AttributeValueType.ICDM_USER) {
      entity.setValueDescEng(getInputData().getDescriptionEng());
      entity.setValueDescGer(getInputData().getDescriptionGer());
    }

    entity.setDeletedFlag(booleanToYorN(getInputData().isDeleted()));

    setClearingStatus(entity, attr);
    entity.setChangeComment(getInputData().getChangeComment());


    List<TabvAttrValue> tabvAttrValues = dbAttribute.getTabvAttrValues();
    if (null == tabvAttrValues) {
      tabvAttrValues = new ArrayList<>();
    }
    tabvAttrValues.add(entity);
    dbAttribute.setTabvAttrValues(tabvAttrValues);

  }

  private void setClearingStatus(final TabvAttrValue entity, final Attribute attr) throws DataException {
    if (getInputData().isDeleted()) {
      setClearingStatusForDeletedValue(entity);
    }
    else {
      if (getInputData().getClearingStatus() == null) {
        CLEARING_STATUS status;
        if (attr.isNormalized()) {
          status = canModifyValues() ? CLEARING_STATUS.CLEARED : CLEARING_STATUS.NOT_CLEARED;
        }
        else {
          status = CLEARING_STATUS.CLEARED;
        }
        entity.setClearingStatus(status.getDBText());
      }
      else {
        entity.setClearingStatus(getInputData().getClearingStatus());
      }
    }
  }

  /**
   * Set the DB Boolean value
   *
   * @param uiBoolVal uiBoolVal
   */
  private String getDbBoolValue(final String uiBoolVal) {
    if (ApicConstants.BOOLEAN_TRUE_DB_STRING.equalsIgnoreCase(uiBoolVal) ||
        ApicConstants.BOOLEAN_TRUE_STRING.equalsIgnoreCase(uiBoolVal)) {
      return ApicConstants.BOOLEAN_TRUE_DB_STRING;
    }
    return ApicConstants.BOOLEAN_FALSE_DB_STRING;
  }

  private void setValueByType(final TabvAttrValue entity, final AttributeValueType valueType) throws IcdmException {
    String textValueEng = getInputData().getTextValueEng();
    switch (valueType) {
      case BOOLEAN:
        entity.setBoolvalue(getDbBoolValue(getInputData().getBoolvalue()));
        break;
      case DATE:
        setValueForDateType(entity);
        break;
      case NUMBER:
        BigDecimal numValue = getInputData().getNumValue();
        if (null == numValue) {
          throw new IcdmException("Input number is mandatory for Number type attribute values");
        }
        entity.setNumvalue(numValue);
        break;
      case TEXT:
        if (CommonUtils.isEmptyString(textValueEng)) {
          throw new IcdmException("Input text is mandatory for Text type attribute values");
        }
        entity.setTextvalueEng(textValueEng);
        entity.setTextvalueGer(getInputData().getTextValueGer());
        break;
      case HYPERLINK:
        if (CommonUtils.isEmptyString(textValueEng)) {
          throw new IcdmException("Input text is mandatory for Link type attribute values");
        }
        entity.setTextvalueEng(textValueEng);
        break;
      case ICDM_USER:
        setValueForUserType(entity);
        break;
      default:
        break;
    }
    entity.setOthervalue(getInputData().getOtherValue());
  }

  /**
   * @param entity
   * @throws IcdmException
   */
  private void setValueForDateType(final TabvAttrValue entity) throws IcdmException {
    String inputDateValue = getInputData().getDateValue();
    if (inputDateValue == null) {
      throw new IcdmException("Input date is mandatory for date type attribute values");
    }
    try {
      entity.setDatevalue(AttributeCommon.convertAttrDateStringToTimestamp(DateFormat.DATE_FORMAT_15, inputDateValue));
    }
    catch (ParseException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
  }


  /**
   * @param entity
   * @throws DataException
   * @throws NumberFormatException
   */
  private void setValueForUserType(final TabvAttrValue entity) throws DataException {
    UserLoader userLoader = new UserLoader(getServiceData());
    User user = userLoader.getDataObjectByID(getInputData().getUserId());
    UserDetails userDetails = new UserDetailsLoader(getServiceData()).getDataObjectByID(getInputData().getUserId());
    // store user's user id in attributevalue's text value Eng
    entity.setTextvalueEng(user.getDescription());
    // store the user's phone and email in attributevalue's desc
    entity.setValueDescEng(
        "Phone : " + (userDetails.getPhone() == null ? " NOT PROVIDED\n" : userDetails.getPhone() + "\n") + "Email :" +
            (userDetails.getEmail() == null ? " NOT PROVIDED " : userDetails.getEmail()));
    entity.setTabvApicUser(userLoader.getEntityObject(getInputData().getUserId()));
  }

  /**
   * @return
   * @throws DataException
   */
  private boolean canModifyValues() throws DataException {
    return new ApicAccessRightLoader(getServiceData()).getAccessRightsCurrentUser().isApicWrite() ||
        new NodeAccessLoader(getServiceData()).getAllNodeAccessForCurrentUser()
            .containsKey(getInputData().getAttributeId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    TabvAttrValue entity = new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getId());
    setDetails(entity);
    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * @param entity
   */
  private void setClearingStatusForDeletedValue(final TabvAttrValue entity) {
    switch (CLEARING_STATUS.getClearingStatus(getInputData().getClearingStatus())) {
      case CLEARED:
        entity.setClearingStatus(CLEARING_STATUS.DELETED.getDBText());
        break;
      case IN_CLEARING:
      case NOT_CLEARED:
        entity.setClearingStatus(CLEARING_STATUS.REJECTED.getDBText());
        break;
      default:
        break;
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    AttributeValueLoader loader = new AttributeValueLoader(getServiceData());
    TabvAttrValue entity = loader.getEntityObject(getInputData().getId());
    getEm().remove(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {
    // TODO Auto-generated method stub
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean dataChanged() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void validateInput() throws IcdmException {
    // TODO Auto-generated method stub
  }

}
