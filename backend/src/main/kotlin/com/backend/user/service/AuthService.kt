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
    private val animalNames =
        listOf(
            "Cat",
            "Lion",
            "Tiger",
            "Elephant",
            "Giraffe",
        )

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
                username = generateUsername(),
                email = email,
                passwordHash = requireNotNull(passwordEncoder.encode(password)),
            )

        return userRepository.save(user)
    }

    private fun generateUsername(): String {
        val animal = animalNames.random()
        val number = userRepository.count() + 1

        return "$animal$number"
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
