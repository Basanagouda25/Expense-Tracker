package com.example.allinone.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.allinone.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    navController: NavController
){
    val viewModel : AuthViewModel = viewModel()
    // remember is used because compose will remember this value after every recomposition
    // mutableStateOf is used to observe the compose if it is changed the values in ui automatically changes
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val message by viewModel.message.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    LaunchedEffect(isLoggedIn) {
        if(isLoggedIn){
            navController.navigate(Screen.Home.route){
                popUpTo(Screen.Login.route){inclusive = true}
            }
        }
    }

    // now the ui for jetpack compose
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Login Screen",
            fontSize = 28.sp,
            fontFamily = FontFamily.SansSerif
        )
        Spacer(modifier = Modifier.height(20.dp))

        //input email feild
        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Enter Email")}, //  this is like a placeholder
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {Text("Enter password")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick ={
                viewModel.login(email,password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = message)
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            onClick = {
                navController.navigate(Screen.Register.route)
            }
        ) {
            Text("Don't have account? register")
        }
    }
}
/*
@Preview
@Composable
fun loginPreview(){
    login()
}*/