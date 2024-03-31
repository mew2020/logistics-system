<template>
  <el-form :model="data.form" label-width="120px">
    <!--申请表-->
    <el-form-item label="上传图片1 ">
      <el-upload action="http://localhost:9090/files/upload" :on-success="handleFilesUploadSuccess">
        <el-button v-model="data.form.tu" type="primary">上传图片</el-button>
      </el-upload>
    </el-form-item>
    <el-form-item label="上传图片2 ">
      <el-upload action="http://localhost:9090/files/upload" :on-success="ahandleFilesUploadSuccess">
        <el-button v-model="data.form.pian" type="primary">上传图片</el-button>
      </el-upload>
    </el-form-item>
    <el-form-item label="负责老师电话：" >
      <el-input style="width: 300px; " v-model="data.form.dianhua" />
    </el-form-item>

    <el-form-item>
      <el-button type="primary" @click="save">保存</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { reactive } from 'vue'
import request from "@/utils/request";
import {ElMessage} from "element-plus";
import {useRoute} from "vue-router";

// do not use same name with ref
const data = reactive({
  form:{},
})

const load = () => {
  request.get('/Qita/cha', {
    params: {
      id:1
    }
  }).then(res => {
    // data.form = res.data?.list || []
    data.form.tu=res.data.list[0].tu
    data.form.pian=res.data.list[0].pian
    data.form.dianhua=res.data.list[0].dianhua
  })
}
//调用方法,获取后台数据
load()

// '/Tianjia/add',data.form
const save=()=>{
  request.request({
    // 此处根据id是否为空判断什么传输修改方式。后端数据库那块也有个判断
    url:data.form.id? '/Qita/update' : '/Xinzeng/add',
    method:data.form.id? 'PUT':'POST',
    data:data.form,
  }).then(res=>{
    console.log(data.form)
    if(res.code==='200'){
      console.log(data.form)
      ElMessage.success("操作成功")
      load()
    }
    else{
      ElMessage.error(res.msg)
    }
  })
}

// 此处是接收传来的文件信息
const handleFilesUploadSuccess=(res)=>{
  data.form.tu=res.data;
}
const ahandleFilesUploadSuccess=(res)=>{
  data.form.pian=res.data;
}

data.form.id = 1

</script>
