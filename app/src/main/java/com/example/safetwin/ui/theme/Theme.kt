package com.example.safetwin.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val SafeTwinColorScheme = lightColorScheme(
    primary    = Primary,
    onPrimary  = OnPrimary,
    background = Background,
    surface    = Surface,
    error      = DangerRed,
)

@Composable
fun SafeTwinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = SafeTwinColorScheme,
        typography  = Typography,
        content     = content,
    )
}
