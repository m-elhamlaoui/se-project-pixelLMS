<template>
    <v-card>
      <v-card-title>
        <span class="headline">{{headertext}} un utilisateur</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          <v-text-field
            v-model="newUser.name"
            label="Nom"
            :rules="[rules.required]"
            required
          ></v-text-field>
          <v-text-field
            v-model="newUser.email"
            label="Email"
            :rules="[rules.required, rules.email]"
            required
          ></v-text-field>
          <v-text-field
            v-model="newUser.birthdate"
            label="Date de naissance"
            type="date"
            :rules="[rules.required]"
            required
          ></v-text-field>
          <v-text-field
            v-model="newUser.phonenumber"
            label="Numéro de téléphone"
            :rules="[rules.required, rules.phone]"
            required
          ></v-text-field>
          <v-select
            v-model="newUser.role"
            :items="roles"
            label="Rôle"
            :rules="[rules.required, rules.role]"
            required
          ></v-select>
          <v-text-field
            v-model="newUser.password"
            label="Mot de passe"
            :rules="[rules.required]"
            type="password"
            required
          ></v-text-field>

          <v-file-input v-if="filesneeded && newUser.role == 'stagiaire'"
              v-model="CVfile"
              label="CV"
              prepend-icon="mdi-attachment"
              :rules="[rules.files]"
              required
          />
          <v-file-input v-if="filesneeded && newUser.role == 'stagiaire'"
              v-model="Motivationfile"
              label="Lettre de motivation"
              prepend-icon="mdi-attachment"
              :rules="[rules.files]"
              required
          />
          <v-file-input v-if="filesneeded && newUser.role == 'stagiaire'"
              v-model="Assurancefile"
              label="Assurance"
              prepend-icon="mdi-attachment"
              :rules="[rules.files]"
              required
          />

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
    props: ['user', 'useradmin'],
    data() {
      return {
        valid: false,
        headertext: 'Créer',
        newUser: {
          email: '',
          name: '',
          birthdate: '',
          phonenumber: '',
          role: 'stagiaire',
          password: '',
        },

        files: [],
        CVfile: null,
        Motivationfile: null,
        Assurancefile: null,
        filesneeded: true,

        roles: ['stagiaire', 'encadrant', 'superadmin'], 
        rules: {
          required: value => !!value || 'Champ requis.',
          email: value => {
            const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
            return pattern.test(value) || 'Email invalide.'
          },
          phone: value => {
            const pattern = /^\d{10}$/
            return pattern.test(value) || 'Numéro de téléphone invalide.'
          },
          role: value => {
            if (value !== 'stagiaire' && !this.useradmin) {
              return "Vous n'avez pas les droits pour créer cet utilisateur";
            }
            return true;
          },
          files: value => {
            
            if (this.filesneeded && this.newUser.role == 'stagiaire') {
              return value.length > 0 || 'Fichiers requis';
            }
            return true;
          }
        }
      }
    },
    methods: {
      submit() {
        if (this.valid) {
          const files = [];

          if (this.filesneeded && this.newUser.role == 'stagiaire') {
            if (this.Motivationfile) {
              files.push(this.Motivationfile);
            }
            if (this.CVfile) {
              files.push(this.CVfile);
            }
            if (this.Assurancefile) {
              files.push(this.Assurancefile);
            }
          }

          this.$emit('submit', this.newUser, files);
        }
      },
      close() {
        this.$emit('close');
      }
    },
    created() {
      if (this.user) {
        this.newUser = this.user;
        this.headertext = 'Modifier';
        this.filesneeded = false;
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
  