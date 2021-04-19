FROM alpine
WORKDIR /app
COPY "bin"  "."
CMD "./dhallbin"
