// Styles
import '@mdi/font/css/materialdesignicons.css'
import 'vuetify/styles'

import { createVuetify } from 'vuetify';
import { VCalendar } from 'vuetify/labs/VCalendar';

export default createVuetify({
  components: {
    VCalendar,
  },
});