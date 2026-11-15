package com.lailasd.releasemanager.dto

data class DeployRequest(
    val serviceName: String,
    val serviceVersionNumber: Int
)