<template>
  <h1 class="main-text" @click="moveLogin()">Music Roost</h1>
</template>

<script setup lang="ts">
import router from "@/router";
import axios from "axios";

const moveLogin = () => {
  axios.post("/api/loginCheck")
      .then((res) => {
        router.push({ name: res.data.state ? "schedules" : "login"});
      });
};

const kakaoPay = () => {
  axios.post("/api/kakaoPay")
      .then((res) => {
        const width = 600;
        const height = 800;
        const popupX = (window.screen.width / 2)   - (width / 2);
        const popupY = (window.screen.height / 2) - (height / 2);

        window.open(res.data,'',`width=${width}, height=${height}, left=${popupX}, top=${popupY}`);
      });
};
</script>

<style scoped>
.main-text {
  box-sizing: content-box;
  font-size: 100px;
  cursor: pointer;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translateX(-50%) translateY(-50%);
  margin: 0;
}
</style>
