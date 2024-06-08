package com.example.jwt_sample.auth.role;

/**
 * 権限レベル
 */
public enum AuthRole {
  TEMP, // ゲストユーザ
  USER, // 一般ユーザ
  ADMIN // 管理者権限
}
