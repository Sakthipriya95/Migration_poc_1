package com.bosch.caltool.icdm.database.entity.cdr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;


/**
 * The persistent class for the T_RVW_FUNCTIONS database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_RVW_FUNCTIONS")
@NamedQueries(value = {
    @NamedQuery(name = TRvwFunction.GET_FUN_VER_BY_ID, query = "select rvwFunc.functionVers,rvwFunc.rvwFunId from TRvwFunction rvwFunc where rvwFunc.rvwFunId in :rvwFuncID"),
    @NamedQuery(name = TRvwFunction.GET_ALL, query = "SELECT t FROM TRvwFunction t") })
public class TRvwFunction implements Serializable {

  private static final long serialVersionUID = 1L;


  /**
   * Get the function version using rvw function id
   */
  public static final String GET_FUN_VER_BY_ID = "TRvwFile.getRvwFileVerbyFuncId";
  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TRvwFunction.findAll";
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CDR_SEQ_GENERATOR")
  @Column(name = "RVW_FUN_ID", unique = true, nullable = false)
  private long rvwFunId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 20)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 20)
  private String modifiedUser;

  @Version
  @Column(name = "\"VERSION\"")
  private Long version;
  // ICDM-1720
  @Column(name = "FUNCTION_VERS", length = 20)
  private String functionVers;


  // bi-directional many-to-one association to TRvwParameter
  @OneToMany(mappedBy = "TRvwFunction", fetch = FetchType.LAZY)
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TRvwParameter> TRvwParameters;

  // bi-directional many-to-one association to TFunction
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "FUNCTION_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TFunction TFunction;

  // bi-directional many-to-one association to TRvwResult
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "RESULT_ID", nullable = false)
  @BatchFetch(value = BatchFetchType.JOIN)
  private TRvwResult TRvwResult;

  public TRvwFunction() {}

  public long getRvwFunId() {
    return this.rvwFunId;
  }

  public void setRvwFunId(final long rvwFunId) {
    this.rvwFunId = rvwFunId;
  }

  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  public String getCreatedUser() {
    return this.createdUser;
  }

  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  public String getModifiedUser() {
    return this.modifiedUser;
  }

  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  public Long getVersion() {
    return this.version;
  }

  public void setVersion(final Long version) {
    this.version = version;
  }

  public Set<TRvwParameter> getTRvwParameters() {
    return this.TRvwParameters;
  }

  public void setTRvwParameters(final Set<TRvwParameter> TRvwParameters) {
    this.TRvwParameters = TRvwParameters;
  }

  public TFunction getTFunction() {
    return this.TFunction;
  }

  public void setTFunction(final TFunction TFunction) {
    this.TFunction = TFunction;
  }

  public TRvwResult getTRvwResult() {
    return this.TRvwResult;
  }

  public void setTRvwResult(final TRvwResult TRvwResult) {
    this.TRvwResult = TRvwResult;
  }

  /**
   * @return the functionVers
   */
  public String getTFuncVers() {
    return this.functionVers;
  }


  /**
   * @param functionVers the functionVers to set
   */
  public void setTFuncVers(final String functionVers) {
    this.functionVers = functionVers;
  }
}