package com.hubstaff.challenge.screen

import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hubstaff.challenge.IS_LOGIN
import com.hubstaff.challenge.R
import com.hubstaff.challenge.screen.sign_in.SignInScreen
import com.hubstaff.challenge.screen.sign_in.SignInViewModel
import com.hubstaff.challenge.screen.timer.TimerScreen
import com.hubstaff.theme.HubstaffAppTheme
import com.netsoft.android.timer.TimeTicker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences // to save login state when app killed
    private lateinit var editor: SharedPreferences.Editor   //for big apps we handle it in separate class
    private val viewModel: SignInViewModel by viewModels()

    @Inject
    lateinit var timeTicker: TimeTicker

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            initPrefs()
            HubstaffAppTheme {
                Surface(color = MaterialTheme.colors.background) {
                    Main(viewModel)
                }
            }
        }
    }

    private fun initPrefs() {
        sharedPreferences = getSharedPreferences(getString(R.string.app_name), MODE_PRIVATE)
        editor = sharedPreferences.edit();
    }

    private fun observers(navController: NavHostController) {
        viewModel.state
            .flowWithLifecycle(lifecycle)
            .onEach { state -> loginSuccess(state, navController) }
            .launchIn(lifecycleScope)

        viewModel.errorMsg
            .flowWithLifecycle(lifecycle)
            .onEach { msg ->
                if (msg.isNullOrBlank().not() && !viewModel.state.value) failedLogin(
                    msg
                )
            }
            .launchIn(lifecycleScope)
    }

    private fun navigateToTimer(navController: NavHostController) {
        // remove sign in screen
        navController.popBackStack()
        navController.navigate(Routes.Timer.route)
    }

    private fun loginSuccess(state: Boolean, navController: NavHostController) {
        if (state) {
            Timber.d("LoginResult: Success")
            navigateToTimer(navController)
            editor.putBoolean(IS_LOGIN, true).commit()
        }
    }

    private fun failedLogin(msg: String?) {
        Timber.d("LoginResult: $msg")
        mToast(this, msg = msg)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun Main(signInViewModel: SignInViewModel) {
        Box(modifier = Modifier.fillMaxSize()) {
            // check user signed in before or not
            val isLogin = sharedPreferences.getBoolean(IS_LOGIN, false)
            val navController = rememberNavController()
            val startDest = if (!isLogin) Routes.SignIn.route else Routes.Timer.route

            NavHost(navController = navController, startDestination = startDest) {

                observers(navController)

                if (!isLogin)
                    composable(Routes.SignIn.route) {
                        SignInScreen(signInViewModel)
                    }

                composable(Routes.Timer.route) {
                    TimerScreen(navController = navController, timeTicker)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Preview
    @Composable
    fun DefaultPreview(
        @PreviewParameter(SignViewModelProvider::class) signInViewModel: SignInViewModel
    ) {
        HubstaffAppTheme {
            Main(signInViewModel)
        }
    }
}

@ExperimentalCoroutinesApi
class SignViewModelProvider constructor(override val values: Sequence<SignInViewModel>) :
    PreviewParameterProvider<SignInViewModel>
