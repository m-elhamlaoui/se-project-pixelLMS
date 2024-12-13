<template>
    <v-app>
        <div class="background-image"></div> 
        <v-main>
            <v-container fluid class="fill-height dark-background align-center justify-center">
                <v-row class="d-flex justify-center">
                    <v-col cols="12" sm="8" md="4">
                        <v-img
                            src="@/assets/banner.png" 
                            class="mb-4"
                            contain
                        ></v-img>

                        <v-card  class="elevation-12 pa-4" outlined>
                            <v-card-title class="text-h5 text-center">
                                Connexion
                            </v-card-title>

                            <v-divider></v-divider>

                            <v-card-subtitle class="text-center mb-4">
                                <span class="text--secondary">{{ Message }}</span>
                            </v-card-subtitle>

                            <v-card-text>
                                <v-form @submit.prevent="log">
                                    <v-text-field
                                        v-model="username"
                                        label="Nom d'utilisateur"
                                        outlined
                                        dense
                                        class="mb-4"
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="password"
                                        label="Mot de passe"
                                        type="password"
                                        outlined
                                        dense
                                        class="mb-4"
                                    ></v-text-field>
                                    <v-btn
                                        type="submit"
                                        color="primary"
                                        block
                                        class="mt-4"
                                    >
                                        Se connecter
                                    </v-btn>
                                </v-form>
                            </v-card-text>
                        </v-card>
                    </v-col>
                </v-row>
            </v-container>
        </v-main>
    </v-app>
</template>

<script>
import { login } from '@/modules/auth';

export default {
    data() {
        return {
            username: '',
            password: '',
            Message: 'Veuillez entrer vos identifiants',
        };
    },
    methods: {
        async log() {
            const response = await login(this.username, this.password);
            if (response) {
                this.$router.push('/');
            } else {
                this.Message = 'Identifiants invalides';
            }
        },
    },
};
</script>

<style scoped>
.background-image {
    position: fixed;
    top: -20px;
    left: -20px;
    width: 200%;
    height: 200%;
    background-image: url('@/assets/background.jpg'); 
    background-size: cover; 
    background-position: center; 
    background-repeat: no-repeat;
    filter: blur(5px); 
    z-index: 0; 
}


.v-card-title {
    font-weight: bold;
}

.v-card-subtitle {
    font-size: 1.1em;
}

.v-btn {
    text-transform: none;
}

</style>
