package com.astute_vision.shop_navigator

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.astute_vision.shop_navigator.navigation.AVNavigationGraph
import com.astute_vision.shop_navigator.ui.theme.AstuteVisionShopNavigatorTheme
import com.astute_vision.shop_navigator.ui.view.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint
import ru.bitec.eam.nfc.NfcHelper

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val nfcHelper = NfcHelper()
    var onNfcRead: (String) -> Unit = {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setNfc()


        setContent {

            val navController = rememberNavController()

            AstuteVisionShopNavigatorTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AVNavigationGraph(navController = navController)
                }
            }
        }
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        val action = intent?.action
        if (action == NfcAdapter.ACTION_TAG_DISCOVERED ||
            action == NfcAdapter.ACTION_TECH_DISCOVERED ||
            action == NfcAdapter.ACTION_NDEF_DISCOVERED
        ) {
            nfcHelper.nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
            val text = nfcHelper.readMessage(intent = intent)

            if (text.isNotEmpty()) {
                onNfcRead(text)
//                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            }
        }
    }


    private fun setNfc() {
        val manager = this.getSystemService(NFC_SERVICE) as NfcManager
        nfcHelper.nfcAdapter = manager.defaultAdapter

        if (nfcHelper.nfcAdapter != null) {
            nfcHelper.pendingIntent = PendingIntent.getActivity(
                this,
                1,
                Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            val tagDetected = IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED)
            tagDetected.addCategory(Intent.CATEGORY_DEFAULT)


            nfcHelper.writeTagFilters = arrayOf(tagDetected)
        }
    }



    override fun onResume() {
        super.onResume()
        if (nfcHelper?.nfcAdapter != null) {
            nfcHelper?.nfcAdapter?.enableForegroundDispatch(this, nfcHelper.pendingIntent, nfcHelper.writeTagFilters, arrayOf(arrayOf<String>(Ndef::class.java.name)))
        }
    }


    override fun onPause() {
        super.onPause()
        nfcHelper.nfcAdapter?.disableForegroundDispatch(this)
    }
}
