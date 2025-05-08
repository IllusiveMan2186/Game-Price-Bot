const config = {
    BACKEND_SERVICE_URL: window._env_?.BACKEND_SERVICE_URL || "http://localhost:8080/api",
    TELEGRAM_BOT_URL: window._env_?.TELEGRAM_BOT_URL || "https://t.me/GamaPriceTelegramBot",
    SUPPORT_EMAIL: window._env_?.SUPPORT_EMAIL || "info@example.com",
};

export default config;
