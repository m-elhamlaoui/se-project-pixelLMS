<template>
    <v-card>
        <v-card-title>
            <span class="headline">{{ headertext }} une discussion</span>
        </v-card-title>
        <v-card-text>
            <v-form ref="form" v-model="valid">
                <v-text-field
                    v-model="newDiscussion.title"
                    label="Titre"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
                <v-textarea
                    v-model="newDiscussion.description"
                    label="Description"
                ></v-textarea>
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
    props: ['discussion'],
    data() {
        return {
            valid: false,
            headertext: 'Créer',
            newDiscussion: {
                title: '',
                description: '',
            },
            rules: {
                required: value => !!value || 'Champ requis.',
            }
        }
    },
    methods: {
        submit() {
            if (this.valid) {
                this.$emit('submit', this.newDiscussion);
            }
        },
        close() {
            this.$emit('close');
        }
    },
    created() {
        if (this.discussion) {
            this.newDiscussion = this.discussion;
            this.headertext = 'Modifier';
        }
    }
}
</script>

<style scoped>
    .v-text-field, .v-btn {
        margin-top: 0;
    }
</style>
