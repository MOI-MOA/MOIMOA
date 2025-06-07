<img src="/docs/로고.png" alt="로고" />

# 1. 프로젝트 개요

### 📋 서비스 개요

- 모임 공금관리 서비스
- SSAFY 은행 API를 활용한 모임 공금 투명성 확보 및 효율적 관리
- 프로젝트 기간: 2025/03/03 ~ 2025/04/11 (총 40일간)

### 💰 **서비스 특징**

1. **투명한 공금 관리** : 모든 거래내역 공개 및 실시간 잔액 확인
2. **공정한 페이백 시스템**: 참석 여부에 따른 합리적인 비용 분배
3. **자동 정산 기능**: 모임 종료 후 남은 금액 자동 정산
4. **직관적 UI/UX**: 손쉬운 모임 생성 및 관리

### 👭팀원 정보 및 업무 분담 내역

| 이름           | 역할 및 구현 기능                    |
| -------------- | ------------------------------------ |
| 🟧진종수(팀장) | **Backend**<br>- ERD설계<br><br><br> |
| 🟩함동건(팀원) | **Backend**<br>- 일정 관리 기능<br>- 일정 통장 관리 기능<br>- 일정 통장 페이백 로직 구현              |
| 🟥이동영(팀원) | **Backend**<br>- 계좌 관리 기능<br>- 거래 관련 기능<br>- SSAFY BANK 외부 API 연동       |
| 🟦배한진(팀원) | **FullStack**<br><br><br>            |
| 🟨이다영(팀원) | **Infra**<br>- CI/CD 파이프라인 구축<br>- Docker 컨테이너화<br>- AWS 배포 관리                |

<br>

# 2. 기획 배경
### 문제 상황 및 해결 방안

1. **공금 관리의 어려움** (회비 미납, 불투명 운영, 총무의 횡령)
  - 정해진 날짜, 시간에만 출금 및 결제 가능
  - 정해진 금액만 출금 가능 (모임 참여자 납입분만 출금 가능)
  - 투명한 거래내역 공개로 신뢰성 확보


2. **모임 불참자의 금전 이해관계 문제**
- 사전 불참 의사 표명 시 회비 환급
- 참석 의사 표명 후 불참 시, 투표로 정한 비율에 따라 회비 환급
- 예시:
  - 총 인원 100명 / 참여 의사 인원 50명 / 실 참여 인원 30명
  - 총 비용: 320만 원 (예약금 포함)
  - 30명 기준 300만 원이 아닌 320만 원 사용 필요
  - 180만 원을 정해진 비율대로 20명에게 환불


# 3. 설계 및 구현

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

### 🖼️ 아키텍쳐 설계
<img src="/docs/아키텍처 설계도.png" alt="아키텍처 설계도" />

**👉 [🔗인프라 구축 상세 내용 보기](docs/INFRA/README.md)**

<br>

### 💾데이터베이스 모델링(ERD)

<img src="/docs/ERD.png" alt="ERD" />

<br>

### 📝기능명세서

[Notion\_기능명세서](https://www.notion.so/1af2fd403c298006a3acddf37dc8a5a9)

<br>

### 📄API명세서

[Notion_API명세서](https://www.notion.so/API-167c7eb0fd0f4152ad4a8dd22845ed3a)

<br>

# 3. 기능 상세 설명

<table>
  <tr>
    <td align="center" width="33%">
      <h4>1. 영희의 모임 생성</h4>
      <img src="/docs/1.영희의_모임생성.gif" alt="1.영희의_모임생성" width="100%" />
    </td>
    <td align="center" width="33%">
      <h4>2. 모임 초대코드 생성</h4>
      <img src="/docs/2.초대코드_생성.gif" alt="2.초대코드_생성" width="100%" />
    </td>
    <td align="center" width="33%">
      <h4>3. 모임 참여하기</h4>
      <img src="/docs/3.모임참여.gif" alt="3.모임참여" width="100%" />
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <h4>4. 모임 수락하기</h4>
      <img src="/docs/4.모임수락.gif" alt="4.모임수락" width="100%" />
    </td>
    <td align="center" width="33%">
      <h4>5. 모임통장에 입금</h4>
      <img src="/docs/5.모임에_돈넣기.gif" alt="5.모임에_돈넣기" width="100%" />
    </td>
    <td align="center" width="33%">
      <h4>6. 모임 일정 생성</h4>
      <img src="/docs/6.일정생성.gif" alt="6.일정생성" width="100%" />
    </td>
  </tr>
  <tr>
    <td align="center" width="33%">
      <h4>7. 일정참여시 돈 부족으로 입금 후 다시 시도</h4>
      <img src="/docs/7.일정참여_돈없어서_돈넣고_다시시도.gif" alt="7.일정참여_돈없어서_돈넣고_다시시도" width="100%" />
    </td>
    <td align="center" width="33%">
      <h4>8. 모임 탈퇴시 납입한 금액 입금</h4>
      <img src="/docs/8.모임탈퇴시_돈들어옴.gif" alt="8.모임탈퇴시_돈들어옴" width="100%" />
    </td>
    <td align="center" width="33%">
      <h4>9. 패널티 적용되는 일정에 대한 참석 취소</h4>
      <img src="/docs/9.페널티적용_일정참석취소.gif" alt="9.페널티적용_일정참석취소" width="100%" />
    </td>
  </tr>
</table>

# 4. 팀원 소개

<div align="center">
  <!-- 첫 번째 줄 - 2명 -->
  <table>
    <tr>
      <th align="center" width="50%">Backend</th>
      <th align="center" width="50%">Backend</th>
    </tr>
    <tr>
      <td align="center">
        <img src="/api/placeholder/200/150" alt="종수 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/doros508">종수</a> ✨
      </td>
      <td align="center">
        <img src="/docs/동영.png" alt="동영 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/dongschiken">동영</a> 🐿️
      </td>
    </tr>
    <tr>
      <td align="center">
        <div>~본 종수 이미지는?</div>
      </td>
      <td align="center">
        <div>~본 동영 이미지는?</div>
      </td>
    </tr>
    <tr>
      <td align="center">
        <ul>
          <li>🫀 책임감 있고 팀에 주인의식을 가지는 리더</li>
          <li>⭐️ 다양한 오류에 대한 문제해결 능력</li>
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

  <!-- 두 번째 줄 - 3명 -->
  <table style="margin-top: 20px;">
    <tr>
      <th align="center" width="33.33%">Backend</th>
      <th align="center" width="33.33%">Backend</th>
      <th align="center" width="33.33%">Backend</th>
    </tr>
    <tr>
      <td align="center">
        <img src="/api/placeholder/200/150" alt="한진 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/baebaebaeh">한진</a> ✨
      </td>
      <td align="center">
        <img src="/api/placeholder/200/150" alt="동건 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/">동건</a> 🐿️
      </td>
      <td align="center">
        <img src="/docs/daram.jpg" alt="다영 프로필 이미지" width="200px" height="150px">
        <br>
        <a href="https://github.com/solveDayLee">다영</a> 🌟
      </td>
    </tr>
    <tr>
      <td align="center">
        <div>~본 한진 이미지는?</div>
      </td>
      <td align="center">
        <div>~본 동건 이미지는?</div>
      </td>
      <td align="center">
        <div>~본 다영 이미지는?</div>
      </td>
    </tr>
    <tr>
      <td align="center">
        <ul>
          <li>🔍 문제해결 능력이 뛰어납니다</li>
          <li>💪 끈기있게 도전하는 자세</li>
          <li>👨‍💻 코드 최적화에 대한 열정</li>
        </ul>
      </td>
      <td align="center">
        <ul>
          <li>👀 세심한 코드 리뷰 능력</li>
          <li>😎 맡은 일을 책임감 있게 해냅니다</li>
          <li>🧠 논리적인 사고와 체계적인 접근</li>
        </ul>
      </td>
      <td align="center">
        <ul>
          <li>🌱 지속적인 학습과 성장</li>
          <li>⚡ 빠른 적응력과 실행력</li>
          <li>🤝 원활한 소통과 협업 능력</li>
        </ul>
      </td>
    </tr>
  </table>
</div>

---
# Git 브랜치 전략 가이드

Git 브랜치 전략은 팀 협업 시 코드 관리를 체계적으로 하기 위한 중요한 요소입니다. 아래에 GitHub Flow 기반의 브랜치 전략을 정리했습니다.

## 브랜치 구조

```
master
  └── develop
       ├── front
       │    └── fe/feature/19-login
       └── back
            └── be/feature/18-login
```

## 브랜치 명명 규칙

브랜치 이름은 작업 타입과 내용을 명확히 표현해야 합니다:

- `[팀]/[타입]/[이슈번호]-[작업명]`
  - 예: `fe/feature/19-login`, `be/fix/32-signup-validation`

### 브랜치 타입
- `feature`: 새로운 기능 개발
- `fix`: 버그 수정
- `style`: UI/UX 변경
- `refactor`: 코드 리팩토링
- `docs`: 문서 작업
- `hotfix`: develop 브랜치에서 발생한 긴급 버그 수정

## 작업 흐름

1. **브랜치 생성 전 확인**
   - 항상 최신 develop 브랜치에서 새 브랜치를 생성합니다
   - `git checkout develop`
   - `git pull origin develop`
   - `git checkout -b [브랜치명]`

2. **작업 및 커밋**
   - 규칙에 맞는 커밋 메시지 작성
   - 작은 단위로 자주 커밋하기

3. **원격 저장소와 동기화**
   - 커밋 전 항상 pull 먼저 수행
   - 충돌 발생 시 stash 활용하여 해결

4. **Pull Request 및 Merge**
   - 작업 완료 후 GitHub/GitLab에서 PR 생성
   - 코드 리뷰 후 승인받은 PR만 merge
   - merge 후 해당 브랜치는 삭제 (브랜치 정리)

## 주의사항

### 브랜치 확인
- 작업 시작 전 항상 현재 브랜치 확인: `git branch`
- 다른 브랜치 작업 내용과 충돌이 없는지 확인

### Merge 시 브랜치 삭제 여부
- 기본적으로 feature, fix, style 등의 작업용 브랜치는 merge 후 삭제 권장
- master, develop, front, back과 같은 주요 브랜치는 유지

### Master 브랜치 Push 정책
- master 브랜치는 직접 푸시하지 않고, PR을 통해서만 코드 반영
- master에 병합하기 전 develop 브랜치에서 충분한 테스트 진행

## Pull/Fetch 사용 가이드

### Pull 방식
```bash
# 커밋 전 항상 pull 먼저 실행
git pull origin [브랜치명]

# 충돌 발생 시
git stash push [충돌난 파일전체 경로]
git pull origin [브랜치명]
git stash apply

# 작업 후 커밋 및 푸시
git add .
git commit -m "feat: 회원가입 기능 추가"
git push origin [브랜치명]

# GitHub/GitLab에서 PR 생성 후 merge
```

### Fetch 사용
```bash
# 원격 저장소 변경사항 확인만 하기
git fetch origin
git diff [로컬브랜치] origin/[원격브랜치]

# 변경사항 확인 후 merge 결정
git merge origin/[브랜치명]
```
