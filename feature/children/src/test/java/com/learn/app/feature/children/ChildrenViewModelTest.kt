package com.learn.app.feature.children

import com.learn.app.core.domain.repository.AuthRepository
import com.learn.app.core.domain.repository.ChildrenRepository
import com.learn.app.core.domain.usecase.CreateChildUseCase
import com.learn.app.core.domain.usecase.DeleteAccountUseCase
import com.learn.app.core.domain.usecase.DeleteChildUseCase
import com.learn.app.core.domain.usecase.GetChildrenUseCase
import com.learn.app.core.domain.usecase.LogoutUseCase
import com.learn.app.core.domain.usecase.UpdateChildUseCase
import com.learn.app.core.model.Child
import com.learn.app.core.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChildrenViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `onSaveChild keeps add dialog open when create fails`() = runTest(dispatcher.scheduler) {
        val repository = FakeChildrenRepository(createError = IllegalStateException("create failed"))
        val viewModel = createViewModel(repository)

        viewModel.onShowAddDialog()
        viewModel.onDialogNameChange("Hanako")
        viewModel.onDialogGradeChange("3年")

        viewModel.onSaveChild()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.showAddDialog)
        assertNull(viewModel.uiState.value.editingChild)
        assertEquals("Hanako", viewModel.uiState.value.dialogName)
        assertEquals("3年", viewModel.uiState.value.dialogGrade)
        assertEquals("追加に失敗しました", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    @Test
    fun `onSaveChild keeps edit dialog open when update fails`() = runTest(dispatcher.scheduler) {
        val existingChild = Child(id = "child-1", name = "Taro", grade = "2年", isActive = true)
        val repository = FakeChildrenRepository(
            initialChildren = listOf(existingChild),
            updateError = IllegalStateException("update failed"),
        )
        val viewModel = createViewModel(repository)
        advanceUntilIdle()

        viewModel.onShowEditDialog(existingChild)
        viewModel.onDialogNameChange("Jiro")
        viewModel.onDialogGradeChange("4年")

        viewModel.onSaveChild()
        advanceUntilIdle()

        assertEquals(existingChild, viewModel.uiState.value.editingChild)
        assertEquals("Jiro", viewModel.uiState.value.dialogName)
        assertEquals("4年", viewModel.uiState.value.dialogGrade)
        assertEquals("更新に失敗しました", viewModel.uiState.value.errorMessage)
        assertFalse(viewModel.uiState.value.showAddDialog)
        assertFalse(viewModel.uiState.value.isSaving)
    }

    private fun createViewModel(repository: ChildrenRepository): ChildrenViewModel {
        val fakeAuth = FakeAuthRepository()
        return ChildrenViewModel(
            getChildrenUseCase = GetChildrenUseCase(repository),
            createChildUseCase = CreateChildUseCase(repository),
            updateChildUseCase = UpdateChildUseCase(repository),
            deleteChildUseCase = DeleteChildUseCase(repository),
            logoutUseCase = LogoutUseCase(fakeAuth),
            deleteAccountUseCase = DeleteAccountUseCase(fakeAuth),
        )
    }

    private class FakeChildrenRepository(
        initialChildren: List<Child> = emptyList(),
        private val createError: Throwable? = null,
        private val updateError: Throwable? = null,
    ) : ChildrenRepository {
        private val children = initialChildren.toMutableList()

        override suspend fun getChildren(): List<Child> = children.toList()

        override suspend fun createChild(name: String, grade: String?): Child {
            createError?.let { throw it }
            return Child(id = "new-child", name = name, grade = grade, isActive = true)
                .also(children::add)
        }

        override suspend fun updateChild(childId: String, name: String, grade: String?): Child {
            updateError?.let { throw it }
            val index = children.indexOfFirst { it.id == childId }
            val updated = children[index].copy(name = name, grade = grade)
            children[index] = updated
            return updated
        }

        override suspend fun patchChild(childId: String, fields: Map<String, Any?>): Child {
            throw UnsupportedOperationException("Not needed in this test")
        }

        override suspend fun deleteChild(childId: String) {
            children.removeAll { it.id == childId }
        }
    }

    private class FakeAuthRepository : AuthRepository {
        override suspend fun login(email: String, password: String): String = ""
        override suspend fun signup(email: String, password: String) {}
        override suspend fun getMe(): User =
            User(id = "", email = "", displayName = null, avatarUrl = null, provider = "")
        override suspend fun logout() {}
        override suspend fun deleteAccount() {}
    }
}
