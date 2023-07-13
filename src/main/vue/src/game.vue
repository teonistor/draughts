<!--suppress HtmlUnknownTag, HtmlUnknownAttribute, CheckEmptyScriptTag -->
<template>
  <v-app>
    <v-container>
      <v-row>
        <v-col md="4" v-for="partialIndex in partialIndices">

          <p>Board at {{ JSON.stringify(partialIndex) }}</p>

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
  import board3D from './components/board3D.vue';

  const testBoard = '[[[1,2,3,4],"BP"],[[0,1,0,3],"BP"],[[0,0,2,4],"BP"],[[1,0,0,3],"BK"],[[1,2,0,3],"BP"],[[0,0,3,3],"BP"],[[1,1,3,3],"BP"],[[1,0,3,4],"BP"],[[1,2,3,0],"WP"],[[1,1,3,1],"WK"],[[0,0,1,1],"WP"],[[0,1,2,3],"BP"],[[1,1,2,4],"BP"],[[1,1,0,4],"BP"],[[0,2,2,4],"BP"],[[0,0,1,3],"BP"],[[1,0,1,4],"BP"],[[1,1,0,0],"WP"],[[0,2,2,0],"WP"],[[1,0,1,0],"WP"],[[0,1,1,4],"BP"],[[0,0,0,4],"BP"],[[0,1,3,0],"WP"],[[0,0,0,0],"WP"],[[0,2,0,0],"WP"],[[0,2,0,4],"BP"],[[1,2,2,3],"BP"],[[1,2,1,4],"BP"],[[1,0,2,3],"BP"],[[1,2,0,1],"WP"],[[0,2,1,1],"WP"],[[0,0,3,1],"WP"],[[0,1,1,0],"WP"],[[1,0,2,1],"WP"],[[1,0,0,1],"WP"],[[0,0,2,0],"WP"],[[0,1,3,4],"BP"],[[1,1,1,1],"WP"],[[1,0,3,0],"WP"],[[0,2,3,1],"WP"],[[0,2,3,3],"BP"],[[1,2,1,0],"WP"],[[1,2,2,1],"WP"],[[1,1,1,3],"BP"],[[1,1,2,0],"WP"],[[0,2,1,3],"BP"],[[0,1,2,1],"WP"],[[0,1,0,1],"WP"]]';
  const testDimensions = [2, 3, 4, 5];

  export default {
    name: 'game',
    components: {board3D},

    data: () => ({
      board: JSON.parse(testBoard),
      partialIndices: [[0], [1]]
    }),

    computed: {

    },

    methods: {
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
      }
    },

    mounted () {

    }
  }
</script>