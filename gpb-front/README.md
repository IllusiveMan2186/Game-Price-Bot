# Frontend Part

Frontend web part of GPB application.

## **Setup for Development Environment**

### **Requirements**
- **Node.js**: `18.18.0`
- **npm** 

---

## **Steps to Run Locally**

### **1️⃣ Install Dependencies**
First, install the required dependencies:
```sh
npm install --legacy-peer-deps
```

### **2️⃣ Setup Environment Variables**
The frontend reads environment variables from `public/env.js`. Create the file if it does not exist:

📂 **`public/env.js`**
```js
window._env_ = {
  BACKEND_SERVICE_URL: "http://localhost:8080",
  TELEGRAM_BOT_URL: "https://api.telegram.org/bot...",
  SUPPORT_EMAIL: "support@example.com"
};
```
> Ensure `env.js` is inside the `public/` directory, as React will load it at runtime.

### **3️⃣ Start the Development Server**
Run the following command to start the React development server:
```sh
npm start
```
This will start the server **`http://localhost:3000`** .

### **4️⃣ Verify `env.js` is Loaded**
After running the app, check if environment variables are loaded correctly by opening the following URL in your browser:
```
http://localhost:3000/env.js
```
If it loads correctly, your environment variables are set up properly.

---


