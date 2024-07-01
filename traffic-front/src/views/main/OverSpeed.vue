<script setup>
import {ref, reactive, getCurrentInstance, nextTick, computed, onMounted} from "vue";
import { useRouter, useRoute } from "vue-router";
import { Search } from '@element-plus/icons-vue'
import * as path from "node:path";
const { proxy } = getCurrentInstance();
const router = useRouter();
let params = {
  pageSize: 50,
  pageNo: 1,
}
let pageTotal = 0;
let updateTable = ref(false);
let tableData = ref([]);

onMounted(() => {
  loadData();
})
const loadData = async () => {
  console.log(pageTotal)
  let res = await proxy.Request({
    url: "/speeding/page",
    showLoading: true,
    params: params,
  });
  pageTotal = res.data.totalCount / 5;
  tableData.value = res.data.list;
  updateTable.value = !updateTable.value;
}

const pageChange = (page) => {
  params.pageNo = page;
  loadData();
}

</script>

<template>
  <div>
    <div class="top">
      <div style="padding-top: 10px">
        <span>超速车辆监控</span>
      </div>
      <div style="text-align: right;width: 90%;padding-bottom: 10px">
        <el-input
            v-model="input1"
            style="width: 240px"
            size="large"
            placeholder="查找"
            :suffix-icon="Search"
        />
      </div>
    </div>
    <div class="body">
      <div class="table">
        <el-table :data="tableData" height="760" style="width: 100%" :key="updateTable" border>
          <el-table-column prop="actionTime" label="时间" width="180" />
          <el-table-column prop="car" label="车牌号" width="180" />
          <el-table-column prop="areaName" label="超速区域" />
          <el-table-column prop="roadName" label="超速地点" />
          <el-table-column prop="limitSpeed" label="限速" />
          <el-table-column prop="realSpeed" label="实际车速" />
          <el-table-column fixed="right" label="操作" width="120">
            <template #default="scope">
              <el-button
                  link
                  type="primary"
                  size="small"
                  @click.prevent="router.push({path: '/control/track', query: {car: scope.row.car}})"
              >
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div class="pagination">
        <el-pagination background layout="prev, pager, next" :total=pageTotal @change="pageChange"/>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.top {
  margin-top: 20px;
  display: flex;
}
.body {
  .pagination {
    float: right;
    padding-right: 100px;
    padding-top: 15px;
  }
}
</style>