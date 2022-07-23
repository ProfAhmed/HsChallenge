package com.hubstaff.challenge.screen

import android.content.Context
import android.widget.Toast

fun mToast(context: Context?, msg: String?) =
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()