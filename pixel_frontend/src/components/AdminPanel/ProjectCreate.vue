<template>
    <v-card>
        <v-card-title>
            <span class="headline">{{ headertext }} un projet</span>
        </v-card-title>
        <v-card-text>
            <v-form ref="form" v-model="valid">
                <v-text-field
                    v-model="newProject.title"
                    label="Titre"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
                <v-textarea
                    v-model="newProject.description"
                    label="Description"
                ></v-textarea>
                <v-text-field
                    v-model="newProject.enddate"
                    label="Date de fin"
                    type="date"
                ></v-text-field>
                <v-select
                    v-model="newProject.status"
                    :items="statusOptions"
                    label="Statut"
                    :rules="[rules.required]"
                    required
                ></v-select>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn color="primary" text @click="close">Annuler</v-btn>
            <v-btn color="primary" text :disabled="!valid" @click="submit">Soumettre</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
export default {
    props: ['project'],
    data() {
        return {
            valid: false,
            headertext: 'Créer',
            newProject: {
                title: '',
                description: '',                
                enddate: '',
                status: '',
            },
            statusOptions: ['Non Commencée', 'En Cours', 'Terminée'], 
            rules: {
                required: value => !!value || 'Champ requis.',
            }
        }
    },
    methods: {
        submit() {
            if (this.valid) {
                this.$emit('submit', this.newProject);
            }
        },
        close() {
            this.$emit('close');
        }
    },
    created() {
        if (this.project) {
            this.newProject = this.project ;
            this.headertext = 'Modifier';
        }
    }
}
</script>

<style scoped>
.v-text-field,
.v-btn {
    margin-top: 0;
}
</style>
