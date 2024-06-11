/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.model.emr;

import java.util.SortedSet;
import java.util.TreeSet;

import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.util.ModelUtil;

/**
 * @author gge6cob
 */
public class EmrFileMapping implements IModel, Comparable<EmrFileMapping> {

  /**
   *
   */
  private static final long serialVersionUID = -4673200608957643235L;
  private EmrFile emrFile;
 
  /**
   * Flag to check if the EMR data was updated through Excel file upload or through Request Json
   */
  private boolean fileUpload;
  private SortedSet<PidcVariant> variantSet = new TreeSet<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getId() {
    return this.emrFile.getId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setId(final Long fileId) {
    this.emrFile.setId(fileId);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final EmrFileMapping fileObj) {
    return ModelUtil.compare(this.emrFile.getId(), fileObj.emrFile.getId());
  }

  @Override
  public boolean equals(final Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj == null) {
      return false;
    }


    if (obj.getClass() == this.getClass()) {
      // Both id and name should be equal
      return ModelUtil.isEqual(getId(), ((EmrFileMapping) obj).getId());


    }
    return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return ModelUtil.generateHashCode(getId());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return this.emrFile.getVersion();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setVersion(final Long version) {
    this.emrFile.setVersion(version);
  }


  /**
   * @return the emrFile
   */
  public EmrFile getEmrFile() {
    return this.emrFile;
  }


  /**
   * @param emrFile the emrFile to set
   */
  public void setEmrFile(final EmrFile emrFile) {
    this.emrFile = emrFile;
  }


  /**
   * @return the variantSet
   */
  public SortedSet<PidcVariant> getVariantSet() {
    return this.variantSet;
  }


  /**
   * @param sortedSet the variantSet to set
   */
  public void setVariantSet(final SortedSet<PidcVariant> sortedSet) {
    this.variantSet = sortedSet == null ? null : new TreeSet<>(sortedSet);
  }


  /**
   * @return the fileUpload
   */
  public boolean isFileUpload() {
    return this.fileUpload;
  }


  /**
   * @param fileUpload the fileUpload to set
   */
  public void setFileUpload(final boolean fileUpload) {
    this.fileUpload = fileUpload;
  }
}
