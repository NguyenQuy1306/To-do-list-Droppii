# To-do-list-Droppii

# Todo List Project

## 📌 Giới thiệu

Dự án **Todo List** là project ngắn hạn được thực hiện nhằm pass vòng 1 vị trí Intern Backend Developer tại Droppii.

## 🛠️ Công nghệ sử dụng:

- **Spring Boot**
- **PostgreSQL** (Partitioning, Indexing)
- **Hibernate & JPA**
- **Lombok**
- **Maven**
- **Redis**
- **Docker**
- **Kafka**

## 📂 Cấu trúc dự án
Gồm 2 service: TaskService và NotificationService
### TaskService
```
├── src
│   ├── main
│   │   ├── java/com/interviewproject/todolist
│   │   │   ├── config          # Cấu hình ứng dụng
│   │   │   ├── constants       # Định nghĩa các hằng số sử dụng trong ứng dụng
│   │   │   ├── controller
│   │   │   ├── exception       # Xử lý các ngoại lệ và lỗi trong ứng dụng
│   │   │   ├── handler
│   │   │   ├── model           # Định nghĩa các Entity
│   │   │   ├── repository
│   │   │   ├── service         # Xử lý logic nghiệp vụ chính
│   │   │   ├── specification   # Tạo truy vấn động với Spring JPA Specification
│   ├── resources
│   │   ├── application.properties  # Cấu hình Spring Boot
│   │   ├── task.sql                # File tạo bảng task và dữ liệu mẫu

```
### NotificationService
```
├── src
│   ├── main
│   │   ├── java/com/todolist/notificationservice
│   │   │   ├── config          # Cấu hình ứng dụng
│   │   │   ├── constants       # Định nghĩa các hằng số sử dụng trong ứng dụng
│   │   │   ├── controller
│   │   │   ├── exception       # Xử lý các ngoại lệ và lỗi trong ứng dụng
│   │   │   ├── handler
│   │   │   ├── model           # Định nghĩa các Entity
│   │   │   ├── repository
│   │   │   ├── service         # Xử lý logic nghiệp vụ chính
│   ├── resources
│   │   ├── application.properties  # Cấu hình Spring Boot

```

## 🎯 Các tính năng chính

- [x] **Quản lý công việc (Task CRUD) và quản lý các phụ thuộc (Dependency CRUD)**
- [x] **Sử dụng Redis Cache để lưu trữ task thường được query**
- [x] **Sử dụng PostgreSQL Partitioning để tối ưu hoá truy vấn**
- [x] **Chỉ mục (Indexing) giúp tăng hiệu suất tìm kiếm**
- [x] **Gửi thông báo các task sắp hết hạn và hết hạn**

## 🗄️ Sơ đồ Database

Dưới đây là sơ đồ database của dự án:

![Database Schema](img/database-diagram.svg)

## 🚀 Cách chạy dự án với Docker

### 1️⃣ Chạy ứng dụng:

docker-compose up -d --build

**NOTE: Restart 2 container: task_service và notification_service nếu gặp lỗi.** 

### 3️⃣ Thêm index để tăng tốc truy vấn

```sql
CREATE INDEX idx_task_status_duedate ON task(status, duedate);
```
