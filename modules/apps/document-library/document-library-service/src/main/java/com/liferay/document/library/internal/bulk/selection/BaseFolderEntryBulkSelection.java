/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.document.library.internal.bulk.selection;

import com.liferay.bulk.selection.BaseContainerEntryBulkSelection;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.repository.DocumentRepository;
import com.liferay.portal.kernel.repository.RepositoryProvider;
import com.liferay.portal.kernel.repository.capabilities.BulkOperationCapability;
import com.liferay.portal.kernel.repository.model.RepositoryModel;
import com.liferay.portal.kernel.repository.model.RepositoryModelOperation;
import com.liferay.portal.kernel.util.ResourceBundleLoader;

import java.util.Map;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseFolderEntryBulkSelection<T extends RepositoryModel<T>>
	extends BaseContainerEntryBulkSelection<T> {

	public BaseFolderEntryBulkSelection(
		long repositoryId, long folderId, Map<String, String[]> parameterMap,
		ResourceBundleLoader resourceBundleLoader, Language language,
		RepositoryProvider repositoryProvider) {

		super(folderId, parameterMap, resourceBundleLoader, language);

		_repositoryId = repositoryId;
		_folderId = folderId;
		_repositoryProvider = repositoryProvider;
	}

	@Override
	public Stream<T> stream() throws PortalException {
		DocumentRepository documentRepository =
			_repositoryProvider.getLocalRepository(_repositoryId);

		if (!documentRepository.isCapabilityProvided(
				BulkOperationCapability.class)) {

			return Stream.empty();
		}

		BulkOperationCapability bulkOperationCapability =
			documentRepository.getCapability(BulkOperationCapability.class);

		BulkOperationCapability.Filter<Long> bulkFilter =
			new BulkOperationCapability.Filter<>(
				BulkOperationCapability.Field.FolderId.class,
				BulkOperationCapability.Operator.EQ, _folderId);

		return StreamSupport.stream(
			new Spliterator<T>() {

				@Override
				public int characteristics() {
					return 0;
				}

				@Override
				public long estimateSize() {
					return Long.MAX_VALUE;
				}

				@Override
				public void forEachRemaining(Consumer<? super T> action) {
					_exhausted = true;

					try {
						bulkOperationCapability.execute(
							bulkFilter, getRepositoryModelOperation(action));
					}
					catch (PortalException pe) {
						ReflectionUtil.throwException(pe);
					}
				}

				@Override
				public boolean tryAdvance(Consumer<? super T> consumer) {
					if (_exhausted) {
						return false;
					}

					forEachRemaining(consumer);

					return true;
				}

				@Override
				public Spliterator<T> trySplit() {
					return null;
				}

				private boolean _exhausted;

			},
			false);
	}

	protected abstract RepositoryModelOperation getRepositoryModelOperation(
		Consumer<? super T> action);

	private final long _folderId;
	private final long _repositoryId;
	private final RepositoryProvider _repositoryProvider;

}