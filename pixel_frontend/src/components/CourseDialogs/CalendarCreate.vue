<template>
    <v-card>
        <v-card-title>
            <span class="headline">Plan an event</span>
        </v-card-title>
        <v-card-text>
            <v-form ref="form" v-model="valid">
                <v-text-field
                    v-model="newEvent.title"
                    label="Title"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
                <v-textarea
                    v-model="newEvent.description"
                    :rules="[rules.required]"
                    label="Description"
                ></v-textarea>
                <v-text-field
                    v-model="newEvent.date"
                    label="Date"
                    type="date"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
                <v-text-field
                    v-model="newEvent.starttime"
                    label="Start time"
                    type="time"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
                <v-text-field
                    v-model="newEvent.endtime"
                    label="End time"
                    type="time"
                    :rules="[rules.required]"
                    required
                ></v-text-field>
            </v-form>
        </v-card-text>
        <v-card-actions>
            <v-spacer></v-spacer>
            <v-btn text @click="close">Cancel</v-btn>
            <v-btn color="primary" text :disabled="!valid" @click="submit">Submit</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
export default {
    data() {
        return {
            valid: false,
            newEvent: {
                title: '',
                description: '',
                date: '',
                starttime: '',
                endtime: ''
            },
            rules: {
                required: value => !!value || 'Required field.',
            }
        }
    },
    methods: {
        validateAndFormatTime(time) {
            return time ? time : '00:00:00';
        },
        formatDateTime() {
            if (this.newEvent.date) {
                this.newEvent.starttime = `${this.newEvent.date}T${this.validateAndFormatTime(this.newEvent.startTime)}`;
                this.newEvent.endtime = `${this.newEvent.date}T${this.validateAndFormatTime(this.newEvent.endTime)}`;
            } else {
                console.error('Date is not provided');
            }
        },
        submit() {
            if (this.valid) {
                this.formatDateTime();
                this.newEvent.date = null;
                this.$emit('submit', this.newEvent);
            }
        },
        close() {
            this.$emit('close');
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
