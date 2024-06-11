package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.layer.CompositeLayer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.forms.widgets.Form;


/**
 * Creates new NAT Table for specific configuration<br>
 * 
 * @author adn1cob
 */
public class NATLayerFactory {


  private static NatTable natTable;

  /**
   * * Use this constructor to create NAT table with COMPOSITE layer
   * 
   * @param baseForm baseForm
   * @param compLayer compLayer
   * @param autoconfigure false to provide custom configuration
   * @return NATTABLE
   */
  public static NatTable createDefaultStyleNatTable(final Form baseForm, final CompositeLayer compLayer,
      final boolean autoconfigure) {

    natTable =
        new NatTable(baseForm.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE |
            SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, compLayer,
            autoconfigure);

    return natTable;
  }

  /**
   * Use this constructor to create NAT table with filter GRID LAYER
   * 
   * @param baseForm baseForm
   * @param filterGridLayer gridLayer
   * @param autoconfigure false to provide custom configuration
   * @return NatTable
   */
  public NatTable createDefaultStyleNatTable(final Form baseForm, final AbstractNATGridLayer<?> filterGridLayer,
      final boolean autoconfigure) {

    natTable =
        new NatTable(baseForm.getBody(), SWT.FULL_SELECTION | SWT.NO_BACKGROUND | SWT.NO_REDRAW_RESIZE |
            SWT.DOUBLE_BUFFERED | SWT.BORDER | SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL | SWT.MULTI, filterGridLayer,
            autoconfigure);
    return natTable;
  }


}
