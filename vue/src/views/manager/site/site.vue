<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入查询的站点名称" v-model="data.name"
        :prefix-icon="Search"></el-input>
      <el-select style="width: 200px; margin-right: 10px;" placeholder="请选择查询的站点级别" v-model="data.level">
        <el-option label="总部" value="1"></el-option>
        <el-option label="省级" value="2"></el-option>
        <el-option label="市级/地级" value="3"></el-option>
        <el-option label="县级" value="4"></el-option>
        <el-option label="乡级" value="5"></el-option>
      </el-select>
      <!--      查询事件记得写@click="load",不然不变,由于数没传进去-->
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <div style="margin-bottom: 10px;margin-left: 5px">
        <el-button type="primary" @click="addSite">新增</el-button>
      </div>
      <!--      固定表头表单-->
      <div>
        <!--        此处内容对应的script中的data里tableDate内容    ctrl+D复制上行-->
        <el-table :data="data.tableData" height="395" style="width: 100%" stripe>
          <el-table-column prop="id" label="站点id" width="150" />
          <el-table-column prop="name" label="站点名称" width="250" />
          <el-table-column prop="level" label="站点级别" width="200">
            <template #default="{ row }">
              <span v-if="row.level == '0'">未启用</span>
              <span v-if="row.level == '1'">总部</span>
              <span v-if="row.level == '2'">省级</span>
              <span v-if="row.level == '3'">市级/地级</span>
              <span v-if="row.level == '4'">县级</span>
              <span v-if="row.level == '5'">乡级</span>
            </template>
          </el-table-column>
          <el-table-column prop="location" label="站点地址" width="300" />
          <el-table-column prop="managerId" label="站点负责人id" width="180" />
          <el-table-column prop="managerName" label="站点负责人" width="180" />
          <el-table-column fixed="right" align="center" label="操作" width="160">
            <template #default="scope">
              <!--              此处传递当前行-->
              <!-- @click="handleEdit(scope.row)" -->
              <!-- @click="dialogVisible=true" -->
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="del(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <div>
      </div>

    </div>
  </div>
</template>
<script setup>
import { reactive, ref } from "vue";
//此处对应的这个(搜索图标),详见express网站 :prefix-icon="Search
import { Search } from "@element-plus/icons-vue";
//选第二个
import request from "@/utils/request";
import { ElMessage, ElMessageBox } from "element-plus";
import { useRouter } from "vue-router";

// const dialogVisible = ref(false)

const data = reactive({
  name: '',
  level: '',
  tableData: [
    //{id: '',name: '', location: '',level: '',longtitude: '',latitude: '',manager_id: '',manager_name: ''};
  ],
  //对话框表单
  dialogForm: {
    id: '',
  },
});

// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/site', {
    params: {
      name: data.name,
      level: data.level,
    }
  }).then(res => {
    console.log(res)
    console.log(res.data)
    data.tableData = res.data
  })
}
//调用方法,获取后台数据
load()
const reset = () => {
  data.name = ''
  data.level = ''
  load()
}
//新增页面的跳转
const addSite = () => {
  location.href = '/siteEdit'
}

// 传参，使得修改时显示数据
const router = useRouter()
const handleEdit = (row) => {
  router.push({
    path: '/siteEdit',
    query: row,
    // 多个参数这样的写法
    // query:{Shuju}
  })
}



const del = (id) => {
  ElMessageBox.confirm(
    '删除后数据无法恢复，是否确认删除？',
    '删除确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.delete('/site/' + id).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("删除成功")
        } else {
          ElMessage.error("删除失败")
        }
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消删除',
      })
    })
}

</script>