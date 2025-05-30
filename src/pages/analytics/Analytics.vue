<template>
  <div class="analytics-container">
    <el-card class="analytics-card">
      <template #header>
        <div class="analytics-header">
          <h2>数据统计与分析</h2>
          <el-button type="text" @click="refreshData">刷新数据</el-button>
        </div>
      </template>
      
      <el-tabs v-model="activeTab" type="card">
        <el-tab-pane label="用户统计" name="user">
          <div class="chart-container">
            <div class="chart-item">
              <h3>用户增长趋势</h3>
              <div ref="userGrowthChart" style="width: 100%; height: 300px;"></div>
            </div>
            <div class="chart-item">
              <h3>用户类型分布</h3>
              <div ref="userTypeChart" style="width: 100%; height: 300px;"></div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="任务统计" name="job">
          <div class="chart-container">
            <div class="chart-item">
              <h3>任务完成情况</h3>
              <div ref="jobCompletionChart" style="width: 100%; height: 300px;"></div>
            </div>
            <div class="chart-item">
              <h3>任务类型分布</h3>
              <div ref="jobTypeChart" style="width: 100%; height: 300px;"></div>
            </div>
          </div>
        </el-tab-pane>
        
        <el-tab-pane label="收入统计" name="income">
          <div class="chart-container">
            <div class="chart-item">
              <h3>平台收入趋势</h3>
              <div ref="incomeTrendChart" style="width: 100%; height: 300px;"></div>
            </div>
            <div class="chart-item">
              <h3>收入来源分布</h3>
              <div ref="incomeSourceChart" style="width: 100%; height: 300px;"></div>
            </div>
          </div>
        </el-tab-pane>
      </el-tabs>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import * as echarts from 'echarts';

const activeTab = ref('user');
const userGrowthChart = ref(null);
const userTypeChart = ref(null);
const jobCompletionChart = ref(null);
const jobTypeChart = ref(null);
const incomeTrendChart = ref(null);
const incomeSourceChart = ref(null);

let chartInstances = {};

const initCharts = () => {
  // 用户增长趋势图
  chartInstances.userGrowth = echarts.init(userGrowthChart.value);
  chartInstances.userGrowth.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: [120, 200, 150, 80, 70, 110] }]
  });

  // 用户类型分布图
  chartInstances.userType = echarts.init(userTypeChart.value);
  chartInstances.userType.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      data: [
        { value: 735, name: '雇主' },
        { value: 580, name: '零工者' },
        { value: 484, name: '双角色用户' }
      ]
    }]
  });

  // 任务完成情况图
  chartInstances.jobCompletion = echarts.init(jobCompletionChart.value);
  chartInstances.jobCompletion.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [
      { type: 'bar', name: '总任务数', data: [120, 200, 150, 80, 70, 110] },
      { type: 'bar', name: '完成任务数', data: [100, 180, 130, 70, 60, 90] }
    ]
  });

  // 任务类型分布图
  chartInstances.jobType = echarts.init(jobTypeChart.value);
  chartInstances.jobType.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      radius: ['40%', '70%'],
      data: [
        { value: 335, name: '技术开发' },
        { value: 310, name: '设计创意' },
        { value: 234, name: '文案写作' },
        { value: 135, name: '营销推广' },
        { value: 154, name: '其他' }
      ]
    }]
  });

  // 收入趋势图
  chartInstances.incomeTrend = echarts.init(incomeTrendChart.value);
  chartInstances.incomeTrend.setOption({
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{ type: 'line', data: [820, 932, 901, 934, 1290, 1330] }]
  });

  // 收入来源分布图
  chartInstances.incomeSource = echarts.init(incomeSourceChart.value);
  chartInstances.incomeSource.setOption({
    tooltip: { trigger: 'item' },
    series: [{
      type: 'pie',
      roseType: 'radius',
      data: [
        { value: 1048, name: '技术服务费' },
        { value: 735, name: '会员费' },
        { value: 580, name: '广告收入' },
        { value: 484, name: '其他收入' }
      ]
    }]
  });
};

const refreshData = async () => {
  try {
    const res = await getAnalyticsData();
    // 更新图表数据
    // 这里可以根据实际API返回的数据结构进行更新
    chartInstances.userGrowth.setOption({
      series: [{ data: res.data.userGrowth }]
    });
    ElMessage.success('数据刷新成功');
  } catch (error) {
    ElMessage.error('数据刷新失败');
  }
};

const resizeCharts = () => {
  Object.values(chartInstances).forEach(chart => {
    chart && chart.resize();
  });
};

onMounted(() => {
  initCharts();
  window.addEventListener('resize', resizeCharts);
});

onBeforeUnmount(() => {
  window.removeEventListener('resize', resizeCharts);
  Object.values(chartInstances).forEach(chart => {
    chart && chart.dispose();
  });
});
</script>

<style lang="scss" scoped>
.analytics-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .analytics-card {
    margin-bottom: 20px;

    .analytics-header {
      display: flex;
      align-items: center;
      justify-content: space-between;

      h2 {
        margin: 0;
      }
    }
    
    .chart-container {
      display: flex;
      flex-wrap: wrap;
      gap: 20px;
      margin-top: 20px;
      
      .chart-item {
        flex: 1;
        min-width: 500px;
        padding: 20px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
        
        h3 {
          margin-top: 0;
          margin-bottom: 20px;
          text-align: center;
        }
      }
    }
  }
}
</style>