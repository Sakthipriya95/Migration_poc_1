/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.HashMap;
import java.util.Map;
import com.bosch.caltool.icdm.model.general.IcdmFileData;
/**
 * @author bru2cob
 */
public class EmrFileInputData {

  /**
   * Pidc version id
   */
  private Long pidcVersId;
 
  /**
   * Key - file path , value - emr file object
   */
  private final Map<String, EmrFile> emrFileMap = new HashMap<>();
  /**
   * Key - file path , value - file object
   */
  private final Map<String, IcdmFileData> icdmFileDataMap = new HashMap<>();

  /**
   * Link to PidcVersion
   */
  private String pidcVersionLink;

  /**
   * @return the pidcVersId
   */
  public Long getPidcVersId() {
    return this.pidcVersId;
  }

  /**
   * @param pidcVersId the pidcVersId to set
   */
  public void setPidcVersId(final Long pidcVersId) {
    this.pidcVersId = pidcVersId;
  }
  /**
   * @return the emrFileMap
   */
  public final Map<String, EmrFile> getEmrFileMap() {
    return this.emrFileMap;
  }
  /**
   * @return the icdmFileDataMap
   */
  public final Map<String, IcdmFileData> getIcdmFileDataMap() {
    return this.icdmFileDataMap;
  }

  /**
   * @return the pidcVersionHyperlink
   */
  public String getPidcVersionLink() {
    return this.pidcVersionLink;
  }


  /**
   * @param pidcVersionLink the pidcVersionHyperlink to set
   */
  public void setPidcVersionLink(final String pidcVersionLink) {
    this.pidcVersionLink = pidcVersionLink;
  }
}
