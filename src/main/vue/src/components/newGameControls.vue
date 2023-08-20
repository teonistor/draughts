<!--suppress HtmlUnknownTag, CheckEmptyScriptTag, HtmlUnknownAttribute -->
<template>
  <v-expansion-panels popout
                      v-model="expanded">

    <v-expansion-panel hover focusable popout>
      <v-expansion-panel-header>
        New game
      </v-expansion-panel-header>

      <v-expansion-panel-content class="outer-panel">

        Starting rows:<br>
        <v-text-field type="number" style="width:100px" v-model="startingRows" />

        Board sizes: ({{ boardSizes.length }} dimensions)<br>
        <v-btn @click="addDimension">+</v-btn>
        <v-btn @click="removeDimension">-</v-btn>

        <p v-for="boardSize in boardSizes">
          <v-text-field type="number" style="width:100px; display: inline-block" v-model="boardSize.value" />
          &emsp;–&ensp;{{ boardSize.description }}
        </p>

        <v-btn @click="newGame">New game</v-btn>

      </v-expansion-panel-content>
    </v-expansion-panel>
  </v-expansion-panels>
</template>
<script>
  const descriptions = [
    'board height – the privileged dimension with respect to which pieces are initially placed, and have to move forward until promoted',
    'board width',
    'board depth',
    'meta height',
    'meta width'
  ];
  const defaultDescription = 'some higher dimension';

  export default {
    name: 'newGameControls',
    props: ['stompClient', 'startingRowsIn', 'boardSizesIn'],

    data: () => ({
      expanded: null,

      startingRows: 1,
      boardSizes: [],

      boardSizesInput: ''
    }),

    computed: {
    },

    methods: {
      addDimension () {
        this.boardSizes = [{
          value: 3,
          description: descriptions[this.boardSizes.length] || defaultDescription
        }].concat(this.boardSizes);
      },

      removeDimension () {
        this.boardSizes = this.boardSizes.slice(1);
      },

      newGame () {
        this.stompClient.send("/draughts/new-game", {}, JSON.stringify({
          startingRows: parseInt(this.startingRows),
          boardSizes: this.boardSizes.map(e => parseInt(e.value))
        }));
        this.expanded = null;
      }
    },

    watch: {
    },

    mounted () {
      this.addDimension();
      this.addDimension();
      this.addDimension();
    }
  }
</script>