package com.interviewpreparation.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.interviewpreparation.core.network.ApiConfig
import com.interviewpreparation.data.remote.InterviewApi
import com.interviewpreparation.data.repository.DeskRepositoryImpl
import com.interviewpreparation.feature.desk.deskScreen
import com.interviewpreparation.feature.home.HomeViewModel
import com.interviewpreparation.feature.home.HomeViewModelFactory
import com.interviewpreparation.feature.home.homeScreen
import com.interviewpreparation.feature.review.reviewScreen
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun appNavigation() {
    val navController = rememberNavController()

    val api =
        Retrofit
            .Builder()
            .baseUrl(ApiConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(InterviewApi::class.java)

    val repository = DeskRepositoryImpl(api)

    val homeViewModel: HomeViewModel =
        viewModel(
            factory = HomeViewModelFactory(repository),
        )

    NavHost(
        navController = navController,
        startDestination = "home",
    ) {
        composable("home") {
            homeScreen(
                onStartReview = {
                    navController.navigate("review")
                },
                onDeskClick = { deskId ->
                    navController.navigate("desk/$deskId")
                },
                viewModel = homeViewModel,
            )
        }

        composable("review") {
            reviewScreen(
                api = api,
                onBack = {
                    navController.popBackStack()
                },
            )
        }

        composable("desk/{deskId}") { backStackEntry ->
            val deskId =
                backStackEntry.arguments
                    ?.getString("deskId")
                    ?: return@composable

            deskScreen(
                deskId = deskId,
                onBack = {
                    navController.popBackStack()
                },
            )
        }
    }
}
