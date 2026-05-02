FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY apps/web-app/build/distributions/         /usr/share/nginx/html/
COPY apps/playground-app/build/distributions/  /usr/share/nginx/html/playground/
EXPOSE 8080
