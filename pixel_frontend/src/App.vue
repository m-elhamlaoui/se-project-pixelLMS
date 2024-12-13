<template>
  <v-app>
    <SideBar v-if="showSidebar" />
    <v-main>
      <v-container fluid>
        <router-view></router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script>
import SideBar from './components/SideBar.vue';
import { isLogged } from './modules/auth';

export default {
  components: {
    SideBar,
  },
  data() {
    return {
    }
  },
  computed: {
    showSidebar() {
      const logged = isLogged();
      return this.$route.path !== '/auth' && logged;
    },
  },
};

</script>

<style>

</style>