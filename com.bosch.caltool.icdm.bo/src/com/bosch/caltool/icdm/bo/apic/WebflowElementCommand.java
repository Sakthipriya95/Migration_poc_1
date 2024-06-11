package com.bosch.caltool.icdm.bo.apic;

import com.bosch.caltool.dmframework.bo.AbstractCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.database.entity.apic.TWebFlowElement;
import com.bosch.caltool.icdm.model.apic.WebflowElement;


/**
 * Command class for Webflow Element
 *
 * @author dja7cob
 */
public class WebflowElementCommand extends AbstractCommand<WebflowElement, WebflowElementLoader> {

  /**
   *
   */
  private Long eleId;


  /**
   * Constructor
   *
   * @param input input data
   * @param isUpdate if true, update, else create
   * @param isDelete if true, delete mode, else as per 'isUpdate' parameter
   * @param serviceData service Data
   * @throws IcdmException error when initializing
   */
  public WebflowElementCommand(final ServiceData serviceData, final WebflowElement input, final boolean isUpdate,
      final boolean isDelete) throws IcdmException {
    super(serviceData, input, new WebflowElementLoader(serviceData),
        isDelete ? COMMAND_MODE.DELETE : (isUpdate ? COMMAND_MODE.UPDATE : COMMAND_MODE.CREATE));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void create() throws IcdmException {
    TWebFlowElement entity = new TWebFlowElement();
    entity.setElementID(getEleId());
    entity.setVariantID(getInputData().getVariantId());
    entity.setIsDeleted("N");
    setUserDetails(COMMAND_MODE.CREATE, entity);
    persistEntity(entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void update() throws IcdmException {
    WebflowElementLoader loader = new WebflowElementLoader(getServiceData());
    TWebFlowElement entity = loader.getEntityObject(getInputData().getId());

    entity.setElementID(getInputData().getElementId());
    entity.setVariantID(getInputData().getVariantId());
    entity.setIsDeleted(getInputData().getIsDeleted() ? "Y" : "N");

    setUserDetails(COMMAND_MODE.UPDATE, entity);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void delete() throws IcdmException {
    // NA
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

  /**
   * @return the eleId
   */
  public Long getEleId() {
    return this.eleId;
  }


  /**
   * @param eleId the eleId to set
   */
  public void setEleId(final Long eleId) {
    this.eleId = eleId;
  }
}
