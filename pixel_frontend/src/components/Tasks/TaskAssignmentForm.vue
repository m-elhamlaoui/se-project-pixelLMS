<template>
    <v-card v-if="currentTask">
        <v-list>
            <v-list-item v-for="user in users" :key="user.userid">
                <v-checkbox v-model="user.selected" :label="user.name">
                    
                </v-checkbox>
            </v-list-item>
        </v-list>

        <v-card-actions>
            <v-btn text @click="close">Close</v-btn>
            <v-btn color="primary" @click="submitTask">Submit</v-btn>
        </v-card-actions>
    </v-card>
</template>

<script>
import { getUsersInTask, getUsersInCourse } from '@/modules/data/user';

export default {
    props: ['currentTask', 'courseID'],
    data() {
        return {
            users: [],
            copyoftheoriginal: []
        }
    },
    methods: {
        close(){
            this.$emit('close');
        },
        submitTask(){
            const differences = this.users.filter((user, index) => {
                return user.selected !== this.copyoftheoriginal[index].selected;
            });
            this.$emit('submit', differences);
        }
    },
    async created(){
        if (this.currentTask) {
            const allUsers = await getUsersInCourse(this.currentTask.courseid);
            const usersInTasks = await getUsersInTask(this.currentTask.taskid);

            this.users = allUsers.map(user => {
                return {
                    ...user,
                    selected: Array.isArray(usersInTasks) && usersInTasks.some(u => u.userid === user.userid)
                };
            });

            this.copyoftheoriginal = allUsers.map(user => {
                return {
                    ...user,
                    selected: Array.isArray(usersInTasks) && usersInTasks.some(u => u.userid === user.userid)
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
</style>
