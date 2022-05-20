<template>
<!--  <button @click="signIn">login</button>-->
<!--  <button @click="signOut">log out</button>-->
<!--  <div>{{ googleUserData }}</div>-->
  <div>
<!--    <h3>Google Profile Information</h3>-->
<!--    <p>This information will populate after the Google One-Tap Signin is completed.</p>-->
<!--    <img :title="googleUserData.name" :src="googleUserData.picture" alt="User's Profile Picture">-->
<!--    <p>Name: {{ googleUserData.name}}</p>-->
<!--    <p>Email: {{ googleUserData.email }}</p>-->
<!--    <p>Email Verified: {{ googleUserData.email_verified }}</p>-->
<!--    <p>Sub: {{ googleUserData.sub }}</p>-->
  </div>
</template>

<script lang="ts">
import googleOneTapSignin from "../composables/googleOneTapSignin"
import {defineComponent, onMounted, ref, watch} from 'vue'
import {useStore} from "@/store";
import {ActionTypes} from "@/store/actions";

export default defineComponent({
  name: 'GoogleAuthSignIn',
  setup(){
    const store = useStore()

    const setJwt = (jwt: string) => store.dispatch(ActionTypes.SetJwt, {jwt})

    const googleUserData = ref({
      name: '',
      email: '',
      email_verified: '',
      picture: '',
      sub: ''
    })

    onMounted(() => {
      const { googleOptions, oneTapSignin, userData, jwt } = googleOneTapSignin()
      oneTapSignin(googleOptions)
      watch(jwt, () => {
        console.log('Google authentication successful (jwt obtained)')
        setJwt(jwt.value)
      })
      watch(userData, () => {
        googleUserData.value = userData.value
      })
    })
    return { googleUserData }
  }
})
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
h3 {
  margin: 40px 0 0;
}
ul {
  list-style-type: none;
  padding: 0;
}
li {
  display: inline-block;
  margin: 0 10px;
}
a {
  color: #42b983;
}
</style>
