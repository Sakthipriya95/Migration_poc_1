package com.bosch.caltool.nattable;


/**
 * <code>NatColumnFilterObserver</code> listens to {@link INatColumnFilterObservable}
 * 
 * @author jvi6cob
 */
public interface INatColumnFilterObserver {

  /**
   * <b>Status bar is updated with number of individual column(s) filtered rows</b>
   * 
   * @param outlineSelection boolean
   */
  public void updateStatusBar(boolean outlineSelection);

}
