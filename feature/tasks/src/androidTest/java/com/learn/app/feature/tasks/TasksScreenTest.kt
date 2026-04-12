package com.learn.app.feature.tasks

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learn.app.core.model.Task
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TasksScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── ヘルパー ───────────────────────────────────────────────

    private fun setContent(uiState: TasksUiState) {
        composeTestRule.setContent {
            TasksContent(
                uiState = uiState,
                onBack = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onArchiveTask = {},
                onNameChange = {},
                onDescriptionChange = {},
                onSubjectChange = {},
                onMinutesChange = {},
                onDayToggle = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onSaveTask = {},
                onDismissDialog = {},
                onErrorDismiss = {},
            )
        }
    }

    private fun makeTask(
        id: String = "1",
        name: String = "算数ドリル",
        subject: String = "算数",
        minutes: Int = 30,
        daysMask: Int = 0b0111110,
        startDate: String? = null,
        endDate: String? = null,
    ) = Task(
        id = id,
        name = name,
        description = null,
        subject = subject,
        defaultMinutes = minutes,
        daysMask = daysMask,
        isArchived = false,
        startDate = startDate,
        endDate = endDate,
    )

    // ─── ローディング ────────────────────────────────────────────

    @Test
    fun loading_doesNotShowEmptyMessage() {
        setContent(TasksUiState(isLoading = true))
        composeTestRule.onNodeWithText("タスクが登録されていません", substring = true).assertDoesNotExist()
    }

    // ─── 空リスト ────────────────────────────────────────────────

    @Test
    fun emptyTasks_showsEmptyMessage() {
        setContent(TasksUiState(tasks = emptyList()))
        composeTestRule.onNodeWithText("タスクが登録されていません", substring = true).assertIsDisplayed()
    }

    // ─── タスク一覧 ──────────────────────────────────────────────

    @Test
    fun taskList_showsTaskName() {
        setContent(TasksUiState(tasks = listOf(makeTask(name = "算数ドリル"))))
        composeTestRule.onNodeWithText("算数ドリル").assertIsDisplayed()
    }

    @Test
    fun taskList_showsSubjectAndMinutes() {
        // タスク名と教科名が被らないよう別の値を使用
        setContent(TasksUiState(tasks = listOf(makeTask(name = "ドリル", subject = "国語", minutes = 45))))
        composeTestRule.onNodeWithText("国語", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("45分", substring = true).assertIsDisplayed()
    }

    @Test
    fun taskList_showsDateRange_whenSet() {
        val task = makeTask(startDate = "2025-04-01", endDate = "2025-06-30")
        setContent(TasksUiState(tasks = listOf(task)))
        composeTestRule.onNodeWithText("2025-04-01", substring = true).assertIsDisplayed()
        composeTestRule.onNodeWithText("2025-06-30", substring = true).assertIsDisplayed()
    }

    @Test
    fun taskList_doesNotShowDate_whenNotSet() {
        val task = makeTask(startDate = null, endDate = null)
        setContent(TasksUiState(tasks = listOf(task)))
        composeTestRule.onNodeWithText("〜").assertDoesNotExist()
    }

    @Test
    fun taskList_editButton_callsOnShowEditDialog() {
        var called = false
        val task = makeTask()
        composeTestRule.setContent {
            TasksContent(
                uiState = TasksUiState(tasks = listOf(task)),
                onBack = {},
                onShowAddDialog = {},
                onShowEditDialog = { called = true },
                onArchiveTask = {},
                onNameChange = {},
                onDescriptionChange = {},
                onSubjectChange = {},
                onMinutesChange = {},
                onDayToggle = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onSaveTask = {},
                onDismissDialog = {},
                onErrorDismiss = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("編集").performClick()
        assertTrue(called)
    }

    @Test
    fun taskList_archiveButton_callsOnArchiveTask() {
        var called = false
        val task = makeTask()
        composeTestRule.setContent {
            TasksContent(
                uiState = TasksUiState(tasks = listOf(task)),
                onBack = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onArchiveTask = { called = true },
                onNameChange = {},
                onDescriptionChange = {},
                onSubjectChange = {},
                onMinutesChange = {},
                onDayToggle = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onSaveTask = {},
                onDismissDialog = {},
                onErrorDismiss = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("アーカイブ").performClick()
        assertTrue(called)
    }

    // ─── FAB ────────────────────────────────────────────────────

    @Test
    fun fabClick_callsOnShowAddDialog() {
        var called = false
        composeTestRule.setContent {
            TasksContent(
                uiState = TasksUiState(),
                onBack = {},
                onShowAddDialog = { called = true },
                onShowEditDialog = {},
                onArchiveTask = {},
                onNameChange = {},
                onDescriptionChange = {},
                onSubjectChange = {},
                onMinutesChange = {},
                onDayToggle = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onSaveTask = {},
                onDismissDialog = {},
                onErrorDismiss = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("タスクを追加").performClick()
        assertTrue(called)
    }

    // ─── 戻るボタン ──────────────────────────────────────────────

    @Test
    fun backButton_callsOnBack() {
        var called = false
        composeTestRule.setContent {
            TasksContent(
                uiState = TasksUiState(),
                onBack = { called = true },
                onShowAddDialog = {},
                onShowEditDialog = {},
                onArchiveTask = {},
                onNameChange = {},
                onDescriptionChange = {},
                onSubjectChange = {},
                onMinutesChange = {},
                onDayToggle = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onSaveTask = {},
                onDismissDialog = {},
                onErrorDismiss = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("戻る").performClick()
        assertTrue(called)
    }

    // ─── 追加ダイアログ ──────────────────────────────────────────

    @Test
    fun addDialog_showsAddTitle() {
        setContent(TasksUiState(showDialog = true, editingTask = null))
        composeTestRule.onNodeWithText("タスクを追加").assertIsDisplayed()
    }

    @Test
    fun addDialog_showsFields() {
        setContent(TasksUiState(showDialog = true))
        composeTestRule.onNodeWithText("タスク名").assertIsDisplayed()
        composeTestRule.onNodeWithText("教科").assertIsDisplayed()
        composeTestRule.onNodeWithText("標準時間（分）").assertIsDisplayed()
    }

    @Test
    fun addDialog_showsDayChips() {
        setContent(TasksUiState(showDialog = true))
        // 横スクロール可能なため、ノードの存在確認（表示領域外の曜日も含む）
        DAY_LABELS.forEach { label ->
            composeTestRule.onNodeWithText(label).assertExists()
        }
    }

    @Test
    fun addDialog_saveButton_disabledWhenNameEmpty() {
        setContent(TasksUiState(showDialog = true, dialogName = "", dialogSubject = "算数", dialogMinutes = "30"))
        composeTestRule.onNodeWithText("保存").assertIsNotEnabled()
    }

    @Test
    fun addDialog_saveButton_disabledWhenSubjectEmpty() {
        setContent(TasksUiState(showDialog = true, dialogName = "算数ドリル", dialogSubject = "", dialogMinutes = "30"))
        composeTestRule.onNodeWithText("保存").assertIsNotEnabled()
    }

    @Test
    fun addDialog_saveButton_disabledWhenMinutesInvalid() {
        setContent(TasksUiState(showDialog = true, dialogName = "算数ドリル", dialogSubject = "算数", dialogMinutes = "0"))
        composeTestRule.onNodeWithText("保存").assertIsNotEnabled()
    }

    @Test
    fun addDialog_saveButton_enabledWhenValid() {
        setContent(TasksUiState(showDialog = true, dialogName = "算数ドリル", dialogSubject = "算数", dialogMinutes = "30"))
        composeTestRule.onNodeWithText("保存").assertIsEnabled()
    }

    // ─── 編集ダイアログ ──────────────────────────────────────────

    @Test
    fun editDialog_showsEditTitle() {
        setContent(TasksUiState(showDialog = true, editingTask = makeTask(), dialogName = "算数ドリル", dialogSubject = "算数", dialogMinutes = "30"))
        composeTestRule.onNodeWithText("タスクを編集").assertIsDisplayed()
    }

    // ─── キャンセルボタン ─────────────────────────────────────────

    @Test
    fun dialog_cancelButton_callsOnDismissDialog() {
        var called = false
        composeTestRule.setContent {
            TasksContent(
                uiState = TasksUiState(showDialog = true),
                onBack = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onArchiveTask = {},
                onNameChange = {},
                onDescriptionChange = {},
                onSubjectChange = {},
                onMinutesChange = {},
                onDayToggle = {},
                onStartDateChange = {},
                onEndDateChange = {},
                onSaveTask = {},
                onDismissDialog = { called = true },
                onErrorDismiss = {},
            )
        }
        composeTestRule.onNode(hasText("キャンセル") and hasClickAction()).performClick()
        assertTrue(called)
    }
}
