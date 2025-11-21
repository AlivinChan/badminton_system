# 校园羽毛球馆场地预约管理系统

## 项目简介

这是一个基于Java开发的校园羽毛球馆场地预约管理系统，支持控制台和Swing图形界面两种操作方式。系统使用对象数组作为数据存储，并通过Java序列化实现数据持久化。

## 功能特性

### 学生功能
- 学生注册与登录
- 查询可用场地（按日期、时段、类型）
- 预约场地（自动冲突检测和费用计算）
- 取消预约
- 查看我的预约
- 对已完成预约进行评分（1-5分）

### 管理员功能
- 管理员登录
- 查看所有预约
- 确认预约完成
- 更改场地状态（可用/维护中）
- 场地评分统计
- 收入统计（按时间段）

## 技术特点

- **面向对象设计**：采用MVC架构，职责清晰
- **数据存储**：使用对象数组，支持自动扩容
- **持久化**：Java序列化保存到 `data.db` 文件
- **冲突检测**：智能检测时段重叠冲突
- **费用计算**：支持工作日/周末、白天/晚间不同费率
- **异常处理**：完善的业务异常和输入校验

## 项目结构

```
src/main/java/com/badminton/
├── model/              # 实体类
│   ├── Student.java
│   ├── Admin.java
│   ├── Court.java
│   ├── Booking.java
│   ├── TimeSlot.java
│   ├── CourtType.java
│   ├── CourtStatus.java
│   └── BookingState.java
├── service/            # 业务服务层
│   ├── UserService.java
│   ├── AdminService.java
│   ├── CourtService.java
│   ├── BookingService.java
│   └── StatisticsService.java
├── persistence/        # 数据持久化
│   └── InMemoryDB.java
├── util/              # 工具类
│   ├── BusinessException.java
│   ├── FeePolicy.java
│   └── DefaultFeePolicy.java
├── ui/
│   ├── console/       # 控制台界面
│   │   └── ConsoleUI.java
│   └── swing/         # Swing图形界面
│       ├── MainFrame.java
│       ├── LoginPanel.java
│       ├── StudentDashboardPanel.java
│       └── AdminDashboardPanel.java
└── Main.java          # 主程序入口（控制台）
```

## 运行说明

### Windows系统

1. **编译项目**：双击运行 `compile.bat` 或命令行执行：
   ```bash
   compile.bat
   ```

2. **运行控制台界面**：双击运行 `run-console.bat` 或命令行执行：
   ```bash
   run-console.bat
   ```

3. **运行Swing图形界面**：双击运行 `run-swing.bat` 或命令行执行：
   ```bash
   run-swing.bat
   ```

### Linux/Mac系统

1. **编译项目**：执行 `compile.sh`（需要先添加执行权限）：
   ```bash
   chmod +x compile.sh
   ./compile.sh
   ```

2. **运行控制台界面**：
   ```bash
   chmod +x run-console.sh
   ./run-console.sh
   ```

3. **运行Swing图形界面**：
   ```bash
   chmod +x run-swing.sh
   ./run-swing.sh
   ```

### 手动编译运行

#### 方式一：控制台界面

```bash
# 编译
javac -d out -encoding UTF-8 -sourcepath src/main/java src/main/java/com/badminton/**/*.java

# 运行
java -cp out com.badminton.Main
```

#### 方式二：Swing图形界面

```bash
# 编译
javac -d out -encoding UTF-8 -sourcepath src/main/java src/main/java/com/badminton/**/*.java

# 运行
java -cp out com.badminton.ui.swing.MainFrame
```

## 使用说明

### 首次运行

系统会自动创建默认数据：
- **默认管理员**：工号 `admin`，密码 `admin123`
- **默认场地**：
  - C001, C002：单打场地
  - C003, C004：双打场地

### 控制台操作

1. **学生注册**：输入学号、姓名、手机号
2. **学生登录**：输入学号即可登录
3. **查询可用场地**：输入日期（yyyy-MM-dd）、开始时间（HH:mm）、结束时间（HH:mm）、场地类型
4. **预约场地**：输入场地编号和时段信息
5. **管理员登录**：输入工号和密码

### 费用标准

- **工作日白天**（18:00之前）：
  - 单打：10元/小时
  - 双打：15元/小时
- **工作日晚间/周末**（18:00之后或周末）：
  - 单打：15元/小时
  - 双打：20元/小时

费用按分钟精确计算。

### 数据格式

- **日期格式**：`yyyy-MM-dd`（例如：2024-01-15）
- **时间格式**：`HH:mm`（例如：14:30）

## 核心算法

### 1. 预约冲突检测

使用 `TimeSlot.overlaps()` 方法检测时段重叠：
- 两时段不重叠当且仅当：`this.end <= other.start || other.end <= this.start`
- 重叠条件取反即可

### 2. 费用计算策略

实现 `FeePolicy` 接口，根据：
- 场地类型（单打/双打）
- 日期（工作日/周末）
- 时段（白天/晚间）

自动计算费用。

### 3. 场地评分统计

统计每个场地的平均评分和评分次数，只统计已完成的预约且评分大于0的记录。

## 测试建议

1. **学生注册与登录**：测试学号唯一性、手机号格式校验
2. **预约功能**：测试正常预约、冲突检测、场地维护状态
3. **取消预约**：测试权限检查、状态检查
4. **管理员功能**：测试确认完成、状态更改
5. **费用计算**：测试跨时段、跨白天/晚间、跨周末
6. **持久化**：重启程序后验证数据加载

## 扩展功能建议

- 预约撤销时间限制（如距开始1小时前可取消）
- 优先权策略（先到先得）
- 邮件/短信提醒
- 导出报表（CSV/PDF）
- 数据库支持（SQLite/MySQL）
- Web版本（REST API + 前端）

## 开发环境

- Java 8 或更高版本
- 无需额外依赖（仅使用Java标准库）

## 注意事项

- 首次运行会在当前目录创建 `data.db` 文件用于数据持久化
- 控制台输入请严格按照提示格式输入
- 日期和时间格式错误会导致操作失败，请仔细输入

