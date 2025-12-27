# Build stage
FROM node:20-alpine AS build
WORKDIR /app

# Copy package files for dependency caching
COPY package*.json ./
RUN npm ci

# Copy source code and build
COPY . .
RUN npm run build

# Runtime stage
FROM nginx:alpine
WORKDIR /usr/share/nginx/html

# Remove default nginx static files
RUN rm -rf ./*

# Copy built files from build stage
COPY --from=build /app/dist .

# Copy nginx configuration template
COPY nginx.conf.template /etc/nginx/templates/default.conf.template

# Set default environment variables
ENV PORT=80
ENV BACKEND_URL=http://localhost:8080

# Expose port (Railway uses PORT env variable)
EXPOSE ${PORT}

# Start nginx with envsubst for environment variable substitution
CMD ["/bin/sh", "-c", "envsubst '${PORT} ${BACKEND_URL}' < /etc/nginx/templates/default.conf.template > /etc/nginx/conf.d/default.conf && nginx -g 'daemon off;'"]
