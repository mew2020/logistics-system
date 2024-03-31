<template>
  <div>
    <!--  上方搜索框,此处的card样式在assets/css/global.css中-->
    <div class="card" style="margin-bottom: 10px;">
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入奖学金名称查询" v-model="data.name"
                :prefix-icon="Search"></el-input>
      <el-input style="width: 250px; margin-right: 10px;" placeholder="请输入奖学金类型查询" v-model="data.leixing"
                :prefix-icon="Search"></el-input>
      <!--      查询事件记得写@click="load",不然不变,由于数没传进去-->
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" style="margin: 0 10px" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <!--      固定表头表单-->
      <div>
        <!--        此处内容对应的script中的data里tableDate内容    ctrl+D复制上行-->
        <el-table :data="data.tableData" height="440" style="width: 100%">
          <el-table-column prop="name" label="奖学金名称" width="180"/>
          <el-table-column prop="leixing" label="奖学金类型" width="180"/>
          <el-table-column prop="xname" label="学生姓名" width="180"/>
          <el-table-column prop="ban" label="所属学院" width="180"/>
          <el-table-column prop="xuehao" label="学生学号" width="180"/>
          <el-table-column fixed="right" align="center" label="操作" width="160" >
          <template #default="scope">
            <el-button type="danger" size="medium" @click="handleadd(scope.row.id)">处 理</el-button>
          </template>
          </el-table-column>
        </el-table>
      </div>
    </div>

    <el-dialog width="50%" v-model="data.formVisible" title="复审">
      <el-form :model="data.form" ref="formRef" :rules="rules">
        <el-form-item :label-width="formLabelWidth" label="举报原因" style="margin-right: 40px">
          <el-text >{{data.yuanyin}}</el-text>
        </el-form-item>
        <el-form-item style="margin-right: 40px" label="审核回复" :label-width="formLabelWidth" prop="username">
          <el-input v-model="data.form.huifu" autocomplete="off" />
        </el-form-item>
        <el-form-item label="是否通过" :label-width="formLabelWidth" prop="tong">
          <el-radio-group v-model="data.form.zhuangtai" size="large">
            <el-radio-button label="初审过"  />
            <el-radio-button label="未通过" />
          </el-radio-group>
<!--          <el-select v-model="data.form.zhuangtai" placeholder="请选择">-->
<!--            <el-option label="通过" value="已通过" />-->
<!--            <el-option label="不通过" value="未通过" />-->
<!--          </el-select>-->
        </el-form-item>
      </el-form>
      <template #footer >
      <span class="dialog-footer">
        <el-button @click="data.formVisible = false">取消</el-button>
        <el-button type="primary" @click="save(data.id)">确定</el-button>
      </span>
      </template>
    </el-dialog>

  </div>
</template>
<script setup>
import {reactive,ref} from "vue";
//此处对应的这个(搜索图标),详见express网站 :prefix-icon="Search
import {Search} from "@element-plus/icons-vue";
//选第二个
import request from "@/utils/request";
import {ElMessage, ElMessageBox} from "element-plus";
import {useRouter} from "vue-router";

const formLabelWidth = '140px'
const data = reactive({
  name: '',
  leixing: '',
  yuanyin:'',
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
  request.get('/Sh/selectPage', {
    params: {
      name: data.name,
      leixing: data.leixing,
      zhuangtai:"已举报",
      fushen:"未审核",
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
  data.leixing = ''
  load()
}



const handleadd=(id)=>{
  request.get('/Sh/zhaoyu', {
    params: {
      id:id
    }
  }).then(res => {
    data.yuanyin = res.data.list[0].yuanyin
  })
  //清空数据
  data.form={}
  data.formVisible=true
  data.form.id=id
}

//表单绑定ref，用于表单预校验,valid是true/flase
const formRef = ref()
const save=()=>{
  formRef.value.validate((valid)=>{
    if(valid){
      request.put('/Sh/xin',data.form).then(res=>{
        if(res.code==='200'){
          console.log(data.form)
          load()
          data.formVisible=false
          ElMessage.success("操作成功")
        }
        else{
          ElMessage.error(res.msg)
        }
      })

    }
  })

}

// data.form.zhuangtai = ref('已通过')


// const rules=reactive({
//   username: [
//     { required: true, message: '请输入审核回复', trigger: 'blur' },
//     // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
//   ],
//   tong: [
//     { required: true, message: '请选择是否通过', trigger: 'blur' },
//     // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
//   ],
// })
</script>

