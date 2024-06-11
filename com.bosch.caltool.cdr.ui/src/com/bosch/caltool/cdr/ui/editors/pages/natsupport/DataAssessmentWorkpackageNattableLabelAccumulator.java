/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.cdr.ui.editors.pages.natsupport;

import org.eclipse.nebula.widgets.nattable.data.IRowDataProvider;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.LabelStack;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;

import com.bosch.caltool.cdr.ui.editors.pages.DataAssessmentWorkPackagesPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUIConstants;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.cdr.CDRConstants;
import com.bosch.caltool.icdm.model.cdr.DaWpResp;

/**
 * This class extends {@link ColumnOverrideLabelAccumulator} which is used for registration/addition of labels for a
 * given column.
 *
 * @author NDV4KOR
 */
public class DataAssessmentWorkpackageNattableLabelAccumulator extends ColumnOverrideLabelAccumulator {


  private final DataLayer bodyDataLayer;

  /**
   * @param bodyDataLayer ILayer instance
   */
  public DataAssessmentWorkpackageNattableLabelAccumulator(final DataLayer bodyDataLayer) {
    super(bodyDataLayer);
    this.bodyDataLayer = bodyDataLayer;
  }

  /**
   * This method is used to add labels to a cell
   */
  @Override
  public void accumulateConfigLabels(final LabelStack configLabels, final int columnPosition, final int rowPosition) {
    IRowDataProvider<DaWpResp> bodyDataProvider = (IRowDataProvider<DaWpResp>) this.bodyDataLayer.getDataProvider();

    DaWpResp daAssessWorkpackage = bodyDataProvider.getRowObject(rowPosition);

    if (columnPosition == DataAssessmentWorkPackagesPage.COLUMN_NUM_OVERALL_WP) {
      configLabels.addLabel(daAssessWorkpackage.getWpReadyForProductionFlag().equals(ApicConstants.CODE_YES)
          ? CommonUIConstants.LABEL_FOR_YES : CommonUIConstants.LABEL_FOR_NO);
    }

    if (columnPosition == DataAssessmentWorkPackagesPage.COLUMN_NUM_WP_FINISHED) {
      configLabels.addLabel(daAssessWorkpackage.getWpFinishedFlag().equals(ApicConstants.CODE_YES)
          ? CommonUIConstants.LABEL_FOR_YES : CommonUIConstants.LABEL_FOR_NO);
    }


    configLabelForQnaireAnsClmn(configLabels, columnPosition, daAssessWorkpackage);


    if (columnPosition == DataAssessmentWorkPackagesPage.COLUMN_NUM_PARAMETER_REVIEWED) {
      configLabels.addLabel(daAssessWorkpackage.getParameterReviewedFlag().equals(ApicConstants.CODE_YES)
          ? CommonUIConstants.LABEL_FOR_YES : CommonUIConstants.LABEL_FOR_NO);
    }
    if (columnPosition == DataAssessmentWorkPackagesPage.COLUMN_NUM_HEX_FILE_EQUAL_TO_REVIEWS) {
      configLabels.addLabel(daAssessWorkpackage.getHexRvwEqualFlag().equals(ApicConstants.CODE_YES)
          ? CommonUIConstants.LABEL_FOR_YES : CommonUIConstants.LABEL_FOR_NO);
    }


  }

  /**
   * @param configLabels
   * @param columnPosition
   * @param daAssessWorkpackage
   */
  private void configLabelForQnaireAnsClmn(final LabelStack configLabels, final int columnPosition,
      final DaWpResp daAssessWorkpackage) {
    if (columnPosition == DataAssessmentWorkPackagesPage.COLUMN_NUM_QNAIRE_ANSWERED_BASELINED) {
      if (daAssessWorkpackage.getQnairesAnsweredFlag()
          .equals(CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.N_A.getDbType())) {
        configLabels.addLabel(CommonUIConstants.NOT_APPLICABLE);
      }

      else {
        configLabels.addLabel(daAssessWorkpackage.getQnairesAnsweredFlag()
            .equals(CDRConstants.DA_QNAIRE_STATUS_FOR_WPRESP.YES.getDbType()) ? CommonUIConstants.LABEL_FOR_YES
                : CommonUIConstants.LABEL_FOR_NO);
      }
    }
  }

}