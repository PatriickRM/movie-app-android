package com.patrick.movieapp.data.remote.dto

// Request para actualizar perfil
data class UpdateProfileRequest(
    val fullName: String?
)

// Request para cambiar contraseña
data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)


// Response de plan de usuario
data class UserPlanResponse(
    val name: String
)