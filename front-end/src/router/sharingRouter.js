import SharingForm from "@/views/SharingForm.vue";
import SharingRoomDetail from "@/views/SharingRoomDetail.vue";

export const sharingRoutes = [
  {
    path: "/make/sharingRoom",
    name: "SharingForm",
    component: SharingForm,
  },
  {
    path: "/user/room/:id",
    name: "SharingRoomDetail",
    component: SharingRoomDetail,
    props: true,
  },
];
