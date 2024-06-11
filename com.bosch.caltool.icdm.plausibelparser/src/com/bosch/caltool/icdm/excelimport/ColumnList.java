/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package com.bosch.caltool.icdm.excelimport;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * Contains a list of all relevant columns
 * @author imi2si
 * @version 1.0
 * @created 07-Feb-2014 10:29:25
 */
public class ColumnList implements Iterable<Column>{

    /**
     * List of all relevant columns
     */
    private List<Column> columnList;

    /**
     * Creates a new instance of a column list
     */
    public ColumnList(){
      columnList = new LinkedList<Column>();
    }

    /**
     * Add a new column to the column list
     * 
     * @param column a column that should be added to the list
     */
    public void addColumn(Column column){
      columnList.add(column);
    }

    /** 
     * {@inheritDoc}
     */
    @Override
    public Iterator<Column> iterator() {
      // TODO Auto-generated method stub
      return columnList.iterator();
    }
    
    public Column getColumnByName(String colName) {
      for (Column col:columnList)
      {
        if (col.getColumnName().equalsIgnoreCase(colName))
        {
          return col;
        }
      }
      
      return null;
    }
}