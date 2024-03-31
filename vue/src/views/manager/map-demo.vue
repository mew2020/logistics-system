<template>
  <el-input v-model="pos.lng"></el-input>
  <el-input v-model="pos.lat"></el-input>
  <b-map height="100vh" :enable-scroll-wheel-zoom="true" :center="pos">
        <b-zoom></b-zoom>
        <b-scale></b-scale>
        <b-location anchor="BMAP_ANCHOR_TOP_RIGHT"></b-location>
        <b-marker :position="pos"></b-marker>
  </b-map>
</template>
  
<!-- <script type="text/javascript">
    // GL版命名空间为BMapGL
    // 按住鼠标右键，修改倾斜角和角度
	var map = new BMapGL.Map("allmap");    // 创建Map实例
	map.centerAndZoom(new BMapGL.Point(116.404, 39.915), 11);  // 初始化地图,设置中心点坐标和地图级别
	map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放
  center="重庆城市科技学院巴南校区"
  :center="{lng: 106.542669, lat: 29.348462}"
</script> -->



<script setup>
  import {ref} from "vue";
  var pos = ref({lng: 106.542669, lat: 29.348462});
</script>


<style>
/* 这里可以添加样式 */
</style>
  