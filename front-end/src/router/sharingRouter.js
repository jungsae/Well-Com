import SharingForm from "@/views/SharingForm.vue";
import SharingRoomDetail from "@/views/SharingRoomDetail.vue";
import NanumGameRoom from "@/views/NanumGameRoom.vue";

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
  {
    path: "/user/nanumGame/:id",
    name: "NanumGameRoom",
    component: NanumGameRoom,
    props: true,
  },
];
