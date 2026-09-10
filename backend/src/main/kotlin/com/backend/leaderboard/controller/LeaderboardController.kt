package com.backend.leaderboard.controller

import com.backend.leaderboard.controller.dto.LeaderboardUserResponse
import com.backend.leaderboard.service.LeaderboardService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/leaderboard")
class LeaderboardController(
    private val leaderboardService: LeaderboardService,
) {
    private val log = LoggerFactory.getLogger(LeaderboardController::class.java)

    @Operation(summary = "Get daily leaderboard")
    @ApiResponses(
        ApiResponse(
            responseCode = LeaderboardApiCodes.OK,
            description = LeaderboardApiMessages.OK,
        ),
        ApiResponse(
            responseCode = LeaderboardApiCodes.UNAUTHORIZED,
            description = LeaderboardApiMessages.UNAUTHORIZED,
        ),
    )
    @GetMapping("/day")
    fun getDay(): ResponseEntity<List<LeaderboardUserResponse>> {
        log
            .atInfo()
            .log("Getting daily leaderboard")

        return ResponseEntity.ok(
            leaderboardService.getDay(),
        )
    }

    @Operation(summary = "Get weekly leaderboard")
    @ApiResponses(
        ApiResponse(
            responseCode = LeaderboardApiCodes.OK,
            description = LeaderboardApiMessages.OK,
        ),
        ApiResponse(
            responseCode = LeaderboardApiCodes.UNAUTHORIZED,
            description = LeaderboardApiMessages.UNAUTHORIZED,
        ),
    )
    @GetMapping("/week")
    fun getWeek(): ResponseEntity<List<LeaderboardUserResponse>> {
        log
            .atInfo()
            .log("Getting weekly leaderboard")

        return ResponseEntity.ok(
            leaderboardService.getWeek(),
        )
    }

    @Operation(summary = "Get all-time leaderboard")
    @ApiResponses(
        ApiResponse(
            responseCode = LeaderboardApiCodes.OK,
            description = LeaderboardApiMessages.OK,
        ),
        ApiResponse(
            responseCode = LeaderboardApiCodes.UNAUTHORIZED,
            description = LeaderboardApiMessages.UNAUTHORIZED,
        ),
    )
    @GetMapping("/all-time")
    fun getAllTime(): ResponseEntity<List<LeaderboardUserResponse>> {
        log
            .atInfo()
            .log("Getting all-time leaderboard")

        return ResponseEntity.ok(
            leaderboardService.getAllTime(),
        )
    }
}
