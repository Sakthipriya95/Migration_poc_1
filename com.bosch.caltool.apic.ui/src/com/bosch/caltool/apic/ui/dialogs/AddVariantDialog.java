/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.apic.ui.Activator;
import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.datamodel.core.IModel;
import com.bosch.caltool.icdm.client.bo.apic.AbstractProjectObjectBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;


/**
 * @author dmo5cob
 */

public class AddVariantDialog extends PIDCAttrValueEditDialog {

  /**
   * PIDCard instance
   */
  private final PidcVersion selPidcVer;

  // ICDM 395 This map will hold the attribute and corresponding value if the variant is getting created from the leaf
  // node
  private Map<Attribute, AttributeValue> attrValMap = new HashMap<>();

  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param projObjBO while creating a new PIDC, this should be null
   * @param apicObject instance
   * @param selPidcVer This selected PIDCard instance
   * @param viewer PIDC treeviewer instance
   * @param attrValMap attribute value map
   */
  public AddVariantDialog(final Shell parentShell, final AbstractProjectObjectBO projObjBO, final IModel apicObject,
      final TreeViewer viewer, final PidcVersion selPidcVer, final Map<Attribute, AttributeValue> attrValMap) {
    super(parentShell, projObjBO, apicObject, viewer, selPidcVer);
    this.selPidcVer = selPidcVer;
    this.attrValMap = attrValMap;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle(ApicUiConstants.ADD_A_VARIANT);
    // Set the message
    setMessage(ApicUiConstants.ADD_A_VARIANT + " to " + this.selPidcVer.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // Get selected pidc name from table viewer
    boolean usedVariant;
    final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue = getSelAttrValFromTabViewer();

    // iCDM-1155
    // get all variants of selected pid card
    SortedSet<PidcVariant> variantsSet = this.pidcVersionBO.getVariantsSet(true);

    // check if varaint is used
    usedVariant = checkVariantIsUsed(attributeValue, variantsSet);
    if (!usedVariant) {
      PidcVariantServiceClient pidcVarServiceClient = new PidcVariantServiceClient();

      PidcVariantData pidcVarData = new PidcVariantData();
      if (this.attrValMap != null) {
        Map<Long, Long> structAttrValueMap = getAttrValueMapWithId(this.attrValMap);
        pidcVarData.setStructAttrValueMap(structAttrValueMap);
      }
      pidcVarData.setPidcVersion(this.selPidcVer);
      pidcVarData.setVarNameAttrValue(attributeValue);

      try {
        pidcVarServiceClient.create(pidcVarData);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }

  /**
   * @param attrValMap2
   * @return
   */
  private Map<Long, Long> getAttrValueMapWithId(final Map<Attribute, AttributeValue> attrValMap2) {
    Map<Long, Long> structAttrValueMap = new HashMap<>();
    for (Entry<Attribute, AttributeValue> attrValueEntry : attrValMap2.entrySet()) {
      if (null != attrValueEntry.getValue()) {
        structAttrValueMap.put(attrValueEntry.getKey().getId(), attrValueEntry.getValue().getId());
      }
    }
    return structAttrValueMap;
  }


}
