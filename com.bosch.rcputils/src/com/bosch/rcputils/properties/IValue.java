/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.bosch.rcputils.properties;

/**
 * IValue describes a value that can be get from / set to an object.
 * 
 */
public interface IValue {

	public Object getValue(Object element);

	public void setValue(Object element, Object value);

}
