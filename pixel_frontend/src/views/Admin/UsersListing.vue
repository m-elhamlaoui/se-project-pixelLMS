<template>
    <v-container fluid>
        <v-row>
            <v-col>
                <v-text-field min-width="100px"
                    v-model="userSearch"
                    label="Rechercher des utilisateurs"
                    prepend-inner-icon="mdi-magnify"
                ></v-text-field>
            </v-col>

            <v-spacer></v-spacer>

            <v-col cols="auto">
                <v-btn color="primary" @click="openCreateUserDialog">Créer un utilisateur</v-btn>
            </v-col>
        </v-row>
  
        <v-list>
            <v-list-item-group v-if="filteredUsers.length > 0">
                <v-list-item
                    v-for="user in filteredUsers"
                    :key="user.userid"
                    @click="openEditUserDialog(user)"
                >
                    <v-list-item-content>
                        <v-list-item-title>{{ user.name }}</v-list-item-title>
                        <v-list-item-subtitle><strong>Email :</strong> {{ user.email }}</v-list-item-subtitle>
                        <v-list-item-subtitle><strong>ID :</strong> {{ user.userid }}</v-list-item-subtitle>
                    </v-list-item-content>
                </v-list-item>
            </v-list-item-group>
            <v-list v-else>
                <v-list-item>Cela prendra quelques secondes. Si le problème persiste et qu'aucun utilisateur n'est trouvé, il se peut qu'il n'existe pas.</v-list-item>
            </v-list>
        </v-list>

        <v-dialog v-model="editDialog" max-width="600px">
            <UserCreate @close="close" @submit="myeditUser" :useradmin="superadmin" :user="currentUser"/>
        </v-dialog>

        <v-dialog v-model="createDialog" max-width="600px">
            <UserCreate @close="close" @submit="mycreateUser" :useradmin="superadmin" :user="null"/>
        </v-dialog>

        <!-- Affichage des messages d'erreur -->
        <v-snackbar v-model="errorSnackbar" color="red">
            {{ errorMessage }}
            <v-btn color="white" text @click="errorSnackbar = false">Fermer</v-btn>
        </v-snackbar>
    </v-container>
</template>
  
<script>
import UserCreate from '@/components/AdminPanel/UserCreate.vue';
import { createUser, updateUser, getUserByEmail } from '@/modules/data/user'; 
import { getUser } from '@/modules/auth';
import { uploadFile } from '@/modules/data/file';

export default {
    props: ['users'],
    components: {
        UserCreate
    },
    data() {
        return {
            userSearch: '',
            superadmin: false,
            editDialog: false,
            createDialog: false,
            currentUser: null,
            errorSnackbar: false,
            errorMessage: ''
        };
    },
    computed: {
        filteredUsers() {
            return this.users.filter(user =>
                user.name.toLowerCase().includes(this.userSearch.toLowerCase()) ||
                user.email.toLowerCase().includes(this.userSearch.toLowerCase())
            );
        },
    },
    methods: {
        close() {
            this.createDialog = false;
            this.editDialog = false;
            this.currentUser = null;
        },
        openCreateUserDialog() {
            this.createDialog = true;
        },
        openEditUserDialog(user) {
            this.currentUser = user;
            this.editDialog = true;
        },

        async mycreateUser(user, files) {
            try {
                this.close();
                await createUser(user, user.password);
                const newuser = await getUserByEmail(user.email);
                if (files.length > 0) {
                    for (const file of files) {
                        await uploadFile(file, newuser.userid, 'profile');
                    }
                }
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Erreur lors de la création de l\'utilisateur : ' + error.message;
                this.errorSnackbar = true;
            }
        },
        async myeditUser(user, files) {
            try {
                this.close();
                await updateUser(user, user.password);
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Erreur lors de la mise à jour de l\'utilisateur : ' + error.message;
                this.errorSnackbar = true;
            }
        }
    },
    async created() {
        const user = await getUser();
        this.superadmin = user.role.toLowerCase() === 'superadmin';
    }
};
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
