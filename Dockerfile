#Dockerfile

# jdk17 Image Start
FROM openjdk:17

# 인자 설정 - JAR_File
ARG JAR_FILE=build/libs/*.jar

# jar 파일 복제
COPY ${JAR_FILE} app.jar

# 설정 파일을 컨테이너로 복사
COPY src/main/resources/application.yml /app/config/application.yml
#COPY src/main/resources/application-secret.yml /app/config/application-secret.yml

# 애플리케이션 실행
ENTRYPOINT ["java", "-Dspring.config.location=/app/config/application.yml", "-jar", "/app/app.jar"]

# 인자 설정 부분과 jar 파일 복제 부분 합쳐서 진행해도 무방
#COPY build/libs/*.jar app.jar

## 실행 명령어
#ENTRYPOINT ["java", "-jar", "-Duser.timezone=Asia/Seoul", "-Dspring.profiles.active=docker", "/app.jar"]