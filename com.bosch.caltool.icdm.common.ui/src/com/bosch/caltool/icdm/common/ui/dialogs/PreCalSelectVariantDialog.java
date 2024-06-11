/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.common.ui.dialogs;

import java.util.Map;
import java.util.SortedSet;

import org.eclipse.swt.widgets.Shell;

import com.bosch.caltool.icdm.common.ui.wizards.PreCalDataExportWizard;
import com.bosch.caltool.icdm.common.ui.wizards.pages.PreCalAttrValWizardPage;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersion;
import com.bosch.caltool.icdm.model.apic.pidc.PidcVersionAttribute;

/**
 * This class provides a dialog to select the variant value
 */
public class PreCalSelectVariantDialog extends VariantChooserDialog {

  // AttrsValuesWizardPage instance
  private final PreCalAttrValWizardPage attrValsPage;


  /**
   * @param shell the parent shell
   * @param pidcVer version
   * @param attrSet Attribute Set
   * @param pidcVersAttrMap pidc Version Attribute Map
   * @param page attribute value wizard page instance
   */
  public PreCalSelectVariantDialog(final Shell shell, final PidcVersion pidcVer, final SortedSet<Attribute> attrSet,
      final Map<Long, PidcVersionAttribute> pidcVersAttrMap, final PreCalAttrValWizardPage page) {

    super(shell, pidcVer, attrSet, pidcVersAttrMap);
    this.attrValsPage = page;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected void okPressed() {
    PreCalDataExportWizard wiard = this.attrValsPage.getWizard();

    if (getSelVariant() != null) {
      wiard.getDataHandler().setSelVariant(getSelVariant());
      wiard.getDataHandler().loadProjAttrValDetails();
      this.attrValsPage.updateAttrValTableViewer();
    }
    super.okPressed();
  }

}