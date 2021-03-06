<%--
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
--%>

<%@ include file="/init.jsp" %>

<%
UADHierarchyDisplay uadHierarchyDisplay = (UADHierarchyDisplay)request.getAttribute(UADWebKeys.UAD_HIERARCHY_DISPLAY);
ViewUADEntitiesDisplay viewUADEntitiesDisplay = (ViewUADEntitiesDisplay)request.getAttribute(UADWebKeys.VIEW_UAD_ENTITIES_DISPLAY);

SearchContainer<UADEntity> uadEntitySearchContainer = viewUADEntitiesDisplay.getSearchContainer();

ViewUADEntitiesManagementToolbarDisplayContext viewUADEntitiesManagementToolbarDisplayContext = new ViewUADEntitiesManagementToolbarDisplayContext(liferayPortletRequest, liferayPortletResponse, request, uadEntitySearchContainer);

boolean topLevelView = true;

if (uadHierarchyDisplay != null) {
	topLevelView = false;

	uadHierarchyDisplay.addPortletBreadcrumbEntries(request, renderResponse, locale);
}
%>

<clay:management-toolbar
	displayContext="<%= viewUADEntitiesManagementToolbarDisplayContext %>"
/>

<aui:form method="post" name="viewUADEntitiesFm">
	<aui:input name="redirect" type="hidden" value="<%= currentURL %>" />
	<aui:input name="p_u_i_d" type="hidden" value="<%= String.valueOf(selectedUser.getUserId()) %>" />
	<aui:input name="applicationKey" type="hidden" value="<%= viewUADEntitiesDisplay.getApplicationKey() %>" />
	<aui:input name="uadRegistryKey" type="hidden" value="<%= viewUADEntitiesDisplay.getUADRegistryKey() %>" />

	<%
	for (Class<?> typeClass : viewUADEntitiesDisplay.getTypeClasses()) {
	%>

		<aui:input name='<%= "primaryKeys__" + typeClass.getSimpleName() %>' type="hidden" />
		<aui:input name='<%= "uadRegistryKey__" + typeClass.getSimpleName() %>' type="hidden" value="<%= typeClass.getName() %>" />

	<%
	}
	%>

	<div class="closed container-fluid container-fluid-max-xl sidenav-container sidenav-right" id="<portlet:namespace />infoPanelId">
		<liferay-portlet:resourceURL copyCurrentRenderParameters="<%= true %>" id="/info_panel" var="entityTypeSidebarURL">
			<liferay-portlet:param name="hierarchyView" value="<%= String.valueOf(viewUADEntitiesDisplay.isHierarchy()) %>" />
			<liferay-portlet:param name="topLevelView" value="<%= String.valueOf(topLevelView) %>" />
		</liferay-portlet:resourceURL>

		<div id="breadcrumb">
			<liferay-ui:breadcrumb
				showCurrentGroup="<%= false %>"
				showGuestGroup="<%= false %>"
				showLayout="<%= false %>"
				showPortletBreadcrumb="<%= true %>"
			/>
		</div>

		<liferay-frontend:sidebar-panel
			resourceURL="<%= entityTypeSidebarURL %>"
			searchContainerId="UADEntities"
		>
			<liferay-util:include page="/info_panel.jsp" servletContext="<%= application %>" />
		</liferay-frontend:sidebar-panel>

		<div class="sidenav-content">
			<liferay-ui:search-container
				searchContainer="<%= uadEntitySearchContainer %>"
			>
				<liferay-ui:search-container-row
					className="com.liferay.user.associated.data.web.internal.display.UADEntity"
					escapedModel="<%= true %>"
					keyProperty="primaryKey"
					modelVar="uadEntity"
				>

					<%
					List<KeyValuePair> columnEntries = uadEntity.getColumnEntries();

					String uadEntityHref = uadEntity.getViewURL();

					if (uadEntityHref == null) {
						uadEntityHref = uadEntity.getEditURL();
					}

					boolean showUserIcon = false;

					if ((uadEntity.getViewURL() != null) && !uadEntity.isUserOwned()) {
						showUserIcon = true;
					}

					for (KeyValuePair columnEntry : columnEntries) {
						String columnEntryKey = columnEntry.getKey();

						String cssClass = "table-cell-expand";

						if (columnEntry.equals(columnEntries.get(0))) {
							cssClass = "table-cell-expand table-list-title";
						}
						else if (columnEntryKey.equals("count")) {
							cssClass = "table-cell-expand-smallest";
						}
					%>

						<liferay-ui:search-container-column-text
							cssClass="<%= cssClass %>"
							name="<%= columnEntryKey %>"
						>
							<aui:a href="<%= uadEntityHref %>"><%= StringUtil.shorten(columnEntry.getValue(), 200) %></aui:a>

							<c:if test='<%= columnEntryKey.equals("name") && showUserIcon %>'>
								<liferay-ui:icon
									cssClass="disabled"
									icon="user"
									markupView="lexicon"
									message="this-parent-item-does-not-belong-to-the-user-but-contains-children-items-belonging-to-the-user"
									toolTip="<%= true %>"
								/>
							</c:if>
						</liferay-ui:search-container-column-text>

					<%
					}
					%>

					<liferay-ui:search-container-column-jsp
						cssClass="entry-action-column"
						path="/uad_entity_action.jsp"
					/>
				</liferay-ui:search-container-row>

				<liferay-ui:search-iterator
					markupView="lexicon"
					resultRowSplitter="<%= viewUADEntitiesDisplay.getResultRowSplitter() %>"
				/>
			</liferay-ui:search-container>
		</div>
	</div>
</aui:form>

<aui:script>
	function <portlet:namespace/>doAnonymizeMultiple() {
		<portlet:namespace />doMultiple(
			'<portlet:actionURL name="/anonymize_uad_entities" />',
			'<liferay-ui:message key="are-you-sure-you-want-to-anonymize-the-selected-items" />',
			'<liferay-ui:message key="only-items-belonging-to-the-user-will-be-anonymized" />'
		);
	}

	function <portlet:namespace/>doDeleteMultiple() {
		<portlet:namespace />doMultiple(
			'<portlet:actionURL name="/delete_uad_entities" />',
			'<liferay-ui:message key="are-you-sure-you-want-to-delete-the-selected-items" />',
			'<liferay-ui:message key="only-items-belonging-to-the-user-will-be-deleted" />'
		);
	}

	function <portlet:namespace/>doMultiple(actionURL, message, hierarchyMessage) {
		var userOwnedPrimaryKeys = '<%= viewUADEntitiesDisplay.getUserOwnedEntityPKsString() %>';

		var userOwnedPrimaryKeyArray = userOwnedPrimaryKeys.split(',');

		var form = document.getElementById('<portlet:namespace />viewUADEntitiesFm');

		if (form) {

			<%
			for (Class<?> typeClass : viewUADEntitiesDisplay.getTypeClasses()) {
				String primaryKeysVar = "primaryKeys" + typeClass.getSimpleName();
			%>

				var <%= primaryKeysVar %> = form.querySelector('#<portlet:namespace />primaryKeys__<%= typeClass.getSimpleName() %>');

				if (<%= primaryKeysVar %>) {
					var primaryKeys = Liferay.Util.listCheckedExcept(form, '<portlet:namespace />allRowIds', '<portlet:namespace />rowIds<%= typeClass.getSimpleName() %>');

					<%= primaryKeysVar %>.setAttribute(
						'value',
						primaryKeys
					);

					var primaryKeyArray = primaryKeys.split(',');

					for (var i = 0; i < primaryKeyArray.length; i++) {
						if ((primaryKeyArray[i] != '') && !userOwnedPrimaryKeyArray.includes(primaryKeyArray[i])) {
							message = hierarchyMessage;
						}
					}
				}

			<%
			}
			%>

		}

		<portlet:namespace />confirmAction('viewUADEntitiesFm', actionURL, message);
	}
</aui:script>

<%@ include file="/action/confirm_action_js.jspf" %>