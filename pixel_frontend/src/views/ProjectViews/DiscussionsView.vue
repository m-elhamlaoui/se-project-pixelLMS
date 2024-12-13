<template>
  <v-container fluid class="messaging-app">
      <v-row>
          <v-col cols="12">
              <v-card-title>
                  <v-row class="w-100">
                  <v-col cols="auto">
                      {{ title }}
                  </v-col>
                  <v-spacer></v-spacer>
                  <v-col cols="auto" class="text-right">
                      <v-icon color="primary" class="mr-3" @click="editDiscussion">mdi-pencil</v-icon>
                      <v-icon color="red" @click="deleteDiscussion">mdi-trash-can</v-icon>
                  </v-col>
                  </v-row>
              </v-card-title>
              <v-card-subtitle>
                  {{ description }}
              </v-card-subtitle>
  
          </v-col>
      </v-row>
  
      <v-divider class="my-4"></v-divider>
  
      <v-list class="message-list">
          <v-list-item-group v-for="message in messages" :key="message.id">
              <v-card class="my-1">
                  <v-list-item>
                  <v-list-item-content>
                      <v-list-item-title>{{ message.content }}</v-list-item-title>
                      <v-list-item-subtitle>Par <strong>{{ message.username }}</strong> à {{ formatdate(message.timestamp) }}</v-list-item-subtitle>
                  </v-list-item-content>
                  </v-list-item>
              </v-card>   
          </v-list-item-group>
      </v-list>
  
      <v-row class="message-row">
      <v-col cols="10">
          <v-text-field v-model="message" label="Votre message" outlined></v-text-field>
      </v-col>
      <v-col cols="auto">
          <v-btn @click="sendMessage" color="primary"><v-icon> mdi-send </v-icon></v-btn>
      </v-col>
      </v-row>
  
      <v-dialog v-model="deletedialog" max-width="500px">
      <v-card>
          <v-card-title>Supprimer la discussion</v-card-title>
          <v-card-text>Êtes-vous sûr de vouloir supprimer cette discussion ?</v-card-text>
          <v-card-actions>
          <v-btn @click="closeDialog">Annuler</v-btn>
          <v-btn @click="mydeleteDiscussion" color="red">Supprimer</v-btn>
          </v-card-actions>
      </v-card>
      </v-dialog>
  
      <v-dialog v-model="editdialog" max-width="600px">
      <DiscussionCreate @submit="submitDiscussion" @close="closeDialog" :discussion="currentdiscussion"></DiscussionCreate>
      </v-dialog>
  
      <v-snackbar v-model="snackbar.show" :color="snackbar.color" timeout="3000">
      {{ snackbar.text }}
      <v-btn text @click="snackbar.show = false">Fermer</v-btn>
      </v-snackbar>
  </v-container>
  </template>
  
  <script>
  import { deleteDiscussion, getDiscussionById, updateDiscussion, fetchMessages, sendMessage as apiSendMessage } from '@/modules/data/discussion';
  import DiscussionCreate from '@/components/ProjectDialogs/DiscussionCreate.vue';
  
  export default {
    components: {
      DiscussionCreate
    },
    data() {
      return {
        title: 'Titre de la discussion',
        description: 'Ceci est la description de la discussion.',
        message: '',
        messages: [],
        deletedialog: false,
        editdialog: false,
        currentdiscussion: null,
        pollingInterval: null,
        snackbar: {
          show: false,
          text: '',
          color: 'error'
        }
      };
    },
    methods: {
      closeDialog() {
        this.deletedialog = false;
        this.editdialog = false;
      },
      editDiscussion() {
        this.editdialog = true;
      },
      deleteDiscussion() {
        this.deletedialog = true;
      },
      async submitDiscussion(disc) {
        try {
          await updateDiscussion(disc);
          window.location.reload();
        } catch (err) {
          this.snackbar.text = 'Échec de la mise à jour de la discussion';
          this.snackbar.show = true;
        }
      },
      async mydeleteDiscussion() {
        try {
          await deleteDiscussion(this.currentdiscussion.discussionid);
          this.$router.push('/project/'+this.currentdiscussion.projectid);
        } catch (err) {
          this.snackbar.text = 'Échec de la suppression de la discussion';
          this.snackbar.show = true;
        }
      },
      async sendMessage() {
        try {
          await apiSendMessage(this.message, this.currentdiscussion.discussionid);
          this.message = '';
          this.fetchMessages();
        } catch (err) {
          this.snackbar.text = 'Échec de l\'envoi du message';
          this.snackbar.show = true;
        }
      },
      async fetchMessages() {
        try {
          const messages = await fetchMessages(this.currentdiscussion.discussionid);
          this.messages = messages.sort((b, a) => new Date(b.timestamp) - new Date(a.timestamp));
        } catch (err) {
          this.snackbar.text = 'Échec de la récupération des messages';
          this.snackbar.show = true;
        }
      },
      formatdate(timestamp) {
        const date = new Date(timestamp);
        return date.toLocaleString();
      }
    },
    async created() {
      const discussionId = this.$route.params.id;
      this.currentdiscussion = await getDiscussionById(discussionId);
  
      this.title = this.currentdiscussion.title;
      this.description = this.currentdiscussion.description;
  
      this.fetchMessages();
      this.pollingInterval = setInterval(() => {
        this.fetchMessages();
      }, 1000);
    },
    beforeDestroy() {
      clearInterval(this.pollingInterval);
    }
  };
  </script>
  
  <style scoped>
  
  .messaging-app {
    max-width: 900px;
    margin: auto;
    padding-bottom: 80px; /* Ensure there's space for the message bar */
  }
  
  .v-card {
    border-radius: 8px;
  }
  
  .v-list-item-group {
    margin-bottom: 10px;
  }
  
  .message-list {
    height: 400px;
    overflow-y: auto;
    margin-bottom: 20px;
  }
  
  .message-row {
    padding: 10px;
    background: white;
    border-top: 1px solid #ddd;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    box-sizing: border-box; /* Ensure padding is included in width */
  }
  </style>
  