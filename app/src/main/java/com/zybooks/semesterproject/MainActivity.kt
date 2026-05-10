package com.zybooks.semesterproject


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.zybooks.semesterproject.ui.theme.SemesterProjectTheme
import com.zybooks.semesterproject.ui.theme.ui.SemesterScreen
import com.zybooks.semesterproject.ui.theme.ui.SemesterViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreLogic = DSLogic(this)
        val viewModel: SemesterViewModel by viewModels()
        viewModel.setDataStore(dataStoreLogic)

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            SemesterProjectTheme(dynamicColor = false, darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SemesterScreen(viewModel)
                }
            }
        }
    }
}