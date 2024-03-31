<!--此处修改申请表有点问题-->
<template>
  <el-form :model="data.form" label-width="120px" ref="formRef" :rules="rules">
    <el-form-item label="奖学金名称" prop="name">
      <el-input style="width: 300px; " v-model="data.form.name"/>
    </el-form-item>
    <el-form-item label="奖学金类型" prop="leixing">
      <el-select style="width: 300px; " v-model="data.form.leixing" placeholder="请进行选择">
        <el-option label="国家级奖学金" value="国家级奖学金"/>
        <el-option label="省级奖学金" value="省级奖学金"/>
        <el-option label="校级奖学金" value="校级奖学金"/>
      </el-select>
    </el-form-item>
    <el-form-item label="奖学金金额" prop="jine">
      <el-input style="width: 300px; " v-model="data.form.jine"/>
    </el-form-item>

    <!--    当前时间-->
    <el-form-item label="发布时间">
      <el-text class="mx-1">{{ data.form.time }}</el-text>
    </el-form-item>
    <!--申请表-->
    <el-form-item label="申请表" prop="biao">
      <el-upload action="http://localhost:9090/files/upload" :on-success="handleFilesUploadSuccess">
        <el-button v-model="data.form.biao" type="primary">上传申请表</el-button>
      </el-upload>
    </el-form-item>

    <el-form-item label="起止时间" prop="bdate">
      <el-col style="width: 300px; margin-right: 10px; " :span="11">
        <el-date-picker
            v-model="data.form.bdate"
            type="date"
            placeholder="请选择日期"
            style="width: 100%"
        />
      </el-col>
      <el-col :span="11" prop="edate">
        <el-date-picker
            v-model="data.form.edate"
            placeholder="请选择日期"
            style="width: 100%"
        />
      </el-col>
    </el-form-item>
    <el-form-item label="申请条件" prop="tiaojian">
      <el-input style="width: 700px; " v-model="data.form.tiaojian" type="textarea"/>
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="save">创建</el-button>
      <el-button @click="fanhui">返回</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import {reactive, ref} from 'vue'
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";
import {Value} from "sass";
//表单绑定ref，用于表单预校验,valid是true/flase
const formRef = ref()
// do not use same name with ref
const data = reactive({
  form: {},
})
// '/Tianjia/add',data.form

const fanhui = () => {
  location.href = '/jxx'
}

const route = useRoute()
//打印传递过来的数据
data.form = route.query
console.log(data.form)

const handleFilesUploadSuccess = (res) => {
  if (res.data != null) {
    data.form.biao = res.data;
  }
}

var getTime = new Date().getTime(); //获取到当前时间戳
var time = new Date(getTime); //创建一个日期对象
function nowDate(time) {
  var year = time.getFullYear(); // 年
  var month = (time.getMonth() + 1).toString().padStart(2, '0'); // 月
  var date = time.getDate().toString().padStart(2, '0'); // 日
  return (
      year + "-" + month + "-" + date + " "
  )
}

//这里是判断是那种情况下的发布时间
if (!data.form.id) {
  //判断是add还是update
  data.form.time = nowDate(time)
}


const save = () => {
  request.get('/Jxx/cbiao', {
    params: {
      id: data.form.id,
    }
  }).then(res => {
    data.form.biao = res.data.list[0].biao
    // console.log(res.data.list[0].biao)
    console.log(data.form.biao)
    console.log(data.form.biao)
    formRef.value.validate((valid) => {
      if (valid) {
        request.request({
          // 此处根据id是否为空判断什么传输修改方式。后端数据库那块也有个判断
          url: data.form.id ? '/Tianjia/update' : '/Tianjia/add',
          method: data.form.id ? 'PUT' : 'POST',
          data: data.form,
        }).then(res => {
          console.log(data.form)
          if (res.code === '200') {
            console.log(data.form)
            ElMessage.success("操作成功")
            location.href = '/jxx'
          } else {
            ElMessage.error(res.msg)
          }
        })
      }
    })

  })

}


const rules = reactive({
  name: [
    {required: true, message: '请输入奖学金名称', trigger: 'blur'},
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  leixing: [
    {required: true, message: '请选择奖学金类型', trigger: 'blur'},
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  jine: [
    {required: true, message: '请输入奖学金金额', trigger: 'blur'},
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  biao: [
    {required: true, message: '请上传申请表', trigger: 'blur'},
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  bdate: [
    {required: true, message: '请选择起止时间', trigger: 'blur'},
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  tiaojian: [
    {required: true, message: '请输入申请条件', trigger: 'blur'},
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
})
</script>




