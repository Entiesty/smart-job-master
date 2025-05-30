<template>
  <div class="container" >
    <!-- 仪表盘页面 -->
    <div class="page-section">
      <div class="row" >
        <el-row>
          <el-col :span="6"  @click="changeList('待接受') ">
            <div class="stats-card" >
              <div class="stats-label">待接受的工作</div>
              <div class="stats-value">{{pendJobs.length || 0 }}</div>
            </div>
          </el-col>
          <el-col :span="6"  @click="changeList('进行中')">
            <div class="stats-card" >
              <div class="stats-label">进行中的工作</div>
              <div class="stats-value">{{doingJobs.length || 0}}</div>
            </div>
          </el-col>
         
          <el-col :span="6" @click="changeList('已完成')">
            <div class="stats-card">
              <div class="stats-label">已完成工作</div>
              <div class="stats-value">{{ doneJobs.length ||0 }}</div>
            </div>
          </el-col>
          <el-col :span="6" @click="changeList('已支付')">
            <div class="stats-card">
              <div class="stats-label">已支付工作</div>
              <div class="stats-value">{{paidJobs.length || 0}}</div>
            </div>
          </el-col>
          <el-col :span="6" @click="changeList('已结束')">
            <div class="stats-card">
              <div class="stats-label">已结束工作</div>
              <div class="stats-value">{{overJobs.length || 0}}</div>
            </div>
          </el-col>
          <el-col :span="6"  @click="changeList('已取消')">
            <div class="stats-card" >
              <div class="stats-label">已取消的工作</div>
              <div class="stats-value">{{cancelledJobs.length || 0}}</div>
            </div>
          </el-col>
          <el-col :span="6"  >
            <div class="stats-card" >
              <div class="stats-label">总收入</div>
              <div class="stats-value">{{payIncome|| 0}}</div>
              <el-progress :percentage="payIncome/totalIncome *100" :stroke-width="8" :show-text="false"></el-progress>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stats-card">
              <div class="stats-label">客户评价</div>
              <div >
              <el-rate  
                v-model="rating"
                disabled
                show-score
                :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
              ></el-rate>
              </div>
            </div>
          </el-col>    
        </el-row>
      </div>
      <div class="card">
        <div class="card-title">
          <el-button
            type="primary"
            link
            size="large"
            @click="changeToAlljobs()"
            >全部工作</el-button
          >
          <el-button
            type="primary"
            link
            size="large"
            @click="switchPage('jobList')"
            >找工作</el-button
          >
        </div>
        <div class="job-card" v-for="job in recentJobs" :key="job.id" @click="viewJobDetail(job)">
          <div class="job-title">{{ job.companyName }}</div>
          <div class="job-meta">
            <div class="job-meta-item">
              <i> 地点</i>
              <i class="el-icon-location"></i> {{ job.location }}
            </div>
            <div class="job-meta-item">
              <i> 截止时间</i>
              <i class="el-icon-time"></i> {{ job.endDate }}
            </div>
            <div class="job-meta-item">
              <i> 状态</i>
              <i class="el-icon-user"></i> {{ job.status }}
            </div>
          
          </div>
          <div class="job-description">{{ job.description }}</div>
          <div class="job-footer">
            <div class="job-price">￥{{ job.income }}</div>
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
          <el-tag :type="getApplicantStatusType(selectedAcceptance.status)" style="margin-top: 5px">{{
            selectedAcceptance.status
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
        <el-link type="primary" :underline="false" @click="getFile('other')"
          ><i class="el-icon-picture" style="margin-right: 5px"></i>
          参考资料</el-link
        >
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="jobDetailVisible = false">取消</el-button>
        <el-button v-if="selectedAcceptance.status ==='进行中'"   type="primary" @click="jobDetailVisible = false,handleDone()">完成任务</el-button>
        <el-button v-if="selectedAcceptance.status ==='待接受'"   type="warning" @click="jobDetailVisible = false,handleCancel()">取消任务</el-button>

      </span>
    </template>
  </el-dialog>
</template>
<script setup>
import {  onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useJobStore } from "../../store/job";
import { ElDialog, ElMessage } from "element-plus";
import { useChatStore } from "../../store/chat";
import { useFileStore } from "../../store/file";
import { useUserStore } from "../../store/user";
const chatStore = useChatStore();
const jobStore = useJobStore();
const jobs = ref([]);
const recentJobs = ref([]);
const doingJobs = ref([]);
const doneJobs = ref([]);
const pendJobs = ref([]);
const overJobs = ref([]);
const rejectedJobs = ref([]);
const paidJobs = ref([]);  
const cancelledJobs = ref([]);
const userStore = useUserStore();
const rating = ref(userStore.userInfo.worker?.rating || 0);
const totalIncome = ref(1);
const payIncome = ref(0);


onMounted(async () => {
  jobs.value = await jobStore.getJobAcceptances();
  recentJobs.value = jobs.value
  jobs.value.forEach((a) =>  { 
    switch (a.status) {
      case "进行中":
      doingJobs.value.push(a);
        break;
      case "已结束":
      payIncome.value += a.income;
      totalIncome.value += a.income;
      overJobs.value.push(a);
        break;
      case "待接受":
      pendJobs.value.push(a);
        break;
      case "已拒绝":
      rejectedJobs.value.push(a);
        break;
      case "已取消":
      cancelledJobs.value.push(a);
        break;
      case "已完成":
      totalIncome.value += a.income;
      doneJobs.value.push(a);
        break;
      case "已支付":
      paidJobs.value.push(a);
        break;
      default:
        break;
    }
  }
  );

});
const router = useRouter();
// 切换页面
const switchPage = (page,job) => {
  if (page === "jobList") {
    router.push("/jobList");
  }

};
const changeList = (status) => {
  recentJobs.value = jobs.value.filter((a) => a.status === status);

};
const changeToAlljobs = () => {
    recentJobs.value = jobs.value;
};
const selectedJob = ref(null);
const selectedAcceptance = ref(null);
const jobDetailVisible = ref(false);
const viewJobDetail = async (job) => {
  selectedJob.value =  await jobStore.getJobById(job.jobId)
  selectedAcceptance.value = jobs.value.find((a) => a.jobId === job.jobId);
  jobDetailVisible.value = true;
};


// 联系发布者
const contactPublisher = async (job) => {
  try {
    // 创建聊天会话
    await chatStore.createChatSession(userStore.userInfo.id, job.enterpriseId);
      // 跳转到聊天页面
      router.push({
        path: '/chat',
      });
  } catch (error) {
    console.error("创建聊天会话失败:", error);
    ElMessage.error("联系失败，请稍后重试");
  }
};
const handleDone = async () => {
  console.log(selectedJob.value.id);
  jobStore.updateApplicationStatus(selectedJob.value.id, 7).then(() => {
      ElMessage.success('任务已完成,待雇主确认');
    })
}
const handleCancel = async () => {
  jobStore.updateApplicationStatus(selectedJob.value.id, 6).then(() => {
      ElMessage.success('任务已取消');
    }
  )
  cancelledJobs.value.push(selectedAcceptance.value)
  pendJobs.value = pendJobs.value.filter((a) => a.id !== selectedAcceptance.value.id);
}
// 获取申请人状态对应的标签类型
const getApplicantStatusType = (status) => {
  switch (status) {
    case '待接收':
      return 'warning';
    case '已批准':
    case '进行中':
    case '待确认完成':
    case '已支付':
      return 'success';
    case '已拒绝':
      return 'danger';
    default:
      return 'info';
  }
};
const files = useFileStore();
const getFile = async (type) => {
  var path = '';
  if (type === 'contract') {
    path = selectedJob.value.contractFilePath;
  } else {
    path = selectedJob.value.otherFilePath;}
    if(path === '')
    {
      ElMessage.error('文件不存在');
      return;
    }
  const response = await files.getFile(path);
  if (response && response.data) {
    const blob = new Blob([response.data], { type: 'application/octet-stream' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${selectedJob.value.title}_${type}.pdf`;
    link.click();
    URL.revokeObjectURL(url);
  }
}
</script>

<style scoped>
.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  overflow: hidden;
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
  .job-price {
  font-size: 18px;
  font-weight: 600;
  color: #f56c6c;
}
.job-actions {
  display: flex;
  justify-content: flex-end;
}
}


/* 仪表盘统计卡片 */
.stats-card {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  margin: 0 5px;
  margin-bottom: 20px;
  text-align: center;
  height: 100px;
  display: flex;
  flex-direction: column;
  justify-content: center;;
  padding: 15px;
  transition: box-shadow 0.3s;
  cursor: pointer;
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
.signature-link{
  display: flex;
  justify-content: space-between;
}
</style>
