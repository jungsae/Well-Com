import SharingForm from "@/views/SharingForm.vue";
import MemberCreate from "@/views/SharingRoomDetail.vue";

export const sharingRoutes = [
  {
    path: "/make/sharingRoom",
    name: "SharingForm",
    component: SharingForm,
  },
  {
    path: "/user/room/:id",
    name: "SharingRoomDetail",
    component: MemberCreate,
    props: true,
  },
];
