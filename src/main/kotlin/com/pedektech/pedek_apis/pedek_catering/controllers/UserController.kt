package com.pedektech.pedek_apis.pedek_catering.controllers

import com.pedektech.pedek_apis.models.*
import com.pedektech.pedek_apis.pedek_catering.models.*
import com.pedektech.pedek_apis.pedek_catering.services.UserService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

// Updated UserController with enhanced device management endpoints
@RestController
@RequestMapping("/api/v1/user")
class UserController(
    private val userService: UserService
) {

    @GetMapping("/device/validate")
    fun validateDeviceAccess(
        @RequestParam macAddress: String,
        @RequestParam(required = false) userIdentifier: String?
    ): ResponseEntity<DeviceAccessResult> {
        if (!userService.isValidDeviceId(macAddress)) {
            return ResponseEntity.badRequest().body(
                DeviceAccessResult(
                    status = false,
                    isValid = false,
                    isAttachedToAccount = false,
                    hasMultipleAccounts = false,
                    message = "Invalid MAC address format"
                )
            )
        }

        val result = userService.validateDeviceAccess(macAddress, userIdentifier)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/create-with-device")
    fun createUserWithDevice(
        @RequestParam macAddress: String,
        @RequestBody userDetails: CreateUserRequest
    ): ResponseEntity<CreateAccountResult> {
        if (!userService.isValidDeviceId(macAddress)) {
            return ResponseEntity.badRequest().body(
                CreateAccountResult(
                    status = false,
                    message = "Invalid MAC address format",
                    userId = null
                )
            )
        }

        val result = userService.createUserAccountWithDevice(macAddress, userDetails)
        return if (result.status) {
            ResponseEntity.status(HttpStatus.CREATED).body(result)
        } else {
            ResponseEntity.badRequest().body(result)
        }
    }

    @PostMapping("/device/change")
    fun handleDeviceChange(
        @RequestBody request: DeviceChangeRequest
    ): ResponseEntity<DeviceChangeResult> {
        if (!userService.isValidDeviceId(request.newMacAddress)) {
            return ResponseEntity.badRequest().body(
                DeviceChangeResult(
                    success = false,
                    message = "Invalid MAC address format",
                    requiresLogin = false
                )
            )
        }

        val result = userService.handleDeviceChange(
            request.userIdentifier,
            request.newMacAddress,
            request.currentPassword
        )

        return if (result.success) {
            ResponseEntity.ok(result)
        } else if (result.requiresLogin) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result)
        } else {
            ResponseEntity.badRequest().body(result)
        }
    }

    @PostMapping("/{userId}/device/attach")
    fun attachDevice(
        @PathVariable userId: String,
        @RequestBody request: AttachDeviceRequest
    ): ResponseEntity<AttachDeviceResult> {
        if (!userService.isValidDeviceId(request.macAddress)) {
            return ResponseEntity.badRequest().body(
                AttachDeviceResult(
                    status = false,
                    message = "Invalid MAC address format"
                )
            )
        }

        val result = userService.attachDeviceToUser(userId, request.macAddress)
        return if (result.status) {
            ResponseEntity.ok(result)
        } else {
            ResponseEntity.badRequest().body(result)
        }
    }

    @DeleteMapping("/{userId}/device/detach")
    fun detachDevice(@PathVariable userId: String): ResponseEntity<AttachDeviceResult> {
        val result = userService.detachDeviceFromUser(userId)
        return ResponseEntity.ok(result)
    }
}