/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.apic.ws.client.seriesstatisticsfilter;

import java.util.ArrayList;
import java.util.List;

import com.bosch.calcomp.caldataanalyzer.filter.IFilter;
import com.bosch.calcomp.caldataanalyzer.filter.labelfilter.impl.ClusterLabelValueFilterImpl;
import com.bosch.calcomp.caldataanalyzer.model.DataHolder;
import com.bosch.calcomp.caldataanalyzer.model.OperatorAndDataModel;
import com.bosch.caltool.apic.ws.DefaultSeriesStatisticsFilterType;


/**
 * Translates an array of SeriesStatisticsExport filters of the webservice to a format the CalDataAnalyzer can
 * understand. FilterAdapter takes an array of DefaultSeriesStatisticsFilterType objects and converts them into a list
 * of IFilter objects used by the CalDataAnalyzer.<br>
 * Program flow:<br>
 * <ul>
 * <li>Pass the the DefaultSeriesStatisticsFilterType-Array and the DataHolder to the Constructor</li>
 * <li>Constructor calls the private methods
 * <ul>
 * <li>convertParamFilter() for converting into a label value filter,</li>
 * <li>convertLabelExistsFilter() for converting into a label exists filter</li>
 * <li>convertSysConstFilter() and for converting into a system constant filter.</li>
 * <li>...additional methods should be added if in future other filters are required</li>
 * </ul>
 * <li>{@link FilterAdapter#getFilterList() getFilterList()} returns the converted filter list ready to pass to the
 * CalDataAnalyzer</li>
 * </ul>
 *
 * @author imi2si
 * @see com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.AbstractSeriesStatisticsFilter
 *      AbstractSeriesStatisticsFilter
 * @since 1.14
 */
public class FilterAdapter {

  /**
   * DataHolder required for CalDataAnalyzer
   */
  private final DataHolder dataHolder;

  /**
   * The original filters passed from the webservice
   */
  private final DefaultSeriesStatisticsFilterType[] filters;

  /**
   * The original filters passed from the webservice
   */
  private List<AbstractSeriesStatisticsFilter> adapterFilterList = null;

  /**
   * The resulting filter list after processing the webservice list. This list is required because the CalDataAnalyzer
   * datatype IFilter is not sufficent for performing the checks required to determine if a filter should be added.
   */
  private final List<IFilter> filterList = new ArrayList<>();

  /**
   * String representation of the filter criteria
   */
  private final StringBuffer filterCriteria = new StringBuffer();

  /**
   * Constructor that needs an array of DefaultSeriesStatisticsFilterTypes and a data holder. After passing these two
   * parameters the conversiton happends. That means, that after calling the constructor, the result can be get by
   * calling {@link FilterAdapter#getFilterList() getFilterList()}.
   *
   * @param filters an array of DefaultSeriesStatisticsFilterType objects from the webservice
   * @param dataHolder the DataHolder object from the CalDataAnalyzer
   */
  public FilterAdapter(final DefaultSeriesStatisticsFilterType[] filters, final DataHolder dataHolder) {
    this.filters = filters == null ? new DefaultSeriesStatisticsFilterType[0] : filters;
    this.dataHolder = dataHolder;

    convertParamFilter();
    convertLabelExistsFilter();
    convertSysConstFilter();
    convertFunctionFilter();
    convertBaseCompFilter();
  }

  /**
   * Returns the converted filters of the webservice as a list of IFilter objects that can be passed to the
   * CalDataAnalyzer.
   *
   * @return a List of IFilter objects
   */
  public List<IFilter> getFilterList() {
    return new ArrayList<>(this.filterList);
  }

  /**
   * Returns a textual representation of the filters (a where clause so to say).
   *
   * @return the filters as text
   */
  public String getFilters() {
    return this.filterCriteria.toString();
  }

  /**
   * Conversion method for a parameter filter. Creates a parameter-value-filter if type of Webservice Filter is of
   * ISeriesStatisticsFilter.DataType.PARAMETER_FILTER. If there are several filters, they are connected to a cluster
   * using an AND operator. This is necessary for the CalDataAnalyzer to work correctly.
   */
  private void convertParamFilter() {
    this.adapterFilterList = new ArrayList<>();
    for (DefaultSeriesStatisticsFilterType filter : this.filters) {
      addToAdapterFilterList(new DefaultSeriesStatLabelAdapter(filter, this.dataHolder));
    }

    /*
     * ClusterLabelValueFilters are required if there are many filter that must be checked. The ClusterLabelValue
     * connectes the single filters with an AND-Condition with each other. Called in recursive manner with method
     * getClusterLabelValueFilter.
     */
    if (!this.adapterFilterList.isEmpty()) {
      this.filterList.add(new ClusterLabelValueFilterImpl(getClusterLabelValueFilter(this.adapterFilterList, 0)));
    }
  }

  /**
   * Connects the filter n with filter n+1 using the AND operator. Cycles through the list of filters by recursive
   * calls.
   *
   * @return an OperatorAndDataModel that connects the current filter with the next filter.
   */
  private Object getClusterLabelValueFilter(final List<AbstractSeriesStatisticsFilter> filterListParam, final int index) {
    /*
     * Attention: filterListParam.get(index).getFilter() -> getFilter() is very important. Only than, the right Object
     * type for the CalDataAnalyzer is returned The CalDataAnalyzer classes work with Superclass Object and instanceOf
     * checks. Thus in the adapter classes is no better type check possible.
     */
    if (filterListParam.size() > (index + 1)) {
      return new OperatorAndDataModel(filterListParam.get(index).getFilter(), getClusterLabelValueFilter(
          filterListParam, index + 1));
    }

    return filterListParam.get(index).getFilter();
  }

  /**
   * Conversion method for a parameter filter. Creates a parameter-exists-filter if type of Webservice Filter is of
   * ISeriesStatisticsFilter.DataType.PARAMETER_FILTER. A parameter exists filter must exist beside the value filter.
   * Otherwise the CalDataAnalyzer ignore the Parameter Value filter. If no values are filtered, just an
   * existance-filter is created.
   */
  private void convertLabelExistsFilter() {
    this.adapterFilterList = new ArrayList<>();
    for (DefaultSeriesStatisticsFilterType filter : this.filters) {
      addToAdapterFilterList(new LabelExistsAdapter(filter, this.dataHolder));
    }

    addToAnalyzerFilterList();
  }

  /**
   * Conversion method for a system constants filter. Creates a system-constants-filter if type of Webservice Filter is
   * of AbstractSeriesStatisticsFilter.DataType.SYSTEM_CONSTANT.
   */
  private void convertSysConstFilter() {
    this.adapterFilterList = new ArrayList<>();
    for (DefaultSeriesStatisticsFilterType filter : this.filters) {
      addToAdapterFilterList(new SysConstAdapter(filter, this.dataHolder));
    }

    addToAnalyzerFilterList();
  }

  /**
   * Conversion method for a function filter. Creates a function-filter if type of Webservice Filter is of
   * AbstractSeriesStatisticsFilter.DataType.FUNCTION_FILTER.
   */
  private void convertFunctionFilter() {
    this.adapterFilterList = new ArrayList<>();
    for (DefaultSeriesStatisticsFilterType filter : this.filters) {
      addToAdapterFilterList(new FunctionAdapter(filter, this.dataHolder));
    }

    addToAnalyzerFilterList();
  }

  /**
   * Conversion method for a base components filter. Creates a base-component-filter if type of Webservice Filter is of
   * AbstractSeriesStatisticsFilter.DataType.BASE_COMPONENTS_FILTER.
   */
  private void convertBaseCompFilter() {
    this.adapterFilterList = new ArrayList<>();
    for (DefaultSeriesStatisticsFilterType filter : this.filters) {
      addToAdapterFilterList(new BaseCompAdapter(filter, this.dataHolder));
    }

    addToAnalyzerFilterList();
  }

  /**
   * Adds the passed filter to the list of checked filters when the filter is valid and not yet existing.
   *
   * @param filter the AbstractSeriesStatisticsFilter that should be added to the list of filters
   */
  private void addToAdapterFilterList(final AbstractSeriesStatisticsFilter filter) {
    if (filter.isValid() && !filterExists(filter)) {
      // Add to check if filter exists. Only possible if type is of list is AbstractSeriesStatisticsFilter
      this.adapterFilterList.add(filter);

      // Add this filter to the String representation of the filter. This is used for logging and adding to the
      // CalDataHistory afterwards.
      this.filterCriteria.append(filter.toString());
    }
  }

  /**
   * Creates the final list for the CalDataAnalyzer on base of the AdapterFilterList. Two lists are necessary because
   * the Interface IFilter of the CalDataAnalyzer doen't provide enough information to use just one list.
   */
  private void addToAnalyzerFilterList() {
    for (AbstractSeriesStatisticsFilter adapterFilter : this.adapterFilterList) {
      this.filterList.add(adapterFilter.getFilter());
    }
  }

  /**
   * Checks if a passed filter already exists in the target list using the compareTo method.
   *
   * @param filter the filter that should be checked for existance
   * @return true if the filter already exists in the list of AdapterFilters, otherwise false
   * @see com.bosch.caltool.apic.ws.client.seriesstatisticsfilter.AbstractSeriesStatisticsFilter#compareTo(AbstractSeriesStatisticsFilter)
   *      Comparions logic for filter existance
   */
  private boolean filterExists(final AbstractSeriesStatisticsFilter filter) {

    for (AbstractSeriesStatisticsFilter filterComp : this.adapterFilterList) {
      if (filterComp.compareTo(filter) == 0) {
        return true;
      }
    }

    return false;
  }
}
