new Vue({
  el: '#app',
  data: () => ({

    stompClient: null,

    offsetI: 0,
    offsetJ: 0,
    rows: [""],
    player: '',
    winner: ''
  }),

  methods: {

    connect () {
      let socket = new SockJS('/ttt-subscribe');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, frame => {
        this.stompClient.subscribe('/ttt/board', this.receiveBoard);
        this.stompClient.subscribe('/ttt/winner', this.receiveWinner);
      });

      // Poor man's callback chain
      let stompOnClose = socket.onclose;
      socket.onclose = status => {
        stompOnClose(status);
        this.stompClient = null;
      }
    },

    receiveBoard (message) {
      let data = JSON.parse(message.body);
      this.offsetI = data[0];
      this.offsetJ = data[1];
      this.rows = data[2];
      this.player = data[3];
    },

    receiveWinner (message) {
      this.winner = message.body;
    },

    send (i, j) {
      this.stompClient.send("/ttt/click", {}, JSON.stringify([i + this.offsetI, j + this.offsetJ]));
    }
  },

  mounted () {
    this.connect();
  }
});
