<template>
  <div class="vertical-scroll">
    <div class="top">
      危险驾驶与涉嫌套牌报警
    </div>
    <div class="body">
      <div class="message-container">
        <div style="margin-left: 200px">
          <Vue3Marquee :vertical="true">
            <div v-for="(item, index) in messageList" :key="index" class="message-item">
              {{ item.actionTime }} 车辆&nbsp;&nbsp; <span style="color: #007fff">{{ item.car }}</span>&nbsp;&nbsp;于&nbsp;&nbsp;<div style="width: 100px;text-align: center">{{ item.location }}</div>  <span style="color: red">{{ item.violation }}</span>
            </div>
          </Vue3Marquee>
        </div>
        <div style="margin-left: 300px">
          <Vue3Marquee :vertical="true">
            <div v-for="(item, index) in messageList1  " :key="index" class="message-item">
              {{ item.actionTime }} 车辆&nbsp;&nbsp; <span style="color: #007fff">{{ item.car }}</span>&nbsp;&nbsp;于&nbsp;&nbsp;<div style="width: 100px;text-align: center">{{ item.location }}</div>  <span style="color: red">{{ item.violation }}</span>
            </div>
          </Vue3Marquee>
        </div>
      </div>
      <div class="table">
        <div class="left" style="width: 45%" >
          <div style="display: flex">
            <span>涉嫌套牌</span>
            <el-button style="margin-left: auto">刷新</el-button>
          </div>
          <el-table :data="messageList" height="600" :key="updateTable" border>
            <el-table-column prop="actionTime" label="时间" width="180" />
            <el-table-column prop="car" label="车牌号" width="180" />
            <el-table-column prop="location" label="发现地点" />
            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <el-button
                    link
                    type="primary"
                    size="small"
                    @click.prevent="router.push('/control/track')"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="display: flex;margin-top: 10px">
            <el-input
                v-model="input1"
                style="width: 240px"
                size="large"
                placeholder="查找"
                :suffix-icon="Search"
            />
            <el-pagination style="margin-left: auto" background layout="prev, pager, next" :total=30 @change="pageChange"/>
          </div>
        </div>
        <div class="right" style="width: 45%;margin-left: 40px">
          <div style="display: flex">
            <span>危险驾驶</span>
            <el-button style="margin-left: auto">刷新</el-button>
          </div>
          <el-table :data="messageList1" height="600" :key="updateTable" border>
            <el-table-column prop="actionTime" label="时间" width="180" />
            <el-table-column prop="car" label="车牌号" width="180" />
            <el-table-column prop="location" label="发现地点" />
            <el-table-column fixed="right" label="操作" width="120">
              <template #default="scope">
                <el-button
                    link
                    type="primary"
                    size="small"
                    @click.prevent="router.push('/control/track')"
                >
                  查看
                </el-button>
              </template>
            </el-table-column>
          </el-table>
          <div style="display: flex;margin-top: 10px">
            <el-input
                v-model="input1"
                style="width: 240px"
                size="large"
                placeholder="查找"
                :suffix-icon="Search"
            />
            <el-pagination style="margin-left: auto" background layout="prev, pager, next" :total=30 @change="pageChange"/>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import {Vue3Marquee} from 'vue3-marquee'
import {Search} from "@element-plus/icons-vue";

// messageList 数据请按照你的实际需求获取或设置
const router = useRouter();
let messageList = [
  {
    car: "苏D95674",
    violation: "涉嫌套牌",
    actionTime: "2024-05-06 09:22:16",
    location: "未命名道路"
  },{
    car: "苏D63299",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 13:52:36",
    location: "西太湖大道"
  },{
    car: "苏D73246",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 13:56:47",
    location: "金武快速路"
  },{
    car: "苏D99871",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 14:01:06",
    location: "谢桥路"
  },{
    car: "苏D64358",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 14:33:32",
    location: "花园街"
  },{
    car: "苏D95674",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 14:34:16",
    location: "千峰线"
  },{
    car: "苏D63299",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 14:41:35",
    location: "夏溪西路"
  },{
    car: "苏D73246",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 14:56:57",
    location: "燕山路"
  },{
    car: "苏D99871",
    violation: "涉嫌套牌",
    actionTime: "2024-05-13 16:07:29",
    location: "谢桥路"
  },
];

let messageList1 = [
  {
    car: "苏D37295",
    violation: "危险驾驶",
    actionTime: "2024-05-13 13:22:36",
    location: "天目山路"
  },{
    car: "苏D47454",
    violation: "危险驾驶",
    actionTime: "2024-05-13 14:12:06",
    location: "未命名道路"
  },{
    car: "苏D63018",
    violation: "危险驾驶",
    actionTime: "2024-05-13 14:23:26",
    location: "清凉东路"
  },{
    car: "苏D87264",
    violation: "危险驾驶",
    actionTime: "2024-05-13 15:29:01",
    location: "邮电路"
  },
  {
    car: "苏D39244",
    violation: "危险驾驶",
    actionTime: "2024-05-13 16:47:52",
    location: "创业中路"
  }
];

// 初始化
onMounted(() => {
});

</script>

<style scoped lang="scss">
.message-container {
  margin-top: 30px;
  height: 100px;
  display: flex;
}
.message-item {
  margin-top: 20px;
  width: 100%;
  display: flex;
}
.table {
  display: flex;
  margin-top: 20px;
}
</style>
