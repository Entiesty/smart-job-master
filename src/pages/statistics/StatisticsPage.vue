<template>
  <div class="statistics-container">
    <h2>数据统计</h2>
    <div class="charts">
      <div class="chart-item">
        <div ref="jobTrendChart" style="width: 100%; height: 300px;"></div>
      </div>
      <div class="chart-item">
        <div ref="jobCategoryChart" style="width: 100%; height: 300px;"></div>
      </div>
      <div class="chart-item">
        <div ref="completionRateChart" style="width: 100%; height: 300px;"></div>
      </div>
      <div class="chart-item">
        <div ref="userActivityChart" style="width: 100%; height: 300px;"></div>
      </div>
      <div class="chart-item">
        <div ref="incomeTrendChart" style="width: 100%; height: 300px;"></div>
      </div>
      <div class="chart-item">
        <div ref="skillDemandChart" style="width: 100%; height: 300px;"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';
import { useJobStore } from '@/store/job';

const jobStore = useJobStore();
const jobTrendChart = ref(null);
const jobCategoryChart = ref(null);
const completionRateChart = ref(null);
const userActivityChart = ref(null);
const incomeTrendChart = ref(null);
const skillDemandChart = ref(null);

onMounted(() => {
  // 初始化任务趋势图表
  const trendChart = echarts.init(jobTrendChart.value);
  trendChart.setOption({
    title: { text: '任务发布趋势' },
    tooltip: {},
    xAxis: { data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: {},
    series: [{
      name: '任务量',
      type: 'bar',
      data: [5, 20, 36, 10, 10, 20]
    }]
  });

  // 初始化任务类别图表
  const categoryChart = echarts.init(jobCategoryChart.value);
  categoryChart.setOption({
    title: { text: '任务类别分布' },
    tooltip: { trigger: 'item' },
    series: [{
      name: '任务类别',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: 'IT技术' },
        { value: 735, name: '设计创意' },
        { value: 580, name: '文案写作' },
        { value: 484, name: '翻译服务' },
        { value: 300, name: '其他' }
      ]
    }]
  });

  // 初始化任务完成率趋势图
  const completionChart = echarts.init(completionRateChart.value);
  completionChart.setOption({
    title: { text: '任务完成率趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value', axisLabel: { formatter: '{value}%' } },
    series: [{
      name: '完成率',
      type: 'line',
      data: [85, 90, 87, 92, 95, 93]
    }]
  });

  // 初始化用户活跃度统计
  const activityChart = echarts.init(userActivityChart.value);
  activityChart.setOption({
    title: { text: '用户活跃度统计' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{
      name: '活跃用户数',
      type: 'bar',
      data: [1200, 1800, 1500, 2000, 2200, 2500]
    }]
  });

  // 初始化收入增长趋势
  const incomeChart = echarts.init(incomeTrendChart.value);
  incomeChart.setOption({
    title: { text: '收入增长趋势' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: ['1月', '2月', '3月', '4月', '5月', '6月'] },
    yAxis: { type: 'value' },
    series: [{
      name: '收入(万元)',
      type: 'line',
      data: [12, 18, 15, 20, 22, 25]
    }]
  });

  // 初始化热门技能需求分布
  const skillChart = echarts.init(skillDemandChart.value);
  skillChart.setOption({
    title: { text: '热门技能需求分布' },
    tooltip: { trigger: 'item' },
    series: [{
      name: '技能需求',
      type: 'pie',
      radius: '50%',
      data: [
        { value: 1048, name: '前端开发' },
        { value: 735, name: 'UI设计' },
        { value: 580, name: '文案撰写' },
        { value: 484, name: '数据分析' },
        { value: 300, name: '其他' }
      ]
    }]
  });
});
</script>

<style scoped>
.statistics-container {
  padding: 20px;
}
.charts {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}
.chart-item {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>