/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.nattable.filters;

import java.util.Map;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.nebula.widgets.nattable.columnChooser.command.DisplayColumnChooserCommand;
import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.ruleseditor.pages.ParametersRulePage;


/**
 * @author jvi6cob
 */
public class CustomDisplayColumnChooserCommandHandler extends AbstractLayerCommandHandler<DisplayColumnChooserCommand> {

  private final ColumnHideShowLayer columnHideShowLayer;
  private final ColumnGroupModel columnGroupModel;
  private final SelectionLayer selectionLayer;
  private final DataLayer columnHeaderDataLayer;
  private final ColumnHeaderLayer columnHeaderLayer;
  private final boolean sortAvailableColumns;
  private IDialogSettings dialogSettings;
  private Map<Attribute, String> combiMap;
  private ParametersRulePage paramRulesPage;

  public CustomDisplayColumnChooserCommandHandler(final SelectionLayer selectionLayer,
      final ColumnHideShowLayer columnHideShowLayer, final ColumnHeaderLayer columnHeaderLayer,
      final DataLayer columnHeaderDataLayer, final ColumnGroupModel columnGroupModel,
      final Map<Attribute, String> combiMap, final ParametersRulePage parametersRulePage) {

    this(selectionLayer, columnHideShowLayer, columnHeaderLayer, columnHeaderDataLayer, columnGroupModel, false);
    this.combiMap = combiMap;
    this.paramRulesPage = parametersRulePage;
  }

  public CustomDisplayColumnChooserCommandHandler(final SelectionLayer selectionLayer,
      final ColumnHideShowLayer columnHideShowLayer, final ColumnHeaderLayer columnHeaderLayer,
      final DataLayer columnHeaderDataLayer, final ColumnGroupModel columnGroupModel,
      final boolean sortAvalableColumns) {

    this.selectionLayer = selectionLayer;
    this.columnHideShowLayer = columnHideShowLayer;
    this.columnHeaderLayer = columnHeaderLayer;
    this.columnHeaderDataLayer = columnHeaderDataLayer;
    this.columnGroupModel = columnGroupModel;
    this.sortAvailableColumns = sortAvalableColumns;
  }

  @Override
  public boolean doCommand(final DisplayColumnChooserCommand command) {
    CustomColumnChooser columnChooser = new CustomColumnChooser(command.getNatTable().getShell(), this.selectionLayer,
        this.columnHideShowLayer, this.columnHeaderLayer, this.columnHeaderDataLayer, this.columnGroupModel,
        this.sortAvailableColumns, this.combiMap, this.paramRulesPage);

    columnChooser.setDialogSettings(this.dialogSettings);
    columnChooser.openDialog();
    return true;
  }

  public void setDialogSettings(final IDialogSettings dialogSettings) {
    this.dialogSettings = dialogSettings;
  }

  public Class<DisplayColumnChooserCommand> getCommandClass() {
    return DisplayColumnChooserCommand.class;
  }

}
