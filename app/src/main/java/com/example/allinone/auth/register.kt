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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.allinone.navigation.Screen
import com.google.firebase.auth.FirebaseAuth


@Composable
fun Register(
    navController: NavController
){
    val viewModel : AuthViewModel = viewModel()

    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    val isRegistered by viewModel.isRegistered.collectAsState()

    LaunchedEffect(isRegistered) {
        if(isRegistered){
            navController.navigate(Screen.Login.route){
                popUpTo(Screen.Register.route){inclusive = true}
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),

        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Register Screen",
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = name,
            onValueChange = {name = it},
            label = {Text("Enter your name")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = {phoneNumber = it},
            label = {Text("Enter your Number")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {email = it},
            label = {Text("Enter your email")},
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = password,
            onValueChange = {password = it},
            label = {Text("Enter your password")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                viewModel.register(name,phoneNumber,email,password)
            }
        ) {
            Text("Register")
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(text = message)
        Spacer(modifier = Modifier.height(20.dp))
        TextButton(
            onClick = {
                navController.navigate(Screen.Login.route)
            }
        ) {
            Text("Already have account? Login")
        }
    }
}