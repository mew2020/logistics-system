<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入id/用户名/昵称/姓名/电话" v-model="data.condition"
        :prefix-icon="Search"></el-input>
      <el-select style="width: 200px; margin-right: 10px;" placeholder="请选择查询的用户类型" v-model="data.status">
        <el-option label="被停权" value="0"></el-option>
        <el-option label="普通用户" value="1"></el-option>
        <el-option label="快递员" value="2"></el-option>
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
          <el-table-column prop="id" label="用户id" width="180" />
          <el-table-column prop="username" label="用户名" width="180" />
          <el-table-column prop="status" label="用户类型" width="180">
            <template #default="{ row }">
              <span v-if="row.status == '0'">被停权</span>
              <span v-if="row.status == '1'">普通用户</span>
              <span v-if="row.status == '2'">快递员</span>
            </template>
          </el-table-column>
          <el-table-column prop="nickname" label="用户昵称" width="180" />
          <el-table-column prop="phone" label="手机号" width="180" />
          <el-table-column prop="balance" label="余额" width="180" />
          <el-table-column fixed="right" align="center" label="操作" width="280">
            <template #default="scope">
              <!--              此处传递当前行-->
              <el-button type="info" size="small"
                @click="detailForm = scope.row; detailDialogVisible = true">详情</el-button>
                <el-button type="primary" size="small"
                @click="balanceForm.id = scope.row.id; balanceDialogVisible = true">余额变更</el-button>
              <el-button type="primary" size="small" v-if="scope.row.status == 1 && scope.row.disabled == undefined"
                @click="registerForm.id = scope.row.id; registerDialogVisible = true;">快递员信息注册</el-button>
              <el-button type="danger" size="small"
                v-else-if="scope.row.disabled == false || scope.row.disabled == true"
                @click="disabledChange(scope.row.id)">快递员状态变更</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>


  <el-dialog v-model="detailDialogVisible" title="用户详情" width="500">
    <el-form :model="detailForm">
      <el-form-item label="用户id:" :label-width="formLabelWidth">
        <span>{{ detailForm.id }}</span>
      </el-form-item>
      <el-form-item label="用户名:" :label-width="formLabelWidth">
        <span>{{ detailForm.username }}</span>
      </el-form-item>
      <el-form-item label="用户类型:" :label-width="formLabelWidth">
        <span v-if="detailForm.status == 0">被停权</span>
        <span v-if="detailForm.status == 1">普通用户</span>
        <span v-if="detailForm.status == 2">快递员</span>
      </el-form-item>
      <el-form-item label="用户昵称:" :label-width="formLabelWidth">
        <span>{{ detailForm.nickname }}</span>
      </el-form-item>
      <el-form-item label="手机号:" :label-width="formLabelWidth">
        <span>{{ detailForm.phone }}</span>
      </el-form-item>
      <el-form-item label="余额:" :label-width="formLabelWidth">
        <span>{{ detailForm.balance }}</span>
      </el-form-item>

      <hr v-if="detailForm.name != null" />
      <el-form-item label="姓名:" :label-width="formLabelWidth" v-if="detailForm.name != null">
        <span>{{ detailForm.name }}</span>
      </el-form-item>
      <el-form-item label="快递员状态:" :label-width="formLabelWidth" v-if="detailForm.name != null">
        <span v-if="detailForm.disabled == 1">被停权</span>
        <span v-if="detailForm.disabled == 0">启用中</span>
      </el-form-item>
      <el-form-item label="站点id:" :label-width="formLabelWidth" v-if="detailForm.name != null">
        <span>{{ detailForm.siteId }}</span>
      </el-form-item>
      <el-form-item label="站点名称:" :label-width="formLabelWidth" v-if="detailForm.name != null">
        <span>{{ detailForm.siteName }}</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <!-- <el-button @click="detailDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="detailDialogVisible = false">
          Confirm
        </el-button> -->
        <el-button type="danger" size="medium" @click="statusChange(detailForm.id)">用户账号状态变更</el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="registerDialogVisible" title="快递员信息注册" width="500">
    <el-form :model="registerForm">
      <el-form-item label="用户id:" :label-width="formLabelWidth">
        <span>{{ registerForm.id }}</span>
      </el-form-item>
      <el-form-item label="姓名:" :label-width="formLabelWidth">
        <el-input v-model="registerForm.name" placeholder="请输入快递员姓名" autocomplete="off" />
      </el-form-item>
      <el-form-item label="快递员状态:" :label-width="formLabelWidth">
        <el-select v-model="registerForm.disabled" placeholder="请选择状态">
          <el-option label="停权" value="1" selected />
          <el-option label="启用" value="0" />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="registerDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="courierRegister()">
          submit
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="balanceDialogVisible" title="余额变更" width="500">
    <el-form :model="balanceForm">
      <el-form-item label="用户id:" :label-width="formLabelWidth">
        <span>{{ balanceForm.id }}</span>
      </el-form-item>
      <el-form-item label="变更状态:" :label-width="formLabelWidth" required>
        <el-select v-model="balanceForm.status" placeholder="请选择变更状态">
          <el-option label="增加" value="1" selected  />
          <el-option label="减少" value="-1" />
        </el-select>
      </el-form-item>
      <el-form-item label="变更金额:" :label-width="formLabelWidth" required>
        <el-input v-model="balanceForm.amount" placeholder="请输入变更金额" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="balanceDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="balanceChange(balanceForm)">
          submit
        </el-button>
      </div>
    </template>
  </el-dialog>

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
  tableData: [
    //{id: '',username: '', status: '',nickname: '',phone: '',balance: '', | name:'', disabled: '', siteId: '', siteName: ''};
  ],
})
// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/admin/user/list', {
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

const formLabelWidth = '200px'
const detailDialogVisible = ref(false)
const detailForm = reactive({
  id: -1,
  username: '',
  status: 0,
  nickname: '',
  phone: '',
  balance: -1,
  name: '',
  disabled: 0,
  siteId: -1,
  siteName: ''
})

const registerDialogVisible = ref(false)
const registerForm = reactive({
  id: -1,
  amount: ''
})

const balanceDialogVisible = ref(false)
var balanceForm = reactive({
  status:'',
  amount: '',
})


// 传参，使得修改时显示数据
const router = useRouter()
// const handleEdit = (row) => {
//   router.push({
//     path: '/tianjia',
//     query: row,
//     // 多个参数这样的写法
//     // query:{Shuju}
//   })
// }

const statusChange = (id) => {
  ElMessageBox.confirm(
    '是否确认对其账号状态进行变更？',
    '状态变更确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/user/status/' + id).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("变更账号状态成功")
        } else {
          ElMessage.error(res.msg)
        }

        detailDialogVisible.value = false
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消账号状态变更',
      })
    })
}

const disabledChange = (id) => {
  var admin = JSON.parse(localStorage.getItem('admin'))
  ElMessageBox.confirm(
    '是否确认对该快递员状态进行变更？',
    '状态变更确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/courier/disabled', {
        id: id,
        siteId: parseInt(admin.siteId)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("变更快递员状态成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
      
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消快递员状态变更',
      })
    })
}

const courierRegister = () => {
  var admin = JSON.parse(localStorage.getItem('admin'))
  ElMessageBox.confirm(
    '是否确认提交？',
    '提交确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.post('/admin/courier/register', {
        id: registerForm.id,
        name: registerForm.name,
        disabled: parseInt(registerForm.disabled),
        siteId: parseInt(admin.siteId)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("注册成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
    })
    .catch(() => {
    })
}

const balanceChange = (form) => {
  var _this = this;
  ElMessageBox.confirm(
    '是否确认金额变更？',
    '提交确认',
    {
      type: 'warning',
    },
  )
    .then(() => {
      request.put('/admin/user/balance', {
        id: form.id,
        status: parseInt(form.status),
        amount: parseInt(form.amount)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("变更余额成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
    })
    .catch(() => {
      console.log("catch...")
    })
}

</script>