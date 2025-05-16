# ───────────────── Stage 1: Build the app ─────────────────
FROM node:18.18 AS build

ENV APP_HOME=/app
WORKDIR $APP_HOME

# Install dependencies (cache this layer unless package files change)
COPY package.json package-lock.json ./
RUN npm ci --legacy-peer-deps

# Copy source code and build assets
COPY . .
RUN npm run build


# ───────────────── Stage 2: Final runtime ─────────────────
FROM node:18.18-alpine

# Metadata label pointing to the source code in the monorepo
LABEL org.opencontainers.image.source="https://github.com/IllusiveMan2186/Game-Price-Bot/tree/main/gpb-react"

ENV APP_HOME=/app
WORKDIR $APP_HOME

# Copy build output and production dependencies from builder stage
COPY --from=build $APP_HOME/build ./build
COPY --from=build $APP_HOME/node_modules ./node_modules
COPY --from=build $APP_HOME/package.json ./package.json

# Copy server script and entrypoint for environment variables
COPY server.js ./server.js
COPY env.sh /docker-entrypoint.d/env.sh
RUN chmod +x /docker-entrypoint.d/env.sh

# Run as non-root user
RUN chown -R node:node $APP_HOME/build
USER node

# Expose application port
EXPOSE 3000

# Entrypoint and default command
ENTRYPOINT ["/docker-entrypoint.d/env.sh"]
CMD ["node", "server.js"]
