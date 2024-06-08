package com.example.jwt_sample.auth.util;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.jwt_sample.auth.bean.GenerateTokenResponse;

/**
 * JWTの操作関係をまとめたクラス
 */
@Component // おまじないみたいなもの
public class JwtUtil {

  @Value("${jwt.security.key}")
  private String SECURITY_KEY; // セキュリティキー

  @Value("${jwt.security.access.expiration}")
  private int EXPIRATION; // 有効期限

  @Value("${jwt.security.storage.key}")
  private String STORAGE_KEY; // 格納場所

  @Value("${jwt.security.issue.name}")
  private String ISSUE; // 発行者

  @Value("${jwt.security.start.text}")
  private String TOKEN_START; // トークンの始まりの文字

  /**
   * JWTトークン生成
   * 
   * @param userID
   * @return トークン
   */
  public GenerateTokenResponse generateToken(String userID, UUID token_id) {
    // アルゴリズム設定
    Algorithm alg = Algorithm.HMAC256(SECURITY_KEY);

    // 現在時刻取得
    Date now = new Date();

    // 有効期限取得
    Calendar cal = Calendar.getInstance();

    // 現在時刻設定
    cal.setTime(now);

    // 有効期限分追加
    cal.add(Calendar.MINUTE, EXPIRATION);

    // Date型に変換
    Date exp = cal.getTime();

    // トークン生成
    String token = TOKEN_START + JWT.create()
        .withJWTId(token_id.toString()) // トークンID
        .withIssuer(ISSUE) // 発行元
        .withSubject(userID) // 利用者
        .withIssuedAt(now) // 発行日
        .withNotBefore(now) // 有効期限開始日
        .withExpiresAt(exp) // 有効期限
        .sign(alg);

    // 戻り値作成
    GenerateTokenResponse res = GenerateTokenResponse.builder()
        .token(token)
        .now(now)
        .expires(exp)
        .build();

    // 値を返す
    return res;
  }

  /**
   * トークン復号
   * 
   * @param token
   * @return {@link DecodedJWT}
   */
  public DecodedJWT decodeJwt(String token) throws JWTVerificationException {
    DecodedJWT decodedJWT;

    // アルゴリズム設定
    Algorithm alg = Algorithm.HMAC256(SECURITY_KEY);

    // 認証情報の登録
    JWTVerifier verifier = JWT.require(alg)
        .withIssuer(ISSUE) // 発行者
        .build();

    // 復号
    decodedJWT = verifier.verify(token);

    return decodedJWT;
  }

}
