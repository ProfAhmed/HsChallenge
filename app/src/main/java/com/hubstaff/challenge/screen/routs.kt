package com.hubstaff.challenge.screen

sealed class Routes(val route: String) {
    object SignIn : Routes("SignIn")
    object Timer : Routes("Timer")
}