<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入姓名/电话/订单号" v-model="data.condition"
        :prefix-icon="Search"></el-input>
      <el-select style="width: 200px; margin-right: 10px;" placeholder="请选择查询的订单状态" v-model="data.status">
        <el-option label="已下单" value="0"></el-option>
        <el-option label="待上门" value="1"></el-option>
        <el-option label="待派送" value="7"></el-option>
        <el-option label="派送中" value="8"></el-option>
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
          <el-table-column prop="pickerName" label="取件快递员" width="180" />
          <el-table-column prop="dispatcherName" label="派件快递员" width="180" />
          <el-table-column prop="status" label="订单状态" width="180">
            <template #default="{ row }">
              <span v-if="row.status == '-1'">已取消</span>
              <span v-if="row.status == '0'">已下单，未分配</span>
              <span v-if="row.status == '1'">待上门</span>
              <span v-if="row.status == '2'">已取件</span>
              <span v-if="row.status == '3'">待付款</span>
              <span v-if="row.status == '4'">已付款</span>
              <span v-if="row.status == '5'">已入库</span>
              <span v-if="row.status == '6'">运输中</span>
              <span v-if="row.status == '7'">待派送，未分配</span>
              <span v-if="row.status == '8'">派送中</span>
              <span v-if="row.status == '9'">已完成</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" width="180" />
          <el-table-column fixed="right" align="center" label="操作" width="300">
            <template #default="scope">
              <!--              此处传递当前行-->
              <el-button type="info" size="small" @click="gotoDetail(scope.row)">详情</el-button>
              <el-button type="primary" size="small" v-if="scope.row.status == 0 || scope.row.status == 7"
                @click="allocateForm.id = scope.row.id; allocateDialogVisible = true">分配快递员</el-button>
              <el-button type="warning" size="small" v-if="scope.row.status == 1 || scope.row.status == 8"
                @click="reallocateForm.id = scope.row.id; reallocateDialogVisible = true">更换快递员</el-button>
              <el-button type="danger" size="small" v-if="scope.row.status >= 0 && scope.row.status < 5"
                @click="cancel(scope.row.id)">取消订单</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>


  <el-dialog v-model="allocateDialogVisible" title="分配快递员" width="500">
    <el-form :model="allocateForm">
      <el-form-item label="订单id:" :label-width="formLabelWidth">
        <span>{{ allocateForm.id }}</span>
      </el-form-item>
      <el-form-item label="快递员:" :label-width="formLabelWidth">
        <el-select v-model="allocateForm.courierId">
          <el-option v-for="courier in data.courierList" :key="courier.id" :label="courier.name" :value="courier.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="allocateDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="allocateCourier(allocateForm)">
          submit
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="reallocateDialogVisible" title="更换快递员" width="500">
    <el-form :model="reallocateForm">
      <el-form-item label="订单id:" :label-width="formLabelWidth">
        <span>{{ reallocateForm.id }}</span>
      </el-form-item>
      <el-form-item label="快递员:" :label-width="formLabelWidth">
        <el-select v-model="reallocateForm.courierId">
          <el-option v-for="courier in data.courierList" :key="courier.id" :label="courier.name" :value="courier.id" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="reallocateDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="reallocateCourier(reallocateForm)">
          submit
        </el-button>
      </div>
    </template>
  </el-dialog>





</template>
<script setup>
import { reactive, ref, onBeforeMount } from "vue";
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
  courierList: []
})

const allocateDialogVisible = ref(false)
let allocateForm = reactive({
  id: -1,
  adminId: -1,
})
const reallocateDialogVisible = ref(false)
let reallocateForm = reactive({
  id: -1,
  adminId: -1,
})


onBeforeMount(() => {
  data.condition = ''
  data.status = '已下单'
  loadDefault()

  var admin = JSON.parse(localStorage.getItem('admin'))
  console.log(admin)
  allocateForm.adminId = admin.id
  reallocateForm.adminId = admin.id

  //获取快递员信息
  request.get('/admin/courier/list', {
    params: {
      siteId: admin.siteId
    }
  }).then(res => {
    data.courierList = res.data
  })
})

const allocateCourier = (form) => {
  var _this = this;
  console.log(form)
  ElMessageBox.confirm(
    '是否确认进行快递员分配？',
    '分配确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/courier/allocate', {
        orderId: parseInt(form.id),
        courierId: parseInt(form.courierId),
        adminId: parseInt(form.adminId)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          allocateDialogVisible.value = false
          allocateForm.courierId = null
          ElMessage.success("分配成功")
          reset();
        } else {
          ElMessage.error("分配失败")
        }
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消分配',
      })
    })
}

const reallocateCourier = (form) => {
  var _this = this;
  console.log(form)
  ElMessageBox.confirm(
    '是否确认进行快递员分配？',
    '分配确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/courier/reallocate', {
        orderId: parseInt(form.id),
        courierId: parseInt(form.courierId),
        adminId: parseInt(form.adminId)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据

          reallocateDialogVisible.value = false
          reallocateForm.courierId = null
          ElMessage.success("更换快递员成功")
        } else {
          ElMessage.error("更换快递员失败")
        }
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消更换快递员',
      })
    })
}

// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/admin/order/list', {
    params: {
      condition: data.condition,
      status: data.status,
    }
  }).then(res => {
    data.tableData = res.data
  })
}

const loadDefault = () => {
  request.get('/admin/order/list/default').then(res => {
    console.log(res.data)
    data.tableData = res.data
  })
}
//调用方法,获取后台数据

const reset = () => {
  data.condition = ''
  data.status = '已下单'
  loadDefault()
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