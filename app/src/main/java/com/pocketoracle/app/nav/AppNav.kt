package com.pocketoracle.app.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pocketoracle.app.features.AnswerBookScreen
import com.pocketoracle.app.features.BleRemoteScreen
import com.pocketoracle.app.features.ClockScreen
import com.pocketoracle.app.features.CoinScreen
import com.pocketoracle.app.features.CountdownScreen
import com.pocketoracle.app.features.DiceScreen
import com.pocketoracle.app.features.FortuneScreen
import com.pocketoracle.app.features.HomeScreen
import com.pocketoracle.app.features.MbtiScreen
import com.pocketoracle.app.features.PomodoroScreen
import com.pocketoracle.app.features.SettingsScreen
import com.pocketoracle.app.features.TenScreen
import com.pocketoracle.app.features.WoodfishScreen

object Routes {
    const val HOME = "home"
    const val ANSWERS = "answers"
    const val FORTUNE = "fortune"
    const val MBTI = "mbti"
    const val TEN = "ten"
    const val WOODFISH = "woodfish"
    const val COIN = "coin"
    const val CLOCK = "clock"
    const val POMODORO = "pomodoro"
    const val COUNTDOWN = "countdown"
    const val BLE = "ble"
    const val DICE = "dice"
    const val SETTINGS = "settings"
}

@Composable
fun AppNav(navController: NavHostController = rememberNavController()) {
    val back: () -> Unit = { navController.popBackStack() }

    NavHost(navController = navController, startDestination = Routes.HOME) {
        composable(Routes.HOME) { HomeScreen(onOpen = { navController.navigate(it) }) }
        composable(Routes.ANSWERS) { AnswerBookScreen(back) }
        composable(Routes.FORTUNE) { FortuneScreen(back) }
        composable(Routes.MBTI) { MbtiScreen(back) }
        composable(Routes.TEN) { TenScreen(back) }
        composable(Routes.WOODFISH) { WoodfishScreen(back) }
        composable(Routes.COIN) { CoinScreen(back) }
        composable(Routes.CLOCK) { ClockScreen(back) }
        composable(Routes.POMODORO) { PomodoroScreen(back) }
        composable(Routes.COUNTDOWN) { CountdownScreen(back) }
        composable(Routes.BLE) { BleRemoteScreen(back) }
        composable(Routes.DICE) { DiceScreen(back) }
        composable(Routes.SETTINGS) { SettingsScreen(back) }
    }
}
