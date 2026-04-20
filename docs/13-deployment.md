# Deploy Production (khong dung localhost)

Tai lieu nay huong dan deploy backend Spring Boot len production, bo hoan toan `localhost` de trai nghiem thuc te hon.

---

## 0) Muc tieu va kien truc de xuat

- Backend: deploy len Render (Web Service, Runtime Docker)
- Database: MongoDB Atlas (da co)
- Frontend: deploy len Vercel/Netlify (hoac platform ban dang dung)
- Thanh toan VNPay:
  - `vnpay.returnUrl` -> URL frontend production
  - `vnpay.ipnUrl` -> URL backend production

Vi du URL production:
- Backend: `https://cap2-api.onrender.com`
- Frontend: `https://cap2-web.vercel.app`

---

## 1) Lam sach va bao mat truoc khi deploy (rat quan trong)

File `src/main/resources/application.properties` hien dang chua thong tin nhay cam (Mongo URI, email app password, JWT secret, VNPay secret).

Can lam ngay:
1. **Rotate toan bo secret** da lo trong source:
   - MongoDB user/password
   - Gmail app password
   - JWT secret
   - VNPay hash secret + tmnCode (neu can)
2. Khong de secret trong Git nua, chuyen qua Environment Variables tren platform deploy.
3. Neu repo da public/da share, xem nhu secret cu da bi lo.

---

## 2) Chuan hoa cau hinh theo Environment Variables

Tren production, Spring Boot se doc env var theo relaxed binding. Mapping de dung:

| Property hien tai | Environment Variable |
|---|---|
| `server.port` | `PORT` (Render tu cap, khong can set tay) |
| `spring.data.mongodb.uri` | `SPRING_DATA_MONGODB_URI` |
| `spring.data.mongodb.database` | `SPRING_DATA_MONGODB_DATABASE` |
| `jwt.secret-key` | `JWT_SECRET_KEY` |
| `jwt.expiration` | `JWT_EXPIRATION` |
| `spring.mail.host` | `SPRING_MAIL_HOST` |
| `spring.mail.port` | `SPRING_MAIL_PORT` |
| `spring.mail.username` | `SPRING_MAIL_USERNAME` |
| `spring.mail.password` | `SPRING_MAIL_PASSWORD` |
| `vnpay.tmnCode` | `VNPAY_TMNCODE` |
| `vnpay.hashSecret` | `VNPAY_HASHSECRET` |
| `vnpay.payUrl` | `VNPAY_PAYURL` |
| `vnpay.returnUrl` | `VNPAY_RETURNURL` |
| `vnpay.ipnUrl` | `VNPAY_IPNURL` |
| `vnpay.version` | `VNPAY_VERSION` |
| `vnpay.command` | `VNPAY_COMMAND` |
| `vnpay.currCode` | `VNPAY_CURRCODE` |
| `vnpay.locale` | `VNPAY_LOCALE` |
| `vnpay.orderType` | `VNPAY_ORDERTYPE` |

Luu y:
- Khong set cung luc 2 gia tri mau thuan (vd vua hard-code trong file, vua set env).
- Uu tien de production dung env vars 100%.

---

## 3) Sua CORS de chay duoc domain production

File `src/main/java/cap2/config/CorsConfig.java` dang hard-code:
- `http://localhost:5173`

Ban can cho phep them domain frontend production (vi du `https://cap2-web.vercel.app`).

Toi uu hon: dua origin vao env var (vd `APP_CORS_ORIGINS`) de sau nay doi domain khong can sua code.

Checklist CORS:
- Co `https://<frontend-domain>`
- Co the giu `http://localhost:5173` cho dev local
- `allowCredentials=true` chi dung khi can cookie/session

---

## 4) Deploy backend len Render (runtime Docker)

### 4.1 Tao service
1. Push code len GitHub.
2. Vao Render -> New + -> Web Service.
3. Connect repo `cap2`.
4. Chon Runtime: `Docker` (trong truong hop list khong co Java).
5. Dien `Root Directory` dung thu muc chua `pom.xml` va `Dockerfile`.

### 4.2 Tao file Docker (mot lan duy nhat)

Tao `Dockerfile` tai root project:

```dockerfile
# syntax=docker/dockerfile:1

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

COPY pom.xml ./
RUN mvn -q -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
```

Tao `.dockerignore`:

```text
target
.git
.idea
.vscode
uploads
node_modules
*.log
```

### 4.3 Build/Start command tren Render

Khi chon runtime Docker:
- Render se build image tu `Dockerfile`.
- Thuong khong can nhap `Build Command` va `Start Command`.

Neu Render UI bat buoc dien command, dung:

```powershell
docker build -t cap2 .
docker run -e PORT=8080 -p 8080:8080 cap2
```

Luu y: command tren la de test local bang Docker, khong phai command production trong container.

### 4.4 Environment Variables tren Render
Set cac bien trong muc (2), toi thieu:
- `SPRING_DATA_MONGODB_URI`
- `SPRING_DATA_MONGODB_DATABASE`
- `JWT_SECRET_KEY`
- `JWT_EXPIRATION`
- `SPRING_MAIL_HOST`
- `SPRING_MAIL_PORT`
- `SPRING_MAIL_USERNAME`
- `SPRING_MAIL_PASSWORD`
- `VNPAY_TMNCODE`
- `VNPAY_HASHSECRET`
- `VNPAY_PAYURL`
- `VNPAY_RETURNURL`
- `VNPAY_IPNURL`

Khong can set `PORT` thu cong neu Render auto cung cap.

### 4.5 Kiem tra service song
Sau khi deploy xong:
- Mo URL backend production
- Test nhanh endpoint public (vd `GET /products`)
- Kiem tra logs tren Render

---

## 5) MongoDB Atlas cho production

1. Tao DB user rieng cho production, quyen toi thieu can thiet.
2. Dua connection string moi vao `SPRING_DATA_MONGODB_URI`.
3. Network Access:
   - Cach de: cho phep tam `0.0.0.0/0` (de chay nhanh)
   - Cach an toan hon: gioi han theo IP/range cua ha tang ban dung
4. Bat canh bao login bat thuong tren Atlas.

---

## 6) Deploy frontend va bo localhost o client

Sau khi co backend URL production:
1. Deploy frontend len Vercel/Netlify.
2. Sua bien moi truong frontend, vi du:
   - `VITE_API_BASE_URL=https://cap2-api.onrender.com`
3. Build lai frontend.
4. Kiem tra toan bo API call da tro den production URL, khong con `localhost`.

---

## 7) Cau hinh VNPay dung URL production

Cap nhat tren production env:
- `VNPAY_RETURNURL=https://<frontend-domain>/payment-result`
- `VNPAY_IPNURL=https://<backend-domain>/payments/vnpay/ipn`

Dong thoi khai bao callback URL tuong ung trong portal/sandbox VNPay (neu VNPay yeu cau).

Luu y quan trong:
- `returnUrl` la noi user duoc redirect sau thanh toan.
- `ipnUrl` la endpoint server-to-server de xac nhan giao dich va cap nhat DB.
- Production can uu tien IPN cho cap nhat trang thai don hang.

---

## 8) Checklist test sau deploy (bat buoc)

### 8.1 Auth + API co ban
- Dang ky/Dang nhap thanh cong
- Goi endpoint can token thanh cong
- CORS khong bi chan tren frontend production

### 8.2 Dat hang
- Tao gio hang -> tao don hang -> xem chi tiet don
- Kiem tra du lieu luu dung tren MongoDB Atlas

### 8.3 VNPay
- Tao payment URL
- Thanh toan thanh cong -> return URL dung
- IPN goi ve backend thanh cong
- `paymentStatus` trong DB chuyen `PAID`
- Test case huy giao dich/that bai

### 8.4 Email
- Test quen mat khau/email notification
- Kiem tra mail gui thanh cong, khong vao spam qua nhieu

---

## 9) Van hanh sau deploy

- Bat alert khi app down (UptimeRobot/Render alerts).
- Dat lich rotate secret dinh ky (30-60 ngay).
- Theo doi logs cac endpoint nhay cam (`/auth`, `/payments/vnpay/ipn`).
- Moi lan doi domain frontend/backend -> cap nhat lai CORS + VNPay URL.

---

## 10) Loi thuong gap va cach xu ly nhanh

1. **Loi CORS**
   - Nguyen nhan: chua them domain frontend production.
   - Xu ly: cap nhat `CorsConfig`/env CORS, deploy lai backend.

2. **Mongo timeout/khong ket noi duoc**
   - Nguyen nhan: Atlas chua mo Network Access hoac URI sai.
   - Xu ly: kiem tra whitelist IP + user/password + db name.

3. **VNPay return duoc nhung DB khong cap nhat**
   - Nguyen nhan: IPN URL khong public/sai domain.
   - Xu ly: kiem tra `VNPAY_IPNURL`, logs `/payments/vnpay/ipn`, va firewall/network.

4. **Mail khong gui duoc**
   - Nguyen nhan: sai app password/SMTP policy.
   - Xu ly: tao app password moi, cap nhat env, redeploy.

---

## 11) Goi y trinh tu thuc hien nhanh

1. Rotate secrets.
2. Deploy backend (Render) + set env vars.
3. Fix CORS cho production domain.
4. Deploy frontend + sua API base URL.
5. Cap nhat VNPay return/ipn URL.
6. Chay checklist test muc 8.

Neu ban muon, buoc tiep theo toi co the viet them:
- Mau `application-prod.properties` an toan
- Mau bien moi truong cho frontend
- Checklist go-live 1 trang de team tick nhanh truoc khi demo.
