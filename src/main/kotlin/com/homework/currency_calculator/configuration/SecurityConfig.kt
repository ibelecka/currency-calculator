package com.homework.currency_calculator.configuration
//import jakarta.servlet.http.HttpServletResponse
//import org.springframework.context.annotation.Bean
//import org.springframework.context.annotation.Configuration
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
//import org.springframework.security.config.annotation.web.builders.HttpSecurity
//import org.springframework.security.core.userdetails.User
//import org.springframework.security.core.userdetails.UserDetailsService
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
//import org.springframework.security.crypto.password.PasswordEncoder
//import org.springframework.security.provisioning.InMemoryUserDetailsManager
//import org.springframework.security.web.SecurityFilterChain
//
//@Configuration
//class SecurityConfig {
//
//    @Bean
//    fun passwordEncoder(): PasswordEncoder {
//        return BCryptPasswordEncoder()
//    }
//
//    @Bean
//    fun userDetailsService(): UserDetailsService {
//        val userDetailsService = InMemoryUserDetailsManager()
//        userDetailsService.createUser(User.withUsername("user").password(passwordEncoder().encode("password")).roles("USER").build())
//        userDetailsService.createUser(User.withUsername("admin").password(passwordEncoder().encode("admin")).roles("ADMIN").build())
//        return userDetailsService
//    }
//
//    @Bean
//    fun securityFilterChain(http: HttpSecurity): HttpSecurity {
//        http
//            .authorizeHttpRequests { requests ->
//                requests
//                    .requestMatchers("/public/**").permitAll()  // Public endpoints
//                //.requestMatchers("/admin/**").hasRole("ADMIN")  // Admin-only endpoints
//                //.anyRequest().authenticated()  // All other endpoints require authentication
//            }
//            .csrf { csrf -> csrf.disable()
//    }
//        return http
//
//    }

//}