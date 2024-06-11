package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.BatchFetch;
import org.eclipse.persistence.annotations.BatchFetchType;
import org.eclipse.persistence.annotations.OptimisticLocking;

import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;

/**
 * The persistent class for the T_EMR_PIDC_VARIANT database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_EMR_PIDC_VARIANT")
@NamedQueries(value = {
    @NamedQuery(name = "TEmrPidcVariant.findAll", query = "SELECT t FROM TEmrPidcVariant t"),
    @NamedQuery(name = TEmrPidcVariant.GET_BY_PIDC_VERSION, query = "SELECT emrVar FROM TEmrPidcVariant emrVar" +
        " WHERE emrVar.tEmrFile.deletedFlag ='N' and emrVar.tabvProjectVariant.tPidcVersion.pidcVersId = :" +
        TEmrPidcVariant.QP_PIDC_VERS_ID),
    @NamedQuery(name = TEmrPidcVariant.GET_BY_PIDC_VARIANT, query = "SELECT emrVar FROM TEmrPidcVariant emrVar" +
        " WHERE emrVar.tEmrFile.deletedFlag ='N' and emrVar.tabvProjectVariant.variantId = :" +
        TEmrPidcVariant.QP_VARIANT_ID) })
public class TEmrPidcVariant implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The Constant GET_ALL. */
  public static final String GET_ALL = "TEmrPidcVariant.findAll";

  /**
   *
   */
  public static final String GET_BY_PIDC_VERSION = "TEmrPidcVariant.findByPidcVersion";

  /**
   *
   */
  public static final String GET_BY_PIDC_VARIANT = "TEmrPidcVariant.findByPidcVariant";

  /**
  *
  */
  public static final String QP_PIDC_VERS_ID = "pidcVersId";

  /**
   *
   */
  public static final String QP_VARIANT_ID = "variantId";

  /** The emr pv id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "EMR_PV_ID")
  private long emrPvId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The modified date. */
  @Column(name = "MODIFIED_DATE")
  private Timestamp modifiedDate;

  /** The modified user. */
  @Column(name = "MODIFIED_USER")
  private String modifiedUser;

  /** The version. */
  @Version
  @Column(name = "\"VERSION\"", nullable = false)
  private Long version;

  /** The EMR Variant. */
  @Column(name = "EMR_VARIANT")
  private Long emrVariant;

  /** The T emr emission standard. */
  // bi-directional many-to-one association to TEmrEmissionStandard
  @ManyToOne
  @JoinColumn(name = "EMS_ID")
  private TEmrEmissionStandard tEmrEmissionStandard;

  /** The T emr file. */
  // bi-directional many-to-one association to TEmrFile
  @ManyToOne
  @JoinColumn(name = "EMR_FILE_ID")
  private TEmrFile tEmrFile;

  /** The tabv project variant. */
  // bi-directional many-to-one association to TabvProjectVariant
  @ManyToOne
  @JoinColumn(name = "VARIANT_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvProjectVariant tabvProjectVariant;

  /**
   * Instantiates a new t emr pidc variant.
   */
  public TEmrPidcVariant() {
    // Public constructor
  }

  /**
   * Gets the emr pv id.
   *
   * @return the emr pv id
   */
  public long getEmrPvId() {
    return this.emrPvId;
  }

  /**
   * Sets the emr pv id.
   *
   * @param emrPvId the new emr pv id
   */
  public void setEmrPvId(final long emrPvId) {
    this.emrPvId = emrPvId;
  }

  /**
   * Gets the created date.
   *
   * @return the created date
   */
  public Timestamp getCreatedDate() {
    return this.createdDate;
  }

  /**
   * Sets the created date.
   *
   * @param createdDate the new created date
   */
  public void setCreatedDate(final Timestamp createdDate) {
    this.createdDate = createdDate;
  }

  /**
   * Gets the created user.
   *
   * @return the created user
   */
  public String getCreatedUser() {
    return this.createdUser;
  }

  /**
   * Sets the created user.
   *
   * @param createdUser the new created user
   */
  public void setCreatedUser(final String createdUser) {
    this.createdUser = createdUser;
  }

  /**
   * Gets the modified date.
   *
   * @return the modified date
   */
  public Timestamp getModifiedDate() {
    return this.modifiedDate;
  }

  /**
   * Sets the modified date.
   *
   * @param modifiedDate the new modified date
   */
  public void setModifiedDate(final Timestamp modifiedDate) {
    this.modifiedDate = modifiedDate;
  }

  /**
   * Gets the modified user.
   *
   * @return the modified user
   */
  public String getModifiedUser() {
    return this.modifiedUser;
  }

  /**
   * Sets the modified user.
   *
   * @param modifiedUser the new modified user
   */
  public void setModifiedUser(final String modifiedUser) {
    this.modifiedUser = modifiedUser;
  }

  /**
   * Gets the version.
   *
   * @return the version
   */
  public Long getVersion() {
    return this.version;
  }

  /**
   * Sets the version.
   *
   * @param version the new version
   */
  public void setVersion(final Long version) {
    this.version = version;
  }


  /**
   * @return the emrVariant
   */
  public Long getEmrVariant() {
    return this.emrVariant;
  }


  /**
   * @param emrVariant the emrVariant to set
   */
  public void setEmrVariant(final Long emrVariant) {
    this.emrVariant = emrVariant;
  }

  /**
   * Gets the t emr emission standard.
   *
   * @return the t emr emission standard
   */
  public TEmrEmissionStandard getTEmrEmissionStandard() {
    return this.tEmrEmissionStandard;
  }

  /**
   * Sets the t emr emission standard.
   *
   * @param tEmrEmissionStandard the new t emr emission standard
   */
  public void setTEmrEmissionStandard(final TEmrEmissionStandard tEmrEmissionStandard) {
    this.tEmrEmissionStandard = tEmrEmissionStandard;
  }

  /**
   * Gets the t emr file.
   *
   * @return the t emr file
   */
  public TEmrFile getTEmrFile() {
    return this.tEmrFile;
  }

  /**
   * Sets the t emr file.
   *
   * @param tEmrFile the new t emr file
   */
  public void setTEmrFile(final TEmrFile tEmrFile) {
    this.tEmrFile = tEmrFile;
  }

  /**
   * Gets the tabv project variant.
   *
   * @return the tabv project variant
   */
  public TabvProjectVariant getTabvProjectVariant() {
    return this.tabvProjectVariant;
  }

  /**
   * Sets the tabv project variant.
   *
   * @param tabvProjectVariant the new tabv project variant
   */
  public void setTabvProjectVariant(final TabvProjectVariant tabvProjectVariant) {
    this.tabvProjectVariant = tabvProjectVariant;
  }

}