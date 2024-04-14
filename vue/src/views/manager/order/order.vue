<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入姓名/电话/订单号" v-model="data.condition"
        :prefix-icon="Search"></el-input>
      <el-select style="width: 200px; margin-right: 10px;" placeholder="请选择查询的订单状态" v-model="data.status">
        <el-option label="已取消" value="-1"></el-option>
        <el-option label="已下单" value="0"></el-option>
        <el-option label="待上门" value="1"></el-option>
        <el-option label="已取件" value="2"></el-option>
        <el-option label="待付款" value="3"></el-option>
        <el-option label="已付款" value="4"></el-option>
        <el-option label="已入库" value="5"></el-option>
        <el-option label="运输中" value="6"></el-option>
        <el-option label="待派送" value="7"></el-option>
        <el-option label="派送中" value="8"></el-option>
        <el-option label="已完成" value="9"></el-option>
      </el-select>
      <!--      查询事件记得写@click="load",不然不变,由于数没传进去-->
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <!--      固定表头表单-->
      <div>
        <!--        此处内容对应的script中的data里tableDate内容    ctrl+D复制上行-->
        <el-table :data="data.tableData" height="395" style="width: 100%">
          <el-table-column prop="id" label="订单id" width="180" />
          <el-table-column prop="userId" label="用户id" width="180" />
          <el-table-column prop="senderName" label="寄件人" width="180" />
          <el-table-column prop="senderPhone" label="寄件人电话" width="180" />
          <el-table-column prop="receiverName" label="收件人" width="180" />
          <el-table-column prop="senderPhone" label="收件人电话" width="180" />
          <el-table-column prop="status" label="订单状态" width="180">
            <template #default="{ row }">
              <span v-if="row.status == '-1'">已取消</span>
              <span v-if="row.status == '0'">已下单</span>
              <span v-if="row.status == '1'">待上门</span>
              <span v-if="row.status == '2'">待付款</span>
              <span v-if="row.status == '4'">已付款</span>
              <span v-if="row.status == '5'">已入库</span>
              <span v-if="row.status == '6'">运输中</span>
              <span v-if="row.status == '7'">待派送</span>
              <span v-if="row.status == '8'">派送中</span>
              <span v-if="row.status == '9'">已完成</span>
            </template>
          </el-table-column>
          <el-table-column fixed="right" align="left" label="操作" width="160">
            <template #default="scope">
              <!--              此处传递当前行-->
              <el-button type="info" size="small" @click="gotoDetail(scope.row)">详情</el-button>
              <el-button type="danger" size="small" v-if="scope.row.status >= 0 && scope.row.status < 5" @click="cancel(scope.row.id)">取消订单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
      <!-- <div id="dmap"></div> -->
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

const data = reactive({
  name: '',
  level: '',
  tableData: [
    //{id: '',name: '', location: '',level: '',longtitude: '',latitude: '',manager_id: '',manager_name: ''};
  ],
  //默认不展示弹窗
  formVisible: false,
  form: {},
})
// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/admin/order/list', {
    params: {
      condition: data.condition,
      status: data.status,
    }
  }).then(res => {
    console.log(res.data)
    data.tableData = res.data
  })
}
//调用方法,获取后台数据
load()
const reset = () => {
  data.condition = ''
  data.status = ''
  load()
}

// 传参，使得修改时显示数据
const router = useRouter()
const gotoDetail = (row) => {
  router.push({
    path: '/orderDetail',
    query: {
      id: row.id
    }
  })
}
const cancel = (id) => {
  var admin = JSON.parse(localStorage.getItem('admin'))
  ElMessageBox.confirm(
    '取消后无法恢复，是否确认取消？',
    '取消确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/order/cancel', {
        adminId: parseInt(admin.id),
        orderId: id
      }).then(res => {
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
        message: '已取消操作',
      })
    })
}

</script>