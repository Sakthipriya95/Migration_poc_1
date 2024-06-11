package com.bosch.caltool.apic.ui.views.providers;


import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import com.bosch.caltool.apic.ui.editors.AttributesEditor;
import com.bosch.caltool.apic.ui.editors.AttributesEditorInput;
import com.bosch.caltool.icdm.client.bo.apic.AttributeValueClientBO;
import com.bosch.caltool.icdm.client.bo.general.CurrentUserBO;
import com.bosch.caltool.icdm.common.ui.propertysource.AbstractDataObjectPropertySource;
import com.bosch.caltool.icdm.common.util.CommonUtils;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.ApicConstants;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * class for Showing the Attribute Values in the properties View.
 *
 * @author rgo7cob
 */
public class AttributesValueTablePropertiesViewSource extends AbstractDataObjectPropertySource<AttributeValue> {

  /**
   * Attribute name property
   */
  private static final String PROP_ATTR_NAME = "Attribute";
  /**
   * Unit property
   */
  private static final String PROP_UNIT = "Unit";
  /**
   * Value deleted property
   */
  private static final String PROP_DELETED = "Deleted";
  /**
   * Modifiable property
   */
  private static final String PROP_MODIFIABLE = "Modifiable";
  /**
   * Characterstic value mapped property
   */
  private static final String PROP_CHARVAL = ApicConstants.CHARVAL;
  /**
   * Clearing status property
   */
  private static final String PROP_CLEARING_STATUS = "Clearing Status";
  /**
   * Change comment property
   */
  private static final String PROP_CHANGE_COMMENT = "Change Comment";

  /**
   * Contsructor
   *
   * @param attrVal attrVal
   */
  public AttributesValueTablePropertiesViewSource(final AttributeValue attrVal) {
    super(attrVal);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Object getStrPropertyValue(final String propKey) {
    String result = "";
    AttributeValue attrVal = getDataObject();
    // Attribute name
    if (PROP_ATTR_NAME.equals(propKey)) {
      if (null != getAttrValBO()) {
        result = getAttrValBO().getAttribute().getName();
      }
    }
    // Unit Column.
    else if (PROP_UNIT.equals(propKey)) {
      result = attrVal.getUnit();
    }
    // Modifiable Column.
    else if (PROP_MODIFIABLE.equals(propKey)) {
      try {
        result = CommonUtils.getDisplayText(new CurrentUserBO().hasApicWriteAccess());
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().errorDialog(exp.getMessage(), exp, com.bosch.caltool.apic.ui.Activator.PLUGIN_ID);
      }
    }
    // Deleted Column.
    else if (PROP_DELETED.equals(propKey)) {
      result = CommonUtils.getDisplayText(attrVal.isDeleted());
    }
    // Clearing status of value
    else if (PROP_CLEARING_STATUS.equals(propKey)) {
      // Clearing status is mandatory in database table. Null check is not required
      result = new AttributeValueClientBO(attrVal).getClearingStatus().getUiText();
    }
    // Value class column.
    else if (PROP_CHARVAL.equals(propKey)) {
      // to be done
    }
    // ICDM-1397
    else if (PROP_CHANGE_COMMENT.equals(propKey)) {
      if (null != attrVal.getChangeComment()) {
        result = attrVal.getChangeComment().replace("\n", " ");
      }
    }
    return result;
  }

  /**
   * This method returns attribute value client bo
   * 
   * @return
   */
  public AttributeValueClientBO getAttrValBO() {
    final IWorkbenchWindow[] wbWindows = PlatformUI.getWorkbench().getWorkbenchWindows();
    if (wbWindows.length == 0) {
      return null;
    }
    AttributesEditorInput editorInput;
    final IEditorReference[] editorRefArr = wbWindows[0].getActivePage().getEditorReferences();
    for (IEditorReference editor : editorRefArr) {
      // check if the active editor is Attributes Editor
      if (editor.getPart(false) instanceof AttributesEditor) {
        try {
          editorInput = (AttributesEditorInput) editor.getEditorInput();
          return editorInput.getAttrValBO();
        }
        catch (PartInitException e) {
          CDMLogger.getInstance().warn(e.getMessage(), e);
        }
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String[] getDescFields() {
    return new String[] {
        PROP_ATTR_NAME,
        PROP_UNIT,
        PROP_MODIFIABLE,
        PROP_DELETED,
        PROP_CLEARING_STATUS,
        PROP_CHARVAL,
        PROP_CHANGE_COMMENT };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected String getTitle() {
    return "VALUE : " + getDataObject().getName();
  }

}
