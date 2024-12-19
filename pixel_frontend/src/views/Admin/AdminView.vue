<template>
  <v-app>
    <v-container>
      <!-- Header with User Role -->
      <h2 class="user-role">
        You are a {{ userRole }}
      </h2>

      <v-divider class="mb-10"></v-divider>

      <!-- Vuetify Tabs -->
      <v-card>
        <v-tabs v-model="tab" align-tabs="start" color="primary">
          <v-tab :value="1">Users</v-tab>
          <v-tab :value="2">Courses</v-tab>
        </v-tabs>

        <v-tabs-window v-model="tab">
          <v-tabs-window-item :value="1">
            <UsersListing :users="users" />
          </v-tabs-window-item>

          <v-tabs-window-item :value="2">
            <CoursesListing :courses="courses" />
          </v-tabs-window-item>
        </v-tabs-window>
      </v-card>

      <!-- Error Message Display -->
      <v-snackbar v-model="errorSnackbar" color="red">
        {{ errorMessage }}
        <v-btn color="white" text @click="errorSnackbar = false">Close</v-btn>
      </v-snackbar>
    </v-container>
  </v-app>
</template>

<script>
import { getUser } from '@/modules/auth';
import { getUsersInCourse, getAllUsers } from '@/modules/data/user';
import { fetchSuperviseCourses, getAllCourses } from '@/modules/data/course';

import UsersListing from './UsersListing.vue';
import CoursesListing from './CoursesListing.vue';

export default {
  components: {
    UsersListing, CoursesListing
  },
  data() {
    return {
      userRole: 'teacher', 
      tab: 1,
      users: [],
      courses: [],
      errorSnackbar: false,
      errorMessage: ''
    };
  },
  async created() {
    try {
      const user = await getUser();
      this.userRole = user.role;
      

      if (user.role.toLowerCase() === 'admin') {
        this.courses = await getAllCourses();
        this.users = await getAllUsers();
      } else {
        this.courses = await fetchSuperviseCourses(user.userid);
        const allUsers = [];
        for (const course of this.courses) {
          const usersInCourse = await getUsersInCourse(course.courseid);
          allUsers.push(...usersInCourse);
        }
        this.users = Array.from(new Map(allUsers.map(user => [user.userid, user])).values());
      }
    } catch (error) {
      this.errorMessage = 'Error : ' + error.message;
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
