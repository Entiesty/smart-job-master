<template>
  <div class="container">
    <h1 class="page-title">评价查看</h1>

    <div class="card">
      <div class="card-title">
        <span>筛选评价</span>
      </div>
      <el-form :inline="true" :model="reviewFilter" class="demo-form-inline">
        <el-form-item label="评分范围">
          <el-select
            v-model="reviewFilter.rating"
            placeholder="选择评分范围"
            clearable
          >
            <el-option label="5星" value="5-5"></el-option>
            <el-option label="4星及以上" value="4-5"></el-option>
            <el-option label="3星及以上" value="3-5"></el-option>
            <el-option label="全部评分" value="0-5"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="评价类型">
          <el-select
            v-model="reviewFilter.type"
            placeholder="选择评价类型"
            clearable
          >
            <el-option label="我对其他人的评价" value="me"></el-option>
            <el-option label="其他人对我的评价" value="other"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="时间范围">
          <el-select
            v-model="reviewFilter.time"
            placeholder="选择时间范围"
            clearable
          >
            <el-option label="最近一周" value="week"></el-option>
            <el-option label="最近一个月" value="month"></el-option>
            <el-option label="最近三个月" value="quarter"></el-option>
            <el-option label="全部时间" value="all"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
    </div>
    <!-- 评价列表 -->
    <div class="review-list">
      <div class="card" v-for="review in reviews" :key="review.id">
        <div class="review-card" @click="viewReviewDetail(review.id)">
          <div class="review-header">
            <img :src="review.fromUser.avatar" class="review-avatar" />
            <div class="review-info">
              <div class="review-name">{{ review.fromUser.username }}</div>
              <div class="review-rating">
                <el-rate
                  v-model="review.rating"
                  disabled
                  text-color="#ff9900"
                ></el-rate>
              </div>
              <div class="review-time">{{ review.createdAt }}</div>
            </div>
          </div>
          <!-- todo -->
          <!-- <div class="review-job-info">
            <span class="review-job-title">评价项目: {{ review.jobTitle }}</span>
            <span class="review-job-price">项目金额: {{ review.jobPrice }}</span>
          </div> -->
          <div class="review-content">{{ review.content }}</div>
        </div>
      </div>
    </div>
    <!-- 分页 -->
    <div class="pagination">
      <el-pagination
        background
        layout="prev, pager, next"
        :total="totalReviews"
        :page-size="pageSize"
        :current-page="currentPage"
        @current-change="handlePageChange"
      >
      </el-pagination>
    </div>

    <!-- 评价详情对话框 -->
    <el-dialog title="评价详情" v-model="reviewDetailVisible" width="50%">
      <div v-if="selectedReview">
        <div class="review-detail-header">
          <img :src="selectedReview.fromUser.avatar" class="review-detail-avatar" />
          <div class="review-detail-info">
            <h2>{{ selectedReview.fromUser.username }}</h2>
            <div class="review-detail-rating">
              <el-rate
                v-model="selectedReview.rating"
                disabled
                text-color="#ff9900"
              ></el-rate>
            </div>
            <div class="review-detail-time">{{ selectedReview.createdAt }}</div>
          </div>
        </div>
        <div class="review-detail-section">
          <h3>评价内容</h3>
          <p>{{ selectedReview.content }}</p>
        </div>
        <div
          class="review-detail-section"
          v-if="selectedReview.images && selectedReview.images.length > 0"
        >
          <h3>评价图片</h3>
          <div class="review-detail-images">
            <el-image
              v-for="(image, index) in selectedReview.images"
              :key="index"
              :src="image"
              :preview-src-list="selectedReview.images"
              fit="cover"
              class="review-detail-image"
            >
            </el-image>
          </div>
        </div>
        <div class="review-detail-section" v-if="selectedReview.reply">
          <h3>回复内容</h3>
          <div class="review-detail-reply">{{ selectedReview.reply }}</div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="reviewDetailVisible = false">关闭</el-button>
          <el-button
            type="primary"
            v-if="!selectedReview.reply"
            @click="showReplyForm = true"
            >回复</el-button
          >
        </span>
      </template>
    </el-dialog>
    <!-- 回复表单对话框 -->
    <el-dialog title="回复评价" v-model="showReplyForm" width="40%">
      <el-form :model="replyForm" label-width="80px">
        <el-form-item label="回复内容">
          <el-input
            type="textarea"
            v-model="replyForm.content"
            :rows="4"
            placeholder="请输入回复内容"
          ></el-input>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="showReplyForm = false">取消</el-button>
          <el-button type="primary" @click="submitReply">提交回复</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computedAsync } from "@vueuse/core";
import { ref, reactive } from "vue";
import { useReviewStore } from "../../store/review";
import { useFileStore } from "../../store/file";

// 筛选条件
const reviewFilter = reactive({
  rating: "",
  type: "",
  time: "",
});

// 分页相关
const currentPage = ref(1);
const pageSize = ref(10);
const totalReviews = ref(100);
const reviewStore = useReviewStore();
const fileStore = useFileStore();
// 评价列表
const reviews = computedAsync(async () => {
  const res = await reviewStore.getReview({
    current: currentPage.value,
    size: pageSize.value,
    total: totalReviews.value,
    ...reviewFilter,
  });
  await Promise.all(
    res.map(async (w) => {
      w.fromUser.avatar = await fileStore.getImg(w.fromUser.avatar);
    })
  );
  return res;
}, [currentPage, pageSize, totalReviews, reviewFilter]);


// 评价详情
const reviewDetailVisible = ref(false);
const selectedReview = ref(null);

// 回复表单
const showReplyForm = ref(false);
const replyForm = reactive({
  content: "",
});

// 查看评价详情
const viewReviewDetail = (reviewId) => {
  selectedReview.value = reviews.value.find((r) => r.id === reviewId);
  reviewDetailVisible.value = true;
};

// 提交回复
const submitReply = () => {
  if (selectedReview.value && replyForm.content) {
    // 这里应该调用API提交回复
    selectedReview.value.reply = replyForm.content;

    // 更新列表中的数据
    const index = reviews.value.findIndex(
      (r) => r.id === selectedReview.value.id
    );
    if (index !== -1) {
      reviews.value[index].reply = replyForm.content;
    }

    // 清空表单并关闭对话框
    replyForm.content = "";
    showReplyForm.value = false;
  }
};

// 搜索评价
const searchReviews = () => {
  // 调用评价store的getReview方法，传入当前分页参数和筛选条件
  // 参数说明:
  // - current: 当前页码
  // - size: 每页显示数量
  // - total: 总评价数
  // - ...reviewFilter: 展开筛选条件对象
  const res = reviewStore.getReview({
    current: currentPage.value,
    size: pageSize.value,
    total: totalReviews.value,
    ...reviewFilter
  });
  console.log(res); // 打印API响应结果
};

// 重置筛选条件
const resetFilter = () => {
  // 遍历筛选条件对象的所有属性
  Object.keys(reviewFilter).forEach((key) => {
    reviewFilter[key] = ""; // 将每个筛选条件重置为空字符串
  });
  searchReviews(); // 重置后立即执行搜索，刷新评价列表
};

const handlePageChange = (page) => {
  currentPage.value = page;
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
/* 评价卡片样式 */
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

.review-job-info {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 14px;
  color: #666;
}

.review-content {
  color: #333;
  font-size: 14px;
  line-height: 1.6;
  margin-bottom: 10px;
}

.review-tags {
  margin-bottom: 10px;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}

.review-image {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  cursor: pointer;
}

.review-reply {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-top: 10px;
}

.reply-title {
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 14px;
  color: #409eff;
}

.reply-content {
  font-size: 14px;
  color: #666;
}

/* 评价详情样式 */
.review-detail-header {
  display: flex;
  margin-bottom: 20px;
}

.review-detail-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  margin-right: 15px;
  object-fit: cover;
}

.review-detail-info {
  flex: 1;
}

.review-detail-time {
  color: #999;
  font-size: 12px;
  margin-top: 5px;
}

.review-detail-section {
  margin-bottom: 20px;
}

.review-detail-section h3 {
  font-size: 16px;
  margin-bottom: 10px;
  color: #333;
  font-weight: 600;
}

.review-detail-job {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
  margin-bottom: 10px;
}

.review-detail-job-title {
  font-weight: 600;
  margin-bottom: 5px;
  font-size: 16px;
}

.review-detail-job-price {
  color: #f56c6c;
  font-weight: 600;
  margin-bottom: 5px;
}

.review-detail-job-date {
  color: #666;
  font-size: 14px;
}

.review-detail-tags {
  margin-bottom: 10px;
}

.review-detail-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 10px;
}

.review-detail-image {
  width: 120px;
  height: 120px;
  border-radius: 4px;
  cursor: pointer;
}

.review-detail-reply {
  background-color: #f5f7fa;
  padding: 10px;
  border-radius: 4px;
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
.demo-form-inline{
  .el-form-item{
    width: 20%;
  }
}
</style>
