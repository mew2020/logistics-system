<template>
  <div>

    <div class="card" style="line-height: 30px">
      <div>欢迎您，{{ user.name }} 祝您今天过得开心hhh！</div>
    </div>

  </div>
</template>

<script setup>
  import request from "@/utils/request";
  const user = JSON.parse(localStorage.getItem('student-user') || '{}')
</script>