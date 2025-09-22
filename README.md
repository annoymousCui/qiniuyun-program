# AI角色扮演平台

一个基于AI的角色扮演语音聊天平台，用户可以搜索感兴趣的角色（如哈利波特、苏格拉底等）并进行语音聊天。

## 项目架构

### 技术栈
- **后端**: Java 17, Spring Boot 3.2.0, Spring Cloud 2023.0.0
- **数据库**: MySQL 8.0, MongoDB 7.0, Redis 7.2
- **消息队列**: RocketMQ 5.1.4
- **注册中心**: Nacos 2.3.0
- **网关**: Spring Cloud Gateway
- **AI能力**: 基础LLM模型调用（OpenAI GPT-3.5-turbo）
- **语音能力**: 语音识别（STT）和文字转语音（TTS）
- **前端**: HTML5, CSS3, JavaScript

### AI能力说明
本项目**仅使用以下基础AI能力**，不调用任何第三方Agent功能：
1. **LLM模型调用**: 纯大语言模型文本生成能力
2. **语音识别**: 音频转文字的基础识别能力
3. **文字转语音**: 文本转音频的基础合成能力

### 模块结构
```
ai-roleplay-platform/
├── ai-gateway/              # 网关模块
├── ai-user-service/         # 用户服务模块
├── ai-character-service/    # 角色管理服务模块
├── ai-chat-service/         # AI聊天服务模块
├── ai-voice-service/        # 语音处理服务模块
├── ai-common/              # 公共模块
├── frontend/               # 前端页面
└── sql/                    # 数据库脚本
```

## 功能特性

### 核心功能
1. **用户管理**: 用户注册、登录、信息管理
2. **角色搜索**: 支持按关键词、分类搜索角色
3. **智能聊天**: 基于基础LLM模型的角色扮演对话
4. **语音交互**: 基础语音识别和文字转语音功能
5. **聊天记录**: 保存和管理聊天历史

### AI能力限制
- ✅ **允许**: 基础LLM模型调用（文本生成）
- ✅ **允许**: 语音识别（STT）和文字转语音（TTS）
- ❌ **禁止**: 第三方Agent能力调用
- ❌ **禁止**: 复杂的AI工作流和自动化任务

### 角色库
- 哈利·波特（魔法世界）
- 苏格拉底（古希腊哲学）
- 夏洛克·福尔摩斯（推理侦探）
- 爱因斯坦（物理学家）
- 孙悟空（神话传说）
- 拿破仑（法国皇帝）
- 达·芬奇（文艺复兴大师）
- 甘道夫（中土世界巫师）
- 孔子（中国古代思想家）
- 蜘蛛侠（漫威超级英雄）

## 系统架构图

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   前端Web界面    │    │   移动端App     │    │   管理后台      │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │      Gateway网关模块      │
                    │   (路由、认证、限流)      │
                    └─────────────┬─────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
┌───────▼────────┐    ┌─────────▼─────────┐    ┌─────────▼─────────┐
│   用户服务      │    │    角色管理服务    │    │   AI聊天服务      │
│  (用户管理)     │    │   (角色库管理)    │    │  (LLM集成)       │
└────────────────┘    └───────────────────┘    └───────────────────┘
        │                        │                        │
        └────────────────────────┼────────────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │     语音处理服务          │
                    │  (语音转文字/文字转语音)   │
                    └─────────────┬─────────────┘
                                 │
        ┌────────────────────────┼────────────────────────┐
        │                        │                        │
┌───────▼────────┐    ┌─────────▼─────────┐    ┌─────────▼─────────┐
│     MySQL      │    │     MongoDB       │    │      Redis        │
│  (用户数据)     │    │  (聊天记录)       │    │   (缓存/会话)     │
└────────────────┘    └───────────────────┘    └───────────────────┘
                                 │
                    ┌─────────────▼─────────────┐
                    │      RocketMQ            │
                    │    (消息队列/事件)        │
                    └───────────────────────────┘
```

## 业务流程

### 用户注册登录流程
1. 用户访问前端页面
2. 选择注册或登录
3. 填写用户信息
4. 后端验证并返回JWT token
5. 前端保存token并跳转到主界面

### 角色聊天流程
1. 用户浏览或搜索角色
2. 选择感兴趣的角色开始聊天
3. 输入文字或语音消息
4. 语音消息通过语音服务转换为文字
5. 聊天服务调用AI生成角色回复
6. 可选择将回复转换为语音播放
7. 聊天记录保存到MongoDB

### 数据流转
1. **用户数据**: MySQL存储用户基本信息
2. **角色数据**: MongoDB存储角色信息和元数据
3. **聊天记录**: MongoDB存储对话历史
4. **缓存数据**: Redis存储会话信息和临时数据
5. **消息队列**: RocketMQ处理异步任务和事件

## 快速开始

### 环境要求
- Java 17+
- MySQL 8.0+
- MongoDB 7.0+
- Redis 7.2+
- RocketMQ 5.1.4+
- Nacos 2.3.0+

### 安装步骤

1. **克隆项目**
```bash
git clone <repository-url>
cd ai-roleplay-platform
```

2. **启动中间件服务**
```bash
# 启动MySQL
# 启动MongoDB
# 启动Redis
# 启动RocketMQ
# 启动Nacos
```

3. **初始化数据库**
```bash
# 执行MySQL初始化脚本
mysql -u root -p < sql/init.sql

# 导入角色数据到MongoDB
# Windows系统
cd sql
import_mongodb_data.bat

# Linux/Mac系统
cd sql
chmod +x import_mongodb_data.sh
./import_mongodb_data.sh

# 或者手动执行
mongoimport --db qiniuyun_ai_roleplay --collection characters --file sql/characters.json --jsonArray --upsert
```

4. **配置服务**
- 修改各服务的配置文件中的数据库连接信息
- 配置OpenAI API密钥
- 配置Nacos注册中心地址

5. **启动服务**
```bash
# 启动网关
cd ai-gateway
mvn spring-boot:run

# 启动用户服务
cd ai-user-service
mvn spring-boot:run

# 启动角色服务
cd ai-character-service
mvn spring-boot:run

# 启动聊天服务
cd ai-chat-service
mvn spring-boot:run

# 启动语音服务
cd ai-voice-service
mvn spring-boot:run
```

6. **访问应用**
- 打开浏览器访问: http://localhost:8080
- 或直接打开 frontend/index.html

## API接口

### 用户服务 (ai-user-service:8081)
- `POST /api/user/register` - 用户注册
- `POST /api/user/login` - 用户登录
- `GET /api/user/info` - 获取用户信息
- `PUT /api/user/update` - 更新用户信息

### 角色服务 (ai-character-service:8082)
- `GET /api/character/search` - 搜索角色
- `GET /api/character/popular` - 获取热门角色
- `GET /api/character/{id}` - 获取角色详情
- `POST /api/character` - 创建角色
- `PUT /api/character/{id}` - 更新角色
- `DELETE /api/character/{id}` - 删除角色

### 聊天服务 (ai-chat-service:8083)
- `POST /api/chat/send` - 发送消息
- `GET /api/chat/history` - 获取聊天历史
- `DELETE /api/chat/clear` - 清空聊天记录

### 语音服务 (ai-voice-service:8084)
- `POST /api/voice/speech-to-text` - 语音转文字
- `POST /api/voice/text-to-speech` - 文字转语音

## 配置说明

### 数据库配置
- MySQL: 存储用户数据
- MongoDB: 存储角色和聊天记录
- Redis: 缓存和会话管理

### 中间件配置
- Nacos: 服务注册与发现
- RocketMQ: 消息队列和事件处理
- Gateway: API网关和路由

### AI服务配置
- **LLM服务**: 配置OpenAI API密钥，仅使用基础文本生成能力
- **语音识别**: 可配置百度、阿里云等语音识别API（可选）
- **TTS服务**: 可配置百度、阿里云等文字转语音API（可选）
- **注意**: 所有AI能力都为基础调用，不涉及Agent功能

## 开发说明

### 项目特点
1. **微服务架构**: 各模块独立部署，便于扩展
2. **前后端分离**: 前端纯HTML/JS，后端RESTful API
3. **数据库分离**: 不同类型数据使用不同数据库
4. **异步处理**: 使用消息队列处理耗时任务
5. **缓存优化**: Redis缓存提升性能

### 扩展建议
1. 添加更多AI模型支持
2. 实现实时语音对话
3. 增加角色自定义功能
4. 添加群聊功能
5. 实现移动端适配

## 注意事项

1. 确保所有中间件服务正常运行
2. 配置正确的数据库连接信息
3. 设置有效的OpenAI API密钥
4. 根据实际环境调整端口配置
5. 生产环境需要配置HTTPS和安全认证

## 许可证

MIT License
