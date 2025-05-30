<template>
  <div class="statistics">
    <el-row :gutter="20">
      <el-col :span="12">
        <el-card>
          <h3>任务完成统计</h3>
          <div class="chart-container">
            <el-empty description="暂无数据" v-if="!stats.jobCompletion" />
            <div v-else class="chart" ref="jobChart"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <h3>收入趋势</h3>
          <div class="chart-container">
            <el-empty description="暂无数据" v-if="!stats.incomeTrend" />
            <div v-else class="chart" ref="incomeChart"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useStatisticStore } from '@/store/statistic';
import * as echarts from 'echarts';

const statisticStore = useStatisticStore();
const stats = ref({});
const jobChart = ref(null);
const incomeChart = ref(null);

onMounted(async () => {
  stats.value = await statisticStore.getStatistics();
  
  if (stats.value.jobCompletion) {
    initJobChart();
  }
  
  if (stats.value.incomeTrend) {
    initIncomeChart();
  }
});

const initJobChart = () => {
  const chart = echarts.init(jobChart.value);
  chart.setOption({
    tooltip: {},
    series: [{
      type: 'pie',
      data: stats.value.jobCompletion
    }]
  });
};

const initIncomeChart = () => {
  const chart = echarts.init(incomeChart.value);
  chart.setOption({
    xAxis: {
      type: 'category',
      data: stats.value.incomeTrend.map(item => item.month)
    },
    yAxis: {
      type: 'value'
    },
    series: [{
      data: stats.value.incomeTrend.map(item => item.amount),
      type: 'line'
    }]
  });
};
</script>

<style scoped>
.statistics {
  padding: 20px;
}
.chart-container {
  height: 300px;
}
.chart {
  width: 100%;
  height: 100%;
}
</style>