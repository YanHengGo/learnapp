package com.learn.app.feature.children

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.learn.app.core.model.Child
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChildrenScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // ─── ヘルパー ───────────────────────────────────────────────

    private fun setContent(uiState: ChildrenUiState) {
        composeTestRule.setContent {
            ChildrenContent(
                uiState = uiState,
                onChildSelected = {},
                onLoggedOut = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onDeleteChild = {},
                onShowLogoutConfirm = {},
                onDismissLogoutConfirm = {},
                onLogout = {},
                onDismissDialog = {},
                onNameChange = {},
                onGradeChange = {},
                onSaveChild = {},
                onErrorDismiss = {},
                onLoadChildren = {},
            )
        }
    }

    // ─── ローディング ────────────────────────────────────────────

    @Test
    fun loading_showsProgressIndicator() {
        setContent(ChildrenUiState(isLoading = true))
        composeTestRule.onNodeWithContentDescription("子どもを追加").assertIsDisplayed()
        // CircularProgressIndicator はセマンティクスがないため isLoading=true 時に
        // 子ども未登録テキストが表示されないことで確認
        composeTestRule.onNodeWithText("子どもが登録されていません").assertDoesNotExist()
    }

    // ─── エラー状態 ──────────────────────────────────────────────

    @Test
    fun loadError_showsErrorMessageAndReloadButton() {
        setContent(ChildrenUiState(isLoadError = true, children = emptyList()))
        composeTestRule.onNodeWithText("データの取得に失敗しました").assertIsDisplayed()
        composeTestRule.onNodeWithText("再読み込み").assertIsDisplayed()
    }

    @Test
    fun loadError_reloadButton_callsOnLoadChildren() {
        var reloadCalled = false
        composeTestRule.setContent {
            ChildrenContent(
                uiState = ChildrenUiState(isLoadError = true, children = emptyList()),
                onChildSelected = {},
                onLoggedOut = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onDeleteChild = {},
                onShowLogoutConfirm = {},
                onDismissLogoutConfirm = {},
                onLogout = {},
                onDismissDialog = {},
                onNameChange = {},
                onGradeChange = {},
                onSaveChild = {},
                onErrorDismiss = {},
                onLoadChildren = { reloadCalled = true },
            )
        }
        composeTestRule.onNodeWithText("再読み込み").performClick()
        assertTrue(reloadCalled)
    }

    // ─── 空リスト ────────────────────────────────────────────────

    @Test
    fun emptyChildren_showsEmptyMessage() {
        setContent(ChildrenUiState(children = emptyList()))
        composeTestRule.onNodeWithText("子どもが登録されていません").assertIsDisplayed()
        composeTestRule.onNodeWithText("右下のボタンから追加してください").assertIsDisplayed()
    }

    @Test
    fun emptyChildren_doesNotShowErrorMessage() {
        setContent(ChildrenUiState(children = emptyList()))
        composeTestRule.onNodeWithText("データの取得に失敗しました").assertDoesNotExist()
    }

    // ─── 子ども一覧 ──────────────────────────────────────────────

    @Test
    fun childrenList_showsChildNames() {
        val children = listOf(
            Child(id = "1", name = "たろう", grade = "1年", isActive = true),
            Child(id = "2", name = "はなこ", grade = "3年", isActive = true),
        )
        setContent(ChildrenUiState(children = children))
        composeTestRule.onNodeWithText("たろう").assertIsDisplayed()
        composeTestRule.onNodeWithText("はなこ").assertIsDisplayed()
    }

    @Test
    fun childrenList_showsGrade() {
        val children = listOf(
            Child(id = "1", name = "たろう", grade = "2年", isActive = true),
        )
        setContent(ChildrenUiState(children = children))
        composeTestRule.onNodeWithText("2年").assertIsDisplayed()
    }

    @Test
    fun childrenList_childClick_callsOnChildSelected() {
        var selectedId = ""
        val child = Child(id = "child-1", name = "たろう", grade = null, isActive = true)
        composeTestRule.setContent {
            ChildrenContent(
                uiState = ChildrenUiState(children = listOf(child)),
                onChildSelected = { selectedId = it },
                onLoggedOut = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onDeleteChild = {},
                onShowLogoutConfirm = {},
                onDismissLogoutConfirm = {},
                onLogout = {},
                onDismissDialog = {},
                onNameChange = {},
                onGradeChange = {},
                onSaveChild = {},
                onErrorDismiss = {},
                onLoadChildren = {},
            )
        }
        composeTestRule.onNodeWithText("たろう").performClick()
        assertTrue(selectedId == "child-1")
    }

    // ─── 追加ダイアログ ──────────────────────────────────────────

    @Test
    fun fabClick_callsOnShowAddDialog() {
        var called = false
        composeTestRule.setContent {
            ChildrenContent(
                uiState = ChildrenUiState(),
                onChildSelected = {},
                onLoggedOut = {},
                onShowAddDialog = { called = true },
                onShowEditDialog = {},
                onDeleteChild = {},
                onShowLogoutConfirm = {},
                onDismissLogoutConfirm = {},
                onLogout = {},
                onDismissDialog = {},
                onNameChange = {},
                onGradeChange = {},
                onSaveChild = {},
                onErrorDismiss = {},
                onLoadChildren = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("子どもを追加").performClick()
        assertTrue(called)
    }

    @Test
    fun addDialog_showsTitleAndFields() {
        setContent(ChildrenUiState(showAddDialog = true))
        composeTestRule.onNodeWithText("子どもを追加").assertIsDisplayed()
        composeTestRule.onNodeWithText("名前").assertIsDisplayed()
        composeTestRule.onNodeWithText("学年（任意）").assertIsDisplayed()
    }

    @Test
    fun addDialog_saveButton_disabledWhenNameEmpty() {
        setContent(ChildrenUiState(showAddDialog = true, dialogName = ""))
        composeTestRule.onNodeWithText("保存").assertIsNotEnabled()
    }

    @Test
    fun addDialog_saveButton_enabledWhenNameFilled() {
        setContent(ChildrenUiState(showAddDialog = true, dialogName = "たろう"))
        composeTestRule.onNodeWithText("保存").assertIsEnabled()
    }

    // ─── ログアウトダイアログ ────────────────────────────────────

    @Test
    fun logoutIcon_click_callsOnShowLogoutConfirm() {
        var called = false
        composeTestRule.setContent {
            ChildrenContent(
                uiState = ChildrenUiState(),
                onChildSelected = {},
                onLoggedOut = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onDeleteChild = {},
                onShowLogoutConfirm = { called = true },
                onDismissLogoutConfirm = {},
                onLogout = {},
                onDismissDialog = {},
                onNameChange = {},
                onGradeChange = {},
                onSaveChild = {},
                onErrorDismiss = {},
                onLoadChildren = {},
            )
        }
        composeTestRule.onNodeWithContentDescription("ログアウト").performClick()
        assertTrue(called)
    }

    @Test
    fun logoutDialog_showsWhenFlagIsTrue() {
        setContent(ChildrenUiState(showLogoutConfirm = true))
        composeTestRule.onNodeWithText("ログアウトしますか？").assertIsDisplayed()
        composeTestRule.onNodeWithText("キャンセル").assertIsDisplayed()
    }

    @Test
    fun logoutDialog_confirmClick_callsOnLogout() {
        var called = false
        composeTestRule.setContent {
            ChildrenContent(
                uiState = ChildrenUiState(showLogoutConfirm = true),
                onChildSelected = {},
                onLoggedOut = {},
                onShowAddDialog = {},
                onShowEditDialog = {},
                onDeleteChild = {},
                onShowLogoutConfirm = {},
                onDismissLogoutConfirm = {},
                onLogout = { called = true },
                onDismissDialog = {},
                onNameChange = {},
                onGradeChange = {},
                onSaveChild = {},
                onErrorDismiss = {},
                onLoadChildren = {},
            )
        }
        composeTestRule.onNodeWithText("ログアウト").performClick()
        assertTrue(called)
    }
}
