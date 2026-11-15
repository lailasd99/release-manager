
package com.lailasd.releasemanager.repository

import com.lailasd.releasemanager.entity.SystemVersion
import org.springframework.data.jpa.repository.JpaRepository

interface SystemVersionRepository :
    JpaRepository<SystemVersion, Long> {

fun findTopByEnvironmentOrderByVersionNumberDesc(
        environment: String
    ): SystemVersion?

    fun findByEnvironmentAndVersionNumber(
        environment: String,
        versionNumber: Int
    ): SystemVersion?
}
