package com.example.qrcodescanningappdemo

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.core.content.ContextCompat
import com.example.qrcodescanningappdemo.ui.theme.QrCodeScanningAppDemoTheme
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {

    private var textResult = mutableStateOf("")
    private var barCodeLauncher = registerForActivityResult(ScanContract()) { result ->
        if (result.contents == null) {
            Toast.makeText(this@MainActivity, "cancelled", Toast.LENGTH_SHORT).show()
        } else {
            textResult.value = result.contents
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showCamera()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ProvideContext(this) {
                QrCodeScanningAppDemoTheme {
                    Surface(color = Color.White) {
                        QRCodeScanningApp()
                    }
                }
            }
        }
    }

    @Composable
    fun QRCodeScanningApp() {
        Scaffold(
            bottomBar = {
                BottomAppBar(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Surface(
                        modifier = Modifier.padding(8.dp),
                        color = MaterialTheme.colorScheme.secondary,
                        contentColor = MaterialTheme.colorScheme.onSecondary
                    ) {
                        FloatingActionButton(
                            onClick = { checkCameraPermission() },
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.qr_scan),
                                contentDescription = "Scan QR Code"
                            )
                        }
                    }
                }
            },
            content = { padding ->
                Column(modifier = Modifier
                    .padding(padding)) {
                    QRCodeContent()
                }
            }
        )
    }

    @Composable
    fun QRCodeContent() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Generate QR Code",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QRCodeButton(text = "URL", onClick = { startUrlQrCodeActivity() })
                QRCodeButton(text = "Text", onClick = { startTextQrCodeActivity() })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QRCodeButton(text = "Contact", onClick = { startContactQrCodeActivity() })
                QRCodeButton(text = "Email", onClick = { startEmailQrCodeActivity() })
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QRCodeButton(text = "Phone No.", onClick = { startPhoneQrCodeActivity() })
                QRCodeButton(text = "SMS", onClick = { startSMSQrCodeActivity() })
            }
        }
    }

    @Composable
    fun QRCodeButton(text: String, onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .width(150.dp)
        ) {
            Text(text = text)
        }
    }

    private fun showCamera() {
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR code")
        options.setCameraId(0)
        options.setBeepEnabled(false)
        options.setOrientationLocked(false)

        barCodeLauncher.launch(options)
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(this@MainActivity, "Camera Required", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    private fun startUrlQrCodeActivity() {
        val intent = Intent(this@MainActivity, UrlQrCodeActivity::class.java)
        startActivity(intent)
    }

    private fun startTextQrCodeActivity() {
        val intent = Intent(this@MainActivity, TextQrCodeActivity::class.java)
        startActivity(intent)
    }

    private fun startContactQrCodeActivity() {
        val intent = Intent(this@MainActivity, ContactQrCodeActivity::class.java)
        startActivity(intent)
    }

    private fun startEmailQrCodeActivity() {
        val intent = Intent(this@MainActivity, EmailQrCodeActivity::class.java)
        startActivity(intent)
    }

    private fun startPhoneQrCodeActivity() {
        val intent = Intent(this@MainActivity, PhoneNoQrCodeActivity::class.java)
        startActivity(intent)
    }

    private fun startSMSQrCodeActivity() {
        val intent = Intent(this@MainActivity, SMSQrCodeActivity::class.java)
        startActivity(intent)
    }
}

@Composable
fun ProvideContext(context: Context, content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalContext provides context) {
        content()
    }
}