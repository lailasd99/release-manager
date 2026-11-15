
package com.lailasd.releasemanager.entity

import jakarta.persistence.*

@Entity
@Table(
    name = "system_version_services",
    uniqueConstraints = [
        UniqueConstraint(
            name = "uk_snapshot_service",
            columnNames = ["system_version_id", "service_name"]
        )
    ]
)
open class ServiceVersionSnapshot(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "system_version_id", nullable = false)
    open var systemVersion: SystemVersion? = null,

    @Column(name = "service_name", nullable = false, length = 150)
    open var serviceName: String = "",

    @Column(name = "service_version_number", nullable = false)
    open var serviceVersionNumber: Int = 1
)
