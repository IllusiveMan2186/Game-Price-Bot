#!/bin/sh 

echo "⚙️ Generating env.js with runtime environment variables"

cat <<EOF > /app/build/env.js
window._env_ = {
  BACKEND_SERVICE_URL: "${BACKEND_SERVICE_URL}",
  TELEGRAM_BOT_URL: "${TELEGRAM_BOT_URL}",
  SUPPORT_EMAIL: "${SUPPORT_EMAIL}"
};
EOF

echo "✅ env.js created:"
cat /app/build/env.js

exec "$@"