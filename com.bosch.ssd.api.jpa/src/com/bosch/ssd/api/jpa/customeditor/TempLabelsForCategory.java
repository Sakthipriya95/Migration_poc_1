package com.bosch.ssd.api.jpa.customeditor;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The persistent class for the TEMP_LABELS_FOR_CATEGORY database table.
 */
@Entity
@Table(name = "K5ESK_LDB2.TEMP_LABELS_FOR_CATEGORY")

public class TempLabelsForCategory implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @Column(name = "SEQ_ID", unique = true, nullable = false)
  private long seqid;


  /**
   * @return the seq_id
   */
  public long getSeqid() {
    return this.seqid;
  }


  /**
   * @param seq_id the seq_id to set
   */
  public void setSeqid(final long seq_id) {
    this.seqid = seq_id;
  }


  @Column(name = "LABEL_NAME", length = 200)
  private String labelName;


  /**
   * @return the labelName
   */
  public String getLabelName() {
    return this.labelName;
  }


  /**
   * @param labelName the labelName to set
   */
  public void setLabelName(final String labelName) {
    this.labelName = labelName;
  }


  public TempLabelsForCategory() {}


}