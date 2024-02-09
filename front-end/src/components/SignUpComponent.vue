<template>
  <v-dialog v-model="dialog" max-width="500px">
    <v-card>
      <v-card-actions>
        <v-btn color="primary" text @click.stop="dialog = false">Close2</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>
</template>

<script>
export default {
  props: {
    value: {
      type: Boolean,
      required: true,
    },
  },
  computed: {
    dialog: {
      get() {
        return this.value;
      },
      set(value) {
        this.$emit('input', value);
      },
    },
  },
};
</script>