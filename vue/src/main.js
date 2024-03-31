import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import baiduMap from 'vue3-baidu-map-gl'

//由于此处引入element控件，所以，能直接用其中的图片样式
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import '@/assets/css/global.css'

const app = createApp(App)

app.use(router)
app.use(ElementPlus, {
    locale: zhCn,
})
app.use(baiduMap,{
    ak: 'c6IC2XhuOyxFOOLDwhWLEm9ASHtDSzDK',
    plugins: ['TrackAnimation'],
})
app.mount('#app')

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
    app.component(key, component)
}