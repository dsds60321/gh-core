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
//            "message": "대시보드 데이터 조회 성공",
//            "description": "Dashboard data retrieved successfully",
//            "timestamp": "2025-07-28T13:00:00Z",
//            "data": {
//        "anniversaries": [
//        {
//            "id": "uuid-1",
//                "type": "ANNIVERSARY",
//                "title": "처음 만난 날",
//                "date": "2025-08-15",
//                "isContinue": 1,
//                "isPrivate": 0,
//                "description": "소중한 첫만남",
//                "createdAt": "2025-01-01T10:00:00Z",
//                "updatedAt": "2025-01-01T10:00:00Z"
//        },
//        {
//            "id": "uuid-2",
//                "type": "BIRTHDAY",
//                "title": "생일",
//                "date": "2025-09-20",
//                "isContinue": 1,
//                "isPrivate": 0,
//                "description": "생일 축하해",
//                "createdAt": "2025-01-01T10:00:00Z",
//                "updatedAt": "2025-01-01T10:00:00Z"
//        },
//        {
//            "id": "uuid-3",
//                "type": "ANNIVERSARY",
//                "title": "6개월 기념일",
//                "date": "2025-12-15",
//                "isContinue": 0,
//                "isPrivate": 0,
//                "description": "6개월 축하",
//                "createdAt": "2025-01-01T10:00:00Z",
//                "updatedAt": "2025-01-01T10:00:00Z"
//        }
//    ],
//        "schedules": [
//        {
//            "id": "todo-1",
//                "title": "영화 보기",
//                "description": "아바타 보러가기",
//                "date": "2025-07-28",
//                "location": "CGV 강남",
//                "priority": "high",
//                "status": "pending",
//                "assignedTo": "user-1",
//                "category": "데이트",
//                "estimatedDuration": 180,
//                "tags": ["영화", "데이트"],
//            "completedAt": null,
//                "completedBy": null,
//                "createdBy": "user-1",
//                "createdAt": "2025-07-28T09:00:00Z",
//                "updatedAt": "2025-07-28T09:00:00Z"
//        },
//        {
//            "id": "todo-2",
//                "title": "카페 가기",
//                "description": "새로 생긴 카페 체크",
//                "date": "2025-07-29",
//                "location": "홍대 카페거리",
//                "priority": "medium",
//                "status": "pending",
//                "assignedTo": "user-2",
//                "category": "데이트",
//                "estimatedDuration": 120,
//                "tags": ["카페", "데이트"],
//            "completedAt": null,
//                "completedBy": null,
//                "createdBy": "user-2",
//                "createdAt": "2025-07-28T10:00:00Z",
//                "updatedAt": "2025-07-28T10:00:00Z"
//        },
//        {
//            "id": "todo-3",
//                "title": "쇼핑하기",
//                "description": "여름옷 쇼핑",
//                "date": "2025-08-02",
//                "location": "명동",
//                "priority": "low",
//                "status": "pending",
//                "assignedTo": null,
//                "category": "쇼핑",
//                "estimatedDuration": 240,
//                "tags": ["쇼핑", "옷"],
//            "completedAt": null,
//                "completedBy": null,
//                "createdBy": "user-1",
//                "createdAt": "2025-07-28T11:00:00Z",
//                "updatedAt": "2025-07-28T11:00:00Z"
//        }
//    ],
//        "completedTasksThisWeek": 3,
//                "pendingTasksCount": 7,
//                "stats": {
//            "totalAnniversaries": 8,
//                    "completedTasksThisMonth": 15,
//                    "pendingTasks": 7,
//                    "thisMonthTasks": 22,
//                    "daysFromStart": 1
//        }
//    }
//    };
}
