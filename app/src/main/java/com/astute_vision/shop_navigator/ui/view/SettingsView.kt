package com.astute_vision.shop_navigator.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import com.astute_vision.shop_navigator.MainActivity
import com.astute_vision.shop_navigator.navigation.AVNavigationGraphDestination
import com.astute_vision.shop_navigator.ui.view.home.DialogStringField

@Composable
fun SettingsView() {
    var showDialog by remember {
        mutableStateOf(false)
    }

    val activity = LocalContext.current as MainActivity

    if (showDialog) {
        activity.nfcHelper.setNfcWriteNow(true)
        Dialog(onDismissRequest = {
            activity.nfcHelper.setNfcWriteNow(false)
            showDialog = false
        }) {
            DialogStringField(onConfirm = {
                activity.nfcHelper.writeToNFC(it, activity)
                showDialog = false
            }) {
                activity.nfcHelper.setNfcWriteNow(false)
                showDialog = false
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column {
            Button(onClick = {
                showDialog = true
            }) {
                Text("Записать метку")

            }

        }
    }
}