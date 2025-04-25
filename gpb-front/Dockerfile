FROM node:18.18 AS builder
WORKDIR /app
COPY package.json package-lock.json ./
RUN npm install --legacy-peer-deps
COPY . .
RUN npm run build

# Final stage
FROM node:18.18
WORKDIR /app

# Копируем build и node_modules из builder
COPY --from=builder /app/build ./build
COPY --from=builder /app/package.json ./package.json
COPY --from=builder /app/node_modules ./node_modules

COPY server.js ./

# Copy entrypoint script for env variables
COPY env.sh /docker-entrypoint.d/env.sh
RUN chmod +x /docker-entrypoint.d/env.sh

EXPOSE 3000
ENTRYPOINT ["/docker-entrypoint.d/env.sh"]
CMD ["node", "server.js"]