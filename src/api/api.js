import api from "./index";

// 用户相关API
export const getUserInfoApi = () => api.get(`/auth/getUserInfo`);
export const captchaApi = () => api.get("/auth/captcha");
export const loginApi = (credentials) => api.post("/auth/login", credentials);
export const autoLoginApi = () => api.get("/auth/autoLogin");
export const logoutApi = () => api.get("/auth/logout");
export const getUserAvatarApi = (userId) => api.get(`/auth/getAvatar/${userId}`);
export const enterpriseRegisterApi = (userData) =>
  api.post("/auth/enterpriseRegister", userData);
export const workerRegisterApi = (userData) =>
  api.post("/auth/workerRegister", userData);
export const verifyCaptchaApi = (key, value) =>
  api.post("/auth/verifyCaptcha", {
    captchaKey: key,
    captcha: value,
  });
export const updateUserInfoApi = (profileData) =>
  api.put("/auth/updateUserInfo", profileData);
export const updateUserCertificatesApi = (profileData) =>
  api.put("/auth/updateUserCertificates", profileData);
export const deleteUserCertificatesApi = (id) =>
  api.delete(`/auth/deletUserCertificates/${id}`, id);
export const updateWorkerApi = (data) => api.put(`/auth/updateWorker`,data);
export const updateEnterpriseApi = (data) => api.put(`/auth/updateEnterprise`,data);

// 任务相关API
export const getJobsApi = (data) => api.post("/job/jobs", data);
export const getJobByIdApi = (id) => api.get(`/job/jobs/${id}`);
export const applyJobApi = (data) => api.post(`/job/apply`, data);
export const getJobAcceptancesApi = () => api.get(`/job/getJobAcceptances`);
export const getSkillLabelsApi = () => api.get(`/job/getSkillLabel`);
export const getWorkerSkillLabelsApi = () => api.get(`/job/getWorkerSkillLabel`);

export const getLocationsApi = () => api.get(`/job/getLocations`);
export const getWorkerLocationsApi = () => api.get(`/job/getWorkerLocations`);

export const getWorkersApi = (page) => api.post(`/job/getWorkers`,page);
// 企业相关API
export const postJobApi = (jobData) => api.post(`/job/postJob`, jobData);
export const getTasksApi = () => api.get(`/job/tasks`);
export const getTaskApplicantsApi = (jobId) => api.get(`/job/tasks/${jobId}/applicants`);
export const updateApplicationStatusApi = (acceptanceId,status) => api.put(`/job/applications/${acceptanceId}/${status}`);
export const payApi = (jobId,workerId) => api.put(`/job/pay/${jobId}/${workerId}`);


//file
export const uploadFileApi = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return api.post("/file/uploadFile", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};
export const uploadImgApi = (file) => {
  const formData = new FormData();
  formData.append("file", file);
  return api.post("/file/uploadImage", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
};
export const getFileApi = (data) =>
  api.get(
    `/file/getFile`,
    {
      params:{
          path: data,
       },
        responseType: "blob" 
   }
  );
export const getImageApi = (data) =>
  api.get(
    `/file/getImage`,
    {
      params:{
          path: data,
        },
        responseType: "blob" 
    }
  );


//评价
export const reviewApi = (data) => api.post(`/reviews`, data);
export const getReviewByUserIdApi = (data) => api.post(`/reviews/review`,data);


