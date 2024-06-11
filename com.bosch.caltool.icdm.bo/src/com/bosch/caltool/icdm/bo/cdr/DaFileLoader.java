package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.database.entity.cdr.TDaFile;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.DaFile;


/**
 * Loader class for DaFile
 *
 * @author say8cob
 */
public class DaFileLoader extends AbstractBusinessObject<DaFile, TDaFile> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public DaFileLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.DA_FILE, TDaFile.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected DaFile createDataObject(final TDaFile entity) throws DataException {
    DaFile object = new DaFile();

    object.setId(entity.getDaFileId());
    object.setDataAssessmentId(entity.getTDaDataAssessment().getDataAssessmentId());
    object.setFileName(entity.getFileName());
    object.setFileData(entity.getFileData());

    setCommonFields(object, entity);

    return object;
  }

}
