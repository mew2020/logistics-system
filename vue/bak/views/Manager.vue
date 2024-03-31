<template>
  <div>
    <div style="height: 60px; background-color: #fff; display: flex; align-items: center; border-bottom: 1px solid #ddd">
      <div style="flex: 1">
        <div style="padding-left: 20px; display: flex; align-items: center">
          <img src="@/assets/imgs/logo.png" alt="" style="width: 40px">
          <div style="font-weight: bold; font-size: 24px; margin-left: 5px">奖学金管理系统</div>
        </div>
      </div>
      <div style="width: fit-content; padding-right: 10px; display: flex; align-items: center;" >
        <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="" style="width: 40px; height: 40px">
        <span style="margin-left: 5px" v-if="data.user!=6">学生工作领导小组</span>
        <span style="margin-left: 5px" v-if="data.user==6">学生处</span>
      </div>
    </div>

    <div style="display: flex">
      <div style="width: 200px; border-right: 1px solid #ddd; min-height: calc(100vh - 60px)">
        <el-menu
            router
            style="border: none"
            :default-active="$route.path"
            :default-openeds="['/home', '2']"
        >
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <span>系统首页</span>
          </el-menu-item>

<!--          v-if="user===6"  是几显示谁-->

    <!--            此处图标之后记得换,还有路由名-->
          <el-sub-menu index="2" v-if="data.user!=6">
            <template #title>
              <el-icon><Memo /></el-icon>
              <span>奖学金信息管理</span>
            </template>
            <el-menu-item index="/jxx">
              <el-icon><Document /></el-icon>
              <span>奖学金信息</span>
            </el-menu-item>
            <el-menu-item index="/shenhe">
              <el-icon><Stamp /></el-icon>
              <span>奖学金初审</span>
            </el-menu-item>
          </el-sub-menu>
<!--          至此,上面是一个小框-->
          <el-menu-item index="/chengji" v-if="data.user!=6">
            <el-icon><User /></el-icon>
            <span>学生成绩</span>
          </el-menu-item >
          <el-menu-item index="/gongshi" v-if="data.user!=6">
            <el-icon><DataBoard /></el-icon>
            <span>举报信息</span>
          </el-menu-item>

          <el-sub-menu index="3" v-if="data.user!=6">
            <template #title>
              <el-icon><MoreFilled /></el-icon>
              <span>其他</span>
            </template>
            <el-menu-item index="/video">
              <el-icon><PictureFilled /></el-icon>
              <span>轮播图信息</span>
            </el-menu-item>
            <el-menu-item index="/wenz">
              <el-icon><List /></el-icon>
              <span>文章信息</span>
            </el-menu-item>
          </el-sub-menu>




<!--          此处开始是学生处的侧边菜单栏-->
            <el-menu-item index="/fushen" v-if="data.user==6">
              <el-icon><Stamp /></el-icon>
              <span>奖学金复审</span>
            </el-menu-item>
          <!--          至此,上面是一个小框-->
          <el-menu-item index="/jubao" v-if="data.user==6">
            <el-icon><DataBoard /></el-icon>
            <span>举报信息</span>
          </el-menu-item>
          <el-menu-item index="/lishi" v-if="data.user==6">
            <el-icon><DataAnalysis /></el-icon>
            <span>发放历史</span>
          </el-menu-item >



          <el-menu-item index="login" @click="logout">
            <el-icon><SwitchButton /></el-icon>
            <span>退出系统</span>
          </el-menu-item>
        </el-menu>
      </div>

      <div style="flex: 1; width: 0; background-color: #f8f8ff; padding: 10px">
        <router-view />
      </div>
    </div>

  </div>
</template>

<script setup>
//引入request对象
import request from "@/utils/request";
import {Value} from "sass";
import {ElMessage} from "element-plus";


import { useRoute } from 'vue-router'
import {reactive} from "vue";
const $route = useRoute()
console.log($route.path)

const data = reactive({
  user:localStorage.getItem('student-user')
})
console.log(data.user)

const load = () => {
  //此处是将字符串分割，返回角色信息
  var temp;
  for(let i=0;i<data.user.length-1;i++) {
    temp = (data.user||"").split('"')
    // 这个东西在网页的控制台显示
    // console.log(temp[12][1])
  }
  // 此处是获取相应角色信息
  // console.log(temp)
  data.user=temp[12][1]
  console.log(data.user)
}
load()

const logout = () => {
  localStorage.removeItem('student-user')
  location.href='/login'   //跳转到主页
}
</script>

<style scoped>
.el-menu-item.is-active {
  background-color: #dcede9 !important;
}
.el-menu-item:hover {
  color: #11A983;
}
:deep(th)  {
  color: #333;
}
</style>