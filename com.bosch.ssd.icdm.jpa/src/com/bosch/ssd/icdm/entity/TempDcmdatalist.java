package com.bosch.ssd.icdm.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.QueryHint;
import javax.persistence.Table;

import org.eclipse.persistence.annotations.Cache;
import org.eclipse.persistence.annotations.DatabaseChangeNotificationType;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;


/**
 * The persistent class for the TEMP_DCMDATALIST database table.
 */
@Entity
@Table(name = "TEMP_DCMDATALIST")
/**
 * Get DCM File Data Information
 */
@NamedQuery(name = "TempDcmdatalist.getDcmFileData", query = "select OBJ from TempDcmdatalist OBJ WHERE OBJ.uniId = :uniId order by obj.seqNo", hints = {
    @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "1000"),
    @QueryHint(name = QueryHints.MAINTAIN_CACHE, value = HintValues.FALSE) })
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
public class TempDcmdatalist implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Data Description
   */
  @Column(name = "DATA_DESC")
  private String dataDesc;

  /**
   * Sequence Number
   */
  @Id
  @Column(name = "SEQ_NO")
  private BigDecimal seqNo;

  /**
   * Unique Id
   */
  @Column(name = "UNI_ID")
  private String uniId;

  /**
   * default constructor
   */
  public TempDcmdatalist() {
    // constructor
  }

  /**
   * @return data description
   */
  public String getDataDesc() {
    return this.dataDesc;
  }

  /**
   * @param dataDesc -data description
   */
  public void setDataDesc(final String dataDesc) {
    this.dataDesc = dataDesc;
  }

  /**
   * @return sequence number
   */
  public BigDecimal getSeqNo() {
    return this.seqNo;
  }

  /**
   * @param seqNo - sequence number
   */
  public void setSeqNo(final BigDecimal seqNo) {
    this.seqNo = seqNo;
  }

  /**
   * @return unique id
   */
  public String getUniId() {
    return this.uniId;
  }

  /**
   * @param uniId - unique id
   */
  public void setUniId(final String uniId) {
    this.uniId = uniId;
  }

}