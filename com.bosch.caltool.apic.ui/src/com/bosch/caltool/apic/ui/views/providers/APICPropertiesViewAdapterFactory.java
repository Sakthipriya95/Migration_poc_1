/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.views.providers;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.views.properties.IPropertySource;

import com.bosch.caltool.apic.ui.editors.PIDCEditor;
import com.bosch.caltool.apic.ui.editors.compare.PidcNattableRowObject;
import com.bosch.caltool.apic.ui.editors.pages.AttributesPage;
import com.bosch.caltool.apic.ui.editors.pages.NodeAccessRightsPage;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.client.bo.apic.AttributesDataHandler;
import com.bosch.caltool.icdm.client.bo.apic.PIDCDetailsNode;
import com.bosch.caltool.icdm.client.bo.apic.PidcSubVariantBO;
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.client.bo.general.NodeAccessPageDataHandler;
import com.bosch.caltool.icdm.client.bo.uc.UseCaseEditorRowAttr;
import com.bosch.caltool.icdm.common.ui.editors.AbstractFormPage;
import com.bosch.caltool.icdm.common.ui.utils.CommonUiUtils;
import com.bosch.caltool.icdm.common.ui.views.providers.A2LPropertiesViewSource;
import com.bosch.caltool.icdm.common.ui.views.providers.AccessRightsPagePropertiesViewSource;
import com.bosch.caltool.icdm.common.ui.views.providers.ProjectObjectPropertiesViewSource;
import com.bosch.caltool.icdm.model.apic.attr.AttrGroup;
import com.bosch.caltool.icdm.model.apic.attr.AttrNValueDependency;
import com.bosch.caltool.icdm.model.apic.attr.AttrSuperGroup;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.IProjectObject;
import com.bosch.caltool.icdm.model.apic.pidc.PidcA2lFileExt;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.user.NodeAccess;

/**
 * Properties view adapter factory for APIC objects
 *
 * @author rvu1cob
 */
// ICDM-82
public class APICPropertiesViewAdapterFactory implements IAdapterFactory {

  /**
   * {@inheritDoc}
   */
  @Override
  public Object getAdapter(final Object adaptableObject, final Class adapterType) {

    Object returnObject = null;
    // ICDM-108
    if (adapterType == IPropertySource.class) {
      // Object type - Node access right
      returnObject = checkNodeAccess(adaptableObject);
      if (returnObject == null) {
        // object types - attributes, values, attribute/value dependencies
        returnObject = checkAttrValueDependency(adaptableObject);
      }
      if (returnObject == null) {
        // object types - pidc, version, variant, subvariant, project attribute at all levels
        returnObject = checkProjectObject(adaptableObject);
      }
      if (returnObject == null) {
        // object types - attribute super groups, attribute groups
        returnObject = checkAttrGroups(adaptableObject);
      }
      if (returnObject == null) {
        // object type - a2l file
        returnObject = checkA2LFile(adaptableObject);
      }
    }
    return returnObject;
  }

  /**
   * NodeAccessRights
   *
   * @param adaptableObject object selected
   */
  private Object checkNodeAccess(final Object adaptableObject) {
    Object returnObject = null;
    if (adaptableObject instanceof NodeAccess) {
      NodeAccessPageDataHandler dataHandler = getNodeAccessPageDataHandler();
      if (dataHandler != null) {
        returnObject =
            new AccessRightsPagePropertiesViewSource((NodeAccess) adaptableObject, getNodeAccessPageDataHandler());
      }
    }
    return returnObject;
  }

  /**
   * A2L File
   *
   * @param adaptableObject
   */
  private Object checkA2LFile(final Object adaptableObject) {
    if (adaptableObject instanceof PidcA2lFileExt) {
      return new A2LPropertiesViewSource((PidcA2lFileExt) adaptableObject);
    }
    return null;
  }

  /**
   * Project Objects - PIDC, Version, Variant, SubVariant, Project Attributes
   *
   * @param adaptableObject object selected
   */
  private Object checkProjectObject(final Object adaptableObject) {

    // Project, Version, Variant, Sub-variant
    IProjectObject projObj = null;
    if (adaptableObject instanceof IProjectObject) {
      projObj = (IProjectObject) adaptableObject;
    }
    // Special case : variant type pidc details virtual node in PIDC Details View
    else if ((adaptableObject instanceof PIDCDetailsNode) && ((PIDCDetailsNode) adaptableObject).isVariantNode()) {
      projObj = ((PIDCDetailsNode) adaptableObject).getPidcVariant();
    }
    // ICDM-137
    Object returnObject = null;


    if (projObj != null) {
      returnObject = new ProjectObjectPropertiesViewSource(getProjectObjectBO(projObj), projObj);
    }
    // Project attribute
    if (adaptableObject instanceof PidcNattableRowObject) {
      returnObject = new PIDCPagePropertiesViewSource((PidcNattableRowObject) adaptableObject);

    }
    return returnObject;
  }

  /**
   * Attribute Super Groups, Attribute Groups
   *
   * @param adaptableObject object selected
   */
  private Object checkAttrGroups(final Object adaptableObject) {
    Object returnObject = null;
    if (adaptableObject instanceof AttrSuperGroup) {
      returnObject = new AttrSuperGrpPropViewSource((AttrSuperGroup) adaptableObject);
    }
    else if (adaptableObject instanceof AttrGroup) {
      returnObject = new AttrGroupPropViewSource((AttrGroup) adaptableObject);
    }
    return returnObject;
  }

  /**
   * Attribute, Attribute Value, Attribute Dependency, Value Dependency
   *
   * @param adaptableObject object selected
   */
  private Object checkAttrValueDependency(final Object adaptableObject) {
    Object returnObject = null;
    // ICDM-83
    if (adaptableObject instanceof Attribute) {
      returnObject = new AttributesTablePropertiesViewSource((Attribute) adaptableObject);
    }
    // ICDM-952
    // Special case : Get the attribute from Use case nat input(use case editor)
    else if (adaptableObject instanceof UseCaseEditorRowAttr) {
      UseCaseEditorRowAttr useCaseNatInput = (UseCaseEditorRowAttr) adaptableObject;
      returnObject = new AttributesTablePropertiesViewSource(useCaseNatInput.getAttributeBO().getAttribute());
    }
    // ICDM-136
    else if (adaptableObject instanceof AttributeValue) {
      returnObject = new AttributesValueTablePropertiesViewSource((AttributeValue) adaptableObject);
    }
    else if (adaptableObject instanceof AttrNValueDependency) {
      returnObject = new AttrDependencyTablePropertiesViewSource((AttrNValueDependency) adaptableObject,
          getAttributesDataHandler());
    }
    return returnObject;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<?>[] getAdapterList() {
    return new Class[] { IPropertySource.class };
  }


  /**
   * @param projectObject
   * @return project BO
   */
  private AbstractProjectObjectBO getProjectObjectBO(final IProjectObject projectObject) {

    AbstractProjectObjectBO projBo = null;


    if (CommonUiUtils.getInstance().getActiveEditor() instanceof PIDCEditor) {

      PIDCEditor editor = (PIDCEditor) CommonUiUtils.getInstance().getActiveEditor();
      PidcVersion pidcVersion = editor.getPidcPage().getPidcVersionBO().getPidcVersion();

      if (projectObject instanceof PidcVersion) {
        projBo = editor.getPidcPage().getPidcVersionBO();
      }
      else if (projectObject instanceof PidcVariant) {
        projBo = new PidcVariantBO(pidcVersion, (PidcVariant) projectObject,
            editor.getEditorInput().getPidcVersionBO().getPidcDataHandler());
      }
      else if (projectObject instanceof PidcSubVariant) {
        projBo = new PidcSubVariantBO(pidcVersion, (PidcSubVariant) projectObject,
            editor.getEditorInput().getPidcVersionBO().getPidcDataHandler());
      }
    }

    return projBo;
  }

  /**
   * @return instance of NodeAccessPageDataHandler
   */
  private NodeAccessPageDataHandler getNodeAccessPageDataHandler() {
    AbstractFormPage activePage = CommonUiUtils.getInstance().getActivePageInActiveEditor();
    if (activePage instanceof NodeAccessRightsPage) {
      return (NodeAccessPageDataHandler) activePage.getDataHandler();
    }
    return null;
  }

  /**
   * @return instance of AttributesDataHandler
   */
  private AttributesDataHandler getAttributesDataHandler() {
    AbstractFormPage activePage = CommonUiUtils.getInstance().getActivePageInActiveEditor();
    if (activePage instanceof AttributesPage) {
      return ((AttributesPage) activePage).getAttrHandler();
    }
    return null;
  }


}
