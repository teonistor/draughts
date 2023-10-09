<!--suppress HtmlUnknownTag, HtmlUnknownAttribute, CheckEmptyScriptTag -->
<template>
  <v-container fluid>
    <v-row>
      <v-col cols md="6">
        <h2>Select player(s) or create game</h2>
        <p>How it works</p>
        <ul>
          <li>You can assign yourself to multiple players in a game (for hotseat) but not to multiple games</li>
          <li>Once all players in a game are assigned (and you are one of them) you can navigate to it</li>
          <li>However, correct turn order based on player assignment is not currently enforced because I am yet to program for it</li>
          <li>(WIP) You can spectate any game</li>
          <li>At most 10 games can exist at a time. I might modify this limit often</li>
          <li>(WIP) If nothing happens in a game for 10 minutes (including after it is over) it will disappear and make more
              room in the list</li>
        </ul>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols md="6" v-for="game in games">
        <v-card>
          <v-card-title>
            Game of {{ game.name }} created at {{ game.key }}
          </v-card-title>
          <v-card-text>
            <span v-for="player in game.players">
              <v-btn v-if="player.state === 'mine'" outlined
                     :style="{background: mineColor}"
                     @click="deallocate(game.key, player.key, user)" >
                {{ player.key }}
              </v-btn>
              <v-btn v-if="player.state === 'blocked'" disabled >
                {{ player.key }}
              </v-btn>
              <v-btn v-if="player.state === 'available'"
                     @click="allocate(game.key, player.key, user)" >
                {{ player.key }}
              </v-btn>
              &ensp;
            </span>
            <v-btn v-if="isGameReady(game)"
                   :style="{background: mineColor}"
                   @click="$router.push({query: {gid: game.key}})" >
              Launch
            </v-btn>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-row>
      <v-col cols md="12">
        <v-btn @click="whichNewGameControls = 'draughts'">Create another</v-btn>
      </v-col>
    </v-row>

    <p id="cookie-notice">
      <svg viewBox="0 0 24 24">
        <path fill="currentColor" d="M16 14.5C16 15.3 15.3 16 14.5 16S13 15.3 13 14.5 13.7 13 14.5 13C15.3 13 16 13.7 16 14.5M18.9 10.5C19 11 19 11.5 19 12C19 17 15 21 10 21S1 17 1 12 5 3 10 3C10 3 11 3 11 4V6H12C12 6 13 6 13 7V8H15C15 8 16 8 16 9V10H18C18 10 18.6 10 18.9 10.5M17 12H15.5C14.7 12 14 11.3 14 10.5V10H12.5C11.7 10 11 9.3 11 8.5V8H10.5C9.7 8 9 7.3 9 6.5V5.1C5.9 5.5 3.5 8 3.1 11.1C3.2 10.5 3.8 10 4.5 10C5.3 10 6 10.7 6 11.5S5.3 13 4.5 13C3.7 13 3.1 12.4 3 11.6C3 12.1 3 12.6 3.1 13.1C3.5 15.8 5.6 18 8.2 18.7C7.8 18.4 7.5 18 7.5 17.5C7.5 16.7 8.2 16 9 16S10.5 16.7 10.5 17.5C10.5 18.2 10 18.8 9.3 19C13.5 19.4 17 16.1 17 12M9.5 11C8.7 11 8 11.7 8 12.5S8.7 14 9.5 14 11 13.3 11 12.5 10.3 11 9.5 11M9 7.5C9 6.7 8.3 6 7.5 6S6 6.7 6 7.5 6.7 9 7.5 9 9 8.3 9 7.5M21 17H23V15H21V17M21 7V13H23V7H21Z" />
      </svg>
      Cookie notice: This part of the site will store one single solitary cookie in your browser so that it can actually remember what player you are. If you don't like that, delete it from your browser, close the page, and never come back.
    </p>

    <p style="{font-size: small}">
      Debug: you are {{ user }}
    </p>

    <v-overlay opacity="0.975" :value="whichNewGameControls === 'draughts'">
      <newGameControls :stompClient="stompClient" />
      <v-btn @click="whichNewGameControls = null">Cancel</v-btn>
    </v-overlay>

  </v-container>
</template>
<script>
import cookies from 'vue-cookies';
import newGameControls from '../components/newGameControls.vue';
import SockJS from 'sockjs-client';
import Stomp from 'stompjs';

const uid = (function () {
  const uidKey = 'game.lobby.uid';
  let maybeUid = cookies.get(uidKey);
  if (!maybeUid) {
    maybeUid = Math.round(Math.random() * 1147483648) + 1000000000;
    cookies.set(uidKey, maybeUid);
  }
  return maybeUid;
})()

export default {
  name: 'lobby',
  components: {newGameControls},

  data: () => ({
    games: [],
    mineColor: '#117744',
    stompClient: null,
    whichNewGameControls: null
  }),

  methods: {

    connect () {
      let socket = new SockJS(this.$backendRoot + '/stomp');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe('/lobby/state', this.receiveState);
      });

      // Poor man's callback chain
      let stompOnClose = socket.onclose;
      socket.onclose = status => {
        stompOnClose(status);
        this.stompClient = null;
      }
    },

    receiveState (message) {
      const state = JSON.parse(message.body);
      this.games = Object.keys(state)
        .sort()
        .map(k => {
          const allocation = state[k];
          return {
            key: allocation.key,
            name: allocation.name,
            players: Object.keys(allocation.allocated).concat(allocation.unallocated)
              .sort()
              .map(key => ({key, state: allocation.allocated[key] === uid && 'mine' || allocation.allocated[key] && 'blocked' || 'available' }))
          };
        });
    },

    create () {
      this.stompClient.send("/lobby/lobby/create", {}, 'aaa');
    },

    allocate (game, player, user) {
      this.stompClient.send("/lobby/allocate", {}, JSON.stringify([game, player, user]));
    },

    deallocate (game, player, user) {
      this.stompClient.send("/lobby/deallocate", {}, JSON.stringify([game, player, user]));
    },

    isGameReady(game) {
      function countPlayersWithState(game, state) {
        return game.players.filter(player => player.state === state).length;
      }

      return countPlayersWithState(game, 'mine') > 0 && countPlayersWithState(game, 'available') === 0;
    }
  },

  computed: {
    user () {
      return uid;
    }
  },

  mounted () {
    this.connect();
  }
}
</script>
<style>
#cookie-notice {
	max-width: 768px;
	font-size: 10px;
	text-align: justify;
	margin: 0 auto;
	padding: 30px;
}

#cookie-notice>svg {
	width: 48px;
	height: 52px;
	float: left;
	margin-right: 12px;
}
</style>