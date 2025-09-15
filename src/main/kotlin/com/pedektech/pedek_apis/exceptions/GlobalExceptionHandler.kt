package com.pedektech.pedek_apis.exceptions


import io.jsonwebtoken.ExpiredJwtException
import org.hibernate.PropertyValueException
import org.springframework.dao.DataAccessException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.orm.jpa.JpaSystemException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import java.sql.SQLIntegrityConstraintViolationException
import java.time.LocalDateTime
import java.util.regex.Pattern

class DuplicateProductException(message: String) : RuntimeException(message)



@ControllerAdvice
class GlobalExceptionHandler {


    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "timestamp" to LocalDateTime.now(),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "Bad Request",
            "message" to ex.message.toString(),
            "path" to "/api/favourites/toggle" // you can dynamically fetch the URL if needed
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(JpaSystemException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleJpaSystemException(ex: JpaSystemException): ResponseEntity<Map<String, Any>> {
        val errorMessage = if (ex.rootCause?.message?.contains("relation") == true && ex.rootCause?.message?.contains("does not exist") == true) {
            "The requested resource is currently unavailable. Please try again later."
        } else {
            "An unexpected error occurred. Please try again later."
        }

        val errorResponse = mapOf(
            "message" to errorMessage,
            "status" to false,
            "error" to "Internal Server Error"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(DataAccessException::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleDataAccessException(ex: DataAccessException): ResponseEntity<Map<String, Any>> {
        val errorMessage = if (ex.rootCause?.message?.contains("relation") == true && ex.rootCause?.message?.contains("does not exist") == true) {
            "The requested resource is currently unavailable. Please try again later."
        } else {
            "An unexpected error occurred. Please try again later."
        }

        val errorResponse = mapOf(
            "message" to errorMessage,
            "status" to false,
            "error" to "Internal Server Error"
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }


    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<Map<String, Any>> {
        val errorMessage = parseErrorMessage(ex.localizedMessage)
        val errorResponse = mapOf(
            "message" to errorMessage,
            "status" to false,
            "error" to ex.localizedMessage
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(PropertyValueException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePropertyValueException(ex: PropertyValueException): ResponseEntity<Map<String, Any>> {
        val fieldName = extractFieldName(ex.message)
        val errorResponse = mapOf(
            "message" to "The field '$fieldName' cannot be null or empty. Please provide a valid value.",
            "status" to false,
            "error" to ex.localizedMessage
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleSQLIntegrityConstraintViolationException(ex: SQLIntegrityConstraintViolationException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "message" to "A duplicate entry was found. Please ensure all unique fields are unique.",
            "status" to false,
            "error" to ex.localizedMessage
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ExpiredJwtException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleExpiredJwtException(ex: ExpiredJwtException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "message" to "Your session has expired. Please log in again.",
            "status" to false,
            "error" to ex.localizedMessage
        )
        return ResponseEntity(errorResponse, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "message" to "The ${ex.method} method is not supported for this endpoint. Please use one of the supported methods: ${ex.supportedHttpMethods?.joinToString(", ")}.",
            "status" to false,
            "error" to ex.localizedMessage
        )
        return ResponseEntity(errorResponse, HttpStatus.METHOD_NOT_ALLOWED)
    }


    @ExceptionHandler(DuplicateProductException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateProductException(ex: DuplicateProductException): ResponseEntity<Map<String, Any>> {
        // Create a response map with the error details
        val errorResponse = mapOf(
            "message" to (ex.message ?: "A duplicate entry error occurred."), // Provide a default message if none is available
            "status" to false,
            "error" to "Conflict" // Descriptive error type
        )

        // Return a ResponseEntity with the error response and CONFLICT status
        return ResponseEntity(errorResponse, HttpStatus.CONFLICT)
    }


    @ExceptionHandler(MissingServletRequestParameterException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMissingServletRequestParameterException(ex: MissingServletRequestParameterException): ResponseEntity<Map<String, Any>> {
        val errorResponse = mapOf(
            "message" to "The required request parameter '${ex.parameterName}' is missing. Please provide it and try again.",
            "status" to false,
            "error" to ex.localizedMessage
        )
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    private fun parseErrorMessage(message: String): String {
        val enumPattern = Pattern.compile(
            """Cannot deserialize value of type `[^`]+\.([^`]+)` from String "[^"]+": not one of the values accepted for Enum class: \[(.+)]"""
        )
        val datePattern = Pattern.compile(
            """Cannot deserialize value of type `java\.util\.Date` from String "[^"]+": not a valid representation"""
        )
        val matcherEnum = enumPattern.matcher(message)
        val matcherDate = datePattern.matcher(message)

        return when {
            matcherEnum.find() -> {
                val enumType = matcherEnum.group(1)
                val acceptedValues = matcherEnum.group(2)
                "Invalid value provided for '$enumType'. Please use one of the following: [$acceptedValues]"
            }
            matcherDate.find() -> {
                "Invalid date format provided. Please use one of the standard formats: yyyy-MM-dd'T'HH:mm:ss.SSSX, yyyy-MM-dd'T'HH:mm:ss.SSS, EEE, dd MMM yyyy HH:mm:ss zzz, or yyyy-MM-dd."
            }
            else -> {
                "Invalid request data."
            }
        }
    }

    private fun extractFieldName(message: String?): String {
        val pattern = Pattern.compile(
            """not-null property references a null or transient value : [^\.]+\.(\w+)"""
        )
        val matcher = pattern.matcher(message ?: "")
        return if (matcher.find()) matcher.group(1) else "Unknown field"
    }
}
