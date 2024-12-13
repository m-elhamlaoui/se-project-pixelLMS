<template>
  <v-container fluid fill-height>
    <v-row align="center" justify="center">
      <v-col cols="12">
        <!-- Snackbar for error messages -->
        <v-snackbar v-model="snackbar.show" :timeout="6000" color="error">
          {{ snackbar.message }}
          <v-btn text @click="snackbar.show = false">Close</v-btn>
        </v-snackbar>

        <v-card v-if="project.title" class="pa-5 elevation-2 rounded-lg">
          <v-row>
            <v-col cols="12" md="6">
              <v-card-title class="headline mb-3">
                {{ project.title }}
              </v-card-title>

              <v-card-subtitle class="mb-2">
                <strong>Description :</strong>
              </v-card-subtitle>

              <v-card-text class="mb-4">
                {{ project.description }}
              </v-card-text>

              <v-card-text class="mb-4">
                <div>
                  <strong>Date de début :</strong> {{ project.startdate }}
                </div>
                <div>
                  <strong>Date de fin :</strong> {{ project.enddate }}
                </div>
                <div>
                  <strong>Statut :</strong> {{ project.status }}
                </div>
              </v-card-text>

              <v-divider></v-divider>

              <v-card-subtitle class="mt-4">
                <strong>Contact du superviseur :</strong>
              </v-card-subtitle>

              <v-card-text>
                <div class="mb-2">
                  <strong>Nom :</strong> {{ supervisor.name }}
                </div>
                <div class="mb-2">
                  <strong>Email :</strong> <a :href="'mailto:' + supervisor.email">{{ supervisor.email }}</a>
                </div>
                <div class="mb-2">
                  <strong>Téléphone :</strong> <a :href="'tel:' + supervisor.phonenumber">{{ supervisor.phonenumber }}</a>
                </div>
                <v-btn :to="supervisor.profileLink" class="mt-4" color="primary" text>
                  Voir le profil
                </v-btn>
              </v-card-text>

              <v-divider class="mt-4"></v-divider>

              <v-card-actions class="d-flex flex-column align-start">
                <v-hover v-slot:default="{ isHovering }">
                  <v-btn v-if="showButtons" @click="openDiscussionDialog" class="mx-0 my-2" :color="isHovering ? 'primary' : 'grey lighten-2'" outlined>
                    <v-icon left>mdi-comment-plus</v-icon>
                    Commencer une discussion
                  </v-btn>
                </v-hover>
                <v-hover v-slot:default="{ isHovering }">
                  <v-btn v-if="showButtons" @click="openCalendarDialog" class="mx-0 my-2" :color="isHovering ? 'primary' : 'grey lighten-2'" outlined>
                    <v-icon left>mdi-calendar-plus</v-icon>
                    Ajouter un événement
                  </v-btn>
                </v-hover>
              </v-card-actions>
            </v-col>

            <v-col cols="12" md="6">
              <v-card-subtitle class="mt-4">
                <strong>Personnes engagées :</strong>
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

        <v-card v-if="!project.title" class="pa-5 elevation-2 rounded-lg">
          <v-card-title class="headline">
            Chargement de la page
          </v-card-title>
          <v-card-text>
            Si la page reste comme ça, le projet demandé est introuvable. Veuillez vérifier l'URL et réessayer.
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
import { getProjectById } from '@/modules/data/project';
import { getUsersInProject } from '@/modules/data/user';
import { createEvent } from '@/modules/data/calendar';
import { createDiscussion } from '@/modules/data/discussion';

import CalendarCreate from '@/components/ProjectDialogs/CalendarCreate.vue';
import DiscussionCreate from '@/components/ProjectDialogs/DiscussionCreate.vue';

export default {
  components: {
    CalendarCreate,
    DiscussionCreate,
  },
  data() {
    return {
      project: {},
      supervisor: {
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
      const projectId = this.$route.params.id;

      const project = await getProjectById(projectId);
      if (!project) {
        throw new Error('Projet introuvable');
      }
      this.project = project;

      const supervisorID = project.userid;
      const loggeduser = await getUser();
      if (supervisorID == loggeduser.userid){
        this.showButtons = true;
      }

      const people = await getUsersInProject(projectId);

      const exist = people.find(person => person.userid === loggeduser.userid);
      if (!exist){
        this.$router.back();
        return
      }

      const supervisor = people.find(person => person.userid === supervisorID);
      
      if (supervisor) {
        this.supervisor = {
          name: supervisor.name,
          email: supervisor.email,
          phonenumber: supervisor.phonenumber,
          profileLink: `/profile/${supervisor.userid}`,
        };
      }
      
      this.people = people.map(person => ({
        id: person.userid,
        name: person.name,
        profileLink: `/profile/${person.userid}`,
      }));
      
      localStorage.setItem('project', projectId);

    } catch (error) {
      console.error('Erreur lors de la récupération des données :', error);
      this.showSnackbar('Impossible de charger les données du projet. Veuillez réessayer plus tard.');
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
      event.projectid = this.project.projectid;
      try{
        await createEvent(event);
        window.location.reload();
      } catch (error) {
        console.error('Erreur lors de la création de l\'événement :', error);
        this.showSnackbar('Impossible de créer l\'événement. Veuillez réessayer plus tard.');
      }
    },
    async createDiscussion(discussion) {
      discussion.projectid = this.project.projectid;
      try{
        await createDiscussion(discussion);
        window.location.reload();
      } catch (error) {
        console.error('Erreur lors de la création de la discussion :', error);
        this.showSnackbar('Impossible de créer la discussion. Veuillez réessayer plus tard.');
      }
    },
    showSnackbar(message) {
      this.snackbar.message = message;
      this.snackbar.show = true;
    }
  },
};
</script>
