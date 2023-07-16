<!--suppress HtmlUnknownTag, HtmlUnknownAttribute, CheckEmptyScriptTag -->
<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col md="4">

          <v-text-field /> <v-text-field />
          <v-btn @click="newGame">New game</v-btn>

        </v-col>

        <v-col md="4" v-for="partialIndex in partialIndices">
          <p>Board at ({{ partialIndex.join(', ') }}, z, x, y)</p>

          <board3D :depth="3"
                   :width="4"
                   :height="5"
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

  const testBoard = '[[[1,2,3,4],"BP"],[[0,1,0,3],"BP"],[[0,0,2,4],"BP"],[[1,0,0,3],"BK"],[[1,2,0,3],"BP"],[[0,0,3,3],"BP"],[[1,1,3,3],"BP"],[[1,0,3,4],"BP"],[[1,2,3,0],"WP"],[[1,1,3,1],"WK"],[[0,0,1,1],"WP"],[[0,1,2,3],"BP"],[[1,1,2,4],"BP"],[[1,1,0,4],"BP"],[[0,2,2,4],"BP"],[[0,0,1,3],"BP"],[[1,0,1,4],"BP"],[[1,1,0,0],"WP"],[[0,2,2,0],"WP"],[[1,0,1,0],"WP"],[[0,1,1,4],"BP"],[[0,0,0,4],"BP"],[[0,1,3,0],"WP"],[[0,0,0,0],"WP"],[[0,2,0,0],"WP"],[[0,2,0,4],"BP"],[[1,2,2,3],"BP"],[[1,2,1,4],"BP"],[[1,0,2,3],"BP"],[[1,2,0,1],"WP"],[[0,2,1,1],"WP"],[[0,0,3,1],"WP"],[[0,1,1,0],"WP"],[[1,0,2,1],"WP"],[[1,0,0,1],"WP"],[[0,0,2,0],"WP"],[[0,1,3,4],"BP"],[[1,1,1,1],"WP"],[[1,0,3,0],"WP"],[[0,2,3,1],"WP"],[[0,2,3,3],"BP"],[[1,2,1,0],"WP"],[[1,2,2,1],"WP"],[[1,1,1,3],"BP"],[[1,1,2,0],"WP"],[[0,2,1,3],"BP"],[[0,1,2,1],"WP"],[[0,1,0,1],"WP"]]';
  const testDimensions = [2, 3, 4, 5];

  export default {
    name: 'game',
    components: {board3D},

    data: () => ({
      // From settings
      boardSizes: [],
      partialIndices: [[0], [1]],

      // From state
      board: JSON.parse(testBoard),
      currentPlayer: null,
      ongoingJump: null,
      isGameOver: false,

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
          if (partialIndex.map((d,i) => kv[0][i] === d).reduce((a,b) => a && b))
            data[kv[0].slice(partialIndex.length).join(',')] = kv[1];
        });
        return data;
      },

      parityAt (partialIndex) {
        if (!this.board.length)
          return -1;
        else
          return this.board[0][0].slice(partialIndex.length).reduce((a,b) => a + b) % 2;
      },


      newGame () {
        this.stompClient.send("/draughts/new-game", {}, JSON.stringify({
          startingRows: 2,
          boardSizes: [5, 5, 5]
        }));
      },


      receiveState (message) {
        const state = JSON.parse(message.body);
        this.board = state.board;
        this.currentPlayer = state.currentPlayer;
        this.ongoingJump = state.ongoingJump;
        this.isGameOver = state.isGameOver;
      },

      receiveSettings (message) {
        const settings = JSON.parse(message.body);
        this.boardSizes = settings.boardSizes;
        this.partialIndices = settings.partialIndices;
      },

      receiveMessage (message) {

      }
    },

    mounted () {
      this.connect();
    }
  }
</script>