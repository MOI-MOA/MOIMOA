<img src="우리 어플 아이콘" />

# 1. 프로젝트 개요

### 📋 서비스 개요

- 모임 공금관리 서비스스
- SSAFY 은행 API를 사용하여 **선거를 온라인으로 진행**할 수 있도록 돕는
  서비스입니다.
- **프로젝트 기간:** 2025/03/03 ~ 2025/04/11 (총총40일간)

### 💰 **서비스 특징**

1.
2.
3.
4.

### 👭팀원 정보 및 업무 분담 내역

| 이름           | 역할 및 구현 기능                    |
| -------------- | ------------------------------------ |
| 🟧진종수(팀장) | **Backend**<br>- ERD설계<br><br><br> |
| 🟩함동건(팀원) | **Backend**<br><br><br>              |
| 🟦배한진(팀원) | **FullStack**<br><br><br>            |
| 🟥이동영(팀원) | **FullStack**<br><br><br>            |
| 🟨이다영(팀원) | **Infra**<br><br><br>                |

<br>

# 2. 설계 및 구현

### 🛠 기술 스택

**Frontend** <br>
![React](https://img.shields.io/badge/react-61DAFB.svg?style=for-the-badge&logo=react&logoColor=white)
![NPM](https://img.shields.io/badge/NPM-FF415B.svg?style=for-the-badge&logo=NPM&logoColor=white)
![TypeScript](https://img.shields.io/badge/typescript-3178C6.svg?style=for-the-badge&logo=typescript&logoColor=white)
![Next.js](https://img.shields.io/badge/next.js-E2123.svg?style=for-the-badge&logo=next.js&logoColor=white)
![axios](https://img.shields.io/badge/axios-E1123.svg?style=for-the-badge&logo=axios&logoColor=white)
![tailwindcss](https://img.shields.io/badge/tailwindcss-01afca.svg?style=for-the-badge&logo=tailwindcss&logoColor=white)

**Backend** <br>
![Java](https://img.shields.io/badge/java-3670A0?style=for-the-badge&logo=Java&logoColor=ffdd54)
![Spring Boot](https://img.shields.io/badge/spring_boot-6DB33F.svg?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/spring_security-6DB33F.svg?style=for-the-badge&logo=springsecurity&logoColor=white)
![Spring Data JPA](https://img.shields.io/badge/spring_data_jpa-6DB33F.svg?style=for-the-badge&logo=springdatajpa&logoColor=white)
![SpringBatch](https://img.shields.io/badge/Spring_Batch-6DB33F.svg?style=for-the-badge&logo=Springbatch&logoColor=white)
![Spring Quartz](https://img.shields.io/badge/Spring_Quartz-6DB33F?style=for-the-badge&logo=SpringQuartz&logoColor=white)
![QueryDSL](https://img.shields.io/badge/QueryDSL-0089CF?style=for-the-badge&logo=querydsl&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/redis-FF4438?style=for-the-badge&logo=redis&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000.svg?style=for-the-badge&logo=jwt&logoColor=white)

**DevOps** <br>
![NginX](https://img.shields.io/badge/NginX-009639.svg?style=for-the-badge&logo=nginx&logoColor=white)
![Docker](https://img.shields.io/badge/docker-2496ED.svg?style=for-the-badge&logo=docker&logoColor=white)
![Jenkins](https://img.shields.io/badge/jenkins-D24939.svg?style=for-the-badge&logo=jenkins&logoColor=white)
![Amazon EC2](https://img.shields.io/badge/amazon_ec2-FF9900.svg?style=for-the-badge&logo=amazonec2&logoColor=white)
![GitLab](https://img.shields.io/badge/gitlab-FC6D26.svg?style=for-the-badge&logo=gitlab&logoColor=white)

**Tools** <br>
![Visual Studio Code](https://img.shields.io/badge/Visual%20Studio%20Code-0078d7.svg?style=for-the-badge&logo=visual-studio-code&logoColor=white)
![Intellij IDEA](https://img.shields.io/badge/Intelij_IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white)
![Postman](https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=postman&logoColor=white)
![Figma](https://img.shields.io/badge/figma-F24E1E.svg?style=for-the-badge&logo=figma&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)
![jira](https://img.shields.io/badge/jira-2580f5.svg?style=for-the-badge&logo=jira&logoColor=white)

<br>

### 🖼️아키텍쳐 설계

<img src="우리깃헙이미지주소" alt="아키텍쳐 설계" />

<br>

### 💾데이터베이스 모델링(ERD)

<img src="우리깃헙이미지주소" alt="ERD" />

<br>

### 📝기능명세서

[Notion\_기능명세서](https://www.notion.so/1af2fd403c298006a3acddf37dc8a5a9)

<br>

### 📄API명세서

[Notion_API명세서](https://www.notion.so/API-167c7eb0fd0f4152ad4a8dd22845ed3a)

<br>

### 🗂️프로젝트 폴더 구조

**Frontend** - Yarn Berry + Vite + React + Typescript

```text
client/
├── app/
│   ├── login/
│   ├── uncheck-schedule/
│   ├── landing/
│   ├── signup/
│   ├── group/
│   ├── profile/
│   ├── context/
│   ├── app/
│   ├── layout.tsx
│   ├── page.tsx
│   ├── middleware.tsx
│   ├── loading.tsx
│   └── globals.css
├── components/
│   ├── ui/
│   ├── Header.tsx
│   ├── BalanceComparison.tsx
│   ├── Footer.tsx
│   ├── layout.tsx
│   └── theme-provider.tsx
├── lib/
├── public/
├── styles/
├── hooks/
├── package.json
├── next.config.mjs
├── tailwind.config.js
└── tsconfig.json
```

**Backend** - Spring Boot

```text
server/
├── src/
│   ├── main/
│   │   ├── ── java.com.b110.jjeonchongmu/
│   │   │   ├── global/
│   │   │   │   ├── component/
│   │   │   │   ├── config/
│   │   │   │   ├── exception/
│   │   │   │   ├── interceptor/
│   │   │   │   ├── security/
│   │   │   │   └── util/
│   │   │   ├── domain/
│   │   │   │   ├── account/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── batch/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── enums/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   ├── gathering/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   ├── main/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   ├── mypage/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   ├── notification/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   ├── schedule/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   ├── test/
│   │   │   │   │   └── SimpleJenkinsTest.java
│   │   │   │   ├── trade/
│   │   │   │   │   ├── api/
│   │   │   │   │   ├── dto/
│   │   │   │   │   ├── entity/
│   │   │   │   │   ├── enums/
│   │   │   │   │   ├── repo/
│   │   │   │   │   └── service/
│   │   │   │   └── user/
│   │   │   │       ├── api/
│   │   │   │       ├── dto/
│   │   │   │       ├── entity/
│   │   │   │       ├── repo/
│   │   │   │       └── service/
```

<br>

<img src='우리이미지 주소' alt='모임상세화면' width='200' /> <img src='./images/내정보, 자동이체 목록.gif' alt='자동이체 목록' width='200' /> <img src='./images/메인에서 일정' alt='메인 일정 보여주기기' width='200' />

# 3. 기능 상세 설명

<br>
<br>

# Front-End

## ✅ 로그인/회원가입/개인계좌생성

### 진행 순서 및 핵심 기능

1. 기능 설명

## ✅ 모임

### 1️⃣

## ✅ 일정

### 1️⃣ 일정 생성

- **React State Management**

  - `useState`를 활용하여 선거 정보를 상태로 관리.
  - 날짜 및 시간을 별도의 상태(`dateState`)로 분리하여 입력값 유효성 검사 수행.

### 2️⃣ 일정 정산

- **상태 관리**

## ✅ 계좌 관리

### 1️⃣

<br>

# Back-End

## ✅ 로그인/회원가입/계좌생성

## ✅ 모임

### 1️⃣ 외부~~

## ✅ 일정

### 1️⃣ 채팅 기능

## ✅ 외부 API

### 1️⃣ 외부~~

<br>

**Infra**

- 웹서버: NginX
- 실행환경: Docker, Docker-compose
- CI/CD: Jenkins
- 배포: AWS EC2

## ✅ 배포 환경 구축 (담당: 이다영)

### 1️⃣ 웹서버

- **NGINX**

### 2️⃣ 배포 환경 구축과 CI/CD

- **Docker, Docker-compose**

  - `docker`를 이용하여 실행환경을 컨테이너화.
  - 배포에 사용되는 컨테이너 6개를 `docker-compose`로 묶어서 배포.

- **Jenkins**

  - Docker에서 Jenkins image를 pull 받아서 실행
  - 파이프라인 스크립트를 작성하여 배포

- **AWS EC2**
  - 제공받은 AWS EC2 사용

<br>

# 4. 팀원 소개

<div align="center">
  <table>
    <tr>
      <th align="center" width="50%">Backend</th>
      <th align="center" width="50%">Backend</th>
    </tr>
    <tr>
      <td align="center">
        <img src="docs/동영.png" alt="동영 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/dongschiken">dongs</a> ✨
      </td>
      <td align="center">
        <img src="docs/민석이형.png" alt="민석 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/KOOMINSEOK9">민석</a> 🐿️
      </td>
    </tr>
    <tr>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
    </tr>
    <tr>
      <td align="center">
        <ul>
          <li>🫀 책임감 있고 팀에 주인의식을 가지는 리더</li>
          <li>⭐️ 다양한 오류에 대한 문제해결 능력 </li>
          <li>👩🏻‍💻 학습속도가 빠르고 기본기가 탄탄합니다.</li>
        </ul>
      </td>
      <td align="center">
        <ul>
          <li>👀 다양한 영역에 대한 통찰력과 이해력</li>
          <li>😎 맡은 일을 책임감 있게 해냅니다.</li>
          <li>🧑‍💻 기술 도입에 신중하며 합리적인 판단능력</li>
        </ul>
      </td>
    </tr>
  </table>
    <table>
    <tr>
      <th align="center" width="50%">Backend</th>
      <th align="center" width="50%">Backend</th>
    </tr>
    <tr>
      <td align="center">
        <img src="docs/동영.png" alt="동영 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/dongschiken">dongs</a> ✨
      </td>
      <td align="center">
        <img src="docs/민석이형.png" alt="민석 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/KOOMINSEOK9">민석</a> 🐿️
      </td>
    </tr>
        <tr>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
    </tr>
    <tr>
      <td align="center">
        <ul>
          <li>🫀 책임감 있고 팀에 주인의식을 가지는 리더</li>
          <li>⭐️ 다양한 오류에 대한 문제해결 능력 </li>
          <li>👩🏻‍💻 학습속도가 빠르고 기본기가 탄탄합니다.</li>
        </ul>
      </td>
      <td align="center">
        <ul>
          <li>👀 다양한 영역에 대한 통찰력과 이해력</li>
          <li>😎 맡은 일을 책임감 있게 해냅니다.</li>
          <li>🧑‍💻 기술 도입에 신중하며 합리적인 판단능력</li>
        </ul>
      </td>
    </tr>
  </table>
    <table>
    <tr>
      <th align="center" width="50%">Backend</th>
      <th align="center" width="50%">Backend</th>
    </tr>
    <tr>
      <td align="center">
        <img src="docs/동영.png" alt="동영 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/dongschiken">dongs</a> ✨
      </td>
      <td align="center">
        <img src="docs/민석이형.png" alt="민석 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/KOOMINSEOK9">민석</a> 🐿️
      </td>
    </tr>
        <tr>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
    </tr>
    <tr>
      <td align="center">
        <ul>
          <li>🫀 책임감 있고 팀에 주인의식을 가지는 리더</li>
          <li>⭐️ 다양한 오류에 대한 문제해결 능력 </li>
          <li>👩🏻‍💻 학습속도가 빠르고 기본기가 탄탄합니다.</li>
        </ul>
      </td>
      <td align="center">
        <ul>
          <li>👀 다양한 영역에 대한 통찰력과 이해력</li>
          <li>😎 맡은 일을 책임감 있게 해냅니다.</li>
          <li>🧑‍💻 기술 도입에 신중하며 합리적인 판단능력</li>
        </ul>
      </td>
    </tr>
  </table>
</div>

