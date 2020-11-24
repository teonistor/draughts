new Vue({
  el: '#app',
  data: () => ({

    stompClient: null,

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
      this.rows = JSON.parse(message.body);
    },

    send (i, j) {
      this.stompClient.send("/ttt/click", {}, JSON.stringify([i, j]));
    }
  },

  mounted () {
    this.connect();
  }
});
