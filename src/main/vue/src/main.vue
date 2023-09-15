<!--suppress HtmlUnknownTag CheckEmptyScriptTag-->
<template>
  <v-app>
    <lobby />
  </v-app>
</template>
<script>
  import game from './views/game.vue';
  import lobby from './views/lobby.vue';

  export default {
    components: {lobby}
  }
</script>
