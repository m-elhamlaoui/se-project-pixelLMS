<template>
    Logout!
</template>

<script>
import { logout } from '@/modules/auth';

export default {
    async created() {
        await logout();
        this.$router.push('/auth');
    },
};

</script>