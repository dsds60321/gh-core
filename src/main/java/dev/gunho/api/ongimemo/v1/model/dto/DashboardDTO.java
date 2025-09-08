package dev.gunho.api.ongimemo.v1.model.dto;

import java.time.LocalDateTime;
import java.util.List;

public class DashboardDTO {

    public record Request(LocalDateTime reflectionEndDate, LocalDateTime praiseEndDate){}

    public record Response(List<ReflectionDTO.WithRecipientsResponse> reflectionList, List<PraiseDto.WithRecipientsResponse> praiseList){
    }

    public static void main(String[] args) {
        LocalDateTime now = LocalDateTime.now();
        System.out.println(now);
    }
}
