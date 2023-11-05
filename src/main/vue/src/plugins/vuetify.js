import Vue from 'vue';
import Vuetify from 'vuetify';
import 'vuetify/dist/vuetify.min.css';

Vue.use(Vuetify);

export default new Vuetify({
  theme: {
    dark: true,

    themes: {
      light: {
        primary: '#ca007a'
      },

      dark: {
        primary: '#ee44aa'
      }
    }
  }
});
