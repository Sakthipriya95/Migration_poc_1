package com.bosch.caltool.icdm.database.entity.apic.emr;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.bosch.caltool.icdm.database.entity.apic.TPidcVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvIcdmFile;

/**
 * The persistent class for the T_EMR_FILE database table.
 */
@Entity
@OptimisticLocking(cascade = true)
@Table(name = "T_EMR_FILE")
@NamedQueries(value = {
    @NamedQuery(name = TEmrFile.GET_BY_PIDC_VERSION, query = "SELECT t FROM TEmrFile t where t.deletedFlag ='N' and t.tPidcVersion.pidcVersId = :" +
        TEmrFile.QP_PIDC_VERS_ID),
    @NamedQuery(name = TEmrFile.GET_BY_PIDC_VARIANT, query = "SELECT DISTINCT emrFile FROM TEmrFile emrFile, TEmrPidcVariant emrVar" +
        " WHERE emrFile.emrFileId = emrVar.tEmrFile.emrFileId AND emrFile.deletedFlag ='N' AND emrVar.tabvProjectVariant.variantId = :" +
        TEmrFile.QP_VARIANT_ID) })

public class TEmrFile implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   *
   */
  public static final String GET_BY_PIDC_VERSION = "TEmrFile.findByPidcVersion";

  /**
   *
   */
  public static final String GET_BY_PIDC_VARIANT = "TEmrFile.findByPidcVariant";

  /**
   *
   */
  public static final String QP_PIDC_VERS_ID = "pidcVersId";

  /**
   *
   */
  public static final String QP_VARIANT_ID = "variantId";

  /** The emr file id. */
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQV_ATTRIBUTES_GENERATOR")
  @Column(name = "EMR_FILE_ID")
  private long emrFileId;

  /** The created date. */
  @Column(name = "CREATED_DATE")
  private Timestamp createdDate;

  /** The created user. */
  @Column(name = "CREATED_USER")
  private String createdUser;

  /** The deleted flag. */
  @Column(name = "DELETED_FLAG")
  private String deletedFlag;

  /** The description. */
  private String description;

  /** The is variant. */
  @Column(name = "IS_VARIANT")
  private String isVariant;

  /** The loaded without errors flag. */
  @Column(name = "LOADED_WITHOUT_ERRORS_FLAG")
  private String loadedWithoutErrorsFlag;

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

  /** The T emr file data. */
  // bi-directional many-to-one association to TEmrFileData
  @OneToMany(mappedBy = "TEmrFile")
  private Set<TEmrFileData> tEmrFileData;

  /** The T emr pidc variants. */
  // bi-directional many-to-one association to TEmrPidcVariant
  @OneToMany(mappedBy = "tEmrFile")
  @BatchFetch(value = BatchFetchType.JOIN)
  private Set<TEmrPidcVariant> tEmrPidcVariants;

  /** The T emr upload errors. */
  // bi-directional many-to-one association to TEmrUploadError
  @OneToMany(mappedBy = "TEmrFile")
  private Set<TEmrUploadError> tEmrUploadErrors;

  /** The tabv icdm file. */
  // bi-directional many-to-one association to TabvIcdmFile
  @ManyToOne
  @JoinColumn(name = "ICDM_FILE_ID")
  @BatchFetch(value = BatchFetchType.JOIN)
  private TabvIcdmFile tabvIcdmFile;

  /** The T pidc version. */
  // bi-directional many-to-one association to TPidcVersion
  @ManyToOne
  @JoinColumn(name = "PIDC_VERS_ID")
  private TPidcVersion tPidcVersion;

  /**
   * Instantiates a new t emr file.
   */
  public TEmrFile() {
    // Public constructor
  }

  /**
   * Gets the emr file id.
   *
   * @return the emr file id
   */
  public long getEmrFileId() {
    return this.emrFileId;
  }

  /**
   * Sets the emr file id.
   *
   * @param emrFileId the new emr file id
   */
  public void setEmrFileId(final long emrFileId) {
    this.emrFileId = emrFileId;
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
   * Gets the deleted flag.
   *
   * @return the deleted flag
   */
  public String getDeletedFlag() {
    return this.deletedFlag;
  }

  /**
   * Sets the deleted flag.
   *
   * @param deletedFlag the new deleted flag
   */
  public void setDeletedFlag(final String deletedFlag) {
    this.deletedFlag = deletedFlag;
  }

  /**
   * Gets the description.
   *
   * @return the description
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Sets the description.
   *
   * @param description the new description
   */
  public void setDescription(final String description) {
    this.description = description;
  }

  /**
   * Gets the checks if is variant.
   *
   * @return the checks if is variant
   */
  public String getIsVariant() {
    return this.isVariant;
  }

  /**
   * Sets the checks if is variant.
   *
   * @param isVariant the new checks if is variant
   */
  public void setIsVariant(final String isVariant) {
    this.isVariant = isVariant;
  }

  /**
   * Gets the loaded without errors flag.
   *
   * @return the loaded without errors flag
   */
  public String getLoadedWithoutErrorsFlag() {
    return this.loadedWithoutErrorsFlag;
  }

  /**
   * Sets the loaded without errors flag.
   *
   * @param loadedWithoutErrorsFlag the new loaded without errors flag
   */
  public void setLoadedWithoutErrorsFlag(final String loadedWithoutErrorsFlag) {
    this.loadedWithoutErrorsFlag = loadedWithoutErrorsFlag;
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
   * Gets the t emr file data.
   *
   * @return the t emr file data
   */
  public Set<TEmrFileData> getTEmrFileData() {
    return this.tEmrFileData;
  }

  /**
   * Sets the t emr file data.
   *
   * @param tEmrFileData the new t emr file data
   */
  public void setTEmrFileData(final Set<TEmrFileData> tEmrFileData) {
    this.tEmrFileData = tEmrFileData;
  }

  /**
   * Adds the TEmrFileData.
   *
   * @param temrFileData the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData addTEmrFileData(final TEmrFileData temrFileData) {
    if (null == getTEmrFileData()) {
      this.tEmrFileData = new HashSet<>();
    }
    getTEmrFileData().add(temrFileData);
    temrFileData.setTEmrFile(this);

    return temrFileData;
  }

  /**
   * Removes the TEmrFileData.
   *
   * @param temrFileData the TEmrFileData
   * @return the TEmrFileData
   */
  public TEmrFileData removeTEmrFileData(final TEmrFileData temrFileData) {
    getTEmrFileData().remove(temrFileData);
    temrFileData.setTEmrFile(null);

    return temrFileData;
  }

  /**
   * Gets the t emr pidc variants.
   *
   * @return the t emr pidc variants
   */
  public Set<TEmrPidcVariant> getTEmrPidcVariants() {
    return this.tEmrPidcVariants;
  }

  /**
   * Sets the t emr pidc variants.
   *
   * @param tEmrPidcVariants the new t emr pidc variants
   */
  public void setTEmrPidcVariants(final Set<TEmrPidcVariant> tEmrPidcVariants) {
    this.tEmrPidcVariants = tEmrPidcVariants;
  }

  /**
   * Adds the T emr pidc variant.
   *
   * @param tEmrPidcVariant the t emr pidc variant
   * @return the t emr pidc variant
   */
  public TEmrPidcVariant addTEmrPidcVariant(final TEmrPidcVariant tEmrPidcVariant) {
    if (getTEmrPidcVariants() == null) {
      this.tEmrPidcVariants = new HashSet<>();
    }
    getTEmrPidcVariants().add(tEmrPidcVariant);
    tEmrPidcVariant.setTEmrFile(this);

    return tEmrPidcVariant;
  }

  /**
   * Removes the T emr pidc variant.
   *
   * @param tEmrPidcVariant the t emr pidc variant
   * @return the t emr pidc variant
   */
  public TEmrPidcVariant removeTEmrPidcVariant(final TEmrPidcVariant tEmrPidcVariant) {
    getTEmrPidcVariants().remove(tEmrPidcVariant);
    tEmrPidcVariant.setTEmrFile(null);

    return tEmrPidcVariant;
  }

  /**
   * Gets the t emr upload errors.
   *
   * @return the t emr upload errors
   */
  public Set<TEmrUploadError> getTEmrUploadErrors() {
    return this.tEmrUploadErrors;
  }

  /**
   * Sets the t emr upload errors.
   *
   * @param tEmrUploadErrors the new t emr upload errors
   */
  public void setTEmrUploadErrors(final Set<TEmrUploadError> tEmrUploadErrors) {
    this.tEmrUploadErrors = tEmrUploadErrors;
  }

  /**
   * Adds the T emr upload error.
   *
   * @param tEmrUploadError the t emr upload error
   * @return the t emr upload error
   */
  public TEmrUploadError addTEmrUploadError(final TEmrUploadError tEmrUploadError) {
    if (this.tEmrUploadErrors == null) {
      this.tEmrUploadErrors = new HashSet<>();
    }
    getTEmrUploadErrors().add(tEmrUploadError);
    tEmrUploadError.setTEmrFile(this);

    return tEmrUploadError;
  }

  /**
   * Removes the T emr upload error.
   *
   * @param tEmrUploadError the t emr upload error
   * @return the t emr upload error
   */
  public TEmrUploadError removeTEmrUploadError(final TEmrUploadError tEmrUploadError) {
    if (this.tEmrUploadErrors == null) {
      this.tEmrUploadErrors = new HashSet<>();
    }
    getTEmrUploadErrors().remove(tEmrUploadError);
    tEmrUploadError.setTEmrFile(null);

    return tEmrUploadError;
  }

  /**
   * Gets the tabv icdm file.
   *
   * @return the tabv icdm file
   */
  public TabvIcdmFile getTabvIcdmFile() {
    return this.tabvIcdmFile;
  }

  /**
   * Sets the tabv icdm file.
   *
   * @param tabvIcdmFile the new tabv icdm file
   */
  public void setTabvIcdmFile(final TabvIcdmFile tabvIcdmFile) {
    this.tabvIcdmFile = tabvIcdmFile;
  }

  /**
   * Gets the t pidc version.
   *
   * @return the t pidc version
   */
  public TPidcVersion getTPidcVersion() {
    return this.tPidcVersion;
  }

  /**
   * Sets the t pidc version.
   *
   * @param tPidcVersion the new t pidc version
   */
  public void setTPidcVersion(final TPidcVersion tPidcVersion) {
    this.tPidcVersion = tPidcVersion;
  }

}