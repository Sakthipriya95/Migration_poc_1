/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr.qnaire;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.dmframework.bo.AbstractSimpleBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.bo.apic.attr.AttributeValueLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPackageDivisionLoader;
import com.bosch.caltool.icdm.bo.wp.WorkPkgLoader;
import com.bosch.caltool.icdm.common.exception.IcdmException;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.cdr.RvwQnaireWPTypesModel;
import com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire;
import com.bosch.caltool.icdm.model.cdr.qnaire.QuestionnaireVersion;
import com.bosch.caltool.icdm.model.wp.WorkPackageDivision;
import com.bosch.caltool.icdm.model.wp.WorkPkg;

/**
 * @author dmr1cob
 */
public class RvwQnaireLoader extends AbstractSimpleBusinessObject {

  /**
   * @param serviceData {@link ServiceData}
   */
  public RvwQnaireLoader(final ServiceData serviceData) {
    super(serviceData);
  }

  /**
   * Find the workpackages which a. does not have questionnaire - will be stored in 'qsToBeCreated' list b. which has
   * questionnaire and no active version - will be stored in 'qsWithoutActiveVersions' set c. which has questionnaire
   * and active version - will be stored in 'qsWithActiveVersions' set
   *
   * @param icdmWps
   * @param divId
   * @return
   * @throws IcdmException
   */
  public RvwQnaireWPTypesModel loadWpTypes(final SortedSet<WorkPkg> icdmWps, final long divId) throws IcdmException {

    // Loader Intilization
    QuestionnaireLoader questionnaireLoader = new QuestionnaireLoader(getServiceData());
    QuestionnaireVersionLoader questionnaireVersionLoader = new QuestionnaireVersionLoader(getServiceData());
    WorkPkgLoader workPackageLoader = new WorkPkgLoader(getServiceData());
    WorkPackageDivisionLoader workPackageDivisionLoader = new WorkPackageDivisionLoader(getServiceData());
    AttributeValueLoader attributeValueLoader = new AttributeValueLoader(getServiceData());
    AttributeValue division = attributeValueLoader.getDataObjectByID(divId);

    // Model intilization
    RvwQnaireWPTypesModel rvwQnaireWPTypesModel = new RvwQnaireWPTypesModel();

    // List and Set intilization
    List<WorkPkg> qsToBeCreated = new ArrayList<>();
    List<Long> wpDivsWithQs = new ArrayList<>();
    SortedSet<QuestionnaireVersion> qsWithActiveVersions = new TreeSet<>();
    SortedSet<Questionnaire> qsWithoutActiveVersions = new TreeSet<>();

    boolean toBeCreated;
    if (workPackageLoader.getMappedWpsDiv().containsKey(division)) {

      // ICDM-2646
      SortedSet<WorkPkg> wpSet = new TreeSet<>(workPackageLoader.getIcdmWorkPackageMapForDiv(division).values());

      Set<WorkPkg> existngWpList = new HashSet<>();

      // Set to hold the work packages with selected FC2WP type (division) & which are present in the
      // T_WORKPACKAGE_DIVISION table and have questionaire in
      // the T_QUESTIONNAIRE table
      Set<WorkPkg> allDivWpsWithQn = new HashSet<>();

      // Set to hold the work packages with selected FC2WP type (division) & which are present in the
      // T_WORKPACKAGE_DIVISION table and no questionaire in the
      // T_QUESTIONNAIRE table
      Set<WorkPkg> divWpsWithoutQn = new HashSet<>();

      for (com.bosch.caltool.icdm.model.cdr.qnaire.Questionnaire quest : questionnaireLoader.getAll(false, true)
          .values()) {
        Long valueId = workPackageDivisionLoader.getEntityObject(quest.getWpDivId()).getTabvAttrValue().getValueId();
        if (valueId.equals(division.getId())) {
          WorkPkg workPkg = workPackageLoader
              .getDataObjectByID(workPackageDivisionLoader.getDataObjectByID(quest.getWpDivId()).getWpId());
          if (wpSet.contains(workPkg)) {
            existngWpList.add(workPkg);
          }
        }
      }
      rvwQnaireWPTypesModel.getExistngWpList().addAll(existngWpList);
      for (Questionnaire quest : questionnaireLoader.getAll(true, true).values()) {
        WorkPkg workPkg = workPackageLoader
            .getDataObjectByID(workPackageDivisionLoader.getDataObjectByID(quest.getWpDivId()).getWpId());
        Long valueId = workPackageDivisionLoader.getEntityObject(quest.getWpDivId()).getTabvAttrValue().getValueId();
        if (valueId.equals(division.getId())) {
          allDivWpsWithQn.add(workPkg);
        }
      }
      rvwQnaireWPTypesModel.getAllDivWpsWithQn().addAll(allDivWpsWithQn);
      for (WorkPkg wp : wpSet) {
        if (!allDivWpsWithQn.contains(wp)) {
          divWpsWithoutQn.add(wp);
        }
      }
      rvwQnaireWPTypesModel.getDivWpsWithoutQn().addAll(divWpsWithoutQn);
      Collection<WorkPackageDivision> wpList = workPackageLoader.getWrkPkgDetailsMap().values();
      for (WorkPkg icdmWp : icdmWps) {
        toBeCreated = true;
        for (WorkPackageDivision wpDiv : wpList) {
          if (wpDiv.getWpId().equals(icdmWp.getId()) && wpDiv.getDivAttrValId().equals(division.getId())) {
            // get the questionnaire corresponding to this mapping
            wpDivsWithQs.add(wpDiv.getId());
            toBeCreated = false;
            break;
          }
        }

        if (divWpsWithoutQn.contains(icdmWp) || (!existngWpList.contains(icdmWp) && toBeCreated)) {
          // If the wp is not having a questionaire with the selected division, create an entry in the questionnaire
          // table
          qsToBeCreated.add(icdmWp);
        }
      }
    }
    else {
      qsToBeCreated.addAll(icdmWps);
    }
    if (!wpDivsWithQs.isEmpty()) {
      // get the questionnaires with or without active version
      qsWithActiveVersions = questionnaireVersionLoader.getQuestionnaireVersions(wpDivsWithQs, qsWithoutActiveVersions);
    }
    rvwQnaireWPTypesModel.getQsWithActiveVersions().addAll(qsWithActiveVersions);
    rvwQnaireWPTypesModel.getQsWithoutActiveVersions().addAll(qsWithoutActiveVersions);
    rvwQnaireWPTypesModel.getWpDivsWithQs().addAll(wpDivsWithQs);
    rvwQnaireWPTypesModel.getQsToBeCreated().addAll(qsToBeCreated);

    return rvwQnaireWPTypesModel;
  }

}
