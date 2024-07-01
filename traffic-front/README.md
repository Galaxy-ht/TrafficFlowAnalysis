# traffic-front

可视化交互层。基于 Vue 3 Composition API + Vite 5 构建的交通监控单页应用，集成 Mars2D GIS 地图引擎与 ECharts 可视化图表，提供实时交通监控、违规报警推送、车辆轨迹追踪、预测数据展示等交互界面。

## 页面结构

采用 Vue Router 嵌套路由，`Framework.vue` 作为主布局（顶部导航栏 + 左侧菜单 + 内容区），所有业务页面作为其子路由。路由守卫 `beforeEach` 从 Cookie 中读取用户信息，未登录用户自动重定向至 `/login`。

```
/login                          # 登录/注册
/
├── /main/all                   # 综合监控面板（默认首页）
├── /main/overSpeed             # 超速监控
├── /main/congestion            # 拥堵监控与预测
├── /main/flow                  # 流量监控与预测
├── /warning                    # 危驾预警（WebSocket 实时推送）
├── /control/track              # 车辆轨迹跟踪
└── /control/arrangement        # 实时车辆布控
```

### 综合监控面板

![综合监控界面](../docs/images/screenshot-dashboard.png)

三栏布局：左侧区域-道路-卡口三级索引树，中间 Mars2D 地图视图，右侧汇总统计面板。卡口以彩色标记点渲染在地图上，颜色根据拥堵指数分级。支持点击卡口查看实时过车详情。

### 超速监控

![超速监控界面](../docs/images/screenshot-overspeed.png)

分页表格展示超速车辆完整信息（车牌号、卡口位置、道路名称、实际速度、限速值、拍摄时间）。支持按车牌号、区域、道路关键词模糊搜索。

### 拥堵监控与预测

![拥堵监控与预测界面](../docs/images/screenshot-congestion.png)

以图表形式展示各区县/道路的实时拥堵指数与预测拥堵指数。拥堵指数计算公式：

```
CI = (Vf / Vc) × (Q / C)
```

其中 `Vf` 为自由流速，`Vc` 为当前平均车速，`Q` 为当前车流量，`C` 为道路最大通行能力。实时数据来自 Flink 窗口聚合，预测数据来自 GCN-tffc 模型输出。

### 流量监控与预测

![实时车流量监控界面](../docs/images/screenshot-flow.png)

展示 Flink 实时计算层得到的道路车流量情况，以及 TCN 模型训练层得到的车流量预测情况，以直观简洁的形式将道路车流量展示给用户。

### 实时智能报警

![实时智能报警界面](../docs/images/screenshot-alert.png)

基于 WebSocket 长连接实现服务端主动推送。Flink 检测到套牌或危险驾驶行为后，通过后端 WebSocket 通道即时推送至本页面。告警列表分为"涉嫌套牌"和"危险驾驶"两个标签页，最新告警自动置顶。支持点击告警记录跳转至轨迹跟踪页面。

### 车辆轨迹跟踪

![车辆轨迹跟踪界面](../docs/images/screenshot-track.png)

输入车牌号从 HBase 检索该车辆全部历史轨迹，在地图上以折线连接各卡口点位，标注通过时间戳。同时展示车辆基本信息和违法记录列表。

### 车辆实时布控

![车辆实时布控界面](../docs/images/screenshot-arrangement.png)

在地图上展示各区县、道路的实时车辆分布，支持地图快速定位拥堵点，识别高峰期和低谷期的流量变化，制定针对性的交通调控策略。

## 用户认证

![用户注册](../docs/images/screenshot-register.png)

![用户登录](../docs/images/screenshot-login.png)

系统提供完整的用户注册与登录功能。注册时需填写用户名、邮箱和密码，通过邮箱验证码校验后完成注册。登录后 Token 持久化至 Cookie，路由守卫自动校验登录状态。

## 技术实现

### GIS 地图集成

地图引擎选用 Mars2D（基于 Leaflet 的国产 GIS 框架），配置在 `public/config/config.json`。默认底图包含天地图、高德地图、ArcGIS、OSM 等多种图层。默认视图中心为常州坐标（119.729790, 31.642752）。通过 `mars2d-echarts` 插件在 GIS 地图上叠加 ECharts 统计图层，实现地图与图表的一体化渲染。

### 状态管理

使用 Pinia 管理全局状态，主要包括用户登录信息、当前选中区域/道路/卡口等交互状态。

### HTTP 通信

`utils/Request.js` 封装 Axios 实例，统一处理请求拦截（自动附加 Token）、响应拦截（错误码统一提示）、加载状态。开发环境通过 Vite 代理将 `/api` 转发至 `http://127.0.0.1:630`。

### 构建优化

Vite 生产构建配置了 `manualChunks` 策略，按 `node_modules` 下的包名自动拆分 vendor chunk。路由全部使用动态 `import()` 实现页面级代码分割，首屏仅加载当前路由所需资源。

## 依赖

| 组件 | 版本 | 用途 |
| ---- | ---- | ---- |
| Vue | 3.2.47 | 前端框架（Composition API + `<script setup>`） |
| Vite | 5.2.0 | 构建工具 |
| Element Plus | 2.2.36 | UI 组件库 |
| Pinia | 2.0.32 | 状态管理 |
| Vue Router | 4.1.6 | 路由管理 |
| Axios | 1.3.4 | HTTP 客户端 |
| Mars2D | 3.2.4 | GIS 地图引擎 |
| Leaflet | 1.9.4 | 地图底层库 |
| mars2d-echarts | 3.2.4 | 地图 ECharts 图层 |
| Sass | 1.59.2 | CSS 预处理 |
| vue-cookies | 1.8.3 | Cookie 管理 |
| js-md5 | 0.7.3 | 密码 MD5 摘要 |

## 本地开发

### 前置条件

- Node.js 18+
- 后端 API 服务运行于 `http://127.0.0.1:630`

### 启动开发服务器

```bash
cd traffic-front
npm install
npm run dev
```

开发服务器监听 `http://localhost:1024`（绑定 `0.0.0.0`），支持 HMR 热更新。

### 生产构建

```bash
npm run build       # 产物输出至 dist/
npm run preview     # 预览生产构建
```

## 配置说明

开发环境 API 代理配置在 `vite.config.js`：

```js
server: {
  port: 1024,
  host: '0.0.0.0',
  proxy: {
    "/api": {
      target: "http://127.0.0.1:630",
      changeOrigin: true,
    }
  }
}
```

地图配置在 `public/config/config.json`，包含底图图层列表、控件配置、默认视图中心坐标等参数。