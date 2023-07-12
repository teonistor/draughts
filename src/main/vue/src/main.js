import Vue from 'vue';
import vuetify from './plugins/vuetify';
import 'roboto-fontface/css/roboto/roboto-fontface.css';
import '@mdi/font/css/materialdesignicons.css';

import game from './game.vue';

new Vue({
  vuetify,
  render: h => h(game)
}).$mount('#app')
