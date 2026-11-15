package com.lailasd.releasemanager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ReleaseManageApplication

fun main(args: Array<String>) {
	runApplication<ReleaseManageApplication>(*args)
}
