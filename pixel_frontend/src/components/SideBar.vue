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
            </span>
          </div>
        </v-list-item-content>
      </v-list-item>
      
      <v-list-item         
          @click="Linkclicked('/course', true)"
          class="specialbackground headline"
          v-if="!isMobile && courseselected"
      >
        <strong>Projet:</strong> {{ tr_courseName }}
      </v-list-item>

      <v-divider v-if="!isMobile && !courseselected"></v-divider>

      <v-list-item
        @click="Linkclicked('/profile/0', false)"
        link
        class="list-item"
      >
        <v-list-item-content>
          <v-icon>mdi-account</v-icon>
          <span v-if="!isMobile" class="item-title">Profile</span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/', false)"
        link
        class="list-item"
      >
        <v-list-item-content>
          <v-icon>mdi-view-dashboard</v-icon>
          <span v-if="!isMobile" class="item-title">DashBoard</span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/calendar', false)"
        link
        class="list-item"
      >
        <v-list-item-content>
          <v-icon>mdi-calendar</v-icon>
          <span v-if="!isMobile" class="item-title">Calendar</span>
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
          <span v-if="!isMobile" class="item-title">Admin Interface </span>
        </v-list-item-content>
      </v-list-item>

      <v-list-item
        link
        class="list-item"
        v-if="courseselected && !isMobile"
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
              <v-list-item-title>No discussion</v-list-item-title>
            </div>
          </div>

        </v-menu>
      </v-list-item>

      <v-list-item
        @click="Linkclicked('/tasks', true)"
        link
        class="list-item"
        v-if="courseselected"
      >
        <v-list-item-content>
          <v-icon>mdi-checkbox-marked-outline</v-icon>
          <span v-if="!isMobile" class="item-title">tasks</span>
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
import { getCourseDiscussions } from '@/modules/data/discussion';
import { getCourseById } from '@/modules/data/course';

export default {
data() {

return {
  drawerWidth: 300, 
  courseName: 'Select a course',
  discussions: [],
  logout: { title: 'Log out', icon: 'mdi-logout', route: '/logout' },
  isMobile: false,
  
  courseselected: false,
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
tr_courseName(){
  return this.courseName.length > 20
    ? this.courseName.substring(0, 20) + '...'
    : this.courseName;
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
  Linkclicked(path, injectcourse){
    if (injectcourse){
      this.$router.push(path + "/" + localStorage.getItem("course"));
      return;
    }
    localStorage.removeItem("course");
    this.$router.push(path);
  },
  async updateCourse() {
    const currentValue = localStorage.getItem("course");
    if (currentValue == null) {
      this.courseselected = false;
      this.courseName = 'Select a course';
      this.discussions = [];
    } else {
      const course = await getCourseById(currentValue);
      this.courseName = course.title;
      this.courseselected = true;
      this.discussions = await getCourseDiscussions(currentValue);
    }
  }
},
  async created() {
    if (!isLogged()){
      return;
    }
    const user = await getUser();
    this.isadmin = user.role.toLowerCase() !== "student";

    setInterval(() => {
      this.updateCourse();
    }, 100);
  }
};
</script>

<style scoped>

.v-navigation-drawer {
background-image: url('@/assets/background.png'); 
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