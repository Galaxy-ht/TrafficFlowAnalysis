<template>
  <div style="height: 100%">
    <div class="top">
      <div class="selectors">
        <el-select
            placeholder="常州市"
            style="width: 240px"
            disabled
            class="selector"
        ></el-select>
        <el-select clearable v-model="value" placeholder="区县" style="width: 240px" class="selector" @change="selectCounty">
          <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
        <el-select
            v-model="value1"
            filterable
            remote
            placeholder="查找道路"
            clearable
            :remote-method="remoteMethod"
            :loading="loading"
            style="width: 240px"
            class="selector"
            @change="selectRoad"
        >
          <el-option
              v-for="item in options1"
              :key="item.value"
              :label="item.label"
              :value="item.value"
          />
        </el-select>
      </div>
    </div>
    <div class="map">
      <div style="float: right;margin-right: 30px">
        <span>数据每分钟刷新一次</span>
        <el-icon style="margin-left: 5px;cursor: pointer" @click="refresh"><Refresh /></el-icon>
      </div>
      <Mars2dMap :url="configUrl" map-key="test" ref="marsMap" @onload="marsOnload" />
    </div>
  </div>
</template>

<script setup lang="ts">
import Mars2dMap from "../../components/mars2d-map.vue"
import {getCurrentInstance, onMounted, ref} from 'vue'
import * as mars2d from "mars2d"
const { proxy } = getCurrentInstance();
const L = mars2d.L
const value = ref('')
const loading = ref(false)
const options = [
  {
    value: '01',
    label: '武进区',
  },
  {
    value: '02',
    label: '新北区',
  },
  {
    value: '03',
    label: '钟楼区',
  },
  {
    value: '04',
    label: '天宁区',
  },
  {
    value: '05',
    label: '金坛区',
  },
  {
    value: '06',
    label: '溧阳市',
  },
]
const configUrl = "../config/config.json"
let pageSize = 10000
let echartsLayer = new mars2d.layer.EchartsLayer({})
let areaId: string
interface ListItem {
  value: string
  label: string
}
let states: any
const list = ref<ListItem[]>([])
const options1 = ref<ListItem[]>([])
const value1 = ref<string[]>([])
let roadName = "";
let data
let map :mars2d.Map
onMounted(() => {
  // loadRoad();
  searchRoad("");
})
const loadRoad = async () => {
  map.removeLayer(echartsLayer);
  let res = await proxy.Request({
    url: "/czRoad/page",
    showLoading: true,
    params: {
      areaId: areaId,
      pageSize: pageSize,
      name: roadName
    }
  });
  if (!res) {
    return;
  }
  data = res.data.list.map(item => {
    return {
      value: Math.floor(Math.random() * 20),
      coords: item.theGeom.map(coords => {
        return [coords[1], coords[0]];
      })
    };
  })
  console.log(data)
  showEchartsLayer()
}

const searchRoad = async (params) => {
  let res = await proxy.Request({
    url: "/czRoad/searchRoad",
    showLoading: false,
    params: {
      name: params
    }
  });
  states = res.data;
  list.value = states.map((item) => {
    return { value: `${item.value}`, label: `${item.value}` }
  })
}

const remoteMethod = (query: string) => {
  if (query) {
    loading.value = true
    setTimeout(() => {
      loading.value = false
      options1.value = list.value.filter((item) => {
        return item.label.toLowerCase().includes(query.toLowerCase())
      })
    }, 200)
  } else {
    options1.value = []
  }
}
const marsOnload = (loadedMap: any) => {
  map = loadedMap;
  loadRoad();
}

function refresh() {
  loadRoad();
}
function selectCounty(event) {
  areaId = event;
  pageSize = 1000;
  if (event == "01") {
    pageSize = 10000;
    map.setView([31.700, 119.942], 11)
  } else if (event == "02") {
    map.setView([31.864, 119.955], 11)
  } else if (event == "03") {
    map.setView([31.802, 119.901], 11)
  } else if (event == "04") {
    map.setView([31.793, 119.994], 11)
  } else if (event == "05") {
    map.setView([31.723, 119.582], 11)
  } else if (event == "06") {
    map.setView([31.415, 119.483], 11)
  } else {
    pageSize = 10000;
  }
  loadRoad();
}
function selectRoad(event) {
  roadName = event;
  loadRoad().then(() => {
    let center = data[0].coords[0].reverse();
    console.log(center)
    map.setView(center, 13);
  })
}
function addRoad(road) {
  graphicLayer = new mars2d.layer.GraphicLayer({})
  const graphic = new mars2d.graphic.Polyline({
    latlngs: road,
    style: {
      width: 4,
      color: "rgb(255, 0, 100)"
    }
  })
  graphicLayer.addGraphic(graphic);
  map.addLayer(graphicLayer)
}
function showEchartsLayer() {

  const options = {
    visualMap: {
      type: "piecewise",
      left: "right",
      top: "top",
      /* pieces: [
              {min: 15}, // 不指定 max，表示 max 为无限大（Infinity）。
              {min: 12, max: 15},
              {min: 9, max: 12},
              {min: 6, max: 9},
              {min: 3, max: 6},
              {max: 3}     // 不指定 min，表示 min 为无限大（-Infinity）。
      ], */
      min: 0,
      max: 15,
      splitNumber: 5,
      maxOpen: true,
      color: ["red", "yellow", "green"]
    },
    tooltip: {
      formatter: function (params, ticket, callback) {
        return `车流量:  ${params.value* 53} ` + `<br />预计5分种后车流量：${params.value* 47 + 38}`
      },
      trigger: "item"
    },
    series: [
      {
        type: "lines",
        coordinateSystem: "mars2dMap",
        polyline: true,
        data,
        lineStyle: {
          normal: {
            opacity: 1,
            width: 4
          },
          emphasis: {
            width: 6
          }
        },
        effect: {
          show: true,
          symbolSize: 2,
          color: "white"
        }
      }
    ]
  }

  // 创建Echarts图层
  echartsLayer = new mars2d.layer.EchartsLayer(options)
  map.addLayer(echartsLayer)
}

</script>

<style scoped>
.top {
  height: 10%;
  .selectors {
    text-align: center;
    padding-top: 30px;
    .selector {
      margin-right: 20px;
    }
  }
}
.map {
  height: 85%;
  width: 96%;
}
</style>