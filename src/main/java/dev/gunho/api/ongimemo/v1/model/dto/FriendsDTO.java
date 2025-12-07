package dev.gunho.api.ongimemo.v1.model.dto;

import dev.gunho.api.ongimemo.v1.model.enums.Gender;
import lombok.Builder;

public class FriendsDTO {

    @Builder
    public record Response(String id, String email, String phone, String nickname, Gender gender, String bio, boolean status){}

    public record Request (){}
}
