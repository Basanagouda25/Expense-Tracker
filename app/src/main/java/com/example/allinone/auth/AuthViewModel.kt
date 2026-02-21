package com.example.allinone.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.allinone.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


// we use viewModel because it survives configuration changes like if we rotate out mobile the ui will not destroy
class AuthViewModel : ViewModel(){
    private val repository = AuthRepository()

    // _message is private because only viewmodel can handle it
    private val _message = MutableStateFlow("")
    val message : StateFlow<String> = _message

    //for going to home screen after correct login use this
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn : StateFlow<Boolean> = _isLoggedIn

    fun login(
        email: String,
        password: String
    ){
        viewModelScope.launch {                             // here viewmodel will connect to coroutine. if viewmodel crashes coroutines will cancle
            val result = repository.login(email,password)   // no callback here this will suspend until firebase finishes

            result.onSuccess{
                _message.value = it
                _isLoggedIn.value = true
            }

            result.onFailure{
                _message.value = it.message?:"Login Error"
            }
        }
    }

    private val _isRegistered = MutableStateFlow(false)
    val isRegistered : StateFlow<Boolean> = _isRegistered
    fun register(
        name: String,
        phoneNumber : String,
        email: String,
        password: String
    ){
        viewModelScope.launch {
            val result = repository.register(name,phoneNumber,email,password)

            result.onSuccess{
                _message.value = it
                _isRegistered.value = true
            }

            result.onFailure{
                _message.value = it.message?:"registration Error"
            }
        }
    }
}