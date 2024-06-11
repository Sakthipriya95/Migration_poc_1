package com.bosch.caltool.icdm.database.entity.a2l;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.bosch.caltool.icdm.database.entity.cdr.TParameter;


/**
 * The persistent class for the T_A2L_GRP_PARAM database table.
 */
@Entity
@Table(name = "T_A2L_GRP_PARAM")
@NamedNativeQueries(value = {
    @NamedNativeQuery(name = "TA2lGrpParam.findAll", query = "SELECT t FROM TA2lGrpParam t"),
    @NamedNativeQuery(name = TA2lGrpParam.INSERT_GRP_PARAM, query = "INSERT INTO T_A2L_GRP_PARAM(param_id,group_id) SELECT tparam.id,gttparam.group_id from t_parameter tparam,gtt_parameters gttparam where tparam.name=gttparam.param_name and tparam.ptype=gttparam.type") })
public class TA2lGrpParam implements Serializable {

  /**
   * Named Native query to insert T_A2L_GRP_PARAM with param name, param type and group id as arguements
   */
  public static final String INSERT_GRP_PARAM = "TA2lGrpParam.insertGrpParam";

  private static final long serialVersionUID = 1L;

  @Id
  @SequenceGenerator(name = "T_A2L_GRP_PARAM_A2LPARAMID_GENERATOR")
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "A2L_PARAM_ID", unique = true, nullable = false, precision = 15)
  private long a2lParamId;

  @Column(name = "PARAM_ID", nullable = false)
  private long paramId;

  @Column(name = "CREATED_DATE", nullable = false)
  private Timestamp createdDate;

  @Column(name = "CREATED_USER", nullable = false, length = 30)
  private String createdUser;

  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  @Column(name = "MODIFIED_USER", length = 30)
  private String modifiedUser;

  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  // bi-directional many-to-one association to TA2lGroup
  @ManyToOne
  @JoinColumn(name = "GROUP_ID", nullable = false)
  private TA2lGroup TA2lGroup;

  // bi-directional one-to-one association to TParameter
  @ManyToOne
  @JoinColumn(name = "PARAM_ID", nullable = false, insertable = false, updatable = false)
  private TParameter TParameter;


  public TA2lGrpParam() {}


  public long getA2lParamId() {
    return this.a2lParamId;
  }

  public void setA2lParamId(final long a2lParamId) {
    this.a2lParamId = a2lParamId;
  }


  public long getParamId() {
    return this.paramId;
  }

  public void setParamId(final long paramId) {
    this.paramId = paramId;
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

  public TA2lGroup getTA2lGroup() {
    return this.TA2lGroup;
  }

  public void setTA2lGroup(final TA2lGroup TA2lGroup) {
    this.TA2lGroup = TA2lGroup;
  }

  public TParameter getTParameter() {
    return this.TParameter;
  }

  public void setTParameter(final TParameter TParameter) {
    this.TParameter = TParameter;
  }

}