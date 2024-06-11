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
import com.bosch.ssd.icdm.model.CDRRulesWithFile;
import com.bosch.ssd.icdm.model.FeatureValueModel;

/**
 * @author bru2cob
 */
public class CommonRulesResolver extends AbstractSimpleBusinessObject implements IReviewRulesResolver {

  private final Set<String> modifiedLabelList;
  private final Set<FeatureValueModel> featureValModelSet;

  /**
   * @param modifiedLabelList modified Label List
   * @param featureValModelSet feature Val Model
   * @param serviceData Service Data
   */
  public CommonRulesResolver(final Set<String> modifiedLabelList, final Set<FeatureValueModel> featureValModelSet,
      final ServiceData serviceData) {

    super(serviceData);

    this.modifiedLabelList = modifiedLabelList;
    this.featureValModelSet = featureValModelSet;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public CDRRulesWithFile getRules() throws IcdmException {
    // Create service's 'Work' directory
    File file = new File(Messages.getString("SERVICE_WORK_DIR"));
    file.mkdir();

    // Create 'review' root directory
    file = new File(file.getAbsoluteFile() + File.separator + AbstractReviewProcess.DATARVW_ROOT_DIR_NAME);
    file.mkdir();

    // create directory for this review
    String currentDateStr = ApicUtil.getCurrentTime(DateFormat.DATE_FORMAT_20);
    file = new File(file.getAbsoluteFile() + "\\review_" + currentDateStr);
    file.mkdir();

    return new SSDServiceHandler(getServiceData()).readRulesandGetSSDFileDependency(
        new ArrayList<>(this.modifiedLabelList), new ArrayList<FeatureValueModel>(this.featureValModelSet),
        file.getAbsolutePath());
  }

}
