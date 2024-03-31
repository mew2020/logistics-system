<template>
  <div>
    <!--    调用下面封装的样式-->
    <div class="login-container">
      <div style="width:350px" class="login-box">
        <div style="font-weight:bold;font-size:24px;text-align: center;margin-bottom: 30px">登 录</div>
        <el-form :model="data.form" ref="formRef" :rules="rules">
          <!--          <el-form-item label="账号">-->
<!--          此处的prop的名需和下面一致，才能调用-->
          <el-form-item prop="username">
          <el-input prefix-icon="User" v-model="data.form.username" placeholder="请输入账号"/>
          </el-form-item>
          <!--          </el-form-item>-->
          <el-form-item prop="password">
          <el-input prefix-icon="Lock" v-model="data.form.password" placeholder="请输入密码"/>
          </el-form-item>
          <el-form-item prop="xueyuan" v-if="data.form.role==3">
            <el-input prefix-icon="Discount"  v-model="data.form.xueyuan" placeholder="请输入所属学院"/>
          </el-form-item>

          <el-form-item>
            <el-radio-group v-model="data.form.role">
              <el-radio :label="3">学生工作领导小组</el-radio>
              <el-radio :label="6">学生处</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item>
<!--            此处@click="login"实现与后端的绑定-->
            <el-button type="primary" style="width: 100%" @click="login">登录 </el-button>
          </el-form-item>
        </el-form>
        <div style="margin-top: 30px;text-align: right">
          还没有账号，请<a href="/register">注册</a>
        </div>
      </div>
    </div>
  </div>
</template>
<!--ctrl+alt+L排代码快捷键-->
<script setup>
//这个必须导入,ref需要被引用
import {reactive,ref} from "vue";
//引入request对象
import request from "@/utils/request";
import {Value} from "sass";
import {ElMessage} from "element-plus";


const data = reactive({
  form: {}
})
data.form.role = 3
//表单绑定ref，用于表单预校验,valid是true/flase
const formRef = ref()
const login=()=>{
  formRef.value.validate((valid)=>{
    if(valid){
        request.post('/login',data.form).then(res=>{
          if(res.code==='200'){
            localStorage.setItem('student-user',JSON.stringify(res.data))//传递JSON字符串，放入缓存
            ElMessage.success('登陆成功')
              location.href = '/home'   //跳转到主页
          }
          else{
            ElMessage.error(res.msg)
          }
        })
    }
  })
}
// 以下是定义输入要求
const rules=reactive({
  username: [
    { required: true, message: '请输入账号', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
  xueyuan: [
    { required: true, message: '请输入所属学院', trigger: 'blur' },
    // { min: 3, max: 5, message: 'Length should be 3 to 5', trigger: 'blur' },
  ],
})

</script>
<!--此处封装样式-->
<style scoped>
.login-container {
  min-height: 100vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
  background-image: url("@/assets/imgs/beijing.png");
  background-size: cover;
}
.login-box{
  background-color: rgba(255,255,255,.6);
  box-shadow: 0 0 10px rgba(0,0,0,0.1);
  padding-right: 50px;
  padding-left: 50px;
  padding-bottom: 20px;
  padding-top: 30px;
}
</style>