/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.views.providers;

import org.eclipse.ui.views.properties.IPropertyDescriptor;

import com.bosch.caltool.icdm.client.bo.a2l.A2LFileBO;
import com.bosch.caltool.icdm.client.bo.a2l.PidcA2LBO;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.rcputils.propertysource.AbstractPropertySource;

/**
 * This class is used to display details of A2L File mapped with PVersion in Properties View in PIDC Editor
 *
 * @author svj7cob
 */
// ICDM-1544
public class A2LPropertiesViewSource extends AbstractPropertySource {

  /**
   * A2L File Name
   */
  private static final String A2L_FILE_NAME = "A2L File";

  /**
   * PIDC Version
   */
  private static final String PIDC_VERSION = "PIDC Version";
  /**
   * Modified User
   */
  private static final String CREATED_USER = "Created User";

  /**
   * Modified Date
   */
  private static final String CREATED_DATE = "Created Date";
  /**
   * Modified User
   */
  private static final String MODIFIED_USER = "Modified User";

  /**
   * Modified Date
   */
  private static final String MODIFIED_DATE = "Modified Date";
  /**
   * Modified User
   */
  private static final String ASSIGNED_USER = "Assigned User";

  /**
   * Modified Date
   */
  private static final String ASSIGNED_DATE = "Assigned Date";

  /**
   * PVER Name
   */
  private static final String PVER_NAME = "PVER Name";

  /**
   * PVER Variant
   */
  private static final String PVER_VARIANT = "PVER Variant";

  /**
   * A2l Created Date from vCDM
   */
  private static final String A2L_CREATED_DATE = "A2L Created Date";

  /**
   * A2l File name from vCDM
   */
  private static final String INTERNAL_FILE_NAME = "Internal File Name";

  /**
   * A2l File date from TA2l_FileInfo
   */
  // ICDM-1651
  private static final String INTERNAL_FILE_DATE = "Internal File Date";

  /**
   * Par2Wp flag details
   */
  private static final String PAR2WP = "Par2WP";

  /**
   * Active Par2WP flag details
   */
  private static final String ACTIVE_PAR2WP = "Par2WP Active Version";

  /**
   * SSD Software version
   */
  private static final String SSD_SOFTWARE_VERSION = "SSD Software version";


  /**
   * Active flag
   */
  private static final String ACTIVE = "Active";

  /**
   * instance of pidc a2l file ext
   */
  private final PidcA2lFileExt pidcA2lFileExt;


  /**
   * Fields to be displayed in Properties View
   */
  private static final String[] PROP_DESC_FIELDS = new String[] {
      A2L_FILE_NAME,
      PIDC_VERSION,
      ACTIVE,
      PAR2WP,
      ACTIVE_PAR2WP,
      PVER_NAME,
      PVER_VARIANT,
      SSD_SOFTWARE_VERSION,
      A2L_CREATED_DATE,
      INTERNAL_FILE_NAME,
      INTERNAL_FILE_DATE,
      CREATED_DATE,
      CREATED_USER,
      MODIFIED_DATE,
      MODIFIED_USER,
      ASSIGNED_DATE,
      ASSIGNED_USER };

  /**
   * Constructor
   *
   * @param pidcA2lFileExt - instance of PidcA2lFileExt for which properties have to be displayed
   */
  public A2LPropertiesViewSource(final PidcA2lFileExt pidcA2lFileExt) {
    super();
    this.pidcA2lFileExt = pidcA2lFileExt;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public IPropertyDescriptor[] getPropertyDescriptors() {
    return CommonUiUtils.createPropertyDescFields(A2LPropertiesViewSource.PROP_DESC_FIELDS);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getPropertyValue(final Object objID) {
    if (PROP_TITLE.equals(objID)) {
      return getTitle();
    }
    return getStrPropertyValue((String) objID);
  }

  /**
   * get the field relevant to the details of A2L File
   *
   * @param propKey String
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String result = null;
    PidcA2LBO pidcA2lBo = null;
    A2LFileBO a2lFileBO;
    if (this.pidcA2lFileExt.getPidcA2l() != null) {
      pidcA2lBo = new PidcA2LBO(this.pidcA2lFileExt.getPidcA2l().getId(), this.pidcA2lFileExt);
    }
    if (pidcA2lBo == null) {
      a2lFileBO = new A2LFileBO(this.pidcA2lFileExt.getA2lFile());
    }
    else {
      a2lFileBO = pidcA2lBo.getA2lFileBO();
    }
    switch (propKey) {
      case A2L_FILE_NAME:
        result = CommonUtils.checkNull(this.pidcA2lFileExt.getA2lFile().getFilename());
        break;
      case PIDC_VERSION:
        result = getPidcVersion(pidcA2lBo);
        break;
      case ACTIVE:
        result = (pidcA2lBo == null) ? "" : CommonUtils.getDisplayText(pidcA2lBo.getPidcA2l().isActive());
        break;

      case PAR2WP:
        result = (pidcA2lBo == null) ? "" : CommonUtils.getDisplayText(pidcA2lBo.getPidcA2l().isWpParamPresentFlag());
        break;

      case ACTIVE_PAR2WP:
        result =
            (pidcA2lBo == null) ? "" : CommonUtils.getDisplayText(pidcA2lBo.getPidcA2l().isActiveWpParamPresentFlag());
        break;
      case PVER_NAME:
        result = this.pidcA2lFileExt.getA2lFile().getSdomPverName();
        break;
      case PVER_VARIANT:
        result = CommonUtils.checkNull(this.pidcA2lFileExt.getA2lFile().getSdomPverVariant());
        break;
      case A2L_CREATED_DATE:
        result = CommonUtils.checkNull(this.pidcA2lFileExt.getA2lFile().getFiledate());
        break;
      case INTERNAL_FILE_NAME:
        result = CommonUtils.checkNull(a2lFileBO.getInternalFileName());
        break;
      case INTERNAL_FILE_DATE:
        result = CommonUtils.checkNull(a2lFileBO.getInternalFileDate());
        break;
      case SSD_SOFTWARE_VERSION:
        result = (pidcA2lBo == null) ? "" : CommonUtils.checkNull(pidcA2lBo.getPidcA2l().getSsdSoftwareVersion());
        break;
      case CREATED_DATE:
        result = (pidcA2lBo == null) ? "" : CommonUtils.checkNull(pidcA2lBo.getCreatedDate());
        break;
      case CREATED_USER:
        result = (pidcA2lBo == null) ? "" : CommonUtils.checkNull(pidcA2lBo.getCreatedUser());
        break;
      case MODIFIED_DATE:
        result = (pidcA2lBo == null) ? "" : CommonUtils.checkNull(pidcA2lBo.getModifiedDate());
        break;
      case MODIFIED_USER:
        result = (pidcA2lBo == null) ? "" : CommonUtils.checkNull(pidcA2lBo.getModifiedUser());
        break;
      case ASSIGNED_DATE:
        result = getAssignedDate(pidcA2lBo);
        break;
      case ASSIGNED_USER:
        result = getAssignedUser(pidcA2lBo);
        break;
      default:
        result = "";
    }
    return result;
  }


  /**
   * Get the pidc version
   *
   * @param pidcA2l PIDCA2lBO
   * @return String
   */
  private String getPidcVersion(final PidcA2LBO pidcA2lBo) {
    if (pidcA2lBo == null) {
      return "";
    }
    return CommonUtils
        .checkNull(String.valueOf((pidcA2lBo.getPidcVersion() == null ? "" : pidcA2lBo.getPidcVersion().getName())));
  }


  /**
   * Get the assigned user
   *
   * @param pidcA2l - pidc a2l object
   * @return assigned user name
   */
  private String getAssignedUser(final PidcA2LBO pidcA2lBo) {

    return CommonUtils.checkNull(pidcA2lBo == null ? "" : pidcA2lBo.getAssignedUser());

  }


  /**
   * Get the assigned date
   *
   * @param pidcA2l PIDCA2l
   * @return assigned date
   */
  private String getAssignedDate(final PidcA2LBO pidcA2lBo) {
    return CommonUtils.checkNull(pidcA2lBo == null ? "" : pidcA2lBo.getAssignedDate());
  }

  /**
   * {@inheritDoc} get the Title
   */
  @Override
  protected String getTitle() {
    return "PIDCA2L - " + this.pidcA2lFileExt.getA2lFile().getFilename();
  }


}
