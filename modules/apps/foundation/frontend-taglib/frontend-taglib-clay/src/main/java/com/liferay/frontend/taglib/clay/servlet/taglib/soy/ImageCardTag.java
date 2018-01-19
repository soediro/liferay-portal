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

package com.liferay.frontend.taglib.clay.servlet.taglib.soy;

import com.liferay.frontend.taglib.clay.servlet.taglib.soy.base.BaseClayTag;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.util.WebKeys;

import java.util.Map;

/**
 * @author Julien Castelain
 */
public class ImageCardTag extends BaseClayTag {

	public ImageCardTag() {
		super("card", "ClayImageCard", true);
	}

	@Override
	public int doStartTag() {
		Map<String, Object> context = getContext();

		if (Validator.isNull(context.get("spritemap"))) {
			ThemeDisplay themeDisplay = (ThemeDisplay)request.getAttribute(
				WebKeys.THEME_DISPLAY);

			putValue(
				"spritemap",
				themeDisplay.getPathThemeImages().concat("/clay/icons.svg"));
		}

		return super.doStartTag();
	}

	public void setActionItems(Object actionItems) {
		putValue("actionItems", actionItems);
	}

	public void setDisabled(Boolean disabled) {
		putValue("disabled", disabled);
	}

	public void setElementClasses(String elementClasses) {
		putValue("elementClasses", elementClasses);
	}

	public void setFileType(String fileType) {
		putValue("fileType", fileType);
	}

	public void setFileTypeStyle(String fileTypeStyle) {
		putValue("fileTypeStyle", fileTypeStyle);
	}

	public void setGroupName(String groupName) {
		putValue("groupName", groupName);
	}

	public void setHref(String href) {
		putValue("href", href);
	}

	public void setIcon(String icon) {
		putValue("icon", icon);
	}

	public void setId(String id) {
		putValue("id", id);
	}

	public void setImageAlt(String imageAlt) {
		putValue("imageAlt", imageAlt);
	}

	public void setImageSrc(String imageSrc) {
		putValue("imageSrc", imageSrc);
	}

	public void setInputName(String inputName) {
		putValue("inputName", inputName);
	}

	public void setInputValue(String inputValue) {
		putValue("inputValue", inputValue);
	}

	public void setLabels(Object labels) {
		putValue("labels", labels);
	}

	public void setLabelStylesMap(Object labelStylesMap) {
		putValue("labelStylesMap", labelStylesMap);
	}

	public void setSelectable(Boolean selectable) {
		putValue("selectable", selectable);
	}

	public void setSelected(Boolean selected) {
		putValue("selected", selected);
	}

	public void setSpritemap(String spritemap) {
		putValue("spritemap", spritemap);
	}

	public void setSubtitle(String subtitle) {
		putValue("subtitle", subtitle);
	}

	public void setTitle(String title) {
		putValue("title", title);
	}

}