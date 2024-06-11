/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.rest.service.ssd;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.beanutils.BeanUtils;

import com.bosch.caltool.apic.ws.common.WsCommonConstants;
import com.bosch.caltool.apic.ws.rest.annotation.CompressData;
import com.bosch.caltool.apic.ws.rest.service.AbstractRestService;
import com.bosch.caltool.icdm.bo.checkssd.RuleDescriptionLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.ssd.OEMRuleDescriptionInput;
import com.bosch.caltool.icdm.model.ssd.RuleIdIcdmDescriptionModel;
import com.bosch.ssd.icdm.model.RuleIdDescriptionModel;


/**
 * Rest service to fetch ssd Rule Descriptions for OEM.
 *
 * @author rgo7cob
 */
@Path("/" + WsCommonConstants.RWS_CONTEXT_SSD + "/" + WsCommonConstants.RWS_CONTEXT_RULE_DESC)
public class RuleDescriptionService extends AbstractRestService {


  /**
   * @param oemInputList oemInputList
   * @return String yes or no
   * @throws IcdmException error in service
   */
  @POST
  @Produces({ MediaType.APPLICATION_JSON })
  @Consumes({ MediaType.APPLICATION_JSON })
  @CompressData
  public Response getRuleDescription(final List<OEMRuleDescriptionInput> oemInputList) throws IcdmException {
    if (CommonUtils.isNullOrEmpty(oemInputList)) {
      throw new InvalidInputException("Parameters cannot be empty");
    }

    List<com.bosch.checkssd.ws.complilabels.model.OEMRuleDescriptionInput> oemInput =
        oemInputList.stream().map(this::toLoaderInput).collect(Collectors.toList());
    Map<String, RuleIdDescriptionModel> ruleDescMap = new RuleDescriptionLoader(getServiceData()).getOEMParam(oemInput);

    Map<String, RuleIdIcdmDescriptionModel> outputModel = new HashMap<>();
    for (Entry<String, RuleIdDescriptionModel> oemEntry : ruleDescMap.entrySet()) {
      copyObjects(oemEntry, outputModel);
    }

    return Response.ok(outputModel).build();
  }

  /**
   * to change OEMRuleDescriptionInput model for loader
   */
  private com.bosch.checkssd.ws.complilabels.model.OEMRuleDescriptionInput toLoaderInput(
      final OEMRuleDescriptionInput input) {

    com.bosch.checkssd.ws.complilabels.model.OEMRuleDescriptionInput modifiedInput =
        new com.bosch.checkssd.ws.complilabels.model.OEMRuleDescriptionInput();

    modifiedInput.setRuleId(input.getRuleId());
    modifiedInput.setRevision(input.getRevision());

    return modifiedInput;
  }

  /**
   * @param oemEntry
   * @param outputModel
   * @throws IcdmException
   */
  private void copyObjects(final Entry<String, RuleIdDescriptionModel> oemEntry,
      final Map<String, RuleIdIcdmDescriptionModel> outputModel)
      throws IcdmException {
    RuleIdIcdmDescriptionModel output = new RuleIdIcdmDescriptionModel();

    try {
      BeanUtils.copyProperties(output, oemEntry.getValue());
    }
    catch (IllegalAccessException | InvocationTargetException exp) {
      throw new IcdmException(exp.getMessage(), exp);
    }
    outputModel.put(oemEntry.getKey(), output);

  }

}
