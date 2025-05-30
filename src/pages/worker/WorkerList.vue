<template>
  <div class="container">
    <h1 class="page-title">工作者查找</h1>

    <!-- 筛选卡片 -->
    <div class="card">
      <div class="card-title">
        <span>筛选工作者</span>
      </div>

      <el-form :inline="true" :model="workerFilter" class="demo-form-inline">
        <el-form-item label="技能类型">
          <el-select
            v-model="workerFilter.skill"
            placeholder="选择技能类型"
            clearable
          >
            <el-option
              v-for="item in workerSkills"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评分范围">
          <el-select
            v-model="workerFilter.rating"
            placeholder="选择评分范围"
            clearable
          >
            <el-option label="4.5分以上" value="4.5-5"></el-option>
            <el-option label="4.0-4.5分" value="4.0-4.5"></el-option>
            <el-option label="3.5-4.0分" value="3.5-4.0"></el-option>
            <el-option label="3.5分以下" value="0-3.5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="工作地点">
          <el-select
            v-model="workerFilter.location"
            placeholder="选择工作地点"
            clearable
          >
          <el-option
              v-for="item in workerLocations"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- 工作者列表 -->
    <div class="worker-list">
      <div
        class="worker-card"
        v-for="worker in workers"
        :key="worker.id"
        @click="viewWorkerDetail(worker.id)"
      >
        <div class="worker-header">
          <img :src="worker.avatar" class="worker-avatar" />
          <div class="worker-info">
            <div class="worker-name">{{ worker.username }}</div>
            <div class="worker-title">{{ worker.title }}</div>
            <div class="worker-rating">
              <el-rate
                v-model="worker.rating"
                disabled
                show-score
                text-color="#ff9900"
              ></el-rate>
              <span style="margin-left: 5px"
                >({{ worker.reviews?.length }}条评价)</span
              >
            </div>
          </div>
        </div>
        <div class="worker-skills">
          <el-tag
            type="primary"
            size="small"
            v-for="skill in worker.label"
            :key="skill"
            >{{ skill }}</el-tag
          >
        </div>
        <div class="worker-bio">{{ worker.introduction }}</div>
        <div class="worker-footer">
          <el-tag type="success" size="small"
            >已完成 {{ worker.doneJobs }} 个工作</el-tag
          >
          <el-button
            type="primary"
            size="small"
            @click.stop="contactPublisher(worker.id)"
            >联系TA</el-button
          >
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalWorkers"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      >
      </el-pagination>
    </div>

    <!-- 工作者详情对话框 -->
    <el-dialog title="工作者详情" v-model="workerDetailVisible" width="50%">
      <div v-if="selectedWorker">
        <div class="worker-detail-header">
          <img :src="selectedWorker.avatar" class="worker-detail-avatar" />
          <div class="worker-detail-info">
            <h2>{{ selectedWorker.username }}</h2>
            <div class="worker-detail-title">{{ selectedWorker.title }}</div>
            <div class="worker-detail-rating">
              <el-rate
                v-model="selectedWorker.rating"
                disabled
                show-score
                text-color="#ff9900"
              ></el-rate>
            </div>
            <div class="worker-detail-location">
              <i class="el-icon-location"></i> {{ selectedWorker.location }}
            </div>
          </div>
        </div>

        <div class="worker-detail-section">
          <h3>技能标签</h3>
          <div class="worker-detail-skills">
            <el-tag
              v-for="skill in selectedWorker.label"
              :key="skill"
              class="tag tag-primary"
              size="small"
              >{{ skill }}</el-tag
            >
          </div>
        </div>

        <div class="worker-detail-section">
          <h3>个人简介</h3>
          <p>{{ selectedWorker.introduction }}</p>
        </div>
        <div class="worker-detail-section">
          <h3>工作经验</h3>
          <div class="timeline">
            <div>
              <div class="timeline-time">{{ selectedWorker.exp }}</div>
            </div>
          </div>
        </div>
        <div class="worker-detail-section">
          <h3>客户评价</h3>
          <div
            class="review-card"
            v-for="review in selectedWorker.reviews"
            :key="review.id"
          >
            <div class="review-header">
              <div class="review-info">
                <div class="review-name">
                  <el-avatar
                    :src="review.fromUser.avatar"
                    class="review-avatar"
                  />
                  <div class="name">{{ review.fromUser.username }}</div>
                  <div class="review-time">{{ review.createdAt }}</div>
                </div>
                <div class="review-rating">
                  <el-rate
                    v-model="review.rating"
                    disabled
                    text-color="#ff9900"
                  ></el-rate>
                </div>
              </div>
            </div>
            <div class="review-content">{{ review.content }}</div>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="workerDetailVisible = false">关闭</el-button>
          <el-button type="primary" @click="contactPublisher(selectedWorker.id)"
            >联系TA</el-button
          >
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computedAsync } from "@vueuse/core";
import { ref, reactive } from "vue";
import { useJobStore } from "../../store/job";
import { useFileStore } from "../../store/file";
import { useChatStore } from "../../store/chat";
import { useUserStore } from "../../store/user";
const job = useJobStore();
const fileStore = useFileStore();
const workerSkills = computedAsync(async () => {
  const res = await job.getWorkerSkillLabels();
  return res;
});
const workerLocations = computedAsync(async () => {
  const res = await job.getWorkerLocations();
  return res;
});
// 筛选条件
const workerFilter = reactive({
  skill: "",
  rating: "0-5",
  location: "",
});

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const totalWorkers = ref(100);
// 重置筛选条件
// 工作者列表
const workers = computedAsync(async () => {
  const res = await job.getWorkers({
    current: currentPage.value,
    size: pageSize.value,
    total: totalWorkers.value,
    ...workerFilter,
  });
  // 加载图片
  await Promise.all(
    res.map(async (w) => {
      w.avatar = await fileStore.getImg(w.avatar);
      w.reviews.map(async (r) => {
        r.fromUser.avatar = await fileStore.getImg(r.fromUser.avatar);
      });
    })
  );
  return res;
}, [currentPage, pageSize, totalWorkers, workerFilter]);

// 工作者详情
const workerDetailVisible = ref(false);
const selectedWorker = ref(null);

// 查看工作者详情
const viewWorkerDetail = (workerId) => {
  selectedWorker.value = workers.value.find((w) => w.id === workerId);
  workerDetailVisible.value = true;
};
import { useRouter } from "vue-router";
const chat = useChatStore();
const userStore = useUserStore();
const router = useRouter();
const contactPublisher = async (id) => {
  try {
    // 创建聊天会话
   await chat.createChatSession(userStore.userInfo.id, id);
    router.push( '/chat');
    
  } catch (error) {
    console.error("创建聊天会话失败:", error);
    ElMessage.error("联系失败，请稍后重试");
  }
};
// 处理分页变化
const handlePageChange = (page) => {
  currentPage.value = page;
  // 这里应该调用API获取对应页码的数据
};
</script>

<style scoped>
.container {
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  padding: 20px;
  margin-bottom: 20px;
}
.review-card {
  border: 1px solid #ebeef5;
  border-radius: 8px;
  padding: 15px;
  .review-name {
    display: flex;
    .name {
      margin-left: 10px;
      align-content: center;
    }
    .review-time {
      margin-left: 10px;
      align-content: center;
    }
  }
}

/* 工作者卡片样式 */
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

/* 工作者详情样式 */
.worker-detail-header {
  display: flex;
  margin-bottom: 20px;
}

.worker-detail-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  margin-right: 20px;
  object-fit: cover;
}

.worker-detail-info {
  flex: 1;
}

.worker-detail-title {
  color: #409eff;
  font-size: 16px;
  margin: 5px 0;
}

.worker-detail-location {
  color: #666;
  font-size: 14px;
  margin-top: 5px;
}

.worker-detail-section {
  margin-bottom: 20px;
}

.worker-detail-section h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  font-weight: 600;
}

.worker-detail-skills {
  margin-bottom: 10px;
}

.timeline-title {
  font-weight: 600;
  margin-bottom: 5px;
}

.timeline-description {
  color: #666;
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

/* 分页样式 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
.demo-form-inline {
  .el-form-item {
    margin-bottom: 0;
    width: 20%;
  }
}
</style>
