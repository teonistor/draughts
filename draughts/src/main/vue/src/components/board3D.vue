<!--suppress HtmlUnknownTag, CheckEmptyScriptTag, HtmlUnknownAttribute -->
<template>
  <svg :height="pixelHeight"
       :width="pixelWidth"
       :style="{transform: whiteOnTop && 'scaleX(-1)' || 'scaleY(-1)'}" >

    <g v-for="(ignore1,y) in height"  >
      <g v-for="(ignore2,x) in width">

        <rect :x="2 + x * boxSize"
              :y="2 + y * boxSize"
              :height="boxSize" :width="boxSize"
              style="fill:rgba(100,100,100,100);stroke-width:3;stroke:rgb(200,200,200)" />

        <rect v-for="(ignore3,z) in depth"
              :class="styleAt(z,x,y)"
              :x="2 + x * boxSize + hStart + z * hGap"
              :y="2 + y * boxSize + vStart + z * vGap"
              rx="20" ry="20"
              height="40" width="40"
              @click="$emit('selectOnBoard', z,x,y)" />

      </g>
    </g>
  </svg>
</template>
<script>
  const pieceToStyle = {
    'BK': 'hoverable black king',
    'BP': 'hoverable black peon',
    'WK': 'hoverable white king',
    'WP': 'hoverable white peon'};

  export default {
    name: 'board3D',
    props: ['depth', 'width', 'height', 'data', 'parity', 'selected', 'highlighted', 'whiteOnTop'],

    data: () => ({
      hStart: 5,
    }),

    computed: {
      pixelHeight () {
        return this.height * this.boxSize + 10;
      },

      pixelWidth () {
        return this.width * this.boxSize + 10;
      },

      boxSize () {
        return (this.depth < 7) && 100 || (40 + this.depth * 10);
      },

      hGap () {
        return (this.depth > 5) && 10 || (this.depth > 1) && (50 / (this.depth - 1)) || 0;
      },

      vGap () {
        return this.hGap / 2;
      },

      vStart () {
        return (this.boxSize - (this.depth - 1) * this.vGap) / 2 - 20;
      }
    },

    methods: {
      styleAt (z,x,y) {
        if ((z+x+y + this.parity) % 2)
          return 'unreachable';

        const zxy = [z,x,y].join(',');
        const maybeSelected  = this.selected === zxy && ' selected' || '';
        const maybeHighlight = this.highlighted[zxy] && ' highlight' || '';

        const piece = this.data[zxy];
        return (piece && pieceToStyle[piece] || 'hoverable') + maybeSelected + maybeHighlight;
      }
    },

    watch: {
      pixelHeight (value) {
        this.$emit('pixelHeight', value);
      },

      pixelWidth (value) {
        this.$emit('pixelWidth', value);
      }
    },

    mounted () {
      // The very first values on object creation don't get emitted
      setTimeout(() => {
        this.$emit('pixelHeight', this.pixelHeight);
        this.$emit('pixelWidth', this.pixelWidth);
      }, 500);
    }
  }
</script>
<style>
  .unreachable {
    stroke-width:3;
    stroke:rgba(150,150,150,0.3);
    fill: rgb(150,150,150);
  }
  .hoverable {
    stroke-width:3;
    cursor:pointer;
    fill: blue;
  }
  .hoverable.highlight {
    fill: cyan;
  }
  .hoverable.black {
    fill: black;
  }
  .hoverable.white {
    fill: white;
  }
  .hoverable.king {
    stroke:rgb(255,150,10);
  }
  .hoverable.peon {
    stroke:rgb(200,200,200);
  }
  .hoverable.black.highlight {
    fill: #003311;
  }
  .hoverable.white.highlight {
    fill: #ccffdd;
  }
  .hoverable:hover, .hoverable.black:hover, .hoverable.white:hover {
    fill: red;
  }
  .hoverable.selected, .unreachable.selected {
    stroke-width: 7;
  }
</style>