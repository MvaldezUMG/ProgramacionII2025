package com.example.clase7

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.clase7.data.UsersRepository
import com.example.clase7.data.view_models.FileUploadViewModel
import com.example.clase7.data.view_models.UploadState
import com.example.clase7.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth

fun getFileNameFromUri(context: android.content.Context, uri: Uri): String? {
    return when (uri.scheme) {
        "content" -> {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val displayNameIndex = cursor.getColumnIndex("_display_name")
                    if (displayNameIndex != -1) {
                        cursor.getString(displayNameIndex)
                    } else {
                        "file_${System.currentTimeMillis()}"
                    }
                } else {
                    "file_${System.currentTimeMillis()}"
                }
            }
        }
        "file" -> uri.lastPathSegment
        else -> "file_${System.currentTimeMillis()}"
    }
}

@Composable
fun UsersFormScreen(navController: NavController){

    val roles = listOf("coordinator", "teacher")
    val auth = Firebase.auth

    val context = LocalContext.current

    var stateEmail by remember {mutableStateOf("")}
    var stateRoles by remember {mutableStateOf("")}

    var stateMessage by remember {mutableStateOf("")}

    var selectedOptions = remember {mutableStateListOf<String>()}

    val repository = UsersRepository(context.resources)

    val viewModel = viewModel<FileUploadViewModel>()

    val uploadState by viewModel.uploadState.collectAsState()
    val uploadedFiles by viewModel.uploadedFiles.collectAsState()


    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // Obtener nombre del archivo desde el URI
            val fileName = getFileNameFromUri(context, uri)
            viewModel.uploadFile(context, uri, fileName)
        }
    }


    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement =Arrangement.Center
    ){


        Spacer(modifier = Modifier.height(10.dp))
        Text(stringResource(R.string.user_screen_new_user))
        OutlinedTextField(
            value = stateEmail,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = stringResource(R.string.content_description_icon_email)
                )
            },
            onValueChange = {stateEmail = it},
            label = {Text(stringResource(R.string.fields_email))},
//            supportingText = {
//                if (emailMessage.isNotEmpty()){
//                    Text(
//                        text=emailMessage,
//                        color=Color.Red
//                    )
//                }
//            }
        )
        Spacer(modifier = Modifier.height(10.dp))

        Column {
            roles.forEach{ option ->
                Row(
                   Modifier.fillMaxWidth()
                       .selectable(
                           selected = selectedOptions.contains(option),
                           onClick = {
                               if (selectedOptions.contains(option)) {
                                   selectedOptions.remove(option)
                               } else {
                                   selectedOptions.add(option)
                               }
                           }
                       ).padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Checkbox(
                        checked = selectedOptions.contains(option),
                        onCheckedChange = null // null recommended for accessibility with selectable modifier
                    )
                    Text(
                        text = option,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                filePickerLauncher.launch("*/*")
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC9252B),
                contentColor = Color.White
            )
        ){
            Text(stringResource(R.string.user_form_screen_upload))
        }

        when (val state = uploadState) {
            is UploadState.Loading -> {
                CircularProgressIndicator()
                Text("Subiendo archivo...")
            }
            is UploadState.Success -> {
                Text(
                    text = "Archivo subido exitosamente: ${state.file.name}",
                    color = MaterialTheme.colorScheme.primary
                )

                LaunchedEffect(state) {
                    kotlinx.coroutines.delay(2000)
                    viewModel.resetState()
                }
            }
            is UploadState.Error -> {
                Text(
                    text = "Error: ${state.message}",
                    color = MaterialTheme.colorScheme.error
                )

                Button(onClick = { viewModel.resetState() }) {
                    Text("Reintentar")
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = {
                val roles = selectedOptions.joinToString(",")
                val user = User("", stateEmail, roles)
                repository.saveUser(user, navController)

            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFC9252B),
                contentColor = Color.White
            )
        ){
            Text(stringResource(R.string.user_form_screen_save))
        }

        IconButton(onClick = {navController.popBackStack()}){
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription= stringResource(R.string.content_description_icon_exit)
            )
        }

        Text(
            text = stateMessage
        )
    }
}