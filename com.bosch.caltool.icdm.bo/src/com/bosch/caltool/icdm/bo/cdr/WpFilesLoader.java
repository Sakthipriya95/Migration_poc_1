package com.bosch.caltool.icdm.bo.cdr;

import com.bosch.caltool.dmframework.bo.AbstractBusinessObject;
import com.bosch.caltool.dmframework.bo.ServiceData;
import com.bosch.caltool.icdm.common.exception.DataException;
import com.bosch.caltool.icdm.common.exception.InvalidInputException;
import com.bosch.caltool.icdm.database.entity.cdr.TWpArchival;
import com.bosch.caltool.icdm.database.entity.cdr.TWpFiles;
import com.bosch.caltool.icdm.model.MODEL_TYPE;
import com.bosch.caltool.icdm.model.cdr.WpFiles;


/**
 * Loader class for WpFiles
 *
 * @author msp5cob
 */
public class WpFilesLoader extends AbstractBusinessObject<WpFiles, TWpFiles> {


  /**
   * Constructor
   *
   * @param serviceData Service Data
   */
  public WpFilesLoader(final ServiceData serviceData) {
    super(serviceData, MODEL_TYPE.WP_FILES, TWpFiles.class);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected WpFiles createDataObject(final TWpFiles entity) throws DataException {
    WpFiles object = new WpFiles();

    object.setId(entity.getWpFileId());
    object.setWpArchivalId(entity.getTWpArchival().getWpArchivalId());
    object.setFileName(entity.getFileName());
    object.setFileData(entity.getFileData());

    setCommonFields(object, entity);

    return object;
  }

  /**
   * @param wpArchivalId wpArchivalId
   * @return file data as byte array
   * @throws InvalidInputException InvalidInputException
   */
  public byte[] getWpArchivalFiles(final Long wpArchivalId) throws InvalidInputException {
    TWpArchival tWpArchival = new WpArchivalLoader(getServiceData()).getEntityObject(wpArchivalId);
    if ((tWpArchival == null) || tWpArchival.getTWpFiles().isEmpty()) {
      throw new InvalidInputException("No Files found for Wp Archival Id : " + wpArchivalId);
    }
    return tWpArchival.getTWpFiles().get(0).getFileData();
  }

}
