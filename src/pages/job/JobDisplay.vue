<template>
  <div class="container">
    <!-- 工作机会页面 -->
    <div class="page-section" :class="{ active: currentPage === 'jobs' }">
      <h1 class="page-title">工作机会</h1>
      <div class="card">
        <div class="card-title">
          <span>筛选工作</span>
        </div>
        <el-form :inline="true" :model="jobFilter">
          <el-form-item label="工作类型">
            <el-select
              v-model="jobFilter.jobType"
              placeholder="选择工作类型"
              clearable
            >
            <el-option
                  v-for="item in skillOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                ></el-option>
            
            </el-select>
          </el-form-item>
          <el-form-item label="预算范围">
            <el-select
              v-model="jobFilter.budget"
              placeholder="选择预算范围"
              clearable
            >
              <el-option label="500元以下" value="0-500"></el-option>
              <el-option label="500-1000元" value="500-1000"></el-option>
              <el-option label="1000-3000元" value="1000-3000"></el-option>
              <el-option label="3000元以上" value="3000+"></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="工作地点">
            <el-select
              v-model="jobFilter.location"
              placeholder="选择工作地点"
              clearable
            >
            <el-option
                  v-for="item in LocationOptions"
                  :key="item.value"
                  :label="item.label"
                  :value="item"
                ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item>
            <div class="card-button">
              <el-button type="primary" @click="searchJobs">搜索</el-button>
              <el-button @click="resetJobFilter">重置</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>

      <div class="card">
        <div class="card-title">
          <span>工作列表</span>
        </div>

        <div
          class="job-card"
          v-for="job in filteredJobs"
          :key="job.id"
          @click="viewJobDetail(job)"
        >
          <div class="job-title">{{ job.contactInfo }}</div>
          <div class="job-meta">
            <div class="job-meta-item">
              <i class="el-icon-location"></i>{{ job.location }}
            </div>
            <div class="job-meta-item">
              <i class="el-icon-location"></i> {{ job.salaryType }}
            </div>
            <div class="job-meta-item">
              <i class="el-icon-time"></i> {{ job.startDate }}
            </div>
            <div class="job-meta-item">
              <i class="el-icon-user"></i> {{ job.companyName }}
            </div>
            <div class="job-price">￥{{ job.price }}</div>
          </div>
          <div class="job-description">{{ job.description }}</div>
          
          <div class="job-footer">
            <div>
              <el-tag
                type="primary"
                size="small"
                v-for="tag in job.skills"
                :key="tag"
                >{{ tag }}</el-tag
              >
            </div>
            <div class="job-actions">
              <el-button 
              type="primary"
            link
            size="large"
                @click.stop="contactPublisher(job)"
                class="job-contact"
              >
              <el-icon>
                <i-ep-message></i-ep-message>
              </el-icon>
              联系</el-button>
            </div>
          </div>
        </div>
        <div class="pagination">
          <el-pagination
            layout="prev, pager, next"
            :total="pageForm.total"
            :page-size="pageForm.size"
            @current-change="handleJobPageChange"
          >
          </el-pagination>
        </div>
      </div>
    </div>
  </div>
  <!-- 工作详情对话框 -->
  <el-dialog v-model="jobDetailVisible" title="工作详情" width="60%">
    <div v-if="selectedJob">
      <h2 style="margin-bottom: 15px">{{ selectedJob.title }}</h2>
      <div style="display: flex; margin-bottom: 15px">
        <div style="flex: 1">
          <div style="color: #666; margin-bottom: 5px">
            <i class="el-icon-user" style="margin-right: 5px"></i> 发布者:
            {{ selectedJob.companyName }}
          </div>
          <div style="color: #666; margin-bottom: 5px">
            <i class="el-icon-location" style="margin-right: 5px"></i> 地点:
            {{ selectedJob.location }}
          </div>
          <div style="color: #666; margin-bottom: 5px">
            <i class="el-icon-time" style="margin-right: 5px"></i> 发布者:
            {{ selectedJob.startDate }}
          </div>
          <div style="color: #666; margin-bottom: 5px">
            <i class="el-icon-time" style="margin-right: 5px"></i> 结束:
            {{ selectedJob.endDate }}
          </div>
          <div style="color: #666; margin-bottom: 5px">
            <i class="el-icon-time" style="margin-right: 5px"></i> 结算方式:
            {{ selectedJob.salaryType }}
          </div>
        </div>
        <div style="text-align: right">
          <div style="font-size: 24px; font-weight: 600; color: #f56c6c">
            ￥{{ selectedJob.price }}
          </div>
          <el-tag type="success" style="margin-top: 5px">{{
            selectedJob.status
          }}</el-tag>
        </div>
      </div>
      <div style="margin-bottom: 20px">
        <el-tag
          type="primary"
          style="margin-right: 5px; margin-bottom: 5px"
          v-for="tag in selectedJob.skills"
          :key="tag"
          >{{ tag }}</el-tag
        >
      </div>

      <div style="margin-bottom: 20px">
        <h3 style="margin-bottom: 10px">工作描述</h3>
        <div style="color: #666; line-height: 1.6">
          {{ selectedJob.description }}
        </div>
      </div>

      <div style="margin-bottom: 20px">
        <h3 style="margin-bottom: 10px">工作要求</h3>
        <div style="color: #666; line-height: 1.6">
          {{ selectedJob.requirment }}
        </div>
      </div>

      <div style="margin-bottom: 20px">
        <h3 style="margin-bottom: 10px">工作附件</h3>
        <el-link type="primary" :underline="false" style="margin-right: 15px" @click="getFile('contract') "
          ><i class="el-icon-document" style="margin-right: 5px"></i>
          合同文件</el-link
        >
        <el-link type="primary" :underline="false" @click="getFile()"
          ><i class="el-icon-picture" style="margin-right: 5px"></i>
          参考资料</el-link
        >
      </div>
      <div v-if="user.role === 'worker'">
        <h3 style="margin-bottom: 10px">申请工作</h3>
        <el-form :model="jobApplication" label-width="80px">
          <el-form-item label="报价">
            <el-input-number
              v-model="jobApplication.income"
              :min="100"
              :step="100"
              controls-position="right"
              style="width: 200px"
            ></el-input-number>
          </el-form-item>
          <el-form-item label="预计完成">
            <el-input-number
              v-model="jobApplication.days"
              :min="1"
              :max="30"
              controls-position="right"
              style="width: 200px"
            ></el-input-number>
            <span style="margin-left: 10px">天</span>
          </el-form-item>
          <el-form-item label="申请说明">
            <el-input
              type="textarea"
              v-model="jobApplication.message"
              :rows="4"
            ></el-input>
          </el-form-item>
        </el-form>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="jobDetailVisible = false">取消</el-button>
        <el-button
          type="primary"
          @click="applyJob()"
          v-if="user.role === 'worker'"
          >申请工作</el-button
        >
        <el-button
          type="primary"
          @click="saveJob()"
          v-if="user.role === 'employer'"
          >保存修改</el-button
        >
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { onMounted, ref, computed } from "vue";
import { useUserStore } from "../../store/user";
import { useJobStore } from "../../store/job";
import { ElMessage } from "element-plus";
import { useFileStore } from "../../store/file";
import { useRouter } from "vue-router";
const jobFilter = ref({
  jobType: "",
  budget: "",
  location: "",
});
const currentPage = ref("dashboard");
const userStore = useUserStore();
const user = userStore.userInfo;
const job = useJobStore();
const jobList = ref({});
const skillOptions = computed(() => {
  return  job.skillLabels.map(item => ({
    value: item,
    label: item
  }))
})

const LocationOptions = computed(() => {
  return job.locations.map(item => ({
    value: item,
    label: item
  }));
});
onMounted(async () => {
  await job.getSkillLabels();
  await job.getLocations();

  jobList.value = await job.getJobs(pageForm.value);
  filteredJobs.value = jobList.value;
});
const pageForm = ref({
  current: 1,
  size: 5,
  total: 10,
});
const resetJobFilter = () => {
  jobFilter.value = {
    jobType: "",
    budget: "",
    location: "",
  };
  filteredJobs.value = jobList.value;
};
const jobApplication = ref({
  income: 1000,
  days: 7,
  message: "",
  id: 0,
  enterpriseId: 0,
});

const filteredJobs = ref("");
// 搜索工作
const searchJobs = () => {
  filteredJobs.value = jobList.value.filter((job) => {
    const typeMatch = !jobFilter.value.jobType.value || job.skills.some((skill) => 
       skill.trim() === jobFilter.value.jobType.value
    )
    const budgetMatch = 
      !jobFilter.value.budget ||
      (jobFilter.value.budget === "0-500" && job.price <= 500) ||
      (jobFilter.value.budget === "500-1000" &&job.price > 500 && job.price <= 1000) ||
      (jobFilter.value.budget === "1000-3000" && job.price> 1000 && job.price<= 3000 )||
      (jobFilter.value.budget === "3000+" && job.price > 3000);
    const locationMatch =
      !jobFilter.value.location || job.location === jobFilter.value.location.value;
    return typeMatch && budgetMatch && locationMatch;
  });
};
const viewJobDetail = (job) => {
  selectedJob.value = job;
  jobDetailVisible.value = true;
};

const handleJobPageChange = async (page) => {
  pageForm.value.current = page;
  jobList.value = await job.getJobs(pageForm.value);
  filteredJobs.value = jobList.value;
};
const selectedJob = ref(null);
const jobDetailVisible = ref(false);
const applyJob = () => {
  jobApplication.value.id = selectedJob.value.id;
  jobApplication.value.enterpriseId = selectedJob.value.userId;
  job.applyJob(jobApplication.value);
  ElMessage.success("工作申请已提交");
  jobDetailVisible.value = false;
};

const saveJob = (row) => {
  console.log(11);
  console.log(row);
  ElMessage.success("工作信息已保存");
  jobDetailVisible.value = false;
};
const fileStore = useFileStore();
const router = useRouter();
// 联系发布者
const contactPublisher = async (job) => {
  try {
    // 创建聊天会话
   await chat.createChatSession(user.id, job.userId);
    router.push( '/chat');
    
  } catch (error) {
    console.error("创建聊天会话失败:", error);
    ElMessage.error("联系失败，请稍后重试");
  }
};
const getFile = async (type) => {
  console.log(type);
  let res 
  try {
    if(type === 'contract'){
      if (!selectedJob.value || !selectedJob.value.contractFilePath) {
      ElMessage.error("合同文件路径不存在");
      return;
    }
  
    res = await fileStore.getFile(encodeURIComponent(selectedJob.value.contractFilePath));
  
    }else{
      if (!selectedJob.value ||!selectedJob.value.otherFilePath) {
      ElMessage.error("参考资料路径不存在");
      return;
      }

    res = await fileStore.getFile(encodeURIComponent(selectedJob.value.otherFilePath));
    }
    if (!res || res.byteLength === 0) {
      throw new Error('文件内容为空');
    }
    // 根据文件扩展名确定MIME类型
    const filePath = type === 'contract' ? selectedJob.value.contractFilePath : selectedJob.value.otherFilePath;
    const extension = filePath.split('.').pop().toLowerCase();
    
    // 常见文件类型的MIME映射
    const mimeTypes = {
      'pdf': 'application/pdf',
      'doc': 'application/msword',
      'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
      'xls': 'application/vnd.ms-excel',
      'xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
      'ppt': 'application/vnd.ms-powerpoint',
      'pptx': 'application/vnd.openxmlformats-officedocument.presentationml.presentation',
      'jpg': 'image/jpeg',
      'jpeg': 'image/jpeg',
      'png': 'image/png',
      'gif': 'image/gif',
      'svg': 'image/svg+xml',
      'txt': 'text/plain',
      'html': 'text/html',
      'css': 'text/css',
      'js': 'text/javascript',
      'json': 'application/json',
      'xml': 'application/xml',
      'zip': 'application/zip',
      'rar': 'application/x-rar-compressed',
      '7z': 'application/x-7z-compressed'
    };
    
    const mimeType = mimeTypes[extension] || 'application/octet-stream';
    console.log('文件类型:', mimeType);
    
    // 创建Blob对象
    const blob = new Blob([res], { type: mimeType });
    // 创建下载链接
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    // 从文件路径中提取文件名
    const fileName = filePath.split('/').pop() || 'download';
    a.download = fileName;
    // 触发下载
    document.body.appendChild(a);
    a.click();
    // 对于图片类型，也可以直接在新窗口打开预览
    if (mimeType.startsWith('image/')) {
      window.open(url, '_blank');
    }
  } catch (error) {
    console.error("下载合同文件失败:", error);
    ElMessage.error("下载合同文件失败，请稍后重试");
  }
};
import { useChatStore } from "../../store/chat";
const chat= useChatStore();


</script>

<style scoped>
/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: "PingFang SC", "Microsoft YaHei", sans-serif;
}

body {
  background-color: #f5f7fa;
  color: #333;
  line-height: 1.6;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

/* 导航样式 */
.navbar {
  background-color: #409eff;
  color: white;
  padding: 15px 0;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.navbar-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  font-size: 24px;
  font-weight: bold;
}

.nav-links {
  display: flex;
  gap: 20px;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  padding: 5px 10px;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.nav-link:hover {
  background-color: rgba(255, 255, 255, 0.2);
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
}

/* 页面标题 */
.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 600;
}

/* 卡片样式 */
.card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  .el-form-item {
    width: 20%;
    margin-bottom: 15px;
  }
  .card-button {
    margin-left: auto;
    display: flex;
    .el-button {
      margin-left: 20px;
      width: 80px;
    }
  }
}

.card-title {
  font-size: 18px;
  margin-bottom: 15px;
  color: #409eff;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

/* 按钮样式 */
.primary-btn {
  background-color: #409eff;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.primary-btn:hover {
  background-color: #66b1ff;
}

.secondary-btn {
  background-color: white;
  color: #409eff;
  border: 1px solid #409eff;
  padding: 10px 20px;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
}

.secondary-btn:hover {
  background-color: #ecf5ff;
}

/* 标签样式 */
.tag {
  display: inline-block;
  padding: 4px 10px;
  border-radius: 4px;
  font-size: 12px;
  margin-right: 8px;
  margin-bottom: 8px;
}

.tag-primary {
  background-color: #ecf5ff;
  color: #409eff;
  border: 1px solid #d9ecff;
}

.tag-success {
  background-color: #f0f9eb;
  color: #67c23a;
  border: 1px solid #e1f3d8;
}

.tag-warning {
  background-color: #fdf6ec;
  color: #e6a23c;
  border: 1px solid #faecd8;
}

/* 分页样式 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 表单样式 */
.form-group {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
}

/* 工作卡片 */
.job-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
  cursor: pointer;
}

.job-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.job-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 10px;
  color: #333;
}

.job-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 10px;
  color: #666;
  font-size: 14px;
  position: relative;
}

.job-meta-item {
  display: flex;
  align-items: center;
}

.job-meta-item i {
  margin-right: 5px;
  color: #409eff;
}

.job-description {
  color: #666;
  margin-bottom: 15px;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.job-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.job-actions {
  display: flex;
  gap: 10px;

.job-contact{
  font-size: 18px;
  font-weight: 600;
  width: 100px;
  .el-icon{
  font-size: 18px;
  font-weight: 600;
  margin-right: 5px;
  }
}
}
.job-price {
  position: absolute;
  right: 100px; /* 距离父容器右侧的距离 */
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}
/* 工作者卡片 */
.worker-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
  cursor: pointer;
}

.worker-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.worker-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.worker-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 15px;
  object-fit: cover;
}

.worker-info {
  flex: 1;
}

.worker-name {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 5px;
}

.worker-title {
  color: #409eff;
  font-size: 14px;
  margin-bottom: 5px;
}

.worker-rating {
  display: flex;
  align-items: center;
  color: #e6a23c;
  font-size: 14px;
}

.worker-skills {
  margin-bottom: 15px;
}

.worker-bio {
  color: #666;
  margin-bottom: 15px;
  font-size: 14px;
}

.worker-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.worker-rate {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}

/* 仪表盘统计卡片 */
.stats-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
  text-align: center;
}

.stats-value {
  font-size: 28px;
  font-weight: 600;
  margin: 10px 0;
  color: #409eff;
}

.stats-label {
  color: #666;
  font-size: 14px;
}

/* 消息卡片 */
.message-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
  transition: box-shadow 0.3s;
  cursor: pointer;
}

.message-card:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.message-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.message-info {
  flex: 1;
}

.message-sender {
  font-weight: 600;
  margin-bottom: 2px;
}

.message-time {
  color: #999;
  font-size: 12px;
}

.message-preview {
  color: #666;
  font-size: 14px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 评价卡片 */
.review-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 15px;
}

.review-header {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.review-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  margin-right: 10px;
  object-fit: cover;
}

.review-info {
  flex: 1;
}

.review-name {
  font-weight: 600;
  margin-bottom: 2px;
}

.review-rating {
  color: #e6a23c;
  font-size: 14px;
}

.review-time {
  color: #999;
  font-size: 12px;
}

.review-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

/* 时间线样式 */
.timeline {
  position: relative;
  padding-left: 30px;
}

.timeline-item {
  position: relative;
  padding-bottom: 20px;
  border-left: 2px solid #ebeef5;
  padding-left: 20px;
}

.timeline-item:last-child {
  border-left: 2px solid transparent;
}

.timeline-dot {
  position: absolute;
  left: -9px;
  top: 0;
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #409eff;
}

.timeline-time {
  color: #999;
  font-size: 12px;
  margin-bottom: 5px;
}

.timeline-content {
  color: #666;
  font-size: 14px;
  line-height: 1.6;
}

/* 页面切换样式 */
.page-section {
  display: block;
  animation: fadeIn 0.3s ease-in-out;
}

.page-section.active {
  display: block;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

/* 响应式布局 */
@media (max-width: 768px) {
  .nav-links {
    display: none;
  }

  .job-meta {
    flex-direction: column;
    gap: 5px;
  }

  .worker-header {
    flex-direction: column;
    align-items: flex-start;
  }

  .worker-avatar {
    margin-right: 0;
    margin-bottom: 10px;
  }
}
</style>
