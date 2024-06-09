package com.example.jwt_sample.auth.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.jwt_sample.auth.role.AuthRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ユーザデータ
 */
@Data // getterやsetterの追加
@Builder // コンストラクタを使用しないインスタンス生成するメソッドの追加
@NoArgsConstructor // 引数を必要としないコンストラクタ追加
@AllArgsConstructor // 属性すべてを必要とするコンストラクタ
@Entity // データベースのデータ(おまじないみたいなもの)
@Table(name = "users") // テーブル(おまじないみたいなもの)
public class User implements UserDetails {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id; // ID

  @Column(unique = true) // ユニーク制約
  private String name; // 名前

  @Column(unique = true) // ユニーク制約
  private String email; // メールアドレス

  private String password; // パスワード

  @Enumerated(EnumType.STRING)
  private AuthRole role; // ロール

  private boolean valid; // 有効か (アカウントを無効にするなどに使用)

  /**
   * ロールの取得
   */
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role.name()));
  }

  /**
   * ユーザ名取得
   */
  @Override
  public String getUsername() {
    return this.name;
  }

  /**
   * 有効期限切れかどうか
   */
  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  /**
   * ロックされていないか
   */
  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  /**
   * パスワードなどの有効期限が切れていないか
   */
  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  /**
   * アカウントが有効化されているか
   */
  @Override
  public boolean isEnabled() {
    return this.valid;
  }
}
