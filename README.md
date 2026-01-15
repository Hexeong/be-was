# 1주차 직무교육 과제 - 웹서버 만들기
> 해당 Repository는 HMG_Softeer 7기 과정의 직무교육 과제 1주차 내용입니다.

## 기능 요구사항 명세
1. InputStream으로 들어오는 HTTP Request를 파싱하여 로거로 출력
2. 요청 URL에 따라 src/main/resources/static 디렉토리 하위에 존재하는 정적 파일 응답
   - html 파일 외에도 css나 favicon 등과 같은 다양한 컨텐츠 타입도 지원한다.
   - 지원되는 컨텐츠 타입의 확장자 목록: html, css, js, ico, png, jpg
4. 쿠키와 세션을 사용한 유저 인증 방식 지원
5. 커스텀 템플릿 엔진을 사용하여 동적 렌더링을 지원
6. 게시글, 댓글, 사용자 관련 CRUD 기능
   - DB는 인메모리 H2 데이터베이스를 사용한다.
   - 각 도메인의 Create 요청은 리다이렉트를 응답으로 내보낸다.
7. 에러 처리 페이지 구현
   - 404 포함 다양한 에러 상태에 대해서 에러 페이지를 구현한다.
8. 파일 업로드 기능 및 유해 파일 업로드 방지
   - 저장한 이미지는 게시글 조회시 같이 보여져야 한다.

## 프로젝트 구조
```
be-was/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── annotation/       # 라우팅 및 의존성 관련 어노테이션
│   │   │   ├── dao/              # 데이터베이스와 상호작용하는 DAO
│   │   │   ├── db/               # 데이터베이스 커넥션 및 세션 관리
│   │   │   ├── exception/        # 커스텀 예외 처리
│   │   │   ├── handler/          # HTTP 요청을 처리하는 핸들러
│   │   │   ├── interceptor/      # 요청 전후 처리 로직을 담은 인터셉터
│   │   │   ├── model/            # User, Article 등 데이터 모델
│   │   │   ├── proxy/            # 트랜잭션 관리를 위한 프록시
│   │   │   ├── resolver/         # 어노테이션 기반 인자 및 뷰 리졸버
│   │   │   ├── util/             # 각종 유틸리티 클래스
│   │   │   └── webserver/        # 웹서버의 핵심 로직
│   │   └── resources/
│   │       ├── templates/        # 동적 HTML 템플릿
│   │       └── static/           # 정적 리소스
│   └── test/
│       └── java/
├── docs/                         # Javadoc 문서
└── build.gradle                  # 프로젝트 빌드 및 의존성 설정
```

## 프로그래밍 요구사항 명세
1. 개발 환경: JDK-17, IntelliJ IDE 사용
2. 프로젝트 분석: 프로젝트의 동작 원리를 파악하기.
3. 기존 코드의 구조 변경: Thread 직접 호출이 아닌 Concurrent 패키지 기반 스레드 관리로 변경
4. OOP와 클린코딩 적용하기

## 제한 사항
1. build.gradle에 초기에 주어진 패키지 외 외부 패키지 의존성을 최소화한다.
2. JDK의 nio는 사용하지 않는다.
3. Lombok은 사용하지 않는다.
4. MVC와 관련된 Naming은 사용하지 않는다.(Controller, Service, DTO, Repository 등)

## 문서
- 자세한 클래스 및 메소드 정보는 [Javadoc](https://hexeong.github.io/be-was/docs/index.html)을 참고해주세요.

## 공부한 내용 정리
- [Github Wiki 링크](https://github.com/Hexeong/be-was/wiki)
