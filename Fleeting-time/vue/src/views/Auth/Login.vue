<!-- 登录页面 -->
<template>
    <div class="login-body">
        <div class="login-form">
            <div class="login-image">
                <img src="/assets/图标.jpeg" alt="登录背景图">
            </div>
            <el-form ref="ruleFormRef"
                     :model="LoginForm"
                     :rules="rules"
                     label-width="auto">
                <p> 继续你的旅程吧！</p>
                <br>

                <!--登录方式切换-->
                <el-tabs v-model="loginMethod" class="login-tabs">
                    <el-tab-pane label="账号登录" name="account" />
                    <el-tab-pane label="手机登录" name="phone" />
                    <el-tab-pane label="邮箱登录" name="email" />
                </el-tabs>

                <!-- 动态表单字段 -->
                <el-form-item v-if="loginMethod === 'account'" label="账号" prop="account">
                    <el-input v-model="LoginForm.account" placeholder="请输入账号" />
                </el-form-item>

                <el-form-item v-if="loginMethod === 'phone'" label="手机号" prop="phone">
                    <el-input v-model="LoginForm.phone" placeholder="请输入手机号" />
                </el-form-item>

                <el-form-item v-if="loginMethod === 'email'" label="邮箱" prop="email">
                    <el-input v-model="LoginForm.email" placeholder="请输入邮箱" />
                </el-form-item>

                <!-- 通用密码框 -->
                <el-form-item label="密码" prop="password">
                    <el-input placeholder="请输入您的密码" type="password" v-model="LoginForm.password" show-password />
                </el-form-item>

                <!-- 登录和取消按钮 -->
                <el-form-item label="" class="button-group">
                    <div class="button-row">
                        <el-button type="primary" class="login-btn" @click="handleLogin" :loading="loading" :disabled="loading">{{ loading ? '登录中...' : '登录' }}</el-button>
                        <el-button type="primary" class="cancel-btn" @click="handleCancel" :disabled="loading">取消</el-button>
                    </div>

                    <div class="register-link">
                        还没有账号？
                        <el-button type="primary" @click="goToRegister" :disabled="loading" style="padding: 0; margin-left: 5px;">立即注册</el-button>
                    </div>
                </el-form-item>
            </el-form>
        </div>
    </div>
</template>

<script setup>
    import { reactive, ref } from "vue"
    import { useRouter } from "vue-router"
    import { publicAxios } from "@/utils/request"
    import { ElMessage } from 'element-plus'
    import { useAuthStore } from '@/stores/auth'

    const loading = ref(false) // 控制加载状态
    const loginMethod = ref('account') // 登录方式：account、phone、email
    const authStore = useAuthStore()

    const LoginForm = reactive({
        account: '',
        phone: '',
        email: '',
        password: ''
    })

    const ruleFormRef = ref()

    const rules = {
        account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
        password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
    }

    const router = useRouter()

    const handleLogin = () => {
        ruleFormRef.value.validate(async (valid) => {
            if (valid) {
                loading.value = true
                try {
                    let payload = {}
                    if (loginMethod.value === 'account') {
                        payload = { identifier: LoginForm.account, password: LoginForm.password }
                    } else if (loginMethod.value === 'phone') {
                        payload = { identifier: LoginForm.phone, password: LoginForm.password }
                    } else {
                        payload = { identifier: LoginForm.email, password: LoginForm.password }
                    }

                    const response = await publicAxios.post('user/login', payload, {
                        headers: { 'Content-Type': 'application/json' }
                    })

                    if (response.data.code === 200) {
                        ElMessage.success('登录成功！')
                        console.log(response)
                        authStore.login(response.data.data)
                        router.push('/')
                    } else {
                        ElMessage.error(response.data.message || '登录失败')
                    }
                } catch (error) {
                    console.error(error)
                    ElMessage.error('用户名或密码错误')
                } finally {
                    loading.value = false
                }
            }
        })
    }

    // 取消登录
    const handleCancel = () => {
        router.push('/')
    }

    // 去注册页
    const goToRegister = () => {
        router.push('/register')
    }
</script>


<style lang="css" scoped>

    .login-tabs {
        margin-bottom: 1rem;
    }

    .login-body:before {
        content: "";
        position: fixed;
        top: 0;
        left: 0;
        width: 100%;
        height: 100%;
        background-image: url("/assets/背景1.jpeg");
        background-size: cover;
        background-position: center; /* 居中 */
        filter: brightness(70%); /* 调整亮度 */
        z-index: -1; /* 让背景在最底层 */
    }

    .login-body {
        display: grid;
        place-items: center;
        min-height: 100vh;
    }

    .login-form {
        display: grid;
        grid-template-columns: 1fr 1fr; /* 两列等宽布局 */
        align-items: center; /* 垂直居中 */
        gap: 40px; /* 图片和表单之间的间距 */
        background-color: rgba(255, 255, 255, 0.8);
        background-position: center;
        padding: 8rem;
        border-radius: 20px;
        width: 800px;
    }

    .login-image {
        display: flex;
        justify-content: center;
    }

        .login-image img {
            max-width: 100%;
            max-height: 600px;
            object-fit: contain;
            border-radius: 8px;
        }

    .el-button {
        color: white;
        background-color: #0aa3fb;
        border-radius: 8px;
        width: 100%;
    }

    .el-input {
        border-radius: 8px;
    }


    p {
        font-size: 150%;
        font-weight: bold;
    }

    .button-group {
        margin-top: 20px;
        display: flex;
        justify-content: flex-end;
        align-items: center;
        width: 100%;
    }

    .button-row {
        display: flex;
        gap: 10px; /* 按钮之间的间距 */
        width: 100%;
    }

    .register-link {
        margin-top: 15px;
        display: flex;
        align-items: center;
        gap: 5px;
        font-size: 14px;
        color: #666;
        white-space: nowrap;
        width: 100%;
    }

    .login-btn, .cancel-btn {
        flex: 1; /* 使两个按钮等宽 */
    }
</style>
