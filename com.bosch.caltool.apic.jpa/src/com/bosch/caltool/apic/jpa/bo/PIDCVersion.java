package com.bosch.caltool.apic.jpa.bo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import com.bosch.calcomp.externallink.ILinkableObject;
import com.bosch.caltool.dmframework.notification.IEntityType;
import com.bosch.caltool.icdm.common.util.ApicUtil;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.common.util.Language;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrix;
import com.bosch.caltool.icdm.database.entity.apic.TFocusMatrixVersion;
import com.bosch.caltool.icdm.database.entity.apic.TabvPidcDetStructure;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectAttr;
import com.bosch.caltool.icdm.database.entity.apic.TabvProjectVariant;
import com.bosch.caltool.icdm.model.apic.ApicConstants;

/**
 * This class represents an APIC user as stored in the database table TABV_PROJECTIDCARD
 *
 * @author hef2fe
 * @version 1.0
 * @created 08-Feb-2013 14:03:34
 */
/**
 * @author rgo7cob
 */
@Deprecated
public class PIDCVersion extends AbstractProjectObject<PIDCAttribute>
    implements Comparable<PIDCVersion>, IPastableItem, ILinkableObject {

  /**
   * Key name for PIDC revision field in object details
   */
  public static final String FLD_PIDC_REVISION = "PIDC_REVISION";
  /**
   * Key name for PIDC version state field in object details
   */
  public static final String FLD_PIDC_VRSN_STATE = "PIDC_VRSN_STATE";
  /**
   * Key name for PIDC version name field in object details
   */
  public static final String FLD_PIDC_VRSN_NAME = "FLD_PIDC_VRSN_NAME";
  /**
   * Key name for PIDC version desc eng field in object details
   */
  public static final String FLD_PIDC_VRSN_DESC_ENG = "FLD_PIDC_VRSN_DESC_ENG";
  /**
   * Key name for PIDC version desc eng field in object details
   */
  public static final String FLD_PIDC_VRSN_DESC_GER = "FLD_PIDC_VRSN_DESC_GER";

  /**
   * string builder size for tooltip
   */
  private static final int STR_BUILDER_SIZE = 65;

  private static final Long FM_WORKING_SET_VERSION = 0L;

  private final Map<Long, PIDCAttribute> allAttrMap;

  /**
   * Map of deleted attributes
   */
  private final Map<Long, PIDCAttribute> deletedAttrsMap;

  /**
   * Invisible attributes
   */
  private final SortedSet<Attribute> invisibleAttributes;

  /**
   * Invisible project attributes
   */
  private final Map<Long, PIDCAttribute> invsblePIDCAttrMap;

  /**
   * Project attributes, defined at variant level
   */
  private final Map<Long, PIDCAttribute> variantAttrMap;

  /**
   * Used attributes
   */
  private final Map<Long, PIDCAttribute> usedPIDCAttrMap;

  /**
   * Not used attributes
   */
  private final Map<Long, PIDCAttribute> notUsedPIDCAttrMap;

  /**
   * Not defined attributes
   */
  private final Map<Long, PIDCAttribute> usedNotDefindPIDCAttrMap;

  /**
   * Data provider
   */
  private final ApicDataProvider dataProvider;

  // ICDM 397
  /**
   * Maximum level of det structure
   */
  private long pidcDetStructMaxLevel;// 0 by default

  /**
   * Virtual level structure
   */
  // ICDM 397
  private final Map<Long, PIDCDetStructure> pidcVirtualLevelAttrsMap;

  /**
   * Details node manager
   */
  private final PidcDetNodeManager nodeMgr = new PidcDetNodeManager(this);

  // ICDM-479
  private final Map<Long, PIDCAttribute> attrMap = new ConcurrentHashMap<>();


  /**
   * duplicate alias names
   */
  private final List<String> dupAlaiasNames = new ArrayList<>();


  /**
   * If true, focus matrix children are loaded
   */
  // ICDM-2569
  private boolean focusMatrixVersLoaded;

  /**
   * The grouped attributes of a particular version is available in this set
   */
  private final Set<IPIDCAttribute> groupedAttrs = new HashSet<>();

  /**
   * Map of Focus matrix versions.
   * <p>
   * Key - version number. Working set version is 0<br>
   * Value - version BO
   */
  // ICDM-2569
  private final ConcurrentMap<Long, FocusMatrixVersion> fmVersMap = new ConcurrentHashMap<>();

  private PIDCVersionStatistics pidcVersionStatistics;

  private final ConcurrentMap<IPIDCAttribute, IPIDCAttribute> predefAttrGrpAttrMap = new ConcurrentHashMap<>();

  /**
   * @return the predefAttrGrpAttrMap key - predefined attr , value - grouped attr
   */
  public ConcurrentMap<IPIDCAttribute, IPIDCAttribute> getPredefAttrGrpAttrMap() {
    return this.predefAttrGrpAttrMap;
  }

  /**
   * PIDCVersion Constructor
   *
   * @param apicDataProvider the dataprovider
   * @param pidcVersID version's Primary Key
   */
  public PIDCVersion(final ApicDataProvider apicDataProvider, final Long pidcVersID) {

    super(apicDataProvider, pidcVersID);

    this.dataProvider = apicDataProvider;

    getDataCache().getAllPidcVersionMap().put(pidcVersID, this);

    this.allAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.deletedAttrsMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.variantAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.usedPIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.notUsedPIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.usedNotDefindPIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.invisibleAttributes = Collections.synchronizedSortedSet(new TreeSet<Attribute>());

    this.invsblePIDCAttrMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCAttribute>());

    this.pidcVirtualLevelAttrsMap = Collections.synchronizedMap(new ConcurrentHashMap<Long, PIDCDetStructure>());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isModifiable() {
    // ICDM-2354
    NodeAccessRight curUserAccRight = getPidc().getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess()) {
      return !getPidc().isDeleted();
    }
    return false;
  }

  /**
   * @return true if variant can be created for the PIDC
   */
  public boolean canCreateVariant() {

    return isModifiable() && (getStatus() == PidcVersionStatus.IN_WORK);

  }

  /**
   * @return true if sub variant can be created for the PIDC
   */
  public boolean canCreateSubVariant() {
    return isModifiable() && (getStatus() == PidcVersionStatus.IN_WORK);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Long getVersion() {
    return getEntityProvider().getDbPIDCVersion(getID()).getVersion();
  }

  /**
   * @return PIDC name
   */
  public String getPidcVersionName() {
    return getEntityProvider().getDbPIDCVersion(getID()).getVersName();
  }

  // ICDM-1635
  @Override
  public PIDCVersion getPidcVersion() {
    return this;
  }

  /**
   * @return the Pidc Version English Desc
   */
  public String getPidcVersionDescEng() {
    return getEntityProvider().getDbPIDCVersion(getID()).getVersDescEng();
  }

  /**
   * @return the Pidc Version German Desc
   */
  public String getPidcVersionDescGer() {
    return getEntityProvider().getDbPIDCVersion(getID()).getVersDescGer();
  }

  /**
   * @return PIDC name
   */
  public String getPidcName() {
    return getPidc().getName();
  }

  /**
   * @return true if the parent PIDC is deleted
   */
  public boolean isDeleted() {
    return getPidc().isDeleted();
  }

  /**
   * @return Created Date
   */
  @Override
  public Calendar getCreatedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDCVersion(getID()).getCreatedDate());
  }

  /**
   * @return Modified date
   */
  @Override
  public Calendar getModifiedDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDCVersion(getID()).getModifiedDate());
  }

  /**
   * @return Created User
   */
  @Override
  public String getCreatedUser() {
    return getEntityProvider().getDbPIDCVersion(getID()).getCreatedUser();
  }

  /**
   * @return Modified User
   */
  @Override
  public String getModifiedUser() {
    return getEntityProvider().getDbPIDCVersion(getID()).getModifiedUser();
  }

  /**
   * @return PIDC revision id
   */
  public long getPidcRevision() {
    return getEntityProvider().getDbPIDCVersion(getID()).getProRevId();
  }

  /**
   * Returns status of the PIDC Version
   *
   * @return status of the Version
   */
  public PidcVersionStatus getStatus() {
    return PidcVersionStatus.getStatus(getEntityProvider().getDbPIDCVersion(getID()).getPidStatus());
  }

  /**
   * @return all attributes of the PIDC with refresh it is called when loading PIDC
   */
  @Override
  public Map<Long, PIDCAttribute> getAttributes() {
    return getAttributes(true);
  }

  /**
   * Get all attributes of this Project version.
   * <p>
   * The return map includes : <br>
   * a) all attributes not defined yet<br>
   * b) all defined attributes<br>
   * c) Project level attributes, when attributes are definded at variant/sub-variant level<br>
   * <p>
   * The return does NOT include : <br>
   * a) invisible attributes.<br>
   * b) deleted attributes<br>
   * c) name attributes for PIDC, Variant, Sub-Variant
   *
   * @param refresh value true loads freshly, refresh false takes existing attrmap
   * @return all atrributes. Key - attribute ID; Value - Project attribute
   */
  @Override
  public synchronized Map<Long, PIDCAttribute> getAttributes(final boolean refresh) {

    // Reload all attribute collections when (a) input flag is true, or (b) not loaded before.
    if (refresh || !this.childrenLoaded) {
      this.allAttrMap.clear();
      this.attrMap.clear();
      // get undefined attributes
      fillAllAttributes();

      // get defined attributes
      fillDefinedAttributes();

      this.attrMap.putAll(this.allAttrMap);

      // Thread dead lock heppens when pidcversion getAttributes is locked by thread 1 and pidc varaint getAttribute is
      // locked by thread 2.
      // here thread 1 again itreates through the variants and calls getAttributes so dead lock occurs.
      if (!CommonUtils.isStartedFromWebService()) {
        fillApplicableGroupedAttributes();
        fillPredefinedAttributes();
      }

      // remove invisible attributes from lists
      removeInvisibleAttributes();


      this.childrenLoaded = true;

    }

    // return complete list
    return this.allAttrMap;
  }


  /**
   * @return map of all attributes with key as attribute ID
   */
  // ICDM-479
  @Override
  public Map<Long, PIDCAttribute> getAttributesAll() {
    getAttributes();
    return this.attrMap;

  }

  /**
   * Icdm-891
   *
   * @return boolean indicating the pid card has Invalid attr Values
   */
  @Override
  public boolean hasInvalidAttrValues() {
    boolean ret = false;
    final Map<Long, PIDCAttribute> allAttributes = getAttributes(false);
    // Check attributes at PIDC Version level first
    for (PIDCAttribute pidcAttr : allAttributes.values()) {
      if (!pidcAttr.isVariant() && pidcAttr.isValueInvalid()) {
        ret = true;
        break;
      }
    }
    if (!ret) {
      // If not invalid values found, also check attributes at variant level
      for (PIDCVariant variant : getVariantsSet()) {
        ret = variant.hasInvalidAttrValues();
        if (ret) {
          break;
        }
      }
    }
    return ret;
  }


  /**
   * @param pidcAttr pidcAttr
   * @return true is value is defined
   */
  public boolean isValueDefined(final PIDCAttribute pidcAttr) {
    boolean ret = false;
    if (pidcAttr.isVariant()) {
      for (PIDCVariant variant : getVariantsSet(false)) {
        PIDCAttributeVar pidcVarAttr = variant.getAttributes(false).get(pidcAttr.getAttribute().getID());
        // if attr is visible at pidc level and invisble at variant level, then attr is not relevant and cannot be
        // considered as 'Not defined'
        if (null == pidcVarAttr) {
          continue;
        }
        ret = variant.isValueDefined(pidcVarAttr);
        if (!ret) {
          break;
        }
      }
    }
    if (((ApicConstants.PROJ_ATTR_USED_FLAG.YES == pidcAttr.getIsUsedEnum()) &&
        (pidcAttr.getAttributeValue() != null)) || (ApicConstants.PROJ_ATTR_USED_FLAG.NO == pidcAttr.getIsUsedEnum())) {
      ret = true;

    }
    return ret;
  }

  /**
   * @return set of invisible attributes
   */
  public Set<Attribute> getInvisibleAttributes() {
    return this.invisibleAttributes;
  }

  /**
   * Remove the attributes which are not visible based on the attribute dependencies
   */
  private void removeInvisibleAttributes() {

    // clear the list of invisible attributes
    this.invisibleAttributes.clear();
    this.invsblePIDCAttrMap.clear();

    LinkedHashMap<Long, PIDCAttribute> linkedHashMap = new LinkedHashMap<Long, PIDCAttribute>();
    calculateInsertionOrder(this.allAttrMap, linkedHashMap, null);
    // iterate over all attributes and check if they are valid
    // based on the dependencies
    for (PIDCAttribute pidcAttribute : linkedHashMap.values()) {
      // check the attributes dependencies based on the list of attribute
      // values
      // defined in this PIDC
      boolean valid = pidcAttribute.getAttribute().isValid(linkedHashMap);
      if (!valid) {
        this.invisibleAttributes.add(pidcAttribute.getAttribute());
        this.invsblePIDCAttrMap.put(pidcAttribute.getAttribute().getAttributeID(), pidcAttribute);
      }

    }
    // remove all invisible attributes
    for (Attribute attribute : this.invisibleAttributes) {
      this.allAttrMap.remove(attribute.getAttributeID());
    }

  }

  /**
   * Calculates the proper order of attributes taking into account the dependencies
   *
   * @param validAttrMap
   * @param orderedAttrMap
   * @param lastToPresent
   */
  private void calculateInsertionOrder(final Map<Long, PIDCAttribute> validAttrMap,
      final Map<Long, PIDCAttribute> orderedAttrMap, final Boolean lastToPresent) {

    for (Entry<Long, PIDCAttribute> attrEntry : validAttrMap.entrySet()) {
      Long attrID = attrEntry.getKey();
      PIDCAttribute pidcAttr = attrEntry.getValue();
      if (orderedAttrMap.containsKey(attrID)) {
        continue;
      }
      if ((lastToPresent == null) || lastToPresent) {
        // Fill from last to present for this list
        List<AttrDependency> attrDependencies = pidcAttr.getAttribute().getAttrDependencies(false);
        if ((attrDependencies != null) && !attrDependencies.isEmpty()) {
          Map<Long, PIDCAttribute> validAttrDependenciesInImport = new HashMap<Long, PIDCAttribute>();
          validAttrDependenciesInImport.putAll(validAttrMap);
          validAttrDependenciesInImport.remove(attrID);
          isRecursionRequired(validAttrMap, orderedAttrMap, attrDependencies, validAttrDependenciesInImport);
          orderedAttrMap.put(attrID, pidcAttr);
        }
        else {
          orderedAttrMap.put(attrID, pidcAttr);
        }
      }

      if ((lastToPresent == null) || !lastToPresent) {
        // Fill from present+1 to first for this list
        List<AttrDependency> referentialAttrDependencies =
            pidcAttr.getAttribute().getReferentialAttrDependencies(false);
        if ((referentialAttrDependencies != null) && !referentialAttrDependencies.isEmpty()) {
          Map<Long, PIDCAttribute> validAttrRefDepen = new HashMap<Long, PIDCAttribute>();
          validAttrRefDepen.putAll(validAttrMap);
          validAttrRefDepen.remove(attrID);
          isRecursionRequired(validAttrMap, orderedAttrMap, attrID, pidcAttr, referentialAttrDependencies,
              validAttrRefDepen);
        }
        else {
          if (!orderedAttrMap.containsKey(attrID)) {
            orderedAttrMap.put(attrID, pidcAttr);
          }
        }
      }
    }
  }

  /**
   * @param validAttrMap
   * @param orderedAttrMap
   * @param attrID
   * @param pidcAttr
   * @param referentialAttrDependencies
   * @param validAttrRefDepen
   */
  private void isRecursionRequired(final Map<Long, PIDCAttribute> validAttrMap,
      final Map<Long, PIDCAttribute> orderedAttrMap, final Long attrID, final PIDCAttribute pidcAttr,
      final List<AttrDependency> referentialAttrDependencies, final Map<Long, PIDCAttribute> validAttrRefDepen) {
    boolean recursionRequired = false;
    for (AttrDependency attrRefDependency : referentialAttrDependencies) {
      Attribute attribute = attrRefDependency.getAttribute();
      if (attribute == null) {
        continue;
      }
      Long attributeID = attribute.getAttributeID();
      if (validAttrMap.containsKey(attributeID)) {
        recursionRequired = true;
      }
    }
    if (!orderedAttrMap.containsKey(attrID)) {
      orderedAttrMap.put(attrID, pidcAttr);
    }
    if (recursionRequired) {
      calculateInsertionOrder(validAttrRefDepen, orderedAttrMap, false);
    }
  }

  /**
   * @param validAttrMap
   * @param orderedAttrMap
   * @param attrDependencies
   * @param validAttrDependenciesInImport
   */
  private void isRecursionRequired(final Map<Long, PIDCAttribute> validAttrMap,
      final Map<Long, PIDCAttribute> orderedAttrMap, final List<AttrDependency> attrDependencies,
      final Map<Long, PIDCAttribute> validAttrDependenciesInImport) {
    boolean recursionRequired = false;
    for (AttrDependency attrDependency : attrDependencies) {
      Attribute dependencyAttribute = attrDependency.getDependencyAttribute();
      if (dependencyAttribute == null) {
        continue;
      }
      Long attributeID = dependencyAttribute.getAttributeID();
      if (validAttrMap.containsKey(attributeID)) {
        recursionRequired = true;
      }
    }
    if (recursionRequired) {
      calculateInsertionOrder(validAttrDependenciesInImport, orderedAttrMap, true);
    }
  }

  private void fillDefinedAttributes() {

    PIDCAttribute pidcAttr;

    // add attributes with or without values
    // exactly: all attributes defined in TabV_ProjectAttr
    for (TabvProjectAttr dbPidcAttr : getEntityProvider().getDbPIDCVersion(getID()).getTabvProjectAttrs()) {
      pidcAttr = getDataCache().getPidcAttribute(dbPidcAttr.getPrjAttrId());

      if (pidcAttr == null) {
        pidcAttr = new PIDCAttribute(this.dataProvider, dbPidcAttr.getPrjAttrId());
      }

      // skip attributes marked as deleted
      if (pidcAttr.getAttribute().isDeleted()) {
        continue;
      }

      // remove the empty attribute
      this.allAttrMap.remove(pidcAttr.getAttribute().getAttributeID());

      // add the attribute
      this.allAttrMap.put(dbPidcAttr.getTabvAttribute().getAttrId(), pidcAttr);
    }
  }

  private void fillAllAttributes() {

    PIDCAttribute pidcAttr;

    // add attributes to the list
    for (Attribute attribute : getDataCache().getAllAttributes().values()) {
      // Putting the deleted attributes in a separate map
      if (attribute.isDeleted()) {
        pidcAttr = new PIDCAttribute(this.dataProvider, null, attribute.getAttributeID(), getID());
        this.deletedAttrsMap.put(attribute.getAttributeID(), pidcAttr);
      }

      // skip attributes which are marked as deleted ,and also attributes
      // having levels -1,-2 and -3 which are
      // attributes Project Name,Variant Code and VariantCoding Code respectively.
      // ICDM-211
      if (attribute.isDeleted() || (attribute.getAttrLevel() == ApicConstants.PROJECT_NAME_ATTR) ||
          (attribute.getAttrLevel() == ApicConstants.VARIANT_CODE_ATTR) ||
          (attribute.getAttrLevel() == ApicConstants.SUB_VARIANT_CODE_ATTR)) {
        continue;
      }

      pidcAttr = new PIDCAttribute(this.dataProvider, null, attribute.getAttributeID(), getID());

      this.allAttrMap.put(attribute.getAttributeID(), pidcAttr);
    }

  }

  /**
   * Get a MAP of the PIDCs Variants including deleted variants
   *
   * @return map of variants, with key as variant id and value as variant object
   */
  public Map<Long, PIDCVariant> getVariantsMap() {
    final Map<Long, PIDCVariant> pidcVariantsMap = new ConcurrentHashMap<Long, PIDCVariant>();

    for (TabvProjectVariant pidcVariant : getEntityProvider().getDbPIDCVersion(getID()).getTabvProjectVariants()) {
      pidcVariantsMap.put(pidcVariant.getVariantId(), getDataCache().getPidcVaraint(pidcVariant.getVariantId()));
    }
    this.childrenLoaded = true;
    return pidcVariantsMap;
  }

  /**
   * ICDM-789 Get a MAP of the PIDCs Variants
   *
   * @param deleteNeeded deletedNeeded true or false if deleted variants are required
   * @return map of variants, with key as variant id and value as variant object
   */
  public Map<Long, PIDCVariant> getVariantsMap(final boolean deleteNeeded) {

    return getVariantsMap(deleteNeeded, false);
  }

  // ICDM-2198
  /**
   * Get a MAP of the PIDCs Variants
   *
   * @param deleteNeeded deletedNeeded true or false if deleted variants are required
   * @param showOnlyUncleared true or false if variants with only uncleared / missing mandatory values to be displayed
   *          showOnlyUncleared = true --> Only variants with uncleared / missing mandatory values will be displayed in
   *          PIDC structure view
   * @return map of variants, with key as variant id and value as variant object
   */
  public Map<Long, PIDCVariant> getVariantsMap(final boolean deleteNeeded, final boolean showOnlyUncleared) {

    final Map<Long, PIDCVariant> pidcVariantsMapIterate = getVariantsMap();

    final Map<Long, PIDCVariant> pidcVariantsMap = new ConcurrentHashMap<Long, PIDCVariant>();

    for (PIDCVariant pidcVariant : pidcVariantsMapIterate.values()) {

      // Check if uncleared variants need to be displayed
      if (showOnlyUncleared) {
        // If uncleared variants are needed, check if it has invalid attributes and mandatory attributes are not defined
        if (pidcVariant.hasInvalidAttrValues() || (!pidcVariant.isAllMandatoryAttrDefined())) {
          checkIfDeletedNeeded(pidcVariantsMap, pidcVariant);
        }
      }
      else {
        checkIfDeletedNeeded(pidcVariantsMap, pidcVariant);
      }
      // Check whether deleted variants are needed
      if (deleteNeeded && pidcVariant.isDeleted()) {
        pidcVariantsMap.put(pidcVariant.getVariantID(), pidcVariant);
      }
    }

    return pidcVariantsMap;
  }

  /**
   * @param pidcVariantsMap
   * @param pidcVariant
   */
  private void checkIfDeletedNeeded(final Map<Long, PIDCVariant> pidcVariantsMap, final PIDCVariant pidcVariant) {
    // Check whether the variant is not deleted, else deleted variants will get added irrespective of the 'show deleetd'
    // flag
    if (!pidcVariant.isDeleted()) {
      pidcVariantsMap.put(pidcVariant.getVariantID(), pidcVariant);
    }
  }

  /**
   * Get a map of the structure attrs ICDM 397
   *
   * @return virtual level attributes with key as 'level' and value as 'structure object'
   */
  public Map<Long, PIDCDetStructure> getVirtualLevelAttrs() {

    this.pidcVirtualLevelAttrsMap.clear();
    for (TabvPidcDetStructure pidDetStruct : getEntityProvider().getDbPIDCVersion(getID()).getTabvPidcDetStructures()) {

      this.pidcVirtualLevelAttrsMap.put(pidDetStruct.getPidAttrLevel(),
          new PIDCDetStructure(this.dataProvider, pidDetStruct.getPdsId()));
      setPidcDetStructMaxLevel(Math.max(getPidcDetStructMaxLevel(), pidDetStruct.getPidAttrLevel().longValue()));

    }
    this.childrenLoaded = true;
    return this.pidcVirtualLevelAttrsMap;
  }

  /**
   * This method checks for whether all mandatory attrs are defined in PIDC
   *
   * @return boolean defines whether all mandatory attrs are defined in PIDC
   */
  // ICDM-329
  // ICDM-179
  @Override
  public final boolean isAllMandatoryAttrDefined() {
    boolean allManAttrDefined;
    // Get PIDC all attributes
    final Collection<PIDCAttribute> pidcAttrs = this.getAttributes(false).values();
    // Validate Pidc attributes
    allManAttrDefined = checkManProjAttributes(pidcAttrs);
    if (allManAttrDefined) {
      allManAttrDefined = canReleaseVariantAttrs();
    }
    if (null != this.pidcVersionStatistics) {
      int definedImpAttrCount = this.pidcVersionStatistics.getUsedFlagSetImpAttrCount();
      int totalImpAttrCount = this.pidcVersionStatistics.getImportantAttrCount();
      if ((totalImpAttrCount - definedImpAttrCount) > 0) {
        allManAttrDefined = false;
      }
    }
    return allManAttrDefined;
  }

  /**
   * @return the isProjUseCaseAttrNotDefined
   */
  public boolean isProjUseCaseAttrNotDefined() {
    if (this.pidcVersionStatistics != null) {
      return this.pidcVersionStatistics.isProjUseCaseAttrNotDefined();
    }
    return false;
  }

  /**
   * This method checks for whether PIDC Variant attributes can do release or not
   *
   * @return boolean
   */
  // ICDM-329
  // ICDM-179
  private boolean canReleaseVariantAttrs() {
    boolean canRelease = true;
    // Get PIDC variants
    for (PIDCVariant pidcVariant : getVariantsMap().values()) {
      // Check if variant is not deleted
      if (!pidcVariant.isDeleted()) {
        // Get Variant all attributes
        final Collection<PIDCAttributeVar> varaAttrs = pidcVariant.getAttributes(false, false).values();
        canRelease = checkManProjAttributes(varaAttrs);
        if (canRelease) {
          canRelease = checkManSubVarAttrs(pidcVariant);
          if (!canRelease) {
            break;
          }
        }
        else {
          break;
        }
      }
    }
    return canRelease;
  }

  /**
   * This method checks for whether PIDC Sub-Variant attributes can do release or not
   *
   * @param pidcVariant instance
   * @return boolean
   */
  // ICDM-329
  // ICDM-179
  private boolean checkManSubVarAttrs(final PIDCVariant pidcVariant) {
    boolean canRelease = true;
    // Get Variant Sub-variants
    for (PIDCSubVariant pidcSubVariant : pidcVariant.getSubVariantsMap().values()) {
      // Check if sub-variant is not deleted
      if (!pidcSubVariant.isDeleted()) {
        // Get Sub-variant all attributes
        final Collection<PIDCAttributeSubVar> subVarAttrs = pidcSubVariant.getAttributes(false, false).values();
        canRelease = checkManProjAttributes(subVarAttrs);
        if (!canRelease) {
          return canRelease;
        }
      }
    }
    return canRelease;
  }

  /**
   * This method validates PIDC/Variant/Sub-Variant attributes
   *
   * @param <P>
   * @param projAttrs
   * @return boolean
   */
  private <P extends IPIDCAttribute> boolean checkManProjAttributes(final Collection<P> projAttrs) {
    boolean canRelease = true;
    for (P attribute : projAttrs) {
      canRelease = isMandatoryAttrDefined(attribute);
      if (!canRelease) {
        break;
      }
    }
    return canRelease;
  }

  /**
   * This method checks for whether PIDC mandatory attribute is defined or not
   *
   * @param projAttr defines Project Attribute
   * @return boolean defines mandatory attribute is defined or not
   */
  // ICDM-329
  // ICDM-179
  protected <P extends IPIDCAttribute> boolean isMandatoryAttrDefined(final P projAttr) {
    boolean attrValStatus = true;
    // Check for attribute is not deleted, mandatory, not a variant
    if (projAttr.isMandatory() && !projAttr.getAttribute().isDeleted() && !projAttr.isVariant()) {
      final String usedFlag = projAttr.getIsUsed();
      // If flag = Yes then value should be provided.
      if ((ApicConstants.PROJ_ATTR_USED_FLAG.NEW_ATTR.getUiType().equals(usedFlag) ||
          ApicConstants.USED_NOTDEF_DISPLAY.equals(usedFlag)) ||
          (ApicConstants.USED_YES_DISPLAY.equals(usedFlag) && (projAttr.getAttributeValue() == null))) {
        attrValStatus = false;
      }
      // If used flag == No, then attribute is considered as 'defined'.
      // Value need not be checked
    }
    return attrValStatus;
  }

  /**
   * Get a sorted set of attributes using the given sort column
   *
   * @param sortColumn column to sort
   * @return the sorted set
   */
  public SortedSet<PIDCAttribute> getAllAttributes(final int sortColumn) {
    SortedSet<PIDCAttribute> resultSet = new TreeSet<PIDCAttribute>(new Comparator<PIDCAttribute>() {

      @Override
      public int compare(final PIDCAttribute pidcAttr1, final PIDCAttribute pidcAttr2) {
        return pidcAttr1.compareTo(pidcAttr2, sortColumn);
      }
    });

    resultSet.addAll(getAttributes().values());

    return resultSet;

  }

  /**
   * Returns the defined attributes of this PIDC. This includes the variant attributes and the structure attributes Key
   * is the attribute ID and value is an instance of PIDCAttribute
   *
   * @return the defined attributes
   */
  protected synchronized Map<Long, PIDCAttribute> getDefinedAttributes() {
    ConcurrentMap<Long, PIDCAttribute> definedAttrMap = new ConcurrentHashMap<Long, PIDCAttribute>();
    for (PIDCAttribute pidcAttr : getAttributes().values()) {
      if (pidcAttr.getID() != null) {
        definedAttrMap.put(pidcAttr.getAttribute().getAttributeID(), pidcAttr);
      }
    }
    return definedAttrMap;
  }

  /**
   * Get a sorted set of the PIDCs Variants
   *
   * @return variants
   */
  public synchronized SortedSet<PIDCVariant> getVariantsSet() {
    return new TreeSet<PIDCVariant>(getVariantsMap().values());
  }

  /**
   * Method also checks for deleted variants. If all variants are deleted retrun false
   *
   * @param strictMode flag to make the check in strict manner
   * @return true if the strict mode is false and pidc version has varaints. If the strictMode is true then return true
   *         if atleast one of the variants is not deleted.
   */
  public boolean hasVariants(final boolean strictMode) {
    if (strictMode) {
      for (PIDCVariant variant : getVariantsMap().values()) {
        if (!variant.isDeleted()) {
          return true;
        }
      }
      return false;
    }
    return !getVariantsMap().isEmpty();
  }


  /**
   * ICDM-789 Get a sorted set of the PIDCs Variants
   *
   * @param deletedNeeded true or false if deleted variants are required
   * @return variants SortedSet of variants
   */
  public synchronized SortedSet<PIDCVariant> getVariantsSet(final boolean deletedNeeded) {
    final SortedSet<PIDCVariant> resultSet = new TreeSet<PIDCVariant>();
    resultSet.addAll(getVariantsMap(deletedNeeded).values());
    return resultSet;
  }

  // ICDM-2198
  /**
   * Get a sorted set of the PIDCs Variants
   *
   * @param deletedNeeded true or false if deleted variants are required
   * @param unclearedNeeded true or false if variants with uncleared / missing mandatory values required
   * @return variants SortedSet of variants
   */
  public synchronized SortedSet<PIDCVariant> getVariantsSet(final boolean deletedNeeded,
      final boolean unclearedNeeded) {
    final SortedSet<PIDCVariant> resultSet = new TreeSet<PIDCVariant>();
    resultSet.addAll(getVariantsMap(deletedNeeded, unclearedNeeded).values());
    return resultSet;
  }

  /**
   * @return sorted set of PVERs
   */
  public synchronized SortedSet<SdomPver> getPVerSet() {

    SortedSet<SdomPver> resultSet = new TreeSet<SdomPver>();

    PIDCAttribute pidcPVerAttribute = getAttributes().get(getDataCache().getPVerAttribute().getAttributeID());

    if (pidcPVerAttribute.isVariant()) {
      // PVER attribute is variant => many SDOM PVER
      for (PIDCVariant variant : getVariantsMap(false).values()) {

        Map<Long, PIDCAttributeVar> varAttrs = variant.getAttributes();

        PIDCAttributeVar pVerAttr = varAttrs.get(getDataCache().getPVerAttribute().getAttributeID());

        if (pVerAttr == null) {
          // PVER attribute not defined
          continue;
        }

        AttributeValue pVerValue = pVerAttr.getAttributeValue();

        if (pVerValue == null) {
          // SDOM PVER attribute not defined in variant
          continue;
        }
        // add SDOM PVER attribute if not existing in list
        resultSet.add(new SdomPver(this.dataProvider, pVerValue, getID()));

      }

    }
    else {
      // PVER attribute is not variant => only one SDOM PVER
      AttributeValue pVerValue = pidcPVerAttribute.getAttributeValue();
      if (pVerValue != null) {
        resultSet.add(new SdomPver(this.dataProvider, pVerValue, getID()));
      }
    }

    return resultSet;
  }

  /**
   * Method also checks for deleted variants. If all variants are deleted retrun false
   *
   * @return whether the pidc has variants
   */
  public boolean hasVariants() {
    return hasVariants(true);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compareTo(final PIDCVersion pidcVersion) {
    int compareResult = ApicUtil.compare(getName(), pidcVersion.getName()); // ICDM-1501
    if (compareResult == 0) {
      compareResult = ApicUtil.compare(getID(), pidcVersion.getID());
    }
    return compareResult;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean equals(final Object obj) {
    return super.equals(obj);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int hashCode() {
    return super.hashCode();
  }

  /**
   * This method returns all variant attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  public synchronized Map<Long, PIDCAttribute> getAllVariantAttributes() {
    this.variantAttrMap.clear();
    // Get all variant PIDC attributes
    for (PIDCAttribute attribute : this.allAttrMap.values()) {
      if (attribute.isVariant()) {
        this.variantAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }

    }
    return this.variantAttrMap;
  }


  /**
   * This method returns used PIDC attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  @Override
  public synchronized Map<Long, PIDCAttribute> getAttributesUsed() {
    this.usedPIDCAttrMap.clear();
    // Get all used PIDC attributes
    for (PIDCAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.YES.getUiType().equals(attribute.getIsUsed()) &&
          !attribute.isValueInvalid()) {
        this.usedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }

    return this.usedPIDCAttrMap;
  }

  /**
   * This method returns not used PIDC attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  @Override
  public synchronized Map<Long, PIDCAttribute> getAttributesNotUsed() {
    this.notUsedPIDCAttrMap.clear();
    // Get all not used PIDC attributes
    for (PIDCAttribute attribute : this.allAttrMap.values()) {
      if (ApicConstants.PROJ_ATTR_USED_FLAG.NO.getUiType().equals(attribute.getIsUsed()) &&
          !attribute.isValueInvalid()) {
        this.notUsedPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.notUsedPIDCAttrMap;
  }

  /**
   * This method returns used not defined PIDC attributes
   *
   * @return Map<Long, PIDCAttribute>
   */
  @Override
  public synchronized Map<Long, PIDCAttribute> getAttributesNotDefined() {
    this.usedNotDefindPIDCAttrMap.clear();
    // Get all used not defined PIDC attributes
    for (PIDCAttribute attribute : this.allAttrMap.values()) {
      if (CommonUtils.isEqual(ApicConstants.PROJ_ATTR_USED_FLAG.NOT_DEFINED.getUiType(), attribute.getIsUsed()) ||
          attribute.isValueInvalid()) {
        this.usedNotDefindPIDCAttrMap.put(attribute.getAttribute().getAttributeID(), attribute);
      }
    }
    return this.usedNotDefindPIDCAttrMap;
  }

  /**
   * Returns the PIDC Node to which this PIDC belongs.
   * <p>
   * Use getPidc().getLeafNode() instead
   *
   * @return the PIDC node
   */
  @Deprecated
  public PIDCNode getPIDCNode() {
    return getPidc().getLeafNode();
  }

  /**
   * @return summary of data
   */
  @Override
  public Map<String, String> getObjectDetails() {
    ConcurrentMap<String, String> summaryMap = new ConcurrentHashMap<String, String>();

    // Name, description, deleted flag etc. is not added to the map as this
    // is available in the attribute value
    summaryMap.put(FLD_PIDC_REVISION, String.valueOf(getPidcRevision()));
    if (null != getStatus()) {
      summaryMap.put(FLD_PIDC_VRSN_STATE, getStatus().getUiStatus());
    }
    summaryMap.put(FLD_PIDC_VRSN_NAME, getPidcVersionName());
    if (null != getPidcVersionDescEng()) {
      summaryMap.put(FLD_PIDC_VRSN_DESC_ENG, getPidcVersionDescEng());
    }
    if (null != getPidcVersionDescGer()) {
      summaryMap.put(FLD_PIDC_VRSN_DESC_GER, getPidcVersionDescGer());
    }
    return summaryMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isPasteAllowed(final Object selectedObj, final Object copiedObj) {
    // Get the user info
    final ApicUser apicUser = this.dataProvider.getCurrentUser();
    if ((selectedObj instanceof PIDCNode) && (copiedObj instanceof PIDCVersion) && apicUser.canCreatePIDC()) {
      final PIDCNode pidcNode = (PIDCNode) selectedObj;
      final long maxLevel = getDataCache().getPidcStructMaxLevel();
      if (pidcNode.getLevel() == maxLevel) {
        return true;
      }
    }
    return false;
  }


  /**
   * @return maximum structure level
   */
  public long getPidcDetStructMaxLevel() {
    return this.pidcDetStructMaxLevel;
  }

  /**
   * @param maxLevel ICDM 397
   */
  public void setPidcDetStructMaxLevel(final long maxLevel) {
    this.pidcDetStructMaxLevel = maxLevel;
  }

  /**
   * @return This map stores the attributes which are deleted ICDM 449
   */
  public Map<Long, PIDCAttribute> getDeletedAttrsMap() {
    return this.deletedAttrsMap;
  }

  /**
   * Gets the root level nodes of this project ID card. If virtual structure is not defined for this project, the method
   * returns null
   *
   * @param includeDeletedVarSubVars true to include deleted variants and sub variants
   * @param onlyUncleared true if variants with uncleared / missing mandatory values required
   * @return PIDC Nodes at the root level
   */
  public SortedSet<PIDCDetailsNode> getRootVirtualNodes(final boolean includeDeletedVarSubVars,
      final boolean onlyUncleared) {
    // iCDM-911
    // iCDM-2198
    this.nodeMgr.setIncludeDeletedVarSubVars(includeDeletedVarSubVars);
    this.nodeMgr.setShowOnlyUncleared(onlyUncleared);
    return this.nodeMgr.getRootVirtualNodes();
  }

  /**
   * Reset the virtual node structure. To be used if the virtual structure is changed
   */
  public void resetNodes() {
    this.nodeMgr.resetNodes();
  }

  /**
   * Refresh the nodes. To be used if attribute values are changed.
   */
  public void refreshNodes() {
    this.nodeMgr.refreshNodes();
  }

  /**
   * Checks whether the attribute is a structure attribute
   *
   * @param attr attribute
   * @return true/false
   */
  public boolean isStructAttr(final Attribute attr) {
    if (!hasVirtualStructure()) {
      return false;
    }
    // Checking whether the attribute is a structure attribute
    for (PIDCDetStructure pidcdetStruct : getVirtualLevelAttrs().values()) {
      if (pidcdetStruct.getAttrID().longValue() == attr.getAttributeID().longValue()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether this PIDC has virtual structure
   *
   * @return true/false
   */
  public boolean hasVirtualStructure() {
    return (getEntityProvider().getDbPIDCVersion(getID()).getTabvPidcDetStructures() != null) &&
        !getEntityProvider().getDbPIDCVersion(getID()).getTabvPidcDetStructures().isEmpty();
  }


  /**
   * Gets the invisible attributes
   *
   * @return the invisiblePIDCAttributesMap with AttributeID as key
   */
  public Map<Long, PIDCAttribute> getInvisiblePIDCAttributesMap() {
    return this.invsblePIDCAttrMap;
  }


  /**
   * {@inheritDoc}
   */
  @Override
  public String getName() {
    return getPidc().getName() + " (" + getPidcVersionName() + ")";
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IEntityType<?, ?> getEntityType() {
    return EntityType.PIDC_VERSION;
  }

  // ICDM-1107
  /**
   * @return tooltip with name & description
   */
  @Override
  public String getToolTip() {
    StringBuilder tooltip = new StringBuilder(PIDCVersion.STR_BUILDER_SIZE);
    tooltip.append("PIDC : ").append(getPidc().getName()).append("\nPIDC Description : ")
        .append(getPidc().getDescription()).append("\nVersion Name : ").append(getPidcVersionName());
    if (CommonUtils.isNotNull(getDescription())) {
      tooltip.append("\nVersion Description : ").append(getDescription());
    }
    return tooltip.toString();
  }

  /**
   * Returns pidc node name for specified level
   *
   * @param attrName level attr name
   * @return node name of pidc corresponding to level
   * @Deprecated use <code>PIDCard.getNodeName()</code> instead
   */
  @Deprecated
  public String getNodeName(final String attrName) {
    PIDCNode nodeObj = getPIDCNode();
    while (nodeObj != null) {
      if (nodeObj.getNodeAttr().getName().equalsIgnoreCase(attrName)) {
        return nodeObj.getNodeName();
      }
      nodeObj = nodeObj.getParent();
    }
    return "";
  }

  // ICDM-1484
  /**
   * @return the entire structure information of pidcversion
   */
  public String getPidcVersionPath() {
    int level;
    StringBuilder name = new StringBuilder(ApicConstants.PIDC_VERSION_STRING_SIZE);
    String levelName = "";
    int maxLevel = (int) this.dataProvider.getPidcStructMaxLevel();
    SortedSet<Attribute> attrs = this.dataProvider.getSortedAttributes(false);
    for (level = 1; level <= maxLevel; level++) {
      Iterator<Attribute> attrIterate = attrs.iterator();
      while (attrIterate.hasNext()) {
        Attribute attr = attrIterate.next();
        if (attr.getAttrLevel() == level) {
          levelName = attr.getName();
          break;
        }
      }
      name.append(getNodeName(levelName));
      name.append("->");
      levelName = "";
    }
    return name.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getDescription() {
    String returnValue;

    if (getDataCache().getLanguage() == Language.ENGLISH) {
      returnValue = getPidcVersionDescEng();
    }
    else {// GERMAN:
      returnValue = getPidcVersionDescGer();
      if (CommonUtils.isEmptyString(returnValue)) {
        returnValue = getPidcVersionDescEng();
      }
    }

    return returnValue;
  }

  /**
   * @return PIDC of this PIDC Version
   */
  public PIDCard getPidc() {
    return getDataCache().getPidc(getEntityProvider().getDbPIDCVersion(getID()).getTabvProjectidcard().getProjectId());
  }

  /**
   * @return Reference version of this PIDC Version, ie, the version from which this version was created
   */
  public PIDCVersion getParentVersion() {
    PIDCVersion parent = null;
    // For the first version the parent version would be null
    if (null != getEntityProvider().getDbPIDCVersion(getID()).getTPidcVers()) {
      parent =
          getDataCache().getPidcVersion(getEntityProvider().getDbPIDCVersion(getID()).getTPidcVers().getPidcVersId());
    }
    return parent;
  }

  /**
   * @return the proRevId
   */
  public Long getProRevId() {
    return getEntityProvider().getDbPIDCVersion(getID()).getProRevId();
  }

  /**
   * @return true if this is the active version of the pid card
   */
  public boolean isActiveVersion() {
    return CommonUtils.isEqual(getPidc().getActiveVersion(), this);
  }


  /**
   * Get the mapped Varaints. From the Sdom Pver name of the A2l file get the Pver name and if the Sdom pver is in
   * Variant level ,show only the mapped Variants.
   *
   * @param a2lFile a2lFile object
   * @return the mapped Varaints for the Sdom PVER of A2l.
   */
  public SortedSet<PIDCVariant> getMappedVariants(final A2LFile a2lFile) {
    PIDCAttribute pidcAttr = getAttributes(false).get(getDataCache().getPVerAttribute().getAttributeID());
    SortedSet<PIDCVariant> mappedVaraints = new TreeSet<>();
    SortedSet<PIDCVariant> variantsSet = getVariantsSet(false);
    if (pidcAttr.isVariant() && (a2lFile != null)) {
      for (PIDCVariant pidcVariant : variantsSet) {
        PIDCAttributeVar pVerAttr =
            pidcVariant.getAttributes(false, false).get(getDataCache().getPVerAttribute().getAttributeID());
        if ((pVerAttr != null) && (pVerAttr.getAttributeValue() != null) &&
            a2lFile.getSdomPverName().equals(pVerAttr.getAttributeValue().getValue())) {
          mappedVaraints.add(pidcVariant);
        }
      }
    }
    else {
      mappedVaraints.addAll(variantsSet);
    }

    return mappedVaraints;
  }


  /**
   * @return true, if A2L files are mapped to this PIDC version
   */
  public boolean hasA2lFiles() {
    return (getEntityProvider().getDbPIDCVersion(getID()).getTabvPidcA2ls() != null) &&
        !getEntityProvider().getDbPIDCVersion(getID()).getTabvPidcA2ls().isEmpty();
  }

  /**
   * Checks whether focus matrix definition is available in the pidc version. The following conditions should e
   * satisfied if focus matrix deleted flag is not 'Y' if attribute associated with focus matrix, deleted flag is not
   * 'Y' if use case section and its parents use case associated with focus matrix deleted flag is not 'Y'
   *
   * @return true, if Focus Matrix are mapped to this PIDC version
   */
  // ICDM-2254
  // ICDM-2485
  public boolean hasFocusMatrix() {

    // ICDM-2644
    Set<TFocusMatrix> dbFmSet =
        getEntityProvider().getDbFocuMatrixVersion(getFocusMatrixWorkingSetVersion().getID()).getTFocusMatrixs();
    if ((null == dbFmSet) || dbFmSet.isEmpty()) {
      // No focus matrix definitions
      return false;
    }

    PIDCAttribute pidcAttr;
    AbstractUseCaseItem useCaseItem;

    for (TFocusMatrix dbFm : dbFmSet) {
      if (ApicConstants.YES.equals(dbFm.getDeletedFlag())) {
        // FM marked as deleted
        continue;
      }

      pidcAttr = getAttributes(false).get(dbFm.getTabvAttribute().getAttrId());
      if (isFMApplicableForPIDCAttr(pidcAttr)) {
        // Condition pidcAttr is null includes deleted attributes, attributes missing due to attr dependency
        continue;
      }

      useCaseItem = getDataCache().getFocusMatrix(dbFm.getFmId()).getUseCaseItem();
      if (isUCItemOrParentDeleted(useCaseItem)) {
        // Use case item/parent item is deleted
        continue;
      }
      return true;

    }

    return false;
  }

  /**
   * @param pidcAttr
   * @return
   */
  private boolean isFMApplicableForPIDCAttr(final PIDCAttribute pidcAttr) {
    return (pidcAttr == null) || !pidcAttr.isFocusMatrixApplicable();
  }

  /**
   * @param useCaseItem
   * @return
   */
  private boolean isUCItemOrParentDeleted(final AbstractUseCaseItem useCaseItem) {
    return useCaseItem.isDeleted() || useCaseItem.isParentLevelDeleted();
  }

  /**
   * Method inside PIDC version
   *
   * @return true if attr can be transfered
   */
  public boolean canTranfertoVcdm() {
    if (getPidc().getAliasDefinition() != null) {
      this.dupAlaiasNames.clear();
      return checkVaraints();
    }
    return true;
  }

  /**
   * @return true if vcdm aprj attr has value
   */
  public boolean isVcdmAprjValSet(){
    boolean isValueSet=false;
    for(PIDCAttribute pidcAttr : getAttributes().values()){
      if ((pidcAttr.getAttribute().getAttrLevel() == ApicConstants.VCDM_APRJ_NAME_ATTR) &&
          (pidcAttr.getAttributeValue() != null)) {
        isValueSet=true;
      }
    }
    return isValueSet;
  }
  /**
   * @return
   */
  private boolean checkVaraints() {
    // get all varaint map
    Map<Long, PIDCVariant> variantsMap = getVariantsMap();
    for (PIDCVariant variant : variantsMap.values()) {
      Set<String> attrNameSet = new HashSet<>();
      Map<Long, PIDCAttributeVar> allAttr = variant.getAttributesAll();
      for (PIDCAttributeVar pidcAttrVar : allAttr.values()) {
        if (!attrNameSet.add(pidcAttrVar.getEffectiveAttrAlias())) {
          this.dupAlaiasNames.add(pidcAttrVar.getEffectiveAttrAlias());
          return false;
        }
      }
    }
    return true;
  }

  /**
   * @param nameEng English name of variant
   * @param nameGer name in German
   * @return true if the name already exists [Task : 278578]
   */
  public boolean checkIfVarNameExists(final String nameEng, final String nameGer) {
    boolean alreadyExists = false;
    // get all variants
    Map<Long, PIDCVariant> variantsMap = getVariantsMap();
    for (PIDCVariant variant : variantsMap.values()) {
      if (((ApicDataProvider) getDataProvider()).getLanguage() == Language.ENGLISH) {
        if (variant.getName().equalsIgnoreCase(nameEng)) {
          alreadyExists = true;
          break;

        }
      }
      else {
        if (CommonUtils.isNotEmptyString(nameGer) && variant.getName().equalsIgnoreCase(nameGer)) {
          alreadyExists = true;
          break;
        }
        if (variant.getName().equalsIgnoreCase(nameEng)) {
          alreadyExists = true;
          break;
        }
      }

    }
    return alreadyExists;
  }

  /**
   * @return the dupAlaiasNames
   */
  public List<String> getDupAlaiasNames() {
    return this.dupAlaiasNames;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PIDCVersionStatistics getProjectStatistics() {
    this.pidcVersionStatistics = new PIDCVersionStatistics(this);
    return this.pidcVersionStatistics;
  }


  /**
   * @return the focusMatrixDefinitionLoaded status
   */
  // ICDM-2569
  public boolean isFocusMatrixVersionsLoaded() {
    return this.focusMatrixVersLoaded;
  }

  /**
   * Resets focus matrix definitions loaded in this class
   */
  // ICDM-2569
  public void resetFocusMatrixVersionsLoaded() {
    this.focusMatrixVersLoaded = false;
  }


  /**
   * @param user user
   * @return true if the pidc is hidden
   */
  public boolean isHidden(final ApicUser user) {

    // If the attribute value for a PIDC-Version is quotation (hidden), the PIDC is shown only for users with any kind
    // of access rights on the PIDC (Read, Write, Owner, Grant) and for users with APIC_WRITE access
    if (getDataCache().getPidcVrsnExclusnHandler().isExcluded(getID())) {
      // If user is null return hidden
      if (user == null) {
        return true;
      }
      // if the user has apic write access then the pidc version is not hidden
      if (user.hasApicWriteAccess()) {
        return false;
      }
      // get all node access
      SortedSet<NodeAccessRight> allAccessRights = getPidc().getAccessRights();
      NodeAccessRight currentUserAccessRights = null;
      currentUserAccessRights = getCurrUserAccRights(user, allAccessRights, currentUserAccessRights);
      // If the curr is null or does not have read access
      if ((currentUserAccessRights == null) || !currentUserAccessRights.hasReadAccess()) {
        return true;
      }

    }
    return false;

  }

  /**
   * @param user
   * @param allAccessRights
   * @param currentUserAccessRights
   * @return
   */
  private NodeAccessRight getCurrUserAccRights(final ApicUser user, final SortedSet<NodeAccessRight> allAccessRights,
      NodeAccessRight currentUserAccessRights) {
    for (NodeAccessRight nodeAccessRight : allAccessRights) {
      // Check for equals in the access rights
      if (CommonUtils.isEqual(nodeAccessRight.getApicUser(), user)) {
        currentUserAccessRights = nodeAccessRight;
        break;
      }
    }
    return currentUserAccessRights;
  }

  /**
   * @return if the pidcversion is to be hidden or not
   */
  public boolean isHidden() {
    // If the attribute value for a PIDC-Version is quotation (hidden), the PIDC is shown only for users with any kind
    // of access rights on the PIDC (Read, Write, Owner, Grant) and for users with APIC_WRITE access
    // checks if this pidcversion is in the exluded list
    if (getDataCache().getPidcVrsnExclusnHandler().isExcluded(getID())) {
      // if the user has apic write access then the pidc version is not hidden
      if (getDataCache().getCurrentUser().hasApicWriteAccess()) {
        return false;
      }
      // checks the node access rights. If the user has the access rights on the PIDC (Read, Write, Owner, Grant) then
      // the pidcversion is not hidden
      NodeAccessRight curUserAccRight = getPidc().getCurrentUserAccessRights();
      if ((curUserAccRight == null) || !curUserAccRight.hasReadAccess()) {
        return true;
      }
    }
    return false;

  }

  /**
   * @return true if the user can modify focus matrix entries
   */
  public boolean canModifyFocusMatrix() {
    boolean isModifiable = false;
    NodeAccessRight curUserAccRight = getPidcVersion().getPidc().getCurrentUserAccessRights();
    // the attribute can be modified if the user can modify the PIDC
    if ((curUserAccRight != null) && curUserAccRight.hasWriteAccess()) {
      // structure attributes can not be modified
      isModifiable = !(getPidcVersion().isDeleted() || (getPidcVersion().getStatus() == PidcVersionStatus.LOCKED));
    }
    return isModifiable;
  }

  /**
   * @return sorted collection of focus matrix versions
   */
  // ICDM-2569
  public SortedSet<FocusMatrixVersion> getFocusMatrixVersionsSorted() {
    return new TreeSet<FocusMatrixVersion>(getFocusMatrixVersionMap().values());
  }

  /**
   * Returns map of Focus matrix versions.
   * <p>
   * Key - version number. Working set version is 0<br>
   * Value - version BO
   *
   * @return version map
   */
  // ICDM-2569
  Map<Long, FocusMatrixVersion> getFocusMatrixVersionMap() {

    if (!this.focusMatrixVersLoaded) {

      getLogger().debug("Loading focus matrix versions for PIDC version - {}", getID());

      this.fmVersMap.clear();

      for (TFocusMatrixVersion dbFmVer : getEntityProvider().getDbPIDCVersion(getID()).getTFocusMatrixVersions()) {
        Long fmVersID = dbFmVer.getFmVersId();
        FocusMatrixVersion fmVers = getDataCache().getFocusMatrixVersion(fmVersID);
        if (fmVers == null) {
          fmVers = new FocusMatrixVersion(this.dataProvider, fmVersID);
        }
        this.fmVersMap.put(fmVers.getRevisionNumber(), fmVers);
      }

      this.focusMatrixVersLoaded = true;

      getLogger().debug(
          "Focus matrix versions loading completed for PIDC version - {}. Number of versions - {}; Working set version - {}",
          getID(), this.fmVersMap.size(), getFocusMatrixWorkingSetVersion().getID());
    }

    return this.fmVersMap;

  }

  /**
  *
  */
  public void fillPredefinedAttributes() {
    this.predefAttrGrpAttrMap.clear();
    Set<IPIDCAttribute> grpdAttrSet = getPidcVersion().getApplicableGroupedAttributes();
    for (IPIDCAttribute grpdAttr : grpdAttrSet) {
      getPredefinedAttr(grpdAttr);
    }
  }


  /**
   * @param grpAttr
   */
  private void checkGrpAttrLvl(final PIDCAttribute grpAttr) {
    // Check if the grouped attribute is in variant level
    if (grpAttr.isVariant()) {
      checkForVariantLvl(grpAttr);
    }
    else {
      this.groupedAttrs.add(grpAttr);
    }
  }

  /**
   * @param grpAttr
   */
  private void checkForVariantLvl(final PIDCAttribute grpAttr) {

    for (PIDCVariant pidcVar : grpAttr.getPidcVersion().getVariantsMap().values()) {
      PIDCAttributeVar grpAttrVar = pidcVar.getVarAttribute(grpAttr.getAttribute().getID());
      if (null != grpAttrVar) {
        // Check if the grouped attribute is in sub variant level
        checkForSubVarLvl(grpAttr, pidcVar, grpAttrVar);
      }
    }
  }

  /**
   * @param grpAttr
   * @param pidcVar
   * @param grpAttrVar
   */
  private void checkForSubVarLvl(final PIDCAttribute grpAttr, final PIDCVariant pidcVar,
      final PIDCAttributeVar grpAttrVar) {
    // Check if the grouped attribute is in sub variant level
    if (grpAttrVar.isVariant()) {
      for (PIDCSubVariant pidcSubVar : pidcVar.getSubVariantsMap().values()) {
        PIDCAttributeSubVar grpAttrSubVar = pidcSubVar.getSubVarAttribute(grpAttr.getAttribute().getID());
        this.groupedAttrs.add(grpAttrSubVar);
      }
    }
    else {
      this.groupedAttrs.add(grpAttrVar);
    }
  }

  /**
   * @param grpAttr
   */
  private void getPredefinedAttr(final IPIDCAttribute grpAttr) {

    if (grpAttr instanceof PIDCAttribute) {
      PIDCAttribute grpdAttr = (PIDCAttribute) grpAttr;

      if ((null != grpdAttr.getAttributeValue()) && checkGroupedAttrValueValidity(grpdAttr.getAttributeValue()) &&
          (null != grpdAttr.getAttributeValue().getPreDefinedAttrValueSet())) {

        getPredefAttrLevels(grpdAttr);
      }
    }
    else if (grpAttr instanceof PIDCAttributeVar) {
      PIDCAttributeVar grpdAttr = (PIDCAttributeVar) grpAttr;

      if ((null != grpdAttr.getAttributeValue()) && checkGroupedAttrValueValidity(grpdAttr.getAttributeValue()) &&
          (null != grpdAttr.getAttributeValue().getPreDefinedAttrValueSet())) {

        for (PredefinedAttrValue predefAttrVal : grpdAttr.getAttributeValue().getPreDefinedAttrValueSet()) {

          PIDCAttribute predefAttr = this.allAttrMap.get(predefAttrVal.getPredefinedAttribute().getAttributeID());
          if (null != predefAttr) {
            // Check if predef attr is in variant
            if (predefAttr.isVariant()) {
              if (null != grpdAttr.getPidcVariant().getAttributesAll().get(predefAttr.getAttribute().getID())) {
                PIDCAttributeVar predefAttrVar =
                    grpdAttr.getPidcVariant().getAttributesAll().get(predefAttr.getAttribute().getID());
                if (null != predefAttrVar) {
                  // Check if predef attr is in sub variant
                  if (predefAttrVar.isVariant()) {
                    for (PIDCSubVariant pidcSubVar : grpdAttr.getPidcVariant().getSubVariantsMap().values()) {
                      if (null != pidcSubVar.getAttributesAll().get(predefAttr.getAttribute().getID())) {
                        PIDCAttributeSubVar predefAttrSubVar =
                            pidcSubVar.getAttributesAll().get(predefAttr.getAttribute().getID());

                        this.predefAttrGrpAttrMap.put(predefAttrSubVar, grpdAttr);
                      }
                    }
                  }
                  else {
                    this.predefAttrGrpAttrMap.put(predefAttrVar, grpdAttr);
                  }
                }
              }
            }
            else {
              this.predefAttrGrpAttrMap.put(predefAttr, grpdAttr);
            }
          }
        }
      }
    }
    else if (grpAttr instanceof PIDCAttributeSubVar) {
      PIDCAttributeSubVar grpdAttr = (PIDCAttributeSubVar) grpAttr;
      if ((null != grpdAttr.getAttributeValue()) && checkGroupedAttrValueValidity(grpdAttr.getAttributeValue()) &&
          (null != grpdAttr.getAttributeValue().getPreDefinedAttrValueSet())) {

        for (PredefinedAttrValue predefAttrVal : grpdAttr.getAttributeValue().getPreDefinedAttrValueSet()) {

          PIDCAttribute predefAttr = this.allAttrMap.get(predefAttrVal.getPredefinedAttribute().getAttributeID());
          if (null != predefAttr) {
            // Check if predef attr is in variant
            if (predefAttr.isVariant()) {
              if (null != grpdAttr.getPidcSubVariant().getPidcVariant().getAttributesAll()
                  .get(predefAttr.getAttribute().getID())) {
                PIDCAttributeVar predefAttrVar = grpdAttr.getPidcSubVariant().getPidcVariant().getAttributesAll()
                    .get(predefAttr.getAttribute().getID());
                if (null != predefAttrVar) {
                  // Check if predef attr is in sub variant
                  if (predefAttrVar.isVariant()) {
                    if (null != grpdAttr.getPidcSubVariant().getAttributesAll()
                        .get(predefAttr.getAttribute().getID())) {
                      PIDCAttributeSubVar predefAttrSubVar =
                          grpdAttr.getPidcSubVariant().getAttributesAll().get(predefAttr.getAttribute().getID());

                      this.predefAttrGrpAttrMap.put(predefAttrSubVar, grpdAttr);
                    }
                  }
                  else {

                    this.predefAttrGrpAttrMap.put(predefAttrVar, grpdAttr);
                  }
                }
              }
            }
            else {

              this.predefAttrGrpAttrMap.put(predefAttr, grpdAttr);
            }
          }
        }
      }
    }
  }

  /**
   * @param grpdAttr
   */
  private void getPredefAttrLevels(final PIDCAttribute grpdAttr) {
    for (PredefinedAttrValue predefAttrVal : grpdAttr.getAttributeValue().getPreDefinedAttrValueSet()) {

      PIDCAttribute predefAttr = this.allAttrMap.get(predefAttrVal.getPredefinedAttribute().getAttributeID());
      if (null != predefAttr) {
        // Check if predef attr is in variant
        if (predefAttr.isVariant()) {
          for (PIDCVariant pidcVar : predefAttr.getPidcVersion().getVariantsMap().values()) {
            if (null != pidcVar.getAttributesAll().get(predefAttr.getAttribute().getID())) {
              PIDCAttributeVar predefAttrVar = pidcVar.getAttributesAll().get(predefAttr.getAttribute().getID());

              if (null != predefAttrVar) {
                // Check if predef attr is in sub variant
                if (predefAttrVar.isVariant()) {
                  for (PIDCSubVariant pidcSubVar : pidcVar.getSubVariantsMap().values()) {
                    if (null != pidcSubVar.getAttributesAll().get(predefAttr.getAttribute().getID())) {
                      PIDCAttributeSubVar predefAttrSubVar =
                          pidcSubVar.getAttributesAll().get(predefAttr.getAttribute().getID());
                      this.predefAttrGrpAttrMap.put(predefAttrSubVar, grpdAttr);
                    }
                  }
                }
                else {
                  this.predefAttrGrpAttrMap.put(predefAttrVar, grpdAttr);
                }
              }
            }
          }
        }
        else {
          this.predefAttrGrpAttrMap.put(predefAttr, grpdAttr);
        }
      }
    }
  }

  /**
   * @return working set version of the focus matrix
   */
  // ICDM-2569
  public FocusMatrixVersion getFocusMatrixWorkingSetVersion() {
    return getFocusMatrixVersionMap().get(FM_WORKING_SET_VERSION);
  }

  /**
   * @param attrValue value for which validity in pidc needs to be checked
   * @return boolean flag :checks the validity of grouped attribute in a pidcversion
   */
  public boolean checkGroupedAttrValueValidity(final AttributeValue attrValue) {

    for (Attribute attr : ((ApicDataProvider) getDataProvider()).getPidcStructAttrMap().values()) {
      PIDCAttribute levelAttr = this.attrMap.get(attr.getID());
      if ((null == attrValue.getValidity()) || ((null != attrValue.getValidity()) && (null != levelAttr) &&
          attrValue.getValidity().getValidityAttribute().getID().equals(attr.getID()) &&
          attrValue.getValidity().getValidityAttributeValues().containsValue(levelAttr.getAttributeValue()))) {
        return true;
      }
    }
    return false;
  }


  /**
   * @return set of grouped attrs in the pidcversion
   */
  public Set<IPIDCAttribute> fillApplicableGroupedAttributes() {
    this.groupedAttrs.clear();
    for (PIDCAttribute pidcAttr : this.attrMap.values()) {
      if (pidcAttr.getAttribute().isGrouped()) {
        checkGrpAttrLvl(pidcAttr);
      }
    }
    return this.groupedAttrs;
  }

  /**
   * @return set of grouped attrs
   */
  public Set<IPIDCAttribute> getApplicableGroupedAttributes() {
    return this.groupedAttrs;
  }

  /**
   * @return attr value set for division attribute
   */
  public AttributeValue getDivisionAttrValue() {
    Long attrId =
        Long.valueOf(((ApicDataProvider) getDataProvider()).getParameterValue(ApicConstants.PIDC_DIVISION_ATTR));
    return getAttributesAll().get(attrId).getAttributeValue();

  }

  /**
   * @return Calendar
   */
  public Calendar getLastConfirmationDate() {
    return ApicUtil.timestamp2calendar(getEntityProvider().getDbPIDCVersion(getID()).getLastConfirmationDate());
  }

  /**
   * Task 242053
   *
   * @return true if up to date
   */
  public boolean isUpToDate() {
    if (getStatus() == PidcVersionStatus.IN_WORK) {
      // check conditions only in case of pidc version

      Calendar lastConfirmationDate = getLastConfirmationDate();
      if (null == lastConfirmationDate) {
        return false;
      }
      long diffInDays = TimeUnit.MILLISECONDS
          .toDays(Math.abs(Calendar.getInstance().getTimeInMillis() - lastConfirmationDate.getTimeInMillis()));
      Long intervalDays = Long
          .valueOf(((ApicDataProvider) getDataProvider()).getParameterValue(ApicConstants.PIDC_UP_TO_DATE_INTERVAL));

      return diffInDays < intervalDays;
    }
    return true;
  }


  /**
   * @return the pidc attr
   */
  public PIDCAttribute getSSDProjNodeAttr() {
    long wpAttrId =
        Long.valueOf(((ApicDataProvider) getDataProvider()).getParameterValue(ApicConstants.SSD_PROJ_NODE_ATTR_ID));
    PIDCAttribute wpTypeAttr = getAttributes().get(wpAttrId);
    return wpTypeAttr;
  }


  /**
   * @return is owner boolean
   */
  public boolean isOwner() {
    NodeAccessRight curUserAccRight = getPidc().getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.isOwner()) {
      return !getPidc().isDeleted();
    }
    return false;
  }

  /**
   * @return is readable boolean
   */
  public boolean isReadable() {
    NodeAccessRight curUserAccRight = getPidc().getCurrentUserAccessRights();
    if ((curUserAccRight != null) && curUserAccRight.hasReadAccess()) {
      return !getPidc().isDeleted();
    }
    return false;
  }
}