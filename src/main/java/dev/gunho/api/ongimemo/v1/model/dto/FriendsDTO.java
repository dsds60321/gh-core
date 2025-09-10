package dev.gunho.api.ongimemo.v1.model.dto;

import dev.gunho.api.ongimemo.v1.model.enums.Gender;

public class FriendsDTO {

    public record Response(String id, String email, String phone, String nickname, Gender gender, String bio, boolean status){}
}
