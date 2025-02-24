#!/bin/sh

echo "window._env_ = {" > /usr/share/nginx/html/env.js
echo "  BACKEND_SERVICE_URL: \"${BACKEND_SERVICE_URL}\"," >> /usr/share/nginx/html/env.js
echo "  TELEGRAM_BOT_URL: \"${TELEGRAM_BOT_URL}\"," >> /usr/share/nginx/html/env.js
echo "  SUPPORT_EMAIL: \"${SUPPORT_EMAIL}\"" >> /usr/share/nginx/html/env.js
echo "};" >> /usr/share/nginx/html/env.js

exec "$@"