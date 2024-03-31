<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入文章标题查询" v-model="data.biaoti"
                :prefix-icon="Search"></el-input>
      <!--      查询事件记得写@click="load",不然不变,由于数没传进去-->
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <div style="margin-bottom: 10px;margin-left: 5px">
        <el-button type="primary" @click="tiaojia">新增</el-button>
      </div>
      <!--      固定表头表单-->
      <div>
        <!--        此处内容对应的script中的data里tableDate内容    ctrl+D复制上行-->
        <el-table :data="data.tableData" height="395" style="width: 100%">
          <el-table-column prop="biaoti" label="文章标题" width="180"/>
          <el-table-column prop="miaoshu" label="文章描述" width="180"/>
          <el-table-column prop="neirong" label="文章内容" width="180"/>
          <el-table-column prop="tupian" label="图片" width="180">
<!--            此处是定义图片预览-->
            <template #default="scope">
              <el-image v-if="scope.row.tupian" :src="scope.row.tupian" :preview-src-list="[scope.row.tupian]" style="width:40px;heigh:40px;border-radius: 5px"></el-image>
            </template>
          </el-table-column>
          <el-table-column prop="time" label="发布时间" width="180"/>
          <el-table-column fixed="right" align="center" label="操作" width="160">
            <template #default="scope">
              <!--              此处传递当前行-->
              <el-button type="primary" size="small" @click="handleEdit(scope.row)">编辑</el-button>
              <el-button type="danger" size="small" @click="del(scope.row.id)">删除</el-button>
            </template>
          </el-table-column>
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
  biaoti: '',
  tableData: [
    //{id:'2023-3-4',name:'aaa',leixing:'fgdgs',jine:'aaa',time:'aaa',biao:'aaa',date:'aaa',tiaojian:'aaa'};
  ],
  //默认不展示弹窗
  formVisible: false,
  form: {},
})
// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/Wenz/selectPage', {
    params: {
      biaoti: data.biaoti,
    }
  }).then(res => {
    data.tableData = res.data?.list || []
    //此处是将字符串分割，只返回文件名
    for(let i=0;i<data.tableData.length;i++) {
      var jbiao = (data.tableData[i].biao||"").split('_')
      data.tableData[i].biao=jbiao[1]
    }
  })
}
//调用方法,获取后台数据
load()
const reset = () => {
  data.biaoti=''
  load()
}
//新增页面的跳转
const tiaojia = () => {
  location.href = '/xinzeng'
}

// 传参，使得修改时显示数据
const router = useRouter()
const handleEdit = (row) => {
  router.push({
    path: '/xinzeng',
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
        request.delete('/Wenz/delete/' + id).then(res => {
          if (res.code === '200') {
            load()  //刷新数据
            ElMessage.success("操作成功")
          } else {
            ElMessage.error(res.msg)
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
