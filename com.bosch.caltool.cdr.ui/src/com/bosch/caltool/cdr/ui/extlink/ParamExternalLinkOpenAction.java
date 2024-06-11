/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.extlink;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.cdr.ui.Activator;
import com.bosch.caltool.icdm.common.bo.general.EXTERNAL_LINK_TYPE;
import com.bosch.caltool.icdm.common.ui.AbstractExternalLinkOpenAction;
import com.bosch.caltool.icdm.common.ui.actions.CommonActionSet;
import com.bosch.caltool.icdm.common.ui.views.ScratchPadViewPart;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.a2l.Parameter;
import com.bosch.caltool.icdm.ws.rest.client.a2l.ParameterServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * Action to add parameter to scratchpad via external link
 *
 * @author bru2cob
 */
public class ParamExternalLinkOpenAction extends AbstractExternalLinkOpenAction {


  /**
   * {@inheritDoc}
   */
  @Override
  public boolean openLink(final Map<String, String> properties) {
    // get the param name
    String paramName = properties.get(EXTERNAL_LINK_TYPE.CDR_FUNCTION_PARAM.getKey());
    String paramType = null;
    String[] strIds;

    // if param name has deliminator then it contains param type
    if (paramName.contains(DELIMITER_MULTIPLE_ID)) {
      // get param name and type
      strIds = paramName.split(DELIMITER_MULTIPLE_ID);
      paramName = strIds[0];
      paramType = strIds[1];
    }

    // get the cdrfuncparam obj based on the param name
    Map<Long, Parameter> cdrFuncParameters;
    try {
      cdrFuncParameters = new ParameterServiceClient().getParamByNameOnly(paramName);
    }
    catch (ApicWebServiceException e) {
      CDMLogger.getInstance().errorDialog(e.getMessage(), e, Activator.PLUGIN_ID);
      return false;
    }
    boolean addedToSP = false;
    if (CommonUtils.isNotEmpty(cdrFuncParameters)) {
      // if param type is available get the specific param else the first param from list
      SortedSet<Parameter> cdrFuncParams = getParamToBeAdded(paramType, cdrFuncParameters);
      if (!cdrFuncParams.isEmpty()) {
        // add the param obj to scratchpad
        addedToSP = addParamToScratchPad(cdrFuncParams);
      }
    }

    if (!addedToSP) {
      CDMLogger.getInstance().warnDialog(
          "Parameter name is not added to scratchpad since it is not mapped to any function version",
          Activator.PLUGIN_ID);
    }
    return true;
  }

  /**
   * Get the param obj based on name / name and type
   *
   * @param paramType paramType
   * @param cdrFuncParam cdrFuncParam
   * @param cdrFuncParameters cdrFuncParameters
   * @return
   */
  private SortedSet<Parameter> getParamToBeAdded(final String paramType, final Map<Long, Parameter> cdrFuncParameters) {
    SortedSet<Parameter> funcParams = new TreeSet<>();
    if (null != paramType) {
      for (Parameter param : cdrFuncParameters.values()) {
        if (param.getType().equalsIgnoreCase(paramType)) {
          funcParams.add(param);
          break;
        }
      }
    }
    else {
      funcParams.addAll(cdrFuncParameters.values());
    }
    return funcParams;
  }

  /**
   * Add the param obj to scratchpad
   *
   * @param cdrFuncParam
   */
  private boolean addParamToScratchPad(final SortedSet<Parameter> cdrFuncParam) {
    final ScratchPadViewPart scratchViewPart = (ScratchPadViewPart) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
        .getActivePage().findView(ScratchPadViewPart.PART_ID);
    if (scratchViewPart != null) {
      CommonActionSet actionSet = new CommonActionSet();
      actionSet.addItemsToScratchPad(cdrFuncParam, scratchViewPart);
      return true;
    }
    return false;
  }

}
