
package com.lailasd.releasemanager.controller

import com.lailasd.releasemanager.dto.DeployRequest
import com.lailasd.releasemanager.dto.ServiceVersionResponse
import com.lailasd.releasemanager.service.ReleaseManagerService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

@RestController
class ReleaseManagerController(
    private val releaseManagerService: ReleaseManagerService
) {

    @PostMapping("/deploy")
    fun deploy(
        @RequestBody request: DeployRequest,
        httpRequest: HttpServletRequest
    ): Int {

        val environment = resolveEnvironment(httpRequest.serverName)

        return releaseManagerService.deploy(
            environment = environment,
            serviceName = request.serviceName,
            serviceVersionNumber = request.serviceVersionNumber
        )
    }

    @GetMapping("/services")
    fun getServices(
        @RequestParam systemVersion: Int,
        httpRequest: HttpServletRequest
    ): List<ServiceVersionResponse> {

        val environment = resolveEnvironment(httpRequest.serverName)

        return releaseManagerService.getServices(
            environment = environment,
            systemVersionNumber = systemVersion
        )
    }

    private fun resolveEnvironment(host: String): String {
        val normalizedHost = host.lowercase()

        if (normalizedHost == "localhost" ||
            normalizedHost == "127.0.0.1"
        ) {
            return "development"
        }

        if (normalizedHost.endsWith(".localhost")) {
            return normalizedHost.substringBefore(".")
        }

        val parts = normalizedHost.split(".")

        if (parts.size < 3) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Environment subdomain is missing"
            )
        }

        return parts.first()
    }
}
