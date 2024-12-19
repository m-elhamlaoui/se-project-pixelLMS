<template>
    <v-card>
        <v-card-title>
            <span class="headline">{{ headertext }} course </span>
        </v-card-title>
        <v-card-text>
            <v-form ref="form" v-model="valid">
                <v-text-field
                    v-model="newCourse.title"
                    label="Title"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
                <v-textarea
                    v-model="newCourse.description"
                    label="Description"
                ></v-textarea>
                <v-select
                    v-model="newCourse.status"
                    :items="statusOptions"
                    label="Status"
                    :rules="[rules.required]"
                    required
                ></v-select>
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
    props: ['course'],
    data() {
        return {
            valid: false,
            headertext: 'Create',
            newCourse: {
                title: '',
                description: '',                
                enddate: '',
                status: '',
            },
            statusOptions: ['Not started', 'In progress', 'Completed'], 
            rules: {
                required: value => !!value || 'Required field.',
            }
        }
    },
    methods: {
        submit() {
            if (this.valid) {
                this.$emit('submit', this.newCourse);
            }
        },
        close() {
            this.$emit('close');
        }
    },
    created() {
        if (this.course) {
            this.newCourse = this.course ;
            this.headertext = 'Edit';
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
