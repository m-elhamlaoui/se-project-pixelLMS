<template>
  <v-container v-if="!loading">
    <!-- Greeting Header -->
    <v-row>
      <h2>Welcome, {{ username }}</h2><br>
      <v-divider></v-divider><br>
      <h3>Continue your work :</h3>
    </v-row>

    <!-- Filter Controls -->
    <v-row class="my-4">
      <v-col>
        <v-text-field
          v-model="filters.title"
          label="Filter by title"
          solo
        ></v-text-field>
      </v-col>
    </v-row>
    
    <!-- Cards -->
    <v-row v-if="courses.length > 0">
      <v-col v-for="course in sortedFilteredCourses" :key="course.courseid" cols="12" md="4">
        <DashboardCard :data="course" :background="true" class="card-hover" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import DashboardCard from '@/components/DashboardCard.vue';
import { getUser } from '@/modules/auth';
import { fetchEngagedCourses } from '@/modules/data/course';

export default {
  components: {
    DashboardCard,
  },
  data() {
    return {
      username: '',
      courses: [],
      filters: {
        title: '', // Filter by title only
      },
      loading: true,
    };
  },
  computed: {
    filteredCourses() {
      return this.courses.filter(course => {
        // Only filter by title
        return this.filters.title ? course.title.toLowerCase().includes(this.filters.title.toLowerCase()) : true;
      });
    },
    sortedFilteredCourses() {
      return this.filteredCourses.sort((a, b) => {
        const statusOrder = ['Non Commencée', 'En Cours', 'Terminée']; 
        return statusOrder.indexOf(a.status) - statusOrder.indexOf(b.status);
      });
    }
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString();
    },
    truncateDescription(description) {
      return description.length > 50 ? description.substring(0, 50) + '...' : description;
    }
  },
  async created() {
    const user = await getUser();
    if (user == null) { return; }

    this.username = user.name;
    const courses = await fetchEngagedCourses(user.userid);

    this.courses = courses;
    this.loading = false;
  }
};

</script>

<style>
.green-avatar {
  background-color: #4caf50; /* Green color */
  color: white;
}

.card-hover {
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.card-hover:hover {
  transform: scale(1.05);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

.course-description {
  font-size: 14px;
  line-height: 1.5;
}


</style>
