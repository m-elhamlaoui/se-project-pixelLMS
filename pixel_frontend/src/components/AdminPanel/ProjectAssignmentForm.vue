<template>
    <v-card>
        <v-card-title>
            <span class="headline">Assigner des utilisateurs</span>
        </v-card-title>
        <v-card-text>
            <v-text-field
                v-model="newUserEmail"
                label="Email de nouvel utilisateur"
                @keyup.enter="addUser"
                outlined
            ></v-text-field>
            <v-btn @click="addUser" color="primary">Ajouter</v-btn>

            <v-list>
                <v-list-item-group>
                    <v-list-item v-for="user in users" :key="user.userid">
                        <v-row no-gutters>
                            <v-col cols="auto">
                                <v-checkbox v-model="user.selected"></v-checkbox>
                            </v-col>
                            <v-col>
                                <v-list-item-content>
                                    <v-list-item-title>{{ user.name }} {{ user.userid }}</v-list-item-title>
                                    <v-list-item-subtitle>{{ user.email }}</v-list-item-subtitle>
                                </v-list-item-content>
                            </v-col>
                        </v-row>
                    </v-list-item>
                </v-list-item-group>
            </v-list>
        </v-card-text>

        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn @click="close">Annuler</v-btn>
            <v-btn @click="submitTask" color="primary">Enregistrer</v-btn>
        </v-card-actions>
    </v-card>

    <v-snackbar v-model="errorSnackbar" color="error" top>
        {{ errorMessage }}
        <v-btn text @click="errorSnackbar = false">Fermer</v-btn>
    </v-snackbar>
</template>

<script>
import { getUser } from '@/modules/auth';
import { getUserByEmail, getUsersInProject } from '@/modules/data/user';

export default {
    props: ['project'],
    data() {
        return {
            newUserEmail: '',
            users: [],
            copyoftheoriginal: [],

            myuser: null,

            errorMessage: '',
            errorSnackbar: false
        }
    },
    methods: {
        close(){
            this.$emit('close');
        },
        submitTask(){
            const differences = this.users.filter(user => {
                const originalUser = this.copyoftheoriginal.find(original => original.userid === user.userid);
                if (!originalUser) {
                    return true;
                }
                return originalUser.selected !== user.selected;
            });
            this.$emit('submit', this.project.projectid, differences);
        },
        async addUser() {
            if (this.newUserEmail) {
                var newUser = null;
                try{
                    newUser = await getUserByEmail(this.newUserEmail); 
                }
                catch(e){
                }
                
                if (newUser.userid === this.myuser.userid) {
                    this.showError('Vous ne pouvez pas vous ajouter à un projet');
                    return;
                }

                if (newUser) {
                    this.users.push({
                        ...newUser,
                        selected: true
                    });
                    this.copyoftheoriginal.push({
                        ...newUser,
                        selected: false
                    });
                    this.newUserEmail = ''; 
                }
                else{
                    this.showError('Utilisateur non trouvé');
                }
            }
        },
        showError(message){
            this.errorMessage = message;
            this.errorSnackbar = true;
        }
    },
    async created(){
        this.myuser = await getUser();
        if (this.project) {
            var allUsers = await getUsersInProject(this.project.projectid);
            allUsers = allUsers.filter(user => user.userid !== this.myuser.userid);
            this.users = allUsers.map(user => {
                return {
                    ...user,
                    selected: true
                };
            });
            this.copyoftheoriginal = allUsers.map(user => {
                return {
                    ...user,
                    selected: true
                };
            });
        }
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
.v-list-item-content {
  display: flex;
  align-items: center;
}
</style>
