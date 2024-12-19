<template>
    <v-container fluid>
        <v-row class="my-4">
            <v-col>
                <v-text-field
                    min-width="100px"
                    v-model="filters.title"
                    label="Filter by title"
                    solo
                ></v-text-field>
            </v-col>
            <v-spacer></v-spacer>

            <v-col cols="auto">
                <v-btn color="primary" @click="openCreateCourseDialog">Create course</v-btn>
            </v-col>
        </v-row>

        <v-list>
            <v-list-item-group v-if="filteredCourses.length > 0">
                <v-list-item
                    v-for="course in filteredCourses"
                    :key="course.courseid"
                    @click="openEditCourseDialog(course)"
                >
                    <v-list-item-content>
                        <v-row>
                            <v-col cols="auto">
                                <v-list-item-title>{{ course.title }}</v-list-item-title>
                                <v-list-item-subtitle>
                                    <strong>Description:</strong> {{ truncateDescription(course.description) }}
                                </v-list-item-subtitle>
                            </v-col>
                            <v-spacer></v-spacer>
                            <v-col cols="auto">
                                <v-icon color="primary" @click.stop="openAssignCourseDialog(course)" small class="mr-2">
                                    mdi-account-plus
                                </v-icon>
                            </v-col>
                        </v-row>
                    </v-list-item-content>
                </v-list-item>
            </v-list-item-group>
            <v-list v-else>
                <v-list-item>This will take a few seconds. If the issue persists and no project is found, it may not exist.</v-list-item>
            </v-list>
        </v-list>

        <v-dialog v-model="editDialog" max-width="600px">
            <CourseCreate @close="close" @submit="myeditCourse" :course="currentCourse" />
        </v-dialog>

        <v-dialog v-model="createDialog" max-width="600px">
            <CourseCreate @close="close" @submit="mycreateCourse" :course="null" />
        </v-dialog>

        <v-dialog v-model="assignDialog" max-width="600px">
            <CourseAssignmentForm @close="close" @submit="myassigncourse" :course="currentCourse" />
        </v-dialog>

        <!-- Display Error Messages -->
        <v-snackbar v-model="errorSnackbar" color="red">
            {{ errorMessage }}
            <v-btn color="white" text @click="errorSnackbar = false">Close</v-btn>
        </v-snackbar>
    </v-container>
</template>

<script>
import CourseCreate from '@/components/AdminPanel/CourseCreate.vue';
import CourseAssignmentForm from '@/components/AdminPanel/CourseAssignmentForm.vue';
import { insertCourse, updateCourse, updateCourseAssignment } from '@/modules/data/course';

export default {
    props: ['courses'],
    components: {
        CourseCreate,
        CourseAssignmentForm,
    },
    data() {
        return {
            filters: {
                title: '', // Only filtering by title
            },
            editDialog: false,
            createDialog: false,
            assignDialog: false,
            currentCourse: null,
            errorSnackbar: false,
            errorMessage: '',
        };
    },
    computed: {
        filteredCourses() {
            return this.courses.filter((course) =>
                this.filters.title
                    ? course.title.toLowerCase().includes(this.filters.title.toLowerCase())
                    : true
            );
        },
    },
    methods: {
        close() {
            this.createDialog = false;
            this.editDialog = false;
            this.assignDialog = false;
            this.currentCourse = null;
        },
        openCreateCourseDialog() {
            this.createDialog = true;
        },
        openEditCourseDialog(course) {
            this.currentCourse = course;
            this.editDialog = true;
        },
        openAssignCourseDialog(course) {
            this.currentCourse = course;
            this.assignDialog = true;
        },
        async mycreateCourse(course) {
            try {
                this.close();
                await insertCourse(course);
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Error occurred: ' + error.message;
                this.errorSnackbar = true;
            }
        },
        async myeditCourse(course) {
            try {
                this.close();
                await updateCourse(course);
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Error occurred: ' + error.message;
                this.errorSnackbar = true;
            }
        },
        async myassigncourse(courseid, modifications) {
            const traitement = modifications.map((modification) => {
                return [modification.selected, modification.userid];
            });

            try {
                await updateCourseAssignment(courseid, traitement);
                window.location.reload();
            } catch (error) {
                this.errorMessage = 'Error occurred: ' + error.message;
                this.errorSnackbar = true;
            }
        },
        truncateDescription(description) {
            return description.length > 50 ? description.substring(0, 50) + '...' : description;
        },
    },
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
.padding-5 {
    padding: 10px;
}
</style>
