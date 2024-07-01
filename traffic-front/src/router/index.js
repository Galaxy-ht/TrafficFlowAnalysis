import { createRouter, createWebHistory } from 'vue-router'
import VueCookies from 'vue-cookies'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/login',
      name: '登录',
      component: () => import("@/views/Login.vue")
    },
    {
      path: "/",
      component: () => import("@/views/Framework.vue"),
      children: [
        {
          path: '/',
          redirect: "/main/all"
        },
        {
          path: '/main/all',
          name: '首页',
          meta: {
            needLogin: true,
            menuCode: "main"
          },
          component: () => import("@/views/main/Main.vue")
        },
        {
          path: '/main/overSpeed',
          name: '超速监控',
          meta: {
            needLogin: true,
            menuCode: "main"
          },
          component: () => import("@/views/main/OverSpeed.vue")
        },
        {
          path: '/main/congestion',
          name: '拥堵监控',
          meta: {
            needLogin: true,
            menuCode: "main"
          },
          component: () => import("@/views/main/Congestion.vue")
        },
        {
          path: '/main/flow',
          name: '流量监控',
          meta: {
            needLogin: true,
            menuCode: "main"
          },
          component: () => import("@/views/main/Flow.vue")
        },
        {
          path: '/warning',
          name: '危驾预警',
          meta: {
            needLogin: true,
            menuCode: "share"
          },
          component: () => import("@/views/warning/Dangerous.vue")
        },
        {
          path: '/control',
          redirect: '/control/track'
        },
        {
          path: '/control/track',
          name: '车辆跟踪',
          meta: {
            needLogin: true,
            menuCode: "recycle"
          },
          component: () => import("@/views/control/Track.vue")
        },
        {
          path: '/control/arrangement',
          name: '实时布控',
          meta: {
            needLogin: true,
            menuCode: "recycle"
          },
          component: () => import("@/views/control/Arrangement.vue")
        },
        {
          path: '/analysis',
          name: '数据分析',
          meta: {
            needLogin: true,
            menuCode: "analysis"
          },
          component: () => import("@/views/analysis/Analysis.vue")
        },
      //   {
      //     path: '/settings/sysSetting',
      //     name: '系统设置',
      //     meta: {
      //       needLogin: true,
      //       menuCode: "settings"
      //     },
      //     component: () => import("@/views/admin/SysSettings.vue")
      //   },
      //   {
      //     path: '/settings/userList',
      //     name: '用户管理',
      //     meta: {
      //       needLogin: true,
      //       menuCode: "settings"
      //     },
      //     component: () => import("@/views/admin/UserList.vue")
      //   },
      //   {
      //     path: '/settings/fileList',
      //     name: '用户文件',
      //     meta: {
      //       needLogin: true,
      //       menuCode: "settings"
      //     },
      //     component: () => import("@/views/admin/FileList.vue")
      //   },
      ]
    },
    // {
    //   path: '/shareCheck/:shareId',
    //   name: '分享校验',
    //   component: () => import("@/views/webshare/ShareCheck.vue")
    // },
    // {
    //   path: '/share/:shareId',
    //   name: '分享',
    //   component: () => import("@/views/webshare/Share.vue")
    // }, {
    //   path: '/qqlogincalback',
    //   name: "qq登录回调",
    //   component: () => import('@/views/QqLoginCallback.vue'),
    // }
  ]
})

router.beforeEach((to, from, next) => {
  const userInfo = VueCookies.get("userInfo");
  if (to.meta.needLogin != null && to.meta.needLogin && userInfo == null) {
    router.push("/login");
  }
  next();
})

export default router
