package com.example.safetwin.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.safetwin.ui.theme.DangerRed
import com.example.safetwin.ui.theme.SafeGreen
import com.example.safetwin.ui.theme.WarningOrange

enum class BadgeStatus { DANGER, WARNING, SAFE }

@Composable
fun StatusBadge(status: BadgeStatus, label: String) {
    val (bg, fg) = when (status) {
        BadgeStatus.DANGER  -> DangerRed    to Color.White
        BadgeStatus.WARNING -> WarningOrange to Color.White
        BadgeStatus.SAFE    -> SafeGreen    to Color.White
    }
    Box(
        modifier = Modifier
            .background(bg, RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, color = fg, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
