package com.netsoft.android.timer

import kotlinx.coroutines.flow.Flow

/**
 * interface for ticker, provides the ways to start/stop ticking and subscribe for updates
 */
sealed interface TimeTicker {
    /**
     * shares current timer state
     */
    val timerState: Flow<TimerState>

    /**
     * format timeMillis to HH:MM:SS
     */
    val formattedTime: String

    /**
     * starts ticker with period
     * @return true if started and false if can't start, because ticker is already running
     */
    fun start()

    /**
     * stops ticker if it is running, else does nothing
     */
    fun stop()

    /**
     * reset timer to 00:00:00
     */
    fun reset()

}

data class TimerState(val value: Long, val isRunning: Boolean)