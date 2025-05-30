<template>
  <div class="page-container">
    <h1 class="page-title">任务管理与申请审批</h1>
    <el-row :gutter="20">
      <!-- 任务列表 -->
      <el-col :span="8">
        <el-card class="box-card">
          <template #header>
            <div class="card-header">
              <span>我发布的任务</span>
            </div>
          </template>
          <div v-if="employerTasks.length === 0" class="empty-tasks">
            暂未发布任何任务。
          </div>
          <el-menu v-else default-active="0" @select="handleTaskSelect">
            <el-menu-item
              v-for="(task, index) in employerTasks"
              :key="task.id"
              :index="String(index)"
            >
              <template #title>
                <span>{{ task.title }}</span>
                <el-tag
                  size="small"
                  :type="task.status === '招募中' ? 'success' : 'info'"
                  style="margin-left: 10px"
                  >{{ task.status }}</el-tag
                >
              </template>
            </el-menu-item>
          </el-menu>
        </el-card>
      </el-col>
      <!-- 申请人列表与任务详情 -->
      <el-col :span="16">
        <el-card class="box-card" v-if="selectedTask">
          <template #header>
            <div class="card-header">
              <span>任务详情: {{ selectedTask.title }}</span>
            </div>
          </template>
          <div>
            <p><strong>描述:</strong> {{ selectedTask.description }}</p>
            <p><strong>预算:</strong> ¥{{ selectedTask.price }}</p>
            <p><strong>发布日期:</strong> {{ selectedTask.startDate }}</p>
            <p><strong>截止日期:</strong> {{ selectedTask.endDate }}</p>
            <p><strong>状态:</strong> {{ selectedTask.status }}</p>
          </div>
          <el-divider />

          <h3>申请人列表</h3>
          <div v-if="applicants.length === 0" class="empty-applicants">
            暂无零工申请此任务。
          </div>
          <el-table :data="applicants" class="applicants-table" v-else>
            <el-table-column prop="name" label="姓名" width="120">
              <template #default="scope">
                <div style="display: flex; align-items: center">
                  <el-avatar
                    :size="30"
                    :src="scope.row.avatar"
                    style="margin-right: 8px"
                  ></el-avatar>
                  <span>{{ scope.row.username }}</span>
                </div>
              </template>
            </el-table-column>
            <el-table-column prop="skills" label="技能">
              <template #default="scope">
                <el-tag
                  v-for="skill in scope.row.label"
                  :key="skill"
                  size="small"
                  style="margin-right: 5px"
                  >{{ skill }}</el-tag
                >
              </template>
            </el-table-column>
            <el-table-column prop="applicationDate" label="报价">
              <template #default="scope">
                {{ scope.row.income }}
              </template>
            </el-table-column>
            <el-table-column
              prop="applicationDate"
              label="申请日期"
              width="100"
            >
              <template #default="scope">
                {{ scope.row.applyTime }}
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag
                  :type="getApplicantStatusType(scope.row.status)"
                  size="small"
                  >{{ scope.row.status }}</el-tag
                >
              </template>
            </el-table-column>
            <el-table-column label="操作" width="180">
              <template #default="scope">
                <div v-if="scope.row.status == '待接受'">
                  <el-button
                    size="small"
                    type="primary"
                    @click="approveApplication(scope.row, 2)"
                    :disabled="scope.row.status !== '待接受'"
                    >批准</el-button
                  >
                  <el-button
                    size="small"
                    type="danger"
                    @click="rejectApplication(scope.row, 4)"
                    :disabled="scope.row.status !== '待接受'"
                    >拒绝</el-button
                  >
                </div>
                <div v-else-if="scope.row.status == '已完成'">
                  <el-button
                    size="small"
                    type="primary"
                    @click="openPaymentDialog(scope.row)"
                    >支付</el-button
                  >
                </div>
                <div
                  v-else-if="
                    scope.row.status == '待确认完成' ||
                    scope.row.status == '进行中'
                  "
                >
                  <el-button
                    size="small"
                    type="primary"
                    @click="approveApplication(scope.row, 3)"
                    >确认完成</el-button
                  >
                </div>
                <div v-else-if="scope.row.status == '已支付'">
                  <el-button
                    size="small"
                    type="danger"
                    @click="openRatingDialog(scope.row)"
                    >评价</el-button
                  >
                </div>
              
              </template>
            </el-table-column>
          </el-table>
        </el-card>
        <el-card class="box-card placeholder-card" v-else>
          <div class="placeholder-content">
            <el-icon :size="50" color="#c0c4cc"><Files /></el-icon>
            <p>请从左侧选择一个任务以查看详情和申请人。</p>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>

  <!-- 支付对话框 -->
  <el-dialog
    v-model="paymentDialogVisible"
    title="任务支付"
    width="30%"
    :close-on-click-modal="false"
  >
    <div class="payment-dialog-content">
      <p class="payment-info">
        您即将为
        <strong>{{ currentApplicant?.username }}</strong> 完成的任务进行支付
      </p>
      <p class="payment-amount">
        支付金额: <span class="amount">¥{{ currentApplicant?.income }}</span>
      </p>
      <div class="payment-method">
        <p class="method-title">请选择支付方式:</p>
        <el-radio-group v-model="paymentMethod">
          <el-radio label="alipay">支付宝</el-radio>
          <el-radio label="wechat">微信支付</el-radio>
          <el-radio label="bank">银行卡</el-radio>
        </el-radio-group>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="paymentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmPayment">确认支付</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 评价对话框 -->
  <el-dialog
    v-model="ratingDialogVisible"
    title="任务评价"
    width="40%"
    :close-on-click-modal="false"
  >
    <div class="rating-dialog-content">
      <p class="rating-info">
        您正在对
        <strong>{{ currentApplicant?.username }}</strong> 的工作表现进行评价
      </p>
      <div class="rating-stars">
        <p class="rating-title">综合评分:</p>
        <el-rate
          v-model="ratingValue"
          :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
          :texts="['很差', '较差', '一般', '良好', '优秀']"
          show-text
        />
      </div>

      <div class="rating-comment">
        <p class="comment-title">评价内容:</p>
        <el-input
          type="textarea"
          v-model="ratingComment"
          :rows="4"
          placeholder="请输入您对该零工的评价内容..."
        />
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="ratingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitRating">提交评价</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { ElMessage, ElMessageBox } from "element-plus";
import { Files } from "@element-plus/icons-vue"; // 引入图标
import { useJobStore } from "../../store/job";
import { useFileStore } from "../../store/file";
import { useReviewStore } from "../../store/review";
const job = useJobStore();
// 雇主发布的任务列表
const employerTasks = ref([]);

// 当前选中的任务
const selectedTask = ref(null);

// 选定任务的申请人列表
const applicants = ref([]);

// 支付对话框相关
const paymentDialogVisible = ref(false);
const paymentMethod = ref("alipay");
const currentApplicant = ref(null);

// 评价对话框相关
const ratingDialogVisible = ref(false);
const ratingValue = ref(5);
const ratingComment = ref("");

// 组件挂载时获取任务列表
onMounted(async () => {
  employerTasks.value = await job.getTasks();
  if (employerTasks.value.length > 0) {
    selectedTask.value = employerTasks.value[0];
    await handleTaskSelect("0"); // 模拟选中第一个
  }
});

const fileStore = useFileStore();
// 选择任务
const handleTaskSelect = async (index) => {
  const taskIndex = parseInt(index, 10);
  if (employerTasks.value[taskIndex]) {
    selectedTask.value = employerTasks.value[taskIndex];
    applicants.value = await job.getTaskApplicants(selectedTask.value.id);
    applicants.value.forEach(async (applicant) => {
      const res = await fileStore.getImg(applicant.avatar);
      applicant.avatar = res;
    });
  } else {
    selectedTask.value = null;
    applicants.value = [];
  }
};
// 批准申请
const statuMap = new Map([
  [2, "进行中"],
  [3, "已完成"],
  [4, "已拒绝"],
  [5, "已支付"],
  [6, "已取消"],
  [7, "待确认完成"],
]);
const approveApplication = (applicant, statuId) => {
  ElMessageBox.confirm(
    `确定要批准 ${applicant.username} 的任务申请吗?`,
    "提示",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(async () => {
      job.updateApplicationStatus(applicant.jobId, statuId).then(() => {
        const appIndex = applicants.value.findIndex(
          (a) => a.id === applicant.id
        );
        if (appIndex !== -1) {
          const statu = statuMap.get(statuId);
          applicants.value[appIndex].status = statu;
        }
        ElMessage.success("操作已完成");
      });
    })
    .catch(() => {
      ElMessage.info("操作已取消");
    });
};

// 打开支付对话框
const openPaymentDialog = (applicant) => {
  currentApplicant.value = applicant;
  paymentDialogVisible.value = true;
};

// 确认支付
const confirmPayment = () => {
  if (!paymentMethod.value) {
    ElMessage.warning("请选择支付方式");
    return;
  }

  ElMessageBox.confirm(
    `确定要通过${
      paymentMethod.value === "alipay"
        ? "支付宝"
        : paymentMethod.value === "wechat"
        ? "微信支付"
        : "银行卡"
    }支付￥${currentApplicant.value?.income}吗?`,
    "支付确认",
    {
      confirmButtonText: "确定",
      cancelButtonText: "取消",
      type: "warning",
    }
  )
    .then(async () => {
      // 调用支付接口，这里模拟支付成功
      job
        .pay(currentApplicant.value.jobId, currentApplicant.value.workerId)
        .then(() => {
          const appIndex = applicants.value.findIndex(
            (a) => a.id === currentApplicant.value.id
          );
          if (appIndex !== -1) {
            applicants.value[appIndex].status = "已支付";
          }
          paymentDialogVisible.value = false;
          ElMessage.success("支付成功");
        });
    })
    .catch(() => {
      ElMessage.info("支付已取消");
    });
};

// 打开评价对话框
const openRatingDialog = (applicant) => {
  currentApplicant.value = applicant;
  ratingDialogVisible.value = true;
  ratingValue.value = 5; // 默认5星
  ratingComment.value = "";
};

const reviewStore = useReviewStore();
// 提交评价
const submitRating =async () => {

  if (ratingValue.value === 0) {
    ElMessage.warning("请选择评分");
    return;
  }
 await reviewStore.review({
    jobId: currentApplicant.value.jobId,
    toUserId: currentApplicant.value.workerId,
    content: ratingComment.value,
    rating: ratingValue.value,
  }).then(() => {
    ratingDialogVisible.value = false;
    ElMessage.success("评价提交成功");
    const appIndex = applicants.value.findIndex(
          (a) => a.id === applicant.id
        );
        if (appIndex !== -1) {
          applicants.value[appIndex].status = "已结束";
        }
 
  })
};
// 拒绝申请
const rejectApplication = (applicant, statuId) => {
  ElMessageBox.confirm(`确定要拒绝 ${applicant.name} 的任务申请吗?`, "提示", {
    confirmButtonText: "确定",
    cancelButtonText: "取消",
    type: "warning",
  })
    .then(async () => {
      job.updateApplicationStatus(applicant.jobId, statuId).then(() => {
        const appIndex = applicants.value.findIndex(
          (a) => a.id === applicant.id
        );
        if (appIndex !== -1) {
          applicants.value[appIndex].status = "已拒绝";
        }
        ElMessage.success("申请已拒绝");
      });
    })
    .catch(() => {
      ElMessage.info("操作已取消");
    });
};

// 获取申请人状态对应的标签类型
const getApplicantStatusType = (status) => {
  switch (status) {
    case "待接收":
      return "warning";
    case "已批准":
    case "进行中":
      return "success";
    case "已拒绝":
      return "danger";
    default:
      return "info";
  }
};
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.page-title {
  font-size: 24px;
  margin-bottom: 20px;
  color: #333;
  font-weight: 600;
}

.box-card {
  min-height: 700px; /* 确保卡片有足够高度 */
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
}

.empty-tasks,
.empty-applicants {
  text-align: center;
  color: #909399;
  padding: 20px;
  font-size: 14px;
}

.el-menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.placeholder-card {
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #909399;
}

.placeholder-content p {
  margin-top: 10px;
  font-size: 14px;
}

.el-table .el-button + .el-button {
  margin-left: 8px;
}

.applicants-table {
  width: 100%;
  display: flex;
}

/* 支付对话框样式 */
.payment-dialog-content {
  padding: 10px 0;
}

.payment-info {
  margin-bottom: 15px;
  font-size: 16px;
}

.payment-amount {
  margin-bottom: 20px;
  font-size: 16px;
}

.amount {
  color: #f56c6c;
  font-size: 20px;
  font-weight: bold;
}

.method-title {
  margin-bottom: 10px;
  font-weight: bold;
}

.payment-method {
  margin-top: 20px;
}

/* 评价对话框样式 */
.rating-dialog-content {
  padding: 10px 0;
}

.rating-info {
  margin-bottom: 20px;
  font-size: 16px;
}

.rating-title,
.comment-title {
  margin-bottom: 10px;
  font-weight: bold;
}

.rating-stars {
  margin-bottom: 20px;
}

.rating-comment {
  margin-top: 20px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
}
</style>
