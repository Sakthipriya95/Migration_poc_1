/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.ruleseditor.dialogs;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.nebula.jface.gridviewer.CheckEditingSupport;
import org.eclipse.nebula.jface.gridviewer.GridTableViewer;
import org.eclipse.nebula.jface.gridviewer.GridViewerColumn;
import org.eclipse.swt.SWT;

import com.bosch.caltool.apic.ui.util.ApicUiConstants;
import com.bosch.caltool.icdm.ruleseditor.views.providers.BitwiseLimitTableLabelProvider;
import com.bosch.rcputils.griddata.GridDataUtil;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;
import com.bosch.rcputils.nebula.gridviewer.GridViewerColumnUtil;


/**
 * @author bru2cob
 */
public class BitwiseLimitTable {

  /**
   * GridTableViewer instance for PIDC attribute
   */
  private GridTableViewer bitsTabViewer;

  /**
   * List of input bits for the table
   */
  List<BitValue> inputBitTypes;

  private final BitWiseLimitConfigDialog bitWiseLimitConfigDialog;

  /**
   * @param bitWiseLimitConfigDialog
   */
  public BitwiseLimitTable(final BitWiseLimitConfigDialog bitWiseLimitConfigDialog) {
    this.bitWiseLimitConfigDialog = bitWiseLimitConfigDialog;
  }


  /**
   * Create the bitwise table viewer
   */
  public void createBitWiseTable() {
    this.bitsTabViewer =
        GridTableViewerUtil.getInstance().createGridTableViewer(this.bitWiseLimitConfigDialog.getForm().getBody(),
            SWT.FULL_SELECTION | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI,
            GridDataUtil.getInstance().getHeightHintGridData(100));
    createBitwiseTableCol();
    this.bitsTabViewer.setContentProvider(ArrayContentProvider.getInstance());
    this.inputBitTypes = this.bitWiseLimitConfigDialog.getBitWiseConfig().getBitTypes();
    this.bitsTabViewer.setInput(this.inputBitTypes);


  }


  /**
   * Create the bitwise table columns
   */
  private void createBitwiseTableCol() {
    createBitTypeCol();
    createBitColumns();

  }

  /**
   *
   */
  private void createBitTypeCol() {
    final GridViewerColumn typeColumn =
        GridViewerColumnUtil.getInstance().createGridViewerColumn(this.bitsTabViewer, "", 55);
    typeColumn.setLabelProvider(new BitwiseLimitTableLabelProvider(ApicUiConstants.COLUMN_INDEX_0));
  }

  private void createBitColumns() {
    for (int i = BitValue.NO_OF_BITS - 1; i >= 0; i--) {
      int columnIndex = BitValue.NO_OF_BITS - i;
      final GridViewerColumn bitColumn = GridViewerColumnUtil.getInstance()
          .createGridViewerCheckStyleColumn(this.bitsTabViewer, Integer.toString(i), 25);
      bitColumn.setEditingSupport(new CheckEditingSupport(bitColumn.getViewer()) {

        @Override
        public void setValue(final Object arg0, final Object arg1) {
          setBitsValue(arg0, arg1, columnIndex);

        }
      });
      bitColumn.setLabelProvider(new BitwiseLimitTableLabelProvider(columnIndex));
    }
  }

  /**
   * @return the inputBitTypes
   */
  public List<BitValue> getInputBitTypes() {
    return this.inputBitTypes;
  }


  /**
   * @return the bitsTabViewer
   */
  public GridTableViewer getBitsTabViewer() {
    return this.bitsTabViewer;
  }

  /**
   * Adds/removes the bits based on the selection in the table viewer
   *
   * @param arg0 selected obj
   * @param arg1
   */
  private void setBitsValue(final Object arg0, final Object arg1, final int colIndex) {
    if (arg0 instanceof BitValue) {
      BitValue bitValue = (BitValue) arg0;
      boolean checkedVal = (boolean) arg1;
      int bitIndex = bitValue.getColNumOrBitIndex(colIndex);
      if (checkedVal) {
        addSetBits(bitValue, bitIndex);
      }
      else {
        if (bitValue.getBitsList().contains(bitIndex)) {
          bitValue.removeBit(bitIndex);
        }
      }
      setBitSelected();
      this.bitsTabViewer.refresh();
    }
  }

  /**
   * Add the checked (set) bits to the bitlist
   *
   * @param bitValue
   * @param bitIndex
   */
  private void addSetBits(final BitValue bitValue, final int bitIndex) {
    for (BitValue bits : this.inputBitTypes) {
      if (bits.getBitsList().contains(bitIndex)) {
        bits.removeBit(bitIndex);
      }
    }
    bitValue.addBit(bitIndex);
  }

  /**
   * if one bit in the dialog is set , set the flag to true
   */
  private void setBitSelected() {
    for (BitValue bits : this.inputBitTypes) {
      if (!bits.getBitsList().isEmpty()) {
        this.bitWiseLimitConfigDialog.setBitSelected(true);
        break;
      }
    }
  }

}
