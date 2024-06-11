/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.review;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.cdr.SSDServiceHandler;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.DateFormat;
import com.bosch.caltool.icdm.common.util.messages.Messages;
import com.bosch.caltool.icdm.model.cdr.RuleSet;
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * @author bru2cob
 */
public class RuleSetRulesResolver extends AbstractSimpleBusinessObject implements IReviewRulesResolver {

  private final RuleSet ruleSet;
  private final Set<String> modifiedLabelList;
  private final Set<FeatureValueModel> featureValModelSet;

  /**
   * @param ruleSet Rule-Set
   * @param modifiedLabelList modified Label List
   * @param featureValModelSet feature Val Model
   * @param serviceData Service Data
   */
  public RuleSetRulesResolver(final RuleSet ruleSet, final Set<String> modifiedLabelList,
      final Set<FeatureValueModel> featureValModelSet, final ServiceData serviceData) {

    super(serviceData);

    this.ruleSet = ruleSet;
    this.modifiedLabelList = modifiedLabelList;
    this.featureValModelSet = featureValModelSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile getRules() throws IcdmException {
    File file = new File(Messages.getString("SERVICE_WORK_DIR"));
    if (!file.exists()) {
      file.mkdir();
    }
    file = new File(file.getAbsoluteFile() + File.separator + AbstractReviewProcess.DATARVW_ROOT_DIR_NAME);
    file.mkdir();
    String currentDate = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    file = new File(file.getAbsoluteFile() + "\\review_" + currentDate);
    file.mkdir();
    Long nodeId = this.ruleSet.getSsdNodeId();
    // get the cdr rules with the file path for the rule set
    return new SSDServiceHandler(getServiceData()).readRulesandGetSSDFileDpndyForNode(
        new ArrayList<>(this.modifiedLabelList), new ArrayList<FeatureValueModel>(this.featureValModelSet), nodeId,
        file.getAbsolutePath(), true);


  }

}
