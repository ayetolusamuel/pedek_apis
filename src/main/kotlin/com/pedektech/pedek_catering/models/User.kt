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
    val name: String,
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


    @Column(name = "user_password", nullable = false)
    var userPassword: String? = null,  // Changed to var for mutability| // Will be null for social accounts

    var isActive:Boolean = true,




    val provider: String? = null, // e.g., "GOOGLE", "FACEBOOK"
    val providerId: String? = null // The unique ID from Google/Facebook
)

enum class Role {
    CUSTOMER, ADMIN, STAFF
}
