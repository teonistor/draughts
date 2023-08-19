<!--suppress HtmlUnknownTag, CheckEmptyScriptTag, HtmlUnknownAttribute -->
<template>
  <div :style="boxStyle">
    <span v-for="y in metaHeight">
      <span v-for="(ignore,x) in metaWidth">

        <board3D :depth="boardDepth"
                 :width="boardWidth"
                 :height="boardHeight"
                 :data="data[x + ',' + (metaHeight - y)]"
                 :parity="(parity + x + metaHeight - y) % 2"
                 :selected="selected[x + ',' + (metaHeight - y)] || {}"
                 :highlighted="highlighted[x + ',' + (metaHeight - y)] || {}"
                 :whiteOnTop="whiteOnTop"
                 :noprop="debug(x + ',' + (metaHeight - y))"
                 @selectOnBoard="onSelect(x, (metaHeight - y), ...arguments)"
                 @pixelWidth="pixelWidth = arguments[0]"
                 @pixelHeight="pixelHeight = arguments[0]" />
      </span>
      <br>
    </span>
  </div>
</template>
<script>
  import board3D from './board3D.vue';

  export default {
    name: 'metaBoard',
    components: {board3D},
    props: ['metaWidth', 'metaHeight', 'boardDepth', 'boardWidth', 'boardHeight', 'data', 'parity', 'selected', 'highlighted', 'whiteOnTop'],

    data: () => ({
      pixelWidth: 10,
      pixelHeight: 10
    }),

    computed: {
      boxStyle() {
        return {
          border: '3px #eeeeee solid',
          width: this.pixelWidth * this.metaWidth + 20 + 'px',
          height: this.pixelHeight * this.metaHeight + 25 + 'px',
          'padding-left': '5px'
        };
      }
    },

    methods: {
      debug() {
        console.log('A', arguments);
        return 2;
      },

      onSelect(mx, my, bd, bx, by) {
        this.$emit('select', mx, my, bd, bx, by);
      }
    }
  }
</script>