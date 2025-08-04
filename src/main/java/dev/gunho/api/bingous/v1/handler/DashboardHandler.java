package dev.gunho.api.bingous.v1.handler;

import dev.gunho.api.bingous.v1.service.DashboardService;
import dev.gunho.api.global.constants.CoreConstants;
import dev.gunho.api.global.model.dto.ApiResponse;
import dev.gunho.api.global.util.ResponseHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static dev.gunho.api.global.constants.CoreConstants.Network.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class DashboardHandler {

    private final DashboardService dashboardService;


    public Mono<ServerResponse> getDashboard(ServerRequest request) {
        return dashboardService.getDashboardData(request)
                .flatMap(data -> {
                    return ResponseHelper.ok(ApiResponse.success(data));
                })
                .onErrorResume(error -> {
                    log.error("Error getting dashboard data ");
                    return ResponseHelper.systemError("대시보드 데이터 조회 중 오류가 발생했습니다.");
                });
    }

//    {
//        "code": "200",
//            "message": "성공",
//            "description": "대시보드 데이터 조회 성공",
//            "timestamp": "2025-08-03T23:30:00Z",
//            "data": {
//        "anniversaries": [
//        {
//            "id": "1",
//                "type": "ANNIVERSARY",
//                "title": "사귄 지 100일",
//                "date": "2025-08-15",
//                "isContinue": 1,
//                "isPrivate": 0,
//                "description": "우리가 사귄 지 100일째 되는 날",
//                "createdAt": "2025-05-01T10:00:00Z",
//                "updatedAt": "2025-05-01T10:00:00Z"
//        }
//    ],
//        "schedules": [
//        {
//            "id": "1",
//                "title": "영화 보기",
//                "description": "아바타 보러 가기",
//                "date": "2025-08-04",
//                "time": "19:00",
//                "location": "CGV 강남",
//                "priority": "high",
//                "status": "pending",
//                "assignedTo": "sebin",
//                "category": "entertainment",
//                "estimatedDuration": 180,
//                "tags": ["데이트", "영화"],
//            "completedAt": null,
//                "completedBy": null,
//                "createdBy": "gunho",
//                "createdAt": "2025-08-03T15:00:00Z",
//                "updatedAt": "2025-08-03T15:00:00Z"
//        }
//    ],
//        "budget": {
//            "total": 634580,
//                    "byUser": {
//                "sebin": 223600,
//                        "gunho": 410980
//            },
//            "items": [
//            {
//                "id": 40,
//                    "coupleId": 3,
//                    "paidBy": "sebin",
//                    "title": "캠핑 식비",
//                    "description": "하이볼 위해 사이다 + 라면 등 구매",
//                    "location": "영월 캠핑장 앞 슈퍼",
//                    "amount": 9300,
//                    "category": "food",
//                    "expenseDate": "2025-08-02",
//                    "createdBy": "sebin",
//                    "createdAt": "2025-08-03T23:23:53",
//                    "updatedAt": "2025-08-03T23:24:10"
//            }
//      ]
//        },
//        "completedTasksThisWeek": 3,
//                "pendingTasksCount": 5,
//                "stats": {
//            "totalAnniversaries": 8,
//                    "completedTasksThisMonth": 12,
//                    "pendingTasks": 5,
//                    "thisMonthTasks": 17,
//                    "daysFromStart": 245
//        }
//    }
//    }
}
