<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入姓名/电话/订单号" v-model="data.condition"
        :prefix-icon="Search"></el-input>
      <el-select style="width: 200px; margin-right: 10px;" placeholder="请选择查询的订单状态" v-model="data.status">
        <!-- (limit1 track.status == 1 && track.siteId) || (limit1 track.status == 6  toSiteId) -->
        <!-- list所有order => 根据status过滤 => (status == 1 && track.status == 1 && track.siteId) || (status == 6 && track.status == 6 orderByCreateTime && limit1  track.toSiteId) -->
        <el-option label="待入库" value="1"></el-option>
        <el-option label="已入库" value="2"></el-option>
        <el-option label="已出库" value="3"></el-option>
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
          <el-table-column prop="id" label="订单id" width="150" />
          <el-table-column prop="userId" label="关联用户id" width="150" />
          <el-table-column prop="senderAddress" label="寄件人地址" width="180" />
          <el-table-column prop="receiverAddress" label="收件人地址" width="180" />
          <el-table-column prop="status" label="订单状态" width="120">
            <template #default="{ row }">
              <span v-if="row.status == '-1'">已取消</span>
              <span v-if="row.status == '0'">已下单</span>
              <span v-if="row.status == '1'">待上门</span>
              <span v-if="row.status == '2'">已取件</span>
              <span v-if="row.status == '4'">已付款</span>
              <span v-if="row.status == '5'">已入库</span>
              <span v-if="row.status == '6'">运输中</span>
              <span v-if="row.status == '7'">待派送</span>
              <span v-if="row.status == '8'">派送中</span>
              <span v-if="row.status == '9'">已完成</span>
            </template>
          </el-table-column>
          <el-table-column prop="createTime" label="下单时间" width="180" />
          <el-table-column fixed="right" align="left" label="操作" width="300">
            <template #default="scope">
              <!--              此处传递当前行-->
              <el-button type="info" size="small" @click="gotoDetail(scope.row)">详情</el-button>
              <el-button type="danger" size="small" v-if="scope.row.status >= 0 && scope.row.status < 5"
                @click="cancel(scope.row.id)">取消订单</el-button>
              <el-button type="primary" size="small" v-if="scope.row.status == 4 && data.status == 1"
                @click="entry(scope.row.id)">入库</el-button>
              <el-button type="primary" size="small" v-if="scope.row.status == 6 && data.status == 1"
                @click="entryDialogVisible = true;entryForm.id = scope.row.id">入库</el-button>
              <el-button type="primary" size="small" v-if="(scope.row.status == 5 || scope.row.status == 6) && data.status == 2"
                @click="outDialogVisible = true;outForm.id = scope.row.id">出库</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>


  <el-dialog v-model="entryDialogVisible" title="入库" width="500">
    <el-form :model="entryForm">
      <el-form-item label="订单id:" :label-width="formLabelWidth">
        <span>{{ entryForm.id }}</span>
      </el-form-item>
      <el-form-item label="是否到达最终站点:" :label-width="formLabelWidth">
        <el-select v-model="entryForm.arriveFlag">
          <el-option key="否" label="否" value="0" />
          <el-option key="是" label="是" value="1" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="entryDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="entryWithFlag(entryForm)">
          submit
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="outDialogVisible" title="出库" width="500">
    <el-form :model="outForm">
      <el-form-item label="订单id:" :label-width="formLabelWidth">
        <span>{{ outForm.id }}</span>
      </el-form-item>
      <el-form-item label="目的站点id:" :label-width="formLabelWidth">
        <el-input type="number" v-model="outForm.toSiteId" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="outDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="outOfStock(outForm)">
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
  admin: {},
  tableData: [
    //{id: '',name: '', location: '',level: '',longtitude: '',latitude: '',manager_id: '',manager_name: ''};
  ],
  courierList: []
})

const entryDialogVisible = ref(false)
const entryForm = reactive({
  id: -1,
  arriveFlag: 0,
})

const outDialogVisible = ref(false)
const outForm = reactive({
  id: -1,
  siteId: -1
})


onBeforeMount(() => {
  data.condition = ''
  data.status = '待入库'

  var admin = JSON.parse(localStorage.getItem('admin'))
  data.admin = admin
  loadPreEntry()
})

const entry = (orderId) => {
  ElMessageBox.confirm(
    '是否确认入库？',
    '入库确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/order/entry', {
        orderId: orderId,
        adminId: data.admin.id
      }).then(res => {
        console.log(res)
        load()

        entryDialogVisible.value = false;
        ElMessage.success("入库成功！")
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消入库',
      })
    })
}

const entryWithFlag = (form) => {
  ElMessageBox.confirm(
    '是否确认入库？',
    '入库确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/order/entry', {
        orderId: form.id,
        adminId: data.admin.id,
        arriveFlag: parseInt(form.arriveFlag)
      }).then(res => {
        console.log(res)
        load()

        entryDialogVisible.value = false;
        ElMessage.success("入库成功！")
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消入库',
      })
    })
}

const outOfStock = (form) => {
  ElMessageBox.confirm(
    '是否确认出库？',
    '出库确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/order/out', {
        orderId: form.id,
        adminId: data.admin.id,
        toSiteId: form.toSiteId
      }).then(res => {
        if(res.code == '200') {
          load()
        
          outDialogVisible.value = false;
          ElMessage.success("出库成功！")
        } else {
          ElMessage.error("出库失败！(" + res.msg + ")")
        }
        
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消出库',
      })
    })
}

const load = () => {
  if (data.status == 1 || data.status == '待入库') {
    loadPreEntry();
  } else if (data.status == 2) {
    loadEntry();
  } else {
    loadOut();
  }
}

const loadOut = () => {
  request.get('/admin/order/list/out/' + data.admin.siteId, {
    params: {
      condition: data.condition,
    }
  }).then(res => {
    data.tableData = res.data
  })
}

const loadEntry = () => {
  request.get('/admin/order/list/entry/' + data.admin.siteId, {
    params: {
      condition: data.condition,
    }
  }).then(res => {
    data.tableData = res.data
  })
}

const loadPreEntry = () => {
  request.get('/admin/order/list/preentry/' + data.admin.siteId, {
    params: {
      condition: data.condition,
    }
  }).then(res => {
    data.tableData = res.data
  })
}

const reset = () => {
  data.condition = ''
  data.status = '待入库'
  loadPreEntry()
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