
package com.lailasd.releasemanager.service

import com.lailasd.releasemanager.entity.SystemVersion
import com.lailasd.releasemanager.entity.ServiceVersionSnapshot
import com.lailasd.releasemanager.repository.SystemVersionRepository
import com.lailasd.releasemanager.repository.ServiceVersionSnapshotRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.lailasd.releasemanager.dto.ServiceVersionResponse
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@Service
class ReleaseManagerService(
    private val systemVersionRepository: SystemVersionRepository,
    private val snapshotRepository: ServiceVersionSnapshotRepository
) {

    @Transactional
    fun deploy(
        environment: String,
        serviceName: String,
        serviceVersionNumber: Int
    ): Int {

        require(environment.isNotBlank()) {
            "Environment cannot be empty"
        }

        require(serviceName.isNotBlank()) {
            "Service name cannot be empty"
        }

        require(serviceVersionNumber > 0) {
            "Service version must be greater than zero"
        }

        val env = environment.trim().lowercase()
        val name = serviceName.trim()

        // Get the latest system version for this environment
        val latest = systemVersionRepository
            .findTopByEnvironmentOrderByVersionNumberDesc(env)

        // Retrieve the complete deployment snapshot
        val currentSnapshots = latest?.id?.let {
            snapshotRepository.findAllBySystemVersion_Id(it)
        } ?: emptyList()

        // Check whether this service already exists
        val existingService = currentSnapshots.find {
            it.serviceName == name
        }

        // Nothing changed: return the current system version
        if (existingService?.serviceVersionNumber == serviceVersionNumber) {
            return latest!!.versionNumber
        }

        // Something changed: create a new system version
        val newVersionNumber = (latest?.versionNumber ?: 0) + 1

        val newSystemVersion = systemVersionRepository.save(
            SystemVersion(
                environment = env,
                versionNumber = newVersionNumber
            )
        )

        // Copy the previous snapshot and apply the deployment change
        val newSnapshots = currentSnapshots.map { snapshot ->
            ServiceVersionSnapshot(
                systemVersion = newSystemVersion,
                serviceName = snapshot.serviceName,
                serviceVersionNumber =
                    if (snapshot.serviceName == name)
                        serviceVersionNumber
                    else
                        snapshot.serviceVersionNumber
            )
        }.toMutableList()

        // Add the service if it has never been deployed before
        if (existingService == null) {
            newSnapshots.add(
                ServiceVersionSnapshot(
                    systemVersion = newSystemVersion,
                    serviceName = name,
                    serviceVersionNumber = serviceVersionNumber
                )
            )
        }

        // Save all service versions belonging to the new snapshot
        snapshotRepository.saveAll(newSnapshots)

        return newVersionNumber
    }

    @Transactional(readOnly = true)
    fun getServices(
        environment: String,
        systemVersionNumber: Int
    ): List<ServiceVersionResponse> {

        val systemVersion = systemVersionRepository
            .findByEnvironmentAndVersionNumber(
                environment.lowercase(),
                systemVersionNumber
            )
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "System version not found"
            )

        val snapshots = snapshotRepository.findAllBySystemVersion_Id(
            requireNotNull(systemVersion.id)
        )

        return snapshots.map {
            ServiceVersionResponse(
                name = it.serviceName,
                version = it.serviceVersionNumber
            )
        }
    }
}
