FROM nginx:latest

# 기존 설정 값 제거(Optional)
RUN rm /etc/nginx/nginx.conf || true

# 커스텀 nginx.conf를 복사해 넣기
COPY ./nginx.conf /etc/nginx/nginx.conf