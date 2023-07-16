const localDevelopmentPort = '8090';
const ipV4Address = /^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$/;

import Vue from 'vue';
import vuetify from './plugins/vuetify';
import 'roboto-fontface/css/roboto/roboto-fontface.css';
import '@mdi/font/css/materialdesignicons.css';

import game from './game.vue';


if (location.hostname === 'localhost') {
  console.log('Superseding absolute path to backend because running on localhost');
  Vue.prototype.$backendRoot = 'http://localhost:' + localDevelopmentPort;

} else if (ipV4Address.test(location.hostname)) {
  console.log('Superseding absolute path to backend due to access by IP address');
  Vue.prototype.$backendRoot = 'http://' + location.hostname + ':' + localDevelopmentPort;

} else if (location.hostname.indexOf('herokuapp.com') >= 0) {
  Vue.prototype.$backendRoot = '';

} else {
  Vue.prototype.$backendRoot = 'https://teonistor.herokuapp.com';

}

Vue.prototype.$localDevelopmentPort = localDevelopmentPort

new Vue({
  vuetify,
  render: h => h(game)
}).$mount('#app')
