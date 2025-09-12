package com.example.clase7

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.ui.unit.dp




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuccessScreen(navController: NavController){
    Scaffold (
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = {navController.popBackStack()}){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription= "icon register"
                        )
                    }
                }
            )
        }
    ){ innerPadding ->
        Box(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement =Arrangement.Center
        )
        {
        Image(
            painter = painterResource(R.drawable.umglogo),
            contentDescription = null,
            modifier = Modifier
                .size(120.dp),
            contentScale = ContentScale.Crop
        )
            Text(
                text = stringResource(R.string.login_screen_success),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0066B3)
            )
        }
    }
}