package com.ql.giapha.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ql.giapha.filter.AuthTokenFilter;
import com.ql.giapha.service.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

        @Autowired
        UserDetailsServiceImpl userDetailsServiceImpl;

        @Autowired
        AuthTokenFilter authTokenFilter;

        @Bean
        public AuthenticationProvider authenticationProvider() {
                DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
                authProvider.setUserDetailsService(userDetailsServiceImpl);
                authProvider.setPasswordEncoder(passwordEncoder());
                return authProvider;
        }

        // @Bean
        // @Primary
        // public AuthenticationManagerBuilder configureAuthenticationManagerBuilder(
        // AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        // authenticationManagerBuilder.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder());
        // return authenticationManagerBuilder;
        // }

        @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
                return authConfig.getAuthenticationManager();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                return http.cors().and().csrf().disable()
                                .authorizeHttpRequests()
                                .requestMatchers("/api/auth/login", "/api/family/**", "api/person/**",
                                                "api/person/delete/**", "api/calendar",
                                                "/api/getInfo", "/api/changePassword")
                                .permitAll()
                                .and()
                                .authorizeHttpRequests()
                                .requestMatchers("/api/family/**", "/api/person/**", "/api/calendar")
                                .hasAnyRole("ADMIN", "USER")
                                .and()
                                .authorizeHttpRequests()
                                .requestMatchers("/api/getInfo").hasRole("USER").and()
                                .authorizeHttpRequests()
                                .requestMatchers("/api/**").hasRole("ADMIN")
                                .anyRequest().authenticated().and()
                                .sessionManagement()
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                                .and()
                                .authenticationProvider(authenticationProvider())
                                .addFilterBefore(authTokenFilter, UsernamePasswordAuthenticationFilter.class)
                                .build();
        }
        // @Bean
        // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // http
        // .cors().and().csrf().disable()
        // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
        // .authorizeHttpRequests(authorize -> authorize
        // .requestMatchers("/login", "/employee", "logout", "/api/department",
        // "/api/auth/**").permitAll()
        // .requestMatchers("/employee/**", "/addDepartment", "/editDepartment",

        // "/deleteDepartment")
        // .hasRole("ADMIN")
        // .requestMatchers("/addvehicle").permitAll()
        // // .anyRequest().authenticated()
        // );
        // http.authenticationProvider(authenticationProvider());
        // http.addFilterBefore(authTokenFilter,
        // UsernamePasswordAuthenticationFilter.class);

        // return http.build();
        // }

}
