package com.bosch.caltool.nattable;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.ComboBoxGlazedListsFilterStrategy;
import org.eclipse.nebula.widgets.nattable.filterrow.combobox.FilterRowComboBoxDataProvider;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;

/**
 * @author bru2cob
 */
public class CustomComboGlazedListsFilterStrategy<T> extends ComboBoxGlazedListsFilterStrategy<T> {

  /**
   * Constant for initial capacity of matcher list
   */
  private static final int MATCHER_INITIAL_CAPACITY = 3;

  /**
   * IColumnAccessor is used by this class to access columns
   */
  protected final IColumnAccessor<T> columAccessor;
  /**
   * IConfigRegistry is used by this class to get Nat specific data
   */
  protected final IConfigRegistry confgRegistry;
  /**
   * This MatcherEditor is composed of
   * allColumnMatcherEditor,individualColumnMatcherEditor,outlineFilterMatcherEditor,toolbarFilterMatcherEditor
   */
  protected final CompositeMatcherEditor<T> masterMatcherEditor;
  /**
   * This MatcherEditor is used to match rows in NatTable when filter is enabled/modified in Filter Text widget
   */
  protected final CompositeMatcherEditor<T> allColumnMatcherEditor;
  /**
   * This MatcherEditor is used to match rows in NatTable when filter is enabled/modified in NatTable Individual column
   * Filter
   */
  protected final CompositeMatcherEditor<T> individualColumnMatcherEditor;
  /**
   * This MatcherEditor is used to match rows in NatTable when there is a selection in outline view
   */
  protected final CompositeMatcherEditor<T> outlineFilterMatcherEditor;
  /**
   * This MatcherEditor is used to match rows in NatTable when predefined toolbar filters are triggered
   */
  protected final CompositeMatcherEditor<T> toolbarFilterMatcherEditor;

  private final INatColumnFilterObserver natColumnFilterObserver;
  /**
   * Indicates whether individual column filter is applied. Used for clearing all individual column filters when text is
   * entered in common filter text widget
   */
  private boolean columnFilterApplied;
  private Matcher outlineFilterMatcher;
  private Matcher toolBarFilterMatcher;
  private Matcher allColumnFilterMatcher;
  FilterRowComboBoxDataProvider<T> comboBoxDataProvider;

  /**
   * @param filterList
   * @param columnAccessor
   * @param configRegistry
   */
  public CustomComboGlazedListsFilterStrategy(final FilterRowComboBoxDataProvider<T> comboBoxDataProvider,
      final FilterList<T> filterList, final IColumnAccessor<T> columnAccessor, final IConfigRegistry configRegistry,
      final INatColumnFilterObserver natColFilterObserver, final MatcherEditor<T> matchEditor) { 
    super(comboBoxDataProvider, filterList, columnAccessor, configRegistry);
    this.columAccessor = columnAccessor;
    this.confgRegistry = configRegistry;
    this.natColumnFilterObserver = natColFilterObserver;
    this.comboBoxDataProvider = comboBoxDataProvider;
    this.allColumnMatcherEditor = new CompositeMatcherEditor<T>();
    this.individualColumnMatcherEditor = new CompositeMatcherEditor<T>();
    this.outlineFilterMatcherEditor = new CompositeMatcherEditor<T>();
    this.toolbarFilterMatcherEditor = new CompositeMatcherEditor<T>();

    EventList<MatcherEditor<T>> matcherEditors = new BasicEventList<>(MATCHER_INITIAL_CAPACITY);
    matcherEditors.add(this.allColumnMatcherEditor);
    matcherEditors.add(this.individualColumnMatcherEditor);
    matcherEditors.add(this.outlineFilterMatcherEditor);
    matcherEditors.add(this.toolbarFilterMatcherEditor);
    matcherEditors.add(matchEditor);

    this.masterMatcherEditor = new CompositeMatcherEditor<T>(matcherEditors);
    filterList.setMatcherEditor(this.masterMatcherEditor);


  }


  /**
   * Create Custom GlazedLists matcher editors and apply them to facilitate filtering. This method is called by the
   * filter text widget present above NatTable where filtering is applied to all columns
   *
   * @param filterTxtObj String
   */
  public void applyFilterInAllColumns(final String filterTxtObj) {
    this.allColumnMatcherEditor.getMatcherEditors().clear();

    // If filter text contains only *, it is equivalent to not providing filter condition.
    // In such a case, matcher editor is not created
    if (!filterTxtObj.isEmpty() && !filterTxtObj.equals("*")) {
      EventList<MatcherEditor<T>> matcherEditors = new BasicEventList<MatcherEditor<T>>();
      CustomMatcherEditor<T> customMatcherEditor = new CustomMatcherEditor<>(this.allColumnFilterMatcher);
      matcherEditors.add(customMatcherEditor);

      this.allColumnMatcherEditor.getMatcherEditors().addAll(matcherEditors);
      this.allColumnMatcherEditor.setMode(CompositeMatcherEditor.AND);
    }
    consolidateFilterMatcherEditors();
    this.natColumnFilterObserver.updateStatusBar(false);


  }


  /**
   * Create Custom GlazedLists matcher editors and apply them to facilitate filtering. This method is called by the
   * Outline selection listener
   *
   * @param clearOutlineFilter boolean
   */
  public void applyOutlineFilterInAllColumns(final boolean clearOutlineFilter) {
    try {
      this.outlineFilterMatcherEditor.getMatcherEditors().clear();

      if (!clearOutlineFilter) {
        EventList<MatcherEditor<T>> matchrEditors = new BasicEventList<MatcherEditor<T>>();
        CustomMatcherEditor<T> customMatcherEditor = new CustomMatcherEditor<>(this.outlineFilterMatcher);
        matchrEditors.add(customMatcherEditor);

        this.outlineFilterMatcherEditor.getMatcherEditors().addAll(matchrEditors);
        this.outlineFilterMatcherEditor.setMode(CompositeMatcherEditor.OR);
      }
      consolidateFilterMatcherEditors();
      this.natColumnFilterObserver.updateStatusBar(true);

    }
    catch (Exception e) {
      // TODO: logger
    }
  }

  /**
   * Create Custom GlazedLists matcher editors and apply them to facilitate filtering. This method is called by the pre
   * defined filter
   *
   * @param clearOutlineFilter boolean
   */
  public void applyToolBarFilterInAllColumns(final boolean clearOutlineFilter) {
    try {
      this.toolbarFilterMatcherEditor.getMatcherEditors().clear();

      if (!clearOutlineFilter) {
        EventList<MatcherEditor<T>> matcherEditors = new BasicEventList<MatcherEditor<T>>();
        CustomMatcherEditor<T> customMatcherEditor = new CustomMatcherEditor<>(this.toolBarFilterMatcher);
        matcherEditors.add(customMatcherEditor);

        this.toolbarFilterMatcherEditor.getMatcherEditors().addAll(matcherEditors);
        this.toolbarFilterMatcherEditor.setMode(CompositeMatcherEditor.OR);
      }
      consolidateFilterMatcherEditors();
      this.natColumnFilterObserver.updateStatusBar(false);

    }
    catch (Exception e) {
      // TODO: logger
    }
  }

  private void consolidateFilterMatcherEditors() {
    this.masterMatcherEditor.setMode(CompositeMatcherEditor.AND);
  }


  /**
   * @return the columnFilterApplied
   */
  public boolean isColumnFilterApplied() {
    return this.columnFilterApplied;
  }


  /**
   * @param columnFilterApplied the columnFilterApplied to set
   */
  public void setColumnFilterApplied(final boolean columnFilterApplied) {
    this.columnFilterApplied = columnFilterApplied;
  }

  /**
   * @param outlineFilterMatcher Matcher
   */
  public void setOutlineNatFilterMatcher(final Matcher outlineFilterMatcher) {
    this.outlineFilterMatcher = outlineFilterMatcher;
  }


  /**
   * @return the toolBarFilterMatcher
   */
  public Matcher getToolBarFilterMatcher() {
    return this.toolBarFilterMatcher;
  }


  /**
   * @param toolBarFilterMatcher Matcher
   */
  public void setToolBarFilterMatcher(final Matcher toolBarFilterMatcher) {
    this.toolBarFilterMatcher = toolBarFilterMatcher;
  }


  /**
   * @return Matcher
   */
  public Matcher getAllColumnFilterMatcher() {
    return this.allColumnFilterMatcher;
  }


  /**
   * @param allColumnFilterMatcher the allColumnFilterMatcher to set
   */
  public void setAllColumnFilterMatcher(final Matcher allColumnFilterMatcher) {
    this.allColumnFilterMatcher = allColumnFilterMatcher;
  }


  /**
   * This resets all the set filters in the table
   */
  public void resetAllFilters() {
    this.individualColumnMatcherEditor.getMatcherEditors().clear();
    this.allColumnMatcherEditor.getMatcherEditors().clear();
    this.outlineFilterMatcherEditor.getMatcherEditors().clear();
    this.toolbarFilterMatcherEditor.getMatcherEditors().clear();
    consolidateFilterMatcherEditors();
  }


  /**
   * @return the outlineFilterMatcherEditor
   */
  public CompositeMatcherEditor<T> getOutlineFilterMatcherEditor() {
    return this.outlineFilterMatcherEditor;
  }


}
