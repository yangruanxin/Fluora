<!-- 注册页面 -->
<template>
    <div class="register-body">
        <div class="register-form">
            <div class="login-image">
                <img src="/assets/图标.jpeg" alt="登录背景图">
            </div>
            <el-form ref="ruleFormRef"
                     :model="RegisterForm"
                     :rules="rules"
                     label-width="auto">
<<<<<<< HEAD
                <p>开启你的旅程吧！</p>
                <br>

                <!-- 注册方式切换 -->
                <el-tabs v-model="registerMethod" class="register-tabs">
                    <el-tab-pane label="账号注册" name="account" />
                    <el-tab-pane label="手机注册" name="phone" />
                    <el-tab-pane label="邮箱注册" name="email" />
                </el-tabs>

                <!-- 动态表单字段 -->
                <el-form-item v-if="registerMethod === 'account'" label="账号" prop="account">
                    <el-input placeholder="请输入您的账号" v-model="RegisterForm.account" />
                </el-form-item>

                <template v-if="registerMethod === 'phone'">
                    <el-form-item label="手机号" prop="phone">
                        <el-input placeholder="请输入手机号" v-model="RegisterForm.phone" />
                    </el-form-item>
                    <el-form-item label="验证码" prop="code">
                        <el-input v-model="RegisterForm.code" placeholder="短信验证码" style="width: 160px; margin-right: 10px;" />
                        <el-button @click="sendSmsCode" :loading="smsCaptchaLoading" :disabled="smsCountdown > 0">
                            {{ smsCountdown > 0 ? `${smsCountdown}s后重新获取` : '获取验证码' }}
                        </el-button>
                    </el-form-item>
                </template>

                <template v-if="registerMethod === 'email'">
                    <el-form-item label="邮箱" prop="email">
                        <el-input placeholder="请输入邮箱" v-model="RegisterForm.email" />
                    </el-form-item>
                    <el-form-item label="验证码" prop="code">
                        <el-input v-model="RegisterForm.code" placeholder="输入邮箱验证码" style="width: 160px; margin-right: 10px;" />
                        <el-button @click="sendEmailCode" :loading="emailCaptchaLoading" :disabled="emailCountdown > 0">
                            {{ emailCountdown > 0 ? `${emailCountdown}s后重新获取` : '获取验证码' }}
                        </el-button>
                    </el-form-item>
                </template>
                
                <!-- 密码框 -->
                <el-form-item label="密码" prop="password">
                    <el-input placeholder="请输入您的密码" type="password" v-model="RegisterForm.password" show-password />
                </el-form-item>
                <!-- 确认密码框 -->
                <el-form-item label="密码" prop="confirmPassword">
                    <el-input placeholder="请再次输入您的密码以确认" type="password" v-model="RegisterForm.confirmPassword" show-password />
                </el-form-item>
                <el-form-item label="" class="button-group">
                    <div class="button-row">
                        <el-button type="primary" class="login-btn" @click="handleRegister">注册</el-button>
                        <el-button type="primary" class="cancel-btn" @click="handleCancel">取消</el-button>
                    </div>
                </el-form-item>
            </el-form>
=======

                <el-tabs v-model="activeTab" type="card">
                    <!-- 账号注册 -->
                    <el-tab-pane label="账号注册" name="account">
                        <el-form :model="accountForm" :rules="rules.account" ref="accountFormRef" label-width="auto">
                            <p>开启你的旅程吧！</p>
                            <br>
                            <!-- 账号框 -->
                            <el-form-item label="账号" prop="account">
                                <el-input placeholder="请输入您的账号" v-model="RegisterForm.account" />
                            </el-form-item>
                            <!-- 密码框 -->
                            <el-form-item label="密码" prop="password">
                                <el-input placeholder="请输入您的密码" type="password" v-model="RegisterForm.password" show-password />
                            </el-form-item>
                            <!-- 确认密码框 -->
                            <el-form-item label="密码" prop="confirmPassword">
                                <el-input placeholder="请再次输入您的密码以确认" type="password" v-model="RegisterForm.confirmPassword" show-password />
                            </el-form-item>
                            <el-form-item label="" class="button-group">
                                <div class="button-row">
                                    <el-button type="primary" class="login-btn" @click="handleRegister">注册</el-button>
                                    <el-button type="primary" class="cancel-btn" @click="handleCancel">取消</el-button>
                                </div>
                            </el-form-item>
                        </el-form>
                        <el-tab-pane>

                            <!-- 邮箱注册 -->
                            <el-tab-pane label="邮箱注册" name="email">
                                <el-form :model="emailForm" :rules="rules.email" ref="emailFormRef" label-width="auto">
                                    <p>开启你的旅程吧！</p>
                                    <br>
                                    <!--邮箱框-->
                                    <el-form-item label="邮箱" prop="email">
                                        <el-input v-model="emailForm.email" placeholder="请输入您的邮箱" />
                                    </el-form-item>
                                    <!--密码框-->
                                    <el-form-item label="密码" prop="password">
                                        <el-input type="password" v-model="emailForm.password" placeholder="请输入密码" show-password />
                                    </el-form-item>
                                    <!--确认密码框-->
                                    <el-form-item label="确认密码" prop="confirmPassword">
                                        <el-input type="password" v-model="emailForm.confirmPassword" placeholder="请确认密码" show-password />
                                    </el-form-item>
                                    <!--邮箱验证码框-->
                                    <el-form-item label="验证码" prop="code">
                                        <el-input v-model="emailForm.code" placeholder="输入邮箱验证码" style="width: 160px; margin-right: 10px;" />
                                        <el-button @click="sendEmailCode" :loading="emailCaptchaLoading">获取验证码</el-button>
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" @click="handleRegister">注册</el-button>
                                        <el-button @click="handleCancel">取消</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-tab-pane>
                            <!-- 手机注册-->
                            <el-tab-pane label="手机注册" name="phone">
                                <el-form :model="phoneForm" :rules="rules.phone" ref="phoneFormRef" label-width="auto">
                                    <p>开启你的旅程吧！</p>
                                    <br>
                                    <!--手机号框-->
                                    <el-form-item label="手机号" prop="phone">
                                        <el-input v-model="phoneForm.phone" placeholder="请输入手机号" />
                                    </el-form-item>
                                    <!--密码框-->
                                    <el-form-item label="密码" prop="password">
                                        <el-input type="password" v-model="phoneForm.password" placeholder="请输入密码" show-password />
                                    </el-form-item>
                                    <!--确认密码框-->
                                    <el-form-item label="确认密码" prop="confirmPassword">
                                        <el-input type="password" v-model="phoneForm.confirmPassword" placeholder="请确认密码" show-password />
                                    </el-form-item>
                                    <!--短信验证码框-->
                                    <el-form-item label="短信验证码" prop="code">
                                        <el-input v-model="phoneForm.code" placeholder="短信验证码" style="width: 160px; margin-right: 10px;" />
                                        <el-button @click="sendSmsCode" :loading="smsCaptchaLoading">获取验证码</el-button>
                                    </el-form-item>
                                    <el-form-item>
                                        <el-button type="primary" @click="handleRegister">注册</el-button>
                                        <el-button @click="handleCancel">取消</el-button>
                                    </el-form-item>
                                </el-form>
                            </el-tab-pane>
                </el-tabs>
>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791
        </div>
    </div>
</template>

<script setup>

<<<<<<< HEAD
    import { reactive, ref } from "vue"
=======
    import { reactive, ref, onMounted } from "vue"
>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791
    import { useRouter } from "vue-router"
    import { publicAxios } from '@/utils/request'
    import { ElMessage } from 'element-plus'

<<<<<<< HEAD
    const registerMethod = ref('account')

    const RegisterForm = reactive({
        account: '',
        phone: '',
        email: '',
        code: '',
=======

    // 表单引用
    const accountFormRef = ref()
    const emailFormRef = ref()
    const phoneFormRef = ref()

    const accountForm = reactive({
        account: '',
>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791
        password: '',
        confirmPassword: ''
    })

<<<<<<< HEAD
    //创建路由实例
    const router = useRouter()
=======
    const emailForm = reactive({
        email: '',
        password: '',
        confirmPassword: '',
        //captchaInput: '',
        code: ''
    })

    const phoneForm = reactive({
        phone: '',
        password: '',
        confirmPassword: '',
        //captchaInput: '',
        code: ''
    })

    //创建路由实例
    const router = useRouter()

    const activeTab = ref('account')
    const captchaImageUrl = ref('')
    const emailCaptchaLoading = ref(false)
    const smsCaptchaLoading = ref(false)

    //处理取消按钮点击事件
    const handleCancel = () => {
        router.push('/')
    }
>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791

    const ruleFormRef = ref()

    // 验证规则
    const rules = {
<<<<<<< HEAD
        account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
        email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }],
        code: [{ required: true, message: '请输入验证码', trigger: 'blur' }],
        password: [
            { required: true, message: '请输入密码', trigger: 'blur' },
            { min: 6, message: '密码至少6位', trigger: 'blur' }
        ],
        confirmPassword: [
            { required: true, message: '请确认密码', trigger: 'blur' },
            {
                validator: (rule, value, callback) => {
                    if (value !== RegisterForm.password) {
                        callback(new Error('两次输入密码不一致'))
                    } else {
                        callback()
                    }
                },
                trigger: 'blur'
            }
        ]
    }

    const smsCaptchaLoading = ref(false)
    const emailCaptchaLoading = ref(false)
    const smsCountdown = ref(0)
    const emailCountdown = ref(0)

    const sendSmsCode = () => {
        smsCaptchaLoading.value = true
        setTimeout(() => {
            smsCaptchaLoading.value = false
            smsCountdown.value = 60
            const timer = setInterval(() => {
                smsCountdown.value--
                if (smsCountdown.value <= 0) clearInterval(timer)
            }, 1000)
            ElMessage.success('验证码已发送')
        }, 1000)
    }

    const sendEmailCode = () => {
        emailCaptchaLoading.value = true
        setTimeout(() => {
            emailCaptchaLoading.value = false
            emailCountdown.value = 60
            const timer = setInterval(() => {
                emailCountdown.value--
                if (emailCountdown.value <= 0) clearInterval(timer)
            }, 1000)
            ElMessage.success('验证码已发送')
        }, 1000)
    }

    //处理取消按钮点击事件
    const handleCancel = () => {
        router.push('/')
=======
        account: {
            account: [{ required: true, message: '请输入账号', trigger: 'blur' }],
            password:
                [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码至少6位', trigger: 'blur' }
                ],
            confirmPassword: [
                { required: true, message: '请确认密码', trigger: 'blur' },
                {
                    validator: (rule, value, callback) => {
                        if (value !== RegisterForm.password) {
                            callback(new Error('两次输入密码不一致'))
                        } else {
                            callback()
                        }
                    },
                    trigger: 'blur'
                }
            ]
        },
        email: {
            email: [{ required: true, message: '请输入邮箱', trigger: 'blur' }]
              password:
                [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码至少6位', trigger: 'blur' }
                ],
            confirmPassword: [
                { required: true, message: '请确认密码', trigger: 'blur' },
                {
                    validator: (rule, value, callback) => {
                        if (value !== RegisterForm.password) {
                            callback(new Error('两次输入密码不一致'))
                        } else {
                            callback()
                        }
                    },
                    trigger: 'blur'
                }
            ]
              code: [{ required: true, message: '请输入邮箱验证码', trigger: 'blur' }]
        }
          phone: {
            phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
            password:
                [
                    { required: true, message: '请输入密码', trigger: 'blur' },
                    { min: 6, message: '密码至少6位', trigger: 'blur' }
                ],
            confirmPassword: [
                { required: true, message: '请确认密码', trigger: 'blur' },
                {
                    validator: (rule, value, callback) => {
                        if (value !== RegisterForm.password) {
                            callback(new Error('两次输入密码不一致'))
                        } else {
                            callback()
                        }
                    },
                    trigger: 'blur'
                }
            ]
              code: [{ required: true, message: '请输入邮箱验证码', trigger: 'blur' }]
        }
    }

    const sendEmailCode = async () => {
        if (!emailForm.email || !emailForm.captchaInput) return ElMessage.warning('请填写邮箱和图形验证码')
        emailCaptchaLoading.value = true
        try {
            const res = await publicAxios.post('/email/send-code', {
                email: emailForm.email,
                captcha: emailForm.captchaInput
            })
            ElMessage.success(res.data.message || '验证码已发送')
        } catch {
            ElMessage.error('发送失败')
        } finally {
            emailCaptchaLoading.value = false
        }
    }

    const sendSmsCode = async () => {
        if (!phoneForm.phone || !phoneForm.captchaInput) return ElMessage.warning('请填写手机号和图形验证码')
        smsCaptchaLoading.value = true
        try {
            const res = await publicAxios.post('/sms/send-code', {
                phone: phoneForm.phone,
                captcha: phoneForm.captchaInput
            })
            ElMessage.success(res.data.message || '验证码已发送')
        } catch {
            ElMessage.error('发送失败')
        } finally {
            smsCaptchaLoading.value = false
        }
>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791
    }

    const handleRegister = () => {
        const refMap = {
            account: accountFormRef,
            email: emailFormRef,
            phone: phoneFormRef
        }

        refMap[activeTab.value].value.validate(async (valid) => {
            if (valid) {
                try {
<<<<<<< HEAD
                    let payload = {
                        password: RegisterForm.password,
                        confirmPassword: RegisterForm.confirmPassword
                    }

                    if (registerMethod.value === 'account') {
                        payload.username = RegisterForm.account
                    } else if (registerMethod.value === 'phone') {
                        payload.phone = RegisterForm.phone
                        payload.code = RegisterForm.code
                    } else if (registerMethod.value === 'email') {
                        payload.email = RegisterForm.email
                        payload.code = RegisterForm.code
                    }
                    // 使用 publicAxios 发起注册请求
                    const response = await publicAxios.post('/user/register', {
                        username: RegisterForm.account,
                        password: RegisterForm.password
                    })

=======
                    let payload = {}
                    let url = ''

                    if (activeTab.value === 'account') {
                        payload = {
                            username: accountForm.account,
                            password: accountForm.password
                        }
                        url = '/user/register'
                    } else if (activeTab.value === 'email') {
                        payload = {
                            email: emailForm.email,
                            password: emailForm.password,
                            code: emailForm.code
                        }
                        url = '/user/registeremail'
                    } else {
                        payload = {
                            phone: phoneForm.phone,
                            password: phoneForm.password,
                            code: phoneForm.code
                        }
                        url = '/user/registerphone'
                    }

                    // 使用 publicAxios 发起注册请求
                    const response = await publicAxios.post(url, payload)

>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791
                    console.log('返回数据内容：', response.data)

                    if (response.data.code === 200) {
                        ElMessage.success('注册成功！')
                        router.push('/login') // 注册成功跳转登录页
                    } else {
                        ElMessage.error(response.data.message || '注册失败')
                    }
                } catch (error) {
                    console.error('请求出错：', error)
                    ElMessage.error('网络错误或服务器异常')
                }
            }
        })
    }



</script>

<style lang="css" scoped>

    .register-body:before {
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

    .register-body {
        display: grid;
        place-items: center;
        min-height: 100vh;
    }

    .register-form {
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

    .register-image {
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
    }

    .button-row {
        display: flex;
        gap: 10px; /* 按钮之间的间距 */
        width: 100%;
    }

    .login-btn, .cancel-btn {
        flex: 1; /* 使两个按钮等宽 */
    }

<<<<<<< HEAD
</style>
=======
</style>
>>>>>>> b5fa8e172b7eede5ee25928006a97db178cd8791
