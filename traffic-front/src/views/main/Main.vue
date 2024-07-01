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
let pageSize = 100
let graphicLayer: any
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
const eventTarget = new mars2d.BaseClass()
let roadName = "";
let data
graphicLayer = new mars2d.layer.GraphicLayer({})
let map :mars2d.Map
let data1
onMounted(() => {
  // loadRoad();
  searchRoad("");
})
const loadRoad1 = async () => {
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
  data1 = res.data.list.map(item => {
    return {
      value: Math.floor(Math.random() * 20),
      coords: item.theGeom.map(coords => {
        return [coords[1], coords[0]];
      })
    };
  })
  let road = res.data.list.map(item => {
    return item.theGeom;
  })
  addRoad(road)
}

const loadRoad = async () => {
  map.removeLayer(graphicLayer);
  graphicLayer = new mars2d.layer.GraphicLayer({})
  let res = await proxy.Request({
    url: "/monitor/page",
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

  addMonitor(res.data.list);
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
  // 加一些演示数据
  map = loadedMap;
  loadRoad();
}

function refresh() {
  loadRoad();
}
function selectCounty(event) {
  areaId = event;
  pageSize = 100;
  if (event == "01") {
    pageSize = 100;
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
    pageSize = 1000;
  }
  loadRoad();
}
function selectRoad(event) {
  roadName = event;
  loadRoad1().then(() => {
    let center = data1[0].coords[0].reverse();
    console.log(center)
    map.setView(center, 13);
  })
  loadRoad();
}

function addMonitor(latlng) {
  for (let i = 0; i < latlng.length; i++) {
    const graphic = new mars2d.graphic.Marker({
      latlng: latlng[i].theGeom[0],
      style: {
        image: "/src/assets/monitor.png",
        width: 44,
        height: 44,
      }
    })
    graphicLayer.addGraphic(graphic)
  }
  initLayerManager()
  map.addLayer(graphicLayer)
}

function initLayerManager() {
  // 在layer上绑定监听事件
  graphicLayer.on(mars2d.EventType.click, function (event) {
    console.log("监听layer，单击了矢量对象", event)
  })

  // 可在图层上绑定popup,对所有加到这个图层的矢量数据都生效
  bindLayerPopup()

  eventTarget.fire("defuatData", {
    enabledShowHide: true,
    enabledPopup: true,
    enabledTooltip: false,
    enabledRightMenu: true
  })
}

function bindLayerPopup() {
  graphicLayer.bindPopup(function (event) {
    console.log(event)
    const attr = event?.attr || {}
    // attr["道路"] = event.type
    attr["道路"] = roadName == "" ? "镇常线" : roadName
    attr["车流量"] = Math.random().toFixed(2)*400 + 100
    attr["平均车速"] = (Math.random()*100 + 30).toFixed(2)
    attr["违章数量"] = Math.floor(Math.random() * 20) + 1

    return mars2d.Util.getTemplateHtml({ title: "卡口信息", template: "all", attr })
  })
}

function addRoad(road) {
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