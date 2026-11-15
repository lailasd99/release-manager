
package com.lailasd.releasemanager.repository

import com.lailasd.releasemanager.entity.ServiceVersionSnapshot
import org.springframework.data.jpa.repository.JpaRepository

interface ServiceVersionSnapshotRepository :
    JpaRepository<ServiceVersionSnapshot, Long> {

    fun findAllBySystemVersion_Id(
        systemVersionId: Long
    ): List<ServiceVersionSnapshot>
}
