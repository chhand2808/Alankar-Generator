package org.example.kmpproj1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.alankargenerator.AlankarScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
                AlankarScreen()
        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}