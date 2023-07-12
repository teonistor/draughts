<!--suppress HtmlUnknownTag, CheckEmptyScriptTag, HtmlUnknownAttribute -->
<template>
  <svg :height="height * boxSize + 4"
       :width="width * boxSize + 4" >

    <g v-for="(ignore1,y) in height"  >
      <g v-for="(ignore2,x) in width">

        <rect :x="2 + x * boxSize"
              :y="2 + y * boxSize"
              :height="boxSize" :width="boxSize"
              style="fill:rgba(100,100,100,100);stroke-width:3;stroke:rgb(200,200,200)" />

        <rect v-for="(ignore3,z) in depth"
              :class="styleAt(z,x,y)"
              :x="2 + x * boxSize + hStart + z * hGap"
              :y="2 + y * boxSize + vStart - z * vGap"
              rx="20" ry="20"
              height="40" width="40" />

      </g>
    </g>
  </svg>
</template>
<script>
  export default {
    name: 'board3D',
    props: ['depth', 'width', 'height', 'data'],

    data: () => ({
      hStart: 5,
    }),

    computed: {
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
        return (this.boxSize + (this.depth - 1) * this.vGap) / 2 - 20;
      }
    },

    methods: {
      styleAt (z,x,y) {
        const piece = this.data[[z,x,y].join(',')];
        if (!piece)
          return 'hoverable';
        else if (piece[0] === 'B')
          return 'hoverable black';
        else
          return 'hoverable white';
      }
    },

    mounted () {

    }
  }
</script>
<style>
  .hoverable {
    stroke-width:3;
    stroke:rgb(200,200,200);
    cursor:pointer;
    fill: blue;
  }
  .hoverable.black {
    fill: black;
  }
  .hoverable.white {
    fill: white;
  }
  .hoverable:hover, .hoverable.black:hover, .hoverable.white:hover {
    fill: red;
  }
</style>