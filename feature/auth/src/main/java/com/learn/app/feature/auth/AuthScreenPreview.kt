package com.learn.app.feature.auth

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, name = "ログイン画面")
@Composable
private fun PreviewLogin() {
    MaterialTheme {
        AuthContent(
            uiState = AuthUiState(mode = AuthMode.LOGIN),
            onEmailChange = {},
            onPasswordChange = {},
            onModeToggle = {},
            onSubmit = {},
        )
    }
}

@Preview(showBackground = true, name = "新規登録画面")
@Composable
private fun PreviewSignup() {
    MaterialTheme {
        AuthContent(
            uiState = AuthUiState(mode = AuthMode.SIGNUP),
            onEmailChange = {},
            onPasswordChange = {},
            onModeToggle = {},
            onSubmit = {},
        )
    }
}

@Preview(showBackground = true, name = "ローディング中")
@Composable
private fun PreviewLoading() {
    MaterialTheme {
        AuthContent(
            uiState = AuthUiState(isLoading = true),
            onEmailChange = {},
            onPasswordChange = {},
            onModeToggle = {},
            onSubmit = {},
        )
    }
}

@Preview(showBackground = true, name = "エラー表示")
@Composable
private fun PreviewError() {
    MaterialTheme {
        AuthContent(
            uiState = AuthUiState(
                errorMessage = "メールアドレスまたはパスワードが正しくありません",
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onModeToggle = {},
            onSubmit = {},
        )
    }
}

@Preview(showBackground = true, name = "プライバシーポリシー")
@Composable
private fun PreviewPrivacyPolicy() {
    MaterialTheme {
        PrivacyPolicyScreen(onBack = {})
    }
}
