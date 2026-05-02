FROM nginx:alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY apps/web-app/build/distributions/         /usr/share/nginx/html/
COPY apps/playground-app/build/distributions/  /usr/share/nginx/html/playground/
EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/ || exit 1
