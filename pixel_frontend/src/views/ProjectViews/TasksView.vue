<template>
  <v-container fluid>
    <!-- Filtres et Actions -->
    <v-row>
      <v-col>
        <v-text-field min-width="100px"
          v-model="search"
          label="Rechercher des tâches"
          outlined
        />
      </v-col>
      <v-col>
        <v-select min-width="100px"
          v-model="statusFilter"
          :items="statusOptions"
          label="Filtrer par statut"
          class="status-filter"
        />
      </v-col>
      <v-spacer></v-spacer>
      <v-col cols="auto">
        <v-btn color="primary" v-if="isadmin" @click="openCreateTaskDialog">Nouvelle Tâche</v-btn>
      </v-col>
    </v-row>

    <!-- Liste des Tâches -->
    <v-list>
      <v-list-item-group v-if="filteredTasks.length">
        <v-list-item
          v-for="task in sortedTasks"
          :class="{ 'grey--text text--darken-2': !isTaskActive(task) }"
          @click="openTaskDialog(task)"
        >
          <v-list-item-content>
            <v-row>
              <v-col>
                <v-list-item-title>{{ task.title }}</v-list-item-title>
                <v-list-item-subtitle><strong>Échéance :</strong> {{ task.duedate }}</v-list-item-subtitle>
              </v-col>
              <v-col v-if="isadmin" class="d-flex justify-end align-center" cols="auto">
                <v-icon @click.stop="openDeleteConfirmationDialog(task)" color="red" small class="mr-2">mdi-trash-can</v-icon>
                <v-icon color="primary" @click.stop="openUserManagementDialog(task)" small class="mr-2">mdi-account-plus</v-icon>
                <v-icon color="primary" @click.stop="openEditTaskDialog(task)" small>mdi-pencil</v-icon>
              </v-col>
            </v-row>
          </v-list-item-content>
        </v-list-item>
      </v-list-item-group>
      <v-list v-else>
        <v-list-item>Aucune tâche trouvée.</v-list-item>
      </v-list>
    </v-list>

    <!-- Dialogues -->

    <v-dialog v-model="deleteDialog" max-width="600px">
      <v-card class="padding-5">
        <v-card-title class="text-red">Supprimer la Tâche</v-card-title>
        <v-card-text>
          Êtes-vous sûr de vouloir supprimer cette tâche ?
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="mydeleteTask(currentTask)">Oui</v-btn>
          <v-btn @click="close">Non</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="manageDialog" max-width="600px">
      <TaskAssignmentForm @close="close" @submit="updateAssignments" :projectID="projectID" :currentTask="currentTask"/>
    </v-dialog>

    <v-dialog v-model="createDialog" max-width="600px">
      <TaskUpdateForm @close="close" @submit="createTask" :projectID="projectID" :optionalTaskDetails="currentTask"/>
    </v-dialog>

    <v-dialog v-model="dialog" max-width="600px">
      <TaskSubmission @close="close" @submit="submitTask" :projectID="projectID" :currentTask="currentTask" />
    </v-dialog>

    <!-- Snackbar pour les erreurs -->
    <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="snackbar.timeout">
      {{ snackbar.message }}
      <v-btn color="white" text @click="snackbar.show = false">Fermer</v-btn>
    </v-snackbar>

  </v-container>
</template>

<script>
import TaskSubmission from '@/components/Tasks/TaskSubmission.vue';
import TaskUpdateForm from '@/components/Tasks/TaskUpdateForm.vue';
import TaskAssignmentForm from '@/components/Tasks/TaskAssignmentForm.vue';
import { deleteTask, getTasksByProject, insertOrUpdateTask, updateTaskAssignment } from '@/modules/data/task';
import { getUser } from '@/modules/auth';
import { getUsersInTask } from '@/modules/data/user';
import { uploadFile } from '@/modules/data/file';

export default {
  components: {
    TaskSubmission, TaskUpdateForm, TaskAssignmentForm
  },
  data() {
    return {
      search: '',
      statusFilter: 'Tous',
      tasks: [],
      statusOptions: [],
      userspertasks: {},

      projectID: null,
      userID: null,
      isadmin: false,

      currentTask: null,
      manageDialog: false,
      dialog: false,
      createDialog: false,
      deleteDialog: false,

      snackbar: {
        show: false,
        message: '',
        color: 'error',
        timeout: 6000
      }
    };
  },
  computed: {
    filteredTasks() {
      return this.tasks.filter(task =>
        task.title.toLowerCase().includes(this.search.toLowerCase()) &&
        (this.statusFilter !== 'Tous' ? task.status === this.statusFilter : true)
      );
    },
    sortedTasks() {
      return this.filteredTasks.sort((a, b) => {
        if (this.isTaskActive(a) && !this.isTaskActive(b)) return -1;
        if (!this.isTaskActive(a) && this.isTaskActive(b)) return 1;
        return new Date(a.duedate) - new Date(b.duedate);
      });
    },
  },
  methods: {
    close() {
      this.dialog = false;
      this.createDialog = false;
      this.manageDialog = false;
      this.deleteDialog = false;
      this.currentTask = null;
    },
    openTaskDialog(task) {
      this.currentTask = task;
      this.dialog = true;
    },
    openCreateTaskDialog() {
      this.currentTask = null;
      this.createDialog = true;
    },
    openEditTaskDialog(task) {
      this.currentTask = task;
      this.createDialog = true;
    },
    openUserManagementDialog(task) {
      this.currentTask = task;
      this.manageDialog = true;
    },
    openDeleteConfirmationDialog(task) {
      this.currentTask = task;
      this.deleteDialog = true;
    },

    async mydeleteTask(task) {
      try {
        await deleteTask(task.taskid);
        window.location.reload();
      } catch (error) {
        this.showSnackbar('Erreur lors de la suppression de la tâche.');
      }
    },
    async createTask(task) {
      try {
        await insertOrUpdateTask(task.taskid, task);
        window.location.reload();
      } catch (error) {
        this.showSnackbar('Erreur lors de la création de la tâche.');
      }
    },
    async submitTask(task, files) {
      try {
        for (const file of files) {
          await uploadFile(file, task.taskid, 'task');
        }
        window.location.reload();
      } catch (error) {
        this.showSnackbar('Erreur lors du téléchargement des fichiers: ' + error.message);
      }
    },
    async updateAssignments(modifications) {
      if (modifications.length === 0) {
        return;
      }
      try {
        
        const traitement = modifications.map(modification => {
            return [modification.selected, modification.userid];
        });
        const success = await updateTaskAssignment(this.currentTask.taskid, traitement);
        if (success) {
          window.location.reload();
        }
      } catch (error) {
        this.showSnackbar('Erreur lors de la mise à jour des affectations.');
      }
    },

    isTaskActive(task) {
      if (this.isadmin) return true;
      const users = this.userspertasks[task.taskid] || []; 
      return users.some(user => user.userid === this.user.userid);
    },

    showSnackbar(message) {
      this.snackbar.message = message;
      this.snackbar.show = true;
    }
  },

  async created() {
    const projectID = this.$route.params.id;
    this.projectID = projectID;

    try {
      this.user = await getUser();
      this.isadmin = (this.user.role.toLowerCase() !== 'stagiaire');
      if (!this.user) return;

      this.tasks = await getTasksByProject(projectID);
      this.statusOptions = ['Tous', ...new Set(this.tasks.map(task => task.status))];

      this.tasks.forEach(async task => {
        this.userspertasks[task.taskid] = await getUsersInTask(task.taskid);
      });
    } catch (error) {
      this.showSnackbar('Erreur lors du chargement des tâches.');
    }
  },
};
</script>

<style scoped>
/* Styles personnalisés */
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
