package dev.gunho.api.bingous.v1.util;

import dev.gunho.api.bingous.v1.model.enums.DeviceType;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.server.reactive.ServerHttpRequest;

@Getter
@Builder
public class DeviceInfoUtil {

    private DeviceType deviceType;
    private String deviceName;
    private String userAgent;
    private String ipAddress;

    public static DeviceInfoUtil extractFromRequest(ServerHttpRequest request) {
        String userAgentString = request.getHeaders().getFirst("User-Agent");
        String deviceId = request.getHeaders().getFirst("X-Device-ID"); // 앱에서 보내는 커스텀 헤더

        UserAgent userAgent = UserAgent.parseUserAgentString(userAgentString);

        DeviceType deviceType = determineDeviceType(userAgentString, userAgent);
        String deviceName = buildDeviceName(userAgent);
        String ipAddress = extractIpAddress(request);

        return DeviceInfoUtil.builder()
                .deviceType(deviceType)
                .deviceName(deviceName)
                .userAgent(userAgentString)
                .ipAddress(ipAddress)
                .build();
    }

    private static DeviceType determineDeviceType(String userAgentString, UserAgent userAgent) {
        if (userAgentString == null) return DeviceType.WEB;

        String lowerUA = userAgentString.toLowerCase();

        // React Native 앱 감지
        if (lowerUA.contains("bingous-app")) { // 앱에서 커스텀 User-Agent 사용
            if (lowerUA.contains("ios")) return DeviceType.IOS;
            if (lowerUA.contains("android")) return DeviceType.ANDROID;
        }

        // 일반적인 모바일 감지
        if (userAgent.getOperatingSystem().isMobileDevice()) {
            if (lowerUA.contains("iphone") || lowerUA.contains("ipad")) {
                return DeviceType.IOS;
            }
            if (lowerUA.contains("android")) {
                return DeviceType.ANDROID;
            }
        }

        return DeviceType.WEB;
    }

    private static String buildDeviceName(UserAgent userAgent) {
        StringBuilder deviceName = new StringBuilder();

        if (userAgent.getOperatingSystem() != null) {
            deviceName.append(userAgent.getOperatingSystem().getName());
        }

        if (userAgent.getBrowser() != null) {
            if (deviceName.length() > 0) deviceName.append(" / ");
            deviceName.append(userAgent.getBrowser().getName());
        }

        return deviceName.length() > 0 ? deviceName.toString() : "Unknown Device";
    }

    private static String extractIpAddress(ServerHttpRequest request) {
        // X-Forwarded-For 헤더 체크 (프록시/로드밸런서 뒤에 있을 때)
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        // X-Real-IP 헤더 체크
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        // 직접 연결된 IP
        if (request.getRemoteAddress() != null) {
            return request.getRemoteAddress().getAddress().getHostAddress();
        }

        return "unknown";
    }
}
