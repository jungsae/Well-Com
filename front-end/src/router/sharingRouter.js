import SharingHome from "@/views/SharingHome.vue";
import SharingForm from "@/views/SharingForm.vue";
import MemberCreate from "@/views/SharingRoomDetail.vue";

export const sharingRoutes = [
  {
    path: "/sharingHome",
    name: "SharingHome",
    component: SharingHome,
  },
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