<template>
    <v-app>
        <div class="background-image"></div>
        <v-main>
            <v-container fluid class="fill-height">
                <v-row class="fill-height d-flex">
                    <!-- Left Column: Image -->
                    <v-col cols="12" md="5" class="d-flex align-center justify-center">
                        <v-img
                            src="@/assets/banner.png"
                            class="image-left"
                            contain
                        ></v-img>
                    </v-col>

                    <!-- Right Column: Login Interface -->
                    <v-col cols="12" md="7" class="d-flex align-center justify-center">
                        <v-card class="elevation-12 pa-6 login-card" outlined>
                            <v-card-title class="text-h4 text-center">
                                Login
                            </v-card-title>

                            <v-divider></v-divider>

                            <v-card-subtitle class="text-center mb-6">
                                <span class="text--secondary">{{ Message }}</span>
                            </v-card-subtitle>

                            <v-card-text>
                                <v-form @submit.prevent="log">
                                    <v-text-field
                                        v-model="username"
                                        label="Username"
                                        outlined
                                        dense
                                        class="mb-4"
                                    ></v-text-field>
                                    <v-text-field
                                        v-model="password"
                                        label="Password"
                                        type="password"
                                        outlined
                                        dense
                                        class="mb-4"
                                    ></v-text-field>
                                    <v-btn
                                        type="submit"
                                        color="dark orange"
                                        block
                                        class="mt-4"
                                    >
                                        Confirm
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
            Message: 'Enter your information',
        };
    },
    methods: {
        async log() {
            const response = await login(this.username, this.password);
            if (response) {
                this.$router.push('/');
            } else {
                this.Message = 'Username or password incorrect';
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
    background-image: url('@/assets/background.png');
    background-size: cover;
    background-position: center;
    background-repeat: no-repeat;
    filter: blur(4px);
    z-index: 0;
}

.image-left {
    max-width: 80%;
    max-height: 80%;
    object-fit: contain;
}

.login-card {
    width: 80%; /* Increased width for a balanced look */
    max-width: 600px; /* Limit maximum width on larger screens */
}

.v-card-title {
    font-weight: bold;
}

.v-card-subtitle {
    font-size: 1.2em;
}

.v-btn {
    text-transform: none;
    font-size: 1.1em;
    font-weight: bold;
    background-color: rgb(240, 140, 17);
    color: rgb(255, 255, 255);
}

.v-text-field {
    font-size: 1.1em;
}
</style>
