export const sharingRoutes = [
  {
    path: "/sharingHome",
    name: "SharingHome",
    component: ()=>import("@/views/SharingHome.vue"),
  },
  {
    path: "/make/sharingRoom",
    name: "SharingForm",
    component: ()=>import("@/views/SharingForm.vue"),
  },
  {
    path: "/user/room/:id",
    name: "SharingRoomDetail",
    component: ()=>import("@/views/SharingRoomDetail.vue"),
    props: true,
  },
];