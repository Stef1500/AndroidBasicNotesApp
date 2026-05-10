package com.zybooks.semesterproject.ui.theme.ui

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material.icons.filled.CameraAlt
import android.Manifest



@Composable
fun SemesterScreen(viewModel: SemesterViewModel) {
    val noteText by viewModel.noteText.collectAsState()
    val noteImage by viewModel.noteImage.collectAsState()
    val selectedNote by viewModel.selectedNote.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    var isMenuOpen by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var newNoteName by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = MediaStore.Images.Media.insertImage(
                context.contentResolver,
                it,
                "note_${viewModel.selectedNote.value}",
                null
            )
            uri?.let { viewModel.onImageSelected(it) }
        }
    }

    // Permissions
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(null)
        }
    }

    Box(Modifier.fillMaxSize()) {
        Row(Modifier.fillMaxSize()) {
            // Side menu
            if (isMenuOpen) {
                Column(
                    Modifier
                        .width(180.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp)
                ) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings",
                            modifier = Modifier.clickable { showSettings = true })
                        Icon(Icons.Default.Add, contentDescription = "Add",
                            modifier = Modifier.clickable { showDialog = true })
                    }

                    Spacer(Modifier.height(24.dp))
                    Text("Notes", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(8.dp))

                    notes.forEach { name ->
                        val isSelected = selectedNote == name
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                name,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable {
                                        viewModel.loadNote(name)
                                        isMenuOpen = false
                                    },
                                style = if (isSelected)
                                    MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.primary)
                                else
                                    MaterialTheme.typography.bodyMedium
                            )
                            IconButton(onClick = { viewModel.deleteNote(name) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete $name")
                            }
                        }
                    }
                }
            }

            // Main note editor
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                //top bar
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start
                ) {
                    IconButton(onClick = { isMenuOpen = !isMenuOpen }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = if (isMenuOpen) "Close Menu" else "Open Menu"
                        )
                    }
                    IconButton(onClick = {
                        val hasPermission = ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                        if (hasPermission) {
                            cameraLauncher.launch(null)
                        } else {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    }) {
                        Icon(Icons.Default.CameraAlt, contentDescription = "Take Photo")
                    }
                }

                Spacer(Modifier.height(8.dp))

                BasicTextField(
                    value = noteText,
                    onValueChange = { newText -> viewModel.onNoteChange(newText) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(8.dp),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                noteImage?.let { uri ->
                    Spacer(Modifier.height(16.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Note Image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }

    // create newn notes
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Create New Note") },
            text = {
                TextField(
                    value = newNoteName,
                    onValueChange = { newNoteName = it },
                    label = { Text("Note name") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newNoteName.isNotBlank()) {
                        viewModel.addNote(newNoteName)
                    }
                    showDialog = false
                    newNoteName = ""
                }) {
                    Text("Create")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    newNoteName = ""
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Settings
    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Text("Dark Mode")
                    Spacer(Modifier.height(8.dp))
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { viewModel.toggleDarkMode(it) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Close")
                }
            }
        )
    }
}