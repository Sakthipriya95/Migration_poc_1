/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.bosch.rcputils.tables;

import org.eclipse.jface.viewers.ViewerCell;

/**
 * An ICellFormatter is responsible for formatting a cell. Should be used to
 * apply additional formatting to the cell, like setting colors / images.
 * 
 */
public interface ICellFormatter {

	public void formatCell(ViewerCell cell, Object value);

}
