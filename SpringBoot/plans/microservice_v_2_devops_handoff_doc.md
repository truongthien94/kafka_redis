# Microservice V2 – Tài liệu chuẩn hoá repo và kế hoạch triển khai Docker

## 1. Mục tiêu tài liệu

Tài liệu này dùng để chuẩn hoá repo `Microservice-V2` trước khi chỉnh sửa và triển khai môi trường local bằng Docker.

Mục tiêu chính:
- Chuẩn hoá cấu trúc repo để dễ build, dễ chạy, dễ debug.
- Đưa toàn bộ hệ thống về một entrypoint chung bằng `docker-compose.yml` tại root.
- Chuẩn hoá config giữa môi trường local và môi trường Docker.
- Dựng được full stack để test API, Kafka và Redis.
- Có checklist rõ ràng để triển khai và verify kết quả.

---

## 2. Phạm vi hệ thống cần chạy

### 2.1. Các thành phần hạ tầng
- MySQL
- Redis
- Kafka
- Zookeeper (nếu Kafka image đang dùng cần)
- Kafka UI
- RedisInsight

### 2.2. Các service nghiệp vụ
- Service Registry
- API Gateway
- Order Service
- Product Service
- Promotion Service

### 2.3. Dữ liệu khởi tạo
- Dùng thư mục `mysql-init` làm nguồn dữ liệu khởi tạo chuẩn.
- Không dùng dữ liệu seed thủ công trên máy cá nhân làm nguồn kiểm thử chính.

---

## 3. Mục tiêu kỹ thuật sau khi chuẩn hoá

Sau khi hoàn thành, repo phải đạt được các điều kiện sau:
- Có thể clone repo và chạy bằng một lệnh chính.
- `docker-compose.yml` nằm ở root repo.
- Mỗi service có Dockerfile rõ ràng.
- Các service không còn hard-code `localhost` khi chạy trong container.
- Tách được config local và config docker.
- Dựng được MySQL, Redis, Kafka và toàn bộ service trên cùng network Docker.
- Test được các flow:
  - Tạo order
  - Kafka Order -> Product
  - Kafka Product -> Order
  - Promotion code
  - Redis lock key

---

## 4. Kiến trúc chạy local đề xuất

### 4.1. Vị trí file compose
Đề xuất đặt file như sau:

```text
Microservice-V2/
├── docker-compose.yml
├── .env
├── README.md
├── mysql-init/
├── APIGateway/
├── OrderService/
├── ProductService/
├── PromotionService/
└── ServiceRegistry/
```

### 4.2. Lý do đặt `docker-compose.yml` ở root
- Đây là file chạy cho toàn hệ thống, không phải riêng một service.
- Dễ quản lý network, volume, env và dependency chung.
- Dễ mount `mysql-init`.
- Dễ build nhiều service trong cùng repo.
- Tránh nhầm lẫn rằng compose thuộc riêng `OrderService`.

---

## 5. Chuẩn hoá cấu trúc repo

### 5.1. Việc cần làm
1. Đưa `docker-compose.yml` ra root repo.
2. Thêm `README.md` ở root.
3. Thêm `.gitignore` chuẩn cho Java/Maven/IntelliJ.
4. Xoá hoặc ignore các file/thư mục không nên commit:
   - `.idea/`
   - `target/`
   - generated sources
5. Giữ tên thư mục service nhất quán và dễ hiểu.
   - Ví dụ: `APIGateway`, `ServiceRegistry`, `OrderService`, `ProductService`, `PromotionService`

### 5.2. Kết quả mong đợi
- Repo sạch.
- Dễ đọc.
- Dễ onboarding.
- Không mang theo artifact build của máy local.

---

## 6. Chuẩn hoá runtime và build

### 6.1. Mục tiêu
Đồng bộ version chạy giữa các service để tránh lệch môi trường.

### 6.2. Việc cần làm
1. Chốt một version Java dùng chung cho toàn bộ service.
2. Chốt Maven wrapper hoặc Maven version dùng trong build.
3. Chuẩn hoá Dockerfile của tất cả service theo một pattern chung.
4. Kiểm tra tính tương thích giữa các service Spring Boot.

### 6.3. Kết quả mong đợi
- Mọi service build được theo cùng quy chuẩn.
- Không xảy ra tình trạng service này chạy được nhưng service khác lỗi vì lệch JDK.

---

## 7. Chuẩn hoá config môi trường

### 7.1. Mục tiêu
Tách rõ config chạy local và config chạy Docker.

### 7.2. Nguyên tắc
- Không hard-code `localhost` cho service chạy trong container.
- Dùng environment variables hoặc profile riêng.
- Tách rõ:
  - local mode
  - docker mode

### 7.3. Mapping host chuẩn

#### Local mode
- MySQL: `localhost:3306`
- Redis: `localhost:6379`
- Kafka: `localhost:29092` hoặc theo port host map
- Eureka: `http://localhost:8761/eureka`

#### Docker mode
- MySQL: `mysql:3306`
- Redis: `redis:6379`
- Kafka: `kafka:9092`
- Eureka: `http://service-registry:8761/eureka`

### 7.4. Việc cần làm
1. Đưa các host/port sang env variables.
2. Tách `application-local.properties` và `application-docker.properties` nếu cần.
3. Tất cả service đọc config từ env trước, có default fallback hợp lý.
4. Chỉnh lại URL internal call giữa các service nếu đang dùng sai host.

### 7.5. Kết quả mong đợi
- Cùng một codebase chạy được cả local lẫn Docker.
- Không cần sửa tay mỗi lần đổi môi trường.

---

## 8. Chuẩn hoá Dockerfile cho từng service

### 8.1. Mục tiêu
Mỗi service có Dockerfile dễ build, dễ maintain, nhất quán.

### 8.2. Việc cần làm
Tạo hoặc chuẩn hoá Dockerfile cho:
- Service Registry
- API Gateway
- Order Service
- Product Service
- Promotion Service

### 8.3. Nguyên tắc Dockerfile
- Dùng multi-stage build.
- Stage 1: build jar bằng Maven.
- Stage 2: chạy jar trên JRE/JDK runtime.
- Expose đúng port.
- Có thể dùng user non-root nếu cần chuẩn hơn.

### 8.4. Kết quả mong đợi
- Mỗi service có image build được độc lập.
- Toàn hệ thống có thể được dựng từ compose.

---

## 9. Thiết kế `docker-compose.yml` chuẩn

### 9.1. Mục tiêu
Có 1 file compose duy nhất để dựng full local system.

### 9.2. Các service cần có
#### Hạ tầng
- mysql
- redis
- redisinsight
- zookeeper
- kafka
- kafka-ui

#### Ứng dụng
- service-registry
- api-gateway
- promotion-service
- product-service
- order-service

### 9.3. Network
Tất cả service dùng chung một network, ví dụ:
- `micro-net`

### 9.4. Volume
- MySQL cần volume để lưu data.
- Có thể thêm volume cho Kafka nếu muốn giữ dữ liệu khi restart.

### 9.5. Healthcheck
Nên thêm healthcheck cho:
- mysql
- redis
- kafka
- service-registry

### 9.6. Nguyên tắc expose port
Chỉ expose những port cần test từ host:
- API Gateway
- Service Registry
- Kafka UI
- RedisInsight
- MySQL nếu cần check DB từ ngoài
- Redis nếu cần test từ host

### 9.7. Kết quả mong đợi
- Chạy được bằng:

```bash
docker compose up -d --build
```

---

## 10. Chuẩn hoá dữ liệu MySQL

### 10.1. Mục tiêu
Dùng `mysql-init` làm source of truth cho dữ liệu test.

### 10.2. Việc cần làm
1. Rà lại schema tạo trong `init.sql`.
2. Chốt tên schema cho nhất quán.
3. Seed đủ data để test:
   - products
   - promotions
   - orders nếu cần
4. Không phụ thuộc dữ liệu có sẵn trên máy cá nhân.

### 10.3. Điều chỉnh promotion để test
Để test flow promotion theo note hiện tại, chỉnh mã `SUMMER20` sao cho hợp lệ với thời gian test.

Đề xuất:
- `start_date = '2026-01-01 00:00:00'`

### 10.4. Kết quả mong đợi
- Mỗi lần reset DB là có dữ liệu test giống nhau.
- Không phát sinh sai lệch giữa môi trường.

---

## 11. Chuẩn hoá flow Kafka

### 11.1. Mục tiêu
Xác minh hệ thống event-driven chạy đúng giữa các service.

### 11.2. Flow cần test
#### Flow 1: Order -> Product
- Order Service tạo order.
- Order Service publish event sang Kafka.
- Product Service consume event.
- Product Service trừ stock trong DB.

#### Flow 2: Product -> Order
- Product Service publish event về Kafka sau khi xử lý stock.
- Order Service consume event.
- Order Service cập nhật trạng thái order.

### 11.3. Kết quả mong đợi
- Sau khi tạo order:
  - stock bị trừ đúng số lượng
  - status order đổi sang `PREPARED`

### 11.4. Việc cần làm
1. Chốt tên topic chính thức.
2. Chốt payload class giữa producer và consumer.
3. Cấu hình serializer/deserializer rõ ràng.
4. Thêm log ở các điểm:
   - publish thành công
   - consume thành công
   - update stock thành công
   - update status thành công
5. Thêm retry connect Kafka nếu app boot sớm hơn Kafka.

### 11.5. Kết quả mong đợi
- Kafka không chỉ “lên container” mà phải chạy đúng flow nghiệp vụ.

---

## 12. Chuẩn hoá flow Promotion

### 12.1. Mục tiêu
Kiểm tra order tạo với promotion code hợp lệ và promotion usage được cập nhật đúng.

### 12.2. Flow cần test
- Gọi API tạo order với `promotionCode = SUMMER20`.
- Promotion hợp lệ.
- Tổng tiền được giảm đúng theo rule.
- `usage_limit` giảm đi 1.

### 12.3. Điều kiện test
- Promotion code phải còn hiệu lực.
- `start_date` và `end_date` phù hợp.
- `usage_limit > 0`.
- `is_deleted = 0`.
- `min_order_value` thoả điều kiện.

### 12.4. Kết quả mong đợi
- Order tạo thành công.
- Discount áp dụng đúng.
- `usage_limit` bị trừ đúng 1 đơn vị.

---

## 13. Chuẩn hoá flow Redis

### 13.1. Mục tiêu
Kiểm tra `ProductService` có tạo Redis lock key đúng khi xử lý order.

### 13.2. Ý nghĩa kỹ thuật
Flow Redis trong bài này là flow **distributed lock**, không phải cache thông thường.

Redis được dùng để:
- Tạo lock key khi ProductService chuẩn bị trừ stock.
- Tránh race condition khi nhiều request cùng xử lý một nhóm product.

### 13.3. Cấu hình Redis chuẩn
#### Nếu chạy full Docker
- Host: `redis`
- Port: `6379`

#### Nếu chạy app từ IDE/local
- Host: `localhost`
- Port: `6379`

### 13.4. Cấu hình RedisInsight
#### Nếu RedisInsight chạy trong Docker cùng network
- Host: `redis`
- Port: `6379`

#### Nếu RedisInsight chạy ngoài host
- Host: `localhost`
- Port: `6379`

### 13.5. Key cần kiểm tra
Key mong đợi có prefix:

```text
lock:products:
```

Ví dụ:

```text
lock:products:<joined_product_ids>
```

### 13.6. Cách test
1. Đặt breakpoint tại đoạn tạo `lockKey` trong `ProductService`.
2. Đặt breakpoint tại đoạn `tryLock(...)`.
3. Gọi API tạo order.
4. Khi debug dừng, kiểm tra `lockKey`.
5. Resume.
6. Mở RedisInsight và search:

```text
lock:*
```

hoặc:

```text
lock:products:*
```

7. Xác nhận key xuất hiện.
8. Xác nhận key chỉ tồn tại ngắn trong thời gian xử lý rồi biến mất.

### 13.7. Kết quả mong đợi
- Redis connect thành công.
- ProductService tạo được lock key.
- Key đúng format.
- Key tồn tại tạm thời khi xử lý order.
- Key được release hoặc hết hạn sau khi xử lý xong.

---

## 14. Danh sách port đề xuất

### 14.1. Hạ tầng
- MySQL: `3306`
- Redis: `6379`
- Zookeeper: `2181`
- Kafka internal: `9092`
- Kafka external host map: `29092`
- Kafka UI: `8080`
- RedisInsight: `8001`

### 14.2. Ứng dụng
- Service Registry: `8761`
- API Gateway: `8282`
- Order Service: `8098`
- Product Service: `8091`
- Promotion Service: `8082`

### 14.3. Lưu ý
Port thực tế cần xác nhận lại với cấu hình hiện tại của từng service trước khi chốt compose chính thức.

---

## 15. Kế hoạch test sau khi chuẩn hoá

### 15.1. Test case 1 – Create order không promotion
#### Bước thực hiện
1. Gọi API tạo order.
2. Kiểm tra response trả về.
3. Kiểm tra bảng `orders`.
4. Kiểm tra bảng `products`.
5. Kiểm tra log Kafka.

#### Kết quả mong đợi
- Order được tạo.
- Event được publish.
- Product consume được event.
- Stock giảm đúng.
- Order status đổi sang `PREPARED`.

### 15.2. Test case 2 – Create order có promotion
#### Bước thực hiện
1. Dùng promotion code `SUMMER20`.
2. Gọi API tạo order.
3. Kiểm tra bảng `promotions`.
4. Kiểm tra total amount sau giảm giá.

#### Kết quả mong đợi
- Promotion hợp lệ.
- Discount áp dụng đúng.
- `usage_limit` giảm 1.
- Stock giảm đúng.
- Order status đổi `PREPARED`.

### 15.3. Test case 3 – Redis lock
#### Bước thực hiện
1. Đặt breakpoint ở ProductService.
2. Gọi API create order.
3. Resume qua đoạn lock.
4. Mở RedisInsight kiểm tra key.

#### Kết quả mong đợi
- Thấy lock key đúng prefix.
- Key tồn tại ngắn rồi biến mất.

---

## 16. Checklist công việc triển khai

### 16.1. Phase 1 – Dọn repo
- [ ] Đưa `docker-compose.yml` ra root
- [ ] Thêm `README.md`
- [ ] Thêm `.gitignore`
- [ ] Loại bỏ `.idea`, `target`, generated files

### 16.2. Phase 2 – Chuẩn hoá build
- [ ] Chốt Java version
- [ ] Chuẩn hoá Dockerfile tất cả service
- [ ] Kiểm tra build local từng service

### 16.3. Phase 3 – Chuẩn hoá config
- [ ] Đưa host/port sang env variables
- [ ] Tách config local và docker
- [ ] Bỏ hard-code `localhost` trong docker mode

### 16.4. Phase 4 – Dựng hạ tầng
- [ ] MySQL
- [ ] Redis
- [ ] Zookeeper
- [ ] Kafka
- [ ] Kafka UI
- [ ] RedisInsight

### 16.5. Phase 5 – Dựng ứng dụng
- [ ] Service Registry
- [ ] API Gateway
- [ ] Promotion Service
- [ ] Product Service
- [ ] Order Service

### 16.6. Phase 6 – Seed và smoke test
- [ ] Mount `mysql-init`
- [ ] Verify schema
- [ ] Verify seed data
- [ ] Verify promotion `SUMMER20`

### 16.7. Phase 7 – Integration test
- [ ] Test create order
- [ ] Test Kafka Order -> Product
- [ ] Test Kafka Product -> Order
- [ ] Test Promotion
- [ ] Test Redis lock

---

## 17. Deliverables mong đợi

Sau khi hoàn thành, cần có các deliverables sau:
- `docker-compose.yml` tại root
- `.env` mẫu
- Dockerfile chuẩn cho từng service
- README hướng dẫn chạy
- init SQL chuẩn
- checklist test API / Kafka / Redis
- log xác nhận các flow chính hoạt động

---

## 18. Kết luận

Repo cần được chuẩn hoá theo hướng:
- một entrypoint chạy toàn hệ thống
- config rõ ràng theo môi trường
- Docker Compose ở root
- dữ liệu test đồng nhất
- flow Kafka và Redis được verify bằng test thực tế

Đây là bước bắt buộc trước khi tiếp tục chỉnh sửa nghiệp vụ hoặc debug lỗi tích hợp.
