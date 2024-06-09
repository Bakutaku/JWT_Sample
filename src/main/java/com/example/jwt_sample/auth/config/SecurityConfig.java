package com.example.jwt_sample.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.jwt_sample.auth.filter.JwtAuthenticationFilter;
import com.example.jwt_sample.auth.repository.UserRepository;
import com.example.jwt_sample.auth.role.AuthRole;

import lombok.RequiredArgsConstructor;

/**
 * セキュリティのコンフィグ
 */
@Configuration // コンフィグ
@EnableWebSecurity // セキュリティのコンフィグ
@RequiredArgsConstructor // Beanのインスタンス自動生成用
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtFilter; // JWT認証フィルター

  private final UserRepository userRep; // ユーザのデータベース

  // 認証設定
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    // csrf設定
    http.csrf(csrf -> csrf
        .disable() // 無効化
    );

    // session設定
    http.sessionManagement(session -> session
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 無効化
    );

    // アクセス権限設定
    http.authorizeHttpRequests(auth -> auth
        .requestMatchers("/api/auth/**").permitAll() // 認証関係のAPI
        .requestMatchers("/api/public/**").permitAll() // 認証不要のAPI
        .requestMatchers("/api/admin/**").hasRole(AuthRole.ADMIN.toString()) // 管理者用のAPI
        .anyRequest().authenticated() // その他
    );

    // 認証設定
    http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class); // JWT認証

    // 結果を返す。
    return http.build();
  }

  /**
   * パスワードの暗号化
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * パスワード認証のデータベース連携
   */
  @Bean
  public UserDetailsService userDetailsService() {
    // ユーザ名またはメールアドレスからユーザを探す(見つからない場合は例外発生)
    return user -> userRep.findByName(user)
        .orElseGet(() -> userRep.findByEmail(user)
            .orElseThrow(() -> new UsernameNotFoundException("Account Not Found!")));
  }

  /**
   * パスワード認証の設定
   */
  @Bean
  public AuthenticationProvider authenticationProvider() {

    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

    // データベース登録
    authProvider.setUserDetailsService(userDetailsService());

    // パスワードエンコーダー登録
    authProvider.setPasswordEncoder(passwordEncoder());
    return authProvider;
  }

  /**
   * AuthenticationProviderの管理?
   */
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
    return config.getAuthenticationManager();
  }

}
