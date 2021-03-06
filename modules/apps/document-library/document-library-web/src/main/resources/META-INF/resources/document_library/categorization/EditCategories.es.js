import 'clay-multi-select';
import 'clay-radio';
import Component from 'metal-component';
import {Config} from 'metal-state';
import Soy from 'metal-soy';
import 'frontend-js-web/liferay/compat/modal/Modal.es';
import 'asset-taglib/asset_categories_selector/AssetCategoriesSelector.es';
import templates from './EditCategories.soy';

/**
 * Handles the categories of the selected
 * fileEntries inside a modal.
 */
class EditCategories extends Component {

	/**
	 * @inheritDoc
	 */
	attached() {
		this._bulkStatusComponent =	Liferay.component(this.namespace + 'BulkStatus');
	}

	/**
	 * Close the modal.
	 */
	close() {
		this.showModal = false;
	}

	/**
	 * @inheritDoc
	 */
	created() {
		this.append = true;
		this.dataSource = [];

		this._feedbackErrorClass = 'form-feedback-item';
		this._requiredVocabularyErrorMarkupText = '<div class="' + this._feedbackErrorClass + '">' + Liferay.Language.get('this-field-is-required') + '</div>';
	}

	/**
	 * Open the modal and get the
	 * commont categories.
	 */
	open(fileEntries, selectAll, folderId) {
		this.fileEntries = fileEntries;
		this.selectAll = selectAll;
		this.folderId = folderId;
		this.showModal = true;

		this._getCommonCategories();
	}

	/**
	 * Checks if the vocabulary is empty or not.
	 *
	 * @param  {String} vocabularyId
	 * @return {Boolean} true if it has a category, false if is empty.
	 */
	_checkRequiredVocabulary(vocabularyId) {
		let inputNode = this._getVocabularyInputNode(vocabularyId);
		let valid = true;

		if (inputNode.value) {
			inputNode.parentElement.classList.remove('has-error');
		}
		else {
			inputNode.parentElement.classList.add('has-error');

			let feedbackErrorNode = inputNode.parentElement.querySelector('.' + this._feedbackErrorClass);

			if (!feedbackErrorNode) {
				inputNode.parentElement.insertAdjacentHTML(
					'beforeend',
					this._requiredVocabularyErrorMarkupText
				);
			}

			valid = false;
		}

		return valid;
	}

	/**
	 * Creates the ajax request.
	 *
	 * @param {String} url Url of the request
	 * @param {Object} bodyData The body of the request
	 * @param {Function} callback Callback function
	 */
	_fetchCategoriesRequest(url, bodyData, callback) {
		let body = JSON.stringify(bodyData);

		let headers = new Headers();
		headers.append('Content-Type', 'application/json');

		const request = {
			body,
			credentials: 'include',
			headers,
			method: 'POST'
		};

		fetch(url, request)
			.then(
				response => response.json()
			)
			.then(
				response => {
					callback(response);
				}
			)
			.catch(
				(xhr) => {
					this.close();
				}
			);
	}

	/**
	 * Gets the common categories for the selected
	 * file entries and updates the state.
	 *
	 * @private
	 * @review
	 */
	_getCommonCategories() {
		this.loading = true;

		let bodyData = {
			folderId: this.folderId,
			repositoryId: this.repositoryId,
			selectAll: this.selectAll,
			selection: this.fileEntries
		};

		this._fetchCategoriesRequest(
			this.urlCategories,
			bodyData,
			response => {
				if (response) {
					this.description = response.description;
					this.groupIds = response.groupIds;
					this.loading = false;
					this.multiple = (this.fileEntries.length > 1) || this.selectAll;
					this.vocabularies = this._parseVocabularies(response.vocabularies);
				}
			}
		);
	}

	/**
	 * Get all the categoryIds selected for all
	 * the vocabularies.
	 *
	 * @return {List<Long>} List of categoryIds.
	 */
	_getFinalCategories() {
		let finalCategories = [];
		let inputElementName = this.namespace + this.hiddenInput;

		this.vocabularies.forEach(
			vocabulary => {
				let inputNode = document.getElementById(inputElementName + vocabulary.id);

				if (inputNode.value) {
					let categoryIds = inputNode.value.split(',').map(Number);
					finalCategories = finalCategories.concat(categoryIds);
				}
			}
		);

		return finalCategories;
	}

	/**
	 * Gets the input where categories are saved for a vocabulary.
	 *
	 * @param  {String} vocabularyId [description]
	 * @return {DOMElement} input node.
	 */
	_getVocabularyInputNode(vocabularyId) {
		return document.getElementById(this.namespace + this.hiddenInput + vocabularyId);
	}

	/**
	 * Checks if a required vocabulary has categories or not.
	 *
	 * @param  {Event} event
	 */
	_handleCategoriesChange(event) {
		let vocabularyId = event.vocabularyId[0];

		if (this._requiredVocabularies.includes(parseInt(vocabularyId, 10))) {
			this._checkRequiredVocabulary(vocabularyId);
		}
	}

	/**
	 * Sync the input radio with the state
	 * @param {!Event} event
	 * @private
	 * @review
	 */
	_handleRadioChange(event) {
		this.append = event.target.value === 'add';
	}

	/**
	 * Sends request to backend services
	 * to update the categories.
	 *
	 * @private
	 * @review
	 */
	_handleSaveBtnClick() {
		if (!this._validateRequiredVocabularies()) {
			return;
		}

		let finalCategories = this._getFinalCategories();

		let addedCategories = [];

		if (!this.append) {
			addedCategories = finalCategories;
		}
		else {
			addedCategories = finalCategories.filter(
				categoryId => this.initialCategories.indexOf(categoryId) == -1
			);
		}

		let removedCategories = this.initialCategories.filter(
			category => finalCategories.indexOf(category) == -1
		);

		let bodyData = {
			append: this.append,
			folderId: this.folderId,
			repositoryId: this.repositoryId,
			selectAll: this.selectAll,
			selection: this.fileEntries,
			toAddCategoryIds: addedCategories,
			toRemoveCategoryIds: removedCategories
		};

		let instance = this;

		this._fetchCategoriesRequest(
			this.urlUpdateCategories,
			bodyData,
			response => {
				instance.close();

				if (instance._bulkStatusComponent) {
					instance._bulkStatusComponent.startWatch();
				}
			}
		);
	}

	_parseVocabularies(vocabularies) {
		let initialCategories = [];
		let requiredVocabularies = [];
		let vocabulariesList = [];

		vocabularies.forEach(
			vocabulary => {
				let categories = this._parseCategories(vocabulary.categories);

				let categoryIds = categories.map(item => item.value);

				let obj = {
					id: vocabulary.vocabularyId.toString(),
					required: vocabulary.required,
					selectedCategoryIds: categoryIds.join(','),
					selectedItems: categories,
					singleSelect: !vocabulary.multiValued,
					title: vocabulary.name
				};

				vocabulariesList.push(obj);

				if (vocabulary.required) {
					requiredVocabularies.push(vocabulary.vocabularyId);
				}

				initialCategories = initialCategories.concat(categoryIds);
			}
		);

		this.initialCategories = initialCategories;
		this._requiredVocabularies = requiredVocabularies;

		return vocabulariesList;
	}

	/**
	 * Transforms the categories list in the object needed
	 * for the ClayMultiSelect component.
	 *
	 * @param {List<Long, String>} categories
	 * @return {List<{label, value}>} new commonItems object list
	 */
	_parseCategories(categories) {
		let categoriesObjList = [];

		if (categories.length > 0) {
			categories.forEach(
				item => {
					let itemObj = {
						'label': item.name,
						'value': item.categoryId
					};

					categoriesObjList.push(itemObj);
				}
			);
		}

		return categoriesObjList;
	}

	_validateRequiredVocabularies() {
		let requiredVocabularies = this._requiredVocabularies;
		let valid = true;

		if (requiredVocabularies) {
			requiredVocabularies.forEach(
				vocabularyId => {
					if (!this._checkRequiredVocabulary(vocabularyId)) {
						valid = false;
					}
				}
			);
		}

		return valid;
	}
}

/**
 * State definition.
 * @ignore
 * @static
 * @type {!Object}
 */
EditCategories.STATE = {

	/**
	 * Description
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {String}
	 */
	description: Config.string(),

	/**
	 * List of selected file entries.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {List<String>}
	 */
	fileEntries: Config.array(),

	/**
	 * Folder Id
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {String}
	 */
	folderId: Config.string(),

	/**
	 * Group Ids.
	 *
	 * @type {List<String>}
	 */
	groupIds: Config.array().value([]),

	/**
	* Hidden input name
	*
	* @type {String}
	 */
	hiddenInput: Config.string().value('assetCategoryIds_').internal(),

	/**
	 * Original categoryIds
	 *
	 * @type {List<Long>}
	 */
	initialCategories: Config.array().internal(),

	/**
	 * Flag that indicate if loading icon must
	 * be shown.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {Boolean}
	 */
	loading: Config.bool().value(false).internal(),

	/**
	 * Flag that indicate if multiple
	 * file entries has been selected.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {Boolean}
	 */
	multiple: Config.bool().value(false),

	/**
	 * Portlet's namespace
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {string}
	 */
	namespace: Config.string().required(),

	/**
	 * RepositoryId
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {String}
	 */
	repositoryId: Config.string().required(),

	/**
	 * Flag that indicate if "select all" checkbox
	 * is checked.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {Boolean}
	 */
	selectAll: Config.bool(),

	/**
	 * Url to the categories selector page
	 * @type {String}
	 */
	selectCategoriesUrl: Config.string().required(),

	/**
	 * Flag that indicate if the modal must
	 * be shown.
	 *
	 * @instance
	 * @memberof EditTags
	 * @review
	 * @type {Boolean}
	 */
	showModal: Config.bool().value(false).internal(),

	/**
	 * Path to images.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {String}
	 */
	spritemap: Config.string().required(),

	/**
	 * Url to backend service that provides
	 * the common categories info.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {String}
	 */
	urlCategories: Config.string().required(),

	/**
	 * Url to backend service that updates
	 * the categories.
	 *
	 * @instance
	 * @memberof EditCategories
	 * @review
	 * @type {String}
	 */
	urlUpdateCategories: Config.string().required(),

	/**
	 * List of vocabularies
	 *
	 * @type {Array}
	 */
	vocabularies: Config.array().value([])
};

// Register component

Soy.register(EditCategories, templates);

export default EditCategories;