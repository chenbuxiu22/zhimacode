# 智码工坊 · AI 零代码应用生成平台

一个基于 **Spring Boot 3 + LangChain4j + Vue 3** 的 AI 应用生成平台：用户用自然语言描述需求，AI 自动选择生成策略，通过**工具调用**流式生成完整的可部署网站，并支持可视化编辑、一键部署分享和后台管理。

> 本项目是在鱼皮（程序员鱼皮）[AI 零代码应用生成平台](https://github.com/liyupi/yu-ai-code-mother)教学项目基础上完成的个人重构版本，重新设计了包结构、品牌体系与工程组织，并在此过程中深入学习 AI 智能体、AI 工作流与微服务架构。

## 一、项目功能

### 1. 智能代码生成

用户输入一句自然语言需求，系统自动完成路由决策与代码生成：

- **智能路由**：用轻量模型（qwen-turbo）判断需求复杂度，选择三种生成策略之一
- **三种生成模式**：
  | 模式 | 说明 | 产物 |
  |---|---|---|
  | HTML | 单页面，适合展示型站点 | 单个 `index.html` |
  | MULTI_FILE | 结构分离的原生站点 | `index.html` + `style.css` + `script.js` |
  | VUE_PROJECT | 完整 Vue 3 工程，支持多页面路由 | 整个工程目录（可 npm 构建） |
- **工具调用（Function Calling）**：Vue 工程模式下，AI 通过文件写入 / 读取 / 修改 / 删除 / 目录浏览等工具逐个生成文件，而非一次性吐出一大段代码，从而突破输出 token 上限
- **流式输出**：通过 SSE 实时推送 AI 的思考过程、工具调用与文件写入进度，用户能看到 AI "边想边写"

### 2. 可视化编辑

生成结果实时预览，可进入编辑模式选中页面元素，直接与 AI 对话修改页面，所见即所得。

### 3. 一键部署分享

将生成的应用部署到本地静态目录，自动用 Selenium 截取封面图上传对象存储，生成 `deployKey` 访问地址，同时支持源码打包下载。

### 4. 企业级管理

用户管理、应用管理、对话管理、精选应用设置；基于 Prometheus + Grafana 的 AI 调用指标与系统性能监控。

## 二、技术栈

### 后端

| 分类 | 技术 |
|---|---|
| 基础框架 | Spring Boot 3.5、JDK 21（大量使用虚拟线程） |
| AI 框架 | LangChain4j 1.1.0、LangGraph4j 1.6.0 |
| 大模型 | DeepSeek（chat / reasoner）、阿里云百炼 qwen-turbo（智能路由）、通义万相（Logo 生图） |
| 微服务 | Spring Cloud Alibaba、Nacos（注册中心）、Dubbo 3.3（tri 协议） |
| 数据访问 | MyBatis-Flex、HikariCP、MySQL |
| 缓存 | Redis（Spring Session 共享登录态 + 对话记忆存储）、Redisson（分布式限流）、Caffeine（本地缓存） |
| 对象存储 | 腾讯云 COS |
| 截图 | Selenium + WebDriverManager |
| 监控 | Actuator + Micrometer + Prometheus + Grafana |
| API 文档 | Knife4j（OpenAPI 3） |

### 前端

Vue 3.5 + TypeScript + Vite + Ant Design Vue + Pinia + Vue Router

## 三、AI 能力设计

### 1. AI 服务工厂与多租户隔离

每个应用对应独立的 AI 服务实例，按 `appId + 生成类型` 缓存（Caffeine），实例持有各自的 `MessageWindowChatMemory`（窗口 20 条），实现对话记忆隔离；对话历史落 Redis，支持应用重启后恢复上下文。

### 2. 输入 / 输出护栏

- **输入护栏**：敏感词过滤 + 提示词注入正则识别 + 长度限制，拦截恶意输入
- **输出护栏**：重试式输出校验，保证生成结果符合预期格式

### 3. AI 工作流（LangGraph4j）

使用 LangGraph4j 将复杂生成流程编排为状态图，支持条件分支、并发节点与子图：

```
image_collector → prompt_enhancer → router → code_generator → code_quality_check → project_builder
```

- 图片收集节点内部并发执行：内容配图、插画、架构图、Logo 四类资源并行获取后聚合
- 路由节点按需求类型决定后续走向
- 代码质检节点对大模型产物做结构化校验（返回 `isValid` / `errors` / `suggestions`）

### 4. 流式输出与响应式编程

自定义 `StreamingChatModel` 的流式 handler，把 LangChain4j 的 token 流转换为 Reactor `Flux`，再以 SSE 推送给前端；区分"思考中 / 工具调用 / 内容输出"三类事件，前端分别渲染。

## 四、项目结构

项目提供**单体版**与**微服务版**两套实现，共享同一套前端。

### 单体版（根目录 `src/`）

```
src/main/java/com/zhima/zhimacode/
├── ai/              # AI 服务、路由、护栏、工具调用、流式消息模型
├── langgraph4j/     # LangGraph4j 工作流：节点、状态、AI 服务、图片工具
├── core/            # 生成门面、代码解析器、文件保存器、流处理器、Vue 构建器
├── config/          # 模型配置、Redis 对话记忆、缓存配置
├── controller/      # 应用 / 对话 / 用户 / 静态资源 / 工作流 SSE 接口
├── service/         # 业务服务层
├── manager/         # COS 对象存储
├── ratelimter/      # 基于 Redisson 的分布式限流
├── monitor/         # AI 调用指标采集与上报
└── generator/       # MyBatis-Flex 代码生成器
```

### 微服务版（`zhimacode-microservice/`）

| 模块 | 职责 | 端口 |
|---|---|---|
| `zhimacode-common` | 公共基础：注解、统一响应、异常、常量、工具、COS |
| `zhimacode-model` | 实体 / DTO / VO / 枚举 |
| `zhimacode-client` | Dubbo 服务契约（`InnerUserService`、`InnerScreenshotService`） |
| `zhimacode-user` | 用户服务：登录注册、权限校验 | 8124 |
| `zhimacode-app` | 应用服务：应用与对话管理、代码生成、部署、限流 | 8125 |
| `zhimacode-ai` | AI 能力库（被 app 依赖的 jar，不独立启动） | — |
| `zhimacode-screenshot` | 截图服务：Selenium 网页截图 | 8127 |

服务间通过 Dubbo（tri 协议）调用，Nacos 作为注册中心；用户服务与截图服务以 Dubbo 接口对外暴露，应用服务直接引用。

### 前端（`zhimacode-frontend/`）

```
src/
├── pages/           # 主页、生成对话页、编辑页、管理后台、登录注册
├── components/      # 全局头尾、应用卡片、部署成功弹窗、Markdown 渲染等
├── api/             # OpenAPI 自动生成的接口层
└── stores/          # Pinia 状态（登录用户）
```

## 五、本地运行

### 环境要求

JDK 21、Maven 3.8+、Node.js 18+、MySQL 8、Redis

### 1. 初始化数据库

```bash
mysql -u root -p < sql/create_table.sql
```

脚本会创建 `zhimacode` 库与 `user` / `app` / `chat_history` 三张表。

### 2. 配置环境变量

在项目根目录创建 `.env`：

```properties
DEEPSEEK_API_KEY=你的 DeepSeek API Key
DASHSCOPE_API_KEY=你的阿里云百炼 API Key
# 以下可选
PEXELS_API_KEY=
COS_SECRET_ID=
COS_SECRET_KEY=
```

必填项说明：

- `DEEPSEEK_API_KEY`：[DeepSeek 开放平台](https://platform.deepseek.com/) 申请，用于代码生成与推理
- `DASHSCOPE_API_KEY`：[阿里云百炼](https://bailian.console.aliyun.com/) 申请，用于智能路由与 Logo 生图

可选服务（不配置时对应功能降级，不影响主流程）：Pexels 提供真实图片搜索，COS 提供截图托管与封面访问。

### 3. 启动单体版

```bash
./mvnw spring-boot:run
```

服务启动在 `http://localhost:8123/api`，接口文档 `http://localhost:8123/api/doc.html`。

### 4. 启动微服务版

需先启动 Nacos（默认 `127.0.0.1:8848`，账号密码 `nacos`），再依次启动 `zhimacode-user`、`zhimacode-screenshot`、`zhimacode-app` 三个应用。

### 5. 启动前端

```bash
cd zhimacode-frontend
npm install
npm run dev
```

前端默认代理到 `http://localhost:8123`（单体版）。访问提示的本地地址即可使用。

## 六、配置说明

| 配置项 | 位置 | 说明 |
|---|---|---|
| 模型接入 | `application.yml` → `langchain4j.open-ai.*` | 三类模型：chat / streaming / reasoning / routing |
| 部署域名 | `code.deploy-host` | 生成应用的访问域名前缀 |
| 生成产物目录 | `AppConstant#CODE_OUTPUT_ROOT_DIR` | 默认 `tmp/code_output` |
| 部署产物目录 | `AppConstant#CODE_DEPLOY_ROOT_DIR` | 默认 `tmp/code_deploy` |
| 敏感词与注入规则 | `PromptSafetyInputGuardrail` | 输入护栏词表与正则 |
| 系统提示词 | `src/main/resources/prompt/*.txt` | 7 个生成策略提示词模板 |

## 七、可扩展点

- **新增生成策略**：在 `CodeGenTypeEnum` 增加枚举 → 补充路由提示词 → 实现 `AiCodeGeneratorService` 对应方法 → 注册 Parser 与 Saver
- **调整生成产物形态**：修改 `prompt/codegen-vue-project-system-prompt.txt` 等系统提示词，即可约束 AI 产出的工程结构与技术选型
- **接入其他大模型**：修改 `langchain4j.open-ai.*` 的 `base-url` 与 `model-name`（兼容 OpenAI 协议即可）
- **扩展工作流**：在 `langgraph4j` 包中新增节点并调整图结构
