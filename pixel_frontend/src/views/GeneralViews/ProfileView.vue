<template>
  <v-container fluid fill-height>

    <v-alert v-if="error" type="error">
          {{ error }}
    </v-alert>

    <v-card class="pa-4 mb-4" v-if="user.name">
      <v-card-title class="headline">
        {{ user.name }}
      </v-card-title>
      <v-card-subtitle>
        Email : {{ user.email }}
      </v-card-subtitle>
      <v-card-subtitle>
        Téléphone : {{ user.phonenumber }}
      </v-card-subtitle>
      <v-card-subtitle>
        Rôle : {{ user.role }}
      </v-card-subtitle>
      <v-card-subtitle>
        Date de création : {{ user.creationdate }}
      </v-card-subtitle>
    </v-card>

    <v-card>
        <v-tabs v-model="tab" align-tabs="start" color="primary">
          <v-tab :value="1">Les projets sur lesquels il travaille</v-tab>
          <v-tab v-if="supervisedProjects.length" :value="2">Les projets supervisés</v-tab>
          <v-tab :value="3">Fichiers</v-tab>
        </v-tabs>

        <v-tabs-window v-model="tab">
          <v-tabs-window-item :value="2" class="padding-10">
            <v-list v-if="supervisedProjects && supervisedProjects.length">
              <v-list-item v-for="project in supervisedProjects" :key="project.projectid" class="card-hover">
                <v-list-item-title>{{ project.title }}</v-list-item-title>
                <v-list-item-subtitle>{{ project.title }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
            <v-list class="ml-5" v-else>
              <v-list-item>Aucun projet trouvé.</v-list-item>
            </v-list>
          </v-tabs-window-item>

          <v-tabs-window-item :value="1">
            <v-list v-if="engagedProjects && engagedProjects.length">
              <v-list-item v-for="project in engagedProjects" :key="project.projectid" class="card-hover">
                <v-list-item-title>{{ project.title }}</v-list-item-title>
                <v-list-item-subtitle>{{ project.title }}</v-list-item-subtitle>
              </v-list-item>
            </v-list>
            <v-list class="ml-5" v-else>
              <v-list-item>Aucun projet trouvé.</v-list-item>
            </v-list>
          </v-tabs-window-item>

          <v-tabs-window-item :value="3">
            <v-list v-if="userfiles && userfiles.length">
              <v-list-item v-for="file in userfiles" class="card-hover">
                <v-list-item-content>
                    <v-row no-gutters>
                    <v-col cols="10">
                        <v-list-item-title>{{ getName(file.path) }}</v-list-item-title>
                    </v-col>
                    <v-col cols="2" class="d-flex justify-end">
                        <v-btn color="primary" small @click="downloadFile(file.fileid)" icon>
                        <v-icon small>mdi-download</v-icon>
                        </v-btn>
                    </v-col>
                    </v-row>
                </v-list-item-content>
              </v-list-item>
            </v-list>
            <v-list class="ml-5" v-else>
              <v-list-item-content>Aucun fichier ajouté</v-list-item-content>
            </v-list>
          </v-tabs-window-item>

        </v-tabs-window>
    </v-card>

  </v-container>
</template>

<script>
import { getUserbyId } from '@/modules/data/user';
import { downloadFile, getFilesByProfile } from '@/modules/data/file';
import { fetchEngagedProjects, fetchSuperviseProjects } from '@/modules/data/project';
import { getUser } from '@/modules/auth';
import DashboardCard from '@/components/DashboardCard.vue';

export default {
  components: {
    DashboardCard,
  },
  data() {
    return {
      user: {},
      supervisedProjects: [],
      engagedProjects: [],
      userfiles: [],
      error: null,

      tab: 1,
    };
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString('fr-FR'); // Format date for French locale
    },
    getName(path){
        return path.split('\\').pop();
    },
    async downloadFile(fileId) {
        try {
            await downloadFile(fileId);
        } catch (error) {
            this.showError('Échec du téléchargement du fichier: ' + error.message);
        }
    },
  },
  
  async created() {
    var userId = this.$route.params.id;
    try {
      if (userId === '0') {
        this.user = await getUser();
      } else {
        this.user = await getUserbyId(userId);
      }

      userId = this.user.userid;
      const supervisedProjects = await fetchSuperviseProjects(userId);
      const engagedProjects = await fetchEngagedProjects(userId);
      for (let i = 0; i < supervisedProjects.length; i++) {
        supervisedProjects[i].startDate = this.formatDate(supervisedProjects[i].startdate);
        supervisedProjects[i].endDate = this.formatDate(supervisedProjects[i].enddate);
      }
      for (let i = 0; i < engagedProjects.length; i++) {
        engagedProjects[i].startDate = this.formatDate(engagedProjects[i].startdate);
        engagedProjects[i].endDate = this.formatDate(engagedProjects[i].enddate);
      }

      const supervisedProjectIds = new Set(supervisedProjects.map(p => p.projectid));
      this.supervisedProjects = supervisedProjects;
      this.engagedProjects = engagedProjects.filter(project => !supervisedProjectIds.has(project.projectid));

      this.userfiles = await getFilesByProfile(userId);
    } catch (error) {
      console.error('Erreur lors de la récupération des données :', error);
      this.error = 'Échec du chargement des données. Veuillez réessayer plus tard.';
    }
  },
  watch: {
    '$route.params.id': function(newId) {
      window.location.reload();
    }
  }
};
</script>

<style scoped>
.v-card {
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.card-hover {
  transition: transform 0.3s;
}
.card-hover:hover {
  transform: scale(1.005);
}

.card-column {
  display: flex;
  justify-content: center;
  margin-bottom: 16px;
}

.card-column .v-card {
  min-width: 200px;
  max-width: 344px;
  width: 100%;
}

.padding-10 {
  padding: 10px;
}

.v-list-item {
  border-bottom: 1px solid #e0e0e0;
}
.v-list-item:last-child {
  border-bottom: none;
}
.v-card {
  border-radius: 0;
}
.grey--text.text--darken-2 {
  cursor: not-allowed;
  color: #757575 !important; 
  background-color: #f0f0f0 !important; 
  pointer-events: none; 
}
.padding-5 {
  padding: 10px;
}
</style>

