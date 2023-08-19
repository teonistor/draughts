<!--suppress HtmlUnknownTag, CheckEmptyScriptTag, HtmlUnknownAttribute -->
<template>
  <v-expansion-panels popout
                      v-model="expanded">

    <v-expansion-panel hover focusable popout>
      <v-expansion-panel-header>
        New game
      </v-expansion-panel-header>

      <v-expansion-panel-content class="outer-panel">

        <v-btn @click="newGame">New game</v-btn>
        with the following settings:
        <v-text-field v-model="startingRowsInput" label="Starting rows"/>
        <v-text-field v-model="boardSizesInput" label="Sizes"/>

<!-- the privileged dimension with respect to which pieces are initially placed, and have to move forward until promoted -->

      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>
<script>
  export default {
    name: 'newGameControls',
    props: ['stompClient', 'startingRows', 'boardSizes'],

    data: () => ({
      expanded: null,
      startingRowsInput: '',
      boardSizesInput: ''
    }),

    computed: {
    },

    methods: {
      newGame () {
        this.stompClient.send("/draughts/new-game", {}, JSON.stringify({
          startingRows: parseInt(this.startingRowsInput),
          boardSizes: this.boardSizesInput.split(" ").map(e => parseInt(e))
        }));
        this.expanded = null;
      }
    },

    watch: {
    },

    mounted () {
    }
  }
</script>