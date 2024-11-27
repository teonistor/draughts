<!--suppress HtmlUnknownTag CheckEmptyScriptTag-->
<template>
  <v-app>
    <game v-if="!!gid" :gid="gid" />
    <lobby v-if="showLobby" @assignGid="assignGid" />
  </v-app>
</template>
<script>
  import game from './views/game.vue';
  import lobby from './views/lobby.vue';

  export default {
    components: {game, lobby},

    data: () => ({
      gid: null,
      showLobby: false
    }),

    methods: {
      assignGid(gid) {
        this.gid = gid;
        this.showLobby = !gid;
      }
    },

    created() {
      this.$watch(
        () => this.$route.query,
        (next, prev) => this.assignGid(next.gid));
    },

    mounted () {
      this.assignGid(this.$route.query && this.$route.query.gid)
    }
  }
</script>
