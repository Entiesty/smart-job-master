<template>
  <div class="profile-container">
    <el-card class="profile-card">
      <template #header>
        <div class="profile-header">
          <el-avatar :size="100" :src="userStore.userAvatar"></el-avatar>
          <div class="profile-info">
            <h2>{{  userInfo.username }}</h2>
            <p>{{ userInfo.role === 'enterprise' ? '企业用户' : '零工用户' }}</p>
            <el-rate v-if="userInfo.role==='worker'" v-model="userInfo.worker.rating" disabled show-score text-color="#ff9900"></el-rate>
          </div>
        </div>
      </template>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="基本信息" name="basic">
          <el-form :model="profileForm" ref="profileFormRef" label-width="120px">
            <el-form-item label="用户名" prop="username">
              <el-input v-model="profileForm.username" disabled></el-input>
            </el-form-item>
            <el-form-item label="手机号码" prop="phone">
              <el-input v-model="profileForm.contactPhone"></el-input>
            </el-form-item>
            <el-form-item label="电子邮箱" prop="email">
              <el-input v-model="profileForm.contactEmail"></el-input>
            </el-form-item>
            <el-form-item label="个人简介" prop="bio">
              <el-input type="textarea" v-model="profileForm.introduction" :rows="4"></el-input>
            </el-form-item>
            <el-form-item label="头像" prop="avatar">
              <el-upload
                class="avatar-uploader"
                action="/api/file/uploadImage"
                :show-file-list="false"
                :on-success="handleAvatarSuccess"
                :before-upload="beforeAvatarUpload"
              >
                <img v-if="userCover" :src="userCover" class="avatar" />
                <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
              </el-upload>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveBasicInfo">保存基本信息</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="技能与经验" name="skills" v-if="userInfo.role === 'worker'">
          <el-form :model="skillsForm" ref="skillsFormRef" label-width="120px">
            <el-form-item label="技能标签" prop="skills">
              <el-select
                v-model="skillsLabel"
                multiple
                filterable
                allow-create
                default-first-option
                placeholder="请选择或添加您的技能标签"
                style="width: 100%"
              >
                <el-option
                  v-for="item in skillOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item"
                ></el-option>
              </el-select>
            </el-form-item>
            
            <el-form-item label="工作经验" prop="experience">
              <el-input type="textarea" v-model="skillsForm.exp" :rows="4" placeholder="请描述您的工作经验"></el-input>
            </el-form-item>
            
            <el-form-item label="可工作时间" prop="availableTime">
              <el-time-select
                v-model="skillsForm.workTimeBegin"
                start="08:00"
                step="00:30"
                end="24:00"
                placeholder="开始时间"
                style="width: 48%; margin-right: 4%"
              ></el-time-select>
              <el-time-select
                v-model="skillsForm.workTimeEnd"
                start="08:00"
                step="00:30"
                end="24:00"
                placeholder="结束时间"
                style="width: 48%"
              ></el-time-select>
            </el-form-item>
            
            <el-form-item label="工作日" prop="workDays">
              <el-checkbox-group v-model="skillsForm.workDay">
                <el-checkbox label="1">周一</el-checkbox>
                <el-checkbox label="2">周二</el-checkbox>
                <el-checkbox label="3">周三</el-checkbox>
                <el-checkbox label="4">周四</el-checkbox>
                <el-checkbox label="5">周五</el-checkbox>
                <el-checkbox label="6">周六</el-checkbox>
                <el-checkbox label="0">周日</el-checkbox>
              </el-checkbox-group>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveSkillsInfo">保存技能信息</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        <el-tab-pane label="企业信息" name="company" v-if="userInfo.role === 'enterprise'">
          <el-form :model="companyForm" ref="companyFormRef" label-width="120px">
            <el-form-item label="企业名称" prop="companyName">
              <el-input v-model="companyForm.companyName"></el-input>
            </el-form-item>
            <el-form-item label="企业地址" prop="companyAddress">
              <el-input v-model="companyForm.address"></el-input>
            </el-form-item>
            <el-form-item label="社会信用代码" prop="companyPhone">
              <el-input v-model="companyForm.creditCode"></el-input>
            </el-form-item>
            <el-form-item label="营业执照号" prop="companyPhone">
              <el-input v-model="companyForm.businessLicense"></el-input>
            </el-form-item>
            <el-form-item label="联系人" prop="companyPhone">
              <el-input v-model="companyForm.contactName"></el-input>
            </el-form-item>
            <el-form-item label="联系电话" prop="companyPhone">
              <el-input v-model="companyForm.contactPhone"></el-input>
            </el-form-item>
            <el-form-item label="联系邮箱" prop="companyPhone">
              <el-input v-model="companyForm.contactEmail"></el-input>
            </el-form-item>
            <el-form-item label="企业简介" prop="companyDesc">
              <el-input type="textarea" v-model="companyForm.companyDesc" :rows="4"></el-input>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="saveCompanyInfo">保存企业信息</el-button>
            </el-form-item>
          </el-form>
        </el-tab-pane>
        
        <el-tab-pane label="证书认证" name="certificates" v-if="userInfo.role === 'worker'">
          <div class="certificates-container">
            <el-empty v-if="certificates.length === 0" description="暂无证书信息"></el-empty>
            
            <el-table v-else :data="certificates" style="width: 100%">
              <el-table-column prop="name" label="证书名称" width="180"></el-table-column>
              <el-table-column prop="issueDate" label="颁发日期" width="180"></el-table-column>
              <el-table-column prop="status" label="认证状态">
                <template #default="scope">
                  <el-tag :type="scope.row.statu === '已认证' ? 'success' : 'warning'">
                    {{ scope.row.statu }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作">
                <template #default="scope">
                  <el-button size="small" type="danger" @click="deleteCertificate(scope.row.id)">删除</el-button>
                </template>
              </el-table-column>
            </el-table>
            
            <div class="add-certificate">
              <el-button type="primary" @click="showAddCertificateDialog">添加证书</el-button>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
    
    <!-- 添加证书对话框 -->
    <el-dialog v-model="certificateDialogVisible" title="添加证书" width="500px">
      <el-form :model="newCertificate" ref="certificateFormRef" label-width="100px">
        <el-form-item label="证书名称" prop="name">
          <el-input v-model="newCertificate.name"></el-input>
        </el-form-item>
        <el-form-item label="颁发机构" prop="issuer">
          <el-input v-model="newCertificate.issuer"></el-input>
        </el-form-item>
        <el-form-item label="颁发日期" prop="issueDate">
          <el-date-picker v-model="newCertificate.issueDate" type="date" placeholder="选择日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="证书图片" prop="image">
          <el-upload
            class="certificate-uploader"
            action="/api/file/uploadFile"
            :show-file-list="false"
            :on-success="handleCertificateSuccess"
            :before-upload="beforeCertificateUpload"
          >
            <img v-if="cover" :src="cover" class="certificate-image" />
            <el-icon v-else class="certificate-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="certificateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="addCertificate">确认</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue';
import { useUserStore } from '@/store/user';
import { ElMessage } from 'element-plus';
import { Plus } from '@element-plus/icons-vue';
import { useJobStore } from '../../store/job';
import { useFileStore } from '../../store/file';


const userStore = useUserStore();
const activeTab = ref('basic');
const certificateDialogVisible = ref(false);

// 用户信息
const userInfo = computed(() => userStore.userInfo || {
  username: '',
  nickname: '',
  avatar: '',
  userType: 'worker',
  rating: 0
});

// 基本信息表单
const profileFormRef = ref(null);
const profileForm = reactive({
  id: userInfo.value?.id || '',
  username: userInfo.value?.username || '',
  contactPhone: userInfo.value?.contactPhone || '',
  contactEmail: userInfo.value?.contactEmail || '',
  introduction: userInfo.value?.introduction || '',
  avatar: userInfo.value?.avatar || '',
  token:userInfo.value?.token || '',
});

// 头像上传相关方法
const userCover = ref(userStore.userAvatar)
;
const fileStore = useFileStore()
const handleAvatarSuccess =async (response) => {
  profileForm.avatar = response.data;
  const res = await fileStore.getImg(profileForm.avatar)
  userCover.value = res
  ElMessage.success('头像上传成功');
};

const beforeAvatarUpload = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isLt2M = file.size / 1024 / 1024 < 2;
  if (!isJPG && !isPNG) {
    ElMessage.error('上传头像图片只能是 JPG 或 PNG 格式!');
    return false;
  }
  if (!isLt2M) {
    ElMessage.error('上传头像图片大小不能超过 2MB!');
    return false;
  }
  return true;
};
// 保存基本信息
const saveBasicInfo = async () => {
  try {
    await userStore.updateUserInfo(profileForm);
    userStore.setAvatar(userCover.value);
    ElMessage.success('基本信息保存成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('保存失败，请重试');
  }
};
// 技能表单
const skillsFormRef = ref(null);
const skillsLabel= ref(userInfo.value.worker?.label.map(item => ({
    value: item,
    label: item
  }))||[]);
const skillsForm = reactive({
  label:skillsLabel.value.map(item => item.value) || [],
  exp: userInfo.value.worker?.exp || '',
  workTimeBegin: userInfo.value.worker?.workTimeBegin || '',
  workTimeEnd: userInfo.value.worker?.workTimeEnd || '',
  workDay: userInfo.value.worker?.workDay || []
});


// 企业信息表单
const companyFormRef = ref(null);
const companyForm = userInfo.value.enterprise
// 证书列表
const certificates = ref(userInfo.value.worker?.certificates || []);
const certificateFormRef = ref(null);
const newCertificate = reactive({
  name: '',
  issuer: '',
  issueDate: '',
  filePath: '',
  userId: userInfo.value?.id || '',
  token:userInfo.value?.token || ''
});

// 技能选项
const jobs = useJobStore();
onMounted(async () => {
  await jobs.getSkillLabels();
})
const skillOptions = computed(() => {
  return jobs.skillLabels.map(item => ({
    value: item.label,
    label: item.label
  }));
});
// 保存技能信息
const saveSkillsInfo = async () => {
  try {
    skillsForm.label = skillsLabel.value.map(item => item.value);
    skillsForm.workDay = skillsForm.workDay;
    await userStore.updateWorker(skillsForm);
    userStore.setWorkerInfo(skillsForm) 
    ElMessage.success('技能信息保存成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('保存失败，请重试');
  }
};
// 保存企业信息
const saveCompanyInfo = async () => {
  try {
    await userStore.updateEnterprise(companyForm)
    ElMessage.success('企业信息保存成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('保存失败，请重试');
  }
};
// 证书相关方法
const cover = ref(null);
const showAddCertificateDialog = () => {
  certificateDialogVisible.value = true;
  
};

const handleCertificateSuccess = async (response) => {
  newCertificate.filePath = response.data;
};

const beforeCertificateUpload = (file) => {
  const isJPG = file.type === 'image/jpeg';
  const isPNG = file.type === 'image/png';
  const isPDF = file.type === 'application/pdf';
  const isLt5M = file.size / 1024 / 1024 < 5;
  cover.value = URL.createObjectURL(file);
  if (!isJPG && !isPNG && !isPDF) {
    ElMessage.error('上传证书只能是 JPG、PNG 或 PDF 格式!');
    return false;
  }
  if (!isLt5M) {
    ElMessage.error('上传证书大小不能超过 5MB!');
    return false;
  }
  return true;
};
const addCertificate =async () => {
  if (!newCertificate.name || !newCertificate.issueDate) {
    ElMessage.warning('请填写完整的证书信息');
    return;
  }
  await userStore.addUserCertificate(newCertificate)
  certificateDialogVisible.value = false;
  ElMessage.success('证书添加成功，等待审核');
};

const deleteCertificate = (id) => {
  certificates.value = certificates.value.filter(cert => cert.id !== id);
  userStore.deleteUserCertificate(id)
  ElMessage.success('证书删除成功');
};

</script>

<style lang="scss" scoped>
.profile-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 20px;
  
  .profile-card {
    margin-bottom: 20px;
    
    .profile-header {
      display: flex;
      align-items: center;
      
      .profile-info {
        margin-left: 20px;
        
        h2 {
          margin: 0 0 5px 0;
        }
        
        p {
          margin: 0 0 10px 0;
          color: #909399;
        }
      }
    }
  }
  
  .avatar-uploader,
  .certificate-uploader {
    width: 178px;
    height: 178px;
    border: 1px dashed #d9d9d9;
    border-radius: 6px;
    cursor: pointer;
    position: relative;
    overflow: hidden;
    transition: border-color 0.3s;
    
    &:hover {
      border-color: #409EFF;
    }
    
    .avatar,
    .certificate-image {
      width: 178px;
      height: 178px;
      display: block;
    }
    
    .avatar-uploader-icon,
    .certificate-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 178px;
      height: 178px;
      line-height: 178px;
      text-align: center;
    }
  }
  
  .certificates-container {
    margin-bottom: 20px;
    
    .add-certificate {
      margin-top: 20px;
      text-align: center;
    }
  }
  
  @media screen and (max-width: 768px) {
    .profile-header {
      flex-direction: column;
      text-align: center;
      
      .profile-info {
        margin-left: 0;
        margin-top: 15px;
      }
    }
  }
}
</style>