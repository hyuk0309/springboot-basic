# SpringBoot Basic Weekly Mission

## 프로젝트 소개

데브코스에서 3주 동안 스프링부트를 학습하면서, 학습 내용을 적용시켜 점진적으로 발전시킨 개인 프로젝트.

도메인: 쿠폰 관리 시스템.

<br/>

## 프로젝트 환경

### Front-End

- Terminal
- Spring Thymeleaf

### Back-End

- Spring Boot
- Spring Core
- Spring MVC
- Spring JDBC
- Spring Thymeleaf

### Database

- MySQL

<br/>

## 프로젝트 상세 소개

### Week 1

Spring Core를 이용해 콘솔 쿠폰 관리 시스템 만들기.

<br/>

**구현 기능**

- 쿠폰 저장 기능 (직렬화 이용 파일에 저장)
- 쿠폰 조회 기능 (역직렬화를 이용 파일에서 불러오기)
- 고객 블랙 리스트 명단 조회.

<br/>

### Week 2

Spring Jdbc를 이용해 기존 파일로 관리하던 데이터를 MySQL을 이용해 관리하기.

<br/>

**구현 기능**

- Week 1에서 만든 기능을 MySQL에서 가능하도록 이전시키기.
- 고객 테이블 추가
  - 고객 생성 기능.
  - 고객 조회 기능.
- 특정 고객에게 바우처 할당 기능.
- 고객이 어떤 바우처를 보유하고 있는지 조회 기능.
- 고객이 보유한 바우처 제거 기능.
- 특정 바우처를 보유한 고객 조회 기능.

<br/>

### Week 3

Spring MVC를 적용해 Console이 아닌, Web에서 서비스 이용하게 기능 추가하기.

<br/>

- 관리자 페이지 개발
  - 쿠폰
    - 쿠폰 전체 조회 페이지.
      - GET /vouchers
    - 쿠폰 상세 조회 페이지.
      - GET /vouchers/{voucherId}
    - 쿠폰 입력 페이지.
      - GET /vouchers/new
    - 쿠폰 입력 요청.
      - POST /vouchers/new
      - Form
        ```
        "voucherType" : "fixed" | "percent"
        "amount" : 2000 (fixed 타입 경우)
        "percent" : 20 (percent 타입 경우)
        ```
    - 쿠폰 삭제 기능.
      - POST /vouchers/{voucherId}/delete

  - 손님
    - 손님 전체 조회 페이지.
      - GET /customers
    - 손님 상세 조회 페이지.
      - GET /customers/{customerId}
    - 손님 등록 페이지.
      - GET /customers/new
    - 손님 등록 요청.
      - POST /customers/new
      - Form
        ```
        "name" : "pang"
        "email" : "pang@gmail.com"
        ```
    - 손님 삭제 기능.
      - POST /customers/{customerId}/delete

<br/>

- API 개발 (/api/v1)
  - 전체 쿠폰 조회 API.
    - GET /vouchers
    - Query Paramters
      ~~~
      //특정 바우처 타입 조회
      "voucherType" : "fixed" | "percent" 
      //특정 기간에 생성된 바우처 조회
      "start" : "2022-04-27"
      "end" : "2022-04-29"
      ~~~
  - 쿠폰 생성 API.
    - POST /vouchers
    - Body(Json)
      ```
      {
        "voucherType" : "fixed" | "percent",
        "amount" : 2000, (fixed 타입 경우)
        "percent" : 50,  (percent 타입 경우)
      }
      ```
  - 쿠폰 삭제 API
    - DELETE /vouchers/{voucherId}
  - 특정 쿠폰 조회 API.
    - GET /vouchers/{voucherId}

<br/>

## 최종 프로젝트 설계도

- 도메인 설계도

![domain](https://user-images.githubusercontent.com/29492667/169812256-86b49f5e-d954-4082-b06e-b1b24a3555cd.png)

<br/>

- 테이블 설계도

![database](https://user-images.githubusercontent.com/29492667/169812334-12c05037-1a62-49c5-8ad3-cbe71f6691a4.png)

<br/>

- 프로젝트 설계도

![class](https://user-images.githubusercontent.com/29492667/169812397-3770af93-e362-4c6d-abdc-f671cff3d383.png)
