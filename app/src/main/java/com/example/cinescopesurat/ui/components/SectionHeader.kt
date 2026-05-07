package com.example.cinescopesurat.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SectionHeader(tag: String, title: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = 24.dp, vertical = 24.dp)) {
        Text(
            tag.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp
        )
        Text(
            title,
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Black,
            color = MaterialTheme.colorScheme.onBackground,
            letterSpacing = (-1).sp
        )
    }
}
