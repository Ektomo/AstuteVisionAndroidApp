package ru.bitec.eam.nfc

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.*
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable
import android.os.Build
import android.util.Log
import android.widget.Toast
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.experimental.and

class NfcHelper {

    var writeTagFilters: Array<IntentFilter> = arrayOf()
    var nfcAdapter: NfcAdapter? = null
    var pendingIntent: PendingIntent? = null
    var nfcTag: Tag? = null
    private var isNfcWriteNow: AtomicBoolean = AtomicBoolean(false)

    fun setNfcWriteNow(value: Boolean){
        isNfcWriteNow.set(value)
    }

    fun readMessage(intent: Intent): String {
        if(isNfcWriteNow.get()){
            return ""
        }
        nfcTag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        //            val nfcTag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        val msgs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU ){
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, Array<NdefMessage>::class.java)
        }else{
            intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)
        }?.map { it as NdefMessage }


        if (msgs.isNullOrEmpty()) return ""

        val payload = msgs[0].records[0].payload
        val textEncoding: Charset =
            if ((payload[0] and 128.toByte()) == 0.toByte()) Charsets.UTF_8 else Charsets.UTF_16
        val languageCodeLength = (payload[0] and 51).toInt()

        val text: String

        try {
            text = String(
                payload,
                languageCodeLength + 1,
                payload.size - languageCodeLength - 1,
                textEncoding
            )
        } catch (e: java.lang.Exception) {
            Log.e("UnsupportedEncoding", e.toString())
            return ""
        }
        return text
    }


    private fun createRecord(text: String): NdefRecord {
        val lang = "en"
        val textBytes = text.toByteArray()
        val langBytes =
            lang.toByteArray(StandardCharsets.US_ASCII)
        val langLength = langBytes.size
        val textLength = textBytes.size
        val payload = ByteArray(1 + langLength + textLength)

        // set status byte (see NDEF spec for actual bits)
        payload[0] = langLength.toByte()

        // copy langbytes and textbytes into payload
        System.arraycopy(langBytes, 0, payload, 1, langLength)
        System.arraycopy(textBytes, 0, payload, 1 + langLength, textLength)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }


    fun writeToNFC(value: String, context: Context) {
        if (nfcAdapter == null) {
            return
        }
        try {
            val records = arrayOf(createRecord(value))
            val message = NdefMessage(records)
            // Get an instance of Ndef for the tag.
            val ndefTag = Ndef.get(nfcTag)
            if (ndefTag == null) {
                // Let's try to format the Tag in NDEF
                val nForm = NdefFormatable.get(nfcTag)
                if (nForm != null) {
                    nForm.connect()
                    nForm.format(message)
                    nForm.close()
                }
            } else {
                // Enable I/O
                ndefTag.connect()
                // Write the login
                ndefTag.writeNdefMessage(message)
                // Close the connection
                ndefTag.close()
            }
            Toast.makeText(context, "Метка обновлена", Toast.LENGTH_LONG).show()
        } catch (e: IOException) {
            Toast.makeText(context, "Ошибка при записи метки", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        } catch (e: FormatException) {
            Toast.makeText(context, "Ошибка при записи метки", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }finally {
            isNfcWriteNow.set(false)
        }
//        finally {
//            setWriteNow(false);
//        }
    }

}