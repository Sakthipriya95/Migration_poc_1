package com.bosch.caltool.nattable;


/**
 * <code>NatColumnFilterObservable</code> is implemented by classes (eg: {@link CustomGlazedListsFilterStrategy}) who
 * need to inform the {@link INatColumnFilterObserver}(eg:SysConstNatFormPage) form page when individual column filter
 * is triggered so that the {@link INatColumnFilterObserver} can update its ui(in this case the <b>Filter text</b> and
 * the <b>Status bar</b> )
 * 
 * @author jvi6cob
 */
public interface INatColumnFilterObservable {

  /**
   * Method used to notify observers of type <code>NatColumnFilterObservable</code>
   */
  public void notifyNatFilterColumnObserver();

}
