<template>
    <v-card>
        <v-card-title>
            <span class="headline">{{ headertext }} a discussion</span>
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
            <v-btn color="primary" text @click="close">Cancel</v-btn>
            <v-btn color="primary" text :disabled="!valid" @click="submit">Submit</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
export default {
    props: ['discussion'],
    data() {
        return {
            valid: false,
            headertext: 'Create',
            newDiscussion: {
                title: '',
                description: '',
            },
            rules: {
                required: value => !!value || 'Required field.',
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
            this.headertext = 'Edit';
        }
    }
}
</script>

<style scoped>
    .v-text-field, .v-btn {
        margin-top: 0;
    }
</style>
