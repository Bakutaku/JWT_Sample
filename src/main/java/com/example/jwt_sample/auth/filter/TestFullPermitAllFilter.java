package com.example.jwt_sample.auth.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * 全ての通信を認証済みにするテスト用のフィルター
 */
@Component
@RequiredArgsConstructor
public class TestFullPermitAllFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    // 仮ユーザ作成
    UserDetails user = User
        .withUsername("User")
        .password("Test")
        .roles("ADMIN")
        .build();

    // 認証トークン作成
    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null, null);

    // 認証情報登録(認証済みにする)
    SecurityContextHolder.getContext().setAuthentication(authToken);

    // 次のフィルターへ
    filterChain.doFilter(request, response);

  }
}
