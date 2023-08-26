<!--suppress HtmlUnknownTag, HtmlUnknownAttribute, CheckEmptyScriptTag -->
<template>
  <v-app>
    <v-container fluid>
      <newGameControls :stompClient="stompClient" />

      <v-row>
        <v-col md="6">
          Connection: {{ stompClient && '✅' || '❌' }}
          <br>
          Selected: {{ selected }}
          <p v-if="!currentPlayer">No active game</p>
          <p v-else>{{ situation }}
            <v-btn v-if="situation.indexOf('(or pass)') > -1" @click="pass">Pass</v-btn>
          </p>

          <p>{{ message }}</p>
        </v-col>
        <v-col md="6">
          Black on top &ensp;
          <v-switch v-model="whiteOnTop" style="display: inline-block; transform: translateY(5px)"/>
          &ensp;White on top
        </v-col>
      </v-row>


      <div style="overflow-x: auto; text-align: center;">

        <metaBoard v-if="!!board"
                   v-for="index in higherIndices"
                   :metaWidth="metaWidth"
                   :metaHeight="metaHeight"
                   :boardDepth="boardDepth"
                   :boardWidth="boardWidth"
                   :boardHeight="boardHeight"
                   :data="board[index]"
                   :parity="parityAt(index)"
                   :selected="selectedAt(index)"
                   :highlighted="highlighted[index] || {}"
                   :whiteOnTop="whiteOnTop"
                   @select="onSelect(index, ...arguments)" />
        <br>
      </div>

      <div style="font-style: oblique; opacity: 0.8; text-align: center">
        Found an issue? Curious about the code? <a href="https://github.com/teonistor/draughts/tree/gui/src">Find the repo on Github</a>.
      </div>
    </v-container>
  </v-app>
</template>
<script>
  import SockJS from 'sockjs-client';
  import Stomp from 'stompjs';

  import metaBoard from './components/metaBoard.vue';
  import newGameControls from './components/newGameControls.vue';

  export default {
    name: 'game',
    components: {metaBoard, newGameControls},

    data: () => ({
      // From state
      board: null,
      currentPlayer: null,
      availableMoves: {},
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

      // Gameplay
      selected: null,

      // UI
      whiteOnTop: false,

      // Other...
      stompClient: null
    }),

    computed: {
      highlighted () {
        return this.selected
            && ((((this.availableMoves || {})[this.selected[0]] || {})[this.selected[1]] || {})[this.selected[2]] || {})
            || this.availableMoves;
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

      parityAt (index) {
        if (!this.board)
          return -1;
        else
          // Parity tied to arbitrary choice in InitialBoardProvider
          return index && (index.split(',').map(e => parseInt(e)).reduce((a,b) => a + b, 0) % 2)
                       || 0;
      },

      selectedAt (index) {
        if (this.selected && this.selected[0] === index) {
          const result = {};
          result[this.selected[1]] = this.selected[2];
          return result;
        }
        return {};
      },


      pass () {
        this.stompClient.send("/draughts/pass", {}, '');
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

      onSelect (index, mx, my, bd, bx, by) {
        // Dismiss clicks on grey
        if ((this.parityAt(index) + mx + my + bd + bx + by) % 2)
          return;

        const selection = [index, [mx, my].join(','), [bd, bx, by].join(',')];

        if (!this.selected) {
          this.selected = selection;
          return;
        }

        const oldSelectionStr = this.selected.join(',');
        const newSelectionStr = selection.join(',');

        if (oldSelectionStr === newSelectionStr) {
          this.selected = null;
          return;
        }

        function removeLeadingComma(s) {
          return s.indexOf(',') === 0 && s.substring(1) || s;
        }

        // Because we're dealing with comma-separated chunks everywhere, which simplifies things everywhere else
        this.stompClient.send('/draughts/click', {}, '[[' + removeLeadingComma(oldSelectionStr) + '],[' + removeLeadingComma(newSelectionStr) + ']]');
        this.selected = null;
      }
    },

    mounted () {
      this.connect();
    }
  }
</script>