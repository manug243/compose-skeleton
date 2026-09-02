package io.github.manug243.composeskeleton.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeSkeletonTheme {
                SkeletonExampleScreen()
            }
        }
    }
}
