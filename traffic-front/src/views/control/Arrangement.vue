<script setup lang="ts">
import {ref, reactive, getCurrentInstance, nextTick, computed, onMounted} from "vue";
import { useRouter, useRoute } from "vue-router";
import Mars2dMap from "../../components/mars2d-map.vue"
import * as mars2d from "mars2d"

const L = mars2d.L
const configUrl = "../config/config.json"
const { proxy } = getCurrentInstance();
let graphicLayer = new mars2d.layer.GraphicLayer({})
let echartsLayer
let map: mars2d.Map
const options1 = [
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
const value = ref('')
let star1 = Math.floor(Math.random() * 500) + 63
let star2 = Math.floor(Math.random() * 500) + 63
let star3 = Math.floor(Math.random() * 500) + 63
let star4 = Math.floor(Math.random() * 500) + 63
let star5 = Math.floor(Math.random() * 500) + 63
let star6 = Math.floor(Math.random() * 500) + 63
let data = [
  { name: "武进区", value: Math.floor(Math.random() * 500) + 63, star: star1, location: [119.942, 31.700] },
  { name: "新北区", value: Math.floor(Math.random() * 500) + 63, star: star2, location: [119.955, 31.864] },
  { name: "钟楼区", value: Math.floor(Math.random() * 500) + 63, star: star3, location: [119.901, 31.802] },
  { name: "天宁区", value: Math.floor(Math.random() * 500) + 63, star: star4, location: [119.994, 31.793] },
  { name: "金坛区", value: Math.floor(Math.random() * 500) + 63, star: star5, location: [119.582, 31.723] },
  { name: "溧阳市", value: Math.floor(Math.random() * 500) + 63, star: star6, location: [119.483, 31.415] },
]
onMounted(() => {

})
const marsOnload = (loadedMap: mars2d.Map) => {
  map = loadedMap;
  showEchartsLayer()
}

function updateColor(color) {
  echartsLayer.setEchartsOption({
    series: [
      {
        itemStyle: { normal: { color } }
      }
    ]
  })
}
let areaId
const loadRoad = async () => {
  map.removeLayer(echartsLayer);
  let res = await proxy.Request({
    url: "/monitor/page",
    showLoading: true,
    params: {
      areaId: areaId,
      pageSize: 10,
      pageNo: Math.floor(Math.random() * 20) + 3
    }
  });
  if (!res) {
    return;
  }

  data = res.data.list.map(item => ({
    name: item.name,
    value: Math.floor(Math.random() * 500) + 63,
    star: Math.floor(Math.random() * 500) + 63,
    location: item.theGeom[0].map(Number).reverse()
  }));
  console.log(data)
  showEchartsLayer()
}

function selectCounty(event) {
  areaId = event;
  if (event == "01") {
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
  }
  loadRoad();
}
function showEchartsLayer() {

  const brcolor = "#ff8b00"

  // 纬度做偏移处理,避免重叠
  if (data.length > 1) {
    data.sort(function (a, b) {
      return b.location[1] - a.location[1]
    })
    for (let i = 1; i < data.length; i++) {
      const thisItem = data[i].location

      let ispy = false
      for (let j = 0; j < i; j++) {
        const lastItem = data[j].location
        const offX = Math.abs(lastItem[0] - thisItem[0])
        const offY = Math.abs(lastItem[1] - thisItem[1])
        if (offX < 0.025 && offY < 0.005) {
          ispy = true
          break
        }
      }

      if (ispy) {
        thisItem[1] -= 0.006 // 偏移纬度
      }

      // console.log(data[i].name +','+thisItem.join(",")+','+ispy+','+offX+','+offY);
    }
  }

  let sum = 0
  const dataVals = []


  const dataLatlngs = []
  for (let i = 0; i < data.length; i++) {
    sum += data[i].value

    dataVals.push({
      name: data[i].name,
      star: data[i].star,
      value: data[i].location.concat(data[i].value)
    })

    dataLatlngs.push([data[i].location[1], data[i].location[0]])
  }
  if (dataLatlngs.length > 0) {
    map.stop()
    map.fitBounds(dataLatlngs)
  }

  const options = {
    animation: true,
    animationDuration: 1000,
    animationEasing: "cubicInOut",
    animationDurationUpdate: 1000,
    animationEasingUpdate: "cubicInOut",
    tooltip: {
      trigger: "item"
    },
    series: [
      {
        type: "effectScatter",
        coordinateSystem: "mars2dMap",
        data: dataVals,
        symbolSize: function (val) {
          if (sum === 0) {
            return 8
          }

          const num = (val[2] / sum) * 150
          return Math.max(num, 8)
        },
        showEffectOn: "render",
        rippleEffect: {
          brushType: "stroke"
        },
        hoverAnimation: true,
        label: {
          normal: {
            formatter: "{b}",
            position: "right",
            show: true
          }
        },
        tooltip: {
          formatter: function (params, ticket, callback) {
            if (params.value[2] <= 0) {
              return params.name
            } else {
              return params.name + "车辆数： " + params.value[2] + "<br />五分钟后预计：" + params.data.star
            }
          }
        },
        itemStyle: {
          normal: {
            color: brcolor,
            shadowBlur: 60,
            shadowColor: "#cccccc"
          }
        },
        zlevel: 1
      }
    ]
  }

  // 创建Echarts图层
  echartsLayer = new mars2d.layer.EchartsLayer(options)
  map.addLayer(echartsLayer)

  echartsLayer.on("click", function (event) {
    console.log("单击了图层", event)
  })
}
</script>

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

<style scoped lang="scss">
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
  height: 75%;
  width: 96%;
}
</style>