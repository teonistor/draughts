<!--suppress HtmlUnknownTag, HtmlUnknownAttribute, CheckEmptyScriptTag -->
<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col md="4">
          <p v-if="!currentPlayer">No active game</p>
          <p v-else>{{ situation }}</p>
          <p>{{ message }}</p>

          <!-- TODO make this a component to hold the add/remove dimensions aspect separate -->
          <v-btn @click="newGame">New game</v-btn> with the following settings:
          <v-text-field v-model="startingRowsInput" label="Starting rows" />
          <v-text-field v-model="boardSizesInput" label="Sizes" />

        </v-col>

        <v-col md="4" v-for="partialIndex in partialIndices">
          <p>Board at ({{ partialIndex.join(', ') }}, z, x, y)</p>

          <board3D :depth="last3D.depth"
                   :width="last3D.width"
                   :height="last3D.height"
                   :data="section(partialIndex)"
                   :parity="parityAt(partialIndex)" />

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
      ongoingJump: null,
      situation: "",

      // From settings
      boardSizes: [],
      partialIndices: [],
      last3D: {depth: 1, width: 1, height: 1},

      // From messaging
      message: '',

      // New game inputs
      startingRowsInput: '',
      boardSizesInput: '',

      // Other...
      stompClient: null
    }),

    computed: {

    },

    watch: {
      partialIndices (valu) {
        console.log(valu)
      }
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

        function localDebug(u) {
          console.log('u', u)
          return u;
        }

        const data = {};
        this.board.forEach(kv => {
          if (partialIndex.map((d,i) => localDebug(kv[0][i]) === localDebug(d)).reduce((a,b) => a && b, true))
            data[kv[1].join(',')] = kv[2];
        });
        console.log('Data section', data)
        return data;
      },

      parityAt (partialIndex) {
        if (!this.board.length)
          return -1;
        else
          // Parity tied to arbitrary choice in InitialBoardProvider
          return partialIndex.reduce((a,b) => a + b, 0) % 2;
      },


      newGame () {
        this.stompClient.send("/draughts/new-game", {}, JSON.stringify({
          startingRows: parseInt(this.startingRowsInput),
          boardSizes: this.boardSizesInput.split(" ").map(e => parseInt(e))
        }));
      },


      receiveState (message) {
        const state = JSON.parse(message.body);
        this.board = state.board;
        this.currentPlayer = state.currentPlayer;
        this.ongoingJump = state.ongoingJump;
        this.situation = state.situation;
      },

      receiveSettings (message) {
        const settings = JSON.parse(message.body);
        this.boardSizes = settings.boardSizes;
        this.partialIndices = settings.partialIndices;
        this.last3D = settings.last3D;
      },

      receiveMessage (message) {
        this.message = message;
      }
    },

    mounted () {
      this.connect();
    }
  }
</script>