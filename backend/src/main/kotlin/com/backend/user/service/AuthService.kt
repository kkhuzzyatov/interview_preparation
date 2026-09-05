package com.backend.user.service

import com.backend.exceptions.UserAlreadyExistsException
import com.backend.exceptions.UserIsNotExistException
import com.backend.jwt.JwtProvider
import com.backend.user.controller.dto.LoginResult
import com.backend.user.entity.User
import com.backend.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.UUID

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtProvider: JwtProvider,
) {
    fun register(
        email: String,
        password: String,
    ): User {
        if (userRepository.existsByEmail(email)) {
            throw UserAlreadyExistsException("Email уже зарегистрирован")
        }

        val user =
            User(
                id = UUID.randomUUID(),
                email = email,
                passwordHash = passwordEncoder.encode(password),
            )

        return userRepository.save(user)
    }

    fun login(
        email: String,
        password: String,
    ): LoginResult {
        val user =
            userRepository.findByEmail(email)
                ?: throw IllegalArgumentException("Wrong email or password")

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw IllegalArgumentException("Wrong email or password")
        }

        val token = jwtProvider.generate(user.id, user.email)

        return LoginResult(token)
    }

    fun getMyUuid(userId: UUID): User = getUserById(userId)

    fun getUserById(userId: UUID): User =
        userRepository
            .findById(userId)
            .orElseThrow {
                UserIsNotExistException("User with id $userId does not exist")
            }
}
