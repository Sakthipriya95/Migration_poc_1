/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.support;

import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryMigrator;
import org.eclipse.mylyn.tasks.core.AbstractTaskListMigrator;
import org.eclipse.mylyn.tasks.core.spi.RepositoryConnectorDescriptor;
import org.eclipse.mylyn.tasks.tests.connector.MockRepositoryConnector;

public class MockRepositoryConnectorDescriptor extends RepositoryConnectorDescriptor {

	public static class DynamicMockRepositoryConnector extends MockRepositoryConnector {

		public final static String CONNECTOR_KIND = MockRepositoryConnector.CONNECTOR_KIND + ".dynamic";

		@Override
		public String getConnectorKind() {
			return CONNECTOR_KIND;
		}

		@Override
		public String getLabel() {
			return super.getLabel() + " (contributed at runtime)";
		}
	};

	public MockRepositoryConnectorDescriptor() {
	}

	@Override
	public AbstractRepositoryConnector createRepositoryConnector() {
		return new DynamicMockRepositoryConnector();
	}

	@Override
	public AbstractTaskListMigrator createTaskListMigrator() {
		return null;
	}

	@Override
	public AbstractRepositoryMigrator createRepositoryMigrator() {
		return null;
	}

}
