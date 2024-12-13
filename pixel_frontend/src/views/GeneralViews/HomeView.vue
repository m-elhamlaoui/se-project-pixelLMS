<template>
  <v-container v-if="!loading">
    <!-- Greeting Header -->
    <v-row>
      <h2>Bonjour, {{ username }}</h2><br>
      <v-divider></v-divider><br>
      <h3>Continuez votre travail :</h3>
    </v-row>

    <!-- Filter Controls -->
    <v-row class="my-4">
      <v-col>
        <v-text-field
          v-model="filters.title"
          label="Filtrer par titre"
          solo
        ></v-text-field>
      </v-col>
      <v-col>
        <v-select
          v-model="filters.status"
          :items="statusOptions"
          label="Filtrer par statut"
          solo
        ></v-select>
      </v-col>
      <v-col>
        <v-select
          v-model="filters.startYear"
          :items="yearOptions"
          label="Année de début"
          solo
        ></v-select>
      </v-col>
    </v-row>
    
    <!-- Cards -->
    <v-row v-if="projects.length > 0">
      <v-col v-for="project in sortedFilteredProjects" :key="project.projectid" cols="12" md="4">
        <DashboardCard :data="project" :background="true" class="card-hover" />
      </v-col>
    </v-row>
  </v-container>
</template>

<script>
import DashboardCard from '@/components/DashboardCard.vue';
import { getUser } from '@/modules/auth';
import { fetchEngagedProjects } from '@/modules/data/project';

export default {
  components: {
    DashboardCard,
  },
  data() {
    return {
      username: '',
      projects: [],
      filters: {
        title: '',
        status: 'Tous',
        startYear: 'Tous',
      },
      statusOptions: ['Tous'],
      loading: true,
    };
  },
  computed: {
    filteredProjects() {
      return this.projects.filter(project => {
        const startYearMatch = this.filters.startYear !== 'Tous' ? new Date(project.startdate).getFullYear() == this.filters.startYear : true;
        return (
          (this.filters.title ? project.title.toLowerCase().includes(this.filters.title.toLowerCase()) : true) &&
          (this.filters.status !== 'Tous' ? project.status === this.filters.status : true) &&
          startYearMatch
        );
      });
    },
    sortedFilteredProjects() {
      return this.filteredProjects.sort((a, b) => {
        const statusOrder = ['Non Commencée', 'En Cours', 'Terminée']; 
        return statusOrder.indexOf(a.status) - statusOrder.indexOf(b.status);
      });
    },
    yearOptions() {
      const currentYear = new Date().getFullYear();
      const years = ['Tous'];
      for (let year = currentYear; year >= 2020; year--) {
        years.push(year);
      }
      return years;
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
    const projects = await fetchEngagedProjects(user.userid);

    const allstatus = new Set();
    for (let i = 0; i < projects.length; i++) {
      allstatus.add(projects[i].status);
      projects[i].startDate = this.formatDate(projects[i].startdate);
      projects[i].endDate = this.formatDate(projects[i].enddate);
    }
    this.statusOptions.push(...allstatus);

    this.projects = projects;
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

.project-description {
  font-size: 14px;
  line-height: 1.5;
}


</style>
