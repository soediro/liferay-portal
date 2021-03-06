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

package com.liferay.data.engine.rest.internal.resource.v1_0;

import com.liferay.data.engine.rest.dto.v1_0.DataDefinition;
import com.liferay.data.engine.rest.dto.v1_0.DataDefinitionField;
import com.liferay.data.engine.rest.dto.v1_0.DataDefinitionRule;
import com.liferay.data.engine.rest.dto.v1_0.DataRecord;
import com.liferay.data.engine.rest.internal.dto.v1_0.util.DataDefinitionUtil;
import com.liferay.data.engine.rest.internal.dto.v1_0.util.DataRecordValueUtil;
import com.liferay.data.engine.rest.internal.rule.function.v1_0.DataRuleFunction;
import com.liferay.data.engine.rest.internal.rule.function.v1_0.DataRuleFunctionFactory;
import com.liferay.data.engine.rest.internal.rule.function.v1_0.DataRuleFunctionResult;
import com.liferay.data.engine.rest.internal.storage.DataStorage;
import com.liferay.data.engine.rest.resource.v1_0.DataRecordResource;
import com.liferay.dynamic.data.lists.model.DDLRecord;
import com.liferay.dynamic.data.lists.model.DDLRecordSet;
import com.liferay.dynamic.data.lists.model.DDLRecordSetVersion;
import com.liferay.dynamic.data.lists.service.DDLRecordLocalService;
import com.liferay.dynamic.data.lists.service.DDLRecordService;
import com.liferay.dynamic.data.lists.service.DDLRecordSetLocalService;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.model.DDMStructureVersion;
import com.liferay.dynamic.data.mapping.service.DDMContentLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStorageLinkLocalService;
import com.liferay.dynamic.data.mapping.service.DDMStructureService;
import com.liferay.portal.kernel.security.auth.PrincipalThreadLocal;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.vulcan.pagination.Page;
import com.liferay.portal.vulcan.pagination.Pagination;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.BadRequestException;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ServiceScope;

/**
 * @author Jeyvison Nascimento
 */
@Component(
	properties = "OSGI-INF/liferay/rest/v1_0/data-record.properties",
	scope = ServiceScope.PROTOTYPE, service = DataRecordResource.class
)
public class DataRecordResourceImpl extends BaseDataRecordResourceImpl {

	@Activate
	public void activate() {
		_dataStorage = new DataStorage(
			_ddlRecordSetLocalService, _ddmContentLocalService,
			_ddmStructureService);
	}

	@Override
	public boolean deleteDataRecord(Long dataRecordId) throws Exception {
		_dataStorage.delete(dataRecordId);

		_ddlRecordLocalService.deleteDDLRecord(dataRecordId);

		return true;
	}

	@Override
	public DataRecord getDataRecord(Long dataRecordId) throws Exception {
		return _toDataRecord(_ddlRecordLocalService.getDDLRecord(dataRecordId));
	}

	@Override
	public Page<DataRecord> getDataRecordCollectionDataRecordsPage(
			Long dataRecordCollectionId, Pagination pagination)
		throws Exception {

		return Page.of(
			transform(
				_ddlRecordLocalService.getRecords(
					dataRecordCollectionId, pagination.getStartPosition(),
					pagination.getEndPosition(), null),
				this::_toDataRecord),
			pagination,
			_ddlRecordLocalService.getRecordsCount(
				dataRecordCollectionId, PrincipalThreadLocal.getUserId()));
	}

	@Override
	public DataRecord postDataRecordCollectionDataRecord(
			Long dataRecordCollectionId, DataRecord dataRecord)
		throws Exception {

		DDLRecordSet ddlRecordSet = _ddlRecordSetLocalService.getRecordSet(
			dataRecordCollectionId);

		_validate(
			DataDefinitionUtil.toDataDefinition(ddlRecordSet.getDDMStructure()),
			dataRecord);

		return _toDataRecord(
			_ddlRecordLocalService.addRecord(
				PrincipalThreadLocal.getUserId(), ddlRecordSet.getGroupId(),
				_dataStorage.save(ddlRecordSet.getGroupId(), dataRecord),
				dataRecord.getDataRecordCollectionId(), new ServiceContext()));
	}

	@Override
	public DataRecord putDataRecord(Long dataRecordId, DataRecord dataRecord)
		throws Exception {

		DDLRecord ddlRecord = _ddlRecordService.getRecord(dataRecordId);

		DDLRecordSet ddlRecordSet = ddlRecord.getRecordSet();

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		_validate(
			DataDefinitionUtil.toDataDefinition(ddmStructure), dataRecord);

		long ddmStorageId = _dataStorage.save(
			ddlRecord.getGroupId(), dataRecord);

		_ddlRecordLocalService.updateRecord(
			PrincipalThreadLocal.getUserId(), dataRecordId, ddmStorageId,
			new ServiceContext());

		DDLRecordSetVersion ddlRecordSetVersion =
			ddlRecordSet.getRecordSetVersion();

		DDMStructureVersion ddmStructureVersion =
			ddlRecordSetVersion.getDDMStructureVersion();

		_ddmStorageLinkLocalService.addStorageLink(
			_portal.getClassNameId(DataRecord.class.getName()), ddmStorageId,
			ddmStructureVersion.getStructureVersionId(), new ServiceContext());

		return dataRecord;
	}

	private DataRecord _toDataRecord(DDLRecord ddlRecord) throws Exception {
		DDLRecordSet ddlRecordSet = ddlRecord.getRecordSet();

		DDMStructure ddmStructure = ddlRecordSet.getDDMStructure();

		return new DataRecord() {
			{
				dataRecordCollectionId = ddlRecordSet.getRecordSetId();
				dataRecordValues = _dataStorage.get(
					ddmStructure.getStructureId(), ddlRecord.getDDMStorageId());
				id = ddlRecord.getRecordId();
			}
		};
	}

	private void _validate(DataDefinition dataDefinition, DataRecord dataRecord)
		throws Exception {

		// Field names

		Set<String> dataDefinitionFieldNames = Stream.of(
			dataDefinition.getDataDefinitionFields()
		).map(
			dataDefinitionField -> dataDefinitionField.getName()
		).collect(
			Collectors.toSet()
		);

		Map<String, Object> dataRecordValuesMap = DataRecordValueUtil.toMap(
			dataRecord.getDataRecordValues());

		Set<String> fieldNames = dataRecordValuesMap.keySet();

		List<String> missingFieldNames = fieldNames.stream(
		).filter(
			fieldName -> !dataDefinitionFieldNames.contains(fieldName)
		).collect(
			Collectors.toList()
		);

		if (!missingFieldNames.isEmpty()) {
			throw new BadRequestException(
				"Missing fields: " +
					ArrayUtil.toStringArray(missingFieldNames));
		}

		// Field values

		List<DataDefinitionRule> dataDefinitionRules = Stream.of(
			dataDefinition.getDataDefinitionRules()
		).filter(
			dataDefinitionRule -> Objects.equals(
				dataDefinitionRule.getRuleType(), "validation")
		).collect(
			Collectors.toList()
		);

		if (dataDefinitionRules.isEmpty()) {
			return;
		}

		Map<String, DataDefinitionField> dataDefinitionFields = Stream.of(
			dataDefinition.getDataDefinitionFields()
		).collect(
			Collectors.toMap(DataDefinitionField::getName, Function.identity())
		);

		Map<String, Set<String>> errorCodesMap = new HashMap<>();

		for (DataDefinitionRule dataDefinitionRule : dataDefinitionRules) {
			DataRuleFunction dataRuleFunction =
				DataRuleFunctionFactory.getDataRuleFunction(
					dataDefinitionRule.getName());

			if (dataRuleFunction == null) {
				continue;
			}

			for (String dataDefinitionFieldName :
					dataDefinitionRule.getDataDefinitionFieldNames()) {

				DataDefinitionField dataDefinitionField =
					dataDefinitionFields.get(dataDefinitionFieldName);

				DataRuleFunctionResult dataRuleFunctionResult =
					dataRuleFunction.validate(
						dataDefinitionField,
						dataDefinitionRule.getDataDefinitionRuleParameters(),
						DataRecordValueUtil.getDataDefinitionFieldValue(
							dataDefinitionField,
							dataRecord.getDataRecordValues()));

				if (dataRuleFunctionResult.isValid()) {
					continue;
				}

				Set<String> errorCodes = errorCodesMap.getOrDefault(
					dataDefinitionFieldName, new HashSet<>());

				errorCodes.add(dataRuleFunctionResult.getErrorCode());

				errorCodesMap.put(dataDefinitionFieldName, errorCodes);
			}
		}

		if (!errorCodesMap.isEmpty()) {
			throw new BadRequestException(errorCodesMap.toString());
		}
	}

	private DataStorage _dataStorage;

	@Reference
	private DDLRecordLocalService _ddlRecordLocalService;

	@Reference
	private DDLRecordService _ddlRecordService;

	@Reference
	private DDLRecordSetLocalService _ddlRecordSetLocalService;

	@Reference
	private DDMContentLocalService _ddmContentLocalService;

	@Reference
	private DDMStorageLinkLocalService _ddmStorageLinkLocalService;

	@Reference
	private DDMStructureService _ddmStructureService;

	@Reference
	private Portal _portal;

}