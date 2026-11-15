
package com.lailasd.releasemanager.service

import com.lailasd.releasemanager.repository.SystemVersionRepository
import com.lailasd.releasemanager.repository.ServiceVersionSnapshotRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class ReleaseManagerServiceTest {

    @Autowired
    private lateinit var releaseManagerService: ReleaseManagerService

    @Autowired
    private lateinit var systemVersionRepository: SystemVersionRepository

    @Autowired
    private lateinit var snapshotRepository: ServiceVersionSnapshotRepository

    private fun getServices(
        environment: String,
        version: Int
    ): Map<String, Int> {

        val systemVersion = systemVersionRepository
            .findByEnvironmentAndVersionNumber(environment, version)
            ?: error("SystemVersion $version not found")

        return snapshotRepository
            .findAllBySystemVersion_Id(requireNotNull(systemVersion.id))
            .associate {
                it.serviceName to it.serviceVersionNumber
            }
    }

    @Test
    fun `first deployment should create system version 1`() {

        val result = releaseManagerService.deploy(
            environment = "test-first",
            serviceName = "Service A",
            serviceVersionNumber = 1
        )

        assertEquals(1, result)

        assertEquals(
            mapOf("Service A" to 1),
            getServices("test-first", 1)
        )
    }

    @Test
    fun `deploying a new service should create a new system version`() {

        val environment = "test-new-service"

        releaseManagerService.deploy(environment, "Service A", 1)

        val result = releaseManagerService.deploy(
            environment,
            "Service B",
            1
        )

        assertEquals(2, result)

        // The previous snapshot must remain unchanged
        assertEquals(
            mapOf("Service A" to 1),
            getServices(environment, 1)
        )

        // The new snapshot must contain both services
        assertEquals(
            mapOf(
                "Service A" to 1,
                "Service B" to 1
            ),
            getServices(environment, 2)
        )
    }

    @Test
    fun `changing an existing service version should create a new system version`() {

        val environment = "test-update"

        releaseManagerService.deploy(environment, "Service A", 1)

        val result = releaseManagerService.deploy(
            environment,
            "Service A",
            2
        )

        assertEquals(2, result)

        // Version 1 must still contain the original deployment
        assertEquals(
            mapOf("Service A" to 1),
            getServices(environment, 1)
        )

        // Version 2 must contain the updated deployment
        assertEquals(
            mapOf("Service A" to 2),
            getServices(environment, 2)
        )
    }

    @Test
    fun `deploying an unchanged service version should not create a new system version`() {

        val environment = "test-unchanged"

        val firstResult = releaseManagerService.deploy(
            environment,
            "Service A",
            1
        )

        val secondResult = releaseManagerService.deploy(
            environment,
            "Service A",
            1
        )

        assertEquals(1, firstResult)
        assertEquals(1, secondResult)

        // No SystemVersion 2 should have been created
        assertNull(
            systemVersionRepository.findByEnvironmentAndVersionNumber(
                environment,
                2
            )
        )

        assertEquals(
            mapOf("Service A" to 1),
            getServices(environment, 1)
        )
    }


    @Test
    fun `rolling back to a previous service version should create a new system version`() {

        val environment = "test-rollback"

        // Initial deployment
        val first = releaseManagerService.deploy(
            environment, "Service A", 1
        )

        // Deploy a second service
        val second = releaseManagerService.deploy(
            environment, "Service B", 1
        )

        // Upgrade Service A
        val third = releaseManagerService.deploy(
            environment, "Service A", 2
        )

        // Roll back Service A to version 1
        val rollback = releaseManagerService.deploy(
            environment, "Service A", 1
        )

        // Verify SystemVersion numbers
        assertEquals(1, first)
        assertEquals(2, second)
        assertEquals(3, third)
        assertEquals(4, rollback)

        // Verify historical snapshots remain unchanged
        assertEquals(
            mapOf("Service A" to 1),
            getServices(environment, 1)
        )

        assertEquals(
            mapOf(
                "Service A" to 1,
                "Service B" to 1
            ),
            getServices(environment, 2)
        )

        assertEquals(
            mapOf(
                "Service A" to 2,
                "Service B" to 1
            ),
            getServices(environment, 3)
        )

        // Verify the rollback created a new snapshot
        assertEquals(
            mapOf(
                "Service A" to 1,
                "Service B" to 1
            ),
            getServices(environment, 4)
        )
    }

}
