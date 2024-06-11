/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.checkssd;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.checkssd.ws.complilabels.model.OEMRuleDescriptionInput;
import com.bosch.checkssd.ws.complilabels.model.RuleDescriptionModel;
import com.bosch.ssd.icdm.model.RuleIdDescriptionModel;

/**
 * loader for rule description service, abstract review process ,
 *
 * @author hnu1cob
 */
public class RuleDescriptionLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData service data
   */
  public RuleDescriptionLoader(final ServiceData serviceData) {
    super(serviceData);
  }


  /**
   * @param input OEMRuleDescriptionInput
   * @return OEMRuleDescriptionInput for ssd
   */
  private com.bosch.ssd.icdm.model.OEMRuleDescriptionInput toSsdInput(final OEMRuleDescriptionInput input) {

    com.bosch.ssd.icdm.model.OEMRuleDescriptionInput modifiedInput =
        new com.bosch.ssd.icdm.model.OEMRuleDescriptionInput();

    modifiedInput.setRuleId(input.getRuleId());
    modifiedInput.setRevision(input.getRevision());

    return modifiedInput;
  }

  /**
   * @param oemEntry input model
   * @param outputModelMap model for output
   * @throws IcdmException exception
   */
  private void copyObjects(final Entry<String, RuleIdDescriptionModel> oemEntry,
      final Map<String, RuleDescriptionModel> outputModelMap)
      throws IcdmException {

    RuleDescriptionModel output = new RuleDescriptionModel();

    try {
      BeanUtils.copyProperties(output, oemEntry.getValue());
    }
    catch (IllegalAccessException | InvocationTargetException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
    outputModelMap.put(oemEntry.getKey(), output);

  }

  /**
   * @param oemInputList List of OEMRuleDescriptionInput
   * @return Map of RuleDescriptionModel
   * @throws IcdmException data retrieval error
   */
  public Map<String, RuleIdDescriptionModel> getOEMParam(final List<OEMRuleDescriptionInput> oemInputList)
      throws IcdmException {

    getLogger().info("Fetching OEM rule descriptions. Input count = {}", oemInputList.size());

    Map<String, RuleIdDescriptionModel> ruleDescMap = new HashMap<>();
    Set<com.bosch.ssd.icdm.model.OEMRuleDescriptionInput> paramsSet =
        oemInputList.stream().map(this::toSsdInput).collect(Collectors.toSet());

    SSDServiceHandler ssdHandler = new SSDServiceHandler(getServiceData());
    ruleDescMap.putAll(ssdHandler.getOEMDetailsForRuleId(paramsSet));

    getLogger().info("OEM rule descriptions retrieved. Output count = {}", ruleDescMap.size());

    return ruleDescMap;

  }

  /**
   * @param oemInputList list of OEMRuleDescriptionInput
   * @return map of RuleDescriptionModel
   */
  public Map<String, RuleDescriptionModel> getOEMParamForCheckssd(final List<OEMRuleDescriptionInput> oemInputList) {
    Map<String, RuleDescriptionModel> outputModel = new HashMap<>();
    if (!CommonUtils.isNullOrEmpty(oemInputList)) {
      try {
        Map<String, RuleIdDescriptionModel> ruleDescMap = getOEMParam(oemInputList);

        for (Entry<String, RuleIdDescriptionModel> oemEntry : ruleDescMap.entrySet()) {
          copyObjects(oemEntry, outputModel);
        }
      }
      catch (IcdmException exp) {
        getLogger().error(exp.getMessage(), exp);
      }

    }

    return outputModel;

  }

}
