# OpenJDK 이미지 사용
FROM openjdk:17-jdk-alpine

# 작업 디렉토리 설정
WORKDIR /app

# JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 실행 명령어 설정
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]