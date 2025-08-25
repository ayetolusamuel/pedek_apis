package com.pedektech.pedek_catering.models

import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "users")
@SequenceGenerator(
    name = "user_seq",
    sequenceName = "user_sequence",
    initialValue = 1,
    allocationSize = 1
)
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    val id: Long? = null,

    @Column(name = "user_id", unique = true, nullable = false)
    var userId: String? = null,
    val fullName: String,
    val userName:String,
    val phoneNumber: String? = null,
    @Enumerated(EnumType.STRING)
    val role: Role = Role.CUSTOMER,
    @Column(name = "created_at")
    @Temporal(TemporalType.DATE)
    val createdAt: Date? = null,

    @Column(name = "modified_at")
    @Temporal(TemporalType.DATE)
    var modifiedAt: Date? = null,

    @Column(name = "email_address", unique = true, nullable = false)
    var emailAddress: String? = null,

    @Column(name = "pic_url")
    var picUrl: String? = null,

    @Column(name = "access_device")
    var accessDevice: String? = null,


    @Column(name = "user_password", nullable = false)
    var userPassword: String? = null,  // Changed to var for mutability| // Will be null for social accounts

    var isActive:Boolean = true,

    val provider: String? = null, // e.g., "GOOGLE", "FACEBOOK"
    val providerId: String? = null // The unique ID from Google/Facebook
)

enum class Role {
    CUSTOMER, ADMIN, STAFF
}

/**
* Enum for different device access actions
*/
enum class DeviceAction {
    ALLOW_ACCESS,
    CREATE_NEW_ACCOUNT,
    ATTACH_TO_EXISTING_USER,
    DEVICE_CHANGE_DETECTED,
    ACCOUNT_INACTIVE,
    RESOLVE_CONFLICT
}

/**
 * Result class for account creation
 */
data class CreateAccountResult(
    val status: Boolean,
    val message: String,
    val userId: String?,
    val createdUser: User? = null,
    val existingUser: User? = null
)

/**
 * Result class for device change operations
 */
data class DeviceChangeResult(
    val success: Boolean,
    val message: String,
    val requiresLogin: Boolean,
    val user: User? = null,
    val oldDevice: String? = null,
    val newDevice: String? = null
)

/**
 * Request class for creating new user
 */
data class CreateUserRequest(
    val fullName: String,
    val userName: String,
    val emailAddress: String,
    val phoneNumber: String?,
    val password: String,
    val role: Role? = Role.CUSTOMER,
    val provider: String? = null,
    val providerId: String? = null
)
/**
 * Enhanced result class for device access validation
 */
data class DeviceAccessResult(
    val status:Boolean,
    val isValid: Boolean,
    val isAttachedToAccount: Boolean,
    val hasMultipleAccounts: Boolean,
    val action: DeviceAction? = null,
    val message: String,
    val suggestedAction: String? = null,
    val associatedUser: User? = null,
    val conflictingUsers: List<User> = emptyList()
)

/**
 * Result class for device attachment operations
 */
data class AttachDeviceResult(
    val status: Boolean,
    val message: String
)


/**
 * Request body for device change
 */
data class DeviceChangeRequest(
    val userIdentifier: String, // email, username, or userId
    val newMacAddress: String,
    val currentPassword: String? = null
)

/**
 * Request body for attaching device
 */
data class AttachDeviceRequest(
    val macAddress: String
)
