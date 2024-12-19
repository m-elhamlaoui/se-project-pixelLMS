<template>
  <div class="calendar-container">
    <div class="calendar">
      <div class="calendar-header">
        <button @click="prevMonth" class="nav-button">« Previous</button>
        <span class="month-year">{{ currentMonthName }} {{ currentYear }}</span>
        <button @click="nextMonth" class="nav-button">Next »</button>
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
        Close
      </v-btn>
    </v-snackbar>

    <v-dialog v-model="deleteDialogVisible" max-width="600">
      <v-card>
        <v-card-title class="headline">Confirm</v-card-title>
        <v-card-text>Are you sure you want to delete this event?</v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn text @click="deleteDialogVisible = false">Cancel</v-btn>
          <v-btn color="error" text @click="confirmDelete">Remove</v-btn>
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
      <p><strong>Course :</strong> {{ selectedEvent.courseTitle }}</p>
      <p><strong>Description :</strong> {{ selectedEvent.description }}</p>
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
      return new Intl.DateTimeFormat('en-US', { month: 'long' }).format(new Date(this.currentYear, this.currentMonth));
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
        console.error('Error while retrieving the events:', error);
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
      return new Date(date).toLocaleString('en-US', { hour: '2-digit', minute: '2-digit' });
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
          console.error('Error while deleting the event:', error);
          this.snackbar.message = 'Error while deleting the event.';
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
  background-image: url('@/assets/background.png');
  border-bottom: 1px solid rgba(0, 0, 0, 0.2);
}

.nav-button {
  background: rgba(0, 0, 0, 0.2);
  color: #ffffff;
  cursor: pointer;
  font-size: 1em;
  padding: 10px;
  border-radius: 8px;
  transition: background-color 0.3s, color 0.3s;
}

.nav-button:hover {
  background-color: #faf5f5;
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
  color: #f7f7fc;
  background-color: #684911;
  padding: 5px;
  border-radius: 6px;
  transition: background-color 0.3s;
}

.event-title:hover {
  background-color: #F4A300;
}

.event-details {
  padding: 20px;
  background: #31b0c7;
  border-top: 1px solid #ffffff;
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
