<template>
  <div class="calendar-container">
    <div class="calendar">
      <div class="calendar-header">
        <button @click="prevMonth" class="nav-button">« Précédent</button>
        <span class="month-year">{{ currentMonthName }} {{ currentYear }}</span>
        <button @click="nextMonth" class="nav-button">Suivant »</button>
      </div>
      <div class="calendar-grid">
        <div v-for="day in daysInMonth" :key="day.date" class="calendar-day">
          <div class="day-number">{{ day.day }}</div>
          <div v-if="day.events.length > 0" class="events-list">
            <ul>
              <li class="event-title" v-for="event in day.events" @click="onEventClick(event)" :key="event.eventnumber">{{ event.title }}</li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <v-snackbar
      v-model="snackbar.visible"
      :color="snackbar.color"
      :timeout="snackbar.timeout"
      top
    >
      {{ snackbar.message }}
      <v-btn
        color="white"
        @click="snackbar.visible = false"
        text
      >
        Fermer
      </v-btn>
    </v-snackbar>

    <v-dialog v-model="deleteDialogVisible" max-width="600">
      <v-card>
        <v-card-title class="headline">Confirmation</v-card-title>
        <v-card-text>Êtes-vous sûr de vouloir supprimer cet événement ?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="deleteDialogVisible = false">Annuler</v-btn>
          <v-btn color="error" text @click="confirmDelete">Supprimer</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <div v-if="selectedEvent" class="event-details">
      <v-row>
        <v-col col="auto">
          <h2>{{ selectedEvent.title }}</h2>
        </v-col>
        <v-spacer></v-spacer>
        <v-col col="auto">
          <v-icon @click="openDeleteDialog(selectedEvent.eventnumber)" color="error">mdi-delete</v-icon>
        </v-col>
      </v-row>
      <p><strong>Projet :</strong> {{ selectedEvent.projectTitle }}</p>
      <p><strong>Description :</strong> {{ selectedEvent.description }}</p>
      <p><strong>Heure de début :</strong> {{ formatDate(selectedEvent.starttime) }}</p>
      <p><strong>Heure de fin :</strong> {{ formatDate(selectedEvent.endtime) }}</p>
    </div>
  </div>
</template>

<script>
import { getUser } from '@/modules/auth';
import { getEventsbyUserId, deleteEvent } from '@/modules/data/calendar';

export default {
  data() {
    return {
      currentMonth: new Date().getMonth(),
      currentYear: new Date().getFullYear(),
      events: [],
      selectedEvent: null,
      deleteDialogVisible: false,
      eventToDelete: null,
      snackbar: {
        visible: false,
        message: '',
        color: 'success',
        timeout: 3000
      }
    };
  },
  computed: {
    currentMonthName() {
      return new Intl.DateTimeFormat('fr-FR', { month: 'long' }).format(new Date(this.currentYear, this.currentMonth));
    },
    daysInMonth() {
      const days = [];
      const date = new Date(this.currentYear, this.currentMonth, 1);
      while (date.getMonth() === this.currentMonth) {
        days.push({
          date: new Date(date),
          day: date.getDate(),
          events: this.events.filter(event => {
            const eventDate = new Date(event.starttime);
            return eventDate.getDate() === date.getDate() &&
              eventDate.getMonth() === this.currentMonth &&
              eventDate.getFullYear() === this.currentYear;
          })
        });
        date.setDate(date.getDate() + 1);
      }
      return days;
    }
  },
  methods: {
    async fetchEvents() {
      try {
        const user = await getUser();
        const events = await getEventsbyUserId(user.userid);
        this.events = events;
      } catch (error) {
        console.error('Erreur lors de la récupération des événements :', error);
      }
    },
    prevMonth() {
      if (this.currentMonth === 0) {
        this.currentMonth = 11;
        this.currentYear--;
      } else {
        this.currentMonth--;
      }
      this.fetchEvents();
    },
    nextMonth() {
      if (this.currentMonth === 11) {
        this.currentMonth = 0;
        this.currentYear++;
      } else {
        this.currentMonth++;
      }
      this.fetchEvents();
    },
    formatDate(date) {
      return new Date(date).toLocaleString('fr-FR', { hour: '2-digit', minute: '2-digit' });
    },
    onEventClick(event) {
      this.selectedEvent = event;
    },
    openDeleteDialog(eventnumber) {
      this.deleteDialogVisible = true;
      this.eventToDelete = eventnumber;
    },
    async confirmDelete() {
      if (this.eventToDelete) {
        try {
          await deleteEvent(this.eventToDelete);
          this.selectedEvent = null;
          this.eventToDelete = null;
          this.deleteDialogVisible = false;
          window.location.reload();
        } catch (error) {
          console.error('Erreur lors de la suppression de l\'événement :', error);
          this.snackbar.message = 'Erreur lors de la suppression de l\'événement';
          this.snackbar.color = 'error';
          this.snackbar.visible = true;
          this.deleteDialogVisible = false;
        }
      }
    }
  },
  async created() {
    await this.fetchEvents();
  }
};
</script>

<style scoped>
.calendar-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
  padding: 20px;
}

.calendar {
  display: flex;
  flex-direction: column;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
  color: #ecf0f1;
}

.calendar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  background: #1e1e2f;
  border-bottom: 1px solid #34495e;
}

.nav-button {
  background: none;
  border: 2px solid #F2B746;
  color: #F2B746;
  cursor: pointer;
  font-size: 1em;
  padding: 10px;
  border-radius: 8px;
  transition: background-color 0.3s, color 0.3s;
}

.nav-button:hover {
  background-color: #F2B746;
  color: #1e1e2f;
}

.month-year {
  flex: 1;
  text-align: center;
  font-size: 1.5em;
  font-weight: bold;
}

.calendar-grid {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
}

.calendar-day {
  border: 1px solid #e3e3e3;
  padding: 10px;
  text-align: center;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.3s;
}

.day-number {
  font-size: 1.3em;
  font-weight: bold;
  color: #1e1e2f;
}

.events-list {
  margin-top: 5px;
}

.events-list ul {
  list-style-type: none;
  padding: 0;
  margin: 0;
  max-height: 120px;
  overflow-y: auto;
}

.event-title {
  margin: 0;
  font-size: 0.9em;
  color: #1e1e2f;
  background-color: #F2B746;
  padding: 5px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.event-title:hover {
  background-color: #F4A300;
}

.event-details {
  padding: 20px;
  background: #1e1e2f;
  border-top: 1px solid #34495e;
  box-shadow: 0 -4px 8px rgba(0, 0, 0, 0.2);
  color: #ecf0f1;
}

@media (max-width: 768px) {
  .calendar-grid {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 480px) {
  .calendar-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}
</style>
