package com.bosch.caltool.nattable;

import static org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX;
import static org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes.FILTER_COMPARATOR;
import static org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes.FILTER_DISPLAY_CONVERTER;
import static org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes.TEXT_DELIMITER;
import static org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes.TEXT_MATCHING_MODE;
import static org.eclipse.nebula.widgets.nattable.style.DisplayMode.NORMAL;
import static org.eclipse.nebula.widgets.nattable.util.ObjectUtils.isNotNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.convert.IDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.FilterRowUtils;
import org.eclipse.nebula.widgets.nattable.filterrow.IFilterStrategy;
import org.eclipse.nebula.widgets.nattable.filterrow.ParseResult;
import org.eclipse.nebula.widgets.nattable.filterrow.ParseResult.MatchType;
import org.eclipse.nebula.widgets.nattable.filterrow.TextMatchingMode;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.FunctionList;
import ca.odell.glazedlists.FunctionList.Function;
import ca.odell.glazedlists.TextFilterator;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;
import ca.odell.glazedlists.matchers.Matcher;
import ca.odell.glazedlists.matchers.MatcherEditor;
import ca.odell.glazedlists.matchers.TextMatcherEditor;
import ca.odell.glazedlists.matchers.ThresholdMatcherEditor;


/**
 * CustomGlazedListsFilterStrategy
 *
 * @param <T> NatTable Input data type
 * @author jvi6cob
 */
public class CustomGlazedListsFilterStrategy<T> implements IFilterStrategy<T>, INatColumnFilterObservable {

  /**
   * Constant for initial capacity of matcher list
   */
  private static final int MATCHER_INITIAL_CAPACITY = 3;
  /**
   *
   */
  private static final int CONTAINS = TextMatcherEditor.CONTAINS;
  /**
   *
   */
  private static final int REGULAR_EXPRESSION = TextMatcherEditor.REGULAR_EXPRESSION;
  /**
   *
   */
  private static final int STARTS_WITH = TextMatcherEditor.STARTS_WITH;
  /**
   *
   */
  private static final int EXACT = TextMatcherEditor.EXACT;
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

  private final AbstractNatInputToColumnConverter natInputToColumnConverter;
  private final INatColumnFilterObserver natColumnFilterObserver;
  /**
   * Indicates whether individual column filter is applied. Used for clearing all individual column filters when text is
   * entered in common filter text widget
   */
  private boolean columnFilterApplied;
  private Matcher outlineFilterMatcher;
  private Matcher toolBarFilterMatcher;
  private Matcher allColumnFilterMatcher;

  /**
   * Create a new DefaultGlazedListsFilterStrategy on the given CompositeMatcherEditor.
   * <p>
   * Note: To use constructor you need to set the CompositeMatcherEditor as MatcherEditor on the FilterList
   *
   * @param filtrList FilterList<T>
   * @param colAccessor The IColumnAccessor necessary to access the column data of the row objects in the FilterList.
   * @param configRegstry The IConfigRegistry necessary to retrieve filter specific configurations.
   * @param natInputToColumnConverter AbstractNatInputToColumnConverter
   * @param natColFilterObserver INatColumnFilterObserver
   */
  public CustomGlazedListsFilterStrategy(final FilterList<T> filtrList, final IColumnAccessor<T> colAccessor,
      final IConfigRegistry configRegstry, final AbstractNatInputToColumnConverter natInputToColumnConverter,
      final INatColumnFilterObserver natColFilterObserver) {
    this.columAccessor = colAccessor;
    this.confgRegistry = configRegstry;
    this.natInputToColumnConverter = natInputToColumnConverter;
    this.natColumnFilterObserver = natColFilterObserver;

    this.allColumnMatcherEditor = new CompositeMatcherEditor<T>();
    this.individualColumnMatcherEditor = new CompositeMatcherEditor<T>();
    this.outlineFilterMatcherEditor = new CompositeMatcherEditor<T>();
    this.toolbarFilterMatcherEditor = new CompositeMatcherEditor<T>();

    EventList<MatcherEditor<T>> matcherEditors = new BasicEventList<>(MATCHER_INITIAL_CAPACITY);
    matcherEditors.add(this.allColumnMatcherEditor);
    matcherEditors.add(this.individualColumnMatcherEditor);
    matcherEditors.add(this.outlineFilterMatcherEditor);
    matcherEditors.add(this.toolbarFilterMatcherEditor);

    this.masterMatcherEditor = new CompositeMatcherEditor<T>(matcherEditors);
    filtrList.setMatcherEditor(this.masterMatcherEditor);

  }

  /**
   * Create GlazedLists matcher editors and apply them to facilitate filtering. Usually called by the individual column
   * filter in NatTable
   */
  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public void applyFilter(final Map<Integer, Object> filtrIndexToObjMap) {
    try {
      this.individualColumnMatcherEditor.getMatcherEditors().clear();


      if (filtrIndexToObjMap.isEmpty()) {

        // Update status bar of natColumnFilterObserver when user clears text in individual column filter
        this.natColumnFilterObserver.updateStatusBar(false);
        return;
      }

      EventList<MatcherEditor<T>> matchrEditors = new BasicEventList<MatcherEditor<T>>();

      processFiltering(filtrIndexToObjMap, matchrEditors);

      setColFilterApplied(matchrEditors);

      consolidateFilterMatcherEditors();

    }
    catch (Exception e) {
      // TODO: logger
    }
  }

  /**
   * @param filtrIndexToObjMap
   * @param matchrEditors
   */
  private void processFiltering(final Map<Integer, Object> filtrIndexToObjMap,
      final EventList<MatcherEditor<T>> matchrEditors) {
    for (Entry<Integer, Object> mapEntry : filtrIndexToObjMap.entrySet()) {
      Integer colIndex = getColumnIndex(mapEntry);
      String filterTxt = getFilterText(mapEntry);
      IDisplayConverter displayConvertr = getDisplayConvertor(colIndex);
      // TODO: add flag to used CustomFilterRowUtils which provides custom icdm regex/FilterRowUtils which supports
      // Normal regex
      TextMatchingMode textMatchMode =
          this.confgRegistry.getConfigAttribute(TEXT_MATCHING_MODE, NORMAL, FILTER_ROW_COLUMN_LABEL_PREFIX + colIndex);
      String txtDelimter =
          this.confgRegistry.getConfigAttribute(TEXT_DELIMITER, NORMAL, FILTER_ROW_COLUMN_LABEL_PREFIX + colIndex);
      List<ParseResult> parseRslts = CustomFilterRowUtils.parse(filterTxt, txtDelimter, textMatchMode);

      EventList<MatcherEditor<T>> stringMatchrEditors = new BasicEventList<MatcherEditor<T>>();
      for (ParseResult parseResultObj : parseRslts) {
        processParseResults(matchrEditors, colIndex, displayConvertr, textMatchMode, stringMatchrEditors,
            parseResultObj);
      }

      if (!stringMatchrEditors.isEmpty()) {
        addStringCompositeMatcher(matchrEditors, stringMatchrEditors);
      }
    }
  }

  /**
   * @param matchrEditors
   */
  private void setColFilterApplied(final EventList<MatcherEditor<T>> matchrEditors) {
    this.individualColumnMatcherEditor.getMatcherEditors().addAll(matchrEditors);
    this.individualColumnMatcherEditor.setMode(CompositeMatcherEditor.AND);
    // Update status bar of natColumnFilterObserver with individual column filtered rows
    this.natColumnFilterObserver.updateStatusBar(false);
    // Set the columnFilterApplied flag to true when ever a individual column filter is applied
    this.columnFilterApplied = true;
  }

  /**
   * @param matchrEditors
   * @param stringMatchrEditors
   */
  private void addStringCompositeMatcher(final EventList<MatcherEditor<T>> matchrEditors,
      final EventList<MatcherEditor<T>> stringMatchrEditors) {
    CompositeMatcherEditor<T> stringCompositeMatchrEditor = new CompositeMatcherEditor<T>(stringMatchrEditors);
    stringCompositeMatchrEditor.setMode(CompositeMatcherEditor.OR);
    matchrEditors.add(stringCompositeMatchrEditor);
  }

  /**
   * @param matchrEditors
   * @param colIndex
   * @param displayConvertr
   * @param textMatchMode
   * @param stringMatchrEditors
   * @param parseResultObj
   */
  private void processParseResults(final EventList<MatcherEditor<T>> matchrEditors, final Integer colIndex,
      final IDisplayConverter displayConvertr, final TextMatchingMode textMatchMode,
      final EventList<MatcherEditor<T>> stringMatchrEditors, final ParseResult parseResultObj) {
    MatchType matchOpertn = parseResultObj.getMatchOperation();
    if (matchOpertn == MatchType.NONE) {
      stringMatchrEditors
          .add(getTextMatcherEditor(colIndex, textMatchMode, displayConvertr, parseResultObj.getValueToMatch()));
    }
    else {
      Object threshld = displayConvertr.displayToCanonicalValue(parseResultObj.getValueToMatch());
      Comparator comparatr =
          this.confgRegistry.getConfigAttribute(FILTER_COMPARATOR, NORMAL, FILTER_ROW_COLUMN_LABEL_PREFIX + colIndex);
      final Function<T, Object> columnValueProvider = getColumnValueProvider(colIndex);
      matchrEditors.add(getThresholdMatcherEditor(colIndex, threshld, comparatr, columnValueProvider, matchOpertn));
    }
  }

  /**
   * @param colIndex
   * @return
   */
  private IDisplayConverter getDisplayConvertor(final Integer colIndex) {
    return this.confgRegistry.getConfigAttribute(FILTER_DISPLAY_CONVERTER, NORMAL,
        FILTER_ROW_COLUMN_LABEL_PREFIX + colIndex);
  }

  /**
   * @param mapEntry
   * @return
   */
  private String getFilterText(final Entry<Integer, Object> mapEntry) {
    return (String) mapEntry.getValue();
  }

  /**
   * @param mapEntry
   * @return
   */
  private Integer getColumnIndex(final Entry<Integer, Object> mapEntry) {
    return mapEntry.getKey();
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
   * Converts the object inserted to the filter cell at the column position to the correspnding String.
   *
   * @param colIndex The column index of the filter cell that should be processed.
   * @param obj The value set to the filter cell that needs to be converted
   * @return String value for the filter value.
   */
  protected String getStringFromColumnObject(final int colIndex, final Object obj) {
    final IDisplayConverter displayConverter = this.confgRegistry.getConfigAttribute(FILTER_DISPLAY_CONVERTER, NORMAL,
        FILTER_ROW_COLUMN_LABEL_PREFIX + colIndex);
    return displayConverter.canonicalToDisplayValue(obj).toString();
  }

  /**
   * Set up a threshold matcher for tokens like '&gt;20', '&lt;=10' etc.
   *
   * @param columnIndex Integer
   * @param threshold Object
   * @param comparator Comparator<Object>
   * @param columnValueProvider Function<T, Object>
   * @param matchOperation MatchType
   * @return ThresholdMatcherEditor<T, Object>
   */
  protected ThresholdMatcherEditor<T, Object> getThresholdMatcherEditor(final Integer columnIndex,
      final Object threshold, final Comparator<Object> comparator, final Function<T, Object> columnValueProvider,
      final MatchType matchOperation) {
    ThresholdMatcherEditor<T, Object> thresholdMatcherEditor =
        new ThresholdMatcherEditor<T, Object>(threshold, null, comparator, columnValueProvider);

    FilterRowUtils.setMatchOperation(thresholdMatcherEditor, matchOperation);
    return thresholdMatcherEditor;
  }

  /**
   * @param columnIndex int
   * @return FunctionList.Function Function which exposes the content of the given column index from the row object
   */
  protected FunctionList.Function<T, Object> getColumnValueProvider(final int columnIndex) {
    return new FunctionList.Function<T, Object>() {

      @Override
      public Object evaluate(final T rowObject) {
        Object evaluateObj = CustomGlazedListsFilterStrategy.this.columAccessor.getDataValue(rowObject, columnIndex);
        return CustomGlazedListsFilterStrategy.this.natInputToColumnConverter.getColumnValue(evaluateObj, columnIndex);
      }
    };
  }

  /**
   * Sets up a text matcher editor for String tokens
   *
   * @param colIndex of the column for which the matcher editor is being set up
   * @param txtMatchingMode TextMatchingMode
   * @param convertor IDisplayConverter
   * @param filtrTxt text entered by the user in the filter row
   * @return TextMatcherEditor
   */
  protected TextMatcherEditor<T> getTextMatcherEditor(final Integer colIndex, final TextMatchingMode txtMatchingMode,
      final IDisplayConverter convertor, final String filtrTxt) {
    TextMatcherEditor<T> txtMatcherEditor = new TextMatcherEditor<T>(getTextFilterator(colIndex, convertor));
    txtMatcherEditor.setFilterText(new String[] { filtrTxt });
    txtMatcherEditor.setMode(getGlazedListsTextMatcherEditorMode(txtMatchingMode));
    return txtMatcherEditor;
  }

  /**
   * @param colIndex Integer
   * @param convertor IDisplayConverter
   * @return TextFilterator
   */
  protected TextFilterator<T> getTextFilterator(final Integer colIndex, final IDisplayConverter convertor) {
    return new TextFilterator<T>() {

      @Override
      public void getFilterStrings(final List<String> objAsListOfStrings, final T rowObj) {
        Object cellDataObject = CustomGlazedListsFilterStrategy.this.columAccessor.getDataValue(rowObj, colIndex);
        cellDataObject =
            CustomGlazedListsFilterStrategy.this.natInputToColumnConverter.getColumnValue(cellDataObject, colIndex);
        Object displayVal = convertor.canonicalToDisplayValue(cellDataObject);
        displayVal = isNotNull(displayVal) ? displayVal : ""; //$NON-NLS-1$
        objAsListOfStrings.add(displayVal.toString().toLowerCase());

      }
    };
  }

  /**
   * @param textMatchMode TextMatchingMode
   * @return int
   */
  public int getGlazedListsTextMatcherEditorMode(final TextMatchingMode textMatchMode) {
    switch (textMatchMode) {
      case EXACT:
        return EXACT;
      case STARTS_WITH:
        return STARTS_WITH;
      case REGULAR_EXPRESSION:
        return REGULAR_EXPRESSION;
      default:
        return CONTAINS;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void notifyNatFilterColumnObserver() {
    // TODO Auto-generated method stub

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
