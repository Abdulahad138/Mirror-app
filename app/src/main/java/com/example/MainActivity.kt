package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.Quiz
import androidx.compose.material.icons.outlined.Science
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.model.SimulationState
import com.example.ui.screens.CalculatorScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.SimulationScreen
import com.example.ui.screens.TheoryScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppTab(
    val titleBn: String,
    val titleEn: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    SIMULATION("সিমুলেশন", "Simulation", Icons.Filled.Science, Icons.Outlined.Science),
    THEORY("তত্ত্ব ও প্রয়োগ", "Theory", Icons.Filled.MenuBook, Icons.Outlined.MenuBook),
    CALCULATOR("সমাধান", "Solver", Icons.Filled.Calculate, Icons.Outlined.Calculate),
    QUIZ("অনুশীলন", "Quiz", Icons.Filled.Quiz, Icons.Outlined.Quiz)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent() {
    var selectedTab by remember { mutableStateOf(AppTab.SIMULATION) }
    var simulationState by remember { mutableStateOf(SimulationState()) }

    // Back handling: If on secondary tab, back button returns to Simulation tab
    BackHandler(enabled = selectedTab != AppTab.SIMULATION) {
        selectedTab = AppTab.SIMULATION
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(modifier = Modifier.testTag("bottom_nav_bar")) {
                AppTab.values().forEach { tab ->
                    val isSelected = selectedTab == tab
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.titleEn
                            )
                        },
                        label = {
                            Text(
                                text = if (simulationState.isEnglish) tab.titleEn else tab.titleBn,
                                fontSize = 11.sp
                            )
                        },
                        modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
                    )
                }
            }
        }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (selectedTab) {
            AppTab.SIMULATION -> SimulationScreen(
                state = simulationState,
                onStateChange = { simulationState = it },
                modifier = modifier
            )
            AppTab.THEORY -> TheoryScreen(
                isEnglish = simulationState.isEnglish,
                modifier = modifier
            )
            AppTab.CALCULATOR -> CalculatorScreen(
                state = simulationState,
                onApplyToSimulation = { newState ->
                    simulationState = newState
                    selectedTab = AppTab.SIMULATION
                },
                modifier = modifier
            )
            AppTab.QUIZ -> QuizScreen(
                state = simulationState,
                onTestInSimulation = { newState ->
                    simulationState = newState
                    selectedTab = AppTab.SIMULATION
                },
                modifier = modifier
            )
        }
    }
}
