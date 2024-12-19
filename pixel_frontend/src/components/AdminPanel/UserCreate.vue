<template>
    <v-card>
      <v-card-title>
        <span class="headline">{{headertext}} a user</span>
      </v-card-title>
      <v-card-text>
        <v-form ref="form" v-model="valid">
          <v-text-field
            v-model="newUser.name"
            label="Full Name"
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
            label="Birth Date"
            type="date"
            :rules="[rules.required]"
            required
          ></v-text-field>
          <v-text-field
            v-model="newUser.phonenumber"
            label="Phone number"
            :rules="[rules.required, rules.phone]"
            required
          ></v-text-field>
          <v-select
            v-model="newUser.role"
            :items="roles"
            label="Role"
            :rules="[rules.required, rules.role]"
            required
          ></v-select>
          <v-text-field
            v-model="newUser.password"
            label="Password"
            :rules="[rules.required]"
            type="password"
            required
          ></v-text-field>

          <v-file-input v-if="filesneeded && newUser.role == 'student'"
              v-model="CVfile"
              label="School certificate."
              prepend-icon="mdi-attachment"
              :rules="[rules.files]"
              required
          />
          <v-file-input v-if="filesneeded && newUser.role == 'student'"
              v-model="Motivationfile"
              label="Baccalaureate certificate."
              prepend-icon="mdi-attachment"
              :rules="[rules.files]"
              required
          />
          <v-file-input v-if="filesneeded && newUser.role == 'student'"
              v-model="Assurancefile"
              label="Insurance"
              prepend-icon="mdi-attachment"
              :rules="[rules.files]"
              required
          />

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
    props: ['user', 'useradmin'],
    data() {
      return {
        valid: false,
        headertext: 'Create',
        newUser: {
          email: '',
          name: '',
          birthdate: '',
          phonenumber: '',
          role: 'student',
          password: '',
        },

        files: [],
        CVfile: null,
        Motivationfile: null,
        Assurancefile: null,
        filesneeded: true,

        roles: ['student', 'teacher', 'admin'], 
        rules: {
          required: value => !!value || 'Champ requis.',
          email: value => {
            const pattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
            return pattern.test(value) || 'Email invalid.'
          },
          phone: value => {
            const pattern = /^\d{10}$/
            return pattern.test(value) || 'Phone number invalid.'
          },
          role: value => {
            if (value !== 'student' && !this.useradmin) {
              return "You do not have the rights to create this user.";
            }
            return true;
          },
          files: value => {
            
            if (this.filesneeded && this.newUser.role == 'student') {
              return value.length > 0 || 'Required files';
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

          if (this.filesneeded && this.newUser.role == 'student') {
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
        this.headertext = 'Edit';
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
  