package com.netsoft.android.timer

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

class TimeTickerImp @Inject constructor() : TimeTicker {

    private val _timerState = MutableStateFlow(TimerState(0, false))
    override val timerState: MutableStateFlow<TimerState>
        get() = _timerState

    private var _formattedTime by mutableStateOf("00:00:00")
    override val formattedTime: String
        get() = _formattedTime

    private var coroutineScope = CoroutineScope(Dispatchers.Main)
    private var _isActive = false
    private var timeMillis = 0L
    private var lastTimestamp = 0L


    @RequiresApi(Build.VERSION_CODES.O)
    override fun start() {
        if (_isActive) return

        coroutineScope.launch {
            lastTimestamp = System.currentTimeMillis()
            _timerState.value = TimerState(lastTimestamp, true)
            this@TimeTickerImp._isActive = true
            while (this@TimeTickerImp._isActive) {
                delay(1000L)
                timeMillis += System.currentTimeMillis() - lastTimestamp
                lastTimestamp = System.currentTimeMillis()
                _formattedTime = formatTime(timeMillis)
            }
        }
        return
    }

    override fun stop() {
        _isActive = false
        _timerState.value = TimerState(lastTimestamp, false)
    }

    override fun reset() {
        coroutineScope.cancel()
        coroutineScope = CoroutineScope(Dispatchers.Main)
        timeMillis = 0L
        lastTimestamp = 0L
        _formattedTime = "00:00:00"
        _isActive = false
        _timerState.value = TimerState(0, false)
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun formatTime(timeMillis: Long): String {

        return String.format(
            "%02d:%02d:%02d", java.util.concurrent.TimeUnit.MILLISECONDS.toHours(timeMillis),
            java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(timeMillis) - java.util.concurrent.TimeUnit.HOURS.toMinutes(
                java.util.concurrent.TimeUnit.MILLISECONDS.toHours(
                    timeMillis
                )
            ),
            java.util.concurrent.TimeUnit.MILLISECONDS.toSeconds(timeMillis) - java.util.concurrent.TimeUnit.MINUTES.toSeconds(
                java.util.concurrent.TimeUnit.MILLISECONDS.toMinutes(
                    timeMillis
                )
            )
        )
    }

}

@Module
@InstallIn(SingletonComponent::class)
abstract class TimeTickerProvider {

    @Singleton
    @Binds
    internal abstract fun getTimeTicker(
        impl: TimeTickerImp,
    ): TimeTicker
}