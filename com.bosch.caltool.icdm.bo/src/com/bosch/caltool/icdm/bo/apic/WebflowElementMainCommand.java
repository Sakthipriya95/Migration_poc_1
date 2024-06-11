/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.apic;

import java.util.List;

import com.bosch.caltool.dmframework.bo.AbstractSimpleCommand;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.model.apic.WebflowElement;

/**
 * @author dja7cob
 */
public class WebflowElementMainCommand extends AbstractSimpleCommand {

  private final Long elementId;

  /**
   * @param serviceData
   * @throws IcdmException
   */
  public WebflowElementMainCommand(final ServiceData serviceData, final Long elementId) throws IcdmException {
    super(serviceData);
    this.elementId = elementId;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  protected void execute() throws IcdmException {
    List<WebflowElement> webflowEleList = new WebflowElementLoader(getServiceData()).getWebFlowElements(this.elementId);
    if (webflowEleList.isEmpty()) {
      throw new InvalidInputException("The element ID not existing. Please provide an existing element ID.");
    }
    if (!validateWebFlowElements(webflowEleList)) {
      throw new InvalidInputException(
          "The element ID is marked as deleted. Only not deleted element IDs are accepted.");
    }
    for (WebflowElement webflowEle : webflowEleList) {
      webflowEle.setIsDeleted(true);
      WebflowElementCommand cmd = new WebflowElementCommand(getServiceData(), webflowEle, true, false);
      executeChildCommand(cmd);
    }
  }

  /**
   * @param webflowEleList
   * @return
   * @throws IcdmException
   */
  private boolean validateWebFlowElements(final List<WebflowElement> webflowEleList) {
    for (WebflowElement webflowElement : webflowEleList) {
      if (webflowElement.getIsDeleted()) {
        return false;
      }
    }
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void doPostCommit() throws IcdmException {

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean hasPrivileges() throws IcdmException {
    return true;
  }

}
