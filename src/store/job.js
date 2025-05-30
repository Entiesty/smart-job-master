import { defineStore } from 'pinia';
import { ref } from 'vue';
import { getJobsApi ,postJobApi} from '@/api/api';
import { ElMessage } from 'element-plus';
import { applyJobApi,getJobAcceptancesApi,getSkillLabelsApi,getLocationsApi, getTasksApi, getTaskApplicantsApi, updateApplicationStatusApi, getJobByIdApi, payApi, getWorkersApi, getWorkerSkillLabelsApi, getWorkerLocationsApi } from '../api/api';

export const useJobStore = defineStore('job', () => {
  const jobs = ref([]);
  const skillLabels=ref([]);
  const locations=ref([]);
  const getJobAcceptances =async () => {
    const response = await getJobAcceptancesApi();
    return  response.data.data;
  } 
  const getJobs = async (data) => {
    const response = await getJobsApi(data);
    jobs.value = response.data.data;
    return jobs.value;
  };
  const getJobById = async (id) => {
    const response = await getJobByIdApi(id);
    return response.data.data;
  };
  
  const postJob = async (data) => {
    const response = await postJobApi(data);
      if(response.data.code === 200){
        ElMessage.success('发布成功')
      }else{
        ElMessage.error('发布失败')
  };
}
  const applyJob = async (jobId) => {
    await applyJobApi(jobId);
    // 更新任务状态
    const job = jobs.value.find(j => j.id === jobId);
    if (job) {
      job.status = '已申请';
    }
  };
  const getSkillLabels =async () => {
    const res =  await getSkillLabelsApi();
    skillLabels.value = res.data.data;
  }
  const getWorkerSkillLabels=async () => {
    const res =  await getWorkerSkillLabelsApi();
    return res.data.data;
  }
  const getLocations =async () => {
    const res =  await getLocationsApi();
    locations.value = res.data.data;
  }
  const getWorkerLocations =async () => {
    const res =  await getWorkerLocationsApi();
    return res.data.data;
  };
  const getTasks = async () => {
    const res =  await getTasksApi();
    return res.data.data;
  };
  const getTaskApplicants = async (jobId) => {
    const res =  await getTaskApplicantsApi(jobId);
    return res.data.data;
  };
  const updateApplicationStatus = async (acceptanceId,status) => {
    console.log(acceptanceId,status)
    const response = await updateApplicationStatusApi(acceptanceId,status);
    if (response.data.code === 200) {
      ElMessage.success('更新成功');
    } else {
      ElMessage.error('更新失败');
    }
  };
  const pay = async (jobId, workerId) => {
    await payApi(jobId, workerId);
  };
  const getWorkers = async (page) => {
    const res = await getWorkersApi(page);
    return res.data.data;
  };
  return {
    jobs,
    getJobs,
    getJobAcceptances,
    updateApplicationStatus,
    getWorkerLocations,
    getLocations,
    getTaskApplicants,
    getWorkerSkillLabels,
    getWorkers,
    getTasks,
    locations,
    pay,
    getSkillLabels,
    applyJob,
    postJob,
    getJobById,
    skillLabels,
  };
});