<template>
  <div class="framework">
    <div class="header">
      <div class="logo">
        <div><el-image style="width: 40px; height: 40px" src="../src/assets/logo.png" fit="contain"/> </div>
        <span class="name">城市交通实时监控平台</span>
      </div>
      <div class="right-panel">

        <el-dropdown>
          <div class="user-info">
            <span class="nick-name">{{ userInfo.nickName }}</span>
          </div>
          <template #dropdown>
            <el-dropdown-menu>
              <el-dropdown-item @click="updatePassword" class="message-item">
                修改密码
              </el-dropdown-item>
              <el-dropdown-item @click="logout" class="message-item">
                退出
              </el-dropdown-item>
            </el-dropdown-menu>
          </template>
        </el-dropdown>
      </div>
    </div>
    <div class="body">
      <div class="left-sider">
        <div class="menu-list">
          <div
            @click="jump(item)"
            :class="[
              'menu-item',
              item.menuCode == currentMenu.menuCode ? 'active' : '',
            ]"
            v-for="item in menus"
          >
            <template v-if="item.allShow || (!item.allShow && userInfo.isAdmin)">
              <div :class="['iconfont', 'icon-' + item.icon]"></div>
              <div class="text">
                {{ item.name }}
              </div>
            </template>
          </div>
        </div>
        <div class="menu-sub-list" v-if="currentMenu.children != null">
          <div
            @click="jump(sub)"
            :class="['menu-item-sub', currentPath == sub.path ? 'active' : '']"
            v-for="sub in currentMenu.children"
          >
            <span class="text">{{ sub.name }}</span>
          </div>
        </div>
      </div>
      <div class="body-content">
        <router-view v-slot="{ Component }">
          <component
              ref="routerViewRef"
              :is="Component"
          />
        </router-view>
      </div>
    </div>
    <!--修改密码-->
    <UpdatePassword ref="updatePasswordRef"></UpdatePassword>
  </div>
</template>

<script setup>
import UpdatePassword from "./UpdatePassword.vue";
import {
  ref,
  reactive,
  getCurrentInstance,
  watch,
  nextTick,
  computed,
} from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();
const router = useRouter();
const route = useRoute();

const api = {
  logout: "/userInfo/logout",
};

const timestamp = ref(0);
//获取用户信息
const userInfo = ref(proxy.VueCookies.get("userInfo"));

const menus = [
  {
    icon: "cloude",
    name: "实时监控",
    menuCode: "main",
    path: "/main/all",
    allShow: true,
    children: [
      {
        name: "综合监控",
        category: "all",
        path: "/main/all",
      },
      {
        name: "超速监控",
        category: "video",
        path: "/main/overSpeed",
      },
      {
        name: "流量监控",
        category: "image",
        path: "/main/flow",
      },
      {
        name: "拥堵监控",
        category: "music",
        path: "/main/congestion",
      },
    ],
  },
  {
    path: "/warning",
    icon: "clock",
    name: "智能报警",
    menuCode: "share",
    allShow: true,
  },
  {
    path: "/control",
    icon: "move",
    name: "车辆布控",
    menuCode: "recycle",
    allShow: true,
    children: [
      {
        name: "车辆跟踪",
        path: "/control/track",
      },
      {
        name: "实时布控",
        path: "/control/arrangement",
      },
    ],
  },
  // {
  //   path: "/analysis",
  //   icon: "all",
  //   name: "数据分析",
  //   menuCode: "analysis",
  //   allShow: true,
  // },
  {
    path: "/settings/fileList",
    icon: "settings",
    name: "用户设置 ",
    menuCode: "settings",
    allShow: false,
    children: [
      {
        name: "用户文件",
        path: "/settings/fileList",
      },
      {
        name: "用户管理",
        path: "/settings/userList",
      },
      {
        path: "/settings/sysSetting",
        name: "系统设置",
      },
    ],
  },
];

const jump = (data) => {
  if (!data.path || data.menuCode == currentMenu.value.menuCode) {
    return;
  }
  router.push(data.path);
};


const currentMenu = ref({});
const currentPath = ref();

const setMenu = (menuCode, path) => {
  const menu = menus.find((item) => {
    return item.menuCode === menuCode;
  });
  currentMenu.value = menu;
  currentPath.value = path;
};

watch(
  () => route,
  (newVal, oldVal) => {
    if (newVal.meta.menuCode) {
      setMenu(newVal.meta.menuCode, newVal.path);
    }
  },
  { immediate: true, deep: true }
);

//修改密码
const updatePasswordRef = ref();
const updatePassword = () => {
  updatePasswordRef.value.show();
};

//退出登录
const logout = () => {
  proxy.Confirm(`你确定要删除退出吗`, async () => {
    let result = await proxy.Request({
      url: api.logout,
    });
    if (!result) {
      return;
    }
    proxy.VueCookies.remove;
    router.push("/login");
  });
};
</script>

<style lang="scss" scoped>
.header {
  box-shadow: 0 3px 10px 0 rgb(0 0 0 / 6%);
  height: 56px;
  padding-left: 24px;
  padding-right: 24px;
  position: relative;
  z-index: 200;
  display: flex;
  align-items: center;
  justify-content: space-between;

  .logo {
    display: flex;
    align-items: center;
    .icon-pan {
      font-size: 40px;
      color: #1296db;
    }
    .name {
      font-weight: bold;
      margin-left: 5px;
      font-size: 25px;
      color: #05a1f5;
    }
  }
  .right-panel {
    display: flex;
    align-items: center;
    .icon-transfer {
      cursor: pointer;
    }
    .user-info {
      margin-right: 10px;
      display: flex;
      align-items: center;
      cursor: pointer;
      .avatar {
        margin: 0px 5px 0px 15px;
      }
      .nick-name {
        color: #05a1f5;
      }
    }
  }
}
.body {
  display: flex;
  .left-sider {
    border-right: 1px solid #f1f2f4;
    display: flex;
    .menu-list {
      height: calc(100vh - 56px);
      width: 80px;
      box-shadow: 0 3px 10px 0 rgb(0 0 0 / 6%);
      border-right: 1px solid #f1f2f4;
      .menu-item {
        text-align: center;
        font-size: 14px;
        font-weight: bold;
        padding: 20px 0px;
        cursor: pointer;
        &:hover {
          background: #f3f3f3;
        }
        .iconfont {
          font-weight: normal;
          font-size: 28px;
        }
      }
      .active {
        .iconfont {
          color: #06a7ff;
        }
        .text {
          color: #06a7ff;
        }
      }
    }
    .menu-sub-list {
      width: 160px;
      padding: 10px 0px;
      position: relative;
      .menu-item-sub {
        text-align: center;
        line-height: 40px;
        border-radius: 5px;
        cursor: pointer;
        &:hover {
          background: #f3f3f3;
        }
        .text {
          font-size: 13px;
        }
      }
      .active {
        background: #eef9fe;
        .iconfont {
          color: #05a1f5;
        }
        .text {
          color: #05a1f5;
        }
      }

      .tips {
        margin-top: 10px;
        color: #888888;
        font-size: 13px;
      }

      .space-info {
        position: absolute;
        bottom: 10px;
        width: 100%;
        padding: 0px 5px;
        .percent {
          padding-right: 10px;
        }
        .space-use {
          margin-top: 5px;
          color: #7e7e7e;
          display: flex;
          justify-content: space-around;
          .use {
            flex: 1;
          }
          .iconfont {
            cursor: pointer;
            margin-right: 20px;
            color: #05a1f5;
          }
        }
      }
    }
  }
  .body-content {
    flex: 1;
    width: 100%;
    padding-left: 20px;
  }
}
</style>