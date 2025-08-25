package com.pedektech.pedek_catering.services





import com.pedektech.pedek_catering.models.*
import com.pedektech.pedek_catering.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,

    ) {

    /**
     * Validates device access and handles automatic account creation or device change scenarios
     *
     * @param macAddress The MAC address of the device to check
     * @param userIdentifier Optional user identifier (email, username, etc.) for device change detection
     * @return DeviceAccessResult containing validation results and suggested actions
     */
    fun validateDeviceAccess(macAddress: String, userIdentifier: String? = null): DeviceAccessResult {
        val normalizedMacAddress = normalizeMacAddress(macAddress)
        val usersWithDevice = userRepository.findByAccessDevice(normalizedMacAddress)

        return when {
            // Scenario 1: Device not attached to any account
            usersWithDevice.isEmpty() -> {
                if (userIdentifier != null) {
                    // Check if user exists with different device
                    val existingUser = findUserByIdentifier(userIdentifier)
                    if (existingUser != null && existingUser.accessDevice != null) {
                        // User exists but with different device - device change scenario
                        DeviceAccessResult(
                            status = false,
                            isValid = false,
                            isAttachedToAccount = false,
                            hasMultipleAccounts = false,
                            action = DeviceAction.DEVICE_CHANGE_DETECTED,
                            message = "User exists but with different device. Device change detected.",
                            associatedUser = existingUser,
                            suggestedAction = "Help user login and update device"
                        )
                    } else if (existingUser != null && existingUser.accessDevice == null) {
                        // User exists but has no device attached
                        DeviceAccessResult(
                            status = false,
                            isValid = true,
                            isAttachedToAccount = false,
                            hasMultipleAccounts = false,
                            action = DeviceAction.ATTACH_TO_EXISTING_USER,
                            message = "User exists without device. Ready to attach device.",
                            associatedUser = existingUser,
                            suggestedAction = "Attach device to existing user account"
                        )
                    } else {
                        // No user found - create new account
                        DeviceAccessResult(
                            status = false,
                            isValid = false,
                            isAttachedToAccount = false,
                            hasMultipleAccounts = false,
                            action = DeviceAction.CREATE_NEW_ACCOUNT,
                            message = "Device not attached and no existing user found.",
                            suggestedAction = "Create new user account and attach device"
                        )
                    }
                } else {
                    // No user identifier provided
                    DeviceAccessResult(
                        status = false,
                        isValid = false,
                        isAttachedToAccount = false,
                        hasMultipleAccounts = false,
                        action = DeviceAction.CREATE_NEW_ACCOUNT,
                        message = "Device not attached to any account",
                        suggestedAction = "Create new user account and attach device"
                    )
                }
            }

            // Scenario 2: Device attached to exactly one account
            usersWithDevice.size == 1 -> {
                val user = usersWithDevice.first()
                DeviceAccessResult(
                    status = false,
                    isValid = user.isActive,
                    isAttachedToAccount = true,
                    hasMultipleAccounts = false,
                    action = if (user.isActive) DeviceAction.ALLOW_ACCESS else DeviceAction.ACCOUNT_INACTIVE,
                    message = if (user.isActive) "Device is properly attached to an active account"
                    else "Device is attached to an inactive account",
                    associatedUser = user,
                    suggestedAction = if (user.isActive) "Allow access" else "Activate account"
                )
            }

            // Scenario 3: Device attached to multiple accounts (conflict)
            else -> {
                DeviceAccessResult(
                    status = false,
                    isValid = false,
                    isAttachedToAccount = true,
                    hasMultipleAccounts = true,
                    action = DeviceAction.RESOLVE_CONFLICT,
                    message = "Device is attached to multiple accounts (${usersWithDevice.size} accounts found)",
                    conflictingUsers = usersWithDevice,
                    suggestedAction = "Resolve account conflict - user must choose primary account"
                )
            }
        }
    }

    /**
     * Creates a new user account and attaches the device
     *
     * @param deviceMacAddress The MAC address to attach
     * @param userDetails The user details for account creation
     * @return CreateAccountResult containing operation results
     */
    fun createUserAccountWithDevice(
        deviceMacAddress: String,
        userDetails: CreateUserRequest
    ): CreateAccountResult {
        val normalizedMacAddress = normalizeMacAddress(deviceMacAddress)

        // Validate MAC address format
        if (!isValidDeviceId(deviceMacAddress)) {
            return CreateAccountResult(
                status = false,
                message = "Invalid MAC address format",
                userId = null
            )
        }

        // Check if device is already attached to another account
        val deviceValidation = validateDeviceAccess(deviceMacAddress)
        if (deviceValidation.hasMultipleAccounts) {
            return CreateAccountResult(
                status = false,
                message = "Cannot create account: device attached to multiple existing accounts",
                userId = null
            )
        }

        if (deviceValidation.isAttachedToAccount) {
            return CreateAccountResult(
                status = false,
                message = "Cannot create account: device already attached to existing account (${deviceValidation.associatedUser?.userName})",
                userId = null,
                existingUser = deviceValidation.associatedUser
            )
        }

        // Check if user already exists by email or username
        if (userRepository.existsByEmailAddress(userDetails.emailAddress)) {
            return CreateAccountResult(
                status = false,
                message = "Account with this email already exists",
                userId = null
            )
        }

        if (userRepository.existsByUserName(userDetails.userName)) {
            return CreateAccountResult(
                status = false,
                message = "Username already taken",
                userId = null
            )
        }

        val hashedPassword = passwordEncoder.encode(userDetails.password)

        // Create new user
        val newUser = User(
            userId = generateUserId(),
            fullName = userDetails.fullName,
            userName = userDetails.userName,
            emailAddress = userDetails.emailAddress,
            phoneNumber = userDetails.phoneNumber,
            userPassword = hashedPassword, // Should be encrypted before saving
            accessDevice = normalizedMacAddress,
            role = userDetails.role ?: Role.CUSTOMER,
            createdAt = Date(),
            modifiedAt = Date(),
            isActive = true,
            provider = userDetails.provider,
            providerId = userDetails.providerId
        )

        val savedUser = userRepository.save(newUser)

        return CreateAccountResult(
            status = true,
            message = "User account created successfully with device attached",
            userId = savedUser.userId,
            createdUser = savedUser
        )
    }

    /**
     * Attaches a device to a user account after validation
     *
     * @param userId The user ID to attach the device to
     * @param macAddress The MAC address of the device
     * @return AttachDeviceResult containing operation results
     */
    fun attachDeviceToUser(userId: String, macAddress: String): AttachDeviceResult {
        val normalizedMacAddress = normalizeMacAddress(macAddress)

        // Check if device is already attached to other accounts
        val deviceValidation = validateDeviceAccess(normalizedMacAddress)

        if (deviceValidation.hasMultipleAccounts) {
            return AttachDeviceResult(
                status = false,
                message = "Cannot attach device: already attached to multiple accounts"
            )
        }

        if (deviceValidation.isAttachedToAccount &&
            deviceValidation.associatedUser?.userId != userId) {
            return AttachDeviceResult(
                status = false,
                message = "Cannot attach device: already attached to another account (${deviceValidation.associatedUser?.userName})"
            )
        }

        // Find the user to attach device to
        val user = userRepository.findByUserId(userId)
            ?: return AttachDeviceResult(
                status = false,
                message = "User not found"
            )

        // Attach device to user
        user.accessDevice = normalizedMacAddress
        user.modifiedAt = Date()
        userRepository.save(user)

        return AttachDeviceResult(
            status = true,
            message = "Device successfully attached to user account"
        )
    }

    /**
     * Removes device attachment from a user account
     */
    fun detachDeviceFromUser(userId: String): AttachDeviceResult {
        val user = userRepository.findByUserId(userId)
            ?: return AttachDeviceResult(
                status = false,
                message = "User not found"
            )

        user.accessDevice = null
        user.modifiedAt = Date()
        userRepository.save(user)

        return AttachDeviceResult(
            status = true,
            message = "Device successfully detached from user account"
        )
    }

    /**
     * Handles device change scenario - helps user login and updates device
     *
     * @param userIdentifier User identifier (email, username, or userId)
     * @param newMacAddress The new MAC address to attach
     * @param currentPassword User's current password for verification
     * @return DeviceChangeResult containing operation results
     */
    fun handleDeviceChange(
        userIdentifier: String,
        newMacAddress: String,
        currentPassword: String? = null
    ): DeviceChangeResult {
        val normalizedNewMacAddress = normalizeMacAddress(newMacAddress)

        // Find the user
        val user = findUserByIdentifier(userIdentifier)
            ?: return DeviceChangeResult(
                success = false,
                message = "User not found",
                requiresLogin = false
            )

        // If password is provided, verify it (implement password verification logic)
        if (currentPassword != null && !verifyPassword(user, currentPassword)) {
            return DeviceChangeResult(
                success = false,
                message = "Invalid password",
                requiresLogin = true,
                user = user
            )
        }

        // Check if new device is already attached to another account
        val newDeviceValidation = validateDeviceAccess(normalizedNewMacAddress)
        if (newDeviceValidation.isAttachedToAccount &&
            newDeviceValidation.associatedUser?.userId != user.userId) {
            return DeviceChangeResult(
                success = false,
                message = "New device is already attached to another account",
                requiresLogin = false,
                user = user
            )
        }

        // Store old device for logging/audit purposes
        val oldDevice = user.accessDevice

        // Update user's device
        user.accessDevice = normalizedNewMacAddress
        user.modifiedAt = Date()
        userRepository.save(user)

        return DeviceChangeResult(
            success = true,
            message = "Device updated successfully. User can now access with new device.",
            requiresLogin = false,
            user = user,
            oldDevice = oldDevice,
            newDevice = normalizedNewMacAddress
        )
    }

    /**
     * Helper function to find user by various identifiers
     */
    private fun findUserByIdentifier(identifier: String): User? {
        return when {
            identifier.contains("@") -> userRepository.findByEmailAddress(identifier)
            identifier.startsWith("pedek") -> userRepository.findByUserId(identifier) // Assuming userId format
            else -> userRepository.findByUserName(identifier)
        }
    }

    /**
     * Password verification helper (implement according to your encryption strategy)
     */
    private fun verifyPassword(user: User, providedPassword: String): Boolean {
       return passwordEncoder.matches(user.userPassword, providedPassword)
    }

    /**
     * Generates a unique user ID
     */
    private fun generateUserId(): String {
        return "pedek${System.currentTimeMillis()}"
    }

    /**
     * Normalizes MAC address format for consistent storage and comparison
     * Converts to uppercase and removes common separators
     */
    private fun normalizeMacAddress(macAddress: String): String {
        return macAddress.replace(Regex("[:-]"), "").uppercase()
    }

    /**
     * Validates MAC address format
     */
    fun isValidDeviceId(id: String): Boolean {
        val regex = Regex("^[a-fA-F0-9]{64}$") // SHA-256: 64 hex characters
        return regex.matches(id)
    }
}

/*


    /*
    POST /auth/register – Register a new user (customer or staff)


POST /auth/login – Authenticate user and issue JWT


GET /auth/profile – Get current user profile (requires auth)


PUT /auth/profile – Update profile info


GET /admin/users – List all users (admin only)


GET /admin/users/{userId} – Get user details (admin)


PUT /admin/users/{userId}/role – Update user role (admin)
     */

    fun registerUser(user: User){
        userRepository.save(user)
    }
    fun loginUser(){}
    fun getUserDetails(){}
    fun getUserProfile(){}
    fun updateUserProfile(){}
    fun getAllUserByAdmin(){}
    fun updateUserDetailsByAdmin(){}
    fun updateUserDetailsByUser(){}
 */