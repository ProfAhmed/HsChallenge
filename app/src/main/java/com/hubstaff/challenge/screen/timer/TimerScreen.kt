package com.hubstaff.challenge.screen.timer

import StopWatchDisplay
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.hubstaff.challenge.component.CustomTopAppBar
import com.netsoft.android.timer.TimeTicker
import kotlinx.coroutines.flow.MutableStateFlow

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TimerScreen(navController: NavHostController, timeTicker: TimeTicker) {
    Box(modifier = Modifier.fillMaxSize()) {
        ScaffoldWithTopBar(navController, timeTicker)
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ScaffoldWithTopBar(navController: NavHostController, timeTicker: TimeTicker) {
    Scaffold(
        topBar = {
            CustomTopAppBar(navController, "Timer", true)
        }, content = {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                val stopWatch = remember { timeTicker }
                StopWatchDisplay(
                    formattedTime = stopWatch.formattedTime,
                    onStartClick = stopWatch::start,
                    onStopClick = stopWatch::stop,
                    isActive = (stopWatch.timerState as MutableStateFlow).value.isRunning,
                    onResetClick = stopWatch::reset
                )
            }

        })
}