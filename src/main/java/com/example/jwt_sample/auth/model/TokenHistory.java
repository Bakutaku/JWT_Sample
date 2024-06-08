package com.example.jwt_sample.auth.model;

import java.util.Date;
import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 発行したトークンのIDを保存するテーブル
 */
@Data // getterやsetterの追加
@Builder // コンストラクタを使用しないインスタンス生成するメソッドの追加
@NoArgsConstructor // 引数を必要としないコンストラクタ追加
@AllArgsConstructor // 属性すべてを必要とするコンストラクタ
@Entity // データベースのデータ(おまじないみたいなもの)
@Table(name = "tokens") // テーブル(おまじないみたいなもの)
public class TokenHistory {

  @Id
  @GeneratedValue
  private Integer id; // ID

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  private User user; // 発行者
  private UUID tokenId; // トークンID
  private UUID refreshToken; // リフレッシュトークン
  private Date issuedDate; // 発行日
  private Date expiresDate; // 有効期限
}