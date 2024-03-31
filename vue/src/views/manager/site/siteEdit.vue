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
      <el-input style="width: 300px; " v-model="location.addr" @change="data.form.location = location.addr"></el-input>
    </el-form-item>

    <el-form-item label="经纬度" required>
      <el-input prop="longitude" style="width: 140px; margin-right: 20px " v-model="data.form.longitude" disabled></el-input>
      <el-input prop="latitude" style="width: 140px; " v-model="data.form.latitude" disabled></el-input>
    </el-form-item>

    <BMap height="50vh" width="50%" :enable-scroll-wheel-zoom="true" :center="location.addr" @click="onMapClick">
      <b-zoom></b-zoom>
      <b-scale></b-scale>
      <b-location></b-location>
      <b-marker :position="location.latlng"></b-marker>
    </BMap>

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
import { reactive, ref } from 'vue'
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
})

//地图初始化位置
var location = ref({
  addr: '重庆城市科技学院巴南校区',
  latlng: {
    lng: 106.542669,
    lat: 29.348462
  }
});
// var location = ref({lng: 106.542669, lat: 29.348462});
const props = defineProps({
  x: {
    type: Number,
    default: 106.542669
  },
  y: {
    type: Number,
    default: 29.348462
  }
})
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
const onMapClick = (event) => {
  const { lng, lat } = event.latlng;
  location.latlng = event.latlng;
  data.form.latitude = lat;
  data.form.longitude = lng;

  // // 使用逆地理编码服务获取地址字符串
  // const geoc = new BMap.Geocoder();
  // const pt = new BMap.Point(lng, lat);
  // geoc.getLocation(pt, (result) => {
  //   if (result) {
  //     data.form.location = result.address;
  //   }
  // });
}

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

const commitEdit = () => {
  if(data.form.name == '' || data.form.name == null) {
    ElMessage.error("请输入站点名称！");
    return;
  }
  else if(data.form.level == '' || data.form.level == null) {
    ElMessage.error("请选择站点级别！");
    return;
  }
  else if(data.form.location == '' || data.form.location == null) {
    ElMessage.error("请输入站点位置！");
    return;
  }
  else if(data.form.longitude == '' || data.form.longitude == null || data.form.latitude == '' || data.form.latitude == null) {
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
