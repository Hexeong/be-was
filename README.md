# 1주차 직무교육 과제 - 웹서버 만들기
> 해당 Repository는 HMG_Softeer 7기 과정의 직무교육 과제 1주차 내용입니다.

## 기능 요구사항 명세
1. InputStream으로 들어오는 HTTP Request를 파싱하여 로거로 출력
2. 요청 URL에 따라 src/main/resources/static 디렉토리 하위에 존재하는 정적 파일 응답
   - html 파일 외에도 css나 favicon 등과 같은 다양한 컨텐츠 타입도 지원한다.
   - 지원되는 컨텐츠 타입의 확장자 목록: html, css, js, ico, png, jpg

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

## 공부한 내용 정리
- [Github Wiki 링크](https://github.com/Hexeong/be-was/wiki/HMG-Softeer-%EC%A7%81%EB%AC%B4%EA%B5%90%EC%9C%A1-%EA%B3%BC%EC%A0%9C-1%EC%A3%BC%EC%B0%A8-%E2%80%90-%EC%9B%B9%EC%84%9C%EB%B2%84-%EB%A7%8C%EB%93%A4%EA%B8%B0)