
package com.lailasd.releasemanager.entity

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(
    name = "system_versions",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_environment_version",
            columnNames = ["environment", "version_number"]
        )
    ]
)
open class SystemVersion(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @Column(nullable = false, length = 100)
    open var environment: String = "",

    @Column(name = "version_number", nullable = false)
    open var versionNumber: Int = 0,

    @Column(name = "created_at", nullable = false, updatable = false)
    open var createdAt: Instant = Instant.now()
)
