<template>
<!--  <div id="nav">-->
<!--    <router-link to="/">Home</router-link> |-->
<!--    <router-link to="/about">About</router-link>-->
<!--  </div>-->
  <router-view/>
</template>

<script lang="ts">
import {defineComponent} from "vue";
import {useUiStore} from "@/stores/ui";

export default defineComponent({
  name: 'app',
  setup() {
    const uiStore = useUiStore()
    return {
      onVisibilitychange: () => {
        uiStore.updateChatVisibility(document.visibilityState === "visible")
      },
      onFocus: () => {
        uiStore.updateChatVisibility(true);
      },
      onBlur: () => {
        uiStore.updateChatVisibility(false);
      },
      onOnline: () => {
        uiStore.updateNavigatorOnlineStatus(true);
      },
      onOffline: () => {
        uiStore.updateNavigatorOnlineStatus(false);
      }
    }
  },

  mounted() {
    window.addEventListener('visibilitychange', this.onVisibilitychange)
    window.addEventListener('focus', this.onFocus)
    window.addEventListener('blur', this.onBlur)
    window.addEventListener('offline', this.onOffline);
    window.addEventListener('online', this.onOnline);
  },

  unmounted() {
    window.removeEventListener('visibilitychange', this.onVisibilitychange)
    window.removeEventListener('focus', this.onFocus)
    window.removeEventListener('blur', this.onBlur)
    window.removeEventListener('offline', this.onOffline);
    window.removeEventListener('online', this.onOnline);  }
});
</script>

<style lang="scss">

html {
  box-sizing: border-box;
}
* {
  box-sizing: inherit;
}
body, html {
  height: 100%;
}
html {
  font-size: 62.5%; /* équivalence "10px" sur l'élément racine */
}
body {
  margin: 0;
  font-size: 1.6em; /* taille de base pour tous les éléments équivalent 14px */
}
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
//
//#nav {
//  padding: 30px;
//
//  a {
//    font-weight: bold;
//    color: #2c3e50;
//
//    &.router-link-exact-active {
//      color: #42b983;
//    }
//  }
//}
</style>
