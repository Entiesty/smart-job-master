<template>
  <div class="post-job-container">
    <h2 class="page-title">发布任务</h2>
    <el-form
      :model="jobForm"
      :rules="rules"
      ref="jobFormRef"
      label-width="120px"
      class="job-form"
    >
      <el-form-item label="任务标题" prop="title">
        <el-input
          v-model="jobForm.title"
          placeholder="请输入任务标题"
        ></el-input>
      </el-form-item>

      <el-form-item label="任务类别" prop="category">
        <el-select
          v-model="jobForm.category"
          placeholder="请选择任务类别"
          style="width: 100%"
        >
          <el-option
            v-for="item in categoryOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="任务描述" prop="description">
        <el-input
          type="textarea"
          v-model="jobForm.description"
          :rows="4"
          placeholder="请详细描述任务内容、要求等信息"
        ></el-input>
      </el-form-item>
      <el-form-item label="任务要求" prop="requirement">
        <el-input
          type="textarea"
          v-model="jobForm.requirement"
          :rows="4"
          placeholder="请输入任务要求等信息"
        ></el-input>
      </el-form-item>
      <el-form-item label="工作地点" prop="location">
        <el-input
          v-model="jobForm.location"
          placeholder="请输入工作地点"
        ></el-input>
      </el-form-item>

      <el-form-item label="薪资预算" prop="price">
        <el-input-number
          v-model="jobForm.price"
          :min="0"
          :precision="2"
          :step="100"
          style="width: 100%"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="计薪方式" prop="salaryType">
        <el-radio-group v-model="jobForm.salaryType">
          <el-radio :label="'hourly'">按小时</el-radio>
          <el-radio :label="'daily'">按天</el-radio>
          <el-radio :label="'fixed'">固定金额</el-radio>
        </el-radio-group>
      </el-form-item>

      <el-form-item label="开始日期" prop="startDate">
        <el-date-picker
          v-model="jobForm.startDate"
          type="date"
          placeholder="选择开始日期"
          style="width: 100%"
        ></el-date-picker>
      </el-form-item>

      <el-form-item label="结束日期" prop="endDate">
        <el-date-picker
          v-model="jobForm.endDate"
          type="date"
          placeholder="选择结束日期"
          style="width: 100%"
        ></el-date-picker>
      </el-form-item>

      <el-form-item label="所需人数" prop="headcount">
        <el-input-number
          v-model="jobForm.headcount"
          :min="1"
          :max="100"
          style="width: 100%"
        ></el-input-number>
      </el-form-item>

      <el-form-item label="技能要求" prop="skills">
        <el-select
          v-model="jobForm.skills"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="请选择或添加所需技能"
          style="width: 100%"
        >
          <el-option
            v-for="item in skillOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          ></el-option>
        </el-select>
      </el-form-item>

      <el-form-item label="联系方式" prop="contractInfo">
        <el-input
          v-model="jobForm.contractInfo"
          placeholder="请输入联系方式"
        ></el-input>
      </el-form-item>
      <el-form-item label="资料上传">
        <div class="upload">
          <el-upload
            :http-request="uploadFile"
            multiple
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">合同上传</el-button>
            <template #tip>
              <div class="el-upload__tip">可上传合同、协议等</div>
            </template>
          </el-upload>
          <el-upload
            :http-request="uploadContractFile"
            multiple
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button type="primary">附件上传</el-button>
            <template #tip>
              <div class="el-upload__tip">可上传需求文档、参考图片等</div>
            </template>
          </el-upload>
        </div>
      </el-form-item>
      <div class="button">
        <el-form-item>
          <el-button type="primary" @click="submitForm">发布任务</el-button>
          <el-button @click="resetForm">重置</el-button>
        </el-form-item>
      </div>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import { useJobStore } from "@/store/job";
import { useUserStore } from "../../store/user";
import { baseURL } from "../../api";
import { useFileStore } from "../../store/file";

const router = useRouter();
const jobStore = useJobStore();
const jobFormRef = ref(null);
const userStore = useUserStore();
// 表单数据
const jobForm = reactive({
  title: "",
  category: "",
  description: "",
  location: "",
  price: 0,
  salaryType: "hourly",
  startDate: "",
  endDate: "",
  headcount: 1,
  skills: [],
  contractInfo: "",
  userId: "",
  contractFilePath: "",
  otherFilePath: "",
  requirement: "",
});

// 任务类别选项
const categoryOptions = [
  { value: "快递配送", label: "快递配送" },
  { value: "保洁服务", label: "保洁服务" },
  { value: "搬家服务", label: "搬家服务" },
  { value: "维修服务", label: "维修服务" },
  { value: "设计服务", label: "设计服务" },
  { value: "教育培训", label: "教育培训" },
  { value: "市场营销", label: "市场营销" },
  { value: "IT服务", label: "IT服务" },
  { value: "其他服务", label: "其他服务" },
];

// 技能选项
const skillOptions = [
  { value: "驾驶技能", label: "驾驶技能" },
  { value: "烹饪技能", label: "烹饪技能" },
  { value: "编程技能", label: "编程技能" },
  { value: "设计技能", label: "设计技能" },
  { value: "语言技能", label: "语言技能" },
  { value: "教学技能", label: "教学技能" },
  { value: "销售技能", label: "销售技能" },
  { value: "管理技能", label: "管理技能" },
];

// 表单验证规则
const rules = {
  title: [
    { required: true, message: "请输入任务标题", trigger: "blur" },
    { min: 2, max: 50, message: "长度在 2 到 50 个字符", trigger: "blur" },
  ],
  category: [{ required: true, message: "请选择任务类别", trigger: "change" }],
  description: [
    { required: true, message: "请输入任务描述", trigger: "blur" },
    { min: 10, max: 500, message: "长度在 10 到 500 个字符", trigger: "blur" },
  ],
  location: [{ required: true, message: "请输入工作地点", trigger: "blur" }],
  price: [
    { required: true, message: "请输入薪资预算", trigger: "blur" },
    { type: "number", min: 0, message: "薪资不能小于0", trigger: "blur" },
  ],
  startDate: [
    { required: true, message: "请选择开始日期", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (value && jobForm.endDate && value > jobForm.endDate) {
          callback(new Error("开始日期不能晚于结束日期"));
        } else {
          callback();
        }
      },
      trigger: "change",
    },
  ],
  endDate: [
    { required: true, message: "请选择结束日期", trigger: "change" },
    {
      validator: (rule, value, callback) => {
        if (value && jobForm.startDate && value < jobForm.startDate) {
          callback(new Error("结束日期不能早于开始日期"));
        } else {
          callback();
        }
      },
      trigger: "change",
    },
  ],
  headcount: [
    { required: true, message: "请输入所需人数", trigger: "blur" },
    { type: "number", min: 1, message: "人数不能小于1", trigger: "blur" },
  ],
  contractInfo: [
    { required: true, message: "请输入联系方式", trigger: "blur" },
  ],
  requirement: [
    { required: true, message: "请输入任务要求", trigger: "blur" },
    { min: 10, max: 500, message: "长度在 10 到 500 个字符", trigger: "blur" },
  ],
};

// 提交表单
const submitForm = async () => {
  if (!jobFormRef.value) return;
  await jobFormRef.value.validate(async (valid) => {
    if (valid) {
      try {
        jobForm.userId = parseInt(userStore.userInfo.id);
        await jobStore.postJob(jobForm);
        router.push("/jobList");
      } catch (error) {
        console.error(error);
        ElMessage.error("任务发布失败，请重试");
      }
    } else {
      ElMessage.error("请正确填写表单信息");
      return false;
    }
  });
};

// 重置表单
const resetForm = () => {
  console.log("1" + userStore.userInfo.id);
  jobFormRef.value.resetFields();
};
const fileStore = useFileStore();
const uploadFile = async (file) => {
  jobForm.otherFilePath = await fileStore.uploadFile(file.file);
};
const uploadContractFile = async (file) => {
  jobForm.contractFilePath = await fileStore.uploadFile(file.file);
};
const handleExceed = (files, fileList) => {
  ElMessage.warning(`当前限制选择 1 个文件`);
};
</script>

<style lang="scss" scoped>
.post-job-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;

  .page-title {
    font-size: 24px;
    font-weight: bold;
    margin-bottom: 30px;
    color: #303133;
    text-align: center;
  }

  .job-form {
    background-color: #fff;
    padding: 30px;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
    .el-form-item {
      margin-bottom: 22px;
      display: flex;
      align-items: center;
      justify-content: space-between;
    }
    .button {
      display: flex;
      justify-content: space-between;
      .el-button {
        margin-left: 100px;
      }
    }
  }

  @media screen and (max-width: 768px) {
    .job-form {
      padding: 20px 15px;
    }
  }
}
.upload {
  display: flex;
  justify-content: space-between;
  :deep(.el-upload) {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    margin-left: 40px;
  }
  :deep(.el-upload__tip) {
    font-size: 14px;
    color: #999;
    margin-top: 10px;
    margin-left: 40px;
  }
}
</style>
