/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ui.listeners;

import java.util.Map;

import org.eclipse.nebula.widgets.grid.GridItem;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;

import com.bosch.caltool.apic.ui.dialogs.AddNewPredefinedValDialog;
import com.bosch.caltool.apic.ui.dialogs.EditValueDialog;
import com.bosch.caltool.apic.ui.editors.pages.PredefinedAttributesPage;
import com.bosch.caltool.icdm.model.apic.attr.Attribute;
import com.bosch.caltool.icdm.model.apic.attr.AttributeValue;
import com.bosch.rcputils.nebula.gridviewer.GridTableViewerUtil;

/**
 * ICDM-2593 Listener for the value edit column of the predefined attribute value table viewer
 *
 * @author dja7cob
 */
public class PredefinedAttrTableViewerListeners {

  /**
   * constant for left mouse click
   */
  protected static final int LEFT_MOUSE_CLICK = 1;
  /**
   * PredefinedAttributesPage instance
   */
  private final PredefinedAttributesPage predefinedAttributesPage;
  /**
   * selPredefinedValMap instance
   */
  private final Map<Attribute, AttributeValue> selPredefinedValMap;
  /**
   * EditValueDialog instance
   */
  private final EditValueDialog editValDialog;

  /**
   * @param predefinedAttributesPage PredefinedAttributesPage
   * @param selPredefinedValMap Map of predefined attribute and value selected
   * @param editValDialog instance
   */
  public PredefinedAttrTableViewerListeners(final PredefinedAttributesPage predefinedAttributesPage,
      final Map<Attribute, AttributeValue> selPredefinedValMap, final EditValueDialog editValDialog) {
    this.predefinedAttributesPage = predefinedAttributesPage;
    this.selPredefinedValMap = selPredefinedValMap;
    this.editValDialog = editValDialog;
  }

  /**
   * Left click listener for value edit column
   */
  public void addMouseDownListener() {
    PredefinedAttrTableViewerListeners.this.predefinedAttributesPage.getPredefinedAttrValTableViewer().getGrid()
        .addMouseListener(new MouseAdapter() {

          /**
           * {@inheritDoc}
           */
          @Override
          public void mouseDown(final MouseEvent event) {


            final Point point = new Point(event.x, event.y);
            // Determine which row was selected
            final GridItem item = PredefinedAttrTableViewerListeners.this.predefinedAttributesPage
                .getPredefinedAttrValTableViewer().getGrid().getItem(point);

            if ((item != null) && !item.isDisposed() && (event.button == LEFT_MOUSE_CLICK)) {
              final Object data = item.getData();
              final Attribute pidcAttr = (Attribute) data;
              final int columnIndex = GridTableViewerUtil.getInstance().getTabColIndex(event,
                  PredefinedAttrTableViewerListeners.this.predefinedAttributesPage.getPredefinedAttrValTableViewer());

              // Determine which column was selected
              // If it is the value edit column, open the dialog to select the predefined attribute value
              if (columnIndex == 2) {
                final Rectangle rect = item.getBounds(columnIndex);
                if (rect.contains(point)) {
                  editTabItem(pidcAttr);
                }
              }

            }
          }
        });
  }

  /**
   * Opem dialog to add value to the selected predefined attribute
   */
  private void editTabItem(final Attribute pidcAttr) {
    AddNewPredefinedValDialog predefinedValDialog = new AddNewPredefinedValDialog(Display.getCurrent().getActiveShell(),
        pidcAttr, this.predefinedAttributesPage, this.selPredefinedValMap, this.editValDialog);
    predefinedValDialog.open();
  }
}
