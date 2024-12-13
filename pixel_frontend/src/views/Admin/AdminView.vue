<template>
  <v-app>
    <v-container>
      <!-- Header with User Role -->
      <h2 class="user-role">
        Vous êtes un(e) {{ userRole }}
      </h2>

      <v-divider class="mb-10"></v-divider>

      <!-- Vuetify Tabs -->
      <v-card>
        <v-tabs v-model="tab" align-tabs="start" color="primary">
          <v-tab :value="1">Utilisateurs</v-tab>
          <v-tab :value="2">Projets</v-tab>
        </v-tabs>

        <v-tabs-window v-model="tab">
          <v-tabs-window-item :value="1">
            <UsersListing :users="users" />
          </v-tabs-window-item>

          <v-tabs-window-item :value="2">
            <ProjectsListing :projects="projects" />
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card>

      <!-- Error Message Display -->
      <v-snackbar v-model="errorSnackbar" color="red">
        {{ errorMessage }}
        <v-btn color="white" text @click="errorSnackbar = false">Fermer</v-btn>
      </v-snackbar>
    </v-container>
  </v-app>
</template>

<script>
import { getUser } from '@/modules/auth';
import { getUsersInProject, getAllUsers } from '@/modules/data/user';
import { fetchSuperviseProjects, getAllProjects } from '@/modules/data/project';

import UsersListing from './UsersListing.vue';
import ProjectsListing from './ProjectsListing.vue';

export default {
  components: {
    UsersListing, ProjectsListing
  },
  data() {
    return {
      userRole: 'supervisor', 
      tab: 1,
      users: [],
      projects: [],
      errorSnackbar: false,
      errorMessage: ''
    };
  },
  async created() {
    try {
      const user = await getUser();
      this.userRole = user.role;
      

      if (user.role.toLowerCase() === 'superadmin') {
        this.projects = await getAllProjects();
        this.users = await getAllUsers();
      } else {
        this.projects = await fetchSuperviseProjects(user.userid);
        const allUsers = [];
        for (const project of this.projects) {
          const usersInProject = await getUsersInProject(project.projectid);
          allUsers.push(...usersInProject);
        }
        this.users = Array.from(new Map(allUsers.map(user => [user.userid, user])).values());
      }
    } catch (error) {
      this.errorMessage = 'Une erreur s\'est produite : ' + error.message;
      this.errorSnackbar = true;
    }
  }
};
</script>

<style scoped>
.user-role {
  margin-bottom: 20px;
}
</style>
