<template>
  <v-card class="mx-auto my-4" min-width="200" max-width="344">
    <v-img v-if="background"
      height="150px"
      :style="{ backgroundColor: cardBackgroundColor}"
      cover
    ></v-img>

    <v-card-title>
      {{ data.title }}
    </v-card-title>

    <v-card-subtitle>
      Encadrant : {{ data.supervisor }}
    </v-card-subtitle>

    <v-card-actions>
      <v-btn v-if="background"
        class="custom-button"
        @click="openProject"
      >
        Sélectionner
      </v-btn>

      <v-spacer></v-spacer>

      <v-btn
        :icon="show ? 'mdi-chevron-up' : 'mdi-chevron-down'"
        @click="show = !show"
      ></v-btn>
    </v-card-actions>

    <v-expand-transition>
      <v-card-text v-show="show">
        <div>
          <strong>Statut :</strong> {{ data.status }}
        </div>
        <div>
          <strong>Date de début :</strong> {{ data.startDate }}
        </div>
        <div>
          <strong>Date de fin :</strong> {{ data.endDate }}
        </div>
        <div class="mt-2">
          <strong>Description :</strong> {{ truncatedDescription }}
        </div>
      </v-card-text>
    </v-expand-transition>
  </v-card>
</template>

<script>
export default {
  props: ['data', 'background'],
  data() {
    return {
      show: false,
      cardBackgroundColor: this.getDarkerColor(),
    };
  },
  computed: {
    truncatedDescription() {
      return this.data.description.length > 50
        ? this.data.description.substring(0, 50) + '...'
        : this.data.description;
    },
  },
  methods: {
    openProject() {
      this.$router.push("/project/" + this.data.projectid);
    },
    getDarkerColor() {
      const randomColor = '#' + Math.floor(Math.random() * 16777215).toString(16);
      return this.darkenColor(randomColor, 80); 
    },
    darkenColor(color, percent) {
      let r = parseInt(color.slice(1, 3), 16);
      let g = parseInt(color.slice(3, 5), 16);
      let b = parseInt(color.slice(5, 7), 16);
      
      r = Math.max(0, Math.min(255, r - percent));
      g = Math.max(0, Math.min(255, g - percent));
      b = Math.max(0, Math.min(255, b - percent));
      
      return `#${this.toHex(r)}${this.toHex(g)}${this.toHex(b)}`;
    },
    toHex(value) {
      let hex = value.toString(16);
      return hex.length === 1 ? '0' + hex : hex;
    }
  },
};
</script>

<style scoped>
.v-card {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.v-card:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.custom-button {
  background-color: #1976D2; /* Couleur bleue */
  color: white;
  text-transform: none; /* Empêche le texte en majuscules */
}

.custom-button:hover {
  background-color: #1565C0; /* Bleu plus foncé au survol */
}
</style>
