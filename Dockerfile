# 새로운 이미지를 생성할 때 기반으로 사용할 이미지를 지정 ( 이미지 이름:태그 )
# jdk 11 이 있는 컨테이너 사용
FROM openjdk:17

# 이미지 빌드 시점에서 사용할 변수 지정
ARG JAR_FILE=build/libs/app.jar

# 호스트에 있는 파일이나 디렉토리를 Docker 이미지의 파일 시스템으로 복사
COPY ${JAR_FILE} ./app.jar

# 컨테이너에서 사용할 환경 변수 지정
# TimeZone 환경 변수
ENV TZ=Asia/Seoul

# 컨테이너가 실행되었을 때 항상 실행되어야 하는 커맨드 지정
ENTRYPOINT ["java","-jar","./app.jar"]


# 도커 이미지 빌드(생성) docker
# docker build -t jyuka/application-project-test ./
# docker run -p 8080:8080 jyuka/application-project-test