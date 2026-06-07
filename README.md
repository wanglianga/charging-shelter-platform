# 电动车充电车棚管理平台

## 原始需求

> 后端修复 backend/pom.xml 中 mysql:mysql-connector-java 缺少版本导致 Maven 无法解析的问题，并确认 mvn spring-boot:run 可以启动到 /api；前端修复 frontend/index.html 中 title 标签错误、dev 服务访问根路径返回 404 的问题，并确认 npm run dev 后页面可正常打开；同时修复docker启动的问题，完成后再进行最小功能自测：登录后能看到车棚占用、预约记录、订单、巡检、告警和投诉等核心页面，至少验证一条业主预约/充电订单流程和一条告警/投诉处理流程。

## 项目原始需求

> 搭建一个给物业、业主、充电运维商和社区安全员使用的电动车充电车棚平台，Vue3 展示车棚占用、预约和巡检记录，Spring Boot 管理充电订单、电表读数、费用结算和安全告警。业主预约车棚和充电插座，扫码开始充电；物业维护车位、收费规则、黑名单和投诉；运维商上报电表、插座状态、故障维修和断电记录；安全员记录消防通道占用、飞线充电、烟感报警和夜间巡查。系统要把预约、入棚、充电、计费、离场、维修和巡检连成闭环。插座跳闸、超时占位、消防通道堵塞、业主投诉要对应不同处理结果。

## 项目简介

本项目是一个面向多角色电动车充电车棚管理平台，采用前后端分离架构：

- **前端**：Vue 3 + Vite + Element Plus + ECharts
- **后端**：Spring Boot 2.7 + Spring Data JPA + H2/MySQL
- **容器化**：Docker + Docker Compose

## 技术栈

### 后端
- Spring Boot 2.7.18
- Spring Data JPA
- H2 Database（开发环境）
- MySQL 8.0（生产环境）
- Lombok
- Maven

### 前端
- Vue 3.3
- Vite 4.4
- Element Plus 2.3
- Vue Router 4.2
- Pinia 2.1
- Axios 1.5
- ECharts 5.4

## 功能模块

### 1. 业主功能
- 查看车棚占用状态
- 预约车棚和充电插座
- 扫码开始充电
- 查看充电订单和费用
- 提交投诉

### 2. 物业功能
- 维护车棚和插座信息
- 管理收费规则
- 管理用户黑名单
- 处理业主投诉
- 查看充电订单和费用结算

### 3. 运维商功能
- 上报电表读数
- 上报插座状态
- 记录故障维修
- 记录断电记录

### 4. 安全员功能
- 记录消防通道占用
- 记录飞线充电
- 记录烟感报警
- 记录夜间巡查

### 5. 业务闭环
预约 → 入棚 → 充电 → 计费 → 离场 → 维修 → 巡检

### 6. 告警处理
- 插座跳闸 → 通知运维商维修
- 超时占位 → 通知物业处理
- 消防通道堵塞 → 通知安全员处理
- 业主投诉 → 物业跟进处理

## 启动方式

### 前置要求

- Node.js >= 16
- JDK 1.8
- Maven 3.6+
- Docker 20+
- Docker Compose 2+

---

## Docker 一键启动（推荐）

### 1. 构建并启动所有服务

```bash
docker compose up --build
```

### 2. 后台运行

```bash
docker compose up --build -d
```

### 3. 查看服务日志

```bash
docker compose logs -f
```

### 4. 停止服务

```bash
docker compose down
```

### 5. 访问地址

- 前端页面：http://localhost
- 后端API：http://localhost:8080/api
- H2数据库控制台：http://localhost:8080/api/h2-console

---

## 本地开发启动

### 后端启动

#### 1. 进入后端目录

```bash
cd backend
```

#### 2. 安装依赖并构建

```bash
mvn clean install -DskipTests
```

#### 3. 启动应用

```bash
mvn spring-boot:run
```

后端启动后访问：http://localhost:8080/api

#### 4. H2 数据库控制台

访问地址：http://localhost:8080/api/h2-console

连接参数：
- JDBC URL: `jdbc:h2:mem:charging_shelter`
- 用户名: `sa`
- 密码: （空）

### 前端启动

#### 1. 进入前端目录

```bash
cd frontend
```

#### 2. 安装依赖

```bash
npm install
```

#### 3. 启动开发服务器

```bash
npm run dev
```

前端启动后访问：http://localhost:3000

---

## 测试账号

系统预置了以下测试账号：

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 物业 | admin | admin123 |
| 业主 | owner1 | owner123 |
| 业主 | owner2 | owner123 |
| 运维商 | operator | operator123 |
| 安全员 | officer | officer123 |

---

## 目录结构

```
.
├── backend/                    # Spring Boot 后端
│   ├── src/
│   │   └── main/
│   │       ├── java/com/charging/shelter/
│   │       │   ├── entity/     # 实体类
│   │       │   ├── repository/ # 数据访问层
│   │       │   ├── service/    # 业务逻辑层
│   │       │   ├── controller/ # 控制层
│   │       │   ├── config/     # 配置类
│   │       │   ├── dto/        # 数据传输对象
│   │       │   └── enums/      # 枚举类
│   │       └── resources/
│   │           └── application.yml
│   ├── Dockerfile
│   ├── .dockerignore
│   └── pom.xml
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── views/           # 页面组件
│   │   ├── components/      # 公共组件
│   │   ├── router/        # 路由配置
│   │   ├── store/         # 状态管理
│   │   ├── api/           # API 封装
│   │   ├── utils/         # 工具函数
│   │   ├── assets/        # 静态资源
│   │   ├── App.vue
│   │   └── main.js
│   ├── public/
│   ├── Dockerfile
│   ├── .dockerignore
│   ├── nginx.conf
│   ├── vite.config.js
│   ├── index.html
│   └── package.json
├── mysql/                    # MySQL 初始化脚本
│   └── init.sql
├── docker-compose.yml      # Docker Compose 配置
├── Dockerfile              # 根目录 Dockerfile
├── .dockerignore
├── .done                 # 任务完成记录
└── README.md             # 项目说明文档
```

## API 接口列表

### 用户管理
- `GET /api/users` - 获取所有用户
- `GET /api/users/{id}` - 获取用户详情
- `POST /api/users` - 创建用户
- `POST /api/users/login` - 用户登录
- `PUT /api/users/{id}/blacklist` - 设置/取消黑名单

### 车棚管理
- `GET /api/shelters` - 获取所有车棚
- `GET /api/shelters/{id}` - 获取车棚详情
- `POST /api/shelters` - 新增车棚
- `PUT /api/shelters/{id}` - 更新车棚
- `DELETE /api/shelters/{id}` - 删除车棚

### 插座管理
- `GET /api/sockets` - 获取所有插座
- `GET /api/sockets/shelter/{shelterId}` - 获取车棚下的插座
- `POST /api/sockets` - 新增插座
- `PUT /api/sockets/{id}/status/{status}` - 更新插座状态

### 预约管理
- `GET /api/reservations` - 获取所有预约
- `GET /api/reservations/user/{userId}` - 获取用户预约
- `POST /api/reservations` - 创建预约
- `PUT /api/reservations/{id}/checkin` - 确认入棚
- `PUT /api/reservations/{id}/cancel` - 取消预约
- `PUT /api/reservations/{id}/complete` - 完成预约

### 订单管理
- `GET /api/orders` - 获取所有订单
- `GET /api/orders/user/{userId}` - 获取用户订单
- `POST /api/orders/start` - 开始充电
- `PUT /api/orders/{id}/stop` - 结束充电
- `PUT /api/orders/{id}/pay` - 支付订单

### 告警管理
- `GET /api/alarms` - 获取所有告警
- `GET /api/alarms/status/{status}` - 按状态获取告警
- `POST /api/alarms` - 上报告警
- `PUT /api/alarms/{id}/process` - 开始处理
- `PUT /api/alarms/{id}/resolve` - 解决告警
- `PUT /api/alarms/{id}/dismiss` - 忽略告警

### 巡检管理
- `GET /api/inspections` - 获取所有巡检记录
- `GET /api/inspections/officer/{officerId}` - 获取安全员巡检记录
- `POST /api/inspections` - 新增巡检记录
- `PUT /api/inspections/{id}/resolve` - 标记问题已解决

### 投诉管理
- `GET /api/complaints` - 获取所有投诉
- `POST /api/complaints` - 提交投诉
- `PUT /api/complaints/{id}/handle` - 处理投诉

### 维修管理
- `GET /api/maintenance` - 获取所有维修记录
- `GET /api/maintenance/pending` - 获取待维修记录
- `POST /api/maintenance` - 上报故障
- `PUT /api/maintenance/{id}/complete` - 完成维修

### 收费规则
- `GET /api/fee-rules` - 获取所有收费规则
- `GET /api/fee-rules/active` - 获取当前生效规则
- `POST /api/fee-rules` - 新增收费规则
- `PUT /api/fee-rules/{id}` - 更新收费规则
- `DELETE /api/fee-rules/{id}` - 删除收费规则

### 电表读数
- `GET /api/meter-readings` - 获取所有电表读数
- `GET /api/meter-readings/socket/{socketId}` - 获取插座的电表读数
- `POST /api/meter-readings` - 上报电表读数

## 注意事项

1. 开发环境默认使用 H2 内存数据库，数据不会持久化
2. 生产环境建议切换到 MySQL，修改 `backend/src/main/resources/application.yml` 配置
3. Docker 启动时默认使用 MySQL 数据库
4. 系统首次启动会自动初始化测试数据
5. 密码为简化实现未加密，生产环境建议使用加密存储
