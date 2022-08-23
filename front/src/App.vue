<script setup lang="ts">
import {RouterView} from "vue-router";</script>

<template>
  <RouterView />
</template>

<style>
  body {
    display: flex;
    width: 100%;
    min-width: 1200px;
    height: 100vh;
    margin: 0;
    padding: 0;
    justify-content: center;
    align-items: center;
  }

  #app {
    width: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  a {
    text-decoration: none;
  }
</style>
