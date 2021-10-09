# Commit Log Convention

- [AngularJS Commit Message Conventions](https://gist.github.com/stephenparish/9941e89d80e2bc58a153) 참고해 Commit Log 작성

# Git Flow

## 주의 사항

- master merge 하기전에 무조건 dev branch로 merge

## branch 종류

### master

- 최종 배포 버전

### release

- branch 명 : `release/${application.version}`
- 목적 : QA테스트를 위한 공간

### dev : mater branch merge전 구현된 기능 통합 하기 위한 branch

### feature

- branch 명
    - `feature/${application.issue}` : `feature/#2`
    - `feature/${issue.number}-${application.function}` : `feature/#35-refactor`

- 목적 : 구현해야할 기능

### hotfix

- branch 명 : `hotfix/${issue.name}`
- 목적 : 급하게 수정해야하는 기능이 존재

## git Flow 순서

- `issue` 먼저 등록
- `feature/${function.name}` : 개인 repo/메인 repo
- `dev` 메인 repo code 리뷰
- `master` : 메인 repo -> 배포