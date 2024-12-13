<template>
    <v-card>
    <v-card-title>
        <span class="headline">{{Action}} une Tâche</span>
    </v-card-title>
    
    <v-card-subtitle>
        <v-form ref="form" v-model="valid">
        <v-text-field
            v-model="task.title"
            :rules="titleRules"
            label="Titre"
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
            label="Date d'échéance"
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
        <v-btn text @click="close">Annuler</v-btn>
        <v-btn
        color="primary"
        @click="submit"
        :disabled="!valid"
        >Soumettre</v-btn>
    </v-card-actions>
    </v-card>
  </template>
  
  <script>

  export default {
    props: ['optionalTaskDetails', 'projectID'],
    data() {
      return {
        Action: 'Créer',
        valid: false,
        menu: false,
        task: {
          title: '',
          description: '',
          duedate: '',
          status: 'Non Commencée',
          taskid: -1,
          projectid: this.projectID
        },
        titleRules: [(v) => !!v || 'Le titre est requis'],
        descriptionRules: [(v) => !!v || 'La description est requise'],
        dueDateRules: [(v) => !!v || 'La date d\'échéance est requise'],
        statusOptions: ['Non Commencée', 'En Cours', 'Terminée'],
        maxDate: new Date().toISOString().substr(0, 10) // Définit la date max au jour actuel
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
        this.Action = "Mettre à jour"
      }
    }
  };
  </script>
