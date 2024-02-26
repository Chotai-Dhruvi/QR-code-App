package com.example.qrcodescanningappdemo

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import java.util.EnumMap

class PhoneNoQrCodeActivity : ComponentActivity() { override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QRCodeGenerator()
        }
    }
}
@Composable
fun QRCodeGenerator() {
    val context = LocalContext.current
    Surface(color = MaterialTheme.colorScheme.background) {
        val phoneNumber = remember { mutableStateOf("") }
        val generatedQRCode = remember { mutableStateOf<Bitmap?>(null) }
        Surface(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    label = { Text("Enter Phone Number") },
                    modifier = Modifier.padding(vertical = 8.dp)

                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    generatedQRCode.value = generateQRCode(phoneNumber.value)
                    hideSoftKeyboard(context)
                }) {
                    Text("Generate QR Code")
                }
                Spacer(modifier = Modifier.height(16.dp))
                generatedQRCode.value?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Generated QR Code",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

private fun generateQRCode(text: String): Bitmap? {
    val width = 512
    val height = 512
    val hintsMap: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
    hintsMap[EncodeHintType.CHARACTER_SET] = "UTF-8"
    try {
        // Encode the phone number as a tel: URI
        val uri = "tel:$text"
        val bitMatrix = QRCodeWriter().encode(uri, BarcodeFormat.QR_CODE, width, height, hintsMap)
        val bitmap = Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.RGB_565
        )
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(
                    x,
                    y,
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
                )
            }
        }
        return bitmap
    } catch (e: WriterException) {
        e.printStackTrace()
    }
    return null

}