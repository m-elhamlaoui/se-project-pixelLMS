<template>
    <div v-if="selectedEvent">
      <h2>{{ selectedEvent.name }}</h2>
      <p><strong>Start:</strong> {{ selectedEvent.start.toLocaleString() }}</p>
      <p><strong>End:</strong> {{ selectedEvent.end.toLocaleString() }}</p>
      <p><strong>Description:</strong> {{ selectedEvent.description }}</p>
    </div>
    <div v-else>
      <p>No event selected.</p>
    </div>
  </template>
  
  <script>
  export default {
    props: {
      selectedEvent: {
        type: Object,
        default: null,
      },
    },
  };
  </script>
  
  <style scoped>
  /* Your styles here */
  </style>
  