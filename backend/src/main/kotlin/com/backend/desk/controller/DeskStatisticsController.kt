package com.backend.desk.controller

import com.backend.desk.controller.dto.DeskCardLevelStatisticsResponse
import com.backend.desk.service.DeskStatisticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/desks")
class DeskStatisticsController(
    private val deskStatisticsService: DeskStatisticsService,
) {
    private val log = LoggerFactory.getLogger(DeskStatisticsController::class.java)

    @Operation(summary = "Get card level statistics for all desks")
    @ApiResponse(
        responseCode = "200",
        description = "Successful response",
    )
    @GetMapping("/statistics")
    fun getStatistics(): ResponseEntity<List<DeskCardLevelStatisticsResponse>> {
        log
            .atInfo()
            .addKeyValue("operation", "getDeskStatistics")
            .log("Getting desk statistics")

        return ResponseEntity.ok(
            deskStatisticsService.getStatistics(),
        )
    }
}
