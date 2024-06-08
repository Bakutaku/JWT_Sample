package com.example.jwt_sample.auth.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.jwt_sample.auth.model.User;

/**
 * Usersテーブルのレポジトリ
 */
public interface UserRepository extends JpaRepository<User, UUID> {

  /**
   * ユーザ名からユーザを検索する
   * 
   * @param name ユーザ名
   * @return {@link User}
   */
  Optional<User> findByName(String name);

  /**
   * メールアドレスからユーザを検索する
   * 
   * @param email メールアドレス
   * @return {@link User}
   */
  Optional<User> findByEmail(String email);

}