# 智码工坊 · 前端

AI 零代码应用生成平台的前端，基于 Vue 3 + TypeScript + Vite + Ant Design Vue。用户通过与 AI 对话生成网站应用、实时预览效果、部署分享，并管理自己的应用。

## 技术栈

| 项 | 技术 |
|---|---|
| 框架 | Vue 3.5 + TypeScript 5.8 |
| 构建 | Vite 7 |
| UI 组件库 | Ant Design Vue 4.2 |
| 状态管理 | Pinia 3.0 |
| 路由 | Vue Router 4.5 |
| 请求 | Axios 1.11 |
| 内容渲染 | markdown-it + highlight.js |
| 接口层 | @umijs/openapi（由后端 OpenAPI 文档生成） |
| 代码规范 | ESLint + Prettier |

## 功能

### 用户功能

- 应用创建：输入自然语言提示词创建应用
- AI 对话生成：与应用级 AI 服务多轮对话，流式查看生成过程
- 应用管理：修改、删除自己的应用
- 应用部署：一键部署并获取可访问地址
- 应用列表：分页查询自己的应用、查看精选应用

### 管理员功能

- 应用管理：查看、编辑、删除任意应用
- 精选设置：将应用设为精选（优先级 99）
- 对话管理：查看与管理全部对话记录
- 用户管理：查看与管理用户

## 页面

| 路由 | 页面 | 说明 |
|---|---|---|
| `/` | `HomePage.vue` | 主页：需求输入、快捷提示词、我的作品、精选应用 |
| `/user/login` | `user/UserLoginPage.vue` | 登录 |
| `/user/register` | `user/UserRegisterPage.vue` | 注册 |
| `/app/chat/:id` | `app/AppChatPage.vue` | 生成对话页：左侧对话 + 右侧实时预览 |
| `/app/edit/:id` | `app/AppEditPage.vue` | 应用信息编辑：普通用户改名称，管理员可改封面与优先级 |
| `/admin/appManage` | `admin/AppManagePage.vue` | 应用管理后台 |
| `/admin/chatManage` | `admin/ChatManagePage.vue` | 对话管理后台 |
| `/admin/userManage` | `admin/UserManagePage.vue` | 用户管理后台 |

## 目录结构

```
src/
├── api/            # OpenAPI 自动生成的接口定义与类型
├── assets/         # Logo、AI 头像等静态资源
├── components/     # 全局头尾、应用卡片、部署弹窗、Markdown 渲染等
├── config/         # 环境变量配置（接口地址、部署域名）
├── layouts/        # 基础布局
├── pages/          # 页面（app / admin / user 分组）
├── router/         # 路由配置
├── stores/         # Pinia 状态
└── utils/          # 生成类型映射等工具
```

## 开发

```bash
npm install
npm run dev          # 启动开发服务器
npm run build        # 类型检查 + 构建
npm run type-check   # 仅类型检查
npm run lint         # ESLint 修复
npm run openapi2ts   # 由后端 OpenAPI 文档重新生成接口层
```

开发环境下接口默认代理到 `http://localhost:8123`（单体版后端），可在 `vite.config.ts` 与 `.env.development` 中调整。

## 业务流程

### 创建应用

1. 用户在主页输入提示词
2. 调用创建应用接口，得到应用 ID
3. 跳转到对话页并自动发送初始提示词
4. 通过 SSE 实时显示 AI 的思考、工具调用与文件写入过程
5. 生成完成后在右侧 iframe 中预览网站效果

### 部署应用

1. 对话页点击部署按钮
2. 后端复制生成产物到部署目录、截图上传对象存储
3. 返回 `deployKey` 拼成的访问地址
4. 前端展示部署成功弹窗

## 注意事项

- 生成功能依赖后端 AI 服务与模型 API Key
- 部署功能依赖后端静态资源服务与截图能力
- 管理后台功能需要管理员权限
- 接口层由后端 OpenAPI 文档生成，后端接口变更后需重新执行 `npm run openapi2ts`
