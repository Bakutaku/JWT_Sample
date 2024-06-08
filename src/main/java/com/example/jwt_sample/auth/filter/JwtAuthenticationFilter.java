package com.example.jwt_sample.auth.filter;

import java.io.IOException;

import org.slf4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.jwt_sample.auth.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * JWTのセキュリティフィルター
 */
@Component // おまじないみたいなもの
@RequiredArgsConstructor // インスタンス自動生成
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtService jwt; // JWT操作用

  private final Logger logger; // ログ

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    try {
      // トークンを取り出す
      final String token = jwt.getToken(request);

      // トークンがない場合
      if (token == null) {
        // 次のフィルターへ
        filterChain.doFilter(request, response);
      }

      // トークンを認証する(失敗したら例外発生)
      UserDetails user = jwt.authenticate(token);

      // 認証トークン作成(認証情報の登録に使用するもの)
      UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(user, null,
          user.getAuthorities());

      // 認証情報登録(認証済みにする)
      SecurityContextHolder.getContext().setAuthentication(authToken);

      // } catch (JWTVerificationException e) {
      // // JWTトークンのエラー

    } catch (Exception e) {
      logger.debug("JWTログインに失敗しました\n内容:" + e.getMessage());
    }

    // 次のフィルターへ
    filterChain.doFilter(request, response);
  }

}
