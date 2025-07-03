package dev.gunho.api.bingous.v1.model.entity;

import dev.gunho.api.bingous.v1.model.enums.Gender;
import dev.gunho.api.bingous.v1.model.enums.UserStatus;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("users")
public class User implements Persistable<String> {

    @Id
    private String id;

    @Column("email")
    private String email;

    @Column("phone")
    private String phone;

    @Column("password_hash")
    private String passwordHash;

    @Column("nickname")
    private String nickname;

    @Column("profile_image_url")
    private String profileImageUrl;

    @Column("birth_date")
    private LocalDate birthDate;

    @Column("gender")
    private Gender gender;

    @Column("status")
    private UserStatus status;

    @Column("email_verified")
    private Boolean emailVerified;

    @Column("phone_verified")
    private Boolean phoneVerified;

    @Column("last_login_at")
    private LocalDateTime lastLoginAt;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("updated_at")
    private LocalDateTime updatedAt;

    @Setter
    @Transient
    private boolean isNew = true;

    @Override
    @Transient
    public boolean isNew() {
        return isNew;
    }


    /**
     * 엔티티가 저장된 후 호출되어 isNew를 false로 설정
     */
    public User markNotNew() {
        this.isNew = false;
        return this;
    }

}
