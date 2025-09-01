package dev.gunho.api.ongimemo.v1.model.entity;

import dev.gunho.api.bingous.v1.model.enums.Gender;
import dev.gunho.api.bingous.v1.model.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(schema = "ongimemo", name = "users")
public class User {

    @Id
    private Long idx;

    @Column("id")
    private String id;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("password_hash")
    private String passwordHash;

    @Column("nickname")
    private String nickname;

    @Column("gender")
    private Gender gender;

    @Column("status")
    private UserStatus status;

    @Column("email_verified")
    private Boolean emailVerified;

    @Column("phone_verified")
    private Boolean phoneVerified;

    @Column("tryCnt")
    private Integer tryCnt;

    @Column("last_login_at")
    private LocalDateTime lastLoginAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

}
