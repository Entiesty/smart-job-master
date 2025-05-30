<template>
  <div class="contract-container">
    <el-card class="contract-card">
      <template #header>
        <div class="contract-header">
          <h2>合同签署</h2>
          <el-button type="text" @click="showTemplateDialog">选择模板</el-button>
        </div>
      </template>
      <el-form :model="contractForm" ref="contractFormRef" label-width="120px">
        <el-form-item label="合同标题" prop="title">
          <el-input v-model="contractForm.title"></el-input>
        </el-form-item>
        <el-form-item label="合同类型" prop="type">
          <el-select v-model="contractForm.type" placeholder="请选择合同类型">
            <el-option
              v-for="item in contractTypes"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="合同内容" prop="content">
          <el-input type="textarea" v-model="contractForm.content" :rows="10"></el-input>
        </el-form-item>
        <el-form-item label="签署日期" prop="signDate">
          <el-date-picker v-model="contractForm.signDate" type="date" placeholder="选择日期"></el-date-picker>
        </el-form-item>
        <el-form-item label="电子签名" prop="signature">
          <signature-pad v-model="contractForm.signature" ref="signaturePadRef" @signature-submit="handleSignatureSubmit" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="submitContract">提交合同</el-button>
          <el-button @click="previewContract">预览合同</el-button>
        </el-form-item>
      </el-form>
    </el-card>
    
    <el-dialog v-model="templateDialogVisible" title="合同模板" width="70%">
      <el-table :data="templates" height="400" @row-click="selectTemplate">
        <el-table-column prop="name" label="模板名称" width="150"></el-table-column>
        <el-table-column prop="description" label="模板描述"></el-table-column>
      </el-table>
    </el-dialog>
    
    <el-dialog v-model="previewDialogVisible" title="合同预览" width="80%">
      <div class="contract-preview">
        <h2>{{ contractForm.title }}</h2>
        <div class="contract-content" v-html="contractForm.content"></div>
        <div class="signature-area">
          <div class="signature-box">
            <div>甲方签字：</div>
            <img v-if="contractForm.signature" :src="contractForm.signature" class="signature-img" />
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import SignaturePad from '@/components/SignaturePad.vue';

const contractFormRef = ref(null);
const contractForm = reactive({
  title: '',
  type: '',
  content: '',
  signDate: '',
  signature: ''
});

const contractTypes = [
  { value: 'short_term', label: '短期零工合同' },
  { value: 'long_term', label: '长期合作合同' },
  { value: 'project', label: '项目合同' }
];

const templates = ref([]);
const templateDialogVisible = ref(false);
const previewDialogVisible = ref(false);

const showTemplateDialog = async () => {
  try {
    const res = await getTemplates();
    templates.value = res.data;
    templateDialogVisible.value = true;
  } catch (error) {
    ElMessage.error('获取模板失败');
  }
};

const selectTemplate = (template) => {
  contractForm.title = template.name;
  contractForm.content = template.content;
  templateDialogVisible.value = false;
};

// 处理签名提交事件
const handleSignatureSubmit = (signatureData) => {
  contractForm.signature = signatureData;
  ElMessage.success('签名已提交');
};

const previewContract = () => {
  if (!contractForm.title || !contractForm.content) {
    ElMessage.warning('请填写合同标题和内容');
    return;
  }
  console.log(contractForm)
  previewDialogVisible.value = true;
};

const submitContract = async () => {
  try {
    if (!contractForm.signature) {
      ElMessage.warning('请提供电子签名');
      return;
    }
    
    // 这里应该调用API提交合同信息
    // await contractStore.submitContract(contractForm);
    ElMessage.success('合同提交成功');
  } catch (error) {
    console.error(error);
    ElMessage.error('提交失败，请重试');
  }
};

onMounted(() => {
  // 初始化默认合同类型
  contractForm.type = 'short_term';
});
</script>

<style lang="scss" scoped>
.contract-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;

  .contract-card {
    margin-bottom: 20px;

    .contract-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      h2 {
        margin: 0;
      }
    }
  }
  
  .contract-preview {
    padding: 20px;
    background-color: #fff;
    
    .contract-content {
      margin: 20px 0;
      line-height: 1.8;
    }
    
    .signature-area {
      display: flex;
      justify-content: space-between;
      margin-top: 60px;
      
      .signature-box {
        width: 200px;
        border-top: 1px solid #333;
        padding-top: 10px;
        text-align: center;
        
        .signature-img {
          max-width: 100%;
          max-height: 80px;
          margin-top: 10px;
        }
      }
    }
  }
}
</style>