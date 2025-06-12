# 📝 TODO List 

Spring Boot 기반으로 구현한 개인 프로젝트입니다.

사용자별 투두 리스트 관리 기능을 제공합니다.

## 🔗 관련 링크

- [TODO List](http://43.201.96.136/todos)
- [개발 기록](https://velog.io/@leesanghuu/series/투두리스트-만들기)

## 💻 주요 기능

- 사용자별 TODO 항목 CRUD - 추가 / 조회 / 수정 / 삭제
- JWT 기반 사용자 인증 (로그인 없이 임시 토큰 발급)
- 날짜별 할일 관리 기능
- 미완료 할일 자동 이월


## 📌 기술 스택

#### Backend
- **Java 17**
- **Spring Boot**
- **Spring Data JPA**
- **MySQL (AWS RDS)**

#### Infra / DevOps
- **AWS EC2 (Ubuntu)**
- **Nginx**
- **AWS RDS (MySQL)**
- **Git / GitHub**


## 📌 프로젝트 구조

```
com.example.todo
├── config                  # Web 설정
├── controller              # REST API Controller
├── dto                     # 요청/응답 DTO
├── entity                  # JPA Entity
├── exception               # 예외 처리
├── jwt                     # JWT Token 생성/검증 유틸
├── repository              # JpaRepository 인터페이스
└── service                 # Service 로직 (AuthService, TodoService)
```

## 📌 주요 API 

<img width="847" alt="스크린샷 2025-06-13 오전 5 22 16" src="https://github.com/user-attachments/assets/7a2128f6-dece-4167-852e-28df03d343d7" />



## 🚀 개발하며 배운 점

- **JWT 인증 플로우 구현 경험**
  - JWT 토큰 발급/검증을 직접 구현하면서 HMAC 기반 알고리즘(HS256) 사용 경험
  - Interceptor를 활용해 인증 흐름 설계 시 FilterChain과 HandlerInterceptor의 차이점 이해
  - SecurityContextHolder 없이 ThreadLocal 기반 UserContextHolder 설계 및 적용 경험

- **RESTful API 설계 원칙 적용**
  - RESTful URI 설계 
  - Swagger 적용 시 API 설계 개선 (DTO 별도 설계, 명확한 Response 형식 설계)

- **계층별 설계와 테스트 가능성 고려**
  - DTO ↔ Entity 변환 시 명시적 매핑 사용 → 의도 명확화
  - `RestControllerAdvice` 적용을 통한 전역 예외 처리 경험
  - 생성자 주입 방식 적용으로 테스트 용이성 확보

- **테스트 작성**
  - JUnit 5, Mockito, MockMvc 활용
  - Service/Controller 레이어에서 각각 단위 테스트/API 테스트 구성 경험
  - JWT 인증이 필요한 API 테스트 시, 토큰 발급부터 포함하여 end-to-end 흐름 테스트 구성
  - 예외 상황과 경계 케이스(존재하지 않는 투두 접근 등) 테스트 경험
  - 테스트 작성 경험을 통해 API 설계 오류 및 경계 케이스 개선

- **배포 경험**
  - EC2 서버에 Spring Boot 프로젝트 배포 경험
  - Gradle build 후 Jar 파일 실행 및 Nginx reverse proxy 구성 경험


## 🔍 후속 학습 계획

- **CI/CD 파이프라인 구축**
  - GitHub Actions 또는 Jenkins 기반 CI/CD 파이프라인 구축 예정
  - 테스트 자동화 → 빌드 성공 → EC2 배포까지 자동화 목표

- **테스트 커버리지 향상**
  - JaCoCo 적용 → 테스트 커버리지 측정 및 향상

- **보안 고도화**
  - JWT 토큰 관리 고도화 (Refresh Token 저장 전략 개선, 만료 정책 재검토)
  - OAuth2 기반 소셜 로그인 학습 및 적용 시도
  
