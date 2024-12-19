<template>
  <v-container fluid fill-height>
    <v-row align="center" justify="center">
      <v-col cols="12">
        <!-- Snackbar for error messages -->
        <v-snackbar v-model="snackbar.show" :timeout="6000" color="error">
          {{ snackbar.message }}
          <v-btn text @click="snackbar.show = false">Close</v-btn>
        </v-snackbar>

        <v-card v-if="course.title" class="pa-5 elevation-2 rounded-lg">
          <v-row>
            <v-col cols="12" md="6">
              <v-card-title class="headline mb-3">
                {{ course.title }}
              </v-card-title>

              <v-card-subtitle class="mb-2">
                <strong>Description :</strong>
              </v-card-subtitle>

              <v-card-text class="mb-4">
                {{ course.description }}
              </v-card-text>

              <v-card-text class="mb-4">
                <div>
                  <strong>Status :</strong> {{ course.status }}
                </div>
              </v-card-text>

              <v-divider></v-divider>

              <v-card-subtitle class="mt-4">
                <strong>Teacher contact :</strong>
              </v-card-subtitle>

              <v-card-text>
                <div class="mb-2">
                  <strong>Name :</strong> {{ teacher.name }}
                </div>
                <div class="mb-2">
                  <strong>Email :</strong> <a :href="'mailto:' + teacher.email">{{ teacher.email }}</a>
                </div>
                <div class="mb-2">
                  <strong>Phone number :</strong> <a :href="'tel:' + teacher.phonenumber">{{ teacher.phonenumber }}</a>
                </div>
                <v-btn :to="teacher.profileLink" class="mt-4" color="primary" text>
                  View profile
                </v-btn>
              </v-card-text>

              <v-divider class="mt-4"></v-divider>

              <v-card-actions class="d-flex flex-column align-start">
                <v-hover v-slot:default="{ isHovering }">
                  <v-btn v-if="showButtons" @click="openDiscussionDialog" class="mx-0 my-2" :color="isHovering ? 'primary' : 'grey lighten-2'" outlined>
                    <v-icon left>mdi-comment-plus</v-icon>
                    Start a discussion
                  </v-btn>
                </v-hover>
                <v-hover v-slot:default="{ isHovering }">
                  <v-btn v-if="showButtons" @click="openCalendarDialog" class="mx-0 my-2" :color="isHovering ? 'primary' : 'grey lighten-2'" outlined>
                    <v-icon left>mdi-calendar-plus</v-icon>
                    Add an event
                  </v-btn>
                </v-hover>
              </v-card-actions>
            </v-col>

            <v-col cols="12" md="6">
              <v-card-subtitle class="mt-4">
                <strong>Engaged people :</strong>
              </v-card-subtitle>

              <v-card-text>
                <v-list>
                  <v-list-item
                    v-for="person in people"
                    :key="person.id"
                    class="person-item"
                  >
                    <v-list-item-content>
                      <v-list-item-title>
                        <router-link :to="person.profileLink">{{ person.name }}</router-link>
                      </v-list-item-title>
                    </v-list-item-content>
                  </v-list-item>
                </v-list>
              </v-card-text>
            </v-col>
          </v-row>
        </v-card>

        <v-card v-if="!course.title" class="pa-5 elevation-2 rounded-lg">
          <v-card-title class="headline">
            Page loading
          </v-card-title>
          <v-card-text>
            If the page remains like this, the requested project cannot be found. Please check the URL and try again
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <v-dialog v-model="calendarDialog" max-width="600">
      <CalendarCreate @submit="createEvent" @close="close" ></CalendarCreate>
    </v-dialog>

    <v-dialog v-model="discussionDialog" max-width="600">
      <DiscussionCreate @submit="createDiscussion" @close="close" ></DiscussionCreate>
    </v-dialog>

  </v-container>
</template>

<script>
import { getUser } from '@/modules/auth';
import { getCourseById } from '@/modules/data/course';
import { getUsersInCourse } from '@/modules/data/user';
import { createEvent } from '@/modules/data/calendar';
import { createDiscussion } from '@/modules/data/discussion';

import CalendarCreate from '@/components/CourseDialogs/CalendarCreate.vue';
import DiscussionCreate from '@/components/CourseDialogs/DiscussionCreate.vue';

export default {
  components: {
    CalendarCreate,
    DiscussionCreate,
  },
  data() {
    return {
      course: {},
      teacher: {
          name: 'Non attribué',
          email: 'Non disponible',
          phonenumber: 'Non disponible',
          profileLink: '#',
      },
      people: [],
      snackbar: {
        show: false,
        message: ''
      },
      showButtons: true,  

      calendarDialog: false,
      discussionDialog: false,
    };
  },
  async created() {
    try {
      const courseId = this.$route.params.id;

      const course = await getCourseById(courseId);
      if (!course) {
        throw new Error('Course unfound');
      }
      this.course = course;

      const teacherID = course.userid;
      const loggeduser = await getUser();
      if (teacherID == loggeduser.userid){
        this.showButtons = true;
      }

      const people = await getUsersInCourse(courseId);

      const exist = people.find(person => person.userid === loggeduser.userid);
      if (!exist){
        this.$router.back();
        return
      }

      const teacher = people.find(person => person.userid === teacherID);
      
      if (teacher) {
        this.teacher = {
          name: teacher.name,
          email: teacher.email,
          phonenumber: teacher.phonenumber,
          profileLink: `/profile/${teacher.userid}`,
        };
      }
      
      this.people = people.map(person => ({
        id: person.userid,
        name: person.name,
        profileLink: `/profile/${person.userid}`,
      }));
      
      localStorage.setItem('course', courseId);

    } catch (error) {
      console.error('Error while retrieving the data:', error);
      this.showSnackbar('Unable to load the course data. Please try again later.');
    }
  },
  methods: {
    close(){
      this.calendarDialog = false;
      this.discussionDialog = false;
    },
    openDiscussionDialog(){
      this.discussionDialog = true;
    },
    openCalendarDialog() {
      this.calendarDialog = true;
    },
    async createEvent(event) {
      event.courseid = this.course.courseid;
      try{
        await createEvent(event);
        window.location.reload();
      } catch (error) {
        console.error('Error while creating the event', error);
        this.showSnackbar('Unable to create the event. Please try again later.');
      }
    },
    async createDiscussion(discussion) {
      discussion.courseid = this.course.courseid;
      try{
        await createDiscussion(discussion);
        window.location.reload();
      } catch (error) {
        console.error('"Error while creating the discussion:', error);
        this.showSnackbar('"Unable to create the discussion. Please try again later.');
      }
    },
    showSnackbar(message) {
      this.snackbar.message = message;
      this.snackbar.show = true;
    }
  },
};
</script>
