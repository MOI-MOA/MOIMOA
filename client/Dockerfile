FROM node:20-alpine
WORKDIR /app
COPY . .
RUN npm install
# 타입 검사 없이 빌드 실행
RUN NEXT_IGNORE_TS_ERRORS=1 npm run build
EXPOSE 3000
ENTRYPOINT [ "npm", "run", "start" ]