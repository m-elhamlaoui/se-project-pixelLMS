<template>
    <v-card v-if="currentTask">
        <v-card-title>
            <span class="headline">{{ currentTask.title }}</span>
        </v-card-title>
        <v-card-subtitle class="description">
            {{ currentTask.description }}
        </v-card-subtitle>
        <v-card-text>
            <div><strong>Deadline:</strong> {{ currentTask.duedate }}</div>
            <div><strong>Status:</strong> {{ currentTask.status }}</div>
        </v-card-text>

        <v-card-text>
            <v-list>
                <v-list-item-group v-if="importfiles && importfiles.length > 0">
                    <v-list-item
                        v-for="(file, index) in importfiles"
                        :key="index"
                    >
                        <v-list-item-content>
                            <v-row no-gutters>
                            <v-col cols="10">
                                <v-list-item-title>{{ getName(file.path) }}</v-list-item-title>
                            </v-col>
                            <v-col cols="2" class="d-flex justify-end">
                                <v-btn color="primary" small @click="downloadFile(file.fileid)" icon>
                                <v-icon small>mdi-download</v-icon>
                                </v-btn>
                            </v-col>
                            </v-row>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>

                <v-list-item-group v-if="files && files.length > 0">
                    <v-list-item
                        v-for="(file, index) in files"
                        :key="index"
                    >
                        <v-list-item-content>
                            <v-row no-gutters>
                            <v-col cols="10">
                                <v-list-item-title>{{ file.name }}</v-list-item-title>
                            </v-col>
                            <v-col cols="2" class="d-flex justify-end">
                                <v-icon @click="removeFile(index)">mdi-delete</v-icon>
                            </v-col>
                            </v-row>
                        </v-list-item-content>
                    </v-list-item>
                </v-list-item-group>
                
                <v-list-item v-if="(!files || files.length == 0) && (!importfiles || importfiles.length == 0)">
                    <v-list-item-content>No file added.</v-list-item-content>
                </v-list-item>
            </v-list>
            <v-file-input
                v-model="newFile"
                label="Add file"
                @change="addFile"
                prepend-icon="mdi-attachment"
            />
        </v-card-text>

        <v-card-actions>
            <v-icon>mdi-alert</v-icon> Once submitted, it is no longer possible to delete the file.
            <v-btn text @click="close">Close</v-btn>
            <v-btn color="primary" @click="submitTask">Submit</v-btn>
        </v-card-actions>

        <v-snackbar v-model="snackbar.show" :color="snackbar.color" :timeout="snackbar.timeout">
            {{ snackbar.message }}
        </v-snackbar>
    </v-card>
</template>

<script>
import { getFilesByTask, downloadFile } from '@/modules/data/file';

export default {
    props: ['currentTask', 'courseID'],
    data() {
        return {
            importfiles: [],
            files: [], 
            newFile: null,
            snackbar: {
                show: false,
                message: '',
                color: 'red',
                timeout: 4000
            }
        };
    },
    methods: {
        close() {
            this.$emit('close');
        },
        submitTask() {
            this.$emit('submit', this.currentTask, this.files);
        },
        addFile() {
            if (this.newFile) {
                this.files.push(this.newFile);
                this.newFile = null;
            }
        },
        removeFile(index) {
            if (index >= 0 && index < this.files.length) {
                this.files.splice(index, 1);
            }
        },
        getName(path){
            return path.split('\\').pop();
        },
        async downloadFile(fileId) {
            try {
                await downloadFile(fileId);
            } catch (error) {
                this.showError('File upload failed:' + error.message);
            }
        },
        showError(message) {
            this.snackbar.message = message;
            this.snackbar.show = true;
        }
    },
    async created() {
        try {
            this.importfiles = await getFilesByTask(this.currentTask.taskid) || [];
        } catch (error) {
            this.showError('Failed to retrieve the files:' + error.message);
        }
    }
}
</script>

<style scoped>
.v-card {
    border-radius: 8px;
    padding: 16px;
    max-width: 600px;
    margin: auto;
}
.v-card-title {
    border-bottom: 1px solid #e0e0e0;
    padding-bottom: 8px;
    font-weight: bold;
}
.v-card-subtitle {
    color: #555;
    font-size: 16px;
    margin-bottom: 10px;
    margin-top: 10px;
    line-height: 1.5; /*Ensure better readability.*/
    white-space: normal; /* Allow the text to wrap to the next line. */
    word-wrap: break-word; /*Break long words to prevent overflow. */
}
.v-card-text {
    margin-bottom: 16px;
    font-size: 14px;
}
.v-file-input {
    margin-top: 8px;
}
.v-list-item {
    border-bottom: 1px solid #e0e0e0;
}
.v-list-item:last-child {
    border-bottom: none;
}
</style>
