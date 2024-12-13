<template>
    <v-container fluid>
        <v-row class="my-4">
            <v-col>
                <v-text-field min-width="100px"
                v-model="filters.title"
                label="Filtrer par titre"
                solo
                ></v-text-field>
            </v-col>
            <v-col>
                <v-select min-width="100px"
                v-model="filters.status"
                :items="statusOptions"
                label="Filtrer par statut"
                solo
                ></v-select>
            </v-col>
            <v-col>
                <v-select min-width="100px"
                v-model="filters.startYear"
                :items="yearOptions"
                label="Année de début"
                solo
                ></v-select>
            </v-col>
            <v-spacer></v-spacer>

            <v-col cols="auto">
                <v-btn color="primary" @click="openCreateProjectDialog">Créer un projet</v-btn>
            </v-col>
        </v-row>

        <v-list>
            <v-list-item-group v-if="sortedFilteredProjects.length > 0">
                <v-list-item
                    v-for="project in sortedFilteredProjects"
                    :key="project.projectid"
                    @click="openEditProjectDialog(project)"
                >
                    <v-list-item-content>
                        <v-row>
                            <v-col cols="auto">
                                <v-list-item-title>{{ project.title }}</v-list-item-title>
                                <v-list-item-subtitle><Strong>Description: </Strong>{{ truncateDescription(project.description) }}</v-list-item-subtitle>
                            </v-col>
                            <v-spacer></v-spacer>
                            <v-col cols="auto">
                                <v-icon color="primary" @click.stop="openAssignProjectDialog(project)" small class="mr-2">mdi-account-plus</v-icon>
                            </v-col>
                        </v-row>

                        <v-row>
                            <v-col cols="auto">
                                <v-list-item-subtitle><strong>Statut :</strong> {{ project.status }}</v-list-item-subtitle>
                            </v-col>
                            <v-col cols="auto">
                                <v-list-item-subtitle><strong>Année de début :</strong> {{ formatDate(project.startdate) }}</v-list-item-subtitle>
                            </v-col>
                            <v-col cols="auto">
                                <v-list-item-subtitle><strong>Année de fin :</strong> {{ formatDate(project.enddate) }}</v-list-item-subtitle>
                            </v-col>
                        </v-row>
                    </v-list-item-content>
                </v-list-item>
            </v-list-item-group>
            <v-list v-else>
                <v-list-item>Cela prendra quelques secondes. Si le problème persiste et qu'aucun projet n'est trouvé, il se peut qu'il n'existe pas.</v-list-item>
            </v-list>
        </v-list>

        <v-dialog v-model="editDialog" max-width="600px">
            <ProjectCreate @close="close" @submit="myeditProject" :project="currentProject"/>
        </v-dialog>

        <v-dialog v-model="createDialog" max-width="600px">
            <ProjectCreate @close="close" @submit="mycreateProject" :project="null"/>
        </v-dialog>

        <v-dialog v-model="assignDialog" max-width="600px">
            <ProjectAssignmentForm @close="close" @submit="myassignproject" :project="currentProject"/>
        </v-dialog>

        <!-- Affichage des messages d'erreur -->
        <v-snackbar v-model="errorSnackbar" color="red">
            {{ errorMessage }}
            <v-btn color="white" text @click="errorSnackbar = false">Fermer</v-btn>
        </v-snackbar>
    </v-container>
    

</template>

<script>
import ProjectCreate from '@/components/AdminPanel/ProjectCreate.vue';
import ProjectAssignmentForm from '@/components/AdminPanel/ProjectAssignmentForm.vue';
import { insertProject, updateProject, updateProjectAssignment } from '@/modules/data/project';

export default {
    props: ['projects'],
    components: {
        ProjectCreate, ProjectAssignmentForm
    },
    data() {
        return {
            filters: {
                title: '',
                status: 'Tous',
                startYear: 'Tous',
            },
            statusOptions: ['Tous'],

            editDialog: false,
            createDialog: false,
            assignDialog: false,
            currentProject: null,

            errorSnackbar: false,
            errorMessage: ''
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
        close() {
            this.createDialog = false;
            this.editDialog = false;
            this.assignDialog = false;
            this.assignDialog = false;
            this.currentProject = null;
        },
        openCreateProjectDialog() {
            this.createDialog = true;
        },
        openEditProjectDialog(project) {
            this.currentProject = project;
            this.editDialog = true;
        },
        openAssignProjectDialog(project) {
            this.currentProject = project;
            this.assignDialog = true;
        },

        async mycreateProject(project) {
            try {
                this.close();
                await insertProject(project);
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Erreur lors de la création du projet : ' + error.message;
                this.errorSnackbar = true;
            }
        },
        async myeditProject(project) {
            try {
                this.close();
                await updateProject(project);
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Erreur lors de la modification du projet : ' + error.message;
                this.errorSnackbar = true;
            }
        },
        async myassignproject(projectid, modifications){

            const traitement = modifications.map(modification => {
                return [modification.selected, modification.userid];
            });

            await updateProjectAssignment(projectid, traitement);
            window.location.reload();
        },
        formatDate(date) {
            return new Date(date).toLocaleDateString();
        },
        truncateDescription(description) {
            return description.length > 50 ? description.substring(0, 50) + '...' : description;
        }
    },
    created(){
        const projects = this.projects;
        const allstatus = new Set();
        for (let i = 0; i < projects.length; i++) {
            allstatus.add(projects[i].status);
            projects[i].startDate = this.formatDate(projects[i].startdate);
            projects[i].endDate = this.formatDate(projects[i].enddate);
        }
        this.statusOptions.push(...allstatus);
    }
}

</script>
  
<style scoped>
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
