package com.example.qrcodescanningappdemo

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder

class ContactQrCodeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ContactInfoScreen(context = this)
        }
    }
}

@Composable
fun ContactInfoScreen(context: Context) {
    var name by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var qrCodeBitmap by remember { mutableStateOf<Bitmap?>(null) }

    val isNameValid = name.isNotBlank()

    // Enable button only if phone number is valid
    val isPhoneNumberValid = phoneNumber.matches(Regex("\\d{10}"))

    // Enable button only if email is valid
    val isEmailValid = email.matches(Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{3,64}"))
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.padding(8.dp)
            )
            TextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Phone Number") },
                modifier = Modifier.padding(8.dp)
            )
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.padding(8.dp)
            )

            Spacer(modifier = Modifier.weight(1f))

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Button(
                    onClick = {
                        if (isNameValid && isPhoneNumberValid && isEmailValid) {
                            generateContactQRCode(name, phoneNumber, email)?.let {
                                qrCodeBitmap = it
                            }
                        } else {
                            Toast.makeText(
                                context,
                                "Please enter valid information",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    enabled = isNameValid && isPhoneNumberValid && isEmailValid
                ) {
                    Text("Generate QR Code")
                }
            }

            qrCodeBitmap?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Contact QR Code",
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

fun generateContactQRCode(name: String, phoneNumber: String, email: String): Bitmap? {
    val barcodeEncoder = BarcodeEncoder()
    val contactInfo = "BEGIN:VCARD\n" +
            "FN:$name\n" +
            "TEL:$phoneNumber\n" +
            "EMAIL:$email\n" +
            "END:VCARD"

    return try {
        barcodeEncoder.encodeBitmap(contactInfo, BarcodeFormat.QR_CODE, 400, 400)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}