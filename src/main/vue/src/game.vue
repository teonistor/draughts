<!--suppress HtmlUnknownTag, HtmlUnknownAttribute, CheckEmptyScriptTag -->
<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col md="4">
          <p v-if="!currentPlayer">No active game</p>
          <p v-else>{{ situation }}
            <v-btn v-if="situation.indexOf('(or pass)') > -1" @click="pass">Pass</v-btn>
          </p>

          <p>{{ message }}</p>

          <!-- TODO make this a component to hold the add/remove dimensions aspect separate -->
          <v-btn @click="newGame">New game</v-btn> with the following settings:
          <v-text-field v-model="startingRowsInput" label="Starting rows" />
          <v-text-field v-model="boardSizesInput" label="Sizes" />

          <hr>

          Connection: {{ stompClient && '✅' || '❌' }}

          <p>
            Black on top &ensp;
            <v-switch v-model="whiteOnTop" style="display: inline-block; transform: translateY(5px)"/>
            &ensp;White on top
          </p>

        </v-col>

        <v-col md="4" v-for="partialIndex in higherIndices">
          <p>Board at ({{ partialIndex.join(', ') }}, z, x, y)</p>

          <board3D :depth="last3D.depth"
                   :width="last3D.width"
                   :height="last3D.height"
                   :data="section(partialIndex)"
                   :parity="parityAt(partialIndex)"
                   :selected="selectedAt(partialIndex)"
                   :highlighted="highlightAt(partialIndex)"
                   :whiteOnTop="whiteOnTop"
                   @select="onClick(partialIndex, ...arguments)" />

        </v-col>
      </v-row>
    </v-container>
  </v-app>
</template>
<script>
  import SockJS from 'sockjs-client';
  import Stomp from 'stompjs';

  import board3D from './components/board3D.vue';

  export default {
    name: 'game',
    components: {board3D},

    data: () => ({
      // From state
      board: [],
      currentPlayer: null,
      availableMoves: [],
      situation: '',

      // From settings
      startingRows: 0,
      higherIndices: [],
      metaWidth: 1,
      metaHeight: 1,
      boardDepth: 1,
      boardWidth: 1,
      boardHeight: 1,

      // From messaging
      message: '',

      // New game inputs
      startingRowsInput: '',
      boardSizesInput: '',

      // Gameplay
      selected: null,

      // UI
      whiteOnTop: false,

      // Other...
      stompClient: null
    }),

    computed: {

    },

    methods: {

      connect () {
        let socket = new SockJS(this.$backendRoot + '/draughts-subscribe');
        this.stompClient = Stomp.over(socket);
        this.stompClient.connect({}, frame => {
          this.stompClient.subscribe('/draughts/draughts-state', this.receiveState);
          this.stompClient.subscribe('/draughts/draughts-settings', this.receiveSettings);
          this.stompClient.subscribe('/draughts/draughts-message', this.receiveMessage);
        });

        // Poor man's callback chain
        let stompOnClose = socket.onclose;
        socket.onclose = status => {
          stompOnClose(status);
          this.stompClient = null;
        }
      },


      section (partialIndex) {

        const data = {};
        this.board.forEach(kv => {
          if (partialIndex.map((d,i) => kv[0][i] === d).reduce((a,b) => a && b, true))
            data[kv[1].join(',')] = kv[2];
        });
        return data;
      },

      highlightAt (partialIndex) {
        if (!this.availableMoves)
          return [];

        if (this.selected) {
          return this.availableMoves.filter(move => JSON.stringify(this.selected[0]) === JSON.stringify(move[0])
                                                 && JSON.stringify([this.selected[1], this.selected[2], this.selected[3]]) === JSON.stringify(move[1])
                                                 && JSON.stringify(partialIndex) === JSON.stringify(move[2]))
                                    .map(move => move[3]);

        } else {
          return this.availableMoves.filter(move => JSON.stringify(partialIndex) === JSON.stringify(move[0]))
                                    .map(move => move[1]);

        }


        const data = {};
        this.board.forEach(kv => {
          if (partialIndex.map((d,i) => kv[0][i] === d).reduce((a,b) => a && b, true))
            data[kv[1].join(',')] = kv[2];
        });
        return data;
      },

      parityAt (partialIndex) {
        if (!this.board.length)
          return -1;
        else
          // Parity tied to arbitrary choice in InitialBoardProvider
          return partialIndex.reduce((a,b) => a + b, 0) % 2;
      },

      selectedAt (partialIndex) {
        return (this.selected && (JSON.stringify(this.selected[0]) === JSON.stringify(partialIndex)))
            && this.selected.slice(1)
            || null;
      },


      pass () {
        this.stompClient.send("/draughts/pass", {}, '');
      },

      newGame () {
        this.stompClient.send("/draughts/new-game", {}, JSON.stringify({
          startingRows: parseInt(this.startingRowsInput),
          boardSizes: this.boardSizesInput.split(" ").map(e => parseInt(e))
        }));
      },


      receiveState (message) {
        const state = JSON.parse(message.body);
        this.board          = state.board;
        this.currentPlayer  = state.currentPlayer;
        this.availableMoves = state.availableMoves;
        this.situation      = state.situation;
      },

      receiveSettings (message) {
        const settings = JSON.parse(message.body);
        this.startingRows  = settings.startingRows;
        this.higherIndices = settings.higherIndices;
        this.metaWidth     = settings.metaWidth;
        this.metaHeight    = settings.metaHeight;
        this.boardDepth    = settings.boardDepth;
        this.boardWidth    = settings.boardWidth;
        this.boardHeight   = settings.boardHeight;
      },

      receiveMessage (message) {
        this.message = message.body;
      },

      onClick (partialIndex, z, x, y) {
        if (!this.selected)
          this.selected = [partialIndex, z, x, y];

        else if (JSON.stringify(this.selected) === JSON.stringify([partialIndex, z, x, y]))
          this.selected = null;

        else {
          const from = [].concat(this.selected[0]).concat([this.selected[1], this.selected[2], this.selected[3]]);
          const to = [].concat(partialIndex).concat([z, x, y]);

          this.stompClient.send("/draughts/click", {}, JSON.stringify([from, to]));
          this.selected = null;

        }
      }
    },

    mounted () {
      this.connect();
    }
  }
</script>