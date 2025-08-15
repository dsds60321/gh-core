package dev.gunho.api.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${firebase.service-account-file:}")
    private String serviceAccountFile;

    @PostConstruct
    public void initializeFirebase() {
        try {
            if (FirebaseApp.getApps().isEmpty()) {
                GoogleCredentials credentials = getCredentials();

                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(credentials)
                        .build();

                FirebaseApp.initializeApp(options);
                log.info("Firebase 초기화 완료");
            }
        } catch (IOException e) {
            log.error("Firebase 초기화 실패", e);
            throw new RuntimeException("Firebase 초기화 실패", e);
        }
    }

    private GoogleCredentials getCredentials() throws IOException {
        if (!serviceAccountFile.isEmpty()) {
            log.info("Firebase 서비스 계정 파일 로드: {}", serviceAccountFile);

            // 절대 경로인지 확인
            if (serviceAccountFile.startsWith("/") || serviceAccountFile.contains(":")) {
                // 절대 경로로 파일 읽기
                try (InputStream serviceAccount = new FileInputStream(serviceAccountFile)) {
                    return GoogleCredentials.fromStream(serviceAccount);
                }
            } else {
                // 클래스패스에서 파일 읽기 (src/main/resources 하위)
                Resource resource = new ClassPathResource(serviceAccountFile);
                if (resource.exists()) {
                    try (InputStream serviceAccount = resource.getInputStream()) {
                        return GoogleCredentials.fromStream(serviceAccount);
                    }
                } else {
                    log.warn("클래스패스에서 Firebase 서비스 계정 파일을 찾을 수 없음: {}", serviceAccountFile);
                    throw new IOException("Firebase 서비스 계정 파일을 찾을 수 없습니다: " + serviceAccountFile);
                }
            }
        } else {
            // 기본 애플리케이션 자격증명 사용 (GCP 환경)
            log.info("기본 애플리케이션 자격증명 사용");
            return GoogleCredentials.getApplicationDefault();
        }
    }
}
