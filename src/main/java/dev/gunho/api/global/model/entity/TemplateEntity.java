package dev.gunho.api.global.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Table("templates")
public class TemplateEntity {

    @Id
    @Column("idx")
    private Long idx;

    @Column("type")
    private String type;

    @Column("name")
    private String name;

    @Column("content")
    private String content;

    @Column("subject")
    private String subject;

    @Column("isActive")  // 👈 명시적으로 컬럼명 지정
    private boolean isActive;



}
