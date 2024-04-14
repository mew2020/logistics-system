import {createRouter, createWebHistory} from 'vue-router'
import { ElMessage } from "element-plus";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'Manager',
            // 此处是第一个页面跳转的东西，下面的/home是对应路由，自然可以自己编一个，简单
            component: () => import('@/views/Manager.vue'),
            redirect: '/login',
            children: [
                {path: 'home', name: 'Home', component: () => import('@/views/manager/Home.vue')},
                //此处是那个主页框Manage下的路由,此处大写的是在上面的controller里用
                {path: 'jxx', name: 'Jxx', component: () => import('@/views/manager/jxx.vue')},
                {path: 'tianjia', name: 'Tianjia', component: () => import('@/views/manager/zaxiang/jxx.vue')},
                {path: 'xinzeng', name: 'Xinzeng', component: () => import('@/views/manager/zaxiang/wenz.vue')},
                {path: 'shenhe', name: 'Shenhe', component: () => import('@/views/manager/shenhe.vue')},
                {path: 'chengji', name: 'Chengji', component: () => import('@/views/manager/chengji.vue')},
                {path: 'gongshi', name: 'Gongshi', component: () => import('@/views/manager/gongshi.vue')},
                {path: 'video', name: 'Video', component: () => import('@/views/manager/video.vue')},
                {path: 'wenz', name: 'Wenz', component: () => import('@/views/manager/wenz.vue')},
                {path: 'fushen', name: 'Fushen', component: () => import('@/views/manager/guanli/shenhe.vue')},
                {path: 'jubao', name: 'Jubao', component: () => import('@/views/manager/guanli/jubao.vue')},
                {path: 'lishi', name: 'Lishi', component: () => import('@/views/manager/guanli/lishi.vue')},

                {path: 'map-demo', name: 'map-demo', component: () => import('@/views/manager/map-demo.vue')},
                {path: 'map-demo2', name: 'map-demo2', component: () => import('@/views/manager/map-demo2.vue')},
                {path: 'site', name: 'Site', component: () => import('@/views/manager/site/site.vue')},
                {path: 'siteEdit', name: 'SiteEdit', component: () => import('@/views/manager/site/siteEdit.vue')},
                {path: 'user', name: 'User', component: () => import('@/views/manager/user.vue')},
                {path: 'admin', name: 'Admin', component: () => import('@/views/manager/admin.vue')},
                {path: 'order', name: 'Order', component: () => import('@/views/manager/order/order.vue')},
                {path: 'orderDetail', name: 'OrderDetail', component: () => import('@/views/manager/order/orderDetail.vue')},
                {path: 'PickupAndDelivery', name: 'PickupAndDelivery', component: () => import('@/views/PickupAndDelivery.vue')},
                {path: 'stock', name: 'Stock', component: () => import('@/views/stock.vue')},
            ]
        },
        //每个页面都有路由，在该文件中添加新的路由
        {
            path: '/login',
            name: 'login',
            component: () => import('@/views/login.vue'),
        }, {
            path: '/register',
            name: 'register',
            component: () => import('@/views/Register.vue'),
        },
    ]
})

router.beforeEach((to, from, next) => {
    if(to.path.startsWith('/login')) {
        localStorage.removeItem('admin')
        next()
    } else {
        // let admin = localStorage.getItem('admin');
        // if(!admin) {     
        //     ElMessage.error("您当前尚未登录，请登录后再尝试！")
        //     next({path:'/login'})
        // }
        next()
    }

}) 

export default router
