package de.nielsfalk.formdsl.app.presentation

import de.nielsfalk.bson.util.ObjectId
import de.nielsfalk.formdsl.dsl.Form
import de.nielsfalk.formdsl.misc.FormData
import de.nielsfalk.formdsl.misc.FormDataValue
import de.nielsfalk.formdsl.misc.FormsListItem

data class FormsState(
    val availableForms: List<FormsListItem> = emptyList(),
    val selectedForm: SelectedState? = null,
    val loading: Boolean = false,
    val saving: Boolean = false,
    val unsavedData: UnsavedData?=null
)

data class UnsavedData(
    val formId: ObjectId,
    val data: FormData,
    val dataId: String? = null
)

data class SelectedState(
    val form: Form,
    val data: FormData = FormData(emptyMap()),
    val dataId: String? = null
)

fun FormsState.copy(values: Map<String, FormDataValue>): FormsState {
    val selected = selectedForm?: return this
    return this.copy(
        unsavedData = UnsavedData(
            selected.form.id,
            selected.data.copy(values = values),
            selected.dataId
        ),
        selectedForm = selected.copy(
            data = selected.data.copy(values = values)
        )
    )
}

fun FormsState.successfulUpdated(justSaved: UnsavedData, updatedVersion: Long) =
    copy(
        saving = false,
        unsavedData = if (unsavedData == justSaved) null else unsavedData?.let { unsavedData ->
            unsavedData.copy(
                data = unsavedData.data.copy(
                    version = updatedVersion
                )
            )
        },
        selectedForm = selectedForm?.let { selected ->
            selected.copy(
                data = selected.data.copy(
                    version = updatedVersion
                )
            )
        }
    )

fun FormsState.successfulCreated(justSaved: UnsavedData, dataId: String) =
    copy(
        saving = false,
        unsavedData = if (unsavedData == justSaved) null else unsavedData?.copy(dataId = dataId),
        selectedForm = selectedForm?.copy(dataId = dataId)
    )

fun FormsState.saving() = copy(saving = true)