<template>
  <el-form :model="data.form" label-width="120px" ref="formRef" :rules="rules">
    <el-form-item label="文章标题" prop="biaoti">
      <el-input style="width: 300px; " v-model="data.form.biaoti" />
    </el-form-item>
    <el-form-item label="文章描述" prop="miaoshu">
      <el-input style="width: 700px; " v-model="data.form.miaoshu" type="textarea" />
    </el-form-item>
    <!--    当前时间-->
    <el-form-item label="发布时间" >
      <el-text class="mx-1" >{{data.form.time}}</el-text>
    </el-form-item>
    <!--申请表-->
    <el-form-item label="上传图片" prop="tupian">
      <el-upload action="http://localhost:9090/files/upload" :on-success="handleFilesUploadSuccess">
        <el-button v-model="data.form.tupian" type="primary">上传图片</el-button>
      </el-upload>
    </el-form-item>

    <el-form-item label="文章内容" prop="neirong">
      <el-input style="width: 700px; " v-model="data.form.neirong" type="textarea" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="save">创建</el-button>
      <el-button @click="fanhui">返回</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive,ref } from 'vue'
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";

// do not use same name with ref
const data = reactive({
  form:{},
})

//表单绑定ref，用于表单预校验,valid是true/flase
const formRef = ref()
// '/Tianjia/add',data.form
const save=()=>{
  formRef.value.validate((valid)=>{
    if(valid){
      request.request({
        // 此处根据id是否为空判断什么传输修改方式。后端数据库那块也有个判断
        url:data.form.id? '/Xinzeng/update' : '/Xinzeng/add',
        method:data.form.id? 'PUT':'POST',
        data:data.form,
      }).then(res=>{
        console.log(data.form)
        if(res.code==='200'){
          console.log(data.form)
          ElMessage.success("操作成功")
          location.href='/wenz'
        }
        else{
          ElMessage.error(res.msg)
        }
      })

    }
  })

}
const fanhui=()=>{
  location.href='/wenz'
}

const route = useRoute()
//打印传递过来的数据
data.form=route.query

const handleFilesUploadSuccess=(res)=>{
  data.form.tupian=res.data;
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
if(!data.form.id){
  //判断是add还是update
  data.form.time=nowDate(time)
}

const rules=reactive({
  biaoti: [
    { required: true, message: '请输入文章标题', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  miaoshu: [
    { required: true, message: '请输入文章描述', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  tupian: [
    { required: true, message: '请上传图片', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  neirong: [
    { required: true, message: '请请输入文章内容', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
})
</script>




