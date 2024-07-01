<script setup>
import { ref, reactive, getCurrentInstance, nextTick, computed } from "vue";
import { useRouter, useRoute } from "vue-router";
const { proxy } = getCurrentInstance();
</script>

<template>
  <div>
    <div class="top">
      套牌
    </div>
  </div>
</template>

<style scoped lang="scss">
.top {
  margin-top: 20px;
}
</style>