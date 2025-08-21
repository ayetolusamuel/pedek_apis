//package com.pedektech.pedek_catering.repositories
//
//import com.pedektech.pedek_catering.models.Campaign
//import com.pedektech.pedek_catering.models.User
//import org.springframework.data.jpa.repository.JpaRepository
//import org.springframework.data.jpa.repository.Query
//import org.springframework.data.repository.query.Param
//
//interface UserRepository : JpaRepository<User, Long> {
//    fun findByUserName(userName: String): User?
//    fun findByUserId(userId: String): User?
//    fun findByEmailAddress(emailAddress: String): User?
//    @Query("""
//    SELECT u FROM User u WHERE
//    LOWER(u.userName) LIKE LOWER(CONCAT('%', :query, '%'))
//    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :query, '%'))
//""")
//    fun searchUsersByUsernameOrFullName(@Param("query") query: String): List<User>
//
//    fun existsByEmailAddress(email: String): Boolean
//    fun existsByUserName(username: String): Boolean
//}
