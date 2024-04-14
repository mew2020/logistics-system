<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入id/用户名/昵称/姓名/电话" v-model="data.condition"
        :prefix-icon="Search"></el-input>
      <el-select style="width: 200px; margin-right: 10px;" placeholder="请选择查询的用户类型" v-model="data.status">
        <el-option label="被停权" value="0"></el-option>
        <el-option label="启用中" value="1"></el-option>
      </el-select>
      <!--      查询事件记得写@click="load",不然不变,由于数没传进去-->
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <div style="margin-bottom: 10px;margin-left: 5px">
        <el-button type="primary" @click="registerDialogVisible = true">新增</el-button>
      </div>
      <!--      固定表头表单-->
      <div>
        <!--        此处内容对应的script中的data里tableDate内容    ctrl+D复制上行-->
        <el-table :data="data.tableData" height="395" style="width: 100%">
          <el-table-column prop="id" label="管理员id" width="180" />
          <el-table-column prop="username" label="用户名" width="180" />
          <el-table-column prop="name" label="姓名" width="180" />
          <el-table-column prop="status" label="账户状态" width="180">
            <template #default="{ row }">
              <span v-if="row.status == '0'">被停权</span>
              <span v-if="row.status == '1'">启用中</span>
            </template>
          </el-table-column>
          <el-table-column prop="phone" label="手机号" width="180" />
          <el-table-column fixed="right" align="center" label="操作" width="300">
            <template #default="scope">
              <!--              此处传递当前行-->
              <el-button type="primary" size="small" @click="updateForm = scope.row; updateDialogVisible = true;">编辑</el-button>
              <el-button type="danger" size="small" @click="statusChange(scope.row.id)">状态变更</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </div>

  <el-dialog v-model="registerDialogVisible" title="新增员工(管理员)" width="500">
    <el-form :model="registerForm">
      <el-form-item label="姓名:" :label-width="formLabelWidth" required>
        <el-input v-model="registerForm.name" placeholder="请输入管理员姓名" autocomplete="off" />
      </el-form-item>
      <el-form-item label="用户名:" :label-width="formLabelWidth" required>
        <el-input v-model="registerForm.username" placeholder="请输入username" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码:" :label-width="formLabelWidth" required>
        <el-input v-model="registerForm.password" placeholder="请输入password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="手机号:" :label-width="formLabelWidth" required>
        <el-input v-model="registerForm.phone" placeholder="请输入手机号" autocomplete="off" />
      </el-form-item>
      <el-form-item label="账户状态:" :label-width="formLabelWidth" required>
        <el-select v-model="registerForm.status" placeholder="请选择状态">
          <el-option label="停权" value="1" selected />
          <el-option label="启用" value="0" />
        </el-select>
      </el-form-item>
      <el-form-item label="站点id:" :label-width="formLabelWidth" required>
        <el-input v-model="registerForm.siteId" placeholder="请输入对应站点的id" autocomplete="off" />
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="registerDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="adminRegister()">
          submit
        </el-button>
      </div>
    </template>
  </el-dialog>

  <el-dialog v-model="updateDialogVisible" title="修改员工(管理员)" width="500">
    <el-form :model="updateForm">
      <el-form-item label="id:" :label-width="formLabelWidth">
        <span>{{updateForm.id}}</span>
      </el-form-item>
      <el-form-item label="姓名:" :label-width="formLabelWidth">
        <el-input v-model="updateForm.name" placeholder="请输入管理员姓名" autocomplete="off" />
      </el-form-item>
      <el-form-item label="用户名:" :label-width="formLabelWidth" required>
        <el-input v-model="updateForm.username" placeholder="请输入username" autocomplete="off" />
      </el-form-item>
      <el-form-item label="密码:" :label-width="formLabelWidth">
        <el-input v-model="updateForm.password" placeholder="请输入password" autocomplete="off" />
      </el-form-item>
      <el-form-item label="手机号:" :label-width="formLabelWidth" required>
        <el-input v-model="updateForm.phone" placeholder="请输入手机号" autocomplete="off" />
      </el-form-item>
      <el-form-item label="站点id:" :label-width="formLabelWidth" required>
        <el-input v-model="updateForm.siteId" placeholder="请输入对应站点的id" autocomplete="off" />
      </el-form-item>
      <el-form-item label="站点名:" :label-width="formLabelWidth">
        <span>{{updateForm.siteName}}</span>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="updateDialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="adminUpdate(updateForm)">
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
  condition: '',
  status: '',
  tableData: [
    //{id: '',username: '', status: '',nickname: '',phone: '',balance: '', | name:'', disabled: '', siteId: '', siteName: ''};
  ],
})
// 定义方法,连接后台路由,获得相应数据,具体数据内容可以在postman里看
//name:data.name是将前台的查询数据传入,让后台接收
const load = () => {
  request.get('/admin/list', {
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

const formLabelWidth = '100px'
// const detailDialogVisible = ref(false)
// const detailForm = reactive({
//   id: -1,
//   username: '',
//   status: 0,
//   nickname: '',
//   phone: '',
//   balance: -1,
//   name:'',
//   disabled: 0,
//   siteId: -1,
//   siteName: ''
// })
const registerDialogVisible = ref(false)
const registerForm = reactive({
  name: '',
  username: '',
  password: '',
})
const updateDialogVisible = ref(false)
const updateForm = reactive({
  name: '',
  username: '',
  password: '',
})

const statusChange = (id) => {
  ElMessageBox.confirm(
    '是否确认对其账号状态进行变更？',
    '状态变更确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/status/' + id).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("变更账号状态成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消账号状态变更',
      })
    })
}

const adminRegister = () => {
  ElMessageBox.confirm(
    '是否确认新增该管理员？',
    '新增管理员确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.post('/admin/add', {
        name: registerForm.name,
        status: parseInt(registerForm.status),
        username: registerForm.username,
        password: registerForm.password,
        phone: registerForm.phone,
        siteId: parseInt(registerForm.siteId)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          ElMessage.success("新增管理员成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消管理员新增',
      })
    })
}

const adminUpdate = (form) => {
  ElMessageBox.confirm(
    '是否确认修改该管理员信息？',
    '修改管理员信息确认',
    {
      type: 'warning',
    }
  )
    .then(() => {
      request.put('/admin/update', {
        id: form.id,
        name: form.name,
        username: form.username,
        password: form.password,
        phone: form.phone,
        siteId: parseInt(form.siteId)
      }).then(res => {
        if (res.code === '200') {
          load()  //刷新数据
          updateDialogVisible.value = false
          ElMessage.success("修改管理员信息成功")
        } else {
          ElMessage.error(res.msg)
        }
      })
    })
    .catch(() => {
      ElMessage({
        type: 'info',
        message: '已取消管理员信息修改',
      })
    })
}

</script>