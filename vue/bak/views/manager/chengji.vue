<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入学生姓名查询" v-model="data.name"
                :prefix-icon="Search"></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <div style="margin-bottom: 10px;margin-left: 5px">
        <el-button style="margin-left: 5px" type="primary" @click="save">新增</el-button>
      </div>
      <!--      固定表头表单-->
      <div>
        <!--        此处内容对应的script中的data里tableDate内容    ctrl+D复制上行-->
        <el-table :data="data.tableData" border height="395" style="width: 100%;margin-left: 10px">
          <el-table-column prop="name" label="学生姓名" width="180"/>
          <el-table-column prop="ban" label="所属学院" width="180"/>
          <el-table-column prop="xuehao" label="学生学号" width="180"/>
          <el-table-column prop="chengji" label="学生成绩" width="180"/>
        </el-table>
      </div>
    </div>
  </div>
</template>
<script setup>
import {reactive} from "vue";
//此处对应的这个(搜索图标),详见express网站 :prefix-icon="Search
import {Search} from "@element-plus/icons-vue";
//选第二个
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRouter} from "vue-router";

const data = reactive({
  name: '',
  chengji: '',
  ban:localStorage.getItem('student-user'),
  tableData: [
    //{id:'2023-3-4',name:'aaa',leixing:'fgdgs',jine:'aaa',time:'aaa',biao:'aaa',date:'aaa',tiaojian:'aaa'};
  ],
  //默认不展示弹窗
  formVisible: false,
  form: {},
})
const ban = () => {
  //此处是将字符串分割，返回角色信息
  var temp;
  for(let i=0;i<data.ban.length;i++) {
    temp = (data.ban||"").split('"')
    // 这个东西在网页的控制台显示
    // console.log(temp[12][1])
  }
  // console.log(temp)
  // 此处是获取管理员学院信息，显示学生按学院显示给对应学院管理员
  data.ban=temp[11]
  // console.log(data.xueyuan)
}
ban()
// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/Chengji/selectPage', {
    params: {
      name: data.name,
      ban:data.ban
    }
  }).then(res => {
    data.tableData = res.data?.list || []
  })
}
//调用方法,获取后台数据
load()
const reset = () => {
  data.name = ''
  load()
}
// 记得到时候加个跳转页面
const save = () => {
  request.post('/Chengji/add', data.form).then(res => {
    if (res.code === '200') {
      console.log(data.form)
      ElMessage.success("操作成功")
    } else {
      ElMessage.error(res.msg)
    }
  })
}
</script>
