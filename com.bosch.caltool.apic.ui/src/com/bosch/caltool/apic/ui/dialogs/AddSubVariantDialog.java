package com.bosch.caltool.apic.ui.dialogs;


import java.util.HashMap;
import java.util.Map;
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
import com.bosch.caltool.icdm.client.bo.apic.PidcVariantBO;
import com.bosch.caltool.icdm.logger.CDMLogger;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcSubVariantData;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVariant;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.ws.rest.client.apic.PidcSubVariantServiceClient;
import com.bosch.caltool.icdm.ws.rest.client.exception.ApicWebServiceException;

/**
 * @author mga1cob
 */
// ICDM-121
public class AddSubVariantDialog extends PIDCAttrValueEditDialog {

  /**
   * PIDCVariant instance
   */
  private final PidcVariant selVariant;
  private Map<Long, Long> strAttrValMap = new HashMap<>();


  /**
   * The parameterized constructor
   *
   * @param parentShell instance
   * @param apicObject instance
   * @param selPidcVar This selected PIDCVariant instance
   * @param viewer PIDC details treeviewer instance
   * @param pidcVers PidcVersion
   * @param projObjBO PidcDataHandler
   * @param map structureAttrValMap
   */
  public AddSubVariantDialog(final Shell parentShell, final IModel apicObject, final TreeViewer viewer,
      final PidcVariant selPidcVar, final PidcVersion pidcVers, final AbstractProjectObjectBO projObjBO,
      final Map<Long, Long> map) {
    super(parentShell, projObjBO, apicObject, viewer, pidcVers);
    this.selVariant = selPidcVar;
    this.strAttrValMap = map;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected Control createContents(final Composite parent) {
    final Control contents = super.createContents(parent);
    // Set the title
    setTitle(ApicUiConstants.ADD_A_SUB_VARIANT);
    // Set the message
    setMessage(ApicUiConstants.ADD_A_SUB_VARIANT + " to " + this.selVariant.getName(), IMessageProvider.INFORMATION);
    return contents;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    // Get selected Sub-variant name from table viewer
    final com.bosch.caltool.icdm.model.apic.attr.AttributeValue attributeValue = getSelAttrValFromTabViewer();
    // iCDM-1155
    boolean usedSubVariant;
    PidcVariantBO pidcVarHandler = new PidcVariantBO(this.pidcVer, this.selVariant, this.pidcDataHandler);
    // get all variants of slected pid card
    SortedSet<PidcSubVariant> variantsSet = pidcVarHandler.getSubVariantsSet(true);
    // check if varaint is used
    usedSubVariant = checkSubVariantIsUsed(attributeValue, variantsSet);
    if (!usedSubVariant) {

      PidcSubVariantServiceClient pidcSubVarClient = new PidcSubVariantServiceClient();
      PidcSubVariantData pidcSubVarCreationData = new PidcSubVariantData();
      if (this.strAttrValMap != null) {
        pidcSubVarCreationData.setStructAttrValueMap(this.strAttrValMap);
      }
      pidcSubVarCreationData.setPidcVariantId(this.selVariant.getId());
      pidcSubVarCreationData.setSubvarNameAttrValue(attributeValue);

      try {
        pidcSubVarClient.create(pidcSubVarCreationData);
      }
      catch (ApicWebServiceException exp) {
        CDMLogger.getInstance().error(exp.getMessage(), exp, Activator.PLUGIN_ID);
      }
    }
    super.okPressed();
  }


}
