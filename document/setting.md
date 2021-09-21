# Project 설정

## Application rest api(swagger)

- profile local로 설정
- application 실행
- [swagger url 이동](http://localhost:8000/swagger-ui/index.html)

## coverage test

- jacoco
- sonarqube

```
docker run -d --name sonarqube -p 9090:9000 sonarqube
```

## Docker

### Dockerfile

- docker 파일 빌드 및 실행

```
docker build --tag clack2933/sharedfood-service:1.0 .
```

### docker-compose.yml

- default : local로 설정
- docker-compose.yml 파일 선택

```
docker-compose -f docker-compose.yml up -d
```

- docker-compose.yml 파일을 default로 사용할 때

```
docker-compose up -d
```

- build를 다시해야하는 경우

```
# 실행과 build 동시에 진행
docker-compose up -d --build
# image build만 다시하는 것
docker-compose build
```

- 중단 및 취소

```
docker-compose down
```

- [참고사이트](https://www.daleseo.com/docker-compose/)

## application setting

- 설정 정보 : jwt key, oauth 정보
