<template>
  <el-form :model="data.form" label-width="120px">
    <el-form-item label="订单id:">
      <span>{{ data.form.id }}</span>
    </el-form-item>
    <el-form-item label="关联用户id:">
      <span>{{ data.form.userId }}</span>
    </el-form-item>
    <el-form-item label="订单状态:" props="status">
      <span v-if="data.form.status == -1">已取消</span>
      <span v-if="data.form.status == 0">已下单</span>
      <span v-if="data.form.status == 1">待上门</span>
      <span v-if="data.form.status == 2">已取件</span>
      <span v-if="data.form.status == 3">待付款</span>
      <span v-if="data.form.status == 4">已付款</span>
      <span v-if="data.form.status == 5">已入库</span>
      <span v-if="data.form.status == 6">运输中</span>
      <span v-if="data.form.status == 7">待派送</span>
      <span v-if="data.form.status == 8">派送中</span>
      <span v-if="data.form.status == 9">已完成</span>
    </el-form-item>
    <el-form-item label="创建时间：" props="createTime">
      <span>{{ data.form.createTime }}</span>
    </el-form-item>
    <el-form-item label="更新时间：" props="updateTime">
      <span>{{ data.form.updateTime }}</span>
    </el-form-item>
    <el-form-item label="物品：" props="goods">
      <span>{{ data.form.goods }}</span>
    </el-form-item>
    <el-form-item label="重量：" props="weight">
      <span>{{ data.form.weight }} kg</span>
    </el-form-item>
    <el-form-item label="价格：" props="price">
      <span>{{ data.form.price }} 元</span>
    </el-form-item>

    <el-collapse v-model="collapseName">
      <el-collapse-item title="寄件人信息" name="1">
        <el-form-item label="寄件人姓名：" props="senderName">
          <span>{{ data.form.senderName }} </span>
        </el-form-item>
        <el-form-item label="寄件人电话：" props="senderPhone">
          <span>{{ data.form.senderPhone }}</span>
        </el-form-item>
        <el-form-item label="寄件人地址：" props="senderAddress">
          <span>{{ data.form.senderAddress }}</span>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="收件人信息" name="2">
        <el-form-item label="收件人姓名：" props="receiverName">
          <span>{{ data.form.receiverName }} </span>
        </el-form-item>
        <el-form-item label="收件人电话：" props="receiverPhone">
          <span>{{ data.form.receiverPhone }}</span>
        </el-form-item>
        <el-form-item label="收件人地址：" props="receiverAddress">
          <span>{{ data.form.receiverAddress }}</span>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="取/派件快递员信息" name="3">
        <el-form-item label="取件快递员id:" v-if="data.form.pickerId != null">
          <span>{{ data.form.pickerId }} </span>
        </el-form-item>
        <el-form-item label="取件快递员姓名：" label-width="145" v-if="data.form.pickerId != null">
          <span>{{ data.form.pickerName }}</span>
        </el-form-item>
        <el-form-item label="派件快递员id:" v-if="data.form.dispatcherId != null">
          <span>{{ data.form.dispatcherId }} </span>
        </el-form-item>
        <el-form-item label="派件快递员姓名：" label-width="145" v-if="data.form.dispatcherName != null">
          <span>{{ data.form.dispatcherName }}</span>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="轨迹信息" name="4">
        <el-timeline style="max-width: 600px; margin-top: 20px;">
          <el-timeline-item v-for="(activity, index) in data.trackList" :key="index" :timestamp="activity.createTime"
            color="#0bbd87">
            {{ activity.remark }}
          </el-timeline-item>
        </el-timeline>
      </el-collapse-item>

    </el-collapse>
  </el-form>

  <!-- <el-timeline style="max-width: 600px; margin-top: 20px;">
    <el-timeline-item v-for="(activity, index) in data.trackList" :key="index" :timestamp="activity.createTime"
      color="#0bbd87">
      {{ activity.remark }}
    </el-timeline-item>
  </el-timeline> -->

</template>

<script setup>
import { reactive, ref, onBeforeMount } from 'vue'
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { routerKey, useRoute, useRouter } from "vue-router";

onBeforeMount(() => {
  const route = useRoute()
  const router = useRouter()
  data.form.id = route.query.id
  load()
})

const load = () => {
  request.get('/order?id=' + data.form.id).then(res => {
    console.log(res.data)
    data.form = res.data
  })

  request.get('/track/' + data.form.id).then(res => {
    console.log(res.data)
    data.trackList = res.data
  })
}

const collapseName = ref([])

const data = reactive({
  form: {
    id: -1,
    createTime: '',
    updateTime: '',
    status: -99,
    senderName: '',
    senderPhone: '',
    senderAddress: '',
    receiverName: '',
    receiverPhone: '',
    receiverAddress: '',
    goods: '',
    weight: 0,
    price: 0,
  },
  trackList: []
})

const backToList = () => {
  router.push({
    path: '/order'
  })
}

</script>
