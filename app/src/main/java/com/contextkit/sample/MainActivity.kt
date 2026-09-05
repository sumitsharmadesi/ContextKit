package com.contextkit.sample

import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.contextkit.core.ContextKit
import com.contextkit.core.ContextResult
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                SmartClipboardScreen(
                    readClipboard = { readClipboard() },
                    analyze = { text, callback ->
                        lifecycleScope.launch {
                            callback(ContextKit.analyzeAsync(text))
                        }
                    }
                )
            }
        }
    }

    private fun readClipboard(): String {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        return clipboard.primaryClip?.getItemAt(0)?.coerceToText(this)?.toString().orEmpty()
    }
}

@Composable
private fun SmartClipboardScreen(
    readClipboard: () -> String,
    analyze: (String, (ContextResult) -> Unit) -> Unit
) {
    var text by remember { mutableStateOf("") }
    var result by remember { mutableStateOf<ContextResult?>(null) }
    var analyzing by remember { mutableStateOf(false) }

    Column(
        Modifier.fillMaxSize().padding(20.dp).verticalScroll(rememberScrollState())
    ) {
        Text("Smart Clipboard", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(6.dp))
        Text("Reference app for the ContextKit SDK.")
        Spacer(Modifier.height(18.dp))

        OutlinedTextField(
            value = text,
            onValueChange = { text = it; result = null },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Text") },
            minLines = 4
        )

        Spacer(Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = { text = readClipboard(); result = null }) {
                Text("Read clipboard")
            }
            Button(
                enabled = text.isNotBlank() && !analyzing,
                onClick = {
                    analyzing = true
                    analyze(text) {
                        result = it
                        analyzing = false
                    }
                }
            ) {
                Text(if (analyzing) "Analyzing..." else "Analyze")
            }
        }

        Spacer(Modifier.height(18.dp))
        result?.let { AnalysisCard(it) }
    }
}

@Composable
private fun AnalysisCard(result: ContextResult) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp)) {
            Text("Category: ${result.category}", style = MaterialTheme.typography.titleMedium)
            Text("Confidence: ${(result.confidence * 100).toInt()}%")
            Text("Engine: ${result.engine}")

            Spacer(Modifier.height(12.dp))
            Text("Entities", style = MaterialTheme.typography.titleSmall)
            if (result.entities.isEmpty()) Text("None")
            result.entities.forEach { Text("• ${it.type}: ${it.value}") }

            Spacer(Modifier.height(12.dp))
            Text("Suggested actions", style = MaterialTheme.typography.titleSmall)
            result.suggestedActions.forEach { Text("• ${it.label}") }
        }
    }
}
