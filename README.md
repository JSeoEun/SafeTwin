# SafeTwin

![Version](https://img.shields.io/badge/version-1.0-blue)

AI 기반 건설현장 안전관리 Android 앱

## 소개

SafeTwin은 건설현장의 안전을 디지털 트윈 기술과 AI 영상 분석으로 관리하는 Android 애플리케이션입니다. 현장 사진을 업로드하면 위험 요소를 자동으로 감지하고, 안전 점수와 법적 근거를 함께 제공합니다.

## 주요 기능

| 화면 | 기능 |
|------|------|
| **대시보드** | 안전 점수, 주간 위험 건수, 미해결 항목, 교육 이수율 요약 |
| **AI 분석** | 현장 사진 업로드 → AI 위험 감지 → 위험 등급·법적 근거·조치사항 제공 |
| **디지털 트윈** | 구역(Zone)별 위험 태그를 시각화한 평면도 뷰 |
| **애프터케어** | 감지된 위험 항목 상태 추적 및 조치 관리 |
| **법률/문서** | 관련 법령 검색, 위험성 평가서·교육 이수증 생성 및 전자서명 |

## 기술 스택

- **Language**: Kotlin 2.0.21
- **UI**: Jetpack Compose (BOM 2026.05.00) + Material3
- **Architecture**: MVVM (ViewModel + StateFlow)
- **Navigation**: Navigation Compose 2.8.5
- **Network**: Retrofit2 2.11.0 + OkHttp3 4.12.0 (JWT 자동 갱신 인터셉터)
- **Min SDK**: 26 (Android 8.0)

## 버전 정보

| 항목 | 버전 |
|------|------|
| AGP (Android Gradle Plugin) | 8.13.2 |
| Gradle | 8.13 |
| Kotlin | 2.0.21 |
| Compose BOM | 2026.05.00 |
| AndroidX Core KTX | 1.16.0 |
| Activity Compose | 1.10.1 |
| Lifecycle ViewModel Compose | 2.8.7 |
| Navigation Compose | 2.8.5 |
| Retrofit2 | 2.11.0 |
| OkHttp3 | 4.12.0 |

## 프로젝트 구조

```
app/src/main/java/com/example/safetwin/
├── data/
│   ├── local/          # TokenManager, SessionManager
│   ├── model/          # API 요청/응답 모델
│   └── network/        # ApiService, ApiClient (Retrofit)
├── navigation/         # NavRoutes, AppNavGraph
├── ui/
│   ├── component/      # 공용 컴포넌트 (PrimaryButton, StatusBadge 등)
│   ├── screen/
│   │   ├── auth/       # 로그인, 회원가입
│   │   └── main/       # 대시보드, 분석, 디지털트윈, 애프터케어, 법률
│   └── theme/          # Color, Typography, Theme
├── MainActivity.kt
└── SafeTwinApp.kt
```

## 서버

- **Base URL**: `http://43.201.54.74:8080/`
- 인증 방식: JWT (Access Token + Refresh Token 자동 갱신)

## 빌드 방법

```bash
# 디버그 APK 빌드
./gradlew assembleDebug

# 릴리즈 APK 빌드
./gradlew assembleRelease
```

Android Studio에서 열어 **Run > Run 'app'** 으로 바로 실행할 수도 있습니다.

## API 주요 엔드포인트

| 기능 | 메서드 | 경로 |
|------|--------|------|
| 로그인 | POST | `/api/auth/login` |
| 회원가입 | POST | `/api/auth/signup` |
| 대시보드 요약 | GET | `/api/dashboard/summary` |
| AI 분석 시작 | POST | `/api/analyses` |
| 위험 목록 조회 | GET | `/api/risks` |
| 구역(Zone) 조회 | GET | `/api/topview/zones` |
| 문서 생성 | POST | `/api/docs/risk-assessment` |
| 법령 검색 | GET | `/api/public-data/laws` |
