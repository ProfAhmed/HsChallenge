package com.hubstaff.challenge.screen.sign_in

import android.graphics.Movie
import android.os.StatFs
import androidx.lifecycle.*
import com.netsoft.android.authentication.AuthenticationManager
import com.netsoft.android.authentication.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SignInViewModel @Inject constructor(
    private val authManager: AuthenticationManager,
) : ViewModel() {
    private val _state = MutableStateFlow(false)
    val state: StateFlow<Boolean>
        get() = _state.asStateFlow()

    private val _errorMsg = MutableStateFlow<String?>(" ")
    val errorMsg: SharedFlow<String?>
        get() = _errorMsg.asStateFlow()

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            val loginResult = authManager.login(userName, password)
            _state.value = authManager.loginState.value
            if (!loginResult.success)
                _errorMsg.value = loginResult.error
        }
    }
}