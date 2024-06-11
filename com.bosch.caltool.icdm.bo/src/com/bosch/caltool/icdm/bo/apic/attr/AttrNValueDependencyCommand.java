package com.bosch.caltool.icdm.bo.apic.attr;

import java.util.ArrayList;
import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrDependency;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttrValue;
import com.bosch.caltool.icdm.database.entity.apic.TabvAttribute;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;


/**
 * Command class for AttrNValueDependency
 *
 * @author dmo5cob
 */
public class AttrNValueDependencyCommand extends AbstractCommand<AttrNValueDependency, AttrNValueDependencyLoader> {


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public AttrNValueDependencyCommand(final ServiceData serviceData, final AttrNValueDependency input,
      final boolean isUpdate) throws IcdmException {
    super(serviceData, input, new AttrNValueDependencyLoader(serviceData),
        (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {

    TabvAttrDependency entity = new TabvAttrDependency();

    setValues(entity);

    setUserDetails(COMMAND_MODE.CREATE, entity);

    persistEntity(entity);
  }

  /**
   * @param entity
   */
  private void setValues(final TabvAttrDependency entity) {


    entity.setTabvAttrValue(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId()));
    TabvAttribute tabvAttribute =
        new AttributeLoader(getServiceData()).getEntityObject(getInputData().getDependentAttrId());
    entity.setTabvAttributeD(tabvAttribute);
    if (null == tabvAttribute.getTabvAttrDependenciesD()) {
      tabvAttribute.setTabvAttrDependenciesD(new ArrayList<TabvAttrDependency>());
    }
    tabvAttribute.getTabvAttrDependenciesD().add(entity);
    entity.setTabvAttrValueD(
        new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getDependentValueId()));

    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.YES : ApicConstants.NO);
    entity.setChangeComment(getInputData().getChangeComment());
    if (null != getInputData().getAttributeId()) {
      TabvAttribute attrEntityObject =
          new AttributeLoader(getServiceData()).getEntityObject(getInputData().getAttributeId());
      entity.setTabvAttribute(attrEntityObject);
      List<TabvAttrDependency> deps = attrEntityObject.getTabvAttrDependencies();
      if (null == deps) {
        deps = new ArrayList<TabvAttrDependency>();
      }
      deps.add(entity);
    }
    else if (null != getInputData().getValueId()) {
      TabvAttrValue valEntityObject =
          new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId());
      entity.setTabvAttrValue(valEntityObject);
      List<TabvAttrDependency> deps = valEntityObject.getTabvAttrDependencies();
      if (null == deps) {
        deps = new ArrayList<TabvAttrDependency>();
      }
      deps.add(entity);
    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    AttrNValueDependencyLoader loader = new AttrNValueDependencyLoader(getServiceData());
    TabvAttrDependency entity = loader.getEntityObject(getInputData().getId());
    TabvAttribute attrEntityObject =
        new AttributeLoader(getServiceData()).getEntityObject(getInputData().getAttributeId());
    entity.setTabvAttribute(attrEntityObject);


    entity.setTabvAttrValue(new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getValueId()));
    entity
        .setTabvAttributeD(new AttributeLoader(getServiceData()).getEntityObject(getInputData().getDependentAttrId()));
    entity.setTabvAttrValueD(
        new AttributeValueLoader(getServiceData()).getEntityObject(getInputData().getDependentValueId()));

    entity.setDeletedFlag(getInputData().isDeleted() ? ApicConstants.YES : ApicConstants.NO);
    entity.setChangeComment(getInputData().getChangeComment());

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // not applicable
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
