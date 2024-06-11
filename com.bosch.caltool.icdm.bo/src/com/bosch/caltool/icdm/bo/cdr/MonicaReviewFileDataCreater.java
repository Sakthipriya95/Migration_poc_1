/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.bo.cdr;

import java.util.Map;
import java.util.Set;

import com.bosch.caltool.icdm.model.cdr.MonicaReviewFileData;
import com.bosch.caltool.icdm.model.cdr.MonicaReviewInputData;

/**
 * @author say8cob
 */
public class MonicaReviewFileDataCreater {

  /**
   * @param monicaReviewInputData
   * @param dcmFileName
   * @param dcmByteArray
   * @param monicaFileName
   * @param monicaByteArray
   * @param a2lFunctionList
   * @param calDataByteArrayMap
   * @param monitoringToolOutput
   * @return
   */
  public MonicaReviewFileData createMonicaFileData(final MonicaReviewInputData monicaReviewInputData,
      final String dcmFileName, final byte[] dcmByteArray, final String monicaFileName, final byte[] monicaByteArray,
      final Set<String> a2lFunctionList, final Map<String, byte[]> calDataByteArrayMap) {
    MonicaReviewFileData monicaReviewFileData = new MonicaReviewFileData();
    monicaReviewFileData.setDcmFileName(dcmFileName);
    monicaReviewFileData.setMonicaFileName(monicaFileName);
    monicaReviewFileData.setMonicaByteArray(monicaByteArray);
    monicaReviewFileData.setDcmByteArray(dcmByteArray);
    monicaReviewFileData.setMonicaReviewInputData(monicaReviewInputData);
    monicaReviewFileData.setA2lFunctionList(a2lFunctionList);
    monicaReviewFileData.setCalDataByteArray(calDataByteArrayMap);
    return monicaReviewFileData;
  }
}
