<template>
    <v-card>
    <v-card-title>
        <span class="headline">{{Action}} Task</span>
    </v-card-title>
    
    <v-card-subtitle>
        <v-form ref="form" v-model="valid">
        <v-text-field
            v-model="task.title"
            :rules="titleRules"
            label="Title"
            required
        ></v-text-field>
        <v-textarea
            v-model="task.description"
            :rules="descriptionRules"
            label="Description"
            rows="4"
            required
        ></v-textarea>
        <v-text-field
            v-model="task.duedate"
            label="Deadline"
            type="date"
            :min="maxDate"
            :rules="dueDateRules"
            required
        >
        </v-text-field>
        <v-select
            v-model="task.status"
            :items="statusOptions"
            label="Statut"
            required
        ></v-select>
        </v-form>
    </v-card-subtitle>
    <v-card-actions>
        <v-spacer></v-spacer>
        <v-btn text @click="close">Cancel</v-btn>
        <v-btn
        color="primary"
        @click="submit"
        :disabled="!valid"
        >Submit</v-btn>
    </v-card-actions>
    </v-card>
  </template>
  
  <script>

  export default {
    props: ['optionalTaskDetails', 'courseID'],
    data() {
      return {
        Action: 'Create',
        valid: false,
        menu: false,
        task: {
          title: '',
          description: '',
          duedate: '',
          status: 'Not started',
          taskid: -1,
          courseid: this.courseID
        },
        titleRules: [(v) => !!v || 'Required title'],
        descriptionRules: [(v) => !!v || 'Required description'],
        dueDateRules: [(v) => !!v || 'Required deadline'],
        maxDate: new Date().toISOString().substr(0, 10) 
      };
    },
    methods: {
    close() {
        this.$emit('close');
    },
    submit() {
      if (this.$refs.form.validate()) {
          this.$emit('submit', this.task);
          this.close();
      }
    }
    },
    mounted() {
      if (this.optionalTaskDetails) {
        this.task.title = this.optionalTaskDetails.title;
        this.task.description = this.optionalTaskDetails.description;
        this.task.duedate = this.optionalTaskDetails.duedate;
        this.task.status = this.optionalTaskDetails.status;
        this.task.taskid = this.optionalTaskDetails.taskid;
        this.Action = "Update"
      }
    }
  };
  </script>
