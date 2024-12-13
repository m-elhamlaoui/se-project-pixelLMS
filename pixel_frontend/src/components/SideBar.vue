<template>
  <v-navigation-drawer
    :width="drawerWidth"
    app
    permanent
    class="custom-drawer"
  >
    <v-list dense>
      <v-list-item>
        <v-list-item-content>
          <div style="font-size: xx-small; text-align: center;">
            <v-img max-height="40px" src="@/assets/logo.png"/>
            <span v-if="!isMobile">
              Ministère de l'Agriculture, de la Pêche Maritime,
              du Développement Rural et des Eaux et Forêts 
            </span>
          </div>
        </v-list-item-content>
      </v-list-item>
      
      <v-list-item         
          @click="Linkclicked('/project', true)"
          class="specialbackground headline"
          v-if="!isMobile && projectselected"
      >
        <strong>Projet:</strong> {{ tr_projectName }}
      </v-list-item>

      <v-divider v-if="!isMobile && !projectselected"></v-divider>

      <v-list-item
        @click="Linkclicked('/profile/0', false)"
        link
        class="list-item"
      >
        <v-list-item-content>
          <v-icon>mdi-account</v-icon>
          <span v-if="!isMobile" class="item-title">Profil</span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/', false)"
        link
        class="list-item"
      >
        <v-list-item-content>
          <v-icon>mdi-view-dashboard</v-icon>
          <span v-if="!isMobile" class="item-title">Tableau de bord</span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/calendar', false)"
        link
        class="list-item"
      >
        <v-list-item-content>
          <v-icon>mdi-calendar</v-icon>
          <span v-if="!isMobile" class="item-title">Calendrier</span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/admin', false)"
        link
        class="list-item"
        v-if="isadmin"
      >
        <v-list-item-content>
          <v-icon>mdi-server-security</v-icon>
          <span v-if="!isMobile" class="item-title">Interface admin </span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        link
        class="list-item"
        v-if="projectselected && !isMobile"
      >
        <v-menu location="end" dark>
          <template v-slot:activator="{ props }">
            <v-list-item-content  v-bind="props">
              <div style="width: 100%;">
                <v-icon>mdi-forum</v-icon>
                <span v-if="!isMobile" class="item-title">Discussions</span>
                <span style="float: right;"> <v-icon>mdi-menu-right</v-icon> </span>
              </div>
            </v-list-item-content>
          </template>

          <div class="dropdown">
            <div v-if="discussions.length > 0" v-for="(item, index) in discussions" :key="index" class="dropdown-item">
                <router-link :to="'/discussion/' + item.discussionid">
                  <v-list-item-title>{{ item.title }}</v-list-item-title>
                </router-link>
            </div>
            <div v-else class="dropdown-item">
              <v-list-item-title>Aucune discussion</v-list-item-title>
            </div>
          </div>

        </v-menu>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/tasks', true)"
        link
        class="list-item"
        v-if="projectselected"
      >
        <v-list-item-content>
          <v-icon>mdi-checkbox-marked-outline</v-icon>
          <span v-if="!isMobile" class="item-title">Tâches</span>
        </v-list-item-content>
      </v-list-item>

    </v-list>

    <v-list class="bottom" dense>
      <v-list-item :to="logout.route" link>
        <v-icon>{{ logout.icon }}</v-icon>
        <span v-if="!isMobile" class="item-title">{{ logout.title }}</span>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>

<script>
import { getUser, isLogged } from '@/modules/auth';
import { getProjectDiscussions } from '@/modules/data/discussion';
import { getProjectById } from '@/modules/data/project';

export default {
data() {

return {
  drawerWidth: 300, 
  projectName: 'Selectionner un projet',
  discussions: [],
  logout: { title: 'Se déconnecter', icon: 'mdi-logout', route: '/logout' },
  isMobile: false,
  
  projectselected: false,
  isadmin: false,
};
},
mounted() {
  this.updateLayout();
  window.addEventListener('resize', this.updateLayout);
},
beforeDestroy() {
  window.removeEventListener('resize', this.updateLayout);
},
computed: {
tr_projectName(){
  return this.projectName.length > 20
    ? this.projectName.substring(0, 20) + '...'
    : this.projectName;
}
},
methods: {
  updateLayout() {
    const width = window.innerWidth;
    if (width < 500) {
      this.drawerWidth = 60;
    } else if (width < 900) {
      this.drawerWidth = 200;
    } else {
      this.drawerWidth = 300;
    }
    this.isMobile = width < 500;
  },
  Linkclicked(path, injectproject){
    if (injectproject){
      this.$router.push(path + "/" + localStorage.getItem("project"));
      return;
    }
    localStorage.removeItem("project");
    this.$router.push(path);
  },
  async updateProject() {
    const currentValue = localStorage.getItem("project");
    if (currentValue == null) {
      this.projectselected = false;
      this.projectName = 'Sélectionner un projet';
      this.discussions = [];
    } else {
      const project = await getProjectById(currentValue);
      this.projectName = project.title;
      this.projectselected = true;
      this.discussions = await getProjectDiscussions(currentValue);
    }
  }
},
  async created() {
    if (!isLogged()){
      return;
    }
    const user = await getUser();
    this.isadmin = user.role.toLowerCase() !== "stagiaire";

    setInterval(() => {
      this.updateProject();
    }, 100);
  }
};
</script>

<style scoped>

.v-navigation-drawer {
background-image: url('@/assets/background.jpg'); 
color: #fff;
transition: width 0.3s ease;
}

.specialbackground {
background: linear-gradient(to left, transparent, #1e1e2f);
padding: 10px;
margin-top: 15px;
}

.v-divider{
margin-bottom: 10px;
margin-top: 10px;
}

.custom-drawer {
box-shadow: 0 2px 10px rgba(0, 0, 0, 0.5);
}

.v-list-item {
  color: #fff;
  transition: background-color 0.2s ease;
}

.v-list-item:hover {
background-color: #3a3a4f;
}

.v-icon {
  color: #fff;
}

.headline {
margin-bottom: 16px;
font-size: 1.2em;
}

.item-title {
margin-left: 8px;
font-weight: 500;
}


.bottom {
position: absolute;
bottom: 0;
width: 100%;
}

.dropdown {
  min-width: 200px;
  padding: 10px;
  margin: 10px;
  border-radius: 8px;
  background-color: transparent;
  color: #ffffff;
  font-size: 16px;
  transition: transform 0.3s ease;
}

.dropdown-item {
  padding: 12px;
  margin: 5px 0;
  border-radius: 6px;
  background-color: #3a3a4f;
  transition: background-color 0.3s ease, transform 0.3s ease;
  cursor: pointer;
}

.dropdown-item:hover {
  background-color: #5b5b72;
  transform: translateX(5px);
}

.v-list-item-title {
  color: #ffffff;
  transition: color 0.3s ease;
}

.dropdown-item:hover .v-list-item-title {
  color: #e0e0e0;
}

.dropdown-item a {
  text-decoration: none; /* Removes underline from links */
  color: #ffffff; /* Ensures the text color matches */
}

.dropdown-item a:hover {
  text-decoration: none; /* Ensures underline is removed on hover */
}

</style>