package com.example.qrcodescanningappdemo

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.util.EnumMap

class UrlQrCodeActivity  : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GenerateUrlScreen(context = this)
        }
    }
}

@Composable
fun GenerateUrlScreen(context: Context) {
    var urlText by remember { mutableStateOf("") }
    var qrCodeBitmap by remember { mutableStateOf<ImageBitmap?>(null) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = urlText,
                onValueChange = { urlText = it },
                label = { Text("Enter URL") },
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Button(
                onClick = {
                    if (urlText.isNotEmpty()) {
                        // Generate QR code bitmap
                        qrCodeBitmap = generateQRCodeBitmap(context, urlText)
                        // Hide keyboard when button is clicked
                        hideSoftKeyboard(context)
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(text = "Generate")
            }
            qrCodeBitmap?.let {
                // Display the generated QR code
                QRCodeImage(bitmap = it)
            }
        }
    }
}

private fun generateQRCodeBitmap(context: Context, text: String): ImageBitmap? {
    val hints = EnumMap<EncodeHintType, Any>(EncodeHintType::class.java)
    hints[EncodeHintType.MARGIN] = 0
    val bitMatrix: BitMatrix
    try {
        bitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, 512, 512, hints)
    } catch (e: WriterException) {
        e.printStackTrace()
        return null
    }
    val width = bitMatrix.width
    val height = bitMatrix.height

    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

    val blackColor = ContextCompat.getColor(context, android.R.color.black)
    val whiteColor = ContextCompat.getColor(context, android.R.color.white)

    val pixels = IntArray(width * height)
    for (x in 0 until width) {
        for (y in 0 until height) {
            pixels[y * width + x] = if (bitMatrix[x, y]) blackColor else whiteColor
        }
    }
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)

    return bitmap.asImageBitmap()
}