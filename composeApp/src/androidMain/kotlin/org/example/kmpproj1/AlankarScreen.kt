package com.example.alankargenerator

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Context
import android.media.MediaPlayer
import androidx.compose.ui.platform.LocalContext
import org.example.kmpproj1.R
import kotlinx.coroutines.*
import java.net.URLEncoder

@Composable
fun AlankarScreen() {
    val notes = listOf("सा", "रे", "ग", "म", "प", "ध", "नि", "सां", "रें", "गं")
    var tempo by remember { mutableStateOf(120) }

    // Access Android Context
    val context = LocalContext.current

    // Scrollable container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "🎵 Alankar Generator 🎵", style = MaterialTheme.typography.h4)

        Spacer(modifier = Modifier.height(16.dp))

        // आरोह Section
        var generatedAaroh by remember { mutableStateOf<List<List<String>>>(emptyList()) }
        AarohSection(notes, generatedAaroh) { pattern -> generatedAaroh = pattern }

        Spacer(modifier = Modifier.height(32.dp))

        // अवरोह Section
        var generatedAvroh by remember { mutableStateOf<List<List<String>>>(emptyList()) }
        AvrohSection(notes, generatedAvroh) { pattern -> generatedAvroh = pattern }

        Spacer(modifier = Modifier.height(16.dp))

        // Tempo control
        Text(text = "Set Tempo: $tempo BPM", style = MaterialTheme.typography.h6)
        Slider(
            value = tempo.toFloat(),
            onValueChange = { tempo = it.toInt() },
            valueRange = 40f..200f,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Play आरोह Button
        Button(
            onClick = {
                playPattern(context, generatedAaroh, tempo)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play आरोह")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Play अवरोह Button
        Button(
            onClick = {
                playPattern(context, generatedAvroh, tempo)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Play अवरोह")
        }
    }
}

fun playPattern(context: Context, pattern: List<List<String>>, tempo: Int) {
    val noteToFileMap = mapOf(
        "सा" to R.raw.sa,
        "रे" to R.raw.re,
        "ग" to R.raw.ga,
        "म" to R.raw.ma,
        "प" to R.raw.pa,
        "ध" to R.raw.dh,
        "नि" to R.raw.ni,
        "सां" to R.raw.saan,
        "रें" to R.raw.ren,
        "गं" to R.raw.gan
    )

    val delayMillis = (60000 / tempo).toLong() // Calculate delay based on tempo (milliseconds per beat)

    // Use a CoroutineScope to play the pattern asynchronously
    CoroutineScope(Dispatchers.Main).launch {
        pattern.flatten().forEach { note ->
            val fileResId = noteToFileMap[note]
            if (fileResId != null) {
                val mediaPlayer = MediaPlayer.create(context, fileResId)
                mediaPlayer?.apply {
                    start()
                    setOnCompletionListener { release() }
                }
                delay(delayMillis) // Non-blocking delay for the duration of one note
            }
        }
    }
}
// AarohSection and AvrohSection are passed a callback for generated patterns
@Composable
fun AarohSection(notes: List<String>, generatedPattern: List<List<String>>, onPatternGenerated: (List<List<String>>) -> Unit) {
    var selectedNotes by remember { mutableStateOf(mutableListOf<String>()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Select Notes for आरोह", style = MaterialTheme.typography.h6)

        NoteSelectionGrid(notes, selectedNotes) {
            selectedNotes.add(it) // Add note to the list
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display selected notes as dynamically typed text
        Text(
            text = "Selected आरोह Notes: ${selectedNotes.joinToString(" ")}",
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val pattern = AlankarGenerator.generatePattern(selectedNotes, isReverse = false)
            onPatternGenerated(pattern)
        }) {
            Text("Generate आरोह")
        }

        DisplayGeneratedPattern(generatedPattern)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { selectedNotes.clear() }) {
                Text("Clear All Notes")
            }
            Button(onClick = {
                if (selectedNotes.isNotEmpty()) {
                    selectedNotes.removeLast()
                }
            }) {
                Text("Clear Last Note")
            }
        }
    }
}

@Composable
fun AvrohSection(notes: List<String>, generatedPattern: List<List<String>>, onPatternGenerated: (List<List<String>>) -> Unit) {
    var selectedNotes by remember { mutableStateOf(mutableListOf<String>()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "Select Notes for अवरोह", style = MaterialTheme.typography.h6)

        NoteSelectionGrid(notes, selectedNotes) {
            selectedNotes.add(it) // Add note to the list
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Display selected notes as dynamically typed text
        Text(
            text = "Selected अवरोह Notes: ${selectedNotes.joinToString(" ")}",
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            val pattern = AlankarGenerator.generatePattern(selectedNotes, isReverse = true)
            onPatternGenerated(pattern)
        }) {
            Text("Generate अवरोह")
        }

        DisplayGeneratedPattern(generatedPattern)

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(onClick = { selectedNotes.clear() }) {
                Text("Clear All Notes")
            }
            Button(onClick = {
                if (selectedNotes.isNotEmpty()) {
                    selectedNotes.removeLast()
                }
            }) {
                Text("Clear Last Note")
            }
        }
    }
}

@Composable
fun NoteSelectionGrid(
    notes: List<String>,
    selectedNotes: MutableList<String>,
    onNoteClick: (String) -> Unit
) {
    val notesPerRow = 4
    val rows = notes.chunked(notesPerRow)

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        rows.forEach { rowNotes ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowNotes.forEach { note ->
                    Button(
                        onClick = { onNoteClick(note) },
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = MaterialTheme.colors.surface
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(note)
                    }
                }
                // Add placeholders for incomplete rows
                repeat(notesPerRow - rowNotes.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun DisplayGeneratedPattern(pattern: List<List<String>>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        pattern.forEach { subList ->
            Text(subList.joinToString(" "), style = MaterialTheme.typography.body1)
        }
    }
}
