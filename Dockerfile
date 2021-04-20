FROM ubuntu:20.04
WORKDIR /app
COPY "bin"  "."
CMD "./dhallbin-exe"