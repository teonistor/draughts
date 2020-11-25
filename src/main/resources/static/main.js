new Vue({
  el: '#app',
  data: () => ({

    stompClient: null,

    offsetI: 0,
    offsetJ: 0,

    rows: ["     ","     ","     ","     ","     "]
  }),

  methods: {

    connect () {
      let socket = new SockJS('/ttt-subscribe');
      this.stompClient = Stomp.over(socket);
      this.stompClient.connect({}, frame => {
        console.log('Connected: ' + frame);
        this.stompClient.subscribe('/ttt/board', this.receive);
      });
    },

    receive (message) {
      let data = JSON.parse(message.body);
      this.offsetI = data[0];
      this.offsetJ = data[1];
      this.rows = data[2];
    },

    send (i, j) {
      this.stompClient.send("/ttt/click", {}, JSON.stringify([i + this.offsetI, j + this.offsetJ]));
    }
  },

  mounted () {
    this.connect();
  }
});
