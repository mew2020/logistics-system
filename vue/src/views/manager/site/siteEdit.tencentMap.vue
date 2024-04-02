<!--此处修改申请表有点问题-->
<template>
  <!-- :rules="rules" -->
  <el-form :model="data.form" label-width="120px" ref="formRef">
    <el-form-item label="站点id" prop="id">
      <el-input style="width: 300px; " v-model="data.form.id" disabled />
    </el-form-item>
    <el-form-item label="站点名称" prop="name" required>
      <el-input style="width: 300px; " v-model="data.form.name" />
    </el-form-item>
    <el-form-item label="站点级别" prop="level" required>
      <el-select style="width: 300px;" v-model="data.form.level">
        <el-option label="总部" value="1"></el-option>
        <el-option label="省级" value="2"></el-option>
        <el-option label="市级/地级" value="3"></el-option>
        <el-option label="县级" value="4"></el-option>
        <el-option label="乡级" value="5"></el-option>
      </el-select>
    </el-form-item>
    <el-form-item label="位置" prop="location" required>
      <el-input style="width: 300px; " v-model="data.form.location" @change=""></el-input>
    </el-form-item>

    <el-form-item label="经纬度" required>
      <el-input prop="longitude" style="width: 140px; margin-right: 20px " v-model="data.form.longitude"
        disabled></el-input>
      <el-input prop="latitude" style="width: 140px; " v-model="data.form.latitude" disabled></el-input>
    </el-form-item>

    <div id="mapContainer" style="width: 600px; height:400px;margin-bottom: 10px" />

    <!-- <BMap height="50vh" width="50%" :enable-scroll-wheel-zoom="true" :center="location.addr" @click="onMapClick">
      <b-zoom></b-zoom>
      <b-scale></b-scale>
      <b-location></b-location>
      <b-marker :position="location.latlng"></b-marker>
    </BMap> -->

    <el-form-item label="负责人id" prop="managerId">
      <el-input style="width: 300px; " v-model="data.form.managerId" />
    </el-form-item>
    <el-form-item label="负责人姓名" prop="managerName">
      <el-input style="width: 300px; " v-model="data.form.managerName" />
    </el-form-item>



    <el-form-item>
      <el-button type="primary" @click="commitEdit">提交</el-button>
      <el-button @click="backToList">返回</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive, ref, onMounted } from 'vue'
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { routerKey, useRoute, useRouter } from "vue-router";
import { BMap } from 'vue3-baidu-map-gl';
import { Value } from "sass";
import { timePanelSharedProps } from 'element-plus/es/components/time-picker/src/props/shared';


//表单绑定ref，用于表单预校验,valid是true/flase
const formRef = ref()
// do not use same name with ref
const data = reactive({
  form: {
    longitude: 0,
    latitude: 0,
    id: '',
    name: '',
    location: '',
    level: 0,
    managerId: -1,
    managerName: ''
  },
  map:{},
  marker:[]
})

// //地图初始化位置
// var location = ref({
//   addr: '重庆城市科技学院巴南校区',
//   latlng: {
//     lng: 106.542669,
//     lat: 29.348462
//   }
// });
// var location = ref({lng: 106.542669, lat: 29.348462});
// const props = defineProps({
//   x: {
//     type: Number,
//     default: 106.542669
//   },
//   y: {
//     type: Number,
//     default: 29.348462
//   }
// })
// function handleInitd({ map }) {
//   import('BMap').then(() => {
//     const geoc = new BMap.Geocoder();
//     const pt = new BMap.Point(lng, lat);
//     geoc.getLocation(pt, (result) => {
//       if (result) {
//         data.form.location = result.address;
//         console.log(result)
//       }
//     });
//   })
// }
// const onMapClick = (event) => {
//   const { lng, lat } = event.latlng;
//   location.latlng = event.latlng;
//   data.form.latitude = lat;
//   data.form.longitude = lng;

//   // // 使用逆地理编码服务获取地址字符串
//   // const geoc = new BMap.Geocoder();
//   // const pt = new BMap.Point(lng, lat);
//   // geoc.getLocation(pt, (result) => {
//   //   if (result) {
//   //     data.form.location = result.address;
//   //   }
//   // });
// }


const backToList = () => {
  router.push({
    path: '/site'
  })
}

const route = useRoute()
const router = useRouter();
//打印传递过来的数据
data.form = route.query
console.log(data.form)

onMounted(() => {

  //init map
  var center = new TMap.LatLng(data.form.latitude || 29.341655, data.form.longitude || 106.535755)
  //定义map变量，调用 TMap.Map() 构造函数创建地图
  var map = new TMap.Map(document.getElementById('mapContainer'), {
    center: center,//设置地图中心点坐标
    zoom: 17.2,   //设置地图缩放级别
    pitch: 43.5,  //设置俯仰角
    rotation: 45,    //设置地图旋转角度
    viewMode: '2D',
  });

  //marker
  var markerLayer = new TMap.MultiMarker({
    map: map,  //指定地图容器
    //样式定义
    styles: {
      //创建一个styleId为"myStyle"的样式（styles的子属性名即为styleId）
      "myStyle": new TMap.MarkerStyle({
        "width": 25,  // 点标记样式宽度（像素）
        "height": 35, // 点标记样式高度（像素）
        // "src": '../img/marker.png',  //图片路径
        //焦点在图片中的像素位置，一般大头针类似形式的图片以针尖位置做为焦点，圆形点以圆心位置为焦点
        "anchor": { x: 16, y: 32 }
      })
    },
    //点标记数据数组
    geometries: [{
      "id": "1",   //点标记唯一标识，后续如果有删除、修改位置等操作，都需要此id
      "styleId": 'myStyle',  //指定样式id
      "position": center,  //点标记坐标位置
      "properties": {//自定义属性
        "title": "marker1"
      }
    },
    ]
  });

  data.map = reactive (map);
  data.marker = reactive (markerLayer);

  map.on("click", function (evt) {
    var lat = evt.latLng.getLat().toFixed(6);
    var lng = evt.latLng.getLng().toFixed(6);
    data.form.latitude = lat;
    data.form.longitude = lng;

    markerLayer.updateGeometries([
      {
        "styleId": "marker",
        "id": "1",
        "position": new TMap.LatLng(lat, lng),
      }
    ])
  })

  // map.on("click", clickHandler);
})

// //点击地图处理方法
// var clickHandler = function (evt) {
//   var lat = evt.latLng.getLat().toFixed(6);
//   var lng = evt.latLng.getLng().toFixed(6);
//   data.form.latitude = lat;
//   data.form.longitude = lng;

//   data.marker.updateGeometries([
//     {
//       "styleId": "marker",
//       "id": "1",
//       "position": new TMap.LatLng(lat, lng),
//     }
//   ])
// }


const commitEdit = () => {
  if (data.form.name == '' || data.form.name == null) {
    ElMessage.error("请输入站点名称！");
    return;
  }
  else if (data.form.level == '' || data.form.level == null) {
    ElMessage.error("请选择站点级别！");
    return;
  }
  else if (data.form.location == '' || data.form.location == null) {
    ElMessage.error("请输入站点位置！");
    return;
  }
  else if (data.form.longitude == '' || data.form.longitude == null || data.form.latitude == '' || data.form.latitude == null) {
    ElMessage.error("请在地图上选点，提交经纬度！");
    return;
  }
  console.log(data.form)
  if (data.form.id == null || data.form.id == '') {
    //id为null，说明没有传值，此时一定为新增
    request.post('/site', {
      id: null,
      name: data.form.name,
      location: data.form.location,
      level: data.form.level,
      longitude: data.form.longitude,
      latitude: data.form.latitude,
      managerId: data.form.managerId,
      managerName: data.form.managerName
    }).then(res => {
      if (res.data == "success") {
        ElMessage.success("新增成功")
        router.push({
          path: '/site',
        })
      } else {
        ElMessage.error("新增失败")
        router.push({
          path: '/site',
        })
      }
    })
  } else {
    //id不为null，此时说明有传值，则为修改
    request.put('/site', {
      id: data.form.id,
      name: data.form.name,
      location: data.form.location,
      level: data.form.level,
      longitude: data.form.longitude,
      latitude: data.form.latitude,
      managerId: data.form.managerId,
      managerName: data.form.managerName
    }).then(res => {
      if (res.code === '200') {
        ElMessage.success('修改成功！')
        router.push({
          path: '/site',
        })
      } else {
        ElMessage.error('修改失败！')
      }
    })
  }
}


// const rules = reactive({
//   name: [
//     { required: true, message: '请输入站点名称', trigger: 'blur' },
//   ],
//   level: [
//     { required: true, message: '请选择站点级别', trigger: 'blur' },
//   ],
//   location: [
//     { required: true, message: '请输入站点地址', trigger: 'blur' },
//   ],
//   longitude: [
//     { required: true, message: '请在地图上选点提交经纬度', trigger: 'blur' },
//   ],
//   latitude: [
//     { required: true, message: '请在地图上选点提交经纬度', trigger: 'blur' },
//   ],
// })
</script>
