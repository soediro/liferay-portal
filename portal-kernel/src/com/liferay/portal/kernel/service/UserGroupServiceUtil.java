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

package com.liferay.portal.kernel.service;

import aQute.bnd.annotation.ProviderType;

import com.liferay.portal.kernel.bean.PortalBeanLocatorUtil;
import com.liferay.portal.kernel.util.ReferenceRegistry;

/**
 * Provides the remote service utility for UserGroup. This utility wraps
 * <code>com.liferay.portal.service.impl.UserGroupServiceImpl</code> and is an
 * access point for service operations in application layer code running on a
 * remote server. Methods of this service are expected to have security checks
 * based on the propagated JAAS credentials because this service can be
 * accessed remotely.
 *
 * @author Brian Wing Shun Chan
 * @see UserGroupService
 * @generated
 */
@ProviderType
public class UserGroupServiceUtil {

	/*
	 * NOTE FOR DEVELOPERS:
	 *
	 * Never modify this class directly. Add custom service methods to <code>com.liferay.portal.service.impl.UserGroupServiceImpl</code> and rerun ServiceBuilder to regenerate this class.
	 */

	/**
	 * Adds the user groups to the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	public static void addGroupUserGroups(long groupId, long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().addGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * Adds the user groups to the team
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	public static void addTeamUserGroups(long teamId, long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().addTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * Adds a user group.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user group,
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
	 * @param name the user group's name
	 * @param description the user group's description
	 * @return the user group
	 * @deprecated As of Newton (6.2.x), replaced by {@link
	 #addUserGroup(String, String, ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.model.UserGroup addUserGroup(
			String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addUserGroup(name, description);
	}

	/**
	 * Adds a user group.
	 *
	 * <p>
	 * This method handles the creation and bookkeeping of the user group,
	 * including its resources, metadata, and internal data structures.
	 * </p>
	 *
	 * @param name the user group's name
	 * @param description the user group's description
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 user group.
	 * @return the user group
	 */
	public static com.liferay.portal.kernel.model.UserGroup addUserGroup(
			String name, String description, ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().addUserGroup(name, description, serviceContext);
	}

	/**
	 * Deletes the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 */
	public static void deleteUserGroup(long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().deleteUserGroup(userGroupId);
	}

	/**
	 * Fetches the user group with the primary key.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group with the primary key
	 */
	public static com.liferay.portal.kernel.model.UserGroup fetchUserGroup(
			long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().fetchUserGroup(userGroupId);
	}

	public static java.util.List<com.liferay.portal.kernel.model.UserGroup>
		getGtUserGroups(
			long gtUserGroupId, long companyId, long parentUserGroupId,
			int size) {

		return getService().getGtUserGroups(
			gtUserGroupId, companyId, parentUserGroupId, size);
	}

	/**
	 * Returns the OSGi service identifier.
	 *
	 * @return the OSGi service identifier
	 */
	public static String getOSGiServiceIdentifier() {
		return getService().getOSGiServiceIdentifier();
	}

	/**
	 * Returns the user group with the primary key.
	 *
	 * @param userGroupId the primary key of the user group
	 * @return the user group with the primary key
	 */
	public static com.liferay.portal.kernel.model.UserGroup getUserGroup(
			long userGroupId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getUserGroup(userGroupId);
	}

	/**
	 * Returns the user group with the name.
	 *
	 * @param name the user group's name
	 * @return the user group with the name
	 */
	public static com.liferay.portal.kernel.model.UserGroup getUserGroup(
			String name)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getUserGroup(name);
	}

	public static java.util.List<com.liferay.portal.kernel.model.UserGroup>
			getUserGroups(long companyId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getUserGroups(companyId);
	}

	public static java.util.List<com.liferay.portal.kernel.model.UserGroup>
		getUserGroups(long companyId, String name, int start, int end) {

		return getService().getUserGroups(companyId, name, start, end);
	}

	public static int getUserGroupsCount(long companyId, String name) {
		return getService().getUserGroupsCount(companyId, name);
	}

	/**
	 * Returns all the user groups to which the user belongs.
	 *
	 * @param userId the primary key of the user
	 * @return the user groups to which the user belongs
	 */
	public static java.util.List<com.liferay.portal.kernel.model.UserGroup>
			getUserUserGroups(long userId)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().getUserUserGroups(userId);
	}

	/**
	 * Removes the user groups from the group.
	 *
	 * @param groupId the primary key of the group
	 * @param userGroupIds the primary keys of the user groups
	 */
	public static void unsetGroupUserGroups(long groupId, long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unsetGroupUserGroups(groupId, userGroupIds);
	}

	/**
	 * Removes the user groups from the team.
	 *
	 * @param teamId the primary key of the team
	 * @param userGroupIds the primary keys of the user groups
	 */
	public static void unsetTeamUserGroups(long teamId, long[] userGroupIds)
		throws com.liferay.portal.kernel.exception.PortalException {

		getService().unsetTeamUserGroups(teamId, userGroupIds);
	}

	/**
	 * Updates the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param name the user group's name
	 * @param description the the user group's description
	 * @return the user group
	 * @deprecated As of Newton (6.2.x), replaced by {@link
	 #updateUserGroup(long, String, String, ServiceContext)}
	 */
	@Deprecated
	public static com.liferay.portal.kernel.model.UserGroup updateUserGroup(
			long userGroupId, String name, String description)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateUserGroup(userGroupId, name, description);
	}

	/**
	 * Updates the user group.
	 *
	 * @param userGroupId the primary key of the user group
	 * @param name the user group's name
	 * @param description the the user group's description
	 * @param serviceContext the service context to be applied (optionally
	 <code>null</code>). Can set expando bridge attributes for the
	 user group.
	 * @return the user group
	 */
	public static com.liferay.portal.kernel.model.UserGroup updateUserGroup(
			long userGroupId, String name, String description,
			ServiceContext serviceContext)
		throws com.liferay.portal.kernel.exception.PortalException {

		return getService().updateUserGroup(
			userGroupId, name, description, serviceContext);
	}

	public static UserGroupService getService() {
		if (_service == null) {
			_service = (UserGroupService)PortalBeanLocatorUtil.locate(
				UserGroupService.class.getName());

			ReferenceRegistry.registerReference(
				UserGroupServiceUtil.class, "_service");
		}

		return _service;
	}

	private static UserGroupService _service;

}