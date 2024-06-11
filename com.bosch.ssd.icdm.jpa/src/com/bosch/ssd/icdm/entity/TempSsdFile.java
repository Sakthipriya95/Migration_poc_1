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
import org.eclipse.persistence.annotations.Direction;
import org.eclipse.persistence.annotations.NamedStoredProcedureQueries;
import org.eclipse.persistence.annotations.NamedStoredProcedureQuery;
import org.eclipse.persistence.annotations.StoredProcedureParameter;
import org.eclipse.persistence.config.HintValues;
import org.eclipse.persistence.config.QueryHints;

import com.bosch.ssd.icdm.constants.JPAQueryConstants;

/**
 * The persistent class for the TEMP_SSD_FILE database table.
 *
 * @author SSN9COB
 */
@Entity
@Table(name = "TEMP_SSD_FILE")
@Cache(databaseChangeNotificationType = DatabaseChangeNotificationType.NONE)
@NamedStoredProcedureQueries(value = {
    /**
     * Procedure Call - Del_RptRelease_Data
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "TempSsdFile.Del_WriteReleases_Data", procedureName = "Del_RptRelease_Data", parameters = {
        @StoredProcedureParameter(queryParameter = "cond", name = "cond", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "rptParam", name = "rptParam", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "output", name = "output", type = BigDecimal.class, direction = Direction.OUT) }),
    /**
     * Procedure call - SSD_FileDate
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = JPAQueryConstants.TEMP_SSD_FILE_SSD_FILE_DATA, procedureName = "SSD_FileData", parameters = {
        @StoredProcedureParameter(queryParameter = "pn_pro_rel_id", name = "pn_pro_rel_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "pc_mft", name = "pc_mft", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "pc_ssd", name = "pc_ssd", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "pc_appchK", name = "pc_appchK", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "pc_comp", name = "pc_comp", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "pc_temporary", name = "pc_temporary", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "commentPb", name = "commentPb", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "refreshPb", name = "refreshPb", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "labelSort", name = "labelSort", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "userId", name = "userId", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "commentReportComparsion", name = "commentReportComparsion", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "outputFlag", name = "outputFlag", type = String.class, direction = Direction.OUT) }),
    /**
     * Procedure call - SP_WriteCDF_V1
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = JPAQueryConstants.TEMP_SSD_FILE_CDF_REPORT_EXCEL, procedureName = "SP_WRITECDF_V1", parameters = {
        @StoredProcedureParameter(queryParameter = "pn_pro_rel_id", name = "pn_pro_rel_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "cdfminMaxCheck", name = "cdfminMaxCheck", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "chk_ssd", name = "chk_ssd", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "chk_mft", name = "chk_mft", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "app_hint", name = "app_hint", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "chk_cmpPck", name = "chk_cmpPck", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "chk_compli", name = "chk_compli", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "chk_qssd", name = "chk_qssd", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "chk_ssd2rev", name = "chk_ssd2rev", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "ecutype", name = "ecutype", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "userId", name = "userId", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "primary_id", name = "primary_id", type = String.class, direction = Direction.OUT) }),
    /**
     * Procedure call - SP_ReleaseErrorTemp
     *
     * @author SSN9COB
     */
    @NamedStoredProcedureQuery(name = "TempSsdFile.SP_ReleasesErrorTemp", procedureName = "SP_ReleasesErrorTemp", parameters = {
        @StoredProcedureParameter(queryParameter = "pn_pro_rel_id", name = "pn_pro_rel_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "p_rev_id", name = "p_rev_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "sw_vers_id", name = "sw_vers_id", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "rev_id_list", name = "rev_id_list", type = BigDecimal.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "ecutype", name = "ecutype", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "firstRule", name = "firstRule", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "userId", name = "userId", type = String.class, direction = Direction.IN),
        @StoredProcedureParameter(queryParameter = "primary_id", name = "primary_id", type = String.class, direction = Direction.OUT) }) })
/**
 * Get SSD File Named Query
 * 
 * @author SSN9COB
 */
@NamedQuery(name = "TempSsdFile.getSSDFile", query = "select OBJ from TempSsdFile OBJ WHERE OBJ.uniId = :uniId order by OBJ.seqNo", hints = {
    @QueryHint(name = "eclipselink.jdbc.fetch-size", value = "1000"),
    @QueryHint(name = QueryHints.MAINTAIN_CACHE, value = HintValues.FALSE) })
public class TempSsdFile implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Advance Formula
   */
  @Column(name = "ADV_FORMULA")
  private String advanceFormula;

  /**
   * Seq No
   */
  @Id
  @Column(name = "SEQ_NO")
  private BigDecimal seqNo;

  /**
   * Unique ID
   */
  @Column(name = "UNI_ID")
  private String uniId;

  /**
   *
   */
  public TempSsdFile() {
    // constructor
  }


  /**
   * @return sequence number
   */
  public BigDecimal getSeqNo() {
    return this.seqNo;
  }

  /**
   * @param seqNo sequnce id
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
   * @param uniId unique id
   */
  public void setUniId(final String uniId) {
    this.uniId = uniId;
  }

  /**
   * @return the advanceFormula supports Bigrule
   */
  public String getAdvanceFormula() {
    return this.advanceFormula;
  }

  /**
   * @param advanceFormula the advanceFormula to set
   */
  public void setAdvanceFormula(final String advanceFormula) {
    this.advanceFormula = advanceFormula;
  }

}