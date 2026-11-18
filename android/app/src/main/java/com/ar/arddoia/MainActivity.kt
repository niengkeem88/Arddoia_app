package com.ar.arddoia

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ar.arddoia.ui.ArddoiaRoot
import com.ar.arddoia.ui.theme.ArddoiaTheme
import com.ar.arddoia.ui.viewmodel.ArddoiaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )
        super.onCreate(savedInstanceState)

        val repository = (application as ArddoiaApp).repository

        setContent {
            ArddoiaTheme {
                val viewModel: ArddoiaViewModel = viewModel(
                    factory = ArddoiaViewModel.factory(repository)
                )
                ArddoiaRoot(viewModel = viewModel)
            }
        }
    }
}
