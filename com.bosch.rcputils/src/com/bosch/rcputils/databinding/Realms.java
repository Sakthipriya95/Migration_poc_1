/*******************************************************************************
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 *******************************************************************************/
package com.bosch.rcputils.databinding;

import org.eclipse.core.databinding.observable.Realm;

public class Realms {

	/**
	 * A realm that will accept any thread
	 */
	public static final Realm WHATEVER = new Realm() {

		@Override
		public boolean isCurrent() {
			return true;
		}
	};

}
